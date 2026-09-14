output "namespace" {
  value = var.enabled ? kubernetes_namespace_v1.newrelic[0].metadata[0].name : null
}

output "release_name" {
  value = var.enabled ? helm_release.newrelic_bundle[0].name : null
}

output "release_status" {
  value = var.enabled ? helm_release.newrelic_bundle[0].status : null
}
