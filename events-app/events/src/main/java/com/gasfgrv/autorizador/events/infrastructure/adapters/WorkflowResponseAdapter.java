package com.gasfgrv.autorizador.events.infrastructure.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowResponsePort;
import com.gasfgrv.autorizador.events.infrastructure.exceptions.WorkflowResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowResponseAdapter implements WorkflowResponsePort {

    private final SfnAsyncClient sfnClient;
    private final ObjectMapper objectMapper;

    @Override
    public void responderComSucesso(String taskToken, Pedido response) {
        try {
            String value = objectMapper.writeValueAsString(response);
            sfnClient.sendTaskSuccess(builder -> builder
                    .taskToken(taskToken)
                    .output(value));
        } catch (Exception e) {
            log.error("Erro ao processar os dados dos pedidos", e);
            throw new WorkflowResponseException(e);
        }
    }

    @Override
    public void responderComFalha(String taskToken) {
        sfnClient.sendTaskFailure(builder -> builder
                .taskToken(taskToken));
    }

}
