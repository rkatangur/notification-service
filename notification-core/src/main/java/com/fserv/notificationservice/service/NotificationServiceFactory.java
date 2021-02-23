package com.fserv.notificationservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.fserv.notificationservice.config.NotificationProperties;

@Component
public class NotificationServiceFactory extends AbstractFactoryBean<NotificationService>
		implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationServiceFactory.class);

	@Autowired
	private NotificationProperties appProperties;

	private ApplicationContext applicationContext;

	@Override
	public Class<?> getObjectType() {
		return NotificationService.class;
	}

	@Override
	protected NotificationService createInstance() throws Exception {
		AutowireCapableBeanFactory autowireFactory = applicationContext.getAutowireCapableBeanFactory();
		NotificationService notificationService = null;
		if (appProperties.isUseAmazonSes()) {
			notificationService = createAwsSESNotificationService(autowireFactory);
		}

		if (notificationService == null) {
			notificationService = createNoOpNotificationService(autowireFactory);
		}

		return notificationService;
	}

	private NotificationService createAwsSESNotificationService(AutowireCapableBeanFactory factory) {
		NotificationService notificationService = null;
		try {
			LOGGER.info("Creating AWS SES NotificationService.");
			AwsNotificationServiceImpl awsSesNotificationService = new AwsNotificationServiceImpl();

			factory.autowireBean(awsSesNotificationService);
			factory.initializeBean(awsSesNotificationService, "notificationService");
			notificationService = awsSesNotificationService;
		} catch (Exception e) {
			LOGGER.error(
					"Exception initializing the amazon SES NotificationService, need to default to NoOpNotificationService.",
					e);
		}

		return notificationService;
	}

	private NotificationService createNoOpNotificationService(AutowireCapableBeanFactory factory) {

		LOGGER.info("Creating NoOpNotificationService.");
		NotificationService notificationService = new NoOpNotificationServiceImpl();

		factory.autowireBean(notificationService);
		factory.initializeBean(notificationService, "notificationService");
		return notificationService;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
