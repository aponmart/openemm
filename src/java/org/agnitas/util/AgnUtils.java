/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.util;

import java.util.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.dbcp.*;
import org.apache.struts.action.*;
import javax.sql.*;
import java.sql.*;
import java.text.*;
import org.apache.commons.dbcp.*;
import org.apache.commons.pool.*;
import org.apache.commons.pool.impl.*;
import org.apache.commons.mail.*;
import org.apache.log4j.*;
import org.hibernate.*;
import org.hibernate.cfg.*;
import org.hibernate.event.*;
import org.hibernate.event.def.*;
import org.agnitas.beans.*;
import org.apache.commons.lang.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.support.rowset.*;
import bsh.*;



/**
 *
 * @author  mhe
 */
public class AgnUtils {
    
    /** 
     * Creates a new instance of AgnUtils 
     */
    public AgnUtils() {
    }
    
    /**
     * constant
     */
    protected static long lastErrorMailingSent=0;
    
    public static Logger logger() {
        return Logger.getLogger("org.agnitas");
    }

    /**
     * Getter for property stackTrace.
     * 
     * @return Value of property stackTrace.
     */
    public static String getStackTrace(Exception e) {
        String trace="";
        StackTraceElement[] st=e.getStackTrace();

        for(int c=0; c < st.length && c < 10; c++) {
            trace+=st[c].toString()+"\n";
        }
        return trace;
    }
  
    /**
     * returns a date string
     */
    public static String sqlDateString(String field, String format) {
        String ret="";
        org.hibernate.dialect.Dialect dialect=AgnUtils.getHibernateDialect();

        if(dialect instanceof org.hibernate.dialect.MySQLDialect) {
            format.replaceAll("yyyy", "%Y"); 
            format.replaceAll("yy", "%y"); 
            format.replaceAll("mm", "%m"); 
            format.replaceAll("dd", "%d"); 
            ret="date_format("+field+", '"+format+"')";
        } else {
            ret="to_char("+field+", '"+format+"')";
        }
        return ret;
    }

    /**
     * Converts an object to string with separator.
     */
    public static String join(Object[] values, String separator) {
        StringBuffer ret=new StringBuffer("");

        for(int c=0; c < values.length; c++) {
            if(c > 0) {
                ret.append(separator);
            } 
            ret.append(values[c].toString());
        }
        return ret.toString();
    }
 
    // only return true every 30 Seconds
    public static synchronized boolean sendErrorMailingInterval() {
        boolean result=false;
        if(System.currentTimeMillis()-AgnUtils.lastErrorMailingSent>600000) {
            AgnUtils.lastErrorMailingSent=System.currentTimeMillis();
            result=true;
        }
        
        return result;
    }
    
    /**
     * Getter for property jdbcTemplate.
     * 
     * @return Value of property jdbcTemplate.
     */
    public static synchronized JdbcTemplate getJdbcTemplate(org.springframework.context.ApplicationContext aContext) {
        return new JdbcTemplate((DataSource) aContext.getBean("dataSource"));
    }
    
    /**
     * Getter for property dataSource.
     * 
     * @return Value of property dataSource.
     */
    public static synchronized DataSource retrieveDataSource(ServletContext aContext) {
        BasicDataSource aSource=null;
        
        aSource=(BasicDataSource)org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(aContext).getBean("dataSource");
        
        return aSource;
    }
    
    /**
     * Getter for property sessionFactory.
     * 
     * @return Value of property sessionFactory.
     */
    public static synchronized SessionFactory retrieveSessionFactory(ServletContext aContext) {
        SessionFactory aSource=null;
        
        aSource=(SessionFactory)org.springframework.web.context.support.WebApplicationContextUtils.getWebApplicationContext(aContext).getBean("sessionFactory");
        
        return aSource;
    }
    
    /**
     * Sends an email in the correspondent type.
     */
    public static boolean sendEmail(String from_adr, String to_adr, String subject, String body_text, String body_html, int mailtype, String charset) {
        try{
            // create some properties and get the default Session
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", AgnUtils.getDefaultValue("mail.smtp.host"));
            javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
            //session.setDebug(debug);
            
            // create a message
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from_adr));
            InternetAddress[] address = {new InternetAddress(to_adr)};
            msg.setRecipients(javax.mail.Message.RecipientType.TO, address);
            msg.setSubject(subject, charset);
            msg.setSentDate(new java.util.Date());
            
            switch(mailtype) {
                case 0:
                    msg.setText(body_text, charset);
                    break;
                    
                case 1:
                    Multipart mp = new MimeMultipart("alternative");
                    MimeBodyPart mbp = new MimeBodyPart();
                    mbp.setText(body_text, charset);
                    mp.addBodyPart(mbp);
                    mbp = new MimeBodyPart();
                    mbp.setContent(body_html, "text/html");
                    mp.addBodyPart(mbp);
                    msg.setContent(mp);
                    break;
            }
            
            Transport.send(msg);
        } catch ( Exception e ) {
            AgnUtils.logger().error("sendEmail: "+e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * Sends the attachment of an email.
     */
    static public boolean sendEmailAttachment(String from, String to, String subject, String txt, byte[] att_data, String att_name, String att_type) {
        boolean result=true;
        
        try {
            // Create the attachment
            ByteArrayDataSource attachment = new ByteArrayDataSource(att_data, att_type);
            
            // Create the email message
            MultiPartEmail email = new MultiPartEmail();
            email.setHostName(AgnUtils.getDefaultValue("mail.smtp.host"));
            email.addTo(to);
            email.setFrom(from);
            email.setSubject(subject);
            email.setMsg(txt);
            
            // add the attachment
            email.attach(attachment, att_name, "EMM-Report");
            
            // send the email
            email.send();
        } catch (Exception e) {
            AgnUtils.logger().error("sendEmailAttachment: "+e.getMessage());
            result=false;
        }
        
        return result;
    }
    
    /**
     * Reads a file.
     */
    public static String readFile(String path) {
        String value=null;
        
        try {
            File aFile=new File(path);
            byte[] b=new byte[(int)aFile.length()];
            DataInputStream in=new DataInputStream(new FileInputStream(aFile));
            in.readFully(b);
            value=new String(b);
            in.close();
        } catch (Exception e) {
            value=null;
        }
        
        return value;
    }
    
    /**
     * Checks the email adress
     */
    public static boolean checkEmailAdress(String email){
        boolean value=false;
        try {
            InternetAddress adr=new InternetAddress(email);
            if(adr.getAddress().indexOf("@")==-1) {
                value=false;
            } else {
                value=true;
            }
        } catch (Exception e) {
            value=false;
        }
        return value;
    }
    
    /**
     * Getter for property parameterMap.
     * 
     * @return Value of property parameterMap.
     */
    public static Map getRequestParameterMap(ServletRequest req) {
        String aKey=null;
        String aValue=null;
        HashMap aMap=new HashMap();
        Enumeration e=req.getParameterNames();
        
        while(e.hasMoreElements()) {
            aKey=(String)e.nextElement();
            aValue=(String)req.getParameter(aKey);
            aMap.put(aKey, aValue);
        }
        
        return aMap;
    }
    
    /**
     * Getter for property defaultIntValue.
     * 
     * @return Value of property defaultIntValue.
     */
    public static int getDefaultIntValue(String key) {
        int result=0;
        String resultString=AgnUtils.getDefaultValue(key);
        
        if(resultString!=null) {
            try {
                result=Integer.parseInt(resultString);
            } catch (Exception e) {
                // do nothing
            }
        }
        return result;
    }
    
    /**
     * Getter for property defaultValue.
     * 
     * @return Value of property defaultValue.
     */
    public static String getDefaultValue(String key) {
        ResourceBundle defaults=null;
        String result=null;
        
        try {
            defaults=ResourceBundle.getBundle("emm");
        } catch (Exception e) {
            AgnUtils.logger().error("getDefaultValue: "+e.getMessage());
            return null;
        }
        
        try {
            result=defaults.getString(key);
        } catch (Exception e) {
            AgnUtils.logger().error("getDefaultValue: "+e.getMessage());
            result=null;
        }
        return result;
    }
    
    /**
     * Getter for property hibernateDialect.
     * 
     * @return Value of property hibernateDialect.
     */
    public static org.hibernate.dialect.Dialect getHibernateDialect() {
        return org.hibernate.dialect.DialectFactory.buildDialect(AgnUtils.getDefaultValue("jdbc.dialect"));
    }
    
    /**
     * Getter for property SQLCurrentTimestamp.
     * 
     * @return Value of property SQLCurrentTimestamp.
     */
    public static String getSQLCurrentTimestamp() {
        return org.hibernate.dialect.DialectFactory.buildDialect(AgnUtils.getDefaultValue("jdbc.dialect")).getCurrentTimestampSQLFunctionName();
        
    }
    
    /**
     * Matches  the target with the collection.
     */
    public static boolean matchCollection(String target, Collection aCol) {
        boolean result=false;
        boolean tmpResult=false;
        String aMask=null;
        
        if(target==null || aCol==null) {
            return false;
        }
        
        Iterator aIt=aCol.iterator();
        
        try {
            while(aIt.hasNext()) {
                aMask=(String)aIt.next();
                tmpResult=AgnUtils.match(aMask, target);
                if(tmpResult==true) {
                    result=true;
                    break;
                }
            }
        } catch (Exception e) {
            result=false;
        }
        return result;
    }
    
    public static boolean match(String mask, String target) {
        // ported almost verbatim from rpb's ages old Turbo Pascal 1.0 routine
        //
        // Compare two strings which may contain the DOS wildcard characters * and ?
        // and return a boolean result indicating their "equivalence".
        // e.g. WCMatcher.match("*.java", "WCMatcher.java") will return true as
        // will WCMatcher.match("w?mat*", "WCMatcher.java").  On the other hand,
        // WCMatcher.match("*.java", "WCMatcher.class") will return false (as you
        // would expect).  Note that the name/extension separator (i.e. the period)
        // is treated like any other character in the mask or target when compared
        // with one of the wildcard characters.  "*" will match "hosts" or "hosts."
        // or "java.exe" BUT "*." will only match a target that ends in ".something"
        // Clear as mud?  Try it ... it's fairly intuitive after observing a few
        // examples.
        //
        // Most usage will involve a filename mask (e.g. *.java) being
        // compared with some filename (e.g. WCMatcher.java).  However,
        // either mask or target or both may contain DOS wildcard characters
        // and this routine "should" provide an arguably correct result
        //
        // Note also that this method is case insensitive! i.e. "rpb" == "RPB"
        // (as is DOS).  A future todo item might be to optionally allow a
        // case sensitive comparison.
        //
        // caution - it seems to work
        
        // if anything is null, no match
        if(mask==null || target==null) {
            return false;
        }
        
        int p1 = 0; // used as character index into mask
        int p2 = 0; // used as character index into target
        boolean matched = true; // Assume true to begin.
        // A warning about side effects here:  an initial
        // value of false won't work!!  I've just been too
        // lazy to eliminate the assumption (the routine
        // was written this way back in the early 80's)
        
        if ( (mask.length() == 0) && (target.length() == 0) ) {
            matched = true;
        } else {
            if ( mask.length() == 0 ) {
                if ( target.charAt(0) == '%' ) {
                    matched = true;
                } else {
                    matched = false;
                }
            } else {
                if ( target.length() == 0 ) {
                    if ( mask.charAt(0) == '%' ) {
                        matched = true;
                    } else {
                        matched = false;
                    }
                }
            }
        }
        
        while ( (matched) && (p1 < mask.length()) && (p2 < target.length()) ) {
            if ( (mask.charAt(p1) == '_') || (target.charAt(p2) == '_') ) {
                p1++; p2++;
            } else {
                if ( mask.charAt(p1) == '%' ) {
                    p1++;
                    if ( p1 < mask.length() ) {
                        while ( (p2 < target.length())
                        && (!match(  mask.substring(p1,
                                mask.length()),
                                target.substring(p2,
                                target.length()))) ) {
                            p2++;
                        }
                        if ( p2 >= target.length() ) {
                            matched = false;
                        } else {
                            p1 = mask.length();
                            p2 = target.length();
                        }
                    } else {
                        p2 = target.length();
                    }
                } else {
                    if ( target.charAt(p2) == '%' ) {
                        p2++;
                        if ( p2 < target.length() ) {
                            while ( (p1 < mask.length())
                            && (!match(  mask.substring(p1,
                                    mask.length()),
                                    target.substring(p2,
                                    target.length()))) ) {
                                p1++;
                            }
                            if ( p1 >= mask.length() ) {
                                matched = false;
                            } else {
                                p1 = mask.length();
                                p2 = target.length();
                            }
                        } else {
                            p1 = mask.length();
                        }
                    } else {
                        if ( mask.toLowerCase().charAt(p1) == target.toLowerCase().charAt(p2) ) {
                            p1++;
                            p2++;
                        } else {
                            matched = false;
                        }
                    }
                }
            }
        }//wend
        
        if ( p1 >= mask.length() ) {
            while ( (p2 < target.length()) && (target.charAt(p2) == '%') ) {
                p2++;
            }
            if ( p2 < target.length() ) {
                matched = false;
            }
        }
        
        if ( p2 >= target.length() ) {
            while ( (p1 < mask.length()) && (mask.charAt(p1) == '%') ) {
                p1++;
            }
            if ( p1 < mask.length() ) {
                matched = false;
            }
        }
        return matched;
    }
    
    public static boolean matchOneway(String mask, String target) {
        // like match, but allows only pattern in mask, not in target
        // everything else is a shameless copy of match
        
        // if anything is null, no match
        if(mask==null || target==null) {
            return false;
        }
        
        int p1 = 0; // used as character index into mask
        int p2 = 0; // used as character index into target
        boolean matched = true; // Assume true to begin.
        // A warning about side effects here:  an initial
        // value of false won't work!!  I've just been too
        // lazy to eliminate the assumption (the routine
        // was written this way back in the early 80's)
        
        if ( (mask.length() == 0) && (target.length() == 0) ) {
            matched = true;
        } else {
            if ( target.length() == 0 ) {
                if ( mask.charAt(0) == '%' ) {
                    matched = true;
                } else {
                    matched = false;
                }
            }
        }
        
        while ( (matched) && (p1 < mask.length()) && (p2 < target.length()) ) {
            if ( (mask.charAt(p1) == '_') ) {
                p1++; p2++;
            } else {
                if ( mask.charAt(p1) == '%' ) {
                    p1++;
                    if ( p1 < mask.length() ) {
                        while ( (p2 < target.length())
                        && (!match(  mask.substring(p1,
                                mask.length()),
                                target.substring(p2,
                                target.length()))) ) {
                            p2++;
                        }
                        if ( p2 >= target.length() ) {
                            matched = false;
                        } else {
                            p1 = mask.length();
                            p2 = target.length();
                        }
                    } else {
                        p2 = target.length();
                    }
                } else {
                    if ( mask.toLowerCase().charAt(p1) == target.toLowerCase().charAt(p2) ) {
                        p1++;
                        p2++;
                    } else {
                        matched = false;
                    }
                }
            }
        }//wend
        
        if ( p2 >= target.length() ) {
            while ( (p1 < mask.length()) && (mask.charAt(p1) == '%') ) {
                p1++;
            }
            if ( p1 < mask.length() ) {
                matched = false;
            }
        } else
            matched = false;
        return matched;
    }
    
    /**
     * Returns a date format.
     */
    public static String formatDate(java.util.Date aDate, String pattern) {
        if(aDate==null) {
            return null;
        }
        SimpleDateFormat aFormat=new SimpleDateFormat(pattern);
        return aFormat.format(aDate);
    }
    
    /**
     * Getter for the system date.
     * 
     * @return Value of the system date.
     */
    public static java.util.Date getSysdate(String sysdate) {
        int value=0;
        char operator;
        GregorianCalendar result=new GregorianCalendar();
        
        if(!sysdate.equals("sysdate")) {
            operator=sysdate.charAt(7);
            
            try {
                value=Integer.parseInt(sysdate.substring(8));
            } catch (Exception e) {
                value=0;
            }
            
            switch(operator) {
                case '+':
                    result.add(GregorianCalendar.DAY_OF_MONTH, value);
                    break;
                case '-':
                    result.add(GregorianCalendar.DAY_OF_MONTH, value*(-1));
                    break;
            }
        }
        
        return result.getTime();
    }
    
    /**
     * compares Strings
     * modes:
     * 0 == test for equal
     * 1 == test for not equal
     * 2 == test for a greater b
     * 3 == test for a smaller b
     */
    public static boolean compareString(String a, String b, int mode) {
        boolean result=false;
        
        // if both strings are null, we have a match.
        if(a==null && b==null) {
            return true;
        }
        
        // if one string is null, no match
        if(a==null || b==null) {
            return false;
        }
        
        int stringres=a.compareTo(b);
        switch(mode) {
            case 0:
                if(stringres==0) {
                    result=true;
                }
                break;
                
            case 1:
                if(stringres!=0) {
                    result=true;
                }
                break;
            case 2:
                if(stringres>0) {
                    result=true;
                }
                break;
                
            case 3:
                if(stringres<0) {
                    result=true;
                }
                break;
        }
        return result;
    }
    
    public static String toLowerCase(String source) {
        if(source==null) {
            return null;
        }
        
        return source.toLowerCase();
    }
    
    /**
     * Getter for property reqParameters.
     *
     * @return Value of property reqParameters.
     */
    public static HashMap getReqParameters(HttpServletRequest req) {
        HashMap params=new HashMap();
        String parName=null;
        
        Enumeration aEnum=req.getParameterNames();
        while(aEnum.hasMoreElements()) {
            parName=(String)aEnum.nextElement();
            if(parName.startsWith("__AGN_DEFAULT_") && parName.length()>14) {
                parName=parName.substring(14);
                params.put(parName, req.getParameter("__AGN_DEFAULT_"+parName));
            }
        }
        
        aEnum=req.getParameterNames();
        while(aEnum.hasMoreElements()) {
            parName=(String)aEnum.nextElement();
            params.put(parName, req.getParameter(parName));
        }
        if(req.getQueryString()!=null) {
            params.put("agnQueryString", req.getQueryString());
        }
        
        return params;
    }
    
    /**
     * Checkes the permissions.
     */
    public static boolean allowed(String id, HttpServletRequest req) {
        Set permission=null;
        HttpSession session=req.getSession();
        
        if(session==null) {
            return false; //Nothing allowed if there is no Session
        }
        
        permission=((Admin) session.getAttribute("emm.admin")).getAdminPermissions();
        
        if(permission != null && permission.contains(id)) {
            return true; // Allowed for user.
        }
        
        permission=((Admin) session.getAttribute("emm.admin")).getGroup().getGroupPermissions();
        
        if(permission != null && permission.contains(id)) {
            return true; // Allowed for group.
        }
        
        return false;
    }
    
    /**
     * Gets the used language.
     */
    public static Locale buildLocale(String language) {
        Locale aLoc=null;
        
        if(language!=null) {
            int aPos=language.indexOf('_');
            String lang=language.substring(0,aPos);
            String country=language.substring(aPos+1);
            aLoc=new Locale(lang, country);
        }
        return aLoc;
    }
    
    /**
     * Getter for property firstResult.
     *
     * @return Value of property firstResult.
     */
    public static Object getFirstResult(List aList) {
        Object result=null;
        
        if(aList!=null && aList.size()>0) {
            result=aList.get(0);
        }
        
        return result;
    }
    
    /**
     * Prepares a string ready for saving.
     */
    public static String propertySaveString(String input) {
        if(input==null) {
            input=new String("");
        }
        
        input=StringUtils.replace(input, "=", "\\=");
        input=StringUtils.replace(input, "\"", "\\\"");
        input=StringUtils.replace(input, ",", "\\,");
        
        return input;
    }
    
    /**
     * Search for parameters.
     */
    public static String findParam(String paramName, String paramList) {
        String result=null;
        
        try {
            if(paramName!=null) {
                int posA=paramList.indexOf(paramName+"=\"");
                if(posA!=-1) {
                    int posB=paramList.indexOf("\",", posA);
                    if(posB!=-1) {
                        result=paramList.substring(posA+paramName.length()+2, posB);
                        result=StringUtils.replace(result, "\\=", "=");
                        result=StringUtils.replace(result, "\\\"", "\"");
                        result=StringUtils.replace(result, "\\,", ",");
                    }
                }
            }
        } catch (Exception e) {
            AgnUtils.logger().error("findParam: "+e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Getter for property companyID.
     *
     * @return Value of property companyID.
     */
    public static int getCompanyID(HttpServletRequest req) {
        
        int companyID=0;
        
        try {
            companyID=AgnUtils.getCompany(req).getId();
        } catch (Exception e) {
            AgnUtils.logger().error("no companyID");
            companyID=0;
        }
        
        return companyID;
    }
    
    /**
     * Getter for property admin.
     *
     * @return Value of property admin.
     */
    public static Admin getAdmin(HttpServletRequest req) {
        
        Admin admin=null;
        
        try {
            admin=(Admin)req.getSession().getAttribute("emm.admin");
        } catch (Exception e) {
            AgnUtils.logger().error("no admin");
            admin=null;;
        }
        
        return admin;
    }
    
    /**
     * Getter for property timeZone.
     *
     * @return Value of property timeZone.
     */
    public static TimeZone getTimeZone(HttpServletRequest req) {
        
        TimeZone tz=null;
        
        try {
            tz=TimeZone.getTimeZone(AgnUtils.getAdmin(req).getAdminTimezone());
        } catch (Exception e) {
            AgnUtils.logger().error("no admin");
            tz=null;
        }
        
        return tz;
    }
    
    /**
     * Getter for property company.
     *
     * @return Value of property company.
     */
    public static Company getCompany(HttpServletRequest req) {
        
        Company comp=null;
        
        try {
            comp=AgnUtils.getAdmin(req).getCompany();
        } catch (Exception e) {
            AgnUtils.logger().error("no company");
            comp=null;;
        }
        
        return comp;
    }
    
    /**
     * Checks if date is in future.
     *
     * @param aDate Checked date.
     */
    public static boolean isDateInFuture(java.util.Date aDate) {
        boolean result=false;
        GregorianCalendar aktCal=new GregorianCalendar();
        GregorianCalendar tmpCal=new GregorianCalendar();
        
        tmpCal.setTime(aDate);
        aktCal.add(GregorianCalendar.MINUTE, 5); // look five minutes in future ;-)
        if(aktCal.before(tmpCal)) {
            result=true;
        }
        
        return result;
    }
    
    /**
     * Getter for property bshInterpreter.
     *
     * @return Value of property bshInterpreter.
     */
    public static Interpreter getBshInterpreter(int cID, int customerID, ApplicationContext con) {
        JdbcTemplate tmpl=new JdbcTemplate((DataSource)con.getBean("dataSource"));
        SqlRowSet rset=null;
        Interpreter aBsh=new Interpreter();
        NameSpace aNameSpace=aBsh.getNameSpace();
        aNameSpace.importClass("org.agnitas.util.AgnUtils");
        
        String sqlStatement="select * from customer_"+cID+"_tbl cust where cust.customer_id="+customerID;
        try {
            rset=tmpl.queryForRowSet(sqlStatement);
            SqlRowSetMetaData aMeta=rset.getMetaData();
            if(rset.next()) {
                for(int i=1; i<=aMeta.getColumnCount(); i++) {
                    switch(aMeta.getColumnType(i)) {
                        case java.sql.Types.BIGINT:
                        case java.sql.Types.INTEGER:
                        case java.sql.Types.NUMERIC:
                        case java.sql.Types.SMALLINT:
                        case java.sql.Types.TINYINT:
                            if(rset.getObject(i)!=null) {
                                aNameSpace.setTypedVariable(aMeta.getColumnName(i), java.lang.Integer.class, new Integer(rset.getInt(i)), null);
                            } else {
                                aNameSpace.setTypedVariable(aMeta.getColumnName(i), java.lang.Integer.class, null, null);
                            }
                            break;
                            
                        case java.sql.Types.DECIMAL:
                        case java.sql.Types.DOUBLE:
                        case java.sql.Types.FLOAT:
                            if(rset.getObject(i)!=null) {
                                aNameSpace.setTypedVariable(aMeta.getColumnName(i), java.lang.Double.class, new Double(rset.getDouble(i)), null);
                            } else {
                                aNameSpace.setTypedVariable(aMeta.getColumnName(i), java.lang.Double.class, null, null);
                            }
                            break;
                            
                        case java.sql.Types.CHAR:
                        case java.sql.Types.LONGVARCHAR:
                        case java.sql.Types.VARCHAR:
                            aNameSpace.setTypedVariable(aMeta.getColumnName(i), java.lang.String.class, rset.getString(i), null);
                            break;
                            
                        case java.sql.Types.DATE:
                        case java.sql.Types.TIME:
                        case java.sql.Types.TIMESTAMP:
                            aNameSpace.setTypedVariable(aMeta.getColumnName(i), java.util.Date.class, rset.getDate(i), null);
                    }
                }
            }
            // add virtual column "sysdate"
            aNameSpace.setTypedVariable(AgnUtils.getHibernateDialect().getCurrentTimestampSQLFunctionName(), java.util.Date.class, new java.util.Date(), null);
        } catch (Exception e) {
            AgnUtils.logger().error("getBshInterpreter: "+e.getMessage());
            aBsh=null;
        }
        return aBsh;
    }
}
