@echo off
REM Allow skip with: set SKIP_GIT_HOOKS=1 && git commit -m "..."
IF "%SKIP_GIT_HOOKS%"=="1" (
  echo [pre-commit] skipped
  EXIT /B 0
)

FOR /F "delims=" %%i IN ('git rev-parse --show-toplevel') DO SET REPO_ROOT=%%i
cd /D "%REPO_ROOT%"

echo [pre-commit] Running Spotless format…
call gradlew.bat -q spotlessApply
IF ERRORLEVEL 1 EXIT /B 1

echo [pre-commit] Running checks…
call gradlew.bat -q clean check
IF ERRORLEVEL 1 EXIT /B 1

echo [pre-commit] OK
