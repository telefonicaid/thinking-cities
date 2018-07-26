/* Copyright 2018 Telefónica Soluciones de Informática y Comunicaciones de España, S.A.U.
 * All rights reserved.
 */

package com.telefonica.iot.examples;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Ejemplo de inserción de datos usando la API OMA NGSIv2 expuesta por el Context Broker,
 * en el contexto de un vertical de gestión de residuos urbanos (limpieza).
 *
 * Se asume que los datos ya habrán sido extraídos de la fuente correspondiente,
 * quedando dicho proceso de extracción fuera del ámbito de este ejemplo. A efectos del
 * ejemplo, los datos se encuentran en un JSON array cuyos elementos son conformes al
 * modelo semántico de WasteContainer. Dicho modelo se ecuentra descrito con detalle
 * como modelo armonizado FIWARE en:
 *
 * http://fiware-datamodels.readthedocs.io/en/latest/WasteManagement/WasteContainer/doc/spec/index.html
 *
 * Así mismo, el ejemplo asume simplificaciones con respecto a un caso real, a fin de
 * hacerlo más claro e ilustrativo, pe. certificados sin autofirmar, no se realiza
 * control de errores de I/O, etc.
 */
public class ExampleCBInsertData {

  public static void main(String [] args) throws IOException {

    System.out.println("<<<< CB INSERT EXAMPLE >>>>");

    // Paso 1: obtener un token del IDM
    String token = ExampleCommons.getToken("smartgondor", "/gardens", "user1", "abd123");
    System.out.println("Token: " + token);

    // Pase 2: lanzar la petición haciendo uso de OMA NGSIv2 del CB.
    //
    // A efectos del ejemplo y por simplicidad, los datos se cargan desde un fichero
    // (WasteContainers.json). En un caso real existiría un proceso de extracción desde
    // la fuente de datos correspondiente. En concreto se está utilizando un conjunto
    // de 3 contenedores, con un subconjunto de atributos (location, storeWasteKind,
    // fillingLevel y category) conforme al modelo semántico de WasteContainer.
    //
    // Se incluye a continuación el contenido del fichero en este comentario,
    // por comodidad:
    //
    // [
    //   {
    //     "id": "CONTAINER01",
    //     "type": "WasteContainer",
    //     "category": {
    //       "type": "Text",
    //       "value": "ground"
    //     },
    //     "fillingLevel": {
    //       "type": "Number",
    //       "value": 23.24
    //     },
    //     "location": {
    //       "type": "geo:point",
    //       "value": "42.236762,-8.715371"
    //     },
    //     "storeWasteKind": {
    //       "type": "Text",
    //       "value": "plastic"
    //     }
    //   },
    //   {
    //     "id": "CONTAINER02",
    //     "type": "WasteContainer",
    //     "category": {
    //       "type": "Text",
    //       "value": "ground"
    //     },
    //     "fillingLevel": {
    //       "type": "Number",
    //       "value": 42.51
    //     },
    //     "location": {
    //       "type": "geo:point",
    //       "value": "42.234103,-8.721457"
    //     },
    //     "storeWasteKind": {
    //       "type": "Text",
    //       "value": "glass"
    //     }
    //   },
    //   {
    //     "id": "CONTAINER03",
    //     "type": "WasteContainer",
    //     "category": {
    //       "type": "Text",
    //       "value": "underground"
    //     },
    //     "fillingLevel": {
    //       "type": "Number",
    //       "value": 79.84
    //     },
    //     "location": {
    //       "type": "geo:point",
    //       "value": "42.234746,-8.719665"
    //     },
    //     "storeWasteKind": {
    //       "type": "Text",
    //       "value": "organic"
    //     }
    //   }
    // ]

    String entitiesToInsert = ExampleCommons.getFileAsString("WasteContainers.json");

    JSONObject request = new JSONObject();
    request.put("actionType", "APPEND");
    request.put("entities", new JSONArray(entitiesToInsert));

    CloseableHttpClient httpClient = ExampleCommons.getHttpClient();

    // NGSIv2 es una API flexible y permite múltiples modos de inserción de datos. En
    // este caso, dado que tenemos 3 entidades que insertar, utilizaremos una operación
    // de lote (batch operation): POST /v2/op/update
    //
    // Ver la especificación de NGSIv2 para mayor detalle:
    // http://telefonicaid.github.io/fiware-orion/api/v2/stable

    HttpPost reqPost = new HttpPost("https://platformhost:10027/v2/op/update");
    reqPost.addHeader("content-type", "application/json");
    reqPost.addHeader("fiware-service", "smartgondor");
    reqPost.addHeader("fiware-servicepath", "/gardens");
    reqPost.addHeader("x-auth-token", token);
    reqPost.setEntity(new StringEntity(request.toString()));

    HttpResponse response = httpClient.execute(reqPost);
    System.out.println("Status code: " + response.getStatusLine());

    httpClient.close();
  }
}
