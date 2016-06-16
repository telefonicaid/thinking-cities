# Multitenancy

The platform implements multitenancy based on the *Service* and *SubService* concepts. Service corresponds with the top-level grouping, typically associated to tenants. Within each service, a given tenant can set several subservices. For example, tenants may represent cities (e.g. Madrid, Paris, London, etc.) and subservices within a tenant the different vertical areas in that city (lightning, transport, health, etc.).

Authentication and authorization is based on service and subservices. In other words, user/roles access are per service and subservice. For example, you can define that a given user has read permissions in all subservices of a given service, but only has modification permissions in a subset of such subservices.

Service and subservice are supported in platform APIs. In particular, service and subservice are specified by the `Fiware-Service` and `Fiware-ServicePath` HTTP header respectively. Using these headers a given request (e.g. an entity creation request using the [Data API](data_api.md)) can be scoped in a particular service and subservice.

The following syntax rules apply to service:

* It must be a string of alphanumeric characters (and the `_` symbol)
* Maximum length is 50 characters
* It is interpreted in lowercase

The following syntax rules apply to subservice:

* It must start with '/' symbol
* It must be a string of alphanumeric characters (and the `_` symbol), except the initial `/`.
* It is interpreted in case-sensitive way

