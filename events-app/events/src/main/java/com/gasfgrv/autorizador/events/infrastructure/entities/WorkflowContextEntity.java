package com.gasfgrv.autorizador.events.infrastructure.entities;

import com.gasfgrv.autorizador.events.infrastructure.annotations.TableName;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@TableName(name="tb-autorizador-controle")
@NoArgsConstructor
@Setter
public class WorkflowContextEntity {

    private String executionId;
    private String taskId;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id_exec")
    public String getExecutionId() {
        return executionId;
    }

    @DynamoDbAttribute("id_task")
    public String getTaskId() {
        return taskId;
    }

}

