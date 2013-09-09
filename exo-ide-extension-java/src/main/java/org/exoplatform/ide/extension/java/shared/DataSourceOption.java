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
