/* Copyright 2018 Telefónica Soluciones de Informática y Comunicaciones de España, S.A.U.
 * All rights reserved.
 */

package com.telefonica.iot.examples;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Ejemplo de consumo de datos usando la API del Short Term Historic (STH), en el
 * contexto de un vertical de gestión de residuos urbanos (limpieza).
 *
 * Se asume que en el STH ya existe un histórico de datos relativos a una entidad
 * oncreta, quedando la dinámica de actualizaciones que ha dado lugar al histórico fuera
 * del alcance de este ejemplo. En concreto, se asume la existencia de la entidad
 * CONATINER01 con datos para el atributo fillingLevel entre las 07:30am y las
 * 08:00am (GMT) del 18 de julio, conforme al modelo semántico descrito con detalle
 * como modelo armonizado FIWARE en:
 *
 * http://fiware-datamodels.readthedocs.io/en/latest/WasteManagement/WasteContainer/doc/spec/index.html
 *
 * Así mismo, el ejemplo asume simplificaciones con respecto a un caso real, a fin de
 * hacerlo más claro e ilustrativo, pe. certificados sin autofirmar, no se realiza
 * control de errores de I/O, etc.
 */
public class ExampleSTHConsumeData {

  public static void main(String [] args) throws IOException {

    System.out.println("<<<< STH CONSUME EXAMPLE >>>>");

    // Paso 1: obtener un token del IDM
    String token = ExampleCommons.getToken("smartgondor", "/gardens", "user1", "abd123");
    System.out.println("Token: " + token);

    // Pase 2: obtención de los datos haciendo uso de la API del STH

    CloseableHttpClient httpClient = ExampleCommons.getHttpClient();

    // Los parámetros "dateFrom y "dateTo" delimitan las fechas de inicio y final para
    // la obtención del histórico, mientras que "hLimit" y "hOffset" permiten paginar.
    // Componemos la URL por partes para mayor claridad.

    String url = "/STH/v1/contextEntities/type/WasteContainer/id/CONTAINER01" +
        "/attributes/fillingLevel?" +
        "dateFrom=2018-07-18T00:07:30Z&" +  // desde las 07:30am (GMT) del 18 de julio
        "dateTo=2018-07-18T08:00:00Z&" +    // hasta las 08:00am (GMT) del 18 de julio
        "hLimit=20&" +                      // con un límite máximo de 20 muestras
        "hOffset=0";                        // empezando a contar a partir de la primer
                                            // muestra

    System.out.println(url);

    HttpGet reqGet = new HttpGet("https://platformhost:18666" + url);
    reqGet.addHeader("fiware-service", "smartgondor");
    reqGet.addHeader("fiware-servicepath", "/gardens");
    reqGet.addHeader("x-auth-token", token);

    // La respuesta a la petición incluye un elemento "values" con un array JSON con
    // las muestras del atributo, cada muestra compuesta por un valor y el instante de
    // tiempo correspondiente al valor. En este punto se realizaría el procesado de
    // datos (pe. para representarlos en una gráfica) pero, a efectos de ejemplo, aquí
    // simplemente lo imprimimos por la salida estándar
    HttpResponse response = httpClient.execute(reqGet);
    JSONObject data = ExampleCommons.extractJsonObject(response);

    System.out.println(data.toString(2));

    // Salida esperada (3 muestras en el elemento "values"):
    //
    //  {
    //    "contextResponses": [
    //      {
    //        "contextElement": {
    //          "attributes": [
    //            {
    //              "values": [
    //                {
    //                  "recvTime": "2018-07-18T07:36:37.443Z",
    //                  "attrValue": "23.24",
    //                  "attrType": "Number"
    //                },
    //                {
    //                  "recvTime": "2018-07-18T07:47:37.374Z",
    //                  "attrValue": "35.6",
    //                  "attrType": "Number"
    //                },
    //                {
    //                  "recvTime": "2018-07-18T07:57:23.627Z",
    //                  "attrValue": "41.03",
    //                  "attrType": "Number"
    //                }
    //              ],
    //              "name": "fillingLevel"
    //            }
    //          ],
    //          "id": "CONTAINER01",
    //          "isPattern": false,
    //          "type": "WasteContainer"
    //        },
    //        "statusCode": {
    //          "code": "200",
    //          "reasonPhrase": "OK"
    //        }
    //      }
    //    ]
    //  }

    httpClient.close();
  }
}
