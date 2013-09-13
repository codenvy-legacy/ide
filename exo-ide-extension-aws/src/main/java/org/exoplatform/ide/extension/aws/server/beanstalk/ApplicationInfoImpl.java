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
package org.exoplatform.ide.extension.aws.server.beanstalk;

import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationInfoImpl implements ApplicationInfo {
    private String       name;
    private String       description;
    private long         created;
    private long         updated;
    private List<String> versions;
    private List<String> configurationTemplates;

    public ApplicationInfoImpl(String name,
                               String description,
                               Date created,
                               Date updated,
                               List<String> versions,
                               List<String> configurationTemplates) {
        this(name, description, created == null ? -1 : created.getTime(), updated == null ? -1 : updated.getTime(),
             versions, configurationTemplates);
    }

    public ApplicationInfoImpl(String name,
                               String description,
                               long created,
                               long updated,
                               List<String> versions,
                               List<String> configurationTemplates) {
        this.name = name;
        this.description = description;
        this.created = created;
        this.updated = updated;
        this.versions = versions;
        this.configurationTemplates = configurationTemplates;
    }

    public ApplicationInfoImpl() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public void setCreated(long creationDate) {
        created = creationDate;
    }

    @Override
    public long getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(long modificationDate) {
        updated = modificationDate;
    }

    @Override
    public List<String> getVersions() {
        if (versions == null) {
            versions = new ArrayList<String>();
        }
        return versions;
    }

    @Override
    public void setVersions(List<String> versions) {
        this.versions = versions;
    }

    @Override
    public List<String> getConfigurationTemplates() {
        if (configurationTemplates == null) {
            configurationTemplates = new ArrayList<String>();
        }
        return configurationTemplates;
    }

    @Override
    public void setConfigurationTemplates(List<String> configurationTemplates) {
        this.configurationTemplates = configurationTemplates;
    }

    @Override
    public String toString() {
        return "ApplicationInfoImpl{" +
               "name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", created=" + created +
               ", updated=" + updated +
               ", versions=" + versions +
               ", configurationTemplates=" + configurationTemplates +
               '}';
    }
}
