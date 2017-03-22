Data API allows you to access the information stored at the FIWARE IoT Stack from your devices:

- Update and query data 
- Subscribe to data updates
- Geolocated data queries

All the communications between the various components of the FIWARE IoT Stack occur via the NGSI RESTful interface interfaces/protocols, which is described in a following sections. NGSI RESTful interface is inspired and based on the OMA NGSI specification. This is a standard interface allowing to handle any type of data, which may include meta-data. 

Before continuing with the next section, it would be a good idea to have a look at the [NGSI and Orion Context Broker introductory presentation](http://bit.ly/fiware-orion) in SlideShare.

# Update/Query data

Processes running as part of your application architecture that update context information using REST operations that the Context Broker GE exports, are said to play a **Context Producer** role.  As an example, let’s consider an application for rating restaurants (let’s call it NiceEating). The client part of that application running on the smartphone of users would play the role of Context Producer, enabling them to rate restaurants.

From a multitenancy point of view, let's consider that the service and subservice corresponding to the application are
"smartown" and "NiceEating" respectively. All the requests shown in the examples below will include the `Fiware-service` 
and `Fiware-servicepath` headers corresponding to that. From the point of view of the platform, these headers are
mandatory. More information on multitency in [this specific section in the documentation](multitenancy.md). The examples
also include the `X-Auth-Token` header, which is required to authenticate the requests (see 
[the section about security](security.md) for details).

On the other hand, processes running as part of your application architecture that query context information using REST operations that the Context Broker GE exports are said to play a **Context Consumer** role.  Continuing with our NiceEating app, the mobile application running on the smartphone of users and enabling them to query for the rating of restaurants would play the role of Context Consumer.  Note that a given part of your application may play both the Context Producer and Context Consumer roles.  This would be the case of the mobile client of the NiceEating app enabling end users to rate, and query about rates of, restaurants.

Entities that would be relevant to the NiceEating application are of type Restaurant, Client and Rating. For example, when a given user scores a restaurant (e.g. in a scale from 0 to 5, “Client1234” scores “4” for the “LeBistro” restaurant) the smartphone application plays the Context Producer role **creating** a Rating entity by issuing the following HTTP request:

```
POST <cb_host>:<cb_port>/v2/entities
Content-Type: application/json
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL

{
  "id": "LeBistro::Client1234",
  "type": "Rating",
  "score": {
    "type" : "Integer",
    "value" : 4
  }
}
```

Each time a new Rating entity is created, the average rating for the corresponding restaurant is recalculated by the application backend which (playing also the role of Context Producer) **updates** the Restaurant entity accordingly:

```
PUT <cb_host>:<cb_port>/v2/entities/LeBistro/attrs/average_scoring?type=Restaurant
Content-Type: application/json
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL

{
  "type": "Float",
  "value" : 4.2
}
```

or (more compact):

```
PUT <cb_host>:<cb_port>/v2/entities/LeBistro/attrs/average_scoring/value?type=Restaurant
Content-Type: text/plain
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL

4.2
```

Finally, the user can get the information of a given Restaurant using the smartphone application. In that case the application works as Context Consumer, **querying** the Restaurant entity. For example, to get the average_scoring attribute, the client application could query for it in the following way:

```
GET <cb_host>:<cb_port>/v2/entities/LeBistro/attrs/average_scoring?type=Restaurant
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL
```

getting a JSON response such as the following one:

```
{
  "type": "Float",
  "value": 3.2
} 
```

You can also obtain the values of all attributes of the "LeBistro" restaurant in a single shot:

```
GET <cb_host>:<cb_port>/v2/entities/LeBistro/attrs?type=Restaurant
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL
```

getting a JSON response such as the following one:

```
{
  "name": {
    "type": "Text",
    "value": "Le Bistro"
  },
  "average_scoring": {
    "type": "Float",
    "value": 4.2
  },
  "location": {
    "type": "geo:point",
    "value": "40.419697, -3.691281"
  },
  "postal_address": {
    "type": "Address",
    "value": {
      "street": "Calle Alcala 57",
      "city": "Madrid",
      "country": "Spain"
    }
  },
  "cousine_type": {
    "type": "Text",
    "value": "french"
  }
}
```

Note the postal_address attribute. NGSI attribute values are not limited to simple types as strings, numbers, etc. You can use JSON objects or arrays also as attribute values.

Alternatively, if you want to get a more compact response including **only attribute values**, you can use the *keyValues* format in the following way

```
GET <cb_host>:<cb_port>/v2/entities/LeBistro?type=Restaurant&options=keyValues
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL
```

getting a JSON response such as the following one:

```
{
  "name": "Le Bistro",
  "average_scoring": 4.2,
  "location": "40.419697, -3.691281",
  "postal_address": { 
    "street": "Calle Alcala 57", 
    "city": "Madrid",
    "country": "Spain"
  },
  "cousine_type": "french"
}
```

Finally, the Context Broker GE allows queries using filters. For example, in order to get all the restaurants whose average scoring is greater than 4, the following operation can be used:

```
GET /v2/entities?type=Restaurant&q=average_scoring>4
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL
```


# Subscribe to data updates

Apart from getting information using queries in a synchronous way (as illustrated in the “How to update and query context information” section above), **Context Consumers** can also get context information in an asynchronous way using notifications. In this scenario, the Context Broker GE is “programmed” to send notifications upon given conditions (specified in the subscription request).

In the case of NiceEating, the application backend could use a subscription so each time a new rating is cast by any user, the backend gets notified (in order to recalculate restaurant average score and publish it back in the Context Broker GE).

```
POST <cb_host>:<cb_port>/v2/subscriptions
Content-Type: application/json
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL

{
  "description": "New ratings subscription",
  "subject": {
    "entities": [
      {
        "idPattern": ".*",
        "type": "Rating"
      }
    ],
    "condition": {
      "attrs": [ "score" ]
    }
  },
  "notification": {
    "http": {
      "url": "http://backend.niceeating.foo.com:1028/ratings"
    },
    "attrs": [ "score" ]
  }
}
```

Another case would be an application that subscribes to changes in average ratings of a given restaurant. This may be useful for restaurant owners in other to know how their restaurants score is evolving.

```
POST <cb_host>:<cb_port>/v2/subscriptions
Content-Type: application/json
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL

{
  "description": "Average ratings changes subscription",
  "subject": {
    "entities": [
      {
        "idPattern": ".*",
        "type": "Restaurant"
      }
    ],
    "condition": {
      "attrs": [ "average_scoring" ]
    }
  },
  "notification": {
    "http": {
      "url": "http://myapp.foo.com:1028/restaurant_average_scorings"
    },
    "attrs": [ "average_scoring" ]
  }
}
```

# Geolocated data queries 

One very powerful feature in Context Broker GE is the ability to perform geo-located queries. You can query entities located inside (or outside) a region defined by a circle or a polygon.

For example, to query for all the restaurants within 13 km of the Madrid city center (identified by GPS coordinates 40.418889, -3.691944) a Context Consumer application will use the following query:

```
GET /v2/entities?type=Restaurant&georel=near;maxDistance:1000&geometry=point&coords=40.418889,-3.691944
Fiware-service: smartown
Fiware-servicepath: /NiceEating
X-Auth-Token: D3wiv5j7y6oiwo9w4ds5u20Y0bb6pL
```

# In more detail ...

This brief introduction shows only a reduced subset of Context Broker features. Additional features are: 

* Pagination
* Type browsing
* More filters (apart from the "greater than" filter described in this document)
* Creation and modification dates support
* Metadata
* Registrations and context providers

You can get more information about the FIWARE component providing this functionalty, reference API documentation and source code at [Context Broker (Orion)](context_broker.md).
