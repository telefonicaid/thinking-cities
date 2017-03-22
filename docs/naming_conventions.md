The platform impose several constraints with regards to the naming of the persistence elements (files, databases, tables and collections). Such constraints, and the way the different storages solve them, must be had into account when designing the data model of the solution running on top of the platform.

#Global character set and size limitations
Globally, the platform impose certain rules when defining the data model. These rules directly come from Orion Context Broker [constraints]((http://fiware-orion.readthedocs.io/en/master/user/forbidden_characters/index.html)) regarding the character set used for entity and attribute names and their types:

* The following charaters are forbidden: `<`, `>`, `"`, `'`, `=`, `;`, `(` and `)`.
* The following ones should also be avoided: white space, `?`, `/`, `%` and `&`.

Additionally, Orion Context Broker imposes the following restrictions:

* The [FIWARE-service](http://fiware-orion.readthedocs.io/en/master/user/multitenancy/index.html) must be a string of alphanumeric characters including the underscore, whose maximum length is 50 characters.
* The [FIWARE-servicePath](http://fiware-orion.readthedocs.io/en/master/user/service_path/index.html) must be a string defining a Unix-like path starting with slash and having maximum 10 levels; being each level a string of alphanumeric characters including the underscore, whose maximum lenght is 50 characters. Nevertheless, the platform currently only allows a single level.

All these limitations imposed by Orion Context Broker are propagated to the storages via Cygnus connectors with HDFS, MySQL, CKAN and MongoDB/STH.

#HDFS character set and size limitations
Persisting context data in [HDFS](http://fiware-cygnus.readthedocs.io/en/master/cygnus-ngsi/flume_extensions_catalogue/ngsi_hdfs_sink/index.html) implies creating folders and files, whose path names are constrained to 64 character strings of alphanumerics and underscore:

    hdfs:///user/<FIWARE-service>/<FIWARE-servicePath>/<entityID>_<entityType>/<entityID>_<entityType>.txt

Any other character within the FIWARE-service, FIWARE-servicePath, entity ID and type different than alphanumerics and underscore is replaced by underscore.

#MySQL character set and size limitations
Persisting context data in [MySQL](http://fiware-cygnus.readthedocs.io/en/master/cygnus-ngsi/flume_extensions_catalogue/ngsi_mysql_sink/index.html) implies creating databases and tables, whose names are constrained to 64 character strings of alphanumerics and underscore:

* Databases: `<FIWARE-service>`
* Tables: `<FIWARE-servicePath>_<entityID>_<entityType>`.

Any other character within the FIWARE-service, FIWARE-servicePath, entity ID and type different than alphanumerics and underscore is replaced by underscore.

#CKAN character set and size limitations
Persisting context data in [CKAN](http://fiware-cygnus.readthedocs.io/en/master/cygnus-ngsi/flume_extensions_catalogue/ngsi_ckan_sink/index.html) implies creating organization, packages and resurces, whose names are constrained to 64 character strings of alphanumerics and underscore:

* Organizations: `<FIWARE-service>`.
* Packages: `<FIWARE-service>_<FIWARE-servicePath>`.
* Resources: `<entityID>_<entityType>`.

Any other character within the FIWARE-service, FIWARE-servicePath, entity ID and type different than alphanumerics and underscore is replaced by underscore.

#MongoDB/STH character set and size limitations
Persisting context data in [MongoDB](http://fiware-cygnus.readthedocs.io/en/master/cygnus-ngsi/flume_extensions_catalogue/ngsi_mongo_sink/index.html)/[STH](http://fiware-cygnus.readthedocs.io/en/master/cygnus-ngsi/flume_extensions_catalogue/ngsi_sth_sink/index.html) implies creating databases and collections:

* Databases: `<FIWARE-service>`.
* Collections: `<FIWARE-servicePath>`.

Whose names are constrained this way by [MongoDB](https://docs.mongodb.com/manual/reference/limits/#naming-restrictions):

* `/`, `\`, `.`, `"` and `$` are not accepted in the database names. Database names cannot be empty and must have fewer than 64 characters.
* `$`, the empty string and the null character are not accepted in the collection names. In addition, collections cannot start with the `.system` prefix. The maximum length of the collection namespace, which includes the database name, the dot separator, and the collection name (i.e. `<database>.<collection>`), is 120 bytes.

Nevertheless, the above restrictions does not affect the platform because of the limitations imposed to the FIWARE-service and the FIWARE-servicePath.

