import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import java.io.File;

public class Lab9 {

  public static ArrayList<String> readXML(String filename) {
    ArrayList<String> output = new ArrayList<>();
    try {
      File file = new File(filename);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(file);
      doc.getDocumentElement().normalize();
      NodeList nodeList = doc.getElementsByTagName("Borrowed_by");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);

        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element sectionNode = (Element) node;

          NodeList cidateElementList = sectionNode.getElementsByTagName("Checkin_date");
          Element cidateElement = (Element) cidateElementList.item(0);
          NodeList cidateNodeList = cidateElement.getChildNodes();
          String cidate = ((Node) cidateNodeList.item(0)).getNodeValue().trim();

          NodeList idElementList = sectionNode.getElementsByTagName("MemberID");
          Element idElement = (Element) idElementList.item(0);
          NodeList idNodeList = idElement.getChildNodes();
          String id = ((Node) idNodeList.item(0)).getNodeValue().trim();

          NodeList isbnElementList = sectionNode.getElementsByTagName("ISBN");
          Element isbnElement = (Element) isbnElementList.item(0);
          NodeList isbnNodeList = isbnElement.getChildNodes();
          String isbn = ((Node) isbnNodeList.item(0)).getNodeValue().trim();

          NodeList libraryElementList = sectionNode.getElementsByTagName("Library");
          Element libraryElement = (Element) libraryElementList.item(0);
          NodeList libraryNodeList = libraryElement.getChildNodes();
          String library = ((Node) libraryNodeList.item(0)).getNodeValue().trim();

          NodeList codateElementList = sectionNode.getElementsByTagName("Checkout_date");
          Element codateElement = (Element) codateElementList.item(0);
          NodeList codateNodeList = codateElement.getChildNodes();
          String codate = ((Node) codateNodeList.item(0)).getNodeValue().trim();

          String query = "";
          if (!cidate.equals("N/A")) {
            query += String.format("update borrowed set checkin_date = %s where member_id = %s and isbn = %s", cidate,
                id, isbn);
          } else {
            query += String.format("insert into borrowed values(%s, %s, %s, %s)", id, isbn, library, codate);
          }
          System.out.println(query);
          output.add(query);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return output;
  }

  public static void insertOrUpdate(Statement statement, String[] values) {
    try {
      statement.executeQuery("");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String args[]) {

    Connection con = null;

    try {
      Statement stmt;
      ResultSet rs;
      Scanner scanner = new Scanner(new File(".env"));
      String password = scanner.nextLine();
      scanner.close();
      String username = "jmaster";

      readXML("./libdata.xml");

      String url = "jdbc:mariadb://helmi:3306/" + username;

      con = DriverManager.getConnection(url, username, password);

      System.out.println("URL: " + url);
      System.out.println("Connection: " + con);

      stmt = con.createStatement();

      try {
        rs = stmt.executeQuery("SELECT * FROM author");
        while (rs.next()) {
          System.out.println(rs.getString("author_id"));
        }
      } catch (Exception e) {
        System.out.print(e);
        System.out.println(
            "No Author table to query");
      }

      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
