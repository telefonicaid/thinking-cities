The Complex Event Processing API allows you to analyze data from your IoT device and trigger actions.

The following action types are available:

- update: update an entity's attribute
- sms: send a SMS
- email: send an email
- post: make an HTTP POST
- twitter: send a tweet

This API allows you for example to define rules to trigger email notifications based on the data value thresholds or the the lack of updates from a certain device.

These rules are expressed as an EPL sentence. [EPL](http://www.espertech.com/esper/index.php) is a domain language of Esper, the engine for processing events used. This EPL sentence matches an incoming event if satisfies the conditions and generates an "action-event" that will be sent back to FIWARE IoT Stack to execute the associated action. 


# Send Context to CEP to Activate Rules

In order CEP to assess events and execute rules it is needed to send to CEP the notifications from Context Broker with the context data. In order to send notifications to CEP it is needed to create the relevant subscripcions. It can be done using the [Context Broker API](https://fiware-iot-stack.readthedocs.io/en/latest/data_api/index.html#subscribe-to-data-updates)

Find below an example of subscription creation, where the attribute temperature is sent to CEP.

```
POST /v2/subscriptions HTTP/1.1
Host: <cb_host>:<cb_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}

{
  "description": "Subscription to CEP",
  "subject": {
    "entities": [
      {
        "idPattern": ".*",
        "type": "device"
      }
    ],
    "condition": {
      "attrs": [
        "TimeInstant"
      ]
    }
  },
  "notification": {
    "http": {
      "url": "http://10.0.0.2:9090/notices"
    },
    "attrs": [
      "temperature"
    ]
  },
  "expires": "2026-04-05T14:00:00.00Z"
}
```

Once the context data is modified in Context Broker and the subscription is unleashed, the context will be sent to the CEP, where the event will be assessed and rules will be executed if they are fullfilled.


# Create rule to send emails

Once you have activated the processing for your data, you can create a rule to trigger actions as follows:

```
POST /rules HTTP/1.1
Host: <cep_host>:<cep_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}

{
   "name":"temperature-rule",
   "text":"select *,\"temperature-rule\" as ruleName from pattern [every ev=iotEvent(cast(cast(ev.temperature?,String),float)>40.0)]",
    "action": {
        "type": "email",
        "template": "Alert! temperature is now ${ev.temperature}.",
        "parameters": {
            "to": "someone@yourclient.com",
            "from": "notificactions@yourdomain.com"
            "subject": "Alert! high pression detected"
        }
    }
}
```



# Create rule to send an  HTTP POST

You can also trigger an HTTP POST to an URL specified sending a body built from template:

```
POST /rules HTTP/1.1
Host: <cep_host>:<cep_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}

{
   "name":"temperature-rule",
   "text":"select *,\"temperature-rule\" as ruleName from pattern [every ev=iotEvent(cast(cast(ev.temperature?,String),float)>40.0)]",
    "action": {
        "type": "post",
        "template": "Alert! temperature is now ${ev.temperature}.",
        "parameters": {
            "url": "http://yourcustomer.com"
        }
    }
}
```

# Create rule to send a Tweet

You can send a tweet from your account with the text build from the template field. 

Remember you must create your Twitter app and get its OAuth credentials.

```
POST /rules HTTP/1.1
Host: <cep_host>:<cep_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}

{
    "name":"temperature-rule",
    "text":"select *,\"temperature-rule\" as ruleName from pattern [every ev=iotEvent(cast(cast(ev.temperature?,String),float)>40.0)]",
    "action": {
        "type": "twitter",
        "template": "Alert! temperature is now ${ev.temperature}.",
        "parameters": {
          "consumer_key": "xvz1evFS4wEEPTGEFPHBog",
          "consumer_secret": "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg",
          "access_token_key": "xvz1evFS4wEEPTGEFPHBog",
          "access_token_secret": "L8qq9PZyRg6ieKGEKhZolGC0vJWLw8iEJ88DRdyOg"
        }
    }
}

```

# Create rule to issue a command to a device

It is possible to send a command to a device using FIWARE CEP by updating the relevant Context Broker attribute. First of all, you have to register you IoT device setting the command you want to issue as stated in [Register your IoT device](https://fiware-iot-stack.readthedocs.io/en/latest/device_api/index.html#register-your-iot-device). The rule will update the command attribute in the Context Broker and then the Context Broker acts in the same way as in a manually issued command.

```
POST /rules HTTP/1.1
Host: <cep_host>:<cep_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}

{
    "name": "electrovalve_on",
    "text": "select *,\"electrovalve_on\" as ruleName from pattern [every ev=iotEvent((cast(cast(`humidity`?,String),float))<25 and type=\"humiditySensor\")]",
    "action": {
        "type": "post",
        "template": "{\"type\":\"command\",\"value\":1}",
        "parameters": {
            "url": "[CB_ENDPOINT]/v2/entities/${electrovalveId}/attrs/activate?type=electrovalve",
            "method": "PUT",
            "headers": {
                "Content-type": "application/json",
                "fiware-service": "${service}",
                "fiware-servicepath": "${subservice}"
            }
        }
    }
}

```

# In more detail ...

You can get more information about the FIWARE component providing this functionalty, reference API documentation and source code at [CEP](cep.md).
