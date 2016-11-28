# E2E Flows

## Data sent from app through CB to CKAN

In order to send your data from your application through de Data API to store the data in CKAN resources you need to follow the next steps:

### Pre-requisities:
- Ask for a platform service to the platform administrator
- Ask for an admin user for your service/subservice to the platform administrator.
- Ask for a CKAN user for your service to the platform administrator
- Ask for a organization in CKAN to the platform administrator. The organization name should be the same as the name of your service.
- Ask for a dataset in CKAN to the platform administrator. The name of the dataset should be "yourservice"_"yoursubservice". If you don't have a subservice the name of the dataset should be the name of the service.
- Ask for a resource in CKAN to each one of your entities to the platform administrator. The name of your resources should be "entityname_entitytype". You can also use a single resource for a group of entities (see XXX)
- Ask for a datastore for your resource in CKAN to the platform administrator. The columns of the datastore should have the same name as the name of your antity attributes. There should be also the following mandatory columns (_id, recvtime, fiware-service, fiware-servicepath). Also it is needed to have a column of metadatas for each attribute names "entityattribute_md"
- Create a subservice (not mandatory)
- Create a subscription in CB to CKAN endpoint

### Sending Data
- Ask for a token to the Authentication API (See xxx)
- Send your data to Data API (See xxx)
- Request data to CKAN (via Portal (See xxx) or via API (See xxx)) to check the data is stored properly
- Use your data using CKAN tools or API.