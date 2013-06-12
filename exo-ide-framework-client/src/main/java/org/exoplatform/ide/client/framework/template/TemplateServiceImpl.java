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
package org.exoplatform.ide.client.framework.template;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class TemplateServiceImpl extends TemplateService {

    private String restContext;

    private Loader loader;

    public TemplateServiceImpl(Loader loader) {
        this.loader = loader;
        this.restContext = Utils.getRestContext();
    }



    /** @see org.exoplatform.ide.client.framework.template.TemplateService#getProjectTemplateList(org.exoplatform.gwtframework.commons
     * .rest.AsyncRequestCallback) */
    @Override
    public void getProjectTemplateList(AsyncRequestCallback<List<ProjectTemplate>> callback) throws RequestException {
        String url = restContext + Utils.getWorkspaceName() + "/templates/project/list";

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @throws RequestException
     * @see org.exoplatform.ide.client.framework.template.TemplateService#createProjectFromTemplate(java.lang.String,
     *      java.lang.String, org.exoplatform.ide.client.framework.template.ProjectTemplateImpl,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void createProjectFromTemplate(String vfsId, String parentId, String name, String templateName,
                                          AsyncRequestCallback<ProjectModel> callback) throws RequestException {
        String url = restContext + Utils.getWorkspaceName() + "/templates/project/create";
        url += "?vfsid=" + vfsId;
        url += "&name=" + name;
        url += "&parentId=" + parentId;
        url += "&templateName=" + templateName;
        url = URL.encode(url);
        AsyncRequest.build(RequestBuilder.POST, url).send(callback);
    }
}
