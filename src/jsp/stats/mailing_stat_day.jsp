<%@ page language="java" contentType="text/html; charset=utf-8" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.target.*, org.agnitas.beans.*, org.agnitas.stat.*" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="stats.rdir"/>

<% EmmCalendar my_calendar = new EmmCalendar(java.util.TimeZone.getDefault());
    java.util.Date my_time = my_calendar.getTime();
    String Datum = my_time.toString();
    String timekey = Long.toString(my_time.getTime());
    pageContext.setAttribute("time_key", timekey);
    int tmpMailingID=0;
    int tmpTargetID=0;
    int tmpUrlID=0;
    int tmpMaxblue=0;
    String tmpStartdate = "no";
    String tmpNetto = "no";
    String statfile = "";
    String aktURL = "";
    java.util.Hashtable tmpValues = null;
    String tmpShortname=new String("");
    MailingStatForm aForm=(MailingStatForm)session.getAttribute("mailingStatForm");
    if(aForm!=null) {
        tmpValues=(java.util.Hashtable)aForm.getValues();
        tmpMaxblue=aForm.getMaxblue();
        statfile=aForm.getCsvfile();
        // System.out.println("tmpMaxblue: " + tmpMaxblue);
        tmpMailingID=aForm.getMailingID();
        tmpTargetID=aForm.getTargetID();
        tmpShortname=aForm.getMailingShortname();
        aktURL=aForm.getAktURL();
        tmpUrlID=aForm.getUrlID();
        if(aForm.isNetto())
            tmpNetto = "on";
        //System.out.println("aForm.getStartdate(): " + aForm.getStartdate());
        if(aForm.getStartdate().compareTo("no")!=0)
            tmpStartdate = aForm.getStartdate();
    }
    // map for the csv download
    java.util.Hashtable my_map = null;
    if(pageContext.getSession().getAttribute("map") == null) {
        my_map = new java.util.Hashtable();
        pageContext.getSession().setAttribute("map",my_map);
    } else {
        my_map = (java.util.Hashtable)(pageContext.getSession().getAttribute("map"));
    }
    %>

<% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); %>
<% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
<% pageContext.setAttribute("agnNavigationKey", new String("mailingView")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("Statistics")); %>
<% pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>

<%@include file="/header.jsp"%>
<html:errors/>

<html:form action="/mailing_stat.do">

    <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td>

                <% String day = (String)(pageContext.getRequest().getParameter("startdate"));
                    java.util.GregorianCalendar aCal=new java.util.GregorianCalendar();
                    java.text.SimpleDateFormat bFormat=new java.text.SimpleDateFormat("yyyyMMdd");
                    try {
                        aCal.setTime(bFormat.parse(day));
                    } catch(Exception e) {
                        System.out.println("mailing_stat_day.jsp aCal.setTime Exception: "+e);
                    }
                    java.text.DateFormat aFormat=java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, (java.util.Locale)session.getAttribute(org.apache.struts.Globals.LOCALE_KEY));
                    java.util.Date aDate=aCal.getTime();
                %>

                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <span class="head3"><bean:message key="FeedbAnalys"/>:&nbsp;<%= aFormat.format(aDate) %><br></span>
                        </td>
                        <td align=right>
                            <html:link page="<%= new String("/file_download?key=" + timekey) %>"><img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>icon_save.gif" border="0"></html:link>
                        </td>
                    </tr>
                </table>
                        
                <logic:notEqual name="mailingStatForm" property="urlID" value="0">
                    <br><b>
                    <bean:message key="ForURL"/>
                    <a href="<%= aktURL %>" target="_blank">&quot;<%= aktURL %>&quot;</a></b>
                    <% //pageContext.setAttribute("full_url", pageContext.getAttribute("_agntbl4_full_url")); %>
                </logic:notEqual>

                <p>

                    <b><bean:message key="Target"/>:</b>&nbsp;
                    <html:hidden property="action"/>
                    <html:hidden property="mailingID"/>
                    <html:hidden property="urlID"/>
                    <html:hidden property="startdate"/>

                    <% String uds = (String)(pageContext.getRequest().getParameter("user_date")); %>
                    <% if( uds != null ) { %>
                    <input type="hidden" name="user_date" value="<%= uds %>">
                    <% } %>

                    <html:select property="targetID" size="1">
                        <html:option value="0"><bean:message key="All_Subscribers"/></html:option>
                        <agn:HibernateQuery id="trgt" query="<%= "from Target where companyID="+AgnUtils.getCompanyID(request) %>">
                            <html:option value="<%= ""+((Target)pageContext.getAttribute("trgt")).getId() %>"><%= ((Target)pageContext.getAttribute("trgt")).getTargetName() %></html:option>
                        </agn:HibernateQuery>
                    </html:select> 
                    &nbsp;&nbsp;<html:image src="button?msg=OK" border="0"/>
                </p>
            
            </td>    
        </tr>

        <tr>
            <td><hr size="1" noshade>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td valign=bottom>
                            <bean:message key="Clicks"/>:&nbsp;
                            <hr size="1" noshade>
                            <bean:message key="Time"/>:&nbsp;
                            </a>
                        </td>



                        <!-- * * * * * * *-->        
                        <!-- VALUES BEGIN -->
                        <%  String [] hours = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
                            String aktHour = "";
                            int i = 0;
                            /*
                            System.out.println("jsp: "+((MailingStatForm)session.getAttribute("mailingStatForm")).getValues().size() + " entries:");
                            java.util.Enumeration ke = ((MailingStatForm)session.getAttribute("mailingStatForm")).getValues().keys();
                            while (ke.hasMoreElements()) {
                            System.out.println(" - " + ke.nextElement());
                            }
                             */
                            String aktDate = "";
                            java.text.SimpleDateFormat format01=new java.text.SimpleDateFormat("yyyyMMdd");
                            aCal.add(aCal.DATE, -1);
                            aktDate = format01.format(aCal.getTime());
                        %>

                        <td valign=bottom><hr size="1" noshade>
                            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_DAYSTAT + "&mailingID=" + tmpMailingID + "&urlID=" + tmpUrlID + "&startdate=" + aktDate + "&targetID=" + tmpTargetID) %>">
                                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>arrow_back.gif" border="0">&nbsp;
                            </html:link>
                        </td>


                        <%
                            while(i<24) {
                                    aktHour = hours[i];
                                    //System.out.println("aktHour: " + aktHour);
                        %>

                        <td valign=bottom>
                            <center>
                                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel_h.gif" width="20" border="0" height="<% if(tmpValues.containsKey(aktHour)) { %><%= java.lang.StrictMath.floor(((Integer)(tmpValues.get(aktHour))).doubleValue()/(double)tmpMaxblue*200) %><% } else { %>1<% } %>">&nbsp;
                                <% if(tmpValues.containsKey(aktHour)) { %><%= tmpValues.get(aktHour) %><% } else { %>0<% } %>&nbsp;
                                <hr size="1" noshade>
                                <%= i %>
                            </center>
                        </td>

                        <% statfile += "\r\n\"" + aktHour + "\";\"";
                            if(tmpValues.containsKey(aktHour))
                                statfile += tmpValues.get(aktHour);
                            else
                                statfile += "0";
                            statfile += "\"";
                            i++;
                            }
                        %>
                        <!-- VALUES END -->
                        <!-- * * * * * *-->     
                        <%
                            aCal.add(aCal.DATE, 2);
                            aktDate = format01.format(aCal.getTime());
                        %>

                        <td valign=bottom><hr size="1" noshade>
                            <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_DAYSTAT + "&mailingID=" + tmpMailingID + "&urlID=" + tmpUrlID + "&startdate=" + aktDate + "&targetID=" + tmpTargetID) %>">                          
                                <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>arrow_next.gif" border="0">&nbsp;
                            </html:link>
                        </td>

                    </tr>
                </table>
            </td>
        </tr>

        <tr>
            <td>              
                <hr size="1" noshade>
                <B><bean:message key="Entirely"/>:</B>&nbsp;<%= ((MailingStatForm)session.getAttribute("mailingStatForm")).getClicks()  %>&nbsp;<bean:message key="Clicks"/>
                <html:link page="<%= new String("/mailing_stat.do?action=" + MailingStatAction.ACTION_MAILINGSTAT + "&mailingID=" + tmpMailingID + "&targetID=" + tmpTargetID) %>"><html:img src="button?msg=Back" border="0"/></html:link>
            </td>
        </tr>
    </table>

</html:form>
            <%
                // put csv file from the form in the hash table:
                my_map.put(timekey,  statfile);
                pageContext.getSession().setAttribute("map", my_map);
            %>

<%@include file="/footer.jsp"%>
