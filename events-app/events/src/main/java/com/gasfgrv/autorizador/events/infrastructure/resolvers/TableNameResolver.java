package com.gasfgrv.autorizador.events.infrastructure.resolvers;

import com.gasfgrv.autorizador.events.infrastructure.annotations.TableName;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import org.springframework.stereotype.Component;

@Component
public class TableNameResolver implements DynamoDbTableNameResolver {

    @Override
    public <T> String resolve(Class<T> clazz) {
        TableName annotation = clazz.getAnnotation(TableName.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Missing @TableName annotation on class: " + clazz.getName());
        }

        return annotation.name();
    }

}
