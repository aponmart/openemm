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



<html:form action="/mwSendaddress" focus="senderEmail">
    <html:hidden property="action"/>
    
    <b><font color=#73A2D0><bean:message key="MWizardStep_4_of_11"/></font></b>
    
    <br>
    <img src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>one_pixel.gif" width="400" height="10" border="0">
    <br>
    
    <b><bean:message key="SendAddressMsg"/>:</b><br><br>
    
    
   
    <BR>
    <BR> 

    <table border="0" cellspacing="0" cellpadding="0">
        <tr> 
            <td><bean:message key="Sender_Adress"/>:&nbsp;</td>
            <td> 
                <html:text property="senderEmail" maxlength="99" size="42"/>
            </td>
        </tr>
        <tr> 
            <td><bean:message key="SenderFullname"/>:&nbsp;</td>
            <td> 
                <html:text property="senderFullname" maxlength="99" size="42"/>
            </td>
        </tr>
        
        <tr> 
            <td><bean:message key="ReplyFullName"/>:&nbsp;</td>
            <td> 
                <html:text property="replyFullname" maxlength="99" size="42"/>
            </td>
        </tr>    
    </table>
 
    <% // wizard navigation: %>
    <br>
    <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td>&nbsp;</td>
            <td align="right">
                &nbsp;
                <html:image src="button?msg=Back"  border="0" onclick="document.mailingWizardForm.action.value='previous'"/>
                &nbsp;
                <html:image src="button?msg=Proceed"  border="0" onclick="<%= "document.mailingWizardForm.action.value='" + MailingWizardAction.ACTION_SENDADDRESS + "'" %>"/>
                &nbsp;
            </td>
        </tr>
    </table>         
</html:form>
<%@include file="/footer.jsp"%>
