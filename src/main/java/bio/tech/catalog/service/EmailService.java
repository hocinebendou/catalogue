package bio.tech.catalog.service;

import org.springframework.mail.SimpleMailMessage;

/**
 * Created by hocine on 2017/10/19.
 */
public interface EmailService {

    void sendMessageWithoutAttachment(String to,
                                      String subject,
                                      String text);

    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);

    void sendMessageUsingTemplate(String to,
                                  String subject,
                                  SimpleMailMessage template,
                                  String pathToAttachment,
                                  String... templateArgs);
}
