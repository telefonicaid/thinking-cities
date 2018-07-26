/* Copyright 2018 Telefónica Soluciones de Informática y Comunicaciones de España, S.A.U.
 * All rights reserved.
 */

package com.telefonica.iot.examples;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;

import java.net.URLEncoder;
import java.io.IOException;

/**
 * Ejemplo de inserción de datos a través del IOT Gateway. El ejemplo ilustra el envío de una medida
 * de nivel de llenado (representando dispositivos embarcados en contenedores de basura, en el contexto
 * de un vertical de gestión de residuos urbanos, i.e. limpieza). Se ilustra el uso del transporte HTTP para
 * el envío de las medidas (al endpoint ofrecido por el IoT Gateway), tanto en formato JSON como en formato UL.
 *
 * Se asume la existencia de sendos protocolos (IoTA-JSON y IoTA-UL) provisionados en el IOT Gateway y
 * el mapeo entre la medida que envía los dispositivos relativa al nivel de llenado y el atributo fillingLevel
 * dentro de la entidad WasteContainer correspondiente, conformes al modelo semántico de WasteContainer descrito
 * con detalle como modelo armonizado FIWARE en:
 **
 * http://fiware-datamodels.readthedocs.io/en/latest/WasteManagement/WasteContainer/doc/spec/index.html
 *
 * Así mismo, el ejemplo asume simplificaciones con respecto a un caso real, a fin de hacerlo más claro
 * e ilustrativo, pe. certificados sin autofirmar, no se realiza control de errores de I/O, etc.
 */
public class ExampleIOTAInsertData {

  public static void main(String [] args) throws IOException {

    System.out.println("<<<< IOTA INSERT EXAMPLE >>>>");

    HttpResponse response;

    // Se asume que el dispositivo tiene cargadas previamente las API keys necesarias
    // para el envío de medida que, a efectos de ejemplo, se encuentran almacenadas en
    // las siguiente variables
    String apiKeyJson = "66666666hy5mvj1mmpj4zmavvf";
    String apiKeyUl = "77777777l1ai84qkt9lezxrwz";

    CloseableHttpClient httpClient = ExampleCommons.getHttpClient();
    String baseUrl = "http://platformhost";

    // Envío de medida en formato JSON. El parámetro "i" indica el id de dispositivo y
    // el parámetro "k" la API key. La medida ("f") se codifica en el payload.
    HttpPost reqPost = new HttpPost(baseUrl + ":8185/iot/json?i=container1" +
        "&k=" + apiKeyJson);
    reqPost.addHeader("content-type", "application/json");
    reqPost.setEntity(new StringEntity("{\"f\": 23.4}"));

    response = httpClient.execute(reqPost);
    System.out.println("Status code: " + response.getStatusLine());

    // Envío de medida en formato UL. Más compacto que JSON, incluso la medida se envía
    // como parámetro en la URL (parámetro "d").
    // NOTA: necesitamos URL encoding por el "|"
    String measureUl = URLEncoder.encode("f|31.2", "UTF-8");
    HttpGet reqGet = new HttpGet(baseUrl + ":8085/iot/d?i=container2&k=" +  apiKeyUl +
            "&d=" + measureUl);
    response = httpClient.execute(reqGet);
    System.out.println("Status code: " + response.getStatusLine());

    httpClient.close();
  }
}
