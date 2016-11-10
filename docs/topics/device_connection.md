# How to connect my device to the platform

## Overview

Devices can be connected to the platform to send information about their sensors, and receive information and commands,
through the IoTAgent components. The particular way to connect your devices will depend on the device capabilities and
available protocols.

The current version of the platform supports two protocols: Ultralight 2.0 and JSON. This document will show how to
connect a single device to the platform to send a piece of data, and to receive some data from the platform.

Connecting a device to the Platform will make a single Context Entity with all the information about the device available
in the [Context Broker](../context_broker.md). Interactions with the devices are always channeled through operations over this entity.

More informacion about the device APIs can be found in the [Device API section](../device_api.md). You can find links
to each particular protocol specification and more usefule information in the [Device Gateway section](../device_gateway.md).

Along this example we will assume that we are designing a SmartCity application, that will use several different devices
for its Gardening services. To this extent, we will assume that a service called `smartown` and a subservice called
`/gardens` have been created. All the requests to the platform will include the appropriate ´Fiware-service´ and
`Fiware-servicepath` headers, as explained [here](../multitenancy.md).

## Sending a measure to the Platform

### Configuring the APIKey and protocol

In order for the devices to send information for the subservice, a Configuration Group must be created. This configuration
group will specify which protocol the devices will be using, and the APIKey that will be used to identify that the devices
belong to that particular group.

The following request will create the required group for our subservice:

    POST /iot/services
    Content-Type: application/json
    Fiware-service: smartown
    Fiware-servicepath: /gardens

    {
      "services": [
        {
          "protocol": [
                  "IoTA-UL"
                ],
          "apikey": "801230BJKL23Y9090DSFL123HJK09H324HV8732",
          "entity_type": "Device"
        }
      ]
    }

This request specifies that the default type for the Context Entities representing our devices will be "Device", and
that the protocol we will use for our devices will be Ultralight 2.0.

### Provisioning the device

The next step is provisioning the device in the platform. In this example, we will provision a single device: a Weather
Station having two sensors, a humidity sensor and a thermometer. We can also use this provisioning to add some static
information we know for our entity, that need not be reported by the device itself, but we want appearing in the
entity itself. As en example, we will provision statically the Reference of the Weather Station and its location.

To provision the device in the platfor we issue the following request to the IoTAgent:

    POST /iot/devices
    Content-Type: application/json
    Fiware-service: smartown
    Fiware-servicepath: /gardens

    {
      "devices": [
        {
          "device_id": "ws1956672",
          "protocol": "IoTA-UL",
          "entity_name": "WeatherStation1",
          "entity_type": "Device",
          "attributes": [
            {
              "object_id": "t",
              "name": "temperature",
              "type": "degrees"
            },
            {
              "object_id": "h",
              "name": "humidity",
              "type": "percentage"
            }
          ],
          "commands": [
            {
              "name": "configuration",
              "type": "command"
            }
          ],
          "static_attributes": [
            {
              "name": "location",
              "type": "geo:point",
              "value": "40.392, -3.759"
            },
            {
              "name": "reference",
              "type": "string",
              "value": "917834508965243"
            }
          ]
        }
      ]
    }

This will provision our `ws1` workstation in the system, making it ready to receive measures.

### Sending the measure

In order to send measures to the platform, the device has to fulfill the following requirements:

* Connectivity to the South Bound of the platform (i.e.: the ports used to communicate physical devices with the Platform).
* Either an MQTT or an HTTP client (as those are the transport protocols currently supported).

Being the most common, we will assume that your constraint device have an HTTP client, that can send requests to the
platform. This interaction with the Platform can be implemented with whichever programming language your device supports.
At the present moment, no SDKs are provided for south bound interactions.

In this case, a typical measure report from our Weather Station will consist on a request like the following
one:

    POST /iot/d?k=801230BJKL23Y9090DSFL123HJK09H324HV8732&i=ws1956672
    Content-Type: text/plain

    t|22|h|78

You should notice several things in this request:

* First of all, the path is '/iot/d'. The notification path will depend on the protocol used. This path is the one assigned
to the Ultralight 2.0 protocol

* Also, the query parameters indicate the API Key defined in the first section, and the device ID defined in the previous
one.

* The payload of the request contains the measures, sepparated by bars. The names of the attributes used to report the
measure are the ones defined in the `object_id` attributes of the device provisioning.

* The `Content-Type` header is mandatory and **must** have the value `text/plain`.

If everything is OK, the system will answer with a 200 OK code, and an empty response.

This measure will give rise to a Context Entity like the following one in the Context Broker:

      {
         "type": "Device",
         "isPattern": "false",
         "id": "WeatherStation1",
         "attributes": [
           {
             "name": "temperature",
             "type": "degrees",
             "value": "22"
           },
           {
             "name": "humidity",
             "type": "percentage",
             "value": "78"
           },
           {
             "name": "location",
             "type": "geo:point",
             "value": "40.392, -3.759"
           },
           {
             "name": "reference",
             "type": "string",
             "value": "917834508965243"
           }
         ]
      }

### Sending a command

The Platform gives also the opportunity for the devices to receive commands or information from the platform. To illustrate
this point, we will show how to send some configuration information to a device, using polling commands, i.e.: commands
that do not require the device to be online to be receieved, but that are retrieved when the device connects to the platform.

Commands are also defined in the device provisioning. It can be seen in the `commands` section of the provisioning payload
in the provisioning section above.

Information for the command execution is sent using NGSI queries to the Context Broker, as if we were going to update
directly the Context Entity that represents the device. As an example, we will send the `openUmbrella=false` configuration
order with the following request:

    PUT /v2/entities
    Content-Type: application/json
    Fiware-service: smartown
    Fiware-servicepath: /gardens

    {
      "id": "WeatherStation1",
      "type": "Device",
      "configuration": {
        "type" : "command",
        "value" : "openUmbrella: false"
      }
    }

This request will store the command `configuration` with value `openUmbrella=false` waiting for the device to retrieve it.

A device can use the query parameter `getCmd=1` to retrieve all the pending commands from the system whenever it is sending
a new measure, as in the following example:

    POST /iot/d?k=801230BJKL23Y9090DSFL123HJK09H324HV8732&i=ws1956672&getCmd=1
    Content-Type: text/plain

    t|27|h|89

Contrary to the previous measurement reports, this one will not be answered with an empty payload, but with a list of all
the pending commands, as in the following result:

    200 OK

    ws1956672@configuration|openUmbrella=false

Here we can see the configuration value we sent to the Context Broker in the first place. As you can see, the values
include the Device ID provisioned before, for verification purposes.

Take into account that polling commands (as the ones depicted in this document) have an expiration time, and so, if the
device don't retrieve the available commands in a reasonable amount of time, they will be discarded. Expiration time of
the commands is defined Platform-wide (and currently set to 1 day).

To keep track of the state of commands sent to a device, the IoTAgents create a set of additional attributes in the device
entity that let the user check the current status of the command, and the result of the command if there is any. The
Context Entity with the additional attributes will be like the following:

      {
         "type": "Device",
         "isPattern": "false",
         "id": "WeatherStation1",
         "attributes": [
           {
             "name": "temperature",
             "type": "degrees",
             "value": "22"
           },
           {
             "name": "humidity",
             "type": "percentage",
             "value": "78"
           },
           {
             "name": "location",
             "type": "geo:point",
             "value": "40.392, -3.759"
           },
           {
             "name": "reference",
             "type": "string",
             "value": "917834508965243"
           },
           {
             "name": "configuration_status",
             "type": "commandStatus",
             "value": "PENDING"
           },
           {
             "name": "configuration_info",
             "type": "commandResult",
             "value": " "
           }
         ]
      }

The additional attributes have the same name as the original command with a `_status` suffix (for the stauts attribute)
and a `_info` suffix (for the results). The possible status values for a command are: ERROR, EXPIRED, PENDING and OK.
