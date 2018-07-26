/* Copyright 2018 Telefónica Soluciones de Informática y Comunicaciones de España, S.A.U.
 * All rights reserved.
 */

package com.telefonica.iot.examples;

import java.sql.*;

/**
 * Ejemplo de consumo de datos mediante conexión JDBC a Hive (Hadoop). Se muestra la
 * operativa básica sobre la interfaz SQL ofrecida por Hive (show, use, etc.) así como
 * algunas queries de ejemplo.
 *
 */
public class ExampleHiveConsumeData {

  private static String driverName = "org.apache.hive.jdbc.HiveDriver";

  /**
   * Ejecuta una query en Hive y devuelve el resultado como string.
   *
   * @param con
   * @param query
   * @return
   */
  public static String ejecuta(Connection con, String query) throws SQLException {

    String s = "";
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      return ExampleCommons.resultString(rs);
    }
    catch (SQLException e) {
      // La excepción salta cuando la query no devuelve un ResultSet (pe. una sentencia
      // "use mydb"). En este caso hay que utilizar el método execute()
      Statement stmt = con.createStatement();
      stmt.execute(query);
      return "";
    }
  }

  public static void main(String [] args) throws Exception {

    System.out.println("<<<< HIVE CONSUME EXAMPLE >>>>");

    // Paso 1: creamos la conexión a Hive, haciendo uso de JDBC
    Class.forName(driverName);
    String url = "jdbc:hive2://hivehost:10000/gondorci";
    Connection con = DriverManager.getConnection(url, "sc_gondorci", "abcd1234");

    // Paso 2: ejecutamos setencias SQL de ejemplo
    System.out.println(ejecuta(con, "show databases"));
    System.out.println(ejecuta(con, "use medioambiente"));
    System.out.println(ejecuta(con, "show tables"));
    System.out.println(ejecuta(con, "describe estacion_clima_1"));
    System.out.println(ejecuta(con, "select * from estacion_clima_1"));
    System.out.println(ejecuta(con, "select m1,precipitaciones from estacion_clima_1"));

  }
}
