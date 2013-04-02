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
import com.google.gwt.user.client.Random;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.*;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class TemplateServiceImpl extends TemplateService {

    private final class DefaultFileTemplates {
        public static final String EMPTY_XML = "Empty XML";

        public static final String EMPTY_XML_DESCRIPTION = "Create empty XML file.";

        public static final String EMPTY_HTML = "Empty HTML";

        public static final String EMPTY_HTML_DESCRIPTION = "Create empty HTML file.";

        public static final String EMPTY_TEXT = "Empty TEXT";

        public static final String EMPTY_TEXT_DESCRIPTION = "Create empty TEXT file.";

        public static final String OPENSOCIAL_GADGET = "OpenSocial Gadget";

        public static final String OPENSOCIAL_GADGET_DESCRIPTION = "Sample of OpenSocial Gadget";

        public static final String GROOVY_REST_SERVICE = "Groovy REST Service";

        public static final String GROOVY_REST_SERVICE_DESCRIPTION = "Sample of Groovy REST service.";

        public static final String GROOVY_TEMPLATE = "Template";

        public static final String GROOVY_TEMPLATE_DESCRIPTION = "Sample of Template.";
    }

    private static final String CONTEXT = "/templates";

    private static final String TEMPLATE = "template-";

    /* Fields */
    private String registryContext;

    private String restContext;

    private Loader loader;

    public TemplateServiceImpl(Loader loader, String registryContext, String restContext) {
        this.loader = loader;
        this.registryContext = registryContext;
        this.restContext = restContext;
    }

    @Override
    public void createTemplate(Template template, AsyncRequestCallback<Object> callback) throws RequestException {
        String url = registryContext + CONTEXT + "/" + TEMPLATE + System.currentTimeMillis() + "/?createIfNotExist=true";

        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(template)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(data).send(callback);
    }

    @Override
    public void deleteTemplate(Template template, AsyncRequestCallback<Object> callback) throws RequestException {
        String url = registryContext + CONTEXT + "/" + template.getNodeName();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader)
                    .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).send(callback);
    }

    @Override
    public void getTemplates(AsyncRequestCallback<List<AbstractTemplate>> callback) throws RequestException {
        String url = registryContext + CONTEXT + "/?noCache=" + Random.nextInt();
        int[] acceptStatus = new int[]{HTTPStatus.OK, HTTPStatus.NOT_FOUND};
        callback.setSuccessCodes(acceptStatus);
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.client.framework.template.TemplateService#addFileTemplate(org.exoplatform.ide.client.framework.template
     * .AbstractTemplate,
     *      org.exoplatform.ide.client.framework.template.TemplateCreatedCallback)
     */
    @Override
    public void addFileTemplate(FileTemplate template, AsyncRequestCallback<FileTemplate> callback)
            throws RequestException {
        String url = restContext + "/ide/templates/file/add";

        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(template)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "PUT")
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** @see org.exoplatform.ide.client.framework.template.TemplateService#getFileTemplateList(org.exoplatform.gwtframework.commons.rest
     * .AsyncRequestCallback) */
    @Override
    public void getFileTemplateList(AsyncRequestCallback<List<FileTemplate>> callback) throws RequestException {
        String url = restContext + "/ide/templates/file/list";
        callback.getPayload().addAll(getDefaultFileTemplates());

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.client.framework.template.TemplateService#deleteFileTemplate(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void deleteFileTemplate(String templateName, AsyncRequestCallback<String> callback) throws RequestException {
        String url = restContext + "/ide/templates/file/delete";
        String param = "name=" + URL.encodePathSegment(templateName);
        AsyncRequest.build(RequestBuilder.POST, url + "?" + param).loader(loader).send(callback);
    }

    /** @see org.exoplatform.ide.client.framework.template.TemplateService#getProjectTemplateList(org.exoplatform.gwtframework.commons
     * .rest.AsyncRequestCallback) */
    @Override
    public void getProjectTemplateList(AsyncRequestCallback<List<ProjectTemplate>> callback) throws RequestException {
        String url = restContext + "/ide/templates/project/list";

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.client.framework.template.TemplateService#deleteProjectTemplate(java.lang.String,
     *      org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback)
     */
    @Override
    public void deleteProjectTemplate(String templateName, AsyncRequestCallback<String> callback)
            throws RequestException {
        String url = restContext + "/ide/templates/project/delete";
        String param = "name=" + URL.encodePathSegment(templateName);
        AsyncRequest.build(RequestBuilder.POST, url + "?" + param).loader(loader).send(callback);
    }

    /** @see org.exoplatform.ide.client.framework.template.TemplateService#deleteTemplatesFromRegistry(org.exoplatform.gwtframework
     * .commons.rest.AsyncRequestCallback) */
    @Override
    public void deleteTemplatesFromRegistry(AsyncRequestCallback<String> callback) throws RequestException {
        String url = registryContext + CONTEXT;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, "DELETE")
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).send(callback);
    }

    // ----Implementation-------------------

    private List<FileTemplateImpl> getDefaultFileTemplates() {
        List<FileTemplateImpl> fileTemplates = new ArrayList<FileTemplateImpl>();
        fileTemplates.add(new FileTemplateImpl(MimeType.TEXT_XML, DefaultFileTemplates.EMPTY_XML,
                                               DefaultFileTemplates.EMPTY_XML_DESCRIPTION, FileTemplates.getTemplateFor(MimeType.TEXT_XML),
                                               true));

        fileTemplates.add(new FileTemplateImpl(MimeType.TEXT_HTML, DefaultFileTemplates.EMPTY_HTML,
                                               DefaultFileTemplates.EMPTY_HTML_DESCRIPTION,
                                               FileTemplates.getTemplateFor(MimeType.TEXT_HTML), true));

        fileTemplates.add(new FileTemplateImpl(MimeType.TEXT_PLAIN, DefaultFileTemplates.EMPTY_TEXT,
                                               DefaultFileTemplates.EMPTY_TEXT_DESCRIPTION,
                                               FileTemplates.getTemplateFor(MimeType.TEXT_PLAIN), true));

        fileTemplates
                .add(new FileTemplateImpl(MimeType.GOOGLE_GADGET, DefaultFileTemplates.OPENSOCIAL_GADGET,
                                          DefaultFileTemplates.OPENSOCIAL_GADGET_DESCRIPTION,
                                          FileTemplates.getTemplateFor(MimeType.GOOGLE_GADGET),
                                          true));

        fileTemplates.add(new FileTemplateImpl(MimeType.GROOVY_SERVICE, DefaultFileTemplates.GROOVY_REST_SERVICE,
                                               DefaultFileTemplates.GROOVY_REST_SERVICE_DESCRIPTION,
                                               FileTemplates.getTemplateFor(MimeType.GROOVY_SERVICE),
                                               true));

        fileTemplates
                .add(new FileTemplateImpl(MimeType.GROOVY_TEMPLATE, DefaultFileTemplates.GROOVY_TEMPLATE,
                                          DefaultFileTemplates.GROOVY_TEMPLATE_DESCRIPTION,
                                          FileTemplates.getTemplateFor(MimeType.GROOVY_TEMPLATE),
                                          true));

        return fileTemplates;
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
        String url = restContext + "/ide/templates/project/create";
        url += "?vfsid=" + vfsId;
        url += "&name=" + name;
        url += "&parentId=" + parentId;
        url += "&templateName=" + templateName;
        url = URL.encode(url);
        AsyncRequest.build(RequestBuilder.POST, url).send(callback);
    }
}
