package com.autheus.authmanager.data;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Date;
import java.io.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
//import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.autheus.authmanager.xml.ResultSetXMLReader;
import com.autheus.authmanager.xml.ResultSetInputSource;

import com.autheus.framework.database.HibernateUtil;
import com.autheus.authmanager.common.Datasource;
import com.autheus.authmanager.common.Constants;
import com.autheus.authmanager.common.GroupSetData;
import com.autheus.authmanager.common.ValueText;
import com.autheus.authmanager.common.Report;
import com.autheus.authmanager.common.GroupSetAdmin;
/**
 * Created by IntelliJ IDEA.
 * User: Dave
 * Date: Feb 26, 2006
 * Time: 8:53:18 PM
 */
public class DatasourceManager {

    protected final Log logger = LogFactory.getLog(getClass());
	
    public DatasourceManager() {}

    public String getXMLFromSQL(String sql) throws Exception {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        
        StringWriter sw = new StringWriter();
        Result res = new StreamResult(sw);

        executeSQL(transformer, res, sql);

        return sw.toString();
    }    

    public String getHTMLReport(String xml, String xsltFilePath) throws Exception {
        File xslFile = new File(xsltFilePath, "reportHTML.xsl");

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

        StringWriter sw = new StringWriter();
        Result res = new StreamResult(sw);

    	Source src = new StreamSource(new StringReader(xml));

        transformer.transform(src, res);

       	return sw.toString();
    }

    public String getCSVReport(String xml, String xsltFilePath) throws Exception {
        File xslFile = new File(xsltFilePath, "reportCSV.xsl");

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

        StringWriter sw = new StringWriter();
        Result res = new StreamResult(sw);

    	Source src = new StreamSource(new StringReader(xml));

        transformer.transform(src, res);

       	return sw.toString();
    }
    
    public void getPDFReport(String xml, String xsltFilePath, String outputFilePath) throws Exception {
        File xslFile = new File(xsltFilePath, "reportPDFfo.xsl");
        File pdfFile = new File(outputFilePath, "report.pdf");

        OutputStream out = new java.io.FileOutputStream(pdfFile);
        out = new java.io.BufferedOutputStream(out);
        
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

        FopFactory fopFactory = FopFactory.newInstance();
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
        
        Result res = new SAXResult(fop.getDefaultHandler());

        Source src = new StreamSource(new StringReader(xml));

        transformer.transform(src, res);

        out.close();
    }
    
    public String getXMLXalanReport(String xml, String xsltFilePath) throws Exception {
        File xslFile = new File(xsltFilePath, "SteveMuench.xsl");

        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

        StringWriter sw = new StringWriter();
        Result res = new StreamResult(sw);

    	Source src = new StreamSource(new StringReader(xml));

        transformer.transform(src, res);
/*
        File xmlFile = new File(xsltFilePath, "report.xml");
        FileWriter fileWriter = new FileWriter(xmlFile);
        BufferedWriter buffWriter = new BufferedWriter(fileWriter);
        buffWriter.write(sw.toString());
        buffWriter.close();
*/        
       	return sw.toString();
    }

    private void executeSQL(Transformer transformer, Result res, String sql) throws Exception {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
        	con = session.connection();
        	stmt = con.createStatement();

        	rs = stmt.executeQuery(sql);
        	
        	Source src = new SAXSource(new ResultSetXMLReader(), new ResultSetInputSource(rs));

        	//Start XSLT transformation
            transformer.transform(src, res);
        	
        } finally {
        	if (rs != null)
        		rs.close();

        	if (stmt != null)
        		stmt.close();

            session.close();
        }
    }

    public Report getReport(int id) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();

        Report report = (Report)session.createCriteria(Report.class)
            		.add(Restrictions.eq("id", id)).uniqueResult();
        
        session.close();
        
        return report; 
    }
    
    public List<String[][]>getSchemaNames() throws Exception {
    	List<String[][]>list = new ArrayList<String[][]>();
    	SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
    	
        try {
        	con = session.connection();
        	stmt = con.createStatement();
        	
        	String sql = "SELECT schemaname " +
    					" FROM sys.sysschemas" +
    					" WHERE schemaname Like 'REC_0%'" +
    					" ORDER BY schemaname DESC";
        	
        	rs = stmt.executeQuery(sql);

        	String id = "", date = "", num = "";
        	
        	String schemaName = "", schemaDisplay = "";
        	while(rs.next()) {
            	schemaName = rs.getString("schemaname");
                Pattern p = Pattern.compile("_");
                Matcher m = p.matcher(schemaName);
                int i = 0;
                if (m.find())
                	i = m.end();
                
                if (m.find()) {
                	id = schemaName.substring(i, m.end() - 1);
                	i = m.end();
                }
                
                if (m.find()) {
                	date = schemaName.substring(i, m.end() - 1); 
                	i = m.end();
                }

               	num = schemaName.substring(i); 

                Date dt = (new SimpleDateFormat("ddMMyyHHmm").parse(date));

                schemaDisplay = "RID" +
                				String.format("%04d", Integer.parseInt(id)) + " " + 
                				(new SimpleDateFormat("MM-dd-yyyy HH:mm")).format(dt) + " (" +
                				String.format("%02d", Integer.parseInt(num)) + ")";
                
            	String array[][] = new String[1][2];
            	array[0][0] = schemaName;
            	array[0][1] = schemaDisplay;
            	list.add(array);
        	}
        	
        } catch(Exception e) {
        	throw new Exception(e.toString());
        } finally {
        	if (rs != null)
        		rs.close();

        	if (stmt != null)
        		stmt.close();

            session.close();
        }
        
        return list;
    }
    
    /**
     * <p>
     * Retrieves named datasource from the Id.
     * </p>
     *
     * @return list of user sources from the sources table
     */
    public Datasource getDatasource(int id) {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();

        Transaction tx = session.beginTransaction();
        Datasource sources = (Datasource)session.createCriteria(Datasource.class)
            .add(Restrictions.eq("id", new Integer(id))).uniqueResult();
        tx.commit();
        session.close();

        if (sources != null)
        	sources.setConfirmPassword(sources.getPassword());

        return sources;
    }

    /**
     * <p>
     * Retrieves user sources from the sources table.
     * </p>
     *
     * @return list of groups from the sources table
     */
    public List getGroupSets() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        List sources = session.createCriteria(Datasource.class)
            .add(Restrictions.eq("type", new Integer(Constants.DATASOURCE_TYPE_GROUP)))
            .add(Restrictions.eq("active", new Integer(1))).list();
        tx.commit();
        session.close();
        return sources;
    }

    /**
     * <p>
     * Retrieves user sources from the sources table.
     * </p>
     *
     * @return list of groups from the sources table
     */
    public List getRuleSets() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        List sources = session.createCriteria(Datasource.class)
            .add(Restrictions.eq("type", new Integer(Constants.DATASOURCE_TYPE_RULESET)))
            .add(Restrictions.eq("active", new Integer(1))).list();
        tx.commit();
        session.close();
        return sources;
    }

    public List<ValueText> getReconcileRuleSets() {
        List<ValueText> list = new ArrayList<ValueText>();
    	
    	SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
    	
    	org.hibernate.SQLQuery query = session.createSQLQuery(
    					"SELECT DISTINCT s.id as value, s.source_name as text FROM sources s"
    					+ " INNER JOIN rule_group rg ON s.id = rg.rule_set_source_id"
    					+ " INNER JOIN group_set_data g ON rg.group_source_id = g.datasource_id"
    					+ " WHERE rg.deleted_date IS NULL AND g.deleted_date IS NULL");

    	query.addScalar("value", Hibernate.STRING);
    	query.addScalar("text", Hibernate.STRING);
    	
    	List<?> result = query.list();

    	for (Iterator<?> it = result.iterator(); it.hasNext();) {
            Object[] obj = (Object[])it.next();
            ValueText vt = new ValueText();
    		vt.setValue((String)obj[0]);
    		vt.setText((String)obj[1]);
    		list.add(vt);
    	}
        session.close();

        return list;
    }

    /**
     * <p>
     * Retrieves user sources from the sources table.
     * </p>
     *
     * @return list of groups from the sources table
     */
    public List getUserTargetSets() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        List sources = session.createCriteria(Datasource.class)
            .add(Restrictions.eq("type", new Integer(Constants.DATASOURCE_TYPE_USERTARGET)))
            .add(Restrictions.eq("active", new Integer(1))).list();
        tx.commit();
        session.close();
        return sources;
    }

    /**
     * <p>
     * Retrieves user sources from the sources table.
     * </p>
     *
     * @return list of user sources from the sources table
     */
    public List getUserSourceSets() {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        Session session = factory.openSession();
        Transaction tx = session.beginTransaction();
        List sources = session.createCriteria(Datasource.class)
            .add(Restrictions.eq("type", new Integer(Constants.DATASOURCE_TYPE_USERSOURCE)))
            .add(Restrictions.eq("active", new Integer(1))).list();
        tx.commit();
        session.close();
        return sources;
    }

    /**
     * <p>
     * Saves or updates the requested datasource
     * </p>
     *
     * @param datasource
     */
    public void save(Datasource ds) {
        ds.setActive(1);
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!session.getTransaction().isActive())
        	tx = session.beginTransaction();

        if(ds.getId() == 0)
            session.save(ds);
        else
            session.update(ds);

        if (tx != null) {
            tx.commit();
        }
    }

    public void copy(Datasource ds) {
        ds.setActive(1);
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!session.getTransaction().isActive())
        	tx = session.beginTransaction();
        
        session.save(ds);
        
        if (tx != null) {
            tx.commit();
        }
    }

    @SuppressWarnings("unchecked")
	public void insertGroupSetAdmin(Datasource ds) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!session.getTransaction().isActive())
        	tx = session.beginTransaction();

        GroupSetAdmin gsa = new GroupSetAdmin();
        gsa.setId(ds.getId());
        session.save(gsa);
        
        if (tx != null) {
            tx.commit();
        }
    }
    
    /**
     * <p>
     * Handle delete
     * </p>
     */
    public void delete(Datasource datasource) {
        datasource.setActive(0);
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!session.getTransaction().isActive())
        	tx = session.beginTransaction();
        
        session.update(datasource);

        if (tx != null) {
            tx.commit();
        }
    }

    public void deleteGroupSetAdmin(Datasource ds) {
        Transaction tx = null;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!session.getTransaction().isActive())
        	tx = session.beginTransaction();
        
        GroupSetAdmin g = new GroupSetAdmin();
        g.setId(ds.getId());
        session.delete(g);
        
        if (tx != null) {
            tx.commit();
        }
    }

    public void beginTransaction() {
        org.hibernate.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (!session.getTransaction().isActive())
        	session.beginTransaction();
    }
    
    
    public void commitCurrentTransaction() {
        org.hibernate.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (session.getTransaction().isActive())
        	session.getTransaction().commit();
    	
        HibernateUtil.getSessionFactory().close();    	
    }

    public void rollbackCurrentTransaction() {
        org.hibernate.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        if (session.getTransaction().isActive())
        	session.getTransaction().rollback();    	
    	
        HibernateUtil.getSessionFactory().close();    	
    }
}