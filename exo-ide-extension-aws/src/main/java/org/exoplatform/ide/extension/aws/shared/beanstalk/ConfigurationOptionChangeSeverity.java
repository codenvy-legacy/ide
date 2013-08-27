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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

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
