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

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentHealth;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;

import java.util.Date;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EnvironmentInfoImpl implements EnvironmentInfo {
    private String            name;
    private String            id;
    private String            applicationName;
    private String            versionLabel;
    private String            solutionStackName;
    private String            templateName;
    private String            description;
    private String            endpointUrl;
    private String            cNAME;
    private long              created;
    private long              updated;
    private EnvironmentStatus status;
    private EnvironmentHealth health;

    public static class Builder {
        private String            name;
        private String            id;
        private String            applicationName;
        private String            versionLabel;
        private String            solutionStackName;
        private String            templateName;
        private String            description;
        private String            endpointUrl;
        private String            cNAME;
        private long              created;
        private long              updated;
        private EnvironmentStatus status;
        private EnvironmentHealth health;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder applicationName(String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public Builder versionLabel(String versionLabel) {
            this.versionLabel = versionLabel;
            return this;
        }

        public Builder solutionStackName(String solutionStackName) {
            this.solutionStackName = solutionStackName;
            return this;
        }

        public Builder templateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder endpointUrl(String endpointUrl) {
            this.endpointUrl = endpointUrl;
            return this;
        }

        public Builder cNAME(String cNAME) {
            this.cNAME = cNAME;
            return this;
        }

        public Builder created(Date created) {
            if (created == null) {
                this.created = -1;
                return this;
            }
            this.created = created.getTime();
            return this;
        }

        public Builder updated(Date updated) {
            if (updated == null) {
                this.updated = -1;
                return this;
            }
            this.updated = updated.getTime();
            return this;
        }

        public Builder status(String status) {
            this.status = EnvironmentStatus.fromValue(status);
            return this;
        }

        public Builder health(String health) {
            this.health = EnvironmentHealth.fromValue(health);
            return this;
        }

        public EnvironmentInfo build() {
            return new EnvironmentInfoImpl(this);
        }
    }

    private EnvironmentInfoImpl(Builder builder) {
        this.name = builder.name;
        this.id = builder.id;
        this.applicationName = builder.applicationName;
        this.versionLabel = builder.versionLabel;
        this.solutionStackName = builder.solutionStackName;
        this.templateName = builder.templateName;
        this.description = builder.description;
        this.endpointUrl = builder.endpointUrl;
        this.cNAME = builder.cNAME;
        this.created = builder.created;
        this.updated = builder.updated;
        this.status = builder.status;
        this.health = builder.health;
    }

    public EnvironmentInfoImpl() {
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
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String getVersionLabel() {
        return versionLabel;
    }

    @Override
    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    @Override
    public String getSolutionStackName() {
        return solutionStackName;
    }

    @Override
    public void setSolutionStackName(String solutionStackName) {
        this.solutionStackName = solutionStackName;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
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
    public String getEndpointUrl() {
        return endpointUrl;
    }

    @Override
    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    @Override
    public String getCname() {
        return cNAME;
    }

    @Override
    public void setCname(String cNAME) {
        this.cNAME = cNAME;
    }

    @Override
    public long getCreated() {
        return created;
    }

    @Override
    public void setCreated(long created) {
        this.created = created;
    }

    @Override
    public long getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @Override
    public EnvironmentStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(EnvironmentStatus status) {
        this.status = status;
    }

    @Override
    public EnvironmentHealth getHealth() {
        return health;
    }

    @Override
    public void setHealth(EnvironmentHealth health) {
        this.health = health;
    }

    @Override
    public String toString() {
        return "EnvironmentInfoImpl{" +
               "name='" + name + '\'' +
               ", id='" + id + '\'' +
               ", applicationName='" + applicationName + '\'' +
               ", versionLabel='" + versionLabel + '\'' +
               ", solutionStackName='" + solutionStackName + '\'' +
               ", templateName='" + templateName + '\'' +
               ", description='" + description + '\'' +
               ", endpointUrl='" + endpointUrl + '\'' +
               ", cname='" + cNAME + '\'' +
               ", created=" + created +
               ", updated=" + updated +
               ", status=" + status +
               ", health=" + health +
               '}';
    }
}
