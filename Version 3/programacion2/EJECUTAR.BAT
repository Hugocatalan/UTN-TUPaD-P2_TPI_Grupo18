@echo off
REM Cambiar la página de códigos a UTF-8
chcp 65001 >nul

REM Cambiar al directorio del proyecto
cd /d "%~dp0"

REM Ejecutar el JAR 
java -Dfile.encoding=UTF-8 -jar target/tfi-prog2-1.0-shaded.jar

pause
