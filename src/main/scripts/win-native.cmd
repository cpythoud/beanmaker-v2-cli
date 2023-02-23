@ECHO OFF
REM !!! must be run in x64 Native Tools Command Prompt !!!
native-image -J-Dfile.encoding=IBM00858 -jar beanmaker2-cli-1.0.0-SNAPSHOT-jar-with-dependencies.jar
copy beanmaker2-cli-1.0.0-SNAPSHOT-jar-with-dependencies.exe beanmaker.exe
