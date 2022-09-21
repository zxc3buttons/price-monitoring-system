# PRICE-MONITORING-SYSTEM
## Setting Up Development Environment


1) You need **Java 11+** and set *JAVA_HOME* variable to *path_to_java* and add to *PATH* environment variable *%JAVA_HOME%/bin*


2) You need **PostgreSQL 14** and add to *PATH* environment variable *path_to_postgreSQL_folder/14/bin*


3) You need **Apache-Tomcat 9.x+** version and set *CATALINA_HOME* environment variable *path_to_tomcat_folder*
and add to *PATH* environment variable *%CATALINA_HOME%\bin*


4) You need **Maven** latest version and set *M2_HOME* variable to *path_to_maven* and add to **PATH environment variable *%M2_HOME%\bin*


5) `git clone git@github.com:zxc3buttons/price-monitoring-system.git` in your work folder


6) in *price_monitoring_system/db_init.bat* set *PG_PASSWORD* as your postgres superuser password


7) run *price_monitoring_system/db_init.bat*


8) run *price_monitoring_system/startup.bat*
