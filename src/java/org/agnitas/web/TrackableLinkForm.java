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
import org.agnitas.util.*;
import java.util.*;
import java.io.*;

public final class TrackableLinkForm extends StrutsFormBase {
    
    /** 
     * Holds value of property action. 
     */
    private int action;
    
    /**
     * Holds value of property linkID. 
     */
    private int linkID;
    
    /**
     * Holds value of property linkName. 
     */
    private String linkName;
    
    /**
     * Holds value of property linkAction. 
     */
    private int linkAction;
    
    /**
     * Holds value of property trackable. 
     */
    private int trackable;
    
    /**
     * Holds value of property linkUrl. 
     */
    private String linkUrl;
    
    /**
     * Holds value of property mailingID. 
     */
    private int mailingID;
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
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
    public ActionErrors validate(ActionMapping mapping,
    HttpServletRequest request) {
        
        ActionErrors errors = new ActionErrors();
                
        if(!errors.isEmpty()) {
            mapping.setInput(mapping.findForward("view").getPath());
        }
        return errors;
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
     * Getter for property fontID.
     *
     * @return Value of property fontID.
     */
    public int getLinkID() {
        return this.linkID;
    }
    
    /**
     * Setter for property fontID.
     * 
     * @param linkID 
     */
    public void setLinkID(int linkID) {
        this.linkID = linkID;
    }
    
    /** 
     * Getter for property fontName.
     *
     * @return Value of property fontName.
     */
    public String getLinkName() {
        return this.linkName;
    }
    
    /**
     * Setter for property fontName.
     * 
     * @param linkName 
     */
    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }
    
    /**
     * Getter for property linkAction.
     *
     * @return Value of property linkAction.
     */
    public int getLinkAction() {
        return this.linkAction;
    }
    
    /** 
     * Setter for property linkAction.
     *
     * @param linkAction New value of property linkAction.
     */
    public void setLinkAction(int linkAction) {
        this.linkAction = linkAction;
    }
    
    /**
     * Getter for property trackable.
     *
     * @return Value of property trackable.
     */
    public int getTrackable() {
        return this.trackable;
    }
    
    /**
     * Setter for property trackable.
     *
     * @param trackable New value of property trackable.
     */
    public void setTrackable(int trackable) {
        this.trackable = trackable;
    }
    
    /**
     * Getter for property linkUrl.
     *
     * @return Value of property linkUrl.
     */
    public String getLinkUrl() {
        return this.linkUrl;
    }
    
    /**
     * Setter for property linkUrl.
     *
     * @param linkUrl New value of property linkUrl.
     */
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
    
    /**
     * Getter for property mailingID.
     *
     * @return Value of property mailingID.
     */
    public int getMailingID() {
        return this.mailingID;
    }
    
    /**
     * Setter for property mailingID.
     *
     * @param mailingID New value of property mailingID.
     */
    public void setMailingID(int mailingID) {
        this.mailingID = mailingID;
    }

    /**
     * Holds value of property shortname.
     */
    private String shortname;

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
     * Holds value of property isTemplate.
     */
    private boolean isTemplate;

    /**
     * Getter for property isTemplate.
     *
     * @return Value of property isTemplate.
     */
    public boolean isIsTemplate() {
        return this.isTemplate;
    }

    /**
     * Setter for property isTemplate.
     *
     * @param isTemplate New value of property isTemplate.
     */
    public void setIsTemplate(boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    /**
     * Holds value of property links.
     */
    private Collection links;

    /**
     * Getter for property links.
     *
     * @return Value of property links.
     */
    public Collection getLinks() {
        return this.links;
    }

    /**
     * Setter for property links.
     *
     * @param links New value of property links.
     */
    public void setLinks(Collection links) {
        this.links = links;
    }  
}