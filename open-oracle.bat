@echo off
echo ========================================================
echo   MedFlow: Connecting to Oracle Database 11g Express
echo   User: medflow_admin
echo   Host: localhost:1521 / XE
echo ========================================================
echo.
echo Type your SQL queries here (e.g. SELECT * FROM PATIENTS;)
echo Type EXIT to quit.
echo ========================================================
echo.

sqlplus medflow_admin/medflow
pause
