@echo off
echo ===== SYNCHRONISATION DU PROJET =====
call gradlew.bat --refresh-dependencies
echo.
echo ===== NETTOYAGE ET COMPILATION =====
call gradlew.bat clean build
echo.
echo ===== INSTALLATION SUR APPAREIL =====
call gradlew.bat installDebug
echo.
echo ===== TERMINÉ =====
pause