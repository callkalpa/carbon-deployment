<%@ page import="org.apache.axis2.context.ConfigurationContext" %>
<%@ page import="org.wso2.carbon.CarbonConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIUtil" %>
<%@ page import="org.wso2.carbon.utils.ServerConstants" %>
<%@ page import="org.wso2.carbon.ui.CarbonUIMessage" %>
<%@ page import="org.wso2.carbon.jndi.resource.ui.JNDIResourceConfigurationClient" %>
<%@ page import="org.wso2.carbon.jndi.resource.mgt.data.xsd.Resource" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>

<script type="text/javascript" src="jndi-resource.js"></script>
<%
    String serverURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
    ConfigurationContext configContext =
            (ConfigurationContext) config.getServletContext().getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);
    String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);

    JNDIResourceConfigurationClient client;


    try {
        client = new JNDIResourceConfigurationClient(configContext, serverURL, cookie);

        ArrayList<Resource> allResources = client.getAllResources();
%>

<div id="middle">
    <h2>JNDI Resource Configuration</h2>

    <div id="workArea">
        <p>Available JNDI Resources</p>
        <br/>

        <table class="styledLeft">
            <thead>
            <tr>
                <th>JNDI Resource name</th>
                <th>Resource type</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>

            <%
                String resourceName = null;
                for (Resource resource : allResources) {
                    resourceName = resource.getName();
            %>
            <tr>
                <td><%=resourceName%>
                </td>
                <td><%=resource.getType()%>
                </td>
                <td>
                    <a href="#" class="edit-icon-link"
                       onclick="editRow('<%=resourceName%>')">Edit</a>
                    <a href="#" class="delete-icon-link"
                       onclick="deleteRow('<%=resourceName%>','Are you sure you want to delete the JNDI resource')">Delete</a>
                </td>
            </tr>

            <%
                }
            %>

            </tbody>
        </table>

        <div style="height:30px;">
            <a href="javascript:document.location.href='newjndiresource.jsp'" class="add-icon-link">Add
                JNDI Resource</a>
        </div>


    </div>

</div>


<%
} catch (Exception e) {
    CarbonUIMessage uiMsg = new CarbonUIMessage(CarbonUIMessage.ERROR, e.getMessage(), e);
    session.setAttribute(CarbonUIMessage.ID, uiMsg);
%>
<script type="text/javascript">
    window.location.href = "../admin/error.jsp";
</script>
<%


    }
%>