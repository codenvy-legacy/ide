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
package com.codenvy.ide.ext.java.server.internal.core;

import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.Project;
import com.codenvy.api.project.server.ProjectManager;
import com.codenvy.api.project.shared.Attribute;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.ide.ext.java.server.core.JavaCore;
import com.codenvy.vfs.impl.fs.VirtualFileImpl;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.internal.compiler.util.ObjectVector;
import org.eclipse.jdt.internal.core.JavaModelStatus;
import org.eclipse.jdt.internal.core.OpenableElementInfo;
import org.eclipse.jdt.internal.core.util.MementoTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class JavaProject extends Openable implements IJavaProject {

    private static final Logger LOG = LoggerFactory.getLogger(JavaProject.class);
    private final VirtualFileImpl   virtualFile;
    private       Project           project;
    private       IClasspathEntry[] rawClassPath;
    private       ResolvedClasspath resolvedClasspath;

    public JavaProject(Project project, String tempDir, ProjectManager projectManager, String ws) {
        super(null);
        this.project = project;
        if (!(project.getBaseFolder().getVirtualFile() instanceof VirtualFileImpl)) {
            throw new IllegalArgumentException("Project must be based on com.codenvy.vfs.impl.fs.VirtualFileImpl");
        }
        virtualFile = (VirtualFileImpl)project.getBaseFolder().getVirtualFile();
        List<IClasspathEntry> paths = new ArrayList<>();
        try {
            if (project.getBaseFolder().getParent().getPath().equals("/")) {
                addSources(project, paths);
            } else {
                FolderEntry parentFolder = project.getBaseFolder().getParent();
                Project parentProject = projectManager.getProject(ws, parentFolder.getPath());
                for (Project module : parentProject.getModules()) {
                    addSources(module, paths);
                }
            }
        } catch (IOException e) {
            LOG.error("Can't find sources folder attribute");
        }

        paths.add(JavaCore.newContainerEntry(new Path("codenvy:Jre")));
        try {
            File depDir = new File(tempDir, virtualFile.getId());
            if (depDir.exists()) {
                DirectoryStream<java.nio.file.Path> deps =
                        Files.newDirectoryStream(depDir.toPath(), new DirectoryStream.Filter<java.nio.file.Path>() {
                            @Override
                            public boolean accept(java.nio.file.Path entry) throws IOException {
                                return entry.getFileName().toString().endsWith("jar");
                            }
                        });

                for (java.nio.file.Path dep : deps) {
                    paths.add(JavaCore.newLibraryEntry(new Path(dep.toAbsolutePath().toString()), null, null));
                }
            }
            rawClassPath = paths.toArray(new IClasspathEntry[paths.size()]);
        } catch (VirtualFileSystemException | IOException e) {
            LOG.error("Can't find jar dependency's: ", e);
        }

    }

    private void addSources(Project project, List<IClasspathEntry> paths) throws IOException {
        Attribute attribute = project.getDescription().getAttribute("folders.source");
        if (attribute != null) {
            for (String path : attribute.getValues()) {
                paths.add(JavaCore.newSourceEntry(new Path(virtualFile.getIoFile().getPath() + "/" + path)));
            }
        }
    }

    /**
     * Returns the raw classpath for the project, as a list of classpath
     * entries. This corresponds to the exact set of entries which were assigned
     * using <code>setRawClasspath</code>, in particular such a classpath may
     * contain classpath variable and classpath container entries. Classpath
     * variable and classpath container entries can be resolved using the
     * helper method <code>getResolvedClasspath</code>; classpath variable
     * entries also can be resolved individually using
     * <code>JavaCore#getClasspathVariable</code>).
     * <p>
     * Both classpath containers and classpath variables provides a level of
     * indirection that can make the <code>.classpath</code> file stable across
     * workspaces.
     * As an example, classpath variables allow a classpath to no longer refer
     * directly to external JARs located in some user specific location.
     * The classpath can simply refer to some variables defining the proper
     * locations of these external JARs. Similarly, classpath containers
     * allows classpath entries to be computed dynamically by the plug-in that
     * defines that kind of classpath container.
     * </p>
     * <p>
     * Note that in case the project isn't yet opened, the classpath will
     * be read directly from the associated <tt>.classpath</tt> file.
     * </p>
     *
     * @return the raw classpath for the project, as a list of classpath entries
     * @throws org.eclipse.jdt.core.JavaModelException
     *         if this element does not exist or if an
     *         exception occurs while accessing its corresponding resource
     * @see org.eclipse.jdt.core.IClasspathEntry
     */
    public IClasspathEntry[] getRawClasspath() throws JavaModelException {
        return rawClassPath;
    }

    @Override
    public String[] getRequiredProjectNames() throws JavaModelException {
        return new String[0];
    }

    @Override
    public IClasspathEntry[] getResolvedClasspath(boolean ignoreUnresolvedEntry) throws JavaModelException {
        return new IClasspathEntry[0];
    }

    @Override
    public boolean hasBuildState() {
        return false;
    }

    @Override
    public boolean hasClasspathCycle(IClasspathEntry[] entries) {
        return false;
    }

    @Override
    public boolean isOnClasspath(IJavaElement element) {
        return false;
    }

    @Override
    public boolean isOnClasspath(IResource resource) {
        return false;
    }

    @Override
    public IEvaluationContext newEvaluationContext() {
        return null;
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(IRegion region, IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(IRegion region, WorkingCopyOwner owner, IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(IType type, IRegion region, IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(IType type, IRegion region, WorkingCopyOwner owner, IProgressMonitor monitor)
            throws JavaModelException {
        return null;
    }

    @Override
    public IPath readOutputLocation() {
        return null;
    }

    @Override
    public IClasspathEntry[] readRawClasspath() {
        return new IClasspathEntry[0];
    }

    @Override
    public void setOption(String optionName, String optionValue) {

    }

    @Override
    public void setOptions(Map newOptions) {

    }

    @Override
    public void setOutputLocation(IPath path, IProgressMonitor monitor) throws JavaModelException {

    }

    @Override
    public void setRawClasspath(IClasspathEntry[] entries, IPath outputLocation, boolean canModifyResources, IProgressMonitor monitor)
            throws JavaModelException {

    }

    @Override
    public void setRawClasspath(IClasspathEntry[] entries, boolean canModifyResources, IProgressMonitor monitor) throws JavaModelException {

    }

    @Override
    public void setRawClasspath(IClasspathEntry[] entries, IClasspathEntry[] referencedEntries, IPath outputLocation,
                                IProgressMonitor monitor) throws JavaModelException {

    }

    @Override
    public IClasspathEntry[] getReferencedClasspathEntries() throws JavaModelException {
        return new IClasspathEntry[0];
    }

    @Override
    public void setRawClasspath(IClasspathEntry[] entries, IProgressMonitor monitor) throws JavaModelException {

    }

    @Override
    public void setRawClasspath(IClasspathEntry[] entries, IPath outputLocation, IProgressMonitor monitor) throws JavaModelException {

    }

    /**
     * This is a helper method returning the resolved classpath for the project
     * as a list of simple (non-variable, non-container) classpath entries.
     * All classpath variable and classpath container entries in the project's
     * raw classpath will be replaced by the simple classpath entries they
     * resolve to.
     * <p>
     * The resulting resolved classpath is accurate for the given point in time.
     * If the project's raw classpath is later modified, or if classpath
     * variables are changed, the resolved classpath can become out of date.
     * Because of this, hanging on resolved classpath is not recommended.
     * </p>
     * <p>
     * Note that if the resolution creates duplicate entries
     * (i.e. {@link IClasspathEntry entries} which are {@link Object#equals(Object)}),
     * only the first one is added to the resolved classpath.
     * </p>
     *
     * @see IClasspathEntry
     */
    public IClasspathEntry[] getResolvedClasspath() throws JavaModelException {
        if (resolvedClasspath == null) {
            ResolvedClasspath result = new ResolvedClasspath();
            LinkedHashSet<IClasspathEntry> resolvedEntries = new LinkedHashSet<>();
            for (IClasspathEntry entry : getRawClasspath()) {
                switch (entry.getEntryKind()) {
                    case IClasspathEntry.CPE_LIBRARY:
                    case IClasspathEntry.CPE_SOURCE:
                        addToResult(entry, entry, result, resolvedEntries);
                        break;
                    case IClasspathEntry.CPE_CONTAINER:
                        IClasspathContainer container = JavaCore.getClasspathContainer(entry.getPath(), this);
                        for (IClasspathEntry classpathEntry : container.getClasspathEntries()) {
                            addToResult(entry, classpathEntry, result, resolvedEntries);
                        }
                        break;
                }
            }
            result.resolvedClasspath = new IClasspathEntry[resolvedEntries.size()];
            resolvedEntries.toArray(result.resolvedClasspath);
            resolvedClasspath = result;
        }

        return resolvedClasspath.resolvedClasspath;
    }

    public ResolvedClasspath resolvedClasspath() {
        return resolvedClasspath;
    }

    /**
     * Returns the classpath entry that refers to the given path
     * or <code>null</code> if there is no reference to the path.
     *
     * @param path
     *         IPath
     * @return IClasspathEntry
     * @throws JavaModelException
     */
    public IClasspathEntry getClasspathEntryFor(IPath path) throws JavaModelException {
        getResolvedClasspath(); // force resolution
        if (resolvedClasspath == null) {
            return null;
        }
        Map rootPathToResolvedEntries = resolvedClasspath.rootPathToResolvedEntries;
        if (rootPathToResolvedEntries == null)
            return null;
        IClasspathEntry classpathEntry = (IClasspathEntry)rootPathToResolvedEntries.get(path);
        if (classpathEntry == null) {
            path = getProject().getWorkspace().getRoot().getLocation().append(path);
            classpathEntry = (IClasspathEntry)rootPathToResolvedEntries.get(path);
        }
        return classpathEntry;
    }

    public String getName() {
        return project.getName();
    }

    public IPath getFullPath() {
        return new Path(virtualFile.getIoFile().getPath());
    }

    @Override
    public IClasspathEntry decodeClasspathEntry(String encodedEntry) {
        return null;
    }

    @Override
    public String encodeClasspathEntry(IClasspathEntry classpathEntry) {
        return null;
    }

    @Override
    public IJavaElement findElement(IPath path) throws JavaModelException {
        return null;
    }

    @Override
    public IJavaElement findElement(IPath path, WorkingCopyOwner owner) throws JavaModelException {
        return null;
    }

    @Override
    public IJavaElement findElement(String bindingKey, WorkingCopyOwner owner) throws JavaModelException {
        return null;
    }

    @Override
    public IPackageFragment findPackageFragment(IPath path) throws JavaModelException {
        return null;
    }

    @Override
    public IPackageFragmentRoot findPackageFragmentRoot(IPath path) throws JavaModelException {
        return null;
    }

    @Override
    public IPackageFragmentRoot[] findPackageFragmentRoots(IClasspathEntry entry) {
        return new IPackageFragmentRoot[0];
    }

    @Override
    public IType findType(String fullyQualifiedName) throws JavaModelException {
        return null;
    }

    @Override
    public IType findType(String fullyQualifiedName, IProgressMonitor progressMonitor) throws JavaModelException {
        return null;
    }

    @Override
    public IType findType(String fullyQualifiedName, WorkingCopyOwner owner) throws JavaModelException {
        return null;
    }

    @Override
    public IType findType(String fullyQualifiedName, WorkingCopyOwner owner, IProgressMonitor progressMonitor) throws JavaModelException {
        return null;
    }

    @Override
    public IType findType(String packageName, String typeQualifiedName) throws JavaModelException {
        return null;
    }

    @Override
    public IType findType(String packageName, String typeQualifiedName, IProgressMonitor progressMonitor) throws JavaModelException {
        return null;
    }

    @Override
    public IType findType(String packageName, String typeQualifiedName, WorkingCopyOwner owner) throws JavaModelException {
        return null;
    }

    @Override
    public IType findType(String packageName, String typeQualifiedName, WorkingCopyOwner owner, IProgressMonitor progressMonitor)
            throws JavaModelException {
        return null;
    }

    @Override
    public IPackageFragmentRoot[] getAllPackageFragmentRoots() throws JavaModelException {
        return getAllPackageFragmentRoots(null /*no reverse map*/);
    }

    public IPackageFragmentRoot[] getAllPackageFragmentRoots(Map rootToResolvedEntries) throws JavaModelException {

        return computePackageFragmentRoots(getResolvedClasspath(), true/*retrieveExportedRoots*/, rootToResolvedEntries);
    }

    /**
     * Returns (local/all) the package fragment roots identified by the given project's classpath.
     * Note: this follows project classpath references to find required project contributions,
     * eliminating duplicates silently.
     * Only works with resolved entries
     *
     * @param resolvedClasspath
     *         IClasspathEntry[]
     * @param retrieveExportedRoots
     *         boolean
     * @return IPackageFragmentRoot[]
     * @throws JavaModelException
     */
    public IPackageFragmentRoot[] computePackageFragmentRoots(
            IClasspathEntry[] resolvedClasspath,
            boolean retrieveExportedRoots,
            Map rootToResolvedEntries) throws JavaModelException {

        ObjectVector accumulatedRoots = new ObjectVector();
        computePackageFragmentRoots(
                resolvedClasspath,
                accumulatedRoots,
                new HashSet(5), // rootIDs
                null, // inside original project
                retrieveExportedRoots,
                rootToResolvedEntries);
        IPackageFragmentRoot[] rootArray = new IPackageFragmentRoot[accumulatedRoots.size()];
        accumulatedRoots.copyInto(rootArray);
        return rootArray;
    }

    /**
     * Returns (local/all) the package fragment roots identified by the given project's classpath.
     * Note: this follows project classpath references to find required project contributions,
     * eliminating duplicates silently.
     * Only works with resolved entries
     *
     * @param resolvedClasspath
     *         IClasspathEntry[]
     * @param accumulatedRoots
     *         ObjectVector
     * @param rootIDs
     *         HashSet
     * @param referringEntry
     *         project entry referring to this CP or null if initial project
     * @param retrieveExportedRoots
     *         boolean
     * @throws JavaModelException
     */
    public void computePackageFragmentRoots(
            IClasspathEntry[] resolvedClasspath,
            ObjectVector accumulatedRoots,
            HashSet rootIDs,
            IClasspathEntry referringEntry,
            boolean retrieveExportedRoots,
            Map rootToResolvedEntries) throws JavaModelException {

        if (referringEntry == null) {
            rootIDs.add(rootID());
        }
        for (int i = 0, length = resolvedClasspath.length; i < length; i++) {
            computePackageFragmentRoots(
                    resolvedClasspath[i],
                    accumulatedRoots,
                    rootIDs,
                    referringEntry,
                    retrieveExportedRoots,
                    rootToResolvedEntries);
        }
    }

    /**
     * Returns the package fragment roots identified by the given entry. In case it refers to
     * a project, it will follow its classpath so as to find exported roots as well.
     * Only works with resolved entry
     *
     * @param resolvedEntry
     *         IClasspathEntry
     * @param accumulatedRoots
     *         ObjectVector
     * @param rootIDs
     *         HashSet
     * @param referringEntry
     *         the CP entry (project) referring to this entry, or null if initial project
     * @param retrieveExportedRoots
     *         boolean
     * @throws JavaModelException
     */
    public void computePackageFragmentRoots(
            IClasspathEntry resolvedEntry,
            ObjectVector accumulatedRoots,
            HashSet rootIDs,
            IClasspathEntry referringEntry,
            boolean retrieveExportedRoots,
            Map rootToResolvedEntries) throws JavaModelException {

        String rootID = ((ClasspathEntry)resolvedEntry).rootID();
        if (rootIDs.contains(rootID)) return;

        IPath projectPath = this.getFullPath();
        IPath entryPath = resolvedEntry.getPath();
//        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IPackageFragmentRoot root = null;

        switch (resolvedEntry.getEntryKind()) {

            // source folder
            case IClasspathEntry.CPE_SOURCE:

                if (projectPath.isPrefixOf(entryPath)) {
                    Object target = JavaModelManager.getTarget(entryPath, true/*check existency*/);
                    if (target == null) return;

                    if (target instanceof File && ((File)target).isDirectory()) {
                        root = getPackageFragmentRoot((File)target);
                    }
                }
                break;

            // internal/external JAR or folder
            case IClasspathEntry.CPE_LIBRARY:
                if (referringEntry != null && !resolvedEntry.isExported())
                    return;
                Object target = JavaModelManager.getTarget(entryPath, true/*check existency*/);
                if (target == null)
                    return;

//                if (target instanceof IResource){
//                    // internal target
//                } else
                if (target instanceof File) {
                    // external target
                    if (JavaModelManager.isFile(target)) {
                        root = new JarPackageFragmentRoot((File)target, this);
                    } else if (((File)target).isDirectory()) {
                        root = getPackageFragmentRoot((File)target, entryPath);
//                        root = new ExternalPackageFragmentRoot(entryPath, this);
                    }
                }
                break;

            // recurse into required project
            case IClasspathEntry.CPE_PROJECT:

                if (!retrieveExportedRoots) return;
                if (referringEntry != null && !resolvedEntry.isExported()) return;
                //todo multiproject
//                IResource member = workspaceRoot.findMember(entryPath);
//                if (member != null && member.getType() == IResource.PROJECT) {// double check if bound to project (23977)
//                    IProject requiredProjectRsc = (IProject)member;
//                    if (JavaProject.hasJavaNature(requiredProjectRsc)) { // special builder binary output
//                        rootIDs.add(rootID);
//                        JavaProject requiredProject = (JavaProject)JavaCore.create(requiredProjectRsc);
//                        requiredProject.computePackageFragmentRoots(
//                                requiredProject.getResolvedClasspath(),
//                                accumulatedRoots,
//                                rootIDs,
//                                rootToResolvedEntries == null ? resolvedEntry
//                                                              : ((org.eclipse.jdt.internal.core.ClasspathEntry)resolvedEntry)
//                                        .combineWith((org.eclipse.jdt.internal.core.ClasspathEntry)referringEntry),
//                                // only combine if need to build the reverse map
//                                retrieveExportedRoots,
//                                rootToResolvedEntries);
//                    }
//                    break;
//                }
        }
        if (root != null) {
            accumulatedRoots.add(root);
            rootIDs.add(rootID);
            if (rootToResolvedEntries != null) rootToResolvedEntries
                    .put(root, ((ClasspathEntry)resolvedEntry).combineWith((ClasspathEntry)referringEntry));
        }
    }

    /**
     * @see IJavaProject
     */
    public IPackageFragmentRoot getPackageFragmentRoot(File resource) {
        return getPackageFragmentRoot(resource, null/*no entry path*/);
    }

    private IPackageFragmentRoot getPackageFragmentRoot(File resource, IPath entryPath) {
        if (resource.isDirectory()) {
//            if (ExternalFoldersManager.isInternalPathForExternalFolder(resource.getFullPath()))
//                return new ExternalPackageFragmentRoot(resource, entryPath, this);
            return new PackageFragmentRoot(resource, this);
        } else {
            return new JarPackageFragmentRoot(resource, this);
        }
//        return null;

    }

    /**
     * Answers an ID which is used to distinguish project/entries during package
     * fragment root computations
     *
     * @return String
     */
    public String rootID() {
        return "[PRJ]" + this.getFullPath(); //$NON-NLS-1$
    }

    @Override
    public Object[] getNonJavaResources() throws JavaModelException {
        return new Object[0];
    }

    @Override
    public String getOption(String optionName, boolean inheritJavaCoreOptions) {
        return null;
    }

    public Map getOptions(boolean b) {
        return new HashMap();
    }

    @Override
    public IPath getOutputLocation() throws JavaModelException {
        return null;
    }

    @Override
    public IPackageFragmentRoot getPackageFragmentRoot(String externalLibraryPath) {

//        return new JarPackageFragmentRoot(new Path(externalLibraryPath), this);
        throw new UnsupportedOperationException();
    }

    @Override
    public IPackageFragmentRoot getPackageFragmentRoot(IResource resource) {
        return null;
    }

    @Override
    public IPackageFragmentRoot[] getPackageFragmentRoots() throws JavaModelException {
        return new IPackageFragmentRoot[0];
    }

    @Override
    public IPackageFragmentRoot[] getPackageFragmentRoots(IClasspathEntry entry) {
        return new IPackageFragmentRoot[0];
    }

    @Override
    public IPackageFragment[] getPackageFragments() throws JavaModelException {
        return new IPackageFragment[0];
    }

    @Override
    public IProject getProject() {
        return null;
    }

    @Override
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource)
            throws JavaModelException {
        return false;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public IJavaElement getAncestor(int ancestorType) {
        return null;
    }

    @Override
    public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException {
        return null;
    }

    @Override
    public IResource getCorrespondingResource() throws JavaModelException {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }

    @Override
    public IJavaElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner owner) {
        return null;
    }

    @Override
    public int getElementType() {
        return 0;
    }

    @Override
    public String getHandleIdentifier() {
        return null;
    }

    @Override
    protected char getHandleMementoDelimiter() {
        return 0;
    }

    @Override
    public IJavaModel getJavaModel() {
        return null;
    }

    @Override
    public IJavaProject getJavaProject() {
        return null;
    }

    @Override
    public IOpenable getOpenable() {
        return null;
    }

    @Override
    public IJavaElement getParent() {
        return null;
    }

    public IPath getPath() {
        try {
            return new Path(virtualFile.getPath());
        } catch (VirtualFileSystemException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public IJavaElement getPrimaryElement() {
        return null;
    }

    @Override
    public IResource getResource() {
        return null;
    }

    @Override
    protected IResource resource(org.eclipse.jdt.internal.core.PackageFragmentRoot root) {
        return null;
    }

    @Override
    public ISchedulingRule getSchedulingRule() {
        return null;
    }

    @Override
    public IResource getUnderlyingResource() throws JavaModelException {
        return null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean isStructureKnown() throws JavaModelException {
        return false;
    }

    @Override
    public Object getAdapter(Class aClass) {
        return null;
    }

    @Override
    public void close() throws JavaModelException {

    }

    @Override
    public String findRecommendedLineSeparator() throws JavaModelException {
        return null;
    }

    @Override
    public IBuffer getBuffer() throws JavaModelException {
        return null;
    }

    @Override
    public boolean hasUnsavedChanges() throws JavaModelException {
        return false;
    }

    @Override
    public boolean isConsistent() {
        return false;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void makeConsistent(IProgressMonitor progress) throws JavaModelException {

    }

    @Override
    public void open(IProgressMonitor progress) throws JavaModelException {

    }

    @Override
    public void save(IProgressMonitor progress, boolean force) throws JavaModelException {

    }

    @Override
    protected IStatus validateExistence(IResource underlyingResource) {
        return null;
    }

    @Override
    public IJavaElement[] getChildren() throws JavaModelException {
        return new IJavaElement[0];
    }

    @Override
    public boolean hasChildren() throws JavaModelException {
        return false;
    }

    private void addToResult(IClasspathEntry rawEntry, IClasspathEntry resolvedEntry, ResolvedClasspath result,
                             LinkedHashSet<IClasspathEntry> resolvedEntries) {

        IPath resolvedPath;
        // If it's already been resolved, do not add to resolvedEntries
        if (result.rawReverseMap.get(resolvedPath = resolvedEntry.getPath()) == null) {
            result.rawReverseMap.put(resolvedPath, rawEntry);
            result.rootPathToResolvedEntries.put(resolvedPath, resolvedEntry);
            resolvedEntries.add(resolvedEntry);
        }
    }


    public static class ResolvedClasspath {
        IClasspathEntry[] resolvedClasspath;
        IJavaModelStatus                unresolvedEntryStatus     = JavaModelStatus.VERIFIED_OK;
        HashMap<IPath, IClasspathEntry> rawReverseMap             = new HashMap<>();
        Map<IPath, IClasspathEntry>     rootPathToResolvedEntries = new HashMap<>();
        IClasspathEntry[]               referencedEntries         = null;
    }
}
