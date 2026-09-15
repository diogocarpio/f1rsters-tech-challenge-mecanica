resource "kubernetes_namespace_v1" "newrelic" {
  count = var.enabled ? 1 : 0

  metadata {
    name = var.namespace

    labels = {
      "app.kubernetes.io/managed-by" = "terraform"
      "observability"                = "newrelic"
    }
  }
}

resource "kubernetes_secret_v1" "newrelic_license" {
  count = var.enabled ? 1 : 0

  metadata {
    name      = "newrelic-license"
    namespace = kubernetes_namespace_v1.newrelic[0].metadata[0].name
  }

  type = "Opaque"

  data = {
    license = var.new_relic_license_key
  }
}

resource "helm_release" "newrelic_bundle" {
  count = var.enabled ? 1 : 0

  name       = "newrelic-bundle"
  repository = "https://helm-charts.newrelic.com"
  chart      = "nri-bundle"
  version    = var.nri_bundle_chart_version
  namespace  = kubernetes_namespace_v1.newrelic[0].metadata[0].name

  atomic          = true
  cleanup_on_fail = true
  wait            = true
  timeout         = 600

  values = [
    yamlencode({
      global = {
        cluster                = var.cluster_name
        customSecretName       = kubernetes_secret_v1.newrelic_license[0].metadata[0].name
        customSecretLicenseKey = "license"
        lowDataMode            = var.enable_low_data_mode
      }

      "newrelic-infrastructure" = {
        enabled = true
      }

      "kube-state-metrics" = {
        enabled = true
      }

      "nri-metadata-injection" = {
        enabled = true
      }

      "nri-kube-events" = {
        enabled = var.enable_kube_events
      }

      "newrelic-logging" = {
        enabled = false
      }

      "nri-prometheus" = {
        enabled = false
      }

      "newrelic-prometheus-agent" = {
        enabled = false
      }

      "newrelic-pixie" = {
        enabled = false
      }

      "nr-ebpf-agent" = {
        enabled = false
      }
    })
  ]

  depends_on = [
    kubernetes_secret_v1.newrelic_license
  ]
}
