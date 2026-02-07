@echo off
echo Checking Docker availability...

WHERE docker >nul 2>nul
IF %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker is not installed or not in your PATH.
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop/
    echo After installation, restart your terminal and try again.
    pause
    exit /b 1
)

echo Docker is found. Checking for docker-compose...

WHERE docker-compose >nul 2>nul
IF %ERRORLEVEL% EQU 0 (
    echo Found 'docker-compose' (V1). Starting services...
    docker-compose up -d
    goto :success
)

echo 'docker-compose' command not found. Trying 'docker compose' (V2)...
docker compose version >nul 2>nul
IF %ERRORLEVEL% EQU 0 (
    echo Found 'docker compose' (V2). Starting services...
    docker compose up -d
    goto :success
)

echo [ERROR] Neither 'docker-compose' nor 'docker compose' could be executed.
echo Please ensure Docker Desktop is running.
pause
exit /b 1

:success
echo.
echo ========================================================
echo Kafka environment started successfully!
echo.
echo Broker: localhost:9092
echo Zookeeper: localhost:2181
echo.
echo You can now run the Java examples.
echo ========================================================
pause
