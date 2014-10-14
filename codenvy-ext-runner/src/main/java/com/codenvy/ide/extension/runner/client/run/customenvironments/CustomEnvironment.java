/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.extension.runner.client.run.customenvironments;

import com.google.gwt.http.client.URL;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents custom environment.
 *
 * @author Artem Zatsarynnyy
 */
public class CustomEnvironment {
    private static final String DOCKERFILE_TEMPLATE = "# Base image.  You can pull from Docker Hub.  Codenvy\n" +
                                                      "# provides a series of tested base images that include \n" +
                                                      "# Web Shell, installed utilities, and language support.\n" +
                                                      "# You can browse our images in Docker Hub or at\n" +
                                                      "# github.com/codenvy/dockerfiles. The shellinabox image\n" +
                                                      "# provides core Linux utilities and terminal access to runner.\n" +
                                                      "FROM codenvy/shellinabox\n" +
                                                      "\n" +
                                                      "# Codenvy uses this port to map IDE clients to the output of\n" +
                                                      "# your application executing within the Runner. Set these\n" +
                                                      "# values to the port of your application and Codenvy will\n" +
                                                      "# map this port to the output within the browser, CLI, and API.\n" +
                                                      "# You can set this value multiple times.\n" +
                                                      "# For example:\n" +
                                                      "# ENV CODENVY_APP_PORT_8080_HTTP 8080\n" +
                                                      "#\n" +
                                                      "# ENV CODENVY_APP_PORT_<port>_HTTP <port>\n" +
                                                      "\n" +
                                                      "# Codenvy uses this port to map IDE clients to the debugger\n" +
                                                      "# of your application within the Runner. Set these\n" +
                                                      "# values to the port of your debugger and Codenvy will\n" +
                                                      "# map this port to the debugger console in the browser.\n" +
                                                      "# You can set this value multiple times.\n" +
                                                      "# For example:\n" +
                                                      "# ENV CODENVY_APP_PORT_8000_DEBUG 8000\n" +
                                                      "#\n" +
                                                      "# ENV CODENVY_APP_PORT_<port>_DEBUG <port>\n" +
                                                      "\n" +
                                                      "# Set this value to the port of any terminals operating\n" +
                                                      "# within your runner.  If you inherit a base image from\n" +
                                                      "# codenvy/shellinabox (or any of our images that inherit\n" +
                                                      "# from it, you do not need to set this value.  We already \n" +
                                                      "# set it for you.\n" +
                                                      "# ENV CODENVY_WEB_SHELL_PORT <port>\n" +
                                                      "\n" +
                                                      "# Execute your custom commands here.  You can add\n" +
                                                      "# as many RUN commands as you want.  Combining\n" +
                                                      "# RUN commands into a single entry will cause your \n" +
                                                      "# environment to load faster.  Also, building your image\n" +
                                                      "# with docker offline and uploading it to Docker Hub\n" +
                                                      "# as a pre-built base image will also cause it to load\n" +
                                                      "# Faster.  This example installs python, curl, and the\n" +
                                                      "# Google SDK as an example.\n" +
                                                      "# RUN sudo apt-get update -y && \\\n" +
                                                      "#     sudo apt-get install --no-install-recommends -y -q curl build-essential python3 python3-dev python-pip git python3-pip && \\\n" +
                                                      "#     sudo pip3 install -U pip && \\\n" +
                                                      "#     sudo pip3 install virtualenv && \\\n" +
                                                      "#     sudo mkdir /opt/googlesdk && \\\n" +
                                                      "#     wget -qO- \"https://dl.google.com/dl/cloudsdk/release/google-cloud-sdk.tar.gz\" | sudo tar -zx -C /opt/googlesdk && \\\n" +
                                                      "#     sudo /bin/sh -c \"/opt/googlesdk/google-cloud-sdk/install.sh\" && \\\n" +
                                                      "#     sudo chmod +x /opt/googlesdk/google-cloud-sdk/bin/gcloud\n" +
                                                      "\n" +
                                                      "\n" +
                                                      "# Include this as the CMD instruction in your Dockerfile if\n" +
                                                      "# you'd like the runner to stay alive after your commands\n" +
                                                      "# have finished executing. Keeping the runner alive is\n" +
                                                      "# necessary if you'd like to terminal into the image.  If \n" +
                                                      "# your Dockerfile launches a server or daemon, like Tomcat,\n" +
                                                      "# you do not need to set this value as Docker will not\n" +
                                                      "# terminate until that process has finished.\n" +
                                                      "CMD while true;do true; done" +
                                                      "\n";
    private String name;

    /** Create new environment with the specified {@code name}. */
    public CustomEnvironment(String name) {
        this.name = name;
    }

    /** Get environment's name. */
    public String getName() {
        return name;
    }

    /**
     * Returns paths of script files. Paths are relative to the custom environments folder.
     *
     * @param encode
     *         if <code>true</code> - script names where all characters that are not valid for an URL will be escaped
     * @see com.codenvy.ide.extension.runner.client.inject.RunnerGinModule#provideEnvironmentsFolderRelPath()
     */
    public List<String> getScriptNames(boolean encode) {
        final String dockerScriptName = "/Dockerfile";

        List<String> list = new LinkedList<>();
        list.add((encode ? URL.encodePathSegment(name) : name) + dockerScriptName);
        return list;
    }

    public String getDockerfileTemplate() {
        return DOCKERFILE_TEMPLATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomEnvironment that = (CustomEnvironment)o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
