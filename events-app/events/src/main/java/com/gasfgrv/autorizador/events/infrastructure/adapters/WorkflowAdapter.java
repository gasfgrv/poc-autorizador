package com.gasfgrv.autorizador.events.infrastructure.adapters;

import com.gasfgrv.autorizador.events.domain.ports.out.WorkflowRepositoryPort;
import com.gasfgrv.autorizador.events.infrastructure.dtos.sqs.WorkflowContextDto;
import com.gasfgrv.autorizador.events.infrastructure.entities.WorkflowContextEntity;
import com.gasfgrv.autorizador.events.infrastructure.mappers.WorkflowContextMapper;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

}
