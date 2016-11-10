# Performance considerations

There are several considerations to take into account when using the platform, as they may have a impact in performance. In particular:

- A large number of attributes per entity could impact performance. Typical use cases have tens of attributes
  per entity (e.g. 10-20). If your application uses a larger number (e.g. more than 100 attributes per entity)
  probably you should consider how to redesign your context model in order to avoid that.
- Note that the usage of patterns (regular expressions) and geolocation in queries (both synchronous such as
  `GET /v2/entities` or asynchronous as part of a subscription filter) cost much more than single queries. Thus,
  avoid its unnecessary usage. For example, `GET /v2/entities?idPattern=Room1` to get a single entity would work
  but it should be avoided in favor of `GET /v2/entities?id=Room1` as the later achieve the same result and it 
  is much cheaper in computational terms.
- Context entities should model things (either physical or logical) *existing* in your application domain (e.g. 
  buildings, cars, people, etc.) but they shouldn't be used to model *transient* states (such as alarms, 
  notifications, tickets, etc.). Although the Context Broker API supports this kind of usage, it may involve 
  several operational issues in the platform (if user doesn't manage her transient entities properly). More 
  information on this topic is discussed in [this document](modelling_considerations.md).