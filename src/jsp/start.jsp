<%@ page language="java" import="org.agnitas.beans.*" contentType="text/html; charset=utf-8" %>
<%@ taglib uri="/WEB-INF/agnitas-taglib.tld" prefix="agn" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>

<agn:CheckLogon/>
<% pageContext.setAttribute("sidemenu_active", new String("none")); %>
<% pageContext.setAttribute("sidemenu_sub_active", new String("none")); %>
<% pageContext.setAttribute("agnTitleKey", new String("A_EMM")); %>
<% pageContext.setAttribute("agnSubtitleKey", new String("Welcome")); %>
<% pageContext.setAttribute("agnSubtitleValue", ((Admin)session.getAttribute("emm.admin")).getFullname()); %>
<% pageContext.setAttribute("agnNavigationKey", new String("none")); %>
<% pageContext.setAttribute("agnHighlightKey", new String("none")); %>

<%@include file="/header.jsp"%>
<% int i=0; %>
<html:errors/>
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>  
                  <td>
                     <table border="0" cellspacing="10" cellpadding="0">
                        <tr>
                        <agn:ShowNavigation navigation="sidemenu" highlightKey="">
                           <agn:ShowByPermission token="<%= _navigation_token %>">
                              <% if(i==2) { %> </tr><tr> <% i=0; } i++; %>
                              <td>
                                  <table width="300" cellspacing="0" cellpadding="0">
                                  <tr>
                                      <td width="40"><html:link page="<%= new String(_navigation_href) %>"><img border="0" width="40" height="38" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>splash_<%= _navigation_navMsg.toLowerCase() %>.gif" alt="<bean:message key="<%= _navigation_navMsg %>"/>"></html:link></td>
                                      <td class="boxhead" width="250"><html:link page="<%= _navigation_href %>"><span class="head1"><bean:message key="<%= _navigation_navMsg %>"/></span></html:link></td>
                                      <td width="10"><img border="0" width="10" height="38" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>box_topright.gif" alt="top right"></td>
                                  </tr><tr>
                                      <td colspan=3 class="boxmiddle" height="80" width="300"><img src="images/emm/one_pixel.gif" alt="spacer" width=1 height=60 align="left"><html:link page="<%= _navigation_href %>"><bean:message key="<%= new String("splash."+_navigation_navMsg) %>"/></html:link></td>
                                  </tr><tr>
                                      <td width="40"><img border="0" width="40" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>box_bottomleft.gif" alt="<bean:message key="<%= _navigation_navMsg %>"/>"></td>
                                      <td class="boxbottom"></td>
                                      <td width="10"><img border="0" width="10" height="10" src="<bean:write name="emm.layout" property="baseUrl" scope="session"/>box_bottomright.gif" alt="bottom right"></td>
                                  </tr>
                              </table>
                              </td>
                              
                           </agn:ShowByPermission>
                        </agn:ShowNavigation>
                        </tr>
                     </table>
                  </td>
                </tr>
              </table>
<%@include file="/footer.jsp"%>
