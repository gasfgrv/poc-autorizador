package com.gasfgrv.autorizador.events.infrastructure.adapters;

import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowRepositoryPort;
import com.gasfgrv.autorizador.events.infrastructure.dtos.sqs.WorkflowContextDto;
import com.gasfgrv.autorizador.events.infrastructure.entities.WorkflowContextEntity;
import com.gasfgrv.autorizador.events.infrastructure.mappers.WorkflowContextMapper;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkflowAdapter implements WorkflowRepositoryPort {

    private final DynamoDbTemplate db;
    private final WorkflowContextMapper mapper;

    @Override
    public void salvarContextoWorkflow(String taskToken, String executionArn) {
        WorkflowContextDto workflowContext = new WorkflowContextDto(taskToken, executionArn);
        WorkflowContextEntity entity = mapper.toEntity(workflowContext);

        log.info("Salvando workflow context");
        db.save(entity);
    }

    @Override
    public boolean buscarDadosDaExecucao(String taskToken) {
        Expression expression = Expression.builder()
                .expression("id_task = :taskToken")
                .expressionValues(Collections.singletonMap(":taskToken", AttributeValue.builder().s(taskToken).build()))
                .build();

        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();

        String taskId = db.scan(request, WorkflowContextEntity.class)
                .items()
                .stream()
                .findFirst()
                .map(WorkflowContextEntity::getTaskId)
                .orElse("");

        return taskToken.equals(taskId);
    }

}
