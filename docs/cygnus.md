Cygnus connector implements a Flume-based connector for context data coming from Orion Context Broker and aimed to be stored in a specific persistent storage, such as HDFS, CKAN or MySQL.All the details about Flume can be found at flume.apache.org, but, as a reminder, some concepts will be explained here:

- A Flume source is an agent gathering event data from the real source (Twitter stream, a notification system, etc.), either by polling the source or listening for incoming pushes of data. Gathered data is sent to a Flume channel.
- A Flume channel is a passive store (implemented by means of a file, memory, etc.) that holds the event until it is consumed by the Flume sink.
- A Flume sink connects with the final destination of the data (a local file, HDFS, a database, etc.), taking events from the channel and consuming them (processing and/or persisting it).

There exists a wide collection of already developed sources, channels and sinks. Cygnus, extends that collection by adding:

- OrionRestHandler. A custom HTTP source handler for the default HTTP source. The existing HTTP source behaviour can be governed depending on the request handler associated to it in the configuration. In this case, the custom handler takes care of the method, the target and the headers (specially the Content-Type one) within the request, cheking everything is according to the expected request format.
- OrionHDFSSink. A custom sink that persists Orion content data in a HDFS deployment. There is already a native Flume HDFS sink persisting each event in a new file, but this is not suitable for Cygnus. Several HDFS backends can be used for the data persistence (WebHDFS, HttpFS, Infinity), all of them based on the native WebHDFS REST API from Hadoop.
- OrionCKANSink. A custom sink that persists Orion context data in CKAN Open Data website server instances.
- OrionMySQLSink. A custom sink for persisting Orion context data in a MySQL server. Each user owns a database, and each entity is mapped to a table within that database. Tables contain rows about the values such entity’s attributes have had along time.

## Documentation and API

[Connector Framework](https://github.com/telefonicaid/IoT-STH)

