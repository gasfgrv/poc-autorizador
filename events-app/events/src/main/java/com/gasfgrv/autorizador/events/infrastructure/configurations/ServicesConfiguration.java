package com.gasfgrv.autorizador.events.infrastructure.configurations;

import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowRepositoryPort;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowResponsePort;
import com.gasfgrv.autorizador.events.domain.service.RespostaExecucaoService;
import com.gasfgrv.autorizador.events.domain.service.TaskTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfiguration {

    @Bean
    public TaskTokenService taskTokenService() {
        return new TaskTokenService();
    }

    @Bean
    public RespostaExecucaoService respostaExecucaoService(WorkflowRepositoryPort repository,
                                                           WorkflowResponsePort response) {
        return new RespostaExecucaoService(repository, response);
    }

}
