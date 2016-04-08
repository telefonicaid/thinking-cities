Management API allow you to:

- Create services and subservices
- Create users
- Assign roles to users in service

Please, remember that most of these operations are available at the Admin Portal, unless you are integrating dynamic service and user creation with your application, you don't need to use Management API.

# Create service

First, you have to create your service.  Please, take into accout that you will need the IoT Stack admin token (mentioned as {{admin-token}} in the examples) to do so:

```
POST /orc/service/{{service-id}}/service HTTP/1.1
Host: test.ttcloud.net:8008
X-Auth-Token: {{admin-token}}
Content-Type: application/json

{
    "NEW_SERVICE_NAME":"{{Fiware-Service}}",
    "NEW_SERVICE_DESCRIPTION":"new subservice"
}
```

You will receive a response with your {{service-id}}, which is relevant on next steps:

```
HTTP 201 CREATED
Content-Type: application/json

{
  "id": "5e333fcadadc4707b2a3b26e73c07b69"
}
```

# Create subservice 

Once you have one service, you can segment the data on it creating new subservices:

```
POST /orc/service/{{service-id}}/subservice HTTP/1.1
Host: test.ttcloud.net:8008
X-Auth-Token: {{admin-token}}
Content-Type: application/json

{
    "SERVICE_NAME":"{{Fiware-Service}}",
    "NEW_SUBSERVICE_NAME":"{{Fiware-ServicePath}}",
    "NEW_SUBSERVICE_DESCRIPTION":"new subservice"
}
```

You will recieve now a {{subservice-id}} identifying your subservice


```
HTTP 201 CREATED
Content-Type: application/json

{
  "id": "2a3b26e73c07b695e333fcadadc4707b"
}
```

# Create user

Once the service is created, you can create a user as follows:

```
POST /orc/service/669b1e1a3e6c41ee9a1cbe5ab5165f6e/user HTTP/1.1
Host: test.ttcloud.net:8008
X-Auth-Token: {{admin-token}}
Content-Type: application/json

{
    "NEW_SERVICE_USER_NAME":"{{subservice-user-name}}",
    "NEW_SERVICE_USER_PASSWORD":"{{subservice-user-pass}}",
    "NEW_SERVICE_USER_EMAIL":"{{user-email}}",
    "NEW_SERVICE_USER_DESCRIPTION":"an user description"
}
```

This user is not associated to any service or subservice, so now you have asign a role in your subservice.  

First, retrieve the roles created by default on your subservice:

```
GET /orc/service/{{subservice-id}}/role HTTP/1.1
Host: test.ttcloud.net:8008
X-Auth-Token: {{admin-token}}
Content-Type: application/json
```

You will get the list of roles in the response:

```
HTTP 20O OK
Content-Type: application/json

{
  "totalResults": 2,
  "roles": [
    {
      "domain_id": "669b1e1a3e6c41ee9a1cbe5ab5165f6e",
      "name": "SubServiceCustomer",
      "id": "02f33ab1af5b473aa51d71618ccfad84"
    },
    {
      "domain_id": "669b1e1a3e6c41ee9a1cbe5ab5165f6e",
      "name": "SubServiceAdmin",
      "id": "cbae0186206f4a5a8d00f271ac4e0b4e"
    }
  ]
}
```

These are the roles permissions:

- SubServiceCustomer: can query data stored at FIWARE IoT Stack
- SubServiceAdmin: can query and update data at FIWARE IoT Stack

Please note that these roles id are specific per subservice, so you need to retrieve them.

Now you know the roles ids for your subservice, you can assign one role to the user you created before:

```
POST /orc/service/{{subservice-id}}/role_assignments HTTP/1.1
Host: test.ttcloud.net:8008
X-Auth-Token: {{admin-token}}
Content-Type: application/json

{
    "SUBSERVICE_ID":"{{subservice-id}}",    
    "ROLE_ID":"{{role-id}}",
    "SERVICE_USER_ID":"{{user-id}}"
}
```

You will receive a No Content response as follows:

```
HTTP 204 NO CONTENT
```

# In more detail ...

You can get more information about the FIWARE component providing this functionalty, reference API documentation at [IoT Orchestrator apiary](http://docs.orchestrator2.apiary.io/)
