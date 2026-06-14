# 使用 JDK 17 执行 Maven（不改变系统默认 JDK）
# 用法:
#   .\deploy\windows\mvn-jdk17.ps1 clean package -DskipTests -pl soldier-admin -am
#   .\deploy\windows\mvn-jdk17.ps1 -version

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "jdk17-env.ps1")

Set-Location (Resolve-Path (Join-Path (Join-Path $PSScriptRoot "..") ".."))

& mvn @args
