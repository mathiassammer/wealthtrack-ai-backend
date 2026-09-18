package com.wealthtrack.api.infra.realtime;

import io.ably.lib.rest.AblyRest;
import io.ably.lib.types.AblyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AblyConfig {

    @Value("${ably.api.key}")
    private String ablyApiKey;

    @Bean
    public AblyRest ablyRest() throws AblyException {
        // Inicializa o cliente REST do Ably com a nossa chave segura
        return new AblyRest(ablyApiKey);
    }
}