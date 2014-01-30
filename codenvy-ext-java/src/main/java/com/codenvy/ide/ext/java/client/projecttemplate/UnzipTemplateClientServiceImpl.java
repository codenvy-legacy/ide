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
package com.codenvy.ide.ext.java.client.projecttemplate;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * The implementation of {@link UnzipTemplateClientService}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class UnzipTemplateClientServiceImpl implements UnzipTemplateClientService {
    private static final String BASE_URL                     = "/create-java/" + Utils.getWorkspaceId();
    private static final String UNPACK_MAVEN_JAR_TEMPLATE    = BASE_URL + "/template/maven-jar";
    private static final String UNPACK_MAVEN_WAR_TEMPLATE    = BASE_URL + "/template/maven-war";
    private static final String UNPACK_MAVEN_SPRING_TEMPLATE = BASE_URL + "/template/maven-spring";
    private static final String UNPACK_ANT_JAR_TEMPLATE      = BASE_URL + "/template/ant-jar";
    private static final String UNPACK_ANT_SPRING_TEMPLATE   = BASE_URL + "/template/ant-spring";
    private String           restContext;
    private Loader           loader;
    private ResourceProvider resourceProvider;

    @Inject
    protected UnzipTemplateClientServiceImpl(@Named("restContext") String restContext, Loader loader, ResourceProvider resourceProvider) {
        this.restContext = restContext;
        this.loader = loader;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void unzipMavenJarTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException {
        String requestUrl = restContext + UNPACK_MAVEN_JAR_TEMPLATE;
        String param = "?vfsid=" + resourceProvider.getVfsInfo().getId() + "&name=" + projectName;
        String url = requestUrl + param;
        loader.setMessage("Unpacking from template...");
        AsyncRequest.build(POST, url).loader(loader).send(callback);
    }

    @Override
    public void unzipMavenWarTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException {
        String requestUrl = restContext + UNPACK_MAVEN_WAR_TEMPLATE;
        String param = "?vfsid=" + resourceProvider.getVfsInfo().getId() + "&name=" + projectName;
        String url = requestUrl + param;
        loader.setMessage("Unpacking from template...");
        AsyncRequest.build(POST, url).loader(loader).send(callback);
    }

    @Override
    public void unzipMavenSpringTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException {
        String requestUrl = restContext + UNPACK_MAVEN_SPRING_TEMPLATE;
        String param = "?vfsid=" + resourceProvider.getVfsInfo().getId() + "&name=" + projectName;
        String url = requestUrl + param;
        loader.setMessage("Unpacking from template...");
        AsyncRequest.build(POST, url).loader(loader).send(callback);
    }

    @Override
    public void unzipAntJarTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException {
        String requestUrl = restContext + UNPACK_ANT_JAR_TEMPLATE;
        String param = "?vfsid=" + resourceProvider.getVfsInfo().getId() + "&name=" + projectName;
        String url = requestUrl + param;
        loader.setMessage("Unpacking from template...");
        AsyncRequest.build(POST, url).loader(loader).send(callback);
    }

    @Override
    public void unzipAntSpringTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException {
        String requestUrl = restContext + UNPACK_ANT_SPRING_TEMPLATE;
        String param = "?vfsid=" + resourceProvider.getVfsInfo().getId() + "&name=" + projectName;
        String url = requestUrl + param;
        loader.setMessage("Unpacking from template...");
        AsyncRequest.build(POST, url).loader(loader).send(callback);
    }

}