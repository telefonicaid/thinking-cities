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
 * Ejemplo de consumo de datos usando la API OMA NGSIv2 expuesta por el Context Broker,
 * en el contexto de un vertical de gestión de residuos urbanos (limpieza).
 *
 * Se asume que en el Context Broker de la plataforma ya existirá un conjunto de datos
 * a recuperar, quedando los procedimientos mediante los cuales dichos datos han llegado
 * al Context Broker fuera del alcance de este ejemplo. En concreto, se asume la
 * existencia de entidades de tipo WasteContainer (que son las recuperarán) según el
 * modelo semántico descrito con detalle como modelo armonizado FIWARE en:
 *
 * http://fiware-datamodels.readthedocs.io/en/latest/WasteManagement/WasteContainer/doc/spec/index.html
 *
 * Así mismo, el ejemplo asume simplificaciones con respecto a un caso real, a fin de
 * hacerlo más claro e ilustrativo, pe. certificados sin autofirmar, no se realiza
 * control de errores de I/O, etc.
 */
public class ExampleCBConsumeData {

  public static void main(String [] args) throws IOException {

    System.out.println("<<<< CB CONSUME EXAMPLE >>>>");

    // Paso 1: obtener un token del IDM
    String token = ExampleCommons.getToken("smartgondor", "/gardens", "user1", "abd123");
    System.out.println("Token: " + token);

    // Pase 2: obtención de los datos haciendo uso de OMA NGSIv2 del CB.

    CloseableHttpClient httpClient = ExampleCommons.getHttpClient();

    // NGSIv2 es una API flexible y permite múltiples modos de obtención de datos,
    // pe. filtros aritméticos, geo-queries, etc. En este caso haremos una petición que
    // obtenga todas las entidades de un tipo dado haciendo uso del parámetro ?type=
    //
    // Ver la especificación de NGSIv2 para mayor detalle:
    // http://telefonicaid.github.io/fiware-orion/api/v2/stable

    String url = "https://platformhost:10027/v2/entities?type=WasteContainer";
    HttpGet reqGet = new HttpGet(url);
    reqGet.addHeader("fiware-service", "smartgondor");
    reqGet.addHeader("fiware-servicepath", "/gardens");
    reqGet.addHeader("x-auth-token", token);

    // La respuesta a la petición es un array JSON con las entidades. En este punto se
    // realizaría el procesado de los datos, pero, a efectos de ejemplo, aquí
    // simplemente lo imprimimos por la salida estándar
    HttpResponse response = httpClient.execute(reqGet);
    JSONArray data = ExampleCommons.extractJsonArray(response);

    System.out.println(data.toString(2));

    // Salida esperada (array con tres entidades: CONTAINER01, CONTAINER02 y
    // CONTAINER03):
    //
    //  [
    //    {
    //      "storeWasteKind": {
    //        "metadata": {},
    //        "type": "Text",
    //        "value": "plastic"
    //      },
    //      "location": {
    //        "metadata": {},
    //        "type": "geo:point",
    //        "value": "42.236762,-8.715371"
    //      },
    //      "id": "CONTAINER01",
    //      "type": "WasteContainer",
    //      "category": {
    //        "metadata": {},
    //        "type": "Text",
    //        "value": "ground"
    //      },
    //      "fillingLevel": {
    //        "metadata": {},
    //        "type": "Number",
    //        "value": 23.24
    //      }
    //    },
    //    {
    //      "storeWasteKind": {
    //        "metadata": {},
    //        "type": "Text",
    //        "value": "glass"
    //      },
    //      "location": {
    //        "metadata": {},
    //        "type": "geo:point",
    //        "value": "42.234103,-8.721457"
    //      },
    //      "id": "CONTAINER02",
    //      "type": "WasteContainer",
    //      "category": {
    //        "metadata": {},
    //        "type": "Text",
    //        "value": "ground"
    //      },
    //      "fillingLevel": {
    //        "metadata": {},
    //        "type": "Number",
    //        "value": 42.51
    //      }
    //    },
    //    {
    //      "storeWasteKind": {
    //        "metadata": {},
    //        "type": "Text",
    //        "value": "organic"
    //      },
    //      "location": {
    //        "metadata": {},
    //        "type": "geo:point",
    //        "value": "42.234746,-8.719665"
    //      },
    //      "id": "CONTAINER03",
    //      "type": "WasteContainer",
    //      "category": {
    //        "metadata": {},
    //        "type": "Text",
    //        "value": "underground"
    //      },
    //      "fillingLevel": {
    //        "metadata": {},
    //        "type": "Number",
    //        "value": 79.84
    //      }
    //    }
    //  ]

    httpClient.close();
  }
}
