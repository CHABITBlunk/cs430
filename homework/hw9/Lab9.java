import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.File;

public class Lab9 {

  public static void readXML(String filename, Statement stmt) {
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
          SimpleDateFormat in = new SimpleDateFormat("MM/dd/yyyy");
          SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd");
          if (!cidate.equals("N/A")) {
            java.util.Date d = in.parse(cidate);
            cidate = out.format(d);
            String checkQuery = String.format(
                "select count(*) from borrowed where isbn = '%s' and lib_name = '%s' and member_id = %s;",
                isbn, library, id);
            ResultSet rs = stmt.executeQuery(checkQuery);
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            if (count == 0) {
              System.out.printf("skipping: member %s did not check %s out from  %s\n", id, isbn, library);
              continue;
            }
            query += String.format(
                "update borrowed set checkin_date = '%s' where member_id = '%s' and isbn = '%s' and lib_name = '%s';",
                cidate, id, isbn, library);
          } else {
            java.util.Date cod = in.parse(codate);
            codate = out.format(cod);

            String checkQuery = String.format("select count(*) from located_at where isbn = '%s' and lib_name = '%s';",
                isbn, library);
            ResultSet rs = stmt.executeQuery(checkQuery);
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            if (count == 0) {
              System.out.printf("skipping: isbn %s not found in %s\n", isbn, library);
              continue;
            }
            query += String.format(
                "insert into borrowed(member_id, isbn, lib_name, checkout_date) values('%s', '%s', '%s', '%s');",
                id, isbn, library, codate);
          }
          System.out.println(query);
          stmt.executeQuery(query);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printResultSet(ResultSet rs) throws SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    int columnCount = meta.getColumnCount();
    for (int i = 1; i <= columnCount; i++) {
      System.out.print(meta.getColumnName(i) + "\t");
    }
    System.out.println();
    while (rs.next()) {
      for (int i = 1; i < columnCount; i++) {
        System.out.print(rs.getString(i) + "\t");
      }
      System.out.println();
    }
  }

  public static void main(String args[]) {

    Connection con = null;

    try {
      Statement stmt;
      Scanner scanner = new Scanner(new File(".env"));
      String password = scanner.nextLine();
      scanner.close();
      String username = "jmaster";
      String url = "jdbc:mariadb://helmi:3306/" + username;
      con = DriverManager.getConnection(url, username, password);
      stmt = con.createStatement();

      readXML("./libdata.xml", stmt);

      printResultSet(stmt.executeQuery("select * from borrowed;"));
      System.out.println();
      System.out.println();
      printResultSet(stmt.executeQuery(
          "select m.last_name, m.first_name, m.member_id, b.title, br.lib_name from borrowed br join book b on br.isbn = b.isbn join member m on br.member_id = m.member_id where br.checkin_date is null order by m.last_name, m.first_name;"));

      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
