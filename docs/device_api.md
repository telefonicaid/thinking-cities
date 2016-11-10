
In order to send information from the devices to the platform, you can use the IoT Agents. This components
map South-Bound protocol requests coming from the device to NGSI requests to a Context Broker, that will help you
mapping your device data to an NGSI Entity and attributes.

The Device API allows you to:

- Register your device to reduce the message footprint and use commands.
- Send data from the device to the FIWARE IoT Stack
- Send commands from your application to the device

If you want to quickly connect or simulate virtual devices you may also check FIGWAY, a set of simple python scripts
working as a client SDK for any desktop PC, laptop or gateway supporting a python2.7 environment. This way you may skip
the steps described below and use the python commands as described in the README.md file available at this [Github
repository](https://github.com/telefonicaid/fiware-figway).

# Configure the South Bound protocol

In order for the South Bound protocols (i.e.: the protocols used to communicate physical devices with the Platform)
to work for your services, you must provision the information about your devices, either by provisioning the device itself,
by provisioning a Configuration Group (for each subservice) or both. Configuration Groups (also called Services; do not
mistake them for the multitenancy Service concept), define some default values for the South Bound protocol to NGSI mapping,
that will be applied to every device associated to the group. Devices will be associated to groups based on the API Key
provided by the Device in the communications.

Configuration Groups can be provisioned through the API. If you are not the administrator
of your subservices, you may have been given an API Key. If that's the case, there is no need to configure the
South Bound protocol again. Use the provided data for future interactions.

The following excerpt shows you how to provision a Configuration Group directly to the API. This request must be sent
to the `/iot/services` path in an HTTP POST:
```
{
  "services": [
    {
      "protocol": [
              "IoTA-UL"
            ],
      "apikey": "801230BJKL23Y9090DSFL123HJK09H324HV8732",
      "entity_type": "SensorMachine",
      "commands": [
        {
          "name": "wheel1",
          "type": "Wheel"
        }
      ],
      "lazy": [
        {
          "name": "luminescence",
          "type": "Lumens"
        }
      ],
      "attributes": [
        {
          "name": "status",
          "type": "Boolean"
        }
      ],
      "static_attributes": [
        {
          "name": "bootstrapServer",
          "type": "Address",
          "value": "127.0.0.1"
        }
      ]
    }
  ]
}
```
This request shows the provisioning of an Ultralight 2.0 Configuration group, indicating the API Key, the `entity_type`
that will be associated with all the devices in the group and common attributes all devices in the group will share,
along with the information for its mapping to the NGSI entity.

Currently, the IoT Platform only allows for the existence of a configuration group per subservice for each protocol.

# Register your IoT device 

Remember this step is optional, it is only required if you want to use commands in order to act upon devices or want to
define a mapping to reduce the attributes identifier when you send observations to reduce the message size. But, in case
the device is not provisioned, the Configuration Group **must** be configured.

If you simply want to send observations you can skip this and just go to the "Send observations" section.

**Registering for UL2.0**

On this sample a device is registered to send temperature observations using UL2.0 protocol and a PING command:

```
POST $HOST_IOTAGENT/iot/devices

Headers:
{
	"content-type": "application/json",
	"X - Auth - Token": "[TOKEN]",
	"Fiware - Service": "OpenIoT",
	"Fiware-ServicePath: "/"
}
Payload:
{
	"devices": [{
		"device_id": "[DEV_ID]",
		"entity_name": "[ENTITY_ID]",
		"entity_type": "thing",
		"protocol": "IoTA-UL",
		"timezone": "Europe/Madrid",
		"endpoint": "http://[DEVICE_IP]:[PORT]",
		"attributes": [{
			"object_id": "t",
			"name": "temperature",
			"type": "int"
		}],
		"commands": [{
			"name": "ping",
			"type": "command",
			"value": "[Dev_ID]@ping|%s"
		}],
		"static_attributes": [{
			"name": "att_name",
			"type": "string",
			"value": "value"
		}]
	}]
}
```

Description of the parameters (mandatory parameters are marked as such, the rest remain optional):

- *device_id*: the device identifier (mandatory).
- *entity_name*: the entity ID to be used at the ContextBroker.
- *entity_type: type of the entity that will represent the device in the Context Broker.
- *protocol*: South-Bound protocol the device will be using to communicate with the Platform (mandatory).
- *timezone*: timezone for the device.
- *endpoint*: for devices accepting HTTP commands, address of the device where the commands will be sent.
- *attributes*: Used to map UL2.0 attributes to ContextBroker attributes in the entity representing the device.
- *commands*: Used to indicate which commands the device supports. For HTTP attributes, the "endpoint" attribute will
be required.
- *static_attributes*: the contents of this attribute will be sent in every observation as attributes of the entity.

**Registering for MQTT**

The following example shows the same registration for an MQTT device instead of HTTP:

```
POST $HOST_IOTAGENT/iot/devices

Headers:
{
	"content-type": "application/json",
	"X-Auth-Token": "[TOKEN]",
	"Fiware-Service": "OpenIoT",
	"Fiware - ServicePath ": " / "
}
Payload:
{
	"devices": [{
		"device_id": "[DEV_ID]",
		"entity_name": "[ENTITY_ID]",
		"entity_type": "thing",
		"protocol": "IoTA-UL",
		"timezone": "Europe/Madrid",
		"attributes": [{
			"object_id": "t",
			"name": "temperature",
			"type": "int"
		}],
		"commands": [{
			"name": "ping",
			"type": "command",
			"value": "[Dev_ID]@ping|%s"
		}],
		"static_attributes": [{
			"name": "att_name",
			"type": "string",
			"value": "value"
		}]
	}]
}
```

The example shows that the only difference between both provisionings is the presence or absence of an "endpoint" attribute.
For devices that won't be receiving commands, the later provisioning request will be valid for both transport protocols.

# Send observations 

There are two IoTAgents currently available in the platform, each listening for requests with a different protocol:
Ultralight 2.0 and JSON. In both cases, payloads can be sent to the Agent using two different transport protocols: MQTT
and HTTP. The following sections show some examples of each of the four possible approaches.

**Send measures using UL2.0 HTTP**

Ultralight2.0 (UL2.0) is a proposed simplification of the SensorML (SML) standard – and will be used to send device
measurements (observations) to the ContextBroker. Ultralight2.0 is selected in this example because of its simplicity.

Sending an observation from IoT devices is simple with the following HTTP POST request:

```
POST  $HOST_IOTAGENT/iot/d?k=<apikey>&i=<device_ID>
Headers:
{
	"content-type": "text/plain"
}
Payload: ‘t|25‘
```

The previous example sends an update of the Temperature attribute that is automatically sent by the IoT Agent to the
corresponding entity at the ContextBroker.

Multiple measures for a single observation can be sent, sepparating the values with pipes:
```
POST  $HOST_IOTAGENT/iot/d?k=<apikey>&i=<device_ID>
Headers:
{
	"content-type": "text/plain"
}
Payload: ‘t|25|h|42|l|1299‘
```
This request will generate a single update request to the Context Broker with three attributes, one corresponding to
each measure.

Sending multiple observations in the same message is also possible with the following payload:
```
POST  $HOST_IOTAGENT/iot/d?k=<apikey>&i=<device_ID>
Headers:
{
	"content-type": "text/plain"
}
Payload: ‘t|23#h|80#l|95#m|Quiet‘
```
This request will generate four requests to the Context Broker, each one reporting a different value.

Finally, after connecting your IoT devices this way you (or any other developer with the right access permissions) should be able to use the Data API to read the NGSI entity assigned to your device or see the data on the Management Portal.


**Send measures using UL2.0 MQTT**

Devices (once provisioned under a service) can publish MQTT messages to the IoTAgent. Those messages contain one piece
of information each. That means that one message will be translated into one single entity on the ContexBroker domain.
The information can be typically sensors measures.

This is the topic hierarchy that has to be used by devices:
```
/<api-key>/<device-id>/attrs/<attrName>
```

Where:

- "api-key": this is a unique value per service. It is provided through the provisioning API.
- "device-id": this is typically a sensor id, it has to be unique per “api-key”.
- "attrName": name of the magnitude being measured, for example: temperature, pressure, etc… this is the name of
the attribute being published on ContextBroker.

Example:

```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -u theUser -P thePassword -t /<api_key>/mydevicemqtt/t -m 44.4
```

As it can be noticed in this example, the MQTT broker uses a set of credentials to authenticate users. Please, if you
don't know your credentials, please ask the support team to provide you with a new set.

Another scenario can happen when devices send more than one phenomena within the payload. That is to say, one single
MQTT message carries all measures. When it comes to ContextBroker, there will be one entity publication (per device)
but containing all different attributes as per measures included in the mqtt message (each phenomenon or measure will
be a separate attribute). In order to be able to parse the information on the IoTAgent, devices should follow the
same Ultralight 2.0 format as in the HTTP case.

Topic:

```
<api-key>/<device-id>/attrs
```

Example:

```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -u theUser -P thePassword -t /<api_key>/mydevicemqtt/attrs -m "t|5.4#o|4.3#n|3.2#c|2.1"
```

**Send measures using JSON HTTP**

The simple JSON protocol used by the JSON IoTAgent maps each measurement to an attribute in a JSON Object. The following
example shows how to send a measurement of three different quantities:
```
POST  $HOST_IOTAGENT/iot/json?k=<apikey>&i=<device_ID>
Headers:
{
	"content-type": "text/plain"
}
Payload:
{
    "t": 5.4,
    "o": 4.3,
    "n": 3.2,
    "c": 2.1
}
```

The HTTP transport for the JSON protocol does not allow a single measure syntax.

**Send measures using JSON MQTT**

The payload to use with the MQTT transport of the IoTAgent is exactly the same as the one used in the HTTP version. The
main difference between both approaches is how to indicate the DeviceID of the device that is sending the measurement and
the APIKey of the Service associated to the device. In the case of the MQTT transport, both pieces of information are
specified in the MQTT topic, as we will see in the examples.

There are two kind of measurment reportings available for the MQTT transport: single measurement reports and multiple
measurement reports. Examples are shown as mosquitto_pub sentences.

In the case of single measurements, just the value of the measurement is sent as the message payload, being the rest
of information needed for the update confined to the MQTT topic, as illustrated in the following example:
```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -u theUser -P thePassword -t /<myapikey>/<mydevicemqtt>/attrs/<measurename> -m '5.4'
```
In this example we can see that the topic contains three pieces of data:
- The API Key ('<myapikey>'): identifies the service or configuration associated to the device.
- The Device ID ('<mydevicemqtt>'): that uniquely identifies a device in a service.
- The Measure name ('<measurename>'): that indicates the name of the measure to update.

The message payload contains the plain attribute value ('5.4').

In the case of multiple measurements, the MQTT message will contain a JSON Object with multiple attributes, each one
indicating the value of a single measurement, as illustrated in the following example:
```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -u theUser -P thePassword -t /<myapikey>/<mydevicemqtt>/attrs -m '{ "t": 5.4, "o": 4.3, "n": 3.2, "c": 2.1 }'
```

As it can be noticed in this example, the MQTT broker uses a set of credentials to authenticate users. Please, if you
don't know your credentials, please ask the support team to provide you with a new set.

The topic parts are the same as in the case of a single measure, excluding the measurement name. In this case, four
measurements will be updated in the target entity.

# Act upon devices 

## Transport protocol
In order to send commands to devices, you just need to know which attributes correspond to commands and update them.
You can declare the command related attributes at the registry process (as shown in the previous "Register your IoT
device" section).

If you take a look to the previous device example, you can find that a "ping" command was defined.
Any update on this attribute “Ping” at the NGSI entity in the ContextBroker will send a command to your device.

For HTTP devices, whose "endpoint": "http://[DEVICE_IP]:[PORT]" is declared, then your device is supposed to be
listening for commands at that URL in a synchronous way.

For MQTT devices (where enpoint attribute is not declared) then your devices is supposed to subscribe to the following
MQTT topic:
```
<apiKey>/<deviceId>/cmd
```
where it will receive the command information.

Once the command is completed, the device should return the result of the command to the IoTAgent. For HTTP devices,
the payload should be returned as the answer to the HTTP request. For MQTT devices, the result should be returned to
the following topic:
```
<apiKey>/<deviceId>/cmdexe
```

## Command payloads
Concerning the payload, the command information will have the same information for both transport protocols.
```
<device name>@<command name>|<param name>=<value>|....
```
This indicates that the device (named 'device_name' in the Context Broker) has to execute the command 'command_name',
with the given parameters. E.g.:
```
weatherStation167@ping|param1=1|param2=2
```
This example will tell the Weather Station 167 to reply to a ping message with the provided params.

Once the command has finished its execution in the device, the reply to the server must adhere to the following format:
```
<device name>@<command name>|result
```
Where `device_name` and `command_name` must be the same ones used in the command execution, and the result is the
final result of the command. E.g.:
```
weatherStation167@ping|Ping ok
```
In this case, the Weather station replies with a String value indicating everything has worked fine.


# In more detail ...

You can get more information about the FIWARE component providing this functionalty, reference API documentation and
source code at the [Ultralight IoT Agent](device_gateway.md)

