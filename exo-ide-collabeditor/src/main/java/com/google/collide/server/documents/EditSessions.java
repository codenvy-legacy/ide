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

import com.google.collide.dto.*;
import com.google.collide.dto.server.DtoServerImpls.*;
import com.google.collide.server.WSUtil;
import com.google.collide.server.participants.Participants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.everrest.websockets.WSConnection;
import org.everrest.websockets.WSConnectionContext;
import org.exoplatform.ide.commons.StringUtils;
import org.exoplatform.ide.json.server.JsonArrayListAdapter;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.*;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;
import org.picocontainer.Startable;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

public class EditSessions implements Startable {

    private ChangeEventFilter eventFilter;

    private class WSConnectionListener implements org.everrest.websockets.WSConnectionListener {
        @Override
        public void onOpen(WSConnection connection) {
        }

        @Override
        public void onClose(WSConnection connection) {
            String userId = connection.getHttpSession().getId();
            closeAllSessions(userId);
//         participants.removeParticipant(userId);
        }
    }

    private static final Log LOG = ExoLogger.getLogger(EditSessions.class);

    private static final Gson gson = new GsonBuilder().registerTypeAdapter(DocOpComponentImpl.class,
                                                                           new DocOpComponentDeserializer()).serializeNulls().create();

    private final Participants participants;

    private final VirtualFileSystemRegistry vfsRegistry;

    private EventListenerList listenerList;

    private final ScheduledExecutorService saveScheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Receives Document operations and applies them to the corresponding FileEditSession.
     * <p/>
     * If there is no associated FileEditSession, we need log an error since that probably means we have a stale client.
     */
    private final SelectionTracker selectionTracker = new SelectionTracker();

    // This is important mapping for server side to avoid mapping the same resource twice.
    private final ConcurrentMap<String, FileEditSession> editSessionsByResourceId = new ConcurrentHashMap<String, FileEditSession>();

    private WSConnectionListener listener = new WSConnectionListener();

    public EditSessions(Participants participants, VirtualFileSystemRegistry vfsRegistry, EventListenerList listenerList) {
        this.participants = participants;
        this.vfsRegistry = vfsRegistry;
        this.listenerList = listenerList;
    }

    private class SaveTask implements Runnable {
        // Use system credentials to save file content.
        final ConversationState state = new ConversationState(new Identity(IdentityConstants.SYSTEM));

        @Override
        public void run() {
            try {
                ConversationState.setCurrent(state);
                for (FileEditSession editSession : editSessionsByResourceId.values()) {
                    try {
                        if (editSession.hasChanges()) {
                            editSession.save();
                        }
                    } catch (Exception e) {
                        LOG.debug(e.getMessage(), e);
                    }
                }
            } finally {
                ConversationState.setCurrent(null);
            }
        }
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

    private void addVfsListener(String vfsId, String path,String  itemId) {
        //renamed or moved
        eventFilter = ChangeEventFilter.createOrFilter(new TypeFilter(ChangeEvent.ChangeType.RENAMED),
                                                       new TypeFilter(ChangeEvent.ChangeType.MOVED));

        ChangeEventFilter filter = ChangeEventFilter.createAndFilter(new VfsIDFilter(vfsId), new PathFilter(path), eventFilter);
        listenerList.addEventListener(filter, new InternalListener(itemId, vfsId));
    }

    public GetFileContentsResponse openSession(GetFileContents contentsRequest) {
        final String vfsId = contentsRequest.getWorkspaceId();
        final String path = contentsRequest.getPath();
        final String userId = contentsRequest.getClientId();

        FileEditSession editSession;
        final String resourceId;

        try {
            VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, listenerList);
            org.exoplatform.ide.vfs.shared.File file = (org.exoplatform.ide.vfs.shared.File)vfs.getItemByPath(path, null,
                                                                                                              PropertyFilter.NONE_FILTER);
            resourceId = file.getId();
            editSession = editSessionsByResourceId.get(resourceId);
            if (editSession == null) {
                String text = loadFileContext(vfs, resourceId);
                FileEditSession newEditSession = new FileEditSessionImpl(UUID.randomUUID().toString(), vfs,listenerList, resourceId,
                                                                         path, file.getMimeType(), text, null);
                addVfsListener(vfsId, path, resourceId);
                editSession = editSessionsByResourceId.putIfAbsent(resourceId, newEditSession);
                if (editSession == null) {
                    editSession = newEditSession;
                }
            }
        } catch (VirtualFileSystemException e) {
            LOG.error(e.getMessage(), e);
            return GetFileContentsResponseImpl.make().setFileExists(false);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            return GetFileContentsResponseImpl.make().setFileExists(false);
        }

        if (!editSession.getCollaborators().contains(userId)) {
            editSession.addCollaborator(userId);
            Set<String> sendTo = new LinkedHashSet<String>();
            sendTo.addAll(participants.getAllParticipantId());
            sendTo.remove(userId);
            if (!sendTo.isEmpty()) {
                WSUtil.broadcastToClients(NewFileCollaboratorImpl.make().setPath(path).setEditSessionId(
                        editSession.getFileEditSessionKey()).setParticipant(participants.getParticipant(userId)).toJson(),
                                          sendTo);
            }
        }
        FileContentsImpl fileContents = FileContentsImpl.make().setPath(path).setFileEditSessionKey(
                editSession.getFileEditSessionKey()).setCcRevision(editSession.getDocument().getCcRevision()).setContents(
                editSession.getContents()).setContentType(FileContents.ContentType.TEXT);
        return GetFileContentsResponseImpl.make().setFileExists(true).setFileContents(fileContents);
    }

    private FileEditSession findEditSession(String editSessionId) {
        // Client required to have different 'session key' even for the same resources :( . Since mapping resourceId to
        // editSession is more important for us we keep only this mapping and lookup session by id if need.
        for (FileEditSession editSession : editSessionsByResourceId.values()) {
            if (editSessionId.equals(editSession.getFileEditSessionKey())) {
                return editSession;
            }
        }
        return null;
    }

    public void closeSession(CloseEditor closeMessage) {
        final String editSessionId = closeMessage.getFileEditSessionKey();
        final String userId = closeMessage.getClientId();
        FileEditSession editSession = findEditSession(editSessionId);
        if (editSession != null) {
            try {
                if (editSession.hasChanges()) {
                    editSession.save();
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }

            if (editSession.removeCollaborator(userId)) {
                Set<String> sendTo = new LinkedHashSet<String>();
                sendTo.addAll(participants.getAllParticipantId());
                sendTo.remove(userId);
                if (!sendTo.isEmpty()) {
                    WSUtil.broadcastToClients(FileCollaboratorGoneImpl.make().setPath(editSession.getPath()).setParticipant(
                            participants.getParticipant(userId)).toJson(), sendTo);
                }
                LOG.debug("Close edit session {}, user {} ", closeMessage.getFileEditSessionKey(),
                          closeMessage.getClientId());
            }
            if (editSession.getCollaborators().isEmpty()) {
                editSessionsByResourceId.remove(editSession.getResourceId());
            }
        }
    }

    public List<String> closeAllSessions(String userId) {
        List<String> result = new ArrayList<String>();
        Set<String> sendTo = new LinkedHashSet<String>();
        Collection<? extends String> allParticipantId = participants.getAllParticipantId(userId);
        if (allParticipantId == null) {
            return Collections.emptyList();
        }
        sendTo.addAll(allParticipantId);
        sendTo.remove(userId);

        for (Map.Entry<String, FileEditSession> e : editSessionsByResourceId.entrySet()) {
            FileEditSession editSession = e.getValue();
            if (editSession.removeCollaborator(userId)) {
                result.add(e.getKey());
                if (!sendTo.isEmpty()) {
                    WSUtil.broadcastToClients(FileCollaboratorGoneImpl.make().setPath(editSession.getPath()).setParticipant(
                            participants.getParticipant(userId)).toJson(), sendTo);
                }
                LOG.debug("Close edit session {}, user {} ", editSession.getFileEditSessionKey(), userId);
                if (editSession.getCollaborators().isEmpty()) {
                    editSessionsByResourceId.remove(editSession.getResourceId());
                }
            }
        }
        return result;
    }

    private String loadFileContext(VirtualFileSystem vfs,
                                   String resourceId) throws VirtualFileSystemException, IOException {
        InputStream input = null;
        try {
            ContentStream content = vfs.getContent(resourceId);
            input = content.getStream();
            return StringUtils.toString(input);
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
        String resourceId = docOpRequest.getFileEditSessionKey();
        FileEditSession editSession = findEditSession(resourceId);
        List<String> docOps = ((JsonArrayListAdapter<String>)docOpRequest.getDocOps2()).asList();
        return applyMutation(docOps, docOpRequest.getClientId(), docOpRequest.getCcRevision(),
                             docOpRequest.getSelection(), docOpRequest.getWorkspaceId(), resourceId, editSession);
    }

    private List<DocOp> deserializeDocOps(List<String> serializedDocOps) {
        List<DocOp> docOps = new ArrayList<DocOp>();
        for (String serializedDocOp : serializedDocOps) {
            docOps.add(gson.fromJson(serializedDocOp, DocOpImpl.class));
        }
        return docOps;
    }

    private ServerToClientDocOpsImpl applyMutation(List<String> serializedDocOps, String authorId, int ccRevision,
                                                   DocumentSelection selection, String workspaceId, String resourceId,
                                                   FileEditSession editSession) {
        ServerToClientDocOpsImpl broadcastedDocOps = ServerToClientDocOpsImpl.make();

        try {
            List<DocOp> docOps = deserializeDocOps(serializedDocOps);
            VersionedDocument.ConsumeResult result = editSession.consume(docOps, authorId, ccRevision, selection);

            if (result == null) {
                return broadcastedDocOps;
            }

            // See if we need to update the selection
            checkForSelectionChange(authorId, resourceId, editSession.getDocument(), result.transformedDocumentSelection);

            // Construct the Applied DocOp that we want to broadcast.
            SortedMap<Integer, VersionedDocument.AppliedDocOp> appliedDocOps = result.appliedDocOps;
            List<ServerToClientDocOpImpl> appliedDocOpsList = new ArrayList<ServerToClientDocOpImpl>();
            for (Map.Entry<Integer, VersionedDocument.AppliedDocOp> entry : appliedDocOps.entrySet()) {
                DocOpImpl docOp = (DocOpImpl)entry.getValue().docOp;
                ServerToClientDocOpImpl wrappedBroadcastDocOp = ServerToClientDocOpImpl.make().setClientId(
                        authorId).setAppliedCcRevision(entry.getKey()).setDocOp2(docOp).setWorkspaceId(
                        workspaceId).setFileEditSessionKey(resourceId).setFilePath(editSession.getPath());
                appliedDocOpsList.add(wrappedBroadcastDocOp);
            }

            // Add the selection to the last DocOp if there was one.
            if (result.transformedDocumentSelection != null && appliedDocOpsList.size() > 0) {
                appliedDocOpsList.get(appliedDocOpsList.size() - 1).setSelection(
                        (DocumentSelectionImpl)result.transformedDocumentSelection);
            }

            // Broadcast the applied DocOp all the participants, ignoring the sender.
            broadcastedDocOps.setDocOps(appliedDocOpsList);

            Set<String> sendTo = new LinkedHashSet<String>();
            sendTo.addAll(editSession.getCollaborators());
            sendTo.remove(authorId);
            if (!sendTo.isEmpty()) {
                WSUtil.broadcastToClients(broadcastedDocOps.toJson(), sendTo);
            }
            return broadcastedDocOps;
        } catch (VersionedDocument.DocumentOperationException e) {
            LOG.debug(e.getMessage(), e);
        }
        return broadcastedDocOps;
    }

    private void checkForSelectionChange(String clientId, String resourceId, VersionedDocument document,
                                         DocumentSelection documentSelection) {
      /*
       * Currently, doc ops either contain text changes or selection changes (via annotation doc op
       * components). Both of these modify the user's selection/cursor.
       */
        selectionTracker.selectionChanged(clientId, resourceId, document, documentSelection);
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

    public GetEditSessionCollaboratorsResponse getEditSessionCollaborators(
            GetEditSessionCollaborators sessionParticipantsRequest) {
        FileEditSession editSession = findEditSession(sessionParticipantsRequest.getEditSessionId());
        return GetEditSessionCollaboratorsResponseImpl.make().setParticipants(
                participants.getParticipants(editSession.getCollaborators()));
    }

    public GetOpenedFilesInWorkspaceResponse getOpenedFiles() {
        GetOpenedFilesInWorkspaceResponseImpl response = GetOpenedFilesInWorkspaceResponseImpl.make();
        for (FileEditSession session : editSessionsByResourceId.values()) {

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

    private class InternalListener implements org.exoplatform.ide.vfs.server.observation.EventListener {
        private String itemId;
        private String vfsId;

        public InternalListener(String itemId, String vfsId) {
            this.itemId = itemId;
            this.vfsId = vfsId;
        }

        @Override
        public void handleEvent(ChangeEvent event) throws VirtualFileSystemException {
            FileEditSession fileEditSession = editSessionsByResourceId.remove(itemId);
            fileEditSession.setPath(event.getItemPath());
            fileEditSession.setResourceId(event.getItemId());
            editSessionsByResourceId.putIfAbsent(event.getItemId(), fileEditSession);
            listenerList.removeEventListener(eventFilter, this);
            addVfsListener(vfsId,event.getItemPath(), event.getItemId());
        }
    }
}
