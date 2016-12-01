# Subscriptions and registrations

The [Data API](../data_api.md) exposed by [Context Broker](../context_broker.md) implements both *subscriptions*
and *registrations*. They could look similar as they have common fields (id, entities, attributes, duration, URL,
etc.) and common dynamics (both involve the Context Broker sending outgoing requests) but actually are 
pretty different and address different purposes.

On the one hand, subscriptions:

- Are used to configure notifications to be triggered when some conditions occur. See 
  [this document](how_notifications_work.md) for an introduction on this functionality.
- The URL associated to a subscription identifies a notification receiver, to which notifications covered
  by the subscription will be sent.
- They involve an unidirectional communication from Context Broker to the receiver. In other words,
  the receiver doesn't send any information back to the Context Broker (beyond the result of the notification
  delivery itself at HTTP level).
- Subscription management is part of the 
  [NGSIv2 specification](http://telefonicaid.github.io/fiware-orion/api/v2/stable/).

On the other hand, registrations:

- Are used to configure context sources (also known as *context providers*). Details can be found in 
  [the Context Broker documentation](http://fiware-orion.readthedocs.io/en/1.4.0/user/context_providers/index.html).
- The URL associated to a registration is used to forward queries or updates to context providers covered by the 
  registration.
- In the case of query forwarding, the context provider will send the query result to Context Broker
  as part of the query response. Thus, there is an information flow from context providers to Context Broker.
- By the time being at IoT platform v4.1, registrations management is not yet part of the NGSIv2, so the old 
  NGSIv1 API has to be used. However, the query/udpates forwarded as result of the registrations can use 
  NGSIv2 without problems. More details about this coexistence [here](http://fiware-orion.readthedocs.io/en/1.4.0/user/v1_v2_coexistence/index.html#ngsiv2-query-update-forwarding-to-context-providers).
 