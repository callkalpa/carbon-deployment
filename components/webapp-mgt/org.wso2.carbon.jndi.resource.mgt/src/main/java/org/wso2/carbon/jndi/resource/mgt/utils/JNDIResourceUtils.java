package org.wso2.carbon.jndi.resource.mgt.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.carbon.jndi.resource.mgt.data.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class JNDIResourceUtils {

    public static Element convertResourceToElement(Resource resource) {
        Element element = null;

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            Document document = documentBuilderFactory.newDocumentBuilder().newDocument();

            JAXBContext jaxbContext = JAXBContext.newInstance(Resource.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(resource, document);
            element = document.getDocumentElement();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return element;
    }

    public static Resource convertElementToResource(Element element) {
        Resource resource = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Resource.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            resource = (Resource) unmarshaller.unmarshal(element);
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return resource;
    }

    public static InputStream convertElementToInputStream(Element element) {

        if (element == null) {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source source = new DOMSource(element);
        Result result = new StreamResult(outputStream);
        InputStream inputStream = null;
        try {
            TransformerFactory.newInstance().newTransformer().transform(source, result);
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        } catch (TransformerException e) {
            e.printStackTrace();
        }

        return inputStream;
    }

    public static InputStream convertResourceToInputStream(Resource resource){
        Element element = convertResourceToElement(resource);
        return convertElementToInputStream(element);
    }
}