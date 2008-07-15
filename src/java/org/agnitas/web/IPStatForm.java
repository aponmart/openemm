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
import java.io.*;


public final class IPStatForm extends StrutsFormBase {

    private int listID;
    private int targetID;
    private int action;
    private int lines;    
    private int rest;
    private int total;
    private int maxIPs = 20;
    private LinkedList ips;
    private LinkedList subscribers;    
    private String csvfile;
    
    /**
     * Holds value of property biggest. 
     */
    private int biggest;
    
    /**
     * Holds value of property statReady.
     */
    private boolean statReady;
    
    /**
     * Holds value of property statInProgress.
     */
    private boolean statInProgress;
    
    /**
     * Reset all properties to their default values.
     *
     * @param mapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping mapping, HttpServletRequest request) {

        // this.listID = 0;
        // this.targetID = 0;
        
        //Locale aLoc=(Locale)request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        //MessageResources text=this.getServlet().getResources();
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
        

        if (this.isStatInProgress()) {
            this.action = IPStatAction.ACTION_SPLASH;
        }

        return errors;
    }

    /**
     * Setter for property listID.
     *
     * @param listID New value of property listID.
     */
    public void setListID(int listID) {
        this.listID = listID;
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
     * Setter for property action.
     *
     * @param action New value of property action.
     */
    public void setAction(int action) {
        this.action = action;
    }

    /**
     * Setter for property lines.
     *
     * @param lines New value of property lines.
     */
    public void setLines(int lines) {
        this.lines = lines;
    }
    
    /**
     * Setter for property rest.
     *
     * @param rest New value of property rest.
     */
    public void setRest(int rest) {
        this.rest = rest;
    }
    
    /**
     * Setter for property total.
     *
     * @param total New value of property total.
     */
    public void setTotal(int total) {
        this.total = total;
    }
    
    /**
     * Setter for property ips.
     *
     * @param ips New value of property ips.
     */
    public void setIps(java.util.LinkedList ips) {
        this.ips = ips;
    }
    
    /**
     * Setter for property subscribers.
     *
     * @param subscribers New value of property subscribers.
     */
    public void setSubscribers(java.util.LinkedList subscribers) {
        this.subscribers = subscribers;
    }
    
    /**
     * Setter for property maxIPs.
     *
     * @param maxIPs New value of property maxIPs.
     */
    public void setMaxIPs(int maxIPs) {
        this.maxIPs = maxIPs;
    }

    /**
     * Setter for property csvfile.
     *
     * @param csvfile New value of property csvfile.
     */
    public void setCsvfile(String csvfile) {
        this.csvfile = csvfile;
    }

    /**
     * Setter for property biggest.
     *
     * @param biggest New value of property biggest.
     */
    public void setBiggest(int biggest) {
        this.biggest = biggest;
    }

    /**
     * Getter for property listID.
     *
     * @return Value of property listID.
     */
    public int getListID() {
        return this.listID;
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
     * Getter for property action.
     *
     * @return Value of property action.
     */
    public int getAction() {
        return this.action;
    }
    
    /**
     * Getter for property lines.
     *
     * @return Value of property lines.
     */
    public int getLines() {
        return this.lines;
    }
    
    /**
     * Getter for property rest.
     *
     * @return Value of property rest.
     */
    public int getRest() {
        return this.rest;
    }
    
    /**
     * Getter for property total.
     *
     * @return Value of property total.
     */
    public int getTotal() {
        return this.total;
    }
    
    /**
     * Getter for property ips.
     *
     * @return Value of property ips.
     */
    public String getIps(int ndx) {
        return (String)ips.get(ndx);
    }
    
    /**
     * Getter for property ips.
     *
     * @return Value of property ips.
     */
    public java.util.LinkedList getIps() {
        return ips;
    }

    /**
     * Getter for property subscribers.
     *
     * @return Value of property subscribers.
     */
    public java.util.LinkedList getSubscribers() {
        return subscribers;
    }
    
    /**
     * Getter for property subscribers.
     *
     * @return Value of property subscribers.
     */
    public int getSubscribers(int ndx) {
        return ((Integer)subscribers.get(ndx)).intValue();
    }

    /**
     * Getter for property maxIps.
     *
     * @return Value of property maxIps.
     */
    public int getMaxIPs() {
        return this.maxIPs;
    }    
    
   /**
     * Getter for property csvfile.
     *
     * @return Value of property csvfile.
     */
    public String getCsvfile() {
        return this.csvfile;
    }
    
    /**
     * Getter for property biggest.
     *
     * @return Value of property biggest.
     */
    public int getBiggest() {
        return this.biggest;
    }
    
    /**
     * Getter for property statReady.
     *
     * @return Value of property statReady.
     */
    public boolean isStatReady() {
        return this.statReady;
    }    
    
    /**
     * Setter for property statReady.
     *
     * @param statReady New value of property statReady.
     */
    public void setStatReady(boolean statReady) {
        this.statReady = statReady;
    }
    
    /**
     * Getter for property statInProgress.
     *
     * @return Value of property statInProgress.
     */
    public boolean isStatInProgress() {
        return this.statInProgress;
    }
    
    /**
     * Setter for property statInProgress.
     *
     * @param statInProgress New value of property statInProgress.
     */
    public void setStatInProgress(boolean statInProgress) {
        this.statInProgress = statInProgress;
    }
    
}