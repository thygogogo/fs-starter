# 打包并部署 soldier-admin（默认使用 JDK 17）
# 用法:
#   .\deploy\windows\deploy-admin.ps1
#   $env:DEPLOY_ENV = "prod"; .\deploy\windows\deploy-admin.ps1
#   .\deploy\windows\deploy-admin.ps1 -UseSystemJdk   # 使用系统默认 JDK

param(
    [switch]$UseSystemJdk
)

$ErrorActionPreference = "Stop"

if (-not $UseSystemJdk) {
    . (Join-Path $PSScriptRoot "jdk17-env.ps1")
}

. (Join-Path $PSScriptRoot "config.ps1")
. (Join-Path $PSScriptRoot "_common.ps1")
Enter-ProjectRoot

Write-Host ">>> 部署环境: $($env:DEPLOY_ENV) ($Server, spring.profiles.active=$SpringProfile)"

$remoteDir = "/root/docker/manager-server"
$jarPath = "fs-starter-admin/target/manager-server.jar"

Invoke-MavenPackage -Module "fs-starter-admin"

Write-Host ">>> 推送 $jarPath 到 ${Server}:${remoteDir}/"
Sync-ToRemote -Source $jarPath -RemoteDest "${Server}:${remoteDir}/manager-server.jar"

Invoke-RemoteRestart -Server $Server -RemoteDir $remoteDir

Write-Host ">>> 部署完成"
