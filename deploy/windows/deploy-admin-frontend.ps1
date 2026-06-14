# 打包并部署 admin 前端
# 用法:
#   .\deploy\windows\deploy-admin-frontend.ps1
#   $env:DEPLOY_ENV = "prod"; .\deploy\windows\deploy-admin-frontend.ps1

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "config.ps1")
. (Join-Path $PSScriptRoot "_common.ps1")
Enter-ProjectRoot

Write-Host ">>> 部署环境: $($env:DEPLOY_ENV) ($Server)"

$remoteDir = "/root/docker/manager-frontend"
$distPath = "frontend-soldier-admin/dist"
$buildMode = if ($env:DEPLOY_ENV -eq "prod") { "production" } else { "test" }

Write-Host ">>> 打包前端 admin (mode=$buildMode)..."
Push-Location "frontend-soldier-admin"
try {
    & npm install
    if ($LASTEXITCODE -ne 0) { throw "npm install 失败" }

    if ($env:DEPLOY_ENV -eq "prod") {
        & npm run build:prod
    }
    else {
        & npm run build:test
    }
    if ($LASTEXITCODE -ne 0) { throw "前端构建失败" }
}
finally {
    Pop-Location
}

Write-Host ">>> 上传 dist 到 ${Server}:${remoteDir}/"
Sync-ToRemote -Source "${distPath}/" -RemoteDest "${Server}:${remoteDir}/dist/" -Delete

Invoke-RemoteRestart -Server $Server -RemoteDir $remoteDir

Write-Host ">>> 部署完成"
