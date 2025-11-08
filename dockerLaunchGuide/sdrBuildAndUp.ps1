param(
    [switch]$ForceRebuild  # appelle le script avec -ForceRebuild pour tout rebuilder
)

# Liste des microservices backend à builder
$projects = @(
    @{ Name = "ms-configServer"; Path = "..\sdr-backend\ms-configServer"; Image = "jirawah/ms-configserver:latest" },
    @{ Name = "ms-eurekaServer"; Path = "..\sdr-backend\ms-eurekaServer"; Image = "jirawah/ms-eurekaserver:latest" },
    @{ Name = "ms-auth";         Path = "..\sdr-backend\ms-auth";         Image = "jirawah/ms-auth:latest" },
    @{ Name = "ms-profil";       Path = "..\sdr-backend\ms-profil";       Image = "jirawah/ms-profil:latest" },
    @{ Name = "ms-rdv";          Path = "..\sdr-backend\ms-rdv";          Image = "jirawah/ms-rdv:latest" },
    @{ Name = "ms-friend";       Path = "..\sdr-backend\ms-friend";       Image = "jirawah/ms-friend:latest" },
    @{ Name = "ms-invitation";   Path = "..\sdr-backend\ms-invitation";   Image = "jirawah/ms-invitation:latest" },
    @{ Name = "ms-notif";        Path = "..\sdr-backend\ms-notif";        Image = "jirawah/ms-notif:latest" }
)

# Récupère le dossier du script et s'y place
$basePath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $basePath

Write-Host ""
Write-Host "============================================" -ForegroundColor DarkGray
Write-Host " Démarrage du build SneakyDateReforged" -ForegroundColor Cyan
Write-Host " Force rebuild: $ForceRebuild" -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor DarkGray
Write-Host ""

# --------- Build des microservices ----------
foreach ($project in $projects) {

    # Résolution du chemin absolu
    $absPath = Resolve-Path "$basePath\$($project.Path)" -ErrorAction SilentlyContinue
    if (-not $absPath) {
        Write-Host "Chemin introuvable pour $($project.Name) : $($project.Path). On passe au suivant." -ForegroundColor Yellow
        continue
    }

    # Vérifie si l'image Docker existe déjà en local
    $existingImageId = docker images -q $($project.Image) 2>$null

    if ($existingImageId -and -not $ForceRebuild) {
        Write-Host ""
        Write-Host "--------------------------------------------" -ForegroundColor DarkGray
        Write-Host "Image déjà présente pour $($project.Name) :" -ForegroundColor Yellow
        Write-Host "    $($project.Image) (ID: $existingImageId)" -ForegroundColor Yellow
        Write-Host "=> Skip Maven + docker build pour ce MS." -ForegroundColor Yellow
        Write-Host "--------------------------------------------" -ForegroundColor DarkGray
        Write-Host ""
        continue
    }

    # Sinon, on rebuild normalement
    Write-Host "=== Building $($project.Name) ===" -ForegroundColor Cyan
    Set-Location $absPath

    # Étape 1 : Maven package sans tests
    mvn clean install -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Build Maven échoué pour $($project.Name). Abandon." -ForegroundColor Red
        exit 1
    }

    # Étape 2 : Docker build de l'image
    docker build -t $($project.Image) .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Échec du docker build pour $($project.Name). Abandon." -ForegroundColor Red
        exit 1
    }

    Write-Host ""
    Write-Host "============================================" -ForegroundColor DarkGray
    Write-Host "Image pour $($project.Name) créée / mise à jour avec succès !" -ForegroundColor Green
    Write-Host "Tag : $($project.Image)" -ForegroundColor Green
    Write-Host "============================================" -ForegroundColor DarkGray
    Write-Host ""
}

Write-Host "Images backend OK (existantes ou rebuildées) !" -ForegroundColor Green
Write-Host ""

# --------- Démarrage docker-compose ----------
$composePath = Resolve-Path "$basePath\..\docker-compose\prod"
if (-not $composePath) {
    Write-Host "Dossier docker-compose/prod introuvable." -ForegroundColor Red
    exit 1
}

Write-Host "=== Passage dans le dossier docker-compose/prod ===" -ForegroundColor Cyan
Set-Location $composePath

# Vérifier .env (pour éviter les warnings var vides)
$envFile = Join-Path $composePath ".env"
if (-not (Test-Path $envFile)) {
    Write-Host "[AVERTISSEMENT] Fichier .env introuvable dans docker-compose/prod. Certaines variables seront vides." -ForegroundColor Yellow
} else {
    Write-Host "Fichier .env détecté." -ForegroundColor Green
}

# Build spécifique du frontend (ms-webapp)
Write-Host "=== Build ms-webapp (frontend) ===" -ForegroundColor Cyan
docker-compose build ms-webapp
if ($LASTEXITCODE -ne 0) {
    Write-Host "Échec du docker-compose build pour ms-webapp. Abandon." -ForegroundColor Red
    exit 1
}

# Lancement des conteneurs
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

