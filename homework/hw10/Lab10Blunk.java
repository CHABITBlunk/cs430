import java.sql.*;
import java.util.*;
// import javax.swing.JOptionPane;

import java.io.File;

public class Lab10Blunk {

  static String getValueFromStdin(Scanner s, String message) {
    System.out.print(message);
    return s.next();
  }

  public static void getOrCreateUser(Scanner s, Statement stmt) {
    try {
      String memberID = getValueFromStdin(s, "What is your member ID?");
      ResultSet potentialUser = stmt
          .executeQuery(String.format("select * from member where member_id = %s;", memberID));
      if (potentialUser.next()) {
        String firstName = potentialUser.getString("first_name");
        String lastName = potentialUser.getString("last_name");
        System.out.printf("Welcome, %s %s!%n", firstName, lastName);
      } else {
        System.out.println("I can't find you in our records, so let's set you up as a new user.");
        String firstName = getValueFromStdin(s, "What is your first name?");
        String lastName = getValueFromStdin(s, "What is your last name?");
        String gender = getValueFromStdin(s, "What is your gender (m,f,x)?");
        String dob = getValueFromStdin(s, "What is your date of birth? (yyyy-MM-dd format please)");
        stmt.executeUpdate(String.format("insert into member values(%s, '%s', '%s', '%s', '%s')",
            memberID, firstName, lastName, gender, dob));
        System.out.println("Welcome to our library system!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void printResultSet(ResultSet rs) throws SQLException {
    String libName = rs.getString(2);
    int floorNumber = rs.getInt(3);
    int shelfNumber = rs.getInt(4);
    if (rs.getInt(1) > 0) {
      System.out.printf("This book can be found at %s on the %dth floor in the %dth shelf%n",
          libName, floorNumber, shelfNumber);
    } else {
      System.out.printf("No books available at library %s%n", libName);
    }

    while (rs.next()) {
      if (rs.getInt(1) > 0) {
        System.out.printf("This book can be found at %s on the %dth floor in the %dth shelf%n",
            libName, floorNumber, shelfNumber);
      } else {
        System.out.printf("No books available at library %s%n", libName);
      }
    }
  }

  public static void findBook(Scanner s, Statement stmt) {
    try {
      String input = getValueFromStdin(s, "Enter a book ISBN, title, or author:");
      ResultSet rs;
      if (input.matches("[0-9\\-]+")) {
        rs = stmt.executeQuery(String.format(
            "select copies_available, lib_name, shelf_number, floor_number from located_at where isbn = '%s';",
            input));
      } else {
        rs = stmt.executeQuery(
            String.format(
                "select copies_available, lib_name, shelf_number, floor_number from located_at where author like  '%%%s%%' or title like '%%%s%%';",
                input, input));
      }
      if (rs.next()) {
        printResultSet(rs);
      } else {
        System.out.println("We don't have this book in stock.");
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
      Scanner stdin = new Scanner(System.in);
      getOrCreateUser(stdin, stmt);
      con.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
