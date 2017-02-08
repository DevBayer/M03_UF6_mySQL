import models.Actor;

import java.sql.*;

/**
 * Created by Lluís Bayer Soler on 26/01/17.
 */
public class DBManager {
    Connection conn = null;

    private String ipaddress;
    private String database;
    private String username;
    private String password;

    /**
     * Main constructor for attending the db driver and initialize variables connection.
     * @param ipaddress serverIP, can be serverIP:PORT ex localhost:3306
     * @param database database to connect
     * @param username username of the database
     * @param password password of the username
     */
    public DBManager(String ipaddress, String database, String username, String password) {
        try {
            checkDriver();
        }catch(ClassNotFoundException e){
            System.out.println("DBManager:: the driver for mysql has not found.");
            System.exit(1);
        }
        this.ipaddress = ipaddress;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Function to initialize the connection and check through try-catch
     */
    public void initialize() {
        try {
            if(conn != null && !conn.isValid(1000)) return;
            conn = DriverManager.getConnection("jdbc:mysql://" + ipaddress + "/" + database, username, password);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Function to close the connection
     * @throws SQLException
     */
    public void close() throws  SQLException {
        conn.close();
    }

    /**
     * Function to make a Query
     * @param q query
     * @return ResultSet to be treated
     */
    public ResultSet query(String q){
        try {
            if (conn != null && conn.isValid(1000)) {
                // initialize a simple SQL instruction
                Statement s = conn.createStatement();
                // table of data representing a database result
                ResultSet rs = s.executeQuery(q);
                return rs;
            }else{
                initialize();
                return query(q);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function to make a INSERT or UPDATE or ALTER, without returning data
     * @param q query
     * @return if success
     */
    public String insert(String q){
        try {
            if (conn != null && conn.isValid(1000)) {
                // initialize a simple SQL instruction
                Statement s = conn.createStatement();
                // execute SQL and save the number of rows affected by query
                int exec = s.executeUpdate(q);
                return "("+exec+") query executed";
            } else {
                initialize();
                return insert(q);
            }
        }catch(SQLException e){
            return "Err: "+e.getMessage();
        }
    }

    public Actor getActor(int id){
        try {
            if (conn != null && conn.isValid(1000)) {
                // initialize a simple SQL instruction
                Statement s = conn.createStatement();
                // table of data representing a database result
                ResultSet rs = s.executeQuery("select * from actor where actor_id = "+id);
                while(rs.next()) {
                    Actor a = new Actor();
                    a.setActor_id(id);
                    a.setFirst_name(rs.getString("first_name"));
                    a.setLast_name((rs.getString("last_name")));
                    return a;
                }
            }else{
                initialize();
                return getActor(id);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if driver exists with a throw
     * @throws ClassNotFoundException .
     */
    private void checkDriver() throws ClassNotFoundException{
        Class.forName("com.mysql.jdbc.Driver");
    }
}
