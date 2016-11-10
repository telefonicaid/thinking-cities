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
          "entity_name": "Waste Tank 8",
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

    POST /iot/d?k=801230BJKL23Y9090DSFL123HJK09H324HV8732&i=ws1956672
    Content-Type: text/plain

    l|22|w|78

will give raise to the following Context Broker entity:

      {
         "type": "Device",
         "isPattern": "false",
         "id": "WeatherStation1",
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
           },
           {
             "name": "reference",
             "type": "string",
             "value": "917834508965243"
           }
         ]
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

// @GERMAN: SOME INFORMATION ABOUT HOW TO GET STATISTICS CALCULATED BY THE STH WOULD BE NEEDED