package org.camunda.bpm.engine.db.liquibase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.ChangeLogParseException;
import liquibase.parser.core.xml.XMLChangeLogSAXParser;
import liquibase.resource.ClassLoaderResourceAccessor;

public class CamundaChangeLogLoader
{
    /**
     * Loads all change sets up to the camunda version found in classpath.
     * @return
     */
    public static DatabaseChangeLog loadChangeLog() throws ChangeLogParseException
    {
        return null;
    }
    
    protected static final String[] CAMUNDA_TABLES={
            "ACT_GE_BYTEARRAY","ACT_GE_PROPERTY","ACT_HI_ACTINST","ACT_HI_ATTACHMENT","ACT_HI_CASEACTINST",
            "ACT_HI_CASEINST","ACT_HI_COMMENT","ACT_HI_DETAIL","ACT_HI_INCIDENT","ACT_HI_JOB_LOG",
            "ACT_HI_OP_LOG","ACT_HI_PROCINST","ACT_HI_TASKINST","ACT_HI_VARINST","ACT_ID_GROUP",
            "ACT_ID_INFO","ACT_ID_MEMBERSHIP","ACT_ID_USER",
            "ACT_RE_CASE_DEF","ACT_RE_DEPLOYMENT","ACT_RE_PROCDEF",
            "ACT_RU_AUTHORIZATION","ACT_RU_CASE_EXECUTION","ACT_RU_CASE_SENTRY_PART","ACT_RU_EVENT_SUBSCR",
            "ACT_RU_EXECUTION","ACT_RU_FILTER","ACT_RU_IDENTITYLINK","ACT_RU_INCIDENT",
            "ACT_RU_JOB","ACT_RU_JOBDEF","ACT_RU_METER_LOG","ACT_RU_TASK","ACT_RU_VARIABLE"
    };
    
    /**
     * Returns a list of all tables that are used by camunda (version independent)
     * This might be usefull for exluding those from database diffs
     */
    public static String[] getCamundaTables()
    {
        return Arrays.copyOf(CAMUNDA_TABLES,CAMUNDA_TABLES.length);
    }
    
    /**
     * Loads all change sets up to the given camunda version.
     * Version must be in maven format:
     * - 7.3.1
     * - or 7.3.0-alpha1
     * The 2nd is interpreted as 7.3.0 
     */
    public static DatabaseChangeLog loadChangeLog(String camundaVersion) throws ChangeLogParseException
    {
        List<String> changeLogsToLoad=new ArrayList<>();
        switch (camundaVersion)
        {
            case "7.1.0":
                changeLogsToLoad.add("camunda_schema_7.1_initial.xml");
                changeLogsToLoad.add("camunda_schema_7.1_patch_7.1.4_to_7.1.5.xml");
                changeLogsToLoad.add("camunda_schema_7.1_patch_7.1.9_to_7.1.10.xml");
                break;
            case "7.2.0":
                changeLogsToLoad.add("camunda_schema_7.1_initial.xml");
                changeLogsToLoad.add("camunda_schema_7.1_patch_7.1.4_to_7.1.5.xml");
                changeLogsToLoad.add("camunda_schema_7.1_patch_7.1.9_to_7.1.10.xml");
                changeLogsToLoad.add("camunda_schema_7.1_to_7.2.xml");
                break;
            case "7.3.0":
                changeLogsToLoad.add("camunda_schema_7.1_initial.xml");
                changeLogsToLoad.add("camunda_schema_7.1_patch_7.1.4_to_7.1.5.xml");
                changeLogsToLoad.add("camunda_schema_7.1_patch_7.1.9_to_7.1.10.xml");
                changeLogsToLoad.add("camunda_schema_7.1_to_7.2.xml");
                changeLogsToLoad.add("camunda_schema_7.2_to_7.3.xml");
                break;
            default:
                throw new RuntimeException("The list of required changelogs for camunda version "+camundaVersion+" is not known");
        }
        XMLChangeLogSAXParser parser=new XMLChangeLogSAXParser();
        DatabaseChangeLog combinedChangeLog=new DatabaseChangeLog();
        String prefix=(CamundaChangeLogLoader.class.getPackage().getName().replace('.', '/'))+"/";
        ClassLoaderResourceAccessor resourceAccessor=new ClassLoaderResourceAccessor();
        for (String cl: changeLogsToLoad)
        {
            DatabaseChangeLog loadedCl=parser.parse(prefix+cl, null, resourceAccessor);
            // see liquibase.changelog.DatabaseChangeLog.include(String, boolean, ResourceAccessor)
            if (loadedCl.getPreconditions()!=null && loadedCl.getPreconditions().getNestedPreconditions().size()>0)
            {
                throw new ChangeLogParseException("Global preconditions apply to ALL changesets (and ALL changelogs) - therfore global changesets are not supported.");
            }
            for (ChangeSet changeSet : loadedCl.getChangeSets())
            {
                combinedChangeLog.addChangeSet(changeSet);
            }
        }
        return combinedChangeLog;
    }
    
}
