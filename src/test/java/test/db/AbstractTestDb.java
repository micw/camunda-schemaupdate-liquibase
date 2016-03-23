package test.db;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import liquibase.CatalogAndSchema;
import liquibase.Liquibase;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.parser.core.xml.XMLChangeLogSAXParser;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.core.xml.XMLChangeLogSerializer;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;

public abstract class AbstractTestDb
{
    protected Connection con;
    protected Database liquiDb;
    
    protected final String connectionUrl;
    
    public AbstractTestDb()
    {
        this(null);
    }
    
    public AbstractTestDb(String connectionUrl)
    {
        this.connectionUrl=connectionUrl;
        try
        {
            con = openConnection(connectionUrl);
            liquiDb=createLiqibaseDb();
            liquiDb.setConnection(new JdbcConnection(con));
            reset();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    public void reset() throws Exception
    {
        clearDatabase();
    }
    
    abstract Connection openConnection(String connectionUrl) throws Exception;
    abstract Database createLiqibaseDb();
    abstract void clearDatabase() throws Exception;

    public void applyScripts(String... paths) throws Exception
    {
        for (String path: paths)
        {
            applyScript(path);
        }
    }
    
    public void applyScript(String path) throws Exception
    {
        Statement statement=con.createStatement();
        BufferedReader in=new BufferedReader(new FileReader(path));
        String sql;
        try
        {
            while ((sql=nextStatement(in))!=null)
            {
                statement.execute(sql);
                con.commit();
            }
            
        }
        catch (SQLException ex)
        {
            throw new SQLException("In "+path+":\n:"+ex.getMessage(),ex);
        }
    }
    
    protected String nextStatement(BufferedReader in) throws Exception
    {
        // Quick&Dirty: if line ends with ";", the statement ends
        // but works  fine with the files that we use as input
        StringBuilder sb=new StringBuilder();
        String line;
        while ((line=in.readLine())!=null)
        {
            if (line.startsWith("--") && line.endsWith("--")) continue;
            sb.append(line).append("\n");
            if (line.endsWith(";")) return sb.toString();
        }
        sb.trimToSize();
        if (sb.length()>0) return sb.toString();
        return null; // EOF
    }

    public DatabaseSnapshot createSnapshot() throws Exception
    {
        CatalogAndSchema catalogAndSchema = new CatalogAndSchema(liquiDb.getDefaultCatalogName(), liquiDb.getDefaultSchemaName());
        SnapshotControl snapshotControl = new SnapshotControl(liquiDb);
        return SnapshotGeneratorFactory.getInstance().createSnapshot(catalogAndSchema, liquiDb, snapshotControl);
    }
    
    public void printDiff(DatabaseSnapshot previousSnapshot, String changeSetPrefix) throws Exception
    {
        DiffResult diff=DiffGeneratorFactory.getInstance().compare(createSnapshot(), previousSnapshot, new CompareControl());
        
        DiffToChangeLog diffToChangeLog = new DiffToChangeLog(diff, new DiffOutputControl(false,false,false));
        diffToChangeLog.setChangeSetAuthor("generated");
        diffToChangeLog.setIdRoot(changeSetPrefix);
        System.out.println();
        System.out.println("================================================================");
        System.out.println("=== "+changeSetPrefix);
        System.out.println("----------------------------------------------------------------");
        diffToChangeLog.print(System.out, new XMLChangeLogSerializer());
        System.out.println();
    }
    
    public AbstractTestDb applyChangelog(String changelog) throws Exception
    {
        ResourceAccessor accessor=new ClassLoaderResourceAccessor();
        DatabaseChangeLog cl=new XMLChangeLogSAXParser().parse("org/camunda/bpm/engine/db/liquibase/"+changelog, null, accessor);
        new Liquibase(cl,accessor,liquiDb).update((String)null);
        return this;
    }
    
    public List<String> dump() throws Exception
    {
        List<String> dump=new ArrayList<>();
        ResultSet rs=con.createStatement().executeQuery("SCRIPT NOSETTINGS");
        while (rs.next())
        {
            dump.add(rs.getString(1));
        }
        return dump;
    }

}
