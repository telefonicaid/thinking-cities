Management API allow you to:

- Create services and subservices
- Create users
- Assign roles to users 

Please, remember that most of these operations are available at the Admin Portal, unless you are integrating dynamic service and user creation with your application, you don't need to use Management API.

# Create service

You can create a service just 

```
POST /orc/service/{{service-id}}/subservice HTTP/1.1
Host: test.ttcloud.net:8008
X-Auth-Token: {{admin-token}}
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 28ce14a6-4e8e-9ab2-3a18-b0df09cd74bf

{
    "SERVICE_NAME":"{{Fiware-Service}}",
    "NEW_SUBSERVICE_NAME":"{{FiwareService-Path}}",
    "NEW_SUBSERVICE_DESCRIPTION":"new subservice"
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
Cache-Control: no-cache
Postman-Token: 28ce14a6-4e8e-9ab2-3a18-b0df09cd74bf

{
    "SERVICE_NAME":"{{Fiware-Service}}",
    "NEW_SUBSERVICE_NAME":"{{FiwareService-Path}}",
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

# In more detail ...
