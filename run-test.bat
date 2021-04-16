@echo off

REM ===============================================================================================
REM Java options for memory settings, and program arguments.  NOTE: these will likely have to
REM be modified for a *real* load.  The options are:
REM   -deleteAll  [delete the existing documents before doing a re-load]
REM   -dbHost xxxxx  [database server name or IP address]
REM   -dbSchema xxxxx  [database schema name]
REM   -dbUser xxxxx  [database user name]
REM   -dbPassword xxxxx  [database password]
REM   -solrHome xxxxx  [base directory of SOLR installation and data files]
REM   -placeRepStartId xxxxx  [first place-rep identifier to load, or one if not specified]
REM   -placeRepEndId xxxxx [last place-rep identifier to load, or maximum if not specified]
REM ===============================================================================================
REM -XX:+UseG1GC
set jopts=-Xms1000m -Xmx2000m -Xss64m
REM set args=http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places C:/temp/local-all.txt C:/temp/search-results-41k.txt 12 25000
REM set args=http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places C:/temp/local-all.txt C:/temp/search-results-41k.txt C:/temp/json-new
set args=C:/temp/local-all.txt C:/temp/search-new-ordered.txt

REM ===============================================================================================
REM Add the JAR files for the std-lib-place stuff
REM Add the JAR files for the rest of the application (from WAR)
REM ===============================================================================================
set base=C:\Users\wjohnson000\git\std-lib-place
set jars=C:\apps\apache-tomcat-7.0.53\webapps\std-ws-place\WEB-INF\lib
set version=2.0.2-SNAPSHOT

set cp=.\target\classes
REM set cp=%cp%;%base%\place-api\target\std-lib-place-api-%version%.jar
REM set cp=%cp%;%base%\place-data-access\target\std-lib-place-data-access-%version%.jar
REM set cp=%cp%;%base%\place-data-db\target\std-lib-place-data-db-%version%.jar
REM set cp=%cp%;%base%\place-data-load\target\std-lib-place-data-load-%version%.jar
REM set cp=%cp%;%base%\place-data-local\target\std-lib-place-data-local-%version%.jar

set cp=%cp%;%jars%\*
REM for %%j in (%jars%\*.jar) do call :AddToPath %%j

REM C:\tools\openjdk-1.7.0-u60\bin\java -version
java -classpath "%cp%" %jopts% std.wlj.ws.rawhttp.TestSearchMetrics41K %args%
REM java -classpath "%cp%" %jopts% std.wlj.solr.TestSearch %args%
goto :EOF


REM ===============================================================================================
REM Simple process to add a JAR file to the class path
REM ===============================================================================================
:AddToPath
set cp=%cp%;%1
goto :EOF

:EOF
