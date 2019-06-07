package poc.fwk.jpa.configurers;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "poc.fwk.**.repositories")
@EntityScan(basePackages = "poc.fwk.**.entities")
public class JpaConfigurer {

}
