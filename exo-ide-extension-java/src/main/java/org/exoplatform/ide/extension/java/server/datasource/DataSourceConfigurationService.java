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

package org.exoplatform.ide.extension.java.server.datasource;

import com.codenvy.commons.json.JsonHelper;

import org.exoplatform.ide.extension.java.shared.DataSourceOptions;
import org.exoplatform.ide.extension.java.shared.MavenDependency;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
@Path("{ws-name}/data-source/java")
public class DataSourceConfigurationService {
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @GET
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public DataSourceOptions newConfiguration(@QueryParam("vfsid") String vfsId,
                                              @QueryParam("projectid") String projectId) throws Exception {
        final DataSourceConfiguration cfg =
                new TomcatDataSourceConfiguration(vfsRegistry.getProvider(vfsId).newInstance(null, null), projectId);
        return cfg.newConfiguration();
    }

    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DataSourceOptions newConfiguration(@QueryParam("vfsid") String vfsId,
                                              @QueryParam("projectid") String projectId,
                                              String dependency) throws Exception {
        final DataSourceConfiguration cfg =
                new TomcatDataSourceConfiguration(vfsRegistry.getProvider(vfsId).newInstance(null, null), projectId);
        cfg.addProjectDependency(JsonHelper.fromJson(dependency, MavenDependency.class, null));
        return cfg.newConfiguration();
    }

    @POST
    @Path("configure")
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveAll(@QueryParam("vfsid") String vfsId,
                        @QueryParam("projectid") String projectId,
                        List<DataSourceOptions> dataSourceOptions) throws Exception {
        final DataSourceConfiguration cfg =
                new TomcatDataSourceConfiguration(vfsRegistry.getProvider(vfsId).newInstance(null, null), projectId);
        cfg.configureDataSources(dataSourceOptions);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DataSourceOptions> getAll(@QueryParam("vfsid") String vfsId,
                                          @QueryParam("projectid") String projectId) throws Exception {
        final DataSourceConfiguration cfg =
                new TomcatDataSourceConfiguration(vfsRegistry.getProvider(vfsId).newInstance(null, null), projectId);
        return cfg.getAllDataSources();
    }
}
