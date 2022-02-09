package dev.vality.partyshop.config;

import dev.vality.damsel.domain_config.RepositoryClientSrv;
import dev.vality.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public RepositoryClientSrv.Iface repositoryClient(@Value("${repository.url}") Resource resource,
                                                      @Value("${repository.network-timeout}") int networkTimeout)
            throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(resource.getURI())
                .withNetworkTimeout(networkTimeout)
                .build(RepositoryClientSrv.Iface.class);
    }

}
