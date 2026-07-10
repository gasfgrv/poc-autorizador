package com.gasfgrv.autorizador.events.infrastructure.adapters;

import com.gasfgrv.autorizador.events.infrastructure.dtos.sqs.WorkflowContextDto;
import com.gasfgrv.autorizador.events.infrastructure.entities.WorkflowContextEntity;
import com.gasfgrv.autorizador.events.infrastructure.mappers.WorkflowContextMapper;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class WorkflowRepositoryAdapterTest {

    @Mock
    private DynamoDbTemplate db;

    @Mock
    private WorkflowContextMapper mapper;

    @InjectMocks
    private WorkflowRepositoryAdapter adapter;

    private String taskToken;
    private String executionArn;
    private WorkflowContextEntity entity;

    @BeforeEach
    void setUp() {
        this.taskToken = "token-123";
        this.executionArn = "arn-abc";
        this.entity = new WorkflowContextEntity();
        this.entity.setTaskId(taskToken);
        this.entity.setExecutionId(executionArn);
    }

    @Test
    void deveSalvarContextoWorkflowComSucesso(CapturedOutput output) {
        when(mapper.toEntity(any(WorkflowContextDto.class))).thenReturn(entity);

        adapter.salvarContextoWorkflow(taskToken, executionArn);

        assertTrue(output.getOut().contains("Salvando workflow context"));
        verify(mapper).toEntity(argThat(dto -> taskToken.equals(dto.taskToken())
                && executionArn.equals(dto.executionArn())));
        verify(db).save(entity);
    }

    @Test
    void deveBuscarDadosDaExecucaoERetornarVerdadeiroQuandoEncontrado(CapturedOutput output) {
        PageIterable<WorkflowContextEntity> pageIterable = mock(PageIterable.class);
        SdkIterable<WorkflowContextEntity> sdkIterable = mock(SdkIterable.class);

        when(db.scan(any(ScanEnhancedRequest.class), eq(WorkflowContextEntity.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(Stream.of(entity));

        boolean resultado = adapter.buscarDadosDaExecucao(taskToken);

        assertTrue(resultado);
        assertTrue(output.getOut().contains("Buscando workflow context"));
        assertTrue(output.getOut().contains("busca por task id conclída: " + taskToken));
        verify(db).scan(any(ScanEnhancedRequest.class), eq(WorkflowContextEntity.class));
    }

    @Test
    void deveBuscarDadosDaExecucaoERetornarFalsoQuandoNaoEncontrado(CapturedOutput output) {
        PageIterable<WorkflowContextEntity> pageIterable = mock(PageIterable.class);
        SdkIterable<WorkflowContextEntity> sdkIterable = mock(SdkIterable.class);

        when(db.scan(any(ScanEnhancedRequest.class), eq(WorkflowContextEntity.class))).thenReturn(pageIterable);
        when(pageIterable.items()).thenReturn(sdkIterable);
        when(sdkIterable.stream()).thenReturn(Stream.empty());

        boolean resultado = adapter.buscarDadosDaExecucao(taskToken);

        assertFalse(resultado);
        assertTrue(output.getOut().contains("Buscando workflow context"));
        assertTrue(output.getOut().contains("busca por task id conclída: "));
        verify(db).scan(any(ScanEnhancedRequest.class), eq(WorkflowContextEntity.class));
    }
}
