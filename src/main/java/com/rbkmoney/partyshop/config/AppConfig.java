package com.rbkmoney.partyshop.config;

import com.rbkmoney.damsel.domain_config.RepositoryClientSrv;
import com.rbkmoney.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class AppConfig {

    @Bean
    public RepositoryClientSrv.Iface repositoryClient(@Value("${repository.url}") Resource resource,
                                                      @Value("${repository.network-timeout}") int networkTimeout) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(resource.getURI())
                .withNetworkTimeout(networkTimeout)
                .build(RepositoryClientSrv.Iface.class);
    }

}
