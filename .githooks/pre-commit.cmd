@echo off
REM Allow skip with: set SKIP_GIT_HOOKS=1 && git commit -m "..."
IF "%SKIP_GIT_HOOKS%"=="1" (
  echo [pre-commit] skipped
  EXIT /B 0
)

FOR /F "delims=" %%i IN ('git rev-parse --show-toplevel') DO SET REPO_ROOT=%%i
cd /D "%REPO_ROOT%"

REM Capture originally staged files
SET STAGED_FILES_TEMP=%TEMP%\staged_files_%RANDOM%.txt
git diff --cached --name-only --diff-filter=ACM > "%STAGED_FILES_TEMP%"

echo [pre-commit] Running Spotless format…
call gradlew.bat -q spotlessApply
IF ERRORLEVEL 1 EXIT /B 1

echo [pre-commit] Running checks…
call gradlew.bat -q clean check
IF ERRORLEVEL 1 EXIT /B 1

REM Re-add only the originally staged files
echo [pre-commit] Re-staging originally staged files…
FOR /F "delims=" %%f IN (%STAGED_FILES_TEMP%) DO (
  git add "%%f"
)

REM Clean up temp file
DEL "%STAGED_FILES_TEMP%"

echo [pre-commit] OK
