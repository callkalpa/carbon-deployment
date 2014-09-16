<%@ page import="org.wso2.carbon.jndi.resource.ui.JNDIResourceConfigurationClient" %>
<%

    String jndiResourceName = request.getParameter("name").trim();
    if (jndiResourceName == null || "".equals(jndiResourceName)) {
        throw new ServletException("Resource name is empty");
    }

    JNDIResourceConfigurationClient client = JNDIResourceConfigurationClient.getInstance(config, session);
    client.removeJNDIResource(jndiResourceName);
%>

<script type="text/javascript">
    forward("index.jsp");
</script>