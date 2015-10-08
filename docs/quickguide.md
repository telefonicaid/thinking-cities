# Step 0 - Credentials

Please, request your credentials. You will receive an email containing these fields that are required for the following steps:

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>Field name</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>{{apikey}}      </td>
    <td>API Key used for devices to send data</td>
  </tr>
  <tr>
    <td>{{Fiware-Service}}      </td>
    <td>Service name</td>
  </tr>
  <tr>
    <td>{{Fiware-ServicePath}}      </td>
    <td>Sub-service name</td>
  </tr>
  <tr>
    <td>{{user-name}}      </td>
    <td>User name for web portal</td>
  </tr>
  <tr>
    <td>{{user-password}}      </td>
    <td>Password for web portal</td>
  </tr>
</table>
</p>

In order test the API, we really recommend you use the following sample
collection for [*POSTMAN extension for Google
Chrome*](https://chrome.google.com/webstore/detail/postman/fhbjgbiflinjbdggehcddcbncdddomop),
all this guide samples and some additional operations are there.

[*https://dl.dropboxusercontent.com/u/107902873/ttcloud/ttcloud-gettingstarted.zip*](https://dl.dropboxusercontent.com/u/107902873/ttcloud/ttcloud-gettingstarted.zip)

Please, remember to create a POSTMAN environment with your credentials for this POSTMAN collection.

# Step 1 - Send data

Before doing any coding, identify the sensors on your device and decide
which data do you want to send to Thinking Things Cloud.

Sending data is as simple as sending an HTTP POST request using your API
key with your measures. Please use “\#” and “|” separators to split data
and measures.  

Please remember that you will need to send as query parameters on the URL your {{apikey}} and your device identifier. 

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP method</th>
    <td>POST</td>
  </tr>
  <tr>
    <th>URL</th>
    <td>http://test.ttcloud.net:8082/iot/d?k={{apikey}}&i=mydevice</td>
  </tr>
  <tr>
    <th>HTTP Headers</th>
    <td>-</td>
  </tr>
  <tr>
    <th>HTTP Body</th>
    <td>t|15.5\#p|1015\#g|40.516304/-3.661756\#a|"some-text"\#h|33.5</td>
  </tr>
</table>
</p>


Take into account that no device provisioning or data modeling is
required in advance to send your device data. Anyway, we recommend to
use short magnitude identifiers (like "t", "p" on the sample) to reduce
the message length sent from the device.

Your will simply receive an HTTP 200 OK response to confirm the data was
properly received at Thinking Things Cloud.

# Step 2 - See data

Your device data is now stored in the Thinking Things Cloud, and your
can see it on the web portal. Please access the web portal with your
Username, Password and Fiware-Service:

[*http://test.ttcloud.net:8008/\#/*](http://test.ttcloud.net:8008/#/)

Now you have to switch to your subservice {{Fiware-ServicePath}} at
right top switch:

![](media/image05.png)

After that, you will see your device data the Entities list:

![](media/image01.png)

### Step 3 - Get data

Now your know your data is stored in the Thinking Things Cloud, lets get
it via API.



Now you are ready to invoke the API to get your device data. Just do an
HTTP GET request like this:

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP method</th>
    <td>GET</td>
  </tr>
  <tr>
    <th>HTTP headers</th>
    <td>Accept: application/json ; Fiware-Service: {{Fiware-Service}} ; Fiware-ServicePath: {{Fiware-ServicePath}} ;     X-Auth-Token: {{user-token}}</td>
  </tr>

</table>
</p>



You will get you device data in a json document like this that is FIWARE
NGSI compliant:

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP response code</th>
    <td>200</td>
  </tr>
  <tr>
    <th>HTTP response headers</th>
    <td>Content-Type: application/json</td>
  </tr>
  <tr>
    <th>HTTP Body</th>
    <td>
      {
                "contextElement": {
                         "type": "device",
                          "isPattern": "false",
                          "id": "mydevice",
                         "attributes": [
                          {
                         "name": "TimeInstant",
                          "type": "ISO8601",
                          "value": "2015-06-25T14:13:54.953107"
                          }
                          [...]
                          ]
                         },
                         "statusCode": {
                         "code": "200",
                         "reasonPhrase": "OK"
                         }
      }    
      </td>
  </tr>
</table>
</p>

Please, notice that you will see one attribute per sensor.


### Step 4 - Show in a dashboard

JSON documents are fine, but dashboards are better for humans. If you
want to create a responsive website to see your device data at Thinking
Things Cloud don’t do it from the scratch, just sign up at:

> [*https://freeboard.io/signup*](https://freeboard.io/signup)

After this, you will be able to create a dashboard:

![](media/image02.png)

Once your are at your new dashboard, create an Orion FIWARE datasource
per device.

![](media/image04.png)

On the FIWARE Orion datasource configuration introduce there your credentials and your device type and ID as retrieved on Step 3 from the Thinking Things Cloud API.



![](media/image07.png)

Now it’s time to add widgets to your dashboard, representing your device
data.

Anyway, you can always clone this sample freeboard and use it as a template for yours:


[*https://freeboard.io/board/69lZ9V*](https://freeboard.io/board/69lZ9V)

### Step 5 - Send commands 

You can send commands to the device in order to trigger any action like
turning on a LED or a relay. In order to do so, you need to set your
device at the website.

First, create a new device on the “Devices” management tab:

![](media/image00.png)

After that, register a new command for the device taking into account:

-   Endpoint: if your device has a public IP, we will push the commands
    > to that URL. Otherwise, the commands can be polled.

-   Command value: it must follow these convention
    > device\_id@{{command\_name}}|%s

![](media/image03.png)

Remember that you can also setup your device via API, you will find an
example on the POSTMAN collection.

Once the command is configured, you can send commands to the device just
updating the entity attribute associated to that command, or using the
web interface.

On the Entities section, select the entity linked to that device and
click on the “Send Command” green button to submit it.

![](media/image06.png)

The commands will be received on the device endpoint if configured on
the device setup. Remember that if you left that field empty, the
commands can be pulled directly from the device:

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP method</th>
    <td>GET</td>
  </tr>
  <tr>
    <th>URL</th>
    <td>http://test.ttcloud.net:8082/iot/d?k={{apikey}}&i=mydevice</td>
  </tr>
  <tr>
    <th>HTTP headers</th>
    <td>-</td>
  </tr>
</table>
</p>


The command received will be as follows:

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP Response code</th>
    <td>200</td>
  </tr>
  <tr>
    <th>HTTP Body</th>
    <td>device\_id@{{command\_name}}|{{comand\_params}}</td>
  </tr>
</table>
</p>


# In more detail …

**Sending data**

Devices can provide data using many other protocols like MQTT or COAP,
it is also feasible to set different timestamps, configure customized
mappings between physical device and virtual entity, or provision
measures via HTTP GET. Read the full detail at:

More info: [Device API guide](device_api.md)

**Data API**

Thinking things data API is FIWARE NGSI compliant so it provides lots of
features: subscriptions, partial updates, bulk entities retrieval,
regular expression queries, geolocated entities search.

[*http://es.slideshare.net/fermingalan/fiware-managing-context-information-at-large-scale*](http://es.slideshare.net/fermingalan/fiware-managing-context-information-at-large-scale)

More info: [Data API guide](data_api.md)

**Historic Data API**

Historic data is accessible using the Short Term historic API.


** RAW data query example **

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP method</th>
    <td>GET</td>
  </tr>
  <tr>
    <th>URL</th>
    <td>http://test.ttcloud.net:8666/STH/v1/contextEntities/type/device/id/device:mydevice/attributes/h?lastN=10</td>
  </tr>
  <tr>
    <th>HTTP headers</th>
    <td>Accept: application/json ; Fiware-Service: {{Fiware-Service}} ; Fiware-ServicePath: {{Fiware-ServicePath}} ; X-Auth-Token: {{user-token}}</td>
  </tr>
</table>
</p>


**Aggregated data (sum/hourly) query example **

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP method</th>
    <td>GET</td>
  </tr>
  <tr>
    <th>URL</th>
    <td>
    http://test.ttcloud.net:8666/STH/v1/contextEntities/type/device/id/device:mydevice/attributes/h?aggrMethod=sum&aggrPeriod=hour&dateFrom=2015-02-22T00:00:00.000Z&dateTo=2016-01-22T23:00:00.000Z
    http://test.ttcloud.net:8666/STH/v1/contextEntities/type/device/id/device:mydevice/attributes/h?lastN=10</td>
  </tr>
  <tr>
    <th>HTTP headers</th>
    <td>Accept: application/json ; Fiware-Service: {{Fiware-Service}} ; Fiware-ServicePath: {{Fiware-ServicePath}} ; X-Auth-Token: {{user-token}}</td>
  </tr>
</table>
</p>


>> Remember that in order to collect historic data, it is necessary to
>> configure the required subscription (endpoint:
>> http://test.ttcloud.net:8666/notify).

More info: [Historic Data API](historicdata_api.md)

** New accounts and subservices **

You can create new users and credentials at the web portal. It is also
possible to have different subservices in order to segment properly your
data.

More info: [Management API](management_api.md)

**Data visualization tools**

Do you need to show your devices on a map? want your data in a Google
Sheet? prefer to use Ducksboard for creating dashboards? Take a look to
our set of data visualization tools connectors:

[*https://github.com/telefonicaid/fiware-dataviz*](https://github.com/telefonicaid/fiware-dataviz)

**Getting more API tokens**

With your credentials we gave you a token, but you may need more.

To do so, you can login in the Authentication API to get a new token:

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP method</th>
    <td>POST</td>
  </tr>
  <tr>
    <th>URL</th>
    <td>http://test.ttcloud.net:5001/v3/auth/tokens</td>
  </tr>
  <tr>
    <th>HTTP Headers</th>
    <td>Content-Type: application/json</td>
  </tr>
  <tr>
    <th>HTTP Body</th>
    <td>
      {
            "auth": {
                     "identity": {
                     "methods": [
                     "password"
                     ],
                     "password": {
                     "user": {
                     "domain": {
                     "name": "{{Fiware-Service}}"
                     },
                     "name": "{{user-name}}",
                     "password": "{{user-pass}}"
                     }
                     }
                     },
                     "scope": {
                     "domain": {
                     "name": "{{Fiware-Service}}"
                     }
                     }
                     }
                     }
    </td>
  </tr>
</table>
</p>


You will receive an HTTP 201 Created response with a header called
X-Subject-Token, this is your {{user-token}} like this:

<p>
<table cellpadding="10", border="1" >
  <tr>
    <th>HTTP response code</th>
    <td>201 Created</td>
  </tr>
  <tr>
    <th>HTTP response headers</th>
    <td>Content-Type: application/json ; Vary: X-Auth-Token ; X-Subject-Token: {{user-token}}</td>
  </tr>
  <tr>
    <th>HTTP Headers</th>
    <td>Content-Type: application/json</td>
  </tr>
  <tr>
    <th>HTTP Body</th>
    <td>
    {
       "token": {
               "domain": {
               "id": "67576fe70df44bc280da74916a58d0f1",
               [...]
               "issued\_at": "2015-07-03T07:43:42.517728Z"
               }
        }
    }     
    </td>
  </tr>
</table>
</p>

Please, be careful pasting your {{user-token}} properly on next steps.
This is your API token and it will be valid for 3 years.
