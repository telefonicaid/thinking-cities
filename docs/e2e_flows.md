# E2E Flows

## Data sent from application to CKAN through Data API 
In order to send data from your application to CKAN through the Data API you need to follow the next steps:

### Pre-requisities:
- Ask for a service to the platform administrator.
- Ask for an admin user for your service to the platform administrator.
- Ask for a CKAN user for your service to the platform administrator.
- Ask for an organization in CKAN to the platform administrator. The organization name should be the name of your service.
- Ask for a dataset in CKAN to the platform administrator. The name of the dataset should be "yourservice_yoursubservice". If you don't have a subservice the name of the dataset should be the name of the service.
- Ask for a resource in CKAN to each one of your entities to the platform administrator. The name of your resources should be "entityname_entitytype". 
You can also use a single resource for a group of entities using *name mappings* (see XXX)
- Ask for a datastore for each resource in CKAN to the platform administrator. The columns of the datastore should be the name of your entity attributes. The type of the columns should be *json*. There should be also the following mandatory columns:_id, recvTime, fiwareServiePath, entityType, entityId and TimeInstant. Also it is needed to have a column of metadatas for each attribute named "entityattribute_md"
- Create a subservice (not mandatory)
- Create a subscription in CB to CKAN endpoint

### Sending Data
- Request a token through the Authentication API (See xxx)
- Send your data through Data API using the received token (See xxx)
- Request data to CKAN (via Portal (See xxx) or via API (See xxx)) to check the data is stored properly
- Use your data using CKAN tools or API.