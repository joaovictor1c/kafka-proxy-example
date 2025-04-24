# Kafka User POC

Esta é uma prova de conceito (POC) que demonstra como utilizar o Spring Kafka para consumir mensagens de um tópico e salvar usuários em um banco de dados, implementando o padrão Outbox para garantir a consistência dos eventos.

## Tecnologias utilizadas

- Spring Boot 2.7.3
- Spring Kafka
- Spring Data JPA
- MySQL Database
- Lombok
- Jackson
- Docker/Docker Compose
- Debezium
- k6 (para testes de performance)

## Estrutura do projeto

- `model`: Contém a entidade `User` e `OutboxEvent`
- `repository`: Contém os repositórios JPA para as entidades
- `service`: Contém a lógica de negócio para salvar usuários e eventos
- `config`: Contém as configurações do Kafka, Debezium e da aplicação
- `listener`: Contém o listener do Kafka que processa as mensagens
- `controller`: Contém os endpoints REST e GraphQL para consultar usuários
- `performance-tests`: Contém scripts de testes de performance usando k6

## Docker Compose

O projeto inclui um arquivo `docker-compose.yml` que configura o ambiente de desenvolvimento com:

- Kafka
- ZooKeeper
- Schema Registry
- Debezium Connect
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

## Configuração do Banco de Dados

O projeto utiliza o padrão Outbox para garantir a consistência dos eventos. A tabela `outbox` é criada automaticamente com a seguinte estrutura:

```sql
CREATE TABLE outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255),
    aggregate_id VARCHAR(255),
    event_type VARCHAR(255),
    payload JSONB,
    created_at TIMESTAMP DEFAULT now(),
    sent BOOLEAN DEFAULT FALSE
);
```

## Configuração do Debezium

O Debezium está configurado para monitorar a tabela `outbox` e publicar eventos no Kafka. A configuração do connector é feita através da API do Kafka Connect:

```json
{
  "name": "outbox-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "user",
    "database.password": "password",
    "database.dbname": "userdb",
    "database.server.name": "mysql-server",
    "table.include.list": "userdb.outbox",
    "transforms": "route",
    "transforms.route.type": "io.debezium.transforms.outbox.EventRouter",
    "transforms.route.topic.regex": "(.*)",
    "transforms.route.topic.replacement": "events.$1"
  }
}
```

## Como executar

1. Inicie o ambiente Docker:

```bash
docker-compose up -d
```

2. Configure o Debezium connector:

```bash
curl -X POST -H "Content-Type: application/json" --data @debezium-connector.json http://localhost:8083/connectors
```

3. Execute a aplicação Spring Boot:

```bash
mvn spring-boot:run
```

## Formato dos Eventos

Quando um usuário é criado, um evento é gerado na tabela `outbox` com o seguinte formato:

```json
{
  "userId": "abc-123",
  "email": "ana@exemplo.com",
  "name": "Ana"
}
```

O evento é então publicado no tópico `events.UserCreated` do Kafka.

## Verificando os usuários e eventos

Após enviar mensagens para o tópico, você pode verificar:

1. Os usuários salvos acessando:
```
GET http://localhost:8085/api/users
```

2. Os eventos no Kafka através do AKHQ:
```
http://localhost:8080
```

3. O banco de dados através do Adminer:
```
http://localhost:8081
```

## Interface de gerenciamento do Kafka (AKHQ)

Para acessar a interface de gerenciamento do Kafka:

```
http://localhost:8080
```

Esta interface permite visualizar tópicos, grupos de consumidores, mensagens e outras informações do cluster Kafka.

## Testes de Performance

O projeto inclui scripts de teste de performance usando k6 na pasta `performance-tests`. Estes testes simulam a criação de 10.000 usuários e verificam se foram salvos corretamente.

Para mais informações sobre como executar os testes de performance individualmente, consulte o [README dos testes de performance](performance-tests/README.md).

# Java Application Deployment Guide

This repository contains the configuration files for deploying a Java application using Docker and Kubernetes.

## Project Structure

```
.
├── Dockerfile
├── k8s/
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
│   ├── hpa.yaml
│   ├── nginx-deployment.yaml
│   ├── nginx-service.yaml
│   └── nginx-configmap.yaml
├── adr/
│   ├── README.md
│   ├── template.md
│   ├── 0001-initial-architecture.md
│   ├── 0002-nginx-reverse-proxy.md
│   └── 0003-cors-implementation.md
└── README.md
```

## Docker Configuration

The application uses a multi-stage Dockerfile to optimize the image size:

### Build Stage
- Base image: `maven:3.8.4-openjdk-17-slim`
- Builds the application using Maven
- Skips tests during build process

### Run Stage
- Base image: `openjdk:17-slim`
- Copies the built JAR file from the build stage
- Exposes port 8080
- Runs the application using Java

### Building the Docker Image

```bash
docker build -t java-app .
```

## Kubernetes Configuration

### Prerequisites
- Kubernetes cluster
- kubectl configured
- Metrics server installed (for HPA)
- Docker registry access

### Components

1. **Deployment (`k8s/deployment.yaml`)**
   - Replicas: 2
   - Resource Limits:
     - CPU: 500m
     - Memory: 1Gi
   - Resource Requests:
     - CPU: 250m
     - Memory: 512Mi
   - Health Checks:
     - Liveness Probe: `/actuator/health`
     - Readiness Probe: `/actuator/health`

2. **Service (`k8s/service.yaml`)**
   - Type: LoadBalancer
   - Port: 80 -> 8080 (target)
   - Selector: `app: java-app`

3. **ConfigMap (`k8s/configmap.yaml`)**
   - Application properties configuration
   - Spring Boot settings
   - Management endpoints configuration

4. **HorizontalPodAutoscaler (`k8s/hpa.yaml`)**
   - Min replicas: 2
   - Max replicas: 10
   - Target CPU utilization: 80%

5. **Nginx Configuration**
   
   **Deployment (`k8s/nginx-deployment.yaml`)**
   - Image: nginx:1.25-alpine
   - Replicas: 2
   - Resource Limits:
     - CPU: 200m
     - Memory: 256Mi
   - Resource Requests:
     - CPU: 100m
     - Memory: 128Mi
   - Health Checks:
     - Liveness Probe: `/health`
     - Readiness Probe: `/health`
   
   **Service (`k8s/nginx-service.yaml`)**
   - Type: LoadBalancer
   - Port: 80
   
   **ConfigMap (`k8s/nginx-configmap.yaml`)**
   - Nginx configuration with reverse proxy settings
   - Load balancing to Java application
   - Custom access and error logging
   - Health check endpoint

### Deployment Steps

1. Apply all Kubernetes manifests:
```bash
kubectl apply -f k8s/
```

2. Verify the deployment:
```bash
kubectl get pods
kubectl get services
kubectl get hpa
```

3. Monitor the application:
```bash
kubectl get pods -w
kubectl describe deployment java-app
kubectl describe deployment nginx
```

### Accessing the Application

The application can be accessed through the Nginx LoadBalancer service:

```bash
kubectl get service nginx-service
```

## Architecture

The deployment follows this architecture:

```
Internet -> Nginx LoadBalancer -> Nginx Pods -> Java App Service -> Java App Pods
```

Benefits of this setup:
- Load balancing at both Nginx and application level
- SSL termination capability at Nginx
- Enhanced security with reverse proxy
- Better control over HTTP headers and request routing
- Separate scaling for Nginx and application layers

## Health Checks and Monitoring

The application includes:
- Liveness probe: Checks if the application is running
- Readiness probe: Checks if the application is ready to accept traffic
- Spring Boot Actuator endpoints enabled:
  - /actuator/health
  - /actuator/info
  - /actuator/metrics
- Nginx health check at `/health`

## Scaling

The application automatically scales based on CPU utilization:
- Scales up when CPU usage exceeds 80%
- Maintains between 2 and 10 replicas
- Scaling decisions are made by the HorizontalPodAutoscaler
- Nginx and Java application scale independently

## Troubleshooting

Common commands for troubleshooting:

```bash
# Check pod logs
kubectl logs <pod-name>

# Check pod details
kubectl describe pod <pod-name>

# Check HPA status
kubectl describe hpa java-app-hpa

# Check ConfigMap
kubectl describe configmap java-app-config
kubectl describe configmap nginx-config

# Check Nginx logs
kubectl logs -l app=nginx
```

## Notes

- Ensure your Kubernetes cluster has enough resources for the specified requests and limits
- The application uses Spring Boot Actuator for health monitoring
- ConfigMap contains application-specific configurations
- Both services are exposed via LoadBalancer - ensure your cluster supports this service type
- Nginx is configured with proper proxy headers for better integration with Spring Boot
- The Nginx configuration includes basic security headers and logging

## CORS Configuration

The application implements CORS (Cross-Origin Resource Sharing) at the Nginx level for secure cross-origin requests.

### Allowed Origins

By default, the following origins are allowed:
- `http://localhost` (and any port)
- `https://your-domain.com`

To add more allowed origins:
1. Edit the `nginx-configmap.yaml` file
2. Add new origins to the `map $http_origin $cors_origin` block
3. Apply the changes with `kubectl apply -f k8s/nginx-configmap.yaml`

### CORS Headers

The following CORS headers are configured:
- `Access-Control-Allow-Origin`: Dynamic based on whitelist
- `Access-Control-Allow-Methods`: GET, POST, PUT, DELETE, OPTIONS
- `Access-Control-Allow-Headers`: Authorization, Content-Type, Accept, Origin, X-Requested-With
- `Access-Control-Max-Age`: 86400 (24 hours)
- `Access-Control-Expose-Headers`: Content-Length, Content-Range

### Pre-flight Requests

OPTIONS requests are handled automatically with:
- Cached pre-flight responses (24-hour duration)
- Proper response headers
- Zero-length response body

## Security Notes

- CORS is configured with a whitelist-based approach
- Regular monitoring of CORS rejections is recommended
- Review and update allowed origins as needed
- Keep Nginx and application configurations in sync
- Monitor for any security-related CORS issues

## Observability with OpenTelemetry

The application is instrumented with OpenTelemetry for comprehensive observability.

### Components

1. **Jaeger Backend**
   - All-in-one deployment for development
   - UI available at `http://<cluster-ip>:16686`
   - OTLP endpoints:
     - gRPC: 4317
     - HTTP: 4318

2. **Java Agent**
   - Automatic instrumentation of:
     - Spring Boot components
     - Kafka operations
     - Database queries
     - HTTP endpoints

3. **Custom Instrumentation**
   - User creation flow with detailed spans:
     - Validation phase
     - Database operations
     - Success/failure tracking
   - Custom attributes for business context
   - Error tracking and status codes

### Configuration

OpenTelemetry is configured via:
- Java agent configuration
- Application properties
- Custom span creation in code

Key settings:
```properties
otel.service.name=java-app
otel.traces.exporter=otlp
otel.exporter.otlp.endpoint=http://jaeger:4317
```

### Viewing Traces

1. Access the Jaeger UI:
```bash
kubectl port-forward svc/jaeger 16686:16686
```

2. Open `http://localhost:16686` in your browser

3. Select 'java-app' service to view traces

### Custom Spans

The application includes custom spans for:
- User creation process
- Validation steps
- Database operations
- Error handling

## Monitoring and Alerts

- Monitor trace latency in Jaeger UI
- Set up alerts for error rates
- Track span duration for performance
- Monitor resource usage of OpenTelemetry components 
