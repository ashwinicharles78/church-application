@echo off

REM Change directory to Spring Boot backend project
cd C:\church-application\spring-church-app
start cmd /k "mvn spring-boot:run"

REM Change directory to Angular frontend project
cd C:\church-application\church-angular-app
start cmd /k "ng serve"

REM Optional: Keep this window open
pause
