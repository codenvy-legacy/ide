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
import com.google.collide.dto.GetEditSessionParticipants;
import com.google.collide.dto.GetEditSessionParticipantsResponse;
import com.google.collide.dto.GetFileContents;
import com.google.collide.dto.GetFileContentsResponse;
import com.google.collide.dto.RecoverFromMissedDocOps;
import com.google.collide.dto.RecoverFromMissedDocOpsResponse;
import com.google.collide.dto.ServerToClientDocOps;
import com.google.collide.dto.server.DtoServerImpls.DocOpComponentImpl;
import com.google.collide.dto.server.DtoServerImpls.DocOpImpl;
import com.google.collide.dto.server.DtoServerImpls.DocumentSelectionImpl;
import com.google.collide.dto.server.DtoServerImpls.FileContentsImpl;
import com.google.collide.dto.server.DtoServerImpls.GetEditSessionParticipantsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.GetFileContentsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.RecoverFromMissedDocOpsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.ServerToClientDocOpImpl;
import com.google.collide.dto.server.DtoServerImpls.ServerToClientDocOpsImpl;
import com.google.collide.json.server.JsonArrayListAdapter;
import com.google.collide.server.participants.Participants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.ide.commons.StringUtils;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EditSessions
{
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
   private final ConcurrentMap<String, FileEditSession> editSessions = new ConcurrentHashMap<String, FileEditSession>();

   public EditSessions(Participants participants, VirtualFileSystemRegistry vfsRegistry)
   {
      this.participants = participants;
      this.vfsRegistry = vfsRegistry;
      saveScheduler.scheduleAtFixedRate(new SaveTask(), 2500, 2500, TimeUnit.MILLISECONDS);
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
            for (FileEditSession editSession : editSessions.values())
            {
               try
               {
                  editSession.save();
               }
               catch (Exception e)
               {
                  System.out.printf("Failed to save file [%s] : %s\n", editSession.getSavedPath(), e);
               }
            }
         }
         finally
         {
            ConversationState.setCurrent(null);
         }
         // After save content may remove FileEditSession without collaborators.
         for (Iterator<FileEditSession> iterator = editSessions.values().iterator(); iterator.hasNext(); )
         {
            FileEditSession editSession = iterator.next();
            if (editSession.getCollaborators().isEmpty())
            {
               iterator.remove();
            }
         }
      }
   }

   public GetFileContentsResponse openSession(GetFileContents contentsRequest)
   {
      final String vfsId = contentsRequest.getWorkspaceId();
      final String path = contentsRequest.getPath();
      final String clientId = contentsRequest.getClientId();

      FileEditSession editSession;
      final String resourceId;

      try
      {
         VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
         org.exoplatform.ide.vfs.shared.File file =
            (org.exoplatform.ide.vfs.shared.File)vfs.getItemByPath(path, null, PropertyFilter.NONE_FILTER);
         resourceId = file.getId();
         editSession = editSessions.get(resourceId);
         if (editSession == null)
         {
            String text = loadFileContext(vfs, resourceId);
            FileEditSession newEditSession =
               new FileEditSessionImpl(vfs, resourceId, path, file.getMimeType(), text, null);
            editSession = editSessions.putIfAbsent(resourceId, newEditSession);
            if (editSession == null)
            {
               editSession = newEditSession;
            }
         }
      }
      catch (VirtualFileSystemException e)
      {
         // TODO
         e.printStackTrace();
         return GetFileContentsResponseImpl.make().setFileExists(false);
      }
      catch (IOException e)
      {
         // TODO
         e.printStackTrace();
         return GetFileContentsResponseImpl.make().setFileExists(false);
      }

      editSession.addCollaborator(clientId);
      FileContentsImpl fileContents = FileContentsImpl.make()
         .setPath(path)
         .setFileEditSessionKey(resourceId)
         .setCcRevision(editSession.getDocument().getCcRevision())
         .setContents(editSession.getContents())
         .setContentType(FileContents.ContentType.TEXT);
      return GetFileContentsResponseImpl.make().setFileExists(true).setFileContents(fileContents);
   }

   public void closeSession(CloseEditor closeMessage)
   {
      final String editSessionId = closeMessage.getFileEditSessionKey();
      FileEditSession editSession = editSessions.get(editSessionId);
      if (editSession != null)
      {
         editSession.removeCollaborator(closeMessage.getClientId());
         // TODO : logger debug
         System.out.printf("Close edit session %s, user %s\n", closeMessage.getFileEditSessionKey(), closeMessage.getClientId());
      }
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
         // TODO : create util method which able to close Closeable without throwing i/o exception.
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
      FileEditSession editSession = editSessions.get(resourceId);
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
               .setFilePath(editSession.getSavedPath());
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
         doBroadcast(authorId, broadcastedDocOps, editSession.getCollaborators());
         return broadcastedDocOps;
      }
      catch (VersionedDocument.DocumentOperationException e)
      {
         // TODO
         e.printStackTrace();
      }
      return broadcastedDocOps;
   }

   private void doBroadcast(String authorId, ServerToClientDocOps oprts, Set<String> collaborators)
   {
      final String body = ((ServerToClientDocOpsImpl)oprts).toJson();
      for (String collaborator : collaborators)
      {
         final String channel = "collab_editor." + collaborator;
         if (!channel.equals(authorId))
         {
            ChannelBroadcastMessage message = new ChannelBroadcastMessage();
            message.setChannel(channel);
            message.setBody(body);
            try
            {
               WSConnectionContext.sendMessage(message);
            }
            catch (Exception e)
            {
               // TODO
               e.printStackTrace();
            }
         }
      }
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
      String resourceId = missedDocOpsRequest.getFileEditSessionKey();
      FileEditSession editSession = editSessions.get(resourceId);
      List<String> docOps = ((JsonArrayListAdapter<String>)missedDocOpsRequest.getDocOps2()).asList();

      // If the client is re-sending any unacked doc ops, apply them first
      if (missedDocOpsRequest.getDocOps2().size() > 0)
      {
         applyMutation(docOps,
            missedDocOpsRequest.getClientId(),
            missedDocOpsRequest.getCurrentCcRevision(),
            null,
            missedDocOpsRequest.getWorkspaceId(),
            resourceId,
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
            .setFileEditSessionKey(resourceId)
            .setFilePath(editSession.getSavedPath());
         appliedDocOpsList.add(wrappedBroadcastDocOp);
      }

      return RecoverFromMissedDocOpsResponseImpl.make()
         .setDocOps(appliedDocOpsList)
         .setWorkspaceId(missedDocOpsRequest.getWorkspaceId());
   }

   public GetEditSessionParticipantsResponse getEditSessionCollaborators(
      GetEditSessionParticipants sessionParticipantsRequest)
   {
      FileEditSession editSession = editSessions.get(sessionParticipantsRequest.getEditSessionId());
      return GetEditSessionParticipantsResponseImpl.make()
         .setParticipants(participants.getParticipants(editSession.getCollaborators()));
   }

//  /**
//   * Iterates through all open, dirty edit sessions and saves them to disk.
//   */
//  class FileSaver implements Handler<Message<JsonObject>> {
//    @Override
//    public void handle(Message<JsonObject> message) {
//      saveAll();
//    }
//
//    void saveAll() {
//      Set<Entry<String, FileEditSession>> entries = editSessions.entrySet();
//      Iterator<Entry<String, FileEditSession>> entryIter = entries.iterator();
//      final JsonArray resourceIds = new JsonArray();
//      while (entryIter.hasNext()) {
//        Entry<String, FileEditSession> entry = entryIter.next();
//        String resourceId = entry.getKey();
//        FileEditSession editSession = entry.getValue();
//        if (editSession.hasChanges()) {
//          resourceIds.addString(resourceId);
//        }
//      }
//
//      // Resolve the current paths of opened files in case they have been moved.
//      eb.send("tree.getCurrentPaths", new JsonObject().putArray("resourceIds", resourceIds),
//          new Handler<Message<JsonObject>>() {
//              @Override
//            public void handle(Message<JsonObject> event) {
//              JsonArray currentPaths = event.body.getArray("paths");
//              Iterator<Object> pathIter = currentPaths.iterator();
//              Iterator<Object> resourceIter = resourceIds.iterator();
//
//              if (currentPaths.size() != resourceIds.size()) {
//                logger.error(String.format(
//                    "Received [%d] paths in response to a request specifying [%d] resourceIds",
//                    currentPaths.size(), resourceIds.size()));
//              }
//
//              // Iterate through all the resolved paths and save the files to disk.
//              while (pathIter.hasNext()) {
//                String path = (String) pathIter.next();
//                String resourceId = (String) resourceIter.next();
//
//                if (path != null) {
//                  FileEditSession editSession = editSessions.get(resourceId);
//                  if (editSession != null) {
//                    try {
//                      editSession.save(stripLeadingSlash(path));
//                    } catch (IOException e) {
//                      logger.error(String.format("Failed to save file [%s]", path), e);
//                    }
//                  }
//                }
//              }
//            }
//          });
//    }
//  }

//  /**
//   * Removes an edit session, and notifies clients that they should reload their opened document.
//   */
//  class EditSessionRemover implements Handler<Message<JsonObject>> {
//    @Override
//    public void handle(Message<JsonObject> message) {
//      String resourceId = message.body.getString("resourceId");
//      if (resourceId != null) {
//        editSessions.remove(resourceId);
//      }
//      // TODO: Notify clients to reload their opened document.
//    }
//  }

//  private final FileSaver fileSaver = new FileSaver();
//  private final DocumentMutator documentMutator = new DocumentMutator();
//  private String addressBase;
//
//
//   private Method method;
//
//   private Object vfs;
//
//
//   public Object getVfs()
//   {
//      if (vfs!=null)
//         return vfs;
//
//      try {
//         Field f = vertx.getClass().getDeclaredField("shared");
//         Map m = (Map)f.get(vertx);
//         vfs = m.get("vfs");
//         method = vfs.getClass().getMethod("getContent", String.class, String.class);
//return vfs;
//      }catch (Exception e) {
//        e.printStackTrace();
//         return null;
//      }
//   }

//         @Override
//  public void start() {
//    super.start();
//
//      RuntimeDelegate.setInstance(new RuntimeDelegateImpl());
//
//     this.addressBase = getOptionalStringConfig("address", "documents");
//    vertx.eventBus().registerHandler(addressBase + ".mutate", documentMutator);
//    vertx.eventBus().registerHandler(
//        addressBase + ".createEditSession", new EditSessionCreator(true));
//    vertx.eventBus().registerHandler(
//        addressBase + ".getFileContents", new EditSessionCreator(false));
//    vertx.eventBus().registerHandler(addressBase + ".saveAll", fileSaver);
//    vertx.eventBus().registerHandler(addressBase + ".removeEditSession", new EditSessionRemover());
//    vertx.eventBus().registerHandler(addressBase + ".recoverMissedDocop", new DocOpRecoverer());
//
//    // TODO: Handle content changes on disk and synthesize a docop to apply to the in-memory edit
//    // session, and broadcast to all clients.
//
//    // Set up a regular save interval to flush to disk.
//    vertx.setPeriodic(1500, new Handler<Long>() {
//        @Override
//      public void handle(Long event) {
//        fileSaver.saveAll();
//      }
//    });
//  }

}
