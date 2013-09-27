/*
 * CODENVY CONFIDENTIAL
 *__________________
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


// GENERATED SOURCE. DO NOT EDIT.
package com.codenvy.ide.collaboration.dto.server;

import com.codenvy.ide.dtogen.server.JsonSerializable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.Map;


@SuppressWarnings({"unchecked", "cast"})
public class DtoServerImpls {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    private DtoServerImpls() {
    }

    public static final String CLIENT_SERVER_PROTOCOL_HASH = "7533dde2bfa2284b69172143482de0fd16524946";

    public static class ChatCodePointMessageImpl extends ChatMessageImpl
            implements com.codenvy.ide.collaboration.dto.ChatCodePointMessage, JsonSerializable {

        private ChatCodePointMessageImpl() {
            super(8);
        }

        protected ChatCodePointMessageImpl(int type) {
            super(type);
        }

        public static ChatCodePointMessageImpl make() {
            return new ChatCodePointMessageImpl();
        }

        protected int              startChar;
        private   boolean          _hasStartChar;
        protected int              endChar;
        private   boolean          _hasEndChar;
        protected java.lang.String path;
        private   boolean          _hasPath;
        protected int              startLine;
        private   boolean          _hasStartLine;
        protected int              endLine;
        private   boolean          _hasEndLine;

        public boolean hasStartChar() {
            return _hasStartChar;
        }

        @Override
        public int getStartChar() {
            return startChar;
        }

        public ChatCodePointMessageImpl setStartChar(int v) {
            _hasStartChar = true;
            startChar = v;
            return this;
        }

        public boolean hasEndChar() {
            return _hasEndChar;
        }

        @Override
        public int getEndChar() {
            return endChar;
        }

        public ChatCodePointMessageImpl setEndChar(int v) {
            _hasEndChar = true;
            endChar = v;
            return this;
        }

        public boolean hasPath() {
            return _hasPath;
        }

        @Override
        public java.lang.String getPath() {
            return path;
        }

        public ChatCodePointMessageImpl setPath(java.lang.String v) {
            _hasPath = true;
            path = v;
            return this;
        }

        public boolean hasStartLine() {
            return _hasStartLine;
        }

        @Override
        public int getStartLine() {
            return startLine;
        }

        public ChatCodePointMessageImpl setStartLine(int v) {
            _hasStartLine = true;
            startLine = v;
            return this;
        }

        public boolean hasEndLine() {
            return _hasEndLine;
        }

        @Override
        public int getEndLine() {
            return endLine;
        }

        public ChatCodePointMessageImpl setEndLine(int v) {
            _hasEndLine = true;
            endLine = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ChatCodePointMessageImpl)) {
                return false;
            }
            ChatCodePointMessageImpl other = (ChatCodePointMessageImpl)o;
            if (this._hasStartChar != other._hasStartChar) {
                return false;
            }
            if (this._hasStartChar) {
                if (this.startChar != other.startChar) {
                    return false;
                }
            }
            if (this._hasEndChar != other._hasEndChar) {
                return false;
            }
            if (this._hasEndChar) {
                if (this.endChar != other.endChar) {
                    return false;
                }
            }
            if (this._hasPath != other._hasPath) {
                return false;
            }
            if (this._hasPath) {
                if (!this.path.equals(other.path)) {
                    return false;
                }
            }
            if (this._hasStartLine != other._hasStartLine) {
                return false;
            }
            if (this._hasStartLine) {
                if (this.startLine != other.startLine) {
                    return false;
                }
            }
            if (this._hasEndLine != other._hasEndLine) {
                return false;
            }
            if (this._hasEndLine) {
                if (this.endLine != other.endLine) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasStartChar ? java.lang.Integer.valueOf(startChar).hashCode() : 0);
            hash = hash * 31 + (_hasEndChar ? java.lang.Integer.valueOf(endChar).hashCode() : 0);
            hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
            hash = hash * 31 + (_hasStartLine ? java.lang.Integer.valueOf(startLine).hashCode() : 0);
            hash = hash * 31 + (_hasEndLine ? java.lang.Integer.valueOf(endLine).hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonPrimitive startCharOut = new JsonPrimitive(startChar);
            result.add("startChar", startCharOut);

            JsonPrimitive endCharOut = new JsonPrimitive(endChar);
            result.add("endChar", endCharOut);

            JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
            result.add("path", pathOut);

            JsonPrimitive startLineOut = new JsonPrimitive(startLine);
            result.add("startLine", startLineOut);

            JsonPrimitive endLineOut = new JsonPrimitive(endLine);
            result.add("endLine", endLineOut);

            JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
            result.add("clientId", clientIdOut);

            JsonElement dateTimeOut = (dateTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(dateTime);
            result.add("dateTime", dateTimeOut);

            JsonElement messageOut = (message == null) ? JsonNull.INSTANCE : new JsonPrimitive(message);
            result.add("message", messageOut);

            JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
            result.add("projectId", projectIdOut);

            JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
            result.add("userId", userIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ChatCodePointMessageImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ChatCodePointMessageImpl dto = new ChatCodePointMessageImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("startChar")) {
                JsonElement startCharIn = json.get("startChar");
                int startCharOut = startCharIn.getAsInt();
                dto.setStartChar(startCharOut);
            }

            if (json.has("endChar")) {
                JsonElement endCharIn = json.get("endChar");
                int endCharOut = endCharIn.getAsInt();
                dto.setEndChar(endCharOut);
            }

            if (json.has("path")) {
                JsonElement pathIn = json.get("path");
                java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
                dto.setPath(pathOut);
            }

            if (json.has("startLine")) {
                JsonElement startLineIn = json.get("startLine");
                int startLineOut = startLineIn.getAsInt();
                dto.setStartLine(startLineOut);
            }

            if (json.has("endLine")) {
                JsonElement endLineIn = json.get("endLine");
                int endLineOut = endLineIn.getAsInt();
                dto.setEndLine(endLineOut);
            }

            if (json.has("clientId")) {
                JsonElement clientIdIn = json.get("clientId");
                java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
                dto.setClientId(clientIdOut);
            }

            if (json.has("dateTime")) {
                JsonElement dateTimeIn = json.get("dateTime");
                java.lang.String dateTimeOut = gson.fromJson(dateTimeIn, java.lang.String.class);
                dto.setDateTime(dateTimeOut);
            }

            if (json.has("message")) {
                JsonElement messageIn = json.get("message");
                java.lang.String messageOut = gson.fromJson(messageIn, java.lang.String.class);
                dto.setMessage(messageOut);
            }

            if (json.has("projectId")) {
                JsonElement projectIdIn = json.get("projectId");
                java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
                dto.setProjectId(projectIdOut);
            }

            if (json.has("userId")) {
                JsonElement userIdIn = json.get("userId");
                java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
                dto.setUserId(userIdOut);
            }

            return dto;
        }

        public static ChatCodePointMessageImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ChatMessageImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ChatMessage, JsonSerializable {

        private ChatMessageImpl() {
            super(7);
        }

        protected ChatMessageImpl(int type) {
            super(type);
        }

        public static ChatMessageImpl make() {
            return new ChatMessageImpl();
        }

        protected java.lang.String clientId;
        private   boolean          _hasClientId;
        protected java.lang.String dateTime;
        private   boolean          _hasDateTime;
        protected java.lang.String message;
        private   boolean          _hasMessage;
        protected java.lang.String projectId;
        private   boolean          _hasProjectId;
        protected java.lang.String userId;
        private   boolean          _hasUserId;

        public boolean hasClientId() {
            return _hasClientId;
        }

        @Override
        public java.lang.String getClientId() {
            return clientId;
        }

        public ChatMessageImpl setClientId(java.lang.String v) {
            _hasClientId = true;
            clientId = v;
            return this;
        }

        public boolean hasDateTime() {
            return _hasDateTime;
        }

        @Override
        public java.lang.String getDateTime() {
            return dateTime;
        }

        public ChatMessageImpl setDateTime(java.lang.String v) {
            _hasDateTime = true;
            dateTime = v;
            return this;
        }

        public boolean hasMessage() {
            return _hasMessage;
        }

        @Override
        public java.lang.String getMessage() {
            return message;
        }

        public ChatMessageImpl setMessage(java.lang.String v) {
            _hasMessage = true;
            message = v;
            return this;
        }

        public boolean hasProjectId() {
            return _hasProjectId;
        }

        @Override
        public java.lang.String getProjectId() {
            return projectId;
        }

        public ChatMessageImpl setProjectId(java.lang.String v) {
            _hasProjectId = true;
            projectId = v;
            return this;
        }

        public boolean hasUserId() {
            return _hasUserId;
        }

        @Override
        public java.lang.String getUserId() {
            return userId;
        }

        public ChatMessageImpl setUserId(java.lang.String v) {
            _hasUserId = true;
            userId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ChatMessageImpl)) {
                return false;
            }
            ChatMessageImpl other = (ChatMessageImpl)o;
            if (this._hasClientId != other._hasClientId) {
                return false;
            }
            if (this._hasClientId) {
                if (!this.clientId.equals(other.clientId)) {
                    return false;
                }
            }
            if (this._hasDateTime != other._hasDateTime) {
                return false;
            }
            if (this._hasDateTime) {
                if (!this.dateTime.equals(other.dateTime)) {
                    return false;
                }
            }
            if (this._hasMessage != other._hasMessage) {
                return false;
            }
            if (this._hasMessage) {
                if (!this.message.equals(other.message)) {
                    return false;
                }
            }
            if (this._hasProjectId != other._hasProjectId) {
                return false;
            }
            if (this._hasProjectId) {
                if (!this.projectId.equals(other.projectId)) {
                    return false;
                }
            }
            if (this._hasUserId != other._hasUserId) {
                return false;
            }
            if (this._hasUserId) {
                if (!this.userId.equals(other.userId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
            hash = hash * 31 + (_hasDateTime ? dateTime.hashCode() : 0);
            hash = hash * 31 + (_hasMessage ? message.hashCode() : 0);
            hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
            hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
            result.add("clientId", clientIdOut);

            JsonElement dateTimeOut = (dateTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(dateTime);
            result.add("dateTime", dateTimeOut);

            JsonElement messageOut = (message == null) ? JsonNull.INSTANCE : new JsonPrimitive(message);
            result.add("message", messageOut);

            JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
            result.add("projectId", projectIdOut);

            JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
            result.add("userId", userIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ChatMessageImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ChatMessageImpl dto = new ChatMessageImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("clientId")) {
                JsonElement clientIdIn = json.get("clientId");
                java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
                dto.setClientId(clientIdOut);
            }

            if (json.has("dateTime")) {
                JsonElement dateTimeIn = json.get("dateTime");
                java.lang.String dateTimeOut = gson.fromJson(dateTimeIn, java.lang.String.class);
                dto.setDateTime(dateTimeOut);
            }

            if (json.has("message")) {
                JsonElement messageIn = json.get("message");
                java.lang.String messageOut = gson.fromJson(messageIn, java.lang.String.class);
                dto.setMessage(messageOut);
            }

            if (json.has("projectId")) {
                JsonElement projectIdIn = json.get("projectId");
                java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
                dto.setProjectId(projectIdOut);
            }

            if (json.has("userId")) {
                JsonElement userIdIn = json.get("userId");
                java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
                dto.setUserId(userIdOut);
            }

            return dto;
        }

        public static ChatMessageImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ChatParticipantAddImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ChatParticipantAdd, JsonSerializable {

        private ChatParticipantAddImpl() {
            super(10);
        }

        protected ChatParticipantAddImpl(int type) {
            super(type);
        }

        public static ChatParticipantAddImpl make() {
            return new ChatParticipantAddImpl();
        }

        protected ParticipantInfoImpl participant;
        private   boolean             _hasParticipant;
        protected java.lang.String    projectId;
        private   boolean             _hasProjectId;

        public boolean hasParticipant() {
            return _hasParticipant;
        }

        @Override
        public com.codenvy.ide.collaboration.dto.ParticipantInfo participant() {
            return participant;
        }

        public ChatParticipantAddImpl setParticipant(ParticipantInfoImpl v) {
            _hasParticipant = true;
            participant = v;
            return this;
        }

        public boolean hasProjectId() {
            return _hasProjectId;
        }

        @Override
        public java.lang.String projectId() {
            return projectId;
        }

        public ChatParticipantAddImpl setProjectId(java.lang.String v) {
            _hasProjectId = true;
            projectId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ChatParticipantAddImpl)) {
                return false;
            }
            ChatParticipantAddImpl other = (ChatParticipantAddImpl)o;
            if (this._hasParticipant != other._hasParticipant) {
                return false;
            }
            if (this._hasParticipant) {
                if (!this.participant.equals(other.participant)) {
                    return false;
                }
            }
            if (this._hasProjectId != other._hasProjectId) {
                return false;
            }
            if (this._hasProjectId) {
                if (!this.projectId.equals(other.projectId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasParticipant ? participant.hashCode() : 0);
            hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement participantOut = participant == null ? JsonNull.INSTANCE : participant.toJsonElement();
            result.add("participant", participantOut);

            JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
            result.add("projectId", projectIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ChatParticipantAddImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ChatParticipantAddImpl dto = new ChatParticipantAddImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("participant")) {
                JsonElement participantIn = json.get("participant");
                ParticipantInfoImpl participantOut = ParticipantInfoImpl.fromJsonElement(participantIn);
                dto.setParticipant(participantOut);
            }

            if (json.has("projectId")) {
                JsonElement projectIdIn = json.get("projectId");
                java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
                dto.setProjectId(projectIdOut);
            }

            return dto;
        }

        public static ChatParticipantAddImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ChatParticipantRemoveImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ChatParticipantRemove, JsonSerializable {

        private ChatParticipantRemoveImpl() {
            super(11);
        }

        protected ChatParticipantRemoveImpl(int type) {
            super(type);
        }

        public static ChatParticipantRemoveImpl make() {
            return new ChatParticipantRemoveImpl();
        }

        protected java.lang.String clientId;
        private   boolean          _hasClientId;
        protected java.lang.String projectId;
        private   boolean          _hasProjectId;
        protected java.lang.String userId;
        private   boolean          _hasUserId;

        public boolean hasClientId() {
            return _hasClientId;
        }

        @Override
        public java.lang.String clientId() {
            return clientId;
        }

        public ChatParticipantRemoveImpl setClientId(java.lang.String v) {
            _hasClientId = true;
            clientId = v;
            return this;
        }

        public boolean hasProjectId() {
            return _hasProjectId;
        }

        @Override
        public java.lang.String projectId() {
            return projectId;
        }

        public ChatParticipantRemoveImpl setProjectId(java.lang.String v) {
            _hasProjectId = true;
            projectId = v;
            return this;
        }

        public boolean hasUserId() {
            return _hasUserId;
        }

        @Override
        public java.lang.String userId() {
            return userId;
        }

        public ChatParticipantRemoveImpl setUserId(java.lang.String v) {
            _hasUserId = true;
            userId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ChatParticipantRemoveImpl)) {
                return false;
            }
            ChatParticipantRemoveImpl other = (ChatParticipantRemoveImpl)o;
            if (this._hasClientId != other._hasClientId) {
                return false;
            }
            if (this._hasClientId) {
                if (!this.clientId.equals(other.clientId)) {
                    return false;
                }
            }
            if (this._hasProjectId != other._hasProjectId) {
                return false;
            }
            if (this._hasProjectId) {
                if (!this.projectId.equals(other.projectId)) {
                    return false;
                }
            }
            if (this._hasUserId != other._hasUserId) {
                return false;
            }
            if (this._hasUserId) {
                if (!this.userId.equals(other.userId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
            hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
            hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
            result.add("clientId", clientIdOut);

            JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
            result.add("projectId", projectIdOut);

            JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
            result.add("userId", userIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ChatParticipantRemoveImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ChatParticipantRemoveImpl dto = new ChatParticipantRemoveImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("clientId")) {
                JsonElement clientIdIn = json.get("clientId");
                java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
                dto.setClientId(clientIdOut);
            }

            if (json.has("projectId")) {
                JsonElement projectIdIn = json.get("projectId");
                java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
                dto.setProjectId(projectIdOut);
            }

            if (json.has("userId")) {
                JsonElement userIdIn = json.get("userId");
                java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
                dto.setUserId(userIdOut);
            }

            return dto;
        }

        public static ChatParticipantRemoveImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class DisableEnableCollaborationDtoImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.DisableEnableCollaborationDto, JsonSerializable {

        private DisableEnableCollaborationDtoImpl() {
            super(13);
        }

        protected DisableEnableCollaborationDtoImpl(int type) {
            super(type);
        }

        public static DisableEnableCollaborationDtoImpl make() {
            return new DisableEnableCollaborationDtoImpl();
        }

        protected java.lang.String clientId;
        private   boolean          _hasClientId;
        protected boolean          isEnabled;
        private   boolean          _hasIsEnabled;
        protected java.lang.String projectId;
        private   boolean          _hasProjectId;

        public boolean hasClientId() {
            return _hasClientId;
        }

        @Override
        public java.lang.String clientId() {
            return clientId;
        }

        public DisableEnableCollaborationDtoImpl setClientId(java.lang.String v) {
            _hasClientId = true;
            clientId = v;
            return this;
        }

        public boolean hasIsEnabled() {
            return _hasIsEnabled;
        }

        @Override
        public boolean isEnabled() {
            return isEnabled;
        }

        public DisableEnableCollaborationDtoImpl setIsEnabled(boolean v) {
            _hasIsEnabled = true;
            isEnabled = v;
            return this;
        }

        public boolean hasProjectId() {
            return _hasProjectId;
        }

        @Override
        public java.lang.String projectId() {
            return projectId;
        }

        public DisableEnableCollaborationDtoImpl setProjectId(java.lang.String v) {
            _hasProjectId = true;
            projectId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof DisableEnableCollaborationDtoImpl)) {
                return false;
            }
            DisableEnableCollaborationDtoImpl other = (DisableEnableCollaborationDtoImpl)o;
            if (this._hasClientId != other._hasClientId) {
                return false;
            }
            if (this._hasClientId) {
                if (!this.clientId.equals(other.clientId)) {
                    return false;
                }
            }
            if (this._hasIsEnabled != other._hasIsEnabled) {
                return false;
            }
            if (this._hasIsEnabled) {
                if (this.isEnabled != other.isEnabled) {
                    return false;
                }
            }
            if (this._hasProjectId != other._hasProjectId) {
                return false;
            }
            if (this._hasProjectId) {
                if (!this.projectId.equals(other.projectId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
            hash = hash * 31 + (_hasIsEnabled ? java.lang.Boolean.valueOf(isEnabled).hashCode() : 0);
            hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
            result.add("clientId", clientIdOut);

            JsonPrimitive isEnabledOut = new JsonPrimitive(isEnabled);
            result.add("isEnabled", isEnabledOut);

            JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
            result.add("projectId", projectIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static DisableEnableCollaborationDtoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            DisableEnableCollaborationDtoImpl dto = new DisableEnableCollaborationDtoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("clientId")) {
                JsonElement clientIdIn = json.get("clientId");
                java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
                dto.setClientId(clientIdOut);
            }

            if (json.has("isEnabled")) {
                JsonElement isEnabledIn = json.get("isEnabled");
                boolean isEnabledOut = isEnabledIn.getAsBoolean();
                dto.setIsEnabled(isEnabledOut);
            }

            if (json.has("projectId")) {
                JsonElement projectIdIn = json.get("projectId");
                java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
                dto.setProjectId(projectIdOut);
            }

            return dto;
        }

        public static DisableEnableCollaborationDtoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ItemImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.Item, JsonSerializable {

        public static ItemImpl make() {
            return new ItemImpl();
        }

        protected java.lang.String                                id;
        private   boolean                                         _hasId;
        protected java.lang.String                                name;
        private   boolean                                         _hasName;
        protected com.codenvy.ide.collaboration.dto.Item.ItemType itemType;
        private   boolean                                         _hasItemType;
        protected java.lang.String                                path;
        private   boolean                                         _hasPath;
        protected java.lang.String                                parentId;
        private   boolean                                         _hasParentId;
        protected java.lang.String                                mimeType;
        private   boolean                                         _hasMimeType;
        protected java.util.List<PropertyImpl>                    properties;
        private   boolean                                         _hasProperties;
        protected java.util.Map<String, LinkImpl>                 links;
        private   boolean                                         _hasLinks;

        public boolean hasId() {
            return _hasId;
        }

        @Override
        public java.lang.String getId() {
            return id;
        }

        public ItemImpl setId(java.lang.String v) {
            _hasId = true;
            id = v;
            return this;
        }

        public boolean hasName() {
            return _hasName;
        }

        @Override
        public java.lang.String getName() {
            return name;
        }

        public ItemImpl setName(java.lang.String v) {
            _hasName = true;
            name = v;
            return this;
        }

        public boolean hasItemType() {
            return _hasItemType;
        }

        @Override
        public com.codenvy.ide.collaboration.dto.Item.ItemType getItemType() {
            return itemType;
        }

        public ItemImpl setItemType(com.codenvy.ide.collaboration.dto.Item.ItemType v) {
            _hasItemType = true;
            itemType = v;
            return this;
        }

        public boolean hasPath() {
            return _hasPath;
        }

        @Override
        public java.lang.String getPath() {
            return path;
        }

        public ItemImpl setPath(java.lang.String v) {
            _hasPath = true;
            path = v;
            return this;
        }

        public boolean hasParentId() {
            return _hasParentId;
        }

        @Override
        public java.lang.String getParentId() {
            return parentId;
        }

        public ItemImpl setParentId(java.lang.String v) {
            _hasParentId = true;
            parentId = v;
            return this;
        }

        public boolean hasMimeType() {
            return _hasMimeType;
        }

        @Override
        public java.lang.String getMimeType() {
            return mimeType;
        }

        public ItemImpl setMimeType(java.lang.String v) {
            _hasMimeType = true;
            mimeType = v;
            return this;
        }

        public boolean hasProperties() {
            return _hasProperties;
        }

        @Override
        public com.codenvy.ide.json.shared.JsonArray<com.codenvy.ide.collaboration.dto.Property> getProperties() {
            ensureProperties();
            return (com.codenvy.ide.json.shared.JsonArray)new com.codenvy.ide.json.server.JsonArrayListAdapter(properties);
        }

        public ItemImpl setProperties(java.util.List<PropertyImpl> v) {
            _hasProperties = true;
            properties = v;
            return this;
        }

        public void addProperties(PropertyImpl v) {
            ensureProperties();
            properties.add(v);
        }

        public void clearProperties() {
            ensureProperties();
            properties.clear();
        }

        private void ensureProperties() {
            if (!_hasProperties) {
                setProperties(properties != null ? properties : new java.util.ArrayList<PropertyImpl>());
            }
        }

        public boolean hasLinks() {
            return _hasLinks;
        }

        @Override
        public com.codenvy.ide.json.shared.JsonStringMap<com.codenvy.ide.collaboration.dto.Link> getLinks() {
            ensureLinks();
            return (com.codenvy.ide.json.shared.JsonStringMap)new com.codenvy.ide.json.server.JsonStringMapAdapter(links);
        }

        public ItemImpl setLinks(java.util.Map<String, LinkImpl> v) {
            _hasLinks = true;
            links = v;
            return this;
        }

        public void putLinks(String k, LinkImpl v) {
            ensureLinks();
            links.put(k, v);
        }

        public void clearLinks() {
            ensureLinks();
            links.clear();
        }

        private void ensureLinks() {
            if (!_hasLinks) {
                setLinks(links != null ? links : new java.util.HashMap<String, LinkImpl>());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ItemImpl)) {
                return false;
            }
            ItemImpl other = (ItemImpl)o;
            if (this._hasId != other._hasId) {
                return false;
            }
            if (this._hasId) {
                if (!this.id.equals(other.id)) {
                    return false;
                }
            }
            if (this._hasName != other._hasName) {
                return false;
            }
            if (this._hasName) {
                if (!this.name.equals(other.name)) {
                    return false;
                }
            }
            if (this._hasItemType != other._hasItemType) {
                return false;
            }
            if (this._hasItemType) {
                if (!this.itemType.equals(other.itemType)) {
                    return false;
                }
            }
            if (this._hasPath != other._hasPath) {
                return false;
            }
            if (this._hasPath) {
                if (!this.path.equals(other.path)) {
                    return false;
                }
            }
            if (this._hasParentId != other._hasParentId) {
                return false;
            }
            if (this._hasParentId) {
                if (!this.parentId.equals(other.parentId)) {
                    return false;
                }
            }
            if (this._hasMimeType != other._hasMimeType) {
                return false;
            }
            if (this._hasMimeType) {
                if (!this.mimeType.equals(other.mimeType)) {
                    return false;
                }
            }
            if (this._hasProperties != other._hasProperties) {
                return false;
            }
            if (this._hasProperties) {
                if (!this.properties.equals(other.properties)) {
                    return false;
                }
            }
            if (this._hasLinks != other._hasLinks) {
                return false;
            }
            if (this._hasLinks) {
                if (!this.links.equals(other.links)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasId ? id.hashCode() : 0);
            hash = hash * 31 + (_hasName ? name.hashCode() : 0);
            hash = hash * 31 + (_hasItemType ? itemType.hashCode() : 0);
            hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
            hash = hash * 31 + (_hasParentId ? parentId.hashCode() : 0);
            hash = hash * 31 + (_hasMimeType ? mimeType.hashCode() : 0);
            hash = hash * 31 + (_hasProperties ? properties.hashCode() : 0);
            hash = hash * 31 + (_hasLinks ? links.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonArray result = new JsonArray();

            JsonElement idOut = (id == null) ? JsonNull.INSTANCE : new JsonPrimitive(id);
            result.add(idOut);

            JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
            result.add(nameOut);

            JsonElement itemTypeOut = (itemType == null) ? JsonNull.INSTANCE : new JsonPrimitive(itemType.name());
            result.add(itemTypeOut);

            JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
            result.add(pathOut);

            JsonElement parentIdOut = (parentId == null) ? JsonNull.INSTANCE : new JsonPrimitive(parentId);
            result.add(parentIdOut);

            JsonElement mimeTypeOut = (mimeType == null) ? JsonNull.INSTANCE : new JsonPrimitive(mimeType);
            result.add(mimeTypeOut);

            JsonArray propertiesOut = new JsonArray();
            ensureProperties();
            for (PropertyImpl properties_ : properties) {
                JsonElement propertiesOut_ = properties_ == null ? JsonNull.INSTANCE : properties_.toJsonElement();
                propertiesOut.add(propertiesOut_);
            }
            result.add(propertiesOut);

            JsonObject linksOut = new JsonObject();
            ensureLinks();
            for (Map.Entry<String, LinkImpl> entry0 : links.entrySet()) {
                LinkImpl links_ = entry0.getValue();
                JsonElement linksOut_ = links_ == null ? JsonNull.INSTANCE : links_.toJsonElement();
                linksOut.add(entry0.getKey(), linksOut_);
            }
            result.add(linksOut);
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ItemImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ItemImpl dto = new ItemImpl();
            JsonArray json = jsonElem.getAsJsonArray();

            if (0 < json.size()) {
                JsonElement idIn = json.get(0);
                java.lang.String idOut = gson.fromJson(idIn, java.lang.String.class);
                dto.setId(idOut);
            }

            if (1 < json.size()) {
                JsonElement nameIn = json.get(1);
                java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
                dto.setName(nameOut);
            }

            if (2 < json.size()) {
                JsonElement itemTypeIn = json.get(2);
                com.codenvy.ide.collaboration.dto.Item.ItemType itemTypeOut =
                        gson.fromJson(itemTypeIn, com.codenvy.ide.collaboration.dto.Item.ItemType.class);
                dto.setItemType(itemTypeOut);
            }

            if (3 < json.size()) {
                JsonElement pathIn = json.get(3);
                java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
                dto.setPath(pathOut);
            }

            if (4 < json.size()) {
                JsonElement parentIdIn = json.get(4);
                java.lang.String parentIdOut = gson.fromJson(parentIdIn, java.lang.String.class);
                dto.setParentId(parentIdOut);
            }

            if (5 < json.size()) {
                JsonElement mimeTypeIn = json.get(5);
                java.lang.String mimeTypeOut = gson.fromJson(mimeTypeIn, java.lang.String.class);
                dto.setMimeType(mimeTypeOut);
            }

            if (6 < json.size()) {
                JsonElement propertiesIn = json.get(6);
                java.util.ArrayList<PropertyImpl> propertiesOut = null;
                if (propertiesIn != null && !propertiesIn.isJsonNull()) {
                    propertiesOut = new java.util.ArrayList<PropertyImpl>();
                    java.util.Iterator<JsonElement> propertiesInIterator = propertiesIn.getAsJsonArray().iterator();
                    while (propertiesInIterator.hasNext()) {
                        JsonElement propertiesIn_ = propertiesInIterator.next();
                        PropertyImpl propertiesOut_ = PropertyImpl.fromJsonElement(propertiesIn_);
                        propertiesOut.add(propertiesOut_);
                    }
                }
                dto.setProperties(propertiesOut);
            }

            if (7 < json.size()) {
                JsonElement linksIn = json.get(7);
                java.util.HashMap<String, LinkImpl> linksOut = null;
                if (linksIn != null && !linksIn.isJsonNull()) {
                    linksOut = new java.util.HashMap<String, LinkImpl>();
                    java.util.Set<Map.Entry<String, JsonElement>> entries0 = linksIn.getAsJsonObject().entrySet();
                    for (Map.Entry<String, JsonElement> entry0 : entries0) {
                        JsonElement linksIn_ = entry0.getValue();
                        LinkImpl linksOut_ = LinkImpl.fromJsonElement(linksIn_);
                        linksOut.put(entry0.getKey(), linksOut_);
                    }
                }
                dto.setLinks(linksOut);
            }

            return dto;
        }

        public static ItemImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ItemCreatedDtoImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ItemCreatedDto, JsonSerializable {

        private ItemCreatedDtoImpl() {
            super(4);
        }

        protected ItemCreatedDtoImpl(int type) {
            super(type);
        }

        public static ItemCreatedDtoImpl make() {
            return new ItemCreatedDtoImpl();
        }

        protected ItemImpl         item;
        private   boolean          _hasItem;
        protected java.lang.String userId;
        private   boolean          _hasUserId;

        public boolean hasItem() {
            return _hasItem;
        }

        @Override
        public com.codenvy.ide.collaboration.dto.Item getItem() {
            return item;
        }

        public ItemCreatedDtoImpl setItem(ItemImpl v) {
            _hasItem = true;
            item = v;
            return this;
        }

        public boolean hasUserId() {
            return _hasUserId;
        }

        @Override
        public java.lang.String getUserId() {
            return userId;
        }

        public ItemCreatedDtoImpl setUserId(java.lang.String v) {
            _hasUserId = true;
            userId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ItemCreatedDtoImpl)) {
                return false;
            }
            ItemCreatedDtoImpl other = (ItemCreatedDtoImpl)o;
            if (this._hasItem != other._hasItem) {
                return false;
            }
            if (this._hasItem) {
                if (!this.item.equals(other.item)) {
                    return false;
                }
            }
            if (this._hasUserId != other._hasUserId) {
                return false;
            }
            if (this._hasUserId) {
                if (!this.userId.equals(other.userId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasItem ? item.hashCode() : 0);
            hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement itemOut = item == null ? JsonNull.INSTANCE : item.toJsonElement();
            result.add("item", itemOut);

            JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
            result.add("userId", userIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ItemCreatedDtoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ItemCreatedDtoImpl dto = new ItemCreatedDtoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("item")) {
                JsonElement itemIn = json.get("item");
                ItemImpl itemOut = ItemImpl.fromJsonElement(itemIn);
                dto.setItem(itemOut);
            }

            if (json.has("userId")) {
                JsonElement userIdIn = json.get("userId");
                java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
                dto.setUserId(userIdOut);
            }

            return dto;
        }

        public static ItemCreatedDtoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ItemDeletedDtoImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ItemDeletedDto, JsonSerializable {

        private ItemDeletedDtoImpl() {
            super(3);
        }

        protected ItemDeletedDtoImpl(int type) {
            super(type);
        }

        public static ItemDeletedDtoImpl make() {
            return new ItemDeletedDtoImpl();
        }

        protected java.lang.String filePath;
        private   boolean          _hasFilePath;
        protected java.lang.String fileId;
        private   boolean          _hasFileId;
        protected java.lang.String userId;
        private   boolean          _hasUserId;

        public boolean hasFilePath() {
            return _hasFilePath;
        }

        @Override
        public java.lang.String getFilePath() {
            return filePath;
        }

        public ItemDeletedDtoImpl setFilePath(java.lang.String v) {
            _hasFilePath = true;
            filePath = v;
            return this;
        }

        public boolean hasFileId() {
            return _hasFileId;
        }

        @Override
        public java.lang.String getFileId() {
            return fileId;
        }

        public ItemDeletedDtoImpl setFileId(java.lang.String v) {
            _hasFileId = true;
            fileId = v;
            return this;
        }

        public boolean hasUserId() {
            return _hasUserId;
        }

        @Override
        public java.lang.String getUserId() {
            return userId;
        }

        public ItemDeletedDtoImpl setUserId(java.lang.String v) {
            _hasUserId = true;
            userId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ItemDeletedDtoImpl)) {
                return false;
            }
            ItemDeletedDtoImpl other = (ItemDeletedDtoImpl)o;
            if (this._hasFilePath != other._hasFilePath) {
                return false;
            }
            if (this._hasFilePath) {
                if (!this.filePath.equals(other.filePath)) {
                    return false;
                }
            }
            if (this._hasFileId != other._hasFileId) {
                return false;
            }
            if (this._hasFileId) {
                if (!this.fileId.equals(other.fileId)) {
                    return false;
                }
            }
            if (this._hasUserId != other._hasUserId) {
                return false;
            }
            if (this._hasUserId) {
                if (!this.userId.equals(other.userId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasFilePath ? filePath.hashCode() : 0);
            hash = hash * 31 + (_hasFileId ? fileId.hashCode() : 0);
            hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement filePathOut = (filePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(filePath);
            result.add("filePath", filePathOut);

            JsonElement fileIdOut = (fileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileId);
            result.add("fileId", fileIdOut);

            JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
            result.add("userId", userIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ItemDeletedDtoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ItemDeletedDtoImpl dto = new ItemDeletedDtoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("filePath")) {
                JsonElement filePathIn = json.get("filePath");
                java.lang.String filePathOut = gson.fromJson(filePathIn, java.lang.String.class);
                dto.setFilePath(filePathOut);
            }

            if (json.has("fileId")) {
                JsonElement fileIdIn = json.get("fileId");
                java.lang.String fileIdOut = gson.fromJson(fileIdIn, java.lang.String.class);
                dto.setFileId(fileIdOut);
            }

            if (json.has("userId")) {
                JsonElement userIdIn = json.get("userId");
                java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
                dto.setUserId(userIdOut);
            }

            return dto;
        }

        public static ItemDeletedDtoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ItemMovedDtoImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ItemMovedDto, JsonSerializable {

        private ItemMovedDtoImpl() {
            super(5);
        }

        protected ItemMovedDtoImpl(int type) {
            super(type);
        }

        public static ItemMovedDtoImpl make() {
            return new ItemMovedDtoImpl();
        }

        protected java.lang.String oldPath;
        private   boolean          _hasOldPath;
        protected ItemImpl         movedItem;
        private   boolean          _hasMovedItem;
        protected java.lang.String userId;
        private   boolean          _hasUserId;

        public boolean hasOldPath() {
            return _hasOldPath;
        }

        @Override
        public java.lang.String oldPath() {
            return oldPath;
        }

        public ItemMovedDtoImpl setOldPath(java.lang.String v) {
            _hasOldPath = true;
            oldPath = v;
            return this;
        }

        public boolean hasMovedItem() {
            return _hasMovedItem;
        }

        @Override
        public com.codenvy.ide.collaboration.dto.Item movedItem() {
            return movedItem;
        }

        public ItemMovedDtoImpl setMovedItem(ItemImpl v) {
            _hasMovedItem = true;
            movedItem = v;
            return this;
        }

        public boolean hasUserId() {
            return _hasUserId;
        }

        @Override
        public java.lang.String getUserId() {
            return userId;
        }

        public ItemMovedDtoImpl setUserId(java.lang.String v) {
            _hasUserId = true;
            userId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ItemMovedDtoImpl)) {
                return false;
            }
            ItemMovedDtoImpl other = (ItemMovedDtoImpl)o;
            if (this._hasOldPath != other._hasOldPath) {
                return false;
            }
            if (this._hasOldPath) {
                if (!this.oldPath.equals(other.oldPath)) {
                    return false;
                }
            }
            if (this._hasMovedItem != other._hasMovedItem) {
                return false;
            }
            if (this._hasMovedItem) {
                if (!this.movedItem.equals(other.movedItem)) {
                    return false;
                }
            }
            if (this._hasUserId != other._hasUserId) {
                return false;
            }
            if (this._hasUserId) {
                if (!this.userId.equals(other.userId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasOldPath ? oldPath.hashCode() : 0);
            hash = hash * 31 + (_hasMovedItem ? movedItem.hashCode() : 0);
            hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement oldPathOut = (oldPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(oldPath);
            result.add("oldPath", oldPathOut);

            JsonElement movedItemOut = movedItem == null ? JsonNull.INSTANCE : movedItem.toJsonElement();
            result.add("movedItem", movedItemOut);

            JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
            result.add("userId", userIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ItemMovedDtoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ItemMovedDtoImpl dto = new ItemMovedDtoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("oldPath")) {
                JsonElement oldPathIn = json.get("oldPath");
                java.lang.String oldPathOut = gson.fromJson(oldPathIn, java.lang.String.class);
                dto.setOldPath(oldPathOut);
            }

            if (json.has("movedItem")) {
                JsonElement movedItemIn = json.get("movedItem");
                ItemImpl movedItemOut = ItemImpl.fromJsonElement(movedItemIn);
                dto.setMovedItem(movedItemOut);
            }

            if (json.has("userId")) {
                JsonElement userIdIn = json.get("userId");
                java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
                dto.setUserId(userIdOut);
            }

            return dto;
        }

        public static ItemMovedDtoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ItemRenamedDtoImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ItemRenamedDto, JsonSerializable {

        private ItemRenamedDtoImpl() {
            super(6);
        }

        protected ItemRenamedDtoImpl(int type) {
            super(type);
        }

        public static ItemRenamedDtoImpl make() {
            return new ItemRenamedDtoImpl();
        }

        protected ItemImpl         renamedItem;
        private   boolean          _hasRenamedItem;
        protected java.lang.String oldPath;
        private   boolean          _hasOldPath;
        protected java.lang.String userId;
        private   boolean          _hasUserId;

        public boolean hasRenamedItem() {
            return _hasRenamedItem;
        }

        @Override
        public com.codenvy.ide.collaboration.dto.Item renamedItem() {
            return renamedItem;
        }

        public ItemRenamedDtoImpl setRenamedItem(ItemImpl v) {
            _hasRenamedItem = true;
            renamedItem = v;
            return this;
        }

        public boolean hasOldPath() {
            return _hasOldPath;
        }

        @Override
        public java.lang.String oldPath() {
            return oldPath;
        }

        public ItemRenamedDtoImpl setOldPath(java.lang.String v) {
            _hasOldPath = true;
            oldPath = v;
            return this;
        }

        public boolean hasUserId() {
            return _hasUserId;
        }

        @Override
        public java.lang.String getUserId() {
            return userId;
        }

        public ItemRenamedDtoImpl setUserId(java.lang.String v) {
            _hasUserId = true;
            userId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ItemRenamedDtoImpl)) {
                return false;
            }
            ItemRenamedDtoImpl other = (ItemRenamedDtoImpl)o;
            if (this._hasRenamedItem != other._hasRenamedItem) {
                return false;
            }
            if (this._hasRenamedItem) {
                if (!this.renamedItem.equals(other.renamedItem)) {
                    return false;
                }
            }
            if (this._hasOldPath != other._hasOldPath) {
                return false;
            }
            if (this._hasOldPath) {
                if (!this.oldPath.equals(other.oldPath)) {
                    return false;
                }
            }
            if (this._hasUserId != other._hasUserId) {
                return false;
            }
            if (this._hasUserId) {
                if (!this.userId.equals(other.userId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasRenamedItem ? renamedItem.hashCode() : 0);
            hash = hash * 31 + (_hasOldPath ? oldPath.hashCode() : 0);
            hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement renamedItemOut = renamedItem == null ? JsonNull.INSTANCE : renamedItem.toJsonElement();
            result.add("renamedItem", renamedItemOut);

            JsonElement oldPathOut = (oldPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(oldPath);
            result.add("oldPath", oldPathOut);

            JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
            result.add("userId", userIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ItemRenamedDtoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ItemRenamedDtoImpl dto = new ItemRenamedDtoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("renamedItem")) {
                JsonElement renamedItemIn = json.get("renamedItem");
                ItemImpl renamedItemOut = ItemImpl.fromJsonElement(renamedItemIn);
                dto.setRenamedItem(renamedItemOut);
            }

            if (json.has("oldPath")) {
                JsonElement oldPathIn = json.get("oldPath");
                java.lang.String oldPathOut = gson.fromJson(oldPathIn, java.lang.String.class);
                dto.setOldPath(oldPathOut);
            }

            if (json.has("userId")) {
                JsonElement userIdIn = json.get("userId");
                java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
                dto.setUserId(userIdOut);
            }

            return dto;
        }

        public static ItemRenamedDtoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class LinkImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.Link, JsonSerializable {

        public static LinkImpl make() {
            return new LinkImpl();
        }

        protected java.lang.String href;
        private   boolean          _hasHref;
        protected java.lang.String rel;
        private   boolean          _hasRel;
        protected java.lang.String typeLink;
        private   boolean          _hasTypeLink;

        public boolean hasHref() {
            return _hasHref;
        }

        @Override
        public java.lang.String getHref() {
            return href;
        }

        public LinkImpl setHref(java.lang.String v) {
            _hasHref = true;
            href = v;
            return this;
        }

        public boolean hasRel() {
            return _hasRel;
        }

        @Override
        public java.lang.String getRel() {
            return rel;
        }

        public LinkImpl setRel(java.lang.String v) {
            _hasRel = true;
            rel = v;
            return this;
        }

        public boolean hasTypeLink() {
            return _hasTypeLink;
        }

        @Override
        public java.lang.String getTypeLink() {
            return typeLink;
        }

        public LinkImpl setTypeLink(java.lang.String v) {
            _hasTypeLink = true;
            typeLink = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof LinkImpl)) {
                return false;
            }
            LinkImpl other = (LinkImpl)o;
            if (this._hasHref != other._hasHref) {
                return false;
            }
            if (this._hasHref) {
                if (!this.href.equals(other.href)) {
                    return false;
                }
            }
            if (this._hasRel != other._hasRel) {
                return false;
            }
            if (this._hasRel) {
                if (!this.rel.equals(other.rel)) {
                    return false;
                }
            }
            if (this._hasTypeLink != other._hasTypeLink) {
                return false;
            }
            if (this._hasTypeLink) {
                if (!this.typeLink.equals(other.typeLink)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasHref ? href.hashCode() : 0);
            hash = hash * 31 + (_hasRel ? rel.hashCode() : 0);
            hash = hash * 31 + (_hasTypeLink ? typeLink.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonArray result = new JsonArray();

            JsonElement hrefOut = (href == null) ? JsonNull.INSTANCE : new JsonPrimitive(href);
            result.add(hrefOut);

            JsonElement relOut = (rel == null) ? JsonNull.INSTANCE : new JsonPrimitive(rel);
            result.add(relOut);

            JsonElement typeLinkOut = (typeLink == null) ? JsonNull.INSTANCE : new JsonPrimitive(typeLink);
            result.add(typeLinkOut);
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static LinkImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            LinkImpl dto = new LinkImpl();
            JsonArray json = jsonElem.getAsJsonArray();

            if (0 < json.size()) {
                JsonElement hrefIn = json.get(0);
                java.lang.String hrefOut = gson.fromJson(hrefIn, java.lang.String.class);
                dto.setHref(hrefOut);
            }

            if (1 < json.size()) {
                JsonElement relIn = json.get(1);
                java.lang.String relOut = gson.fromJson(relIn, java.lang.String.class);
                dto.setRel(relOut);
            }

            if (2 < json.size()) {
                JsonElement typeLinkIn = json.get(2);
                java.lang.String typeLinkOut = gson.fromJson(typeLinkIn, java.lang.String.class);
                dto.setTypeLink(typeLinkOut);
            }

            return dto;
        }

        public static LinkImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ParticipantInfoImpl implements com.codenvy.ide.collaboration.dto.ParticipantInfo, JsonSerializable {

        public static ParticipantInfoImpl make() {
            return new ParticipantInfoImpl();
        }

        protected UserDetailsImpl  userDetails;
        private   boolean          _hasUserDetails;
        protected java.lang.String clientId;
        private   boolean          _hasClientId;

        public boolean hasUserDetails() {
            return _hasUserDetails;
        }

        @Override
        public com.codenvy.ide.collaboration.dto.UserDetails getUserDetails() {
            return userDetails;
        }

        public ParticipantInfoImpl setUserDetails(UserDetailsImpl v) {
            _hasUserDetails = true;
            userDetails = v;
            return this;
        }

        public boolean hasClientId() {
            return _hasClientId;
        }

        @Override
        public java.lang.String getClientId() {
            return clientId;
        }

        public ParticipantInfoImpl setClientId(java.lang.String v) {
            _hasClientId = true;
            clientId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ParticipantInfoImpl)) {
                return false;
            }
            ParticipantInfoImpl other = (ParticipantInfoImpl)o;
            if (this._hasUserDetails != other._hasUserDetails) {
                return false;
            }
            if (this._hasUserDetails) {
                if (!this.userDetails.equals(other.userDetails)) {
                    return false;
                }
            }
            if (this._hasClientId != other._hasClientId) {
                return false;
            }
            if (this._hasClientId) {
                if (!this.clientId.equals(other.clientId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * 31 + (_hasUserDetails ? userDetails.hashCode() : 0);
            hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement userDetailsOut = userDetails == null ? JsonNull.INSTANCE : userDetails.toJsonElement();
            result.add("userDetails", userDetailsOut);

            JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
            result.add("clientId", clientIdOut);
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ParticipantInfoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ParticipantInfoImpl dto = new ParticipantInfoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("userDetails")) {
                JsonElement userDetailsIn = json.get("userDetails");
                UserDetailsImpl userDetailsOut = UserDetailsImpl.fromJsonElement(userDetailsIn);
                dto.setUserDetails(userDetailsOut);
            }

            if (json.has("clientId")) {
                JsonElement clientIdIn = json.get("clientId");
                java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
                dto.setClientId(clientIdOut);
            }

            return dto;
        }

        public static ParticipantInfoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ProjectClosedDtoImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ProjectClosedDto, JsonSerializable {

        private ProjectClosedDtoImpl() {
            super(2);
        }

        protected ProjectClosedDtoImpl(int type) {
            super(type);
        }

        protected java.lang.String projectPath;
        private   boolean          _hasProjectPath;
        protected java.lang.String vfsId;
        private   boolean          _hasVfsId;
        protected java.lang.String projectId;
        private   boolean          _hasProjectId;

        public boolean hasProjectPath() {
            return _hasProjectPath;
        }

        @Override
        public java.lang.String projectPath() {
            return projectPath;
        }

        public ProjectClosedDtoImpl setProjectPath(java.lang.String v) {
            _hasProjectPath = true;
            projectPath = v;
            return this;
        }

        public boolean hasVfsId() {
            return _hasVfsId;
        }

        @Override
        public java.lang.String vfsId() {
            return vfsId;
        }

        public ProjectClosedDtoImpl setVfsId(java.lang.String v) {
            _hasVfsId = true;
            vfsId = v;
            return this;
        }

        public boolean hasProjectId() {
            return _hasProjectId;
        }

        @Override
        public java.lang.String projectId() {
            return projectId;
        }

        public ProjectClosedDtoImpl setProjectId(java.lang.String v) {
            _hasProjectId = true;
            projectId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ProjectClosedDtoImpl)) {
                return false;
            }
            ProjectClosedDtoImpl other = (ProjectClosedDtoImpl)o;
            if (this._hasProjectPath != other._hasProjectPath) {
                return false;
            }
            if (this._hasProjectPath) {
                if (!this.projectPath.equals(other.projectPath)) {
                    return false;
                }
            }
            if (this._hasVfsId != other._hasVfsId) {
                return false;
            }
            if (this._hasVfsId) {
                if (!this.vfsId.equals(other.vfsId)) {
                    return false;
                }
            }
            if (this._hasProjectId != other._hasProjectId) {
                return false;
            }
            if (this._hasProjectId) {
                if (!this.projectId.equals(other.projectId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasProjectPath ? projectPath.hashCode() : 0);
            hash = hash * 31 + (_hasVfsId ? vfsId.hashCode() : 0);
            hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement projectPathOut = (projectPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectPath);
            result.add("projectPath", projectPathOut);

            JsonElement vfsIdOut = (vfsId == null) ? JsonNull.INSTANCE : new JsonPrimitive(vfsId);
            result.add("vfsId", vfsIdOut);

            JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
            result.add("projectId", projectIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ProjectClosedDtoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ProjectClosedDtoImpl dto = new ProjectClosedDtoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("projectPath")) {
                JsonElement projectPathIn = json.get("projectPath");
                java.lang.String projectPathOut = gson.fromJson(projectPathIn, java.lang.String.class);
                dto.setProjectPath(projectPathOut);
            }

            if (json.has("vfsId")) {
                JsonElement vfsIdIn = json.get("vfsId");
                java.lang.String vfsIdOut = gson.fromJson(vfsIdIn, java.lang.String.class);
                dto.setVfsId(vfsIdOut);
            }

            if (json.has("projectId")) {
                JsonElement projectIdIn = json.get("projectId");
                java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
                dto.setProjectId(projectIdOut);
            }

            return dto;
        }

        public static ProjectClosedDtoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ProjectOpenedDtoImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ProjectOpenedDto, JsonSerializable {

        private ProjectOpenedDtoImpl() {
            super(1);
        }

        protected ProjectOpenedDtoImpl(int type) {
            super(type);
        }

        protected java.lang.String projectPath;
        private   boolean          _hasProjectPath;
        protected java.lang.String vfsId;
        private   boolean          _hasVfsId;
        protected java.lang.String projectId;
        private   boolean          _hasProjectId;

        public boolean hasProjectPath() {
            return _hasProjectPath;
        }

        @Override
        public java.lang.String projectPath() {
            return projectPath;
        }

        public ProjectOpenedDtoImpl setProjectPath(java.lang.String v) {
            _hasProjectPath = true;
            projectPath = v;
            return this;
        }

        public boolean hasVfsId() {
            return _hasVfsId;
        }

        @Override
        public java.lang.String vfsId() {
            return vfsId;
        }

        public ProjectOpenedDtoImpl setVfsId(java.lang.String v) {
            _hasVfsId = true;
            vfsId = v;
            return this;
        }

        public boolean hasProjectId() {
            return _hasProjectId;
        }

        @Override
        public java.lang.String projectId() {
            return projectId;
        }

        public ProjectOpenedDtoImpl setProjectId(java.lang.String v) {
            _hasProjectId = true;
            projectId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ProjectOpenedDtoImpl)) {
                return false;
            }
            ProjectOpenedDtoImpl other = (ProjectOpenedDtoImpl)o;
            if (this._hasProjectPath != other._hasProjectPath) {
                return false;
            }
            if (this._hasProjectPath) {
                if (!this.projectPath.equals(other.projectPath)) {
                    return false;
                }
            }
            if (this._hasVfsId != other._hasVfsId) {
                return false;
            }
            if (this._hasVfsId) {
                if (!this.vfsId.equals(other.vfsId)) {
                    return false;
                }
            }
            if (this._hasProjectId != other._hasProjectId) {
                return false;
            }
            if (this._hasProjectId) {
                if (!this.projectId.equals(other.projectId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasProjectPath ? projectPath.hashCode() : 0);
            hash = hash * 31 + (_hasVfsId ? vfsId.hashCode() : 0);
            hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement projectPathOut = (projectPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectPath);
            result.add("projectPath", projectPathOut);

            JsonElement vfsIdOut = (vfsId == null) ? JsonNull.INSTANCE : new JsonPrimitive(vfsId);
            result.add("vfsId", vfsIdOut);

            JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
            result.add("projectId", projectIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ProjectOpenedDtoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ProjectOpenedDtoImpl dto = new ProjectOpenedDtoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("projectPath")) {
                JsonElement projectPathIn = json.get("projectPath");
                java.lang.String projectPathOut = gson.fromJson(projectPathIn, java.lang.String.class);
                dto.setProjectPath(projectPathOut);
            }

            if (json.has("vfsId")) {
                JsonElement vfsIdIn = json.get("vfsId");
                java.lang.String vfsIdOut = gson.fromJson(vfsIdIn, java.lang.String.class);
                dto.setVfsId(vfsIdOut);
            }

            if (json.has("projectId")) {
                JsonElement projectIdIn = json.get("projectId");
                java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
                dto.setProjectId(projectIdOut);
            }

            return dto;
        }

        public static ProjectOpenedDtoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ProjectOpenedResponseDtoImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ProjectOpenedResponseDto, JsonSerializable {

        private ProjectOpenedResponseDtoImpl() {
            super(9);
        }

        protected ProjectOpenedResponseDtoImpl(int type) {
            super(type);
        }

        public static ProjectOpenedResponseDtoImpl make() {
            return new ProjectOpenedResponseDtoImpl();
        }

        protected java.util.List<ParticipantInfoImpl> projectParticipants;
        private   boolean                             _hasProjectParticipants;

        public boolean hasProjectParticipants() {
            return _hasProjectParticipants;
        }

        @Override
        public com.codenvy.ide.json.shared.JsonArray<com.codenvy.ide.collaboration.dto.ParticipantInfo> projectParticipants() {
            ensureProjectParticipants();
            return (com.codenvy.ide.json.shared.JsonArray)new com.codenvy.ide.json.server.JsonArrayListAdapter(projectParticipants);
        }

        public ProjectOpenedResponseDtoImpl setProjectParticipants(java.util.List<ParticipantInfoImpl> v) {
            _hasProjectParticipants = true;
            projectParticipants = v;
            return this;
        }

        public void addProjectParticipants(ParticipantInfoImpl v) {
            ensureProjectParticipants();
            projectParticipants.add(v);
        }

        public void clearProjectParticipants() {
            ensureProjectParticipants();
            projectParticipants.clear();
        }

        private void ensureProjectParticipants() {
            if (!_hasProjectParticipants) {
                setProjectParticipants(projectParticipants != null ? projectParticipants : new java.util.ArrayList<ParticipantInfoImpl>());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ProjectOpenedResponseDtoImpl)) {
                return false;
            }
            ProjectOpenedResponseDtoImpl other = (ProjectOpenedResponseDtoImpl)o;
            if (this._hasProjectParticipants != other._hasProjectParticipants) {
                return false;
            }
            if (this._hasProjectParticipants) {
                if (!this.projectParticipants.equals(other.projectParticipants)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasProjectParticipants ? projectParticipants.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonArray projectParticipantsOut = new JsonArray();
            ensureProjectParticipants();
            for (ParticipantInfoImpl projectParticipants_ : projectParticipants) {
                JsonElement projectParticipantsOut_ =
                        projectParticipants_ == null ? JsonNull.INSTANCE : projectParticipants_.toJsonElement();
                projectParticipantsOut.add(projectParticipantsOut_);
            }
            result.add("projectParticipants", projectParticipantsOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ProjectOpenedResponseDtoImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ProjectOpenedResponseDtoImpl dto = new ProjectOpenedResponseDtoImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("projectParticipants")) {
                JsonElement projectParticipantsIn = json.get("projectParticipants");
                java.util.ArrayList<ParticipantInfoImpl> projectParticipantsOut = null;
                if (projectParticipantsIn != null && !projectParticipantsIn.isJsonNull()) {
                    projectParticipantsOut = new java.util.ArrayList<ParticipantInfoImpl>();
                    java.util.Iterator<JsonElement> projectParticipantsInIterator = projectParticipantsIn.getAsJsonArray().iterator();
                    while (projectParticipantsInIterator.hasNext()) {
                        JsonElement projectParticipantsIn_ = projectParticipantsInIterator.next();
                        ParticipantInfoImpl projectParticipantsOut_ = ParticipantInfoImpl.fromJsonElement(projectParticipantsIn_);
                        projectParticipantsOut.add(projectParticipantsOut_);
                    }
                }
                dto.setProjectParticipants(projectParticipantsOut);
            }

            return dto;
        }

        public static ProjectOpenedResponseDtoImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class ProjectOperationNotificationImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.ProjectOperationNotification, JsonSerializable {

        private ProjectOperationNotificationImpl() {
            super(12);
        }

        protected ProjectOperationNotificationImpl(int type) {
            super(type);
        }

        public static ProjectOperationNotificationImpl make() {
            return new ProjectOperationNotificationImpl();
        }

        protected java.lang.String clientId;
        private   boolean          _hasClientId;
        protected java.lang.String message;
        private   boolean          _hasMessage;
        protected java.lang.String projectId;
        private   boolean          _hasProjectId;

        public boolean hasClientId() {
            return _hasClientId;
        }

        @Override
        public java.lang.String clientId() {
            return clientId;
        }

        public ProjectOperationNotificationImpl setClientId(java.lang.String v) {
            _hasClientId = true;
            clientId = v;
            return this;
        }

        public boolean hasMessage() {
            return _hasMessage;
        }

        @Override
        public java.lang.String message() {
            return message;
        }

        public ProjectOperationNotificationImpl setMessage(java.lang.String v) {
            _hasMessage = true;
            message = v;
            return this;
        }

        public boolean hasProjectId() {
            return _hasProjectId;
        }

        @Override
        public java.lang.String projectId() {
            return projectId;
        }

        public ProjectOperationNotificationImpl setProjectId(java.lang.String v) {
            _hasProjectId = true;
            projectId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof ProjectOperationNotificationImpl)) {
                return false;
            }
            ProjectOperationNotificationImpl other = (ProjectOperationNotificationImpl)o;
            if (this._hasClientId != other._hasClientId) {
                return false;
            }
            if (this._hasClientId) {
                if (!this.clientId.equals(other.clientId)) {
                    return false;
                }
            }
            if (this._hasMessage != other._hasMessage) {
                return false;
            }
            if (this._hasMessage) {
                if (!this.message.equals(other.message)) {
                    return false;
                }
            }
            if (this._hasProjectId != other._hasProjectId) {
                return false;
            }
            if (this._hasProjectId) {
                if (!this.projectId.equals(other.projectId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
            hash = hash * 31 + (_hasMessage ? message.hashCode() : 0);
            hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
            result.add("clientId", clientIdOut);

            JsonElement messageOut = (message == null) ? JsonNull.INSTANCE : new JsonPrimitive(message);
            result.add("message", messageOut);

            JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
            result.add("projectId", projectIdOut);
            result.add("_type", new JsonPrimitive(getType()));
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static ProjectOperationNotificationImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            ProjectOperationNotificationImpl dto = new ProjectOperationNotificationImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("clientId")) {
                JsonElement clientIdIn = json.get("clientId");
                java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
                dto.setClientId(clientIdOut);
            }

            if (json.has("message")) {
                JsonElement messageIn = json.get("message");
                java.lang.String messageOut = gson.fromJson(messageIn, java.lang.String.class);
                dto.setMessage(messageOut);
            }

            if (json.has("projectId")) {
                JsonElement projectIdIn = json.get("projectId");
                java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
                dto.setProjectId(projectIdOut);
            }

            return dto;
        }

        public static ProjectOperationNotificationImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class PropertyImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl
            implements com.codenvy.ide.collaboration.dto.Property, JsonSerializable {

        public static PropertyImpl make() {
            return new PropertyImpl();
        }

        protected java.lang.String                 name;
        private   boolean                          _hasName;
        protected java.util.List<java.lang.String> value;
        private   boolean                          _hasValue;

        public boolean hasName() {
            return _hasName;
        }

        @Override
        public java.lang.String getName() {
            return name;
        }

        public PropertyImpl setName(java.lang.String v) {
            _hasName = true;
            name = v;
            return this;
        }

        public boolean hasValue() {
            return _hasValue;
        }

        @Override
        public com.codenvy.ide.json.shared.JsonArray<java.lang.String> getValue() {
            ensureValue();
            return (com.codenvy.ide.json.shared.JsonArray)new com.codenvy.ide.json.server.JsonArrayListAdapter(value);
        }

        public PropertyImpl setValue(java.util.List<java.lang.String> v) {
            _hasValue = true;
            value = v;
            return this;
        }

        public void addValue(java.lang.String v) {
            ensureValue();
            value.add(v);
        }

        public void clearValue() {
            ensureValue();
            value.clear();
        }

        private void ensureValue() {
            if (!_hasValue) {
                setValue(value != null ? value : new java.util.ArrayList<java.lang.String>());
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!super.equals(o)) {
                return false;
            }
            if (!(o instanceof PropertyImpl)) {
                return false;
            }
            PropertyImpl other = (PropertyImpl)o;
            if (this._hasName != other._hasName) {
                return false;
            }
            if (this._hasName) {
                if (!this.name.equals(other.name)) {
                    return false;
                }
            }
            if (this._hasValue != other._hasValue) {
                return false;
            }
            if (this._hasValue) {
                if (!this.value.equals(other.value)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash = hash * 31 + (_hasName ? name.hashCode() : 0);
            hash = hash * 31 + (_hasValue ? value.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonArray result = new JsonArray();

            JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
            result.add(nameOut);

            JsonArray valueOut = new JsonArray();
            ensureValue();
            for (java.lang.String value_ : value) {
                JsonElement valueOut_ = (value_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(value_);
                valueOut.add(valueOut_);
            }
            if (valueOut.size() != 0) {
                result.add(valueOut);
            }
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static PropertyImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            PropertyImpl dto = new PropertyImpl();
            JsonArray json = jsonElem.getAsJsonArray();

            if (0 < json.size()) {
                JsonElement nameIn = json.get(0);
                java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
                dto.setName(nameOut);
            }

            if (1 < json.size()) {
                JsonElement valueIn = json.get(1);
                java.util.ArrayList<java.lang.String> valueOut = null;
                if (valueIn != null && !valueIn.isJsonNull()) {
                    valueOut = new java.util.ArrayList<java.lang.String>();
                    java.util.Iterator<JsonElement> valueInIterator = valueIn.getAsJsonArray().iterator();
                    while (valueInIterator.hasNext()) {
                        JsonElement valueIn_ = valueInIterator.next();
                        java.lang.String valueOut_ = gson.fromJson(valueIn_, java.lang.String.class);
                        valueOut.add(valueOut_);
                    }
                }
                dto.setValue(valueOut);
            }

            return dto;
        }

        public static PropertyImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

    public static class UserDetailsImpl implements com.codenvy.ide.collaboration.dto.UserDetails, JsonSerializable {

        public static UserDetailsImpl make() {
            return new UserDetailsImpl();
        }

        protected java.lang.String givenName;
        private   boolean          _hasGivenName;
        protected java.lang.String displayEmail;
        private   boolean          _hasDisplayEmail;
        protected java.lang.String portraitUrl;
        private   boolean          _hasPortraitUrl;
        protected boolean          isCurrentUser;
        private   boolean          _hasIsCurrentUser;
        protected java.lang.String displayName;
        private   boolean          _hasDisplayName;
        protected java.lang.String userId;
        private   boolean          _hasUserId;

        public boolean hasGivenName() {
            return _hasGivenName;
        }

        @Override
        public java.lang.String getGivenName() {
            return givenName;
        }

        public UserDetailsImpl setGivenName(java.lang.String v) {
            _hasGivenName = true;
            givenName = v;
            return this;
        }

        public boolean hasDisplayEmail() {
            return _hasDisplayEmail;
        }

        @Override
        public java.lang.String getDisplayEmail() {
            return displayEmail;
        }

        public UserDetailsImpl setDisplayEmail(java.lang.String v) {
            _hasDisplayEmail = true;
            displayEmail = v;
            return this;
        }

        public boolean hasPortraitUrl() {
            return _hasPortraitUrl;
        }

        @Override
        public java.lang.String getPortraitUrl() {
            return portraitUrl;
        }

        public UserDetailsImpl setPortraitUrl(java.lang.String v) {
            _hasPortraitUrl = true;
            portraitUrl = v;
            return this;
        }

        public boolean hasIsCurrentUser() {
            return _hasIsCurrentUser;
        }

        @Override
        public boolean isCurrentUser() {
            return isCurrentUser;
        }

        public UserDetailsImpl setIsCurrentUser(boolean v) {
            _hasIsCurrentUser = true;
            isCurrentUser = v;
            return this;
        }

        public boolean hasDisplayName() {
            return _hasDisplayName;
        }

        @Override
        public java.lang.String getDisplayName() {
            return displayName;
        }

        public UserDetailsImpl setDisplayName(java.lang.String v) {
            _hasDisplayName = true;
            displayName = v;
            return this;
        }

        public boolean hasUserId() {
            return _hasUserId;
        }

        @Override
        public java.lang.String getUserId() {
            return userId;
        }

        public UserDetailsImpl setUserId(java.lang.String v) {
            _hasUserId = true;
            userId = v;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof UserDetailsImpl)) {
                return false;
            }
            UserDetailsImpl other = (UserDetailsImpl)o;
            if (this._hasGivenName != other._hasGivenName) {
                return false;
            }
            if (this._hasGivenName) {
                if (!this.givenName.equals(other.givenName)) {
                    return false;
                }
            }
            if (this._hasDisplayEmail != other._hasDisplayEmail) {
                return false;
            }
            if (this._hasDisplayEmail) {
                if (!this.displayEmail.equals(other.displayEmail)) {
                    return false;
                }
            }
            if (this._hasPortraitUrl != other._hasPortraitUrl) {
                return false;
            }
            if (this._hasPortraitUrl) {
                if (!this.portraitUrl.equals(other.portraitUrl)) {
                    return false;
                }
            }
            if (this._hasIsCurrentUser != other._hasIsCurrentUser) {
                return false;
            }
            if (this._hasIsCurrentUser) {
                if (this.isCurrentUser != other.isCurrentUser) {
                    return false;
                }
            }
            if (this._hasDisplayName != other._hasDisplayName) {
                return false;
            }
            if (this._hasDisplayName) {
                if (!this.displayName.equals(other.displayName)) {
                    return false;
                }
            }
            if (this._hasUserId != other._hasUserId) {
                return false;
            }
            if (this._hasUserId) {
                if (!this.userId.equals(other.userId)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = hash * 31 + (_hasGivenName ? givenName.hashCode() : 0);
            hash = hash * 31 + (_hasDisplayEmail ? displayEmail.hashCode() : 0);
            hash = hash * 31 + (_hasPortraitUrl ? portraitUrl.hashCode() : 0);
            hash = hash * 31 + (_hasIsCurrentUser ? java.lang.Boolean.valueOf(isCurrentUser).hashCode() : 0);
            hash = hash * 31 + (_hasDisplayName ? displayName.hashCode() : 0);
            hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
            return hash;
        }

        @Override
        public JsonElement toJsonElement() {
            JsonObject result = new JsonObject();

            JsonElement givenNameOut = (givenName == null) ? JsonNull.INSTANCE : new JsonPrimitive(givenName);
            result.add("givenName", givenNameOut);

            JsonElement displayEmailOut = (displayEmail == null) ? JsonNull.INSTANCE : new JsonPrimitive(displayEmail);
            result.add("displayEmail", displayEmailOut);

            JsonElement portraitUrlOut = (portraitUrl == null) ? JsonNull.INSTANCE : new JsonPrimitive(portraitUrl);
            result.add("portraitUrl", portraitUrlOut);

            JsonPrimitive isCurrentUserOut = new JsonPrimitive(isCurrentUser);
            result.add("isCurrentUser", isCurrentUserOut);

            JsonElement displayNameOut = (displayName == null) ? JsonNull.INSTANCE : new JsonPrimitive(displayName);
            result.add("displayName", displayNameOut);

            JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
            result.add("userId", userIdOut);
            return result;
        }

        @Override
        public String toJson() {
            return gson.toJson(toJsonElement());
        }

        @Override
        public String toString() {
            return toJson();
        }

        public static UserDetailsImpl fromJsonElement(JsonElement jsonElem) {
            if (jsonElem == null || jsonElem.isJsonNull()) {
                return null;
            }

            UserDetailsImpl dto = new UserDetailsImpl();
            JsonObject json = jsonElem.getAsJsonObject();

            if (json.has("givenName")) {
                JsonElement givenNameIn = json.get("givenName");
                java.lang.String givenNameOut = gson.fromJson(givenNameIn, java.lang.String.class);
                dto.setGivenName(givenNameOut);
            }

            if (json.has("displayEmail")) {
                JsonElement displayEmailIn = json.get("displayEmail");
                java.lang.String displayEmailOut = gson.fromJson(displayEmailIn, java.lang.String.class);
                dto.setDisplayEmail(displayEmailOut);
            }

            if (json.has("portraitUrl")) {
                JsonElement portraitUrlIn = json.get("portraitUrl");
                java.lang.String portraitUrlOut = gson.fromJson(portraitUrlIn, java.lang.String.class);
                dto.setPortraitUrl(portraitUrlOut);
            }

            if (json.has("isCurrentUser")) {
                JsonElement isCurrentUserIn = json.get("isCurrentUser");
                boolean isCurrentUserOut = isCurrentUserIn.getAsBoolean();
                dto.setIsCurrentUser(isCurrentUserOut);
            }

            if (json.has("displayName")) {
                JsonElement displayNameIn = json.get("displayName");
                java.lang.String displayNameOut = gson.fromJson(displayNameIn, java.lang.String.class);
                dto.setDisplayName(displayNameOut);
            }

            if (json.has("userId")) {
                JsonElement userIdIn = json.get("userId");
                java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
                dto.setUserId(userIdOut);
            }

            return dto;
        }

        public static UserDetailsImpl fromJsonString(String jsonString) {
            if (jsonString == null) {
                return null;
            }

            return fromJsonElement(new JsonParser().parse(jsonString));
        }
    }

}