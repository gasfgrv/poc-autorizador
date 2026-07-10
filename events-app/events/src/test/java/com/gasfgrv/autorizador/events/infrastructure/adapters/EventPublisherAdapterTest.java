package com.gasfgrv.autorizador.events.infrastructure.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.infrastructure.dtos.kafka.TopicoComandoPayload;
import com.gasfgrv.autorizador.events.infrastructure.exceptions.PublisherException;
import com.gasfgrv.autorizador.events.infrastructure.mappers.PedidoMapper;
import com.gasfgrv.autorizador.events.infrastructure.properties.KafkaPublisherProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class EventPublisherAdapterTest {

    @Captor
    ArgumentCaptor<Message<String>> captor;

    @Mock
    private PedidoMapper pedidoMapper;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private KafkaPublisherProperties publisherProperties;

    @InjectMocks
    private EventPublisherAdapter adapter;

    private Pedido pedido;
    private TopicoComandoPayload payload;
    private String taskToken;
    private String topicName;

    @BeforeEach
    void setUp() {
        this.taskToken = "token-xyz-456";
        this.topicName = "autorizador-comando";
        this.pedido = Pedido.builder()
                .id("pedido-123")
                .clienteId("cliente-456")
                .dataPedido(LocalDate.of(2026, 7, 10))
                .total(BigDecimal.valueOf(150.0))
                .nomeCartao("NOME SOBRENOME")
                .numeroCartao("1234567890123456")
                .expiraEm("12/30")
                .cvv("123")
                .build();

        this.payload = new TopicoComandoPayload(
                "pedido-123",
                "cliente-456",
                LocalDate.of(2026, 7, 10),
                BigDecimal.valueOf(150.0),
                "NOME SOBRENOME",
                "1234567890123456",
                "12/30",
                "123"
        );

        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveEnviarEventoComSucessoEComHeadersCorretos(CapturedOutput output) throws JsonProcessingException {
        when(publisherProperties.name()).thenReturn(topicName);
        when(pedidoMapper.toCommandPayload(pedido)).thenReturn(payload);
        when(kafkaTemplate.send(any(Message.class))).thenReturn(CompletableFuture.completedFuture(null));

        adapter.enviarEvento(pedido, taskToken);

        assertTrue(output.getOut().contains("Enviando evento para: " + topicName));

        verify(kafkaTemplate).send(captor.capture());

        Message<String> message = captor.getValue();
        assertEquals(objectMapper.writeValueAsString(payload), message.getPayload());
        assertEquals(topicName, message.getHeaders().get(KafkaHeaders.TOPIC));
        assertArrayEquals(taskToken.getBytes(), (byte[]) message.getHeaders().get("taskToken"));
    }

    @Test
    void deveLancarPublisherExceptionQuandoOcorrerErroNoEnvio(CapturedOutput output) {
        when(publisherProperties.name()).thenReturn(topicName);
        when(pedidoMapper.toCommandPayload(pedido)).thenReturn(payload);
        when(kafkaTemplate.send(any(Message.class))).thenThrow(new RuntimeException("Kafka error"));

        assertThrows(PublisherException.class, () -> adapter.enviarEvento(pedido, taskToken));

        assertTrue(output.getOut().contains("Erro ao emitir pedido: Kafka error"));
    }
}
