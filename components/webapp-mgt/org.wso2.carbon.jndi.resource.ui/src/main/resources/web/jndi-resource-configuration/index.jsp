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
                for (Resource resource : allResources) {
            %>
            <tr>
                <td><%=resource.getName()%>
                </td>
                <td><%=resource.getType()%>
                </td>
                <td>Action</td>
            </tr>

            <%
                }
            %>

            </tbody>
        </table>


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