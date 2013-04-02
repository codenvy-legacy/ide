/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Andrew Ferguson (Symbian) - [api] enable document setup participants to customize behaviour based on resource being opened -
 *     https://bugs.eclipse.org/bugs/show_bug.cgi?id=208881
 *******************************************************************************/
package com.codenvy.eclipse.core.internal.filebuffers;

import com.codenvy.eclipse.core.filebuffers.FileBuffers;
import com.codenvy.eclipse.core.filebuffers.IDocumentSetupParticipant;
import com.codenvy.eclipse.core.filebuffers.IDocumentSetupParticipantExtension;
import com.codenvy.eclipse.core.filebuffers.IFileBuffer;
import com.codenvy.eclipse.core.filebuffers.IStateValidationSupport;
import com.codenvy.eclipse.core.filebuffers.LocationKind;
import com.codenvy.eclipse.core.resources.IFile;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IResourceRuleFactory;
import com.codenvy.eclipse.core.resources.IWorkspace;
import com.codenvy.eclipse.core.resources.IWorkspaceRunnable;
import com.codenvy.eclipse.core.resources.ResourcesPlugin;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.core.runtime.IProgressMonitor;
import com.codenvy.eclipse.core.runtime.ISafeRunnable;
import com.codenvy.eclipse.core.runtime.IStatus;
import com.codenvy.eclipse.core.runtime.SafeRunner;
import com.codenvy.eclipse.core.runtime.Status;
import com.codenvy.eclipse.core.runtime.jobs.ISchedulingRule;
import com.codenvy.eclipse.jdt.internal.core.util.Util;

import org.exoplatform.ide.editor.shared.text.IDocument;

import java.util.ArrayList;


/** @since 3.3 */
public class ResourceTextFileBufferManager extends TextFileBufferManager {


    public ResourceTextFileBufferManager() {
        fRegistry = new ResourceExtensionRegistry();
    }

    /*
     * @see org.eclipse.core.filebuffers.ITextFileBufferManager#isTextFileLocation(org.eclipse.core.runtime.IPath, boolean)
     * @since 3.2
     */
    public boolean isTextFileLocation(IPath location, boolean strict) {
        //		Assert.isNotNull(location);
        //		location= FileBuffers.normalizeLocation(location);
        //
        //		IFile file= FileBuffers.getWorkspaceFileAtLocation(location, true);
        //		if (file != null) {
        //			if (file.exists()) {
        //				try {
        //					IContentDescription description= file.getContentDescription();
        //					if (description != null) {
        //						IContentType type= description.getContentType();
        //						if (type != null)
        //							return type.isKindOf(TEXT_CONTENT_TYPE);
        //					}
        //				} catch (CoreException x) {
        //					// ignore: API specification tells return true if content type can't be determined
        //				}
        //			} else {
        //				IContentTypeManager manager= Platform.getContentTypeManager();
        //				IContentType[] contentTypes= manager.findContentTypesFor(file.getName());
        //				if (contentTypes != null && contentTypes.length > 0) {
        //					for (int i= 0; i < contentTypes.length; i++)
        //						if (contentTypes[i].isKindOf(TEXT_CONTENT_TYPE))
        //							return true;
        //					return false;
        //				}
        //			}
        //			return !strict;
        //		}
        //
        //		return isTextFileLocation(FileBuffers.getFileStoreAtLocation(location), strict);
        return true;
    }

    /*
     * @see org.eclipse.core.buffer.text.IBufferedFileManager#getDefaultEncoding()
     */
    public String getDefaultEncoding() {
        return ResourcesPlugin.getEncoding();
    }

    /*
     * @see org.eclipse.core.internal.filebuffers.TextFileBufferManager#normalizeLocation(org.eclipse.core.runtime.IPath)
     */
    protected IPath normalizeLocation(IPath location) {
        return FileBuffers.normalizeLocation(location);
    }

    protected AbstractFileBuffer createTextFileBuffer(IPath location, LocationKind locationKind) {
        //		if (locationKind == LocationKind.IFILE || locationKind == LocationKind.NORMALIZE  && FileBuffers.getWorkspaceFileAtLocation
        // (location, true) != null)
        return new ResourceTextFileBuffer(this);
        //		return new FileStoreTextFileBuffer(this);
    }

    //	IAnnotationModel createAnnotationModel(IFile file) {
    //		Assert.isNotNull(file);
    //		IAnnotationModelFactory factory= ((ResourceExtensionRegistry)fRegistry).getAnnotationModelFactory(file);
    //		if (factory != null)
    //			return factory.createAnnotationModel(file.getFullPath());
    //		return null;
    //	}

    public IDocument createEmptyDocument(final IFile file) {
        IDocument documentFromFactory = createEmptyDocumentFromFactory(file);
        final IDocument document;
        if (documentFromFactory != null) {
            document = documentFromFactory;
        } else {
            document = new SynchronizableDocument();
        }

        //		// Set the initial line delimiter
        //		if (document instanceof IDocumentExtension4) {
        //			String initalLineDelimiter= getLineDelimiterPreference(file);
        //			if (initalLineDelimiter != null)
        //				((IDocumentExtension4)document).setInitialLineDelimiter(initalLineDelimiter);
        //		}

        final IDocumentSetupParticipant[] participants = ((ResourceExtensionRegistry)fRegistry).getDocumentSetupParticipants(
                file);
        if (participants != null) {
            for (int i = 0; i < participants.length; i++) {
                final IDocumentSetupParticipant participant = participants[i];
                ISafeRunnable runnable = new ISafeRunnable() {
                    public void run() throws Exception {
                        if (participant instanceof IDocumentSetupParticipantExtension) {
                            ((IDocumentSetupParticipantExtension)participant).setup(document, file.getFullPath(),
                                                                                    LocationKind.IFILE);
                        } else {
                            participant.setup(document);
                        }

                        if (document.getDocumentPartitioner() != null) {
                            String message = NLSUtility.format(
                                    FileBuffersMessages.TextFileBufferManager_warning_documentSetupInstallsDefaultPartitioner,
                                    participant.getClass());
                            IStatus status = new Status(IStatus.WARNING, FileBuffersPlugin.PLUGIN_ID, IStatus.OK, message,
                                                        null);
                            Util.log(status);
                        }
                    }

                    public void handleException(Throwable t) {
                        IStatus status = new Status(IStatus.ERROR, FileBuffersPlugin.PLUGIN_ID, IStatus.OK,
                                                    FileBuffersMessages.TextFileBufferManager_error_documentSetupFailed, t);
                        Util.log(status);
                    }
                };
                SafeRunner.run(runnable);
            }
        }

        return document;
    }

    /**
     * Helper to get rid of deprecation warnings.
     *
     * @param file
     *         the file
     * @return the created empty document or <code>null</code> if none got created
     * @since 3.5
     * @deprecated As of 3.5
     */
    private IDocument createEmptyDocumentFromFactory(final IFile file) {
        //		final IDocument[] runnableResult= new IDocument[1];
        //		final org.eclipse.core.filebuffers.IDocumentFactory factory= ((ResourceExtensionRegistry)fRegistry).getDocumentFactory(file);
        //		if (factory != null) {
        //			ISafeRunnable runnable= new ISafeRunnable() {
        //				public void run() throws Exception {
        //					runnableResult[0]= factory.createDocument();
        //				}
        //				public void handleException(Throwable t) {
        //					IStatus status= new Status(IStatus.ERROR, FileBuffersPlugin.PLUGIN_ID, IStatus.OK,
        // FileBuffersMessages.TextFileBufferManager_error_documentFactoryFailed, t);
        //					FileBuffersPlugin.getDefault().getLog().log(status);
        //				}
        //			};
        //			SafeRunner.run(runnable);
        //		}
        //		return runnableResult[0];
        return null;
    }

    private String getLineDelimiterPreference(IFile file) {
        //		IScopeContext[] scopeContext;
        //		if (file != null && file.getProject() != null) {
        //			// project preference
        //			scopeContext= new IScopeContext[] { new ProjectScope(file.getProject()) };
        //			String lineDelimiter= Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null,
        // scopeContext);
        //			if (lineDelimiter != null)
        //				return lineDelimiter;
        //		}
        //		// workspace preference
        //		scopeContext= new IScopeContext[] { InstanceScope.INSTANCE };
        //		return Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null, scopeContext);
        return null;
    }

    protected String getLineDelimiterPreference(IPath location, LocationKind locationKind) {
        IFile file = null;
        if (locationKind != LocationKind.LOCATION) {
            file = FileBuffers.getWorkspaceFileAtLocation(location);
        }
        return getLineDelimiterPreference(file);
    }

    /*
     * @see org.eclipse.core.filebuffers.IFileBufferManager#validateState(org.eclipse.core.filebuffers.IFileBuffer[],
     * org.eclipse.core.runtime.IProgressMonitor, java.lang.Object)
     * @since 3.1
     */
    public void validateState(final IFileBuffer[] fileBuffers, IProgressMonitor monitor,
                              final Object computationContext) throws CoreException {
        IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
            public void run(IProgressMonitor progressMonitor) throws CoreException {
                IFileBuffer[] toValidate = findFileBuffersToValidate(fileBuffers);
                validationStateAboutToBeChanged(toValidate);
                try {
                    IStatus status = validateEdit(toValidate, computationContext);
                    validationStateChanged(toValidate, true, status);
                } catch (RuntimeException x) {
                    validationStateChangedFailed(toValidate);
                }
            }
        };
        ResourcesPlugin.getWorkspace().run(runnable, computeValidateStateRule(fileBuffers), IWorkspace.AVOID_UPDATE,
                                           monitor);
    }

    private IStatus validateEdit(IFileBuffer[] fileBuffers, Object computationContext) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < fileBuffers.length; i++) {
            IFile file = getWorkspaceFile(fileBuffers[i]);
            if (file != null) {
                list.add(file);
            }
        }
        IFile[] files = new IFile[list.size()];
        list.toArray(files);
        return ResourcesPlugin.getWorkspace().validateEdit(files, computationContext);
    }

    private IFileBuffer[] findFileBuffersToValidate(IFileBuffer[] fileBuffers) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < fileBuffers.length; i++) {
            if (!fileBuffers[i].isStateValidated()) {
                list.add(fileBuffers[i]);
            }
        }
        return (IFileBuffer[])list.toArray(new IFileBuffer[list.size()]);
    }

    private void validationStateAboutToBeChanged(IFileBuffer[] fileBuffers) {
        for (int i = 0; i < fileBuffers.length; i++) {
            if (fileBuffers[i] instanceof IStateValidationSupport) {
                IStateValidationSupport support = (IStateValidationSupport)fileBuffers[i];
                support.validationStateAboutToBeChanged();
            }
        }
    }

    private void validationStateChanged(IFileBuffer[] fileBuffers, boolean validationState, IStatus status) {
        for (int i = 0; i < fileBuffers.length; i++) {
            if (fileBuffers[i] instanceof IStateValidationSupport) {
                IStateValidationSupport support = (IStateValidationSupport)fileBuffers[i];
                support.validationStateChanged(validationState, status);
            }
        }
    }

    private void validationStateChangedFailed(IFileBuffer[] fileBuffers) {
        for (int i = 0; i < fileBuffers.length; i++) {
            if (fileBuffers[i] instanceof IStateValidationSupport) {
                IStateValidationSupport support = (IStateValidationSupport)fileBuffers[i];
                support.validationStateChangeFailed();
            }
        }
    }

    private IFile getWorkspaceFile(IFileBuffer fileBuffer) {
        return FileBuffers.getWorkspaceFileAtLocation(fileBuffer.getLocation());
    }

    private ISchedulingRule computeValidateStateRule(IFileBuffer[] fileBuffers) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < fileBuffers.length; i++) {
            IResource resource = getWorkspaceFile(fileBuffers[i]);
            if (resource != null) {
                list.add(resource);
            }
        }
        IResource[] resources = new IResource[list.size()];
        list.toArray(resources);
        IResourceRuleFactory factory = ResourcesPlugin.getWorkspace().getRuleFactory();
        return factory.validateEditRule(resources);
    }

}
