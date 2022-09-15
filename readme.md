#PRICE-MONITORING-SYSTEM
##Setting Up Development Environment


You need Java 11+ and set JAVA_HOME variable to path_to_java and add to PATH environment variable %JAVA_HOME%/bin


You need PostgreSQL 14 and add to PATH environment variable path_to_postgreSQL_folder/14/bin


You need Apache-Tomcat 9.x+ version and set CATALINA_HOME environment variable path_to_tomcat_folder
and add to PATH environment variable %CATALINA_HOME%\bin


You need maven latest version and set M2_HOME variable to path_to_maven and add to PATH environment variable %M2_HOME%\bin


git clone git@git-students.senla.eu:moscow2022-2/oleg_tokarev.git


in price_monitoring_system/db_init.bat set PG_PASSWORD as your postgres superuser password


run price_monitoring_system/db_init.bat


run price_monitoring_system/startup.bat