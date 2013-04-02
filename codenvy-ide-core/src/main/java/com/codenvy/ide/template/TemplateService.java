/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package com.codenvy.ide.template;

import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class TemplateService {

    private static TemplateService instance;

    public static TemplateService getInstance() {
        return instance;
    }

    protected TemplateService() {
        instance = this;
    }

    public abstract void getTemplates(AsyncRequestCallback<List<AbstractTemplate>> callback) throws RequestException;

    public abstract void createTemplate(Template template, AsyncRequestCallback<Object> callback)
            throws RequestException;

    public abstract void deleteTemplate(Template template, AsyncRequestCallback<Object> callback)
            throws RequestException;

    public abstract void addFileTemplate(FileTemplate template, AsyncRequestCallback<FileTemplate> callback)
            throws RequestException;

    public abstract void getFileTemplateList(AsyncRequestCallback<List<FileTemplate>> callback)
            throws RequestException;

    public abstract void deleteFileTemplate(String templateName, AsyncRequestCallback<String> callback)
            throws RequestException;

    public abstract void getProjectTemplateList(AsyncRequestCallback<List<ProjectTemplate>> callback)
            throws RequestException;

    public abstract void deleteProjectTemplate(String templateName, AsyncRequestCallback<String> callback)
            throws RequestException;

    public abstract void createProjectFromTemplate(String vfsId, String parentId, String name, String templateName,
                                                   AsyncRequestCallback<Project> callback) throws RequestException;

    public abstract void deleteTemplatesFromRegistry(AsyncRequestCallback<String> callback) throws RequestException;
}
