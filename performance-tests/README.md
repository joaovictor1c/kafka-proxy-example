# Testes de Performance com k6

Este diretório contém scripts para realizar testes de performance na aplicação Kafka User POC, usando o k6 como ferramenta de teste.

## Pré-requisitos

1. [k6](https://k6.io/docs/getting-started/installation/) instalado
2. Extensão Kafka para k6 instalada
   ```bash
   k6 extensions install github.com/mostafa/xk6-kafka
   ```
3. Docker e Docker Compose instalados
4. Kafka e a aplicação Spring Boot em execução (porta 8085)

## Estrutura de arquivos

- `k6-user-create.js`: Script para criar 10.000 usuários enviando mensagens para o Kafka

## Como executar os testes

Os testes de monitoramento, verificação de conectividade e scripts de automação foram removidos para simplificação. Execute apenas o teste principal de performance:

1. Certifique-se de que o Kafka e a aplicação estão rodando normalmente.
2. Execute o teste principal:

```sh
k6 run k6-user-create.js
```

O teste irá:
- Enviar mensagens para o tópico Kafka `user-topic`
- Simular múltiplos usuários virtuais enviando mensagens simultaneamente
- Gerar milhares de mensagens ao longo da execução do teste

## Observações

- O monitoramento e a verificação de conectividade foram removidos para simplificação e foco no teste de performance principal.


## Interpretação dos resultados

Após a execução dos testes, o k6 exibirá um resumo dos resultados, incluindo:

- Taxa de sucesso das mensagens enviadas
- Número total de mensagens processadas
- Tempo de resposta médio, mínimo e máximo
- Distribuição dos tempos de resposta (percentis)
- Verificação das condições definidas nos thresholds

Um teste bem-sucedido deve mostrar:
- Mais de 9.000 mensagens enviadas com sucesso
- Tempo de resposta abaixo de 500ms para 95% das requisições
- Taxa de processamento consistente 