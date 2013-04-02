/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.fs;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class QueryExpression {
    private String name;
    private String path;
    private String mediaType;
    private String text;

    public String getPath() {
        return path;
    }

    public QueryExpression setPath(String path) {
        this.path = path;
        return this;
    }

    public String getName() {
        return name;
    }

    public QueryExpression setName(String name) {
        this.name = name;
        return this;
    }

    public String getMediaType() {
        return mediaType;
    }

    public QueryExpression setMediaType(String mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public String getText() {
        return text;
    }

    public QueryExpression setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public String toString() {
        return "QueryExpression{" +
               "name='" + name + '\'' +
               ", path='" + path + '\'' +
               ", mediaType='" + mediaType + '\'' +
               ", text='" + text + '\'' +
               '}';
    }
}
