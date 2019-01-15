The Thinking Cities is a [powered by FIWARE platform](http://marketplace.fiware.org/pages/solutions/c5940dbbdfbcf694f6cdf6ec)
which allows you to connect devices and receive data, integrating all device protocols
and connectivity methods, understanding and interpreting relevant information.
It combines Telefónica components with FIWARE Generic Enablers and isolates data
processing and application service layers from the device and network complexity,
in terms of access, security and network protocols.

These are the main benefits of Thinking Cities platform:

- Simple sensor data integration
- Device-independent APIs for quick app development & lock-in prevention
- Modular
- Scalable. High available
- Open & standards based. FIWARE compliant

## APIs available

Thinking Cities provides the following APIs:

- [Authentication API](authentication_api.md): manages tokens for APIs usage.
- [Device API](device_api.md): allows managing devices, sending data from the device to the cloud and receiving commands.
- [Data API](data_api.md): allows querying and subscribing to data stored at the cloud.
- [Historical Data API](historicdata_api.md): allows querying historic data series and statistics stored at the cloud.
- [CEP API](cep_api.md): allows analyzing data from your IoT device and triggering actions.
- [Management API](management_api.md): allows creating new services and users to provide a multi-tenant environment.

## Multitenancy

Thinking Cities multitenancy model is described [in this section](multitenancy.md).

## Data persistence

FIWARE IoT Data capabilities go far beyond querying the current context data or the short-term history. Thinking Cities provides means for storing hitorical data for the mid and long-term in third-party components; the following ones:

- [HDFS](http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HdfsUserGuide.html), the [Hadoop](http://hadoop.apache.org/) distributed file system.
- [MySQL](https://www.mysql.com/), the well-know relational database manager.
- [CKAN](http://ckan.org/), an Open Data platform.
- [MongoDB](https://www.mongodb.org/), the NoSQL document-oriented database.

## FIWARE Components

Thinking Cities is based on the following [FIWARE components](walkthrough.md) in order to provide its functionality:

The following components used by Thinking Cities have been contributed as open source to FIWARE and can be found within the [FIWARE Catalogue](https://github.com/Fiware/catalogue/):

- IoTAgents (IoTA)
- Context Broker (Orion)
- Short Term Historic (STH)
- Connector Framework (Cygnus)
- Complex Event Processing (Perseo)

The platform also includes the following additional component, which is not part of FIWARE (but still remains open source):

- IoT Orchestrator

The Thinking Cities platform does not use the Identity Management (IDM), Policy Enforcement Point (PEP) and 
Access Control (AC) from the FIWARE Catalogue. The platform contains its own bespoke security components conforming 
with the same GE specifications (see [this clarification](https://ask.fiware.org/question/1/what-is-a-fiware-ge-and-a-gei/) about GE, GEi and GEri terms).

## FIWARE Datamodels

A set of recommended data models to be used with Thinking Cities can be found [here](http://fiware-datamodels.readthedocs.io/en/latest/index.html).
