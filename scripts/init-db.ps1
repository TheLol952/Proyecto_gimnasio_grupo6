param(
    [string]$DatabaseHost = $(if ($env:DB_HOST) { $env:DB_HOST } else { "localhost" }),
    [int]$DatabasePort = $(if ($env:DB_PORT) { [int]$env:DB_PORT } else { 5432 }),
    [string]$DatabaseName = $(if ($env:DB_NAME) { $env:DB_NAME } else { "gimnasio" }),
    [string]$DatabaseUser = $(if ($env:DB_USER) { $env:DB_USER } else { "gimnasio_app" })
)

$ErrorActionPreference = "Stop"

if (-not (Get-Command psql -ErrorAction SilentlyContinue)) {
    throw "psql no esta instalado o no se encuentra en PATH."
}

$projectDirectory = Split-Path -Parent $PSScriptRoot
$schema = Join-Path $projectDirectory "database\schema.sql"
$data = Join-Path $projectDirectory "database\data.sql"

& psql -v ON_ERROR_STOP=1 -h $DatabaseHost -p $DatabasePort -U $DatabaseUser -d $DatabaseName -f $schema
if ($LASTEXITCODE -ne 0) { throw "No fue posible aplicar database/schema.sql." }

& psql -v ON_ERROR_STOP=1 -h $DatabaseHost -p $DatabasePort -U $DatabaseUser -d $DatabaseName -f $data
if ($LASTEXITCODE -ne 0) { throw "No fue posible aplicar database/data.sql." }

Write-Output "Base de datos preparada correctamente."
