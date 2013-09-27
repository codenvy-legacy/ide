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
package org.exoplatform.ide.testframework.server.cloudbees;

import java.util.Map;

/**
 * Bean for Cloudbees applicaiton data.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudbeesApplication.java Aug 16, 2011 12:25:06 PM vereshchaka $
 */
public class CloudbeesApplication {

    private String id;

    private String message;

    private String workDir;

    private String war;

    private Map<String, String> properties;

    public CloudbeesApplication() {
    }

    /**
     * @param id
     * @param message
     * @param workDir
     * @param war
     * @param properties
     */
    public CloudbeesApplication(String id, String message, String workDir, String war, Map<String, String> properties) {
        this.id = id;
        this.message = message;
        this.workDir = workDir;
        this.war = war;
        this.properties = properties;
    }

    /** @return the id */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *         the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return the workDir */
    public String getWorkDir() {
        return workDir;
    }

    /**
     * @param workDir
     *         the workDir to set
     */
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    /** @return the war */
    public String getWar() {
        return war;
    }

    /**
     * @param war
     *         the war to set
     */
    public void setWar(String war) {
        this.war = war;
    }

    /** @return the properties */
    public Map<String, String> getProperties() {
        return properties;
    }

    /**
     * @param properties
     *         the properties to set
     */
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

}
