output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = aws_db_instance.postgres.endpoint
  sensitive   = true
}

output "lambda_function_arn" {
  description = "Lambda authentication function ARN"
  value       = aws_lambda_function.auth.arn
}

output "api_gateway_endpoint" {
  description = "API Gateway endpoint"
  value       = aws_apigatewayv2_stage.dev.invoke_url
}

output "db_instance_id" {
  description = "RDS instance ID"
  value       = aws_db_instance.postgres.id
}

output "newrelic_alert_policy_id" {
  description = "New Relic alert policy id"
  value       = var.enable_newrelic ? newrelic_alert_policy.oficina_observability[0].id : null
}
