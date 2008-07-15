<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, java.util.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<% MailingWizardForm aForm=null;
    aForm=(MailingWizardForm)session.getAttribute("mailingWizardForm");
    Mailing mailing=aForm.getMailing();
%>

<agn:Permission token="mailing.show"/>


<%
// mailing navigation:
    pageContext.setAttribute("sidemenu_active", new String("Mailings")); 
    pageContext.setAttribute("sidemenu_sub_active", new String("New_Mailing"));
    pageContext.setAttribute("agnNavigationKey", new String("MailingWizard"));
    pageContext.setAttribute("agnHighlightKey", new String("MailingWizard"));
    pageContext.setAttribute("agnTitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); 
    pageContext.setAttribute("agnSubtitleValue", mailing.getShortname());
%>

<%@include file="/header.jsp"%>

<html:errors/>


<html:form action="/mwTextmodules">

    <html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="MWizardStep_8_of_11"/></font></b>

    <br>
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>

    
    <span class="head3"><bean:message key="TextModules"/></span>

    <br><br>

    <b><bean:message key="TextModulesMsg"/></b>
    
    <BR>
    <BR> 

    
    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=Back"  border="0" onclick="<%= "document.mailingWizardForm.action.value='previous'" %>"/>
                &nbsp;
                <html:image src="button?msg=Proceed"  border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_TEXTMODULE + "'" %>"/>
                &nbsp;
                <html:image src="button?msg=Skip" border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_MEASURELINKS + "'" %>"/>
                &nbsp;
                <html:image src="button?msg=Finish" border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_FINISH + "'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>             
</html:form>
<%@include file="/footer.jsp"%>
