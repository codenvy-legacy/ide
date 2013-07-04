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
package com.codenvy.ide.ext.aws.shared.s3;

/**
 * Region where stores content on S3
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public enum S3Region {
    /**
     * The US Standard Amazon S3 Region. This region
     * uses Amazon S3 servers located in the United
     * States.
     * <p>
     * This is the default Amazon S3 Region. All requests sent to
     * <code>s3.amazonaws.com</code> go
     * to this region unless a location constraint is specified when creating a bucket.
     * The US Standard Region automatically places
     * data in either Amazon's east or west coast data centers depending on
     * which one provides the lowest latency. The US Standard Region
     * provides eventual consistency for all requests.
     * </p>
     */
    US_Standard(null),

    /**
     * The US-West (Northern California) Amazon S3 Region. This region uses Amazon S3
     * servers located in Northern California.
     * <p>
     * When using buckets in this region, optionally set the client
     * endpoint to <code>s3-us-west-1.amazonaws.com</code> on all requests to these
     * buckets to reduce any latency experienced after the first
     * hour of creating a bucket in this region.
     * </p>
     * <p>
     * In Amazon S3, the US-West (Northern California) Region provides
     * read-after-write consistency for PUTS of new objects in Amazon
     * S3 buckets and eventual consistency for overwrite PUTS and DELETES.
     * </p>
     */
    US_West("us-west-1"),

    /**
     * The US-West-2 (Oregon) Region. This region uses Amazon S3 servers located
     * in Oregon.
     * <p>
     * When using buckets in this region, optionally set the client
     * endpoint to <code>s3-us-west-2.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    US_West_2("us-west-2"),

    /**
     * The EU (Ireland) Amazon S3 Region. This region uses Amazon S3 servers located
     * in Ireland.
     * <p>
     * In Amazon S3, the EU (Ireland) Region provides read-after-write
     * consistency for PUTS of new objects in Amazon S3 buckets and eventual
     * consistency for overwrite PUTS and DELETES.
     * </p>
     */
    EU_Ireland("EU"),

    /**
     * The Asia Pacific (Singapore) Region. This region uses Amazon S3 servers located
     * in Singapore.
     * <p>
     * When using buckets in this region, optionally set the client
     * endpoint to <code>s3-ap-southeast-1.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    AP_Singapore("ap-southeast-1"),

    /**
     * The Asia Pacific (Tokyo) Region. This region uses Amazon S3 servers
     * located in Tokyo.
     * <p>
     * When using buckets in this region, optionally set the client endpoint to
     * <code>s3-ap-northeast-1.amazonaws.com</code> on all requests to these
     * buckets to reduce any latency experienced after the first hour of
     * creating a bucket in this region.
     * </p>
     */
    AP_Tokyo("ap-northeast-1"),

    /**
     * The South America (Sao Paulo) Region. This region uses Amazon S3 servers
     * located in Sao Paulo.
     * <p>
     * When using buckets in this region, optionally set the client endpoint to
     * <code>s3-sa-east-1.amazonaws.com</code> on all requests to these buckets
     * to reduce any latency experienced after the first hour of creating a
     * bucket in this region.
     * </p>
     */
    SA_SaoPaulo("sa-east-1");

    private final String value;

    private S3Region(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * Returns the Amazon S3 Region enumeration value representing the specified Amazon
     * S3 Region ID string. If specified string doesn't map to a known Amazon S3
     * Region, then an <code>IllegalArgumentException</code> is thrown.
     *
     * @param value
     *         The Amazon S3 region ID string.
     * @return The Amazon S3 Region enumeration value representing the specified Amazon
     *         S3 Region ID.
     * @throws IllegalArgumentException
     *         If the specified value does not map to one of the known
     *         Amazon S3 regions.
     */
    public static S3Region fromValue(String value) {
        for (S3Region v : S3Region.values()) {
            if (v.value == null) {
                if (value == null) {
                    return v;
                }
            } else if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
