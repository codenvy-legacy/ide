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
