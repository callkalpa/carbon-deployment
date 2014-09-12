package org.wso2.carbon.jndi.resource.ui;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.jndi.resource.mgt.data.xsd.Resource;
import org.wso2.carbon.jndi.resource.stub.*;

import java.lang.reflect.Array;
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

    public ArrayList<Resource> getAllResources() {
        ArrayList<Resource> resourceses = new ArrayList<Resource>();

        try {
            for(Resource resource: stub.getAllResources()){
                resourceses.add(resource);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return resourceses;
    }
}
