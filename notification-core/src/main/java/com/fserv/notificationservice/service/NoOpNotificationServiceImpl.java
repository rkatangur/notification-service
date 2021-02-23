package com.fserv.notificationservice.service;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fserv.notificationservice.dto.NotificationDto;

public class NoOpNotificationServiceImpl implements NotificationService {

	private static final Logger log = LoggerFactory.getLogger(NoOpNotificationServiceImpl.class);

	@PostConstruct
	public void init() {
	}

	@Override
	public NotificationDto sendSimpleTextOrHtmlEmail(NotificationDto notificationDto) throws IOException {
		return notificationDto;
	}

	@Override
	public NotificationDto sendEmailWithAttachments(NotificationDto notificationDto)
			throws MessagingException, IOException {
		return notificationDto;
	}

}
