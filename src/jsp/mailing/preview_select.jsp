<%@ page language="java" import="org.agnitas.util.*, org.agnitas.web.*, org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>

<agn:Permission token="mailing.send.show"/>

<% 
    int prevX=800;
    int prevY=600;
    int tmpMailingID=0;
    String tmpShortname=new String("");
    MailingSendForm aForm=null;
    int previewAction=MailingSendAction.ACTION_PREVIEW_HTML;
    if(request.getAttribute("mailingSendForm")!=null) {
        aForm=(MailingSendForm)request.getAttribute("mailingSendForm");
        tmpMailingID=aForm.getMailingID();
        tmpShortname=aForm.getShortname();
    }
    if(aForm.getEmailFormat()==0) {
        aForm.setPreviewFormat(0);
    }
    if(aForm.getPreviewFormat()==0) {
        previewAction=MailingSendAction.ACTION_PREVIEW_TEXT;
    }
    if(aForm.getPreviewFormat()==1) {
        previewAction=MailingSendAction.ACTION_PREVIEW_HTML;
    }
    if(aForm.getPreviewFormat()==2) {
        previewAction=MailingSendAction.ACTION_PREVIEW_OFFLINE;
    }


    switch(aForm.getPreviewSize()) {
        case 1:
            prevX=800;
            prevY=600;
            break;
        case 2:
            prevX=1024;
            prevY=768;
            break;
        case 3:
            prevX=1280;
            prevY=1024;
            break;
        case 4:
            prevX=640;
            prevY=480;
            break;
        default:
            aForm.setPreviewSize(1);
            prevX=800;
            prevY=600;
            break;
    }
%>

<logic:equal name="mailingSendForm" property="isTemplate" value="true">
    <% // template navigation:
        pageContext.setAttribute("sidemenu_active", new String("Templates"));
        pageContext.setAttribute("sidemenu_sub_active", new String("none"));
        pageContext.setAttribute("agnNavigationKey", new String("templateView"));
        pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing"));
        pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID));
        pageContext.setAttribute("agnTitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleKey", new String("Template"));
        pageContext.setAttribute("agnSubtitleValue", tmpShortname);
    %>
</logic:equal>

<logic:equal name="mailingSendForm" property="isTemplate" value="false">

    <% pageContext.setAttribute("sidemenu_active", new String("Mailings")); %>
    <% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
    <% pageContext.setAttribute("agnTitleKey", new String("Mailing")); %>
    <% pageContext.setAttribute("agnSubtitleKey", new String("Mailing")); %>
    <% pageContext.setAttribute("agnSubtitleValue", tmpShortname); %>
    <% pageContext.setAttribute("agnNavigationKey", new String("mailingView")); %>
    <% pageContext.setAttribute("agnHighlightKey", new String("Send_Mailing")); %>
    <% pageContext.setAttribute("agnNavHrefAppend", new String("&mailingID="+tmpMailingID)); %>
</logic:equal>

<%@include file="/header.jsp"%>

<html:errors/>
   <table border="0" cellspacing="0" cellpadding="0" width="100%" height="100%">
       <tr> 
           <td>
               <html:form action="/mailingsend">
                   <html:hidden property="mailingID"/>
                   <html:hidden property="action"/>
                   <bean:message key="Recipient"/>:&nbsp;
                   <html:select property="previewCustomerID" size="1">
                       <agn:ShowTable id="agntbl" sqlStatement="<%= new String("SELECT bind.customer_id, cust.email, cust.firstname, cust.lastname FROM customer_"+AgnUtils.getCompanyID(request)+"_tbl cust, customer_"+AgnUtils.getCompanyID(request)+"_binding_tbl bind WHERE (bind.user_type='A' OR bind.user_type='T') AND bind.user_status=1 AND bind.mailinglist_id="+aForm.getMailinglistID()+" AND bind.customer_id=cust.customer_id ORDER BY bind.user_type, bind.customer_id") %>" maxRows="100">
                           <% if(aForm.getPreviewCustomerID()==0) {
            aForm.setPreviewCustomerID(Integer.parseInt((String)pageContext.getAttribute("_agntbl_customer_id")));
                               } %>
                           <html:option value="<%= (String)(pageContext.getAttribute("_agntbl_customer_id")) %>"><%= pageContext.getAttribute("_agntbl_firstname") %>&nbsp;<%= pageContext.getAttribute("_agntbl_lastname") %>&nbsp;(<%= pageContext.getAttribute("_agntbl_email") %>)</html:option>
                       </agn:ShowTable>
                   </html:select>
                   &nbsp;&nbsp;
                   <bean:message key="Format"/>:&nbsp;
                   <html:select property="previewFormat" size="1">
                       <html:option value="0"><bean:message key="Text"/></html:option>
                       <logic:greaterThan name="mailingSendForm" property="emailFormat" value="0">
                           <html:option value="1"><bean:message key="HTML"/></html:option>
                       </logic:greaterThan>
                   </html:select>
                   &nbsp;&nbsp;    
                   <bean:message key="Size"/>:&nbsp;
                   <html:select property="previewSize" size="1">
                       <html:option value="4">640x480</html:option>
                       <html:option value="1">800x600</html:option>
                       <html:option value="2">1024x768</html:option>
                       <html:option value="3">1280x1024</html:option>
                   </html:select>
                   &nbsp;&nbsp;&nbsp;<html:image src="button?msg=Preview" border="0"/>
                   <hr size="1">
               </html:form>
           </td>
       </tr>
       <% if(aForm.getPreviewFormat()==0 || aForm.getPreviewFormat()==1 || aForm.getPreviewFormat()==2) { %>
       <tr>
           <td>
               <jsp:include page="/mailingsend.do" flush="true">
                   <jsp:param name="action" value="<%= MailingSendAction.ACTION_PREVIEW_HEADER %>"/>
                   <jsp:param name="previewCustomerID" value="<%= aForm.getPreviewCustomerID() %>"/>
               </jsp:include>
           </td>
       </tr>
       <% } %>
       <tr height="99%">
           <td width="100%" height="100%">
               <iframe src="<html:rewrite page="<%= new String("/mailingsend.do?action=" + previewAction + "&mailingID=" + aForm.getMailingID() + "&previewCustomerID=" + aForm.getPreviewCustomerID()) %>"/>" width="<%= prevX %>" height="<%= prevY %>" border="0" scrolling="auto">
                   Your Browser does not support IFRAMEs, please update!
               </iframe>
           </td>
       </tr>
   </table>
<%@include file="/footer.jsp"%>
