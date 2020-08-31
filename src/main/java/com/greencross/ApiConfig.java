package com.greencross;

import com.greencross.alis.api.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {
	@Value("${alis.api.url}")
	private String url;
	@Value("${alis.api.content-type}")
	private String contentType;
	@Value("${alis.api.username}")
	private String username;
	@Value("${alis.api.password}")
	private String password;

	@Bean
	public Api t() {
		return new Api(url, contentType, username, password);
	}
}
