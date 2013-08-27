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
package org.exoplatform.ide.extension.cloudfoundry.server.json;

import java.util.Arrays;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InstancesInfo {
    private InstanceInfo[] instances;

    public InstanceInfo[] getInstances() {
        return instances;
    }

    public void setInstances(InstanceInfo[] instances) {
        this.instances = instances;
    }

    @Override
    public String toString() {
        return "InstancesInfo{" +
               "instances=" + (instances == null ? null : Arrays.asList(instances)) +
               '}';
    }
}
