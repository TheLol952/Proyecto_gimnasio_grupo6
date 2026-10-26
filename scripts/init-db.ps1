param(
    [string]$DatabaseHost,
    [int]$DatabasePort,
    [string]$DatabaseName,
    [string]$DatabaseUser
)

$ErrorActionPreference = "Stop"

$projectDirectory = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $projectDirectory ".env"

if (Test-Path -LiteralPath $envFile) {
    foreach ($line in Get-Content -LiteralPath $envFile) {
        if ($line -match '^\s*([^#][^=]*)=(.*)$') {
            $name = $Matches[1].Trim()
            $value = $Matches[2].Trim()
            if (-not [Environment]::GetEnvironmentVariable($name, "Process")) {
                [Environment]::SetEnvironmentVariable($name, $value, "Process")
            }
        }
    }
}

if (-not $DatabaseHost) { $DatabaseHost = $(if ($env:DB_HOST) { $env:DB_HOST } else { "localhost" }) }
if (-not $DatabasePort) { $DatabasePort = $(if ($env:DB_PORT) { [int]$env:DB_PORT } else { 5432 }) }
if (-not $DatabaseName) { $DatabaseName = $(if ($env:DB_NAME) { $env:DB_NAME } else { "gimnasio" }) }
if (-not $DatabaseUser) { $DatabaseUser = $(if ($env:DB_USER) { $env:DB_USER } else { "gimnasio_app" }) }

$requiredVariables = @(
    "DB_PASSWORD",
    "DEMO_ADMIN_USER", "DEMO_ADMIN_PASSWORD",
    "DEMO_RECEPTION_USER", "DEMO_RECEPTION_PASSWORD",
    "DEMO_TRAINER_USER", "DEMO_TRAINER_PASSWORD"
)
foreach ($name in $requiredVariables) {
    if (-not [Environment]::GetEnvironmentVariable($name, "Process")) {
        throw "Falta la variable $name en .env o en el ambiente."
    }
}

if (-not (Get-Command psql -ErrorAction SilentlyContinue)) {
    throw "psql no esta instalado o no se encuentra en PATH."
}

$schema = Join-Path $projectDirectory "database\schema.sql"
$data = Join-Path $projectDirectory "database\data.sql"
$env:PGPASSWORD = $env:DB_PASSWORD

& psql -v ON_ERROR_STOP=1 -h $DatabaseHost -p $DatabasePort -U $DatabaseUser -d $DatabaseName -f $schema
if ($LASTEXITCODE -ne 0) { throw "No fue posible aplicar database/schema.sql." }

& psql -v ON_ERROR_STOP=1 `
    -v "DEMO_ADMIN_USER=$env:DEMO_ADMIN_USER" `
    -v "DEMO_ADMIN_PASSWORD=$env:DEMO_ADMIN_PASSWORD" `
    -v "DEMO_RECEPTION_USER=$env:DEMO_RECEPTION_USER" `
    -v "DEMO_RECEPTION_PASSWORD=$env:DEMO_RECEPTION_PASSWORD" `
    -v "DEMO_TRAINER_USER=$env:DEMO_TRAINER_USER" `
    -v "DEMO_TRAINER_PASSWORD=$env:DEMO_TRAINER_PASSWORD" `
    -h $DatabaseHost -p $DatabasePort -U $DatabaseUser -d $DatabaseName -f $data
if ($LASTEXITCODE -ne 0) { throw "No fue posible aplicar database/data.sql." }

Write-Output "Base de datos preparada correctamente."
