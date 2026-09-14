module "newrelic_kubernetes" {
  source = "../../modules/newrelic-kubernetes"

  enabled                  = var.enable_newrelic
  cluster_name             = var.cluster_name
  new_relic_license_key    = var.new_relic_license_key
  nri_bundle_chart_version = var.nri_bundle_chart_version
  enable_kube_events       = var.enable_newrelic_kube_events
  enable_low_data_mode     = var.newrelic_low_data_mode
}
