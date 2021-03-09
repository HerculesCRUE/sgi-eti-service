package org.crue.hercules.sgi.eti.config;

import org.crue.hercules.sgi.framework.data.domain.SgiAuditorAware;
import org.crue.hercules.sgi.framework.data.jpa.repository.support.SgiJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * DataConfig
 * 
 * Jpa configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = {
    "org.crue.hercules.sgi.eti.repository" }, repositoryBaseClass = SgiJpaRepository.class)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class DataConfig {
  @Bean
  public AuditorAware<String> auditorAware() {
    return new SgiAuditorAware();
  }
}