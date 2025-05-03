package com.example.kafkauserpoc.config;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class GraphQLConfig {
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
            .scalar(localDateTimeScalar());
    }
    
    private GraphQLScalarType localDateTimeScalar() {
        return GraphQLScalarType.newScalar()
            .name("DateTime")
            .description("LocalDateTime scalar type")
            .coercing(new Coercing<LocalDateTime, String>() {
                @Override
                public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
                    if (dataFetcherResult instanceof LocalDateTime) {
                        return ((LocalDateTime) dataFetcherResult).format(formatter);
                    }
                    throw new CoercingSerializeException("Expected LocalDateTime object");
                }

                @Override
                public LocalDateTime parseValue(Object input) throws CoercingParseValueException {
                    try {
                        return LocalDateTime.parse(input.toString(), formatter);
                    } catch (Exception e) {
                        throw new CoercingParseValueException("Invalid LocalDateTime format");
                    }
                }

                @Override
                public LocalDateTime parseLiteral(Object input) throws CoercingParseLiteralException {
                    try {
                        return LocalDateTime.parse(input.toString(), formatter);
                    } catch (Exception e) {
                        throw new CoercingParseLiteralException("Invalid LocalDateTime format");
                    }
                }
            })
            .build();
    }
} 