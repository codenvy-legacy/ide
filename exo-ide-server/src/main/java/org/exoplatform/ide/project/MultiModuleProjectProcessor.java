/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
