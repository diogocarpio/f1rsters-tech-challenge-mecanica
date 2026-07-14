terraform {
  required_version = ">= 1.0"
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
  }
  backend "local" {
    path = "./terraform.tfstate"
  }
}

provider "kubernetes" {
  config_path = var.kubeconfig_path
}

# Namespace
resource "kubernetes_namespace" "this" {
  metadata {
    name = var.namespace
    labels = {
      name        = var.namespace
      environment = var.environment
      project     = var.project_name
    }
  }
}

# ConfigMap
resource "kubernetes_config_map" "app" {
  metadata {
    name      = "app-config"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  data = {
    SPRING_PROFILES_ACTIVE    = var.spring_profiles_active
    APP_ENV                   = var.app_env
    LOG_LEVEL                 = var.log_level
    JWT_ISSUER                = var.jwt_issuer
    JWT_ACCESS_TOKEN_MINUTES  = var.jwt_access_token_minutes
    SECURITY_SEED_ENABLED     = var.security_seed_enabled
    SECURITY_SEED_ADMIN_EMAIL = var.security_seed_admin_email
  }
}

# Secret - App
resource "kubernetes_secret" "app" {
  metadata {
    name      = "app-secret"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  type = "Opaque"

  data = {
    JWT_SECRET_BASE64            = var.jwt_secret_base64
    SECURITY_SEED_ADMIN_PASSWORD = var.security_seed_admin_password
  }
}

# Secret - Database
resource "kubernetes_secret" "db" {
  metadata {
    name      = "db-secret"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  type = "Opaque"

  data = {
    POSTGRES_DB       = var.postgres_db
    POSTGRES_USER     = var.postgres_user
    POSTGRES_PASSWORD = var.postgres_password
  }
}

# Persistent Volume Claim for PostgreSQL
resource "kubernetes_persistent_volume_claim" "postgres" {
  wait_until_bound = false

  metadata {
    name      = "postgres-pvc"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  spec {
    access_modes       = ["ReadWriteOnce"]
    storage_class_name = var.postgres_storage_class_name
    resources {
      requests = {
        storage = var.postgres_storage_size
      }
    }
  }
}

# PostgreSQL Deployment
resource "kubernetes_deployment" "postgres" {
  metadata {
    name      = "postgres-db"
    namespace = kubernetes_namespace.this.metadata[0].name
    labels = {
      app = "postgres-db"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "postgres-db"
      }
    }

    template {
      metadata {
        labels = {
          app = "postgres-db"
        }
      }

      spec {
        container {
          name  = "postgres"
          image = "postgres:15-alpine"

          port {
            container_port = 5432
          }

          env {
            name = "POSTGRES_DB"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db.metadata[0].name
                key  = "POSTGRES_DB"
              }
            }
          }

          env {
            name = "POSTGRES_USER"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }

          env {
            name = "POSTGRES_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }

          resources {
            requests = {
              memory = "256Mi"
              cpu    = "250m"
            }
            limits = {
              memory = "512Mi"
              cpu    = "500m"
            }
          }

          volume_mount {
            name       = "postgres-storage"
            mount_path = "/var/lib/postgresql/data"
          }
        }

        volume {
          name = "postgres-storage"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.postgres.metadata[0].name
          }
        }
      }
    }
  }
}

# PostgreSQL Service
resource "kubernetes_service" "postgres" {
  metadata {
    name      = "postgres-db"
    namespace = kubernetes_namespace.this.metadata[0].name
    labels = {
      app = "postgres-db"
    }
  }

  spec {
    type = "ClusterIP"

    port {
      port        = 5432
      target_port = 5432
      protocol    = "TCP"
    }

    selector = {
      app = "postgres-db"
    }
  }
}

# App Deployment
resource "kubernetes_deployment" "app" {
  wait_for_rollout = false

  metadata {
    name      = "oficina-app"
    namespace = kubernetes_namespace.this.metadata[0].name
    labels = {
      app = "oficina-app"
    }
  }

  spec {
    replicas = var.app_replicas

    selector {
      match_labels = {
        app = "oficina-app"
      }
    }

    template {
      metadata {
        labels = {
          app = "oficina-app"
        }
      }

      spec {
        container {
          name              = "oficina-app"
          image             = var.app_image
          image_pull_policy = "IfNotPresent"

          port {
            container_port = 8080
          }

          env {
            name  = "SPRING_DATASOURCE_URL"
            value = "jdbc:postgresql://postgres-db:5432/oficina"
          }

          env {
            name = "SPRING_DATASOURCE_USERNAME"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }

          env {
            name = "SPRING_DATASOURCE_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }

          env {
            name = "SPRING_PROFILES_ACTIVE"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.app.metadata[0].name
                key  = "SPRING_PROFILES_ACTIVE"
              }
            }
          }

          env {
            name = "APP_ENV"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.app.metadata[0].name
                key  = "APP_ENV"
              }
            }
          }

          env {
            name = "LOG_LEVEL"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.app.metadata[0].name
                key  = "LOG_LEVEL"
              }
            }
          }

          env {
            name = "JWT_ISSUER"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.app.metadata[0].name
                key  = "JWT_ISSUER"
              }
            }
          }

          env {
            name = "JWT_ACCESS_TOKEN_MINUTES"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.app.metadata[0].name
                key  = "JWT_ACCESS_TOKEN_MINUTES"
              }
            }
          }

          env {
            name = "JWT_SECRET_BASE64"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.app.metadata[0].name
                key  = "JWT_SECRET_BASE64"
              }
            }
          }

          env {
            name = "SECURITY_SEED_ENABLED"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.app.metadata[0].name
                key  = "SECURITY_SEED_ENABLED"
              }
            }
          }

          env {
            name = "SECURITY_SEED_ADMIN_EMAIL"
            value_from {
              config_map_key_ref {
                name = kubernetes_config_map.app.metadata[0].name
                key  = "SECURITY_SEED_ADMIN_EMAIL"
              }
            }
          }

          env {
            name = "SECURITY_SEED_ADMIN_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.app.metadata[0].name
                key  = "SECURITY_SEED_ADMIN_PASSWORD"
              }
            }
          }

          resources {
            requests = {
              memory = "512Mi"
              cpu    = "500m"
            }
            limits = {
              memory = "1Gi"
              cpu    = "1000m"
            }
          }

          liveness_probe {
            http_get {
              path = "/actuator/health"
              port = 8080
            }
            initial_delay_seconds = 60
            period_seconds        = 10
            timeout_seconds       = 5
            failure_threshold     = 3
          }

          readiness_probe {
            http_get {
              path = "/actuator/health"
              port = 8080
            }
            initial_delay_seconds = 30
            period_seconds        = 5
            timeout_seconds       = 3
            failure_threshold     = 3
          }
        }
      }
    }
  }
}

# App Service
resource "kubernetes_service" "app" {
  metadata {
    name      = "oficina-app"
    namespace = kubernetes_namespace.this.metadata[0].name
    labels = {
      app = "oficina-app"
    }
  }

  spec {
    type = "LoadBalancer"

    port {
      port        = 80
      target_port = 8080
      protocol    = "TCP"
    }

    selector = {
      app = "oficina-app"
    }
  }
}

# HPA
resource "kubernetes_horizontal_pod_autoscaler_v2" "app" {
  metadata {
    name      = "oficina-app-hpa"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  spec {
    scale_target_ref {
      api_version = "apps/v1"
      kind        = "Deployment"
      name        = kubernetes_deployment.app.metadata[0].name
    }

    min_replicas = var.hpa_min_replicas
    max_replicas = var.hpa_max_replicas

    metric {
      type = "Resource"
      resource {
        name = "cpu"
        target {
          type                = "Utilization"
          average_utilization = var.hpa_cpu_target
        }
      }
    }

    metric {
      type = "Resource"
      resource {
        name = "memory"
        target {
          type                = "Utilization"
          average_utilization = var.hpa_memory_target
        }
      }
    }
  }
}
