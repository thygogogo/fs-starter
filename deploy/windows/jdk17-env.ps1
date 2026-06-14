# 临时指定 JDK 17（仅当前 PowerShell 会话生效）
# 用法: . .\deploy\windows\jdk17-env.ps1

$ErrorActionPreference = "Stop"

try {
    [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
}
catch {}

$script:Jdk17Home = "C:\Users\thy\.jdks\ms-17.0.19"
$JavaExe = Join-Path $script:Jdk17Home "bin\java.exe"

if (-not (Test-Path $JavaExe)) {
    Write-Error "未找到 JDK 17: $script:Jdk17Home"
}

$env:JAVA_HOME = $script:Jdk17Home
$env:PATH = "$($script:Jdk17Home)\bin;$env:PATH"

Write-Host ">>> 使用 JDK 17: $env:JAVA_HOME"
& $JavaExe -version
