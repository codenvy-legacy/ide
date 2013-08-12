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

/**
 * Describes the properties of an environment.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface EnvironmentInfo {
    /**
     * The name of this environment.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>4 - 23<br/>
     *
     * @return The name of this environment.
     */
    String getName();

    /**
     * The ID of this environment.
     *
     * @return The ID of this environment.
     */
    String getId();

    /**
     * The name of the application associated with this environment.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     */
    String getApplicationName();

    /**
     * The application version deployed in this environment.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     */
    String getVersionLabel();

    /**
     * The name of the <code>SolutionStack</code> deployed with this
     * environment.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>0 - 100<br/>
     */
    String getSolutionStackName();

    /**
     * The name of the configuration template used to originally launch this
     * environment.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The name of the configuration template used to originally launch this
     *         environment.
     */
    String getTemplateName();

    /**
     * Describes this environment.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>0 - 200<br/>
     *
     * @return Describes this environment.
     */
    String getDescription();

    /**
     * The URL to the LoadBalancer for this environment.
     *
     * @return The URL to the LoadBalancer for this environment.
     */
    String getEndpointUrl();

    /**
     * The URL to the CNAME for this environment.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 255<br/>
     *
     * @return The URL to the CNAME for this environment.
     */
    String getCname();

    /**
     * The creation date for this environment.
     *
     * @return The creation date for this environment.
     */
    double getCreated();

    /**
     * The last modified date for this environment.
     *
     * @return The last modified date for this environment.
     */
    double getUpdated();

    /**
     * The current operational status of the environment: <ul> <li>
     * <code>Launching</code>: Environment is in the process of initial
     * deployment. </li> <li> <code>Updating</code>: Environment is in the
     * process of updating its configuration settings or application version.
     * </li> <li> <code>Ready</code>: Environment is available to have an
     * action performed on it, such as update or terminate. </li> <li>
     * <code>Terminating</code>: Environment is in the shut-down process.
     * </li> <li> <code>Terminated</code>: Environment is not running. </li>
     * </ul>
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Allowed Values: </b>Launching, Updating, Ready, Terminating, Terminated
     *
     * @return The current operational status of the environment: <ul> <li>
     *         <code>Launching</code>: Environment is in the process of initial
     *         deployment. </li> <li> <code>Updating</code>: Environment is in the
     *         process of updating its configuration settings or application version.
     *         </li> <li> <code>Ready</code>: Environment is available to have an
     *         action performed on it, such as update or terminate. </li> <li>
     *         <code>Terminating</code>: Environment is in the shut-down process.
     *         </li> <li> <code>Terminated</code>: Environment is not running. </li>
     *         </ul>
     * @see EnvironmentStatus
     */
    EnvironmentStatus getStatus();

    /**
     * Describes the health status of the environment. AWS Elastic Beanstalk
     * indicates the failure levels for a running environment: <enumValues>
     * <value name="Red"> <p> <code>Red</code> : Indicates the environment is
     * not working. </value> <value name="Yellow"> <p> <code>Yellow</code>:
     * Indicates that something is wrong, the application might not be
     * available, but the instances appear running. </value> <value
     * name="Green"> <p> <code>Green</code>: Indicates the environment is
     * healthy and fully functional. </value> </enumValues> <ul> <li>
     * <code>Red</code>: Indicates the environment is not responsive. Occurs
     * when three or more consecutive failures occur for an environment.
     * </li> <li> <code>Yellow</code>: Indicates that something is wrong.
     * Occurs when two consecutive failures occur for an environment. </li>
     * <li> <code>Green</code>: Indicates the environment is healthy and
     * fully functional. </li> <li> <code>Grey</code>: Default health for a
     * new environment. The environment is not fully launched and health
     * checks have not started or health checks are suspended during an
     * <code>UpdateEnvironment</code> or <code>RestartEnvironement</code>
     * request. </li> </ul> <p> Default: <code>Grey</code>
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Allowed Values: </b>Green, Yellow, Red, Grey
     *
     * @return Describes the health status of the environment. AWS Elastic Beanstalk
     *         indicates the failure levels for a running environment: <enumValues>
     *         <value name="Red"> <p> <code>Red</code> : Indicates the environment is
     *         not working. </value> <value name="Yellow"> <p> <code>Yellow</code>:
     *         Indicates that something is wrong, the application might not be
     *         available, but the instances appear running. </value> <value
     *         name="Green"> <p> <code>Green</code>: Indicates the environment is
     *         healthy and fully functional. </value> </enumValues> <ul> <li>
     *         <code>Red</code>: Indicates the environment is not responsive. Occurs
     *         when three or more consecutive failures occur for an environment.
     *         </li> <li> <code>Yellow</code>: Indicates that something is wrong.
     *         Occurs when two consecutive failures occur for an environment. </li>
     *         <li> <code>Green</code>: Indicates the environment is healthy and
     *         fully functional. </li> <li> <code>Grey</code>: Default health for a
     *         new environment. The environment is not fully launched and health
     *         checks have not started or health checks are suspended during an
     *         <code>UpdateEnvironment</code> or <code>RestartEnvironement</code>
     *         request. </li> </ul> <p> Default: <code>Grey</code>
     * @see EnvironmentHealth
     */
    EnvironmentHealth getHealth();
}
