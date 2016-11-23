# Users and permissions


## What user permissions are defined in IoTP?

In IoTP there are defined Services and SubServices. SubServices exists into Services.
Tipically a Service represents a smartcity and all of the SubServices represents the verticals of that smartcity. For more details read [multitenancy](../multitenancy.md)

Users are created into Services. The same user name could be used across different Services to represent different Users.
i.e. "adm1" could exists in "smartcity" Service and "adm1" could be another user for "smartgondor" Service

Roles are created into Services. By default all Services created into IoT Platform are created with the following Roles:
- ServiceCustomer: Role for a normal user of the Service, with standard capabilities.
- SubServiceCustomer: Role for a normal user in a SubService, with standard capabilities.
- SubServiceAdmin: Role for an administrator user in a SubService, with full capabilities.

There is one and unique role common for all Services:
- Admin: administrator with full capabilities of the Service

Users receive roles assignments into Services (or Subservices). The permissions are determinated by the Role
which a user has in a service (or subservice). Depending on the IoTP component, user permission implies the ability
of do some actions or not.


| User   | Role               | Service\\```<SubService>```|
| -------|--------------------|------------------------|
| adm1   | admin              | smartcity              |
| adm1   | SubServiceAdmin    | smartcity\\*           |
| Alice  | SubServiceAdmin    | smartgondor\\palaces   |
| bob    | SubServiceCustomer | smartcity\\electricity |
| bob    | SubServiceAdmin    | smartcity\\gardens     |


In deep details, each Role in a Service is defined by a Policy for each IoTP component:

- [IoTP Policies](https://github.com/telefonicaid/orchestrator/tree/master/src/orchestrator/core/policies)
- [Orion component actions](https://github.com/telefonicaid/fiware-pep-steelskin#-rules-to-determine-the-context-broker-action-from-the-request)
- [Perseo component actions](https://github.com/telefonicaid/fiware-pep-steelskin#-rules-to-determine-the-perseo-cep-action-from-the-request)
- [Keypass component actions](https://github.com/telefonicaid/fiware-pep-steelskin#rulesKeypass)
- [Rest API based components (STH, IOTA) actions](https://github.com/telefonicaid/fiware-pep-steelskin#generic-rest-middleware)


## Can I modify permissions for a given user?

The common way to modify permissions for a given user is to assign or unassign Roles.
User can be assgined to admin, ServiceCustomer, SubServiceAdmin and SubServiceCustomer roles in a given Service or SubService.
This can be do it using IoT Portal and [IoT Orchestrator](http://docs.orchestrator2.apiary.io).
- [assign a Role to a User using Orchestrator] (http://docs.orchestrator2.apiary.io/#reference/orchestrator/roles-in-service/create-a-role)


## How can I create a user with special permissions?

To create a new user with special permission you should do the following steps:
- Create new user.
[Orchestrator how to create a new user] (http://docs.orchestrator2.apiary.io/#reference/orchestrator/users-in-service/create-users)

- Create a new Role.
[Orchestrator how to create a new role ] (http://docs.orchestrator2.apiary.io/#reference/orchestrator/roles-in-service/create-a-role)

- Define a new custom XACML Policy, like one of [these](https://github.com/telefonicaid/orchestrator/tree/master/src/orchestrator/core/policies)

- Assign a XACMLpolicy for that role and each IoTP component that you need.
[Set a XACML policy to a Role](http://docs.orchestrator2.apiary.io/#reference/orchestrator/role-in-service/set-xacml-policy-role)

- Assign Role to User.
[Orchestrator assign a Role to a User](http://docs.orchestrator2.apiary.io/#reference/orchestrator/role-assigment/assign-role-to-user)
