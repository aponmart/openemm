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

import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.apache.struts.util.*;
import org.apache.struts.upload.*;
import org.agnitas.util.*;
import java.util.*;
import java.sql.*;
import java.text.*;
import java.io.*;

/**
 *
 * @author  mhe
 */
public class ExportWizardForm extends StrutsFormBase {
    
    /** 
     * Holds value of property charset. 
     */
    private String charset;
    
    /**
     * Holds value of property action. 
     */
    private int action;
    
    /**
     * Holds value of property csvFile. 
     */
    private File csvFile;
    
    /**
     * Holds value of property linesOK. 
     */
    private int linesOK;
    
    /** 
     * Holds value of property dbExportStatus. 
     */
    private int dbExportStatus;
    
    /**
     * Holds value of property separator. 
     */
    private String separator;
    
    /**
     * Holds value of property delimiter. 
     */
    private String delimiter;
    
    /**
     * Holds value of property downloadName. 
     */
    private String downloadName;
    
    /**
     * Holds value of property dbExportStatusMessages. 
     */
    private LinkedList dbExportStatusMessages;
    
    /**
     * Holds value of property mailinglists. 
     */
    private String[] mailinglists;
    
    /**
     * Holds value of property targetID. 
     */
    private int targetID;
    
    /**
     * Holds value of property columns. 
     */
    private String[] columns;
    
    /**
     * Holds value of property mailinglistID. 
     */
    private int mailinglistID;
    
    /**
     * Holds value of property userType. 
     */
    private String userType;
    
    /**
     * Holds value of property userStatus. 
     */
    private int userStatus;

    /**
     * Holds value of property collectingData. 
     */
    private boolean collectingData;
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        return;
    }
    
    /**
     * Initialization
     */
    public void clearData() {
        
        this.columns=new String[] {};
        this.mailinglists=new String[] {};
        this.shortname="";
        this.description="";
        this.targetID=0;
        this.mailinglistID=0;
        this.userStatus=0;
        this.userType=null;
        return;
    }  
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     * 
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     * @return errors
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        if(this.action==ExportWizardAction.ACTION_COLLECT_DATA) {
            if(request.getParameter("columns")==null) {
                this.columns=new String[] {};
            }
        }
        
        if(this.columns!=null && this.columns.length==0 && this.action!=ExportWizardAction.ACTION_QUERY) {
           errors.add("global", new ActionMessage("error.export.no_columns_selected"));
        }

        if(action==ExportWizardAction.ACTION_SAVE) {
           if(this.shortname.length()<3) {
                errors.add("shortname", new ActionMessage("error.nameToShort"));
            }            
        }
        
        
        return errors;
        
    }
    
    /**
     * Getter for property charset.
     *
     * @return Value of property charset.
     */
    public String getCharset() {
        return this.charset;
    }
    
    /**
     * Setter for property charset.
     *
     * @param charset New value of property charset.
     */
    public void setCharset(String charset) {
        this.charset = charset;
    }
    
    /**
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }
    
    /**
     * Getter for property csvFile.
     *
     * @return Value of property csvFile.
     */
    public File getCsvFile() {
        return this.csvFile;
    }
    
    /** 
     * Setter for property csvFile.
     *
     * @param csvFile New value of property csvFile.
     */
    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }
    
    /**
     * Getter for property linesOK.
     *
     * @return Value of property linesOK.
     */
    public int getLinesOK() {
        return this.linesOK;
    }
    
    /**
     * Setter for property linesOK.
     *
     * @param linesOK New value of property linesOK.
     */
    public void setLinesOK(int linesOK) {
        this.linesOK = linesOK;
    }
    
    /**
     * Getter for property dbInsertStatus.
     *
     * @return Value of property dbInsertStatus.
     */
    public int getDbExportStatus() {
        return this.dbExportStatus;
    }
    
    /**
     * Setter for property dbInsertStatus.
     * 
     * @param dbExportStatus 
     */
    public void setDbExportStatus(int dbExportStatus) {
        this.dbExportStatus = dbExportStatus;
    }
    
    /** 
     * Getter for property separator.
     *
     * @return Value of property separator.
     */
    public String getSeparator() {
        return this.separator;
    }
    
    /** 
     * Setter for property separator.
     *
     * @param separator New value of property separator.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }
    
    /**
     * Getter for property delimitor.
     *
     * @return Value of property delimitor.
     */
    public String getDelimiter() {
        return this.delimiter;
    }
    
    /**
     * Setter for property delimitor.
     * 
     * @param delimiter 
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    
    /**
     * Getter for property downloadName.
     *
     * @return Value of property downloadName.
     */
    public String getDownloadName() {
        return this.downloadName;
    }
    
    /**
     * Setter for property downloadName.
     *
     * @param downloadName New value of property downloadName.
     */
    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }
    
    /**
     * Getter for property dbInsertStatusMessages.
     *
     * @return Value of property dbInsertStatusMessages.
     */
    public LinkedList getDbExportStatusMessages() {
        return this.dbExportStatusMessages;
    }
    
    /**
     * Setter for property dbInsertStatusMessages.
     * 
     * @param dbExportStatusMessages 
     */
    public void setDbExportStatusMessages(LinkedList dbExportStatusMessages) {
        this.dbExportStatusMessages = dbExportStatusMessages;
    }
    
    /**
     * Adds a database export status message.
     */
    public void addDbExportStatusMessage(String message) {
        if(this.dbExportStatusMessages==null) {
            this.dbExportStatusMessages=new LinkedList();
        }
        
        this.dbExportStatusMessages.add(message);
    }
    
    /**
     * Getter for property mailinglists.
     *
     * @return Value of property mailinglists.
     */
    public String[] getMailinglists() {
        return this.mailinglists;
    }
    
    /** 
     * Setter for property mailinglists.
     *
     * @param mailinglists New value of property mailinglists.
     */
    public void setMailinglists(String[] mailinglists) {
        this.mailinglists = mailinglists;
    }
    
    /** 
     * Getter for property targetID.
     *
     * @return Value of property targetID.
     */
    public int getTargetID() {
        return this.targetID;
    }
    
    /** 
     * Setter for property targetID.
     *
     * @param targetID New value of property targetID.
     */
    public void setTargetID(int targetID) {
        this.targetID = targetID;
    }
    
    /**
     * Getter for property columns.
     *
     * @return Value of property columns
     */
    public String[] getColumns() {
        return this.columns;
    }
    
    /**
     * Setter for property columns.
     *
     * @param columns New value of property columns.
     */
    public void setColumns(String[] columns) {
        this.columns = columns;
    }
    
    /**
     * Getter for property mailinglistID.
     *
     * @return Value of property mailinglistID.
     */
    public int getMailinglistID() {
        return this.mailinglistID;
    }
    
    /**
     * Setter for property mailinglistID.
     *
     * @param mailinglistID New value of property mailinglistID.
     */
    public void setMailinglistID(int mailinglistID) {
        this.mailinglistID = mailinglistID;
    }
    
    /**
     * Getter for property userType.
     *
     * @return Value of property userType.
     */
    public String getUserType() {
        return this.userType;
    }
    
    /**
     * Setter for property userType.
     *
     * @param userType New value of property userType.
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    /**
     * Getter for property userStatus.
     *
     * @return Value of property userStatus.
     */
    public int getUserStatus() {
        return this.userStatus;
    }
    
    /**
     * Setter for property userStatus.
     *
     * @param userStatus New value of property userStatus.
     */
    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * Checks if collectingData exists.
     */
    public synchronized boolean tryCollectingData() {
        if(this.collectingData) {
            return false;
        } else {
            this.collectingData=true;
            return true;
        }
    }
    
    /**
     * Resets collectingData
     */
    public synchronized void resetCollectingData() {
        this.collectingData=false;
    }

    /**
     * Holds value of property shortname.
     */
    private String shortname="";

    /**
     * Getter for property shortname.
     *
     * @return Value of property shortname.
     */
    public String getShortname() {

        return this.shortname;
    }

    /**
     * Setter for property shortname.
     *
     * @param shortname New value of property shortname.
     */
    public void setShortname(String shortname) {

        this.shortname = shortname;
    }

    /**
     * Holds value of property description.
     */
    private String description;

    /**
     * Getter for property description.
     *
     * @return Value of property description.
     */
    public String getDescription() {

        return this.description;
    }

    /**
     * Setter for property description.
     *
     * @param description New value of property description.
     */
    public void setDescription(String description) {

        this.description = description;
    }

    /**
     * Holds value of property exportPredefID.
     */
    private int exportPredefID;

    /**
     * Getter for property exportPredefID.
     *
     * @return Value of property exportPredefID.
     */
    public int getExportPredefID() {

        return this.exportPredefID;
    }

    /**
     * Setter for property exportPredefID.
     *
     * @param exportPredefID New value of property exportPredefID.
     */
    public void setExportPredefID(int exportPredefID) {

        this.exportPredefID = exportPredefID;
    }
}
