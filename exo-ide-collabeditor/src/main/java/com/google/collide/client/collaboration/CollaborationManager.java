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

package com.google.collide.client.collaboration;

import com.google.collide.client.AppContext;
import com.google.collide.client.code.ParticipantModel;
import com.google.collide.client.communication.MessageFilter;
import com.google.collide.client.communication.PushChannel;
import com.google.collide.client.document.DocumentManager;
import com.google.collide.client.document.DocumentManager.LifecycleListener;
import com.google.collide.client.document.DocumentMetadata;
import com.google.collide.client.editor.Editor;
import com.google.collide.client.util.JsIntegerMap;
import com.google.collide.dto.DocumentSelection;
import com.google.collide.dto.FileContents;
import com.google.collide.dto.NewFileCollaborator;
import com.google.collide.dto.RoutingTypes;
import com.google.collide.json.client.Jso;
import com.google.collide.json.shared.JsonArray;
import com.google.collide.json.shared.JsonIntegerMap;
import com.google.collide.shared.document.Document;
import com.google.collide.shared.util.JsonCollections;
import com.google.collide.shared.util.ListenerRegistrar.RemoverManager;

/**
 * A manager for real-time collaboration.
 *
 * This class listens for document lifecycle changes and creates or tears down individual
 * {@link DocumentCollaborationController}s.
 */
public class CollaborationManager
{

   private final LifecycleListener lifecycleListener = new LifecycleListener()
   {
      @Override
      public void onDocumentCreated(Document document)
      {
      }

      @Override
      public void onDocumentGarbageCollected(Document document)
      {
      }

      @Override
      public void onDocumentOpened(Document document, Editor editor)
      {
         handleDocumentOpened(document, editor);
      }

      @Override
      public void onDocumentClosed(Document document, Editor editor)
      {
         handleDocumentClosed(document, editor);
      }

      @Override
      public void onDocumentLinkedToFile(Document document, FileContents fileContents)
      {
         JsonArray<DocumentSelection> selections = JsonCollections.createArray();
         JsonArray<String> serializedSelections = fileContents.getSelections();
         for (int i = 0, n = serializedSelections.size(); i < n; i++)
         {
            selections.add((DocumentSelection)Jso.deserialize(serializedSelections.get(i)));
         }

         handleDocumentLinkedToFile(document, selections);
      }

      @Override
      public void onDocumentUnlinkingFromFile(Document document)
      {
         handleDocumentUnlinkingFromFile(document);
      }
   };

   private final PushChannel.Listener pushChannelListener = new PushChannel.Listener()
   {
      @Override
      public void onReconnectedSuccessfully()
      {
         docCollabControllersByDocumentId.iterate(
            new JsonIntegerMap.IterationCallback<DocumentCollaborationController>()
            {
               @Override
               public void onIteration(
                  int documentId, DocumentCollaborationController collabController)
               {
                  collabController.handleTransportReconnectedSuccessfully();
               }
            });
      }
   };

   private final MessageFilter.MessageRecipient<NewFileCollaborator> newFileCollaboratorMessageRecipient = new MessageFilter.MessageRecipient<NewFileCollaborator>()
   {
      @Override
      public void onMessageReceived(NewFileCollaborator message)
      {
         addNewCollaborator(message);
      }
   };

   private final AppContext appContext;

   private final RemoverManager removerManager = new RemoverManager();

   private final JsIntegerMap<DocumentCollaborationController> docCollabControllersByDocumentId =
      JsIntegerMap.create();

   private DocumentManager documentManager;

   private final IncomingDocOpDemultiplexer docOpRecipient;

   private CollaborationManager(AppContext appContext, DocumentManager documentManager,
                                IncomingDocOpDemultiplexer docOpRecipient)
   {
      this.appContext = appContext;
      this.documentManager = documentManager;
      this.docOpRecipient = docOpRecipient;
      removerManager.track(documentManager.getLifecycleListenerRegistrar().add(lifecycleListener));
      removerManager.track(
         appContext.getPushChannel().getListenerRegistrar().add(pushChannelListener));
      appContext.getMessageFilter().registerMessageRecipient(RoutingTypes.NEWFILECOLLABORATOR, newFileCollaboratorMessageRecipient);
   }

   public static CollaborationManager create(AppContext appContext, DocumentManager documentManager,
                                             IncomingDocOpDemultiplexer docOpRecipient)
   {
    /*
     * Ideally this whole stack wouldn't be stuck on passing around a workspace id but it is too
     * much work right now to refactor it out so here it stays.
     */
      return new CollaborationManager(appContext, documentManager,
         docOpRecipient);
   }

   public void cleanup()
   {
      docOpRecipient.teardown();
      removerManager.remove();
   }

   DocumentCollaborationController getDocumentCollaborationController(int documentId)
   {
      return docCollabControllersByDocumentId.get(documentId);
   }

   private void handleDocumentLinkedToFile(
      Document document, JsonArray<DocumentSelection> selections)
   {

      String fileEditSessionKey = DocumentMetadata.getFileEditSessionKey(document);
      DocumentCollaborationController docCollabController = new DocumentCollaborationController(
         appContext, ParticipantModel.create(appContext.getFrontendApi(), appContext.getMessageFilter(), fileEditSessionKey), docOpRecipient, document, selections);
      docCollabController.initialize(fileEditSessionKey,
         DocumentMetadata.getBeginCcRevision(document));

      docCollabControllersByDocumentId.put(document.getId(), docCollabController);
   }

   private void handleDocumentUnlinkingFromFile(Document document)
   {
      DocumentCollaborationController docCollabController =
         docCollabControllersByDocumentId.remove(document.getId());
      if (docCollabController != null)
      {
         docCollabController.teardown();
      }
   }

   private void handleDocumentOpened(Document document, Editor editor)
   {
      DocumentCollaborationController docCollabController =
         docCollabControllersByDocumentId.get(document.getId());
      if (docCollabController != null)
      {
         docCollabController.attachToEditor(editor);
      }
   }

   private void handleDocumentClosed(Document document, Editor editor)
   {
      DocumentCollaborationController docCollabController =
         docCollabControllersByDocumentId.get(document.getId());
      if (docCollabController != null)
      {
         docCollabController.detachFromEditor();
      }
   }

   private void addNewCollaborator(NewFileCollaborator message)
   {
      Document document = documentManager.getDocumentByFilePath(message.getPath());
      if(document != null)
      {
         DocumentCollaborationController collaborationController = docCollabControllersByDocumentId.get(document.getId());
         collaborationController.getParticipantModel().addParticipant(true, message.getParticipant());
      }
   }
}
