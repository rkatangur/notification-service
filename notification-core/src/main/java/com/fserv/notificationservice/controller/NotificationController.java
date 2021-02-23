package com.fserv.notificationservice.controller;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fserv.notificationservice.dto.NotificationDto;
import com.fserv.notificationservice.service.NotificationService;

@RestController(value = "/notification")
public class NotificationController {

	private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

	@Autowired
	private NotificationService notificationService;

//	@Autowired
//	private Configuration freemarkerConfig;

	@RequestMapping(value = "/notification/email", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<NotificationDto> sendTextEmail(@RequestBody NotificationDto notificationDto) {
		NotificationDto notificationDto1 = null;
		HttpStatus statusCode = HttpStatus.OK;
		try {
			notificationDto1 = notificationService.sendSimpleTextOrHtmlEmail(notificationDto);
		} catch (IOException e) {
			e.printStackTrace();
			statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		return new ResponseEntity<NotificationDto>(notificationDto1, statusCode);
	}

	@RequestMapping(value = "/notification/emailwithattachment", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public @ResponseBody ResponseEntity<NotificationDto> sendAttachmentEmail(@RequestParam(value = "text") String text,
			@RequestParam(value = "from", required = true) String from,
			@RequestParam(value = "subject", required = false) String subject,
			@RequestParam(value = "to", required = true) List<String> toAddresses,
			@RequestParam(value = "attachments", required = false) List<MultipartFile> multipartFile,
			@RequestParam(value = "cc", required = false) List<String> cc,
			@RequestParam(value = "bcc", required = false) List<String> bcc) throws IOException, MessagingException {
		NotificationDto notificationDto = new NotificationDto();
		notificationDto.setMultipartFiles(multipartFile);
		notificationDto.setSubject(subject);
		notificationDto.setBody(text);
		notificationDto.setToAddresses(toAddresses);
		notificationDto.setCc(cc);
		notificationDto.setBcc(bcc);
		notificationDto.setFrom(from);
		NotificationDto notificationDto1 = notificationService.sendEmailWithAttachments(notificationDto);

		return new ResponseEntity<NotificationDto>(notificationDto1, HttpStatus.OK);
	}

//	@RequestMapping(value = "/emailWithTemplate", method = RequestMethod.POST)
//	public @ResponseBody ResponseEntity<NotificationDto> emailTemplate(@RequestParam(value = "html") String html,
//			@RequestParam(value = "from", required = true) String from,
//			@RequestParam(value = "body", required = false) String body,
//			@RequestParam(value = "subject", required = false) String subject,
//			@RequestParam(value = "to", required = true) List<String> toAddresses,
//			@RequestParam(value = "cc", required = false) List<String> cc,
//			@RequestParam(value = "bcc", required = false) List<String> bcc, @RequestParam(value = "name") String name,
//			@RequestParam(value = "location") String location, @RequestParam(value = "signature") String signature)
//			throws IOException, TemplateException {
//		NotificationDto notificationDto = new NotificationDto();
//		notificationDto.setBody(body);
//		notificationDto.setSubject(subject);
//		notificationDto.setToAddresses(toAddresses);
//		notificationDto.setCc(cc);
//		notificationDto.setBcc(bcc);
//		notificationDto.setFrom(from);
//
//		Map<String, Object> model = new HashMap<String, Object>();
//		model.put("name", name);
//		model.put("location", location);
//		model.put("signature", signature);
//
//		Template emailTemplate = freemarkerConfig.getTemplate("emailTemplate.html");
//		String test = FreeMarkerTemplateUtils.processTemplateIntoString(emailTemplate, model);
//		notificationDto.setHtmlContent(true);
//		notificationDto.setBody(test);
//
//		NotificationDto notificationDtoResp = notificationService.sendHtmlEmail(notificationDto);
//		return new ResponseEntity<NotificationDto>(notificationDtoResp, HttpStatus.OK);
//	}
}
