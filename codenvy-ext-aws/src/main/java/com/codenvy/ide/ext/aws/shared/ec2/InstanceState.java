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
package com.codenvy.ide.ext.aws.shared.ec2;

/**
 * EC2 instance state
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public enum InstanceState {
    pending("pending"),
    running("running"),
    shutting_down("shutting-down"),
    terminated("terminated"),
    stopping("stopping"),
    stopped("stopped");

    private final String value;

    private InstanceState(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    /**
     * Use this in place of valueOf.
     *
     * @param value
     *         real value
     * @return InstanceState corresponding to the value
     */
    public static InstanceState fromValue(String value) {
        for (InstanceState v : InstanceState.values()) {
            if (v.value.equals(value)) {
                return v;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + value + "' ");
    }
}
