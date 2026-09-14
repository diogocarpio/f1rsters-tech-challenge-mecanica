# Development Environment Configuration
aws_region = "sa-east-1"
project_name = "f1rsters-tech-challenge-mecanica"
environment = "dev"

lambda_artifacts_bucket = "f1rsters-tech-challenge-lambda-artifacts"
lambda_auth_s3_key = "auth-function.jar"

# VPC Configuration (Default VPC in sa-east-1)
vpc_id = "vpc-0a76831510c31af51"
private_subnet_ids = ["subnet-0a41d64838503ddae", "subnet-056e5e5f5a523f937"]
allowed_cidr_blocks = ["172.31.0.0/16"]

# Database Configuration
db_name = "oficina"
db_username = "oficinauser"
db_backup_retention_period = 1
# db_password will be loaded from GitHub Secret: DB_PASSWORD

# JWT Configuration
# jwt_secret will be loaded from GitHub Secret: JWT_SECRET

# New Relic Configuration
enable_newrelic = true
# newrelic_account_id will be loaded from GitHub Secret: NEW_RELIC_ACCOUNT_ID
# newrelic_api_key will be loaded from GitHub Secret: NEW_RELIC_API_KEY
newrelic_region = "US"
# newrelic_license_key will be loaded from GitHub Secret: NEW_RELIC_LICENSE_KEY
# eks_cluster_name will be loaded from GitHub Secret: EKS_CLUSTER_NAME
db_password = "dummy_db_password"
jwt_secret = "dummy_jwt"
newrelic_account_id = 123456
newrelic_api_key = "dummy_api_key"
newrelic_license_key = "dummy_license"
eks_cluster_name = "dummy-eks"
