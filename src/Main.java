import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Lluís Bayer Soler on 26/01/17.
 */
public class Main {
    public static void main(String[] args) {
        // init DBManager, is the class attendant for interact with the database
        // pass ip, db, user, pass to set
        DBManager db = new DBManager("172.31.73.162", "sakila", "root", "root");
        // initialize connection with db
        db.initialize();

        // do select
        ResultSet rs = db.query("select * from city");
        try {
            // print data from cursor (rs)
            while (rs.next()) {
                if(rs.isFirst()){
                    System.out.format("%10s | %32s | %10s | %16s\n", "city_id", "city", "country_id", "last_update");
                }
                System.out.format("%10d | %32s | %10d | %16s\n", rs.getInt("city_id"), rs.getString("city"), rs.getInt("country_id"), rs.getDate("last_update"));
            }
        }catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }


        // close db connection, with a try-catch to alert issues
        try {
            db.close();
        }catch(SQLException e){
            System.out.println("Error: "+e.getMessage());
        }
    }
}
