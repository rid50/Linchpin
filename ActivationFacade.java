package com.autheus.authmanager.facade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.List;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Enumeration;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

import com.autheus.authmanager.web.controller.ActivationController;
import com.autheus.authmanager.common.Constants;
import com.autheus.authmanager.common.Activation;
import com.autheus.authmanager.common.Datasource;
import com.autheus.authmanager.common.ReconcileUser;
import com.autheus.authmanager.common.UserSource;
import com.autheus.authmanager.common.CurrentStateGroups;

import com.autheus.authmanager.facade.SourcesFacade;
import com.autheus.authmanager.data.DatasourceManager;
import com.autheus.authmanager.data.SetDataManager;

public class ActivationFacade {
    private Log log = LogFactory.getLog(ActivationFacade.class);

   	private Properties prop = null;
	private JdbcTemplate jdbcTemplate;
	private String schemaName = null;
	private String schemaDisplay = null;
	private String reconcileId = null;
	private boolean _logOutput = false;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void createSchema(Activation command, ActivationController callback) throws Exception {
		if (command.getId() == 0) {
			String[] ids = command.getRowId().split(",");
			this.reconcileId = (String)jdbcTemplate.queryForObject("select id from authmgr.reconcile where" +
												" rule_set_id=" + ids[0] +
												" and usersource_id=" + ids[1] +
												" and usertarget_id=" + ids[2], java.lang.String.class);
		} else
			this.reconcileId = String.valueOf(command.getId());

			if (callback != null)
       		callback.setProgressReportStartDate();

		String recId = String.format("%04d", Integer.parseInt(this.reconcileId));
       	
		java.util.Date date = new java.util.Date();
       	Format formatter = new SimpleDateFormat("ddMMyyHHmm");
       	String schemaName = ("rec_" + recId + "_" +
       						formatter.format(date)).toUpperCase() + "_";
       	
       	schemaDisplay = "RECONCILE ID" + recId + " " + 
       					(new SimpleDateFormat("MM-dd-yyyy HH:mm").format(date));

						prop = new Properties();
       	InputStream stream = this.getClass().getResourceAsStream("/sql.properties");
       	prop.load(stream);
       	stream.close();
       	
        List rows = jdbcTemplate.queryForList("SELECT schemaname " +
        		" FROM sys.sysschemas" +
				" WHERE schemaname Like '" + schemaName + "0%1%'" + " OR " +
				"       schemaname Like '" + schemaName + "0%2%'" + " OR " +
				"       schemaname Like '" + schemaName + "0%3%'" + " OR " +
				"       schemaname Like '" + schemaName + "0%4%'" + " OR " +
				"       schemaname Like '" + schemaName + "0%5%'" + " OR " +
				"       schemaname Like '" + schemaName + "0%6%'" + " OR " +
				"       schemaname Like '" + schemaName + "0%7%'" + " OR " +
				"       schemaname Like '" + schemaName + "0%8%'" + " OR " +
				"       schemaname Like '" + schemaName + "0%9%'" +
				" ORDER BY schemaname DESC");		

        
        String name = null;
        if (rows.size() != 0){
        	Iterator it = rows.iterator();
        	while (it.hasNext()) {
        		Map map = (Map)it.next();
        		name = (String)map.get("schemaname".toUpperCase());
        		try {
        			Integer.parseInt(name.substring(schemaName.length()));
        		} catch (NumberFormatException e) {
        			name = null;
        			continue;
        		}
        		
        		break;
        	}
        }
        
        int n = 1;
        if (name != null) {
        	n = Integer.parseInt(name.substring(schemaName.length()));
        	n++;
        }	

        int i = 0;
        i = 2;
       	schemaName += String.format("%0" + i + "d", n);

       	out(callback, "START " + schemaDisplay);
       	
        String sql = prop.getProperty("createSchema");
		sql = sql.replaceFirst("\\?", schemaName);
		jdbcTemplate.execute(sql);

		this.schemaName = schemaName;
	}
	
	private void execute(String[] sql, String[] param) throws Exception {
        StringBuilder stmt = new StringBuilder();
        for (int i = 0; i < sql.length; i++) {
        	boolean batch = sql[i].endsWith("*");
        	if (batch)
        		sql[i] = sql[i].substring(0, sql[i].length() - 1); 
        	
            int j, k;
            Enumeration en = prop.keys();
        	while (!batch || (batch && en.hasMoreElements())) {
        		String key;
        		if (batch)
        			key = (String)en.nextElement();
        		else
        			key = sql[i];
        			
        		if (key.startsWith(sql[i])) {
        			stmt.append(prop.getProperty(key));
        	        for (j = 0; param != null && j < param.length; j++) {
       	        		if ((k = stmt.indexOf("?")) == -1)
       	        			break;

       	        		stmt.replace(k, k + 1, param[j]);
        	        }
        	        
        			jdbcTemplate.execute(stmt.toString());
        			stmt.setLength(0);
        			
        			if (!batch)
        				break;
        		}
        	}
        }
	}

	private void execute(String key, String param) throws Exception {
        StringBuilder stmt = new StringBuilder();
		stmt.append(prop.getProperty(key));

		int k;
		while (true) {
       		if ((k = stmt.indexOf("?")) == -1)
       			break;

       		stmt.replace(k, k + 1, param);
        }

		jdbcTemplate.execute(stmt.toString());
	}
	
	private List queryForList(String key, String param) throws Exception {
        StringBuilder stmt = new StringBuilder();
		stmt.append(prop.getProperty(key));

		int k;
		while (true) {
       		if ((k = stmt.indexOf("?")) == -1)
       			break;

       		stmt.replace(k, k + 1, param);
        }

        return jdbcTemplate.queryForList(stmt.toString());
	}
	
	private List queryForList(String key, String[] param) throws Exception {
        StringBuilder stmt = new StringBuilder();
		stmt.append(prop.getProperty(key));

		int k;
		for (int j = 0; param != null && j < param.length; j++) {
       		if ((k = stmt.indexOf("?")) == -1)
       			break;

       		stmt.replace(k, k + 1, param[j]);
        }

        return jdbcTemplate.queryForList(stmt.toString());
	}
	
	public void reconcileProcess(Activation command, ActivationController callback) throws Exception {
		String[] ids = command.getRowId().split(",");
		String ruleSetId = ids[0];
		String userSourceId = ids[1];
		String userTargetId = ids[2];
		List list = null;
		
        DatasourceManager dsm = new DatasourceManager();
        Datasource ds = dsm.getDatasource(Integer.parseInt(ruleSetId));
        
        if (ds == null)
        	return;

        SourcesFacade sourcesFacade = new SourcesFacade();
        SetDataManager setDataManager = new SetDataManager();
        
        String[] param;
        String[] dml;
        
        // ------------------------------------------------------------------------------------------------        
        /* copy AUTHMGR.RULE_SET_DATA to RECONCILEXXX.AUTHMGR_RULE_SET_DATA  */
       	out(callback, "SAVING COPY OF SAVED RULE SET DATA");
//        callback.setRunProgressAttribute("SAVING COPY OF SAVED RULE SET DATA");

        param = new String[]{this.schemaName, ruleSetId, String.valueOf(Constants.DATASOURCE_TYPE_RULESET)};
        dml = new String[]{"insert.authmgr_rule_set_to_reconcile_authmgr_rule_set"};
        execute(dml, param);
        /* copy AUTHMGR.RULE_SET_DATA to RECONCILEXXX.AUTHMGR_RULE_SET_DATA  */

        // ------------------------------------------------------------------------------------------------        
        /* fetch RULE_SET_DATA into RECONCILEXXX.RULE_SET_DATA  */
       	out(callback, "SAVING COPY OF DEFINED RULE SET DATA");
//       	callback.setRunProgressAttribute("SAVING COPY OF DEFINED RULE SET DATA");

        Set set = sourcesFacade.getRuleSetData(ds);
        
        setDataManager.saveRuleSetDataReconcile(set, this.schemaName, null);
        /* fetch RULE_SET_DATA into RECONCILEXXX.RULE_SET_DATA  */

        // ------------------------------------------------------------------------------------------------        
        /* copy AUTHMGR.GROUP_SET_DATA to RECONCILEXXX.AUTHMGR_GROUP_SET_DATA  */
       	out(callback, "SAVING COPY OF SAVED GROUP SET DATA");
//       	callback.setRunProgressAttribute("SAVING COPY OF SAVED GROUP SET DATA");

        param = new String[]{this.schemaName, ruleSetId};
        dml = new String[]{"insert.authmgr_group_set_to_reconcile_authmgr_group_set"};
        execute(dml, param);
        /* copy AUTHMGR.GROUP_SET_DATA to RECONCILEXXX.AUTHMGR_GROUP_SET_DATA  */

        // ------------------------------------------------------------------------------------------------        
        /* fetch GROUP_SET_DATA into RECONCILEXXX.GROUP_SET_DATA  */
       	out(callback, "SAVING COPY OF DEFINED GROUP SET DATA");
//       	callback.setRunProgressAttribute("SAVING COPY OF DEFINED GROUP SET DATA");
        
       	/* !!!!!!!!!!!!!!!!!!! this list is engaged in the next step !!!!!!!!!!!!!!!!!!!!!!!!!! */
        list = setDataManager.getReconcileGroupSetIds(this.schemaName, "AUTHMGR_GROUP_SET_DATA");        
        if (list.size() != 0) {
        	Iterator<Integer> it = list.iterator();
        	while (it.hasNext()) {
        		Integer id = (Integer)it.next();
        		ds = dsm.getDatasource(id);
                
                List groupList = sourcesFacade.getGroupData(ds, "");
                setDataManager.saveGroupSetDataReconcile(groupList, id, this.schemaName, null);
        	}
        }
        /* fetch GROUP_SET_DATA into RECONCILEXXX.GROUP_SET_DATA  */
        
        // ------------------------------------------------------------------------------------------------        
        /* fetch SOURCES into RECONCILEXXX.AUTHMGR_SOURCES  */
       	out(callback, "SAVING COPY OF DEFINED SOURCES");
//       	callback.setRunProgressAttribute("SAVING COPY OF DEFINED GROUP SET DATA");
        
        String groupSetIds = "";
        if (list.size() != 0) {
        	Iterator<Integer> it = list.iterator();
        	while (it.hasNext()) {
        		groupSetIds += "," + String.valueOf(it.next());
        	}
        }
        
        param = new String[]{this.schemaName, ruleSetId + ","
        									+ userTargetId + ","
        									+ userSourceId
        									+ groupSetIds};
        dml = new String[]{"insert.into_authmgr_sources"};
        execute(dml, param);
        
        /* fetch SOURCES into RECONCILEXXX.AUTHMGR_SOURCES  */
        
        // ------------------------------------------------------------------------------------------------        
        /* copy AUTHMGR.RULE_GROUP to RECONCILEXXX.AUTHMGR_RULE_GROUP  */
       	out(callback, "SAVING COPY OF SAVED RULE GROUP ASIGNMENTS");
//       	callback.setRunProgressAttribute("SAVING COPY OF SAVED RULE GROUP ASIGNMENTS");

        param = new String[]{this.schemaName, ruleSetId};
        dml = new String[]{"insert.authmgr_rule_group_to_reconcile_authmgr_rule_group"};
        execute(dml, param);
        /* copy AUTHMGR.RULE_GROUP to RECONCILEXXX.AUTHMGR_RULE_GROUP  */

        // ------------------------------------------------------------------------------------------------        
        /* copy AUTHMGR.EXCEPTIONS to RECONCILEXXX.AUTHMGR_EXCEPTIONS  */
       	out(callback, "SAVING COPY OF SAVED EXCEPTION ASSIGNMENTS");
//       	callback.setRunProgressAttribute("SAVING COPY OF SAVED EXCEPTION ASSIGNMENTS");

        param = new String[]{this.schemaName, userTargetId, ruleSetId};
        dml = new String[]{"insert.authmgr_exceptions_to_reconcile_authmgr_exceptions"};
        execute(dml, param);
        /* copy AUTHMGR.EXCEPTIONS to RECONCILEXXX.AUTHMGR_EXCEPTIONS  */

        
        // ------------------------------------------------------------------------------------------------        
        /* copy AUTHMGR.BUSINESS_EXCEPTIONS to RECONCILEXXX.AUTHMGR_BUSINESS_EXCEPTIONS  */
       	out(callback, "SAVING COPY OF SAVED BUSINESS_EXCEPTION ASSIGNMENTS");
//       	callback.setRunProgressAttribute("SAVING COPY OF SAVED EXCEPTION ASSIGNMENTS");

        param = new String[]{this.schemaName, userSourceId, ruleSetId};
        dml = new String[]{"insert.authmgr_business_exceptions_to_reconcile_authmgr_business_exceptions"};
        execute(dml, param);
        /* copy AUTHMGR.BUSINESS_EXCEPTIONS to RECONCILEXXX.AUTHMGR_BUSINESS_EXCEPTIONS  */

        
        // ------------------------------------------------------------------------------------------------        
        /* fetch USER_SOURCE into RECONCILEXXX.USER_SOURCE  */
       	out(callback, "SAVING COPY OF DEFINED USER SOURCE");
//       	callback.setRunProgressAttribute("SAVING COPY OF DEFINED USER SOURCE");

    	list.clear();
       	
        ds = dsm.getDatasource(Integer.parseInt(userSourceId));

        list = sourcesFacade.getUserSourceList(ds, "");
        if (list.size() != 0)
        	setDataManager.saveUserSourceReconcile(list, this.schemaName, null);
        
        /* fetch USER_SOURCE into RECONCILEXXX.USER_SOURCE  */

        // ------------------------------------------------------------------------------------------------        
        /* fetch USER_TARGET into RECONCILEXXX.USER_TARGET  */
       	out(callback, "SAVING COPY OF DEFINED USER TARGET");
//       	callback.setRunProgressAttribute("SAVING COPY OF DEFINED USER TARGET");

       	list.clear();

        ds = dsm.getDatasource(Integer.parseInt(userTargetId));
        list = sourcesFacade.getGroupData(ds, "");
        setDataManager.saveUserTargetReconcile(list, this.schemaName, null);
        /* fetch USER_TARGET into RECONCILEXXX.USER_TARGET  */
        
        // ------------------------------------------------------------------------------------------------        
        /* insert into RECONCILE_USER  */
       	out(callback, "RECONCILING USERS");
//       	callback.setRunProgressAttribute("RECONCILING USERS");
        
        param = new String[]{this.schemaName, ruleSetId, this.schemaName, this.schemaName};
        dml = new String[]{"insert.into_reconcile_user"};
        execute(dml, param);
        /* insert into RECONCILE_USER  */

        // ------------------------------------------------------------------------------------------------        
        /* process BUSINESS_EXCEPTIONS  */
       	out(callback, "PROCESS BUSINESS_EXCEPTIONS");
        
        param = new String[]{this.schemaName, this.schemaName, this.schemaName, ruleSetId, userSourceId};
        list =  queryForList("select.process_business_exceptions", param);
        
        if (list.size() != 0) {
        	Iterator<?> it = list.iterator();
        	List<ReconcileUser> userListInc = new ArrayList<ReconcileUser>();
        	List<ReconcileUser> userListExc = new ArrayList<ReconcileUser>();
        	while (it.hasNext()) {
        		Map map = (Map)it.next();

        		String value = String.valueOf(map.get("VALUE"));
        		String incOrExc = String.valueOf(map.get("INC_OR_EXC"));
        		
        		if ((incOrExc.toLowerCase().equals("e") && 
        			!value.equals((String)map.get("USER_SOURCE_RULE_CODE_VALUE"))) ||
        			(incOrExc.toLowerCase().equals("i") &&
            		value.equals((String)map.get("USER_SOURCE_RULE_CODE_VALUE"))))
        			continue;
        				
           		ReconcileUser user = new ReconcileUser();
           		user.setRunDateTime((Date)map.get("RUN_DATETIME"));
           		user.setRuleSetSourceId(((Integer)map.get("RULE_SET_SOURCE_ID")).intValue());
           		user.setUserSourceId(((Integer)map.get("USER_SOURCE_ID")).intValue());
           		user.setUserTargetId(((Integer)map.get("USER_TARGET_ID")).intValue());
           		user.setUserSourceFirstName((String)map.get("USER_SOURCE_FIRST_NAME"));
           		user.setUserSourceLastName((String)map.get("USER_SOURCE_LAST_NAME"));
           		user.setUserTargetFirstName((String)map.get("USER_TARGET_FIRST_NAME"));
           		user.setUserTargetLastName((String)map.get("USER_TARGET_LAST_NAME"));
           		user.setUserSourceDn((String)map.get("USER_SOURCE_DN"));
           		user.setUserTargetDn((String)map.get("USER_TARGET_DN"));
           		user.setUserSourceGuid((String)map.get("USER_SOURCE_GUID"));
           		user.setUserTargetGuid((String)map.get("USER_TARGET_GUID"));

           		if (incOrExc.toLowerCase().equals("e")) {
               		user.setId(((Integer)map.get("ID")).intValue());
        			user.setUserSourceRuleCodeValue(null);
           		} else
        			user.setUserSourceRuleCodeValue(value);
        			
        		user.setUserSourceTargetCodeValue((String)map.get("USER_SOURCE_TARGET_CODE_VALUE"));
           		user.setUserTargetSourceCodeValue((String)map.get("USER_TARGET_SOURCE_CODE_VALUE"));

           		if (incOrExc.toLowerCase().equals("e"))
        			userListExc.add(user);
        		else
               		userListInc.add(user);
        	}
        	
        	if (userListExc.size() != 0)
        		setDataManager.updateReconcileUsers(userListExc, true, this.schemaName); 	
        	
        	if (userListInc.size() != 0)
        		setDataManager.updateReconcileUsers(userListInc, false, this.schemaName); 	
        }
        
        /* process BUSINESS_EXCEPTIONS  */
        
        // ------------------------------------------------------------------------------------------------        
        /* insert into RECONCILE_GROUPS_USERS  */
//       	callback.setRunProgressAttribute("create view V_RECONCILE_GROUPS_USERS");

//       	execute("create_view.select_reconcile_groups_users", this.schemaName);

       	out(callback, "RECONCILING USERS RULE ASSIGNED GROUPS");
//       	callback.setRunProgressAttribute("RECONCILING USERS RULE ASSIGNED GROUPS");

       	execute("insert.into_reconcile_groups_users", this.schemaName);
       	execute("insert.into_reconcile_groups_users2", this.schemaName);
        /* insert into RECONCILE_GROUPS_USERS  */

        // ------------------------------------------------------------------------------------------------        
        /* fetch GROUP_SET_DATA into CURRENT_STATE_GROUPS  */
       	out(callback, "SAVING CURRENT STATE GROUPS");
//       	callback.setRunProgressAttribute("SAVING CURRENT STATE GROUPS");

        param = new String[]{this.schemaName};
        list =  queryForList("select.group_set_data", param);
        
        if (list.size() != 0) {
        	Iterator it = list.iterator();
        	List<CurrentStateGroups> current = new ArrayList<CurrentStateGroups>();
        	ds = null;
        	while (it.hasNext()) {
        		Map map = (Map)it.next();
        		int datasourceId = Integer.parseInt(String.valueOf(map.get("DATASOURCE_ID")));
                ds = dsm.getDatasource(datasourceId);
        		String groupDn = String.valueOf(map.get("DN"));
                
                List groupMembers = sourcesFacade.getGroupMembers(ds, groupDn);

                if (groupMembers.size() != 0) {
                	Iterator it2 = groupMembers.iterator();
                	while (it2.hasNext()) {
                		String userDn = (String)it2.next();
                		CurrentStateGroups currentState = new CurrentStateGroups();
                		currentState.setRunDateTime(new Date());
                		currentState.setDatasourceId(datasourceId);
                		currentState.setGroupDn(groupDn);
                		currentState.setUserDn(userDn);
                		
                		current.add(currentState);
                		
                	}
                }                	
        	}
        	
        	if (current.size() != 0)
        		setDataManager.saveCurrentStateGroupsReconcile(current, this.schemaName, null);
        	
        }
        
        /* fetch GROUP_SET_DATA into CURRENT_STATE_GROUPS  */

        // ------------------------------------------------------------------------------------------------        
        /* insert into CHANGES_GROUPS_USERS  */
       	out(callback, "SAVING CHANGES TO USERS AND GROUPS");
//       	callback.setRunProgressAttribute("SAVING CHANGES TO USERS AND GROUPS");

       	execute("insert.into_changes_groups_users", this.schemaName);
       	execute("insert.into_changes_groups_users2", this.schemaName);

        /* insert into CHANGES_GROUPS_USERS  */
        
        // ------------------------------------------------------------------------------------------------
        /* update LDAP groups from CHANGES_GROUPS_USERS  */
       	out(callback, "UPDATE GROUPS AND USERS FROM CHANGES TO USERS AND GROUPS");
//       	callback.setRunProgressAttribute("UPDATE GROUPS AND USERS FROM CHANGES TO USERS AND GROUPS");

        param = new String[]{this.schemaName};
        list =  queryForList("select.changes_groups_users", param);
/*       	
        sql = prop.getString("select.changes_groups_users");
		sql = sql.replaceFirst("\\?", this.schemaName);
        list = jdbcTemplate.queryForList(sql);
*/		
        if (list.size() != 0) {
        	Iterator it = list.iterator();
        	while (it.hasNext()) {
        		Map map = (Map)it.next();
        		int datasourceId = Integer.parseInt(String.valueOf(map.get("DATASOURCE_ID")));
        		String groupDn = String.valueOf(map.get("GROUP_DN"));
        		String userDn = String.valueOf(map.get("USER_DN"));
        		char operation = String.valueOf(map.get("OPERATION")).charAt(0);
                ds = dsm.getDatasource(datasourceId);
                
                if (operation == 'A')
                	sourcesFacade.setGroupMember(ds, groupDn, userDn);
                else if (operation == 'D')
                    sourcesFacade.removeGroupMember(ds, groupDn, userDn);
        	}
        }
        
        /* update LDAP groups from CHANGES_GROUPS_USERS  */

        // ------------------------------------------------------------------------------------------------        
        /* insert into AUTHMGR_REPORTS  */
       	out(callback, "SAVING REPORTS SNAPSHOTS");
//       	callback.setRunProgressAttribute("SAVING CHANGES TO USERS AND GROUPS");

        param = new String[]{this.schemaName, this.reconcileId};
        dml = new String[]{"insert.into_authmgr_reports"};
        execute(dml, param);

        /* insert into AUTHMGR_REPORTS  */
        
        // ------------------------------------------------------------------------------------------------        
        /* insert into EXECUTE REPORTS  */
       	out(callback, "REPORTS' EXECUTION");
        
        setDataManager.runReconcileReports(schemaName, Integer.parseInt(reconcileId));
        
       	/* insert into EXECUTE REPORTS  */

        // ------------------------------------------------------------------------------------------------        
       	out(callback, "COMPLETE " + schemaDisplay);
//       	callback.setRunProgressAttribute("COMPLETE " + schemaDisplay);
	}
	
	public synchronized void reconcile(Activation command, ActivationController callback, boolean logOutput) 
		throws Exception {

		_logOutput = logOutput;
		try {
			createSchema(command,  callback);
		
			String[] param = new String[]{this.schemaName};
			String[] ddl = new String[]{"create_table*",
									"create_index*",
									"create_key*"
									};

			execute(ddl, param);
			reconcileProcess(command, callback);
		} catch (Exception ex) {
			dropCurrentSchema();
//			Throwable exd = ex.getCause();
	       	out(callback, "ERROR " + ex.toString());
	       	if (callback != null)
	       		throw new Exception("--- ERROR --- " + ex.toString());
		}
		
//    	executeInserts(command.getRuleSets());
	}

	public synchronized void reconcile(DataSource dataSource, 
										Activation command, 
										ActivationController callback, boolean logOutput) throws Exception {
		setDataSource(dataSource);
		reconcile(command, callback, logOutput);
	}	
	
	public void dropCurrentSchema() throws Exception {
	    String schemaId = (String)jdbcTemplate.queryForObject("SELECT schemaid " +
				" FROM sys.sysschemas" +
				" WHERE schemaname = '" + this.schemaName + "'", java.lang.String.class);
		
	    List rowsT = jdbcTemplate.queryForList("SELECT tablename " +
				" FROM sys.systables" +
				" WHERE schemaid = '" + schemaId + "'");
	    
	    String tablename;
	    if (rowsT.size() != 0){
	    	Iterator itT = rowsT.iterator();
	    	while (itT.hasNext()) {
	    		Map mapT = (Map)itT.next();
	    		tablename = this.schemaName + " ." + (String)mapT.get("tablename".toUpperCase());
	    		jdbcTemplate.execute("DROP TABLE " + tablename);
	    	}
	    }    	
	    jdbcTemplate.execute("DROP SCHEMA " + this.schemaName + " RESTRICT");
	}
	
	public void deleteActivationHistoryRow(Activation command) throws Exception {
		String[] ids = command.getRowId().split(",");
		jdbcTemplate.execute("delete from authmgr.reconcile where" +
								" rule_set_id=" + ids[0] +
								" and usersource_id=" + ids[1] +
								" and usertarget_id=" + ids[2]);
	}
	
	private void out(ActivationController callback, String text) {
       	if (callback != null)
       		callback.setRunProgressAttribute(text, true);

		if (text.startsWith("START")
			|| text.startsWith("COMPLETE")
			|| text.startsWith("ERROR")) {
			if(_logOutput && log.isInfoEnabled()) {
				if (text.startsWith("ERROR"))
					log.error(text.substring(6));
				else
					log.info(text);
			}
		}
	}
}
