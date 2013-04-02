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
