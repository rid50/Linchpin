<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"  "http://www.w3.org/TR/html4/strict.dtd">
<%@ page import="com.autheus.authmanager.web.utils.PresentationUtils,
                 com.autheus.authmanager.common.Constants,
                 com.autheus.authmanager.common.DatabaseType,
                 com.autheus.authmanager.common.LdapType"%>

<%@ include file="/common/include.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
   	  <title><i18n:message key="label.datasource.title"/></title>
      <%@ include file="/common/sharedhead.jsp" %>
      
      <script>
        function toggleDatasourceForm() {
            if(document.getElementById('sourceTypeDb').checked) {
                document.getElementById('ldapTypeDiv').className = 'div_off';
                document.getElementById('dbTypeDiv').className = 'div_on';
            } else {
                document.getElementById('ldapTypeDiv').className = 'div_on';
                document.getElementById('dbTypeDiv').className = 'div_off';
            }
        }

        function submitForm(elemId) {
            document.getElementById(elemId).click();
        }
      </script>
      
  </head>
  <body>
  <div id="wrap">
    <div id="header">
    	<jsp:include page="/common/header.jsp"/>
    </div>
    <div id="nav"></div>
    <div id="main">
        <form method="post" id="datasourceForm">
        <div style="border:1px #ccc solid;padding:3px">
        <table cellpadding="1"  width="100%" border="0">
            <tr class="nav_2_off">
                <%boolean userSource=(String.valueOf(Constants.DATASOURCE_TYPE_USERSOURCE).equals(request.getParameter("type")));%>
                <td class="<%=userSource?"nav_2_on":""%>" width="25%" height="25">
                    <%if(!userSource) {%>
                    <a href="<c:url value='/datasource/datasourceForm.htm?type='/><%=Constants.DATASOURCE_TYPE_USERSOURCE%>">User Source</a>
                    <%} else {%>
                    User Source
                    <%}%>
                </td>
                <%boolean userTarget=(String.valueOf(Constants.DATASOURCE_TYPE_USERTARGET).equals(request.getParameter("type")));%>
                <td class="<%=userTarget?"nav_2_on":""%>" width="25%" height="25">
                    <%if(!userTarget) {%>
                    <a href="<c:url value='/datasource/datasourceForm.htm?type='/><%=Constants.DATASOURCE_TYPE_USERTARGET%>">User Target</a>
                    <%} else {%>
                    User Target
                    <%}%>
                </td>
                <%boolean groups=(String.valueOf(Constants.DATASOURCE_TYPE_GROUP).equals(request.getParameter("type")));%>
                <td class="<%=groups?"nav_2_on":""%>" width="25%" height="25">
                    <%if(!groups) {%>
                    <a href="<c:url value='/datasource/datasourceForm.htm?type='/><%=Constants.DATASOURCE_TYPE_GROUP%>">Group</a>
                    <%} else {%>
                    Groups
                    <%}%>
                </td>
                <%boolean ruleSets=(String.valueOf(Constants.DATASOURCE_TYPE_RULESET).equals(request.getParameter("type")));%>
                <td class="<%=ruleSets?"nav_2_on":""%>" width="25%" height="25">
                    <%if(!ruleSets) {%>
                    <a href="<c:url value='/datasource/datasourceForm.htm?type='/><%=Constants.DATASOURCE_TYPE_RULESET%>">Rule Set</a>
                    <%} else {%>
                    Rule Sets
                    <%}%>
                </td>
            </tr>
            <tr>
                <td colspan="4">
                <div>
                    <table border="0" cellpadding="0" cellspacing="3" align="center" width="400">
                        <tr>
                            <td class="datasource_form_label">Datasource Type:</td>
                            <td class="datasource_form_field">
                                <input id="sourceTypeDb" type="radio" name="datasourceType" value="<%=Constants.DATASOURCE_DB%>" checked onclick="toggleDatasourceForm();"/>
                                <b><i18n:message key="label.db.title" /></b>
                                <input id="sourceTypeLdap" type="radio" name="datasourceType" value="<%=Constants.DATASOURCE_LDAP%>" onclick="toggleDatasourceForm();"/>
                                <b><i18n:message key="label.ldap.title" /></b>
                            </td>
                        </tr>
                    </table>
                </div>
                <div id="dbTypeDiv" class="div_on">
                    <table border="0" cellpadding="0" cellspacing="3" align="center" width="400">
                        <spring:bind path="command.dbType">
                        <tr <c:if test="${status.error}">class="error"</c:if>>
                            <td class="datasource_form_label"><i18n:message key="label.db.databaseType" />:</td>
                            <td class="datasource_form_field">
                                <select name="dbType" class="miniselect" onchange="submitForm('submitDatabaseType')">
                                    <logic:iterate id="currentItem" name="databaseTypes">
                                    <%DatabaseType current = (DatabaseType)pageContext.getAttribute("currentItem");%>
                                    <option value="<%=current.getId()%>" <%=String.valueOf(current.getId()).equals(status.getDisplayValue())?"selected":""%>>
                                        <bean:write name="currentItem" property="databaseVendor" scope="page" />
                                    </option>
                                    </logic:iterate>
                                </select>
                                <logic:equal value="true" name="status" property="error"><span class="error"><bean:write name="status" property="errorMessage"/></span></logic:equal>
                            </td>
                        </tr>
                        </spring:bind>
                        <tr>
                            <td>&nbsp;</td>
                            <td class="datasource_help">
                            Specify a Database Server Vendor.
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td class="datasource_help">
                            <input type="SUBMIT" id="submitDatabaseType" name="handleSelectVendor" value="select" style="display:none"/>
                            </td>
                        </tr>
                    </table>
                </div>
                <div id="ldapTypeDiv" class="div_off">
                    <table border="0" cellpadding="0" cellspacing="3" align="center" width="400">
                        <spring:bind path="command.ldapType">
                        <tr <c:if test="${status.error}">class="error"</c:if>>
                            <td class="datasource_form_label"><i18n:message key="label.ldap.type" />:</td>
                            <td class="datasource_form_field">
                                <select name="ldapType" class="miniselect" onchange="submitForm('submitLdapType')" >
                                    <logic:iterate id="currentItem" name="ldapTypes">
                                    <%LdapType current = (LdapType)pageContext.getAttribute("currentItem");%>
                                    <option value="<%=current.getId()%>" <%=String.valueOf(current.getId()).equals(status.getDisplayValue())?"selected":""%>>
                                        <bean:write name="currentItem" property="ldapVendor" scope="page" />
                                    </option>
                                    </logic:iterate>
                                </select>
                                <logic:equal value="true" name="status" property="error"><span class="error"><bean:write name="status" property="errorMessage"/></span></logic:equal>
                            </td>
                        </tr>
                        </spring:bind>
                        <tr>
                            <td>&nbsp;</td>
                            <td class="datasource_help">
                            LDAP directory type.
                            </td>
                        </tr>
                        <tr>
                            <td>&nbsp;</td>
                            <td class="datasource_help">
                            <input type="SUBMIT" id="submitLdapType" name="handleSelectVendor" value="selectDATASOURCE" style="display:none"/>
                            </td>
                        </tr>
                    </table>
                </div>
                </td>
            </tr>
                
        </table>
        </div>
        </form>
    </div>
    <div id="sidebar">
       <jsp:include page="list/masterList.jsp"/>
    </div>
    <div id="footer"></div>
  </div>

    <%int type=Integer.parseInt(request.getParameter("type"));%>
    <script>
    <%  String disableElements = PresentationUtils.getAvailableFormTypes(type); %>
    <%  String checkElements = PresentationUtils.getUnavailableFormTypes(type);
        if(!"".equals(disableElements)) {%>
            document.getElementById('<%=checkElements%>').checked=true;
        <%}%>
        toggleDatasourceForm();
    <%  String disableElement[] = PresentationUtils.getDisableFormTypes(request, type, true).split(",");
        for(int i=0;disableElements!=null && i<disableElement.length;i++) {
            if(!"".equals(disableElement[i])) {%>
                document.getElementById('<%=disableElement[i]%>').disabled=true;
        <%}%>
    <%}%>
    </script>
  </body>
</html>