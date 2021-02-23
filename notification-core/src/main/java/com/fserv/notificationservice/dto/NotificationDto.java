package com.fserv.notificationservice.dto;

import java.util.List;
import java.util.Map;

import org.file.store.dto.FileDto;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

	private String from;

	private List<String> toAddresses;

	private List<String> cc;

	private List<String> bcc;

	private String subject;

	private boolean isHtmlContent;

	private String body;

	@JsonIgnore
	private List<MultipartFile> multipartFiles;

	List<FileDto> dtos;

	private Map<String, Object> model;

	public List<FileDto> getDtos() {
		return dtos;
	}

	public void setDtos(List<FileDto> dtos) {
		this.dtos = dtos;
	}

	public Map<String, Object> getModel() {
		return model;
	}

	public void setModel(Map<String, Object> model) {
		this.model = model;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public List<MultipartFile> getMultipartFiles() {
		return multipartFiles;
	}

	public void setMultipartFiles(List<MultipartFile> multipartFiles) {
		this.multipartFiles = multipartFiles;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public List<String> getBcc() {
		return bcc;
	}

	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isHtmlContent() {
		return isHtmlContent;
	}

	public void setHtmlContent(boolean isHtmlContent) {
		this.isHtmlContent = isHtmlContent;
	}

	public List<String> getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(List<String> toAddresses) {
		this.toAddresses = toAddresses;
	}
}
