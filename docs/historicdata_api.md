Historic Data API allows you to:

- Get raw time series data sent from your device
- Get aggregated time series data (max, min, average ...) 

Historic Data API provides raw and aggregated time series information about the evolution in time of data from your device. 

In a nutshell, using Historic Data API you can get basic statistics from your IoT device data but without storing and processing it on your service. Please, take into account that you must active the historic for the relevant data from your device.

# Activate historic

You have to choose which data historic from your IoT device is relevant to be stored at the FIWARE IoT Stack.

In order to do so, you have to select the Data API entity attributes.  On the following examples, the temperature attribute will be selected:

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

If you are familiar with FIWARE components, on this request you are using the Data API subscription operation to notify new data from your device to the FIWARE STH component providing the Historic Data API.

# Get raw time series data

Your device data is now stored in the FIWARE IoT Stack, and your can get the raw time series in you application.

You can get the last N values stored as follows:

```
GET /STH/v1/contextEntities/type/device/id/mydevice/attributes/temperature?lastN=10 HTTP/1.1
Host: test.ttcloud.net:8666
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}
```

More complex filtering like data interval selection and results pagination is also available:

```
GET /STH/v1/contextEntities/type/device/id/mydevice/attributes/temperature?hLimit=3&hOffset=0&dateFrom=2014-02-14T00:00:00.00
Host: test.ttcloud.net:8666
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}
```

You will get a list with the requested attribute values and its associated timestamps:

```
HTTP 200 OK
Content-Type : application/json

{
  "contextResponses": [
    {
      "contextElement": {
        "attributes": [
          {
            "name": "temperature",
            "values": [
                {
                    "recvTime": "2014-02-14T13:43:33.306Z"
                    "attrValue": "21.28"
                },
                {
                    "recvTime": "2014-02-14T13:43:34.636Z",
                    "attrValue": "23.42"
                },
                {
                    "recvTime": "2014-02-14T13:43:35.424Z",
                    "attrValue": "22.12"
                }
            ]
          }
        ],
        "id": "mydevice",
        "isPattern": false,
        "type": "device"
      },
      "statusCode": {
        "code": "200",
        "reasonPhrase": "OK"
      }
    }
  ]
}

```

Please, take into account that you will be able to get one attribute (i.e. temperature, hummidity ...) per request.

# Get aggregated time series data

Sometimes, the amount of stored data may be huge and it is not efficient to get the whole dataset in order to calculate basic statistics like average value.  

You can get aggregated data to calculate this basic statistics as follows:

```
GET /STH/v1/contextEntities/type/device/id/mydevice/attributes/temperature?aggrMethod=sum&aggrPeriod=second&dateFrom=2015-02-22T00:00:00.000Z&dateTo=2015-02-22T23:00:00.000Z
Host: test.ttcloud.net:8666
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}} 
Fiware-ServicePath: {{Fiware-ServicePath}} 
X-Auth-Token: {{user-token}}
```

As aggrMethog you can specify the following values that let you calculate:

- max: maximum value
- min: minimum value
- sum: sum of all the samples
- sum2: sum of the square value of all the samples. 
 
Combining the information provided by these aggregated methods with the number of samples you will also get on the response, it is possible to calculate basic statistics such as the average value, the variance as well as the standard deviation.

You will get the aggregated data value and the number of samples on the response:

```
HTTP 200 OK
Content-Type : application/json

{
    "contextResponses": [
        {
            "contextElement": {
                "attributes": [
                    {
                        "name": "temperature",
                        "values": [
                            {
                                "_id": {
                                    "origin": "2015-02-18T02:46:00.000Z",
                                    "range": "minute",
                                    "resolution": "second"
                                },
                                "points": [
                                    {
                                        "offset": 0,
                                        "samples": 123,
                                        "sum": 34.59
                                    }
                                ]
                            }
                        ]
                    }
                ],
                "id": "entityId",
                "isPattern": false
            },
            "statusCode": {
                "code": "200",
                "reasonPhrase": "OK"
            }
        }
    ]
}
```

# In more detail ...

You can get more information about the FIWARE component providing this functionalty, reference API documentation and source code at [Short Term Historic (IDM)](sth.md)
