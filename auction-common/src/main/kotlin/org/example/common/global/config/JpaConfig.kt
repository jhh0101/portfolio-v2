package org.example.common.global.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = ["org.example"])
@EntityScan(basePackages = ["org.example"])
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class JpaConfig