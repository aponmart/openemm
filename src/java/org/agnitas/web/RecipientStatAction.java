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

package org.agnitas.web;

import org.agnitas.util.*;
import org.agnitas.dao.TargetDao;
import org.agnitas.beans.BindingEntry;
import org.agnitas.target.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.springframework.context.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.rowset.*;

public final class RecipientStatAction extends StrutsActionBase {
    
    public static final int ACTION_SELECT  = ACTION_LAST+1;
    public static final int ACTION_DISPLAY = ACTION_LAST+2;
    
    
    /**
     * Process the specified HTTP request, and create the corresponding HTTP
     * response (or forward to another web component that will create it).
     * Return an <code>ActionForward</code> instance describing where and how
     * control should be forwarded, or <code>null</code> if the response has
     * already been completed.
     * 
     * @param form 
     * @param req 
     * @param res 
     * @param mapping The ActionMapping used to select this instance
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     * @return destination
     */
    public ActionForward execute(ActionMapping mapping,
    ActionForm form,
    HttpServletRequest req,
    HttpServletResponse res)
    throws IOException, ServletException {
        
        RecipientStatForm aForm=null;
        ActionMessages errors = new ActionMessages();
        ActionForward destination=null;
        ApplicationContext aContext=this.getWebApplicationContext();
        
        if(!this.checkLogon(req)) {
            return mapping.findForward("logon");
        }
        
        if(form==null) {
            aForm=new RecipientStatForm();
        } else {
            aForm=(RecipientStatForm)form;
        }

        AgnUtils.logger().info("Action: "+aForm.getAction());

       if(!allowed("stats.mailing", req)) {
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.permissionDenied"));
            saveErrors(req, errors);
            return null;
        }


        try {
            switch(aForm.getAction()) {
                case ACTION_DISPLAY:
                    aForm.setAction(ACTION_DISPLAY);
                    aForm.setCompanyID(this.getCompanyID(req));
                    getStatFromDB(aForm, aContext, req);
                    destination=mapping.findForward("display");
                    break;
                default:
                    aForm.setAction(ACTION_DISPLAY);
                    destination=mapping.findForward("select");
                    break;
            }
            
        } catch (Exception e) {
            AgnUtils.logger().error("execute: "+e+"\n"+AgnUtils.getStackTrace(e));
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("error.exception"));
        }
        
        // Report any errors we have discovered back to the original form
        if (!errors.isEmpty()) {
            saveErrors(req, errors);
            return(new ActionForward(mapping.getInput()));
        }
        return destination;
    }

    /**
     * Gets statistics from database.
     */
    protected void getStatFromDB(RecipientStatForm aForm, ApplicationContext aContext, HttpServletRequest req) {
        
        JdbcTemplate jdbc=getJdbcTemplate();
        SqlRowSet rset=null;
        String csvfile = "";
        String sqlStatement = "";
        int mailingListID = 0;
        int numActive     = 0;
        int numBounce     = 0;
        int numHTML       = 0;
        int numOffline    = 0;
        int numOptOut     = 0;
        int numText       = 0;
        int targetID      = 0;
        int companyID     = 0;
        int mType         = 0;
        
        csvfile += SafeString.getLocaleString("RecipientStat", (Locale)req.getSession().getAttribute("messages_lang")) + "\n\n";
        
        // THESE BELONG INTO TRY-CATCH-BLOCKS !!!
        companyID = aForm.getCompanyID();
        targetID = aForm.getTargetID();
        mailingListID = aForm.getMailingListID();
        mType = aForm.getMediaType();
        
        
        // mailing list part of sql statement
        String mailList=new String(" ");
        if(mailingListID !=0) {
            mailList=new String(" bind.MAILINGLIST_ID=" + mailingListID + " AND ");
        }
        
        // target group part of sql statement
        String sqlSelection = new String(" ");
        if(targetID!=0) {
            TargetDao targetDao=(TargetDao) aContext.getBean("TargetDao");
            Target aTarget=targetDao.getTarget(targetID, companyID);

            if(aTarget != null) {
                sqlSelection = new String(" AND ("  + aTarget.getTargetSQL() + ") ");
            }
        }
        
        if(mType == 0) {
            
            sqlStatement="SELECT count(*), bind.user_status, cust.mailtype FROM customer_" + companyID + "_tbl cust, customer_" +
            companyID + "_binding_tbl bind WHERE " + mailList + " cust.customer_id=bind.customer_id"
            + sqlSelection + " GROUP BY bind.user_status, cust.mailtype";
            
            try {
                rset=jdbc.queryForRowSet(sqlStatement);
                while(rset.next()){
                    switch(rset.getInt(2)) {
                        case BindingEntry.USER_STATUS_ACTIVE:
                            switch(rset.getInt(3)) {
                                case 0:
                                    numText += rset.getInt(1);
                                    break;
                                case 1:
                                    numHTML += rset.getInt(1);
                                    break;
                                case 2:
                                    numOffline += rset.getInt(1);
                            }
                            numActive += rset.getInt(1);
                            break;
                            
                        case BindingEntry.USER_STATUS_OPTOUT:
                            numOptOut += rset.getInt(1);
                            break;
                            
                        case BindingEntry.USER_STATUS_ADMINOUT:
                            numOptOut += rset.getInt(1);
                            break;
                            
                        case BindingEntry.USER_STATUS_BOUNCED:
                            numBounce += rset.getInt(1);
                            break;
                            
                    }
                }
                
            } catch ( Exception e) {
                AgnUtils.logger().error("getStatFromDB: "+e);
                AgnUtils.logger().error("SQL: "+sqlStatement);
            }
            
            
        } else {
            
            sqlStatement="SELECT count(*), bind.user_status FROM customer_" + companyID + "_tbl cust, customer_" +
            companyID + "_binding_tbl bind WHERE " + mailList + " cust.customer_id=bind.customer_id"
            + sqlSelection + " AND bind.mediatype = " + mType + " GROUP BY bind.user_status";
            
            try {
                rset=jdbc.queryForRowSet(sqlStatement);
                while(rset.next()){
                    switch(rset.getInt(2)) {
                        case BindingEntry.USER_STATUS_ACTIVE:
                            numActive += rset.getInt(1);
                            break;
                            
                        case BindingEntry.USER_STATUS_OPTOUT:
                            numOptOut += rset.getInt(1);
                            break;
                            
                        case BindingEntry.USER_STATUS_ADMINOUT:
                            numOptOut += rset.getInt(1);
                            break;
                            
                        case BindingEntry.USER_STATUS_BOUNCED:
                            numBounce += rset.getInt(1);
                            break;
                            
                    }
                }
                
            } catch ( Exception e) {
                AgnUtils.logger().error("getStatFromDB: "+e);
                AgnUtils.logger().error("SQL: "+sqlStatement);
            }
            
        }
        
        
        // fill up form with results;
        aForm.setNumOptout(numOptOut);
        aForm.setNumBounce(numBounce);
        aForm.setNumActive(numActive);
        aForm.setNumRecipients(numActive + numBounce + numOptOut);
        
        if(mType == 0) {
            aForm.setNumText(numText);
            aForm.setNumHTML(numHTML);
            aForm.setNumOffline(numOffline);
        } else {
            aForm.setNumText(0);
            aForm.setNumHTML(0);
            aForm.setNumOffline(0);
        }
        
        // set blue bar length
        if(aForm.getNumActive() != 0) {
            double tmp = 200.0/(numActive + numBounce + numOptOut);
            aForm.setBlueBounce((int)(tmp*numBounce));
            aForm.setBlueOptout((int)(tmp*numOptOut));
            aForm.setBlueActive((int)(tmp*numActive));
            
            if(mType == 0) {
                aForm.setBlueText((int)(tmp*numText));
                aForm.setBlueHTML((int)(tmp*numHTML));
                aForm.setBlueOffline((int)(tmp*numOffline));
            } else {
                aForm.setBlueText(1);       // teilung durch Null
                aForm.setBlueHTML(1);       // is nich witzig
                aForm.setBlueOffline(1);    // (to be elaborated)
            }
        } else {
            aForm.setBlueBounce(1);
            aForm.setBlueOptout(1);
            aForm.setBlueActive(1);
            aForm.setBlueText(1);
            aForm.setBlueHTML(1);
            aForm.setBlueOffline(1);
        }
        
        // fill up csv file:
        csvfile += "\n";
        
        csvfile += SafeString.getLocaleString("SubscriberStatus", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;\n";
        csvfile += SafeString.getLocaleString("Opt_Outs", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;" + numOptOut + "\n";
        csvfile += SafeString.getLocaleString("Bounces", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;" + numBounce + "\n";
        csvfile += SafeString.getLocaleString("Active", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;" + numActive + "\n";
        csvfile += SafeString.getLocaleString("Total", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;" + aForm.getNumRecipients() + "\n";
        csvfile += "\n";
        
        if(mType == 0) {
            csvfile += SafeString.getLocaleString("SubscriberMailtype", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;\n";
            csvfile += SafeString.getLocaleString("Text_Version", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;" + numText + "\n";
            csvfile += SafeString.getLocaleString("HTML_Version", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;" + numHTML + "\n";
            csvfile += SafeString.getLocaleString("OfflineHTML", (Locale)req.getSession().getAttribute("messages_lang")) + ": ;" + numOffline + "\n";
        }
        // and put it in the session:
        req.getSession().setAttribute("csvdata", csvfile);
        
        
        
        // the monthly overview is performed in the JSP
        
        aForm.setCvsfile(csvfile);
        return;
    }  
}