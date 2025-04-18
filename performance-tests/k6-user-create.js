import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';
import kafka from 'k6/x/kafka';

// Configurações do Kafka
const bootstrapServers = ['localhost:29092'];
const topicName = 'user-topic';

// Configurações de segurança
const saslConfig = {
    username: 'admin',
    password: 'admin-secret',
    mechanism: 'PLAIN'
};

const sslConfig = {
    caCert: '',
    clientCert: '',
    clientKey: ''
};

// Métricas personalizadas
const successfulMessages = new Counter('successful_messages');
const failedMessages = new Counter('failed_messages');
const messageRate = new Rate('message_rate');
const messageLatency = new Trend('message_latency');

// Opções de execução do teste
export let options = {
  scenarios: {
    load_test: {
      executor: 'ramping-vus',
      startVUs: 10,
      stages: [
        { duration: '30s', target: 100 }, // Rampa até 100 VUs em 30 segundos
        { duration: '2m', target: 100 },  // Mantém 100 VUs por 2 minutos
        { duration: '30s', target: 0 },   // Reduz para 0 VUs em 30 segundos
      ],
    },
    stress_test: {
      executor: 'constant-arrival-rate',
      rate: 200, // Taxa de geração de requisições por segundo
      timeUnit: '1s',
      duration: '2m',
      preAllocatedVUs: 50,
      maxVUs: 200,
    },
  },
  thresholds: {
    'successful_messages': ['count>9500'], // Espera-se mais de 9500 mensagens com sucesso
    'message_rate': ['rate>0.95'], // Taxa de sucesso deve ser maior que 95%
    'message_latency': ['p(95)<1000'], // 95% das mensagens devem ser enviadas em menos de 1s
  },
};

// Função para gerar dados aleatórios de usuário
function generateUser(index) {
  const timestamp = new Date().toISOString();
  return {
    id: `user-${index}`,
    name: `User Test ${index}`,
    email: `user${index}@example.com`,
    username: `user_${index}`,
    createdAt: timestamp,
    metadata: {
      source: 'k6-performance-test',
      iteration: __ITER,
      vu: __VU
    }
  };
}

// Função de inicialização
export function setup() {
  console.log('Iniciando setup do teste de performance...');
  
  // Cria o produtor
  const producer = new kafka.Producer({
    brokers: bootstrapServers,
    topic: topicName,
    sasl: saslConfig,
    ssl: sslConfig,
  });

  console.log('Setup concluído com sucesso');
  return { producer };
}

// Função principal
export default function (data) {
  const { producer } = data;
  const startTime = new Date().getTime();
  
  // Gera um ID único para este usuário
  const userId = __VU * 10000 + __ITER;
  
  // Cria um objeto de usuário
  const user = generateUser(userId);
  const message = JSON.stringify(user);
  
  try {
    // Envia a mensagem para o Kafka
    const messages = [{
      value: message,
      key: `user-${userId}`,
    }];
    
    producer.produce(messages);
    
    const endTime = new Date().getTime();
    const latency = endTime - startTime;
    
    successfulMessages.add(1);
    messageRate.add(true);
    messageLatency.add(latency);
    
    if (__ITER % 100 === 0) {
      console.log(`[VU ${__VU}] Mensagem ${__ITER} enviada com sucesso. Latência: ${latency}ms`);
    }
    
    // Verifica o sucesso da operação
    check(producer, {
      'mensagem enviada com sucesso': () => true,
      'latência aceitável': () => latency < 1000,
    });
  } catch (err) {
    console.error(`[VU ${__VU}] Erro ao enviar mensagem ${__ITER}: ${err}`);
    failedMessages.add(1);
    messageRate.add(false);
    
    check(null, {
      'mensagem enviada com sucesso': () => false,
    });
  }
  
  // Pausa breve entre mensagens
  sleep(0.05);
}

// Função de limpeza
export function teardown(data) {
  console.log('Iniciando teardown...');
  const { producer } = data;
  
  if (producer) {
    producer.close();
  }
  
  console.log('Teste finalizado. Métricas:');
  console.log(`- Mensagens enviadas com sucesso: ${successfulMessages.values.count}`);
  console.log(`- Mensagens com falha: ${failedMessages.values.count}`);
  console.log(`- Taxa de sucesso: ${messageRate.values.rate * 100}%`);
  console.log(`- Latência média: ${messageLatency.values.avg}ms`);
  console.log(`- Latência p95: ${messageLatency.values.p95}ms`);
}