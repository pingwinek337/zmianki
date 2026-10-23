@echo off
cd /d "%~dp0"
where git >nul 2>nul
if errorlevel 1 (
  echo Nie masz Gita. Zainstaluj: https://git-scm.com/download/win
  echo Potem uruchom ten plik jeszcze raz.
  pause
  exit /b 1
)
if exist .git rmdir /s /q .git
git init -b main
git add -A
git -c user.name=pingwinek337 -c user.email=pingwinek337@users.noreply.github.com commit -m "AutoReroll mod"
git remote add origin https://github.com/pingwinek337/AUTOZMIANECZKA.git
echo.
echo Teraz pojawi sie okno logowania do GitHuba - zaloguj sie.
git push -u origin main --force
echo.
echo Gotowe. Wejdz na GitHub, zakladka Actions.
pause
