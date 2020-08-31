package com.greencross;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfigurer {
	@Override
	public void setDataSource(DataSource dataSource) {

	}
}
