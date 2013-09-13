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


    /**
     * @see org.exoplatform.ide.client.framework.template.TemplateService#getProjectTemplateList(org.exoplatform.gwtframework.commons
     *      .rest.AsyncRequestCallback)
     */
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
