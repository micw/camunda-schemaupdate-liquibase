package test.db;
import java.sql.Connection;
import java.sql.DriverManager;

import liquibase.database.Database;
import liquibase.database.core.H2Database;

public class H2TestDb extends AbstractTestDb
{
    @Override
    Connection openConnection(String connectionUrl) throws Exception
    {
        new org.h2.Driver();
        return DriverManager.getConnection("jdbc:h2:mem:test");
    }
    
    @Override
    Database createLiqibaseDb()
    {
        return new H2Database();
    }
    
    @Override
    void clearDatabase() throws Exception
    {
        con.createStatement().execute("DROP ALL OBJECTS");
    }
    
}
