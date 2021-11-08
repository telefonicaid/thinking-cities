# Tips on how to model your project entities with the IoT Platform

There are some considerations to make when designing how to model a project in the platform. This document will give you
some hints to avoid most common mistakes (use them as hints to guide your modelling, not as strict rules).

## Modeling your IDs in the right way

Entity ids and attribute names should be like *real* IDs. In other words, using whitespaces, accents or
any other funny weird character in ID strings is a really bad idea. In fact, although that is allowed in the NGSIv1
API (due to legacy reasons), it is forbidden in the NGSIv2 version of the API (check the "Field syntax restrictions"
section in the [NGSIv2 specification document](http://telefonicaid.github.io/fiware-orion/api/v2/stable) for details).

Why this is a bad idea? There are several reasons:

* Take into account that the IoT Platform would use that identifiers (or strings derived from that identifiers) in
  places where such characters are not allowed. For example, some persistence backends are based in databases which 
  doesn't accept whitespaces or non-ASCII characters in table databases.
* IDs may appear as part of URLs (e.g. the URL identifying an entity at [Context Broker](../context_broker.md) and
  using non-ASCII characters in that places makes these URL more complex.

Sometimes you may think you need to use ids with whitespaces and non-ASCII characters to render that information
correctly, e.g. in a graphic-user interface. For instance, you have an entity that you want to show as
"Row 12/Seat B" with an attribute "Occupation status" in your application and you may think that 
modeling in the following way is a good idea:


      {
         "type": "Seat",
         "isPattern": "false",
         "id": "Row 12/Seat B",
         "attributes": [
           {
             "name": "Occupation status",
             "type": "String",
             "value": "occupied"
           },
           ...
           }
         ]
      }
 
However, it is not a good idea. If you need descriptive texts for entities or attributes, then use specific 
attributes and metadata for them respectively, which values are not ids and doesn't have any of the problems 
described above. Taking that into account, you could model in the following for the example above:

      {
         "type": "Seat",
         "isPattern": "false",
         "id": "Row12SeatB",
         "attributes": [
           {
             "name": "description",
             "type": "String",
             "value": "Row 12/Seat B"
           },
           {
             "name": "status",
             "type": "String",
             "value": "occupied",
             "metadata": [
               {
                 "name": "description",
                 "type": "String",
                 "value": "Occupation status"
               }
             ]
           },
           ...
           }
         ]
      }

As a general guideline, you should use identifiers with the following properties:

* They **must** be unique: It's better to have globally unique IDs if that's possible, but, for the cases 
  where they aren't, they should be at least unique at the service level. It's also important to design 
  the process of ID assignment so that the probability of generating an ID collision is as lower as 
  possible (i.e.: it's better to have a 16 bytes hexadecimal UUID than an 8bit integer).

* They **should** never change (or do it under extraordinary circumstances): the ID uniquely identifies 
  your entities, and not only the Context Broker, but potentially multiple other systems may use it 
  to identify objects associated to it (e.g. this specially affects the persistence backends). That
  turns any change in the ID into a potential migration of data in multiple systems, with it associated
  (usually very large) costs.

* They **should not** be tied to the data: as that bound would make it easier to brake any of the
  two first rules. Even if you are completely sure that identifying your users with their Driver Licenses 
  is unique and immutable, chances are that the Government choose to change it; use a UUID instead. 
  That will ensure uniqueness and, since the UUID only belongs to the system, you will be the one 
  who decides when and how it may change (if it is allowed to do it at all). However, note that we 
  are not using UUID in this documentation for didactic reasons but in real usage use case 
  you should consider this recommendation.
  
* (*) They **should not** use the underscore (`_`) character: although accepted by context broker, it is a
  bad idea using it as part of your IDs since the persistence backends use the underscore too for special
  purposes. On the one hand, it is used as concatenator character. On the other hand, it is used as
  replacement character when a character within the ID is not accepted by the persistence backends.
  
* (*) They **should** avoid using uppercase letters when using PostgreSQL-based persistence backends, e.g.
  CKAN (or Carto in the future): they usually convert uppercase letters into lowercase. This means IDs
  such as `Car` and `car` will be different at context broker level, but the same at persistence backend
  level. Of course, if you are not considering using PostgreSQL-based persistence backends, ignore this
  advice.

The above consideration applies to entity ids and attribute names but also to other pieces of context 
information which take the role of an ID, in particular to entity types, attribute types, metadata names and 
metadata types.

(*) This guideline won't make sense once the new encoding is enabled in the IoT Platform. Such a new
encoding uses a concatenator different than underscore and not accepted characters (including uppercase letters) are encoded following Unicode format.

## The IoT Platform is centered in context

The central piece of the IoT Platform is the Context Broker: a component that lets you store and query information
about context entities. But, what is a Context Entity? Context Entities are logical abstractions that represent the elements
of your system. Some examples are:

* If you are modelling a Smart City, you may have entities like: Streetlight, Garden, Bus, Street, RecycleBin...
* Modelling a Industry you may find useful entities like: Worker, Machine, Product, and so on.
* When designing a Smart Parking you will have entities like: Car, User, Floor...

What does it mean that those objects are Context Entities (instead of classes, objects, tables, or documents). The
difference lays in how it is treated around the platform: modifications of the CEs can be observed by subscriptions,
the history of those changes stored at [persistence backends](../cygnus.md) (HDFS, CKAN, MySQL) and [STH](../sth.md),
rules can be defined to modify the global Context based on CEs changes with [CEP](../cep.md)...

All the given examples have several things in common:

* All of them model nouns: Context Entities are subjects of interactions; they have a history, they may have subscriptions
to their changes, etc. This behavior does not fit well with processes (and things typically modelled as verbs or actions).

* All those entities are expected to have a long life in the system. Entities are not supposed to model
transient information (e.g.: support tickets, mails, log files). This doesn't mean short-lived entities can't be modelled
in the system, but having those kind of entities in your project will cause several operational issues in the platform.

## The Platform is not a ticketing system

Another common mistake involves the use of the Platform for short-lived ticketing, as in the following scenarios:

* A system that keeps track of alarms risen by sensors, and whether they have been solved or not. Modelled entities are:
Sensor and Alarm.

* A booking system for Theater seats, modelling each ticket as a Context Entity. Modelled entities are: Seat and Ticket.

In both cases there is a Context Entity that does not comply with the above rules about Context Entities: Ticket and Alarm.
Both entities model short lived elements that will be created once and again in the system, cluttering your service with
old information.

Objects like tickets, even being nouns, model interactions between entities in your system instead of modelling contextual
elements: a ticket is a temporal association between a user (that may go once and again to the same theater) and the seat
(where he will be seated in a particular play). A more convenient way to model this kind of information would be to
directly code it in the seat, as in the following example (NGSIv1):

      {
         "type": "Seat",
         "isPattern": "false",
         "id": "Row12SeatB",
         "attributes": [
           {
             "name": "status",
             "type": "String",
             "value": "occupied"
           },
           {
             "name": "play",
             "type": "String",
             "value": "Richard III"
           },
           {
             "name": "audience",
             "type": "Person",
             "value": {
                "name": "Bob Smith"
             }
           }
         ]
      }

or the same entity in NGSIv2 format:

      {
         "type": "Seat",
         "id": "Row12SeatB",
         "status": {
             "type": "String",
             "value": "occupied"
         },
         "play": {
             "type": "String",
             "value": "Richard III"
         },
         "audience": {
             "type": "Person",
             "value": {
                "name": "Bob Smith"
           }
         }
      }

With this modelling, the Context Broker will be storing the actual picture of the Theater: which seats have been sold and
which seats haven't. The history of each seat could also be made available (by means of the STH and other history
persistence backend), giving information of past plays. And all that, following the performance and modelling
recommendations.

This approach has some caveats, though:

* It cannot model future events (not easily, at least). Modelling future purchases for the same seat for diferent days
could be difficult in this situation.

* It doesn't allow to interact with past events (once again, not easily). If some of the information in the seat could be
 modified (e.g.: ratings by the audience, or payment receipt information) for past seats, it wouldn't be possible. The same
 goes for an alarm that was raised multiple times, if some new information arrives about a past alarm notification.

For all those situations where this kind of model cannot fit your problem, you may consider to manage that part of your
problem externally, through a ticketing system, or a specialized backend. The IoTPlatform offers multiple ways to
link your data in external systems with data in the platform, so you can still benefit from the Platform features, while
using specialized tools for particular processes (like support or purchases).

## Attributes are not collections

A commmon mistake when a beginner is modelling a problem in the Platform is: to see the structure of a Context Entity
(i.e.: objects with an identity that have collections of other objects, the attributes, that have names and values) (NGSIv1):

      {
         "type": "SensorMachine",
         "isPattern": "false",
         "id": "machine1",
         "attributes": [
           {
             "name": "attr1",
             "type": "String",
             "value": "val1"
           },
           {
             "name": "attr2",
             "type": "String",
             "value": "val2"
           },
           {
             "name": "attr3",
             "type": "String",
             "value": "val3"
           }
         ]
      }

or the same entity in NGSIv2 format:

      {
         "type": "SensorMachine",
         "id": "machine1",
         "attr1": {
             "type": "String",
             "value": "val1"
         },
         "attr2": {
             "type": "String",
             "value": "val2"
         },
         "attr3": {
             "type": "String",
             "value": "val3"
         }
      }

and think that it will fit any model that has an object containing a list of things, like the following one:

      {
         "type": "DepartmentWorkers",
         "isPattern": "false",
         "id": "RRHH",
         "attributes": [
           {
             "name": "Bob",
             "type": "User",
             "value": {
                "name": "Bob",
                "surname": "Smith",
                "ID": "3456"
             }
           },
           {
             "name": "Alice",
             "type": "User",
             "value": {
                "name": "Alice",
                "surname": "Johnson",
                "ID": "8744"
             }
           }
         ]
      }

or the same entity in NGSIv2 format:

      {
         "type": "DepartmentWorkers",
         "id": "RRHH",
         "Bob": {
             "type": "User",
             "value": {
                "name": "Bob",
                "surname": "Smith",
                "ID": "3456"
             }
         },
         "Alice": {
             "type": "User",
             "value": {
                "name": "Alice",
                "surname": "Johnson",
                "ID": "8744"
             }
         }
      }

This is a wrong model for this problem for several reasons:

* First of all, the querying and subscription features of the Context Broker are based on the hypothesis that each of your
model entities is going to be implemented as an independent Context Entity. Grouping information inside other entities
would end up in cumbersome queries and subscription logic (and it may even make impossible to execute some queries or
subscriptions).

* It is also a problem due to performance factors. The performance of some operations depend on the number of attributes
of the involved CEs. Making CEs with ever-growing number of attributes may end up damaging the performance of your project.

* It is also worth mentioning that there are physical limits to the size of entities in the Context Broker, so
evergrowing entities may not be possible to model, depending on their size.

Another scenario where we could find the same modelling problem would be the aforementioned Theater scenario. In that
scenario we could have thought that a nice model could be having a Theater Context Entity like the following (NGSIv1):

      {
         "type": "Theater",
         "isPattern": "false",
         "id": "LondonShakespeareTheater",
         "attributes": [
           {
             "name": "1-A",
             "type": "Seat",
             "value": {
                "status": "Occupied",
                "name": "Bob Smith"
             }
           },
           {
             "name": "1-B",
             "type": "Seat",
             "value": {
                "status": "Free"
             }
           },
           {
             "name": "1-C",
             "type": "Seat",
             "value": {
                "status": "Occupied",
                "name": "Alice Johnson"
             }
           },

           [...]
         ]
      }

or the same entity in NGSIv2 format:

      {
         "type": "Theater",
         "id": "LondonShakespeareTheater",
         "1-A": {
             "type": "Seat",
             "value": {
                "status": "Occupied",
                "name": "Bob Smith"
             }
         },
         "1-B": {
             "type": "Seat",
             "value": {
                "status": "Free"
             }
         },
         "1-C": {
             "type": "Seat",
             "value": {
                "status": "Occupied",
                "name": "Alice Johnson"
             }
           },

         [...]
      }


This scenario has the same problems as the department worker scenario above. It would be best to model each of the Seats
as a Context Entity by itself, and model de Theater, either as a subservice holding all the information about the seats,
or as a sepparete Context Entity, having a link with their seats (through an attribute in the seat).