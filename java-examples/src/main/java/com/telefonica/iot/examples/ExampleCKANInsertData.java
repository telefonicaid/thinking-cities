/* Copyright 2018 Telefónica Soluciones de Informática y Comunicaciones de España, S.A.U.
 * All rights reserved.
 */

package com.telefonica.iot.examples;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;

/**
 * Ejemplo de inserción de datos en CKAN (Open Data), en el contexto de un vertical de
 * gestión de residuos urbanos (limpieza).
 *
 * CKAN permite diversos tipos de datasets y recursos asociados. En el contexto de este
 * ejemplo, hemos elegido un resource de tipo DataStore. Se asume que los datos ya
 * habrán sido extraídos de la fuente correspondiente, quedando dicho proceso de
 * extracción fuera del ámbito de este ejemplo. A efectos del ejemplo, insertaremos un
 * conjunto de registros, cuyos datos se encuentran en un JSON array de objetos cuyos
 * elementos son los campos del registro. Dichos registros (en lo que a entidad y
 * atributos respecta) son conformes al modelo semántico de WasteContainer, que se
 * encuentra descrito con detalle como modelo armonizado FIWARE en:
 *
 * http://fiware-datamodels.readthedocs.io/en/latest/WasteManagement/WasteContainer/doc/spec/index.html
 *
 * Así mismo, el ejemplo asume simplificaciones con respecto a un caso real, a fin de
 * hacerlo más claro e ilustrativo, pe. certificados sin autofirmar, no se realiza
 * control de errores de I/O, etc.
 *
 */
public class ExampleCKANInsertData {

  public static void main(String [] args) throws IOException {

    System.out.println("<<<< CKAN INSERT EXAMPLE >>>>");

    // Se asume que conocemos de antemano las credenciales a utilizar en CKAN y el
    // ID del resource
    String apiKey = "3db1cdae-7777-4eeb-8888-8de485ca0136";
    String resourceId = "77cac516-8888-173b-bd1a-0116186390e2";

    // Pase 1: insertar el registro haciendo uso de la API de CKAN
    //
    // A efectos del ejemplo y por simplicidad, los datos se cargan desde un fichero
    // (DataStoreRecord.json). En un caso real existiría un proceso de extracción desde
    // la fuente de datos correspondiente. En concreto se están utilizando registros
    // con un subconjunto de atributos (location, storeWasteKind, fillingLevel y
    // category) conformes al modelo semántico de WasteContainer.
    //
    // Se incluye a continuación el contenido del fichero en este comentario,
    // por comodidad:
    //
    //  [
    //    {
    //      "recvTime": "2018-07-24T16:13:23.423Z",
    //      "entityType": "WasteContainer",
    //      "fiwareServicePath": "/gardens",
    //      "entityId": "CONTAINER01",
    //      "storeWasteKind": "plastic",
    //      "location": "42.236762,-8.715371",
    //      "category": "ground",
    //      "fillingLevel": 70
    //    },
    //    {
    //      "recvTime": "2018-07-24T17:13:23.423Z",
    //      "entityType": "WasteContainer",
    //      "fiwareServicePath": "/gardens",
    //      "entityId": "CONTAINER01",
    //      "storeWasteKind": "plastic",
    //      "location": "42.236762,-8.715371",
    //      "category": "ground",
    //      "fillingLevel": 70
    //    }
    //  ]

    String recordToInsert = ExampleCommons.getFileAsString("DataStoreRecord.json");

    JSONObject request = new JSONObject();
    request.put("resource_id", resourceId);
    request.put("method", "insert");
    request.put("records", new JSONArray(recordToInsert));
    request.put("force", "True");

    CloseableHttpClient httpClient = ExampleCommons.getHttpClient();

    // Hacemos uso del método "datastore_upsert" para insertar los registros

    String url = "https://platformhost:8446/api/3/action/datastore_upsert";
    HttpPost reqPost = new HttpPost(url);
    reqPost.addHeader("authorization", apiKey);
    reqPost.setEntity(new StringEntity(request.toString()));

    HttpResponse response = httpClient.execute(reqPost);
    System.out.println("Status code: " + response.getStatusLine());

    httpClient.close();
  }
}
