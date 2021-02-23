package com.fserv.notificationservice.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.file.store.dto.FileDto;
import org.file.store.model.FileObj;
import org.file.store.repository.FileRepository;
import org.file.store.service.FileService;
import org.file.store.service.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.util.StringUtils;
import com.fserv.notificationservice.config.NotificationProperties;
import com.fserv.notificationservice.dto.NotificationDto;

public class AwsNotificationServiceImpl implements NotificationService {

	private static final Logger log = LoggerFactory.getLogger(AwsNotificationServiceImpl.class);

	@Autowired
	private NotificationProperties appProperties;

	@Autowired(required = false)
	@Lazy
	private FileService fileService;

	private AmazonSimpleEmailService awsSesClient;

	@Autowired
	private FileRepository fileRepository;
	
	@PostConstruct
	public void init() {
		AWSCredentials awsCredentials = null;
		if (!StringUtils.isNullOrEmpty(appProperties.getAccessKey())
				&& !StringUtils.isNullOrEmpty(appProperties.getSecretKey())) {
			awsCredentials = new BasicAWSCredentials(appProperties.getAccessKey(), appProperties.getSecretKey());
		}

		AmazonSimpleEmailServiceClientBuilder sesClientBuilder = AmazonSimpleEmailServiceClient.builder();

		ClientConfiguration clientConf = new ClientConfiguration();
		clientConf.setMaxConnections(appProperties.getMaxAwsClientConnections());
		
		if (clientConf != null) {
			sesClientBuilder.withClientConfiguration(clientConf);
		}

		if (awsCredentials != null) {
			sesClientBuilder.withCredentials(new AWSStaticCredentialsProvider(awsCredentials));
		}

		if (!StringUtils.isNullOrEmpty(appProperties.getSesRegion())) {
			sesClientBuilder.withRegion(appProperties.getSesRegion());
		}

		try {
		 // EndpointConfiguration endpointConfiguration = new EndpointConfiguration("email-smtp.us-east-1.amazonaws.com",appProperties.getSesRegion());
		 // sesClientBuilder.setEndpointConfiguration(endpointConfiguration);
			awsSesClient = sesClientBuilder.build();
		} catch (Exception e) {
			throw new BeanInitializationException("Exception initializing AwsNotificationServiceImpl", e);
		}
	}

//	@Override
//	public NotificationDto sendTextEmail(NotificationDto notificationDto) throws IOException {
//		sendEmail(buildEmailRequest(notificationDto));
//		return notificationDto;
//	}
//
//	@Override
//	public NotificationDto sendHtmlEmail(NotificationDto notificationDto) throws IOException {
//		sendEmail(buildEmailRequest(notificationDto));
//		return notificationDto;
//	}

	@Override
	public NotificationDto sendSimpleTextOrHtmlEmail(NotificationDto notificationDto) throws IOException {
		sendEmail(buildEmailRequest(notificationDto));
		return notificationDto;
	}

	private SendEmailRequest buildEmailRequest(NotificationDto notificationDto) {

		SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination().withToAddresses(notificationDto.getToAddresses())
						.withCcAddresses(notificationDto.getCc()).withBccAddresses(notificationDto.getBcc()));

		String fromEmail = StringUtils.isNullOrEmpty(notificationDto.getFrom()) ? appProperties.getFrom()
				: notificationDto.getFrom();
		request.withSource(fromEmail);

		if (notificationDto.isHtmlContent()) {
			request.withMessage(new Message()
					.withBody(
							new Body().withHtml(new Content().withCharset("UTF-8").withData(notificationDto.getBody())))
					.withSubject(new Content().withCharset("UTF-8").withData(notificationDto.getSubject())));

		} else {
			request.withMessage(new Message()
					.withBody(
							new Body().withHtml(new Content().withCharset("UTF-8").withData(notificationDto.getBody())))
					.withSubject(new Content().withCharset("UTF-8").withData(notificationDto.getSubject())));
		}
		return request;
	}

	private void sendEmail(SendEmailRequest request) {
		try {
			log.info("Attempting to send an email through Amazon SES by using the AWS SDK for Java...");
			awsSesClient.sendEmail(request);
			log.info("Email sent!");
		} catch (Exception ex) {
			log.info("The email was not sent.");
			log.error("Error message: " + ex.getMessage(), ex);
		}
	}

	@Override
	public NotificationDto sendEmailWithAttachments(NotificationDto notificationDto)
			throws MessagingException, IOException {

		List<FileDto> dtos = new ArrayList<FileDto>();
		if (appProperties.isUseS3fileStore() && fileService != null) {
			for (MultipartFile file : notificationDto.getMultipartFiles()) {
			 
				FileDto fileDto = new FileDto();
			        fileDto.setContent(file.getBytes());
		            fileDto.setFileSize(file.getSize());
		            fileDto.setOriginalFileName(file.getOriginalFilename()); 
				FileDto savedFile = fileService.save(fileDto);
				 
				dtos.add(savedFile);
			}
		} else {
			log.info("Not saving any attachments to Amazon-S3.");
		}

		String DefaultCharSet = MimeUtility.getDefaultJavaCharset();
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message.setSubject(notificationDto.getSubject(), "UTF-8");
		message.setFrom(new InternetAddress(notificationDto.getFrom()));
		for (String to : notificationDto.getToAddresses()) {
			message.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(to));
		}
		if (notificationDto.getCc() != null) {
			for (String cc : notificationDto.getCc()) {
				message.addRecipients(javax.mail.Message.RecipientType.CC, InternetAddress.parse(cc));
			}
		}
		if (notificationDto.getBcc() != null) {
			for (String bcc : notificationDto.getBcc()) {
				message.addRecipients(javax.mail.Message.RecipientType.BCC, InternetAddress.parse(bcc));
			}
		}
		MimeMultipart msg_body = new MimeMultipart("alternative");
		MimeBodyPart wrap = new MimeBodyPart();
		MimeBodyPart textPart = new MimeBodyPart();

		textPart.setContent(MimeUtility.encodeText(notificationDto.getBody(), DefaultCharSet, "B"),
				"text/plain; charset=UTF-8");
		textPart.setHeader("Content-Transfer-Encoding", "base64");
		msg_body.addBodyPart(textPart);
		wrap.setContent(msg_body);
		MimeMultipart msg = new MimeMultipart("mixed");
		message.setContent(msg);
		msg.addBodyPart(wrap);

		for (MultipartFile multipartFile : notificationDto.getMultipartFiles()) {
			File f = File.createTempFile(multipartFile.getOriginalFilename(), ".dat");

			FileOutputStream fos = new FileOutputStream(f);
			fos.write(multipartFile.getBytes());
			fos.close();

			MimeBodyPart att = new MimeBodyPart();
			DataSource fds = new FileDataSource(f);
			att.setDataHandler(new DataHandler(fds));
			att.setFileName(fds.getName());
			msg.addBodyPart(att);
		}

		try {
			log.info("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);
			RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
			SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);
			awsSesClient.sendRawEmail(rawEmailRequest);
			log.info("Email sent!");
		} catch (Exception ex) {
			log.error("Error message: " + ex.getMessage(), ex);
		}
		notificationDto.setDtos(dtos);
		return notificationDto;
	}

}
