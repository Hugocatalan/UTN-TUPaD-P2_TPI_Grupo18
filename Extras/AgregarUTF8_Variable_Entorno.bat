@echo off
REM Define la variable JAVA_TOOL_OPTIONS
setx JAVA_TOOL_OPTIONS "-Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -Dconsole.encoding=UTF-8"

echo JAVA_TOOL_OPTIONS configurada permanentemente.
pause
