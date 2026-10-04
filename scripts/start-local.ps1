param(
    [ValidatePattern('^[a-z_][a-z0-9_]*$')]
    [string]$DatabaseName = 'db_rendimiento_local',
    [string]$DatabaseUser = 'postgres',
    [ValidateSet('localhost', '127.0.0.1')]
    [string]$DatabaseHost = 'localhost',
    [ValidateRange(1, 65535)]
    [int]$DatabasePort = 5432
)

$ErrorActionPreference = 'Stop'

function Invoke-Psql {
    param([string]$Database, [string]$Sql)

    $result = & $psql -X -A -t -v ON_ERROR_STOP=1 -h $DatabaseHost -p $DatabasePort -U $DatabaseUser -d $Database -c $Sql
    if ($LASTEXITCODE -ne 0) {
        throw "PostgreSQL rechazo la consulta en la base '$Database'."
    }
    return ($result | Out-String).Trim()
}

$psqlCommand = Get-Command psql.exe -CommandType Application -ErrorAction SilentlyContinue | Select-Object -First 1
$psql = if ($psqlCommand) { $psqlCommand.Source } else { $null }
if (-not $psql) {
    $postgresDirectory = Join-Path $env:ProgramFiles 'PostgreSQL'
    $psql = Get-ChildItem -LiteralPath $postgresDirectory -Filter psql.exe -Recurse -File -ErrorAction SilentlyContinue |
        Where-Object { $_.DirectoryName -match '[\\/]bin$' } |
        Sort-Object FullName -Descending |
        Select-Object -First 1 -ExpandProperty FullName
}
if (-not $psql) {
    throw 'No se encontro psql.exe. Instala PostgreSQL con sus herramientas de linea de comandos.'
}

$javaHome = $env:JAVA_HOME
if (-not $javaHome) {
    $javaHome = [Environment]::GetEnvironmentVariable('JAVA_HOME', 'Machine')
}
$java = if ($javaHome) { Join-Path $javaHome 'bin\java.exe' } else { 'java.exe' }
if (-not (Test-Path -LiteralPath $java -PathType Leaf)) {
    throw "No se encontro java.exe en JAVA_HOME: '$javaHome'."
}
$previousErrorActionPreference = $ErrorActionPreference
$ErrorActionPreference = 'Continue'
try {
    $javaVersion = (& $java -version 2>&1 | Out-String)
    $javaExitCode = $LASTEXITCODE
} finally {
    $ErrorActionPreference = $previousErrorActionPreference
}
if ($javaExitCode -ne 0 -or $javaVersion -notmatch 'version "(?:1\.)?(\d+)') {
    throw 'No se pudo determinar la version de Java. Este proyecto requiere JDK 21.'
}
if ([int]$Matches[1] -lt 21) {
    throw "Se requiere JDK 21; la version encontrada es $($Matches[1]). Actualiza JAVA_HOME antes de iniciar."
}

$previousPgPassword = $env:PGPASSWORD
$previousDbPassword = $env:DB_PASSWORD
$previousProfile = $env:SPRING_PROFILES_ACTIVE
$previousDbHost = $env:DB_HOST
$previousDbPort = $env:DB_PORT
$previousDbName = $env:DB_NAME
$previousDbUser = $env:DB_USER
$previousJavaHome = $env:JAVA_HOME

try {
    if (-not $env:DB_PASSWORD) {
        $securePassword = Read-Host 'Contrasena del usuario PostgreSQL' -AsSecureString
        $passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
        try {
            $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
        } finally {
            [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
        }
    }

    $env:PGPASSWORD = $env:DB_PASSWORD
    $env:DB_HOST = $DatabaseHost
    $env:DB_PORT = [string]$DatabasePort
    $env:DB_NAME = $DatabaseName
    $env:DB_USER = $DatabaseUser
    $env:SPRING_PROFILES_ACTIVE = 'local'
    $env:JAVA_HOME = $javaHome

    $databaseExists = Invoke-Psql 'postgres' "SELECT 1 FROM pg_database WHERE datname = '$DatabaseName';"
    if ($databaseExists -ne '1') {
        Invoke-Psql 'postgres' "CREATE DATABASE $DatabaseName;" | Out-Null
        Write-Host "Base de datos '$DatabaseName' creada."
    }

    $schemaExists = Invoke-Psql $DatabaseName "SELECT 1 FROM pg_namespace WHERE nspname = 'db_tp1';"
    if ($schemaExists -eq '1') {
        $historyExists = Invoke-Psql $DatabaseName "SELECT to_regclass('public.flyway_schema_history') IS NOT NULL;"
        $initialMigrationApplied = if ($historyExists -eq 't') {
            Invoke-Psql $DatabaseName "SELECT 1 FROM public.flyway_schema_history WHERE version = '1' AND success = true;"
        } else { '' }
        if ($initialMigrationApplied -ne '1') {
            throw "La base '$DatabaseName' ya contiene el esquema db_tp1 sin la migracion inicial registrada. No se inicia Flyway porque V1 elimina ese esquema. Usa otra base vacia o respalda y revisa esta manualmente."
        }
    }

    Write-Host "Iniciando backend con perfil local. Flyway aplicara las migraciones pendientes en '$DatabaseName'."
    Push-Location (Resolve-Path (Join-Path $PSScriptRoot '..'))
    try {
        & .\mvnw.cmd spring-boot:run
        if ($LASTEXITCODE -ne 0) {
            throw "El backend termino con codigo $LASTEXITCODE."
        }
    } finally {
        Pop-Location
    }
} finally {
    $env:PGPASSWORD = $previousPgPassword
    $env:DB_PASSWORD = $previousDbPassword
    $env:SPRING_PROFILES_ACTIVE = $previousProfile
    $env:DB_HOST = $previousDbHost
    $env:DB_PORT = $previousDbPort
    $env:DB_NAME = $previousDbName
    $env:DB_USER = $previousDbUser
    $env:JAVA_HOME = $previousJavaHome
}
