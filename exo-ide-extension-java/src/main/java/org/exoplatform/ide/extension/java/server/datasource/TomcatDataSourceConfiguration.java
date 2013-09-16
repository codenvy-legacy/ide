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
import java.util.Comparator;
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
    private static final Map<String, KnownDataSourceOption> all = new LinkedHashMap<String, KnownDataSourceOption>();

    /* We want to have to have the same order of datasource attributes as in template map.
    * Unfortunately it is not possible to get attributes of existed configuration in any particular order.
    * Use method order() to manage ordering of datasource's options when read existed configuration.
    * See method getAllDataSources() */
    private static class KnownDataSourceOption extends DataSourceOption {
        private final int order;

        private KnownDataSourceOption(String name, String value, boolean required, String description, int order) {
            super(name, value, required, description);
            this.order = order;
        }

        private KnownDataSourceOption(DataSourceOption other, int order) {
            super(other);
            this.order = order;
        }
    }

    private static final Comparator<DataSourceOption> DATA_SOURCE_OPTION_COMPARATOR = new Comparator<DataSourceOption>() {
        @Override
        public int compare(DataSourceOption o1, DataSourceOption o2) {
            if (o1 instanceof KnownDataSourceOption && o2 instanceof KnownDataSourceOption) {
                return ((KnownDataSourceOption)o1).order - ((KnownDataSourceOption)o2).order;
            } else if (o1 instanceof KnownDataSourceOption) {
                return 1;
            } else if (o2 instanceof KnownDataSourceOption) {
                return -1;
            } else if (o1.isRequired() && o2.isRequired()) {
                return 0;
            } else if (o1.isRequired()) {
                return 1;
            } else if (o2.isRequired()) {
                return -1;
            }
            return 0;
        }
    };

    static {
        all.put("url", new KnownDataSourceOption("url", null, true, "The database connection URL", 0));
        all.put("username", new KnownDataSourceOption("username", null, true, "The database connection username", 1));
        all.put("password", new KnownDataSourceOption("password", null, true, "The database connection password", 2));
        all.put("driverClassName", new KnownDataSourceOption("driverClassName", null, true,
                                                             "The fully qualified Java class name of the JDBC driver", 3));
        all.put("connectionProperties", new KnownDataSourceOption("connectionProperties", null, false,
                                                                  "The connection properties for JDBC driver in format: name1=value1;name2=value2;...",
                                                                  4));
        //
        all.put("defaultAutoCommit", new KnownDataSourceOption("defaultAutoCommit", "true", false,
                                                               "The default auto-commit state of JDBC connections.", 5));
        all.put("defaultReadOnly", new KnownDataSourceOption("defaultReadOnly", null, false,
                                                             "The default read-only state of JDBC connections. Default value depends to driver implementation",
                                                             6));
        all.put("defaultTransactionIsolation", new KnownDataSourceOption("defaultTransactionIsolation", null, false,
                                                                         "The default TransactionIsolation state of JDBC connections. For details see: docs for java.sql.Connection. Default value depends to driver implementation",
                                                                         7));
        all.put("defaultCatalog", new KnownDataSourceOption("defaultCatalog", null, false,
                                                            "The default catalog name for JDBC connections", 8));
        all.put("initialSize", new KnownDataSourceOption("initialSize", "0", false,
                                                         "The initial number of connections that are created when the pool is started",
                                                         9));
        all.put("maxActive", new KnownDataSourceOption("maxActive", "30", false,
                                                       "The maximum number of connections that can be allocated from this pool at the same time, or negative for no limit",
                                                       10));
        all.put("maxIdle", new KnownDataSourceOption("maxIdle", "10", false,
                                                     "The maximum number of connections that can remain idle in the pool, without extra ones being released, or negative for no limit",
                                                     11));
        all.put("minIdle", new KnownDataSourceOption("minIdle", "0", false,
                                                     "The minimum number of connections that can remain idle in the pool, without extra ones being created",
                                                     12));
        all.put("maxWait", new KnownDataSourceOption("maxWait", "10000", false,
                                                     "The maximum number of milliseconds that the pool will wait for a connection to be returned before throwing an exception, or -1 to wait indefinitely",
                                                     13));
        all.put("validationQuery", new KnownDataSourceOption("validationQuery", null, false,
                                                             "The SQL query that will be used to validate connections from this pool",
                                                             14));
        all.put("poolPreparedStatements", new KnownDataSourceOption("poolPreparedStatements", "false", false,
                                                                    "Enable prepared statement pooling for this pool",
                                                                    15));
        all.put("maxOpenPreparedStatements", new KnownDataSourceOption("maxOpenPreparedStatements", "0", false,
                                                                       "The maximum number of open statements that can be allocated from the statement pool at the same time, or zero for no limit",
                                                                       16));
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
                    final KnownDataSourceOption known = all.get(name);
                    if (known != null) {
                        // If options is 'known' then use 'template' to provide additional info about options, such as 'description' and 'required'.
                        KnownDataSourceOption myOption = new KnownDataSourceOption(known, known.order);
                        myOption.setValue(e.getValue());
                        options.getOptions().add(myOption);
                    } else {
                        DataSourceOption myOption = new DataSourceOption(name, e.getValue(), false, null);
                        options.getOptions().add(myOption);
                    }
                }
            }
            java.util.Collections.sort(options.getOptions(), DATA_SOURCE_OPTION_COMPARATOR);
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
