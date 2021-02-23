package com.fserv.notificationservice.service;

import java.io.IOException;

import javax.mail.MessagingException;

import com.fserv.notificationservice.dto.NotificationDto;

public interface NotificationService {

    NotificationDto sendSimpleTextOrHtmlEmail(NotificationDto notificationDto) throws IOException;

    NotificationDto sendEmailWithAttachments(NotificationDto notificationDto) throws MessagingException, IOException;
    
}   
