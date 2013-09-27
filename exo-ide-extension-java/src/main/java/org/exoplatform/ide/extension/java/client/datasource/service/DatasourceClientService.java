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
package org.exoplatform.ide.extension.java.client.datasource.service;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.java.shared.DataSourceOptions;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class DatasourceClientService {
    
    private static final String URL_GET_ALL = "/data-source/java";
    
    private static final String URL_NEW_CONFIGURATION = "/data-source/java/create";

    private static final String URL_SAVE_ALL = "/data-source/java/configure";

    private static DatasourceClientService instance;
    
    public static DatasourceClientService getInstance() {
        return instance;
    }
    
    private String restContext;
    
    public DatasourceClientService(String restContext) {
        instance = this;        
        if (!restContext.endsWith("/")) {
            restContext += "/";
        }
        this.restContext = restContext + Utils.getWorkspaceName();
    }

    public void getAll(String vfsId, String projectId, final AsyncCallback<List<DataSourceOptions>> callback) {
        try {
            String params = "vfsid=" + vfsId + "&projectid=" + projectId;
            String url = restContext + URL_GET_ALL + "?" + params;

            AsyncRequest.build(RequestBuilder.GET, url).loader(IDELoader.get())
                .send(new AsyncRequestCallback<List<DataSourceOptions>>(new DatasourceListUnmarshaller()) {
                    @Override
                    protected void onSuccess(List<DataSourceOptions> datasourceList) {
                        callback.onSuccess(datasourceList);
                    }

                    @Override
                    protected void onFailure(Throwable e) {
                        callback.onFailure(e);
                    }
                });
        } catch (Exception e) {
            callback.onFailure(e);
        }        
    }    
    
    public void newConfiguration(String vfsId, String projectId, final AsyncCallback<DataSourceOptions> callback) {
        try {
            String params = "vfsid=" + vfsId + "&projectid=" + projectId;
            String url = restContext + URL_NEW_CONFIGURATION + "?" + params;

            AsyncRequest.build(RequestBuilder.GET, url).loader(IDELoader.get())
                .send(new AsyncRequestCallback<DataSourceOptions>(new DatasourceUnmarshaller()) {
                    @Override
                    protected void onSuccess(DataSourceOptions datasource) {
                        callback.onSuccess(datasource);
                    }

                    @Override
                    protected void onFailure(Throwable e) {
                        callback.onFailure(e);
                    }
                });
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }
    
    public void saveAll(String vfsId, String projectId, final List<DataSourceOptions> datasourceList, final AsyncCallback<List<DataSourceOptions>> callback) {
        try {
            String params = "vfsid=" + vfsId + "&projectid=" + projectId;
            String url = restContext + URL_SAVE_ALL + "?" + params;
            
            AsyncRequest.build(RequestBuilder.POST, url).loader(IDELoader.get())
                .data(new DatasourceListMarshaller(datasourceList).marshal())
                .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                .send(new AsyncRequestCallback<String>() {
                    @Override
                    protected void onSuccess(String result) {
                        callback.onSuccess(datasourceList);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
        } catch (Exception e) {
            callback.onFailure(e);
        }        
    }
    
}
