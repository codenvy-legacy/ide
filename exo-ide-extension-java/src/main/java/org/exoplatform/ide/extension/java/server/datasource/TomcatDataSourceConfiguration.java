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

import org.exoplatform.ide.extension.java.shared.DataSourceOption;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tomcat specific JNDI DataSource Configuration.
 *
 * @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a>
 */
public class TomcatDataSourceConfiguration extends DataSourceConfiguration {
    private static final Map<String, DataSourceOption> all = new LinkedHashMap<String, DataSourceOption>();

    static {
        all.put("url", new DataSourceOption("url", null, true, "The database connection URL"));
        all.put("username", new DataSourceOption("username", null, true, "The database connection username"));
        all.put("password", new DataSourceOption("password", null, true, "The database connection password"));
        all.put("driverClassName", new DataSourceOption("driverClassName", null, true,
                                                        "The fully qualified Java class name of the JDBC driver"));
        all.put("connectionProperties", new DataSourceOption("connectionProperties", null, false,
                                                             "The connection properties for JDBC driver in format: name1=value1;name2=value2;..."));
        //
        all.put("defaultAutoCommit", new DataSourceOption("defaultAutoCommit", "true", false,
                                                          "The default auto-commit state of JDBC connections."));
        all.put("defaultReadOnly", new DataSourceOption("defaultReadOnly", null, false,
                                                        "The default read-only state of JDBC connections. Default value depends to driver " +
                                                        "implementation"));
        all.put("defaultTransactionIsolation", new DataSourceOption("defaultTransactionIsolation", null, false,
                                                                    "The default TransactionIsolation state of JDBC connections." +
                                                                    "For details see: docs for java.sql.Connection. Default value depends " +
                                                                    "to driver implementation"));
        all.put("defaultCatalog", new DataSourceOption("defaultCatalog", null, false,
                                                       "The default catalog name for JDBC connections"));
        all.put("initialSize", new DataSourceOption("initialSize", "0", false,
                                                    "The initial number of connections that are created when the pool is started"));
        all.put("maxActive", new DataSourceOption("maxActive", "30", false,
                                                  "The maximum number of connections that can be allocated from this pool at the same time, " +
                                                  "or negative for no limit"));
        all.put("maxIdle", new DataSourceOption("maxIdle", "10", false,
                                                "The maximum number of connections that can remain idle in the pool, " +
                                                "without extra ones being released, or negative for no limit"));
        all.put("minIdle", new DataSourceOption("minIdle", "0", false,
                                                "The minimum number of connections that can remain idle in the pool, without extra ones " +
                                                "being created"));
        all.put("maxWait", new DataSourceOption("maxWait", "10000", false,
                                                "The maximum number of milliseconds that the pool will wait for a connection to be " +
                                                "returned before throwing an exception, or -1 to wait indefinitely"));
        all.put("validationQuery", new DataSourceOption("validationQuery", null, false,
                                                        "The SQL query that will be used to validate connections from this pool"));
        all.put("poolPreparedStatements", new DataSourceOption("poolPreparedStatements", "false", false,
                                                               "Enable prepared statement pooling for this pool"));
        all.put("maxOpenPreparedStatements", new DataSourceOption("maxOpenPreparedStatements", "0", false,
                                                                  "The maximum number of open statements that can be allocated from the " +
                                                                  "statement pool at the same time, or zero for no limit"));
    }

    public TomcatDataSourceConfiguration(VirtualFileSystem vfs, String projectId) {
        super(vfs, projectId);
    }

    @Override
    public List<DataSourceOptions> getAllDataSources() throws VirtualFileSystemException, IOException {
        final Folder project = (Folder)vfsAdapter.getItem(projectId, false);
        final Folder webApp = (Folder)vfsAdapter.getItemByPath(project.createPath("src/main/webapp"), false);

        Folder metaInf;
        try {
            metaInf = (Folder)vfsAdapter.getItemByPath(webApp.createPath("META-INF"), false);
        } catch (ItemNotFoundException e) {
            return java.util.Collections.emptyList(); // no META-INF folder then no JNDI configurations
        }

        final String path = metaInf.createPath("context.xml");
        InputStream stream;
        try {
            stream = vfsAdapter.getContentByPath(path).getStream();
        } catch (ItemNotFoundException e) {
            return java.util.Collections.emptyList(); // no context.xml file then no JNDI configurations
        }

        Document doc;
        try {
            doc = loadDocument(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException ignore) {
            }
        }

        final Node contextNode = doc.getDocumentElement();
        final Node first = getChild(contextNode, "Resource");
        if (first == null) {
            return java.util.Collections.emptyList(); // There is no any Resources
        }
        List<DataSourceOptions> result = new ArrayList<DataSourceOptions>();
        for (Node current = first; current != null; current = getNext(current, "Resource")) {
            final Map<String, String> attributes = getAttributes(current);
            DataSourceOptions options = new DataSourceOptions(attributes.get("name"), new ArrayList<DataSourceOption>());
            for (Map.Entry<String, String> e : attributes.entrySet()) {
                final String name = e.getKey();
                // Ignore 'name', 'auth' and 'type' attributes:
                // 1. user not able to change 'auth' and 'type'.
                // 2. 'name' provided in separate field.
                if (!name.equals("name") && !name.equals("auth") && !name.equals("type")) {
                    final DataSourceOption known = all.get(name);
                    DataSourceOption myOption;
                    if (known != null) {
                        // If options is 'known' then use 'template' to provide additional info about options, such as 'description' and 'required'.
                        myOption = new DataSourceOption(known);
                        myOption.setValue(e.getValue());
                    } else {
                        myOption = new DataSourceOption(name, e.getValue(), false, null);
                    }
                    options.getOptions().add(myOption);
                }
            }
            result.add(options);
        }
        return result;
    }

    @Override
    public List<DataSourceOption> getAvailableDataSourceOptions() {
        List<DataSourceOption> options = new ArrayList<DataSourceOption>(all.size());
        for (DataSourceOption option : all.values()) {
            DataSourceOption copy = new DataSourceOption(option);
            if ("driverClassName".equals(copy.getName())) {
                copy.setValue(driverClassName);
            }
            options.add(copy);
        }
        return options;
    }

    private static final byte[] EMPTY_CONTEXT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Context></Context>".getBytes();

    @Override
    protected void configureJndiResource(List<DataSourceOptions> dataSourceOptions) throws VirtualFileSystemException, IOException {
        // Create {my_app}/META-INF/context.xml if it does not exist yet.
        // Then add configuration for JNDI resource.
        final Folder project = (Folder)vfsAdapter.getItem(projectId, false);
        final Folder webApp = (Folder)vfsAdapter.getItemByPath(project.createPath("src/main/webapp"), false);

        Folder metaInf;
        try {
            metaInf = (Folder)vfsAdapter.getItemByPath(webApp.createPath("META-INF"), false);
        } catch (ItemNotFoundException e) {
            metaInf = vfsAdapter.createFolder(webApp.getId(), "META-INF");
        }

        final String path = metaInf.createPath("context.xml");
        InputStream stream;
        try {
            stream = vfsAdapter.getContentByPath(path).getStream();
        } catch (ItemNotFoundException e) {
            vfsAdapter.createFile(metaInf.getId(), "context.xml", "application/xml", new ByteArrayInputStream(EMPTY_CONTEXT_XML));
            stream = new ByteArrayInputStream(EMPTY_CONTEXT_XML);
        }

        Document doc;
        try {
            doc = loadDocument(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException ignore) {
            }
        }

        final Node contextNode = doc.getDocumentElement();
        Set<String> toRetain = new HashSet<String>();
        for (DataSourceOptions cfg : dataSourceOptions) {
            final String name = cfg.getName();
            Node resourceNode = findNodeWithAttribute(contextNode, "Resource", "name", name);
            if (resourceNode == null) {
                resourceNode = doc.createElement("Resource");
                contextNode.appendChild(resourceNode);
            }

            for (DataSourceOption option : cfg.getOptions()) {
                if (option.getValue() != null) {
                    setAttribute(resourceNode, option.getName(), option.getValue());
                }
            }
            setAttribute(resourceNode, "name", name);
            setAttribute(resourceNode, "auth", "Container");
            setAttribute(resourceNode, "type", "javax.sql.DataSource");

            toRetain.add(name);
        }
        retainNodesWithAttribute(contextNode, "Resource", "name", toRetain);
        saveDocument(doc, path);
    }
}
