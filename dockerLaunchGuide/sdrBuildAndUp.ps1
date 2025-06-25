# ------------------------------------------------------------
# Microservices backend - SneakyDateReforged
# ------------------------------------------------------------

$projects = @(
    @{ Name = "ms-configServer"; Path = "..\sdr-backend\ms-configServer"; Image = "jirawah/ms-configserver:latest" },
    @{ Name = "ms-eurekaServer"; Path = "..\sdr-backend\ms-eurekaServer"; Image = "jirawah/ms-eurekaserver:latest" }
)

$basePath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $basePath

foreach ($project in $projects) {
    $absPath = Resolve-Path "$basePath\$($project.Path)"
    Write-Host "=== Building $($project.Name) ===" -ForegroundColor Cyan
    Set-Location $absPath

    mvn clean install -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build Maven échoué pour $($project.Name). Abandon." -ForegroundColor Red
        exit 1
    }

    docker build -t $project.Image .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Échec du docker build pour $($project.Name). Abandon." -ForegroundColor Red
        exit 1
    }

    Write-Host ""
    Write-Host "============================================" -ForegroundColor DarkGray
    Write-Host "Image pour $($project.Name) créée avec succès !" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor DarkGray
    Write-Host ""
}

Write-Host "Images backend construites avec succès !" -ForegroundColor Green
Write-Host ""

# ------------------------------------------------------------
# Lancement de docker-compose
# ------------------------------------------------------------
$composePath = Resolve-Path "$basePath\..\docker-compose\prod"
Write-Host "=== Passage dans le dossier docker-compose/prod ===" -ForegroundColor Cyan
Set-Location $composePath

Write-Host "=== Lancement de docker-compose up -d ===" -ForegroundColor Cyan
docker-compose up -d
if ($LASTEXITCODE -ne 0) {
    Write-Host "Échec du lancement de docker-compose. Vérifiez votre configuration." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "============================================" -ForegroundColor DarkGray
Write-Host "docker-compose a démarré les conteneurs en arrière-plan." -ForegroundColor Green
Write-Host "============================================" -ForegroundColor DarkGray
Write-Host ""
Write-Host "Script terminé avec succès !" -ForegroundColor Green