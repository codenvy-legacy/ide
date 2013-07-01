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
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public enum S3IdentityGroupType {
    AllUsers("AllUsers", "http://acs.amazonaws.com/groups/global/AllUsers"),
    AuthenticatedUsers("AuthenticatedUsers", "http://acs.amazonaws.com/groups/global/AuthenticatedUsers"),
    LogDelivery("LogDelivery", "http://acs.amazonaws.com/groups/s3/LogDelivery");

    private final String value;
    private final String uri;

    private S3IdentityGroupType(String value, String uri) {
        this.value = value;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return value;
    }

    public static S3IdentityGroupType fromValue(String value) {
        for (S3IdentityGroupType v : S3IdentityGroupType.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
