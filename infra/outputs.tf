output "namespace" {
  description = "Namespace criado"
  value       = kubernetes_namespace.this.metadata[0].name
}

output "app_deployment_name" {
  description = "Nome do deployment da aplicação"
  value       = kubernetes_deployment.app.metadata[0].name
}

output "app_service_name" {
  description = "Nome do service da aplicação"
  value       = kubernetes_service.app.metadata[0].name
}

output "postgres_deployment_name" {
  description = "Nome do deployment do PostgreSQL"
  value       = kubernetes_deployment.postgres.metadata[0].name
}

output "postgres_service_name" {
  description = "Nome do service do PostgreSQL"
  value       = kubernetes_service.postgres.metadata[0].name
}

output "hpa_name" {
  description = "Nome do HPA"
  value       = kubernetes_horizontal_pod_autoscaler_v2.app.metadata[0].name
}

output "kubectl_get_pods" {
  description = "Comando para listar pods"
  value       = "kubectl get pods -n ${kubernetes_namespace.this.metadata[0].name}"
}

output "kubectl_get_services" {
  description = "Comando para listar services"
  value       = "kubectl get svc -n ${kubernetes_namespace.this.metadata[0].name}"
}

output "kubectl_get_hpa" {
  description = "Comando para listar HPA"
  value       = "kubectl get hpa -n ${kubernetes_namespace.this.metadata[0].name}"
}
