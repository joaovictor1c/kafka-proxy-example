# Kafka User POC

Esta é uma prova de conceito (POC) que demonstra como utilizar o Spring Kafka para consumir mensagens de um tópico e salvar usuários em um banco de dados.

## Tecnologias utilizadas

- Spring Boot 2.7.3
- Spring Kafka
- Spring Data JPA
- MySQL Database
- Lombok
- Jackson
- Docker/Docker Compose
- k6 (para testes de performance)

## Estrutura do projeto

- `model`: Contém a entidade `User`
- `repository`: Contém o repositório JPA para a entidade `User`
- `service`: Contém a lógica de negócio para salvar usuários
- `config`: Contém as configurações do Kafka e da aplicação
- `listener`: Contém o listener do Kafka que processa as mensagens
- `controller`: Contém os endpoints REST para consultar usuários
- `performance-tests`: Contém scripts de testes de performance usando k6

## Docker Compose

O projeto inclui um arquivo `docker-compose.yml` que configura o ambiente de desenvolvimento com:

- Kafka
- ZooKeeper
- Schema Registry
- Kafka Connect
- AKHQ (Interface de gerenciamento do Kafka)
- MySQL
- Adminer (Interface de gerenciamento do MySQL)

Para iniciar o ambiente:

```bash
docker-compose up -d
```

Para parar o ambiente:

```bash
docker-compose down
```

## Configuração

A aplicação está configurada para:

1. Conectar-se a um servidor Kafka local na porta 29092
2. Consumir mensagens do tópico `user-topic`
3. Salvar os usuários no banco de dados MySQL

## Como executar

1. Inicie o ambiente Docker:

```bash
docker-compose up -d
```

2. Execute a aplicação Spring Boot:

```bash
mvn spring-boot:run
```

3. A aplicação irá iniciar e começar a escutar mensagens no tópico `user-topic`

## Formato da mensagem

Para enviar um usuário para o tópico, use o seguinte formato JSON:

```json
{
  "name": "Nome do Usuário",
  "email": "usuario@exemplo.com",
  "username": "username"
}
```

## Verificando os usuários salvos

Após enviar mensagens para o tópico, você pode verificar os usuários salvos acessando:

```
GET http://localhost:8085/api/users
```

Ou acessar o Adminer em:

```
http://localhost:8081
```

Use as credenciais configuradas no arquivo `docker-compose.yml` (por padrão: `user/password`).

## Interface de gerenciamento do Kafka (AKHQ)

Para acessar a interface de gerenciamento do Kafka:

```
http://localhost:8080
```

Esta interface permite visualizar tópicos, grupos de consumidores, mensagens e outras informações do cluster Kafka.

## Testes de Performance

O projeto inclui scripts de teste de performance usando k6 na pasta `performance-tests`. Estes testes simulam a criação de 10.000 usuários e verificam se foram salvos corretamente.

### Executando todos os testes

Para facilitar a execução dos testes, incluímos scripts que executam todos os testes em sequência:

#### No Linux/MacOS:

```bash
cd performance-tests
chmod +x run-all-tests.sh
./run-all-tests.sh
```

#### No Windows:

```powershell
cd performance-tests
# Se necessário, ajuste a política de execução para rodar scripts PowerShell
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
.\run-all-tests.ps1
```

Estes scripts:
1. Verificam se todas as dependências estão instaladas e os serviços estão em execução
2. Iniciam o monitoramento em segundo plano
3. Executam o teste de criação de usuários
4. Executam o teste de verificação
5. Finalizam o monitoramento

Para mais informações sobre como executar os testes de performance individualmente, consulte o [README dos testes de performance](performance-tests/README.md). 