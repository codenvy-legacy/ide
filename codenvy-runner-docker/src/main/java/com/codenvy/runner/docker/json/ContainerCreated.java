/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.runner.docker.json;

import java.util.Arrays;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class ContainerCreated {
    private String id;
    private String[] warnings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getWarnings() {
        return warnings;
    }

    public void setWarnings(String[] warnings) {
        this.warnings = warnings;
    }

    @Override
    public String toString() {
        return "ContainerCreated{" +
               "id='" + id + '\'' +
               ", warnings=" + Arrays.toString(warnings) +
               '}';
    }
}
