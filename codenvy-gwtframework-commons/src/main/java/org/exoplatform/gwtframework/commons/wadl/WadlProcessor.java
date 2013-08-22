/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.gwtframework.commons.wadl;

import com.google.gwt.xml.client.*;

import org.exoplatform.gwtframework.commons.xml.QName;

import java.util.Arrays;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */

public class WadlProcessor {

    //   public WadlProcessor(String url, Boolean b)
    //   {
    //
    //      com.google.gwt.http.client.RequestBuilder.Method method = RequestBuilder.GET;
    //      builder = new RequestBuilder( method, url);
    //   }
    //
    public WadlProcessor() {
    }

    /**
     * Parse Wadl string
     *
     * @param wadlString
     * @throws IllegalWADLException
     */
    private static void parse(WadlApplication application, String wadlString) throws IllegalWADLException {
        try {
            Document wadl = XMLParser.parse(wadlString);
            Element app = wadl.getDocumentElement();
            NodeList childApp = app.getChildNodes();
            for (int i = 0; i < childApp.getLength(); i++) {
                Node current = childApp.item(i);
                if (current.getNodeType() == Node.ELEMENT_NODE) {

                    if (current.getNodeName().equals(ElementNames.DOC)) {
                        Doc docApp = getDocFromElement(current);
                        application.getDoc().add(docApp);

                    } else if (current.getNodeName().equals(ElementNames.GRAMMARS)) {
                        Grammars grammars = getGrammars(current);
                        application.setGrammars(grammars);

                    } else if (current.getNodeName().equals(ElementNames.RESOURCES)) {
                        Resources resources = getResources(current);
                        application.setResources(resources);

                    } else if (current.getNodeName().equals(ElementNames.RESOURCE_TYPE)) {
                        ResourceType resourceType = getResourceType(current);
                        application.getResourceTypeOrMethodOrRepresentation().add(resourceType);

                    } else if (current.getNodeName().equals(ElementNames.mETHOD)) {
                        Method met = getMethod(current);
                        application.getResourceTypeOrMethodOrRepresentation().add(met);

                    } else if (current.getNodeName().equals(ElementNames.REPRESENTATION)) {
                        RepresentationType representation = getRepresentation(current);
                        application.getResourceTypeOrMethodOrRepresentation().add(representation);

                    } else if (current.getNodeName().equals(ElementNames.PARAM)) {
                        Param param = getParam(current);
                        application.getResourceTypeOrMethodOrRepresentation().add(param);//???
                        //no param field in Application class

                    } else {
                        application.getAny().add(current);
                    }
                }
            }

        } catch (DOMException e) {
            e.printStackTrace();
            throw new IllegalWADLException("Invalid WADL");
        }
    }

    public static void unmarshal(WadlApplication application, String wadlResponce) throws IllegalWADLException {
        if (wadlResponce != null) {
            parse(application, wadlResponce);
        } else
            throw new IllegalWADLException("Empty Wadl");
    }

    /**
     * Generate Doc object from Node element
     *
     * @param {@link
     *         Node}
     * @return {@link Doc}
     * @throws IllegalWADLException
     */

    private static Doc getDocFromElement(Node node) throws IllegalWADLException {
        Doc doc = new Doc();
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            doc.getContent().add(nodeList.item(i).getNodeValue());
        }
        NamedNodeMap attributes = node.getAttributes();

        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("title")) {
                    doc.setTitle(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("xml:lang")) {
                    doc.setLang(attNode.getNodeValue());
                } else {
                    QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                    doc.getOtherAttributes().put(name, attNode.getNodeValue());
                }

            }
        }

        if ((doc.getTitle() == null) || (doc.getLang() == null))
            throw new IllegalWADLException("Doc element must hew attributes title and xml:lang");

        //      Element nod =(Element) elem;
        //      doc.setTitle(nod.getAttribute("title"));
        //      doc.setLang(nod.getAttribute("xml:lang"));
        //      String docText = nod.getFirstChild().getNodeValue();
        //      doc.getContent().add(docText);
        return doc;
    }

    /**
     * Generate ResourceType object from node element
     *
     * @param node
     *         {@link Node}
     * @return {@link ResourceType}
     * @throws IllegalWADLException
     */

    private static ResourceType getResourceType(Node node) throws IllegalWADLException {
        ResourceType resourceType = new ResourceType();
        NodeList resorceList = node.getChildNodes();
        for (int i = 0; i < resorceList.getLength(); i++) {
            Node resNode = resorceList.item(i);
            if (resNode.getNodeType() == Node.ELEMENT_NODE) {
                if (resNode.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(resNode);
                    resourceType.getDoc().add(doc);
                } else if (resNode.getNodeName().equals(ElementNames.PARAM)) {
                    Param param = getParam(resNode);
                    resourceType.getParam().add(param);
                } else if (resNode.getNodeName().equals(ElementNames.mETHOD)) {
                    Method method = getMethod(resNode);
                    resourceType.getMethodOrResource().add(method);
                } else if (resNode.getNodeName().equals(ElementNames.RESOURCE)) {
                    Resource resource = getResource(resNode, null);
                    resourceType.getMethodOrResource().add(resource);
                } else {
                    resourceType.getAny().add(resNode);
                }
            }
        }
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("id")) {
                    resourceType.setId(attNode.getNodeValue());
                }
            }
        }

        return resourceType;
    }

    /**
     * Generate Resources from node element
     *
     * @param nodeElement
     *         {@link Node}
     * @return {@link Resources}
     * @throws IllegalWADLException
     */

    private static Resources getResources(Node nodeElement) throws IllegalWADLException {
        Resources resources = new Resources();
        NamedNodeMap attributes = nodeElement.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attriNode = attributes.item(i);
            if (attriNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attriNode.getNodeName().equals("base")) {
                    resources.setBase(attriNode.getNodeValue());
                } else {
                    QName name = new QName(attriNode.getNodeName(), attriNode.getNamespaceURI());
                    resources.getOtherAttributes().put(name, attriNode.getNodeValue());
                }

            }
        }

        NodeList resourcesNode = nodeElement.getChildNodes();

        for (int i = 0; i < resourcesNode.getLength(); i++) {
            Node resource = resourcesNode.item(i);

            if (resource.getNodeType() == Node.ELEMENT_NODE) {

                if (resource.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(resource);
                    resources.getDoc().add(doc);

                } else if (resource.getNodeName().equals(ElementNames.RESOURCE)) {
                    Resource res = getResource(resource, null);
                    resources.getResource().add(res);

                } else {

                    resources.getAny().add(resource);
                }
            }
        }

        return resources;
    }

    /**
     * Generate Resource object from node element
     *
     * @param resouce
     *         {@link Node}
     * @return {@link Resource}
     * @throws IllegalWADLException
     */

    private static Resource getResource(Node resouce, String basePath) throws IllegalWADLException {
        Resource resource = new Resource();

        NamedNodeMap attributes = resouce.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attriNode = attributes.item(i);
            if (attriNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attriNode.getNodeName().equals("id")) {
                    resource.setId(attriNode.getNodeValue());
                } else if (attriNode.getNodeName().equals("type")) {
                    String t = attriNode.getNodeValue();
                    String[] types = t.split(" ");
                    resource.getType().addAll(Arrays.asList(types));
                } else if (attriNode.getNodeName().equals("queryType")) {
                    resource.setQueryType(attriNode.getNodeValue());
                } else if (attriNode.getNodeName().equals("path")) {
                    resource.setPath(getFullPath(basePath, attriNode.getNodeValue()));
                } else {
                    QName name = new QName(attriNode.getNodeName(), attriNode.getNamespaceURI());
                    resource.getOtherAttributes().put(name, attriNode.getNodeValue());
                }

            }
        }

        NodeList resNodeList = resouce.getChildNodes();

        for (int i = 0; i < resNodeList.getLength(); i++) {
            Node res = resNodeList.item(i);
            if (res.getNodeType() == Node.ELEMENT_NODE) {
                if (res.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(res);
                    resource.getDoc().add(doc);
                } else if (res.getNodeName().equals(ElementNames.PARAM)) {
                    Param par = getParam(res);
                    resource.getParam().add(par);
                } else if (res.getNodeName().equals(ElementNames.mETHOD)) {
                    Method met = getMethod(res);
                    resource.getMethodOrResource().add(met);
                } else if (res.getNodeName().equals(ElementNames.RESOURCE)) {
                    Resource newResource = getResource(res, resource.getPath());
                    resource.getMethodOrResource().add(newResource);
                } else {
                    resource.getAny().add(res);
                }
            }
        }

        return resource;
    }

    /**
     * Returns full path
     *
     * @param basePath
     *         path, that already exists
     * @param localPath
     *         local path of method
     * @return
     */
    private static String getFullPath(String basePath, String localPath) {
        if (basePath == null)
            return localPath;

        if (localPath.startsWith("/")) {
            localPath = localPath.substring(1);
        }
        if (basePath.endsWith("/"))
            return basePath + localPath;
        else
            return basePath + "/" + localPath;
    }

    /**
     * Generate Method object from node element
     *
     * @param res
     *         {@link Node}
     * @return {@link Method}
     * @throws IllegalWADLException
     */

    private static Method getMethod(Node res) throws IllegalWADLException {
        Method method = new Method();
        NodeList methodList = res.getChildNodes();
        for (int i = 0; i < methodList.getLength(); i++) {
            Node metNode = methodList.item(i);
            if (metNode.getNodeType() == Node.ELEMENT_NODE) {
                if (metNode.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(metNode);
                    method.getDoc().add(doc);
                } else if (metNode.getNodeName().equals(ElementNames.REQUEST)) {
                    Request request = getRequest(metNode);
                    method.setRequest(request);
                } else if (metNode.getNodeName().equals(ElementNames.RESPONSE)) {
                    Response response = getResponse(metNode);
                    method.setResponse(response);
                } else {
                    method.getAny().add(metNode);
                }
            }
        }
        NamedNodeMap attributes = res.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("id")) {
                    method.setId(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("name")) {
                    method.setName(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("href")) {
                    method.setHref(attNode.getNodeValue());
                } else {
                    QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                    method.getOtherAttributes().put(name, attNode.getNodeValue());
                }
            }
        }
        if (method.getName() == null)
            throw new IllegalWADLException("Element method must have attribute \"name\" with value");

        return method;
    }

    /**
     * Generate Response object from node element
     *
     * @param node
     *         {@link Node}
     * @return {@link Response}
     * @throws IllegalWADLException
     */

    private static Response getResponse(Node node) throws IllegalWADLException {
        Response response = new Response();
        NodeList responseList = node.getChildNodes();
        for (int i = 0; i < responseList.getLength(); i++) {
            Node resNode = responseList.item(i);
            if (resNode.getNodeType() == Node.ELEMENT_NODE) {
                if (resNode.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(resNode);
                    response.getDoc().add(doc);
                } else if (resNode.getNodeName().equals(ElementNames.PARAM)) {
                    Param param = getParam(resNode);
                    response.getParam().add(param);
                } else if (resNode.getNodeName().equals(ElementNames.REPRESENTATION)) {
                    RepresentationType rep = getRepresentation(resNode);
                    response.getRepresentationOrFault().add(rep);
                } else {
                    response.getAny().add(resNode);
                }
            }
        }
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("status")) {
                    int stat = Integer.parseInt(attNode.getNodeValue());
                    response.setStatus(stat);
                } else {
                    QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                    response.getOtherAttributes().put(name, attNode.getNodeValue());
                }
            }
        }

        return response;
    }

    /**
     * Generate Request object from node
     *
     * @param node
     * @return {@link Request}
     * @throws IllegalWADLException
     */
    private static Request getRequest(Node node) throws IllegalWADLException {
        Request request = new Request();
        NodeList requestList = node.getChildNodes();
        for (int i = 0; i < requestList.getLength(); i++) {
            Node reqNode = requestList.item(i);
            if (reqNode.getNodeType() == Node.ELEMENT_NODE) {
                if (reqNode.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(reqNode);
                    request.getDoc().add(doc);
                } else if (reqNode.getNodeName().equals(ElementNames.PARAM)) {
                    Param param = getParam(reqNode);
                    request.getParam().add(param);
                } else if (reqNode.getNodeName().equals(ElementNames.REPRESENTATION)) {
                    RepresentationType representation = getRepresentation(reqNode);
                    request.getRepresentation().add(representation);
                } else {
                    request.getAny().add(reqNode);
                }
            }
        }

        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                request.getOtherAttributes().put(name, attNode.getNodeValue());
            }
        }

        if (request.getParam().size() != 0) {
            for (Param p : request.getParam())
                if ((!(p.getStyle() == ParamStyle.QUERY) && !(p.getStyle() == ParamStyle.HEADER))) {
                    throw new IllegalWADLException(
                            "The child element param of element request must have attribute width value \"query\" or \"header\".");
                }
        }
        return request;
    }

    /**
     * Generate RepresentationType from node
     *
     * @param {{@link
     *         Node}
     * @return {@link RepresentationType}
     * @throws IllegalWADLException
     */
    private static RepresentationType getRepresentation(Node node) throws IllegalWADLException {
        RepresentationType represent = new RepresentationType();
        NodeList representList = node.getChildNodes();
        for (int i = 0; i < representList.getLength(); i++) {
            Node repNode = representList.item(i);
            if (repNode.getNodeType() == Node.ELEMENT_NODE) {
                if (repNode.getNodeName().equals("doc")) {
                    Doc doc = getDocFromElement(repNode);
                    represent.getDoc().add(doc);
                } else if (repNode.getNodeName().equals("param")) {
                    Param param = getParam(repNode);
                    represent.getParam().add(param);
                } else {
                    represent.getAny().add(repNode);
                }
            }
        }
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("id")) {
                    represent.setId(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("element")) {
                    QName name = new QName(attNode.getNodeValue(), attNode.getNamespaceURI());
                    represent.setElement(name);
                } else if (attNode.getNodeName().equals("mediaType")) {
                    represent.setMediaType(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("href")) {
                    represent.setHref(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("profile")) {
                    //TODO Maybe need parse profiles URL
                    represent.getProfile().add(attNode.getNodeValue());
                } else {
                    QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                    represent.getOtherAttributes().put(name, attNode.getNodeValue());
                }
            }
        }

        return represent;
    }

    /**
     * Generate Param object from node
     *
     * @param {@link
     *         Node}
     * @return {@link Param}
     * @throws IllegalWADLException
     */
    private static Param getParam(Node node) throws IllegalWADLException {
        Param param = new Param();
        NodeList paramNodes = node.getChildNodes();
        for (int i = 0; i < paramNodes.getLength(); i++) {
            Node paramNode = paramNodes.item(i);
            if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                if (paramNode.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(paramNode);
                    param.getDoc().add(doc);
                } else if (paramNode.getNodeName().equals(ElementNames.OPTION)) {
                    Option opt = getOption(paramNode);
                    param.getOption().add(opt);
                } else if (paramNode.getNodeName().equals(ElementNames.LINK)) {
                    Link link = getLink(paramNode);
                    param.setLink(link);
                } else {
                    param.getAny().add(paramNode);
                }
            }
        }
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("href")) {
                    param.setHref(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("name")) {
                    param.setName(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("style")) {
                    try {
                        ParamStyle paramStyle = ParamStyle.fromValue(attNode.getNodeValue());
                        param.setStyle(paramStyle);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalWADLException("Invalid value of attribute style, in element param.");
                    }
                } else if (attNode.getNodeName().equals("id")) {
                    param.setId(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("type")) {
                    QName type = new QName(attNode.getNodeValue(), attNode.getNamespaceURI());
                    param.setType(type);
                } else if (attNode.getNodeName().equals("default")) {
                    param.setDefault(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("required")) {

                    Boolean b = Boolean.parseBoolean(attNode.getNodeValue());
                    param.setRequired(b);
                } else if (attNode.getNodeName().equals("repeating")) {
                    Boolean b = Boolean.parseBoolean(attNode.getNodeValue());
                    param.setRepeating(b);
                } else if (attNode.getNodeName().equals("fixed")) {
                    param.setFixed(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("path")) {
                    param.setPath(attNode.getNodeValue());
                } else {
                    QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                    param.getOtherAttributes().put(name, attNode.getNodeValue());
                }
            }
        }
        if (param.getStyle() == null)
            throw new IllegalWADLException("The element param must have attribute style.");

        return param;
    }

    /**
     * Generate Link object from node
     *
     * @param {@link
     *         Node}
     * @return {@link Link}
     * @throws IllegalWADLException
     */
    private static Link getLink(Node node) throws IllegalWADLException {
        Link link = new Link();
        NodeList linkList = node.getChildNodes();
        for (int i = 0; i < linkList.getLength(); i++) {
            Node linkNode = linkList.item(i);
            if (linkNode.getNodeType() == Node.ELEMENT_NODE) {
                if (linkNode.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(linkNode);
                    link.getDoc().add(doc);
                } else {
                    link.getAny().add(linkNode);
                }
            }
        }
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("resource_type")) {
                    link.setResourceType(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("rel")) {
                    link.setRel(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("rev")) {
                    link.setRev(attNode.getNodeValue());
                } else {
                    QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                    link.getOtherAttributes().put(name, attNode.getNodeValue());
                }
            }
        }

        return link;
    }

    /**
     * Generate Option object from node
     *
     * @param {@link
     *         Node}
     * @return {@link Option}
     * @throws IllegalWADLException
     */
    private static Option getOption(Node node) throws IllegalWADLException {
        Option option = new Option();
        NodeList optionsList = node.getChildNodes();
        for (int i = 0; i < optionsList.getLength(); i++) {
            Node optNode = optionsList.item(i);
            if (optNode.getNodeType() == Node.ELEMENT_NODE) {
                if (optNode.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(optNode);
                    option.getDoc().add(doc);
                } else {
                    option.getAny().add(optNode);
                }

            }
        }
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("value")) {
                    option.setValue(attNode.getNodeValue());
                } else if (attNode.getNodeName().equals("mediaType")) {
                    option.setMediaType(attNode.getNodeValue());
                } else {
                    QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                    option.getOtherAttributes().put(name, attNode.getNodeValue());
                }
            }
        }
        if (option.getValue() == null)
            throw new IllegalWADLException("Element option has no attribute \"value\"");
        return option;

    }

    /**
     * Generate Grammars object from node
     *
     * @param {@link
     *         Node}
     * @return {@link Grammars}
     * @throws IllegalWADLException
     */
    private static Grammars getGrammars(Node node) throws IllegalWADLException {
        Grammars grammars = new Grammars();
        if (node.hasAttributes())
            throw new IllegalWADLException("Illegal attribute - " + node.getAttributes().item(0).getNodeName()
                                           + ", element grammars must not hew any attribute.");

        NodeList grammar = node.getChildNodes();
        for (int i = 0; i < grammar.getLength(); i++) {
            Node gram = grammar.item(i);
            if (gram.getNodeType() == Node.ELEMENT_NODE) {
                if (gram.getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(gram);
                    grammars.getDoc().add(doc);
                } else if (gram.getNodeName().equals(ElementNames.INCLUDE)) {
                    Include inc = getInclude(gram);
                    grammars.getInclude().add(inc);
                } else {
                    grammars.getAny().add(gram);
                }
            }
        }

        return grammars;
    }

    /**
     * Generate Include object from node
     *
     * @param {@link
     *         Node}
     * @return {@link Include}
     * @throws IllegalWADLException
     */

    private static Include getInclude(Node node) throws IllegalWADLException {
        Include inc = new Include();
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attNode = attributes.item(i);
            if (attNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                if (attNode.getNodeName().equals("href")) {
                    inc.setHref(attNode.getNodeValue());
                } else {
                    QName name = new QName(attNode.getNodeName(), attNode.getNamespaceURI());
                    inc.getOtherAttributes().put(name, attNode.getNodeValue());
                }
            }
        }

        if (node.hasChildNodes()) {
            NodeList nodelist = node.getChildNodes();
            for (int i = 0; i < nodelist.getLength(); i++) {
                if (nodelist.item(i).getNodeName().equals(ElementNames.DOC)) {
                    Doc doc = getDocFromElement(node.getFirstChild());
                    inc.getDoc().add(doc);
                } else
                    throw new IllegalWADLException("Illigeal node, found " + node.getFirstChild().getNodeName()
                                                   + " expected doc");

            }

        }
        return inc;
    }

}
