# Users and permissions


## What user permissions are defined in IoTP?


In IoTP there are Services and SubServices. SubServices exists into Services.
Tipicaly a service represent a smartcity and all of the subservices represents the verticals of that smartcity.

Users are created into Services. The same user name could be used across diferent Services to represent different users.
i.e. adm1 could exists in "SmartCity" and adm1 could be another users in "SmartGondor"

Roles are created into Services. By default all Services created into IoT platform are created with the following roles:
- ServiceCustomer
- SubServiceCustomer
- SubServiceAdmin

There is one and unique role common for all Services:
- Admin

Users receive roles assignments into Services (or Subservices). The permissions are determinated by the role
which a user has in a service (or subservice). Depending on the IoTP component, user permission implies the ability
of do some actions or not.

In deep details, each Role in a Service is defined by a Policy for each IoTP component.
[IoTP Policies](https://github.com/telefonicaid/orchestrator/tree/master/src/orchestrator/core/policies)

[Orion actions] (https://github.com/telefonicaid/fiware-pep-steelskin#-rules-to-determine-the-context-broker-action-from-the-request)



## Can I modify permissions for a given user?
TBD

## How can I create a user with special permissions?
TBD
