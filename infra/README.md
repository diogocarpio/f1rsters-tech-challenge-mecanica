# Infraestrutura Terraform - Tech Challenge Mecânica

Este diretório contém a configuração Terraform para provisionar a infraestrutura Kubernetes necessária para executar a aplicação em cluster local (Kind, Minikube ou K3d).

## Pré-requisitos

- Terraform >= 1.0
- kubectl instalado
- Cluster Kubernetes local rodando (Kind, Minikube ou K3d)
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

As variáveis podem ser configuradas em um arquivo `terraform.tfvars`:

```hcl
project_name     = "oficina"
environment      = "production"
namespace        = "oficina"
app_image        = "f1rsters-tech-challenge-mecanica-app:latest"
app_replicas     = 2
postgres_password = "sua-senha-segura"
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

# Acessar a aplicação (NodePort)
kubectl get svc oficina-app -n oficina
```

## Deploy da Aplicação

O Terraform já cria todos os recursos necessários. Alternativamente, você pode usar os manifestos YAML diretamente:

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

## Backend State

O estado do Terraform é armazenado localmente em `./terraform.tfstate`.

## Segurança

- Senhas e dados sensíveis são marcados como `sensitive` nas variáveis
- Secrets Kubernetes são usados para credenciais
- ConfigMaps para configurações não sensíveis
- Probes de health check configurados para resiliência
