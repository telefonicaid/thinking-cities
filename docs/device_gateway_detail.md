# Device API

Device API allows you to:
- Register your device to reduce the message footprint and use commands.
- Send data from the device to the FIWARE IoT Stack
- Send commands from your application to the device

If you want to quickly connect or simulate virtual devices you may also check FIGWAY, a set of simple python scripts working as a client SDK for any desktop PC, laptop or gateway supporting a python2.7 environment. This way you may skip the steps described below and use the python commands as described in the README.md file available at this [Github repository](https://github.com/telefonicaid/fiware-figway).

# Register your IoT device 

Remember this step is optional, it is only required if you want to use commands in order to act upon devices or want to define a mapping to reduce the attributes identifier when you send observations to reduce the message size. 

If you simply want to send observations you can skip this and just to Send Obsersations.

## Registering for UL2.0

On this sample a device is registered to send temperature observations using UL2.0 protocol and a PING command:

```
POST $HOST_IOTAGENT/iot/devices

Headers: {'content-type': 'application/json’; 'X-Auth-Token' : [TOKEN]; "Fiware-Service: OpenIoT”; "Fiware-ServicePath: /"}
Payload:
{"devices": [
	{ "device_id": ”[DEV_ID]",
  	"entity_name": ”[ENTITY_ID]",
  	"entity_type": "thing",
 	"protocol": "PDI-IoTA-UltraLight",
  	"timezone": ”Europe/Madrid",
	"endpoint": "http://[DEVICE_IP]:[PORT]",
"attributes": [
    	{ "object_id": "t",
      	"name": "temperature",
      	"type": "int"
    	} ],
"commands": [
    	{ "name": "ping",
      	"type": "command",
      	"value": "[Dev_ID]@ping|%s"
    	} ],
 "static_attributes": [
    	{ "name": "att_name",
      	"type": "string",
      	"value": "value"
    	}]}]}
```

The important parameters to be defined are:

- [DEV_ID] the device identifier.
- [ENTITY_ID] the entity ID to be used at the ContextBroker
- "attributes": Used to map UL2.0 alias to ContextBroker entities.
- "commands": Used to indicate which commands the device supports. Depending on the "endpoint" configuration, commands will be considered as push or pull.
- "static_attributes": Used to define static attributes (sent in every observation)

## Registering for MQTT 

In this case, it will be necessary to provision a device in the MQTT IoT Agent:

```
POST $HOST_IOTAGENT/iot/devices

Headers: {'content-type': 'application/json’; 'X-Auth-Token' : [TOKEN]; "Fiware-Service: OpenIoT”; "Fiware-ServicePath: /"}
Payload:
{"devices": [
	{ "device_id": ”[DEV_ID]",
  	"entity_name": ”[ENTITY_ID]",
  	"entity_type": "thing",
 	"protocol": "PDI-IoTA-MQTT-UltraLight",
  	"timezone": ”Europe/Madrid",
	"endpoint": "http://[DEVICE_IP]:[PORT]",
"attributes": [
    	{ "object_id": "t",
      	"name": "temperature",
      	"type": "int"
    	} ],
"commands": [
    	{ "name": "ping",
      	"type": "command",
      	"value": "[Dev_ID]@ping|%s"
    	} ],
 "static_attributes": [
    	{ "name": "att_name",
      	"type": "string",
      	"value": "value"
    	}]}]}
```

# Send Observations 

## Sending using UL2.0

Ultralight2.0 (UL2.0) is a proposed simplification of the SensorML (SML) standard – and get those devices sending  their measurements (observations) to the ContextBroker. Ultralight2.0 is selected in this example because of its simplicity. 

Sending an observation from IoT devices is extremely efficient and simple with the following HTTP POST request:

```
POST  $HOST_IOTAGENT/d?k= <apikey>&i= <device_ID>
Headers: {'content-type': 'application/text’; 'X-Auth-Token' : [TOKEN]; "Fiware-Service: OpenIoT”; "Fiware-ServicePath: /"}
Payload: ‘t|25‘
```

The previous example sends an update of the Temperature attribute that is automatically sent by IDAS to the corresponding entity at the ContextBroker.

Sending multiple observations in the same message is also possible with the following payload:

```
//“alias1|value1#alias2|value2#alias3|value3...”
POST  $HOST_IOTAGENT/d?k= <apikey>&i= <device_ID>
Headers: {'content-type': 'application/text’; 'X-Auth-Token' : [TOKEN]; "Fiware-Service: OpenIoT”; "Fiware-ServicePath: /"}
Payload: ‘t|23#h|80#l|95#m|Quiet‘
```

Finally, after connecting your IoT devices this way you (or any other developer with the right access permissions) should be able to use the Data API to read the NGSI entity assigned to your device or see the data on the Management Portal.


## Sending using MQTT 

Devices (once provisioned under a service) can publish MQTT messages to the IoTAgent. Those messages contain one piece of information each. That means that one message will be translated into one single entity on the ContexBroker domain. The information can be typically sensors' measures.

This is the topic hierarchy that has to be used by devices:

```
<api-key>/<device-id>/<type>
```

Where:

-  ''api-key'': this is a unique value per service. It is provided through the provisioning API.
-  ''device-id'': this is typically a sensor id, it has to be unique per “api-key”.
- ''type'': it is the actual phenomenon being measured, for example: temperature, pressure, etc… this is the name of the attribute being published on ContextBroker.

Example:

```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -t <api_key>/mydevicemqtt/t -m 44.4 -u <api_key>
```

Another scenario can happen when devices send more than one phenomena within the payload. That is to say, one single MQTT message carries all measures. When it comes to ContextBroker, there will be one entity publication (per device) but containing all different attributes as per measures included in the mqtt message (each phenomenon or measure will be a separate attribute). In order to be able to parse the information on the IoTAgent, devices should follow this format:

Topic:

```
<api-key>/<device-id>/mul20
```

Example:

```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -t <api_key>/mydevicemqtt/mul20 -m "t|5.4#o|4.3#n|3.2#c|2.1" -u <api_key>
```

# Acting upon devices 

In order to send commands to devices, you just need to know which attributes correspond to commands and update them.

You can declare the command related attributes at the registry process (POST request) in the following way:

If you take a look to the previous device example, you can find that a "Ping" command was defined. 

Any update on this attribute “Ping” at the NGSI entity in the ContextBroker will send a command to your device.

If the row "endpoint": "http://[DEVICE_IP]:[PORT]" is declared, then your device is supposed to be listening for commands at that URL in a synchronous way.

If that enpoint is not declared (if that row does not exist) then your devices is supposed to work in a polling mode and therefore receiving commands in an asynchronous way (i.e. when the device proactively asks for commands).

For a device working in the polling mode to receive commands, the full pending queue of commands will be received with the following HTTP GET request:
 
```
GET  $HOST_IOTAGENT/d?k=<apikey>&i=<device_ID>
Headers: {'content-type': 'application/text’; 'X-Auth-Token' : [TOKEN]; "Fiware-Service: OpenIoT”; "Fiware-ServicePath: /"}
http://130.206.80.40:5371/iot/d?k=[APIKEY]&i=[DEV_ID]

```

# In more detail ...

Device API is provided by Device Backend Gateway IDAS, you can read full reference.



