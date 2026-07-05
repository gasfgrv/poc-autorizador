package com.gasfgrv.autorizador.events.infrastructure.mappers;

import com.gasfgrv.autorizador.events.infrastructure.dtos.sqs.WorkflowContextDto;
import com.gasfgrv.autorizador.events.infrastructure.entities.WorkflowContextEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkflowContextMapper {

    @Mapping(target = "executionId", source = "executionArn")
    @Mapping(target = "taskId", source = "taskToken")
    WorkflowContextEntity toEntity(WorkflowContextDto dto);

}
