@echo off
rem Maven olmadan AzerStar-ı işə salır (.m2 keşindəki jarlardan istifadə edir, internet lazım deyil)
setlocal
cd /d "%~dp0"

set "R=%USERPROFILE%\.m2\repository"
set "FX=%R%\org\openjfx"
set "JDK="
if exist "C:\Program Files\Java\jdk-21.0.10\bin\javac.exe" set "JDK=C:\Program Files\Java\jdk-21.0.10\bin\"

set "MP=%FX%\javafx-base\17.0.6\javafx-base-17.0.6-win.jar;%FX%\javafx-graphics\17.0.6\javafx-graphics-17.0.6-win.jar;%FX%\javafx-controls\17.0.6\javafx-controls-17.0.6-win.jar;%FX%\javafx-fxml\17.0.6\javafx-fxml-17.0.6-win.jar"
set "LIBS=%R%\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;%R%\com\google\protobuf\protobuf-java\3.21.9\protobuf-java-3.21.9.jar"
set "OUT=target\run-classes"

if not exist "%OUT%" mkdir "%OUT%"
dir /s /b src\main\java\*.java | findstr /v /i "module-info.java" > target\sources.txt

"%JDK%javac" -encoding UTF-8 -d "%OUT%" --module-path "%MP%" --add-modules javafx.controls,javafx.fxml @target\sources.txt
if errorlevel 1 (
    echo Kompilyasiya xetasi
    exit /b 1
)

"%JDK%java" --module-path "%MP%" --add-modules javafx.controls,javafx.fxml -cp "%OUT%;src\main\resources;%LIBS%" com.azerstar.App
