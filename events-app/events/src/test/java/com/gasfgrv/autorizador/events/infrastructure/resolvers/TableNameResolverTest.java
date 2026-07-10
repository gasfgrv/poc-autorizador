package com.gasfgrv.autorizador.events.infrastructure.resolvers;

import com.gasfgrv.autorizador.events.infrastructure.annotations.TableName;
import io.awspring.cloud.dynamodb.DynamoDbTableNameResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TableNameResolverTest {

    @TableName(name = "tabela_de_teste_sucesso")
    private static class ClasseComAnotacao {
    }

    private static class ClasseSemAnotacao {
    }

    private DynamoDbTableNameResolver resolver;

    @BeforeEach
    void setUp() {
        this.resolver = new TableNameResolver();
    }

    @Test
    void resolverDeveRetornarNomeDaTabelaQuandoAnotacaoEstiverPresente() {
        String nomeTabela = resolver.resolve(ClasseComAnotacao.class);
        assertEquals("tabela_de_teste_sucesso", nomeTabela);
    }

    @Test
    void resolverDeveLancarExcecaoQuandoAnotacaoEstiverAusente() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> resolver.resolve(ClasseSemAnotacao.class));
        assertTrue(exception.getMessage().contains("Missing @TableName annotation on class"));
        assertTrue(exception.getMessage().contains(ClasseSemAnotacao.class.getName()));
    }

}
