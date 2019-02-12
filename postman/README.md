You can get Postman here: https://www.getpostman.com/

In order to use the Postman collection included in this directory (https://github.com/telefonicaid/thinking-cities/blob/master/postman/ThinkingCities%20APIs%20Basic%20Ops.postman_collection.json), you need to define the following variables in your environment:

* **host**: the host for all the APIs (except IOTA Manager API)
* **hostIota**: the host IOTA Manager API
* **service**: service to use in `Fiware-Service` header
* **subservice**: subservice to use in the `Fiware-ServicePath` header
* **user**: user (used by the "Get auth token" operation)
* **password**: password (used by the "Get auth token" operation)
* **token**: token to use in `X-Auth-Token` header. Use "Get auth token" operation and take the `x-subject-token` header in the response to set this variable
