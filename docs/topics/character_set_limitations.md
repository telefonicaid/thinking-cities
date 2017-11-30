# Character set limitations

There are several charset and naming limitations to take into account when using the platform. In particular:

- General restrictions in the [Data API](../data_api.md) exposed by [Context Broker](../context_broker.md)
  due to security reasons. The details are provided in the [Context Broker documentation](https://fiware-orion.readthedocs.io/en/1.9.0/user/forbidden_characters/index.html) itself.
- Apart from the general restrictions above, there are some additional ones that apply to ID fields such as 
  the entity id or the attribute name. They are described in the section "Field syntax restrictions"
  at the [NGSIv2 specification](http://telefonicaid.github.io/fiware-orion/api/v2/stable/).
- Service and subservices (also named *service path* in some cases) restrictions, which are described in 
  [this document](../multitenancy.md).
- The usage of persistence backends (HDFS, MySQL, CKAN, etc.) and [STH](../sth.md) could impose additional
  limitations. Please, have a look to [the data model persistence naming conventions]( ../naming_conventions.md) for more detail.