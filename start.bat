@echo off
setlocal
cd /d "%~dp0" || exit /b 1

if exist "start.local.bat" call "start.local.bat"
if not defined SPRING_DATASOURCE_USERNAME set "SPRING_DATASOURCE_USERNAME=root"
if not defined SPRING_DATASOURCE_PASSWORD set "SPRING_DATASOURCE_PASSWORD=aluno"
if not defined USE_DOCKER_MYSQL set "USE_DOCKER_MYSQL=true"
if not defined MYSQL_CONTAINER set "MYSQL_CONTAINER=security-store-mysql"

where java >nul 2>&1 || (echo Erro: Java 21 nao encontrado no PATH. & exit /b 1)
where mvn >nul 2>&1 || (echo Erro: Maven nao encontrado no PATH. & exit /b 1)
if not defined JWT_SECRET (echo Erro: configure JWT_SECRET em start.local.bat ou no ambiente. & exit /b 1)
if not defined PII_ENCRYPTION_KEY (echo Erro: configure PII_ENCRYPTION_KEY em start.local.bat ou no ambiente. & exit /b 1)

if /i "%USE_DOCKER_MYSQL%"=="true" call :start_mysql || exit /b 1

echo Iniciando Security Store Labs em http://localhost:8080
call mvn spring-boot:run
exit /b %errorlevel%

:start_mysql
where docker >nul 2>&1 || (echo Erro: Docker nao encontrado. Instale-o ou use USE_DOCKER_MYSQL=false com MySQL local. & exit /b 1)
docker info >nul 2>&1 || (echo Erro: inicie o Docker Desktop. & exit /b 1)
docker container inspect "%MYSQL_CONTAINER%" >nul 2>&1
if errorlevel 1 (
  echo Criando conteiner MySQL %MYSQL_CONTAINER%...
  docker run -d --name "%MYSQL_CONTAINER%" -e "MYSQL_ROOT_PASSWORD=%SPRING_DATASOURCE_PASSWORD%" -p 3306:3306 -v security-store-mysql-data:/var/lib/mysql mysql:8.4 >nul || exit /b 1
) else (
  docker start "%MYSQL_CONTAINER%" >nul 2>&1
)
echo Aguardando MySQL...
for /l %%G in (1,1,60) do (
  docker exec -e "MYSQL_PWD=%SPRING_DATASOURCE_PASSWORD%" "%MYSQL_CONTAINER%" mysql -uroot -e "SELECT 1" >nul 2>&1
  if not errorlevel 1 exit /b 0
  timeout /t 1 /nobreak >nul
)
echo Erro: MySQL nao respondeu em 60 segundos.
exit /b 1
