try {
    [Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
}
catch {}

function Get-ProjectRoot {
    Resolve-Path (Join-Path (Join-Path $PSScriptRoot "..") "..")
}

function Enter-ProjectRoot {
    Set-Location (Get-ProjectRoot)
}

function Sync-ToRemote {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Source,
        [Parameter(Mandatory = $true)]
        [string]$RemoteDest,
        [switch]$Delete
    )

    if (Get-Command rsync -ErrorAction SilentlyContinue) {
        $rsyncArgs = @("-avz", "--progress")
        if ($Delete) {
            $rsyncArgs += "--delete"
        }
        & rsync @rsyncArgs $Source $RemoteDest
    }
    else {
        if ($Delete) {
            Write-Warning "未找到 rsync, 改用 scp (不支持 --delete, 远端多余文件需手动清理)"
        }
        & scp -r $Source $RemoteDest
    }

    if ($LASTEXITCODE -ne 0) {
        throw "文件上传失败: $Source -> $RemoteDest"
    }
}

function Invoke-RemoteRestart {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Server,
        [Parameter(Mandatory = $true)]
        [string]$RemoteDir
    )

    Write-Host ">>> 执行 restart.sh..."
    & ssh $Server "cd $RemoteDir && bash restart.sh"
    if ($LASTEXITCODE -ne 0) {
        throw "远端 restart.sh 执行失败"
    }
}

function Invoke-MavenPackage {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Module
    )

    Write-Host ">>> 打包 $Module..."
    & mvn clean package -DskipTests -pl $Module -am -q
    if ($LASTEXITCODE -ne 0) {
        throw "Maven 打包失败: $Module"
    }
}
