package test;
import org.junit.Test;

import test.db.H2TestDb;

public class TestSchemaUpdateValidity
{
    @Test
    public void testIntial71() throws Exception
    {
//        MySQLTestDatabase testDb=new MySQLTestDatabase("jdbc:mysql://localhost/camunda_test?user=root&password=");
        H2TestDb testDb=new H2TestDb();
        testDb.applyChangelog("camunda_schema_7.1_initial.xml");
        testDb.applyChangelog("camunda_schema_7.1_patch_7.1.4_to_7.1.5.xml");
        testDb.applyChangelog("camunda_schema_7.1_patch_7.1.9_to_7.1.10.xml");
        testDb.applyChangelog("camunda_schema_7.1_to_7.2.xml");
        testDb.applyChangelog("camunda_schema_7.2_to_7.3.xml");
//        testDb.applyChangelog("camunda_schema_7.3_to_7.4.xml");
        
////        testDb.reset();
//        
//        testDb.applyScripts(
//                "doc/camunda_schema_7_1/activiti.mysql.create.engine.sql",
//                "doc/camunda_schema_7_1/activiti.mysql.create.history.sql",
//                "doc/camunda_schema_7_1/activiti.mysql.create.identity.sql"
//          );
        
        
        
//        AbstractTestDb testDb=new H2TestDb();
//        testDb.applyScripts("doc/camunda_schema_7_1/activiti.h2.create.engine.sql",
//                "doc/camunda_schema_7_1/activiti.h2.create.history.sql",
//                "doc/camunda_schema_7_1/activiti.h2.create.identity.sql"
//                );
//        List<String> dumpRef=testDb.dump();
//        
//        testDb.reset();
//        testDb.applyChangelog("camunda_schema_7_1.xml");
//
//        List<String> dumpActual=testDb.dump();
//        
//        System.err.println(dumpRef.size());
//        System.err.println(dumpActual.size());
        
    }
}
