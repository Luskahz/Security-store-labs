@echo off
setlocal
cd /d "%~dp0" || exit /b 1

if exist "start.local.bat" call "start.local.bat"
if not defined SPRING_DATASOURCE_USERNAME set "SPRING_DATASOURCE_USERNAME=root"
if not defined MYSQL_SERVICE_NAME set "MYSQL_SERVICE_NAME=MySQL80"
if not defined MYSQL_PORT set "MYSQL_PORT=3306"
set "MYSQL_STARTED_BY_SCRIPT=0"
if not defined MAVEN_USER_HOME set "MAVEN_USER_HOME=%CD%\.m2-cache"

where java >nul 2>&1 || (echo Erro: Java 21 nao encontrado no PATH. & exit /b 1)
if not defined JWT_SECRET if not defined SPRING_PROFILES_ACTIVE if exist "src\main\resources\application-local.properties" set "SPRING_PROFILES_ACTIVE=local"
if not defined PII_ENCRYPTION_KEY if not defined SPRING_PROFILES_ACTIVE if exist "src\main\resources\application-local.properties" set "SPRING_PROFILES_ACTIVE=local"
if not defined SPRING_DATASOURCE_PASSWORD if not defined SPRING_PROFILES_ACTIVE if exist "src\main\resources\application-local.properties" set "SPRING_PROFILES_ACTIVE=local"
if not defined JWT_SECRET if not defined SPRING_PROFILES_ACTIVE (echo Erro: configure JWT_SECRET em start.local.bat ou no ambiente. & exit /b 1)
if not defined PII_ENCRYPTION_KEY if not defined SPRING_PROFILES_ACTIVE (echo Erro: configure PII_ENCRYPTION_KEY em start.local.bat ou no ambiente. & exit /b 1)

call :start_local_mysql
if errorlevel 1 goto startup_failed

echo Iniciando Security Store Labs em http://localhost:8080
call mvnw.cmd spring-boot:run
set "APP_EXIT_CODE=%errorlevel%"
goto cleanup

:startup_failed
set "APP_EXIT_CODE=1"
goto cleanup

:cleanup
if "%MYSQL_STARTED_BY_SCRIPT%"=="1" (
  echo Parando o servico MySQL iniciado por este script...
  sc stop "%MYSQL_SERVICE_NAME%" >nul 2>&1
)
exit /b %APP_EXIT_CODE%

:start_local_mysql
sc query "%MYSQL_SERVICE_NAME%" >nul 2>&1
if errorlevel 1 (
  echo Erro: servico MySQL "%MYSQL_SERVICE_NAME%" nao encontrado. Instale o MySQL Server ou ajuste MYSQL_SERVICE_NAME.
  exit /b 1
)
sc query "%MYSQL_SERVICE_NAME%" | findstr /i "RUNNING" >nul
if errorlevel 1 (
  echo Iniciando o servico local MySQL "%MYSQL_SERVICE_NAME%"...
  sc start "%MYSQL_SERVICE_NAME%" >nul 2>&1 || (echo Erro ao iniciar o servico. Verifique as permissoes do Windows. & exit /b 1)
  set "MYSQL_STARTED_BY_SCRIPT=1"
)
echo Aguardando MySQL local na porta %MYSQL_PORT%...
for /l %%G in (1,1,60) do (
  powershell.exe -NoProfile -Command "$c=New-Object System.Net.Sockets.TcpClient; try{$c.Connect('127.0.0.1',[int]$env:MYSQL_PORT);exit 0}catch{exit 1}finally{$c.Close()}" >nul 2>&1
  if not errorlevel 1 exit /b 0
  powershell.exe -NoProfile -Command "Start-Sleep -Seconds 1" >nul 2>&1
)
echo Erro: MySQL nao aceitou conexoes na porta %MYSQL_PORT% em 60 segundos.
exit /b 1
