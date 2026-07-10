package com.gasfgrv.autorizador.events.infrastructure.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gasfgrv.autorizador.events.containers.TestcontainersConfiguration;
import com.gasfgrv.autorizador.events.domain.ports.in.AutorizadorInputPort;
import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowRepositoryPort;
import com.gasfgrv.autorizador.events.infrastructure.dtos.kafka.TopicoEventoPayload;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {"spring.cloud.aws.sqs.queue=fila-teste-autorizador"})
class AutorizadorListenerTest {

    @Autowired
    private SqsTemplate sqsTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkflowRepositoryPort workflowRepositoryPort;

    @MockitoBean
    private AutorizadorInputPort autorizadorInputPort;

    @Test
    void listenerDeveProcessarMensagemSqsComSucessoESalvarContextoDoWorkflow() {
        String payload = montaPayload();
        Map<String, Object> payloadComoMapa = converterParaMapa(payload);

        String taskToken = (String) payloadComoMapa.get("taskToken");
        String executionArn = (String) payloadComoMapa.get("executionArn");

        sqsTemplate.send("fila-teste-autorizador", payload);

        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    verify(workflowRepositoryPort).salvarContextoWorkflow(taskToken, executionArn);
                    verify(autorizadorInputPort).autorizarPedido(any(), eq(taskToken));
                });
    }

    private Map<String, Object> converterParaMapa(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            fail(e.getMessage(), e);
            return null;
        }
    }

    private String montaPayload() {
        TopicoEventoPayload payloadSimulado = new TopicoEventoPayload(
                "1234567",
                "98766",
                LocalDate.of(2024, 1, 14),
                BigDecimal.valueOf(100L),
                "FIRSTNAME LASTNAME",
                "1234 1234 1234 1234",
                "XX/YY",
                "123",
                true
        );

        try {
            ObjectNode jsonNode = objectMapper.valueToTree(payloadSimulado);
            jsonNode.put("taskToken", "token-123-xyz");
            jsonNode.put("executionArn", "arn:aws:states:us-east-1:123456789012:execution:flow");
            return objectMapper.writeValueAsString(payloadSimulado);
        } catch (JsonProcessingException e) {
            fail(e.getMessage(), e);
            return "";
        }
    }

}