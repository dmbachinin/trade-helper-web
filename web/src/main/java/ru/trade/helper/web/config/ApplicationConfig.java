package ru.trade.helper.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("ru.trade.helper")
@EnableTransactionManagement
@EnableRetry
@EnableScheduling
@RequiredArgsConstructor
@EnableAsync
public class ApplicationConfig {
}
