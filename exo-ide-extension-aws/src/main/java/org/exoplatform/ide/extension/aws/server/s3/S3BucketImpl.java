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

import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3Owner;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3BucketImpl implements S3Bucket {
    private String name;
    private long creationDate = -1;
    private S3Owner owner;

    public S3BucketImpl() {
    }

    public S3BucketImpl(String name) {
        this.name = name;
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
    public long getCreated() {
        return creationDate;
    }

    @Override
    public void setCreated(long creationDate) {
        this.creationDate = creationDate;
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
        return "S3BucketImpl{" +
               "name='" + name + '\'' +
               ", creationDate=" + creationDate +
               ", owner=" + owner +
               '}';
    }
}
