package com.gasfgrv.autorizador.events.infrastructure.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gasfgrv.autorizador.events.domain.entities.Pedido;
import com.gasfgrv.autorizador.events.infrastructure.exceptions.WorkflowResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;
import software.amazon.awssdk.services.sfn.model.SendTaskFailureRequest;
import software.amazon.awssdk.services.sfn.model.SendTaskFailureResponse;
import software.amazon.awssdk.services.sfn.model.SendTaskSuccessRequest;
import software.amazon.awssdk.services.sfn.model.SendTaskSuccessResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class WorkflowResponseAdapterTest {

    @Mock
    private SfnAsyncClient sfnClient;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private WorkflowResponseAdapter adapter;

    @Captor
    ArgumentCaptor<Consumer<SendTaskSuccessRequest.Builder>> captorSuccess;

    @Captor
    ArgumentCaptor<Consumer<SendTaskFailureRequest.Builder>> captorFailure;

    private String taskToken;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        this.taskToken = "test-token-123";
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

        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void adapterDeveResponderComSucessoDeveEnviarMensagemComSucesso() throws JsonProcessingException {
        CompletableFuture<SendTaskSuccessResponse> successFuture = CompletableFuture
                .completedFuture(SendTaskSuccessResponse.builder().build());

        when(sfnClient.sendTaskSuccess(any(Consumer.class))).thenReturn(successFuture);

        adapter.responderComSucesso(taskToken, pedido);

        verify(sfnClient).sendTaskSuccess(captorSuccess.capture());

        SendTaskSuccessRequest.Builder builder = SendTaskSuccessRequest.builder();
        captorSuccess.getValue().accept(builder);
        SendTaskSuccessRequest request = builder.build();

        assertEquals(taskToken, request.taskToken());
        assertEquals(objectMapper.writeValueAsString(pedido), request.output());
    }

    @Test
    void adapterDeveResponderComSucessoDeveLancarWorkflowResponseExceptionQuandoOcorrerErroDeSerializacao(CapturedOutput output) throws JsonProcessingException {
        doThrow(new RuntimeException("Simulated serialization error"))
                .when(objectMapper).writeValueAsString(any());

        assertThrows(WorkflowResponseException.class, () ->
                adapter.responderComSucesso(taskToken, pedido)
        );

        assertTrue(output.getOut().contains("Erro ao processar os dados dos pedidos"));
        verify(sfnClient, never()).sendTaskSuccess(any(Consumer.class));
    }

    @Test
    void adapterDeveResponderComFalhaDeveEnviarMensagemComFalha() {
        CompletableFuture<SendTaskFailureResponse> failureFuture = CompletableFuture
                .completedFuture(SendTaskFailureResponse.builder().build());

        when(sfnClient.sendTaskFailure(any(Consumer.class))).thenReturn(failureFuture);

        adapter.responderComFalha(taskToken);

        verify(sfnClient).sendTaskFailure(captorFailure.capture());

        SendTaskFailureRequest.Builder builder = SendTaskFailureRequest.builder();
        captorFailure.getValue().accept(builder);
        SendTaskFailureRequest request = builder.build();

        assertEquals(taskToken, request.taskToken());
    }
}
