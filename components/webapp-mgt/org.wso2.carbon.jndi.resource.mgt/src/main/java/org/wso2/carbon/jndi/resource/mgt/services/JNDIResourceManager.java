package org.wso2.carbon.jndi.resource.mgt.services;

import org.w3c.dom.Document;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.jndi.resource.mgt.data.Resource;
import org.wso2.carbon.jndi.resource.mgt.utils.JNDIResourceUtils;
import org.wso2.carbon.registry.api.RegistryException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.naming.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

public class JNDIResourceManager {

    private org.wso2.carbon.registry.api.Registry registry;
    private InitialContext initialContext;

    private static String JMS_RESOURCE_REGISTRY_BASE_PATH = "/repository/components/org.wso2.carbon.jndi.resource";

    public JNDIResourceManager() {
        registerAllResources();
    }

    private void addResourceToRegistry(Resource resource) {
        try {
            org.wso2.carbon.registry.api.Resource resource1 = this.getRegistry().newResource();
            resource1.setContentStream(JNDIResourceUtils.convertResourceToInputStream(resource));
            this.getRegistry().put(JMS_RESOURCE_REGISTRY_BASE_PATH + "/" + resource.getName().replace("/", "."), resource1);
        } catch (RegistryException e) {
            e.printStackTrace();
        }
    }

    private void removeResourceFromRegistry(String name) {
        try {
            this.getRegistry().delete(JMS_RESOURCE_REGISTRY_BASE_PATH + "/" + name.replace("/", "."));
        } catch (RegistryException e) {
            e.printStackTrace();
        }
    }

    private String[] getResourcePaths() {
        org.wso2.carbon.registry.api.Resource resources;
        try {
            resources = this.getRegistry().get(JMS_RESOURCE_REGISTRY_BASE_PATH);
            return (String[]) resources.getContent();
        } catch (RegistryException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Resource> getAllResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();

        String[] resourcePaths = null;

        if ((resourcePaths = getResourcePaths()) == null) {
            return resources;
        }

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
            for (String resourcePath : resourcePaths) {
                org.wso2.carbon.registry.api.Resource res = this.getRegistry().get(resourcePath);
                document = builder.parse(new InputSource(res.getContentStream()));
                Resource resource = JNDIResourceUtils.convertElementToResource(document.getDocumentElement());
                resources.add(resource);
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (RegistryException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resources;
    }

    public Resource getResource(String name) {
        org.wso2.carbon.registry.api.Resource res = null;
        Resource resource = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document document = null;
        try {
            res = this.getRegistry().get(JMS_RESOURCE_REGISTRY_BASE_PATH + name.replace(".", "/"));
            document = builder.parse(new InputSource(res.getContentStream()));
            resource = JNDIResourceUtils.convertElementToResource(document.getDocumentElement());
        } catch (RegistryException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resource;
    }

    private void registerAllResources() {
        try {
            for (Resource resource : getAllResources()) {
                registerJNDIResource(resource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterAllResources() {
        for (Resource resource : getAllResources()) {
            unregisterJNDIResource(resource.getName());
        }
    }

    private void registerJNDIResource(Resource resource) {
        InitialContext initContext = getInitialContext();
        try {
            Properties props = new Properties();
            props.put("name", resource.getName());
            props.put("auth", resource.getAuth());
            props.put("type", resource.getType());
            props.put("description", resource.getDescription());
            props.put("factory", resource.getFactory());
            props.put("physicalName", resource.getPhysicalName());

//            for (Prop property : resource.getProperties()) {
//                props.put(property.getKey(), property.getValue());
//            }

            Class resourceClass = Class.forName(resource.getType());
            Object resourceObject = resourceClass.newInstance();

            Method setProperties = resourceClass.getMethod("setProperties", new Class[]{Properties.class});
            setProperties.invoke(resourceObject, props);

            checkAndCreateJNDISubContexts(initContext, resource.getName());
            initContext.rebind(resource.getName(), resourceObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unregisterJNDIResource(String name) {
        InitialContext initContext = getInitialContext();
        try {
            if (isRegistered(initContext, name)) {
                initContext.unbind(name);
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private org.wso2.carbon.registry.api.Registry getRegistry() {
        if (registry == null) {
            registry = CarbonContext.getCurrentContext().getRegistry(RegistryType.SYSTEM_GOVERNANCE);
        }
        return registry;
    }

    private InitialContext getInitialContext() {
        if (initialContext == null) {
            Hashtable environment = new Hashtable();
            environment.put("java.naming.factory.initial", "org.wso2.carbon.tomcat.jndi.CarbonJavaURLContextFactory");
            try {
                return new InitialContext(environment);
            } catch (NamingException e) {
                e.printStackTrace();
            }
        }
        return initialContext;
    }

    private void checkAndCreateJNDISubContexts(Context context, String jndiName) throws Exception {
        String[] tokens = jndiName.split("/");
        Context tmpCtx;
        String token;
        for (int i = 0; i < tokens.length - 1; i++) {
            token = tokens[i];
            tmpCtx = this.lookupJNDISubContext(context, token);
            if (tmpCtx == null) {
                try {
                    tmpCtx = context.createSubcontext(token);
                } catch (NamingException e) {
                    throw new Exception(
                            "Error in creating JNDI subcontext '" + context +
                                    "/" + token + ": " + e.getMessage(), e
                    );
                }
            }
            context = tmpCtx;
        }
    }

    private Context lookupJNDISubContext(Context context, String jndiName) throws Exception {
        try {
            Object obj = context.lookup(jndiName);
            if (!(obj instanceof Context)) {
                throw new Exception("JNDI context already exists at '" + context + "/" + jndiName);
            }
            return (Context) obj;
        } catch (NamingException e) {
            return null;
        }
    }

    private boolean isRegistered(Context context, String name) {
        try {
            lookupJNDISubContext(context, name);
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public void addJNDIResource(Resource resource) {
        addResourceToRegistry(resource);
        registerJNDIResource(resource);
    }

    public void removeJNDIResource(String name) {
        unregisterJNDIResource(name);
        removeResourceFromRegistry(name);
    }
}