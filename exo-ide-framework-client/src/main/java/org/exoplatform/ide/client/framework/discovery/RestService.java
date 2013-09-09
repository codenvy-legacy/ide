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
package org.exoplatform.ide.client.framework.discovery;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represent REST Service <br>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 21, 2010 5:15:05 PM evgen $
 */
public class RestService implements IRestService {
    private String fqn;

    private String path;

    private String regex;

    private String fullPath;

    private Map<String, RestService> childServices = new HashMap<String, RestService>();

    public RestService(String path) {
        this("", path, "");
    }

    /**
     * @param fqn
     * @param path
     * @param regex
     */
    public RestService(String fqn, String path, String regex) {
        this.fqn = fqn;
        this.path = path;
        this.regex = regex;
        this.fullPath = path;
    }

    /** @see org.exoplatform.ide.client.framework.discovery.IRestService#getFqn() */
    @Override
    public String getFqn() {
        return fqn;
    }

    /** @see org.exoplatform.ide.client.framework.discovery.IRestService#setFqn(java.lang.String) */
    @Override
    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    /** @see org.exoplatform.ide.client.framework.discovery.IRestService#getPath() */
    @Override
    public String getPath() {
        return path;
    }

    /** @see org.exoplatform.ide.client.framework.discovery.IRestService#setPath(java.lang.String) */
    @Override
    public void setPath(String path) {
        this.path = path;
    }

    /** @see org.exoplatform.ide.client.framework.discovery.IRestService#getRegex() */
    @Override
    public String getRegex() {
        return regex;
    }

    /** @see org.exoplatform.ide.client.framework.discovery.IRestService#setRegex(java.lang.String) */
    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /** @return the childServices */
    public Map<String, RestService> getChildServices() {
        return childServices;
    }

    /**
     * @param childServices
     *         the childServices to set
     */
    public void setChildServices(Map<String, RestService> childServices) {
        this.childServices = childServices;
    }

    /** @return the fullPath */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * @param fullPath
     *         the fullPath to set
     */
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

}
