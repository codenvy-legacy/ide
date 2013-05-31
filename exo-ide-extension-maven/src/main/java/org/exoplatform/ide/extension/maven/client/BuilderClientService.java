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
package org.exoplatform.ide.extension.maven.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;

/**
 * Client service for Maven builder.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderClientService.java Feb 17, 2012 12:36:01 PM azatsarynnyy $
 */
public abstract class BuilderClientService {

    /** Maven builder client service. */
    private static BuilderClientService instance;

    /**
     * Return the instance of {@link BuilderClientService}.
     * 
     * @return maven builder client service
     */
    public static BuilderClientService getInstance() {
        return instance;
    }

    protected BuilderClientService() {
        instance = this;
    }

    /**
     * Start new build.
     * 
     * @param projectId identifier of the project we want to send for build
     * @param vfsId identifier of the virtual file system
     * @param callback callback
     * @throws RequestException
     */
    public abstract void build(String projectId, String vfsId, String projectName, String projectType,
                               AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Start new build and publish.
     * 
     * @param projectId identifier of project we want to send for build
     * @param vfsId identifier of virtual file system
     * @param callback callback
     * @throws RequestException
     */
    public abstract void buildAndPublish(String projectId, String vfsId, String projectName, String projectType,
                                         AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Cancel previously launched build.
     * 
     * @param buildid ID of build
     * @param callback callback
     * @param projectName project name
     * @param projectType project type
     * @throws RequestException
     */
    public abstract void cancel(String buildid, String projectName, String projectType, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Check current status of previously launched build.
     * 
     * @param buildid identifier of build
     * @param callback callback
     * @throws RequestException
     */
    public abstract void status(String buildid, AsyncRequestCallback<BuildStatus> callback) throws RequestException;

    /**
     * Get build log.
     * 
     * @param buildid identifier of build
     * @param callback callback
     * @throws RequestException
     */
    public abstract void log(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Get build result.
     * 
     * @param buildid ID of build
     * @param callback callback
     * @throws RequestException
     */
    public abstract void result(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Check is URL for download artifact is valid.
     * 
     * @param url URL for checking
     * @param callback callback
     * @throws RequestException
     */
    public abstract void checkArtifactUrl(String url, AsyncRequestCallback<Object> callback) throws RequestException;
}
