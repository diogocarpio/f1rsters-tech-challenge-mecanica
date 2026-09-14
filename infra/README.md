# Infraestrutura Terraform - Tech Challenge Mecânica

Este diretório contém a configuração Terraform para provisionar a infraestrutura Kubernetes necessária para executar a aplicação. Suporta tanto clusters locais (Docker Desktop, Kind, Minikube, K3d) quanto cloud providers (EKS, GKE, AKS).

## Pré-requisitos

- Terraform >= 1.0
- kubectl instalado
- Cluster Kubernetes rodando (Kind, Minikube, K3d ou cloud provider)
- Docker instalado e rodando

## Provedor Utilizado

- **hashicorp/kubernetes**: Provider para gerenciar recursos Kubernetes

## Recursos Criados

- **Namespace**: Namespace isolado para a aplicação
- **ConfigMap**: Configurações não sensíveis da aplicação (SPRING_PROFILES_ACTIVE, JWT_ISSUER, etc.)
- **Secrets**: Dados sensíveis (JWT_SECRET, senhas de banco)
- **PersistentVolumeClaim**: Storage persistente para PostgreSQL
- **PostgreSQL Deployment**: Deployment do banco de dados PostgreSQL
- **PostgreSQL Service**: Service ClusterIP para o banco
- **App Deployment**: Deployment da aplicação com 2 réplicas, liveness/readiness probes
- **App Service**: Service NodePort para expor a aplicação
- **HPA**: Horizontal Pod Autoscaler para escalabilidade automática (CPU 70%, memória 75%)

## Variáveis

As variáveis podem ser configuradas em um arquivo `terraform.tfvars`. Arquivos de exemplo são fornecidos para diferentes ambientes:

### Para Docker Desktop (Cluster Local)

```bash
terraform apply -var-file="terraform.tfvars.docker-desktop"
```

Configurações principais:
- `postgres_storage_class_name = "hostpath"` (Docker Desktop usa hostpath)
- `app_service_type = "NodePort"` (Docker Desktop não suporta LoadBalancer nativo)
- `app_service_node_port = 30001` (Porta para acessar a aplicação)
- `hpa_min_replicas = 1`, `hpa_max_replicas = 1` (HPA desabilitado por padrão)

### Para Cloud Providers (EKS, GKE, AKS)

```bash
terraform apply -var-file="terraform.tfvars.cloud"
```

Configurações principais:
- `postgres_storage_class_name = "standard"` (Cloud providers usam standard)
- `app_service_type = "LoadBalancer"` (Cloud providers suportam LoadBalancer)
- `hpa_min_replicas = 1`, `hpa_max_replicas = 5` (HPA habilitado)

### Variáveis Principais

```hcl
project_name     = "oficina"
environment      = "production"
namespace        = "oficina"
app_image        = "f1rsters-tech-challenge-mecanica-app:latest"
app_replicas     = 2
postgres_password = "sua-senha-segura"
postgres_storage_class_name = "standard"  # Use "hostpath" para Docker Desktop
app_service_type = "LoadBalancer"         # Use "NodePort" para Docker Desktop
```

## Como Executar

### Inicializar o Terraform

```bash
cd infra
terraform init
```

### Validar a configuração

```bash
terraform validate
```

### Planejar as mudanças

```bash
terraform plan
```

### Aplicar a infraestrutura

```bash
terraform apply
```

### Destruir a infraestrutura

```bash
terraform destroy
```

## Comandos kubectl Úteis

Após aplicar o Terraform, use estes comandos para verificar os recursos:

```bash
# Listar pods
kubectl get pods -n oficina

# Listar services
kubectl get svc -n oficina

# Listar HPA
kubectl get hpa -n oficina

# Ver logs da aplicação
kubectl logs -n oficina -l app=oficina-app

# Acessar a aplicação (NodePort - Docker Desktop)
kubectl get svc oficina-app -n oficina
# Acesse em http://localhost:30001

# Acessar a aplicação (LoadBalancer - Cloud)
kubectl get svc oficina-app -n oficina
# Use o EXTERNAL-IP fornecido
```

## Deploy da Aplicação

O Terraform já cria todos os recursos necessários. Alternativamente, você pode usar os manifestos YAML diretamente:

### Para Cloud Providers (EKS, GKE, AKS)

```bash
cd ../k8s
kubectl apply -f namespace.yaml
kubectl apply -f app-configmap.yaml
kubectl apply -f app-secret.yaml
kubectl apply -f db-secret.yaml
kubectl apply -f db-pvc.yaml
kubectl apply -f db-deployment.yaml
kubectl apply -f db-service.yaml
kubectl apply -f app-deployment.yaml
kubectl apply -f app-service.yaml
kubectl apply -f app-hpa.yaml
```

### Para Docker Desktop (Cluster Local)

```bash
cd ../k8s
kubectl apply -f namespace.yaml
kubectl apply -f app-configmap.yaml
kubectl apply -f app-secret.yaml
kubectl apply -f db-secret.yaml
kubectl apply -f db-pvc-local.yaml      # Usa hostpath ao invés de standard
kubectl apply -f db-deployment.yaml
kubectl apply -f db-service.yaml
kubectl apply -f app-deployment.yaml
kubectl apply -f app-service-nodeport.yaml  # Usa NodePort ao invés de LoadBalancer
# Opcional: kubectl apply -f app-hpa.yaml (requer metrics-server instalado)
```

### Instalar Metrics-server (para HPA em Docker Desktop)

```bash
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
```

## Backend State

O estado do Terraform é armazenado localmente em `./terraform.tfstate`.

## Segurança

- Senhas e dados sensíveis são marcados como `sensitive` nas variáveis
- Secrets Kubernetes são usados para credenciais
- ConfigMaps para configurações não sensíveis
- Probes de health check configurados para resiliência

## Observabilidade New Relic

Para habilitar integração de observabilidade no cluster local ou cloud:

```hcl
enable_newrelic_k8s_integration = true
new_relic_license_key           = "<SUA_LICENSE_KEY>"
new_relic_cluster_name          = "oficina-local-k8s"
new_relic_enabled               = "true"
new_relic_app_name              = "tech-challenge-mecanica-k8s-local"
new_relic_metrics_enabled       = "true"
new_relic_account_id            = "<SEU_ACCOUNT_ID>"
new_relic_api_key               = "<SUA_API_KEY>"
```

A instalação usa o chart `nri-bundle` da New Relic via provider Helm e habilita coleta de recursos Kubernetes (CPU/memória), além de suportar health/uptime e logs/traces pela aplicação instrumentada.

## Diferenças entre Ambientes

### Docker Desktop
- **StorageClass**: `hostpath` (padrão do Docker Desktop)
- **Service Type**: `NodePort` (LoadBalancer não suportado nativamente)
- **HPA**: Opcional (requer instalação do metrics-server)
- **Acesso**: http://localhost:30001

### Cloud Providers (EKS, GKE, AKS)
- **StorageClass**: `standard` (ou provisionador da cloud)
- **Service Type**: `LoadBalancer` (suportado nativamente)
- **HPA**: Habilitado por padrão (metrics-server geralmente instalado)
- **Acesso**: Via EXTERNAL-IP do LoadBalancer
