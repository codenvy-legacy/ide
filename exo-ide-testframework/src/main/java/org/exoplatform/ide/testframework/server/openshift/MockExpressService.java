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
package org.exoplatform.ide.testframework.server.openshift;

import org.exoplatform.ide.testframework.server.FSLocation;
import org.exoplatform.ide.testframework.server.git.MockGitRepoService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.*;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("{ws-name}/openshift/express")
public class MockExpressService {

    /** Created Namespace */
    private static String ns = "";

    /**
     *
     */
    private static Map<String, AppInfo> applicationsByWorkDir = new HashMap<String, AppInfo>();

    /**
     *
     */
    private static Map<String, AppInfo> applicationsByName = new HashMap<String, AppInfo>();

    /** Working directories key - application's name value - working directory */
    private static Map<String, String> workingDirectories = new HashMap<String, String>();

    /**
     * Creates a new Domain
     *
     * @param namespace
     * @param alter
     * @throws ExpressException
     * @throws Exception
     */
    @POST
    @Path("domain/create")
    public void createDomain(@QueryParam("namespace") String namespace, @QueryParam("alter") boolean alter)
            throws ExpressException, Exception {
        ns = namespace;
    }

    /**
     * Creates a new Application
     *
     * @param applicationName
     * @param type
     * @param workDir
     * @param uriInfo
     * @return
     * @throws ExpressException
     * @throws Exception
     */
    @POST
    @Path("apps/create")
    @Produces(MediaType.APPLICATION_JSON)
    public AppInfo createApplication(@QueryParam("app") String applicationName, @QueryParam("type") String type,
                                     @QueryParam("workdir") FSLocation workDir, @Context UriInfo uriInfo)
            throws ExpressException, Exception {
        assert applicationName != null;
        assert type != null;

        AppInfo appInfo = new AppInfo();
        appInfo.setName(applicationName);
        appInfo.setPublicUrl("http://" + applicationName + "-" + ns + ".rhcloud.com/");
        appInfo.setType(type);
        appInfo.setGitUrl("ssh://04fc0584021b4a9da8d95cd6383b12e4@" + applicationName + "-" + ns + ".rhcloud.com/~/git/"
                          + applicationName + ".git/");

        String workingDirectory = workDir.getURL();
        if (workingDirectory.endsWith("/")) {
            workingDirectory = workingDirectory.substring(0, workingDirectory.length() - 1);
        }

        applicationsByWorkDir.put(workingDirectory, appInfo);
        applicationsByName.put(applicationName, appInfo);
        workingDirectories.put(applicationName, workingDirectory);

        if (MockGitRepoService.getInstance() != null) {
            MockGitRepoService.getInstance().addGitDirectory(workingDirectory);
        }

        return appInfo;
    }

    @GET
    @Path("apps/info")
    @Produces(MediaType.APPLICATION_JSON)
    public AppInfo applicationInfo(@QueryParam("app") String app, @QueryParam("workdir") FSLocation workDir,
                                   @Context UriInfo uriInfo) throws Exception {
        if (app != null && !app.isEmpty() && applicationsByName.containsKey(app)) {
            return applicationsByName.get(app);
        }

        return getAppByWorkDir(workDir.getURL());
    }

    /**
     * Search Application by URL
     *
     * @param url
     * @return
     * @throws Exception
     */
    private AppInfo getAppByWorkDir(String url) throws Exception {
        AppInfo appInfo = applicationsByWorkDir.get(url);
        if (appInfo != null) {
            return appInfo;
        }

        if (url.indexOf("/") <= 0) {
            throw new Exception("Application for URL " + url + " not found.");
        }

        url = url.substring(0, url.lastIndexOf("/"));
        return getAppByWorkDir(url);
    }

    @GET
    @Path("apps/type")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<String> applicationTypes() throws Exception {
        Set<String> APP_TYPES = new HashSet<String>(Arrays.asList( //
                                                                   "php-5.3", //
                                                                   // "wsgi-3.2.1", //
                                                                   "rack-1.1") //
        );
        return APP_TYPES;
    }

    @GET
    @Path("user/info")
    @Produces(MediaType.APPLICATION_JSON)
    public RHUserInfo userInfo(@QueryParam("appsinfo") boolean appsInfo) throws ExpressException, Exception {
        RHUserInfo userInfo = new RHUserInfo();

        userInfo.setRhlogin("an4ous@bigmir.net");
        userInfo.setUuid("test-UUID");
        userInfo.setRhcDomain("rhcloud.com");
        userInfo.setNamespace(ns);

        userInfo.setApps(new ArrayList<AppInfo>(applicationsByWorkDir.values()));
        return userInfo;
    }

    @POST
    @Path("apps/destroy")
    public void destroyApplication(@QueryParam("app") String app, @QueryParam("workdir") FSLocation workDir,
                                   @Context UriInfo uriInfo) throws Exception {
        AppInfo appToDelete = null;
        if (app != null && !app.isEmpty() && applicationsByName.containsKey(app)) {
            appToDelete = applicationsByName.get(app);
        } else {
            appToDelete = getAppByWorkDir(workDir.getURL());
        }

        String workingDirectory = workingDirectories.get(appToDelete.getName());

        applicationsByWorkDir.remove(workingDirectory);
        applicationsByName.remove(appToDelete.getName());
        workingDirectories.remove(appToDelete.getName());
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials) throws ExpressException, Exception {
        Iterator<String> keyIter = credentials.keySet().iterator();
        while (keyIter.hasNext()) {
            String k = keyIter.next();
            String v = credentials.get(k);
        }
    }

    @POST
    @Path("reset")
    public void resetMockExpressService() {
        ns = "";
        applicationsByWorkDir = new HashMap<String, AppInfo>();
        applicationsByName = new HashMap<String, AppInfo>();
        workingDirectories = new HashMap<String, String>();
    }

}
