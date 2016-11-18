#<a name="top"></a>Which historical backend must I use?
IoT Platform provides several backends where creating and maintaining historical datasets regarding the context information handled in real time. Among these backends we can find:

* [HDFS](http://hadoop.apache.org/docs/current/hadoop-project-dist/hadoop-hdfs/HdfsUserGuide.html), the [Hadoop](http://hadoop.apache.org/) distributed file system.
* [MicroStrategy](https://www.microstrategy.com), the BI component of the platform.
* [CKAN](http://ckan.org/), our Open Data platform.
* [STH Comet](https://github.com/telefonicaid/IoT-STH), a Short-Term Historic database built on top of [MongoDB](https://www.mongodb.org/).

Given such a wide variety of backends, the next question is: which one suites better to my use case? This will usually depend on these variables:

* **Amount of data**. If you could stimate the amount of data in the short and the mid/long term, that would be one of the most important decission factors.
* **Privacy concerns**. Maybe your data can be made public as Open Data; or not.
* **Desired analytics**. The main final purpose for storing data is to analyze it in order to get value-added insights.
* **Visualization tools**. Very related to the above, graphical tools are always welcome when dealing with large amounts of data.

Additionally, a fifth variable could be wether the persistence backend allows for **static data loading**. This means the backend is not only used for dynamically creating real context datasets, but other sources of static data of your own can be uploaded as well.

Thus, let's analyze all the possibilities from the above variables point of view.

##HDFS
This is the best choice if you don't want to care about the amount of data, since HDFS is the standar *the facto* when dealing with real Big Data. Based on a distributed cluster of machines, the HDFS storage capacity is the aggregation of all the cluster machines' capacity. Therefore, such a backend usually handles data volumnes in the range of the Terabyte; even more.

Nevertheless, HDFS is just a file system in charge of managing folders and (very large) files. So, don't expect for analysis or visualization tools. This is something that other tools in the Hadoop ecosystem must provide; in the case of the IoT Platofom, we can find the following ones:

* **MapReduce**.
* **Querying tools: Apache Hive and Apache Pig**.
* **Apache Hue**.

With regards to privacy, HDFS is a user-oriented tool, where data is protected based on Unix-like permissions. This means the data is at least only available to the user, while members in the same group than the user could have restricted the access, the same than the rest of the world. This is the case of the IoT Platform.

Finally, it must be said you can upload any static dataset of your own to HDFS. Then, this data can be analyzed independently of the context datasets created by the platform, or can be used together with them, for instance, for correlation purposes.

[Top](#top)

##MicroStrategy
This the Business Intelligence system for the IoT Platform. You should use it if your aim is to get business-oriented visualizations and analytics, since MicroStrategy comes with a wide variety of features:

* General data visualization.
* New data discovery.
* Geospatial visualizations.
* Advanced native analytical functions.
* Enterprise reports.
* Dashboards.

All the data must be stored in the Analytical Database (MySQL), which handles a private database per FIWARE service (i.e. a tenant of the platform).

[Top](#top)

##CKAN
If you want to make public your data, there is no doubt: CKAN is the persistence backend where to create your historical datasets since it is one of the most used Open Data platforms in the world. According to the [Open Knowledge Foundation](https://okfn.org/opendata/), making *open* your data means anyone is free to use, reuse and redestribute it. However, using CKAN does not mean all the publised data has to be public, being configurable in a per dataset basis.

Regarding storage capabilities, the data can be uploaded to the FileStore, a simple file manager, or to the DataStore, which is based on [PostgreSQL](http://www.postgresql.org).

Apart from the open data capabilities of CKAN, it must be highlighted the visualization tools it provides in the form of viewers (other [viewers](http://docs.ckan.org/en/latest/maintaining/data-viewer.html) could be added upon request):

* **Text View** and **Image View**. It simply displays the content of text and image files, respectively, within the FileStore. Special formats such as XML or Json have syntax highlighted.
* **Recline View**. This is the visualization extension used for context datasets created by the platform in the DataStore. The data can be displayed as a grid, as a 2-axis graph or as a geolocation map (if the data contains some geolocation data).

CKAN does not provide any analytical tool

And equals than HDFS, CKAN allows for uploading you own datasets (open or not).

[Top](#top)

##STH Comet
As its name denotes, this MongoDB-based backend is used for short-term historical persistence. By short-term must be understood months, days or even days.

It exposes a REST API for data querying. FIWARE service and FIWARE service path must be given in the form of Http headers, together with a authentication token. This means this is a tol for private consumption of data.

An interesting detail regarding STH Comet API that may lead you for choosing it is not only raw records can be retrieved; but aggregated ones as well, which can be considered analytics into some extent:

* Sum of a subset of samples.
* Square sum of a subset of samples.
* Maximum sample in a subset of samples.
* Minimum sample in a subset of samples.
* Number of samples 

Subsets are parameterized by certain range of time and a resolution.

No visualization tools are given with STH Comet. Nevertheless, its REST API could be integrated by a skilled developer with some tools such as [Grafana](http://grafana.org/).

No other data different than the one stored by the platform in MongoDB can be queried.

[Top](#top)

##Summary
|Backend|Capacity|Privacy|Analytics|Visualization|Static upload|
|---|---|---|---|---|---|
|HDFS|Scalable (1)|Per Unix user and group (2)|Apache MapReduce (3)<br>Apache Hive (3)<br>Apache Pig (3)|Apache Hue's FileBrowser (3)|Yes|
|MicroStrategy|?|Per database (4)|Many algorithms|Many dashboards|No|
|CKAN|?|Per dataset (5)|-|CKAN text view<br>CKAN image view<br>CKAN recline view|Yes|
|STH Comet|?|Per FIWARE service and FIWARE service path|Raw and aggregated querying API|-|No|

(1) Having an initial capacity, our commitment is to scale upon client needs.
<br>
(2) For each FIWARE service and FIWARE service path, there may be one or more Unix users.
<br>
(3) Not natively provided by HDFS, but by Hadoop stack.
<br>
(4) A database is handled per each FIWARE service.
<br>
(5) A dataset belongs to a CKAN organization, whose name is equels to the FIWARE service the data belongs to. Someting similar occurs with the dataset name, whose name is the concatenation of the FIWARE service and FIWARE service path.

[Top](#top)
