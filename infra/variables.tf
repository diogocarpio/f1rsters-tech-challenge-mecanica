variable "project_name" {
  description = "Nome do projeto"
  type        = string
  default     = "oficina"
}

variable "environment" {
  description = "Ambiente de deployment"
  type        = string
  default     = "production"
}

variable "kubeconfig_path" {
  description = "Caminho para o arquivo kubeconfig"
  type        = string
  default     = "~/.kube/config"
}

variable "namespace" {
  description = "Namespace Kubernetes"
  type        = string
  default     = "oficina"
}

variable "app_image" {
  description = "Imagem Docker da aplicação"
  type        = string
  default     = "f1rsters-tech-challenge-mecanica-app:latest"
}

variable "app_replicas" {
  description = "Número de réplicas iniciais da aplicação"
  type        = number
  default     = 2
}

# ConfigMap variables
variable "spring_profiles_active" {
  description = "Perfil Spring ativo"
  type        = string
  default     = "prod"
}

variable "app_env" {
  description = "Ambiente da aplicação"
  type        = string
  default     = "production"
}

variable "log_level" {
  description = "Nível de log"
  type        = string
  default     = "INFO"
}

variable "jwt_issuer" {
  description = "Issuer do JWT"
  type        = string
  default     = "tech-challenge-mecanica-api"
}

variable "jwt_access_token_minutes" {
  description = "Tempo de vida do token em minutos"
  type        = string
  default     = "15"
}

variable "security_seed_enabled" {
  description = "Habilitar seed de dados de segurança"
  type        = string
  default     = "true"
}

variable "security_seed_admin_email" {
  description = "Email do admin para seed"
  type        = string
  default     = "admin@oficina.local"
}

# Secret variables
variable "jwt_secret_base64" {
  description = "Secret JWT em Base64"
  type        = string
  sensitive   = true
  default     = "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo0NTY3ODkwQUJDREVG"
}

variable "security_seed_admin_password" {
  description = "Senha do admin para seed"
  type        = string
  sensitive   = true
  default     = "admin123"
}

# Database variables
variable "postgres_db" {
  description = "Nome do banco PostgreSQL"
  type        = string
  default     = "oficina"
}

variable "postgres_user" {
  description = "Usuário do PostgreSQL"
  type        = string
  default     = "oficinauser"
}

variable "postgres_password" {
  description = "Senha do PostgreSQL"
  type        = string
  sensitive   = true
  default     = "oficinapassword"
}

variable "postgres_storage_size" {
  description = "Tamanho do storage do PostgreSQL"
  type        = string
  default     = "1Gi"
}

variable "postgres_storage_class_name" {
  description = "StorageClass usada pelo PVC do PostgreSQL"
  type        = string
  default     = "standard"
}

# HPA variables
variable "hpa_min_replicas" {
  description = "Número mínimo de réplicas do HPA"
  type        = number
  default     = 1
}

variable "hpa_max_replicas" {
  description = "Número máximo de réplicas do HPA"
  type        = number
  default     = 5
}

variable "hpa_cpu_target" {
  description = "Target de utilização de CPU do HPA"
  type        = number
  default     = 70
}

variable "hpa_memory_target" {
  description = "Target de utilização de memória do HPA"
  type        = number
  default     = 75
}
