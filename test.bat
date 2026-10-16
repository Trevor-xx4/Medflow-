@echo off
echo ===================================================
echo   MedFlow: Running OS Simulation Algorithm Tests
echo ===================================================

powershell -Command ^
  "$jars = Get-ChildItem -Path \"$HOME\.m2\repository\" -Filter '*.jar' -Recurse | Where-Object { $_.FullName -notmatch '1\.7\.36' } | ForEach-Object { $_.FullName }; " ^
  "$cp = $jars -join ';'; " ^
  "& 'java' -cp ('target/classes;target/test-classes;' + $cp) com.medflow.simulation.SimulationServiceTest"

pause
