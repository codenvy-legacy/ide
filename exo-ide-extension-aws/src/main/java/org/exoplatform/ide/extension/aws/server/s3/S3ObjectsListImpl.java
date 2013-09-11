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
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class S3ObjectsListImpl implements S3ObjectsList {
    private List<S3Object> s3Objects;
    private String         s3Bucket;
    private String         prefix;
    private String         nextMarker;
    private int            maxKeys;

    public S3ObjectsListImpl() {
    }

    public S3ObjectsListImpl(List<S3Object> s3Objects, String s3Bucket, String prefix, String nextMarker, int maxKeys) {
        this.s3Objects = s3Objects;
        this.s3Bucket = s3Bucket;
        this.prefix = prefix;
        this.nextMarker = nextMarker;
        this.maxKeys = maxKeys;
    }

    @Override
    public List<S3Object> getObjects() {
        return s3Objects;
    }

    @Override
    public void setObjects(List<S3Object> objects) {
        this.s3Objects = objects;
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
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getNextMarker() {
        return nextMarker;
    }

    @Override
    public void setNextMarker(String nextMarker) {
        this.nextMarker = nextMarker;
    }

    @Override
    public int getMaxKeys() {
        return maxKeys;
    }

    @Override
    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    @Override
    public String toString() {
        return "S3ObjectsListImpl{" +
               "s3Objects=" + s3Objects +
               ", s3Bucket='" + s3Bucket + '\'' +
               ", prefix='" + prefix + '\'' +
               ", nextMarker='" + nextMarker + '\'' +
               ", maxKeys=" + maxKeys +
               '}';
    }
}