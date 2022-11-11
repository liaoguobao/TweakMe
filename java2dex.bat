@echo off

set JDK_BIN_DIR=
set ANDROID_VER=21
set ANDROID_SDK=%~dp0
set ANDROID_API=%ANDROID_SDK%\platforms\android-21
set BUILD_TOOLS=%ANDROID_SDK%\build-tools\33.0.0

set SRCDIR_TWEAKME=%~dp0\src
set SRCDIR_APPTWEAK=%SRCDIR_TWEAKME%\com\android\guobao\liao\apptweak
set SRCDIR_PLUGIN=%SRCDIR_APPTWEAK%\plugin
set SRCDIR_UTIL=%SRCDIR_APPTWEAK%\util

set DEPEND_JAR=
setlocal enabledelayedexpansion
for %%i in (%~dp0\libs\*.jar) do set DEPEND_JAR=!DEPEND_JAR! %%i
setlocal disabledelayedexpansion

mkdir %~dp0\tmp
%JDK_BIN_DIR%javac -encoding UTF-8 -classpath %ANDROID_API%\android.jar;%ANDROID_API%\optional\*;%~dp0\libs\* -d %~dp0\tmp %SRCDIR_APPTWEAK%\*.java %SRCDIR_PLUGIN%\*.java %SRCDIR_UTIL%\*.java
%JDK_BIN_DIR%jar -cf %~dp0\tmp\javatweak.jar -C %~dp0\tmp .
%JDK_BIN_DIR%java -classpath %BUILD_TOOLS%\lib\d8.jar com.android.tools.r8.D8 --release --min-api %ANDROID_VER% --lib %ANDROID_API%\android.jar --output %~dp0\ %DEPEND_JAR% %~dp0\tmp\javatweak.jar

if exist %~dp0\javatweak.dex del %~dp0\javatweak.dex
ren %~dp0\classes.dex javatweak.dex
rmdir /s /q %~dp0\tmp