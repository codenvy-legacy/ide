/*
 * Copyright (C) 2010 eXo Platform SAS.
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
