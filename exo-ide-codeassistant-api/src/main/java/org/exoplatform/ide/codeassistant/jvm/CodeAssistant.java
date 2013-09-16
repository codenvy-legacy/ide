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
package org.exoplatform.ide.codeassistant.jvm;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.bean.Dependency;
import org.exoplatform.ide.codeassistant.jvm.bean.TypeInfoBean;
import org.exoplatform.ide.codeassistant.jvm.shared.JavaType;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.ProjectImpl;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * Base class for JVM based programming languages(Java, Groovy) codeassitant.
 * Class contains some basic methods for using
 * {@link CodeAssistantStorage}. All abstract methods is languages and project specific(Java, Groovy).
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistantStorage Feb 8, 2011 2:33:41 PM evgen $
 */
public abstract class CodeAssistant {

    protected CodeAssistantStorageClient storage;

    protected VirtualFileSystemRegistry vfsRegistry;

    /** @param storage */
    public CodeAssistant(CodeAssistantStorageClient storage, VirtualFileSystemRegistry vfsRegistry) {
        this.storage = storage;
        this.vfsRegistry = vfsRegistry;
    }

    /**
     * Find all classes or annotations or interfaces
     *
     * @param type
     *         the string that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION)
     * @param prefix
     *         optional parameter that matching first letter of type name
     * @return Returns list of FQNs matched to class type
     * @throws CodeAssistantException
     */
    public List<ShortTypeInfo> getByType(JavaType type, String prefix, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
        List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>();
        Set<String> dependencys = getProjectDependencys(projectId, vfsId);
        switch (type) {
            case INTERFACE:
                List<ShortTypeInfo> intefaces = storage.getInterfaces(prefix, dependencys);
                if (intefaces != null) {
                    result.addAll(intefaces);
                }
                break;
            case ANNOTATION:
                List<ShortTypeInfo> annotations = storage.getAnnotations(prefix, dependencys);
                if (annotations != null) {
                    result.addAll(annotations);
                }
                break;
            case CLASS:
            case ENUM:
                List<ShortTypeInfo> classes = storage.getClasses(prefix, dependencys);
                if (classes != null) {
                    result.addAll(classes);
                }
                break;
            default:
                break;
        }
        try {
            List<ShortTypeInfo> tmp = getByTypeFromProject(type, prefix, projectId, vfsId);
            if (tmp != null) {
                result.addAll(tmp);
            }
        } catch (ItemNotFoundException e) {
            // nothing to do
        }
        return result;
    }

    /**
     * @param projectId
     * @return
     * @throws VirtualFileSystemException
     * @throws CodeAssistantException
     */
    protected Set<String> getProjectDependencys(String projectId, String vfsId) throws VirtualFileSystemException,
                                                                                       CodeAssistantException {
        if (projectId == null) {
            return Collections.emptySet();
        }
        Set<String> set = new HashSet<String>();
        ProjectImpl project = getProject(projectId, vfsRegistry.getProvider(vfsId).newInstance(null, null));
        return getProjectDependencys(project);
    }


    public Set<String> getProjectDependencys(Project project) throws CodeAssistantException {
        Set<String> set = new HashSet<String>();
        //add rt.jar as dependency
        set.add("java:rt:1.6:jar");
        if (project.hasProperty("exoide:classpath")) {
            String classpath = (String)project.getPropertyValue("exoide:classpath");
            JsonParser parser = new JsonParser();
            try {
                parser.parse(new ByteArrayInputStream(classpath.getBytes()));
                Dependency[] dependencys =
                        (Dependency[])ObjectBuilder.createArray(Dependency[].class, parser.getJsonObject());
                for (Dependency d : dependencys)
                    set.add(d.toString());
            } catch (JsonException e) {

                throw new CodeAssistantException(500, "Can't parse dependencys for project: " + project.getPath());
            }
        }
        return set;
    }

    /**
     * @param projectId
     * @param vfs
     * @return
     * @throws ItemNotFoundException
     * @throws PermissionDeniedException
     * @throws VirtualFileSystemException
     * @throws CodeAssistantException
     */
    protected ProjectImpl getProject(String projectId, VirtualFileSystem vfs) throws ItemNotFoundException,
                                                                                     PermissionDeniedException, VirtualFileSystemException,
                                                                                     CodeAssistantException {
        Item item = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
        ProjectImpl project = null;
        if (item instanceof ProjectImpl)
            project = (ProjectImpl)item;
        else
            throw new CodeAssistantException(400, "'projectId' is not project Id");
        return project;
    }

    /**
     * Returns the Class object associated with the class or interface with the given string name.
     *
     * @param fqn
     *         the Full Qualified Name
     * @param projectId
     *         of current project
     * @param vfsId
     *         Id of VirtualFileSystem
     * @return {@link TypeInfoBean}
     * @throws CodeAssistantException
     */
    public TypeInfo getClassByFQN(String fqn, String projectId, String vfsId) throws CodeAssistantException,
                                                                                     VirtualFileSystemException {
        Set<String> dependencys = getProjectDependencys(projectId, vfsId);
        TypeInfo clazz = storage.getTypeByFqn(fqn, dependencys);
        if (clazz != null) {
            return clazz;
        } else {
            try {
                if (projectId != null)
                    return getClassByFqnFromProject(fqn, projectId, vfsId);
                else
                    // return null if project not specified
                    return null;
            } catch (ItemNotFoundException e) {
                // Return null because we don't found source folder
                // by default it's src/main/java
                // or it's not project
                return null;
            }
        }
    }

    /**
     * Find all nested Java types for fileId file
     *
     * @param fileId
     *         Id of the file
     * @param projectId
     *         Id of project
     * @param vfsId
     *         {@link VirtualFileSystem} Id
     * @return List of nested Java types for file
     * @throws VirtualFileSystemException
     * @throws CodeAssistantException
     */
    public abstract List<ShortTypeInfo> getClassesFromProject(String fileId, String projectId, String vfsId)
            throws VirtualFileSystemException, CodeAssistantException;

    /**
     * Find JavaDoc for FQN
     *
     * @param fqn
     *         of type
     * @return string JavaDoc
     * @throws CodeAssistantException
     */
    public String getClassJavaDoc(String fqn, String projectId, String vfsId) throws CodeAssistantException,
                                                                                     VirtualFileSystemException {
        try {
            return storage.getClassJavaDoc(fqn, getProjectDependencys(projectId, vfsId));
        } catch (CodeAssistantException e) {
            // java doc not found, try search in project
            try {
                return getClassJavaDocFromProject(fqn, projectId, vfsId);
            } catch (ItemNotFoundException itemNotFoundException) {
                // Return null because we don't found source folder
                // by default it's src/main/java
                // or it's not project
                return null;
            }
        }
    }

    /**
     * Find JavaDoc for Java Class member FQN
     *
     * @param fqn
     *         of type
     * @return string JavaDoc
     * @throws CodeAssistantException
     */
    public String getMemberJavaDoc(String fqn, String projectId, String vfsId) throws CodeAssistantException,
                                                                                      VirtualFileSystemException {
        try {
            return storage.getMemberJavaDoc(fqn, getProjectDependencys(projectId, vfsId));
        } catch (CodeAssistantException e) {
            // java doc not found, try search in project
            try {
                return getMemberJavaDocFromProject(fqn, projectId, vfsId);
            } catch (ItemNotFoundException notFoundException) {
                // Return null because we don't found source folder
                // by default it's src/main/java
                // or it's not project
                return null;
            }
        }
    }

    /**
     * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name)<br>
     * <br>
     * Example : if prefix = "java.util.c" set must content:
     * <p/>
     * <pre>
     *  {
     *   java.util.Comparator<T>
     *   java.util.Calendar
     *   java.util.Collection<E>
     *   java.util.Collections
     *   java.util.ConcurrentModificationException
     *   java.util.Currency
     *   java.util.concurrent
     *   java.util.concurrent.atomic
     *   java.util.concurrent.locks
     *  }
     * </pre>
     *
     * @param prefix
     *         the string for matching FQNs
     * @param projectId
     *         Id of the project
     * @param vfsId
     *         Id of the VirtualFileSystem
     * @return
     * @throws CodeAssistantException
     */
    public List<ShortTypeInfo> getTypesByFqnPrefix(String prefix, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
        List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>();
        List<ShortTypeInfo> list = storage.getTypesByFqnPrefix(prefix, getProjectDependencys(projectId, vfsId));
        if (list != null) {
            result.addAll(list);
        }

        try {
            list = getTypesByFqnPrefixInProject(prefix, projectId, vfsId);

            if (list != null) {
                result.addAll(list);
            }
        } catch (ItemNotFoundException e) {
            // Nothing to do
        }

        return result;
    }

    /**
     * Returns the Class object associated with the class or interface with the given string name.
     *
     * @param className
     *         Name of the class, interface, enum or annotation
     * @param projectId
     *         Id of current project
     * @param vfsId
     *         If of the VitrualFileSystem
     * @return list of {@link ShortTypeInfo}
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    public List<ShortTypeInfo> getTypesByNamePrefix(String className, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
        List<ShortTypeInfo> result = new ArrayList<ShortTypeInfo>();
        List<ShortTypeInfo> list = storage.getTypesByNamePrefix(className, getProjectDependencys(projectId, vfsId));
        if (list != null) {
            result.addAll(list);
        }

        try {
            list = getTypesByNamePrefixFromProject(className, projectId, vfsId);
            if (list != null) {
                result.addAll(list);
            }
        } catch (ItemNotFoundException e) {
            // Nothing to do
        }
        return result;
    }

    /**
     * Return sets of {@link TypeInfo} object associated with the class or interface matched to name. (means Class simple name
     * begin on {namePrefix}) Example: if name == "Node" result can content information about: - javax.xml.soap.Node -
     * com.google.gwt.xml.client.Node - org.w3c.dom.Node - org.w3c.dom.traversal.NodeFilter - org.w3c.dom.traversal.NodeIterator
     *
     * @param namePrefix
     *         Prefix for class simple name
     * @param projectId
     *         Id of current project
     * @param vfsId
     *         If of the VitrualFileSystem
     * @return List of {@link TypeInfo}
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    public List<TypeInfo> getTypeInfoByNamePrefix(String namePrefix, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
        List<TypeInfo> searchResult =
                storage.getTypesInfoByNamePrefix(namePrefix, getProjectDependencys(projectId, vfsId));
        List<TypeInfo> result = new ArrayList<TypeInfo>();
        try {
            List<TypeInfo> list = getTypeInfoByNamePrefixFromProject(namePrefix, projectId, vfsId);
            if (list != null) {
                result.addAll(list);
            }
        } catch (ItemNotFoundException e) {
        }
        if (searchResult != null) {
            result.addAll(searchResult);
        }
        return result;
    }

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param prefix
     *         the string for matching package name
     * @param projectId
     *         Id of current project
     * @param vfsId
     *         If of the VitrualFileSystem
     * @return {@link List} of package names
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    public List<String> getPackagesByPrefix(String prefix, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
        List<String> packages = storage.getPackages(prefix, getProjectDependencys(projectId, vfsId));
        List<String> result = new ArrayList<String>();
        try {
            List<String> packagesFromProject = getPackagesByPrefixFromProject(prefix, projectId, vfsId);
            if (packagesFromProject != null) {
                result.addAll(packagesFromProject);
            }
        } catch (ItemNotFoundException e) {
        }

        if (packages != null) {
            result.addAll(packages);

        }
        return result;
    }

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param projectId
     *         Id of current project
     * @param vfsId
     *         Id of current project
     * @return {@link List} of package names
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    public List<String> getAllPackages(String projectId, String vfsId) throws CodeAssistantException,
                                                                              VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Project project = getProject(projectId, vfs);
        return getAllPackages(project, vfs);
    }

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param projectId
     *         Id of current project
     * @param vfsId
     *         Id of current project
     * @return {@link List} of package names
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    public List<String> getAllPackages(Project project, VirtualFileSystem vfs) throws CodeAssistantException,
                                                                                      VirtualFileSystemException {
        List<String> packages = storage.getAllPackages(getProjectDependencys(project));
        List<String> result = new ArrayList<String>();
        try {
            List<String> packagesFromProject = getAllPackagesFromProject(project, vfs);
            if (packagesFromProject != null) {
                result.addAll(packagesFromProject);
            }
        } catch (ItemNotFoundException e) {

        }

        if (packages != null) {
            result.addAll(packages);

        }
        return result;
    }


    /**
     * Return sets of Strings, associated with the package names
     *
     * @param projectId
     *         Id of current project
     * @param vfsId
     *         Id of current virtual file system
     * @return {@link List} of package names
     * @throws VirtualFileSystemException
     * @throws CodeAssistantException
     */
    protected abstract List<String> getAllPackagesFromProject(String projectId, String vfsId)
            throws VirtualFileSystemException, CodeAssistantException;

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param project
     *         current project
     * @param vfs
     *         current virtual file system
     * @return {@link List} of package names
     * @throws VirtualFileSystemException
     * @throws CodeAssistantException
     */
    protected abstract List<String> getAllPackagesFromProject(Project project, VirtualFileSystem fileSystem)
            throws VirtualFileSystemException, CodeAssistantException;

    /**
     * Return sets of Strings, associated with the package names
     *
     * @param prefix
     *         the string for matching package name
     * @param projectId
     *         Id of current project
     * @param vfsId
     *         If of the VitrualFileSystem
     * @return {@link List} of package names
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    protected abstract List<String> getPackagesByPrefixFromProject(String prefix, String projectId, String vfsId)
            throws VirtualFileSystemException, CodeAssistantException;

    /**
     * Return sets of {@link TypeInfo} object associated with the class or interface matched to name. (means Class simple name
     * begin on {namePrefix}) Example: if name == "Node" result can content information about: - javax.xml.soap.Node -
     * com.google.gwt.xml.client.Node - org.w3c.dom.Node - org.w3c.dom.traversal.NodeFilter - org.w3c.dom.traversal.NodeIterator
     *
     * @param namePrefix
     * @param projectId
     * @param vfsId
     * @return
     * @throws VirtualFileSystemException
     * @throws CodeAssistantException
     */
    protected abstract List<TypeInfo> getTypeInfoByNamePrefixFromProject(String namePrefix, String projectId,
                                                                         String vfsId)
            throws VirtualFileSystemException, CodeAssistantException;

    /**
     * Find all classes or annotations or interfaces in project
     *
     * @param type
     *         the enum, that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION)
     * @param prefix
     *         optional parameter that matching first letter of type name
     * @param projectId
     *         Id of the project
     * @param vfsId
     *         Id of the VirtualFileSystem
     * @return Returns list of FQNs matched to class type
     * @throws CodeAssistantException
     */
    protected abstract List<ShortTypeInfo> getByTypeFromProject(JavaType type, String prefix, String projectId,
                                                                String vfsId) throws CodeAssistantException, VirtualFileSystemException;

    /**
     * Search for Java type in project.
     *
     * @param fqn
     *         the Full Qualified Name
     * @param projectId
     *         of current project
     * @param vfsId
     *         Id of VirtualFileSystem
     * @return {@link TypeInfoBean} of null if JavaType not found.
     */
    protected abstract TypeInfo getClassByFqnFromProject(String fqn, String projectId, String vfsId)
            throws VirtualFileSystemException, CodeAssistantException;

    /**
     * Find JavaDoc for FQN
     *
     * @param fqn
     *         of type
     * @return string JavaDoc
     * @throws CodeAssistantException
     *         if Java doc not found.
     */
    protected abstract String getClassJavaDocFromProject(String fqn, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException;

    /**
     * Find JavaDoc for Java Class member FQN
     *
     * @param fqn
     *         of type
     * @return string JavaDoc
     * @throws CodeAssistantException
     *         if Java doc not found.
     */
    protected abstract String getMemberJavaDocFromProject(String fqn, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException;

    /**
     * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name)<br>
     *
     * @param prefix
     *         the string for matching FQNs
     * @param projectId
     *         Id of the project
     * @param vfsId
     *         Id of the VirtualFileSystem
     * @return
     * @throws CodeAssistantException
     */
    protected abstract List<ShortTypeInfo> getTypesByFqnPrefixInProject(String prefix, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException;

    /**
     * Search for Java type in project
     *
     * @param className
     *         Name of the class, interface, enum or annotation
     * @param projectId
     *         Id of current project
     * @param vfsId
     *         If of the VitrualFileSystem
     * @return list of {@link ShortTypeInfo}
     * @throws CodeAssistantException
     */
    protected abstract List<ShortTypeInfo> getTypesByNamePrefixFromProject(String className, String projectId,
                                                                           String vfsId)
            throws CodeAssistantException, VirtualFileSystemException;
}