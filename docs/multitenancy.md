# Multitenancy

The platform implements multitenancy based on the concepts *Service* and *SubService*. Service corresponds to the top-level grouping, typically associated to tenants. Within each service, a given tenant can set several subservices. For example, tenants may represent cities (e.g. Madrid, Paris, London, etc.) and subservices within a tenant may represent the different vertical areas in that city (lightning, transport, health, etc.).

Authentication and authorization is based on service and subservices. In other words, user/roles access are per service and subservice. For example, you can define that a given user has read permissions in all subservices of a given service, but only modification permissions in a subset of such subservices.

Service and subservice are supported in platform APIs. In particular, service and subservice are specified by the `Fiware-Service` and `Fiware-ServicePath` HTTP header respectively. Using these headers, a given request (e.g. an entity creation request using the [Data API](data_api.md)) can be scoped in a particular service and subservice, e.g:

    POST /v2/entities
    Content-Type: application/json
    Fiware-service: smartown
    Fiware-servicepath: /roads

    {
      "id": "BCZ6754",
      "type": "Car",
      "speed": {
         "value": 54,
         "type": "Number"
      }
    }

The following syntax rules apply to service:

* Only alphanumeric characters and underscores (`_`) are allowed.
* Maximum length is 50 characters.
* Minimum length is 1 character.
* If uppercase characters are used, they are converted to lowercase.

The following syntax rules apply to subservice:

* It uses the following syntax: `/<token1>/<token2>/.../<tokenN>` (regular subservices) or `#<token1>/<token2>/.../<tokenN>` (grants). The most common case is `/<token1>`.
* Maximum length is 128 characters.
* Minimum length in each token is 1 character.
* Only alphanumeric characters and underscores (`_`) are allowed in each token.
* It is interpreted in a case-insensitive way considering the platform end-to-end
