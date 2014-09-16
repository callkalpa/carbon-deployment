package org.wso2.carbon.jndi.resource.ui;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.jndi.resource.mgt.data.xsd.Resource;
import org.wso2.carbon.jndi.resource.stub.JNDIResourceConfigurationAdminStub;
import org.wso2.carbon.ui.CarbonUIUtil;
import org.wso2.carbon.utils.ServerConstants;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpSession;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class JNDIResourceConfigurationClient {


    private JNDIResourceConfigurationAdminStub stub;

    public JNDIResourceConfigurationClient(ConfigurationContext configCtx, String backendServerURL, String cookie) throws Exception {
        String serviceURL = backendServerURL + "JNDIResourceConfigurationAdmin";
        stub = new JNDIResourceConfigurationAdminStub(configCtx, serviceURL);
        ServiceClient client = stub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);
        options.setProperty(HTTPConstants.COOKIE_STRING, cookie);
    }

    public static JNDIResourceConfigurationClient getInstance(ServletConfig config,
                                                              HttpSession session) throws Exception {

        String backendServerURL = CarbonUIUtil.getServerURL(config.getServletContext(), session);
        ConfigurationContext configContext = (ConfigurationContext) config.getServletContext()
                .getAttribute(CarbonConstants.CONFIGURATION_CONTEXT);

        String cookie = (String) session.getAttribute(ServerConstants.ADMIN_SERVICE_COOKIE);
        return new JNDIResourceConfigurationClient(configContext, backendServerURL, cookie);

    }

    public ArrayList<Resource> getAllResources() {
        ArrayList<Resource> resourceses = new ArrayList<Resource>();

        try {
            for (Resource resource : stub.getAllResources()) {
                resourceses.add(resource);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return resourceses;
    }

    public void registerJNDIResource(Resource resource) {
        try {
            stub.addJNDIResource(resource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void removeJNDIResource(String name) {
        try {
            stub.removeJNDIResource(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Resource getResource(String name) {
        try {
            return stub.getResource(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

}
