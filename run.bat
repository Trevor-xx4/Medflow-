@echo off
echo ===================================================
echo   MedFlow: Starting Hospital System on Port 8081
echo   Open browser at: http://localhost:8081
echo ===================================================

powershell -Command ^
  "$jars = Get-ChildItem -Path \"$HOME\.m2\repository\" -Filter '*.jar' -Recurse | Where-Object { $_.FullName -notmatch '1\.7\.36' } | ForEach-Object { $_.FullName }; " ^
  "$cp = $jars -join ';'; " ^
  "& 'java' -cp ('target/classes;' + $cp) com.medflow.MedFlowApplication"

pause
