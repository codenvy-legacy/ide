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

import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Configuration {
    String getSolutionStackName();

    /**
     * The name of the application associated with this release.
     *
     * @return The name of the application associated with this release.
     */
    String getApplicationName();

    /**
     * If not <code>null</code>, the name of the configuration template for
     * this configuration set.
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return If not <code>null</code>, the name of the configuration template for
     *         this configuration set.
     */
    String getTemplateName();

    /**
     * The description of this application version.
     *
     * @return The description of this application version.
     */
    String getDescription();

    /**
     * If not <code>null</code>, the name of the environment for this
     * configuration set.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>4 - 23<br/>
     *
     * @return If not <code>null</code>, the name of the environment for this
     *         configuration set.
     */
    String getEnvironmentName();

    /**
     * If this configuration set is associated with an environment, the
     * <code>DeploymentStatus</code> parameter indicates the deployment
     * status of this configuration set: <enumValues> <value name="null"> <p>
     * <code>null</code>: This configuration is not associated with a running
     * environment. </value> <value name="pending"> <p> <code>pending</code>:
     * This is a draft configuration that is not deployed to the associated
     * environment but is in the process of deploying. </value> <value
     * name="deployed"> <p> <code>deployed</code>: This is the configuration
     * that is currently deployed to the associated running environment.
     * </value> <value name="failed"> <p> <code>failed</code>: This is a
     * draft configuration, that failed to successfully deploy. </value>
     * </enumValues> <ul> <li> <code>null</code>: This configuration is not
     * associated with a running environment. </li> <li>
     * <code>pending</code>: This is a draft configuration that is not
     * deployed to the associated environment but is in the process of
     * deploying. </li> <li> <code>deployed</code>: This is the configuration
     * that is currently deployed to the associated running environment.
     * </li> <li> <code>failed</code>: This is a draft configuration that
     * failed to successfully deploy. </li> </ul>
     * <p>
     * <b>Constraints:</b><br/>
     * <b>Allowed Values: </b>deployed, pending, failed
     *
     * @return If this configuration set is associated with an environment, the
     *         <code>DeploymentStatus</code> parameter indicates the deployment
     *         status of this configuration set: <enumValues> <value name="null"> <p>
     *         <code>null</code>: This configuration is not associated with a running
     *         environment. </value> <value name="pending"> <p> <code>pending</code>:
     *         This is a draft configuration that is not deployed to the associated
     *         environment but is in the process of deploying. </value> <value
     *         name="deployed"> <p> <code>deployed</code>: This is the configuration
     *         that is currently deployed to the associated running environment.
     *         </value> <value name="failed"> <p> <code>failed</code>: This is a
     *         draft configuration, that failed to successfully deploy. </value>
     *         </enumValues> <ul> <li> <code>null</code>: This configuration is not
     *         associated with a running environment. </li> <li>
     *         <code>pending</code>: This is a draft configuration that is not
     *         deployed to the associated environment but is in the process of
     *         deploying. </li> <li> <code>deployed</code>: This is the configuration
     *         that is currently deployed to the associated running environment.
     *         </li> <li> <code>failed</code>: This is a draft configuration that
     *         failed to successfully deploy. </li> </ul>
     */
    ConfigurationTemplateDeploymentStatus getDeploymentStatus();

    /**
     * The date (in UTC time) when this configuration set was created.
     *
     * @return The date (in UTC time) when this configuration set was created.
     */
    Long getCreated();

    /**
     * The date (in UTC time) when this configuration set was last modified.
     *
     * @return The date (in UTC time) when this configuration set was last modified.
     */
    Long getUpdated();

    /**
     * A list of the configuration options and their values in this
     * configuration set.
     *
     * @return A list of the configuration options and their values in this
     *         configuration set.
     */
    JsonArray<ConfigurationOption> getOptions();
}
