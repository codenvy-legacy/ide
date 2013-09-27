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
package org.exoplatform.ide.extension.aws.server.s3;

import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectVersion;
import org.exoplatform.ide.extension.aws.shared.s3.S3Owner;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3ObjectVersionImpl implements S3ObjectVersion {
    private String  s3Bucket;
    private String  s3Key;
    private String  versionId;
    private S3Owner owner;
    private long    lastModifiedDate;
    private long    size;

    public S3ObjectVersionImpl() {
    }

    public static class Builder {
        private String  s3Bucket;
        private String  s3Key;
        private String  versionId;
        private S3Owner owner;
        private long    lastModifiedDate;
        private long    size;

        public Builder withS3Bucket(String s3Bucket) {
            this.s3Bucket = s3Bucket;
            return this;
        }

        public Builder withS3Key(String s3Key) {
            this.s3Key = s3Key;
            return this;
        }

        public Builder withVersionId(String versionId) {
            this.versionId = versionId;
            return this;
        }

        public Builder withOwner(S3Owner owner) {
            this.owner = owner;
            return this;
        }

        public Builder withLastModifiedDate(long lastModifiedDate) {
            this.lastModifiedDate = lastModifiedDate;
            return this;
        }

        public Builder withSize(long size) {
            this.size = size;
            return this;
        }

        public S3ObjectVersion build() {
            return new S3ObjectVersionImpl(this);
        }
    }

    private S3ObjectVersionImpl(Builder builder) {
        builder.s3Bucket = s3Bucket;
        builder.s3Key = s3Key;
        builder.versionId = versionId;
        builder.owner = owner;
        builder.lastModifiedDate = lastModifiedDate;
        builder.size = size;
    }

    @Override
    public String getS3Bucket() {
        return s3Bucket;
    }

    @Override
    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    @Override
    public String getS3Key() {
        return s3Key;
    }

    @Override
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    @Override
    public String getVersionId() {
        return versionId;
    }

    @Override
    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    @Override
    public S3Owner getOwner() {
        return owner;
    }

    @Override
    public void setOwner(S3Owner owner) {
        this.owner = owner;
    }

    @Override
    public long getLastModifiedDate() {
        return lastModifiedDate;
    }

    @Override
    public void setLastModifiedDate(long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "S3ObjectVersionImpl{" +
               "s3Bucket='" + s3Bucket + '\'' +
               ", s3Key='" + s3Key + '\'' +
               ", versionId='" + versionId + '\'' +
               ", owner=" + owner +
               ", lastModifiedDate=" + lastModifiedDate +
               ", size=" + size +
               '}';
    }
}
