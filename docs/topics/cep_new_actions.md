# How to add new actions to CEP

The IoT platform [CEP](../cep.md) comes with a set of actions, introduced in the [CEP API documentation](../cep_api.md).

This set is enhanced from time to time adding new actions, according to IoT platform development roadmap.
However, it may happen that at a given moment of time, the current actions set is not enough for your use case 
and you need new actions. In that case you have basically two options:

* Develop a software module encapsulating your action and connect it with the 
  [generic HTTP POST action](https://github.com/telefonicaid/perseo-fe/blob/1.9.0/documentation/plain_rules.md#http-request-action). 
  That is basically, a module exposing a REST API that CEP can invoke by the means of HTTP 
  POST requests. When your module receives the POST call from CEP then the actual action is executed. Note that HTTP POST
  requests can be parametrized using information from the events triggering the rule, so this alternative can be pretty
  flexible. 
* Request the development of the new action as part of the CEP actions set. In this case, please [fill 
  a new issue](https://github.com/telefonicaid/perseo-fe/issues/new) in the CEP repository using the title 
  "New Action Request" and providing a detailed description of the action you are proposing. The IoT platform 
  team will consider your request to be included a next version of the platform. Note that CEP is open source, so 
  if you could provide the code corresponding to the action you are proposing in the form of 
  a [pull request](https://help.github.com/articles/about-pull-requests) to the repository then you could ease 
  the introduction of the new rule in next platform version.
  