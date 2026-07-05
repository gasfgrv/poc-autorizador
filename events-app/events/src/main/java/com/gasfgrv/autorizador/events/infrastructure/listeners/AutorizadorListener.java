package com.gasfgrv.autorizador.events.infrastructure.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowRepositoryPort;
import com.gasfgrv.autorizador.events.infrastructure.dtos.sqs.SqsEventPayloadDto;
import com.gasfgrv.autorizador.events.infrastructure.dtos.sqs.WorkflowContextDto;
import com.gasfgrv.autorizador.events.infrastructure.exceptions.AutorizadorListenerException;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.annotation.SqsListenerAcknowledgementMode;
import io.awspring.cloud.sqs.listener.acknowledgement.Acknowledgement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutorizadorListener {

    private final ObjectMapper objectMapper;
    private final WorkflowRepositoryPort workflowRepository;

    @SqsListener(value = "${spring.cloud.aws.sqs.queue}", acknowledgementMode = SqsListenerAcknowledgementMode.MANUAL)
    public void listen(String message, Acknowledgement acknowledgement) {
        try {
            log.info("Received message from SQS: {}", message);
            SqsEventPayloadDto payload = objectMapper.readValue(message, SqsEventPayloadDto.class);

            WorkflowContextDto workflowContext = payload.toWorkflowContext();
            workflowRepository.salvarContextoWorkflow(workflowContext.taskToken(), workflowContext.executionArn());


            // todo: mandar os dados do evento para o kafka
        } catch (Exception e) {
            log.error("Error listening for events in autorizador", e);
            throw new AutorizadorListenerException(e);
        } finally {
            acknowledgement.acknowledge();
        }
    }

}
