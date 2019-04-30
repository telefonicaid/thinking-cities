
In order to send information from the devices to the platform, you can use the IoT Agents. These components
map South-Bound protocol requests coming from the device to NGSI requests to a Context Broker, that will help you
mapping your device data to an NGSI Entity and attributes.

The Device API allows you to:

- Register your device to reduce the message footprint and use commands.
- Send data from the device to Thinking Cities
- Send commands from your application to the device

The following documents show how to manage device connectivity through the IoT Agent Manager of the IoT Platform. These
APIs have some differences with the standard IoT Agent Device Provisioning APIs as defined in the [IoT Agent Library](https://github.com/telefonicaid/iotagent-node-lib)
but it should be transparent for the users of the Platform.

# Configure the South Bound protocol

In order for the South Bound protocols (i.e.: the protocols used to communicate physical devices with the Platform)
to work for your service, you must provision the information about your devices, either by provisioning the device itself,
by provisioning a Configuration Group (for each subservice) or both. Configuration Groups (also called Services; do not
mistake them for the multitenancy Service concept), define some default values for the South Bound protocol to NGSI mapping,
that will be applied to every device associated to the group. Devices will be associated to groups based on the API Key
provided by the Device in the communications.

Configuration Groups can be provisioned through the API. If you are not the administrator
of your subservices, you may have been given an API Key. If that's the case, there is no need to configure the
South Bound protocol again. Use the provided data for future interactions.

The following excerpt shows you how to provision a Configuration Group directly to the API:

    POST /iot/configGroups?protocol=IoTA-UL
    Content-Type: application/json
    Fiware-service: OpenIoT
    Fiware-servicepath: /

    {
      "services": [
        {
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

This request shows the provisioning of an Ultralight 2.0 Configuration group, indicating the API Key, the `entity_type`
that will be associated with all the devices in the group and common attributes all devices in the group will share,
along with the information for its mapping to the NGSI entity.

Provisioning of JSON Configuration group is exactly the same just replacing `?protocol=IoTA-UL` by `?protocol=IoTA-JSON`. 

Currently, the IoT Platform only allows for the existence of a configuration group per subservice for each protocol and apikey, i.e. one for "IoTA-UL" with apikey "XXYYZZ", "IoTA-UL", other with apikey "AABBBCC" and other for "IoTA-JSON".

# Register your IoT device 

Remember this step is optional, it is only required if you want to use commands in order to act upon devices or you want to
define a mapping to reduce the attributes identifier when you send observations to reduce the message size. But, in case
the device is not provisioned, the Configuration Group **must** be configured.

If you simply want to send observations you can skip this and just go to the "Send observations" section.

Examples below are provided only for IoTA-UL cases for the sake of briefness, but IOTA-JSON examples are
exactly the same just changing `protocol=IoTA-UL` by `protocol=IoTA-JSON`. 

**Registering for HTTP commands**

On this sample a device is registered to use HTTP binding for a PING command:

```
POST /iot/devices
Content-Type: application/json
X-Auth-Token: [TOKEN]
Fiware-Service: OpenIoT
Fiware-ServicePath: /

{
	"devices": [{
		"device_id": "[DEV_ID]",
		"entity_name": "[ENTITY_ID]",
		"entity_type": "thing",
		"protocol":"IoTA-UL",
		"timezone": "Europe/Madrid",
		"endpoint": "http://[DEVICE_IP]:[PORT]",
		"attributes": [{
			"object_id": "t",
			"name": "temperature",
			"type": "int"
		}],
		"commands": [{
			"name": "ping",
			"type": "command"
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
- *entity_name*: the entity ID to be used at the Context Broker.
- *entity_type*: type of the entity that will represent the device in the Context Broker.
- *protocol*: South-Bound protocol the device will be using to communicate with the Platform (mandatory).
- *timezone*: timezone for the device.
- *endpoint*: for devices accepting HTTP commands, address of the device where the commands will be sent.
- *attributes*: Used to map UL2.0 attributes to Context Broker attributes in the entity representing the device.
- *commands*: Used to indicate which commands the device supports. For HTTP attributes, the "endpoint" attribute will
be required.
- *static_attributes*: the contents of this attribute will be sent in every observation as attributes of the entity.

**Registering for MQTT commands**

The following example shows the same registration for an MQTT device instead of HTTP:

```
POST /iot/devices
Content-Type: application/json
X-Auth-Token: [TOKEN]
Fiware-Service: OpenIoT
Fiware-ServicePath: /

{
	"devices": [{
		"device_id": "[DEV_ID]",
		"entity_name": "[ENTITY_ID]",
		"entity_type": "thing",
		"protocol":"IoTA-UL",
		"transport": "MQTT",
		"timezone": "Europe/Madrid",
		"attributes": [{
			"object_id": "t",
			"name": "temperature",
			"type": "int"
		}],
		"commands": [{
			"name": "ping",
			"type": "command"
		}],
		"static_attributes": [{
			"name": "att_name",
			"type": "string",
			"value": "value"
		}]
	}]
}
```

The example shows there are two differences comparing with provisioning for HTTP:

* The absence of an "endpoint" field
* The presence of "transport" field, which value has to be "MQTT".

**Registering without endpoint commands**

For devices that won't be using commands, you don't need to specify "endpoint" field. This is also the case of devices using commands but in pull mode, in which case the command will be stored int the IoT Agent waiting for the device to retrieve the command. This kind of commands are called pull commands.

# Send observations 

There are two IoT Agents currently available in the platform, each listening for requests with a different protocol:
* Ultralight 2.0
* JSON

In both cases, payloads can be sent to the IoT Agent using two different transport protocols: 
* MQTT
* HTTP

In order to select the appropriate IoT Agent, change the declared protocol in the query parameters: IoTA-UL or IoTA-JSON.
The following sections show some examples of each of the four possible approaches.

**Send measures using UL2.0 HTTP**

Ultralight2.0 (UL2.0 or just UL for the sake of simplicity) is a proposed simplification of the SensorML (SML) standard – and will be used to send device measurements (observations) to the Context Broker. Ultralight2.0 is selected in this example because of its simplicity.

Sending an observation from IoT devices is simple with the following HTTP POST request:

```
POST /iot/d?k=<apikey>&i=<deviceId>
Content-Type: text/plain

t|25
```

The previous example sends an update of the `temperature` attribute that is automatically sent by the IoT Agent to the
corresponding entity at the Context Broker.

Multiple measures for a single observation can be sent, separating the values with pipes:

```
POST /iot/d?k=<apikey>&i=<deviceId>
Content-Type: text/plain

t|25|h|42|l|1299
```

This request will generate a single update request to the Context Broker with three attributes, one corresponding to
each measure.

Sending multiple observations in the same message is also possible with the following payload:

```
POST /iot/d?k=<apikey>&i=<deviceId>
Content-Type: text/plain

t|23#h|80#l|95#m|Quiet
```

This request will generate four requests to the Context Broker, each one reporting a different value.

Finally, after connecting your IoT devices this way you (or any other developer with the right access permissions) should be able to use the Data API to read the NGSI entity assigned to your device or see the data on the Management Portal.


**Send measures using UL2.0 MQTT**

Devices (once provisioned under a service) can publish MQTT messages to the IoT Agent. Those messages contain one piece
of information each. That means that one message will be translated into one single entity on the Context Broker domain.
The information can be typically sensors measures.

This is the topic hierarchy that has to be used by devices:

```
/<apikey>/<deviceId>/attrs/<attrName>
```

Where:

- "apikey": this is a unique value per service. It is provided through the provisioning API.
- "deviceId": this is typically a sensor id, it has to be unique per “apikey”.
- "attrName": name of the magnitude being measured, for example: temperature, pressure, etc… this is the name of
the attribute being published on Context Broker.

Example:

```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -u theUser -P thePassword -t /<apikey>/mydevicemqtt/t -m 44.4
```

As it can be noticed in this example, the MQTT broker uses a set of credentials to authenticate users. Please, if you
don't know your credentials, please ask the support team to provide you with a new set.

Another scenario can happen when devices send more than one phenomena within the payload. That is to say, one single
MQTT message carries all measures. When it comes to Context Broker, there will be one entity publication (per device)
but containing all different attributes as per measures included in the mqtt message (each phenomenon or measure will
be a separate attribute). In order to be able to parse the information on the IoT Agent, devices should follow the
same Ultralight 2.0 format as in the HTTP case.

Topic:

```
/<apikey>/<deviceId>/attrs
```

Example:

```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -u theUser -P thePassword -t /<apikey>/mydevicemqtt/attrs -m "t|5.4#o|4.3#n|3.2#c|2.1"
```

**Send measures using JSON HTTP**

The simple JSON protocol used by the JSON IoT Agent maps each measurement to an attribute in a JSON Object. The following
example shows how to send a measurement of three different quantities:
```

POST  /iot/json?k=<apikey>&i=<deviceId>
Content-type: application/json

{
    "t": 5.4,
    "o": 4.3,
    "n": 3.2,
    "c": 2.1
}
```

The HTTP transport for the JSON protocol does not allow a single measure syntax.

**Send measures using JSON MQTT**

The payload to use with the MQTT transport of the IoT Agent is exactly the same as the one used in the HTTP version. The
main difference between both approaches is how to indicate the DeviceID of the device that is sending the measurement and
the APIKey of the Service associated to the device. In the case of the MQTT transport, both pieces of information are
specified in the MQTT topic, as we will see in the examples.

There are two kind of measurement reports available for the MQTT transport: single measurement reports and multiple
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

## Send commands

In order to send commands to devices, you need to know which attributes correspond to commands and update them. You can declare the command related attributes at the registry process (as shown in the previous "Register your IoT device" section). Also, you can declare the protocol you want the commands to be sent (HTTP/MQTT) with the "transport" parameter at the registry process.

If you take a look to the previous device example, you can find that a "ping" command was defined. Any update on this attribute “ping” at the NGSI entity in the Context Broker will send a command to your device. For instance, to send the "ping" command with value "Ping request" you could use the following operation in the Context Broker API:

```
PUT /v2/entities/<entity_id>/attrs/ping?type=<entity_type>

{
  "value": "Ping request",
  "type": "command"
}

```

It is important to note that parameter type, with the entity_type must be included.

Context Broker API is quite flexible and allows to update an attribute in several ways. Please have a look to the [NGSIv2 specification](http://telefonicaid.github.io/fiware-orion/api/v2/stable) for details.

**Important note**: don't use operations in the NGSI API with creation semantics. Otherwise, the entity/attribute will be created locally to Context Broker and the command will not progress to the device (and you will need to delete the created entity/attribute if you want to make it to work again). Thus, the following operations *must not* be used:

* `POST /v2/entities`
* `PUT /v2/entities`
* `POST /v2/op/entites` with `actionType` `append`, `appendStrict` or `replace`
* `POST /v1/updateContext` with `actionType` `APPEND`, `APPEND_STRICT` or `REPLACE`

## HTTP devices

For HTTP devices, in order to retrieve a pull command from IoTA-UL Agent the device should make the following request:

```
GET /iot/d?k=<apikey>&i=<deviceId>&getCmd=1
Accept: application/json
```

For IoTA-JSON Agent it is exactly the same just changing in the request the `/iot/d` by `/iot/json` and setting the correct apikey and deviceId.

It can be also possible for a device to retrieve the commands from the IoT Agent when it sends and observation. It just be needed to include the `&getCmd=1` parameter in the observation request. In the following example a device sends an UL observation and retrieves the commands from the IoTA-UL Agent.

```
POST /iot/d?k=<apikey>&i=<deviceId>&getCmd=1
Content-Type: text/plain

t|25|h|42|l|1299
```

This is also possible for IoTA-JSON Agent changing in the request the `/iot/d` by `/iot/json` and setting the correct apikey and deviceId.

**Push commands**

Push commands are those that are sent to the device once the IoT Agent receives the request from the Context Broker. In order to send push commands it is needed to set the "endpoint": "http://[DEVICE_IP]:[PORT]" in the device provision. The device is supposed to be listening for commands at that URL in a synchronous way. Make sure the device endpoint is reachable by the IoT Agent.

Once the command is delivered, the device should return the result of the command to the IoT Agent as the answer to the 
HTTP request. This result will be progressed to the Context Broker where it will be stored in the "command_info" attribute. The status of the command will be stored in the "command_status" attribute (`OK` if everything goes right). 

Push commands are only valid for HTTP devices. For MQTT devices it is not needed to set the "endpoint" parameter. 

**Pull commands**

Pull commands are those that are stored in the IoT Agent waiting to be retrieved by the devices. This kind of commands are typically used for devices that doesn't have a public IP or the IP cannot be reached. The device connects to the IoT Agent periodically to retrieve commands. In order to send pull commands you just need to ignore the "endpoint" parameter in the device provision.

Once the command request is issued to the IoT agent, the command is stored waiting to be retrieved by the device. In that moment, the status of the command is "command_status": "PENDING". Once the command is retrieved by the device the status is updated to "command_status": "DELIVERED". Eventually, once the device makes the response request with the result of the command the status is updated to "command_status": "OK". Also the result of the command delivered by the device is stored in the "command_info" attribute.

## MQTT devices

For MQTT devices, it is not needed to declare and endpoint. The device is supposed to be subscribed to the following MQTT topic:

```
/<apiKey>/<deviceId>/cmd
```

where it will receive the command information. Please note that the device should subscribe to the broker using the disabled clean session mode (enabled using `--disable-clean-session` option CLI parameter in mosquitto_sub). This option means that all of the subscriptions for the device will be maintained after it disconnects, along with subsequent QoS 1 and QoS 2 commands that arrive. When the device reconnects, it will receive all of the queued commands.

Once the command is completed, the device should return the result of the command to the IoT Agent to the following topic:

```
/<apiKey>/<deviceId>/cmdexe
```

This result will be progressed to the Context Broker where it will be stored in the "command_info" attribute. The status of the command will be stored in the "command_status" attribute (`OK` if everything goes right). 

## Command payloads

**Command payloads for UL**

Commands are sent when the proper update is done in the ContextBroker on the attribute representing the command. We can distinguish between two cases: commands with parameter is a single value and commands that use several paramters.

For commands with a single value as parameter the attibute will be updated at CB using a JSON like this:

```
{ "type": "command", "value": "<commandValue>"}
```

For commands with a single value as parameter the attibute will be updated at CB using a JSON like this:

```
{ "type": "command", "value": { "param1": "val1", "param2": "val2" }
```

Concerning the payload for UL, the command information will have the same information for both transport protocols (HTTP or MQTT). For single-parameter commands:

```
<deviceId>@<commandName>|<commandValue>
```

This indicates that the device (which ID is `deviceId`) has to execute the command `commandName`, with the given `commandValue`. E.g.:

```
Robot1@turn|left
```

This example will tell the Robot 1 to turn to left.

In the case of complex commands requiring parameters, the `commandValue` could be used to implement parameter passing. E.g:

```
weatherStation167@ping|param1=val1|param2=val2
```

This example will tell the Weather Station 167 to reply to a ping message with the provided params. 

Note that you cannot use [forbidden character for Context Broker](https://fiware-orion.readthedocs.io/en/master/user/forbidden_characters/index.html) as part of command parameters, because that would result in a 400 Bad Request error and at CB and the triggering of the command would be never progressed.

Once the command has finished its execution in the device, the reply to the IOTA must adhere to the following format:

```
<deviceId>@<commandName>|<result>
```

Where `deviceId` and `commandName` must be the same ones used in the command execution, and `result` is the final result of the command. E.g.:

```
weatherStation167@ping|Ping ok
```

In this case, the Weather station replies with a string value indicating everything has worked fine.

**Command payloads for JSON**

Commands are sent when the proper update is done in the ContextBroker on the attribute representing the command. We can distinguish between two cases: commands with parameter is a single value and commands that use several paramters.

For commands with a single value as parameter the attibute will be updated at CB using a JSON like this:

```
{ "type": "command", "value": "<commandValue>"}
```

For commands with a single value as parameter the attibute will be updated at CB using a JSON like this:

```
{ "type": "command", "value": { "param1": "val1", "param2": "val2" }
```

Concerning the payload for JSON, the command information will have the same information for both transport protocols (HTTP or MQTT). For single-parameter commands:

```
{
  "<commandName>": <commandValue>
}
```

This indicates that the device has to execute the command `commandName`, with the given `commandValue`. E.g.:

```
{
  "turn": "left"
}
```

This example will tell the device to turn to left.

In the case of complex commands requiring parameters, the `commandValue` could be used to implement parameter passing. E.g:

```
{
  "ping": { "param1": "val1", "param2": "val2" }
}
```

This example will tell the device to reply to a ping message with the provided params. 

Note that you cannot use [forbidden character for Context Broker](https://fiware-orion.readthedocs.io/en/master/user/forbidden_characters/index.html) as part of command parameters, because that would result in a 400 Bad Request error and at CB and the triggering of the command would be never progressed.

Once the command has finished its execution in the device, the reply to the IOTA must adhere to the following format:

```
{
  "<commandName>": "<result>
}
```

Where `commandName` must be the same ones used in the command execution, and `result` is the final result of the command. E.g.:

```
{
  "ping": "ping ok"
}
```

In this case, the device replies with a String value indicating everything has worked fine.



# In more detail ...

You can get more information about the FIWARE component providing this functionality, reference API documentation and source code at the [IoT Agent](device_gateway.md)

