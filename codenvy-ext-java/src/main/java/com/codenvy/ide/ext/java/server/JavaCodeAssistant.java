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
import com.codenvy.api.vfs.server.exceptions.ItemNotFoundException;
import com.codenvy.api.vfs.server.exceptions.PermissionDeniedException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.ItemType;
import com.codenvy.api.vfs.shared.PropertyFilter;
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

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Nov 22, 2011 4:53:15 PM evgen $
 */
@Singleton
public class JavaCodeAssistant extends CodeAssistant {

    /** Default Maven 'sourceDirectory' value */
    public static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

    @Inject
    public JavaCodeAssistant(CodeAssistantStorageClient storage, VirtualFileSystemRegistry vfsRegistry) {
        super(storage, vfsRegistry);
    }

    private JavaDocBuilderVfs parseProject(String projectId, String vfsId) throws VirtualFileSystemException, CodeAssistantException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Project project = getProject(projectId, vfs);
        JavaDocBuilderVfs builder = new JavaDocBuilderVfs(vfs, new VfsClassLibrary(vfs));
        builder.getClassLibrary().addClassLoader(ClassLoader.getSystemClassLoader());
        builder.setErrorHandler(new JavaDocBuilderErrorHandler());
        List<Folder> sourceFolders = getProjectSourceFolders(vfs, project);
        for (Folder sourceFolder : sourceFolders) {
            builder.addSourceTree(sourceFolder);
        }
        return builder;
    }

    private List<Folder> getProjectSourceFolders(VirtualFileSystem vfs, Project project)
            throws VirtualFileSystemException, CodeAssistantException {
        List<Folder> sourceFolders = new ArrayList<>();
        Item parentProject = vfs.getItem(project.getParentId(), false);
        if(parentProject != null && parentProject instanceof Project &&  ((Project)parentProject).getProjectType().equals("Multiple Module Project"))
        {
            ItemList children = vfs.getChildren(parentProject.getId(), -1, 0, ItemType.PROJECT.value(), false);
            for (Item item : children.getItems()) {
                sourceFolders.addAll(getSourceFolders(vfs, (Project)item));
            }
        } else {
            sourceFolders.addAll(getSourceFolders(vfs, project));
        }
        return sourceFolders;
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
    private List<Folder> getSourceFolders(VirtualFileSystem vfs, Project project) throws VirtualFileSystemException, CodeAssistantException {
        List<String> sourcePath = new ArrayList<>(2);
        List<Property> properties = project.getProperties();
        for (Property property : properties) {
            if (property.getName().equalsIgnoreCase("sourceFolder")) {
                sourcePath.add(property.getValue().get(0));
            }
            if (property.getName().equals("java.sourcefolder")) {
                sourcePath.addAll(property.getValue());
            }
        }


        if (sourcePath.isEmpty()) {
            sourcePath.add(DEFAULT_SOURCE_FOLDER);
        }

        List<Folder> sourceFolders = new ArrayList<>(2);
        for (String s : sourcePath) {
            try {
                Item sourceFolder = vfs.getItemByPath(project.getPath() + "/" + s, null, false, PropertyFilter.NONE_FILTER);
                if (sourceFolder.getItemType() != ItemType.FOLDER) {
                    throw new CodeAssistantException(500, "Can't find project source, in " + sourcePath);
                }
                sourceFolders.add((Folder)sourceFolder);
            } catch (ItemNotFoundException e) {
                //ignore, some maven project doesn't have "src/main/java" folders
            }
        }


        return sourceFolders;
    }


    @Override
    protected String getClassJavaDocFromProject(String fqn, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {

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


    @Override
    protected TypeInfo getClassByFqnFromProject(String fqn, String projectId, String vfsId)
            throws VirtualFileSystemException, CodeAssistantException {
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

        return new JavaTypeToTypeInfoConverter(storage, getProjectDependencies(projectId, vfsId)).convert(clazz);
    }

    @Override
    protected List<ShortTypeInfo> getTypesByNamePrefixFromProject(String className, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
        JavaDocBuilderVfs builder = parseProject(projectId, vfsId);
        List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
        JavaTypeToTypeInfoConverter converter = new JavaTypeToTypeInfoConverter(storage,
                                                                                getProjectDependencies(projectId, vfsId));
        for (JavaClass clazz : builder.getClasses()) {
            if (clazz.getName().startsWith(className)) {
                types.add(converter.toShortTypeInfo(clazz));
            }
        }
        return types;
    }

    @Override
    protected List<ShortTypeInfo> getTypesByFqnPrefixInProject(String prefix, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
        JavaDocBuilderVfs builder = parseProject(projectId, vfsId);
        List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
        JavaTypeToTypeInfoConverter converter = new JavaTypeToTypeInfoConverter(storage, getProjectDependencies(projectId, vfsId));
        for (JavaClass clazz : builder.getClasses()) {
            if (clazz.getFullyQualifiedName().startsWith(prefix)) {
                types.add(converter.toShortTypeInfo(clazz));
            }
        }
        return types;
    }

    @Override
    protected List<ShortTypeInfo> getByTypeFromProject(JavaType type, String prefix, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
        JavaDocBuilderVfs builder = parseProject(projectId, vfsId);
        List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
        JavaTypeToTypeInfoConverter converter = new JavaTypeToTypeInfoConverter(storage, getProjectDependencies(projectId, vfsId));
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
    protected String getMemberJavaDocFromProject(String fqn, String projectId, String vfsId)
            throws CodeAssistantException, VirtualFileSystemException {
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
        List<TypeInfo> typeInfos = new ArrayList<>();
        JavaTypeToTypeInfoConverter converter = new JavaTypeToTypeInfoConverter(storage,
                                                                                getProjectDependencies(projectId, vfsId));
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
        List<Folder> sourceFolders = getProjectSourceFolders(vfs, project);
        List<String> packages = new ArrayList<>();
        for (Folder sourceFolder : sourceFolders) {
            FolderScanner scanner = new FolderScanner(sourceFolder, vfs);
            scanner.addFilter(new FolderFilter());
            List<Item> list = scanner.scan();
            String sourcePath = sourceFolder.getPath();
            for (Item i : list) {
                String substring = i.getPath().substring(sourcePath.length() + 1);
                substring = substring.replaceAll("/", ".");
                if (substring.startsWith(prefix)) {
                    packages.add(substring);
                }
            }

        }
        return packages;
    }

    @Override
    protected List<String> getAllPackagesFromProject(Project project,
                                                     VirtualFileSystem vfs) throws VirtualFileSystemException, CodeAssistantException {
        List<Folder> sourceFolders = getProjectSourceFolders(vfs, project);
        List<String> packages = new ArrayList<>();
        for (Folder sourceFolder : sourceFolders) {
            FolderScanner scanner = new FolderScanner(sourceFolder, vfs);
            scanner.addFilter(new FolderFilter());
            List<Item> list = scanner.scan();
            String sourcePath = sourceFolder.getPath();
            for (Item i : list) {
                String substring = i.getPath().substring(sourcePath.length() + 1);
                packages.add(substring.replaceAll("/", "."));
            }
        }
        return packages;
    }

    @Override
    protected List<String> getAllPackagesFromProject(String projectId,
                                                     String vfsId) throws VirtualFileSystemException, CodeAssistantException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Project project = getProject(projectId, vfs);
        return getAllPackagesFromProject(project, vfs);
    }
}
