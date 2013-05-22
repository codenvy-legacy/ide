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

package org.exoplatform.ide.extension.java.shared;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class DataSourceOption {
    private String  name;
    private String  value;
    private boolean required;
    private String  description;

    public DataSourceOption(String name, String value, boolean required, String description) {
        this.name = name;
        this.value = value;
        this.required = required;
        this.description = description;
    }

    public DataSourceOption(DataSourceOption other) {
        this.name = other.name;
        this.value = other.value;
        this.required = other.required;
        this.description = other.description;
    }

    public DataSourceOption() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DataSourceOption{" +
               "name='" + name + '\'' +
               ", value='" + value + '\'' +
               ", required=" + required +
               ", description='" + description + '\'' +
               '}';
    }
}
