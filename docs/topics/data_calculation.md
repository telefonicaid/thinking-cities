# How to calculate data values

Other topics in this same document have addressed how to insert data in the Platform and how to query and modify that
data. But, what is the best way to make automatic calculations based on those data values? There are different ways of
calculating data based on its origin and on the nature of the calculation.

## Calculations based on data coming from the devices

For those pieces of data coming from measurement reports from the Devices, the [IoTAgents](../device_gateway.md) offer mechanisms to make calculations
based on the reported data, through the Expression Language. When a Device is provisioned in the platform, along with the
definition of the values that are going to be directly reported to the system, expressions can be defined combining those
values sent by the device.

The following example shows the provisioning of a Tank entity, representing a liquid waste container. The tank entity
have sensors in the structure that report the total weight of the tank, another one that measures the filling level and
the dimensions of the square base of the tank are known, and equal to 2m by 4m. An expression is provisioned to calculate
the density of the liquid waste currently stored.

    POST /iot/devices
    Content-Type: application/json
    Fiware-service: smartown
    Fiware-servicepath: /wastecontainers

    {
      "devices": [
        {
          "device_id": "t5867934",
          "protocol": "IoTA-UL",
          "entity_name": "WasteTank8",
          "entity_type": "Device",
          "attributes": [
            {
              "object_id": "l",
              "name": "level",
              "type": "meter"
            },
            {
              "object_id": "w",
              "name": "weight",
              "type": "kilogram"
            },
            {
              "name": "density",
              "type": "kilogram/meter^3",
              "expression": "${@weight / (@level * 2 * 4)}"
            }
          ]
        }
      ]
    }

A measure report coming from the container like the following:

    POST /iot/d?k=801230BJKL23Y9090DSFL123HJK09H324HV8732&i=t5867934
    Content-Type: text/plain

    l|22|w|78

will give raise to the following Context Broker entity (NGSIv1)

      {
         "type": "Device",
         "isPattern": "false",
         "id": "WasteTank8",
         "attributes": [
           {
             "name": "level",
             "type": "meter",
             "value": "1"
           },
           {
             "name": "weight",
             "type": "kilogram",
             "value": "24"
           },
           {
             "name": "density",
             "type": "kilogram/meter^3",
             "value": "3"
           }
         ]
      }

or the same entity in NGSIv2 format:

      {
         "type": "Device",
         "id": "WasteTank8",
         "level": {
             "type": "meter",
             "value": "1"
         },
         "weight": {
             "type": "kilogram",
             "value": "24"
         },
         "density": {
             "type": "kilogram/meter^3",
             "value": "3"
         }
      }

To see the complete set of operations and features available for the Expression Language, refer to
the [specification](https://github.com/telefonicaid/iotagent-node-lib/blob/master/doc/expressionLanguage.md).

## Calculations based on data in Context Entities

For those data values that doesn't come from devices, but from external systems, and for those cases when the sources of
context information to calculate those values are multiple, the [CEP](../cep.md) can be used to make some of those calculations.

The CEP gets a notification cointaining a configurable set of attributes (as set in the subscription) and that allows using more fields than the ones sent by device. The notification mechanism allows to send the previous value of an attribute as metadata `previousValue` (see [metadata in notifications](http://fiware-orion.readthedocs.io/en/master/user/metadata/index.html#metadata-in-notifications)). So, as an example, we can think of an entity that periodically sends its coordinates and the time of the measure. We could calculate its average velocity by means of a rule that updates its field `velocity`.

For simplicity, the previous values are taken as values in the incomming notification, but they could be taken as `ev.x__metadata__previousValue` instead of `x0`, and the same for `y0` and `t0` (see [Metadata and object values](https://github.com/telefonicaid/perseo-fe/blob/master/documentation/plain_rules.md#metadata-and-object-values))

The rule could be 
```
{
    "name": "rule_velocity",
    "text": "select *, \"rule_velocity\" as ruleName, Math.hypot(cast(cast(ev.x1?,String),float)-cast(cast(ev.x0?,String),float),cast(cast(ev.y1?,String),float)-cast(cast(ev.y0?,String),float))/(cast(cast(ev.t1?, String), float)-cast(cast(ev.t0?,String), float)) as velocity  from pattern [every ev=iotEvent()]",
    "action": {
        "type": "update",
        "parameters": {
            "attributes": [
                {
                    "name": "velocity",
                    "value": "${velocity}",
                    "type": "Number"
                }
            ]
        }
    }
}
```

And a notification like
```
{
    "subscriptionId": "51c04a21d714fb3b37d7d5a7",
    "originator": "localhost",
    "contextResponses": [
        {
            "contextElement": {
                "attributes": [
                    {
                        "name": "x1",
                        "type": "number",
                        "value": "10"
                    },
                    {
                        "name": "y1",
                        "type": "number",
                        "value": "0"
                    },
                    {
                        "name": "x0",
                        "type": "number",
                        "value": "0"
                    },
                    {
                        "name": "y0",
                        "type": "number",
                        "value": "0"
                    },
                    {
                        "name": "t1",
                        "type": "number",
                        "value": "5"
                    },
                    {
                        "name": "t0",
                        "type": "number",
                        "value": "0"
                    }
                ],
                "type": "BloodMeter",
                "isPattern": "false",
                "id": "bloodm1"
            },
            "statusCode": {
                "code": "200",
                "reasonPhrase": "OK"
            }
        }
    ]
}
```

would generate an update action

```
{  
   "contextElements": [  
      {  
         "isPattern": "false",
         "id": "bloodm1",
         "attributes": [  
            {  
               "name": "velocity",
               "value": "2",
               "type": "Number"
            }
         ],
         "type": "BloodMeter"
      }
   ],
   "updateAction": "APPEND"
}
```

that woud set the field `velocity` to `2` (10/5) in whatever magnitudes we were using

All the functions in [java.lang.Math](https://docs.oracle.com/javase/7/docs/api/java/lang/Math.html) can be used in the EPL expression.


## Statistical calculations based on historic values

For those cases when the data values that want to be calculated are statistical information based on historic values,
the [Short Term Historic](../sth.md) can be used. This component stores historical information of Context Entities and can be used
to retrieve statistical calculations over the stored data.

The STH component is able to calculate statistics about the evolution in time of certain entity attributes. To do it, there are 2 ways to notify this evolution in time to the component:

1. Via the [Cygnus](https://github.com/telefonicaid/fiware-cygnus) component and subscribing it to the Context Broker instance.
2. Via the [STH](https://github.com/telefonicaid/fiware-sth-comet) component itself and directly subscribing it to the Context Broker instance.

Once the STH receives the notifications of entity attribute value changes, it is able to calculate and provide information about:

* Numeric attribute values:
    * Mean
    * Standard deviation
    * Variance
    * Maximum
    * Minimum
* Textual attribute values:
    * Number of occurrences

All this, for distinct resolutions or time frames, such as:

* Months
* Days
* Hours
* Minutes
* Seconds

Regarding the previous example, the STH is able to provide statistical information such as:

1. Which has been the maximum `weight` (attribute) values of the `WasteTank8` (entity) last year with a resolution of months?, sending a GET request to the STH component such as the next one:
```
http://<sth-component>:<sth-port>/STH/v1/contextEntities/type/Entity/id/WasteTank8/attributes/weight?aggrMethod=max&aggrPeriod=month&dateFrom=2015-01-01T00:00:00&dateTo=2015-12-31T23:59:59
```

2. Which has been the mean `level` (attribute) values of the `WasteTank8` (entity) last week with a resolution of hours?, sending a GET request to the STH component such as the next one:
```
http://<sth-component>:<sth-port>/STH/v1/contextEntities/type/Entity/id/WasteTank8/attributes/level?aggrMethod=sum&aggrPeriod=hour&dateFrom=2016-11-07T00:00:00&dateTo=2016-11-13T23:59:59
```

3. Which has been the standard deviation of the `density` (attribute) values of the `WasteTank8` the last 3 days with a resolution of hours or even minutes?, sending a GET request to the STH component such as the next one:
```
http://<sth-component>:<sth-port>/STH/v1/contextEntities/type/Entity/id/WasteTank8/attributes/density?aggrMethod=sum2&aggrPeriod=minute&dateFrom=2016-11-14T00:00:00&dateTo=2016-11-16T23:59:59
```

For futher information about the STH component and all the capabilities it provides, please visit the [STH documentation at ReadTheDocs](http://fiware-sth-comet.readthedocs.io/en/latest/index.html).
