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
package org.exoplatform.ide;

import java.util.List;

/**
 * Project template.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ProjectTemplate.java Jul 28, 2011 5:26:21 PM vereshchaka $
 */
public class ProjectTemplate extends FolderTemplate {
    private String type;

    private List<String> targets;

    /**
     * @param type
     *         the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /** @return the type */
    public String getType() {
        return type;
    }

    /** @return the destination */
    public List<String> getTargets() {
        return targets;
    }

    /**
     * @param destination
     *         the destination to set
     */
    public void setDestination(List<String> targets) {
        this.targets = targets;
    }
}
