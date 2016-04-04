## Development of context-aware applications ##

This components provides you with means to produce, gather, publish and consume context information at large scale and exploit it to transform your application into a truly smart application.  

Context information is represented through values assigned to attributes that characterize those entities relevant to your application. The Context Broker is able to handle context information at large scale by implementing standard REST APIs.  

![](media/cb1.png)

Context information may come from many different sources:

- Already existing systems
- Users, through mobile apps
- Sensor networks

One of the most important features of the Context Broker is that it allows to model and gain access to context information in a way that is independent from the source of that information. As an example, your application may need to be aware about “Places” (identified by their specific geographical coordinates) and the “temperature” in them.  The way in which the temperature of a given place is obtained may vary from place to place.  Thus, the temperature of a given street may be measured through temperature sensors deployed in that street, while in another street it may be obtained through temperature sensors deployed on buses that circulate through the street and even in some other streets may be obtained through users who report the temperature using their smartphones.  Using the RESTAPI exported by the Context Broker GE, the way in which your application will be able to query the temperature of places, or subscribe to changes on temperatures of places will be the same no matter which is the source of a temperature and it will not vary if the source of the temperature for a given place changes over time (e.g., the temperature of a given street turns to be measured through temperature sensors deployed on the streets rather than through buses equipped with sensors).

![](media/cb2.png)

## API Reference Documentation ##

- [API V1 Walkthrough](http://fiware-orion.readthedocs.org/en/develop/user/walkthrough_apiv1/index.html)
- API V2 - ongoing, not yet suitable for production
  - [Cookbook](http://telefonicaid.github.io/fiware-orion/api/v2/cookbook/)
  - [Full reference](http://telefonicaid.github.io/fiware-orion/api/v2/)


