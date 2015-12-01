Authentication system is based on OpenStack Keystone APIs plus a SCIM APIs extension. This modules are used to provide login feature and will allow integrators to manage services, subservices, users and roles.

The platform also includes a control access policy server to validate the actions performed by any user with a component and provide a flexible roles structure.

## ID system: Keystone & SCIM APIs

Keystone SCIM is an OpenStack Keystone extension that enables the management of User, Groups and Roles using SCIM v1.1 standard. As any Keystone extension, it’s designed to be installed on top of an existing Keystone installation, following Keystone recommendations for extensions.The SCIM standard was created to simplify user management in the cloud by defining a schema for representing users and groups and a REST API for all the necessary CRUD operations.

SCIM User and Group API are a direct translation of Keystone User and Group APIs, they even share the same security policies (with the exact same names). On the other hand, SCIM Roles are slightly different from Keystone Roles: now SCIM Roles are domain aware. The extension implementation does not make any modification to the underlying database, in order to maintain backward compatibility with Keystone Roles API.

SCIM Roles are implemented on top of Keystone Roles, prefixing the domain id to the role name. You may argue that this is hacking, and the relational integrity is not maintained. And that’s true, but in this way the database schema is not modified and thus the Keystone Roles API can interact with SCIM Roles out-of-the-box.


## Access Control: Keypass

Keypass is a flexible tool to manage roles and permissions in the platform. It is a multi-tenant XACML server with PAP (Policy Administration Point) and PDP (Policy Detention Point) capabilities. Tenancy is defined by means of an HTTP header. The PDP endpoint will evaluate the Policies for the subjects contained in a XACML request. This is a design decision took by Keypass in order to simplify how the application is used.

You, as a developer, may wonder what a subject is, and why policies are grouped around them. To simplify, a subject is the same you put in a subject-id in an XACML request. You can then structure your user, groups and roles as usual in your preferred Identity Management system, just taking into account that those ids (subject, roles, groups) shall be used in your PEP when building the XACML request.Applying the policies per subject means that policies must be managed grouping them by subject. Keypass PAP API is designed to accomplish this.  

As XACML is an XML specification, Keypass API offers an XML Restful API.From the PAP REST point of view, the only resource is the Policy, which resides in a Subject of a Tenant. Both Tenant and Subject may be seen as namespaces, as they are not resources per se.

## Reference documentation and API

** ID system: Keystone & SCIM APIs **

[SCIM APIs Github](https://github.com/telefonicaid/fiware-keystone-scim)

[Keystone](http://developer.openstack.org/api-ref-identity-v3.html)

** Access Control: Keypass **

[Access Control Github](https://github.com/telefonicaid/fiware-keypass)


 
