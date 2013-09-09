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

import org.exoplatform.ide.extension.aws.shared.s3.NewS3Object;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class NewS3ObjectImpl implements NewS3Object {
    private String s3Bucket;
    private String s3Key;
    private String versionId;

    public NewS3ObjectImpl() {
    }

    public NewS3ObjectImpl(String s3Bucket, String s3Key, String versionId) {
        this.s3Bucket = s3Bucket;
        this.s3Key = s3Key;
        this.versionId = versionId;
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
    public String toString() {
        return "NewS3ObjectImpl{" +
               "s3Bucket='" + s3Bucket + '\'' +
               ", s3Key='" + s3Key + '\'' +
               ", versionId='" + versionId + '\'' +
               '}';
    }
}