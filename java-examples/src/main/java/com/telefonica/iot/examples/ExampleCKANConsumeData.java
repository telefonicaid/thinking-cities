/* Copyright 2018 Telefónica Soluciones de Informática y Comunicaciones de España, S.A.U.
 * All rights reserved.
 */

package com.telefonica.iot.examples;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;

import java.io.IOException;

/**
 * Ejemplo de consumo de datos de CKAN (Open Data), en el contexto de un vertical de
 * gestión de residuos urbanos (limpieza).
 *
 * CKAN permite diversos tipos de datasets y recursos asociados. En el contexto de este
 * ejemplo, hemos elegido un resource de tipo DataStore, asumiendo que dicho DataStore
 * ya tendrá registros a recuperar. Los procedimientos mediante los cuales dichos
 * registros han sido insertados en el DataStore quedan fuera del alcance de este
 * ejemplo (típicamente, habrán sido insertados por Cygnus a partir del stream de
 * notificaciones recibidas desde el Context Broker).
 *
 * En concreto, el recurso estará asociado a una entidad CONTAINER01 de tipo
 * WasteContainer con atributos según el modelo semántico descrito con detalle como
 * modelo armonizado FIWARE en:
 *
 * http://fiware-datamodels.readthedocs.io/en/latest/WasteManagement/WasteContainer/doc/spec/index.html
 *
 * Así mismo, el ejemplo asume simplificaciones con respecto a un caso real, a fin de
 * hacerlo más claro e ilustrativo, pe. certificados sin autofirmar, no se realiza
 * control de errores de I/O, etc.
 */
public class ExampleCKANConsumeData {

  public static void main(String[] args) throws IOException {

    System.out.println("<<<< CKAN CONSUME EXAMPLE >>>>");

    // Se asume que conocemos de antemano las credenciales a utilizar en CKAN y el
    // ID del resource
    String apiKey = "3db1cdae-7777-4eeb-8888-8de485ca0136";
    String resourceId = "77cac516-8888-173b-bd1a-0116186390e2";

    // Paso 1: obtención de los datos haciendo uso de la API de CKAN

    CloseableHttpClient httpClient = ExampleCommons.getHttpClient();

    // Hacemos uso del método "datastore_search" para obtener los registros

    String url = "https://platformhost:8446/api/3/action/datastore_search?" +
        "resource_id=" + resourceId;
    HttpGet reqGet = new HttpGet(url);
    reqGet.addHeader("authorization", apiKey);

    // La respuesta a la petición es un array de registros dentro del elemento
    // "records", a su vez dentro del elemento "result" de la respuesta. En este punto
    // se realizaría el procesado de los datos, pero, a efectos de ejemplo, aquí
    // simplemente lo imprimimos por la salida estándar
    HttpResponse response = httpClient.execute(reqGet);
    JSONArray data = ExampleCommons.extractJsonObject(response).
            getJSONObject("result").
            getJSONArray("records");

    System.out.println(data.toString(2));

    // Salida esperada (2 registros, por brevedad).
    //
    // Cada registro tiene un campo "recvTime" con la marca de tiempo, una
    // identificación de la entidad ("entityId" y "entityType") y el subservicio al que
    // pertenece ("fiwareServicePath"). El resto de los campos se corresponden con los
    // atributos, existiendo dos campos por atributo: uno para el valor
    // (pe. "fillingLevel") y otro acabado en "_md" para los metadata
    // (pe. "fillingLevel_md", en este caso ninguno de los atributos tiene metadata con
    // los que todos estos campos están a null).
    //
    //  [
    //    {
    //      "recvTime": "2018-07-24T15:06:48.719Z",
    //      "entityType": "WasteContainer",
    //      "location_md": null,
    //      "fiwareServicePath": "/gardens",
    //      "entityId": "CONTAINER01",
    //      "category_md": null,
    //      "storeWasteKind": "plastic",
    //      "storeWasteKind_md": null,
    //      "location": "42.236762,-8.715371",
    //      "_id": 1,
    //      "category": "ground",
    //      "fillingLevel_md": null,
    //      "fillingLevel": 60
    //    },
    //    {
    //      "recvTime": "2018-07-24T15:13:23.423Z",
    //      "entityType": "WasteContainer",
    //      "location_md": null,
    //      "fiwareServicePath": "/gardens",
    //      "entityId": "CONTAINER01",
    //      "category_md": null,
    //      "storeWasteKind": "plastic",
    //      "storeWasteKind_md": null,
    //      "location": "42.236762,-8.715371",
    //      "_id": 2,
    //      "category": "ground",
    //      "fillingLevel_md": null,
    //      "fillingLevel": 70
    //    }
    //  ]

    httpClient.close();
  }
}
