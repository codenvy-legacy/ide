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

package com.google.collide.client.code;

import com.codenvy.ide.client.util.QueryCallbacks.SimpleCallback;
import com.codenvy.ide.dtogen.shared.ServerError;
import com.codenvy.ide.json.client.JsoArray;
import com.codenvy.ide.json.client.JsoStringMap;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.users.UsersModel;
import com.google.collide.client.communication.FrontendApi;
import com.google.collide.dto.GetEditSessionCollaboratorsResponse;
import com.google.collide.dto.ParticipantUserDetails;
import com.google.collide.dto.UserDetails;
import com.google.collide.dto.client.DtoClientImpls;

import org.exoplatform.ide.client.framework.websocket.FrontendApi.ApiCallback;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;

/** Model for the participants in the current workspace. */
// TODO: Pass the initial list of participants from the workspace bootstrap response
public class ParticipantModel {

    /** Listener for changes in the participant model. */
    public interface Listener {
        void participantAdded(Participant participant);

        void participantRemoved(Participant participant);
    }


    public static ParticipantModel create(FrontendApi frontendApi, MessageFilter messageFilter, UsersModel usersModel,
                                          String fileEditSessionKey) {
        ParticipantModel model = new ParticipantModel(frontendApi, usersModel, fileEditSessionKey);
        model.registerForInvalidations(messageFilter);
        model.requestAllParticipants();
        return model;
    }

    private final JsoArray<Listener> listeners;

    /**
     * The last callback created when requesting all participants. If this is null, then the last
     * request was for specific participants.
     */
    private SimpleCallback<JsonArray<ParticipantUserDetails>> lastRequestAllCallback;

    private final JsoStringMap<Participant> participantUserDetails = JsoStringMap.create();

    /**
     * Tracks the number of participants (optimization for otherwise having to iterate participants to
     * get its size).
     */
    private       int         count;
    private final FrontendApi frontendApi;

    private UsersModel usersModel;

    private String fileEditSessionKey;

    private ParticipantModel(FrontendApi frontendApi, UsersModel usersModel, String fileEditSessionKey) {
        this.frontendApi = frontendApi;
        this.usersModel = usersModel;
        this.fileEditSessionKey = fileEditSessionKey;
        listeners = JsoArray.create();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public String getUserId(final String clientId) {
        return usersModel.getUserIdByClientId(clientId);
    }

    public Participant getParticipantByUserId(final String id) {
        return usersModel.getParticipant(id);
    }

    /**
     * Gets the participants keyed by user id. Do not modify the returned map (not enforced for
     * performance reasons).
     */
    public JsoStringMap<Participant> getParticipants() {
        return participantUserDetails;
    }

    private void registerForInvalidations(MessageFilter messageFilter) {
//    messageFilter.registerMessageRecipient(RoutingTypes.GETWORKSPACEPARTICIPANTSRESPONSE,
//        new MessageRecipient<GetWorkspaceParticipantsResponse>() {
//
//            @Override
//          public void onMessageReceived(GetWorkspaceParticipantsResponse message) {
//            handleParticipantUserDetails(message.getParticipants(), true);
//          }
//        });
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    private void dispatchParticipantAdded(Participant participant) {
        for (int i = 0, n = listeners.size(); i < n; i++) {
            listeners.get(i).participantAdded(participant);
        }
    }

    private void dispatchParticipantRemoved(Participant participant) {
        for (int i = 0, n = listeners.size(); i < n; i++) {
            listeners.get(i).participantRemoved(participant);
        }
    }

    /** Requests all participants. */
    private void requestAllParticipants() {
        lastRequestAllCallback =
                new SimpleCallback<JsonArray<ParticipantUserDetails>>("Failed to retrieve participants") {
                    @Override
                    public void onQuerySuccess(JsonArray<ParticipantUserDetails> result) {
        /*
         * If there is still an outstanding request for all participants, we should replace all
         * participants with these results. Even if this request isn't the last request for all
         * participants, we should still replace all participants or we might flail in a busy
         * workspace and never update the list.
         *
         * If we've received a tango message containing the updated list of participants,
         * lastRequestAllCallback will be null and we should not replace all participants.
         */
                        boolean replaceAll = (lastRequestAllCallback != null);

        /*
         * If this is the last callback, then set lastRequestAllCallback to null so older
         * lastRequestAllCallbacks received out of order do not overwrite the most recent callback.
         */
                        if (this == lastRequestAllCallback) {
                            lastRequestAllCallback = null;
                        }

                        handleParticipantUserDetails(result, replaceAll);
                    }
                };
        DtoClientImpls.GetEditSessionCollaboratorsImpl req = DtoClientImpls.GetEditSessionCollaboratorsImpl.make();
        req.setEditSessionId(fileEditSessionKey);
        frontendApi.GET_FILE_COLLABORATORS.send(req, new ApiCallback<GetEditSessionCollaboratorsResponse>() {
            @Override
            public void onFail(ServerError.FailureReason reason) {
                // Do nothing.
            }

            @Override
            public void onMessageReceived(GetEditSessionCollaboratorsResponse message) {
                lastRequestAllCallback.onQuerySuccess(message.getParticipants());
            }
        });
    }

    private void handleParticipantUserDetails(JsonArray<ParticipantUserDetails> result, boolean replaceAll) {
        for (ParticipantUserDetails p : result.asIterable()) {
            addParticipant(p);
        }
    }


    public void addParticipant(ParticipantUserDetails item) {
        UserDetails userDetails = item.getUserDetails();
        String userId = userDetails.getUserId();
        // Cache the participants' user details.
        Participant participant = usersModel.getParticipant(userId);
        participantUserDetails.put(item.getParticipant().getId(), participant);
        dispatchParticipantAdded(participant);
    }

    public void removeParticipant(ParticipantUserDetails item) {
        String id = item.getParticipant().getId();
        if (participantUserDetails.containsKey(id)) {
            Participant participant = participantUserDetails.remove(id);
            if (participant != null) {
                count--;
                dispatchParticipantRemoved(participant);
            }
        }
    }

}
