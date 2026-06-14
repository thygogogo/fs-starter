# 配置 SSH 免密登录
# 用法:
#   .\deploy\windows\setup-ssh.ps1
#   $env:DEPLOY_ENV = "prod"; .\deploy\windows\setup-ssh.ps1

$ErrorActionPreference = "Stop"

. (Join-Path $PSScriptRoot "config.ps1")

$sshDir = Join-Path $env:USERPROFILE ".ssh"
$privateKey = Join-Path $sshDir "id_rsa"
$publicKey = Join-Path $sshDir "id_rsa.pub"

if (-not (Test-Path $privateKey)) {
    Write-Host ">>> 生成 SSH 密钥对..."
    New-Item -ItemType Directory -Force -Path $sshDir | Out-Null
    & ssh-keygen -t rsa -b 4096 -f $privateKey -N ""
    if ($LASTEXITCODE -ne 0) {
        throw "ssh-keygen 失败"
    }
}
else {
    Write-Host ">>> SSH 密钥已存在，跳过生成"
}

Write-Host ">>> 推送公钥到 $Server (需要输入服务器密码)..."
$tempPubKey = Join-Path $env:TEMP "soldier_id_rsa.pub"
Copy-Item $publicKey $tempPubKey -Force
try {
    & scp $tempPubKey "${Server}:/tmp/soldier_id_rsa.pub"
    if ($LASTEXITCODE -ne 0) { throw "scp 公钥失败" }

    $remoteCmd = 'mkdir -p ~/.ssh && chmod 700 ~/.ssh && touch ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys && (grep -qxF "$(cat /tmp/soldier_id_rsa.pub)" ~/.ssh/authorized_keys || cat /tmp/soldier_id_rsa.pub >> ~/.ssh/authorized_keys) && rm -f /tmp/soldier_id_rsa.pub'
    & ssh $Server $remoteCmd
    if ($LASTEXITCODE -ne 0) { throw "公钥写入失败" }
}
finally {
    Remove-Item $tempPubKey -Force -ErrorAction SilentlyContinue
}

Write-Host ">>> 配置完成，后续 deploy 脚本可免密执行"
