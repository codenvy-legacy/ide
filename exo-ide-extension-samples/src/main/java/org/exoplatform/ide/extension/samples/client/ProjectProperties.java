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
package org.exoplatform.ide.extension.samples.client;

import org.exoplatform.ide.vfs.client.model.FolderModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Storage for project properties. TODO: Remove this class, when project notion on client will be created.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectProperties.java Sep 8, 2011 6:17:11 PM vereshchaka $
 */
public class ProjectProperties {
    public interface ProjectType {
        public static final String SERVLET_JSP = "Servlet/JSP";

        public static final String SPRING = "Spring";
    }

    public interface Paas {
        public static final String NONE = "None";

        public static final String CLOUDFOUNDRY = "CloudFoundry";

        public static final String CLOUDBEES = "CloudBees";

        public static final String HEROKU = "Heroku";

        public static final String OPENSHIFT = "OpenShift";
    }

    private String name;

    private String type;

    private String paas;

    private FolderModel parenFolder;

    /**
     * Can contain variable properties: If project will be deploy to CloudFoundry, than:
     * <ul>
     * <li><code>name</code> - the name to deploy to CloudFoundry</li>
     * <li><code>url</code> - the url to deploy to CloudFoundry</li>
     * <li><code>target</code> - the target(server) to deploy on CloudFoundry</li>
     * </ul>
     * <p/>
     * If project will be deploy to CloudBees, than:
     * <ul>
     * <li><code>domain</code> - the domain</li>
     * <li><code>name</code> - the name of application</li>
     * </ul>
     */
    private Map<String, String> properties = new HashMap<String, String>();

    public ProjectProperties() {
    }

    public ProjectProperties(String name) {
        this.name = name;
    }

    /** @return the name */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *         the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the type */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *         the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /** @return the paas */
    public String getPaas() {
        return paas;
    }

    /**
     * @param paas
     *         the paas to set
     */
    public void setPaas(String paas) {
        this.paas = paas;
    }

    /**
     * @param parenFolder
     *         the parenFolder to set
     */
    public void setParenFolder(FolderModel parenFolder) {
        this.parenFolder = parenFolder;
    }

    /** @return the parenFolder */
    public FolderModel getParenFolder() {
        return parenFolder;
    }

    /**
     * Contains variable properties: If project will be deploy to CloudFoundry, than:
     * <ul>
     * <li><code>cf-name</code> - the name to deploy to CloudFoundry</li>
     * <li><code>url</code> - the url to deploy to CloudFoundry</li>
     * </ul>
     * <p/>
     * If project will be deploy to CloudBees, than:
     * <ul>
     * <li><code>domain</code> - the domain</li>
     * <li><code>cb-name</code> - the name of application</li>
     * </ul>
     *
     * @return the properties
     */
    public Map<String, String> getProperties() {
        return properties;
    }

}
