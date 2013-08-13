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
