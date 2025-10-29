PASS 1 — Réparer sans tout effacer

1. Fermer Docker Desktop, sur PowerShell Admin :

    # Activer les features nécessaires
    dism /online /enable-feature /featurename:Microsoft-Windows-Subsystem-Linux /all /norestart
    dism /online /enable-feature /featurename:VirtualMachinePlatform /all /norestart
    dism /online /enable-feature /featurename:HypervisorPlatform /all /norestart
    bcdedit /set hypervisorlaunchtype Auto
    
    # Mettre WSL à jour et l’arrêter
    wsl --update
    wsl --set-default-version 2
    wsl --shutdown
    
    # Redémarrer le service WSL
    net stop LxssManager
    net start LxssManager


2. Compacte le disque Docker WSL (si présent) :

    $dockerVhdx = Join-Path $env:LOCALAPPDATA 'Docker\wsl\data\ext4.vhdx'
    if (Test-Path $dockerVhdx) {
    diskpart /s "$($env:TEMP)\_dp.txt" | Out-Null
    } else { 'Aucun VHDX Docker WSL à compacter.' }
    @"
    select vdisk file="$dockerVhdx"
    attach vdisk readonly
    compact vdisk
    detach vdisk
    exit
    "@ | Set-Content -Path "$($env:TEMP)\_dp.txt" -Encoding ASCII
    diskpart /s "$($env:TEMP)\_dp.txt"
    Remove-Item "$($env:TEMP)\_dp.txt" -Force


3. Relancer Docker Desktop et teste.
Fait le ménage sur Docker pour récupérer de la place :

    docker system df
    docker system prune -a --volumes -f
    docker builder prune --all -f




PASS 2 — Reset propre (efface images/volumes Docker locaux

1. Fermer Docker Desktop, puis PowerShell Admin :

wsl --shutdown

# Désenregistrer les distros Docker (OK si déjà absentes)
wsl --unregister docker-desktop
wsl --unregister docker-desktop-data

# Supprimer/renommer les restes (VHDX et caches)
$paths = @(
"$env:LOCALAPPDATA\Docker\wsl\data\ext4.vhdx",
"$env:LOCALAPPDATA\Docker",
"$env:APPDATA\Docker",
"$env:PROGRAMDATA\DockerDesktop"
)
foreach ($p in $paths) {
if (Test-Path $p) {
try {
if ($p -like '*.vhdx') {
Rename-Item $p ("$p.corrupted.{0:yyyyMMddHHmmss}" -f (Get-Date)) -ErrorAction SilentlyContinue
} else {
Remove-Item $p -Recurse -Force -ErrorAction SilentlyContinue
}
} catch {}
}
}

# Revalider WSL & kernel à jour
wsl --update
wsl --status


2. Relancer docker et le build doker-compose
























wsl --shutdown

# Désenregistrer les distros Docker (OK si déjà absentes)
wsl --unregister docker-desktop
wsl --unregister docker-desktop-data

# Supprimer/renommer les restes (VHDX et caches)
$paths = @(
"$env:LOCALAPPDATA\Docker\wsl\data\ext4.vhdx",
"$env:LOCALAPPDATA\Docker",
"$env:APPDATA\Docker",
"$env:PROGRAMDATA\DockerDesktop"
)
foreach ($p in $paths) {
if (Test-Path $p) {
try {
if ($p -like '*.vhdx') {
Rename-Item $p ("$p.corrupted.{0:yyyyMMddHHmmss}" -f (Get-Date)) -ErrorAction SilentlyContinue
} else {
Remove-Item $p -Recurse -Force -ErrorAction SilentlyContinue
}
} catch {}
}
}

# Revalider WSL & kernel à jour
wsl --update
wsl --status
