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

package org.agnitas.beans.impl;

import java.sql.*;
import java.io.*;
import java.util.*;
import org.agnitas.beans.ExportPredef;

public class ExportPredefImpl implements ExportPredef {
    protected int id;

    protected int company;

    protected String charset="ISO-8859-1";
    
    protected String columns="";

    protected String shortname="";

    protected String description="";

    protected String mailinglists="";

    protected int mailinglistID;

    protected String delimiter="";

    protected String separator=";";

    protected int targetID;

    protected String userType="E";

    protected int userStatus;

    protected int deleted;

    ExportPredefImpl() {
    }

    // * * * * *
    //  SETTER:
    // * * * * *
    public void setId(int id) {
        this.id=id;
    }

    public void setCompanyID(int company) {
        this.company=company;
    }

    public void setCharset(String charset) {
        this.charset=charset;
    }
    
    public void setColumns(String columns) {
        this.columns=columns;
    }
    
    public void setShortname(String shortname) {
        this.shortname=shortname;
    }
    
    public void setDescription(String description) {
        this.description=description;
    }
    
    public void setMailinglists(String mailinglists) {
        this.mailinglists=mailinglists;
    }
    
    public void setMailinglistID(int mailinglistID) {
        this.mailinglistID=mailinglistID;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter=delimiter;
    }

    public void setSeparator(String separator) {
        this.separator=separator;
    }

    public void setTargetID(int targetID) {
        this.targetID=targetID;
    }

    public void setUserType(String userType) {
        this.userType=userType;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus=userStatus;
    }

    public void setDeleted(int deleted) {
        this.deleted=deleted;
    }


    // * * * * *
    //  GETTER:
    // * * * * *
    public int getId() {
        return id;
    }

    public int getCompanyID() {
        return company;
    }
    
    public String getCharset() {
        return charset;
    }

    public String getColumns() {
        return columns;
    }

    public String getShortname() {
        return shortname;
    }

    public String getDescription() {
        return description;
    }

    public String getMailinglists() {
        return mailinglists;
    }

    public int getMailinglistID() {
        return mailinglistID;
    }
    
    public String getDelimiter() {
        return delimiter;
    }
    
    public String getSeparator() {
        return separator;
    }
    
    public int getTargetID() {
        return targetID;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public int getUserStatus() {
        return userStatus;
    }
    
    public int getDeleted() {
        return deleted;
    }
    
}
