# java-examples

Ejemplos escritos en Java que hacen uso de la plataforma ThinkingCities.

Requisitos:

* Java SDK. Como referencia, se ha usado el SDK de Java 10, pero cualquier otro a partir de Java 8 debería funcionar.
* Maven >= 3.3.9

Para construir el proyecto:

```
mvn package
```

Para ejecutar los distintos tests:

```
mvn exec:java@<test_name>
```

Donde `<test_name>` es alguno de los siguientes:

* `cb-consume`: consumo de datos mediante API del ContextBroker
* `cb-insert`: inserción de datos mediante API de ContextBroker
* `ckan-consume`: consumo de datos de CKAN
* `ckan-insert`: inserción de datos en CKAN
* `hive-consume`: consumo de datos del BigData vía Hive
* `iota-insert`: inserción de datos mediante IoT Gateway
* `sth-consume`: consumo de datos mediante API de STH

Ejemplo:

```
mvn exec:java@cb-consume
```

Es posible ejecutar varios test (o todos ellos) en secuencia añadiéndolos a la lína de mvn, eg:

```
mvn exec:java@cb-consume exec:java@cb-insert 
```

Consideraciones adicionales:

* Los ejemplos se sitúan dentro del ámbito de un vertical de gestión de residuos (limpieza) en base al modelo semántico establecido por FIWARE para el mismo. En concreto, se utiliza el modelo armonizado de WasteContainer (contenedor de basura) cuya especificación se encuentra en: http://fiware-datamodels.readthedocs.io/en/latest/WasteManagement/WasteContainer/doc/spec/index.html 
* Cada ejemplo es autocontenido (con la salvedad del uso que se hace de la librería ExampleCommons, ver más abajo) y es directamente ejecutable, si bien el servicio y subservicio utilizado ("smartgordon" y "/gardens" respectivamente) se muestran a modo de ejemplo y no están realmente provisionados. También es preciso utilizar una nombre de host o IP valido ("platformhost" en los ejemplos).
* Los ejemplos asumen simplificaciones con respecto al caso real, a fin de hacerlos más claros e ilustrativos. Por ejemplo, no se utiliza un fichero de configuraciones y todos los parámetros van sobre el propio código fuente, la password va en claro, se aceptan certificados autofirmados, programación realizada íntegramente en base a clases estáticas, no se realiza control de errores de I/O, etc.
*	A fin de hacer más simples los ejemplos y focalizarlos en los casos de uso objetivo, se hace uso de una clase estática con métodos de utilidad, denominada ExampleCommons.
*	Los ejemplos compilan y ejecutan usando Java 1.8, si bien serían fácilmente trasladables a versiones anteriores o posteriores ya que no utilizan características especiales de versiones de Java. Para ello, habría que ajustar las propiedades `maven.compiler.source` y `maven.compiler.target` en el fichero pom.xml
