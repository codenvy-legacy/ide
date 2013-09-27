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

import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.S3Item;

import java.util.Date;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationVersionInfoImpl implements ApplicationVersionInfo {
    private String name;
    private String description;
    private String versionLabel;
    private S3Item s3Location;
    private long   created;
    private long   updated;

    public static class Builder {
        private String name;
        private String description;
        private String versionLabel;
        private S3Item s3Location;
        private long   created;
        private long   updated;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder versionLabel(String versionLabel) {
            this.versionLabel = versionLabel;
            return this;
        }

        public Builder s3Location(String s3Bucket, String s3Key) {
            this.s3Location = new S3ItemImpl(s3Bucket, s3Key);
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

        public ApplicationVersionInfo build() {
            return new ApplicationVersionInfoImpl(this);
        }
    }

    private ApplicationVersionInfoImpl(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.versionLabel = builder.versionLabel;
        this.s3Location = builder.s3Location;
        this.created = builder.created;
        this.updated = builder.updated;
    }

    public ApplicationVersionInfoImpl() {
    }

    @Override
    public String getApplicationName() {
        return name;
    }

    @Override
    public void setApplicationName(String name) {
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
    public String getVersionLabel() {
        return versionLabel;
    }

    @Override
    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    @Override
    public S3Item getS3Location() {
        return s3Location;
    }

    @Override
    public void setS3Location(S3Item s3Location) {
        this.s3Location = s3Location;
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
    public String toString() {
        return "ApplicationVersionInfoImpl{" +
               "name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", versionLabel='" + versionLabel + '\'' +
               ", s3Location=" + s3Location +
               ", created=" + created +
               ", updated=" + updated +
               '}';
    }
}
