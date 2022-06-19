## TweakME

TweakMe是一个轻量级的逆向开发框架。
它与传统的xposed、frida、magisk等框架相比的最大优势是无需root环境。
对手机系统的要求很低，只需要一个正常手机即可对app进行逆向分析。
简单的几步操作就可以完成对指定app的进程注入，可以绕过大多数加固(360、梆梆、爱加密等)的签名证书校验。
完成注入后通过Java编写hook代码，一键脚本编译为dex插件。
目前TweakMe框架在5.0到11.0的android手机上测试通过。
支持arm、arm64两种abi。
详细介绍及使用请看《免ROOT框架TweakMe介绍.docx》。