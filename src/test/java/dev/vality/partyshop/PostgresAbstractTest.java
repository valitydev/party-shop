package dev.vality.partyshop;

import dev.vality.partyshop.extension.PostgresContainerExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ExtendWith(PostgresContainerExtension.class)
public abstract class PostgresAbstractTest {

    @DynamicPropertySource
    static void postgresProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PostgresContainerExtension.POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", PostgresContainerExtension.POSTGRES::getUsername);
        registry.add("spring.datasource.password", PostgresContainerExtension.POSTGRES::getPassword);
        registry.add("spring.flyway.url", PostgresContainerExtension.POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", PostgresContainerExtension.POSTGRES::getUsername);
        registry.add("spring.flyway.password", PostgresContainerExtension.POSTGRES::getPassword);
    }

}
