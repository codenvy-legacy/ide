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