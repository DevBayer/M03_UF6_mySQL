import models.Actor;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Lluís Bayer Soler on 26/01/17.
 */
public class MainTwo {
    static DBManager db = null;
    public static void main(String[] args) throws SQLException {
        db = new DBManager("172.31.73.152", "sakila", "root", "root");
        // initialize DBManager with the connection parameters
        //db = new DBManager("192.168.1.24", "sakila", "root", "root");
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
                    // show database info (metadata)
                    showInfoExpanded();
                    break;
                case 3:
                    // show tables (with a simple query (show tables;)) and prettify with a table-format
                    showTables();
                    break;
                case 4:
                    System.out.println("Example Query: select * from actor;");
                    while(q == null || q.isEmpty() || q.equals(" ")) {
                        System.out.println("Query: ");
                        q = readInput_String(sc);
                    }
                    doQuery(q);
                    break;
                case 5:
                    System.out.println("Example Query: insert into actor (actor_id,first_name,last_name) VALUES (201,'LLUÍS','BAYER');");
                    while(q == null || q.isEmpty() || q.equals(" ")) {
                        System.out.println("Query: ");
                        q = readInput_String(sc);
                    }
                    System.out.println(db.insert(q));
                    break;
                case 6:
                    System.out.println("Example Query: UPDATE actor SET last_name='BAYER SOLER' WHERE actor_id=201;");
                    while(q == null || q.isEmpty() || q.equals(" ")) {
                        System.out.println("Query: ");
                        q = readInput_String(sc);
                    }
                    System.out.println(db.insert(q));
                    break;
                case 7:
                    System.out.println("THIS IS A SAMPLE VERSION:");
                    System.out.println("query table actor -> save ResultSet on ArrayList with a Actor Instance");
                    doUserInteraction();
                    break;
                case 8:
                    if(db.conn != null && !db.conn.isClosed()){
                        db.conn.close();
                    }
                    break;
                case 9:
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
            showInfo();
        }
        System.out.println("|------------------------|");
        System.out.println("|========================|");
        System.out.println("|--- 1. init connection -|");
        System.out.println("|========================|");
        System.out.println("|--- 2. show serverinfo -|");
        System.out.println("|========================|");
        System.out.println("|--- 3. list tables -----|");
        System.out.println("|========================|");
        System.out.println("|--- 4. do query --------|");
        System.out.println("|========================|");
        System.out.println("|--- 5. do insert -------|");
        System.out.println("|========================|");
        System.out.println("|--- 6. do update -------|");
        System.out.println("|========================|");
        System.out.println("|--- 7. instance object -|");
        System.out.println("|========================|");
        System.out.println("|--- 8. close conn ------|");
        System.out.println("|========================|");
        System.out.println("|--- 9. exit ------------|");
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

    private static void showInfo() throws SQLException{
        if(db.conn != null && db.conn.isValid(1000)) {
            DatabaseMetaData metadata = db.conn.getMetaData();
            System.out.println(metadata.getDatabaseProductName() + " - " + metadata.getDatabaseProductVersion());
        }else{
            System.out.println("The connection has not open.");
        }
    }

    private static void showInfoExpanded() throws SQLException{
        if(db.conn != null && db.conn.isValid(1000)) {
            DatabaseMetaData metadata = db.conn.getMetaData();
            System.out.println("Connected on "+metadata.getURL()+"@"+metadata.getDatabaseProductName());
            System.out.println(metadata.getDatabaseProductName() + " - " + metadata.getDatabaseProductVersion());
            System.out.println("Using the driver "+metadata.getDriverName()+" - "+metadata.getDriverVersion());

        }else{
            System.out.println("The connection has not open.");
        }
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

    private static ArrayList<Actor> getActors() throws  SQLException {
        ArrayList<Actor> actors = new ArrayList<>();
        ResultSet rs = db.query("select * from actor;");
        while(rs.next()) {
            Actor actor = new Actor();
            actor.setActor_id(rs.getInt("actor_id"));
            actor.setFirst_name(rs.getString("first_name"));
            actor.setLast_name(rs.getString("last_name"));
            actors.add(actor);
        }
        return actors;
    }

    private static void doUserInteraction() throws SQLException {
        Boolean interact = true;
        ArrayList<Actor> actors = getActors();
        while(interact) {
            System.out.println("Size Actors: " + actors.size());
            System.out.println("What do you wanna do?");
            System.out.println("1. List all data");
            System.out.println("2. Insert new data");
            System.out.println("3. Update data");
            System.out.println("4. Exit Interaction");
            Scanner sc = new Scanner(System.in);
            int option = 0;
            while (option == 0) {
                System.out.print("Option: ");
                option = readInput_int(sc);
                if (option > 0) {
                    break;
                }
            }

            switch (option) {
                case 1:
                    System.out.println(String.format("%84s", " ").replace(" ", "-"));
                    System.out.format("|- %-8s | %-32s | %-32s -|\n", "actor_id", "first_name", "last_name");
                    for (Actor actor : actors) {
                        System.out.format("|- %-8d | %-32s | %-32s -|\n", actor.getActor_id(), actor.getFirst_name(), actor.getLast_name());
                        System.out.println(String.format("%84s", " ").replace(" ", "-"));
                    }
                    break;

                case 2:
                    Actor newActor = new Actor();
                    System.out.println("Insert form: ");
                    System.out.println("actor id ?");
                    newActor.setActor_id(readInput_int(sc));
                    System.out.println("first name ?");
                    sc.nextLine(); // force nextline
                    newActor.setFirst_name(readInput_String(sc));
                    System.out.println("last name ?");
                    newActor.setLast_name(readInput_String(sc));

                    String query = newActor.getInsertQuery();
                    db.insert(query);

                    actors = getActors();
                    break;

                case 3:
                    System.out.println("ID:");
                    int id = readInput_int(sc);
                    sc.nextLine(); // force nextline
                    Actor actor = db.getActor(id);
                    if(actor != null) {
                        System.out.println("First name? (" + actor.getFirst_name() + "): ");
                        String first_name = readInput_String(sc);
                        if (!first_name.isEmpty()) {
                            actor.setFirst_name(first_name);
                        }
                        System.out.println("Last name? (" + actor.getLast_name() + "): ");
                        String last_name = readInput_String(sc);
                        if (!last_name.isEmpty()) {
                            actor.setLast_name(last_name);
                        }

                        String query_upd = actor.getUpdateQuery();
                        System.out.println(query_upd);
                        db.insert(query_upd);

                        actors = getActors();
                    }else{
                        System.out.println("Unknown actor.");
                    }
                    break;

                case 4:
                    interact = false;
                    break;
            }
        }

    }

}
