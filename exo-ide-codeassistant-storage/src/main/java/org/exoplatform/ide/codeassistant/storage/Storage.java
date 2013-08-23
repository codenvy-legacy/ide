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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;

/**
 * REST interface for {@link CodeAssistantStorage}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("storage/get")
public class Storage {

    @Inject
    private CodeAssistantStorage storage;

    @POST
    @Path("/annotations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShortTypeInfo> getAnnotation(@QueryParam("prefix") String prefix, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getAnnotations(prefix, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    @POST
    @Path("/classes")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShortTypeInfo> getClasses(@QueryParam("prefix") String prefix, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getClasses(prefix, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    @POST
    @Path("/interfaces")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShortTypeInfo> getInterfaces(@QueryParam("prefix") String prefix, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getInterfaces(prefix, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    @POST
    @Path("/type-by-fqn")
    @Produces(MediaType.APPLICATION_JSON)
    public TypeInfo getTypeByFqn(@QueryParam("fqn") String fqn, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getTypeByFqn(fqn, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    @POST
    @Path("/type-by-fqn-prefix")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShortTypeInfo> getTypeByFqnPrefix(@QueryParam("prefix") String fqnPrefix, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getTypesByFqnPrefix(fqnPrefix, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    @POST
    @Path("/type-by-name-prefix")
    @Produces(MediaType.APPLICATION_JSON)
    public List<ShortTypeInfo> getTypeByNamePrefix(@QueryParam("prefix") String namePrefix, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getTypesByNamePrefix(namePrefix, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    @POST
    @Path("/types-info-by-name-prefix")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TypeInfo> getTypesInfoByNamePrefix(@QueryParam("prefix") String namePrefix, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getTypesInfoByNamePrefix(namePrefix, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    @POST
    @Path("/class-doc")
    @Produces(MediaType.TEXT_PLAIN)
    public String getClassDoc(@QueryParam("fqn") String fqn, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getClassJavaDoc(fqn, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    @POST
    @Path("/member-doc")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMemeberDoc(@QueryParam("fqn") String fqn, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getMemberJavaDoc(fqn, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    /** Get list of package names */
    @POST
    @Path("/find-packages")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getPackages(@QueryParam("package") String packagePrefix, Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getPackages(packagePrefix, dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

    /** Get list of all package names in dependencys */
    @POST
    @Path("/get-packages")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAllPackages(Set<String> dependencys) {
        if (dependencys == null)
            return null;
        try {
            return storage.getAllPackages(dependencys);
        } catch (CodeAssistantException e) {
            return null;
        }
    }

}
