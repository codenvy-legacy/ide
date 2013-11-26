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
package com.codenvy.ide.ext.java.server;

import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentException;
import com.codenvy.api.vfs.server.exceptions.ItemNotFoundException;
import com.codenvy.api.vfs.server.exceptions.PermissionDeniedException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.ItemType;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.File;
import com.codenvy.api.vfs.shared.dto.Folder;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.api.vfs.shared.dto.ItemList;
import com.codenvy.api.vfs.shared.dto.Project;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.ide.ext.java.server.parser.JavaDocBuilderErrorHandler;
import com.codenvy.ide.ext.java.server.parser.JavaDocBuilderVfs;
import com.codenvy.ide.ext.java.server.parser.JavaTypeToTypeInfoConverter;
import com.codenvy.ide.ext.java.server.parser.VfsClassLibrary;
import com.codenvy.ide.ext.java.server.parser.scanner.FolderFilter;
import com.codenvy.ide.ext.java.server.parser.scanner.FolderScanner;
import com.codenvy.ide.ext.java.shared.JavaType;
import com.codenvy.ide.ext.java.shared.ShortTypeInfo;
import com.codenvy.ide.ext.java.shared.TypeInfo;
import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;


import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Nov 22, 2011 4:53:15 PM evgen $
 */
public class JavaCodeAssistant extends CodeAssistant {

    /** Default Maven 'sourceDirectory' value */
    public static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

    public JavaCodeAssistant(CodeAssistantStorageClient storage, VirtualFileSystemRegistry vfsRegistry) {
        super(storage, vfsRegistry);
    }

    private JavaDocBuilderVfs parseProject(String projectId,
                                           String vfsId)
            throws VirtualFileSystemException, CodeAssistantException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);

        Project project = getProject(projectId, vfs);

        Folder sourceFolder = getSourceFolder(vfs, project);

        JavaDocBuilderVfs builder = new JavaDocBuilderVfs(vfs, new VfsClassLibrary(vfs));
        builder.getClassLibrary().addClassLoader(ClassLoader.getSystemClassLoader());
        builder.setErrorHandler(new JavaDocBuilderErrorHandler());
        builder.addSourceTree(sourceFolder);
        return builder;
    }

    /**
     * @param vfs
     * @param project
     * @return
     * @throws ItemNotFoundException
     * @throws PermissionDeniedException
     * @throws VirtualFileSystemException
     * @throws CodeAssistantException
     */
    private Folder getSourceFolder(VirtualFileSystem vfs,
                                   Project project)
            throws VirtualFileSystemException, CodeAssistantException {
        String sourcePath = null;
        List<Property> properties = project.getProperties();
        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            if (property.getName().equalsIgnoreCase("sourceFolder"))
            {
                sourcePath = property.getValue().get(0);
            }
        }
        if (sourcePath == null) {
            sourcePath = DEFAULT_SOURCE_FOLDER;
        }

        Item sourceFolder = vfs.getItemByPath(project.getPath() + "/" + sourcePath, null, false, PropertyFilter.NONE_FILTER);

        if (sourceFolder.getItemType() != ItemType.FOLDER) {
            throw new CodeAssistantException(500, "Can't find project source, in " + sourcePath);
        }
        return (Folder)sourceFolder;
    }


    @Override
    protected String getClassJavaDocFromProject(String fqn, String projectId,
                                                String vfsId) throws CodeAssistantException, VirtualFileSystemException {

        JavaDocBuilderVfs project = parseProject(projectId, vfsId);
        JavaClass clazz = project.getClassByName(fqn);
        if (clazz == null) {
            throw new CodeAssistantException(404, "Not found");
        }

        return getJavaDoc(clazz);
    }

    /** Return word until first point like "ClassName" on file name "ClassName.java" */
    private String getClassNameOnFileName(String fileName) {
        if (fileName != null) {
            return fileName.substring(0, fileName.indexOf("."));
        }

        return null;
    }

    /** Find classes in package */
    private List<ShortTypeInfo> findClassesInPackage(File file, Project project,
                                                     VirtualFileSystem vfs) throws CodeAssistantException, VirtualFileSystemException {
        List<ShortTypeInfo> classes = new ArrayList<ShortTypeInfo>();
        ItemList children = vfs.getChildren(file.getParentId(), -1, 0, "file", false, PropertyFilter.ALL_FILTER);
        for (Item i : children.getItems()) {
            if (i.getName().endsWith(".java")) {
                if (!file.getId().equals(i.getId())) {
                    //classes.add(new ShortTypeInfoBean(getClassNameOnFileName(i.getName()), 0, "CLASS", null));
                }
            }
        }
        return classes;
    }

    @Override
    protected TypeInfo getClassByFqnFromProject(String fqn, String projectId,
                                                String vfsId) throws VirtualFileSystemException, CodeAssistantException {
        JavaDocBuilderVfs builder = parseProject(projectId, vfsId);
        JavaClass clazz = null;
        if (fqn.contains("$")) {
            String parentFqn = fqn.substring(0, fqn.lastIndexOf('$'));
            JavaClass parentClass = builder.getClassByName(parentFqn);
            if (parentClass == null) {
                return null;
            }
            clazz = parentClass.getNestedClassByName(fqn.substring(fqn.lastIndexOf('$') + 1));
        } else {
            clazz = builder.getClassByName(fqn);
        }

        if (clazz == null) {
            //test if asks inner class
            //         String parentFqn = fqn.substring(0, fqn.lastIndexOf('.'));
            //         JavaClass parentClass = builder.getClassByName(parentFqn);
            //         if (parentClass == null)
            //            return null;
            //         clazz = parentClass.getNestedClassByName(fqn.substring(fqn.lastIndexOf('.') + 1));
            //         if (clazz == null)
            return null;
        }

        return new JavaTypeToTypeInfoConverter(storage, getProjectDependencys(projectId, vfsId)).convert(clazz);
    }

    @Override
    protected List<ShortTypeInfo> getTypesByNamePrefixFromProject(String className, String projectId,
                                                                  String vfsId) throws CodeAssistantException, VirtualFileSystemException {
        JavaDocBuilderVfs builder = parseProject(projectId, vfsId);
        List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
        JavaTypeToTypeInfoConverter converter = new JavaTypeToTypeInfoConverter(storage,
                                                                                getProjectDependencys(projectId, vfsId));
        for (JavaClass clazz : builder.getClasses()) {
            if (clazz.getName().startsWith(className)) {
                types.add(converter.toShortTypeInfo(clazz));
            }
        }
        return types;
    }

    @Override
    protected List<ShortTypeInfo> getTypesByFqnPrefixInProject(String prefix, String projectId,
                                                               String vfsId) throws CodeAssistantException, VirtualFileSystemException {
        JavaDocBuilderVfs builder = parseProject(projectId, vfsId);
        List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
        JavaTypeToTypeInfoConverter converter = new JavaTypeToTypeInfoConverter(storage,
                                                                                getProjectDependencys(projectId, vfsId));
        for (JavaClass clazz : builder.getClasses()) {
            if (clazz.getFullyQualifiedName().startsWith(prefix)) {
                types.add(converter.toShortTypeInfo(clazz));
            }
        }
        return types;
    }

    @Override
    protected List<ShortTypeInfo> getByTypeFromProject(JavaType type, String prefix, String projectId,
                                                       String vfsId) throws CodeAssistantException, VirtualFileSystemException {
        JavaDocBuilderVfs builder = parseProject(projectId, vfsId);
        List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
        JavaTypeToTypeInfoConverter converter = new JavaTypeToTypeInfoConverter(storage,
                                                                                getProjectDependencys(projectId, vfsId));
        if (prefix == null || prefix.isEmpty()) {
            for (JavaClass clazz : builder.getClasses()) {
                if (type == JavaTypeToTypeInfoConverter.getType(clazz)) {
                    types.add(converter.toShortTypeInfo(clazz));
                }
            }
        } else {
            for (JavaClass clazz : builder.getClasses()) {
                if (type == JavaTypeToTypeInfoConverter.getType(clazz) && clazz.getName().startsWith(prefix)) {
                    types.add(converter.toShortTypeInfo(clazz));
                }
            }
        }
        return types;
    }

    @Override
    public List<ShortTypeInfo> getClassesFromProject(String fileId, String projectId,
                                                     String vfsId) throws VirtualFileSystemException, CodeAssistantException {
        List<ShortTypeInfo> classNames = null;
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item item = vfs.getItem(fileId, false, PropertyFilter.ALL_FILTER);
        if (item.getItemType() != ItemType.FILE) {
            throw new InvalidArgumentException("Unable find Classes. Item " + item.getName() + " is not a file. ");
        }

        Item p = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);

        Project project = null;
        if (p instanceof Project) {
            project = (Project)p;
        } else {
            throw new InvalidArgumentException("Unable find Classes. Item " + p.getName() + " is not a project. ");
        }

        classNames = findClassesInPackage((File)item, project, vfs);

        return classNames;
    }

    @Override
    protected String getMemberJavaDocFromProject(String fqn, String projectId,
                                                 String vfsId) throws CodeAssistantException, VirtualFileSystemException {
        JavaDocBuilderVfs project = parseProject(projectId, vfsId);
        String classFqn = fqn.substring(0, fqn.lastIndexOf('.'));
        String memberFqn = fqn.substring(fqn.lastIndexOf('.') + 1);
        JavaClass clazz = project.getClassByName(classFqn);
        if (clazz == null) {
            throw new CodeAssistantException(404, "Not found");
        }

        // member is method
        if (memberFqn.contains("(")) {
            for (JavaMethod method : clazz.getMethods()) {
                if ((method.getName() + JavaTypeToTypeInfoConverter.toParameters(method.getParameterTypes(true))).equals(
                        memberFqn)) {
                    return getJavaDoc(method);
                }
            }
        }
        // member is field
        else {
            for (JavaField field : clazz.getFields()) {
                if (field.getName().equals(memberFqn)) {
                    return getJavaDoc(field);
                }
            }
        }

        throw new CodeAssistantException(404, "Not found");
    }

    private String getJavaDoc(AbstractJavaEntity entity) throws CodeAssistantException {
        if (entity.getComment() == null && entity.getTags().length == 0) {
            throw new CodeAssistantException(404, "Not found");
        }

        return (entity.getComment() == null ? "" : entity.getComment()) + JavaTypeToTypeInfoConverter.tagsToString(
                entity.getTags());
    }

    @Override
    protected List<TypeInfo> getTypeInfoByNamePrefixFromProject(String namePrefix, String projectId,
                                                                String vfsId) throws VirtualFileSystemException, CodeAssistantException {
        JavaDocBuilderVfs builder = parseProject(projectId, vfsId);
        List<TypeInfo> typeInfos = new ArrayList<TypeInfo>();
        JavaTypeToTypeInfoConverter converter = new JavaTypeToTypeInfoConverter(storage,
                                                                                getProjectDependencys(projectId, vfsId));
        for (JavaClass clazz : builder.getClasses()) {
            if (clazz.getName().startsWith(namePrefix)) {
                typeInfos.add(converter.convert(clazz));
            }
        }
        return typeInfos;
    }

    @Override
    protected List<String> getPackagesByPrefixFromProject(String prefix, String projectId,
                                                          String vfsId) throws VirtualFileSystemException, CodeAssistantException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);

        Project project = getProject(projectId, vfs);
        Folder sourceFolder = getSourceFolder(vfs, project);

        FolderScanner scanner = new FolderScanner(sourceFolder, vfs);
        scanner.addFilter(new FolderFilter());
        List<Item> list = scanner.scan();
        List<String> pakages = new ArrayList<String>();
        String sourcePath = sourceFolder.getPath();
        for (Item i : list) {
            String substring = i.getPath().substring(sourcePath.length() + 1);
            substring = substring.replaceAll("/", ".");
            if (substring.startsWith(prefix)) {
                pakages.add(substring);
            }
        }
        return pakages;
    }

    @Override
    protected List<String> getAllPackagesFromProject(Project project,
                                                     VirtualFileSystem vfs) throws VirtualFileSystemException, CodeAssistantException {
        Folder sourceFolder = getSourceFolder(vfs, project);
        FolderScanner scanner = new FolderScanner(sourceFolder, vfs);
        scanner.addFilter(new FolderFilter());
        List<Item> list = scanner.scan();
        List<String> pakages = new ArrayList<String>();
        String sourcePath = sourceFolder.getPath();
        for (Item i : list) {
            String substring = i.getPath().substring(sourcePath.length() + 1);
            pakages.add(substring.replaceAll("/", "."));
        }
        return pakages;
    }

    @Override
    protected List<String> getAllPackagesFromProject(String projectId,
                                                     String vfsId) throws VirtualFileSystemException, CodeAssistantException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Project project = getProject(projectId, vfs);
        return getAllPackagesFromProject(project, vfs);
    }
}