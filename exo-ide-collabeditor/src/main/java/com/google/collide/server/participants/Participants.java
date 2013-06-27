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

package com.google.collide.server.participants;

import com.google.collide.dto.GetWorkspaceParticipantsResponse;
import com.google.collide.dto.server.DtoServerImpls.GetWorkspaceParticipantsResponseImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantImpl;
import com.google.collide.dto.server.DtoServerImpls.ParticipantUserDetailsImpl;
import com.google.collide.dto.server.DtoServerImpls.UserDetailsImpl;
import com.google.collide.dto.server.DtoServerImpls.UserLogOutDtoImpl;
import com.google.collide.server.WSUtil;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Participants {
    private static final Log                                 LOG           = ExoLogger.getLogger(Participants.class);
    /** Map of per-user session IDs LoggedInUsers. */
    private final        ConcurrentMap<String, LoggedInUser> loggedInUsers = new ConcurrentHashMap<>();

    public GetWorkspaceParticipantsResponse getParticipants(String workspace) {
        GetWorkspaceParticipantsResponseImpl resp = GetWorkspaceParticipantsResponseImpl.make();
        List<ParticipantUserDetailsImpl> participantsArr = new ArrayList<>();
        for (LoggedInUser user : loggedInUsers.values()) {
            if (user.isLoggedIn(workspace) && !user.isReadOnly()) {
                String userId = user.getId();
                String username = user.getName();
                ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
                ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(username);
                UserDetailsImpl userDetails = UserDetailsImpl.make().setUserId(username).setDisplayEmail(username)
                                                             .setDisplayName(username).setGivenName(username);
                participantDetails.setParticipant(participant);
                participantDetails.setUserDetails(userDetails);
                participantsArr.add(participantDetails);
            }
        }
        resp.setParticipants(participantsArr);
        return resp;
    }

    public Set<String> getAllParticipantIds(String workspace) {
        Set<String> result = new LinkedHashSet<>();
        for (LoggedInUser user : loggedInUsers.values()) {
            if (user.isLoggedIn(workspace) && !user.isReadOnly()) {
                result.add(user.getId());
            }
        }
        return result;
    }

    public List<ParticipantUserDetailsImpl> getParticipants(Set<String> userIds) {
        List<ParticipantUserDetailsImpl> result = new ArrayList<>();
        for (LoggedInUser user : loggedInUsers.values()) {
            String userId = user.getId();
            if (user.isReadOnly()){
                continue;
            }
            if (userIds.contains(userId)) {
                String username = user.getName();
                ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
                ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(username);
                UserDetailsImpl userDetails = UserDetailsImpl.make().setUserId(username).setDisplayEmail(username)
                                                             .setDisplayName(username).setGivenName(username);
                participantDetails.setParticipant(participant);
                participantDetails.setUserDetails(userDetails);
                result.add(participantDetails);
            }
        }
        return result;
    }

    public ParticipantUserDetailsImpl getParticipant(String userId) {
        LoggedInUser user = loggedInUsers.get(userId);
        if (user != null) {
            String username = user.getName();
            ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
            ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(username);
            UserDetailsImpl userDetails = UserDetailsImpl.make().setUserId(username).setDisplayEmail(username)
                                                         .setDisplayName(username).setGivenName(username);
            participantDetails.setParticipant(participant);
            participantDetails.setUserDetails(userDetails);
            return participantDetails;
        }
        return null;
    }

    public boolean removeParticipant(String userId) {
        LOG.debug("Remove participant: {} ", userId);
        LoggedInUser user = loggedInUsers.remove(userId);
        if (user != null) {
            loggedInUsers.remove(userId);
            Set<String> allParticipantIds = getAllParticipantIds(user.getWorkspace());
            UserLogOutDtoImpl userLogOutDto = UserLogOutDtoImpl.make();
            String username = user.getName();
            ParticipantUserDetailsImpl participantDetails = ParticipantUserDetailsImpl.make();
            ParticipantImpl participant = ParticipantImpl.make().setId(userId).setUserId(username);
            participantDetails.setParticipant(participant);
            userLogOutDto.setParticipant(participant);
            WSUtil.broadcastToClients(userLogOutDto.toJson(), allParticipantIds);
            return true;
        } else {
            LOG.debug("Can't find participant: {} to remove", userId);
            return false;
        }
    }

    public void addParticipant(LoggedInUser user) {
        LOG.debug("Add participant: name={}, id={} ", user.getName(), user.getId());
        loggedInUsers.putIfAbsent(user.getId(), user);
    }

    public LoggedInUser getUser(String userId) {
        return loggedInUsers.get(userId);
    }
}
