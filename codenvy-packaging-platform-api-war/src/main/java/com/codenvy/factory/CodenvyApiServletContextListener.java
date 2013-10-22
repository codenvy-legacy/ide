/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.factory;


import com.codenvy.api.factory.AdvancedFactoryUrlValidator;
import com.codenvy.api.factory.FactoryStore;
import com.codenvy.factory.storage.mongo.MongoDBFactoryStore;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Factory Servlet Context Listener class.
 */
public class CodenvyApiServletContextListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(CodenvyApiServletContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sctx = sce.getServletContext();
        sctx.setAttribute(FactoryStore.class.getName(), getFactoryStore());
        sctx.setAttribute(AdvancedFactoryUrlValidator.class.getName(), new AdvancedFactoryUrlFormat());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext sctx = sce.getServletContext();
        sctx.removeAttribute(FactoryStore.class.getName());
        sctx.removeAttribute(AdvancedFactoryUrlValidator.class.getName());
    }


    private FactoryStore getFactoryStore() {

        // For IDE Tomcat which has no properties
        if (System.getProperty("cloud.admin.configuration.dir") == null)
            return new InMemoryFactoryStore();

        File dbSettings =
                new File(new File(System.getProperty("cloud.admin.configuration.dir")), "factory-storage.properties");
        if (!dbSettings.exists() || dbSettings.isDirectory()) {
            return new InMemoryFactoryStore();
        } else {
            Configuration configuration;
            try {
                configuration = new PropertiesConfiguration(dbSettings);
                String host = configuration.getString("host");
                int port = configuration.getInt("port");
                String dbName = configuration.getString("database");
                String collectionName = configuration.getString("collection");
                String username = configuration.getString("username");
                String password = configuration.getString("password");
                if (host != null && !host.isEmpty() && port != 0 && dbName != null && !dbName.isEmpty() &&
                    collectionName != null && !collectionName.isEmpty())
                    return new MongoDBFactoryStore(host, port, dbName, collectionName, username, password);
                else {
                    LOG.warn("Error while parsing MongoDB configuration file. Default settings will be used.");
                    return new MongoDBFactoryStore();
                }
            } catch (ConfigurationException e) {
                LOG.warn("Error while reading MongoDB configuration file. Default settings will be used.");
                return new MongoDBFactoryStore();
            }
        }
    }
}
