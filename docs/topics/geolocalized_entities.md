# Entities geolocalization

The platform allows entities geolocalization. In other words, entities may have an attribute which 
value is interpreted as the entity location, among the following types:

- A point, e.g. to model a Car entity which moves across a city.
- A line, e.g. to model a Street entity
- A polygon with an arbitrary number of vertex, e.g. to model a city neighborhood
- An arbitrary [GeoJSON](http://geojson.org)

Entities location is managed through the [Data API](../data_api.md) exposed by [Context Broker](../context_broker.md). See details at "Geospacial properties of entities" section at the [NGSIv2 specification](http://telefonicaid.github.io/fiware-orion/api/v2/stable/).

Entities location can be exploited at different platform points. In particular:

- At the Data API, through geographical queries (both synchronous such as
  `GET /v2/entities` or asynchronous as part of a subscription filter). See "Geographical Queries" section at the [NGSIv2 specification](http://telefonicaid.github.io/fiware-orion/api/v2/stable/). 
- At the [CEP API](../cep_api.md). CEP is able to process entity point locations
  (other shapes as line, polygon, etc. not yet supported) so its latitude and
  longitude can be easily used in EPL conditions. See [CEP documentation](https://github.com/telefonicaid/perseo-fe/blob/master/documentation/plain_rules.md#location-fields) for more detail.
- Persistence backends:
  - CartoDB. Both point and arbitrary GeoJSON location are supported and correctly persisted
    at CartoDB. See [Cygnus documentation](http://fiware-cygnus.readthedocs.io/en/master/cygnus-ngsi/flume_extensions_catalogue/ngsi_cartodb_sink/index.html#section2.3.6) for more detail.
  - CKAN (column mode): attributes named `geojson` in case-insensitive way which value is
    a valid GeoJSON are correctly shown at CKAN visualization component. See [Cygnus documentation](http://fiware-cygnus.readthedocs.io/en/master/cygnus-ngsi/xxxx) for more detail. Note this is
    not the standard way of mark entity location in the Data API (see aforementioned "Geospacial properties of entities" section) and probably would be aligned in the
    future to work in a similar way to CartoDB persistence. 