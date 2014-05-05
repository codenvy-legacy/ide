/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
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
package com.codenvy.runner.docker;

/**
 * Describes docker based environment for deploy an application. Environment must be described in file 'dockerenv.c5y.json'. The following
 * snippet is an example.
 * <pre>
 *     {
 *         "description":"Tomcat7",
 *         "webPort":8080,
 *         "debugPort":8000
 *     }
 * </pre>
 * The following directory structure is required:
 * <pre>
 *     ${runner.docker.dockerfiles_repo}/
 *        JavaWeb/
 *            Tomcat7/
 *                run.dc5y
 *                debug.dc5y
 *                dockerenv.c5y.json
 *            default/
 *                run.dc5y
 *                debug.dc5y
 *                dockerenv.c5y.json
 * </pre>
 * <ul>
 * <li><b>${runner.docker.dockerfiles_repo}</b> - configuration parameter that points to the root directory where docker files for all
 * supported environments are located</li>
 * <li><b>JavaWeb</b> - directory that contains description of environments for running java web application</li>
 * <li><b>Tomcat7</b> - directory that contains description of environment that uses tomcat 7. This directory must contains file
 * <i>run.dc5y</i> and might contain files <i>debug.dc5y</i> and <i>dockerenv.c5y.json</i>. Docker based runner uses a <i>run.dc5y</i> to
 * create a Docker image that contains user's application and instruction how to start it. Docker based runner uses a <i>debug.dc5y</i> to
 * create a Docker image that contains user's application and instruction how to start it under debug. Need to have this file only is
 * support debug for this type of application. File <i>dockerenv.c5y.json</i> contains additional information.</li>
 * </ul>
 * Valid keys and values for the <i>dockerenv.c5y.json</i> file include the following:
 * <ul>
 * <li><b>description</b> - description of environment. It is possible to provide more than one environment for one type of application,
 * e.g. run java application with different application servers. Description helps user recognize specific of the environment.
 * </li>
 * <li><b>webPort</b> - runner uses webPort value to connect the Docker container and route requests from the Internet to the user
 * application. This value may be omitted for other than web application.</li>
 * <li><b>debugPort</b> - runner uses debugPort value when application is running under debugger. This value may be omitted if debug is not
 * supported.</li>
 * </ul>
 * Typically each type of application might have default environment. Description of such environment (<i>run.dc5y</i>, <i>debug.dc5y</i>
 * and <i>dockerenv.c5y.json</i>) must be located in directory <i>default</i>, see example of directory structure above.
 *
 * @author andrew00x
 */
public class DockerEnvironment {
    private String id;
    private String description;
    private int    webPort;
    private int    debugPort;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWebPort() {
        return webPort;
    }

    public void setWebPort(int webPort) {
        this.webPort = webPort;
    }

    public int getDebugPort() {
        return debugPort;
    }

    public void setDebugPort(int debugPort) {
        this.debugPort = debugPort;
    }

    @Override
    public String toString() {
        return "DockerEnvironment{" +
               "id='" + id + '\'' +
               ", description='" + description + '\'' +
               ", webPort=" + webPort +
               ", debugPort=" + debugPort +
               '}';
    }
}
