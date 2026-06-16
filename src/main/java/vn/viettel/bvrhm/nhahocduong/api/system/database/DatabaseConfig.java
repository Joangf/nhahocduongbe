package vn.viettel.bvrhm.nhahocduong.api.system.database;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "vn.viettel.bvrhm.nhahocduong")
@EnableJpaAuditing
public class DatabaseConfig {}
