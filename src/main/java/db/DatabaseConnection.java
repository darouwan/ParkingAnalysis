package db;


import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by cjf on 2014/9/15.
 */
public  class DatabaseConnection {
    private static Logger logger = Logger.getLogger(DatabaseConnection.class);
    private static Connection conn;
    private static String url = "jdbc:mysql://muzhipark.mysql.rds.aliyuncs.com:3306/muzhipark";
    private static String mysqlUsername="muzhipark";
    private static String mysqlPassword="52762568Jz";

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, mysqlUsername, mysqlPassword);
            logger.info("成功加载MySQL驱动！");
            //System.out.println("成功加载MySQL驱动！");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn(){
        return conn;
    }


    public static void main(String[] args){
        DatabaseConnection databaseConnection = new DatabaseConnection();
    }
}
