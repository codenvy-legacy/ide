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
package org.exoplatform.ide.client.framework.template;

import java.util.List;

/**
 * Template for projects.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 21, 2010 $
 */
public class ProjectTemplateImpl extends FolderTemplateImpl implements ProjectTemplate {
    /** The location of class path file. */
    private String classPathLocation;

    /** Project type (need to detect is generate classpath for project). */
    private String type;

    private List<String> targets;

    /**
     *
     */
    public ProjectTemplateImpl(String name) {
        super(name);
    }

    public ProjectTemplateImpl(String name, String description, String nodeName, List<AbstractTemplate> templates) {
        super(name, description, nodeName, templates);
    }

    public ProjectTemplateImpl(String name, String description, boolean isDefault) {
        super(name, description, isDefault);
    }

    /** @see org.exoplatform.ide.client.framework.template.ProjectTemplate#getClassPathLocation() */
    @Override
    public String getClassPathLocation() {
        return classPathLocation;
    }

    /** @see org.exoplatform.ide.client.framework.template.ProjectTemplate#setClassPathLocation(java.lang.String) */
    @Override
    public void setClassPathLocation(String classPathLocation) {
        this.classPathLocation = classPathLocation;
    }

    /** @see org.exoplatform.ide.client.framework.template.ProjectTemplate#getType() */
    @Override
    public String getType() {
        return type;
    }

    /** @see org.exoplatform.ide.client.framework.template.ProjectTemplate#setType(java.lang.String) */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /** @see org.exoplatform.ide.client.framework.template.ProjectTemplate#getDestination() */
    @Override
    public List<String> getTargets() {
        return targets;
    }

    /** @see org.exoplatform.ide.client.framework.template.ProjectTemplate#setDestination(java.util.List) */
    @Override
    public void setTargets(List<String> targets) {
        this.targets = targets;
    }
}
