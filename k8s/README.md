# Kubernetes Configuration Structure

This directory contains Kubernetes manifests organized by context and responsibility.

## Directory Structure

```
k8s/
├── application/           # Java application components
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── configmap.yaml
│   ├── hpa.yaml
│   └── kustomization.yaml
├── monitoring/           # Observability components
│   ├── jaeger-deployment.yaml
│   ├── jaeger-service.yaml
│   └── kustomization.yaml
├── ingress/             # Ingress components
│   ├── nginx-deployment.yaml
│   ├── nginx-service.yaml
│   ├── nginx-configmap.yaml
│   └── kustomization.yaml
├── namespaces/          # Namespace definitions
│   └── namespaces.yaml
├── kustomization.yaml   # Root kustomization
└── README.md
```

## Namespaces

1. **application**
   - Contains the Java application components
   - User service and related configurations
   - Horizontal Pod Autoscaler

2. **monitoring**
   - Contains observability components
   - Jaeger for distributed tracing
   - Future monitoring tools

3. **ingress**
   - Contains ingress-related components
   - Nginx reverse proxy
   - Load balancing configuration

## Deployment

To deploy all components:

```bash
kubectl apply -k k8s/
```

To deploy specific components:

```bash
# Deploy only application
kubectl apply -k k8s/application/

# Deploy only monitoring
kubectl apply -k k8s/monitoring/

# Deploy only ingress
kubectl apply -k k8s/ingress/
```

## Namespace Communication

- Application is accessible within cluster via: `java-app-service.application.svc.cluster.local`
- Jaeger is accessible via: `jaeger.monitoring.svc.cluster.local`
- Nginx ingress via: `nginx-service.ingress.svc.cluster.local`

## Labels and Selectors

Common labels are applied through kustomize:
- `app.kubernetes.io/name`: Component name
- `app.kubernetes.io/part-of`: Logical group
- `app.kubernetes.io/managed-by`: Management tool

## Configuration

Each context has its own ConfigMap:
- Application config in `application` namespace
- Nginx config in `ingress` namespace

## Monitoring

Jaeger UI is available at:
- In-cluster: `http://jaeger.monitoring:16686`
- Port-forward: `kubectl port-forward -n monitoring svc/jaeger 16686:16686` 