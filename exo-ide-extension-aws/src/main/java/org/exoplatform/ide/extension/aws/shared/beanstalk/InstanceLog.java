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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

/**
 * Information about instance log in specified environment
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface InstanceLog {
    /**
     * Get ID of the instance
     *
     * @return ID of the instance
     */
    String getInstanceId();

    /**
     * Set ID of the instance
     *
     * @param instanceId
     *         ID of the instance
     */
    void setInstanceId(String instanceId);

    /**
     * Get url of the application logs
     *
     * @return url of the logs
     */
    String getLogUrl();

    /**
     * Set url of the application logs
     *
     * @param logUrl
     *         url of the logs
     */
    void setLogUrl(String logUrl);
}
