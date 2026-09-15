variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "sa-east-1"
}

variable "project_name" {
  description = "Project name"
  type        = string
  default     = "f1rsters-tech-challenge-mecanica"
}

variable "environment" {
  description = "Environment name"
  type        = string
  default     = "dev"
}

variable "lambda_artifacts_bucket" {
  description = "S3 bucket for Lambda artifacts"
  type        = string
  default     = "f1rsters-tech-challenge-lambda-artifacts"
}

variable "lambda_auth_s3_key" {
  description = "S3 key for Lambda auth function JAR"
  type        = string
  default     = "auth-function.jar"
}

variable "vpc_id" {
  description = "VPC ID for RDS"
  type        = string
  default     = "vpc-0a76831510c31af51"
}

variable "private_subnet_ids" {
  description = "Private subnet IDs for RDS"
  type        = list(string)
  default     = ["subnet-0a41d64838503ddae", "subnet-056e5e5f5a523f937"]
}

variable "allowed_cidr_blocks" {
  description = "CIDR blocks allowed to access RDS"
  type        = list(string)
  default     = ["10.0.0.0/8"]
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "oficina"
}

variable "db_username" {
  description = "Database username"
  type        = string
  default     = "oficinauser"
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret key"
  type        = string
  sensitive   = true
}

variable "db_backup_retention_period" {
  description = "Number of days to retain automated backups (0-35)"
  type        = number
  default     = 7
}

variable "enable_newrelic" {
  description = "Enable New Relic resources provisioning"
  type        = bool
  default     = false
}

variable "newrelic_account_id" {
  description = "New Relic account ID"
  type        = number
  default     = 0
}

variable "newrelic_api_key" {
  description = "New Relic user API key"
  type        = string
  sensitive   = true
  default     = ""
}

variable "newrelic_region" {
  description = "New Relic region"
  type        = string
  default     = "US"
}

variable "newrelic_license_key" {
  description = "New Relic license key"
  type        = string
  sensitive   = true
  default     = ""
}

variable "eks_cluster_name" {
  description = "Existing EKS cluster name used for New Relic integration"
  type        = string
  default     = ""
}
