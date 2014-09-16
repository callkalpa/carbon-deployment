<%@page import="org.wso2.carbon.jndi.resource.ui.JNDIResourceConfigurationClient" %>
<%@ page import="org.wso2.carbon.jndi.resource.ui.JNDIResourceConfigurationHelper" %>
<%@ page import="org.wso2.carbon.jndi.resource.mgt.data.xsd.Resource" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>

<%
    JNDIResourceConfigurationClient client;
    try {
        client = JNDIResourceConfigurationClient.getInstance(config, session);
        Resource resource = JNDIResourceConfigurationHelper.createResource(request, client);

//        boolean canAdd = true;
//
//        if (client.getResource(resource.getName()) != null) {
//            canAdd = false;
//        }
//
//        if (!canAdd) {
            client.registerJNDIResource(resource);
//        }
%>

<script type="text/javascript">
    forward("index.jsp");
</script>
<%
    } catch (Exception e) {
        e.printStackTrace();
    }
%>
