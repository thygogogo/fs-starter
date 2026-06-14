# 部署配置
# 用法:
#   .\deploy\windows\deploy-admin.ps1              # 默认 test
#   $env:DEPLOY_ENV = "prod"; .\deploy\windows\deploy-admin.ps1

if (-not $env:DEPLOY_ENV) {
    $env:DEPLOY_ENV = "test"
}

switch ($env:DEPLOY_ENV) {
    "test" {
        $script:Server = "root@106.54.22.84"
        $script:SpringProfile = "test"
    }
    "prod" {
        $script:Server = "root@124.221.77.224"
        $script:SpringProfile = "prod"
    }
    default {
        Write-Error "未知环境: $($env:DEPLOY_ENV) (可选: test, prod)"
    }
}
