import liquibase.serializer.core.string.StringSnapshotSerializerReadable;
import liquibase.snapshot.DatabaseSnapshot;
import test.db.MySQLTestDatabase;

/**
 * Helper that creates a H2 database with the initial schemas for Camunda 7.1.
 * This is used to print a liquibase ChangeLog to the console.
 * 
 * The ChangeLog is not perfect but a good starting point for manual edits.
 * 
 * @author mwyraz
 */
public class SchemaGenerator
{
    public static void main(String[] args) throws Exception
    {
        MySQLTestDatabase testDb=new MySQLTestDatabase("jdbc:mysql://localhost/camunda_test?user=root&password=");
        DatabaseSnapshot snapshot=testDb.createSnapshot();
        testDb.applyScripts("doc/camunda_schema_7_1/activiti.mysql.create.engine.sql",
                "doc/camunda_schema_7_1/activiti.mysql.create.history.sql",
                "doc/camunda_schema_7_1/activiti.mysql.create.identity.sql"
                );
        testDb.printDiff(snapshot, "CAMUNDA-7.1-initial");
        
        snapshot=testDb.createSnapshot();
        testDb.applyScripts("doc/camunda_schema_upgrades/mysql_engine_7.1_patch_7.1.4_to_7.1.5.sql");
        testDb.printDiff(snapshot, "CAMUNDA-7.1-patch-7.1.4-to-7.1.5");

        snapshot=testDb.createSnapshot();
        testDb.applyScripts("doc/camunda_schema_upgrades/db2_engine_7.1_patch_7.1.9_to_7.1.10.sql");
        testDb.printDiff(snapshot, "CAMUNDA-7.1-patch-7.1.9-to-7.1.10");

        snapshot=testDb.createSnapshot();
        testDb.applyScripts("doc/camunda_schema_upgrades/mysql_engine_7.1_to_7.2.sql");
        testDb.printDiff(snapshot, "CAMUNDA-7.1-to-7.2");

        snapshot=testDb.createSnapshot();
        testDb.applyScripts("doc/camunda_schema_upgrades/mysql_engine_7.2_to_7.3.sql");
        testDb.printDiff(snapshot, "CAMUNDA-7.2-to-7.3");

        snapshot=testDb.createSnapshot();
        testDb.applyScripts("doc/camunda_schema_upgrades/mysql_engine_7.3_to_7.4.sql");
        testDb.printDiff(snapshot, "CAMUNDA-7.3-to-7.4");
        
        
//        H2TestDb fromDb=new H2TestDb();
//        H2TestDb toDb=new H2TestDb(SchemaLib.schema_7_1_initial);
//        fromDb.printDiff(toDb, "CAMUNDA-7.1-initial");
//        
//        fromDb=toDb.copy();
//        toDb.applyScripts(SchemaLib.schema_7_1_patch_7_1_9_to_7_1_10);
//        fromDb.printDiff(toDb, "CAMUNDA-7.1-patch-7.1.9-to-7.1.10");
//
//        fromDb=toDb.copy();
//        toDb.applyScripts(SchemaLib.schema_7_1_to_7_2);
//        fromDb.printDiff(toDb, "CAMUNDA-7.1-to-7.2");
        
    }
}
