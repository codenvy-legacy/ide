// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.server.documents;

import com.codenvy.commons.lang.IoUtil;
import com.codenvy.ide.dtogen.server.ServerErrorImpl;
import com.codenvy.ide.dtogen.shared.ServerError;
import com.codenvy.ide.json.server.JsonArrayListAdapter;
import com.google.collide.dto.ClientToServerDocOp;
import com.google.collide.dto.CloseEditor;
import com.google.collide.dto.DocOp;
import com.google.collide.dto.DocumentSelection;
import com.google.collide.dto.FileContents;
import com.google.collide.dto.GetEditSessionCollaborators;
import com.google.collide.dto.GetEditSessionCollaboratorsResponse;
import com.google.collide.dto.GetFileContents;
import com.google.collide.dto.GetFileContentsResponse;
import com.google.collide.dto.GetOpenedFilesInWorkspaceResponse;
import com.google.collide.dto.RecoverFromMissedDocOps;
import com.google.collide.dto.RecoverFromMissedDocOpsResponse;
import com.google.collide.dto.ServerToClientDocOps;
import com.google.collide.dto.server.DtoServerImpls.DocOpComponentImpl;
import com.google.collide.dto.server.DtoServerImpls.DocOpImpl;
import com.google.collide.dto.server.DtoServerImpls.DocumentSelectionImpl;
import com.google.collide.dto.server.DtoServerImpls.FileCollaboratorGoneImpl;
import com.google.collide.dto.server.DtoServerImpls.FileContentsImpl;
import com.google.collide.dto.server.DtoServerImpls.GetEditSessionCollaboratorsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.GetFileContentsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.GetOpenedFilesInWorkspaceResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.NewFileCollaboratorImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantUserDetailsImpl;
import com.google.collide.dto.server.DtoServerImpls.RecoverFromMissedDocOpsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.ServerToClientDocOpImpl;
import com.google.collide.dto.server.DtoServerImpls.ServerToClientDocOpsImpl;
import com.google.collide.server.CollaborationEditorException;
import com.google.collide.server.WSUtil;
import com.google.collide.server.participants.LoggedInUser;
import com.google.collide.server.participants.Participants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.everrest.websockets.WSConnection;
import org.everrest.websockets.WSConnectionContext;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.PathFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.VfsIDFilter;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EditSessions implements Startable {

    private static final Log  LOG  = ExoLogger.getLogger(EditSessions.class);
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(DocOpComponentImpl.class,
                                                                           new DocOpComponentDeserializer()).serializeNulls().create();
    private final Participants              participants;
    private final VirtualFileSystemRegistry vfsRegistry;
    private final ScheduledExecutorService                saveScheduler             = Executors.newSingleThreadScheduledExecutor();
    /**
     * Receives Document operations and applies them to the corresponding FileEditSession.
     * <p/>
     * If there is no associated FileEditSession, we need log an error since that probably means we have a stale client.
     */
//    private final SelectionTracker                        selectionTracker         = new SelectionTracker();
    // This is important mapping for server side to avoid mapping the same resource twice.
    private final ConcurrentMap<String, FileEditSession>  editSessions              = new ConcurrentHashMap<String, FileEditSession>();
    private final ConcurrentMap<String, InternalListener> listenersByEditSessionsId = new ConcurrentHashMap<String, InternalListener>();
    private EventListenerList listenerList;
    private WSConnectionListener listener = new WSConnectionListener();

    public EditSessions(Participants participants, VirtualFileSystemRegistry vfsRegistry, EventListenerList listenerList) {
        this.participants = participants;
        this.vfsRegistry = vfsRegistry;
        this.listenerList = listenerList;
    }

    @Override
    public void start() {
        saveScheduler.scheduleAtFixedRate(new SaveTask(), 2500, 2500, TimeUnit.MILLISECONDS);
        listener = new WSConnectionListener();
        WSConnectionContext.registerConnectionListener(listener);
    }

    @Override
    public void stop() {
        saveScheduler.shutdownNow();
        WSConnectionContext.removeConnectionListener(listener);
    }

    public GetFileContentsResponse openSession(GetFileContents contentsRequest) {
        final String vfsId = contentsRequest.getWorkspaceId();
        final String path = contentsRequest.getPath();
        final String userId = contentsRequest.getClientId();

        FileEditSession editSession;

        try {
            VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, listenerList);
            org.exoplatform.ide.vfs.shared.File file =
                    (org.exoplatform.ide.vfs.shared.File)vfs.getItemByPath(path, null, false, PropertyFilter.NONE_FILTER);
            final String resourceId = file.getId();
            final String editSessionKey = file.getVfsId() + resourceId;
            editSession = editSessions.get(editSessionKey);
            if (editSession == null) {
                String text = loadFileContext(vfs, resourceId);
                FileEditSession newEditSession = new FileEditSessionImpl(editSessionKey,
                                                                         vfs,
                                                                         vfsId,
                                                                         resourceId,
                                                                         path,
                                                                         file.getMimeType(),
                                                                         text,
                                                                         null);
                editSession = editSessions.putIfAbsent(editSessionKey, newEditSession);
                if (editSession == null) {
                    editSession = newEditSession;
                    addVfsListener(vfsId, path, editSession);
                }
            }
        } catch (ItemNotFoundException e) {
            LOG.error(e.getMessage(), e);
            return GetFileContentsResponseImpl.make().setFileExists(false);
        } catch (VirtualFileSystemException | IOException e) {
            LOG.error(e.getMessage(), e);
            throw new CollaborationEditorException(e);
        }

        if (editSession.addCollaborator(userId)) {
            if (!participants.getUser(userId).isReadOnly()) {
                Set<String> sendTo =
                        new HashSet<String>(participants.getAllParticipantIds(((FileEditSessionImpl)editSession).getWorkspace()));
                sendTo.remove(userId);
                if (!sendTo.isEmpty()) {
                    WSUtil.broadcastToClients(NewFileCollaboratorImpl.make().setPath(path).setEditSessionId(
                            editSession.getFileEditSessionKey()).setParticipant(participants.getParticipant(userId)).toJson(), sendTo);
                }
            }
        }
        FileContentsImpl fileContents = FileContentsImpl.make().setPath(path).setFileEditSessionKey(
                editSession.getFileEditSessionKey()).setCcRevision(editSession.getDocument().getCcRevision()).setContents(
                editSession.getContents()).setContentType(FileContents.ContentType.TEXT);
        return GetFileContentsResponseImpl.make().setFileExists(true).setFileContents(fileContents);
    }

    private void addVfsListener(String vfsId, String path, FileEditSession fileEditSession) {
        ChangeEventFilter myFilter = ChangeEventFilter.createAndFilter(new VfsIDFilter(vfsId),
                                                                       new PathFilter(path),
                                                                       ChangeEventFilter.createOrFilter(
                                                                               new TypeFilter(ChangeEvent.ChangeType.RENAMED),
                                                                               new TypeFilter(ChangeEvent.ChangeType.MOVED)));
        InternalListener internalListener = new InternalListener(fileEditSession.getFileEditSessionKey(), myFilter);
        if (listenersByEditSessionsId.putIfAbsent(fileEditSession.getFileEditSessionKey(), internalListener) == null) {
            listenerList.addEventListener(myFilter, internalListener);
        }
    }

    private FileEditSession findEditSession(String editSessionId) {
        FileEditSession editSession = editSessions.get(editSessionId);
        if (editSession != null) {
            return editSession;
        }
        ServerErrorImpl error = ServerErrorImpl.make();
        error.setFailureReason(ServerError.FailureReason.MISSING_FILE_SESSION);
        error.setDetails(String.format("File edit session %s not found. ", editSessionId));
        throw new CollaborationEditorException(error);
    }

    public void closeSession(CloseEditor closeMessage) {
        final String editSessionId = closeMessage.getFileEditSessionKey();
        final String userId = closeMessage.getClientId();
        FileEditSession editSession = findEditSession(editSessionId);
        if (editSession != null) {
            Exception saveError = null;
            try {
                if (editSession.hasChanges()) {
                    editSession.save();
                }
            } catch (Exception e) {
                saveError = e;
                LOG.error(e.getMessage(), e);
            }

            if (editSession.removeCollaborator(userId)) {
                LoggedInUser user = participants.getUser(userId);
                // need send to all users in workspace
                Set<String> allParticipantIds = new HashSet<String>(participants.getAllParticipantIds(user.getWorkspace()));
                allParticipantIds.remove(userId);
                if (!allParticipantIds.isEmpty()) {
                    WSUtil.broadcastToClients(FileCollaboratorGoneImpl.make().setPath(editSession.getPath())
                                                                      .setParticipant(participants.getParticipant(userId)).toJson(),
                                              allParticipantIds);
                }
                LOG.debug("Close edit session {}, user {} ", closeMessage.getFileEditSessionKey(), closeMessage.getClientId());
            }
            if (editSession.getCollaborators().isEmpty()) {
                cleanUpEditSession(editSession);
            }
            if (saveError != null) {
                throw new CollaborationEditorException(saveError);
            }
        }
    }

    private void cleanUpEditSession(FileEditSession editSession) {
        editSessions.remove(editSession.getFileEditSessionKey());
        InternalListener internalListener = listenersByEditSessionsId.remove(editSession.getFileEditSessionKey());
        if (internalListener != null) {
            listenerList.removeEventListener(internalListener.myFilter, internalListener);
        }
    }

    public List<String> closeAllSessions(String userId) {
        List<String> result = new ArrayList<String>();
        for (Map.Entry<String, FileEditSession> e : editSessions.entrySet()) {
            FileEditSession editSession = e.getValue();
            if (editSession.removeCollaborator(userId)) {
                if (!editSession.getCollaborators().isEmpty()) {
                    WSUtil.broadcastToClients(FileCollaboratorGoneImpl.make().setPath(editSession.getPath())
                                                                      .setParticipant(participants.getParticipant(userId)).toJson(),
                                              editSession.getCollaborators());
                }
                LOG.debug("Close edit session {}, user {} ", editSession.getFileEditSessionKey(), userId);
            }
            if (editSession.getCollaborators().isEmpty()) {
                cleanUpEditSession(editSession);
            }
            result.add(e.getKey());
        }
        return result;
    }

    private String loadFileContext(VirtualFileSystem vfs, String resourceId) throws VirtualFileSystemException, IOException {
        InputStream input = null;
        try {
            ContentStream content = vfs.getContent(resourceId);
            input = content.getStream();
            return IoUtil.readStream(input);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public ServerToClientDocOps mutate(ClientToServerDocOp docOpRequest) {
        String docOpEditSessionKey = docOpRequest.getFileEditSessionKey();
        FileEditSession editSession = findEditSession(docOpEditSessionKey);
        List<String> docOps = ((JsonArrayListAdapter<String>)docOpRequest.getDocOps2()).asList();
        return applyMutation(docOps, docOpRequest.getClientId(), docOpRequest.getCcRevision(),
                             docOpRequest.getSelection(), docOpRequest.getWorkspaceId(), docOpEditSessionKey, editSession);
    }

    private List<DocOp> deserializeDocOps(List<String> serializedDocOps) {
        List<DocOp> docOps = new ArrayList<DocOp>();
        for (String serializedDocOp : serializedDocOps) {
            docOps.add(gson.fromJson(serializedDocOp, DocOpImpl.class));
        }
        return docOps;
    }

    private ServerToClientDocOpsImpl applyMutation(List<String> serializedDocOps,
                                                   String authorId,
                                                   int ccRevision,
                                                   DocumentSelection selection,
                                                   String workspaceId,
                                                   String docOpEditSessionKey,
                                                   FileEditSession editSession) {
        ServerToClientDocOpsImpl broadcastedDocOps = ServerToClientDocOpsImpl.make();

        VersionedDocument.ConsumeResult result;

//        if(participants.getUser(authorId).isAnonymous()){
//            return broadcastedDocOps;
//        }
        try {
            result = editSession.consume(deserializeDocOps(serializedDocOps), authorId, ccRevision, selection);
        } catch (VersionedDocument.DocumentOperationException e) {
            LOG.error(e.getMessage(), e);
            throw new CollaborationEditorException(e);
        }
        if (result == null) {
            return broadcastedDocOps;
        }

        // See if we need to update the selection
        checkForSelectionChange(authorId, docOpEditSessionKey, editSession.getDocument(), result.transformedDocumentSelection);

        // Construct the Applied DocOp that we want to broadcast.
        SortedMap<Integer, VersionedDocument.AppliedDocOp> appliedDocOps = result.appliedDocOps;
        List<ServerToClientDocOpImpl> appliedDocOpsList = new ArrayList<ServerToClientDocOpImpl>();
        for (Map.Entry<Integer, VersionedDocument.AppliedDocOp> entry : appliedDocOps.entrySet()) {
            DocOpImpl docOp = (DocOpImpl)entry.getValue().docOp;
            ServerToClientDocOpImpl wrappedBroadcastDocOp = ServerToClientDocOpImpl.make().setClientId(
                    authorId).setAppliedCcRevision(entry.getKey()).setDocOp2(docOp).setWorkspaceId(
                    workspaceId).setFileEditSessionKey(docOpEditSessionKey).setFilePath(editSession.getPath());
            appliedDocOpsList.add(wrappedBroadcastDocOp);
        }

        // Add the selection to the last DocOp if there was one.
        if (result.transformedDocumentSelection != null && appliedDocOpsList.size() > 0) {
            appliedDocOpsList.get(appliedDocOpsList.size() - 1).setSelection(
                    (DocumentSelectionImpl)result.transformedDocumentSelection);
        }

        // Broadcast the applied DocOp all the participants, ignoring the sender.
        broadcastedDocOps.setDocOps(appliedDocOpsList);

        Set<String> sendTo = new HashSet<String>(editSession.getCollaborators());
        sendTo.remove(authorId);
        if (!sendTo.isEmpty()) {
            WSUtil.broadcastToClients(broadcastedDocOps.toJson(), sendTo);
        }
        return broadcastedDocOps;
    }

    private void checkForSelectionChange(String clientId, String docOpEditSessionKey, VersionedDocument document,
                                         DocumentSelection documentSelection) {
      /*
       * Currently, doc ops either contain text changes or selection changes (via annotation doc op
       * components). Both of these modify the user's selection/cursor.
       */
//        selectionTracker.selectionChanged(clientId, docOpEditSessionKey, document, documentSelection);
    }

    public RecoverFromMissedDocOpsResponse recoverDocOps(RecoverFromMissedDocOps missedDocOpsRequest) {
        String editSessionId = missedDocOpsRequest.getFileEditSessionKey();
        FileEditSession editSession = findEditSession(editSessionId);
        List<String> docOps = ((JsonArrayListAdapter<String>)missedDocOpsRequest.getDocOps2()).asList();

        // If the client is re-sending any unacked doc ops, apply them first
        if (missedDocOpsRequest.getDocOps2().size() > 0) {
            applyMutation(docOps, missedDocOpsRequest.getClientId(), missedDocOpsRequest.getCurrentCcRevision(), null,
                          missedDocOpsRequest.getWorkspaceId(), editSessionId, editSession);
        }

        // Get all the applied doc ops the client doesn't know about
        SortedMap<Integer, VersionedDocument.AppliedDocOp> appliedDocOps = editSession.getDocument().getAppliedDocOps(
                missedDocOpsRequest.getCurrentCcRevision() + 1);

        List<ServerToClientDocOpImpl> appliedDocOpsList = new ArrayList<ServerToClientDocOpImpl>();
        for (Map.Entry<Integer, VersionedDocument.AppliedDocOp> entry : appliedDocOps.entrySet()) {
            DocOpImpl docOp = (DocOpImpl)entry.getValue().docOp;
            ServerToClientDocOpImpl wrappedBroadcastDocOp = ServerToClientDocOpImpl.make().setClientId(
                    missedDocOpsRequest.getClientId()).setAppliedCcRevision(entry.getKey()).setDocOp2(
                    docOp).setFileEditSessionKey(editSessionId).setFilePath(editSession.getPath());
            appliedDocOpsList.add(wrappedBroadcastDocOp);
        }

        return RecoverFromMissedDocOpsResponseImpl.make().setDocOps(appliedDocOpsList).setWorkspaceId(
                missedDocOpsRequest.getWorkspaceId());
    }

    public GetEditSessionCollaboratorsResponse getEditSessionCollaborators(GetEditSessionCollaborators sessionParticipantsRequest) {
        FileEditSession editSession = findEditSession(sessionParticipantsRequest.getEditSessionId());
        Set<String> collaborators = editSession.getCollaborators();
        return GetEditSessionCollaboratorsResponseImpl.make().setParticipants(
                participants.getParticipants(collaborators));
    }

    public GetOpenedFilesInWorkspaceResponse getOpenedFiles() {
        GetOpenedFilesInWorkspaceResponseImpl response = GetOpenedFilesInWorkspaceResponseImpl.make();
        for (FileEditSession session : editSessions.values()) {
            ArrayList<ParticipantUserDetailsImpl> list = (ArrayList<ParticipantUserDetailsImpl>)participants.getParticipants(
                    session.getCollaborators());
            if (!list.isEmpty()) {
                response.putOpenedFiles(session.getPath(), list);
            }
        }
        return response;
    }

    public Set<String> getEditSessionCollaborators(String ediSessionId) {
        FileEditSession editSession = findEditSession(ediSessionId);
        if (editSession == null) {
            throw new IllegalStateException("Can't find edit session: " + ediSessionId);
        }
        return editSession.getCollaborators();
    }

    private class WSConnectionListener implements org.everrest.websockets.WSConnectionListener {
        @Override
        public void onOpen(WSConnection connection) {
        }

        @Override
        public void onClose(WSConnection connection) {
            String userId = connection.getHttpSession().getId();
            closeAllSessions(userId);
        }
    }

    private class SaveTask implements Runnable {
        @Override
        public void run() {
            for (FileEditSession editSession : editSessions.values()) {
                try {
                    if (editSession.hasChanges()) {
                        editSession.save();
                    }
                } catch (Exception e) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
    }

    private class InternalListener implements org.exoplatform.ide.vfs.server.observation.EventListener {
        private final String            editSessionKey;
        private final ChangeEventFilter myFilter;

        InternalListener(String editSessionKey, ChangeEventFilter myFilter) {
            this.editSessionKey = editSessionKey;
            this.myFilter = myFilter;
        }

        @Override
        public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
            FileEditSession fileEditSession = editSessions.remove(editSessionKey);
            listenerList.removeEventListener(myFilter, this);
            if (fileEditSession != null) {
                listenersByEditSessionsId.remove(fileEditSession.getFileEditSessionKey());
                fileEditSession.setPath(event.getItemPath());
                fileEditSession.setResourceId(event.getItemId());
                editSessions.putIfAbsent(event.getItemId(), fileEditSession);
                addVfsListener(event.getVirtualFileSystem().getInfo().getId(), event.getItemPath(), fileEditSession);
            }
        }
    }
}
