import java.sql.*;
import java.util.*;

import java.io.File;

public class Lab10Blunk {

  private static String getValueFromStdin(Scanner s, String message) {
    System.out.print(message);
    return s.nextLine().trim();
  }

  private static void getOrCreateMember(Scanner s, Statement stmt) {
    try {
      String memberID = getValueFromStdin(s, "What is your member ID? ");
      if (memberID.equalsIgnoreCase("exit") ||
          memberID.equalsIgnoreCase("bye") ||
          memberID.equalsIgnoreCase("quit")) {
        System.exit(0);
      }
      ResultSet potentialUser = stmt
          .executeQuery(String.format("select * from member where member_id = %s;", memberID));
      if (potentialUser.next()) {
        String firstName = potentialUser.getString("first_name");
        String lastName = potentialUser.getString("last_name");
        System.out.printf("Welcome, %s %s!%n", firstName, lastName);
      } else {
        System.out.println("I can't find you in our records, so let's set you up as a new user.");
        String firstName = getValueFromStdin(s, "First name: ");
        String lastName = getValueFromStdin(s, "Last name: ");
        String gender = getValueFromStdin(s, "Gender (m,f,x): ");
        String dob = getValueFromStdin(s, "Date of birth (yyyy-MM-dd): ");
        stmt.executeUpdate(String.format("insert into member values(%s, '%s', '%s', '%s', '%s')",
            memberID, firstName, lastName, gender, dob));
        System.out.println("Welcome to our library system!");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printResultSet(ResultSet rs) throws SQLException {
    boolean foundAny = false, foundAvailable = false;
    while (rs.next()) {
      foundAny = true;
      int copies = rs.getInt("copies_available");
      String libName = rs.getString("lib_name");
      int floorNumber = rs.getInt("floor_number");
      int shelfNumber = rs.getInt("shelf_number");
      if (copies > 0) {
        foundAvailable = true;
        System.out.printf("This book can be found at %s on floor %d in shelf %d%n",
            libName, floorNumber, shelfNumber);
      } else {
        System.out.printf("No books available at library %s%n", libName);
      }
    }
    if (!foundAvailable && foundAny) {
      System.out.println("All copies are currently checked out");
    }
    if (!foundAny) {
      System.out.println("This book is not in the library system");
    }
  }

  private static void findBook(Scanner s, Statement stmt) {
    try {
      String input = getValueFromStdin(s, "Enter a book ISBN, title, or author: ");
      ResultSet rs;
      if (input.matches("[0-9\\-]+")) {
        rs = stmt.executeQuery(String.format(
            "select copies_available, lib_name, shelf_number, floor_number from located_at where isbn = '%s';",
            input));
        printResultSet(rs);
        return;
      }
      rs = stmt.executeQuery(String.format(
          "select distinct b.isbn, b.title, a.first_name, a.last_name from book b join book_author ba on b.isbn = ba.isbn join author a on ba.author_id = a.author_id where title like '%%%s%%' or a.first_name like '%%%s%%' or a.last_name like '%%%s%%';",
          input, input, input));
      List<String> matchingISBNs = new ArrayList<>();
      List<String> descriptions = new ArrayList<>();
      int count = 0;
      while (rs.next()) {
        String isbn = rs.getString("isbn");
        String title = rs.getString("title");
        String authorFirst = rs.getString("first_name");
        String authorLast = rs.getString("last_name");
        matchingISBNs.add(isbn);
        descriptions.add(String.format("[%d] *%s* by %s %s (isbn: %s)", ++count, title, authorFirst, authorLast, isbn));
      }
      if (count == 0) {
        System.out.println("No matching books found");
        return;
      } else if (count > 1) {
        for (String description : descriptions) {
          System.out.println(description);
        }
        int selection = -1;
        while (selection < 1 || selection > matchingISBNs.size()) {
          try {
            String choice = getValueFromStdin(s,
                "Enter the number of the book for which you'd like to check availability: ");
            selection = Integer.parseInt(choice);
          } catch (NumberFormatException e) {
            System.out.println("Invalid input, please enter a number from the list");
          }
        }
        String chosenISBN = matchingISBNs.get(selection - 1);
        rs = stmt.executeQuery(String.format(
            "select copies_available, lib_name, shelf_number, floor_number from located_at where isbn = '%s';",
            chosenISBN));
        printResultSet(rs);
      } else {
        rs = stmt.executeQuery(String.format(
            "select copies_available, lib_name, shelf_number, floor_number from located_at where isbn = '%s';",
            matchingISBNs.get(0)));
        printResultSet(rs);
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
      while (true) {
        getOrCreateMember(stdin, stmt);
        findBook(stdin, stmt);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
