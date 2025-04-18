# Guia de Uso da Collection do Postman

Esta collection do Postman permite testar a API do Kafka User POC e realizar operações relacionadas ao Kafka.

## Arquivos

- `Kafka-User-POC.postman_collection.json`: Collection com todas as requisições
- `Kafka-User-POC.postman_environment.json`: Variáveis de ambiente configuradas para ambiente local

## Como Importar

1. Abra o Postman
2. Clique em "Import" no canto superior esquerdo
3. Arraste e solte os arquivos `Kafka-User-POC.postman_collection.json` e `Kafka-User-POC.postman_environment.json`
4. Confirme a importação

## Configuração do Ambiente

Após importar, certifique-se de selecionar o ambiente "Kafka User POC - Local" no canto superior direito do Postman.

Se necessário, você pode ajustar as variáveis de ambiente clicando no ícone de olho próximo ao seletor de ambiente e então em "Edit".

Variáveis disponíveis:
- `base_url`: URL base da API Spring Boot (padrão: http://localhost:8085)
- `kafka_rest_proxy_url`: URL do Kafka REST Proxy (padrão: http://localhost:8084)

## Verificando a Configuração

Antes de usar as requisições Kafka, certifique-se de que:

1. O ambiente Docker está em execução com todos os serviços (incluindo o kafka-rest-proxy)
2. A aplicação Spring Boot está rodando na porta 8085
3. O Kafka REST Proxy está acessível na porta 8084

Você pode verificar a conectividade executando:

```bash
# Verifica se o Kafka REST Proxy está respondendo
curl http://localhost:8084/topics

# Verifica se a aplicação Spring Boot está respondendo
curl http://localhost:8085/api/users
```

## Requisições Disponíveis

### API Endpoints

- **Listar todos os usuários**: Obtém a lista de todos os usuários salvos no banco de dados.

### Operações Kafka

- **Enviar usuário para tópico Kafka**: Envia um novo usuário para o tópico `user-topic`.
- **Verificar tópicos do Kafka**: Lista todos os tópicos disponíveis no Kafka.
- **Consumir mensagens do tópico**: Lê as mensagens do tópico `user-topic`.

### Testes de Performance

- **Verificar contagem de usuários**: Útil para monitorar o número de usuários durante testes de performance.

## Solução de Problemas

Se você encontrar problemas ao inserir dados:

1. **Verificação do serviço Kafka**
   - Certifique-se de que o serviço Kafka está em execução: `docker ps | grep kafka`
   - Verifique logs do Kafka: `docker logs kafka`

2. **Verificação do Kafka REST Proxy**
   - Certifique-se de que o serviço está em execução: `docker ps | grep kafka-rest-proxy`
   - Verifique logs: `docker logs kafka-rest-proxy`

3. **Teste direto de envio de mensagem**
   - Use o script k6 de diagnóstico: `k6 run performance-tests/verify-kafka-connectivity.js`
   - Este script verifica a conectividade e tenta enviar uma mensagem para o Kafka

4. **Verificação da aplicação Spring Boot**
   - Verifique se a aplicação está rodando e processando mensagens do Kafka
   - Examine os logs da aplicação para erros relacionados ao Kafka ou ao banco de dados

## Exemplos de Fluxo de Trabalho

### 1. Verificação básica da aplicação

1. Execute "Listar todos os usuários" para verificar se a aplicação está funcionando
2. Se estiver vazia, use "Enviar usuário para tópico Kafka" para adicionar alguns usuários
3. Execute "Listar todos os usuários" novamente para confirmar que foram processados

### 2. Monitoramento durante testes de performance

1. Execute "Verificar contagem de usuários" antes do teste para estabelecer uma linha de base
2. Execute os testes de performance com k6
3. Use "Verificar contagem de usuários" periodicamente para monitorar o progresso
4. Após os testes, verifique novamente para confirmar o número final de usuários 