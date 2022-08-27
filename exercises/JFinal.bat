@echo off

rem -------------------------------------------------------------------------
rem
rem ʹ��˵����
rem
rem 1: �ýű����ڱ����Ŀʱֻ��Ҫ�޸� MAIN_CLASS ��������
rem
rem 2: JAVA_OPTS ��ͨ�� -D ���� undertow.port �� undertow.host �����������
rem    �����ļ��е���ֵͬ���⻹�� undertow.resourcePath, undertow.ioThreads
rem    undertow.workerThreads �����������ͨ�� -D ���д���
rem
rem 3: JAVA_OPTS �ɴ����׼�� java �����в���,���� -Xms256m -Xmx1024m ���ೣ�ò���
rem
rem
rem -------------------------------------------------------------------------

setlocal & pushd


rem ���������,�ýű��ļ����ڱ����ĿʱҪ������
set MAIN_CLASS=demo.DemoConfig

rem Java �����в���,������Ҫ�������������,�ĳ��Լ���Ҫ��,ע��Ⱥ�ǰ�����пո�
rem set "JAVA_OPTS=-Xms256m -Xmx1024m -Dundertow.port=80 -Dundertow.host=0.0.0.0"
rem set "JAVA_OPTS=-Dundertow.port=80 -Dundertow.host=0.0.0.0"


if "%1"=="start" goto normal
if "%1"=="stop" goto normal
if "%1"=="restart" goto normal

goto error


:error
echo Usage: jfinal.bat start | stop | restart
goto :eof


:normal
if "%1"=="start" goto start
if "%1"=="stop" goto stop
if "%1"=="restart" goto restart
goto :eof


:start
set APP_BASE_PATH=%~dp0
set CP=%APP_BASE_PATH%config;%APP_BASE_PATH%lib\*
echo starting jfinal undertow
java -Xverify:none %JAVA_OPTS% -cp %CP% %MAIN_CLASS%
goto :eof


:stop
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo stopping jfinal undertow
for /f "tokens=1" %%i in ('jps -l ^| find "%MAIN_CLASS%"') do ( taskkill /F /PID %%i )
goto :eof


:restart
call :stop
call :start
goto :eof

endlocal & popd
pause