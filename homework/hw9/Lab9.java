import java.sql.*;
import java.util.Scanner;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import java.io.File;

public class Lab9 {

  public static void readXML(String filename) {
    try {
      File file = new File(filename);
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(file);
      doc.getDocumentElement().normalize();
      NodeList nodeList = doc.getElementsByTagName("Borrowed_by");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node firstNode = nodeList.item(i);

        if (firstNode.getNodeType() == Node.ELEMENT_NODE) {
          Element sectionNode = (Element) firstNode;
          NodeList memberIDElementList = sectionNode.getElementsByTagName("MemberID");
          Element memberID = (Element) memberIDElementList.item(0);
          NodeList memberIDNodeList = memberID.getChildNodes();
          System.out.println("member_id: " + ((Node) memberIDNodeList.item(0)).getNodeValue().trim());

          NodeList isbnElementList = sectionNode.getElementsByTagName("ISBN");
          Element isbn = (Element) isbnElementList.item(0);
          NodeList isbnNodeList = isbn.getChildNodes();
          System.out.println("isbn: " + ((Node) isbnNodeList.item(0)).getNodeValue().trim());

          NodeList libraryElementList = sectionNode.getElementsByTagName("Library");
          Element library = (Element) libraryElementList.item(0);
          NodeList libraryNodeList = library.getChildNodes();
          System.out.println("library: " + ((Node) libraryNodeList.item(0)).getNodeValue().trim());

          NodeList codateElementList = sectionNode.getElementsByTagName("Checkout_date");
          Element codate = (Element) codateElementList.item(0);
          NodeList codateNodeList = codate.getChildNodes();
          System.out.println("codate: " + ((Node) codateNodeList.item(0)).getNodeValue().trim());

          NodeList cidateElementList = sectionNode.getElementsByTagName("Checkin_date");
          Element cidate = (Element) cidateElementList.item(0);
          NodeList cidateNodeList = cidate.getChildNodes();
          System.out.println("cidate: " + ((Node) cidateNodeList.item(0)).getNodeValue().trim());
        }
      }
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
      System.out.println(password);
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
