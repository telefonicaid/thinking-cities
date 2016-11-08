# How notifications work

The platform is able to send notifications, based in a subscription mechanism implemented by 
[Context Broker](../context_broker.md). Let's describe it step by step.

## Device provisioning at IoT Agent

Let's consider a fleet of several cars that are moving around a given city (Madrid, for example). Each car has an 
onboard sensor, all them managed by platform [IoT Agents](../device_gateway.md), assuming network connectivity 
between the sensor and the platform. First, the sensor has to be provision.... (Dani please complete this part and
the "linking" with next section).

## Subscriptions and notifications at Context Broker 

Now that the car speed information is periodically send to Context Broker, let's see how to configure notifications. 
The simplest notification trigger is based on entity attribute changes. For example, in order to get
notifications each time the `speed` attribute changes in any entity with type `Car` the following
subscription would be used. Notifications will be send to the (sample) endpoint `http://example.com/receiver`.

    POST /v2/subscriptions
    Content-Type: application/json

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

Subscriptions are first class objects managed by Context Broker, as entities are. Thus, they can be modified 
after creation, managed (e.g. paused/resumed, etc.) and eventually deleted. 

By default the notification includes all the attributes in the entity, i.e. not only `speed` but any other
attribute that a Car may have. If you want to receive only a subset of attributes, you can configure it
as part of the `notification` field:

    ...
    "notification": {
      "http": {
          "url": "http://example.com/receiver"
      },
      "attrs": [ "speed" ]
    }
    ...

Moreover, you can include filters associated to the subscription. For example, in order to get notification not
only each time that `speed` changes but also requiring that the speed is greater than 50 km/h and the Car 
is located in the center of Madrid city (in particular, 15 km closer to the city center):

    ...
    "conditions": {
      "attrs": [ "speed" ],
      "expression": {
        "q": "speed>50",
        "georel": "near;maxDistance:15000",
        "geometry": "point",
        "coords": "40.418889,-3.691944"        
      }
    }
    ...
         
By default, notifications received by `http://example.com/receiver` used a "fixed" format, in the following way:

    POST http://example.com/receiver
    Content-Type: application/json
    
    {
      "subscriptionId": "...",   
      "data": [ 
        { 
          "id": "BCZ6754", 
          "type": "Car",   
          "speed": { 
            "value": 57, 
            "type": "Number", 
            "metadata": {} 
          }
        } 
      ]
    }


You can use the `attrsFormat` field in order to select more compact formats (e.g. including just the values of the
attributes, without type or metadata) or, even better, customize completely all the notification parameters.
Customization is done using the `httpCustom` field (instead of `http`) and supports macro substitution. 

For example, you could send text alarms in the following way:

    ...
    "notification": {
      "httpCustom": {
          "url": "http://example.com/receiver",
          "headers": { 
            "Content-Type": "text/plain" 
          },
          "qs": {
            "format": "text"
          },
          "payload": "Car ${id} speed is ${speed} km/h. Be careful you could be fined!" 
      },
      "attrs": [ "speed" ]
    }
    ...


resulting in notifications like this one:

    POST http://example.com/receiver?format=text
    Content-Type: text/plain

    Car BCZ6754 speed is 57 km/h. Be careful you could be fined!

This is just a brief introduction to the overall subscriptions/notifications functionality. You can find
the full details and more examples at the [Context Broker documentation](https://fiware-orion.readthedocs.io/en/master/user/walkthrough_apiv2/index.html#subscriptions) and the "Subscriptions" section at the
[NGSIv2 specification](http://telefonicaid.github.io/fiware-orion/api/v2/stable/).

## The `TimeInstant` metadata

As in IoT Platform version v4.1, the default behavior for Context Broker is to send notifications only
when the attribute has actually changed. Thus, if the Car `speed` is 67 and Context Broker receives
an update with the value 67 then it *doesn't* send a notification.

For those use cases that needs notifying each update (no matter if it results on actual value change or not), the
IoTAgent implements a clever mechanism based on adding a `TimeInstant` metadata with the timestamp of the update. 
This way, it is ensured that from a logical point of view the attribute always changes, as a change in any metadata 
is considered a change in the attribute. Look for the `TimeInstant` element documentation in the [IoT Agent documentation](https://github.com/telefonicaid/iotagent-node-lib#the-timeinstant-element).

In the case of client-provided entities (i.e. entities not managed by IoTAgent but by your application) you may need a 
similar mechanism. In this case, ensure that the `TimeInstant` metadata resolution is higher enough, e.g. if your system 
is able to notify several times per second, then the timestamp resolution should be higher than one second.