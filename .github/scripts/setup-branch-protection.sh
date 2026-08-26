#!/bin/bash

# Script para configurar regras de branch protection no GitHub
# Requer: GitHub CLI (gh) instalado e autenticado

REPO_OWNER="${1:-$(git config --get remote.origin.url | sed -n 's/.*github.com[:/]\([^/]*\)\/.*/\1/p')}"
REPO_NAME="${2:-$(git config --get remote.origin.url | sed -n 's/.*github.com[:/][^/]*\/\(.*\)\.git/\1/p')}"

echo "Configurando branch protection para: $REPO_OWNER/$REPO_NAME"

# Configurar proteção da branch main
gh api \
  --method PUT \
  -H "Accept: application/vnd.github+json" \
  "/repos/$REPO_OWNER/$REPO_NAME/branches/main/protection" \
  -f name="main" \
  -f enforce_admins=true \
  -f required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":false,"require_code_owner_reviews":true}' \
  -f required_status_checks='{"strict":true,"contexts":["build_and_test_lambda","terraform_plan"]}' \
  -f restrictions=null \
  -f allow_force_pushes=false \
  -f allow_deletions=false

echo "Branch protection configurada com sucesso!"
echo "- Requer PR para merge"
echo "- Requer pelo menos 1 aprovação"
echo "- Requer aprovação de code owners"
echo "- Status checks obrigatórios: build_and_test_lambda, terraform_plan"
echo "- Admins não podem bypass"
echo "- Não permite force push"
echo "- Não permite deletar a branch"
