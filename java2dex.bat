::执行本脚本的jdk版本为1.8.0，dx版本为30.0.2
::如果jdk是1.8或者更高版本，要创建低版本字节码时必须显示指定版本号，且-source -target 必须同时使用,并设置为相同的版本号值【-source 1.7 -target 1.7】
::如果dx的版本较低而javac版本较高时，dx命令生成dex的过程中可能会出现版本冲突错误【bad class file magic (cafebabe) or version (0034.0000)】
::比如23.0.1版本的dx，最高只支持到jdk 1.7,如果jdk是1.8，则dx必须是23版本以上，或者javac编译时指定生成1.7版本的字节码

::javac编译后的class文件，先打成jar包再转为dex用下面这两步
::jar -cf %~dp0\tmp\javatweak.jar -C %~dp0\tmp .
::dx --dex --output=%~dp0\javatweak.dex %~dp0\tmp\javatweak.jar

::javac编译后的class文件直接转为dex用下面这一步
::dx --dex --output=%~dp0\javatweak.dex %~dp0\tmp

::dx是一个bat脚本，其等价于java -jar %BUILD_TOOLS%\lib\dx.jar

@echo off
set ANDROID_VER=21
set ANDROID_SDK=%~dp0
set ANDROID_API=%ANDROID_SDK%\platforms\android-21
set BUILD_TOOLS=%ANDROID_SDK%\build-tools\30.0.2

set SRCDIR_TWEAKME=%~dp0\src
set SRCDIR_APPTWEAK=%SRCDIR_TWEAKME%\com\android\guobao\liao\apptweak
set SRCDIR_PLUGIN=%SRCDIR_APPTWEAK%\plugin
set SRCDIR_UTIL=%SRCDIR_APPTWEAK%\util

mkdir %~dp0\tmp
javac -bootclasspath %ANDROID_API%\android.jar -classpath %ANDROID_API%\optional\*;%~dp0\libs\* -d %~dp0\tmp %SRCDIR_APPTWEAK%\*.java %SRCDIR_PLUGIN%\*.java %SRCDIR_UTIL%\*.java
java -jar %BUILD_TOOLS%\lib\dx.jar --dex --min-sdk-version=%ANDROID_VER% --output=%~dp0\javatweak.dex %~dp0\libs\* %~dp0\tmp
rmdir /s /q %~dp0\tmp
