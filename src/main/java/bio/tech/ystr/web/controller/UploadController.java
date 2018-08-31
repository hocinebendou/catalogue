package bio.tech.ystr.web.controller;

import bio.tech.ystr.persistence.dao.SftpRepository;
import bio.tech.ystr.persistence.model.NeoSftp;
import bio.tech.ystr.persistence.model.Role;
import bio.tech.ystr.persistence.model.User;
import bio.tech.ystr.service.UserService;
import bio.tech.ystr.web.error.FileTypeNotSupportedException;
import com.jcraft.jsch.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class UploadController {

    @Autowired
    private UserService userService;

    @Autowired
    private SftpRepository sftpRepository;

    @Autowired
    private MessageSource messages;

    /*
     * ADMIN LAYOUT
     */
    @GetMapping(value = "/upload")
    public String uploadFilesPage(Model model) {

        User user = authenticatedUser();
        model.addAttribute("username", user.getUsername());

        Role role = user.getRoles().stream().findFirst().orElse(null);
        model.addAttribute("role", role.getName());

        Collection<User> bioUsers = userService.findUsersByRole("ROLE_BIOBANK");
        Collection<User> arcUsers = userService.findUsersByRole("ROLE_ARCHIVE");
        Collection<User> users = Stream.concat(bioUsers.stream(), arcUsers.stream()).collect(Collectors.toList());
        model.addAttribute("users", users);

        return "upload";
    }

    @PostMapping(value = "/uploadFile")
    public String onUpload(MultipartFile file, HttpServletRequest request, RedirectAttributes redirectAttributes) {

        if (file.isEmpty() || !isCSV(file)) {
            redirectAttributes.addFlashAttribute("error", messages.getMessage("message.fileTypeNotSupported",null, request.getLocale()));
        } else {
            User user = authenticatedUser();
            String fileType = request.getParameter("fileType");
            Resource dir = userDirectory(user);

            try {
                // copyFileToDir(file, fileType, dir, neoUser);
                getFileFromUser(file, fileType, dir, user);
                redirectAttributes.addFlashAttribute("success", "File uploaded successfully!");
            } catch(Exception e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Error when uploading the file!");
            }
        }

        return "redirect:/upload";
    }

    /*
     * NON API
     */

    private User authenticatedUser() {
        final org.springframework.security.core.userdetails.User authenticated = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userService.findUserByEmail(authenticated.getUsername());
    }

    private Resource userDirectory(User user) {

        return new FileSystemResource("./users/" + user.getUsername() + "/raw");
    }

    private boolean isCSV(MultipartFile file) {

        return file.getContentType().startsWith("text/csv");
    }

    private static String getFileExtension(String name) {

        return name.substring(name.lastIndexOf("."));
    }

    private void getFileFromUser(MultipartFile file, String fileType, Resource dir, User user)
            throws JSchException, SftpException, IOException {

        NeoSftp sftp = sftpRepository.findNeoSftpByUsername(user.getUsername());
        String userSftp = sftp.getSftpUser();
        String serverAddress = sftp.getIpAddress();
        int serverPort = sftp.getPort();
        String pathDir = sftp.getPathDir();
        String destinationPath = dir.getFile().getAbsolutePath();

        fileType = fileType.isEmpty()? "_" : "_" + fileType + "_";
        String prefix =  user.getUsername() + fileType;

        MultipartFile tempFile = new MockMultipartFile(FilenameUtils
                .getBaseName(prefix)
                .concat(new SimpleDateFormat("yyyyMMddHHmm")
                        .format(new Date())) + "." + FilenameUtils
                .getExtension(file.getOriginalFilename()), file.getInputStream());

        tempFile.transferTo(new File("/tmp/" + tempFile.getName()));

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;

        try {
            jsch.addIdentity("~/.ssh/id_rsa", "hocine");
            jsch.setKnownHosts("~/.ssh/known_hosts");
            session = jsch.getSession(userSftp, serverAddress, serverPort);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "yes");

            session.setConfig(config);
            session.connect();

            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            channel.cd(destinationPath);
            channel.get("/tmp/" + tempFile.getName(), destinationPath);
//            channel.get(file.getOriginalFilename(), destinationPath);
        } finally {
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }

            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
}
