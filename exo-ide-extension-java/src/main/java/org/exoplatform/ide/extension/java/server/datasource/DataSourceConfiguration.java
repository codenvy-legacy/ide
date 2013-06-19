/*
 * Copyright (C) 2013 eXo Platform SAS.
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

package org.exoplatform.ide.extension.java.server.datasource;

import com.codenvy.commons.lang.NameGenerator;
import org.exoplatform.ide.extension.java.shared.DataSourceOption;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;
import org.exoplatform.ide.extension.java.shared.Dependency;
import org.exoplatform.ide.extension.java.shared.MavenDependency;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemAdapter;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Base JNDI DataSource configuration.
 *
 * @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a>
 */
public abstract class DataSourceConfiguration {
    protected final VirtualFileSystemAdapter         vfsAdapter;
    protected final String                           projectId;
    protected final DOMImplementationLS              domLS;
    private final   Map<String, Map<String, String>> driverClassNameGuesser;

    /** JDBC driver class name. This property initialized in method {@link #addProjectDependency(org.exoplatform.ide.extension.java.shared.Dependency)}. */
    protected String driverClassName;

    public DataSourceConfiguration(VirtualFileSystem vfs, String projectId) {
        this.vfsAdapter = new VirtualFileSystemAdapter(vfs);
        this.projectId = projectId;
        final DOMImplementationRegistry reg;
        try {
            reg = DOMImplementationRegistry.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        domLS = (DOMImplementationLS)reg.getDOMImplementation("LS");

        driverClassNameGuesser = new HashMap<String, Map<String, String>>();
        HashMap<String, String> mavenMapping = new HashMap<String, String>();
        mavenMapping.put("postgresql:postgresql", "org.postgresql.Driver");
        mavenMapping.put("mysql:mysql-connector-java", "com.mysql.jdbc.Driver");
        mavenMapping.put("org.hsqldb:hsqldb", "org.hsqldb.jdbcDriver");
        mavenMapping.put("org.apache.derby:derbyclient", "org.apache.derby.jdbc.EmbeddedDriver"); // JDBC driver
        mavenMapping.put("org.apache.derby:derby", "org.apache.derby.jdbc.EmbeddedDriver"); // database engine + JDBC driver
        mavenMapping.put("com.h2database:h2", "org.h2.Driver");
        driverClassNameGuesser.put("maven", mavenMapping);
    }

    /**
     * Add dependency to the project. Typically expect dependency (e.g. maven artifact) to jar file which contains JDBC driver.
     *
     * @param dependency
     *         dependency
     */
    public void addProjectDependency(Dependency dependency) throws VirtualFileSystemException, IOException {
        // For now support maven only. In future can be more flexible to support other build systems.
        if (dependency instanceof MavenDependency) {
            addMavenProjectDependency((MavenDependency)dependency);
            driverClassName = guessDriverClassName(dependency);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + dependency);
        }
    }

    /**
     * Try to resolve JDBC driver name from project dependency.
     *
     * @param dependency
     *         dependency
     * @return JDBC driver name or <code>null</code> if cannot resolve name from dependency
     */
    protected String guessDriverClassName(Dependency dependency) {
        // For now support maven only. In future can be more flexible to support other build systems.
        if (dependency instanceof MavenDependency) {
            final MavenDependency mavenDependency = (MavenDependency)dependency;
            return driverClassNameGuesser.get("maven").get(mavenDependency.getGroupId() + ':' + mavenDependency.getArtifactId());
        }
        return null;
    }

    private void addMavenProjectDependency(MavenDependency mavenDependency) throws VirtualFileSystemException, IOException {
        final Folder project = (Folder)vfsAdapter.getItem(projectId, false);
        final String path = project.createPath("pom.xml");
        final InputStream stream = vfsAdapter.getContentByPath(path).getStream();
        Document doc;
        try {
            doc = loadDocument(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException ignore) {
            }
        }
        final Node projectNode = doc.getDocumentElement();
        final Node dependencies = getChild(projectNode, "dependencies");
        final Element dependencyNode = doc.createElement("dependency");
        dependencies.appendChild(dependencyNode);
        final Element groupIdNode = doc.createElement("groupId");
        setText(groupIdNode, mavenDependency.getGroupId());
        dependencyNode.appendChild(groupIdNode);
        final Element artifactIdNode = doc.createElement("artifactId");
        setText(artifactIdNode, mavenDependency.getArtifactId());
        dependencyNode.appendChild(artifactIdNode);
        final Element versionNode = doc.createElement("version");
        setText(versionNode, mavenDependency.getVersion());
        dependencyNode.appendChild(versionNode);

        saveDocument(doc, path);
    }

    /**
     * Create JNDI DataSource configuration.
     *
     * @param dataSourceOptions
     *         configuration options. Typically caller should do next:
     *         <ul>
     *         <li>Get list of available options with method {@link #getAvailableDataSourceOptions()}</li>
     *         <li>Set the desired value ​​for each parameter. Some parameters may be optional or already have preset values. Use method
     *         {@link org.exoplatform.ide.extension.java.shared.DataSourceOption#isRequired()} to check is parameter required or
     *         optional</li>
     *         <li>Use list of options to call this method</li>
     *         </ul>
     */
    public void configureDataSources(List<DataSourceOptions> dataSourceOptions) throws VirtualFileSystemException, IOException {
        List<String> names = new ArrayList<String>(dataSourceOptions.size());
        for (DataSourceOptions cfg : dataSourceOptions) {
            names.add(cfg.getName());
        }
        configureWebXml(names);
        configureJndiResource(dataSourceOptions);
    }

    public abstract List<DataSourceOptions> getAllDataSources() throws VirtualFileSystemException, IOException;

    /**
     * Add 'resource-ref' configuration in web.xml.
     *
     * @param resRefNames
     *         resource reference (JNDI) names
     */
    protected void configureWebXml(List<String> resRefNames) throws VirtualFileSystemException, IOException {
        final Folder project = (Folder)vfsAdapter.getItem(projectId, false);
        final String path = project.createPath("src/main/webapp/WEB-INF/web.xml");
        final InputStream stream = vfsAdapter.getContentByPath(path).getStream();

        Document doc;
        try {
            doc = loadDocument(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException ignore) {
            }
        }
        boolean needUpdate = false;
        final Node webAppNode = doc.getDocumentElement();
        for (String resRefName : resRefNames) {
            Node resourceRefNode = findNodeWithTextChild(webAppNode, "resource-ref", "res-ref-name", resRefName);
            if (resourceRefNode == null) {
                resourceRefNode = doc.createElement("resource-ref");
                webAppNode.appendChild(resourceRefNode);
                final Element resRefNameNode = doc.createElement("res-ref-name");
                setText(resRefNameNode, resRefName);
                resourceRefNode.appendChild(resRefNameNode);
                final Element resTypeNode = doc.createElement("res-type");
                setText(resTypeNode, "javax.sql.DataSource");
                resourceRefNode.appendChild(resTypeNode);
                final Element resAuthNode = doc.createElement("res-auth");
                setText(resAuthNode, "Container");
                resourceRefNode.appendChild(resAuthNode);
                needUpdate = true;
            }
        }
        needUpdate |= retainNodesWithTextChild(webAppNode, "resource-ref", "res-ref-name", resRefNames);
        if (needUpdate) {
            saveDocument(doc, path);
        }
    }

    /** Create new JNDI configuration */
    public DataSourceOptions newConfiguration() {
        return new DataSourceOptions(NameGenerator.generate("jdbc/ds_", 6), getAvailableDataSourceOptions());
    }

    /**
     * Configure JNDI resources. Implementation depends to application server or (and) DataSource factory.
     *
     * @param dataSourceOptions
     *         configuration options, see {@link #configureDataSources(java.util.List)}
     */
    protected abstract void configureJndiResource(List<DataSourceOptions> dataSourceOptions) throws VirtualFileSystemException, IOException;

    /** Get list of available configuration options. Implementation depends to application server or (and) DataSource factory. */
    protected abstract List<DataSourceOption> getAvailableDataSourceOptions();

    // Helper methods for work with DOM

    protected Document loadDocument(InputStream stream) {
        final LSParser parser = domLS.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
        final LSInput input = domLS.createLSInput();
        input.setEncoding("UTF-8");
        input.setByteStream(stream);
        return parser.parse(input);
    }

    protected Node getChild(Node parent, String name) {
        Node start = parent.getFirstChild();
        if (start != null) {
            for (Node current = start; current != null; current = current.getNextSibling()) {
                if (current.getNodeType() == Node.ELEMENT_NODE && name.equals(current.getNodeName())) {
                    return current;
                }
            }
        }
        return null;
    }

    protected Node getNext(Node node, String name) {
        Node start = node.getNextSibling();
        if (start != null) {
            for (Node current = start; current != null; current = current.getNextSibling()) {
                if (current.getNodeType() == Node.ELEMENT_NODE && name.equals(current.getNodeName())) {
                    return current;
                }
            }
        }
        return null;
    }

    protected Node findNodeWithAttribute(Node parent, String name, String attributeName, String attributeValue) {
        Node start = getChild(parent, name);
        if (start != null) {
            for (Node current = start; current != null; current = getNext(current, name)) {
                if (attributeValue.equals(getAttribute(current, attributeName))) {
                    return current;
                }
            }
        }
        return null;
    }

    protected Node findNodeWithTextChild(Node parent, String name, String childName, String text) {
        Node node = getChild(parent, name);
        if (node != null) {
            while (node != null) {
                Node start = getChild(node, childName);
                if (start != null) {
                    for (Node current = start; current != null; current = getNext(current, childName)) {
                        if (text.equals(getText(current))) {
                            return node;
                        }
                    }
                }
                node = getNext(node, name);
            }
        }
        return null;
    }

    protected boolean retainNodesWithAttribute(Node parent, String name, String attributeName, Collection<String> toRetain) {
        Node start = getChild(parent, name);
        if (start != null) {
            List<Node> toRemove = new ArrayList<Node>();
            for (Node current = start; current != null; current = getNext(current, name)) {
                if (!toRetain.contains(getAttribute(current, attributeName))) {
                    toRemove.add(current);
                }
            }
            for (Node remove : toRemove) {
                parent.removeChild(remove);
            }
            return !toRemove.isEmpty();
        }
        return false;
    }

    protected boolean retainNodesWithTextChild(Node parent, String name, String childName, Collection<String> toRetain) {
        Node node = getChild(parent, name);
        if (node != null) {
            List<Node> toRemove = new ArrayList<Node>();
            while (node != null) {
                Node start = getChild(node, childName);
                if (start != null) {
                    for (Node current = start; current != null; current = getNext(current, childName)) {
                        if (!toRetain.contains(getText(current))) {
                            toRemove.add(node);
                        }
                    }
                }
                node = getNext(node, name);
            }
            for (Node remove : toRemove) {
                parent.removeChild(remove);
            }
            return !toRemove.isEmpty();
        }
        return false;
    }

    protected String getAttribute(Node node, String attributeName) {
        NamedNodeMap nodeAttributes = node.getAttributes();
        if (nodeAttributes != null) {
            Node attribute = nodeAttributes.getNamedItem(attributeName);
            if (attribute != null) {
                return attribute.getNodeValue();
            }
        }
        return null;
    }

    protected Map<String, String> getAttributes(Node node) {
        NamedNodeMap nodeAttributes = node.getAttributes();
        if (nodeAttributes != null) {
            final int size = nodeAttributes.getLength();
            Map<String, String> attributes = new LinkedHashMap<String, String>(size);
            for (int i = 0; i < size; i++) {
                Node attribute = nodeAttributes.item(i);
                attributes.put(attribute.getNodeName(), attribute.getNodeValue());
            }
            return attributes;
        }
        return java.util.Collections.emptyMap();
    }

    protected void setAttribute(Node node, String name, String value) {
        NamedNodeMap nodeAttributes = node.getAttributes();
        Node attribute = node.getOwnerDocument().createAttribute(name);
        attribute.setNodeValue(value);
        nodeAttributes.setNamedItem(attribute);
    }

    protected void setText(Node node, String value) {
        Node text = node.getFirstChild();
        while (text != null && Node.TEXT_NODE != text.getNodeType()) {
            text = text.getNextSibling();
        }
        if (text == null) {
            text = node.getOwnerDocument().createTextNode(value);
            node.appendChild(text);
        } else {
            text.setNodeValue(value);
        }
    }

    protected String getText(Node node) {
        Node text = node.getFirstChild();
        while (text != null && Node.TEXT_NODE != text.getNodeType()) {
            text = text.getNextSibling();
        }
        return text != null ? text.getTextContent() : null;
    }

    private static final byte[] XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes();

    protected void saveDocument(Document doc, String path) throws IOException, VirtualFileSystemException {
        final LSSerializer writer = domLS.createLSSerializer();
        final LSOutput output = domLS.createLSOutput();
        writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
        writer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);
        output.setEncoding("UTF-8");
        // If XML file contains block of comments before root element (like license information) after serialization it added to the end of
        // file. It something specific to implementation of com.sun.org.apache.xml.internal.serialize.XMLSerializer.
        // Serialize document in 3 steps:
        // 1. Write XML declaration
        // 2. Write all comments blocks before root element in the same order as in original document.
        // 3. Write root element
        final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        bOut.write(XML_DECLARATION); // (1)
        bOut.write('\n');
        output.setByteStream(bOut);
        final Element root = doc.getDocumentElement();
        // (2) >>>
        Node n = root.getPreviousSibling();
        if (n != null) {
            LinkedList<Node> beforeRoot = new LinkedList<Node>();
            // Be ready to have more then one comment block before root element.
            while (n != null) {
                beforeRoot.push(n);
                n = n.getPreviousSibling();
            }
            while (!beforeRoot.isEmpty()) {
                writer.write(beforeRoot.pop(), output);
            }
        }
        // <<< (2)
        writer.write(root, output); // (3)

        vfsAdapter.updateContentByPath(path, "application/xml", new ByteArrayInputStream(bOut.toByteArray()));
    }
}
