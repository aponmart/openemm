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

package org.agnitas.beans;

import java.sql.*;
import java.io.*;
import java.util.*;

public interface ProfileField {
    /**
     * Setter for property companyID.
     * 
     * @param company New value of property companyID.
     */
    public void setCompanyID(int company);
    
    /**
     * Setter for property column.
     *
     * @param column New value of property column.
     */
    public void setColumn(String column);
    
    /**
     * Setter for property adminID.
     *
     * @param adminID New value of property adminID.
     */
    public void setAdminID(int adminID);
    
    /**
     * Setter for property shortname.
     *
     * @param desc New value of property shortname.
     */
    public void setShortname(String desc);

    /**
     * Setter for property description.
     *
     * @param desc New value of property description.
     */
    public void setDescription(String desc);

    /**
     * Setter for property defaultValue.
     *
     * @param value New value of property defaultValue.
     */
    public void setDefaultValue(String value);

    /**
     * Setter for property modeEdit.
     *
     * @param edit New value of property modeEdit.
     */
    public void setModeEdit(int edit);
    
    /**
     * Setter for property mideInsert.
     *
     * @param insert New value of property modeInsert.
     */
    public void setModeInsert(int insert);
    
    /**
     * Getter for property companyID.
     * 
     * @return Value of property companyID.
     */
    public int getCompanyID();
    
    /**
     * Getter for property column.
     * 
     * @return Value of property column.
     */
    public String getColumn();

    /**
     * Getter for property adminID.
     * 
     * @return Value of property adminID.
     */
    public int getAdminID();
    
    /**
     * Getter for property shortname.
     * 
     * @return Value of property shortname.
     */
    public String getShortname();

    /**
     * Getter for property description.
     * 
     * @return Value of property description.
     */
    public String getDescription();

    /**
     * Getter for property defaultValue.
     * 
     * @return Value of property defaultValue.
     */
    public String getDefaultValue();

    /**
     * Getter for property modeEdit.
     * 
     * @return Value of property modeEdit.
     */
    public int getModeEdit();
    
    /**
     * Getter for property modeInsert.
     * 
     * @return Value of property modeInsert.
     */
    public int getModeInsert();
    
}
