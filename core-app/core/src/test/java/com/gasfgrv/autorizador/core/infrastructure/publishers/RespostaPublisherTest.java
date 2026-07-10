package com.gasfgrv.autorizador.core.infrastructure.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gasfgrv.autorizador.core.containers.TestcontainersConfiguration;
import com.gasfgrv.autorizador.core.domain.entities.Resposta;
import com.gasfgrv.autorizador.core.domain.ports.out.RespostaSenderPort;
import com.gasfgrv.autorizador.core.infrastructure.dtos.TopicoEventoPayload;
import com.gasfgrv.autorizador.core.infrastructure.mappers.EventoMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ExtendWith(OutputCaptureExtension.class)
class RespostaPublisherTest {

    @Autowired
    private RespostaSenderPort publisher;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventoMapper eventoMapper;

    @Value("${spring.kafka.topic.publisher.name}")
    private String topicName;

    @Test
    @DisplayName("Producer deve enviar uma mensagem com sucesso contendo o payload e o header taskToken")
    void producerDeveEnviarUmaMensagem(CapturedOutput output) throws Exception {
        String taskToken = UUID.randomUUID().toString();
        Resposta resposta = new Resposta();

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

        when(eventoMapper.toEventoPayload(any(Resposta.class))).thenReturn(payloadSimulado);

        Consumer<String, String> consumer = consumerFactory.createConsumer("test-group", "client-test");
        consumer.subscribe(Collections.singletonList(topicName));
        consumer.poll(Duration.ofMillis(100));

        publisher.enviarResposta(resposta, taskToken);

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, topicName, Duration.ofSeconds(5));

        byte[] tokenRecebidoBytes = record.headers().lastHeader("taskToken").value();
        assertEquals(taskToken, new String(tokenRecebidoBytes));

        String mensagemEsperada = objectMapper.writeValueAsString(payloadSimulado);
        assertEquals(mensagemEsperada, record.value());

        assertTrue(output.getOut().contains("Enviando resposta para o tópico"));

        consumer.close();
    }

    @Test
    @DisplayName("Deve capturar Exception, gerar log de erro e relançar como RuntimeException")
    void producerDeveLancarExcecaoELogarErro(CapturedOutput output) {
        Resposta resposta = new Resposta();
        String taskToken = "token-falha";

        when(eventoMapper.toEventoPayload(any(Resposta.class)))
                .thenThrow(new RuntimeException("Falha no banco de dados / mapeamento"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                publisher.enviarResposta(resposta, taskToken)
        );

        assertEquals("java.lang.RuntimeException: Falha no banco de dados / mapeamento", exception.getMessage());
        assertTrue(output.getOut().contains("Erro ao enviar resposta: Falha no banco de dados / mapeamento"));
    }

}
