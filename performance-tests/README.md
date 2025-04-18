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

## Executando todos os testes automaticamente

### No Linux/MacOS:

```bash
cd performance-tests
chmod +x run-all-tests.sh
./run-all-tests.sh
```

### No Windows (PowerShell):

```powershell
cd performance-tests
# Se necessário, ajuste a política de execução para rodar scripts PowerShell
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
.\run-all-tests.ps1
```

### No Windows (CMD):

```cmd
cd performance-tests
run-all-tests.bat
```

O script automatizado irá:
1. Verificar se todas as dependências estão instaladas
2. Verificar se os serviços Docker estão em execução
3. Verificar a conectividade com o Kafka
4. Iniciar o monitoramento em segundo plano
5. Executar o teste de criação de usuários
6. Finalizar o monitoramento

## Executando todos os testes em paralelo (manualmente)

Para uma experiência mais completa, você pode executar os testes em terminais separados:

Terminal 1 (Monitoramento):
```bash
k6 run performance-tests/k6-user-monitor.js
```

Terminal 2 (Criação de usuários):
```bash
k6 run performance-tests/k6-user-create.js
```

## Solução de problemas comuns

Se você encontrar o erro "Produtor Kafka não disponível", verifique:

1. Se o Kafka está em execução (use `docker ps | grep kafka`)
2. Se você está usando o endereço correto do broker (por padrão: localhost:29092)
3. Se a extensão k6-kafka está instalada corretamente
4. Execute o script de verificação: `k6 run performance-tests/verify-kafka-connectivity.js`

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