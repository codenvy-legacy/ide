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

import org.exoplatform.ide.extension.aws.shared.s3.S3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3Owner;

import java.util.Date;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class S3ObjectImpl implements S3Object {
    private String  s3Bucket;
    private String  s3Key;
    private String  eTag;
    private long    size;
    private long    updated;
    private String  storageClass;
    private S3Owner owner;

    public static class Builder {
        private String  s3Bucket;
        private String  s3Key;
        private String  eTag;
        private long    size;
        private long    updated;
        private String  storageClass;
        private S3Owner owner;

        public Builder s3Bucket(String s3Bucket) {
            this.s3Bucket = s3Bucket;
            return this;
        }

        public Builder s3Key(String s3Key) {
            this.s3Key = s3Key;
            return this;
        }

        public Builder eTag(String eTag) {
            this.eTag = eTag;
            return this;
        }

        public Builder size(long size) {
            this.size = size;
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

        public Builder storageClass(String storageClass) {
            this.storageClass = storageClass;
            return this;
        }

        public Builder owner(String id, String displayName) {
            this.owner = new S3OwnerImpl(id, displayName);
            return this;
        }

        public S3Object build() {
            return new S3ObjectImpl(this);
        }
    }

    private S3ObjectImpl(Builder builder) {
        this.s3Bucket = builder.s3Bucket;
        this.s3Key = builder.s3Key;
        this.eTag = builder.eTag;
        this.size = builder.size;
        this.updated = builder.updated;
        this.storageClass = builder.storageClass;
        this.owner = builder.owner;
    }

    public S3ObjectImpl() {
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
    public String getETag() {
        return eTag;
    }

    @Override
    public void setETag(String eTag) {
        this.eTag = eTag;
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
    public long getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @Override
    public String getStorageClass() {
        return storageClass;
    }

    @Override
    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
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
    public String toString() {
        return "S3ObjectImpl{" +
               "s3Bucket='" + s3Bucket + '\'' +
               ", s3Key='" + s3Key + '\'' +
               ", eTag='" + eTag + '\'' +
               ", size=" + size +
               ", updated=" + updated +
               ", storageClass='" + storageClass + '\'' +
               ", owner=" + owner +
               '}';
    }
}
