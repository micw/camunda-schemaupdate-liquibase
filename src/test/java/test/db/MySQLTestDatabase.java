package test.db;
import java.sql.Connection;
import java.sql.DriverManager;

import liquibase.database.Database;
import liquibase.database.core.H2Database;
import liquibase.database.core.MySQLDatabase;

public class MySQLTestDatabase extends AbstractTestDb
{
    public MySQLTestDatabase(String connectionUrl)
    {
        super(connectionUrl);
    }
    
    @Override
    Connection openConnection(String connectionUrl) throws Exception
    {
        new com.mysql.jdbc.Driver();
        return DriverManager.getConnection(connectionUrl);
    }
    
    @Override
    Database createLiqibaseDb()
    {
        return new MySQLDatabase();
    }
    
    @Override
    void clearDatabase() throws Exception
    {
        String dbName=con.getCatalog();
        con.createStatement().execute("DROP DATABASE "+dbName);
        con.createStatement().execute("CREATE DATABASE "+dbName);
        con.createStatement().execute("USE "+dbName);
    }
    
}
