Complex Event Processing (CEP) module analyses event data in real-time, generates immediate insight and enables instant response to changing conditions (ie: if the Entity attribute “temperature” is over 25 send an email to me).

CEP APIs allows developers to manage rules exposing CRUD operations, and feed it with NGSI10 notifications to trigger those rules.

## In detail

Perseo CEP component is a distributed “Rules Engine”. The main purpose of this component is to analyse Entities data in Orion Context Broker and trigger events like sending an SMS to the operations & support team, emailing the business decision maker or tweeting to let customers know something cool about the service.
This component receives the events through a POST containing the JSON representation of the event and apply the rules so that they can generate appropriate actions by means of a POST request to another component with the appropriate information. The rules are also provisioned through a POST request with the expression of the rule in EPL.
EPL is the language of rules of [Esper](http://www.espertech.com/products/esper.php). Esper is the Java library that contains the rules engine and event processing logic. Basically the component is a wrapper for the Esper library, providing a REST API and a persistent context (in memory).
When an incoming event fired a rule, the values selected by the EPL statement are sent as a JSON to an URL set in the configuration file. This composed object is an event, derived from the input events. If more than one rule is fired, each one will generate its own “complex” event and will cause an HTTP POST to the predefined URL. This URL is the same regardless the rule or the complex event.

## Documentation and API

[CEP API](https://github.com/telefonicaid/perseo-fe/tree/1.0.3)

[Esper Documentation](http://www.espertech.com/esper/documentation.php)

