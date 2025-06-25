@echo off
echo --- Lancement du script PowerShell de build Docker ---
powershell -ExecutionPolicy Bypass -File "%~dp0sdrBuildAndUp.ps1"
pause