import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.*;
import java.util.ArrayList;

import static java.lang.System.out;

public class JPAInit {

    public static EntityManagerFactory getEntityManagerFactory(){
        return Persistence.createEntityManagerFactory( "org.hibernate.webserver.jpa" );
    }

    static String database = "hiberweb";
    static String username = "postgres";
    static String password = "1385123";

    static String sqlUrl = "jdbc:postgresql://localhost/";
    static Connection connection = null;
    static Statement statement = null;

    public static void prepareDatabase() throws IllegalAccessException, InstantiationException, SQLException {
        //System.out.print(System.getProperty("java.class.path"));
        tryDriver();
        connection = DriverManager.getConnection(sqlUrl, username, password);
        statement = connection.createStatement();
        dropDatabase(database);
        createDatabase(database);
    }


    public static void createDatabase(String database) throws SQLException {
        if (getDatabasesList().contains(database)) {
            //out.print("Database " + database + " allready exists.");
        }else {
            int result = statement.executeUpdate("CREATE DATABASE " + database);
            out.println("result of 'CREATE DATABASE '" + database + " is " + result);
        }
    }

    public static void dropDatabase(String database) throws SQLException {
        if (!getDatabasesList().contains(database)) {
            //out.print("Database " + database + " allready exists.");
        }else {
            int result = statement.executeUpdate("DROP DATABASE " + database);
            out.println("result of 'DROP DATABASE '" + database + " is " + result);
        }
    }

    private static ArrayList<String> getDatabasesList() throws SQLException {
        ResultSet rset = statement.executeQuery("select * from pg_database;");
        ArrayList<String> databaseList = new ArrayList<String>();
        while (rset.next()) {
            databaseList.add(rset.getString(1));
        }
        return databaseList;
    }

    public  static  void tryDriver() throws InstantiationException, IllegalAccessException {
        try {
            Class.forName("org.postgresql.Driver");
            //Class.forName("java.sql.Driver");
        } catch(ClassNotFoundException e) {
            out.println("Class not found: " + e.getMessage());
      }
    }

}
