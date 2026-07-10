package com.gasfgrv.autorizador.core.infrastructure.configurations;

import com.gasfgrv.autorizador.core.domain.service.AutorizarPedidoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class ServicesConfiguration {

    @Bean
    public AutorizarPedidoService service() {
        return new AutorizarPedidoService(new Random());
    }

}
