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

// @CARLOS: I GUESS THIS IS CORRECT, BUT I'M NOT COMPLETELY SURE. IF IT IS, SOME EXAMPLES WOULD BE APPRECIATED

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

1. Which has been the maximum `weight` (attribute) values of the `WasteTank8` (entity) last year with a resolution of months?, sending a GET request to the STH component such as the next one: `http://<sth-component>:<sth-port>/STH/v1/contextEntities/type/Entity/id/WasteTank8/attributes/weight?aggrMethod=max&aggrPeriod=month&dateFrom=2015-01-01T00:00:00&dateTo=2015-12-31T23:59:59`
2. Which has been the mean `level` (attribute) values of the `WasteTank8` (entity) last week with a resolution of hours?, sending a GET request to the STH component such as the next one: `http://<sth-component>:<sth-port>/STH/v1/contextEntities/type/Entity/id/WasteTank8/attributes/level?aggrMethod=sum&aggrPeriod=hour&dateFrom=2016-11-07T00:00:00&dateTo=2016-11-13T23:59:59`
3. Which has been the standard deviation of the `density` (attribute) values of the `WasteTank8` the last 3 days with a resolution of hours or even minutes?, sending a GET request to the STH component such as the next one: `http://<sth-component>:<sth-port>/STH/v1/contextEntities/type/Entity/id/WasteTank8/attributes/density?aggrMethod=sum2&aggrPeriod=minute&dateFrom=2016-11-14T00:00:00&dateTo=2016-11-16T23:59:59`

As already mentioned, for futher information, please visit the [STH documentation at ReadTheDocs](http://fiware-sth-comet.readthedocs.io/en/latest/index.html).
