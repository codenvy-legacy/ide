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
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface EnvironmentInfo {
    String getName();

    void setName(String name);

    String getId();

    void setId(String id);

    String getApplicationName();

    void setApplicationName(String applicationName);

    String getVersionLabel();

    void setVersionLabel(String versionLabel);

    String getSolutionStackName();

    void setSolutionStackName(String solutionStackName);

    String getTemplateName();

    void setTemplateName(String templateName);

    String getDescription();

    void setDescription(String description);

    String getEndpointUrl();

    void setEndpointUrl(String endpointURL);

    String getCname();

    void setCname(String cNAME);

    long getCreated();

    void setCreated(long created);

    long getUpdated();

    void setUpdated(long updated);

    EnvironmentStatus getStatus();

    void setStatus(EnvironmentStatus status);

    EnvironmentHealth getHealth();

    void setHealth(EnvironmentHealth health);
}
