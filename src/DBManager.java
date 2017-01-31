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

    public DBManager(String ipaddress, String database, String username, String password) {
        try {
            checkDriver();
        }catch(ClassNotFoundException e){
            System.out.println("DBManager:: the driver for mysql has not found.");
        }
        this.ipaddress = ipaddress;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void initialize() {
        try {
            if(conn != null && !conn.isValid(1000)) return;
            conn = DriverManager.getConnection("jdbc:mysql://" + ipaddress + "/" + database, username, password);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void close() throws  SQLException {
        conn.close();
    }

    public ResultSet query(String q){
        try {
            if (conn != null && conn.isValid(1000)) {
                Statement s = conn.createStatement();
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

    public String insert(String q){
        try {
            if (conn != null && conn.isValid(1000)) {
                Statement s = conn.createStatement();
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

    private void checkDriver() throws ClassNotFoundException{
        Class.forName("com.mysql.jdbc.Driver");
    }
}
