package com.fserv.notificationservice;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("com.fserv.notificationservice")
@EntityScan("com.fserv.notificationservice.model")
@EnableJpaRepositories("com.fserv.notificationservice.repository")
@Import(FilestoreConfiguration.class)
public class NotificationServiceConfiguration {

}
