/*
 * Copyright (C) 2012 eXo Platform SAS.
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
