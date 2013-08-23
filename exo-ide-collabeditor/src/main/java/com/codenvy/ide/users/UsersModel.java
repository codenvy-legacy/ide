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
package com.codenvy.ide.users;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.commons.shared.ListenerManager;
import com.codenvy.ide.commons.shared.ListenerManager.Dispatcher;
import com.codenvy.ide.dtogen.shared.ServerError.FailureReason;
import com.codenvy.ide.json.client.JsoStringMap;
import com.codenvy.ide.json.client.JsoStringSet;
import com.codenvy.ide.json.shared.JsonArray;
import com.codenvy.ide.json.shared.JsonCollections;
import com.codenvy.ide.json.shared.JsonStringSet;
import com.google.collide.client.bootstrap.BootstrapSession;
import com.google.collide.client.code.Participant;
import com.google.collide.client.communication.FrontendApi;
import com.google.collide.dto.*;
import com.google.collide.dto.client.DtoClientImpls.GetWorkspaceParticipantsImpl;

import org.exoplatform.ide.client.framework.websocket.FrontendApi.ApiCallback;
import org.exoplatform.ide.client.framework.websocket.MessageFilter;
import org.exoplatform.ide.client.framework.websocket.MessageFilter.MessageRecipient;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class UsersModel {
    private static class ColorGenerator {

        private static final String[] COLORS = new String[]{
                "#465FE6", // Blue
                "#FC9229", // Orange
                "#51D13F", // Green
                "#B744D1", // Purple
                "#3BC9D1", // Cyan
                "#D13B45", // Pinky Red
                "#F41BDB", // Magenta
                "#B7AC4A", // Mustard
                "#723226", // Brown

                "#7B68EE", //MediumSlateBlue
                "#FF69B4", //HotPink
                "#87CEFA", //LightSkyBlue
                "#00FF00", //Lime
                "#9400D3", //DarkViolet
                "#90EE90", //LightGreen
                "#00BFFF", //DeepSkyBlue
                "#6495ED", //CornflowerBlue
                "#C71585", //MediumVioletRed
                "#FF7F50", //Coral
                "#FF4500", //OrangeRed
                "#FF8C00", //DarkOrange
                "#FF00FF", //Magenta
                "#FFA500", //Orange
                "#9966CC", //Amethyst
                "#7B68EE", //MediumSlateBlue
                "#8A2BE2", //BlueViolet
                "#9932CC", //DarkOrchid
                "#8B008B", //DarkMagenta
                "#BA55D3", //Magenta
                "#9370DB", //MediumPurple
                "#800080", //Purple
                "#1E90FF", //DodgerBlue
                "#6A5ACD", //SlateBlue
                "#483D8B", //DarkSlateBlue
                "#FF6347", //Tomato
                "#8B0000", //DarkRed
                "#ADFF2F", //
                "#7FFF00", //Chartreuse
                "#7CFC00", //LawnGreen
                "#32CD32", //LimeGreen
                "#B22222", //FireBrick
                "#98FB98", //PaleGreen
                "#4B0082", //Indigo
                "#00FA9A", //MediumSpringGreen
                "#00FF7F", //SpringGreen
                "#FF1493", //DeepPink
                "#3CB371", //
                "#2E8B57", //SeaGreen
                "#228B22", //ForestGreen
                "#008000", //Green
                "#4169E1", //RoyalBlue
                "#DC143C", // Crimson
                "#006400", //DarkGreen
                "#87CEEB", //SkyBlue
        };

        private int previousColorIndex = -1;

        private String nextColor() {
            previousColorIndex = (previousColorIndex + 1) % COLORS.length;
            return COLORS[previousColorIndex];
        }
    }

    /** Listener for changes in the participant model. */
    public interface Listener {
        void participantAdded(Participant participant);

        void participantRemoved(Participant participant);
    }

    private final MessageRecipient<UserLogInDto> userLogInRecipient = new MessageRecipient<UserLogInDto>() {
        @Override
        public void onMessageReceived(UserLogInDto message) {
            createAndAddParticipant(message.getParticipant().getParticipant(), message.getParticipant().getUserDetails());
        }
    };

    private final MessageRecipient<UserLogOutDto> userLogOutRecipient = new MessageRecipient<UserLogOutDto>() {
        @Override
        public void onMessageReceived(UserLogOutDto message) {
            removeUser(message.getParticipant().getUserId());
        }
    };

    private final ColorGenerator colorGenerator;

    /**
     * The set of all active participant ids, including participants who have not been added to the
     * participant list because we don't have user details yet.
     */
    private JsonStringSet presentParticipantsTracker = JsoStringSet.create();

    private final JsoStringMap<Participant> participantsByUserId = JsoStringMap.create();

    private final JsoStringMap<String> clientIdToUserId = JsoStringMap.create();

    private Participant self;

    private ListenerManager<Listener> listenerManager = ListenerManager.create();

    /**
     * A map of user ID to the {@link UserDetails} for the participant. We cache participant info in
     * case users connect and disconnect rapidly. We keep the cache here so we can discard it when the
     * user leaves the workspace.
     */
    // TODO: Should we persist user details for the entire session?
    private final JsoStringMap<UserDetails> participantUserDetails = JsoStringMap.create();

    public UsersModel(FrontendApi frontendApi, MessageFilter messageFilter) {
        GetWorkspaceParticipants req = GetWorkspaceParticipantsImpl.make();
        messageFilter.registerMessageRecipient(RoutingTypes.USERLOGIN, userLogInRecipient);
        messageFilter.registerMessageRecipient(RoutingTypes.USERLOGOUT, userLogOutRecipient);
        colorGenerator = new ColorGenerator();
        frontendApi.GET_WORKSPACE_PARTICIPANTS.send(req, new ApiCallback<GetWorkspaceParticipantsResponse>() {
            @Override
            public void onFail(FailureReason reason) {
                Log.error(getClass(), reason);
            }

            @Override
            public void onMessageReceived(GetWorkspaceParticipantsResponse message) {
                handleParticipantUserDetails(message.getParticipants(), true);
            }
        });


    }

    public Participant getUserById(String userId) {
        return participantsByUserId.get(userId);
    }

    /**
     * Updates the model with the participant user details.
     *
     * @param isAllParticipants
     *         true if the result contains the complete list of participants
     */
    private void handleParticipantUserDetails(
            JsonArray<ParticipantUserDetails> result, boolean isAllParticipants) {
        // Reset the tracker if the result is all inclusive.
        if (isAllParticipants) {
            presentParticipantsTracker = JsonCollections.createStringSet();
        }

        for (int i = 0; i < result.size(); i++) {
            ParticipantUserDetails item = result.get(i);
            UserDetails userDetails = item.getUserDetails();
            String userId = userDetails.getUserId();
            // Cache the participants' user details.
            participantUserDetails.put(userId, userDetails);
            clientIdToUserId.put(item.getParticipant().getId(), userId);

            if (isAllParticipants) {
                presentParticipantsTracker.add(userId);
                if (!participantsByUserId.containsKey(userId)) {
                    createAndAddParticipant(item.getParticipant(), userDetails);
                }
            } else {
        /*
         * Add the participant to the list. If the user is not in presentParticipantsTracker set,
         * then the participant has since disconnected. If the user is in the participants map, then
         * the user was already added to the view.
         */
                if (presentParticipantsTracker.contains(userId)
                    && !participantsByUserId.containsKey(userId)) {
                    createAndAddParticipant(item.getParticipant(), userDetails);
                }
            }
        }

        // Sweep through participants to find removed participants.
        removeOldParticipants();
    }

    /** Removes users who have left the workspace from the participant list. */
    private void removeOldParticipants() {
        // NOTE: Iterating collection that is not affected by removing.
        for (String userId : participantsByUserId.getKeys().asIterable()) {
            if (!presentParticipantsTracker.contains(userId)) {
                Participant participant = participantsByUserId.remove(userId);
                if (participant != null) {
                    dispatchParticipantRemoved(participant);
                }

                for (String clientId : clientIdToUserId.getKeys().asIterable()) {
                    if (clientIdToUserId.get(clientId).equals(userId)) {
                        clientIdToUserId.remove(clientId);
                    }
                }
            }
        }
    }

    private void dispatchParticipantRemoved(final Participant participant) {
        listenerManager.dispatch(new Dispatcher<Listener>() {
            @Override
            public void dispatch(Listener listener) {
                listener.participantAdded(participant);
            }
        });
    }

    private void createAndAddParticipant(
            com.google.collide.dto.Participant participantDto, UserDetails userDetails) {
        boolean isSelf =
                participantDto.getId().equals(BootstrapSession.getBootstrapSession().getActiveClientId());
        String color = isSelf ? "black" : colorGenerator.nextColor();
        Participant participant = Participant.create(
                participantDto, userDetails.getDisplayName(), userDetails.getDisplayEmail(), color, isSelf);
        participantsByUserId.put(participantDto.getUserId(), participant);

        if (isSelf) {
            self = participant;
        }
        dispatchParticipantAdded(participant);
    }

    private void dispatchParticipantAdded(final Participant participant) {
        listenerManager.dispatch(new Dispatcher<Listener>() {
            @Override
            public void dispatch(Listener listener) {
                listener.participantRemoved(participant);
            }
        });
    }

    private void removeUser(String userId) {
        for (String clientId : clientIdToUserId.getKeys().asIterable()) {
            if (clientIdToUserId.get(clientId).equals(userId)) {
                clientIdToUserId.remove(clientId);
            }
        }
    }

    public Participant getParticipant(String userId) {
        return participantsByUserId.get(userId);
    }

    public String getUserIdByClientId(String clientId) {
        return clientIdToUserId.get(clientId);
    }

    public Participant getSelf() {
        return self;
    }

    public void addListener(Listener listener) {
        listenerManager.add(listener);
    }

    public void removeListener(Listener listener) {
        listenerManager.remove(listener);
    }

}
