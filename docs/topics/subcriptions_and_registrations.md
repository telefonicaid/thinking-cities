# Subscriptions and registrations

The [Data API](../data_api.md) exposed by [Context Broker](../context_broker.md) implements both *subscriptions*
and *registrations*. They could look similar as they have common fields (id, entities, attributes, duration, URL,
etc.) and common dynamics (both involve the Context Broker sending outgoing requests) but actually are 
pretty different and address different purposes.

On the one hand, subscriptions:

    POST /v2/subscriptions
    Content-Type: application/json
    Fiware-service: smartown
    Fiware-servicepath: /roads

    {
      "subject": {
        "entities": [
          {
            "idPattern": ".*",
            "type": "Car"
          }
        ],
        "condition": {
            "attrs": [ "speed" ]
        }
      },
      "notification": {
        "http": {
            "url": "http://example.com/receiver"
        }
      }
    }


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

    POST /v2/registrations
    Content-Type: application/json
    Fiware-service: smartown
    Fiware-servicepath: /roads

    {    
      "dataProvided": {
        "entities": [
          {
            "id": "FBH4452",
            "type": "Car"
          }
        ],
        "attrs": [
          "speed"       
        ]
      },
      "provider": {
        "http": {
          "url": "http://myproviders.com/Cars"
        },
        "legacyForwarding": true
      }
    }

- Are used to configure context sources (also known as *context providers*). Details can be found in 
  [the Context Broker documentation](http://fiware-orion.readthedocs.io/en/master/user/context_providers/index.html).
- The URL associated to a registration is used to forward queries or updates to context providers covered by the 
  registration.
- In the case of query forwarding, the context provider will send the query result to Context Broker
  as part of the query response. Thus, there is an information flow from context providers to Context Broker.
 
