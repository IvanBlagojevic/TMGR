=============================================================================================
UPGRADE INSTRUCTIONS
=============================================================================================
Upgrade process is executed on your current database; no data is copied to some other database.
	It is strongly recommended to make MySQL backup before starting upgrade.
	For all upgrades, once the upgrade is complete, it is recommended that index.rebuildOnStartup
	is set back to false in /opt/GS4TR_TMGR_HOME/config/services.properties.

	If you perform test upgrade, on QA instance, execute the following script after upgrade 
	has been completed:
	UPDATE TM_USER_PROFILE SET PASSWORD=sha('password1!') WHERE username = ('admin');
	UPDATE TM_USER_PROFILE SET EMAIL_ADDRESS = 'fake@fake.com' WHERE USERNAME <> 'system';
	To enter debug mode go to tomcat/webapp/TMGR/WEB-INF/classes/log4j.properties and update 
	log4j.category.org.gs4tr.termmanager=ERROR to INFO.



NOTE:
=============================================================================================

If upgrading from TMGR 4.5.2 or prior, then you must upgrade to TMGR 4.6.1 first. Use the 
respective build upgrade instructions.
If upgrading from TMGR 4.6.1, then you must migrate to SOLR first using TMGR 4.7 solr migration 
jar.
Once on 4.7 you can upgrade to any version from 4.8.1 through 4.10.0. The steps are identical.
Once on 4.7, you can also upgrade directly to TMGR 4.11.0 or 4.12.1 solr cloud builds.
If you're already on 4.11 then follow the last set of instructions to get to 4.12.1+.
		
=============================================================================================
From 4.6 to 4.7.0
=============================================================================================
IMPORTANT: TMGR 47 uses SOLR index to store terms. Terms will no longer be in MySQL “TM_TERM” 
and “TM_TERMENTRY" tables. Instead, all regular terms will be moved to the designated solrhome
directory during migration. 
 
1. Back up MySQL DB & TMGR resources folders

2. Shutdown TMGR. You will need to kill the java process as well.

3. Clear out everything in "/opt/GS4TR_TMGR_HOME” folder.

4. Unzip tmgr-resources-4.7.0.zip

5. In the server.properties file in the config folder, update ip.address, and set 
serverAddress to TMGR instance URL. Ensure that index.rebuildOnStartup is set to "true"

6. In the solr-migration.properties file update migration.db.username to “DBuser” & 
migration.db.password to “DBpassword” 

7. Unzip tmgr-solr-migration.zip into "/opt/GS4TR_TMGR_HOME/config/solrhome” folder.

8. Go to configuration.properties file in the solrhome folder and update resource.path to 
“/opt/GS4TR_TMGR_HOME/config”.

9. In the same folder start SOLR migration by executing "/opt/jdk1.7.0_45/bin/java -jar 
gs4tr-termmanager-solr-migration-4.7.0.jar -c configuration.properties”

	NOTE: You should see history migration progress followed by term migration. 
	Do not exit your session or you will need to start over. If migration fails at any 
	time you must drop term_manager MySQL DB & re-import it. You will also need to clear 
	out the solrhome folder, re-unzip tmgr-solr-migration.zip, update all configuration 
	and run the migration jar again.

10. Once migration is completed successfully, unzip gs4tr-termmanager-webapp-4.7.0-imp.war 
into “/opt/apache-tomcat-7.0.27-GL/webapps/TMGR”.

11. Update context.xml in the application META-INF folder with MySQL username and password.

12. Increase the <session-timout> from “20” to “180” in web.xml of the 
application WEB-INF folder.

13. Go to bin folder & update catalina.sh with "-Dsolr.solr.home=$GS4TR_TMGR_HOME/solrhome”

	Start TMGR
	
	
NOTE: 	
=============================================================================================
Java 7 is required for this build.
Be sure you are using a non-expired license by replacing the license in the 47 resources
folder.
Images/pictures stored in term_manager_jack are not supported in this version. You must
upgrade to 4.8.0 or higher to properly migrate them. 
If your database does not contain images and you're not able to startup then try shutting
down, execute "TRUNCATE TABLE TM_VERSION" in MySQL and startup again.
		
=============================================================================================

From 4.7.0/4.8.0/4.9.0/4.9.1 to 4.10
=============================================================================================
1. shutdown TMGR and kill the process

2. make backup of your database(s)

3. Unzip the resource file.

4. Set the services.properties ip.address and the TMGR URL under server.Address. Also,
you must set the index.rebuildOnStartup to 'true' as re-index is required.

5. Set solr-migration.properties with MySQL db.username and db.password.

6. Unzip the application file into “/opt/apache-tomcat-7.0.27-GL/webapps/TMGR”

7. Update context.xml in the application META-INF folder with MySQL username and password.

8. Increase the <session-timout> from “20” to at least 60 in web.xml of the application WEB-INF 
folder.

	Start TMGR
	 
	
NOTE: 	
=============================================================================================
JAVA 1.8.0_51 is required for 4.9+. Be sure to point startup.sh file to the new JAVA 
folder.
If your database does not contain images and you're not able to startup then try shutting
down, execute "TRUNCATE TABLE TM_VERSION" in MySQL and startup again.
Additional errors are expected on startup. Please wait for the app to start and re-indexing 
to complete.

=============================================================================================
From 4.7.0/4.8.0/4.9.0/4.10.0 to 4.11.0/4.12.1/4.12.2/4.12.3
=============================================================================================
IMPORTANT: These instructions are for single VM setup only. For multi-VM setup please
check documentation under TMGR resources.

1. Shutdown TMGR and kill the process

2. make backup of your database(s)

3. Unzip the resource file.

4. Download and unpack solr 5.4.1 in the desired directory - e.g. /opt/solr-5.4.1/
Go to http://archive.apache.org/dist/lucene/solr/x.x.x/solr-x.x.x.tgz . 
Replace x.x.x with version of SOLR to be downloaded.

5. Copy solrhome folder from /opt/GS4TR_TMGR_HOME/config/solrhome to /opt/solr-5.4.1/.

6. Ensure that /opt/solr-5.4.1/bin/solr has jkd1.8.0_51 or newer set for JAVA_HOME and 
then start solr with the following command:

./solr start -c -p 8983 -m 2g -s /opt/solr-5.4.1/solrhome 

-c tells SOLR to start in “cloud mode”. This will also starts embedded ZooKeeper on port 
9983 (by default, ZooKeeper will start on solr port + 1000)
-p 8983 tells SOLR to use port 8983
-s <SOLR_HOME> tells SOLR where it should store data


7. Ensure that /opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh has jkd1.8.0_51 or 
newer set for JAVA_HOME. Then upload configuration set to Zookeeper with the following command:

/opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh -cmd upconfig -z
localhost:9983 -n tmgr -d /opt/solr-5.4.1/solrhome/configsets/tmgr

8. Create collections for Regular and Submission terms. This will be the SOLR index. Both 
collections are required for TMGR upgrade and can be created by running the following:

curl 'http://localhost:8983/solr/admin/collections?
action=CREATE&name=regular&replicationFactor=1&maxShardsPerNode=2&coll
ection.configName=tmgr&router.name=implicit&shards=regular_shard_1'

curl 'http://localhost:8983/solr/admin/collections?
action=CREATE&name=submission&replicationFactor=1&maxShardsPerNode=2&c
ollection.configName=tmgr&router.name=implicit&shards=submission_shard_1'
 
9. Check /opt/GS4TR_TMGR_HOME/config/solrservices.properties file has the following:

solr.zkhosts=localhost:9983 
solr.regular.terms.path=regular
solr.submission.terms.path=submission


10. Set the following in the /opt/GS4TR_TMGR_HOME/config/services.properties: 

ip.address 
server.Address (this should be the client URL)
index.rebuildOnStartup (set to 'true' for upgrades only)
mail.from.name=<Insert Client Name> Term Manager (Do not add client name for shared instances).


11. Unzip the WAR file into “/opt/apache-tomcat-8.0.24-GL/webapps/TMGR”

12. Update context.xml in the webapps/TMGR/META-INF folder with MySQL username and password.

13. Increase the <session-timout> to at least “60” in web.xml of the application WEB-INF 
folder.

	Start TMGR
	

	
NOTE: 	
=============================================================================================

Following errors are expected on startup:
ERROR [jdbc.audit][pool-2-thread-1][user:] - <10. Statement.executeUpdate(alter table 
TM_SUBMISSION_TERMENTRY drop constraint IDX_ST_UUID) alter table TM_SUBMISSION_TERMENTRY 
drop constraint IDX_ST_UUID =>
com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: You have an error in your SQL 
syntax; check the manual that corresponds to your MySQL server version for the right syntax 
to use near 'constraint IDX_ST_UUID' at line 1

Once TMGR starts-up, wait for indexing to reach 100%. You can track indexing by reloading 
browser. After 100% please wait for V2 back up writing to complete, this can take as long as the
re-indexing part. TMGR UI will not be available until both re-indexing and V2 back up is done.
Once you can access the browser, check that you can access the term list to confirm your setup.
If it’s a fresh DB, then check that you can add a term and save it.

=============================================================================================
From 4.11.0 to 4.12.1/4.12.2/4.12.3
=============================================================================================
IMPORTANT: These instructions are for single VM setup only. For multi-VM setup please
check documentation under TMGR resources.

1. Shutdown TMGR and kill the process

2. Shutdown SOLR if it was previously started: /opt/solr-5.4.1/bin/solr stop

3. make backup of your database(s)

4. Unzip the resource file.

5. Delete current 'solrhome' and 'solrhomezoo_data' folders:

rm -r /opt/solr-5.4.1/solrhome
rm -r /opt/solr-5.4.1/solrhomezoo_data


6. Copy solrhome folder from /opt/GS4TR_TMGR_HOME/config/solrhome to /opt/solr-5.4.1/.

7. Ensure that /opt/solr-5.4.1/bin/solr has jkd1.8.0_51 or newer set for JAVA_HOME and 
then start solr with the following command:

./solr start -c -p 8983 -m 2g -s /opt/solr-5.4.1/solrhome 

-c tells SOLR to start in “cloud mode”. This will also starts embedded ZooKeeper on port 
9983 (by default, ZooKeeper will start on solr port + 1000)
-p 8983 tells SOLR to use port 8983
-s <SOLR_HOME> tells SOLR where it should store data


8. Ensure that /opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh has jkd1.8.0_51 or 
newer set for JAVA_HOME. Then upload configuration set to Zookeeper with the following command:

/opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh -cmd upconfig -z
localhost:9983 -n tmgr -d /opt/solr-5.4.1/solrhome/configsets/tmgr

9. Create collections for Regular and Submission terms. This will be the SOLR index. Both 
collections are required for TMGR upgrade and can be created by running the following:

curl 'http://localhost:8983/solr/admin/collections?
action=CREATE&name=regular&replicationFactor=1&maxShardsPerNode=2&coll
ection.configName=tmgr&router.name=implicit&shards=regular_shard_1'

curl 'http://localhost:8983/solr/admin/collections?
action=CREATE&name=submission&replicationFactor=1&maxShardsPerNode=2&c
ollection.configName=tmgr&router.name=implicit&shards=submission_shard_1'
 
10. Check /opt/GS4TR_TMGR_HOME/config/solrservices.properties file has the following:

solr.zkhosts=localhost:9983 
solr.regular.terms.path=regular
solr.submission.terms.path=submission


11. Set the following in the /opt/GS4TR_TMGR_HOME/config/services.properties: 

ip.address 
server.Address (this should be the client URL)
index.rebuildOnStartup (set to 'true' for upgrades only)
mail.from.name=<Insert Client Name> Term Manager (Do not add client name for shared instances).


12. Unzip the WAR file into “/opt/apache-tomcat-8.0.24-GL/webapps/TMGR”

13. Update context.xml in the webapps/TMGR/META-INF folder with MySQL username and password.

14. Increase the <session-timout> to at least “60” in web.xml of the application WEB-INF folder.

	Start TMGR


=============================================================================================
From (4.12.1 to 4.12.2/4.12.3) OR (4.12.2 to 4.12.3)
=============================================================================================	

1. Unzip the WAR file into “/opt/apache-tomcat-8.0.24-GL/webapps/TMGR”

2. Update context.xml in the webapps/TMGR/META-INF folder with MySQL username and password.

3. Increase the <session-timout> to at least “60” in web.xml of the application WEB-INF folder.

   Start TMGR
   
   NOTE: If you need to re-index solr due to a corrupted or lost data,
   		 shutdown TMGR and kill the process,
   		 set the following in the /opt/GS4TR_TMGR_HOME/config/services.properties:  
   		 index.rebuildOnStartup=true
   		 and
   		 index.rebuildVersion=V2
   		 and
   		 Start TMGR


=============================================================================================
From 4.11.0/4.12.1/4.12.2/4.12.3 to 5.0.1
=============================================================================================

IMPORTANT: These instructions are for single VM setup only. For multi-VM setup please
           check documentation under TMGR resources.
           Since, TMGR 5.0.1, default database is MariaDB and MySQL is optional.
           Steps for MariaDB migration are described from 18. to 36.

NOTE: During the upgrade, db backup table will be recreated from SOLR DB.
      Thus, for this migration only, you will need both, MariaDB/MySQL, and SOLR from the previous version.
      It is important not to delete or overwrite indexed data inside solrhome folder.

1. Shutdown TMGR and kill the process

2. Shutdown SOLR if it was previously started: /opt/solr-5.4.1/bin/solr stop

3. Make backup of your database(s)

4. Unzip the resource file but be sure not to delete existing solrhome.

5. Delete jars from 'solrhome/lib' folder:

	rm -r /path/to/solrhome/lib/*

	for example: rm -r /opt/solrhome/lib/*

6. Copy all jars from TMGR_resources/solrhome/lib/ to /path/to/solrhome/lib/

	for example: cp /opt/GS4TR_TMGR_HOME/config/solrhome/lib/* /opt/solrhome/lib/

7. Create tmgrV2 folder under /path/to/solrhome/configsets/

    ***** NOTE: This command will create tmgrV2 folder where new solr configuration will be stored. *****

	for example: mkdir /opt/solrhome/configsets/tmgrV2

8. Copy new configsets files from TMGR_resources/solrhome/configsets/tmgr folder to /path/to/solrhome/configsets/tmgrV2

	for example: cp -r /opt/GS4TR_TMGR_HOME/config/solrhome/configsets/tmgr/* /opt/solrhome/configsets/tmgrV2/

9. Ensure that /opt/solr-5.4.1/bin/solr has jkd1.8.0_51 or newer set for JAVA_HOME and then start solr with the following command:

	./solr start -c -p 8983 -m 2g -s /opt/solrhome

	-c tells SOLR to start in “cloud mode”. This will also starts embedded ZooKeeper on port 9983 (by default, ZooKeeper will start on solr port + 1000)
	-p 8983 tells SOLR to use port 8983
	-s <SOLR_HOME> tells SOLR where it should store data


10. Ensure that /opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh has jkd1.8.0_51 or newer set for JAVA_HOME. Then upload configuration set to Zookeeper with the following command:

    /path/to/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh -cmd upconfig -z localhost:9983 -n tmgrV2 -d /path/to/solrhome/configsets/tmgrV2

    for example:

	/opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh -cmd upconfig -z localhost:9983 -n tmgrV2 -d /opt/solrhome/configsets/tmgrV2

11. Create collections for regular and submission terms. This will be the SOLR index. Both collections are required for TMGR upgrade and can be created by running the following:

    ***** NOTE: This command will create regularV2 collection where regular terms will be indexed and stored. *****

	curl 'http://localhost:8983/solr/admin/collections?action=CREATE&name=regularV2&replicationFactor=1&maxShardsPerNode=2&collection.configName=tmgrV2&router.name=implicit&shards=regularV2_shard_1'

	***** NOTE: This command will create submissionV2 collection where submission terms will be indexed and stored. *****

	curl 'http://localhost:8983/solr/admin/collections?action=CREATE&name=submissionV2&replicationFactor=1&maxShardsPerNode=2&collection.configName=tmgrV2&router.name=implicit&shards=submissionV2_shard_1'

12. Check /opt/GS4TR_TMGR_HOME/config/solrservices.properties file has the following:

	solr.zkhosts=localhost:9983
	solr.regular.terms.path=regular
	solr.submission.terms.path=submission
	solr.regularv2.terms.path=regularV2
	solr.submissionv2.terms.path=submissionV2

13. Set the following in the /opt/GS4TR_TMGR_HOME/config/services.properties:

	ip.address
	server.Address (this should be the client URL)
	mail.from.name=<Insert Client Name> Term Manager (Do not add client name for shared instances).


14. Unzip the WAR file into “/opt/apache-tomcat-8.0.24-GL/webapps/TMGR”

15. Replace context.xml in the webapps/TMGR/META-INF with context.xml.mysql under the same folder

16. Update context.xml in the webapps/TMGR/META-INF folder with MySQL username and password.

16. Increase the <session-timout> to at least “60” in web.xml of the application WEB-INF folder.

17.	Start TMGR and wait rebuild index to finish.

---------------------------------------------------------------------------------------------------------
Now if you want to run TMGR on MySQL you don't need to do anything else. You are done.
However it is recommended that you migrate to default database server which is MariaDB in this version.
Usually, MariaDB is installed on different VM and since this is single VM installation guide,
you need to deploy TMGR on that VM and SOLR also. To do that, Follow next steps:
---------------------------------------------------------------------------------------------------------

18. Shutdown TMGR and kill the process.

19. Shutdown SOLR if it was previously started: /opt/solr-5.4.1/bin/solr stop

20. Make backup of your database(s) and create mysql dump file.

21. GO TO MariaDB VM.

22. Import mysql dump file into the MariaDB.

23. Unzip the resource file.

24. Download and unpack solr 5.4.1 in the desired directory - e.g. /opt/solr-5.4.1/
    Go to http://archive.apache.org/dist/lucene/solr/x.x.x/solr-x.x.x.tgz .
    Replace x.x.x with version of SOLR to be downloaded.

25. Copy solrhome folder from /opt/GS4TR_TMGR_HOME/config/solrhome to /opt/solr-5.4.1/.

26. Ensure that /opt/solr-5.4.1/bin/solr has jkd1.8.0_51 or newer set for JAVA_HOME and
    then start solr with the following command:

./solr start -c -p 8983 -m 2g -s /opt/solr-5.4.1/solrhome

-c tells SOLR to start in “cloud mode”. This will also starts embedded ZooKeeper on port
9983 (by default, ZooKeeper will start on solr port + 1000)
-p 8983 tells SOLR to use port 8983
-s <SOLR_HOME> tells SOLR where it should store data


27. Ensure that /opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh has jkd1.8.0_51 or
   newer set for JAVA_HOME. Then upload configuration set to Zookeeper with the following command:

/opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh -cmd upconfig -z
localhost:9983 -n tmgrV2 -d /opt/solr-5.4.1/solrhome/configsets/tmgr

28. Create collections for Regular and Submission terms. This will be the SOLR index. Both
collections are required for TMGR upgrade and can be created by running the following:

curl 'http://localhost:8983/solr/admin/collections?
action=CREATE&name=regularV2&replicationFactor=1&maxShardsPerNode=2&coll
ection.configName=tmgrV2&router.name=implicit&shards=regularV2_shard_1'

curl 'http://localhost:8983/solr/admin/collections?
action=CREATE&name=submissionV2&replicationFactor=1&maxShardsPerNode=2&c
ollection.configName=tmgrV2&router.name=implicit&shards=submissionV2_shard_1'

30. Check /opt/GS4TR_TMGR_HOME/config/solrservices.properties file has the following:

solr.zkhosts=localhost:9983
solr.regular.terms.path=regular
solr.submission.terms.path=submission
solr.regularv2.terms.path=regularV2
solr.submissionv2.terms.path=submissionV2

31. Change property "index.restoreFromBackup" in /opt/GS4TR_TMGR_HOME/config/services.properties file to:
    index.restoreFromBackup=true

32. Set the following in the /opt/GS4TR_TMGR_HOME/config/services.properties:

    	ip.address
    	server.Address (this should be the client URL)
    	mail.from.name=<Insert Client Name> Term Manager (Do not add client name for shared instances).


33. Unzip the WAR file into “/opt/apache-tomcat-*/webapps/TMGR”

34. Update context.xml in the webapps/TMGR/META-INF folder with MariaDB username and password.

35. Increase the <session-timout> to at least “60” in web.xml of the application WEB-INF folder.

36.	Start TMGR and wait rebuild index to finish.

=============================================================================================
From 5.0.1 to 5.1.0
=============================================================================================

1. Shut down TMGR tomcat and SOLR

2. Back up the TMGR app, GS4TR_TMGR_HOME folder and dbs

3. Unzip the WAR file into “/opt/apache-tomcat-8.0.24-GL/webapps/TMGR”

4. Update context.xml in the webapps/TMGR/META-INF folder with MySQL username and password.

5. Increase the <session-timout> to at least “60” in web.xml of the application WEB-INF folder.

6. Replace existing /path/to/solrhome/lib/gs4tr-termmanager-solr-plugin-5.0.1.jar
   with new TMGR_resources/solrhome/lib/gs4tr-termmanager-solr-plugin-5.1.0.jar

   For example:

   rm /opt/solrhome/lib/gs4tr-termmanager-solr-plugin-5.0.1.jar
   cp /opt/GS4TR_TMGR_HOME/config/solrhome/lib/gs4tr-termmanager-solr-plugin-5.1.0.jar /opt/solrhome/lib/

7. Re-start SOLR

8. Start TMGR
   During the migration, there will be a process to recount the terms which can take quite some time depending on size of the database.
   This recount will not be visible in the logs.
   Do not stop or kill the process until you see the startup message or an error in the logs that is not mentioned below.

NOTE: If you need to re-index solr due to a corrupted or lost data,
   	  shutdown TMGR and kill the process,
   	  set the following in the /opt/GS4TR_TMGR_HOME/config/services.properties:
   	  index.restoreFromBackup=true
   	  and
   	  Start TMGR

=============================================================================================
From 5.1.0 to 5.2.0
=============================================================================================

1. Shut down TMGR tomcat and SOLR

2. Back up the TMGR app, GS4TR_TMGR_HOME folder and dbs

3. Unzip the WAR file into “/opt/apache-tomcat-9.0.5-GL/webapps/TMGR”

4. Update context.xml in the webapps/TMGR/META-INF folder with MySQL username and password.

5. Increase the <session-timout> to at least “60” in web.xml of the application WEB-INF folder.

6. Replace existing /path/to/solrhome/lib/gs4tr-termmanager-solr-plugin-5.1.0.jar
   with new TMGR_resources/solrhome/lib/gs4tr-termmanager-solr-plugin-5.2.0.jar

   For example:

   rm /opt/solrhome/lib/gs4tr-termmanager-solr-plugin-5.1.0.jar
   cp /opt/GS4TR_TMGR_HOME/config/solrhome/lib/gs4tr-termmanager-solr-plugin-5.2.0.jar /opt/solrhome/lib/

7. Re-start SOLR

8. Start TMGR
   During the migration, there will be a process to recount the terms which can take quite some time depending on size of the database.
   This recount will not be visible in the logs.
   Do not stop or kill the process until you see the startup message or an error in the logs that is not mentioned below.

NOTE: If you need to re-index solr due to a corrupted or lost data,
   	  shutdown TMGR and kill the process,
   	  set the following in the /opt/GS4TR_TMGR_HOME/config/services.properties:
   	  index.restoreFromBackup=true
   	  and
   	  Start TMGR

=============================================================================================
From 5.2.0 to 5.3.0
=============================================================================================

1. Shut down TMGR tomcat and SOLR

2. Back up the TMGR app, GS4TR_TMGR_HOME folder and dbs

3. Unzip the WAR file into “/opt/apache-tomcat-9.0.10-GL/webapps/TMGR”

4. Update context.xml in the webapps/TMGR/META-INF folder with MySQL username and password.

5. Increase the <session-timout> to at least “60” in web.xml of the application WEB-INF folder.

6. Replace existing /path/to/solrhome/lib/gs4tr-termmanager-solr-plugin-5.2.0.jar
   with new TMGR_resources/solrhome/lib/gs4tr-termmanager-solr-plugin-5.3.0.jar

   For example:

   rm /opt/solrhome/lib/gs4tr-termmanager-solr-plugin-5.2.0.jar
   cp /opt/GS4TR_TMGR_HOME/config/solrhome/lib/gs4tr-termmanager-solr-plugin-5.3.0.jar /opt/solrhome/lib/

   Replace existing /path/to/solrhome/lib/gs4tr-foundation-locale-1.0.42.jar
   with new TMGR_resources/solrhome/lib/gs4tr-foundation-locale-1.0.44.jar

   For example:

   rm /opt/solrhome/lib/gs4tr-foundation-locale-1.0.42.jar
   cp /opt/GS4TR_TMGR_HOME/config/solrhome/lib/gs4tr-foundation-locale-1.0.44.jar /opt/solrhome/lib/

7. Re-start SOLR

8. Start TMGR
   During the migration, there will be a process to recount the terms which can take quite some time depending on size of the database.
   This recount will not be visible in the logs.
   Do not stop or kill the process until you see the startup message or an error in the logs that is not mentioned below.

NOTE: If you need to re-index solr due to a corrupted or lost data,
   	  shutdown TMGR and kill the process,
   	  set the following in the /opt/GS4TR_TMGR_HOME/config/services.properties:
   	  index.restoreFromBackup=true
   	  and
   	  Start TMGR

=============================================================================================
From 5.0.1/5.1.0/5.2.0/5.3.0 to 5.4.0
=============================================================================================

1. Shut down TMGR tomcat and SOLR. Make backup of your DBs

2. Unzip the WAR file into "/opt/apache-tomcat-9.0.10-GL/webapps/TMGR"
   Unzip the resource file at /opt/GS4TR_TMGR_HOME (previously emptied)

3. Delete index folder contents (usually in "/opt/solrhome" folder) and copy new solrhome (copy all contents from GS4TR_TMGR_HOME/config/solrhome/)

4. Change the following according to your server in the /opt/GS4TR_TMGR_HOME/config/services.properties
	ip.address
	server.Address
	index.restoreFromBackup=true

5. Update context.xml in the webapps/TMGR/META-INF folder with your MariaDB username and password.

6. delete zookeeper folder (usually "solrhomezoo_data")

7. delete dbs and create brand new ones
	create database term_manager CHARACTER SET utf8 COLLATE utf8_unicode_ci;
	GRANT ALL PRIVILEGES ON term_manager.* TO 'pd4'@'localhost';
	create database term_manager_jack CHARACTER SET utf8 COLLATE utf8_unicode_ci;
	GRANT ALL PRIVILEGES ON term_manager_jack.* TO 'pd4'@'localhost';

8. Import your db

9. Ensure that /opt/solr-5.4.1/bin/solr has jdk1.8.0_92 or newer set for JAVA_HOME and then start solr with the following command:
	/opt/solr-5.4.1/bin/solr start -c -p 8983 -m 2g -s /opt/solrhome

10. Ensure that /opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh has jdk1.8.0_92 or newer set for JAVA_HOME. Then upload configuration set to Zookeeper with the following command:
	/opt/solr-5.4.1/server/scripts/cloud-scripts/zkcli.sh -cmd upconfig -z localhost:9983 -n tmgr -d /opt/solrhome/configsets/tmgr

11. Create collections for regular and submission terms. This will be the SOLR index. Both collections are required for TMGR upgrade and can be created by running the following:
	curl 'http://localhost:8983/solr/admin/collections?action=CREATE&name=regularV2&replicationFactor=1&maxShardsPerNode=2&collection.configName=tmgr&router.name=implicit&shards=regularV2_shard_1'

	curl 'http://localhost:8983/solr/admin/collections?action=CREATE&name=submissionV2&replicationFactor=1&maxShardsPerNode=2&collection.configName=tmgr&router.name=implicit&shards=submissionV2_shard_1'

12.Check /opt/GS4TR_TMGR_HOME/config/solrservice.properties file has the following:
    solr.zkhosts=localhost:9983
    solr.regular.terms.path=regular
    solr.submission.terms.path=submission
    solr.regularv2.terms.path=regularV2
    solr.submissionv2.terms.path=submissionV2

13. Start TMGR

NOTE:
These steps will also help SOLR troubleshooting in case TMGR does not start, or the terms cannot be viewed/added.
You'll need to re-import the DB, and be sure to delete solrhomezoo_data and solrhome entirely again

=============================================================================================
NOTE:
=============================================================================================

Following errors are expected on startup:
ERROR [jdbc.audit][pool-2-thread-1][user:] - <10. Statement.executeUpdate(alter table
TM_SUBMISSION_TERMENTRY drop constraint IDX_ST_UUID) alter table TM_SUBMISSION_TERMENTRY
drop constraint IDX_ST_UUID =>
com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException: You have an error in your SQL
syntax; check the manual that corresponds to your MySQL server version for the right syntax
to use near 'constraint IDX_ST_UUID' at line 1

Once TMGR starts-up, the index progress will remain at 0% while V2 back is being rewritten.
Once Indexing starts you'll be able to refresh login screen for progress.
Users cannot login during that time.

=============================================================================================
SOLR troubleshooting:

If your upgrade results in an empty Term List, or an application error when trying to access your terms,
then you need to redeploy SOLR with the previously created collection removed, and the zookeeper cache cleared. The process is as follows:

    1) Shutdown TMGR app and kill the process
    2) Stop SOLR
    3) Clear out /opt/solrhome  (this will remove the collections)
    4) Remove /opt/solrhomezoo_data (this will clear out the zookeeper cache)
    5) Copy /opt/GS4TR_TMGR_HOME/configuration/solrhome to /opt/solrhome
    6) Start SOLR ensuring it's pointing to /opt/solrhome
    7) Upload zookeeper configuration
    8) Create the regular and submission term collections
    9) Start TMGR with restore from backup set to true in the services.properties

=============================================================================================

From 5.4.0 to 5.5.0
=============================================================================================
NOTE:
Java 11 is required for this build.

There is no need to configure index rebuild process.
It will start automatically when TMGR is started.
=============================================================================================

1. Shut down TMGR tomcat and SOLR. Make backup of your DBs

2. Unzip the WAR file into "/opt/apache-tomcat-9.0.10-GL/webapps/TMGR"
   Unzip the resource file at /opt/GS4TR_TMGR_HOME (previously emptied)

3. Change the following according to your server in the /opt/GS4TR_TMGR_HOME/config/services.properties
	ip.address
	server.Address

4. Update context.xml in the webapps/TMGR/META-INF folder with your MariaDB username and password.

5. Download and unpack solr 5.5.4 in the desired directory - e.g. /opt/solr-5.5.4/
Go to http://archive.apache.org/dist/lucene/solr/x.x.x/solr-x.x.x.tgz . 
Replace x.x.x with version of SOLR to be downloaded.

6. Copy solrhome folder from /opt/GS4TR_TMGR_HOME/config/solrhome to /opt/solr-5.5.4/.

7. Copy solr.in.sh file from /opt/GS4TR_TMGR_HOME/config to /opt/solr-5.5.4/bin. Overwrite the existing file.

8. Start solr with the following command:

./solr start -c -p 8983 -m 2g -s /opt/solr-5.5.4/solrhome 

-c tells SOLR to start in “cloud mode”. This will also starts embedded ZooKeeper on port 
9983 (by default, ZooKeeper will start on solr port + 1000)
-p 8983 tells SOLR to use port 8983
-s <SOLR_HOME> tells SOLR where it should store data

9. Upload configuration set to Zookeeper with the following command:

/opt/solr-5.5.4/server/scripts/cloud-scripts/zkcli.sh -cmd upconfig -z
localhost:9983 -n tmgr -d /opt/solr-5.5.4/solrhome/configsets/tmgr

10. Create collections for Regular and Submission terms. This will be the SOLR index. Both
collections are required for TMGR upgrade and can be created by running the following:

curl 'http://localhost:8983/solr/admin/collections?
action=CREATE&name=regular&replicationFactor=1&maxShardsPerNode=2&coll
ection.configName=tmgr&router.name=implicit&shards=regular_shard_1'

curl 'http://localhost:8983/solr/admin/collections?
action=CREATE&name=submission&replicationFactor=1&maxShardsPerNode=2&c
ollection.configName=tmgr&router.name=implicit&shards=submission_shard_1'
 
11. Check /opt/GS4TR_TMGR_HOME/config/solrservices.properties file has the following:

solr.zkhosts=localhost:9983
solr.regular.terms.path=regular
solr.submission.terms.path=submission
solr.regularv2.terms.path=regularV2
solr.submissionv2.terms.path=submissionV2

12. Start TMGR and wait rebuild index to finish.