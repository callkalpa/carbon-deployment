package org.wso2.carbon.jndi.resource.ui;

import org.wso2.carbon.jndi.resource.mgt.data.xsd.Resource;

import javax.servlet.http.HttpServletRequest;

public class JNDIResourceConfigurationHelper {

    public static Resource createResource(HttpServletRequest request, JNDIResourceConfigurationClient client) {
        String name = request.getParameter("name");
        String auth = request.getParameter("auth");
        String type = request.getParameter("type");
        String description = request.getParameter("description");
        String factory = request.getParameter("factory");
        String physicalName = request.getParameter("physicalName");

        Resource resource = new Resource();
        resource.setName(name);
        resource.setAuth(auth);
        resource.setType(type);
        resource.setDescription(description);
        resource.setFactory(factory);
        resource.setPhysicalName(physicalName);

        return resource;
    }
}
