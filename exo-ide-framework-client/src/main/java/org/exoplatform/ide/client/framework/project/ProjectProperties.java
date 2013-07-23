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
package org.exoplatform.ide.client.framework.project;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 26, 2012 10:31:58 AM anya $
 */
public enum ProjectProperties {
    TYPE("vfs:projectType"),
    TARGET("exoide:target"),
    JREBEL_COUNT("jrebelCount"),
    JREBEL("jrebel"),
    DESCRIPTION("exoide:projectDescription"),
    MIME_TYPE("vfs:mimeType");

    private String value;

    private ProjectProperties(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ProjectProperties fromValue(String v) {
        for (ProjectProperties c : ProjectProperties.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
