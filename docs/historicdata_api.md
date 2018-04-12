The Historical Data API exposed by the [Short Time Historic (STH)](https://github.com/telefonicaid/fiware-sth-comet) component allows you to:

- Get historical context information registered into the platform by any device.
- Get aggregated time series context information (maximum values, minimum values, average values, standard deviation of the values and number of occurrences) registered into the platform by any device.

# Historical data activation

Before being able to consume historical and aggregated time series information, it is important to notify the platform the concrete context information we are interested in.

To do it, a subscription from the Short Time Historic (STH) to the Context Broker is needed, specifying the concrete entities and attributes of interest for which the historical and aggregated time series information should be generated.

An example of such a subscription is included next. The concrete template values between `{{`and `}}` should be substituted by the real counterparts:

```
POST /v1/subscribeContext HTTP/1.1
Host: <cb_host>:<cb_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}}
Fiware-ServicePath: {{Fiware-ServicePath}}
X-Auth-Token: {{user-token}}

{
  "entities": [
    {
      "type": "device",
      "isPattern": "false",
      "id": "mydevice"
    }
  ],
  "notifyConditions": [
    {
      "type": "ONCHANGE",
      "condValues": ["TimeInstant"]
    }
  ],
  "reference": "http://<sth_host>:<sth_port>/notify",
  "duration": "P1Y",
  "throttling": "PT1S"
}
```

Notice that as a result of the previous subscription the platform will generate historical and aggregated time series context information about the `mydevice` entity of type `device` for each new attribute value notified by that device to the platform for the next year as set in the `duration` property.

# Historical context information retrieval

Once the historical and aggregated time series context information for the device of interest is activated, it can be retrieved from the platform.

For example, to get the last 10 values of the  `temperature` attribute registered by the device of interest, a request such as the following one should be sent to the platform:

```
GET /STH/v1/contextEntities/type/device/id/mydevice/attributes/temperature?lastN=10 HTTP/1.1
Host: <sth_host>:<sth_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}}
Fiware-ServicePath: {{Fiware-ServicePath}}
X-Auth-Token: {{user-token}}
```

Obviously, the `temperature` attribute could be changed by any other attribute for which the device of interest is registering values into the platform.

The Historical Data API also supports pagination by means of the `hLimit`and `hOffset` query parameters. For example, to get the first 3 temperature values from the specified date, a request such as the following should be sent to the platform:

```
GET /STH/v1/contextEntities/type/device/id/mydevice/attributes/temperature?hLimit=3&hOffset=0&dateFrom=2014-02-14T00:00:00.00
Host: <sth_host>:<sth_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}}
Fiware-ServicePath: {{Fiware-ServicePath}}
X-Auth-Token: {{user-token}}
```

As a result of the previous requests, a set of attribute values including their associated timestamps (this is, the concrete date and time when they were registered into the platform) will be returned, such as the following one:

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

Please, take into account that currently the Historical Data API only makes it possible to get historical context information associated to one concrete attribute (i.e. temperature, hummidity, etc.) per request.

# Aggregated time series context information retrieval

Apart from exposing historical context information, the Historical Data API also exposes aggregated time series context information about the entities and attributes of interest.

This aggregated time series context information makes it possible, for example, to get the maximum temperature values registered into the platform by certain device during certain period of time and grouped by certain time or resolution, such as in the next request:

```
GET /STH/v1/contextEntities/type/device/id/mydevice/attributes/temperature?aggrMethod=sum&aggrPeriod=second&dateFrom=2015-02-22T00:00:00.000Z&dateTo=2015-02-22T23:00:00.000Z
Host: <sth_host>:<sth_port>
Accept: application/json
Content-Type: application/json
Fiware-Service: {{Fiware-Service}}
Fiware-ServicePath: {{Fiware-ServicePath}}
X-Auth-Token: {{user-token}}
```

Currently, the Historical Data API supports the next  `aggrMethod` query parameter values:

1. For numeric attribute values:
    - `max`: maximum value.
    - `min`: minimum value.
    - `sum`: sum of all the samples.
    - `sum2`: sum of the square value of all the samples.
2. For textual attribute values:
    - `occur`: number of occurrences.

On the other hand, the supported values for the `aggrPeriod` query parameter (which makes it possible to set the time by which the context information should be grouped) are: `second`, `minute`, `hour`, `day` and `month`.

A typical response to an aggregated time series context information request such as the previous one is the following:

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
                  "resolution": "second"
                },
                "points": [
                  {
                    "offset": 0,
                    "samples": 123,
                    "sum": 34.59
                  },
                  {
                    "offset": 11,
                    "samples": 34,
                    "sum": 28.38
                  }
                ]
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

As you can see, the response includes an `origin` of time from which the `offset`s returned are referenced for the `resolution` or aggregation period requested.

# In more detail ...

You can get more information about the FIWARE component providing this functionality, reference API documentation and source code at the [Short Term Historic (STH)](sth.md) component section.
