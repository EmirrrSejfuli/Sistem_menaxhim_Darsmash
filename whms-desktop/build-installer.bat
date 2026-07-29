@echo off
REM =====================================================================
REM  Skedar per te ndertuar automatikisht instaluesin .exe te "Sistem Darsmash"
REM
REM  PERPARA se ta ekzekutosh kete skedar:
REM  1. Bej ndryshimet ne kod
REM  2. Ne IntelliJ: paneli Maven -> whms -> Lifecycle -> clean, pastaj package
REM     (kjo krijon target\whms.jar te freskur me ndryshimet e reja)
REM
REM  Pastaj thjesht dopio-klik mbi kete skedar (build-installer.bat) - ai do:
REM  - fshije .exe-ne e vjeter (nese ekziston)
REM  - krijoje nje .exe te ri me te njejtin emer/cilesime
REM =====================================================================

echo.
echo === Duke fshire instaluesin e vjeter (nese ekziston) ===
if exist "Sistem Darsmash-1.0.0.exe" del "Sistem Darsmash-1.0.0.exe"

echo.
echo === Duke ndertuar instaluesin e ri ===
echo (kjo mund te marre 1-3 minuta, prit derisa te mbaroje)
echo.

jpackage --input target --name "Sistem Darsmash" --main-jar whms.jar --main-class org.springframework.boot.loader.launch.JarLauncher --type exe --win-shortcut --win-menu --win-dir-chooser --app-version 1.0.0 --vendor "CreatedByEmirSejfuli" --description "SMD" --runtime-image "C:\Program Files\Microsoft\jdk-21.0.10.7-hotspot"

if %errorlevel% equ 0 (
    echo.
    echo ============================================
    echo   SUKSES! Instaluesi u krijua:
    echo   "Sistem Darsmash-1.0.0.exe"
    echo ============================================
) else (
    echo.
    echo ============================================
    echo   GABIM! Jpackage deshtoi. Shiko tekstin me larte.
    echo ============================================
)

echo.
pause
