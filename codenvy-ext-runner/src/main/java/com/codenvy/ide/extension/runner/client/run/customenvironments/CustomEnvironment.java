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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents custom environment.
 *
 * @author Artem Zatsarynnyy
 */
public class CustomEnvironment {
    private static final String DOCKERFILE_TEMPLATE = "# Base image\n" +
                                                      "FROM <basic image>\n" +
                                                      "\n" +
                                                      "# Set this if you want that of port exposed in this Dockerfile or its parent\n" +
                                                      "# should be treated as port where your application is running\n" +
                                                      "# For example:\n" +
                                                      "# ENV CODENVY_APP_PORT_8080_HTTP 8080\n" +
                                                      "#\n" +
                                                      "#ENV CODENVY_APP_PORT_<port>_HTTP <port>\n" +
                                                      "\n" +
                                                      "# Set this if you want that of port exposed in this Dockerfile or its parent\n" +
                                                      "# should be treated as debug port of yours application\n" +
                                                      "# For example:\n" +
                                                      "# ENV CODENVY_APP_PORT_8000_DEBUG 8000\n" +
                                                      "#\n" +
                                                      "#ENV CODENVY_APP_PORT_<port>_DEBUG <port>" +
                                                      "#\n";
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
        final String mapperFileName = "/Mapper.json";

        List<String> list = new ArrayList<>(2);
        list.add((encode ? URL.encodePathSegment(name) : name) + dockerScriptName);
        list.add((encode ? URL.encodePathSegment(name) : name) + mapperFileName);
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
