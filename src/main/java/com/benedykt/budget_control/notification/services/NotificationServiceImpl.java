package com.benedykt.budget_control.notification.services;

import com.benedykt.budget_control.auth_users.entity.User;
import com.benedykt.budget_control.enums.NotificationType;
import com.benedykt.budget_control.notification.dtos.NotificationDTO;
import com.benedykt.budget_control.notification.entity.Notification;
import com.benedykt.budget_control.notification.repo.NotificationRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepo notificationRepo;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(NotificationDTO notificationDTO, User user) {

        try {
            MimeMessage mimeMailMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMailMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
                    );

            helper.setTo(notificationDTO.getRecipient());
            helper.setSubject(notificationDTO.getSubject());

            // Use template if provided
            if(notificationDTO.getTemplateName() != null){
                Context context = new Context();
                context.setVariables(notificationDTO.getTemplateVariables());

                String htmlContent = templateEngine.process(notificationDTO.getTemplateName(), context);
                helper.setText(htmlContent, true);
            }else{
                // If no template send text ody directly
                helper.setText(notificationDTO.getBody(), true);
            }
            mailSender.send(mimeMailMessage);

            //save in database
            Notification notificationToSave = Notification.builder()
                    .recipient(notificationDTO.getRecipient())
                    .subject(notificationDTO.getSubject())
                    .body(notificationDTO.getBody())
                    .type(NotificationType.EMAIL)
                    .user(user)
                    .build();

            notificationRepo.save(notificationToSave);

        }catch (MessagingException e){
            log.error(e.getMessage());
        }
    }
}
