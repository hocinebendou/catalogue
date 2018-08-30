package bio.tech.ystr.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${admin.mail.address}")
    private String adminEmailAddress;

    @Override
    public void sendMessageWithoutAttachment (String to, String subject, String text) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(to);
            helper.setTo(adminEmailAddress);
            helper.setSubject(subject);
            helper.setText(text);

            emailSender.send(message);

        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }

    @Override
    public void sendMessageWithAttachment (String to, String subject, String text, String pathToAttachment) {

        try {

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(adminEmailAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Report.pdf", file);

            emailSender.send(message);

        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }

    @Override
    public void sendMessageUsingTemplate(String to, String subject, SimpleMailMessage template,
                                         String pathToAttachment, String... templateArgs) {
        String text = String.format(template.getText(), templateArgs);

        sendMessageWithAttachment(to, subject, text, pathToAttachment);
    }
}
