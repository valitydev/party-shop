package dev.vality.partyshop.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class PostgresContainerExtension implements BeforeAllCallback, AfterAllCallback {

    public static PostgreSQLContainer POSTGRES;

    private static final String POSTGRES_IMAGE_NAME = "postgres";
    private static final String POSTGRES_IMAGE_VERSION = "11.4";

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        POSTGRES = new PostgreSQLContainer<>(DockerImageName
                .parse(POSTGRES_IMAGE_NAME)
                .withTag(POSTGRES_IMAGE_VERSION))
                .withDatabaseName("party-shop")
                .withPassword("postgres")
                .withUsername("postgres");
        POSTGRES.start();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        POSTGRES.stop();
    }
}
