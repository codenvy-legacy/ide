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
 * Defined types of projects.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 24, 2012 12:15:43 PM anya $
 */
@Deprecated
public enum ProjectType {
    PHP("PHP"),
    JSP("Servlet/JSP"),
    JAVA("Java"),
    JAR("Jar"),
    WAR("War"),
    JAVASCRIPT("JavaScript"),
    NODE_JS("nodejs"),
    PYTHON("Python"),
    DJANGO("Django"),
    RUBY_ON_RAILS("Rails"),
    RUBY("Ruby"),
    SPRING("Spring"),
    DEFAULT("default"),
    MultiModule("Maven Multi-module"),
    ANDROID("Android");

    /** Project's type name. */
    private String type;

    /**
     * @param type project's type name
     */
    private ProjectType(String type) {
        this.type = type;
    }

    /** @return {@link String} project's type value */
    public String value() {
        return type;
    }

    /**
     * @param v project's type value
     * @return {@link ProjectType}
     */
    public static ProjectType fromValue(String v) {
        for (ProjectType c : ProjectType.values()) {
            if (c.type.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }


    /** {@inheritDoc} */
    @Override
    public String toString() {
        return value();
    }
}
