package com.greencross;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = "com.greencross"
)
public class DatabaseConfig {
	private static final String ENV_JPA_GENERATE_DDL = "spring.datasource.jpa.generate-ddl";
	private static final String ENV_JPA_SHOW_SQL = "spring.datasource.jpa.show-sql";
	private final Environment env;

	public DatabaseConfig(Environment env) {
		this.env = env;
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource getDataSource() {
		return DataSourceBuilder.create().build();
	}

	private Properties jpaProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.hbm2ddl.auto", env.getProperty(ENV_JPA_GENERATE_DDL));
		properties.put("hibernate.show_sql", env.getProperty(ENV_JPA_SHOW_SQL));
		properties.put("hibernate.jdbc.lob.non_contextual_creation", true);
		properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
		return properties;
	}

	@Bean(name="entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean getFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(getDataSource());
		em.setPackagesToScan("com.greencross.lims.entity");
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setPersistenceUnitName("ALIS");
		em.setJpaProperties(jpaProperties());
		return em;
	}
	@Bean(name = "transactionManagerAlis")
	@Primary
	public PlatformTransactionManager getTransactionManager(@Qualifier("entityManagerFactory") EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}
}
