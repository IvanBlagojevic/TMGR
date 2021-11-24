TMGR SOLR SETUP FOR SINGLE VM INSTALLATION


***** Pre-Condition: Download and unpack TMGR resources *****


1) Download SOLR and unpack 

	1.1) open terminal

	1.2) enter into random folder 
	
	     (example: cd /opt)

	1.3) wget https://archive.apache.org/dist/lucene/solr/5.4.1/solr-5.4.1.tgz
	
	1.4) tar -zxvf solr-5.4.1.tgz 
	
2) Setup configuration by copying 'solrhome' from TMGR resources
	
	2.1) cp -r /path/to/solrhome ./ 
	  
	     (example: cp -r /opt/GS4TR_TMGR_HOME/conf/solrhome ./)
	
3) Start SOLR

	3.1) cd /path/to/solr-5.4.1/bin 
	  
	     (example: cd /opt/solr-5.4.1/bin)
	
	3.2) (skip this step if you have java 8 properly installed)
	
	     edit solr file and insert:
	
		 JAVA_HOME=/opt/jdk1.8.0_51
		 export JAVA_HOME

	3.3) ./solr start -c -m 2g -s /path/to/solrhome 
	  
	     (example: ./solr start -c -m 2g -s /opt/solrhome) 
	  
4) Upload configurations

	4.1) cd /path/to/solr-5.4.1/server/scripts/cloud-scripts 
	  
	     (example: cd /opt/solr-5.4.1/server/scripts/cloud-scripts)
	
	4.2) (skip this step if you have java 8 properly installed)
	
		 edit zkcli.sh file and insert:
	
		 JAVA_HOME=/opt/jdk1.8.0_51
		 export JAVA_HOME
	
	4.3) ./zkcli.sh -cmd upconfig -z localhost:9983 -n tmgr -d /path/to/solrhome/configsets/tmgr 
	  
	     (example: ./zkcli.sh -cmd upconfig -z localhost:9983 -n tmgr -d /opt/solrhome/configsets/tmgr)

5) Create collections
	
	5.1) curl 'http://localhost:8983/solr/admin/collections?action=CREATE&name=regular&replicationFactor=1&maxShardsPerNode=2&collection.configName=tmgr&router.name=implicit&shards=regular_shard1'
	
	5.2) curl 'http://localhost:8983/solr/admin/collections?action=CREATE&name=submission&replicationFactor=1&maxShardsPerNode=2&collection.configName=tmgr&router.name=implicit&shards=submission_shard1'
	
6) TMGR options in solrservice.properties
	
	solr.zkhosts=localhost:9983
	solr.regular.terms.path=regular
	solr.submission.terms.path=submission
	
7) Start TMGR as usual
