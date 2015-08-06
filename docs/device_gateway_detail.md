# Publishing data from your IoT Devices #

The first thing you need to do is to connect your IoT devices or Gateways to the above described scenario. This basically means that your IoT devices’ observations reach a ContextBroker.

In the following paragraphs, we provide an example on how you would connect your devices using Ultralight2.0 (UL2.0) – which is a proposed simplification of the SensorML (SML) standard – and get those devices sending  their measurements (observations) to the ContextBroker. Ultralight2.0 is selected in this example because of its simplicity. 

If you want to quickly connect or simulate virtual devices you may also check FIGWAY, a set of simple python scripts working as a client SDK for any desktop PC, laptop or gateway supporting a python2.7 environment. This way you may skip the steps described below and use the python commands as described in the README.md file available at this [Github repository](https://github.com/telefonicaid/fiware-figway).


Basically, there are 3 simple steps to follow:


### 1.    Register your IoT device (optional)

Before your device sends observations or receives commands a register operation is needed:

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

### 2.     Send Observations related to your IoT device

Sending an observation from IoT devices is extremely efficient and simple with the following query:

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

### 3.     Reading measurements sent by your IoT device

Finally, after connecting your IoT devices this way you (or any other developer with the right access permissions) should be able to use the ContextBroker NGSI API to read the NGSI entity assigned to your device. 

The Entity ID will be the following “[ENTITY_ID]”.



# Acting upon devices #

In order to send commands to devices, developers just need to know which attributes correspond to commands and update them.

IoT integrators need to declare the command related attributes at the registry process (POST request) in the following way:

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

# MQTT Example #

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



### Sending individual measures

This is the simplest and more straightforward scenario. Devices (once provisioned under a service) can publish MQTT messages to the IoTAgent. Those messages contain one piece of information each. That means that one message will be translated into one single entity on the ContexBroker domain. The information can be typically sensors' measures.

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

### Sending block measures

Another scenario can happen when devices send more than one phenomena within the payload. That is to say, one single MQTT message carries all measures. When it comes to ContextBroker, there will be one entity publication (per device) but containing all different attributes as per measures included in the mqtt message (each phenomenon or measure will be a separate attribute). In order to be able to parse the information on the IoTAgent, devices should follow this format:

Topic:

```
<api-key>/<device-id>/mul20
```

Example:

```
$ mosquitto_pub -h $HOST_IOTAGENT_MQTT -t <api_key>/mydevicemqtt/mul20 -m "t|5.4#o|4.3#n|3.2#c|2.1" -u <api_key>
```

 
