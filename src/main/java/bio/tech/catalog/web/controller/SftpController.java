package bio.tech.catalog.web.controller;

import bio.tech.catalog.persistence.dao.SftpRepository;
import bio.tech.catalog.persistence.model.NeoSftp;
import bio.tech.catalog.persistence.model.User;
import bio.tech.catalog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.*;

@Controller
public class SftpController {

    @Autowired
    private UserService userService;

    @Autowired
    private SftpRepository sftpRepository;

    @RequestMapping(value = "/sftpconf")
    public String sftpConf(Model model) {

        User user = authenticatedUser();
        NeoSftp sftp = sftpRepository.findNeoSftpByUsername(user.getUsername());
        if (sftp == null)
            sftp = new NeoSftp();
        model.addAttribute("sftpConf", sftp);

        return "sftp";
    }

    @RequestMapping(value = "/sftpconf", method = RequestMethod.POST)
    public String sftpConfCreate(@Valid NeoSftp sftpConf, RedirectAttributes model) throws IOException {

        User user = authenticatedUser();
        NeoSftp  sftp = sftpRepository.findNeoSftpByUsername(user.getUsername());

        if (sftp == null) {
            sftp = new NeoSftp();
            sftp.setUsername(user.getUsername());
        }

        sftp.setPublicKey(sftpConf.getPublicKey());
        sftp.setIpAddress(sftpConf.getIpAddress());
        sftp.setPort(sftpConf.getPort());
        sftp.setSftpUser(sftpConf.getSftpUser());
        sftp.setPathDir(sftpConf.getPathDir());

        // verify the existance or the public. If not add it to the authorized keys file
        String publicKey = sftp.getPublicKey();
        BufferedReader reader;
        reader = new BufferedReader(new FileReader("/home/hocine/.ssh/authorized_keys"));
        boolean contains = false;
        String line = reader.readLine();
        while (line != null) {
            if (line.equals(publicKey)) {
                contains = true;
                break;
            } else {
                line = reader.readLine();
            }
        }
        reader.close();

        if (!contains) {
            FileWriter fw = new FileWriter("/home/hocine/.ssh/authorized_keys", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(publicKey);
            bw.newLine();
            bw.close();
        }

        sftpRepository.save(sftp);
        model.addFlashAttribute("message","SFTP successfully configured.");

        return "redirect:/sftpconf";
    }

    /*
     * NON API
     */

    private User authenticatedUser() {
        final org.springframework.security.core.userdetails.User authenticated = (org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return userService.findUserByEmail(authenticated.getUsername());
    }
}
