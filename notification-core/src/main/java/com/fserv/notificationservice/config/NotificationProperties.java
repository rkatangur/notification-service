package com.fserv.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationProperties {
	
    @Value("${use.amazon.ses:}")
	private boolean useAmazonSes;

    @Value("${ses.access.key:}")
    private String accessKey;

    @Value("${ses.secret.key:}")
    private String secretKey;

    @Value("${ses.from.email:}")
    private String from;
    
    @Value("${ses.region:}")
	private String sesRegion;

	@Value("${use.s3file.store:true}")
	private boolean useS3fileStore;
	
	@Value("${ses.max.aws.client.connections:5}")
	private int maxAwsClientConnections;

	public boolean isUseS3fileStore() {
		return useS3fileStore;
	}

	public void setUseS3fileStore(boolean useS3fileStore) {
		this.useS3fileStore = useS3fileStore;
	}

	public boolean isUseAmazonSes() {
		return useAmazonSes;
	}

	public void setUseAmazonSes(boolean useAmazonSes) {
		this.useAmazonSes = useAmazonSes;
	}

	public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getFrom() {
        return from;
    }
    
    public String getSesRegion() {
		return sesRegion;
	}

	public void setSesRegion(String sesRegion) {
		this.sesRegion = sesRegion;
	}

	public int getMaxAwsClientConnections() {
		return maxAwsClientConnections;
	}
}
