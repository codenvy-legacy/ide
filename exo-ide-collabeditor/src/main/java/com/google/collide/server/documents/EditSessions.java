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

import com.google.collide.dto.ClientToServerDocOp;
import com.google.collide.dto.CloseEditor;
import com.google.collide.dto.DocOp;
import com.google.collide.dto.DocumentSelection;
import com.google.collide.dto.FileContents;
import com.google.collide.dto.GetEditSessionCollaborators;
import com.google.collide.dto.GetEditSessionCollaboratorsResponse;
import com.google.collide.dto.GetFileContents;
import com.google.collide.dto.GetFileContentsResponse;
import com.google.collide.dto.GetOpenendFilesInWorkspaceResponse;
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
import com.google.collide.dto.server.DtoServerImpls.GetOpenendFilesInWorkspaceResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.NewFileCollaboratorImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantUserDetailsImpl;
import com.google.collide.dto.server.DtoServerImpls.RecoverFromMissedDocOpsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.ServerToClientDocOpImpl;
import com.google.collide.dto.server.DtoServerImpls.ServerToClientDocOpsImpl;
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
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;
import org.picocontainer.Startable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EditSessions implements Startable
{
   private class WSConnectionListener implements org.everrest.websockets.WSConnectionListener
   {
      @Override
      public void onOpen(WSConnection connection)
      {
      }

      @Override
      public void onClose(WSConnection connection)
      {
         String userId = connection.getHttpSession().getId();
         closeAllSessions(userId);
//         participants.removeParticipant(userId);
      }
   }

   private static final Log LOG = ExoLogger.getLogger(EditSessions.class);

   private static final Gson gson = new GsonBuilder().registerTypeAdapter(
      DocOpComponentImpl.class, new DocOpComponentDeserializer()).serializeNulls().create();

   private final Participants participants;
   private final VirtualFileSystemRegistry vfsRegistry;
   private final ScheduledExecutorService saveScheduler = Executors.newSingleThreadScheduledExecutor();

   /**
    * Receives Document operations and applies them to the corresponding FileEditSession.
    * <p/>
    * If there is no associated FileEditSession, we need log an error since that probably means we have a stale client.
    */
   private final SelectionTracker selectionTracker = new SelectionTracker();
   // This is important mapping for server side to avoid mapping the same resource twice.
   private final ConcurrentMap<String, FileEditSession> editSessionsByResourceId =
      new ConcurrentHashMap<String, FileEditSession>();

   private WSConnectionListener listener = new WSConnectionListener();

   public EditSessions(Participants participants, VirtualFileSystemRegistry vfsRegistry)
   {
      this.participants = participants;
      this.vfsRegistry = vfsRegistry;
   }

   private class SaveTask implements Runnable
   {
      // Use system credentials to save file content.
      final ConversationState state = new ConversationState(new Identity(IdentityConstants.SYSTEM));

      @Override
      public void run()
      {
         try
         {
            ConversationState.setCurrent(state);
            for (FileEditSession editSession : editSessionsByResourceId.values())
            {
               try
               {
                  if (editSession.hasChanges())
                  {
                     editSession.save();
                  }
               }
               catch (Exception e)
               {
                  LOG.error(e.getMessage(), e);
               }
            }
         }
         finally
         {
            ConversationState.setCurrent(null);
         }
      }
   }

   @Override
   public void start()
   {
      saveScheduler.scheduleAtFixedRate(new SaveTask(), 2500, 2500, TimeUnit.MILLISECONDS);
      listener = new WSConnectionListener();
      WSConnectionContext.registerConnectionListener(listener);
   }

   @Override
   public void stop()
   {
      saveScheduler.shutdownNow();
      WSConnectionContext.removeConnectionListener(listener);
   }

   public GetFileContentsResponse openSession(GetFileContents contentsRequest)
   {
      final String vfsId = contentsRequest.getWorkspaceId();
      final String path = contentsRequest.getPath();
      final String userId = contentsRequest.getClientId();

      FileEditSession editSession;
      final String resourceId;

      try
      {
         VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
         org.exoplatform.ide.vfs.shared.File file =
            (org.exoplatform.ide.vfs.shared.File)vfs.getItemByPath(path, null, PropertyFilter.NONE_FILTER);
         resourceId = file.getId();
         editSession = editSessionsByResourceId.get(resourceId);
         if (editSession == null)
         {
            String text = loadFileContext(vfs, resourceId);
            FileEditSession newEditSession =
               new FileEditSessionImpl(UUID.randomUUID().toString(), vfs, resourceId, path, file.getMimeType(), text, null);
            editSession = editSessionsByResourceId.putIfAbsent(resourceId, newEditSession);
            if (editSession == null)
            {
               editSession = newEditSession;
            }
         }
      }
      catch (VirtualFileSystemException e)
      {
         LOG.error(e.getMessage(), e);
         return GetFileContentsResponseImpl.make().setFileExists(false);
      }
      catch (IOException e)
      {
         LOG.error(e.getMessage(), e);
         return GetFileContentsResponseImpl.make().setFileExists(false);
      }

      if (!editSession.getCollaborators().contains(userId))
      {
         editSession.addCollaborator(userId);
         Set<String> sendTo = new LinkedHashSet<String>();
         sendTo.addAll(participants.getAllParticipantId());
         sendTo.remove(userId);
         if (!sendTo.isEmpty())
         {
            WSUtil.broadcastToClients(
               NewFileCollaboratorImpl.make().setPath(path).setParticipant(participants.getParticipant(userId)).toJson(),
               sendTo
            );
         }
      }
      FileContentsImpl fileContents = FileContentsImpl.make()
         .setPath(path)
         .setFileEditSessionKey(editSession.getFileEditSessionKey())
         .setCcRevision(editSession.getDocument().getCcRevision())
         .setContents(editSession.getContents())
         .setContentType(FileContents.ContentType.TEXT);
      return GetFileContentsResponseImpl.make().setFileExists(true).setFileContents(fileContents);
   }

   private FileEditSession findEditSession(String editSessionId)
   {
      // Client required to have different 'session key' even for the same resources :( . Since mapping resourceId to
      // editSession is more important for us we keep only this mapping and lookup session by id if need.
      for (FileEditSession editSession : editSessionsByResourceId.values())
      {
         if (editSessionId.equals(editSession.getFileEditSessionKey()))
         {
            return editSession;
         }
      }
      return null;
   }

   public void closeSession(CloseEditor closeMessage)
   {
      final String editSessionId = closeMessage.getFileEditSessionKey();
      final String userId = closeMessage.getClientId();
      FileEditSession editSession = findEditSession(editSessionId);
      if (editSession != null)
      {
         try
         {
            if (editSession.hasChanges())
            {
               editSession.save();
            }
         }
         catch (Exception e)
         {
            LOG.error(e.getMessage(), e);
         }

         if (editSession.removeCollaborator(userId))
         {
            Set<String> sendTo = new LinkedHashSet<String>();
            sendTo.addAll(participants.getAllParticipantId());
            sendTo.remove(userId);
            if (!sendTo.isEmpty())
            {
               WSUtil.broadcastToClients(
                  FileCollaboratorGoneImpl.make().setPath(editSession.getPath())
                     .setParticipant(participants.getParticipant(userId)).toJson(),
                  sendTo
               );
            }
            LOG.debug("Close edit session {}, user {} ", closeMessage.getFileEditSessionKey(), closeMessage.getClientId());
         }
         if (editSession.getCollaborators().isEmpty())
         {
            editSessionsByResourceId.remove(editSession.getResourceId());
         }
      }
   }

   public List<String> closeAllSessions(String userId)
   {
      List<String> result = new ArrayList<String>();
      Set<String> sendTo = new LinkedHashSet<String>();
      sendTo.addAll(participants.getAllParticipantId());
      sendTo.remove(userId);

      for (Map.Entry<String, FileEditSession> e : editSessionsByResourceId.entrySet())
      {
         FileEditSession editSession = e.getValue();
         if (editSession.removeCollaborator(userId))
         {
            result.add(e.getKey());
            if (!sendTo.isEmpty())
            {
               WSUtil.broadcastToClients(
                  FileCollaboratorGoneImpl.make().setPath(editSession.getPath())
                     .setParticipant(participants.getParticipant(userId)).toJson(),
                  sendTo
               );
            }
            LOG.debug("Close edit session {}, user {} ", editSession.getFileEditSessionKey(), userId);
            if (editSession.getCollaborators().isEmpty())
            {
               editSessionsByResourceId.remove(editSession.getResourceId());
            }
         }
      }
      return result;
   }

   private String loadFileContext(VirtualFileSystem vfs, String resourceId)
      throws VirtualFileSystemException, IOException
   {
      InputStream input = null;
      try
      {
         ContentStream content = vfs.getContent(resourceId);
         input = content.getStream();
         return StringUtils.toString(input);
      }
      finally
      {
         if (input != null)
         {
            try
            {
               input.close();
            }
            catch (IOException ignored)
            {
            }
         }
      }
   }

   public ServerToClientDocOps mutate(ClientToServerDocOp docOpRequest)
   {
      String resourceId = docOpRequest.getFileEditSessionKey();
      FileEditSession editSession = findEditSession(resourceId);
      List<String> docOps = ((JsonArrayListAdapter<String>)docOpRequest.getDocOps2()).asList();
      return applyMutation(
         docOps,
         docOpRequest.getClientId(),
         docOpRequest.getCcRevision(),
         docOpRequest.getSelection(),
         docOpRequest.getWorkspaceId(),
         resourceId,
         editSession
      );
   }

   private List<DocOp> deserializeDocOps(List<String> serializedDocOps)
   {
      List<DocOp> docOps = new ArrayList<DocOp>();
      for (String serializedDocOp : serializedDocOps)
      {
         docOps.add(gson.fromJson(serializedDocOp, DocOpImpl.class));
      }
      return docOps;
   }

   private ServerToClientDocOpsImpl applyMutation(List<String> serializedDocOps,
                                                  String authorId,
                                                  int ccRevision,
                                                  DocumentSelection selection,
                                                  String workspaceId,
                                                  String resourceId,
                                                  FileEditSession editSession)
   {
      ServerToClientDocOpsImpl broadcastedDocOps = ServerToClientDocOpsImpl.make();

      try
      {
         List<DocOp> docOps = deserializeDocOps(serializedDocOps);
         VersionedDocument.ConsumeResult result = editSession.consume(docOps, authorId, ccRevision, selection);

         if (result == null)
         {
            return broadcastedDocOps;
         }

         // See if we need to update the selection
         checkForSelectionChange(authorId, resourceId, editSession.getDocument(), result.transformedDocumentSelection);

         // Construct the Applied DocOp that we want to broadcast.
         SortedMap<Integer, VersionedDocument.AppliedDocOp> appliedDocOps = result.appliedDocOps;
         List<ServerToClientDocOpImpl> appliedDocOpsList = new ArrayList<ServerToClientDocOpImpl>();
         for (Map.Entry<Integer, VersionedDocument.AppliedDocOp> entry : appliedDocOps.entrySet())
         {
            DocOpImpl docOp = (DocOpImpl)entry.getValue().docOp;
            ServerToClientDocOpImpl wrappedBroadcastDocOp = ServerToClientDocOpImpl.make()
               .setClientId(authorId)
               .setAppliedCcRevision(entry.getKey())
               .setDocOp2(docOp)
               .setWorkspaceId(workspaceId)
               .setFileEditSessionKey(resourceId)
               .setFilePath(editSession.getPath());
            appliedDocOpsList.add(wrappedBroadcastDocOp);
         }

         // Add the selection to the last DocOp if there was one.
         if (result.transformedDocumentSelection != null && appliedDocOpsList.size() > 0)
         {
            appliedDocOpsList.get(appliedDocOpsList.size() - 1)
               .setSelection((DocumentSelectionImpl)result.transformedDocumentSelection);
         }

         // Broadcast the applied DocOp all the participants, ignoring the sender.
         broadcastedDocOps.setDocOps(appliedDocOpsList);

         Set<String> sendTo = new LinkedHashSet<String>();
         sendTo.addAll(editSession.getCollaborators());
         sendTo.remove(authorId);
         if (!sendTo.isEmpty())
         {
            WSUtil.broadcastToClients(broadcastedDocOps.toJson(), sendTo);
         }
         return broadcastedDocOps;
      }
      catch (VersionedDocument.DocumentOperationException e)
      {
         LOG.error(e.getMessage(), e);
      }
      return broadcastedDocOps;
   }

   private void checkForSelectionChange(String clientId, String resourceId,
                                        VersionedDocument document, DocumentSelection documentSelection)
   {
      /*
       * Currently, doc ops either contain text changes or selection changes (via annotation doc op
       * components). Both of these modify the user's selection/cursor.
       */
      selectionTracker.selectionChanged(clientId, resourceId, document, documentSelection);
   }

   public RecoverFromMissedDocOpsResponse recoverDocOps(RecoverFromMissedDocOps missedDocOpsRequest)
   {
      String editSessionId = missedDocOpsRequest.getFileEditSessionKey();
      FileEditSession editSession = findEditSession(editSessionId);
      List<String> docOps = ((JsonArrayListAdapter<String>)missedDocOpsRequest.getDocOps2()).asList();

      // If the client is re-sending any unacked doc ops, apply them first
      if (missedDocOpsRequest.getDocOps2().size() > 0)
      {
         applyMutation(docOps,
            missedDocOpsRequest.getClientId(),
            missedDocOpsRequest.getCurrentCcRevision(),
            null,
            missedDocOpsRequest.getWorkspaceId(),
            editSessionId,
            editSession
         );
      }

      // Get all the applied doc ops the client doesn't know about
      SortedMap<Integer, VersionedDocument.AppliedDocOp> appliedDocOps =
         editSession.getDocument().getAppliedDocOps(missedDocOpsRequest.getCurrentCcRevision() + 1);

      List<ServerToClientDocOpImpl> appliedDocOpsList = new ArrayList<ServerToClientDocOpImpl>();
      for (Map.Entry<Integer, VersionedDocument.AppliedDocOp> entry : appliedDocOps.entrySet())
      {
         DocOpImpl docOp = (DocOpImpl)entry.getValue().docOp;
         ServerToClientDocOpImpl wrappedBroadcastDocOp = ServerToClientDocOpImpl.make()
            .setClientId(missedDocOpsRequest.getClientId()).setAppliedCcRevision(entry.getKey()).setDocOp2(docOp)
            .setFileEditSessionKey(editSessionId)
            .setFilePath(editSession.getPath());
         appliedDocOpsList.add(wrappedBroadcastDocOp);
      }

      return RecoverFromMissedDocOpsResponseImpl.make()
         .setDocOps(appliedDocOpsList)
         .setWorkspaceId(missedDocOpsRequest.getWorkspaceId());
   }

   public GetEditSessionCollaboratorsResponse getEditSessionCollaborators(
      GetEditSessionCollaborators sessionParticipantsRequest)
   {
      FileEditSession editSession = findEditSession(sessionParticipantsRequest.getEditSessionId());
      return GetEditSessionCollaboratorsResponseImpl.make()
         .setParticipants(participants.getParticipants(editSession.getCollaborators()));
   }

   public GetOpenendFilesInWorkspaceResponse getOpenendFiles()
   {
      GetOpenendFilesInWorkspaceResponseImpl response = GetOpenendFilesInWorkspaceResponseImpl.make();
      for(FileEditSession session : editSessionsByResourceId.values())
      {
        response.putOpenedFiles(session.getPath(),
           (ArrayList<ParticipantUserDetailsImpl>)participants.getParticipants(session.getCollaborators()));
      }
      return response;
   }

   public Set<String> getEditSessionCollaborators(String filePath)
   {
      FileEditSession editSession = findEditSessionByPath(filePath);
      if(editSession == null)
      {
         throw  new IllegalStateException("Can't find edit session for file: " + filePath);
      }
      return editSession.getCollaborators();
   }

   private FileEditSession findEditSessionByPath(String filePath)
   {
      // Client required to have different 'session key' even for the same resources :( . Since mapping resourceId to
      // editSession is more important for us we keep only this mapping and lookup session by path if need.
      for (FileEditSession editSession : editSessionsByResourceId.values())
      {
         if (filePath.equals(editSession.getPath()))
         {
            return editSession;
         }
      }
      return null;
   }
}
