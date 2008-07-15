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

package org.agnitas.dao.impl;


import org.agnitas.dao.TrackableLinkDao;
import org.springframework.context.*;
import org.springframework.orm.hibernate3.*;
import org.hibernate.*;
import org.agnitas.beans.*;
import org.agnitas.util.*;

/**
 *
 * @author mhe
 */
public class TrackableLinkDaoImpl implements TrackableLinkDao {
    
    /** Creates a new instance of MailingDaoImpl */
    public TrackableLinkDaoImpl() {
    }
    
    public TrackableLink getTrackableLink(int linkID, int companyID) {
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        
        if(linkID==0 || companyID==0) {
            return null;
        }
        
        return (TrackableLink)AgnUtils.getFirstResult(tmpl.find("from TrackableLink where id = ? and companyID = ?", new Object [] {new Integer(linkID), new Integer(companyID)} ));
    }
    
    public int saveTrackableLink(TrackableLink link) {
        int result=0;
        TrackableLink tmpLink=null;
        
        if(link==null || link.getCompanyID()==0) {
            return 0;
        }
        
        HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
        if(link.getId()!=0) {
            tmpLink=(TrackableLink)AgnUtils.getFirstResult(tmpl.find("from TrackableLink where id = ? and companyID = ?", new Object [] {new Integer(link.getId()), new Integer(link.getCompanyID())} ));
            if(tmpLink==null) {
                link.setId(0);
            }
        }
        
        tmpl.saveOrUpdate("TrackableLink", link);
        result=link.getId();
        tmpl.flush();
        
        return result;
    }
    
    public boolean deleteTrackableLink(int linkID, int companyID) {
        TrackableLink tmp=null;
        boolean result=false;
        
        if((tmp=this.getTrackableLink(linkID, companyID))!=null) {
            HibernateTemplate tmpl=new HibernateTemplate((SessionFactory)this.applicationContext.getBean("sessionFactory"));
            try {
                tmpl.delete(tmp);
                result=true;
            } catch (Exception e) {
                result=false;
            }
        }
        
        return result;
    }
    
    /**
     * Holds value of property applicationContext.
     */
    protected ApplicationContext applicationContext;
    
    /**
     * Setter for property applicationContext.
     * @param applicationContext New value of property applicationContext.
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        
        this.applicationContext = applicationContext;
    }
    
}
