call mvn clean
call mvn package
ren %cd%\target\price_monitoring_system-1.0-SNAPSHOT.war price-monitoring-system.war
xcopy %cd%\target\price-monitoring-system.war %CATALINA_HOME%\webapps
call %CATALINA_HOME%\bin\startup.bat
pause