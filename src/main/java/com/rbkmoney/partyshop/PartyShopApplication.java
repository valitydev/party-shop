package com.rbkmoney.partyshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class PartyShopApplication extends SpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartyShopApplication.class, args);
    }

}
