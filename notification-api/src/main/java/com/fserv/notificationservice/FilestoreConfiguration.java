package com.fserv.notificationservice;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan({ "org.file.store.properties", "org.file.store.service" })
@EntityScan("org.file.store.model")
@EnableJpaRepositories("org.file.store.repository")
//@ComponentScan("org.file.store.repository")
public class FilestoreConfiguration {

}
