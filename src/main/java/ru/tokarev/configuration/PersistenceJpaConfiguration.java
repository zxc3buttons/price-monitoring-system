package ru.tokarev.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@EnableTransactionManagement
@Configuration
public class PersistenceJpaConfiguration {

    @Value("${packages_to_scan}")
    private String packagesToScan;

    @Value("${data_source.driver_class_name}")
    private String dateSourceDriverClassName;

    @Value("${data_source.url}")
    private String dataSourceUrl;

    @Value("${data_source.username}")
    private String dataSourceUsername;

    @Value("${data_source.password}")
    private String dataSourcePassword;

    @Value("${data_source.properties.show_sql}")
    private String dataSourcePropertiesShowSql;

    @Value("${data_source.properties.format_sql}")
    private String dataSourcePropertiesFormatSql;

    @Value("${data_source.properties.hibernate.dialect}")
    private String dataSourcePropertiesHibernateDialect;


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean
                = new LocalContainerEntityManagerFactoryBean();

        localContainerEntityManagerFactoryBean.setDataSource(dataSource());
        localContainerEntityManagerFactoryBean.setPackagesToScan(packagesToScan);

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);
        localContainerEntityManagerFactoryBean.setJpaProperties(additionalProperties());

        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dateSourceDriverClassName);
        dataSource.setUrl(dataSourceUrl);
        dataSource.setUsername(dataSourceUsername);
        dataSource.setPassword(dataSourcePassword);
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    public Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("show_sql", dataSourcePropertiesShowSql);
        properties.setProperty("format_sql", dataSourcePropertiesFormatSql);
        properties.setProperty("hibernate.dialect", dataSourcePropertiesHibernateDialect);

        return properties;
    }
}
