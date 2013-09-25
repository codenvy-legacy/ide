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
package com.codenvy.ide.extension;

/**
 * Describes Dependency information of Extension.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class DependencyDescription {
    private String id;

    private String version;

    /**
     * Create {@link DependencyDescription} instance
     *
     * @param id
     * @param version
     */
    public DependencyDescription(String id, String version) {
        this.id = id;
        this.version = version;
    }

    /**
     * Get required extension id
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Get version of the used dependency
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

}
