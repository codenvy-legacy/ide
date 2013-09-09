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

import org.exoplatform.ide.extension.aws.shared.beanstalk.S3Item;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class S3ItemImpl implements S3Item {
    private String s3Bucket;
    private String s3Key;

    public S3ItemImpl(String s3Bucket, String s3Key) {
        this.s3Bucket = s3Bucket;
        this.s3Key = s3Key;
    }

    public S3ItemImpl() {
    }

    public String getS3Bucket() {
        return s3Bucket;
    }

    public void setS3Bucket(String s3Bucket) {
        this.s3Bucket = s3Bucket;
    }

    public String getS3Key() {
        return s3Key;
    }

    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    @Override
    public String toString() {
        return "S3ItemImpl{" +
               "s3Bucket='" + s3Bucket + '\'' +
               ", s3Key='" + s3Key + '\'' +
               '}';
    }
}
