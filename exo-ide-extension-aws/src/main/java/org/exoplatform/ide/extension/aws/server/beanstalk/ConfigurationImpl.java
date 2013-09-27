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

import com.amazonaws.services.elasticbeanstalk.model.ConfigurationOptionSetting;

import org.exoplatform.ide.extension.aws.shared.beanstalk.Configuration;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOption;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationTemplateDeploymentStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ConfigurationImpl implements Configuration {
    private String                                solutionStackName;
    private String                                applicationName;
    private String                                templateName;
    private String                                description;
    private String                                environmentName;
    private ConfigurationTemplateDeploymentStatus deploymentStatus;
    private long                                  created;
    private long                                  updated;
    private List<ConfigurationOption>             options;

    public static class Builder {
        private String                                solutionStackName;
        private String                                applicationName;
        private String                                templateName;
        private String                                description;
        private String                                environmentName;
        private ConfigurationTemplateDeploymentStatus deploymentStatus;
        private long                                  created;
        private long                                  updated;
        private List<ConfigurationOption>             options;

        public Builder solutionStackName(String solutionStackName) {
            this.solutionStackName = solutionStackName;
            return this;
        }

        public Builder applicationName(String applicationName) {
            this.applicationName = applicationName;
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

        public Builder environmentName(String environmentName) {
            this.environmentName = environmentName;
            return this;
        }

        public Builder deploymentStatus(String deploymentStatus) {
            this.deploymentStatus = ConfigurationTemplateDeploymentStatus.fromValue(deploymentStatus);
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

        public Builder options(List<ConfigurationOptionSetting> awsOptions) {
            if (awsOptions == null) {
                this.options = null;
                return this;
            }
            this.options = new ArrayList<ConfigurationOption>(awsOptions.size());
            for (ConfigurationOptionSetting awsOption : awsOptions) {
                options.add(
                        new ConfigurationOptionImpl(awsOption.getNamespace(), awsOption.getOptionName(), awsOption.getValue()));
            }
            return this;
        }

        public Configuration build() {
            return new ConfigurationImpl(this);
        }
    }


    private ConfigurationImpl(Builder builder) {
        this.solutionStackName = builder.solutionStackName;
        this.applicationName = builder.applicationName;
        this.templateName = builder.templateName;
        this.description = builder.description;
        this.environmentName = builder.environmentName;
        this.deploymentStatus = builder.deploymentStatus;
        this.created = builder.created;
        this.updated = builder.updated;
        this.options = builder.options;
    }

    public ConfigurationImpl() {
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
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
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
    public String getEnvironmentName() {
        return environmentName;
    }

    @Override
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    @Override
    public ConfigurationTemplateDeploymentStatus getDeploymentStatus() {
        return deploymentStatus;
    }

    @Override
    public void setDeploymentStatus(ConfigurationTemplateDeploymentStatus deploymentStatus) {
        this.deploymentStatus = deploymentStatus;
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
    public List<ConfigurationOption> getOptions() {
        return options;
    }

    @Override
    public void setOptions(List<ConfigurationOption> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "ConfigurationImpl{" +
               "solutionStackName='" + solutionStackName + '\'' +
               ", applicationName='" + applicationName + '\'' +
               ", templateName='" + templateName + '\'' +
               ", description='" + description + '\'' +
               ", environmentName='" + environmentName + '\'' +
               ", deploymentStatus=" + deploymentStatus +
               ", created=" + created +
               ", updated=" + updated +
               ", options=" + options +
               '}';
    }
}
