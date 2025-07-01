import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
import javax.xml.parsers.*;
import org.w3c.dom.*;

import java.io.File;

public class Lab10Blunk {

  public static void getOrCreateUser(Statement stmt) {
    try {
      String memberID = JOptionPane.showInputDialog("What is your member ID?");
      ResultSet potentialUser = stmt
          .executeQuery(String.format("select * from member where member_id = %s;", memberID));
      if (potentialUser.next()) {
        String firstName = potentialUser.getString("first_name");
        String lastName = potentialUser.getString("last_name");
        JOptionPane.showMessageDialog(null, String.format("Welcome, %s %s!", firstName, lastName));
      } else {
        JOptionPane.showMessageDialog(null, "I can't find you in our records, so let's set you up as a new user.");
        String firstName = JOptionPane.showInputDialog("What is your first name?");
        String lastName = JOptionPane.showInputDialog("What is your last name?");
        String gender = JOptionPane.showInputDialog("What is your gender?");
        String dob = JOptionPane.showInputDialog("What is your date of birth? (yyyy-MM-dd format please)");
        stmt.executeUpdate(String.format("insert into member values(%s, '%s', '%s', '%s', '%s')",
            memberID, firstName, lastName, gender, dob));
        JOptionPane.showMessageDialog(null, "Welcome to our library system!");
      }
    } catch (Exception e) {
      e.printStackTrace();
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
      getOrCreateUser(stmt);
      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
