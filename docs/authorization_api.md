Authorization API allows you to create, assign and retrieve grants for users of Thinking Cities platform.

# Grants

Grants are a kind of subservices (keystone projects) which can imply for a user that has any role in that subservice a permission for do something in an application built on top of ThingkinCities Platform.


Grants always starts with a '#' characters, in contrast to others subservices which starts without that character.

Some examples of grants could be: `#readerApp`, `#adminApp`, `#managerApp` and so on.

There is a complete description about grants for [Urbo2](https://github.com/telefonicasc/urbo2/blob/master/docs/grants.md)


# Create Grants

Grants, like other subservices (keystone project), can be created using [Management API for create subservice](https://thinking-cities.readthedocs.io/en/latest/management_api/index.html#create-subservice)

Morever, like others subservices, can be created using Administration Portal of ThinkingCities Platform.


# Assign Grants

To assign a grant to a user, a role should be assigned to a user in that grant (keystone project) using [Management API for assign role to user](https://orchestrator2.docs.apiary.io/#reference/orchestrator/user-role-assigment/assign-role-to-user)

Morever, like others role assigments, can be performed using Administrationt Portal of ThinkingCities Platform.


# Get Grants

There is an API to retrieve all grants (aka keystone project roles) for a user:


```
GET /v3/users/{user_id}/project_roles HTTP/1.1
Host: <idm_host>:<idm_port>

```

This call uses a x-auth-token associated to <user_id> user.
You will receive an HTTP 200 OK response like this:

```
HTTP 200 OK
Content-Type : application/json

[
    {
        "domain": "8960989b51164eaeaa42200ecc79a47a",
        "project_name": "/smartcity/gardens",
        "project": "031149af6c5147a782e9cf4c56e1fe11",
        "role_name": "8960989b51164eaeaa42200ecc79a47a#SubServiceAdmin",
        "role": "e0da2d91e8154a32980ed4c5a717fd91",
        "user": "bace4fd6bd9b49fda5727eb83a714a3c",
        "user_name": "user1"
    },
  ....
]
```
