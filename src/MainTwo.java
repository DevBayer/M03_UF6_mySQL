import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Scanner;

/**
 * Created by Lluís Bayer Soler on 26/01/17.
 */
public class MainTwo {
    static DBManager db = null;
    public static void main(String[] args) throws SQLException {
        //db = new DBManager("172.31.73.162", "sakila", "root", "root");
        // initialize DBManager with the connection parameters
        db = new DBManager("192.168.1.24", "sakila", "root", "root");
        // initialize Scanner for read input data
        Scanner sc = new Scanner(System.in);
        // initialize infinite-loop to display the menu
        while(true){
            String q = null;
            printMenu();
            int option = 0;
            while(option == 0) {
                System.out.print("Option: ");
                option = readInput_int(sc);
                if(option > 0){
                    break;
                }
            }
            switch(option){
                case 1:
                    // force initialization
                    db.initialize();
                    break;
                case 2:
                    // show tables (with a simple query (show tables;)) and prettify with a table-format
                    showTables();
                    break;
                case 3:
                    System.out.println("Example Query: select * from actor;");
                    while(q == null || q.isEmpty() || q.equals(" ")) {
                        System.out.println("Query: ");
                        q = readInput_String(sc);
                    }
                    doQuery(q);
                    break;
                case 4:
                    System.out.println("Example Query: insert into actor (actor_id,first_name,last_name) VALUES (201,'LLUÍS','BAYER');");
                    while(q == null || q.isEmpty() || q.equals(" ")) {
                        System.out.println("Query: ");
                        q = readInput_String(sc);
                    }
                    System.out.println(db.insert(q));
                    break;
                case 5:
                    System.out.println("Example Query: UPDATE actor SET last_name='BAYER SOLER' WHERE actor_id=201;");
                    while(q == null || q.isEmpty() || q.equals(" ")) {
                        System.out.println("Query: ");
                        q = readInput_String(sc);
                    }
                    System.out.println(db.insert(q));
                    break;
                case 6:
                    if(db.conn != null && !db.conn.isClosed()){
                        db.conn.close();
                    }
                    break;
                case 7:
                    if(db.conn != null && !db.conn.isClosed()){
                        db.conn.close();
                    }
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
            }
            System.out.println("");
        }

    }

    private static void printMenu() throws SQLException{
        System.out.println("|------------------------|");
        System.out.println("|-------- DB CMD --------|");
        if(db.conn == null || (db.conn != null && db.conn.isClosed())) {
            System.out.println("|- status: no connected -|");
        }else{
            System.out.println("|--- status: connected --|");
        }
        System.out.println("|------------------------|");
        System.out.println("|========================|");
        System.out.println("|--- 1. init connection -|");
        System.out.println("|========================|");
        System.out.println("|--- 2. list tables -----|");
        System.out.println("|========================|");
        System.out.println("|--- 3. do query --------|");
        System.out.println("|========================|");
        System.out.println("|--- 4. do insert -------|");
        System.out.println("|========================|");
        System.out.println("|--- 5. do update -------|");
        System.out.println("|========================|");
        System.out.println("|--- 6. close conn ------|");
        System.out.println("|========================|");
        System.out.println("|--- 7. exit ------------|");
        System.out.println("|========================|");
        System.out.println("|------------------------|");
    }

    private static int readInput_int(Scanner sc){
        if (sc.hasNextInt()) {
            return sc.nextInt();
        } else {
            System.out.println("Invalid input, try again.");
            sc.nextLine();
        }
        return 0;
    }

    private static String readInput_String(Scanner sc){
        while(sc.hasNext()) {
            if(sc.hasNextLine()) {
                return sc.nextLine();
            }
        }
        return "";
    }

    private static void showTables() throws SQLException{
        ResultSet rs = db.query("show tables;");
        while (rs.next()) {
            if(rs.isFirst()){
                System.out.println("|------------------------------------|");
                System.out.format("|- %-32s -|\n", "Tables");
                System.out.println("|------------------------------------|");
            }
            System.out.format("|- %-32s -|\n", rs.getString(1));
            if(rs.isLast()){
                System.out.println("|------------------------------------|");
            }
        }
    }

    private static void doQuery(String query) throws SQLException{
        // print the QueryResult (ResultSet) with a format through column-row
        ResultSet rs = db.query(query);
        if(rs.next()) {
            ResultSetMetaData rsmd = rs.getMetaData();
            String[] titles = new String[rsmd.getColumnCount()-1];
            String[] dumbtitles  = new String[rsmd.getColumnCount()-1];
            String format = "|";
            String format_title = "|";
            for (int i = 1; i < rsmd.getColumnCount(); i++) {
                titles[i-1] = rsmd.getColumnName(i);
                if(rsmd.getColumnType(i) == Types.INTEGER || rsmd.getColumnType(i) == Types.BIGINT || rsmd.getColumnType(i) == Types.SMALLINT) {
                    format += "%10d";
                    format_title += "%10s";
                    dumbtitles[i-1] = String.format("%10s", " ").replace(" ", "-");
                }else{
                    format += "%32s";
                    format_title += "%32s";
                    dumbtitles[i-1] = String.format("%32s", " ").replace(" ", "-");
                }
                if(i != rsmd.getColumnCount()-1){
                    format += "|";
                    format_title += "|";
                }
            }
            format += "|\n";
            format_title += "|\n";
            System.out.format(format_title, dumbtitles);
            System.out.format(format_title, titles);
            System.out.format(format_title, dumbtitles);
            while (rs.next()) {
                Object[] things = new Object[rsmd.getColumnCount()-1];
                for (int i = 1; i < rsmd.getColumnCount(); i++) {
                    things[i-1] = rs.getObject(i);
                }
                System.out.format(format, things);
                if (rs.isLast()) {
                    System.out.format(format_title, dumbtitles);
                }
            }
        }
    }

}
