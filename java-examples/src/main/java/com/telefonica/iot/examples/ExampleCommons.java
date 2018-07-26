/* Copyright 2018 Telefónica Soluciones de Informática y Comunicaciones de España, S.A.U.
 * All rights reserved.
 */

package com.telefonica.iot.examples;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Clase que engloba funcionalidad común utilizada por los distintos ejemplos
 */
public class ExampleCommons {

  /**
   * Función para la obtención de clientes HTTP con los que realizar las conexiones
   */
  public static CloseableHttpClient getHttpClient() {
    // La instancia de plataforma que se utiliza para demos tiene certificados
    // autofirmados, por lo que desactivamos la verificación SSL a la hora de realizar
    // la conexión HTTP
    return HttpClients.custom().
        setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();
  }

  /**
   * Lee un fichero del directorio de recursos (cuyo nombre se pasa como argumento) y
   * devuelve su contenido como un String.
   *
   * @param fileName
   * @return
   * @throws FileNotFoundException
   */
  public static String getFileAsString(String fileName) throws FileNotFoundException {

    StringBuilder result = new StringBuilder("");

    //Get file from resources folder
    ClassLoader classLoader = ExampleCommons.class.getClassLoader();
    File file = new File(classLoader.getResource(fileName).getFile());

    Scanner scanner = new Scanner(file);
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      result.append(line).append("\n");
    }

    scanner.close();

    return result.toString();

  }

  /**
   * Esta función encapsula la petición de token, mediante una llamada al componente IDM
   * de la plataforma ThinkingCities. Dado un servicio, subservicio, usuario y password,
   * devolverá un token válido para realizar el acceso.
   *
   * @param service
   * @param subservice
   * @param user
   * @param pass
   * @return
   */
  public static String getToken(String service, String subservice, String user,
                                String pass) throws IOException {

    StringEntity request = new StringEntity("{" +
        "    \"auth\": {" +
        "        \"identity\": {" +
        "            \"methods\": [" +
        "                \"password\"" +
        "            ],\n" +
        "            \"password\": {" +
        "                \"user\": {" +
        "                    \"domain\": {" +
        "                        \"name\": \"" + service + "\"" +
        "                    }," +
        "                    \"name\": \"" + user + "\"," +
        "                    \"password\": \"" + pass + "\"" +
        "                }" +
        "            }" +
        "        }," +
        "        \"scope\": {" +
        "            \"project\": {" +
        "                \"domain\": {" +
        "                    \"name\": \"" + service + "\"" +
        "                }," +
        "                \"name\": \"" + subservice + "\"" +
        "            }" +
        "        }" +
        "    }" +
        "}");

    CloseableHttpClient httpClient = getHttpClient();

    HttpPost reqPost = new HttpPost("https://platformhost:15001/v3/auth/tokens");
    reqPost.addHeader("content-type", "application/json");
    reqPost.setEntity(request);

    HttpResponse response = httpClient.execute(reqPost);

    // El token viene en la cabecera de la respuesta x-subject-token
    String token = response.getHeaders("x-subject-token")[0].getValue();

    httpClient.close();

    return token;
  }

  /**
   * Función que extrae el body de una response HTTP y la devuelve como array JSON.
   *
   * @param response
   * @return
   */
  public static JSONArray extractJsonArray(HttpResponse response) throws IOException {
    HttpEntity entity = response.getEntity();
    String responseString = EntityUtils.toString(entity, "UTF-8");
    return new JSONArray(responseString);
  }

  /**
   * Función que extrae el body de una response HTTP y la devuelve como objeto JSON.
   *
   * @param response
   * @return
   */
  public static JSONObject extractJsonObject(HttpResponse response) throws IOException {
    HttpEntity entity = response.getEntity();
    String responseString = EntityUtils.toString(entity, "UTF-8");
    return new JSONObject(responseString);
  }

  /**
   * Devuelve el contenido de un ResultSet como un string.
   *
   * @param rs
   * @return
   */
  public static String resultString(ResultSet rs) throws SQLException {
    String s = "";
    while (rs.next()) {
      for (int i = 1; i < rs.getMetaData().getColumnCount(); i++) {
        s += rs.getString(i) + ",";
      }
      s += rs.getString(rs.getMetaData().getColumnCount());
      s += "\n";
    }
    s += "-----------------------------------------------------------------";
    return s;
  }

}
