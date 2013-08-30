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
package org.exoplatform.ide.project;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.shared.Item;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import javax.ws.rs.core.MediaType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class MultiModuleProjectProcessor {

    private VirtualFileSystem vfs;

    public MultiModuleProjectProcessor(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    private Node getNode(Node parent, String nodeName) {
        NodeList nodes = parent.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (nodeName.equals(node.getNodeName())) {
                return node;
            }
        }

        return null;
    }

    public void addModule(Item pomItem, String moduleName) throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        InputStream inputStream = vfs.getContent(pomItem.getId()).getStream();
        Document doc = builder.parse(inputStream);
        inputStream.close();

        Node projectNode = getNode(doc, "project");
        Node modulesNode = getNode(projectNode, "modules");
        if (modulesNode == null) {
            modulesNode = doc.createElement("modules");
            projectNode.appendChild(modulesNode);
        }

        Node moduleNode = doc.createElement("module");
        moduleNode.setTextContent(moduleName);
        modulesNode.appendChild(moduleNode);

        DOMImplementation impl = doc.getImplementation();
        DOMImplementationLS implLS = (DOMImplementationLS)impl.getFeature("LS", "3.0");
        LSSerializer lsSerializer = implLS.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("format-pretty-print", true);

        LSOutput lsOutput = implLS.createLSOutput();
        lsOutput.setEncoding("UTF-8");
        Writer stringWriter = new StringWriter();
        lsOutput.setCharacterStream(stringWriter);
        lsSerializer.write(doc, lsOutput);

        String outXml = stringWriter.toString();
        vfs.updateContent(pomItem.getId(), MediaType.TEXT_XML_TYPE, new ByteArrayInputStream(outXml.getBytes()), null);
    }

}
