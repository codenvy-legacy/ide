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
package com.codenvy.ide.collaboration.dto.client;


@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

    private DtoClientImpls() {
    }

    public static final String CLIENT_SERVER_PROTOCOL_HASH = "7533dde2bfa2284b69172143482de0fd16524946";


    public static class ChatCodePointMessageImpl extends ChatMessageImpl implements com.codenvy.ide.collaboration.dto.ChatCodePointMessage {
        protected ChatCodePointMessageImpl() {
        }

        @Override
        public final native int getStartChar() /*-{
            return this["startChar"];
        }-*/;

        public final native ChatCodePointMessageImpl setStartChar(int startChar) /*-{
            this["startChar"] = startChar;
            return this;
        }-*/;

        public final native boolean hasStartChar() /*-{
            return this.hasOwnProperty("startChar");
        }-*/;

        @Override
        public final native int getEndChar() /*-{
            return this["endChar"];
        }-*/;

        public final native ChatCodePointMessageImpl setEndChar(int endChar) /*-{
            this["endChar"] = endChar;
            return this;
        }-*/;

        public final native boolean hasEndChar() /*-{
            return this.hasOwnProperty("endChar");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native ChatCodePointMessageImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        @Override
        public final native int getStartLine() /*-{
            return this["startLine"];
        }-*/;

        public final native ChatCodePointMessageImpl setStartLine(int startLine) /*-{
            this["startLine"] = startLine;
            return this;
        }-*/;

        public final native boolean hasStartLine() /*-{
            return this.hasOwnProperty("startLine");
        }-*/;

        @Override
        public final native int getEndLine() /*-{
            return this["endLine"];
        }-*/;

        public final native ChatCodePointMessageImpl setEndLine(int endLine) /*-{
            this["endLine"] = endLine;
            return this;
        }-*/;

        public final native boolean hasEndLine() /*-{
            return this.hasOwnProperty("endLine");
        }-*/;

        public static native ChatCodePointMessageImpl make() /*-{
            return {
                _type: 8
            };
        }-*/;
    }


    public static class ChatMessageImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ChatMessage {
        protected ChatMessageImpl() {
        }

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native ChatMessageImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String getDateTime() /*-{
            return this["dateTime"];
        }-*/;

        public final native ChatMessageImpl setDateTime(java.lang.String dateTime) /*-{
            this["dateTime"] = dateTime;
            return this;
        }-*/;

        public final native boolean hasDateTime() /*-{
            return this.hasOwnProperty("dateTime");
        }-*/;

        @Override
        public final native java.lang.String getMessage() /*-{
            return this["message"];
        }-*/;

        public final native ChatMessageImpl setMessage(java.lang.String message) /*-{
            this["message"] = message;
            return this;
        }-*/;

        public final native boolean hasMessage() /*-{
            return this.hasOwnProperty("message");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native ChatMessageImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native ChatMessageImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native ChatMessageImpl make() /*-{
            return {
                _type: 7
            };
        }-*/;
    }


    public static class ChatParticipantAddImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ChatParticipantAdd {
        protected ChatParticipantAddImpl() {
        }

        @Override
        public final native com.codenvy.ide.collaboration.dto.ParticipantInfo participant() /*-{
            return this["participant"];
        }-*/;

        public final native ChatParticipantAddImpl setParticipant(com.codenvy.ide.collaboration.dto.ParticipantInfo participant) /*-{
            this["participant"] = participant;
            return this;
        }-*/;

        public final native boolean hasParticipant() /*-{
            return this.hasOwnProperty("participant");
        }-*/;

        @Override
        public final native java.lang.String projectId() /*-{
            return this["projectId"];
        }-*/;

        public final native ChatParticipantAddImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native ChatParticipantAddImpl make() /*-{
            return {
                _type: 10
            };
        }-*/;
    }


    public static class ChatParticipantRemoveImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ChatParticipantRemove {
        protected ChatParticipantRemoveImpl() {
        }

        @Override
        public final native java.lang.String clientId() /*-{
            return this["clientId"];
        }-*/;

        public final native ChatParticipantRemoveImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String projectId() /*-{
            return this["projectId"];
        }-*/;

        public final native ChatParticipantRemoveImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        @Override
        public final native java.lang.String userId() /*-{
            return this["userId"];
        }-*/;

        public final native ChatParticipantRemoveImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native ChatParticipantRemoveImpl make() /*-{
            return {
                _type: 11
            };
        }-*/;
    }


    public static class DisableEnableCollaborationDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.DisableEnableCollaborationDto {
        protected DisableEnableCollaborationDtoImpl() {
        }

        @Override
        public final native java.lang.String clientId() /*-{
            return this["clientId"];
        }-*/;

        public final native DisableEnableCollaborationDtoImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native boolean isEnabled() /*-{
            return this["isEnabled"];
        }-*/;

        public final native DisableEnableCollaborationDtoImpl setIsEnabled(boolean isEnabled) /*-{
            this["isEnabled"] = isEnabled;
            return this;
        }-*/;

        public final native boolean hasIsEnabled() /*-{
            return this.hasOwnProperty("isEnabled");
        }-*/;

        @Override
        public final native java.lang.String projectId() /*-{
            return this["projectId"];
        }-*/;

        public final native DisableEnableCollaborationDtoImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native DisableEnableCollaborationDtoImpl make() /*-{
            return {
                _type: 13
            };
        }-*/;
    }


    public static class ItemImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.Item {
        protected ItemImpl() {
        }

        @Override
        public final native java.lang.String getId() /*-{
            return this[0];
        }-*/;

        public final native ItemImpl setId(java.lang.String id) /*-{
            this[0] = id;
            return this;
        }-*/;

        public final native boolean hasId() /*-{
            return this.hasOwnProperty(0);
        }-*/;

        @Override
        public final native java.lang.String getName() /*-{
            return this[1];
        }-*/;

        public final native ItemImpl setName(java.lang.String name) /*-{
            this[1] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty(1);
        }-*/;

        @Override
        public final native com.codenvy.ide.collaboration.dto.Item.ItemType getItemType() /*-{
            return @com.codenvy.ide.collaboration.dto.Item.ItemType::valueOf(Ljava/lang/String;)(this[2]);
        }-*/;

        public final native ItemImpl setItemType(com.codenvy.ide.collaboration.dto.Item.ItemType itemType) /*-{
            itemType = itemType.@com.codenvy.ide.collaboration.dto.Item.ItemType::toString()();
            this[2] = itemType;
            return this;
        }-*/;

        public final native boolean hasItemType() /*-{
            return this.hasOwnProperty(2);
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this[3];
        }-*/;

        public final native ItemImpl setPath(java.lang.String path) /*-{
            this[3] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty(3);
        }-*/;

        @Override
        public final native java.lang.String getParentId() /*-{
            return this[4];
        }-*/;

        public final native ItemImpl setParentId(java.lang.String parentId) /*-{
            this[4] = parentId;
            return this;
        }-*/;

        public final native boolean hasParentId() /*-{
            return this.hasOwnProperty(4);
        }-*/;

        @Override
        public final native java.lang.String getMimeType() /*-{
            return this[5];
        }-*/;

        public final native ItemImpl setMimeType(java.lang.String mimeType) /*-{
            this[5] = mimeType;
            return this;
        }-*/;

        public final native boolean hasMimeType() /*-{
            return this.hasOwnProperty(5);
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.codenvy.ide.collaboration.dto.Property> getProperties() /*-{
            return this[6];
        }-*/;

        public final native ItemImpl setProperties(
                com.codenvy.ide.json.shared.JsonArray<com.codenvy.ide.collaboration.dto.Property> properties) /*-{
            this[6] = properties;
            return this;
        }-*/;

        public final native boolean hasProperties() /*-{
            return this.hasOwnProperty(6);
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonStringMap<com.codenvy.ide.collaboration.dto.Link> getLinks() /*-{
            return this[7];
        }-*/;

        public final native ItemImpl setLinks(com.codenvy.ide.json.shared.JsonStringMap<com.codenvy.ide.collaboration.dto.Link> links) /*-{
            this[7] = links;
            return this;
        }-*/;

        public final native boolean hasLinks() /*-{
            return this.hasOwnProperty(7);
        }-*/;

        public static native ItemImpl make() /*-{
            return [];
        }-*/;
    }


    public static class ItemCreatedDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ItemCreatedDto {
        protected ItemCreatedDtoImpl() {
        }

        @Override
        public final native com.codenvy.ide.collaboration.dto.Item getItem() /*-{
            return this["item"];
        }-*/;

        public final native ItemCreatedDtoImpl setItem(com.codenvy.ide.collaboration.dto.Item item) /*-{
            this["item"] = item;
            return this;
        }-*/;

        public final native boolean hasItem() /*-{
            return this.hasOwnProperty("item");
        }-*/;

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native ItemCreatedDtoImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native ItemCreatedDtoImpl make() /*-{
            return {
                _type: 4
            };
        }-*/;
    }


    public static class ItemDeletedDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ItemDeletedDto {
        protected ItemDeletedDtoImpl() {
        }

        @Override
        public final native java.lang.String getFilePath() /*-{
            return this["filePath"];
        }-*/;

        public final native ItemDeletedDtoImpl setFilePath(java.lang.String filePath) /*-{
            this["filePath"] = filePath;
            return this;
        }-*/;

        public final native boolean hasFilePath() /*-{
            return this.hasOwnProperty("filePath");
        }-*/;

        @Override
        public final native java.lang.String getFileId() /*-{
            return this["fileId"];
        }-*/;

        public final native ItemDeletedDtoImpl setFileId(java.lang.String fileId) /*-{
            this["fileId"] = fileId;
            return this;
        }-*/;

        public final native boolean hasFileId() /*-{
            return this.hasOwnProperty("fileId");
        }-*/;

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native ItemDeletedDtoImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native ItemDeletedDtoImpl make() /*-{
            return {
                _type: 3
            };
        }-*/;
    }


    public static class ItemMovedDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ItemMovedDto {
        protected ItemMovedDtoImpl() {
        }

        @Override
        public final native java.lang.String oldPath() /*-{
            return this["oldPath"];
        }-*/;

        public final native ItemMovedDtoImpl setOldPath(java.lang.String oldPath) /*-{
            this["oldPath"] = oldPath;
            return this;
        }-*/;

        public final native boolean hasOldPath() /*-{
            return this.hasOwnProperty("oldPath");
        }-*/;

        @Override
        public final native com.codenvy.ide.collaboration.dto.Item movedItem() /*-{
            return this["movedItem"];
        }-*/;

        public final native ItemMovedDtoImpl setMovedItem(com.codenvy.ide.collaboration.dto.Item movedItem) /*-{
            this["movedItem"] = movedItem;
            return this;
        }-*/;

        public final native boolean hasMovedItem() /*-{
            return this.hasOwnProperty("movedItem");
        }-*/;

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native ItemMovedDtoImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native ItemMovedDtoImpl make() /*-{
            return {
                _type: 5
            };
        }-*/;
    }


    public static class ItemRenamedDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ItemRenamedDto {
        protected ItemRenamedDtoImpl() {
        }

        @Override
        public final native com.codenvy.ide.collaboration.dto.Item renamedItem() /*-{
            return this["renamedItem"];
        }-*/;

        public final native ItemRenamedDtoImpl setRenamedItem(com.codenvy.ide.collaboration.dto.Item renamedItem) /*-{
            this["renamedItem"] = renamedItem;
            return this;
        }-*/;

        public final native boolean hasRenamedItem() /*-{
            return this.hasOwnProperty("renamedItem");
        }-*/;

        @Override
        public final native java.lang.String oldPath() /*-{
            return this["oldPath"];
        }-*/;

        public final native ItemRenamedDtoImpl setOldPath(java.lang.String oldPath) /*-{
            this["oldPath"] = oldPath;
            return this;
        }-*/;

        public final native boolean hasOldPath() /*-{
            return this.hasOwnProperty("oldPath");
        }-*/;

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native ItemRenamedDtoImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native ItemRenamedDtoImpl make() /*-{
            return {
                _type: 6
            };
        }-*/;
    }


    public static class LinkImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.Link {
        protected LinkImpl() {
        }

        @Override
        public final native java.lang.String getHref() /*-{
            return this[0];
        }-*/;

        public final native LinkImpl setHref(java.lang.String href) /*-{
            this[0] = href;
            return this;
        }-*/;

        public final native boolean hasHref() /*-{
            return this.hasOwnProperty(0);
        }-*/;

        @Override
        public final native java.lang.String getRel() /*-{
            return this[1];
        }-*/;

        public final native LinkImpl setRel(java.lang.String rel) /*-{
            this[1] = rel;
            return this;
        }-*/;

        public final native boolean hasRel() /*-{
            return this.hasOwnProperty(1);
        }-*/;

        @Override
        public final native java.lang.String getTypeLink() /*-{
            return this[2];
        }-*/;

        public final native LinkImpl setTypeLink(java.lang.String typeLink) /*-{
            this[2] = typeLink;
            return this;
        }-*/;

        public final native boolean hasTypeLink() /*-{
            return this.hasOwnProperty(2);
        }-*/;

        public static native LinkImpl make() /*-{
            return [];
        }-*/;
    }


    public static class ParticipantInfoImpl extends com.codenvy.ide.json.client.Jso
            implements com.codenvy.ide.collaboration.dto.ParticipantInfo {
        protected ParticipantInfoImpl() {
        }

        @Override
        public final native com.codenvy.ide.collaboration.dto.UserDetails getUserDetails() /*-{
            return this["userDetails"];
        }-*/;

        public final native ParticipantInfoImpl setUserDetails(com.codenvy.ide.collaboration.dto.UserDetails userDetails) /*-{
            this["userDetails"] = userDetails;
            return this;
        }-*/;

        public final native boolean hasUserDetails() /*-{
            return this.hasOwnProperty("userDetails");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native ParticipantInfoImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        public static native ParticipantInfoImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ProjectClosedDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ProjectClosedDto {
        protected ProjectClosedDtoImpl() {
        }

        @Override
        public final native java.lang.String projectPath() /*-{
            return this["projectPath"];
        }-*/;

        public final native ProjectClosedDtoImpl setProjectPath(java.lang.String projectPath) /*-{
            this["projectPath"] = projectPath;
            return this;
        }-*/;

        public final native boolean hasProjectPath() /*-{
            return this.hasOwnProperty("projectPath");
        }-*/;

        @Override
        public final native java.lang.String vfsId() /*-{
            return this["vfsId"];
        }-*/;

        public final native ProjectClosedDtoImpl setVfsId(java.lang.String vfsId) /*-{
            this["vfsId"] = vfsId;
            return this;
        }-*/;

        public final native boolean hasVfsId() /*-{
            return this.hasOwnProperty("vfsId");
        }-*/;

        @Override
        public final native java.lang.String projectId() /*-{
            return this["projectId"];
        }-*/;

        public final native ProjectClosedDtoImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native ProjectClosedDtoImpl make() /*-{
            return {
                _type: 2
            };
        }-*/;
    }


    public static class ProjectOpenedDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ProjectOpenedDto {
        protected ProjectOpenedDtoImpl() {
        }

        @Override
        public final native java.lang.String projectPath() /*-{
            return this["projectPath"];
        }-*/;

        public final native ProjectOpenedDtoImpl setProjectPath(java.lang.String projectPath) /*-{
            this["projectPath"] = projectPath;
            return this;
        }-*/;

        public final native boolean hasProjectPath() /*-{
            return this.hasOwnProperty("projectPath");
        }-*/;

        @Override
        public final native java.lang.String vfsId() /*-{
            return this["vfsId"];
        }-*/;

        public final native ProjectOpenedDtoImpl setVfsId(java.lang.String vfsId) /*-{
            this["vfsId"] = vfsId;
            return this;
        }-*/;

        public final native boolean hasVfsId() /*-{
            return this.hasOwnProperty("vfsId");
        }-*/;

        @Override
        public final native java.lang.String projectId() /*-{
            return this["projectId"];
        }-*/;

        public final native ProjectOpenedDtoImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native ProjectOpenedDtoImpl make() /*-{
            return {
                _type: 1
            };
        }-*/;
    }


    public static class ProjectOpenedResponseDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ProjectOpenedResponseDto {
        protected ProjectOpenedResponseDtoImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.codenvy.ide.collaboration.dto.ParticipantInfo> projectParticipants
                () /*-{
            return this["projectParticipants"];
        }-*/;

        public final native ProjectOpenedResponseDtoImpl setProjectParticipants(
                com.codenvy.ide.json.shared.JsonArray<com.codenvy.ide.collaboration.dto.ParticipantInfo> projectParticipants) /*-{
            this["projectParticipants"] = projectParticipants;
            return this;
        }-*/;

        public final native boolean hasProjectParticipants() /*-{
            return this.hasOwnProperty("projectParticipants");
        }-*/;

        public static native ProjectOpenedResponseDtoImpl make() /*-{
            return {
                _type: 9
            };
        }-*/;
    }


    public static class ProjectOperationNotificationImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.ProjectOperationNotification {
        protected ProjectOperationNotificationImpl() {
        }

        @Override
        public final native java.lang.String clientId() /*-{
            return this["clientId"];
        }-*/;

        public final native ProjectOperationNotificationImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String message() /*-{
            return this["message"];
        }-*/;

        public final native ProjectOperationNotificationImpl setMessage(java.lang.String message) /*-{
            this["message"] = message;
            return this;
        }-*/;

        public final native boolean hasMessage() /*-{
            return this.hasOwnProperty("message");
        }-*/;

        @Override
        public final native java.lang.String projectId() /*-{
            return this["projectId"];
        }-*/;

        public final native ProjectOperationNotificationImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native ProjectOperationNotificationImpl make() /*-{
            return {
                _type: 12
            };
        }-*/;
    }


    public static class PropertyImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.codenvy.ide.collaboration.dto.Property {
        protected PropertyImpl() {
        }

        @Override
        public final native java.lang.String getName() /*-{
            return this[0];
        }-*/;

        public final native PropertyImpl setName(java.lang.String name) /*-{
            this[0] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty(0);
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getValue() /*-{
            if (!this.hasOwnProperty(1)) {
                this[1] = [];
            }
            return this[1];
        }-*/;

        public final native PropertyImpl setValue(com.codenvy.ide.json.shared.JsonArray<java.lang.String> value) /*-{
            this[1] = value;
            return this;
        }-*/;

        public final native boolean hasValue() /*-{
            return this.hasOwnProperty(1);
        }-*/;

        public static native PropertyImpl make() /*-{
            return [];
        }-*/;
    }


    public static class UserDetailsImpl extends com.codenvy.ide.json.client.Jso implements com.codenvy.ide.collaboration.dto.UserDetails {
        protected UserDetailsImpl() {
        }

        @Override
        public final native java.lang.String getGivenName() /*-{
            return this["givenName"];
        }-*/;

        public final native UserDetailsImpl setGivenName(java.lang.String givenName) /*-{
            this["givenName"] = givenName;
            return this;
        }-*/;

        public final native boolean hasGivenName() /*-{
            return this.hasOwnProperty("givenName");
        }-*/;

        @Override
        public final native java.lang.String getDisplayEmail() /*-{
            return this["displayEmail"];
        }-*/;

        public final native UserDetailsImpl setDisplayEmail(java.lang.String displayEmail) /*-{
            this["displayEmail"] = displayEmail;
            return this;
        }-*/;

        public final native boolean hasDisplayEmail() /*-{
            return this.hasOwnProperty("displayEmail");
        }-*/;

        @Override
        public final native java.lang.String getPortraitUrl() /*-{
            return this["portraitUrl"];
        }-*/;

        public final native UserDetailsImpl setPortraitUrl(java.lang.String portraitUrl) /*-{
            this["portraitUrl"] = portraitUrl;
            return this;
        }-*/;

        public final native boolean hasPortraitUrl() /*-{
            return this.hasOwnProperty("portraitUrl");
        }-*/;

        @Override
        public final native boolean isCurrentUser() /*-{
            return this["isCurrentUser"];
        }-*/;

        public final native UserDetailsImpl setIsCurrentUser(boolean isCurrentUser) /*-{
            this["isCurrentUser"] = isCurrentUser;
            return this;
        }-*/;

        public final native boolean hasIsCurrentUser() /*-{
            return this.hasOwnProperty("isCurrentUser");
        }-*/;

        @Override
        public final native java.lang.String getDisplayName() /*-{
            return this["displayName"];
        }-*/;

        public final native UserDetailsImpl setDisplayName(java.lang.String displayName) /*-{
            this["displayName"] = displayName;
            return this;
        }-*/;

        public final native boolean hasDisplayName() /*-{
            return this.hasOwnProperty("displayName");
        }-*/;

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native UserDetailsImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native UserDetailsImpl make() /*-{
            return {

            };
        }-*/;
    }

}