package com.mbopartners.autoconfiguration;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import org.mule.DefaultMuleContext;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.client.MuleClient;
import org.mule.api.context.MuleContextBuilder;
import org.mule.api.context.MuleContextFactory;
import org.mule.api.transaction.TransactionManagerFactory;
import org.mule.client.DefaultLocalMuleClient;
import org.mule.config.DefaultMuleConfiguration;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextBuilder;
import org.mule.context.DefaultMuleContextFactory;
import org.mule.module.spring.transaction.SpringTransactionFactory;
import org.mule.module.spring.transaction.SpringTransactionManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.annotation.PostConstruct;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

@Configuration
public class MuleAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(MuleAutoConfiguration.class);

    @Value("${mule.config.files}")
    String muleConfigFiles;

    @Value("${mule.servlet.url.pattern}")
    String muleServletUrlPattern;

    @Value("${mule.working.directory:./.mule}")
    String muleWorkingDir;

    @Bean
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(1000);
        return userTransactionImp;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public TransactionManager transactionManager() {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean
    public PlatformTransactionManager platformTransactionManager(UserTransaction userTransaction, TransactionManager transactionManager) {
        return new JtaTransactionManager(userTransaction, transactionManager);
    }

    @Bean
    public TransactionManagerFactory transactionManagerFactory(PlatformTransactionManager platformTransactionManager) {
        SpringTransactionFactory factory = new SpringTransactionFactory();
        factory.setManager(platformTransactionManager);
        SpringTransactionManagerFactory managerFactory = new SpringTransactionManagerFactory();
        managerFactory.setTransactionManager(new JtaTransactionManager().getTransactionManager());
        return managerFactory;
    }

    @Bean
    @ConditionalOnProperty(value = "mule.servlet.url.pattern")
    public ServletRegistrationBean muleServletBean(MuleContext muleContext) {
        ServletRegistrationBean bean = new ServletRegistrationBean(
                new SpringBootMuleReceiverServlet(muleContext), muleServletUrlPattern);
        bean.setLoadOnStartup(1);

        return bean;
    }

    @Bean
    MuleClient muleClient(MuleContext muleContext) {
        logger.info("Creating MuleClient");
        return new DefaultLocalMuleClient(muleContext);
    }

    @Bean
    MuleContext muleContext(ApplicationContext context) throws MuleException {
        System.setProperty("mule.workingDirectory", muleWorkingDir);
        logger.info("Creating MuleContext");
        MuleContextFactory muleContextFactory = new DefaultMuleContextFactory();
        logger.info("Loading Mule config files {}", muleConfigFiles);
        String[] configFiles = muleConfigFiles.split(",");
        SpringXmlConfigurationBuilder builder = new SpringXmlConfigurationBuilder(configFiles);
        builder.setParentContext(context);
        MuleContextBuilder contextBuilder = new DefaultMuleContextBuilder();
        MuleContext muleContext = muleContextFactory.createMuleContext(builder, contextBuilder);
        logger.info("Created MuleContext");
        return muleContext;
    }

    @Configuration
    public static class MuleContextPostConstruct {


        @Autowired
        MuleContext muleContext;

        @PostConstruct
        MuleContext createMuleContext() throws MuleException {
            logger.info("Starting MuleContext....");
            muleContext.start();
            return muleContext;
        }
    }
}
