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
package org.exoplatform.ide.shell.shared;

import java.util.HashSet;
import java.util.Set;

public class CLIResourceParameter {
    public enum Type {
        PATH("path"), //
        QUERY("query"), //
        HEADER("header"), //
        MATRIX("matrix"), //
        COOKIE("cookie"), //
        FORM("form"), //
        BODY("body");

        private final String type;

        private Type(String type) {
            this.type = type;
        }

        public String toString() {
            return type;
        }
    }

    private String name;

    private Set<String> options;

    private Type type;

    private boolean mandatory;

    private boolean hasArg = true;

    public CLIResourceParameter(String name, Set<String> options, Type type, boolean mandatory, boolean hasArg) {
        this.name = name;
        this.options = options;
        this.type = type;
        this.mandatory = mandatory;
        this.hasArg = hasArg;
    }
    
    
    /**
     * Copy constructor
     * 
     * @param crp
     */
    public CLIResourceParameter(CLIResourceParameter crp) {
        this(crp.getName(), //
                                crp.getOptions() != null ? new HashSet<String>(crp.getOptions()) : new HashSet<String>(),//
                                 crp.getType(), crp.mandatory, crp.hasArg);
    }


    public CLIResourceParameter() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getOptions() {
        if (options == null) {
            options = new HashSet<String>();
        }
        return options;
    }

    public void setOptions(Set<String> options) {
        this.options = options;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isHasArg() {
        return hasArg;
    }

    public void setHasArg(boolean hasArg) {
        this.hasArg = hasArg;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = hash * 31 + ((name == null) ? 0 : name.hashCode());
        hash = hash * 31 + ((type == null) ? 0 : type.hashCode());
        hash = hash * 31 + ((options == null) ? 0 : options.hashCode());
        hash = hash * 31 + (mandatory ? 123 : 321);
        hash = hash * 31 + (hasArg ? 456 : 654);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        CLIResourceParameter other = (CLIResourceParameter)obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (options == null) {
            if (other.options != null)
                return false;
        } else if (!options.equals(other.options)) {
            return false;
        }
        if (mandatory != other.mandatory) {
            return false;
        }
        if (hasArg != other.hasArg) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CLIResourceParameter [name=" + name + ", options=" + options + ", type=" + type + ", mandatory="
               + mandatory + ", hasArg=" + hasArg + "]";
    }
}
