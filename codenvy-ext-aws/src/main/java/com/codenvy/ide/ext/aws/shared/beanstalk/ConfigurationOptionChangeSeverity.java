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
package com.codenvy.ide.ext.aws.shared.beanstalk;

/**
 * An indication of which action is required if the value for this configuration option changes.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public enum ConfigurationOptionChangeSeverity {
    /** There is no interruption to the environment or application availability. */
    NoInterruption("NoInterruption"),

    /** Application server on the running Amazon EC2 instance is restarted. */
    RestartApplicationServer("RestartApplicationServer"),

    /**
     * The environment is entirely restarted. All AWS resources are deleted and recreated, and the environment is
     * unavailable during the process.
     */
    RestartEnvironment("RestartEnvironment"),

    Unknown("Unknown");

    private final String value;

    private ConfigurationOptionChangeSeverity(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public static ConfigurationOptionChangeSeverity fromValue(String value) {
        for (ConfigurationOptionChangeSeverity v : ConfigurationOptionChangeSeverity.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
