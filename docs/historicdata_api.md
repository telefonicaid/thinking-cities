Historic Data API allows you to:

- Get raw time series data sent from your device
- Get aggregated time series data (max, min, average ...) 

Historic Data API provides raw and aggregated time series information about the evolution in time of data from your device. 

In a nutshell, using Historic Data API you can get basic statistics from your IoT device data but without storing and processing it on your service. Please, take into account that you must active the historic for the relevant data from your device.

# Activate historic

You have to choose which data historic is relevant for you to be stored at the FIWARE IoT Stack.

In order to do so, you have to select which Data API entity attributes are relevant:

```
POST /NGSI10/subscribeContext HTTP/1.1
Host: test.ttcloud.net:1026
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}

{
    "entities": [
        {"type": "device",
        "isPattern": "false",
        "id": "mydevice"
            
        }
    ],
    "reference": "http://test.ttcloud.net:8666/notify", 
    "duration": "P1Y",
    "notifyConditions": [
           {
                "type": "ONCHANGE", 
                "condValues": ["TimeInstant" ]
} ],
"throttling": "PT1S" }
```

If you are familiar with FIWARE componentes, on this request you are using the Data API subscription operation to notify new data from your device to the FIWARE STH component providing the Historic Data API.

# Get raw time series data

# Get aggregated time series data

# In more detail ...
