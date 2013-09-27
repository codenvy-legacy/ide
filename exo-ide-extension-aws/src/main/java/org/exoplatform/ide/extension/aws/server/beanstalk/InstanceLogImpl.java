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
package org.exoplatform.ide.extension.aws.server.beanstalk;

import org.exoplatform.ide.extension.aws.shared.beanstalk.InstanceLog;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InstanceLogImpl implements InstanceLog {
    private String instanceId;
    private String logUrl;

    public InstanceLogImpl() {
    }

    public InstanceLogImpl(String instanceId, String logUrl) {
        this.instanceId = instanceId;
        this.logUrl = logUrl;
    }

    @Override
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    @Override
    public String getLogUrl() {
        return logUrl;
    }

    @Override
    public void setLogUrl(String logUrl) {
        this.logUrl = logUrl;
    }

    @Override
    public String toString() {
        return "InstanceLogImpl{" +
               "instanceId='" + instanceId + '\'' +
               ", logUrl='" + logUrl + '\'' +
               '}';
    }
}
