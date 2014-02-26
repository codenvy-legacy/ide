/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.server;

import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.codenvy.ide.ext.java.server.internal.core.search.matching.JavaSearchNameEnvironment;
import com.codenvy.vfs.impl.fs.FSMountPoint;
import com.codenvy.vfs.impl.fs.VirtualFileImpl;

import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Rest service for {@link com.codenvy.ide.ext.java.worker.WorkerNameEnvironment}
 *
 * @author Evgen Vidolob
 */
@Path("java-name-environment/{ws-id}")
public class RestNameEnvironment {
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(RestNameEnvironment.class);
    @PathParam("ws-id")
    @Inject
    private String wsId;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    private FSMountPoint getMountPoint() throws VirtualFileSystemException {
        MountPoint mountPoint = vfsRegistry.getProvider(wsId).getMountPoint(true);
        if(mountPoint instanceof FSMountPoint){
            return  (FSMountPoint)mountPoint;
        } else throw new IllegalStateException("This service works only with FSMountPoint class");
    }

    @GET
    @Produces("application/json")
    @Path("findTypeCompound")
    public String findTypeCompound(@QueryParam("compoundTypeName") String compoundTypeName, @QueryParam("projectid") String projectId)
            throws VirtualFileSystemException {
        VirtualFileImpl project = getMountPoint().getVirtualFileById(projectId);
        JavaProject javaProject = new JavaProject(project);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);

        NameEnvironmentAnswer answer = environment.findType(getCharArrayFrom(compoundTypeName));
        return processAnswer(answer);
    }


    @GET
    @Produces("application/json")
    @Path("findType")
    public String findType(@QueryParam("typename") String typeName, @QueryParam("packagename") String packageName,
                           @QueryParam("projectid") String projectId)
            throws VirtualFileSystemException {
        VirtualFileImpl project = getMountPoint().getVirtualFileById(projectId);
        JavaProject javaProject = new JavaProject(project);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);

        NameEnvironmentAnswer answer = environment.findType(typeName.toCharArray(), getCharArrayFrom(packageName));
        return processAnswer(answer);
    }

    @GET
    @Path("package")
    @Produces("text/plain")
    public String isPackage(@QueryParam("packagename") String packageName, @QueryParam("parent") String parentPackageName,
                             @QueryParam("projectid") String projectId)
            throws VirtualFileSystemException {
        VirtualFileImpl project = getMountPoint().getVirtualFileById(projectId);
        JavaProject javaProject = new JavaProject(project);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);
        return String.valueOf(environment.isPackage(getCharArrayFrom(parentPackageName), packageName.toCharArray()));
    }


    private String processAnswer(NameEnvironmentAnswer answer) {
        if(answer == null) return null;
        if (answer.isBinaryType()) {
            IBinaryType binaryType = answer.getBinaryType();
            return JsonUtil.toJsonBinaryType(binaryType);
        }
//        else if (answer.isCompilationUnit()) {
//
//        }
        return null;
    }

    private char[][] getCharArrayFrom(String list) {
        String[] strings = list.split(",");
        char[][] arr = new char[strings.length][];
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            arr[i] = s.toCharArray();
        }
        return arr;
    }

}
