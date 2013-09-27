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
package org.exoplatform.ide.testframework.server.heroku;

import java.util.HashMap;

/**
 * Bean for Heroku application's data.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 6, 2011 4:58:08 PM anya $
 */
public class HerokuApplication {
    /** Application's name. */
    private String name;

    /** Location of Git working directory. */
    private String gitUrl;

    /** Remote repository name. */
    private String remoteName;

    /** Application's properties. */
    private HashMap<String, String> properties;

    public HerokuApplication() {
    }

    /**
     * @param name
     *         application's name
     * @param gitUrl
     *         Git working directory location
     * @param remoteName
     *         remote repository name
     * @param properties
     *         application's properties
     */
    public HerokuApplication(String name, String gitUrl, String remoteName, HashMap<String, String> properties) {
        this.name = name;
        this.gitUrl = gitUrl;
        this.remoteName = remoteName;
        this.properties = properties;
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

    /** @return the gitUrl */
    public String getGitUrl() {
        return gitUrl;
    }

    /**
     * @param gitUrl
     *         the gitUrl to set
     */
    public void setGitUrl(String gitUrl) {
        this.gitUrl = gitUrl;
    }

    /** @return the remoteName */
    public String getRemoteName() {
        return remoteName;
    }

    /**
     * @param remoteName
     *         the remoteName to set
     */
    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    /** @return the properties */
    public HashMap<String, String> getProperties() {
        if (properties == null) {
            properties = new HashMap<String, String>();
        }
        return properties;
    }

    /**
     * @param properties
     *         the properties to set
     */
    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }
}
