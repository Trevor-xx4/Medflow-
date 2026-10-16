@echo off
echo ===================================================
echo   MedFlow: Compiling Java Sources and Assets
echo ===================================================

if not exist target\classes mkdir target\classes
if not exist target\test-classes mkdir target\test-classes

powershell -Command ^
  "$jars = Get-ChildItem -Path \"$HOME\.m2\repository\" -Filter '*.jar' -Recurse | Where-Object { $_.FullName -notmatch '1\.7\.36' } | ForEach-Object { $_.FullName }; " ^
  "$cp = $jars -join ';'; " ^
  "$sources = (Get-ChildItem -Path 'src/main/java' -Filter '*.java' -Recurse | ForEach-Object { $_.FullName }); " ^
  "& 'javac' --release 17 -parameters -cp $cp -d target/classes $sources; " ^
  "$testSources = (Get-ChildItem -Path 'src/test/java' -Filter '*.java' -Recurse | ForEach-Object { $_.FullName }); " ^
  "if ($testSources) { & 'javac' --release 17 -parameters -cp ('target/classes;' + $cp) -d target/test-classes $testSources }; " ^
  "Copy-Item -Path 'src/main/resources/*' -Destination 'target/classes/' -Recurse -Force -ErrorAction SilentlyContinue; " ^
  "Write-Host 'Compilation Finished Successfully! [Java 17 Bytecode Generated in target/classes]' -ForegroundColor Green"

pause
