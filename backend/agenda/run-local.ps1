# Arranca agenda cargando variables desde .env del mismo directorio (no se sube a git).
# 1. Copia mail-env.example → .env (o usa mail.env) y rellena SMTP_PASSWORD etc.
# 2. Ejecuta: .\run-local.ps1

$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot

function Load-DotEnvFile {
  param([string]$path)
  foreach ($line in Get-Content $path -Encoding utf8) {
    $trim = $line.Trim()
    if (-not $trim -or $trim.StartsWith('#')) {
      continue
    }
    $eq = $trim.IndexOf('=')
    if ($eq -lt 1) {
      continue
    }
    $name = $trim.Substring(0, $eq).Trim()
    $value = $trim.Substring($eq + 1).Trim()
    if (
      ($value.StartsWith('"') -and $value.EndsWith('"')) -or
      ($value.StartsWith("'") -and $value.EndsWith("'"))
    ) {
      $value = $value.Substring(1, $value.Length - 2)
    }
    Set-Item -Path "Env:$name" -Value $value
  }
}

$candidates = @(
  (Join-Path $PSScriptRoot '.env'),
  (Join-Path $PSScriptRoot 'mail.env')
)
$chosen = $candidates | Where-Object { Test-Path $_ } | Select-Object -First 1

if ($chosen) {
  Load-DotEnvFile -path $chosen
  Write-Host "Variables cargadas desde $(Split-Path -Leaf $chosen) (solo este proceso)." -ForegroundColor DarkGray
} else {
  Write-Host 'Aviso: no hay .env ni mail-env; valores por defecto. Crea uno desde mail-env.example' -ForegroundColor Yellow
}

# No uses "& mvn (...) + `$args`: PowerShell puede terminar pasando "+" a Maven.
mvn spring-boot:run
