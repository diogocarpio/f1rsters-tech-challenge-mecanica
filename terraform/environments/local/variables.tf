variable "kubeconfig_path" {
  description = "Caminho para o arquivo kubeconfig"
  type        = string
  default     = "~/.kube/config"
}

variable "kube_context" {
  description = "Contexto Kubernetes a ser utilizado"
  type        = string
}

variable "enable_newrelic" {
  description = "Habilita a instalação do New Relic Kubernetes"
  type        = bool
  default     = true
}

variable "cluster_name" {
  description = "Nome que identificará o cluster no New Relic"
  type        = string
}

variable "new_relic_license_key" {
  description = "License key New Relic"
  type        = string
  sensitive   = true
}

variable "nri_bundle_chart_version" {
  description = "Versão do chart nri-bundle"
  type        = string
  default     = "8.0.24"
}

variable "enable_newrelic_kube_events" {
  description = "Habilita coleta de eventos Kubernetes"
  type        = bool
  default     = true
}

variable "newrelic_low_data_mode" {
  description = "Reduz a quantidade de métricas enviadas"
  type        = bool
  default     = false
}
