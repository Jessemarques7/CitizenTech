@echo off
REM Sobe a API do CitizenTech (Java 17+ necessario; o Maven e baixado sozinho pelo mvnw).
setlocal
cd /d "%~dp0api"

where java >nul 2>nul
if errorlevel 1 (
  echo [ERRO] Java nao encontrado. Instale o Java 17 ou superior e tente de novo.
  pause
  exit /b 1
)

if "%PORT%"=="" set PORT=8080
netstat -ano | findstr /R /C:":%PORT% .*LISTENING" >nul
if not errorlevel 1 (
  echo [AVISO] A porta %PORT% ja esta em uso por outro programa. Usando a porta 8081.
  set PORT=8081
)

echo.
echo ============================================================
echo  API subindo em http://localhost:%PORT%/api
echo  Swagger:       http://localhost:%PORT%/swagger-ui.html
echo  No app (emulador), use a URL:  http://10.0.2.2:%PORT%/api/
if not "%PORT%"=="8080" echo  ^>^> Toque em "Configurar endereco da API" no app e informe a URL acima.
echo  Deixe esta janela aberta enquanto testa. Ctrl+C para parar.
echo ============================================================
echo.

call "%~dp0api\mvnw.cmd" spring-boot:run
pause
