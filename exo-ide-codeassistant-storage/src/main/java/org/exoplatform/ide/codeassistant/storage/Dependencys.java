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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.jvm.bean.Dependency;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Dependencys {
    private List<Dependency> dependencies;

    private String zipUrl;

    /**
     *
     */
    public Dependencys() {
    }

    /**
     * @param dependencies
     * @param zipUrl
     */
    public Dependencys(List<Dependency> dependencies, String zipUrl) {
        super();
        this.dependencies = dependencies;
        this.zipUrl = zipUrl;
    }

    /** @return the dependencies */
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies
     *         the dependencies to set
     */
    public void setDependencies(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    /** @return the zipUrl */
    public String getZipUrl() {
        return zipUrl;
    }

    /**
     * @param zipUrl
     *         the zipUrl to set
     */
    public void setZipUrl(String zipUrl) {
        this.zipUrl = zipUrl;
    }

}
