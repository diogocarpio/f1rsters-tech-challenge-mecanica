variable "enabled" {
  description = "Controla a instalação do New Relic Kubernetes"
  type        = bool
  default     = true
}

variable "cluster_name" {
  description = "Nome que identificará o cluster no New Relic"
  type        = string
}

variable "namespace" {
  description = "Namespace da integração New Relic"
  type        = string
  default     = "newrelic"
}

variable "new_relic_license_key" {
  description = "License key usada pela integração Kubernetes"
  type        = string
  sensitive   = true
}

variable "nri_bundle_chart_version" {
  description = "Versão fixada do chart nri-bundle"
  type        = string
  default     = "8.0.24"
}

variable "enable_kube_events" {
  description = "Habilita coleta de eventos Kubernetes"
  type        = bool
  default     = true
}

variable "enable_low_data_mode" {
  description = "Reduz a quantidade de métricas enviadas"
  type        = bool
  default     = false
}
