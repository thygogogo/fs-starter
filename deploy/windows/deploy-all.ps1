# 一键部署 admin 后端、app 后端、admin 前端
#
# 用法:
#   .\deploy\windows\deploy-all.ps1              # 测试环境（默认）
#   .\deploy\windows\deploy-all.ps1 -Prod          # 生产环境
#   $env:DEPLOY_ENV = "prod"; .\deploy\windows\deploy-all.ps1

param(
    [switch]$Prod,
    [switch]$UseSystemJdk
)

$ErrorActionPreference = "Stop"

if ($Prod) {
    $env:DEPLOY_ENV = "prod"
}
elseif (-not $env:DEPLOY_ENV) {
    $env:DEPLOY_ENV = "test"
}

. (Join-Path $PSScriptRoot "config.ps1")

Write-Host "========================================"
Write-Host "  一键部署 ($($env:DEPLOY_ENV) -> $Server)"
Write-Host "  1. soldier-admin 后端"
Write-Host "  2. admin 前端"
Write-Host "  3. soldier-app 后端"
Write-Host "========================================"

$deployArgs = @{}
if ($UseSystemJdk) {
    $deployArgs.UseSystemJdk = $true
}

& (Join-Path $PSScriptRoot "deploy-admin.ps1") @deployArgs
& (Join-Path $PSScriptRoot "deploy-admin-frontend.ps1")
& (Join-Path $PSScriptRoot "deploy-app.ps1") @deployArgs

Write-Host ">>> 全部部署完成 ($($env:DEPLOY_ENV) -> $Server)"
