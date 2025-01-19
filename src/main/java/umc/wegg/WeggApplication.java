package umc.wegg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WeggApplication {

	public static void main(String[] args) {
		SpringApplication.run(WeggApplication.class, args);
	}

}
