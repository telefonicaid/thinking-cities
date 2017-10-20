You can get Postman here: https://www.getpostman.com/

In order to use the Postman collection indcluded in this directory, you need to define the following variables in your environment:

* **host**: the host for all the APIs
* **service**: service to use in `Fiware-Service` header
* **subservice**: subservice to use in the `Fiware-ServicePath` header
* **user**: user (used by the "Get auth token" operation)
* **password**: password (used by the "Get auth token" operation)
* **tokken**: token to use in `X-Auth-Token` header. Use "Get auth token" operation and take the `x-subject-token` header in the response to set this variable