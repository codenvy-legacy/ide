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
package com.google.collide.dto.client;


@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

    private DtoClientImpls() {
    }

    public static final String CLIENT_SERVER_PROTOCOL_HASH = "ad523a132045ee5b6786420d3096450bacef6195";


    public static class AddMembersResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.AddMembersResponse {
        protected AddMembersResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> getNewMembers() /*-{
            return this["newMembers"];
        }-*/;

        public final native AddMembersResponseImpl setNewMembers(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> newMembers) /*-{
            this["newMembers"] = newMembers;
            return this;
        }-*/;

        public final native boolean hasNewMembers() /*-{
            return this.hasOwnProperty("newMembers");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getInvalidEmails() /*-{
            return this["invalidEmails"];
        }-*/;

        public final native AddMembersResponseImpl setInvalidEmails(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> invalidEmails) /*-{
            this["invalidEmails"] = invalidEmails;
            return this;
        }-*/;

        public final native boolean hasInvalidEmails() /*-{
            return this.hasOwnProperty("invalidEmails");
        }-*/;

        public static native AddMembersResponseImpl make() /*-{
            return {
                _type: 1
            };
        }-*/;
    }


    public static class AddProjectMembersImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.AddProjectMembers {
        protected AddProjectMembersImpl() {
        }

        @Override
        public final native java.lang.String getUserEmails() /*-{
            return this["userEmails"];
        }-*/;

        public final native AddProjectMembersImpl setUserEmails(java.lang.String userEmails) /*-{
            this["userEmails"] = userEmails;
            return this;
        }-*/;

        public final native boolean hasUserEmails() /*-{
            return this.hasOwnProperty("userEmails");
        }-*/;

        @Override
        public final native com.google.collide.dto.ChangeRoleInfo getChangeRoleInfo() /*-{
            return this["changeRoleInfo"];
        }-*/;

        public final native AddProjectMembersImpl setChangeRoleInfo(com.google.collide.dto.ChangeRoleInfo changeRoleInfo) /*-{
            this["changeRoleInfo"] = changeRoleInfo;
            return this;
        }-*/;

        public final native boolean hasChangeRoleInfo() /*-{
            return this.hasOwnProperty("changeRoleInfo");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native AddProjectMembersImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native AddProjectMembersImpl make() /*-{
            return {
                _type: 2
            };
        }-*/;
    }


    public static class AddWorkspaceMembersImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.AddWorkspaceMembers {
        protected AddWorkspaceMembersImpl() {
        }

        @Override
        public final native java.lang.String getUserEmails() /*-{
            return this["userEmails"];
        }-*/;

        public final native AddWorkspaceMembersImpl setUserEmails(java.lang.String userEmails) /*-{
            this["userEmails"] = userEmails;
            return this;
        }-*/;

        public final native boolean hasUserEmails() /*-{
            return this.hasOwnProperty("userEmails");
        }-*/;

        @Override
        public final native com.google.collide.dto.ChangeRoleInfo getChangeRoleInfo() /*-{
            return this["changeRoleInfo"];
        }-*/;

        public final native AddWorkspaceMembersImpl setChangeRoleInfo(com.google.collide.dto.ChangeRoleInfo changeRoleInfo) /*-{
            this["changeRoleInfo"] = changeRoleInfo;
            return this;
        }-*/;

        public final native boolean hasChangeRoleInfo() /*-{
            return this.hasOwnProperty("changeRoleInfo");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native AddWorkspaceMembersImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native AddWorkspaceMembersImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native AddWorkspaceMembersImpl make() /*-{
            return {
                _type: 3
            };
        }-*/;
    }


    public static class BeginUploadSessionImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.BeginUploadSession {
        protected BeginUploadSessionImpl() {
        }

        @Override
        public final native java.lang.String getSessionId() /*-{
            return this["sessionId"];
        }-*/;

        public final native BeginUploadSessionImpl setSessionId(java.lang.String sessionId) /*-{
            this["sessionId"] = sessionId;
            return this;
        }-*/;

        public final native boolean hasSessionId() /*-{
            return this.hasOwnProperty("sessionId");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native BeginUploadSessionImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getWorkspaceDirsToCreate() /*-{
            return this["workspaceDirsToCreate"];
        }-*/;

        public final native BeginUploadSessionImpl setWorkspaceDirsToCreate(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> workspaceDirsToCreate) /*-{
            this["workspaceDirsToCreate"] = workspaceDirsToCreate;
            return this;
        }-*/;

        public final native boolean hasWorkspaceDirsToCreate() /*-{
            return this.hasOwnProperty("workspaceDirsToCreate");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native BeginUploadSessionImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getWorkspacePathsToReplace() /*-{
            return this["workspacePathsToReplace"];
        }-*/;

        public final native BeginUploadSessionImpl setWorkspacePathsToReplace(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> workspacePathsToReplace) /*-{
            this["workspacePathsToReplace"] = workspacePathsToReplace;
            return this;
        }-*/;

        public final native boolean hasWorkspacePathsToReplace() /*-{
            return this.hasOwnProperty("workspacePathsToReplace");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getWorkspacePathsToUnzip() /*-{
            return this["workspacePathsToUnzip"];
        }-*/;

        public final native BeginUploadSessionImpl setWorkspacePathsToUnzip(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> workspacePathsToUnzip) /*-{
            this["workspacePathsToUnzip"] = workspacePathsToUnzip;
            return this;
        }-*/;

        public final native boolean hasWorkspacePathsToUnzip() /*-{
            return this.hasOwnProperty("workspacePathsToUnzip");
        }-*/;

        public static native BeginUploadSessionImpl make() /*-{
            return {
                _type: 4
            };
        }-*/;
    }


    public static class ChangeRoleInfoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ChangeRoleInfo {
        protected ChangeRoleInfoImpl() {
        }

        @Override
        public final native boolean emailUsers() /*-{
            return this["emailUsers"];
        }-*/;

        public final native ChangeRoleInfoImpl setEmailUsers(boolean emailUsers) /*-{
            this["emailUsers"] = emailUsers;
            return this;
        }-*/;

        public final native boolean hasEmailUsers() /*-{
            return this.hasOwnProperty("emailUsers");
        }-*/;

        @Override
        public final native boolean emailSelf() /*-{
            return this["emailSelf"];
        }-*/;

        public final native ChangeRoleInfoImpl setEmailSelf(boolean emailSelf) /*-{
            this["emailSelf"] = emailSelf;
            return this;
        }-*/;

        public final native boolean hasEmailSelf() /*-{
            return this.hasOwnProperty("emailSelf");
        }-*/;

        @Override
        public final native java.lang.String getEmailMessage() /*-{
            return this["emailMessage"];
        }-*/;

        public final native ChangeRoleInfoImpl setEmailMessage(java.lang.String emailMessage) /*-{
            this["emailMessage"] = emailMessage;
            return this;
        }-*/;

        public final native boolean hasEmailMessage() /*-{
            return this.hasOwnProperty("emailMessage");
        }-*/;

        @Override
        public final native com.google.collide.dto.Role getRole() /*-{
            return @com.google.collide.dto.Role::valueOf(Ljava/lang/String;)(this["role"]);
        }-*/;

        public final native ChangeRoleInfoImpl setRole(com.google.collide.dto.Role role) /*-{
            role = role.@com.google.collide.dto.Role::toString()();
            this["role"] = role;
            return this;
        }-*/;

        public final native boolean hasRole() /*-{
            return this.hasOwnProperty("role");
        }-*/;

        public static native ChangeRoleInfoImpl make() /*-{
            return {
                _type: 5
            };
        }-*/;
    }


    public static class ClientToServerDocOpImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ClientToServerDocOp {
        protected ClientToServerDocOpImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native ClientToServerDocOpImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native ClientToServerDocOpImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native ClientToServerDocOpImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getDocOps2() /*-{
            return this["docOps2"];
        }-*/;

        public final native ClientToServerDocOpImpl setDocOps2(com.codenvy.ide.json.shared.JsonArray<java.lang.String> docOps2) /*-{
            this["docOps2"] = docOps2;
            return this;
        }-*/;

        public final native boolean hasDocOps2() /*-{
            return this.hasOwnProperty("docOps2");
        }-*/;

        @Override
        public final native com.google.collide.dto.DocumentSelection getSelection() /*-{
            return this["selection"];
        }-*/;

        public final native ClientToServerDocOpImpl setSelection(com.google.collide.dto.DocumentSelection selection) /*-{
            this["selection"] = selection;
            return this;
        }-*/;

        public final native boolean hasSelection() /*-{
            return this.hasOwnProperty("selection");
        }-*/;

        @Override
        public final native int getCcRevision() /*-{
            return this["ccRevision"];
        }-*/;

        public final native ClientToServerDocOpImpl setCcRevision(int ccRevision) /*-{
            this["ccRevision"] = ccRevision;
            return this;
        }-*/;

        public final native boolean hasCcRevision() /*-{
            return this.hasOwnProperty("ccRevision");
        }-*/;

        public static native ClientToServerDocOpImpl make() /*-{
            return {
                _type: 6
            };
        }-*/;
    }


    public static class CloseEditorImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CloseEditor {
        protected CloseEditorImpl() {
        }

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native CloseEditorImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native CloseEditorImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        public static native CloseEditorImpl make() /*-{
            return {
                _type: 126
            };
        }-*/;
    }


    public static class CodeBlockImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CodeBlock {
        protected CodeBlockImpl() {
        }

        @Override
        public final native java.lang.String getId() /*-{
            return this[0];
        }-*/;

        public final native CodeBlockImpl setId(java.lang.String id) /*-{
            this[0] = id;
            return this;
        }-*/;

        public final native boolean hasId() /*-{
            return this.hasOwnProperty(0);
        }-*/;

        @Override
        public final native int getBlockType() /*-{
            return this[1];
        }-*/;

        public final native CodeBlockImpl setBlockType(int blockType) /*-{
            this[1] = blockType;
            return this;
        }-*/;

        public final native boolean hasBlockType() /*-{
            return this.hasOwnProperty(1);
        }-*/;

        @Override
        public final native int getEndColumn() /*-{
            return this[2];
        }-*/;

        public final native CodeBlockImpl setEndColumn(int endColumn) /*-{
            this[2] = endColumn;
            return this;
        }-*/;

        public final native boolean hasEndColumn() /*-{
            return this.hasOwnProperty(2);
        }-*/;

        @Override
        public final native int getEndLineNumber() /*-{
            return this[3];
        }-*/;

        public final native CodeBlockImpl setEndLineNumber(int endLineNumber) /*-{
            this[3] = endLineNumber;
            return this;
        }-*/;

        public final native boolean hasEndLineNumber() /*-{
            return this.hasOwnProperty(3);
        }-*/;

        @Override
        public final native java.lang.String getName() /*-{
            return this[4];
        }-*/;

        public final native CodeBlockImpl setName(java.lang.String name) /*-{
            this[4] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty(4);
        }-*/;

        @Override
        public final native int getStartColumn() /*-{
            return this[5];
        }-*/;

        public final native CodeBlockImpl setStartColumn(int startColumn) /*-{
            this[5] = startColumn;
            return this;
        }-*/;

        public final native boolean hasStartColumn() /*-{
            return this.hasOwnProperty(5);
        }-*/;

        @Override
        public final native int getStartLineNumber() /*-{
            return this[6];
        }-*/;

        public final native CodeBlockImpl setStartLineNumber(int startLineNumber) /*-{
            this[6] = startLineNumber;
            return this;
        }-*/;

        public final native boolean hasStartLineNumber() /*-{
            return this.hasOwnProperty(6);
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.CodeBlock> getChildren() /*-{
            if (!this.hasOwnProperty(7)) {
                this[7] = [];
            }
            return this[7];
        }-*/;

        public final native CodeBlockImpl setChildren(com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.CodeBlock> children) /*-{
            this[7] = children;
            return this;
        }-*/;

        public final native boolean hasChildren() /*-{
            return this.hasOwnProperty(7);
        }-*/;

        public static native CodeBlockImpl make() /*-{
            return [];
        }-*/;
    }


    public static class CodeBlockAssociationImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CodeBlockAssociation {
        protected CodeBlockAssociationImpl() {
        }

        @Override
        public final native java.lang.String getSourceFileId() /*-{
            return this[0];
        }-*/;

        public final native CodeBlockAssociationImpl setSourceFileId(java.lang.String sourceFileId) /*-{
            this[0] = sourceFileId;
            return this;
        }-*/;

        public final native boolean hasSourceFileId() /*-{
            return this.hasOwnProperty(0);
        }-*/;

        @Override
        public final native java.lang.String getSourceLocalId() /*-{
            return this[1];
        }-*/;

        public final native CodeBlockAssociationImpl setSourceLocalId(java.lang.String sourceLocalId) /*-{
            this[1] = sourceLocalId;
            return this;
        }-*/;

        public final native boolean hasSourceLocalId() /*-{
            return this.hasOwnProperty(1);
        }-*/;

        @Override
        public final native java.lang.String getTargetFileId() /*-{
            return this[2];
        }-*/;

        public final native CodeBlockAssociationImpl setTargetFileId(java.lang.String targetFileId) /*-{
            this[2] = targetFileId;
            return this;
        }-*/;

        public final native boolean hasTargetFileId() /*-{
            return this.hasOwnProperty(2);
        }-*/;

        @Override
        public final native java.lang.String getTargetLocalId() /*-{
            return this[3];
        }-*/;

        public final native CodeBlockAssociationImpl setTargetLocalId(java.lang.String targetLocalId) /*-{
            this[3] = targetLocalId;
            return this;
        }-*/;

        public final native boolean hasTargetLocalId() /*-{
            return this.hasOwnProperty(3);
        }-*/;

        @Override
        public final native boolean getIsRootAssociation() /*-{
            return this[4];
        }-*/;

        public final native CodeBlockAssociationImpl setIsRootAssociation(boolean isRootAssociation) /*-{
            this[4] = isRootAssociation;
            return this;
        }-*/;

        public final native boolean hasIsRootAssociation() /*-{
            return this.hasOwnProperty(4);
        }-*/;

        public static native CodeBlockAssociationImpl make() /*-{
            return [];
        }-*/;
    }


    public static class CodeErrorImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.CodeError {
        protected CodeErrorImpl() {
        }

        @Override
        public final native com.google.collide.dto.FilePosition getErrorEnd() /*-{
            return this["errorEnd"];
        }-*/;

        public final native CodeErrorImpl setErrorEnd(com.google.collide.dto.FilePosition errorEnd) /*-{
            this["errorEnd"] = errorEnd;
            return this;
        }-*/;

        public final native boolean hasErrorEnd() /*-{
            return this.hasOwnProperty("errorEnd");
        }-*/;

        @Override
        public final native com.google.collide.dto.FilePosition getErrorStart() /*-{
            return this["errorStart"];
        }-*/;

        public final native CodeErrorImpl setErrorStart(com.google.collide.dto.FilePosition errorStart) /*-{
            this["errorStart"] = errorStart;
            return this;
        }-*/;

        public final native boolean hasErrorStart() /*-{
            return this.hasOwnProperty("errorStart");
        }-*/;

        @Override
        public final native java.lang.String getMessage() /*-{
            return this["message"];
        }-*/;

        public final native CodeErrorImpl setMessage(java.lang.String message) /*-{
            this["message"] = message;
            return this;
        }-*/;

        public final native boolean hasMessage() /*-{
            return this.hasOwnProperty("message");
        }-*/;

        @Override
        public final native boolean isError() /*-{
            return this["isError"];
        }-*/;

        public final native CodeErrorImpl setIsError(boolean isError) /*-{
            this["isError"] = isError;
            return this;
        }-*/;

        public final native boolean hasIsError() /*-{
            return this.hasOwnProperty("isError");
        }-*/;

        public static native CodeErrorImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class CodeErrorsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CodeErrors {
        protected CodeErrorsImpl() {
        }

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native CodeErrorsImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.CodeError> getCodeErrors() /*-{
            return this["codeErrors"];
        }-*/;

        public final native CodeErrorsImpl setCodeErrors(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.CodeError> codeErrors) /*-{
            this["codeErrors"] = codeErrors;
            return this;
        }-*/;

        public final native boolean hasCodeErrors() /*-{
            return this.hasOwnProperty("codeErrors");
        }-*/;

        public static native CodeErrorsImpl make() /*-{
            return {
                _type: 9
            };
        }-*/;
    }


    public static class CodeErrorsRequestImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CodeErrorsRequest {
        protected CodeErrorsRequestImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native CodeErrorsRequestImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native CodeErrorsRequestImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        public static native CodeErrorsRequestImpl make() /*-{
            return {
                _type: 10
            };
        }-*/;
    }


    public static class CodeGraphImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.CodeGraph {
        protected CodeGraphImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonStringMap<com.google.collide.dto.CodeBlock> getCodeBlockMap() /*-{
            return this["codeBlockMap"];
        }-*/;

        public final native CodeGraphImpl setCodeBlockMap(
                com.codenvy.ide.json.shared.JsonStringMap<com.google.collide.dto.CodeBlock> codeBlockMap) /*-{
            this["codeBlockMap"] = codeBlockMap;
            return this;
        }-*/;

        public final native boolean hasCodeBlockMap() /*-{
            return this.hasOwnProperty("codeBlockMap");
        }-*/;

        @Override
        public final native com.google.collide.dto.CodeBlock getDefaultPackage() /*-{
            return this["defaultPackage"];
        }-*/;

        public final native CodeGraphImpl setDefaultPackage(com.google.collide.dto.CodeBlock defaultPackage) /*-{
            this["defaultPackage"] = defaultPackage;
            return this;
        }-*/;

        public final native boolean hasDefaultPackage() /*-{
            return this.hasOwnProperty("defaultPackage");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.InheritanceAssociation>
        getInheritanceAssociations() /*-{
            return this["inheritanceAssociations"];
        }-*/;

        public final native CodeGraphImpl setInheritanceAssociations(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.InheritanceAssociation> inheritanceAssociations) /*-{
            this["inheritanceAssociations"] = inheritanceAssociations;
            return this;
        }-*/;

        public final native boolean hasInheritanceAssociations() /*-{
            return this.hasOwnProperty("inheritanceAssociations");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.TypeAssociation> getTypeAssociations() /*-{
            return this["typeAssociations"];
        }-*/;

        public final native CodeGraphImpl setTypeAssociations(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.TypeAssociation> typeAssociations) /*-{
            this["typeAssociations"] = typeAssociations;
            return this;
        }-*/;

        public final native boolean hasTypeAssociations() /*-{
            return this.hasOwnProperty("typeAssociations");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ImportAssociation> getImportAssociations() /*-{
            return this["importAssociations"];
        }-*/;

        public final native CodeGraphImpl setImportAssociations(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ImportAssociation> importAssociations) /*-{
            this["importAssociations"] = importAssociations;
            return this;
        }-*/;

        public final native boolean hasImportAssociations() /*-{
            return this.hasOwnProperty("importAssociations");
        }-*/;

        public static native CodeGraphImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class CodeGraphFreshnessImpl extends com.codenvy.ide.json.client.Jso
            implements com.google.collide.dto.CodeGraphFreshness {
        protected CodeGraphFreshnessImpl() {
        }

        @Override
        public final native java.lang.String getLibsSubgraph() /*-{
            return this["libsSubgraph"];
        }-*/;

        public final native CodeGraphFreshnessImpl setLibsSubgraph(java.lang.String libsSubgraph) /*-{
            this["libsSubgraph"] = libsSubgraph;
            return this;
        }-*/;

        public final native boolean hasLibsSubgraph() /*-{
            return this.hasOwnProperty("libsSubgraph");
        }-*/;

        @Override
        public final native java.lang.String getFileTree() /*-{
            return this["fileTree"];
        }-*/;

        public final native CodeGraphFreshnessImpl setFileTree(java.lang.String fileTree) /*-{
            this["fileTree"] = fileTree;
            return this;
        }-*/;

        public final native boolean hasFileTree() /*-{
            return this.hasOwnProperty("fileTree");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceLinks() /*-{
            return this["workspaceLinks"];
        }-*/;

        public final native CodeGraphFreshnessImpl setWorkspaceLinks(java.lang.String workspaceLinks) /*-{
            this["workspaceLinks"] = workspaceLinks;
            return this;
        }-*/;

        public final native boolean hasWorkspaceLinks() /*-{
            return this.hasOwnProperty("workspaceLinks");
        }-*/;

        @Override
        public final native java.lang.String getFullGraph() /*-{
            return this["fullGraph"];
        }-*/;

        public final native CodeGraphFreshnessImpl setFullGraph(java.lang.String fullGraph) /*-{
            this["fullGraph"] = fullGraph;
            return this;
        }-*/;

        public final native boolean hasFullGraph() /*-{
            return this.hasOwnProperty("fullGraph");
        }-*/;

        @Override
        public final native java.lang.String getFileReferences() /*-{
            return this["fileReferences"];
        }-*/;

        public final native CodeGraphFreshnessImpl setFileReferences(java.lang.String fileReferences) /*-{
            this["fileReferences"] = fileReferences;
            return this;
        }-*/;

        public final native boolean hasFileReferences() /*-{
            return this.hasOwnProperty("fileReferences");
        }-*/;

        @Override
        public final native java.lang.String getFileTreeHash() /*-{
            return this["fileTreeHash"];
        }-*/;

        public final native CodeGraphFreshnessImpl setFileTreeHash(java.lang.String fileTreeHash) /*-{
            this["fileTreeHash"] = fileTreeHash;
            return this;
        }-*/;

        public final native boolean hasFileTreeHash() /*-{
            return this.hasOwnProperty("fileTreeHash");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceTree() /*-{
            return this["workspaceTree"];
        }-*/;

        public final native CodeGraphFreshnessImpl setWorkspaceTree(java.lang.String workspaceTree) /*-{
            this["workspaceTree"] = workspaceTree;
            return this;
        }-*/;

        public final native boolean hasWorkspaceTree() /*-{
            return this.hasOwnProperty("workspaceTree");
        }-*/;

        public static native CodeGraphFreshnessImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class CodeGraphRequestImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CodeGraphRequest {
        protected CodeGraphRequestImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native CodeGraphRequestImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.google.collide.dto.CodeGraphFreshness getFreshness() /*-{
            return this["freshness"];
        }-*/;

        public final native CodeGraphRequestImpl setFreshness(com.google.collide.dto.CodeGraphFreshness freshness) /*-{
            this["freshness"] = freshness;
            return this;
        }-*/;

        public final native boolean hasFreshness() /*-{
            return this.hasOwnProperty("freshness");
        }-*/;

        @Override
        public final native java.lang.String getFilePath() /*-{
            return this["filePath"];
        }-*/;

        public final native CodeGraphRequestImpl setFilePath(java.lang.String filePath) /*-{
            this["filePath"] = filePath;
            return this;
        }-*/;

        public final native boolean hasFilePath() /*-{
            return this.hasOwnProperty("filePath");
        }-*/;

        public static native CodeGraphRequestImpl make() /*-{
            return {
                _type: 11
            };
        }-*/;
    }


    public static class CodeGraphResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CodeGraphResponse {
        protected CodeGraphResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.CodeGraphFreshness getFreshness() /*-{
            return this["freshness"];
        }-*/;

        public final native CodeGraphResponseImpl setFreshness(com.google.collide.dto.CodeGraphFreshness freshness) /*-{
            this["freshness"] = freshness;
            return this;
        }-*/;

        public final native boolean hasFreshness() /*-{
            return this.hasOwnProperty("freshness");
        }-*/;

        @Override
        public final native java.lang.String getLibsSubgraphJson() /*-{
            return this["libsSubgraphJson"];
        }-*/;

        public final native CodeGraphResponseImpl setLibsSubgraphJson(java.lang.String libsSubgraphJson) /*-{
            this["libsSubgraphJson"] = libsSubgraphJson;
            return this;
        }-*/;

        public final native boolean hasLibsSubgraphJson() /*-{
            return this.hasOwnProperty("libsSubgraphJson");
        }-*/;

        @Override
        public final native java.lang.String getFileTreeJson() /*-{
            return this["fileTreeJson"];
        }-*/;

        public final native CodeGraphResponseImpl setFileTreeJson(java.lang.String fileTreeJson) /*-{
            this["fileTreeJson"] = fileTreeJson;
            return this;
        }-*/;

        public final native boolean hasFileTreeJson() /*-{
            return this.hasOwnProperty("fileTreeJson");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceTreeJson() /*-{
            return this["workspaceTreeJson"];
        }-*/;

        public final native CodeGraphResponseImpl setWorkspaceTreeJson(java.lang.String workspaceTreeJson) /*-{
            this["workspaceTreeJson"] = workspaceTreeJson;
            return this;
        }-*/;

        public final native boolean hasWorkspaceTreeJson() /*-{
            return this.hasOwnProperty("workspaceTreeJson");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceLinksJson() /*-{
            return this["workspaceLinksJson"];
        }-*/;

        public final native CodeGraphResponseImpl setWorkspaceLinksJson(java.lang.String workspaceLinksJson) /*-{
            this["workspaceLinksJson"] = workspaceLinksJson;
            return this;
        }-*/;

        public final native boolean hasWorkspaceLinksJson() /*-{
            return this.hasOwnProperty("workspaceLinksJson");
        }-*/;

        @Override
        public final native java.lang.String getFullGraphJson() /*-{
            return this["fullGraphJson"];
        }-*/;

        public final native CodeGraphResponseImpl setFullGraphJson(java.lang.String fullGraphJson) /*-{
            this["fullGraphJson"] = fullGraphJson;
            return this;
        }-*/;

        public final native boolean hasFullGraphJson() /*-{
            return this.hasOwnProperty("fullGraphJson");
        }-*/;

        @Override
        public final native java.lang.String getFileReferencesJson() /*-{
            return this["fileReferencesJson"];
        }-*/;

        public final native CodeGraphResponseImpl setFileReferencesJson(java.lang.String fileReferencesJson) /*-{
            this["fileReferencesJson"] = fileReferencesJson;
            return this;
        }-*/;

        public final native boolean hasFileReferencesJson() /*-{
            return this.hasOwnProperty("fileReferencesJson");
        }-*/;

        public static native CodeGraphResponseImpl make() /*-{
            return {
                _type: 12
            };
        }-*/;
    }


    public static class CodeReferenceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CodeReference {
        protected CodeReferenceImpl() {
        }

        @Override
        public final native com.google.collide.dto.CodeReference.Type getReferenceType() /*-{
            return @com.google.collide.dto.CodeReference.Type::valueOf(Ljava/lang/String;)(this[0]);
        }-*/;

        public final native CodeReferenceImpl setReferenceType(com.google.collide.dto.CodeReference.Type referenceType) /*-{
            referenceType = referenceType.@com.google.collide.dto.CodeReference.Type::toString()();
            this[0] = referenceType;
            return this;
        }-*/;

        public final native boolean hasReferenceType() /*-{
            return this.hasOwnProperty(0);
        }-*/;

        @Override
        public final native com.google.collide.dto.FilePosition getReferenceStart() /*-{
            return this[1];
        }-*/;

        public final native CodeReferenceImpl setReferenceStart(com.google.collide.dto.FilePosition referenceStart) /*-{
            this[1] = referenceStart;
            return this;
        }-*/;

        public final native boolean hasReferenceStart() /*-{
            return this.hasOwnProperty(1);
        }-*/;

        @Override
        public final native com.google.collide.dto.FilePosition getReferenceEnd() /*-{
            return this[2];
        }-*/;

        public final native CodeReferenceImpl setReferenceEnd(com.google.collide.dto.FilePosition referenceEnd) /*-{
            this[2] = referenceEnd;
            return this;
        }-*/;

        public final native boolean hasReferenceEnd() /*-{
            return this.hasOwnProperty(2);
        }-*/;

        @Override
        public final native java.lang.String getTargetFilePath() /*-{
            return this[3];
        }-*/;

        public final native CodeReferenceImpl setTargetFilePath(java.lang.String targetFilePath) /*-{
            this[3] = targetFilePath;
            return this;
        }-*/;

        public final native boolean hasTargetFilePath() /*-{
            return this.hasOwnProperty(3);
        }-*/;

        @Override
        public final native com.google.collide.dto.FilePosition getTargetStart() /*-{
            return this[4];
        }-*/;

        public final native CodeReferenceImpl setTargetStart(com.google.collide.dto.FilePosition targetStart) /*-{
            this[4] = targetStart;
            return this;
        }-*/;

        public final native boolean hasTargetStart() /*-{
            return this.hasOwnProperty(4);
        }-*/;

        @Override
        public final native com.google.collide.dto.FilePosition getTargetEnd() /*-{
            return this[5];
        }-*/;

        public final native CodeReferenceImpl setTargetEnd(com.google.collide.dto.FilePosition targetEnd) /*-{
            this[5] = targetEnd;
            return this;
        }-*/;

        public final native boolean hasTargetEnd() /*-{
            return this.hasOwnProperty(5);
        }-*/;

        @Override
        public final native java.lang.String getTargetSnippet() /*-{
            return this[6];
        }-*/;

        public final native CodeReferenceImpl setTargetSnippet(java.lang.String targetSnippet) /*-{
            this[6] = targetSnippet;
            return this;
        }-*/;

        public final native boolean hasTargetSnippet() /*-{
            return this.hasOwnProperty(6);
        }-*/;

        public static native CodeReferenceImpl make() /*-{
            return [];
        }-*/;
    }


    public static class CodeReferencesImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.CodeReferences {
        protected CodeReferencesImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.CodeReference> getReferences() /*-{
            return this["references"];
        }-*/;

        public final native CodeReferencesImpl setReferences(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.CodeReference> references) /*-{
            this["references"] = references;
            return this;
        }-*/;

        public final native boolean hasReferences() /*-{
            return this.hasOwnProperty("references");
        }-*/;

        public static native CodeReferencesImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ConflictChunkImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.ConflictChunk {
        protected ConflictChunkImpl() {
        }

        @Override
        public final native java.lang.String getRemoteText() /*-{
            return this["remoteText"];
        }-*/;

        public final native ConflictChunkImpl setRemoteText(java.lang.String remoteText) /*-{
            this["remoteText"] = remoteText;
            return this;
        }-*/;

        public final native boolean hasRemoteText() /*-{
            return this.hasOwnProperty("remoteText");
        }-*/;

        @Override
        public final native int getEndLineNumber() /*-{
            return this["endLineNumber"];
        }-*/;

        public final native ConflictChunkImpl setEndLineNumber(int endLineNumber) /*-{
            this["endLineNumber"] = endLineNumber;
            return this;
        }-*/;

        public final native boolean hasEndLineNumber() /*-{
            return this.hasOwnProperty("endLineNumber");
        }-*/;

        @Override
        public final native int getStartLineNumber() /*-{
            return this["startLineNumber"];
        }-*/;

        public final native ConflictChunkImpl setStartLineNumber(int startLineNumber) /*-{
            this["startLineNumber"] = startLineNumber;
            return this;
        }-*/;

        public final native boolean hasStartLineNumber() /*-{
            return this.hasOwnProperty("startLineNumber");
        }-*/;

        @Override
        public final native java.lang.String getBaseText() /*-{
            return this["baseText"];
        }-*/;

        public final native ConflictChunkImpl setBaseText(java.lang.String baseText) /*-{
            this["baseText"] = baseText;
            return this;
        }-*/;

        public final native boolean hasBaseText() /*-{
            return this.hasOwnProperty("baseText");
        }-*/;

        @Override
        public final native java.lang.String getLocalText() /*-{
            return this["localText"];
        }-*/;

        public final native ConflictChunkImpl setLocalText(java.lang.String localText) /*-{
            this["localText"] = localText;
            return this;
        }-*/;

        public final native boolean hasLocalText() /*-{
            return this.hasOwnProperty("localText");
        }-*/;

        @Override
        public final native boolean isResolved() /*-{
            return this["isResolved"];
        }-*/;

        public final native ConflictChunkImpl setIsResolved(boolean isResolved) /*-{
            this["isResolved"] = isResolved;
            return this;
        }-*/;

        public final native boolean hasIsResolved() /*-{
            return this.hasOwnProperty("isResolved");
        }-*/;

        public static native ConflictChunkImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ConflictChunkResolvedImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ConflictChunkResolved {
        protected ConflictChunkResolvedImpl() {
        }

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native ConflictChunkResolvedImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native int getConflictChunkIndex() /*-{
            return this["conflictChunkIndex"];
        }-*/;

        public final native ConflictChunkResolvedImpl setConflictChunkIndex(int conflictChunkIndex) /*-{
            this["conflictChunkIndex"] = conflictChunkIndex;
            return this;
        }-*/;

        public final native boolean hasConflictChunkIndex() /*-{
            return this.hasOwnProperty("conflictChunkIndex");
        }-*/;

        @Override
        public final native com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() /*-{
            return this["conflictHandle"];
        }-*/;

        public final native ConflictChunkResolvedImpl setConflictHandle(
                com.google.collide.dto.NodeConflictDto.ConflictHandle conflictHandle) /*-{
            this["conflictHandle"] = conflictHandle;
            return this;
        }-*/;

        public final native boolean hasConflictHandle() /*-{
            return this.hasOwnProperty("conflictHandle");
        }-*/;

        @Override
        public final native boolean isResolved() /*-{
            return this["isResolved"];
        }-*/;

        public final native ConflictChunkResolvedImpl setIsResolved(boolean isResolved) /*-{
            this["isResolved"] = isResolved;
            return this;
        }-*/;

        public final native boolean hasIsResolved() /*-{
            return this.hasOwnProperty("isResolved");
        }-*/;

        public static native ConflictChunkResolvedImpl make() /*-{
            return {
                _type: 14
            };
        }-*/;
    }


    public static class CreateAppEngineAppStatusImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CreateAppEngineAppStatus {
        protected CreateAppEngineAppStatusImpl() {
        }

        @Override
        public final native com.google.collide.dto.CreateAppEngineAppStatus.Status getStatus() /*-{
            return @com.google.collide.dto.CreateAppEngineAppStatus.Status::valueOf(Ljava/lang/String;)(this["status"]);
        }-*/;

        public final native CreateAppEngineAppStatusImpl setStatus(com.google.collide.dto.CreateAppEngineAppStatus.Status status) /*-{
            status = status.@com.google.collide.dto.CreateAppEngineAppStatus.Status::toString()();
            this["status"] = status;
            return this;
        }-*/;

        public final native boolean hasStatus() /*-{
            return this.hasOwnProperty("status");
        }-*/;

        public static native CreateAppEngineAppStatusImpl make() /*-{
            return {
                _type: 15
            };
        }-*/;
    }


    public static class CreateProjectImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CreateProject {
        protected CreateProjectImpl() {
        }

        @Override
        public final native java.lang.String getSummary() /*-{
            return this["summary"];
        }-*/;

        public final native CreateProjectImpl setSummary(java.lang.String summary) /*-{
            this["summary"] = summary;
            return this;
        }-*/;

        public final native boolean hasSummary() /*-{
            return this.hasOwnProperty("summary");
        }-*/;

        @Override
        public final native java.lang.String getName() /*-{
            return this["name"];
        }-*/;

        public final native CreateProjectImpl setName(java.lang.String name) /*-{
            this["name"] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty("name");
        }-*/;

        public static native CreateProjectImpl make() /*-{
            return {
                _type: 16
            };
        }-*/;
    }


    public static class CreateProjectResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CreateProjectResponse {
        protected CreateProjectResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.ProjectInfo getProject() /*-{
            return this["project"];
        }-*/;

        public final native CreateProjectResponseImpl setProject(com.google.collide.dto.ProjectInfo project) /*-{
            this["project"] = project;
            return this;
        }-*/;

        public final native boolean hasProject() /*-{
            return this.hasOwnProperty("project");
        }-*/;

        public static native CreateProjectResponseImpl make() /*-{
            return {
                _type: 17
            };
        }-*/;
    }


    public static class CreateWorkspaceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CreateWorkspace {
        protected CreateWorkspaceImpl() {
        }

        @Override
        public final native java.lang.String getBaseWorkspaceId() /*-{
            return this["baseWorkspaceId"];
        }-*/;

        public final native CreateWorkspaceImpl setBaseWorkspaceId(java.lang.String baseWorkspaceId) /*-{
            this["baseWorkspaceId"] = baseWorkspaceId;
            return this;
        }-*/;

        public final native boolean hasBaseWorkspaceId() /*-{
            return this.hasOwnProperty("baseWorkspaceId");
        }-*/;

        @Override
        public final native java.lang.String getDescription() /*-{
            return this["description"];
        }-*/;

        public final native CreateWorkspaceImpl setDescription(java.lang.String description) /*-{
            this["description"] = description;
            return this;
        }-*/;

        public final native boolean hasDescription() /*-{
            return this.hasOwnProperty("description");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native CreateWorkspaceImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        @Override
        public final native java.lang.String getName() /*-{
            return this["name"];
        }-*/;

        public final native CreateWorkspaceImpl setName(java.lang.String name) /*-{
            this["name"] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty("name");
        }-*/;

        public static native CreateWorkspaceImpl make() /*-{
            return {
                _type: 18
            };
        }-*/;
    }


    public static class CreateWorkspaceResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CreateWorkspaceResponse {
        protected CreateWorkspaceResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.WorkspaceInfo getWorkspace() /*-{
            return this["workspace"];
        }-*/;

        public final native CreateWorkspaceResponseImpl setWorkspace(com.google.collide.dto.WorkspaceInfo workspace) /*-{
            this["workspace"] = workspace;
            return this;
        }-*/;

        public final native boolean hasWorkspace() /*-{
            return this.hasOwnProperty("workspace");
        }-*/;

        public static native CreateWorkspaceResponseImpl make() /*-{
            return {
                _type: 19
            };
        }-*/;
    }


    public static class CubePingImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.CubePing {
        protected CubePingImpl() {
        }

        @Override
        public final native java.lang.String getFullGraphFreshness() /*-{
            return this["fullGraphFreshness"];
        }-*/;

        public final native CubePingImpl setFullGraphFreshness(java.lang.String fullGraphFreshness) /*-{
            this["fullGraphFreshness"] = fullGraphFreshness;
            return this;
        }-*/;

        public final native boolean hasFullGraphFreshness() /*-{
            return this.hasOwnProperty("fullGraphFreshness");
        }-*/;

        public static native CubePingImpl make() /*-{
            return {
                _type: 20
            };
        }-*/;
    }


    public static class DeployWorkspaceImpl extends GetAppEngineClusterTypeImpl implements com.google.collide.dto.DeployWorkspace {
        protected DeployWorkspaceImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native DeployWorkspaceImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String basePath() /*-{
            return this["basePath"];
        }-*/;

        public final native DeployWorkspaceImpl setBasePath(java.lang.String basePath) /*-{
            this["basePath"] = basePath;
            return this;
        }-*/;

        public final native boolean hasBasePath() /*-{
            return this.hasOwnProperty("basePath");
        }-*/;

        @Override
        public final native java.lang.String appId() /*-{
            return this["appId"];
        }-*/;

        public final native DeployWorkspaceImpl setAppId(java.lang.String appId) /*-{
            this["appId"] = appId;
            return this;
        }-*/;

        public final native boolean hasAppId() /*-{
            return this.hasOwnProperty("appId");
        }-*/;

        @Override
        public final native java.lang.String appVersion() /*-{
            return this["appVersion"];
        }-*/;

        public final native DeployWorkspaceImpl setAppVersion(java.lang.String appVersion) /*-{
            this["appVersion"] = appVersion;
            return this;
        }-*/;

        public final native boolean hasAppVersion() /*-{
            return this.hasOwnProperty("appVersion");
        }-*/;

        public static native DeployWorkspaceImpl make() /*-{
            return {
                _type: 21
            };
        }-*/;
    }


    public static class DeployWorkspaceStatusImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.DeployWorkspaceStatus {
        protected DeployWorkspaceStatusImpl() {
        }

        @Override
        public final native java.lang.String getAppUrl() /*-{
            return this["appUrl"];
        }-*/;

        public final native DeployWorkspaceStatusImpl setAppUrl(java.lang.String appUrl) /*-{
            this["appUrl"] = appUrl;
            return this;
        }-*/;

        public final native boolean hasAppUrl() /*-{
            return this.hasOwnProperty("appUrl");
        }-*/;

        @Override
        public final native int getStatus() /*-{
            return this["status"];
        }-*/;

        public final native DeployWorkspaceStatusImpl setStatus(int status) /*-{
            this["status"] = status;
            return this;
        }-*/;

        public final native boolean hasStatus() /*-{
            return this.hasOwnProperty("status");
        }-*/;

        @Override
        public final native java.lang.String getMessage() /*-{
            return this["message"];
        }-*/;

        public final native DeployWorkspaceStatusImpl setMessage(java.lang.String message) /*-{
            this["message"] = message;
            return this;
        }-*/;

        public final native boolean hasMessage() /*-{
            return this.hasOwnProperty("message");
        }-*/;

        public static native DeployWorkspaceStatusImpl make() /*-{
            return {
                _type: 22
            };
        }-*/;
    }


    public static class DiffChunkResponseImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.DiffChunkResponse {
        protected DiffChunkResponseImpl() {
        }

        @Override
        public final native java.lang.String getBeforeData() /*-{
            return this["beforeData"];
        }-*/;

        public final native DiffChunkResponseImpl setBeforeData(java.lang.String beforeData) /*-{
            this["beforeData"] = beforeData;
            return this;
        }-*/;

        public final native boolean hasBeforeData() /*-{
            return this.hasOwnProperty("beforeData");
        }-*/;

        @Override
        public final native com.google.collide.dto.DiffChunkResponse.DiffType getDiffType() /*-{
            return @com.google.collide.dto.DiffChunkResponse.DiffType::valueOf(Ljava/lang/String;)(this["diffType"]);
        }-*/;

        public final native DiffChunkResponseImpl setDiffType(com.google.collide.dto.DiffChunkResponse.DiffType diffType) /*-{
            diffType = diffType.@com.google.collide.dto.DiffChunkResponse.DiffType::toString()();
            this["diffType"] = diffType;
            return this;
        }-*/;

        public final native boolean hasDiffType() /*-{
            return this.hasOwnProperty("diffType");
        }-*/;

        @Override
        public final native java.lang.String getAfterData() /*-{
            return this["afterData"];
        }-*/;

        public final native DiffChunkResponseImpl setAfterData(java.lang.String afterData) /*-{
            this["afterData"] = afterData;
            return this;
        }-*/;

        public final native boolean hasAfterData() /*-{
            return this.hasOwnProperty("afterData");
        }-*/;

        public static native DiffChunkResponseImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class DiffStatsDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.DiffStatsDto {
        protected DiffStatsDtoImpl() {
        }

        @Override
        public final native int getAdded() /*-{
            return this["added"];
        }-*/;

        public final native DiffStatsDtoImpl setAdded(int added) /*-{
            this["added"] = added;
            return this;
        }-*/;

        public final native boolean hasAdded() /*-{
            return this.hasOwnProperty("added");
        }-*/;

        @Override
        public final native int getChanged() /*-{
            return this["changed"];
        }-*/;

        public final native DiffStatsDtoImpl setChanged(int changed) /*-{
            this["changed"] = changed;
            return this;
        }-*/;

        public final native boolean hasChanged() /*-{
            return this.hasOwnProperty("changed");
        }-*/;

        @Override
        public final native int getDeleted() /*-{
            return this["deleted"];
        }-*/;

        public final native DiffStatsDtoImpl setDeleted(int deleted) /*-{
            this["deleted"] = deleted;
            return this;
        }-*/;

        public final native boolean hasDeleted() /*-{
            return this.hasOwnProperty("deleted");
        }-*/;

        @Override
        public final native int getUnchanged() /*-{
            return this["unchanged"];
        }-*/;

        public final native DiffStatsDtoImpl setUnchanged(int unchanged) /*-{
            this["unchanged"] = unchanged;
            return this;
        }-*/;

        public final native boolean hasUnchanged() /*-{
            return this.hasOwnProperty("unchanged");
        }-*/;

        public static native DiffStatsDtoImpl make() /*-{
            return {
                _type: 23
            };
        }-*/;
    }


    public static class DirInfoImpl extends TreeNodeInfoImpl implements com.google.collide.dto.DirInfo {
        protected DirInfoImpl() {
        }

        @Override
        public final native boolean isComplete() /*-{
            return this["isComplete"];
        }-*/;

        public final native DirInfoImpl setIsComplete(boolean isComplete) /*-{
            this["isComplete"] = isComplete;
            return this;
        }-*/;

        public final native boolean hasIsComplete() /*-{
            return this.hasOwnProperty("isComplete");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.DirInfo> getSubDirectories() /*-{
            return this["subDirectories"];
        }-*/;

        public final native DirInfoImpl setSubDirectories(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.DirInfo> subDirectories) /*-{
            this["subDirectories"] = subDirectories;
            return this;
        }-*/;

        public final native boolean hasSubDirectories() /*-{
            return this.hasOwnProperty("subDirectories");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.FileInfo> getFiles() /*-{
            return this["files"];
        }-*/;

        public final native DirInfoImpl setFiles(com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.FileInfo> files) /*-{
            this["files"] = files;
            return this;
        }-*/;

        public final native boolean hasFiles() /*-{
            return this.hasOwnProperty("files");
        }-*/;

        public static native DirInfoImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class DocOpImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.DocOp {
        protected DocOpImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.DocOpComponent> getComponents() /*-{
            return this["components"];
        }-*/;

        public final native DocOpImpl setComponents(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.DocOpComponent> components) /*-{
            this["components"] = components;
            return this;
        }-*/;

        public final native boolean hasComponents() /*-{
            return this.hasOwnProperty("components");
        }-*/;

        public static native DocOpImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class DeleteImpl extends DocOpComponentImpl implements com.google.collide.dto.DocOpComponent.Delete {
        protected DeleteImpl() {
        }

        @Override
        public final native java.lang.String getText() /*-{
            return this["text"];
        }-*/;

        public final native DeleteImpl setText(java.lang.String text) /*-{
            this["text"] = text;
            return this;
        }-*/;

        public final native boolean hasText() /*-{
            return this.hasOwnProperty("text");
        }-*/;

        public static native DeleteImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class InsertImpl extends DocOpComponentImpl implements com.google.collide.dto.DocOpComponent.Insert {
        protected InsertImpl() {
        }

        @Override
        public final native java.lang.String getText() /*-{
            return this["text"];
        }-*/;

        public final native InsertImpl setText(java.lang.String text) /*-{
            this["text"] = text;
            return this;
        }-*/;

        public final native boolean hasText() /*-{
            return this.hasOwnProperty("text");
        }-*/;

        public static native InsertImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class RetainImpl extends DocOpComponentImpl implements com.google.collide.dto.DocOpComponent.Retain {
        protected RetainImpl() {
        }

        @Override
        public final native boolean hasTrailingNewline() /*-{
            return this["hasTrailingNewline"];
        }-*/;

        public final native RetainImpl setHasTrailingNewline(boolean hasTrailingNewline) /*-{
            this["hasTrailingNewline"] = hasTrailingNewline;
            return this;
        }-*/;

        public final native boolean hasHasTrailingNewline() /*-{
            return this.hasOwnProperty("hasTrailingNewline");
        }-*/;

        @Override
        public final native int getCount() /*-{
            return this["count"];
        }-*/;

        public final native RetainImpl setCount(int count) /*-{
            this["count"] = count;
            return this;
        }-*/;

        public final native boolean hasCount() /*-{
            return this.hasOwnProperty("count");
        }-*/;

        public static native RetainImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class RetainLineImpl extends DocOpComponentImpl implements com.google.collide.dto.DocOpComponent.RetainLine {
        protected RetainLineImpl() {
        }

        @Override
        public final native int getLineCount() /*-{
            return this["lineCount"];
        }-*/;

        public final native RetainLineImpl setLineCount(int lineCount) /*-{
            this["lineCount"] = lineCount;
            return this;
        }-*/;

        public final native boolean hasLineCount() /*-{
            return this.hasOwnProperty("lineCount");
        }-*/;

        public static native RetainLineImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class DocOpComponentImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.DocOpComponent {
        protected DocOpComponentImpl() {
        }

        @Override
        public final native int getType() /*-{
            return this["type"];
        }-*/;

        public final native DocOpComponentImpl setType(int type) /*-{
            this["type"] = type;
            return this;
        }-*/;

        public final native boolean hasType() /*-{
            return this.hasOwnProperty("type");
        }-*/;

        public static native DocOpComponentImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class DocumentSelectionImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.DocumentSelection {
        protected DocumentSelectionImpl() {
        }

        @Override
        public final native com.google.collide.dto.FilePosition getCursorPosition() /*-{
            return this["cursorPosition"];
        }-*/;

        public final native DocumentSelectionImpl setCursorPosition(com.google.collide.dto.FilePosition cursorPosition) /*-{
            this["cursorPosition"] = cursorPosition;
            return this;
        }-*/;

        public final native boolean hasCursorPosition() /*-{
            return this.hasOwnProperty("cursorPosition");
        }-*/;

        @Override
        public final native com.google.collide.dto.FilePosition getBasePosition() /*-{
            return this["basePosition"];
        }-*/;

        public final native DocumentSelectionImpl setBasePosition(com.google.collide.dto.FilePosition basePosition) /*-{
            this["basePosition"] = basePosition;
            return this;
        }-*/;

        public final native boolean hasBasePosition() /*-{
            return this.hasOwnProperty("basePosition");
        }-*/;

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native DocumentSelectionImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native DocumentSelectionImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class EmptyMessageImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.EmptyMessage {
        protected EmptyMessageImpl() {
        }

        public static native EmptyMessageImpl make() /*-{
            return {
                _type: 24
            };
        }-*/;
    }


    public static class EndUploadSessionImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.EndUploadSession {
        protected EndUploadSessionImpl() {
        }

        @Override
        public final native java.lang.String getSessionId() /*-{
            return this["sessionId"];
        }-*/;

        public final native EndUploadSessionImpl setSessionId(java.lang.String sessionId) /*-{
            this["sessionId"] = sessionId;
            return this;
        }-*/;

        public final native boolean hasSessionId() /*-{
            return this.hasOwnProperty("sessionId");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native EndUploadSessionImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        public static native EndUploadSessionImpl make() /*-{
            return {
                _type: 26
            };
        }-*/;
    }


    public static class UnzipFailureImpl extends com.codenvy.ide.json.client.Jso
            implements com.google.collide.dto.EndUploadSessionFinished.UnzipFailure {
        protected UnzipFailureImpl() {
        }

        @Override
        public final native java.lang.String getZipWorkspacePath() /*-{
            return this["zipWorkspacePath"];
        }-*/;

        public final native UnzipFailureImpl setZipWorkspacePath(java.lang.String zipWorkspacePath) /*-{
            this["zipWorkspacePath"] = zipWorkspacePath;
            return this;
        }-*/;

        public final native boolean hasZipWorkspacePath() /*-{
            return this.hasOwnProperty("zipWorkspacePath");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getDisplayFailedWorkspacePaths() /*-{
            return this["displayFailedWorkspacePaths"];
        }-*/;

        public final native UnzipFailureImpl setDisplayFailedWorkspacePaths(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> displayFailedWorkspacePaths) /*-{
            this["displayFailedWorkspacePaths"] = displayFailedWorkspacePaths;
            return this;
        }-*/;

        public final native boolean hasDisplayFailedWorkspacePaths() /*-{
            return this.hasOwnProperty("displayFailedWorkspacePaths");
        }-*/;

        public static native UnzipFailureImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class EndUploadSessionFinishedImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.EndUploadSessionFinished {
        protected EndUploadSessionFinishedImpl() {
        }

        @Override
        public final native java.lang.String getSessionId() /*-{
            return this["sessionId"];
        }-*/;

        public final native EndUploadSessionFinishedImpl setSessionId(java.lang.String sessionId) /*-{
            this["sessionId"] = sessionId;
            return this;
        }-*/;

        public final native boolean hasSessionId() /*-{
            return this.hasOwnProperty("sessionId");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getFailedFileWorkspacePaths() /*-{
            return this["failedFileWorkspacePaths"];
        }-*/;

        public final native EndUploadSessionFinishedImpl setFailedFileWorkspacePaths(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> failedFileWorkspacePaths) /*-{
            this["failedFileWorkspacePaths"] = failedFileWorkspacePaths;
            return this;
        }-*/;

        public final native boolean hasFailedFileWorkspacePaths() /*-{
            return this.hasOwnProperty("failedFileWorkspacePaths");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getFailedDirWorkspacePaths() /*-{
            return this["failedDirWorkspacePaths"];
        }-*/;

        public final native EndUploadSessionFinishedImpl setFailedDirWorkspacePaths(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> failedDirWorkspacePaths) /*-{
            this["failedDirWorkspacePaths"] = failedDirWorkspacePaths;
            return this;
        }-*/;

        public final native boolean hasFailedDirWorkspacePaths() /*-{
            return this.hasOwnProperty("failedDirWorkspacePaths");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.EndUploadSessionFinished.UnzipFailure>
        getUnzipFailures() /*-{
            return this["unzipFailures"];
        }-*/;

        public final native EndUploadSessionFinishedImpl setUnzipFailures(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.EndUploadSessionFinished.UnzipFailure> unzipFailures) /*-{
            this["unzipFailures"] = unzipFailures;
            return this;
        }-*/;

        public final native boolean hasUnzipFailures() /*-{
            return this.hasOwnProperty("unzipFailures");
        }-*/;

        public static native EndUploadSessionFinishedImpl make() /*-{
            return {
                _type: 120
            };
        }-*/;
    }


    public static class EnterWorkspaceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.EnterWorkspace {
        protected EnterWorkspaceImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native EnterWorkspaceImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native EnterWorkspaceImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native EnterWorkspaceImpl make() /*-{
            return {
                _type: 27
            };
        }-*/;
    }


    public static class EnterWorkspaceResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.EnterWorkspaceResponse {
        protected EnterWorkspaceResponseImpl() {
        }

        @Override
        public final native java.lang.String getParticipantsNextVersion() /*-{
            return this["participantsNextVersion"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setParticipantsNextVersion(java.lang.String participantsNextVersion) /*-{
            this["participantsNextVersion"] = participantsNextVersion;
            return this;
        }-*/;

        public final native boolean hasParticipantsNextVersion() /*-{
            return this.hasOwnProperty("participantsNextVersion");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.google.collide.dto.GetDirectoryResponse getFileTree() /*-{
            return this["fileTree"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setFileTree(com.google.collide.dto.GetDirectoryResponse fileTree) /*-{
            this["fileTree"] = fileTree;
            return this;
        }-*/;

        public final native boolean hasFileTree() /*-{
            return this.hasOwnProperty("fileTree");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> getParticipants() /*-{
            return this["participants"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setParticipants(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> participants) /*-{
            this["participants"] = participants;
            return this;
        }-*/;

        public final native boolean hasParticipants() /*-{
            return this.hasOwnProperty("participants");
        }-*/;

        @Override
        public final native com.google.collide.dto.GetSyncStateResponse.SyncState getSyncState() /*-{
            return @com.google.collide.dto.GetSyncStateResponse.SyncState::valueOf(Ljava/lang/String;)(this["syncState"]);
        }-*/;

        public final native EnterWorkspaceResponseImpl setSyncState(com.google.collide.dto.GetSyncStateResponse.SyncState syncState) /*-{
            syncState = syncState.@com.google.collide.dto.GetSyncStateResponse.SyncState::toString()();
            this["syncState"] = syncState;
            return this;
        }-*/;

        public final native boolean hasSyncState() /*-{
            return this.hasOwnProperty("syncState");
        }-*/;

        @Override
        public final native com.google.collide.dto.GetWorkspaceMetaDataResponse getUserWorkspaceMetadata() /*-{
            return this["userWorkspaceMetadata"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setUserWorkspaceMetadata(
                com.google.collide.dto.GetWorkspaceMetaDataResponse userWorkspaceMetadata) /*-{
            this["userWorkspaceMetadata"] = userWorkspaceMetadata;
            return this;
        }-*/;

        public final native boolean hasUserWorkspaceMetadata() /*-{
            return this.hasOwnProperty("userWorkspaceMetadata");
        }-*/;

        @Override
        public final native int getKeepAliveTimerIntervalMs() /*-{
            return this["keepAliveTimerIntervalMs"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setKeepAliveTimerIntervalMs(int keepAliveTimerIntervalMs) /*-{
            this["keepAliveTimerIntervalMs"] = keepAliveTimerIntervalMs;
            return this;
        }-*/;

        public final native boolean hasKeepAliveTimerIntervalMs() /*-{
            return this.hasOwnProperty("keepAliveTimerIntervalMs");
        }-*/;

        @Override
        public final native com.google.collide.dto.WorkspaceInfo getWorkspaceInfo() /*-{
            return this["workspaceInfo"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setWorkspaceInfo(com.google.collide.dto.WorkspaceInfo workspaceInfo) /*-{
            this["workspaceInfo"] = workspaceInfo;
            return this;
        }-*/;

        public final native boolean hasWorkspaceInfo() /*-{
            return this.hasOwnProperty("workspaceInfo");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceSessionHost() /*-{
            return this["workspaceSessionHost"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setWorkspaceSessionHost(java.lang.String workspaceSessionHost) /*-{
            this["workspaceSessionHost"] = workspaceSessionHost;
            return this;
        }-*/;

        public final native boolean hasWorkspaceSessionHost() /*-{
            return this.hasOwnProperty("workspaceSessionHost");
        }-*/;

        @Override
        public final native boolean isReadOnly() /*-{
            return this["isReadOnly"];
        }-*/;

        public final native EnterWorkspaceResponseImpl setIsReadOnly(boolean isReadOnly) /*-{
            this["isReadOnly"] = isReadOnly;
            return this;
        }-*/;

        public final native boolean hasIsReadOnly() /*-{
            return this.hasOwnProperty("isReadOnly");
        }-*/;

        public static native EnterWorkspaceResponseImpl make() /*-{
            return {
                _type: 28
            };
        }-*/;
    }


    public static class FileCollaboratorGoneImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.FileCollaboratorGone {
        protected FileCollaboratorGoneImpl() {
        }

        @Override
        public final native com.google.collide.dto.ParticipantUserDetails getParticipant() /*-{
            return this["participant"];
        }-*/;

        public final native FileCollaboratorGoneImpl setParticipant(com.google.collide.dto.ParticipantUserDetails participant) /*-{
            this["participant"] = participant;
            return this;
        }-*/;

        public final native boolean hasParticipant() /*-{
            return this.hasOwnProperty("participant");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native FileCollaboratorGoneImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native FileCollaboratorGoneImpl make() /*-{
            return {
                _type: 127
            };
        }-*/;
    }


    public static class FileContentsImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.FileContents {
        protected FileContentsImpl() {
        }

        @Override
        public final native java.lang.String getMimeType() /*-{
            return this["mimeType"];
        }-*/;

        public final native FileContentsImpl setMimeType(java.lang.String mimeType) /*-{
            this["mimeType"] = mimeType;
            return this;
        }-*/;

        public final native boolean hasMimeType() /*-{
            return this.hasOwnProperty("mimeType");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ConflictChunk> getConflicts() /*-{
            return this["conflicts"];
        }-*/;

        public final native FileContentsImpl setConflicts(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ConflictChunk> conflicts) /*-{
            this["conflicts"] = conflicts;
            return this;
        }-*/;

        public final native boolean hasConflicts() /*-{
            return this.hasOwnProperty("conflicts");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getSelections() /*-{
            return this["selections"];
        }-*/;

        public final native FileContentsImpl setSelections(com.codenvy.ide.json.shared.JsonArray<java.lang.String> selections) /*-{
            this["selections"] = selections;
            return this;
        }-*/;

        public final native boolean hasSelections() /*-{
            return this.hasOwnProperty("selections");
        }-*/;

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native FileContentsImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native int getCcRevision() /*-{
            return this["ccRevision"];
        }-*/;

        public final native FileContentsImpl setCcRevision(int ccRevision) /*-{
            this["ccRevision"] = ccRevision;
            return this;
        }-*/;

        public final native boolean hasCcRevision() /*-{
            return this.hasOwnProperty("ccRevision");
        }-*/;

        @Override
        public final native com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() /*-{
            return this["conflictHandle"];
        }-*/;

        public final native FileContentsImpl setConflictHandle(com.google.collide.dto.NodeConflictDto.ConflictHandle conflictHandle) /*-{
            this["conflictHandle"] = conflictHandle;
            return this;
        }-*/;

        public final native boolean hasConflictHandle() /*-{
            return this.hasOwnProperty("conflictHandle");
        }-*/;

        @Override
        public final native java.lang.String getContents() /*-{
            return this["contents"];
        }-*/;

        public final native FileContentsImpl setContents(java.lang.String contents) /*-{
            this["contents"] = contents;
            return this;
        }-*/;

        public final native boolean hasContents() /*-{
            return this.hasOwnProperty("contents");
        }-*/;

        @Override
        public final native com.google.collide.dto.FileContents.ContentType getContentType() /*-{
            return @com.google.collide.dto.FileContents.ContentType::valueOf(Ljava/lang/String;)(this["contentType"]);
        }-*/;

        public final native FileContentsImpl setContentType(com.google.collide.dto.FileContents.ContentType contentType) /*-{
            contentType = contentType.@com.google.collide.dto.FileContents.ContentType::toString()();
            this["contentType"] = contentType;
            return this;
        }-*/;

        public final native boolean hasContentType() /*-{
            return this.hasOwnProperty("contentType");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native FileContentsImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native FileContentsImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class FileInfoImpl extends TreeNodeInfoImpl implements com.google.collide.dto.FileInfo {
        protected FileInfoImpl() {
        }

        @Override
        public final native java.lang.String getSize() /*-{
            return this["size"];
        }-*/;

        public final native FileInfoImpl setSize(java.lang.String size) /*-{
            this["size"] = size;
            return this;
        }-*/;

        public final native boolean hasSize() /*-{
            return this.hasOwnProperty("size");
        }-*/;

        public static native FileInfoImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class FileOperationNotificationImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.FileOperationNotification {
        protected FileOperationNotificationImpl() {
        }

        @Override
        public final native com.google.collide.dto.FileOperationNotification.Operation getOperation() /*-{
            return @com.google.collide.dto.FileOperationNotification.Operation::valueOf(Ljava/lang/String;)(this["operation"]);
        }-*/;

        public final native FileOperationNotificationImpl setOperation(
                com.google.collide.dto.FileOperationNotification.Operation operation) /*-{
            operation = operation.@com.google.collide.dto.FileOperationNotification.Operation::toString()();
            this["operation"] = operation;
            return this;
        }-*/;

        public final native boolean hasOperation() /*-{
            return this.hasOwnProperty("operation");
        }-*/;

        @Override
        public final native java.lang.String getEditSessionId() /*-{
            return this["editSessionId"];
        }-*/;

        public final native FileOperationNotificationImpl setEditSessionId(java.lang.String editSessionId) /*-{
            this["editSessionId"] = editSessionId;
            return this;
        }-*/;

        public final native boolean hasEditSessionId() /*-{
            return this.hasOwnProperty("editSessionId");
        }-*/;

        @Override
        public final native java.lang.String getFilePath() /*-{
            return this["filePath"];
        }-*/;

        public final native FileOperationNotificationImpl setFilePath(java.lang.String filePath) /*-{
            this["filePath"] = filePath;
            return this;
        }-*/;

        public final native boolean hasFilePath() /*-{
            return this.hasOwnProperty("filePath");
        }-*/;

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native FileOperationNotificationImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        @Override
        public final native java.lang.String getTarget() /*-{
            return this["target"];
        }-*/;

        public final native FileOperationNotificationImpl setTarget(java.lang.String target) /*-{
            this["target"] = target;
            return this;
        }-*/;

        public final native boolean hasTarget() /*-{
            return this.hasOwnProperty("target");
        }-*/;

        public static native FileOperationNotificationImpl make() /*-{
            return {
                _type: 130
            };
        }-*/;
    }


    public static class FilePositionImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.FilePosition {
        protected FilePositionImpl() {
        }

        @Override
        public final native int getLineNumber() /*-{
            return this[0];
        }-*/;

        public final native FilePositionImpl setLineNumber(int lineNumber) /*-{
            this[0] = lineNumber;
            return this;
        }-*/;

        public final native boolean hasLineNumber() /*-{
            return this.hasOwnProperty(0);
        }-*/;

        @Override
        public final native int getColumn() /*-{
            return this[1];
        }-*/;

        public final native FilePositionImpl setColumn(int column) /*-{
            this[1] = column;
            return this;
        }-*/;

        public final native boolean hasColumn() /*-{
            return this.hasOwnProperty(1);
        }-*/;

        public static native FilePositionImpl make() /*-{
            return [];
        }-*/;
    }


    public static class GetAppEngineClusterTypeImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetAppEngineClusterType {
        protected GetAppEngineClusterTypeImpl() {
        }

        @Override
        public final native com.google.collide.dto.GetAppEngineClusterType.Type getClusterType() /*-{
            return @com.google.collide.dto.GetAppEngineClusterType.Type::valueOf(Ljava/lang/String;)(this["clusterType"]);
        }-*/;

        public final native GetAppEngineClusterTypeImpl setClusterType(com.google.collide.dto.GetAppEngineClusterType.Type clusterType) /*-{
            clusterType = clusterType.@com.google.collide.dto.GetAppEngineClusterType.Type::toString()();
            this["clusterType"] = clusterType;
            return this;
        }-*/;

        public final native boolean hasClusterType() /*-{
            return this.hasOwnProperty("clusterType");
        }-*/;

        public static native GetAppEngineClusterTypeImpl make() /*-{
            return {
                _type: 30
            };
        }-*/;
    }


    public static class GetDeployInformationImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetDeployInformation {
        protected GetDeployInformationImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetDeployInformationImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native GetDeployInformationImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        public static native GetDeployInformationImpl make() /*-{
            return {
                _type: 31
            };
        }-*/;
    }


    public static class DeployInformationImpl extends com.codenvy.ide.json.client.Jso
            implements com.google.collide.dto.GetDeployInformationResponse.DeployInformation {
        protected DeployInformationImpl() {
        }

        @Override
        public final native java.lang.String getAppId() /*-{
            return this["appId"];
        }-*/;

        public final native DeployInformationImpl setAppId(java.lang.String appId) /*-{
            this["appId"] = appId;
            return this;
        }-*/;

        public final native boolean hasAppId() /*-{
            return this.hasOwnProperty("appId");
        }-*/;

        @Override
        public final native java.lang.String getAppYamlPath() /*-{
            return this["appYamlPath"];
        }-*/;

        public final native DeployInformationImpl setAppYamlPath(java.lang.String appYamlPath) /*-{
            this["appYamlPath"] = appYamlPath;
            return this;
        }-*/;

        public final native boolean hasAppYamlPath() /*-{
            return this.hasOwnProperty("appYamlPath");
        }-*/;

        @Override
        public final native java.lang.String getVersion() /*-{
            return this["version"];
        }-*/;

        public final native DeployInformationImpl setVersion(java.lang.String version) /*-{
            this["version"] = version;
            return this;
        }-*/;

        public final native boolean hasVersion() /*-{
            return this.hasOwnProperty("version");
        }-*/;

        public static native DeployInformationImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class GetDeployInformationResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetDeployInformationResponse {
        protected GetDeployInformationResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.GetDeployInformationResponse.DeployInformation>
        getDeployInformation() /*-{
            return this["deployInformation"];
        }-*/;

        public final native GetDeployInformationResponseImpl setDeployInformation(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.GetDeployInformationResponse.DeployInformation>
                        deployInformation) /*-{
            this["deployInformation"] = deployInformation;
            return this;
        }-*/;

        public final native boolean hasDeployInformation() /*-{
            return this.hasOwnProperty("deployInformation");
        }-*/;

        public static native GetDeployInformationResponseImpl make() /*-{
            return {
                _type: 32
            };
        }-*/;
    }


    public static class GetDirectoryImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetDirectory {
        protected GetDirectoryImpl() {
        }

        @Override
        public final native java.lang.String rootId() /*-{
            return this["rootId"];
        }-*/;

        public final native GetDirectoryImpl setRootId(java.lang.String rootId) /*-{
            this["rootId"] = rootId;
            return this;
        }-*/;

        public final native boolean hasRootId() /*-{
            return this.hasOwnProperty("rootId");
        }-*/;

        @Override
        public final native int getDepth() /*-{
            return this["depth"];
        }-*/;

        public final native GetDirectoryImpl setDepth(int depth) /*-{
            this["depth"] = depth;
            return this;
        }-*/;

        public final native boolean hasDepth() /*-{
            return this.hasOwnProperty("depth");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native GetDirectoryImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native GetDirectoryImpl make() /*-{
            return {
                _type: 33
            };
        }-*/;
    }


    public static class GetDirectoryResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetDirectoryResponse {
        protected GetDirectoryResponseImpl() {
        }

        @Override
        public final native java.lang.String getRootId() /*-{
            return this["rootId"];
        }-*/;

        public final native GetDirectoryResponseImpl setRootId(java.lang.String rootId) /*-{
            this["rootId"] = rootId;
            return this;
        }-*/;

        public final native boolean hasRootId() /*-{
            return this.hasOwnProperty("rootId");
        }-*/;

        @Override
        public final native com.google.collide.dto.DirInfo getBaseDirectory() /*-{
            return this["baseDirectory"];
        }-*/;

        public final native GetDirectoryResponseImpl setBaseDirectory(com.google.collide.dto.DirInfo baseDirectory) /*-{
            this["baseDirectory"] = baseDirectory;
            return this;
        }-*/;

        public final native boolean hasBaseDirectory() /*-{
            return this.hasOwnProperty("baseDirectory");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native GetDirectoryResponseImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native GetDirectoryResponseImpl make() /*-{
            return {
                _type: 34
            };
        }-*/;
    }


    public static class GetEditSessionCollaboratorsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetEditSessionCollaborators {
        protected GetEditSessionCollaboratorsImpl() {
        }

        @Override
        public final native java.lang.String getEditSessionId() /*-{
            return this["editSessionId"];
        }-*/;

        public final native GetEditSessionCollaboratorsImpl setEditSessionId(java.lang.String editSessionId) /*-{
            this["editSessionId"] = editSessionId;
            return this;
        }-*/;

        public final native boolean hasEditSessionId() /*-{
            return this.hasOwnProperty("editSessionId");
        }-*/;

        public static native GetEditSessionCollaboratorsImpl make() /*-{
            return {
                _type: 123
            };
        }-*/;
    }


    public static class GetEditSessionCollaboratorsResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetEditSessionCollaboratorsResponse {
        protected GetEditSessionCollaboratorsResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> getParticipants() /*-{
            return this["participants"];
        }-*/;

        public final native GetEditSessionCollaboratorsResponseImpl setParticipants(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> participants) /*-{
            this["participants"] = participants;
            return this;
        }-*/;

        public final native boolean hasParticipants() /*-{
            return this.hasOwnProperty("participants");
        }-*/;

        public static native GetEditSessionCollaboratorsResponseImpl make() /*-{
            return {
                _type: 124
            };
        }-*/;
    }


    public static class GetFileContentsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetFileContents {
        protected GetFileContentsImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetFileContentsImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native GetFileContentsImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native GetFileContentsImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native GetFileContentsImpl make() /*-{
            return {
                _type: 35
            };
        }-*/;
    }


    public static class GetFileContentsResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetFileContentsResponse {
        protected GetFileContentsResponseImpl() {
        }

        @Override
        public final native boolean getFileExists() /*-{
            return this["fileExists"];
        }-*/;

        public final native GetFileContentsResponseImpl setFileExists(boolean fileExists) /*-{
            this["fileExists"] = fileExists;
            return this;
        }-*/;

        public final native boolean hasFileExists() /*-{
            return this.hasOwnProperty("fileExists");
        }-*/;

        @Override
        public final native com.google.collide.dto.FileContents getFileContents() /*-{
            return this["fileContents"];
        }-*/;

        public final native GetFileContentsResponseImpl setFileContents(com.google.collide.dto.FileContents fileContents) /*-{
            this["fileContents"] = fileContents;
            return this;
        }-*/;

        public final native boolean hasFileContents() /*-{
            return this.hasOwnProperty("fileContents");
        }-*/;

        public static native GetFileContentsResponseImpl make() /*-{
            return {
                _type: 36
            };
        }-*/;
    }


    public static class GetFileDiffImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetFileDiff {
        protected GetFileDiffImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetFileDiffImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native GetFileDiffImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native com.google.collide.dto.NodeMutationDto.MutationType getChangedType() /*-{
            return @com.google.collide.dto.NodeMutationDto.MutationType::valueOf(Ljava/lang/String;)(this["changedType"]);
        }-*/;

        public final native GetFileDiffImpl setChangedType(com.google.collide.dto.NodeMutationDto.MutationType changedType) /*-{
            changedType = changedType.@com.google.collide.dto.NodeMutationDto.MutationType::toString()();
            this["changedType"] = changedType;
            return this;
        }-*/;

        public final native boolean hasChangedType() /*-{
            return this.hasOwnProperty("changedType");
        }-*/;

        @Override
        public final native java.lang.String getBeforeNodeId() /*-{
            return this["beforeNodeId"];
        }-*/;

        public final native GetFileDiffImpl setBeforeNodeId(java.lang.String beforeNodeId) /*-{
            this["beforeNodeId"] = beforeNodeId;
            return this;
        }-*/;

        public final native boolean hasBeforeNodeId() /*-{
            return this.hasOwnProperty("beforeNodeId");
        }-*/;

        @Override
        public final native java.lang.String getAfterNodeId() /*-{
            return this["afterNodeId"];
        }-*/;

        public final native GetFileDiffImpl setAfterNodeId(java.lang.String afterNodeId) /*-{
            this["afterNodeId"] = afterNodeId;
            return this;
        }-*/;

        public final native boolean hasAfterNodeId() /*-{
            return this.hasOwnProperty("afterNodeId");
        }-*/;

        @Override
        public final native boolean isStatsOnly() /*-{
            return this["isStatsOnly"];
        }-*/;

        public final native GetFileDiffImpl setIsStatsOnly(boolean isStatsOnly) /*-{
            this["isStatsOnly"] = isStatsOnly;
            return this;
        }-*/;

        public final native boolean hasIsStatsOnly() /*-{
            return this.hasOwnProperty("isStatsOnly");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native GetFileDiffImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native GetFileDiffImpl make() /*-{
            return {
                _type: 37
            };
        }-*/;
    }


    public static class GetFileDiffResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetFileDiffResponse {
        protected GetFileDiffResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.DiffChunkResponse> getDiffChunks() /*-{
            return this["diffChunks"];
        }-*/;

        public final native GetFileDiffResponseImpl setDiffChunks(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.DiffChunkResponse> diffChunks) /*-{
            this["diffChunks"] = diffChunks;
            return this;
        }-*/;

        public final native boolean hasDiffChunks() /*-{
            return this.hasOwnProperty("diffChunks");
        }-*/;

        @Override
        public final native com.google.collide.dto.DiffStatsDto getDiffStats() /*-{
            return this["diffStats"];
        }-*/;

        public final native GetFileDiffResponseImpl setDiffStats(com.google.collide.dto.DiffStatsDto diffStats) /*-{
            this["diffStats"] = diffStats;
            return this;
        }-*/;

        public final native boolean hasDiffStats() /*-{
            return this.hasOwnProperty("diffStats");
        }-*/;

        @Override
        public final native java.lang.String getBeforeFilePath() /*-{
            return this["beforeFilePath"];
        }-*/;

        public final native GetFileDiffResponseImpl setBeforeFilePath(java.lang.String beforeFilePath) /*-{
            this["beforeFilePath"] = beforeFilePath;
            return this;
        }-*/;

        public final native boolean hasBeforeFilePath() /*-{
            return this.hasOwnProperty("beforeFilePath");
        }-*/;

        @Override
        public final native java.lang.String getAfterFilePath() /*-{
            return this["afterFilePath"];
        }-*/;

        public final native GetFileDiffResponseImpl setAfterFilePath(java.lang.String afterFilePath) /*-{
            this["afterFilePath"] = afterFilePath;
            return this;
        }-*/;

        public final native boolean hasAfterFilePath() /*-{
            return this.hasOwnProperty("afterFilePath");
        }-*/;

        public static native GetFileDiffResponseImpl make() /*-{
            return {
                _type: 38
            };
        }-*/;
    }


    public static class GetFileRevisionsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetFileRevisions {
        protected GetFileRevisionsImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetFileRevisionsImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native GetFileRevisionsImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String getRootId() /*-{
            return this["rootId"];
        }-*/;

        public final native GetFileRevisionsImpl setRootId(java.lang.String rootId) /*-{
            this["rootId"] = rootId;
            return this;
        }-*/;

        public final native boolean hasRootId() /*-{
            return this.hasOwnProperty("rootId");
        }-*/;

        @Override
        public final native int getNumOfRevisions() /*-{
            return this["numOfRevisions"];
        }-*/;

        public final native GetFileRevisionsImpl setNumOfRevisions(int numOfRevisions) /*-{
            this["numOfRevisions"] = numOfRevisions;
            return this;
        }-*/;

        public final native boolean hasNumOfRevisions() /*-{
            return this.hasOwnProperty("numOfRevisions");
        }-*/;

        @Override
        public final native java.lang.String getMinId() /*-{
            return this["minId"];
        }-*/;

        public final native GetFileRevisionsImpl setMinId(java.lang.String minId) /*-{
            this["minId"] = minId;
            return this;
        }-*/;

        public final native boolean hasMinId() /*-{
            return this.hasOwnProperty("minId");
        }-*/;

        @Override
        public final native boolean getIncludeBranchRevision() /*-{
            return this["includeBranchRevision"];
        }-*/;

        public final native GetFileRevisionsImpl setIncludeBranchRevision(boolean includeBranchRevision) /*-{
            this["includeBranchRevision"] = includeBranchRevision;
            return this;
        }-*/;

        public final native boolean hasIncludeBranchRevision() /*-{
            return this.hasOwnProperty("includeBranchRevision");
        }-*/;

        @Override
        public final native boolean getIncludeMostRecentRevision() /*-{
            return this["includeMostRecentRevision"];
        }-*/;

        public final native GetFileRevisionsImpl setIncludeMostRecentRevision(boolean includeMostRecentRevision) /*-{
            this["includeMostRecentRevision"] = includeMostRecentRevision;
            return this;
        }-*/;

        public final native boolean hasIncludeMostRecentRevision() /*-{
            return this.hasOwnProperty("includeMostRecentRevision");
        }-*/;

        @Override
        public final native java.lang.String getPathRootId() /*-{
            return this["pathRootId"];
        }-*/;

        public final native GetFileRevisionsImpl setPathRootId(java.lang.String pathRootId) /*-{
            this["pathRootId"] = pathRootId;
            return this;
        }-*/;

        public final native boolean hasPathRootId() /*-{
            return this.hasOwnProperty("pathRootId");
        }-*/;

        @Override
        public final native boolean filtering() /*-{
            return this["filtering"];
        }-*/;

        public final native GetFileRevisionsImpl setFiltering(boolean filtering) /*-{
            this["filtering"] = filtering;
            return this;
        }-*/;

        public final native boolean hasFiltering() /*-{
            return this.hasOwnProperty("filtering");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native GetFileRevisionsImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native GetFileRevisionsImpl make() /*-{
            return {
                _type: 39
            };
        }-*/;
    }


    public static class GetFileRevisionsResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetFileRevisionsResponse {
        protected GetFileRevisionsResponseImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetFileRevisionsResponseImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.Revision> getRevisions() /*-{
            return this["revisions"];
        }-*/;

        public final native GetFileRevisionsResponseImpl setRevisions(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.Revision> revisions) /*-{
            this["revisions"] = revisions;
            return this;
        }-*/;

        public final native boolean hasRevisions() /*-{
            return this.hasOwnProperty("revisions");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native GetFileRevisionsResponseImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native GetFileRevisionsResponseImpl make() /*-{
            return {
                _type: 40
            };
        }-*/;
    }


    public static class GetOpenedFilesInWorkspaceResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetOpenedFilesInWorkspaceResponse {
        protected GetOpenedFilesInWorkspaceResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonStringMap<com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto
                .ParticipantUserDetails>> getOpenedFiles() /*-{
            return this["openedFiles"];
        }-*/;

        public final native GetOpenedFilesInWorkspaceResponseImpl setOpenedFiles(
                com.codenvy.ide.json.shared.JsonStringMap<com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto
                        .ParticipantUserDetails>> openedFiles) /*-{
            this["openedFiles"] = openedFiles;
            return this;
        }-*/;

        public final native boolean hasOpenedFiles() /*-{
            return this.hasOwnProperty("openedFiles");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonStringMap<java.lang.String> getFileEditSessions() /*-{
            return this["fileEditSessions"];
        }-*/;

        public final native GetOpenedFilesInWorkspaceResponseImpl setFileEditSessions(
                com.codenvy.ide.json.shared.JsonStringMap<java.lang.String> fileEditSessions) /*-{
            this["fileEditSessions"] = fileEditSessions;
            return this;
        }-*/;

        public final native boolean hasFileEditSessions() /*-{
            return this.hasOwnProperty("fileEditSessions");
        }-*/;

        public static native GetOpenedFilesInWorkspaceResponseImpl make() /*-{
            return {
                _type: 129
            };
        }-*/;
    }


    public static class GetOpenendFilesInWorkspaceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetOpenendFilesInWorkspace {
        protected GetOpenendFilesInWorkspaceImpl() {
        }

        public static native GetOpenendFilesInWorkspaceImpl make() /*-{
            return {
                _type: 128
            };
        }-*/;
    }


    public static class GetOwningProjectImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetOwningProject {
        protected GetOwningProjectImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetOwningProjectImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        public static native GetOwningProjectImpl make() /*-{
            return {
                _type: 43
            };
        }-*/;
    }


    public static class GetOwningProjectResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetOwningProjectResponse {
        protected GetOwningProjectResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.ProjectInfo getOwningProject() /*-{
            return this["owningProject"];
        }-*/;

        public final native GetOwningProjectResponseImpl setOwningProject(com.google.collide.dto.ProjectInfo owningProject) /*-{
            this["owningProject"] = owningProject;
            return this;
        }-*/;

        public final native boolean hasOwningProject() /*-{
            return this.hasOwnProperty("owningProject");
        }-*/;

        @Override
        public final native com.google.collide.dto.ProjectMembersInfo getProjectMembersInfo() /*-{
            return this["projectMembersInfo"];
        }-*/;

        public final native GetOwningProjectResponseImpl setProjectMembersInfo(
                com.google.collide.dto.ProjectMembersInfo projectMembersInfo) /*-{
            this["projectMembersInfo"] = projectMembersInfo;
            return this;
        }-*/;

        public final native boolean hasProjectMembersInfo() /*-{
            return this.hasOwnProperty("projectMembersInfo");
        }-*/;

        @Override
        public final native com.google.collide.dto.WorkspaceInfo getWorkspace() /*-{
            return this["workspace"];
        }-*/;

        public final native GetOwningProjectResponseImpl setWorkspace(com.google.collide.dto.WorkspaceInfo workspace) /*-{
            this["workspace"] = workspace;
            return this;
        }-*/;

        public final native boolean hasWorkspace() /*-{
            return this.hasOwnProperty("workspace");
        }-*/;

        public static native GetOwningProjectResponseImpl make() /*-{
            return {
                _type: 44
            };
        }-*/;
    }


    public static class GetProjectByIdImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetProjectById {
        protected GetProjectByIdImpl() {
        }

        @Override
        public final native com.google.collide.dto.WorkspaceInfo.WorkspaceType getWorkspaceType() /*-{
            return @com.google.collide.dto.WorkspaceInfo.WorkspaceType::valueOf(Ljava/lang/String;)(this["workspaceType"]);
        }-*/;

        public final native GetProjectByIdImpl setWorkspaceType(com.google.collide.dto.WorkspaceInfo.WorkspaceType workspaceType) /*-{
            workspaceType = workspaceType.@com.google.collide.dto.WorkspaceInfo.WorkspaceType::toString()();
            this["workspaceType"] = workspaceType;
            return this;
        }-*/;

        public final native boolean hasWorkspaceType() /*-{
            return this.hasOwnProperty("workspaceType");
        }-*/;

        @Override
        public final native java.lang.String getStartKey() /*-{
            return this["startKey"];
        }-*/;

        public final native GetProjectByIdImpl setStartKey(java.lang.String startKey) /*-{
            this["startKey"] = startKey;
            return this;
        }-*/;

        public final native boolean hasStartKey() /*-{
            return this.hasOwnProperty("startKey");
        }-*/;

        @Override
        public final native int getPageLength() /*-{
            return this["pageLength"];
        }-*/;

        public final native GetProjectByIdImpl setPageLength(int pageLength) /*-{
            this["pageLength"] = pageLength;
            return this;
        }-*/;

        public final native boolean hasPageLength() /*-{
            return this.hasOwnProperty("pageLength");
        }-*/;

        @Override
        public final native boolean getShouldLoadWorkspaces() /*-{
            return this["shouldLoadWorkspaces"];
        }-*/;

        public final native GetProjectByIdImpl setShouldLoadWorkspaces(boolean shouldLoadWorkspaces) /*-{
            this["shouldLoadWorkspaces"] = shouldLoadWorkspaces;
            return this;
        }-*/;

        public final native boolean hasShouldLoadWorkspaces() /*-{
            return this.hasOwnProperty("shouldLoadWorkspaces");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native GetProjectByIdImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native GetProjectByIdImpl make() /*-{
            return {
                _type: 45
            };
        }-*/;
    }


    public static class GetProjectByIdResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetProjectByIdResponse {
        protected GetProjectByIdResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.ProjectMembersInfo getProjectMembersInfo() /*-{
            return this["projectMembersInfo"];
        }-*/;

        public final native GetProjectByIdResponseImpl setProjectMembersInfo(
                com.google.collide.dto.ProjectMembersInfo projectMembersInfo) /*-{
            this["projectMembersInfo"] = projectMembersInfo;
            return this;
        }-*/;

        public final native boolean hasProjectMembersInfo() /*-{
            return this.hasOwnProperty("projectMembersInfo");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.WorkspaceInfo> getWorkspaces() /*-{
            return this["workspaces"];
        }-*/;

        public final native GetProjectByIdResponseImpl setWorkspaces(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.WorkspaceInfo> workspaces) /*-{
            this["workspaces"] = workspaces;
            return this;
        }-*/;

        public final native boolean hasWorkspaces() /*-{
            return this.hasOwnProperty("workspaces");
        }-*/;

        @Override
        public final native com.google.collide.dto.ProjectInfo getProject() /*-{
            return this["project"];
        }-*/;

        public final native GetProjectByIdResponseImpl setProject(com.google.collide.dto.ProjectInfo project) /*-{
            this["project"] = project;
            return this;
        }-*/;

        public final native boolean hasProject() /*-{
            return this.hasOwnProperty("project");
        }-*/;

        public static native GetProjectByIdResponseImpl make() /*-{
            return {
                _type: 46
            };
        }-*/;
    }


    public static class GetProjectMembersImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetProjectMembers {
        protected GetProjectMembersImpl() {
        }

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native GetProjectMembersImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native GetProjectMembersImpl make() /*-{
            return {
                _type: 47
            };
        }-*/;
    }


    public static class GetProjectMembersResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetProjectMembersResponse {
        protected GetProjectMembersResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> getPendingMembers() /*-{
            return this["pendingMembers"];
        }-*/;

        public final native GetProjectMembersResponseImpl setPendingMembers(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> pendingMembers) /*-{
            this["pendingMembers"] = pendingMembers;
            return this;
        }-*/;

        public final native boolean hasPendingMembers() /*-{
            return this.hasOwnProperty("pendingMembers");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> getMembers() /*-{
            return this["members"];
        }-*/;

        public final native GetProjectMembersResponseImpl setMembers(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> members) /*-{
            this["members"] = members;
            return this;
        }-*/;

        public final native boolean hasMembers() /*-{
            return this.hasOwnProperty("members");
        }-*/;

        public static native GetProjectMembersResponseImpl make() /*-{
            return {
                _type: 48
            };
        }-*/;
    }


    public static class GetProjectsResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetProjectsResponse {
        protected GetProjectsResponseImpl() {
        }

        @Override
        public final native java.lang.String getActiveProjectId() /*-{
            return this["activeProjectId"];
        }-*/;

        public final native GetProjectsResponseImpl setActiveProjectId(java.lang.String activeProjectId) /*-{
            this["activeProjectId"] = activeProjectId;
            return this;
        }-*/;

        public final native boolean hasActiveProjectId() /*-{
            return this.hasOwnProperty("activeProjectId");
        }-*/;

        @Override
        public final native java.lang.String getUserMembershipChangeNextVersion() /*-{
            return this["userMembershipChangeNextVersion"];
        }-*/;

        public final native GetProjectsResponseImpl setUserMembershipChangeNextVersion(
                java.lang.String userMembershipChangeNextVersion) /*-{
            this["userMembershipChangeNextVersion"] = userMembershipChangeNextVersion;
            return this;
        }-*/;

        public final native boolean hasUserMembershipChangeNextVersion() /*-{
            return this.hasOwnProperty("userMembershipChangeNextVersion");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getHiddenProjectIds() /*-{
            return this["hiddenProjectIds"];
        }-*/;

        public final native GetProjectsResponseImpl setHiddenProjectIds(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> hiddenProjectIds) /*-{
            this["hiddenProjectIds"] = hiddenProjectIds;
            return this;
        }-*/;

        public final native boolean hasHiddenProjectIds() /*-{
            return this.hasOwnProperty("hiddenProjectIds");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ProjectInfo> getProjects() /*-{
            return this["projects"];
        }-*/;

        public final native GetProjectsResponseImpl setProjects(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ProjectInfo> projects) /*-{
            this["projects"] = projects;
            return this;
        }-*/;

        public final native boolean hasProjects() /*-{
            return this.hasOwnProperty("projects");
        }-*/;

        public static native GetProjectsResponseImpl make() /*-{
            return {
                _type: 49
            };
        }-*/;
    }


    public static class GetStagingServerInfoResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetStagingServerInfoResponse {
        protected GetStagingServerInfoResponseImpl() {
        }

        @Override
        public final native java.lang.String getStagingServerAppId() /*-{
            return this["stagingServerAppId"];
        }-*/;

        public final native GetStagingServerInfoResponseImpl setStagingServerAppId(java.lang.String stagingServerAppId) /*-{
            this["stagingServerAppId"] = stagingServerAppId;
            return this;
        }-*/;

        public final native boolean hasStagingServerAppId() /*-{
            return this.hasOwnProperty("stagingServerAppId");
        }-*/;

        @Override
        public final native int getLatestMimicVersionId() /*-{
            return this["latestMimicVersionId"];
        }-*/;

        public final native GetStagingServerInfoResponseImpl setLatestMimicVersionId(int latestMimicVersionId) /*-{
            this["latestMimicVersionId"] = latestMimicVersionId;
            return this;
        }-*/;

        public final native boolean hasLatestMimicVersionId() /*-{
            return this.hasOwnProperty("latestMimicVersionId");
        }-*/;

        @Override
        public final native boolean getAutoUpdate() /*-{
            return this["autoUpdate"];
        }-*/;

        public final native GetStagingServerInfoResponseImpl setAutoUpdate(boolean autoUpdate) /*-{
            this["autoUpdate"] = autoUpdate;
            return this;
        }-*/;

        public final native boolean hasAutoUpdate() /*-{
            return this.hasOwnProperty("autoUpdate");
        }-*/;

        @Override
        public final native int getLastKnownMimicVersionId() /*-{
            return this["lastKnownMimicVersionId"];
        }-*/;

        public final native GetStagingServerInfoResponseImpl setLastKnownMimicVersionId(int lastKnownMimicVersionId) /*-{
            this["lastKnownMimicVersionId"] = lastKnownMimicVersionId;
            return this;
        }-*/;

        public final native boolean hasLastKnownMimicVersionId() /*-{
            return this.hasOwnProperty("lastKnownMimicVersionId");
        }-*/;

        public static native GetStagingServerInfoResponseImpl make() /*-{
            return {
                _type: 50
            };
        }-*/;
    }


    public static class GetSyncStateImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetSyncState {
        protected GetSyncStateImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetSyncStateImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        public static native GetSyncStateImpl make() /*-{
            return {
                _type: 51
            };
        }-*/;
    }


    public static class GetSyncStateResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetSyncStateResponse {
        protected GetSyncStateResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.GetSyncStateResponse.SyncState getSyncState() /*-{
            return @com.google.collide.dto.GetSyncStateResponse.SyncState::valueOf(Ljava/lang/String;)(this["syncState"]);
        }-*/;

        public final native GetSyncStateResponseImpl setSyncState(com.google.collide.dto.GetSyncStateResponse.SyncState syncState) /*-{
            syncState = syncState.@com.google.collide.dto.GetSyncStateResponse.SyncState::toString()();
            this["syncState"] = syncState;
            return this;
        }-*/;

        public final native boolean hasSyncState() /*-{
            return this.hasOwnProperty("syncState");
        }-*/;

        public static native GetSyncStateResponseImpl make() /*-{
            return {
                _type: 52
            };
        }-*/;
    }


    public static class GetTemplatesImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetTemplates {
        protected GetTemplatesImpl() {
        }

        @Override
        public final native java.lang.String getLocale() /*-{
            return this["locale"];
        }-*/;

        public final native GetTemplatesImpl setLocale(java.lang.String locale) /*-{
            this["locale"] = locale;
            return this;
        }-*/;

        public final native boolean hasLocale() /*-{
            return this.hasOwnProperty("locale");
        }-*/;

        public static native GetTemplatesImpl make() /*-{
            return {
                _type: 53
            };
        }-*/;
    }


    public static class GetTemplatesResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetTemplatesResponse {
        protected GetTemplatesResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonStringMap<java.lang.String> getTemplates() /*-{
            return this["templates"];
        }-*/;

        public final native GetTemplatesResponseImpl setTemplates(
                com.codenvy.ide.json.shared.JsonStringMap<java.lang.String> templates) /*-{
            this["templates"] = templates;
            return this;
        }-*/;

        public final native boolean hasTemplates() /*-{
            return this.hasOwnProperty("templates");
        }-*/;

        public static native GetTemplatesResponseImpl make() /*-{
            return {
                _type: 54
            };
        }-*/;
    }


    public static class GetUserAppEngineAppIdsImpl extends GetAppEngineClusterTypeImpl
            implements com.google.collide.dto.GetUserAppEngineAppIds {
        protected GetUserAppEngineAppIdsImpl() {
        }

        public static native GetUserAppEngineAppIdsImpl make() /*-{
            return {
                _type: 55
            };
        }-*/;
    }


    public static class GetUserAppEngineAppIdsResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetUserAppEngineAppIdsResponse {
        protected GetUserAppEngineAppIdsResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getAppIds() /*-{
            return this["appIds"];
        }-*/;

        public final native GetUserAppEngineAppIdsResponseImpl setAppIds(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> appIds) /*-{
            this["appIds"] = appIds;
            return this;
        }-*/;

        public final native boolean hasAppIds() /*-{
            return this.hasOwnProperty("appIds");
        }-*/;

        public static native GetUserAppEngineAppIdsResponseImpl make() /*-{
            return {
                _type: 56
            };
        }-*/;
    }


    public static class GetWorkspaceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspace {
        protected GetWorkspaceImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetWorkspaceImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native GetWorkspaceImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native GetWorkspaceImpl make() /*-{
            return {
                _type: 63
            };
        }-*/;
    }


    public static class GetWorkspaceChangeSummaryImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceChangeSummary {
        protected GetWorkspaceChangeSummaryImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetWorkspaceChangeSummaryImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native GetWorkspaceChangeSummaryImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native GetWorkspaceChangeSummaryImpl make() /*-{
            return {
                _type: 57
            };
        }-*/;
    }


    public static class GetWorkspaceChangeSummaryResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceChangeSummaryResponse {
        protected GetWorkspaceChangeSummaryResponseImpl() {
        }

        @Override
        public final native java.lang.String getBaseRootId() /*-{
            return this["baseRootId"];
        }-*/;

        public final native GetWorkspaceChangeSummaryResponseImpl setBaseRootId(java.lang.String baseRootId) /*-{
            this["baseRootId"] = baseRootId;
            return this;
        }-*/;

        public final native boolean hasBaseRootId() /*-{
            return this.hasOwnProperty("baseRootId");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.NodeMutationDto> getNodeMutations() /*-{
            return this["nodeMutations"];
        }-*/;

        public final native GetWorkspaceChangeSummaryResponseImpl setNodeMutations(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.NodeMutationDto> nodeMutations) /*-{
            this["nodeMutations"] = nodeMutations;
            return this;
        }-*/;

        public final native boolean hasNodeMutations() /*-{
            return this.hasOwnProperty("nodeMutations");
        }-*/;

        @Override
        public final native java.lang.String getFinalRootId() /*-{
            return this["finalRootId"];
        }-*/;

        public final native GetWorkspaceChangeSummaryResponseImpl setFinalRootId(java.lang.String finalRootId) /*-{
            this["finalRootId"] = finalRootId;
            return this;
        }-*/;

        public final native boolean hasFinalRootId() /*-{
            return this.hasOwnProperty("finalRootId");
        }-*/;

        public static native GetWorkspaceChangeSummaryResponseImpl make() /*-{
            return {
                _type: 58
            };
        }-*/;
    }


    public static class GetWorkspaceMembersImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceMembers {
        protected GetWorkspaceMembersImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetWorkspaceMembersImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native GetWorkspaceMembersImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native GetWorkspaceMembersImpl make() /*-{
            return {
                _type: 59
            };
        }-*/;
    }


    public static class GetWorkspaceMembersResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceMembersResponse {
        protected GetWorkspaceMembersResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> getMembers() /*-{
            return this["members"];
        }-*/;

        public final native GetWorkspaceMembersResponseImpl setMembers(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> members) /*-{
            this["members"] = members;
            return this;
        }-*/;

        public final native boolean hasMembers() /*-{
            return this.hasOwnProperty("members");
        }-*/;

        public static native GetWorkspaceMembersResponseImpl make() /*-{
            return {
                _type: 60
            };
        }-*/;
    }


    public static class GetWorkspaceMetaDataImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceMetaData {
        protected GetWorkspaceMetaDataImpl() {
        }

        public static native GetWorkspaceMetaDataImpl make() /*-{
            return {
                _type: 122
            };
        }-*/;
    }


    public static class GetWorkspaceMetaDataResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceMetaDataResponse {
        protected GetWorkspaceMetaDataResponseImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceName() /*-{
            return this["workspaceName"];
        }-*/;

        public final native GetWorkspaceMetaDataResponseImpl setWorkspaceName(java.lang.String workspaceName) /*-{
            this["workspaceName"] = workspaceName;
            return this;
        }-*/;

        public final native boolean hasWorkspaceName() /*-{
            return this.hasOwnProperty("workspaceName");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getLastOpenFiles() /*-{
            return this["lastOpenFiles"];
        }-*/;

        public final native GetWorkspaceMetaDataResponseImpl setLastOpenFiles(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> lastOpenFiles) /*-{
            this["lastOpenFiles"] = lastOpenFiles;
            return this;
        }-*/;

        public final native boolean hasLastOpenFiles() /*-{
            return this.hasOwnProperty("lastOpenFiles");
        }-*/;

        @Override
        public final native com.google.collide.dto.RunTarget getRunTarget() /*-{
            return this["runTarget"];
        }-*/;

        public final native GetWorkspaceMetaDataResponseImpl setRunTarget(com.google.collide.dto.RunTarget runTarget) /*-{
            this["runTarget"] = runTarget;
            return this;
        }-*/;

        public final native boolean hasRunTarget() /*-{
            return this.hasOwnProperty("runTarget");
        }-*/;

        public static native GetWorkspaceMetaDataResponseImpl make() /*-{
            return {
                _type: 115
            };
        }-*/;
    }


    public static class GetWorkspaceParticipantsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceParticipants {
        protected GetWorkspaceParticipantsImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native GetWorkspaceParticipantsImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getParticipantIds() /*-{
            return this["participantIds"];
        }-*/;

        public final native GetWorkspaceParticipantsImpl setParticipantIds(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> participantIds) /*-{
            this["participantIds"] = participantIds;
            return this;
        }-*/;

        public final native boolean hasParticipantIds() /*-{
            return this.hasOwnProperty("participantIds");
        }-*/;

        public static native GetWorkspaceParticipantsImpl make() /*-{
            return {
                _type: 61
            };
        }-*/;
    }


    public static class GetWorkspaceParticipantsResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceParticipantsResponse {
        protected GetWorkspaceParticipantsResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> getParticipants() /*-{
            return this["participants"];
        }-*/;

        public final native GetWorkspaceParticipantsResponseImpl setParticipants(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> participants) /*-{
            this["participants"] = participants;
            return this;
        }-*/;

        public final native boolean hasParticipants() /*-{
            return this.hasOwnProperty("participants");
        }-*/;

        public static native GetWorkspaceParticipantsResponseImpl make() /*-{
            return {
                _type: 62
            };
        }-*/;
    }


    public static class GetWorkspaceResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.GetWorkspaceResponse {
        protected GetWorkspaceResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.WorkspaceInfo getWorkspace() /*-{
            return this["workspace"];
        }-*/;

        public final native GetWorkspaceResponseImpl setWorkspace(com.google.collide.dto.WorkspaceInfo workspace) /*-{
            this["workspace"] = workspace;
            return this;
        }-*/;

        public final native boolean hasWorkspace() /*-{
            return this.hasOwnProperty("workspace");
        }-*/;

        public static native GetWorkspaceResponseImpl make() /*-{
            return {
                _type: 64
            };
        }-*/;
    }


    public static class ImportAssociationImpl extends CodeBlockAssociationImpl implements com.google.collide.dto.ImportAssociation {
        protected ImportAssociationImpl() {
        }

        public static native ImportAssociationImpl make() /*-{
            return [];
        }-*/;
    }


    public static class InheritanceAssociationImpl extends CodeBlockAssociationImpl
            implements com.google.collide.dto.InheritanceAssociation {
        protected InheritanceAssociationImpl() {
        }

        public static native InheritanceAssociationImpl make() /*-{
            return [];
        }-*/;
    }


    public static class InvalidationMessageImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.InvalidationMessage {
        protected InvalidationMessageImpl() {
        }

        @Override
        public final native java.lang.String getObjectName() /*-{
            return this["objectName"];
        }-*/;

        public final native InvalidationMessageImpl setObjectName(java.lang.String objectName) /*-{
            this["objectName"] = objectName;
            return this;
        }-*/;

        public final native boolean hasObjectName() /*-{
            return this.hasOwnProperty("objectName");
        }-*/;

        @Override
        public final native java.lang.String getPayload() /*-{
            return this["payload"];
        }-*/;

        public final native InvalidationMessageImpl setPayload(java.lang.String payload) /*-{
            this["payload"] = payload;
            return this;
        }-*/;

        public final native boolean hasPayload() /*-{
            return this.hasOwnProperty("payload");
        }-*/;

        @Override
        public final native java.lang.String getVersion() /*-{
            return this["version"];
        }-*/;

        public final native InvalidationMessageImpl setVersion(java.lang.String version) /*-{
            this["version"] = version;
            return this;
        }-*/;

        public final native boolean hasVersion() /*-{
            return this.hasOwnProperty("version");
        }-*/;

        public static native InvalidationMessageImpl make() /*-{
            return {
                _type: 25
            };
        }-*/;
    }


    public static class KeepAliveImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.KeepAlive {
        protected KeepAliveImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native KeepAliveImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        public static native KeepAliveImpl make() /*-{
            return {
                _type: 67
            };
        }-*/;
    }


    public static class LeaveWorkspaceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.LeaveWorkspace {
        protected LeaveWorkspaceImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native LeaveWorkspaceImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        public static native LeaveWorkspaceImpl make() /*-{
            return {
                _type: 69
            };
        }-*/;
    }


    public static class LoadTemplateImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.LoadTemplate {
        protected LoadTemplateImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native LoadTemplateImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getTemplateTag() /*-{
            return this["templateTag"];
        }-*/;

        public final native LoadTemplateImpl setTemplateTag(java.lang.String templateTag) /*-{
            this["templateTag"] = templateTag;
            return this;
        }-*/;

        public final native boolean hasTemplateTag() /*-{
            return this.hasOwnProperty("templateTag");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native LoadTemplateImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native LoadTemplateImpl make() /*-{
            return {
                _type: 70
            };
        }-*/;
    }


    public static class LoadTemplateResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.LoadTemplateResponse {
        protected LoadTemplateResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.RunTarget getRunTarget() /*-{
            return this["runTarget"];
        }-*/;

        public final native LoadTemplateResponseImpl setRunTarget(com.google.collide.dto.RunTarget runTarget) /*-{
            this["runTarget"] = runTarget;
            return this;
        }-*/;

        public final native boolean hasRunTarget() /*-{
            return this.hasOwnProperty("runTarget");
        }-*/;

        public static native LoadTemplateResponseImpl make() /*-{
            return {
                _type: 71
            };
        }-*/;
    }


    public static class LogFatalRecordImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.LogFatalRecord {
        protected LogFatalRecordImpl() {
        }

        @Override
        public final native com.google.collide.dto.ThrowableDto getThrowable() /*-{
            return this["throwable"];
        }-*/;

        public final native LogFatalRecordImpl setThrowable(com.google.collide.dto.ThrowableDto throwable) /*-{
            this["throwable"] = throwable;
            return this;
        }-*/;

        public final native boolean hasThrowable() /*-{
            return this.hasOwnProperty("throwable");
        }-*/;

        @Override
        public final native java.lang.String getWindowLocation() /*-{
            return this["windowLocation"];
        }-*/;

        public final native LogFatalRecordImpl setWindowLocation(java.lang.String windowLocation) /*-{
            this["windowLocation"] = windowLocation;
            return this;
        }-*/;

        public final native boolean hasWindowLocation() /*-{
            return this.hasOwnProperty("windowLocation");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getRecentHistory() /*-{
            return this["recentHistory"];
        }-*/;

        public final native LogFatalRecordImpl setRecentHistory(com.codenvy.ide.json.shared.JsonArray<java.lang.String> recentHistory) /*-{
            this["recentHistory"] = recentHistory;
            return this;
        }-*/;

        public final native boolean hasRecentHistory() /*-{
            return this.hasOwnProperty("recentHistory");
        }-*/;

        @Override
        public final native java.lang.String getPermutationStrongName() /*-{
            return this["permutationStrongName"];
        }-*/;

        public final native LogFatalRecordImpl setPermutationStrongName(java.lang.String permutationStrongName) /*-{
            this["permutationStrongName"] = permutationStrongName;
            return this;
        }-*/;

        public final native boolean hasPermutationStrongName() /*-{
            return this.hasOwnProperty("permutationStrongName");
        }-*/;

        @Override
        public final native java.lang.String getMessage() /*-{
            return this["message"];
        }-*/;

        public final native LogFatalRecordImpl setMessage(java.lang.String message) /*-{
            this["message"] = message;
            return this;
        }-*/;

        public final native boolean hasMessage() /*-{
            return this.hasOwnProperty("message");
        }-*/;

        public static native LogFatalRecordImpl make() /*-{
            return {
                _type: 72
            };
        }-*/;
    }


    public static class LogFatalRecordResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.LogFatalRecordResponse {
        protected LogFatalRecordResponseImpl() {
        }

        @Override
        public final native java.lang.String getServiceName() /*-{
            return this["serviceName"];
        }-*/;

        public final native LogFatalRecordResponseImpl setServiceName(java.lang.String serviceName) /*-{
            this["serviceName"] = serviceName;
            return this;
        }-*/;

        public final native boolean hasServiceName() /*-{
            return this.hasOwnProperty("serviceName");
        }-*/;

        @Override
        public final native java.lang.String getThrowableProtoHex() /*-{
            return this["throwableProtoHex"];
        }-*/;

        public final native LogFatalRecordResponseImpl setThrowableProtoHex(java.lang.String throwableProtoHex) /*-{
            this["throwableProtoHex"] = throwableProtoHex;
            return this;
        }-*/;

        public final native boolean hasThrowableProtoHex() /*-{
            return this.hasOwnProperty("throwableProtoHex");
        }-*/;

        @Override
        public final native java.lang.String getStackTrace() /*-{
            return this["stackTrace"];
        }-*/;

        public final native LogFatalRecordResponseImpl setStackTrace(java.lang.String stackTrace) /*-{
            this["stackTrace"] = stackTrace;
            return this;
        }-*/;

        public final native boolean hasStackTrace() /*-{
            return this.hasOwnProperty("stackTrace");
        }-*/;

        public static native LogFatalRecordResponseImpl make() /*-{
            return {
                _type: 73
            };
        }-*/;
    }


    public static class LogMetricImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.LogMetric {
        protected LogMetricImpl() {
        }

        @Override
        public final native java.lang.String getAction() /*-{
            return this["action"];
        }-*/;

        public final native LogMetricImpl setAction(java.lang.String action) /*-{
            this["action"] = action;
            return this;
        }-*/;

        public final native boolean hasAction() /*-{
            return this.hasOwnProperty("action");
        }-*/;

        @Override
        public final native java.lang.String getEvent() /*-{
            return this["event"];
        }-*/;

        public final native LogMetricImpl setEvent(java.lang.String event) /*-{
            this["event"] = event;
            return this;
        }-*/;

        public final native boolean hasEvent() /*-{
            return this.hasOwnProperty("event");
        }-*/;

        @Override
        public final native java.lang.String getMessage() /*-{
            return this["message"];
        }-*/;

        public final native LogMetricImpl setMessage(java.lang.String message) /*-{
            this["message"] = message;
            return this;
        }-*/;

        public final native boolean hasMessage() /*-{
            return this.hasOwnProperty("message");
        }-*/;

        @Override
        public final native double getTimestamp() /*-{
            return this["timestamp"];
        }-*/;

        public final native LogMetricImpl setTimestamp(double timestamp) /*-{
            this["timestamp"] = timestamp;
            return this;
        }-*/;

        public final native boolean hasTimestamp() /*-{
            return this.hasOwnProperty("timestamp");
        }-*/;

        public static native LogMetricImpl make() /*-{
            return {
                _type: 74
            };
        }-*/;
    }


    public static class LogMetricsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.LogMetrics {
        protected LogMetricsImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.LogMetric> getMetrics() /*-{
            return this["metrics"];
        }-*/;

        public final native LogMetricsImpl setMetrics(com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.LogMetric> metrics) /*-{
            this["metrics"] = metrics;
            return this;
        }-*/;

        public final native boolean hasMetrics() /*-{
            return this.hasOwnProperty("metrics");
        }-*/;

        public static native LogMetricsImpl make() /*-{
            return {
                _type: 75
            };
        }-*/;
    }


    public static class MembershipChangedPayloadImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.MembershipChangedPayload {
        protected MembershipChangedPayloadImpl() {
        }

        @Override
        public final native com.google.collide.dto.MembershipChangedPayload.MembershipChange getMembershipChange() /*-{
            return @com.google.collide.dto.MembershipChangedPayload.MembershipChange::valueOf(Ljava/lang/String;)(this["membershipChange"]);
        }-*/;

        public final native MembershipChangedPayloadImpl setMembershipChange(
                com.google.collide.dto.MembershipChangedPayload.MembershipChange membershipChange) /*-{
            membershipChange = membershipChange.@com.google.collide.dto.MembershipChangedPayload.MembershipChange::toString()();
            this["membershipChange"] = membershipChange;
            return this;
        }-*/;

        public final native boolean hasMembershipChange() /*-{
            return this.hasOwnProperty("membershipChange");
        }-*/;

        @Override
        public final native java.lang.String getId() /*-{
            return this["id"];
        }-*/;

        public final native MembershipChangedPayloadImpl setId(java.lang.String id) /*-{
            this["id"] = id;
            return this;
        }-*/;

        public final native boolean hasId() /*-{
            return this.hasOwnProperty("id");
        }-*/;

        public static native MembershipChangedPayloadImpl make() /*-{
            return {
                _type: 76
            };
        }-*/;
    }


    public static class MutationImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.Mutation {
        protected MutationImpl() {
        }

        @Override
        public final native java.lang.String getNewPath() /*-{
            return this["newPath"];
        }-*/;

        public final native MutationImpl setNewPath(java.lang.String newPath) /*-{
            this["newPath"] = newPath;
            return this;
        }-*/;

        public final native boolean hasNewPath() /*-{
            return this.hasOwnProperty("newPath");
        }-*/;

        @Override
        public final native com.google.collide.dto.Mutation.Type getMutationType() /*-{
            return @com.google.collide.dto.Mutation.Type::valueOf(Ljava/lang/String;)(this["mutationType"]);
        }-*/;

        public final native MutationImpl setMutationType(com.google.collide.dto.Mutation.Type mutationType) /*-{
            mutationType = mutationType.@com.google.collide.dto.Mutation.Type::toString()();
            this["mutationType"] = mutationType;
            return this;
        }-*/;

        public final native boolean hasMutationType() /*-{
            return this.hasOwnProperty("mutationType");
        }-*/;

        @Override
        public final native com.google.collide.dto.TreeNodeInfo getNewNodeInfo() /*-{
            return this["newNodeInfo"];
        }-*/;

        public final native MutationImpl setNewNodeInfo(com.google.collide.dto.TreeNodeInfo newNodeInfo) /*-{
            this["newNodeInfo"] = newNodeInfo;
            return this;
        }-*/;

        public final native boolean hasNewNodeInfo() /*-{
            return this.hasOwnProperty("newNodeInfo");
        }-*/;

        @Override
        public final native java.lang.String getOldPath() /*-{
            return this["oldPath"];
        }-*/;

        public final native MutationImpl setOldPath(java.lang.String oldPath) /*-{
            this["oldPath"] = oldPath;
            return this;
        }-*/;

        public final native boolean hasOldPath() /*-{
            return this.hasOwnProperty("oldPath");
        }-*/;

        public static native MutationImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class NewFileCollaboratorImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.NewFileCollaborator {
        protected NewFileCollaboratorImpl() {
        }

        @Override
        public final native com.google.collide.dto.ParticipantUserDetails getParticipant() /*-{
            return this["participant"];
        }-*/;

        public final native NewFileCollaboratorImpl setParticipant(com.google.collide.dto.ParticipantUserDetails participant) /*-{
            this["participant"] = participant;
            return this;
        }-*/;

        public final native boolean hasParticipant() /*-{
            return this.hasOwnProperty("participant");
        }-*/;

        @Override
        public final native java.lang.String getEditSessionId() /*-{
            return this["editSessionId"];
        }-*/;

        public final native NewFileCollaboratorImpl setEditSessionId(java.lang.String editSessionId) /*-{
            this["editSessionId"] = editSessionId;
            return this;
        }-*/;

        public final native boolean hasEditSessionId() /*-{
            return this.hasOwnProperty("editSessionId");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native NewFileCollaboratorImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native NewFileCollaboratorImpl make() /*-{
            return {
                _type: 125
            };
        }-*/;
    }


    public static class ConflictHandleImpl extends com.codenvy.ide.json.client.Jso
            implements com.google.collide.dto.NodeConflictDto.ConflictHandle {
        protected ConflictHandleImpl() {
        }

        @Override
        public final native int getConflictIndex() /*-{
            return this["conflictIndex"];
        }-*/;

        public final native ConflictHandleImpl setConflictIndex(int conflictIndex) /*-{
            this["conflictIndex"] = conflictIndex;
            return this;
        }-*/;

        public final native boolean hasConflictIndex() /*-{
            return this.hasOwnProperty("conflictIndex");
        }-*/;

        @Override
        public final native java.lang.String getConflictId() /*-{
            return this["conflictId"];
        }-*/;

        public final native ConflictHandleImpl setConflictId(java.lang.String conflictId) /*-{
            this["conflictId"] = conflictId;
            return this;
        }-*/;

        public final native boolean hasConflictId() /*-{
            return this.hasOwnProperty("conflictId");
        }-*/;

        public static native ConflictHandleImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ConflictedPathImpl extends com.codenvy.ide.json.client.Jso
            implements com.google.collide.dto.NodeConflictDto.ConflictedPath {
        protected ConflictedPathImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native ConflictedPathImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native int getNodeType() /*-{
            return this["nodeType"];
        }-*/;

        public final native ConflictedPathImpl setNodeType(int nodeType) /*-{
            this["nodeType"] = nodeType;
            return this;
        }-*/;

        public final native boolean hasNodeType() /*-{
            return this.hasOwnProperty("nodeType");
        }-*/;

        @Override
        public final native java.lang.String getStartId() /*-{
            return this["startId"];
        }-*/;

        public final native ConflictedPathImpl setStartId(java.lang.String startId) /*-{
            this["startId"] = startId;
            return this;
        }-*/;

        public final native boolean hasStartId() /*-{
            return this.hasOwnProperty("startId");
        }-*/;

        @Override
        public final native boolean isUtf8() /*-{
            return this["isUtf8"];
        }-*/;

        public final native ConflictedPathImpl setIsUtf8(boolean isUtf8) /*-{
            this["isUtf8"] = isUtf8;
            return this;
        }-*/;

        public final native boolean hasIsUtf8() /*-{
            return this.hasOwnProperty("isUtf8");
        }-*/;

        @Override
        public final native java.lang.String getPath() /*-{
            return this["path"];
        }-*/;

        public final native ConflictedPathImpl setPath(java.lang.String path) /*-{
            this["path"] = path;
            return this;
        }-*/;

        public final native boolean hasPath() /*-{
            return this.hasOwnProperty("path");
        }-*/;

        public static native ConflictedPathImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class NodeConflictDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.NodeConflictDto {
        protected NodeConflictDtoImpl() {
        }

        @Override
        public final native com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() /*-{
            return this["conflictHandle"];
        }-*/;

        public final native NodeConflictDtoImpl setConflictHandle(com.google.collide.dto.NodeConflictDto.ConflictHandle conflictHandle) /*-{
            this["conflictHandle"] = conflictHandle;
            return this;
        }-*/;

        public final native boolean hasConflictHandle() /*-{
            return this.hasOwnProperty("conflictHandle");
        }-*/;

        @Override
        public final native java.lang.String getParentDescription() /*-{
            return this["parentDescription"];
        }-*/;

        public final native NodeConflictDtoImpl setParentDescription(java.lang.String parentDescription) /*-{
            this["parentDescription"] = parentDescription;
            return this;
        }-*/;

        public final native boolean hasParentDescription() /*-{
            return this.hasOwnProperty("parentDescription");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice>
        getValidResolutions() /*-{
            _tmp = [];
            this["validResolutions"].forEach(function (in1, tmp0) {
                out1 = @com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice::valueOf(Ljava/lang/String;)(in1);
                _tmp[tmp0] = out1;
            });
            return _tmp;
        }-*/;

        public final native NodeConflictDtoImpl setValidResolutions(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice>
                        validResolutions) /*-{
            _tmp = validResolutions;
            tmp0 = [];
            _tmp.forEach(function (in1) {
                out1 = in1.@com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice::toString()();
                tmp0.push(out1);
            });
            validResolutions = tmp0;
            this["validResolutions"] = validResolutions;
            return this;
        }-*/;

        public final native boolean hasValidResolutions() /*-{
            return this.hasOwnProperty("validResolutions");
        }-*/;

        @Override
        public final native java.lang.String getConflictDescription() /*-{
            return this["conflictDescription"];
        }-*/;

        public final native NodeConflictDtoImpl setConflictDescription(java.lang.String conflictDescription) /*-{
            this["conflictDescription"] = conflictDescription;
            return this;
        }-*/;

        public final native boolean hasConflictDescription() /*-{
            return this.hasOwnProperty("conflictDescription");
        }-*/;

        @Override
        public final native java.lang.String getChildDescription() /*-{
            return this["childDescription"];
        }-*/;

        public final native NodeConflictDtoImpl setChildDescription(java.lang.String childDescription) /*-{
            this["childDescription"] = childDescription;
            return this;
        }-*/;

        public final native boolean hasChildDescription() /*-{
            return this.hasOwnProperty("childDescription");
        }-*/;

        @Override
        public final native com.google.collide.dto.NodeConflictDto.ConflictedPath getChildPath() /*-{
            return this["childPath"];
        }-*/;

        public final native NodeConflictDtoImpl setChildPath(com.google.collide.dto.NodeConflictDto.ConflictedPath childPath) /*-{
            this["childPath"] = childPath;
            return this;
        }-*/;

        public final native boolean hasChildPath() /*-{
            return this.hasOwnProperty("childPath");
        }-*/;

        @Override
        public final native com.google.collide.dto.NodeConflictDto.SimplifiedConflictType getSimplifiedConflictType() /*-{
            return @com.google.collide.dto.NodeConflictDto.SimplifiedConflictType::valueOf(Ljava/lang/String;)
                (this["simplifiedConflictType"]);
        }-*/;

        public final native NodeConflictDtoImpl setSimplifiedConflictType(
                com.google.collide.dto.NodeConflictDto.SimplifiedConflictType simplifiedConflictType) /*-{
            simplifiedConflictType = simplifiedConflictType.@com.google.collide.dto.NodeConflictDto.SimplifiedConflictType::toString()();
            this["simplifiedConflictType"] = simplifiedConflictType;
            return this;
        }-*/;

        public final native boolean hasSimplifiedConflictType() /*-{
            return this.hasOwnProperty("simplifiedConflictType");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto> getGroupedConflicts() /*-{
            return this["groupedConflicts"];
        }-*/;

        public final native NodeConflictDtoImpl setGroupedConflicts(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto> groupedConflicts) /*-{
            this["groupedConflicts"] = groupedConflicts;
            return this;
        }-*/;

        public final native boolean hasGroupedConflicts() /*-{
            return this.hasOwnProperty("groupedConflicts");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto.ConflictedPath> getParentPaths()
            /*-{
            return this["parentPaths"];
        }-*/;

        public final native NodeConflictDtoImpl setParentPaths(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto.ConflictedPath> parentPaths) /*-{
            this["parentPaths"] = parentPaths;
            return this;
        }-*/;

        public final native boolean hasParentPaths() /*-{
            return this.hasOwnProperty("parentPaths");
        }-*/;

        public static native NodeConflictDtoImpl make() /*-{
            return {
                _type: 77
            };
        }-*/;
    }


    public static class NodeHistoryInfoImpl extends TreeNodeInfoImpl implements com.google.collide.dto.NodeHistoryInfo {
        protected NodeHistoryInfoImpl() {
        }

        @Override
        public final native java.lang.String getPredecessorId() /*-{
            return this["predecessorId"];
        }-*/;

        public final native NodeHistoryInfoImpl setPredecessorId(java.lang.String predecessorId) /*-{
            this["predecessorId"] = predecessorId;
            return this;
        }-*/;

        public final native boolean hasPredecessorId() /*-{
            return this.hasOwnProperty("predecessorId");
        }-*/;

        @Override
        public final native java.lang.String getCreationTime() /*-{
            return this["creationTime"];
        }-*/;

        public final native NodeHistoryInfoImpl setCreationTime(java.lang.String creationTime) /*-{
            this["creationTime"] = creationTime;
            return this;
        }-*/;

        public final native boolean hasCreationTime() /*-{
            return this.hasOwnProperty("creationTime");
        }-*/;

        public static native NodeHistoryInfoImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class NodeMutationDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.NodeMutationDto {
        protected NodeMutationDtoImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native NodeMutationDtoImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native NodeMutationDtoImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native com.google.collide.dto.DiffStatsDto getDiffStats() /*-{
            return this["diffStats"];
        }-*/;

        public final native NodeMutationDtoImpl setDiffStats(com.google.collide.dto.DiffStatsDto diffStats) /*-{
            this["diffStats"] = diffStats;
            return this;
        }-*/;

        public final native boolean hasDiffStats() /*-{
            return this.hasOwnProperty("diffStats");
        }-*/;

        @Override
        public final native java.lang.String getNewPath() /*-{
            return this["newPath"];
        }-*/;

        public final native NodeMutationDtoImpl setNewPath(java.lang.String newPath) /*-{
            this["newPath"] = newPath;
            return this;
        }-*/;

        public final native boolean hasNewPath() /*-{
            return this.hasOwnProperty("newPath");
        }-*/;

        @Override
        public final native com.google.collide.dto.NodeMutationDto.MutationType getMutationType() /*-{
            return @com.google.collide.dto.NodeMutationDto.MutationType::valueOf(Ljava/lang/String;)(this["mutationType"]);
        }-*/;

        public final native NodeMutationDtoImpl setMutationType(com.google.collide.dto.NodeMutationDto.MutationType mutationType) /*-{
            mutationType = mutationType.@com.google.collide.dto.NodeMutationDto.MutationType::toString()();
            this["mutationType"] = mutationType;
            return this;
        }-*/;

        public final native boolean hasMutationType() /*-{
            return this.hasOwnProperty("mutationType");
        }-*/;

        @Override
        public final native java.lang.String getOldPath() /*-{
            return this["oldPath"];
        }-*/;

        public final native NodeMutationDtoImpl setOldPath(java.lang.String oldPath) /*-{
            this["oldPath"] = oldPath;
            return this;
        }-*/;

        public final native boolean hasOldPath() /*-{
            return this.hasOwnProperty("oldPath");
        }-*/;

        @Override
        public final native boolean isFile() /*-{
            return this["isFile"];
        }-*/;

        public final native NodeMutationDtoImpl setIsFile(boolean isFile) /*-{
            this["isFile"] = isFile;
            return this;
        }-*/;

        public final native boolean hasIsFile() /*-{
            return this.hasOwnProperty("isFile");
        }-*/;

        public static native NodeMutationDtoImpl make() /*-{
            return {
                _type: 78
            };
        }-*/;
    }


    public static class ParticipantImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.Participant {
        protected ParticipantImpl() {
        }

        @Override
        public final native java.lang.String getUserId() /*-{
            return this["userId"];
        }-*/;

        public final native ParticipantImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        @Override
        public final native java.lang.String getId() /*-{
            return this["id"];
        }-*/;

        public final native ParticipantImpl setId(java.lang.String id) /*-{
            this["id"] = id;
            return this;
        }-*/;

        public final native boolean hasId() /*-{
            return this.hasOwnProperty("id");
        }-*/;

        public static native ParticipantImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ParticipantUserDetailsImpl extends com.codenvy.ide.json.client.Jso
            implements com.google.collide.dto.ParticipantUserDetails {
        protected ParticipantUserDetailsImpl() {
        }

        @Override
        public final native com.google.collide.dto.UserDetails getUserDetails() /*-{
            return this["userDetails"];
        }-*/;

        public final native ParticipantUserDetailsImpl setUserDetails(com.google.collide.dto.UserDetails userDetails) /*-{
            this["userDetails"] = userDetails;
            return this;
        }-*/;

        public final native boolean hasUserDetails() /*-{
            return this.hasOwnProperty("userDetails");
        }-*/;

        @Override
        public final native com.google.collide.dto.Participant getParticipant() /*-{
            return this["participant"];
        }-*/;

        public final native ParticipantUserDetailsImpl setParticipant(com.google.collide.dto.Participant participant) /*-{
            this["participant"] = participant;
            return this;
        }-*/;

        public final native boolean hasParticipant() /*-{
            return this.hasOwnProperty("participant");
        }-*/;

        public static native ParticipantUserDetailsImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ProjectInfoImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.ProjectInfo {
        protected ProjectInfoImpl() {
        }

        @Override
        public final native java.lang.String getRootWsId() /*-{
            return this["rootWsId"];
        }-*/;

        public final native ProjectInfoImpl setRootWsId(java.lang.String rootWsId) /*-{
            this["rootWsId"] = rootWsId;
            return this;
        }-*/;

        public final native boolean hasRootWsId() /*-{
            return this.hasOwnProperty("rootWsId");
        }-*/;

        @Override
        public final native java.lang.String getSummary() /*-{
            return this["summary"];
        }-*/;

        public final native ProjectInfoImpl setSummary(java.lang.String summary) /*-{
            this["summary"] = summary;
            return this;
        }-*/;

        public final native boolean hasSummary() /*-{
            return this.hasOwnProperty("summary");
        }-*/;

        @Override
        public final native java.lang.String getLogoUrl() /*-{
            return this["logoUrl"];
        }-*/;

        public final native ProjectInfoImpl setLogoUrl(java.lang.String logoUrl) /*-{
            this["logoUrl"] = logoUrl;
            return this;
        }-*/;

        public final native boolean hasLogoUrl() /*-{
            return this.hasOwnProperty("logoUrl");
        }-*/;

        @Override
        public final native com.google.collide.dto.Role getCurrentUserRole() /*-{
            return @com.google.collide.dto.Role::valueOf(Ljava/lang/String;)(this["currentUserRole"]);
        }-*/;

        public final native ProjectInfoImpl setCurrentUserRole(com.google.collide.dto.Role currentUserRole) /*-{
            currentUserRole = currentUserRole.@com.google.collide.dto.Role::toString()();
            this["currentUserRole"] = currentUserRole;
            return this;
        }-*/;

        public final native boolean hasCurrentUserRole() /*-{
            return this.hasOwnProperty("currentUserRole");
        }-*/;

        @Override
        public final native java.lang.String getName() /*-{
            return this["name"];
        }-*/;

        public final native ProjectInfoImpl setName(java.lang.String name) /*-{
            this["name"] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty("name");
        }-*/;

        @Override
        public final native java.lang.String getId() /*-{
            return this["id"];
        }-*/;

        public final native ProjectInfoImpl setId(java.lang.String id) /*-{
            this["id"] = id;
            return this;
        }-*/;

        public final native boolean hasId() /*-{
            return this.hasOwnProperty("id");
        }-*/;

        public static native ProjectInfoImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ProjectMembersInfoImpl extends com.codenvy.ide.json.client.Jso
            implements com.google.collide.dto.ProjectMembersInfo {
        protected ProjectMembersInfoImpl() {
        }

        @Override
        public final native int pendingMembersCount() /*-{
            return this["pendingMembersCount"];
        }-*/;

        public final native ProjectMembersInfoImpl setPendingMembersCount(int pendingMembersCount) /*-{
            this["pendingMembersCount"] = pendingMembersCount;
            return this;
        }-*/;

        public final native boolean hasPendingMembersCount() /*-{
            return this.hasOwnProperty("pendingMembersCount");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetails> getMembers() /*-{
            return this["members"];
        }-*/;

        public final native ProjectMembersInfoImpl setMembers(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.UserDetails> members) /*-{
            this["members"] = members;
            return this;
        }-*/;

        public final native boolean hasMembers() /*-{
            return this.hasOwnProperty("members");
        }-*/;

        public static native ProjectMembersInfoImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class RecoverFromDroppedTangoInvalidationImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.RecoverFromDroppedTangoInvalidation {
        protected RecoverFromDroppedTangoInvalidationImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native RecoverFromDroppedTangoInvalidationImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getTangoObjectIdName() /*-{
            return this["tangoObjectIdName"];
        }-*/;

        public final native RecoverFromDroppedTangoInvalidationImpl setTangoObjectIdName(java.lang.String tangoObjectIdName) /*-{
            this["tangoObjectIdName"] = tangoObjectIdName;
            return this;
        }-*/;

        public final native boolean hasTangoObjectIdName() /*-{
            return this.hasOwnProperty("tangoObjectIdName");
        }-*/;

        @Override
        public final native int getCurrentClientVersion() /*-{
            return this["currentClientVersion"];
        }-*/;

        public final native RecoverFromDroppedTangoInvalidationImpl setCurrentClientVersion(int currentClientVersion) /*-{
            this["currentClientVersion"] = currentClientVersion;
            return this;
        }-*/;

        public final native boolean hasCurrentClientVersion() /*-{
            return this.hasOwnProperty("currentClientVersion");
        }-*/;

        public static native RecoverFromDroppedTangoInvalidationImpl make() /*-{
            return {
                _type: 80
            };
        }-*/;
    }


    public static class RecoveredPayloadImpl extends com.codenvy.ide.json.client.Jso
            implements com.google.collide.dto.RecoverFromDroppedTangoInvalidationResponse.RecoveredPayload {
        protected RecoveredPayloadImpl() {
        }

        @Override
        public final native java.lang.String getPayload() /*-{
            return this["payload"];
        }-*/;

        public final native RecoveredPayloadImpl setPayload(java.lang.String payload) /*-{
            this["payload"] = payload;
            return this;
        }-*/;

        public final native boolean hasPayload() /*-{
            return this.hasOwnProperty("payload");
        }-*/;

        @Override
        public final native int getPayloadVersion() /*-{
            return this["payloadVersion"];
        }-*/;

        public final native RecoveredPayloadImpl setPayloadVersion(int payloadVersion) /*-{
            this["payloadVersion"] = payloadVersion;
            return this;
        }-*/;

        public final native boolean hasPayloadVersion() /*-{
            return this.hasOwnProperty("payloadVersion");
        }-*/;

        public static native RecoveredPayloadImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class RecoverFromDroppedTangoInvalidationResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.RecoverFromDroppedTangoInvalidationResponse {
        protected RecoverFromDroppedTangoInvalidationResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.RecoverFromDroppedTangoInvalidationResponse
                .RecoveredPayload> getPayloads() /*-{
            return this["payloads"];
        }-*/;

        public final native RecoverFromDroppedTangoInvalidationResponseImpl setPayloads(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.RecoverFromDroppedTangoInvalidationResponse
                        .RecoveredPayload> payloads) /*-{
            this["payloads"] = payloads;
            return this;
        }-*/;

        public final native boolean hasPayloads() /*-{
            return this.hasOwnProperty("payloads");
        }-*/;

        @Override
        public final native int getCurrentObjectVersion() /*-{
            return this["currentObjectVersion"];
        }-*/;

        public final native RecoverFromDroppedTangoInvalidationResponseImpl setCurrentObjectVersion(int currentObjectVersion) /*-{
            this["currentObjectVersion"] = currentObjectVersion;
            return this;
        }-*/;

        public final native boolean hasCurrentObjectVersion() /*-{
            return this.hasOwnProperty("currentObjectVersion");
        }-*/;

        public static native RecoverFromDroppedTangoInvalidationResponseImpl make() /*-{
            return {
                _type: 81
            };
        }-*/;
    }


    public static class RecoverFromMissedDocOpsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.RecoverFromMissedDocOps {
        protected RecoverFromMissedDocOpsImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native RecoverFromMissedDocOpsImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native RecoverFromMissedDocOpsImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native RecoverFromMissedDocOpsImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getDocOps2() /*-{
            return this["docOps2"];
        }-*/;

        public final native RecoverFromMissedDocOpsImpl setDocOps2(com.codenvy.ide.json.shared.JsonArray<java.lang.String> docOps2) /*-{
            this["docOps2"] = docOps2;
            return this;
        }-*/;

        public final native boolean hasDocOps2() /*-{
            return this.hasOwnProperty("docOps2");
        }-*/;

        @Override
        public final native int getCurrentCcRevision() /*-{
            return this["currentCcRevision"];
        }-*/;

        public final native RecoverFromMissedDocOpsImpl setCurrentCcRevision(int currentCcRevision) /*-{
            this["currentCcRevision"] = currentCcRevision;
            return this;
        }-*/;

        public final native boolean hasCurrentCcRevision() /*-{
            return this.hasOwnProperty("currentCcRevision");
        }-*/;

        public static native RecoverFromMissedDocOpsImpl make() /*-{
            return {
                _type: 82
            };
        }-*/;
    }


    public static class RecoverFromMissedDocOpsResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.RecoverFromMissedDocOpsResponse {
        protected RecoverFromMissedDocOpsResponseImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ServerToClientDocOp> getDocOps() /*-{
            return this["docOps"];
        }-*/;

        public final native RecoverFromMissedDocOpsResponseImpl setDocOps(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ServerToClientDocOp> docOps) /*-{
            this["docOps"] = docOps;
            return this;
        }-*/;

        public final native boolean hasDocOps() /*-{
            return this.hasOwnProperty("docOps");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native RecoverFromMissedDocOpsResponseImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        public static native RecoverFromMissedDocOpsResponseImpl make() /*-{
            return {
                _type: 83
            };
        }-*/;
    }


    public static class RefreshWorkspaceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.RefreshWorkspace {
        protected RefreshWorkspaceImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native RefreshWorkspaceImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getBasePath() /*-{
            return this["basePath"];
        }-*/;

        public final native RefreshWorkspaceImpl setBasePath(java.lang.String basePath) /*-{
            this["basePath"] = basePath;
            return this;
        }-*/;

        public final native boolean hasBasePath() /*-{
            return this.hasOwnProperty("basePath");
        }-*/;

        public static native RefreshWorkspaceImpl make() /*-{
            return {
                _type: 84
            };
        }-*/;
    }


    public static class RequestProjectMembershipImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.RequestProjectMembership {
        protected RequestProjectMembershipImpl() {
        }

        @Override
        public final native java.lang.String projectId() /*-{
            return this["projectId"];
        }-*/;

        public final native RequestProjectMembershipImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native RequestProjectMembershipImpl make() /*-{
            return {
                _type: 85
            };
        }-*/;
    }


    public static class ResolveConflictChunkImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ResolveConflictChunk {
        protected ResolveConflictChunkImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native ResolveConflictChunkImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native ResolveConflictChunkImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native int getConflictChunkIndex() /*-{
            return this["conflictChunkIndex"];
        }-*/;

        public final native ResolveConflictChunkImpl setConflictChunkIndex(int conflictChunkIndex) /*-{
            this["conflictChunkIndex"] = conflictChunkIndex;
            return this;
        }-*/;

        public final native boolean hasConflictChunkIndex() /*-{
            return this.hasOwnProperty("conflictChunkIndex");
        }-*/;

        @Override
        public final native com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() /*-{
            return this["conflictHandle"];
        }-*/;

        public final native ResolveConflictChunkImpl setConflictHandle(
                com.google.collide.dto.NodeConflictDto.ConflictHandle conflictHandle) /*-{
            this["conflictHandle"] = conflictHandle;
            return this;
        }-*/;

        public final native boolean hasConflictHandle() /*-{
            return this.hasOwnProperty("conflictHandle");
        }-*/;

        @Override
        public final native boolean isResolved() /*-{
            return this["isResolved"];
        }-*/;

        public final native ResolveConflictChunkImpl setIsResolved(boolean isResolved) /*-{
            this["isResolved"] = isResolved;
            return this;
        }-*/;

        public final native boolean hasIsResolved() /*-{
            return this.hasOwnProperty("isResolved");
        }-*/;

        public static native ResolveConflictChunkImpl make() /*-{
            return {
                _type: 86
            };
        }-*/;
    }


    public static class ResolveTreeConflictImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ResolveTreeConflict {
        protected ResolveTreeConflictImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native ResolveTreeConflictImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() /*-{
            return this["conflictHandle"];
        }-*/;

        public final native ResolveTreeConflictImpl setConflictHandle(
                com.google.collide.dto.NodeConflictDto.ConflictHandle conflictHandle) /*-{
            this["conflictHandle"] = conflictHandle;
            return this;
        }-*/;

        public final native boolean hasConflictHandle() /*-{
            return this.hasOwnProperty("conflictHandle");
        }-*/;

        @Override
        public final native java.lang.String getNewPath() /*-{
            return this["newPath"];
        }-*/;

        public final native ResolveTreeConflictImpl setNewPath(java.lang.String newPath) /*-{
            this["newPath"] = newPath;
            return this;
        }-*/;

        public final native boolean hasNewPath() /*-{
            return this.hasOwnProperty("newPath");
        }-*/;

        @Override
        public final native com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice getResolutionChoice() /*-{
            return @com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice::valueOf(Ljava/lang/String;)
                (this["resolutionChoice"]);
        }-*/;

        public final native ResolveTreeConflictImpl setResolutionChoice(
                com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice resolutionChoice) /*-{
            resolutionChoice = resolutionChoice.@com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice::toString()();
            this["resolutionChoice"] = resolutionChoice;
            return this;
        }-*/;

        public final native boolean hasResolutionChoice() /*-{
            return this.hasOwnProperty("resolutionChoice");
        }-*/;

        public static native ResolveTreeConflictImpl make() /*-{
            return {
                _type: 87
            };
        }-*/;
    }


    public static class ResolveTreeConflictResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ResolveTreeConflictResponse {
        protected ResolveTreeConflictResponseImpl() {
        }

        @Override
        public final native java.lang.String getRefreshPath() /*-{
            return this["refreshPath"];
        }-*/;

        public final native ResolveTreeConflictResponseImpl setRefreshPath(java.lang.String refreshPath) /*-{
            this["refreshPath"] = refreshPath;
            return this;
        }-*/;

        public final native boolean hasRefreshPath() /*-{
            return this.hasOwnProperty("refreshPath");
        }-*/;

        public static native ResolveTreeConflictResponseImpl make() /*-{
            return {
                _type: 88
            };
        }-*/;
    }


    public static class RetryAlreadyTransferredUploadImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.RetryAlreadyTransferredUpload {
        protected RetryAlreadyTransferredUploadImpl() {
        }

        @Override
        public final native java.lang.String getSessionId() /*-{
            return this["sessionId"];
        }-*/;

        public final native RetryAlreadyTransferredUploadImpl setSessionId(java.lang.String sessionId) /*-{
            this["sessionId"] = sessionId;
            return this;
        }-*/;

        public final native boolean hasSessionId() /*-{
            return this.hasOwnProperty("sessionId");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native RetryAlreadyTransferredUploadImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getUnzipWorkspacePaths() /*-{
            return this["unzipWorkspacePaths"];
        }-*/;

        public final native RetryAlreadyTransferredUploadImpl setUnzipWorkspacePaths(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> unzipWorkspacePaths) /*-{
            this["unzipWorkspacePaths"] = unzipWorkspacePaths;
            return this;
        }-*/;

        public final native boolean hasUnzipWorkspacePaths() /*-{
            return this.hasOwnProperty("unzipWorkspacePaths");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<java.lang.String> getFileWorkspacePaths() /*-{
            return this["fileWorkspacePaths"];
        }-*/;

        public final native RetryAlreadyTransferredUploadImpl setFileWorkspacePaths(
                com.codenvy.ide.json.shared.JsonArray<java.lang.String> fileWorkspacePaths) /*-{
            this["fileWorkspacePaths"] = fileWorkspacePaths;
            return this;
        }-*/;

        public final native boolean hasFileWorkspacePaths() /*-{
            return this.hasOwnProperty("fileWorkspacePaths");
        }-*/;

        public static native RetryAlreadyTransferredUploadImpl make() /*-{
            return {
                _type: 121
            };
        }-*/;
    }


    public static class RevisionImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.Revision {
        protected RevisionImpl() {
        }

        @Override
        public final native java.lang.String getRootId() /*-{
            return this["rootId"];
        }-*/;

        public final native RevisionImpl setRootId(java.lang.String rootId) /*-{
            this["rootId"] = rootId;
            return this;
        }-*/;

        public final native boolean hasRootId() /*-{
            return this.hasOwnProperty("rootId");
        }-*/;

        @Override
        public final native java.lang.String getNodeId() /*-{
            return this["nodeId"];
        }-*/;

        public final native RevisionImpl setNodeId(java.lang.String nodeId) /*-{
            this["nodeId"] = nodeId;
            return this;
        }-*/;

        public final native boolean hasNodeId() /*-{
            return this.hasOwnProperty("nodeId");
        }-*/;

        @Override
        public final native com.google.collide.dto.Revision.RevisionType getRevisionType() /*-{
            return @com.google.collide.dto.Revision.RevisionType::valueOf(Ljava/lang/String;)(this["revisionType"]);
        }-*/;

        public final native RevisionImpl setRevisionType(com.google.collide.dto.Revision.RevisionType revisionType) /*-{
            revisionType = revisionType.@com.google.collide.dto.Revision.RevisionType::toString()();
            this["revisionType"] = revisionType;
            return this;
        }-*/;

        public final native boolean hasRevisionType() /*-{
            return this.hasOwnProperty("revisionType");
        }-*/;

        @Override
        public final native boolean getHasUnresolvedConflicts() /*-{
            return this["hasUnresolvedConflicts"];
        }-*/;

        public final native RevisionImpl setHasUnresolvedConflicts(boolean hasUnresolvedConflicts) /*-{
            this["hasUnresolvedConflicts"] = hasUnresolvedConflicts;
            return this;
        }-*/;

        public final native boolean hasHasUnresolvedConflicts() /*-{
            return this.hasOwnProperty("hasUnresolvedConflicts");
        }-*/;

        @Override
        public final native boolean getIsFinalResolution() /*-{
            return this["isFinalResolution"];
        }-*/;

        public final native RevisionImpl setIsFinalResolution(boolean isFinalResolution) /*-{
            this["isFinalResolution"] = isFinalResolution;
            return this;
        }-*/;

        public final native boolean hasIsFinalResolution() /*-{
            return this.hasOwnProperty("isFinalResolution");
        }-*/;

        @Override
        public final native int getPreviousNodesSkipped() /*-{
            return this["previousNodesSkipped"];
        }-*/;

        public final native RevisionImpl setPreviousNodesSkipped(int previousNodesSkipped) /*-{
            this["previousNodesSkipped"] = previousNodesSkipped;
            return this;
        }-*/;

        public final native boolean hasPreviousNodesSkipped() /*-{
            return this.hasOwnProperty("previousNodesSkipped");
        }-*/;

        @Override
        public final native java.lang.String getTimestamp() /*-{
            return this["timestamp"];
        }-*/;

        public final native RevisionImpl setTimestamp(java.lang.String timestamp) /*-{
            this["timestamp"] = timestamp;
            return this;
        }-*/;

        public final native boolean hasTimestamp() /*-{
            return this.hasOwnProperty("timestamp");
        }-*/;

        public static native RevisionImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class RunTargetImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.RunTarget {
        protected RunTargetImpl() {
        }

        @Override
        public final native com.google.collide.dto.RunTarget.RunMode getRunMode() /*-{
            return @com.google.collide.dto.RunTarget.RunMode::valueOf(Ljava/lang/String;)(this["runMode"]);
        }-*/;

        public final native RunTargetImpl setRunMode(com.google.collide.dto.RunTarget.RunMode runMode) /*-{
            runMode = runMode.@com.google.collide.dto.RunTarget.RunMode::toString()();
            this["runMode"] = runMode;
            return this;
        }-*/;

        public final native boolean hasRunMode() /*-{
            return this.hasOwnProperty("runMode");
        }-*/;

        @Override
        public final native java.lang.String getAlwaysRunFilename() /*-{
            return this["alwaysRunFilename"];
        }-*/;

        public final native RunTargetImpl setAlwaysRunFilename(java.lang.String alwaysRunFilename) /*-{
            this["alwaysRunFilename"] = alwaysRunFilename;
            return this;
        }-*/;

        public final native boolean hasAlwaysRunFilename() /*-{
            return this.hasOwnProperty("alwaysRunFilename");
        }-*/;

        @Override
        public final native java.lang.String getAlwaysRunUrlOrQuery() /*-{
            return this["alwaysRunUrlOrQuery"];
        }-*/;

        public final native RunTargetImpl setAlwaysRunUrlOrQuery(java.lang.String alwaysRunUrlOrQuery) /*-{
            this["alwaysRunUrlOrQuery"] = alwaysRunUrlOrQuery;
            return this;
        }-*/;

        public final native boolean hasAlwaysRunUrlOrQuery() /*-{
            return this.hasOwnProperty("alwaysRunUrlOrQuery");
        }-*/;

        public static native RunTargetImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class SearchImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl implements com.google.collide.dto.Search {
        protected SearchImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native SearchImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native int getPage() /*-{
            return this["page"];
        }-*/;

        public final native SearchImpl setPage(int page) /*-{
            this["page"] = page;
            return this;
        }-*/;

        public final native boolean hasPage() /*-{
            return this.hasOwnProperty("page");
        }-*/;

        @Override
        public final native java.lang.String getQuery() /*-{
            return this["query"];
        }-*/;

        public final native SearchImpl setQuery(java.lang.String query) /*-{
            this["query"] = query;
            return this;
        }-*/;

        public final native boolean hasQuery() /*-{
            return this.hasOwnProperty("query");
        }-*/;

        public static native SearchImpl make() /*-{
            return {
                _type: 89
            };
        }-*/;
    }


    public static class SearchResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SearchResponse {
        protected SearchResponseImpl() {
        }

        @Override
        public final native int getResultCount() /*-{
            return this["resultCount"];
        }-*/;

        public final native SearchResponseImpl setResultCount(int resultCount) /*-{
            this["resultCount"] = resultCount;
            return this;
        }-*/;

        public final native boolean hasResultCount() /*-{
            return this.hasOwnProperty("resultCount");
        }-*/;

        @Override
        public final native int getPageCount() /*-{
            return this["pageCount"];
        }-*/;

        public final native SearchResponseImpl setPageCount(int pageCount) /*-{
            this["pageCount"] = pageCount;
            return this;
        }-*/;

        public final native boolean hasPageCount() /*-{
            return this.hasOwnProperty("pageCount");
        }-*/;

        @Override
        public final native int getPage() /*-{
            return this["page"];
        }-*/;

        public final native SearchResponseImpl setPage(int page) /*-{
            this["page"] = page;
            return this;
        }-*/;

        public final native boolean hasPage() /*-{
            return this.hasOwnProperty("page");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.SearchResult> getResults() /*-{
            return this["results"];
        }-*/;

        public final native SearchResponseImpl setResults(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.SearchResult> results) /*-{
            this["results"] = results;
            return this;
        }-*/;

        public final native boolean hasResults() /*-{
            return this.hasOwnProperty("results");
        }-*/;

        public static native SearchResponseImpl make() /*-{
            return {
                _type: 90
            };
        }-*/;
    }


    public static class SearchResultImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.SearchResult {
        protected SearchResultImpl() {
        }

        @Override
        public final native java.lang.String getTitle() /*-{
            return this["title"];
        }-*/;

        public final native SearchResultImpl setTitle(java.lang.String title) /*-{
            this["title"] = title;
            return this;
        }-*/;

        public final native boolean hasTitle() /*-{
            return this.hasOwnProperty("title");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.Snippet> getSnippets() /*-{
            return this["snippets"];
        }-*/;

        public final native SearchResultImpl setSnippets(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.Snippet> snippets) /*-{
            this["snippets"] = snippets;
            return this;
        }-*/;

        public final native boolean hasSnippets() /*-{
            return this.hasOwnProperty("snippets");
        }-*/;

        @Override
        public final native java.lang.String getUrl() /*-{
            return this["url"];
        }-*/;

        public final native SearchResultImpl setUrl(java.lang.String url) /*-{
            this["url"] = url;
            return this;
        }-*/;

        public final native boolean hasUrl() /*-{
            return this.hasOwnProperty("url");
        }-*/;

        public static native SearchResultImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ServerToClientDocOpImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ServerToClientDocOp {
        protected ServerToClientDocOpImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native ServerToClientDocOpImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native ServerToClientDocOpImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native ServerToClientDocOpImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native com.google.collide.dto.DocumentSelection getSelection() /*-{
            return this["selection"];
        }-*/;

        public final native ServerToClientDocOpImpl setSelection(com.google.collide.dto.DocumentSelection selection) /*-{
            this["selection"] = selection;
            return this;
        }-*/;

        public final native boolean hasSelection() /*-{
            return this.hasOwnProperty("selection");
        }-*/;

        @Override
        public final native java.lang.String getFilePath() /*-{
            return this["filePath"];
        }-*/;

        public final native ServerToClientDocOpImpl setFilePath(java.lang.String filePath) /*-{
            this["filePath"] = filePath;
            return this;
        }-*/;

        public final native boolean hasFilePath() /*-{
            return this.hasOwnProperty("filePath");
        }-*/;

        @Override
        public final native com.google.collide.dto.DocOp getDocOp2() /*-{
            return this["docOp2"];
        }-*/;

        public final native ServerToClientDocOpImpl setDocOp2(com.google.collide.dto.DocOp docOp2) /*-{
            this["docOp2"] = docOp2;
            return this;
        }-*/;

        public final native boolean hasDocOp2() /*-{
            return this.hasOwnProperty("docOp2");
        }-*/;

        @Override
        public final native int getAppliedCcRevision() /*-{
            return this["appliedCcRevision"];
        }-*/;

        public final native ServerToClientDocOpImpl setAppliedCcRevision(int appliedCcRevision) /*-{
            this["appliedCcRevision"] = appliedCcRevision;
            return this;
        }-*/;

        public final native boolean hasAppliedCcRevision() /*-{
            return this.hasOwnProperty("appliedCcRevision");
        }-*/;

        public static native ServerToClientDocOpImpl make() /*-{
            return {
                _type: 92
            };
        }-*/;
    }


    public static class ServerToClientDocOpsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ServerToClientDocOps {
        protected ServerToClientDocOpsImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ServerToClientDocOp> getDocOps() /*-{
            return this["docOps"];
        }-*/;

        public final native ServerToClientDocOpsImpl setDocOps(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.ServerToClientDocOp> docOps) /*-{
            this["docOps"] = docOps;
            return this;
        }-*/;

        public final native boolean hasDocOps() /*-{
            return this.hasOwnProperty("docOps");
        }-*/;

        public static native ServerToClientDocOpsImpl make() /*-{
            return {
                _type: 93
            };
        }-*/;
    }


    public static class SetActiveProjectImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SetActiveProject {
        protected SetActiveProjectImpl() {
        }

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native SetActiveProjectImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native SetActiveProjectImpl make() /*-{
            return {
                _type: 94
            };
        }-*/;
    }


    public static class SetProjectHiddenImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SetProjectHidden {
        protected SetProjectHiddenImpl() {
        }

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native SetProjectHiddenImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        @Override
        public final native boolean isHidden() /*-{
            return this["isHidden"];
        }-*/;

        public final native SetProjectHiddenImpl setIsHidden(boolean isHidden) /*-{
            this["isHidden"] = isHidden;
            return this;
        }-*/;

        public final native boolean hasIsHidden() /*-{
            return this.hasOwnProperty("isHidden");
        }-*/;

        public static native SetProjectHiddenImpl make() /*-{
            return {
                _type: 95
            };
        }-*/;
    }


    public static class SetProjectRoleImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SetProjectRole {
        protected SetProjectRoleImpl() {
        }

        @Override
        public final native com.google.collide.dto.ChangeRoleInfo getChangeRoleInfo() /*-{
            return this["changeRoleInfo"];
        }-*/;

        public final native SetProjectRoleImpl setChangeRoleInfo(com.google.collide.dto.ChangeRoleInfo changeRoleInfo) /*-{
            this["changeRoleInfo"] = changeRoleInfo;
            return this;
        }-*/;

        public final native boolean hasChangeRoleInfo() /*-{
            return this.hasOwnProperty("changeRoleInfo");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native SetProjectRoleImpl setProjectId(java.lang.String projectId) /*-{
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

        public final native SetProjectRoleImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native SetProjectRoleImpl make() /*-{
            return {
                _type: 96
            };
        }-*/;
    }


    public static class SetRoleResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SetRoleResponse {
        protected SetRoleResponseImpl() {
        }

        @Override
        public final native com.google.collide.dto.UserDetailsWithRole getUpdatedUserDetails() /*-{
            return this["updatedUserDetails"];
        }-*/;

        public final native SetRoleResponseImpl setUpdatedUserDetails(com.google.collide.dto.UserDetailsWithRole updatedUserDetails) /*-{
            this["updatedUserDetails"] = updatedUserDetails;
            return this;
        }-*/;

        public final native boolean hasUpdatedUserDetails() /*-{
            return this.hasOwnProperty("updatedUserDetails");
        }-*/;

        public static native SetRoleResponseImpl make() /*-{
            return {
                _type: 97
            };
        }-*/;
    }


    public static class SetStagingServerAppIdImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SetStagingServerAppId {
        protected SetStagingServerAppIdImpl() {
        }

        @Override
        public final native java.lang.String getStagingServerAppId() /*-{
            return this["stagingServerAppId"];
        }-*/;

        public final native SetStagingServerAppIdImpl setStagingServerAppId(java.lang.String stagingServerAppId) /*-{
            this["stagingServerAppId"] = stagingServerAppId;
            return this;
        }-*/;

        public final native boolean hasStagingServerAppId() /*-{
            return this.hasOwnProperty("stagingServerAppId");
        }-*/;

        public static native SetStagingServerAppIdImpl make() /*-{
            return {
                _type: 98
            };
        }-*/;
    }


    public static class SetWorkspaceArchiveStateImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SetWorkspaceArchiveState {
        protected SetWorkspaceArchiveStateImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native SetWorkspaceArchiveStateImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native SetWorkspaceArchiveStateImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        @Override
        public final native boolean archive() /*-{
            return this["archive"];
        }-*/;

        public final native SetWorkspaceArchiveStateImpl setArchive(boolean archive) /*-{
            this["archive"] = archive;
            return this;
        }-*/;

        public final native boolean hasArchive() /*-{
            return this.hasOwnProperty("archive");
        }-*/;

        public static native SetWorkspaceArchiveStateImpl make() /*-{
            return {
                _type: 99
            };
        }-*/;
    }


    public static class SetWorkspaceArchiveStateResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SetWorkspaceArchiveStateResponse {
        protected SetWorkspaceArchiveStateResponseImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native SetWorkspaceArchiveStateResponseImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getArchivedTime() /*-{
            return this["archivedTime"];
        }-*/;

        public final native SetWorkspaceArchiveStateResponseImpl setArchivedTime(java.lang.String archivedTime) /*-{
            this["archivedTime"] = archivedTime;
            return this;
        }-*/;

        public final native boolean hasArchivedTime() /*-{
            return this.hasOwnProperty("archivedTime");
        }-*/;

        public static native SetWorkspaceArchiveStateResponseImpl make() /*-{
            return {
                _type: 100
            };
        }-*/;
    }


    public static class SetWorkspaceRoleImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SetWorkspaceRole {
        protected SetWorkspaceRoleImpl() {
        }

        @Override
        public final native com.google.collide.dto.ChangeRoleInfo getChangeRoleInfo() /*-{
            return this["changeRoleInfo"];
        }-*/;

        public final native SetWorkspaceRoleImpl setChangeRoleInfo(com.google.collide.dto.ChangeRoleInfo changeRoleInfo) /*-{
            this["changeRoleInfo"] = changeRoleInfo;
            return this;
        }-*/;

        public final native boolean hasChangeRoleInfo() /*-{
            return this.hasOwnProperty("changeRoleInfo");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native SetWorkspaceRoleImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native SetWorkspaceRoleImpl setProjectId(java.lang.String projectId) /*-{
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

        public final native SetWorkspaceRoleImpl setUserId(java.lang.String userId) /*-{
            this["userId"] = userId;
            return this;
        }-*/;

        public final native boolean hasUserId() /*-{
            return this.hasOwnProperty("userId");
        }-*/;

        public static native SetWorkspaceRoleImpl make() /*-{
            return {
                _type: 101
            };
        }-*/;
    }


    public static class SnippetImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.Snippet {
        protected SnippetImpl() {
        }

        @Override
        public final native java.lang.String getSnippetText() /*-{
            return this["snippetText"];
        }-*/;

        public final native SnippetImpl setSnippetText(java.lang.String snippetText) /*-{
            this["snippetText"] = snippetText;
            return this;
        }-*/;

        public final native boolean hasSnippetText() /*-{
            return this.hasOwnProperty("snippetText");
        }-*/;

        @Override
        public final native int getLineNumber() /*-{
            return this["lineNumber"];
        }-*/;

        public final native SnippetImpl setLineNumber(int lineNumber) /*-{
            this["lineNumber"] = lineNumber;
            return this;
        }-*/;

        public final native boolean hasLineNumber() /*-{
            return this.hasOwnProperty("lineNumber");
        }-*/;

        public static native SnippetImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class StackTraceElementDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.StackTraceElementDto {
        protected StackTraceElementDtoImpl() {
        }

        @Override
        public final native java.lang.String getClassName() /*-{
            return this["className"];
        }-*/;

        public final native StackTraceElementDtoImpl setClassName(java.lang.String className) /*-{
            this["className"] = className;
            return this;
        }-*/;

        public final native boolean hasClassName() /*-{
            return this.hasOwnProperty("className");
        }-*/;

        @Override
        public final native java.lang.String getFileName() /*-{
            return this["fileName"];
        }-*/;

        public final native StackTraceElementDtoImpl setFileName(java.lang.String fileName) /*-{
            this["fileName"] = fileName;
            return this;
        }-*/;

        public final native boolean hasFileName() /*-{
            return this.hasOwnProperty("fileName");
        }-*/;

        @Override
        public final native int getLineNumber() /*-{
            return this["lineNumber"];
        }-*/;

        public final native StackTraceElementDtoImpl setLineNumber(int lineNumber) /*-{
            this["lineNumber"] = lineNumber;
            return this;
        }-*/;

        public final native boolean hasLineNumber() /*-{
            return this.hasOwnProperty("lineNumber");
        }-*/;

        @Override
        public final native java.lang.String getMethodName() /*-{
            return this["methodName"];
        }-*/;

        public final native StackTraceElementDtoImpl setMethodName(java.lang.String methodName) /*-{
            this["methodName"] = methodName;
            return this;
        }-*/;

        public final native boolean hasMethodName() /*-{
            return this.hasOwnProperty("methodName");
        }-*/;

        public static native StackTraceElementDtoImpl make() /*-{
            return {
                _type: 103
            };
        }-*/;
    }


    public static class SubmitImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl implements com.google.collide.dto.Submit {
        protected SubmitImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceName() /*-{
            return this["workspaceName"];
        }-*/;

        public final native SubmitImpl setWorkspaceName(java.lang.String workspaceName) /*-{
            this["workspaceName"] = workspaceName;
            return this;
        }-*/;

        public final native boolean hasWorkspaceName() /*-{
            return this.hasOwnProperty("workspaceName");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native SubmitImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native SubmitImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        @Override
        public final native java.lang.String getWorkspaceDescription() /*-{
            return this["workspaceDescription"];
        }-*/;

        public final native SubmitImpl setWorkspaceDescription(java.lang.String workspaceDescription) /*-{
            this["workspaceDescription"] = workspaceDescription;
            return this;
        }-*/;

        public final native boolean hasWorkspaceDescription() /*-{
            return this.hasOwnProperty("workspaceDescription");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native SubmitImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native SubmitImpl make() /*-{
            return {
                _type: 104
            };
        }-*/;
    }


    public static class SubmitResponseImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SubmitResponse {
        protected SubmitResponseImpl() {
        }

        @Override
        public final native java.lang.String getSubmissionTime() /*-{
            return this["submissionTime"];
        }-*/;

        public final native SubmitResponseImpl setSubmissionTime(java.lang.String submissionTime) /*-{
            this["submissionTime"] = submissionTime;
            return this;
        }-*/;

        public final native boolean hasSubmissionTime() /*-{
            return this.hasOwnProperty("submissionTime");
        }-*/;

        @Override
        public final native com.google.collide.dto.UserDetails getSubmitter() /*-{
            return this["submitter"];
        }-*/;

        public final native SubmitResponseImpl setSubmitter(com.google.collide.dto.UserDetails submitter) /*-{
            this["submitter"] = submitter;
            return this;
        }-*/;

        public final native boolean hasSubmitter() /*-{
            return this.hasOwnProperty("submitter");
        }-*/;

        public static native SubmitResponseImpl make() /*-{
            return {
                _type: 105
            };
        }-*/;
    }


    public static class SubmittedWorkspaceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.SubmittedWorkspace {
        protected SubmittedWorkspaceImpl() {
        }

        public static native SubmittedWorkspaceImpl make() /*-{
            return {
                _type: 106
            };
        }-*/;
    }


    public static class SyncImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl implements com.google.collide.dto.Sync {
        protected SyncImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native SyncImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native SyncImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        public static native SyncImpl make() /*-{
            return {
                _type: 107
            };
        }-*/;
    }


    public static class SyncConflictsImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.SyncConflicts {
        protected SyncConflictsImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto> getConflicts() /*-{
            return this["conflicts"];
        }-*/;

        public final native SyncConflictsImpl setConflicts(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto> conflicts) /*-{
            this["conflicts"] = conflicts;
            return this;
        }-*/;

        public final native boolean hasConflicts() /*-{
            return this.hasOwnProperty("conflicts");
        }-*/;

        @Override
        public final native java.lang.String getNextTangoVersion() /*-{
            return this["nextTangoVersion"];
        }-*/;

        public final native SyncConflictsImpl setNextTangoVersion(java.lang.String nextTangoVersion) /*-{
            this["nextTangoVersion"] = nextTangoVersion;
            return this;
        }-*/;

        public final native boolean hasNextTangoVersion() /*-{
            return this.hasOwnProperty("nextTangoVersion");
        }-*/;

        public static native SyncConflictsImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class ThrowableDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.ThrowableDto {
        protected ThrowableDtoImpl() {
        }

        @Override
        public final native java.lang.String className() /*-{
            return this["className"];
        }-*/;

        public final native ThrowableDtoImpl setClassName(java.lang.String className) /*-{
            this["className"] = className;
            return this;
        }-*/;

        public final native boolean hasClassName() /*-{
            return this.hasOwnProperty("className");
        }-*/;

        @Override
        public final native com.google.collide.dto.ThrowableDto getCause() /*-{
            return this["cause"];
        }-*/;

        public final native ThrowableDtoImpl setCause(com.google.collide.dto.ThrowableDto cause) /*-{
            this["cause"] = cause;
            return this;
        }-*/;

        public final native boolean hasCause() /*-{
            return this.hasOwnProperty("cause");
        }-*/;

        @Override
        public final native java.lang.String getMessage() /*-{
            return this["message"];
        }-*/;

        public final native ThrowableDtoImpl setMessage(java.lang.String message) /*-{
            this["message"] = message;
            return this;
        }-*/;

        public final native boolean hasMessage() /*-{
            return this.hasOwnProperty("message");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.StackTraceElementDto> getStackTrace() /*-{
            return this["stackTrace"];
        }-*/;

        public final native ThrowableDtoImpl setStackTrace(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.StackTraceElementDto> stackTrace) /*-{
            this["stackTrace"] = stackTrace;
            return this;
        }-*/;

        public final native boolean hasStackTrace() /*-{
            return this.hasOwnProperty("stackTrace");
        }-*/;

        public static native ThrowableDtoImpl make() /*-{
            return {
                _type: 108
            };
        }-*/;
    }


    public static class TreeNodeInfoImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.TreeNodeInfo {
        protected TreeNodeInfoImpl() {
        }

        @Override
        public final native java.lang.String getFileEditSessionKey() /*-{
            return this["fileEditSessionKey"];
        }-*/;

        public final native TreeNodeInfoImpl setFileEditSessionKey(java.lang.String fileEditSessionKey) /*-{
            this["fileEditSessionKey"] = fileEditSessionKey;
            return this;
        }-*/;

        public final native boolean hasFileEditSessionKey() /*-{
            return this.hasOwnProperty("fileEditSessionKey");
        }-*/;

        @Override
        public final native int getNodeType() /*-{
            return this["nodeType"];
        }-*/;

        public final native TreeNodeInfoImpl setNodeType(int nodeType) /*-{
            this["nodeType"] = nodeType;
            return this;
        }-*/;

        public final native boolean hasNodeType() /*-{
            return this.hasOwnProperty("nodeType");
        }-*/;

        @Override
        public final native java.lang.String getName() /*-{
            return this["name"];
        }-*/;

        public final native TreeNodeInfoImpl setName(java.lang.String name) /*-{
            this["name"] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty("name");
        }-*/;

        public static native TreeNodeInfoImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class TypeAssociationImpl extends CodeBlockAssociationImpl implements com.google.collide.dto.TypeAssociation {
        protected TypeAssociationImpl() {
        }

        public static native TypeAssociationImpl make() /*-{
            return [];
        }-*/;
    }


    public static class UndoLastSyncImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.UndoLastSync {
        protected UndoLastSyncImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native UndoLastSyncImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native java.lang.String getClientId() /*-{
            return this["clientId"];
        }-*/;

        public final native UndoLastSyncImpl setClientId(java.lang.String clientId) /*-{
            this["clientId"] = clientId;
            return this;
        }-*/;

        public final native boolean hasClientId() /*-{
            return this.hasOwnProperty("clientId");
        }-*/;

        public static native UndoLastSyncImpl make() /*-{
            return {
                _type: 110
            };
        }-*/;
    }


    public static class UpdateProjectImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.UpdateProject {
        protected UpdateProjectImpl() {
        }

        @Override
        public final native java.lang.String getSummary() /*-{
            return this["summary"];
        }-*/;

        public final native UpdateProjectImpl setSummary(java.lang.String summary) /*-{
            this["summary"] = summary;
            return this;
        }-*/;

        public final native boolean hasSummary() /*-{
            return this.hasOwnProperty("summary");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native UpdateProjectImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        @Override
        public final native java.lang.String getName() /*-{
            return this["name"];
        }-*/;

        public final native UpdateProjectImpl setName(java.lang.String name) /*-{
            this["name"] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty("name");
        }-*/;

        public static native UpdateProjectImpl make() /*-{
            return {
                _type: 111
            };
        }-*/;
    }


    public static class UpdateUserWorkspaceMetadataImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.UpdateUserWorkspaceMetadata {
        protected UpdateUserWorkspaceMetadataImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native UpdateUserWorkspaceMetadataImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.google.collide.dto.GetWorkspaceMetaDataResponse getUserWorkspaceMetadata() /*-{
            return this["userWorkspaceMetadata"];
        }-*/;

        public final native UpdateUserWorkspaceMetadataImpl setUserWorkspaceMetadata(
                com.google.collide.dto.GetWorkspaceMetaDataResponse userWorkspaceMetadata) /*-{
            this["userWorkspaceMetadata"] = userWorkspaceMetadata;
            return this;
        }-*/;

        public final native boolean hasUserWorkspaceMetadata() /*-{
            return this.hasOwnProperty("userWorkspaceMetadata");
        }-*/;

        public static native UpdateUserWorkspaceMetadataImpl make() /*-{
            return {
                _type: 112
            };
        }-*/;
    }


    public static class UpdateWorkspaceImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.UpdateWorkspace {
        protected UpdateWorkspaceImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native UpdateWorkspaceImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.google.collide.dto.WorkspaceInfo getWorkspaceUpdates() /*-{
            return this["workspaceUpdates"];
        }-*/;

        public final native UpdateWorkspaceImpl setWorkspaceUpdates(com.google.collide.dto.WorkspaceInfo workspaceUpdates) /*-{
            this["workspaceUpdates"] = workspaceUpdates;
            return this;
        }-*/;

        public final native boolean hasWorkspaceUpdates() /*-{
            return this.hasOwnProperty("workspaceUpdates");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native UpdateWorkspaceImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native UpdateWorkspaceImpl make() /*-{
            return {
                _type: 113
            };
        }-*/;
    }


    public static class UpdateWorkspaceRunTargetsImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.UpdateWorkspaceRunTargets {
        protected UpdateWorkspaceRunTargetsImpl() {
        }

        @Override
        public final native java.lang.String getWorkspaceId() /*-{
            return this["workspaceId"];
        }-*/;

        public final native UpdateWorkspaceRunTargetsImpl setWorkspaceId(java.lang.String workspaceId) /*-{
            this["workspaceId"] = workspaceId;
            return this;
        }-*/;

        public final native boolean hasWorkspaceId() /*-{
            return this.hasOwnProperty("workspaceId");
        }-*/;

        @Override
        public final native com.google.collide.dto.RunTarget getRunTarget() /*-{
            return this["runTarget"];
        }-*/;

        public final native UpdateWorkspaceRunTargetsImpl setRunTarget(com.google.collide.dto.RunTarget runTarget) /*-{
            this["runTarget"] = runTarget;
            return this;
        }-*/;

        public final native boolean hasRunTarget() /*-{
            return this.hasOwnProperty("runTarget");
        }-*/;

        @Override
        public final native java.lang.String getProjectId() /*-{
            return this["projectId"];
        }-*/;

        public final native UpdateWorkspaceRunTargetsImpl setProjectId(java.lang.String projectId) /*-{
            this["projectId"] = projectId;
            return this;
        }-*/;

        public final native boolean hasProjectId() /*-{
            return this.hasOwnProperty("projectId");
        }-*/;

        public static native UpdateWorkspaceRunTargetsImpl make() /*-{
            return {
                _type: 114
            };
        }-*/;
    }


    public static class UserDetailsImpl extends com.codenvy.ide.json.client.Jso implements com.google.collide.dto.UserDetails {
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

        public static native UserDetailsImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class UserDetailsWithRoleImpl extends UserDetailsImpl implements com.google.collide.dto.UserDetailsWithRole {
        protected UserDetailsWithRoleImpl() {
        }

        @Override
        public final native boolean isCreator() /*-{
            return this["isCreator"];
        }-*/;

        public final native UserDetailsWithRoleImpl setIsCreator(boolean isCreator) /*-{
            this["isCreator"] = isCreator;
            return this;
        }-*/;

        public final native boolean hasIsCreator() /*-{
            return this.hasOwnProperty("isCreator");
        }-*/;

        @Override
        public final native com.google.collide.dto.Role getRole() /*-{
            return @com.google.collide.dto.Role::valueOf(Ljava/lang/String;)(this["role"]);
        }-*/;

        public final native UserDetailsWithRoleImpl setRole(com.google.collide.dto.Role role) /*-{
            role = role.@com.google.collide.dto.Role::toString()();
            this["role"] = role;
            return this;
        }-*/;

        public final native boolean hasRole() /*-{
            return this.hasOwnProperty("role");
        }-*/;

        public static native UserDetailsWithRoleImpl make() /*-{
            return {

            };
        }-*/;
    }


    public static class UserLogInDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.UserLogInDto {
        protected UserLogInDtoImpl() {
        }

        @Override
        public final native com.google.collide.dto.ParticipantUserDetails getParticipant() /*-{
            return this["participant"];
        }-*/;

        public final native UserLogInDtoImpl setParticipant(com.google.collide.dto.ParticipantUserDetails participant) /*-{
            this["participant"] = participant;
            return this;
        }-*/;

        public final native boolean hasParticipant() /*-{
            return this.hasOwnProperty("participant");
        }-*/;

        public static native UserLogInDtoImpl make() /*-{
            return {
                _type: 131
            };
        }-*/;
    }


    public static class UserLogOutDtoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.UserLogOutDto {
        protected UserLogOutDtoImpl() {
        }

        @Override
        public final native com.google.collide.dto.Participant getParticipant() /*-{
            return this["participant"];
        }-*/;

        public final native UserLogOutDtoImpl setParticipant(com.google.collide.dto.Participant participant) /*-{
            this["participant"] = participant;
            return this;
        }-*/;

        public final native boolean hasParticipant() /*-{
            return this.hasOwnProperty("participant");
        }-*/;

        public static native UserLogOutDtoImpl make() /*-{
            return {
                _type: 132
            };
        }-*/;
    }


    public static class WorkspaceInfoImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.WorkspaceInfo {
        protected WorkspaceInfoImpl() {
        }

        @Override
        public final native java.lang.String getParentId() /*-{
            return this["parentId"];
        }-*/;

        public final native WorkspaceInfoImpl setParentId(java.lang.String parentId) /*-{
            this["parentId"] = parentId;
            return this;
        }-*/;

        public final native boolean hasParentId() /*-{
            return this.hasOwnProperty("parentId");
        }-*/;

        @Override
        public final native java.lang.String getOwningProjectId() /*-{
            return this["owningProjectId"];
        }-*/;

        public final native WorkspaceInfoImpl setOwningProjectId(java.lang.String owningProjectId) /*-{
            this["owningProjectId"] = owningProjectId;
            return this;
        }-*/;

        public final native boolean hasOwningProjectId() /*-{
            return this.hasOwnProperty("owningProjectId");
        }-*/;

        @Override
        public final native java.lang.String getCreatedTime() /*-{
            return this["createdTime"];
        }-*/;

        public final native WorkspaceInfoImpl setCreatedTime(java.lang.String createdTime) /*-{
            this["createdTime"] = createdTime;
            return this;
        }-*/;

        public final native boolean hasCreatedTime() /*-{
            return this.hasOwnProperty("createdTime");
        }-*/;

        @Override
        public final native com.google.collide.dto.Visibility getVisibility() /*-{
            return @com.google.collide.dto.Visibility::valueOf(Ljava/lang/String;)(this["visibility"]);
        }-*/;

        public final native WorkspaceInfoImpl setVisibility(com.google.collide.dto.Visibility visibility) /*-{
            visibility = visibility.@com.google.collide.dto.Visibility::toString()();
            this["visibility"] = visibility;
            return this;
        }-*/;

        public final native boolean hasVisibility() /*-{
            return this.hasOwnProperty("visibility");
        }-*/;

        @Override
        public final native java.lang.String getArchivedTime() /*-{
            return this["archivedTime"];
        }-*/;

        public final native WorkspaceInfoImpl setArchivedTime(java.lang.String archivedTime) /*-{
            this["archivedTime"] = archivedTime;
            return this;
        }-*/;

        public final native boolean hasArchivedTime() /*-{
            return this.hasOwnProperty("archivedTime");
        }-*/;

        @Override
        public final native java.lang.String getSubmissionTime() /*-{
            return this["submissionTime"];
        }-*/;

        public final native WorkspaceInfoImpl setSubmissionTime(java.lang.String submissionTime) /*-{
            this["submissionTime"] = submissionTime;
            return this;
        }-*/;

        public final native boolean hasSubmissionTime() /*-{
            return this.hasOwnProperty("submissionTime");
        }-*/;

        @Override
        public final native java.lang.String getSortTime() /*-{
            return this["sortTime"];
        }-*/;

        public final native WorkspaceInfoImpl setSortTime(java.lang.String sortTime) /*-{
            this["sortTime"] = sortTime;
            return this;
        }-*/;

        public final native boolean hasSortTime() /*-{
            return this.hasOwnProperty("sortTime");
        }-*/;

        @Override
        public final native com.google.collide.dto.RunTarget getRunTarget() /*-{
            return this["runTarget"];
        }-*/;

        public final native WorkspaceInfoImpl setRunTarget(com.google.collide.dto.RunTarget runTarget) /*-{
            this["runTarget"] = runTarget;
            return this;
        }-*/;

        public final native boolean hasRunTarget() /*-{
            return this.hasOwnProperty("runTarget");
        }-*/;

        @Override
        public final native com.google.collide.dto.UserDetails getSubmitter() /*-{
            return this["submitter"];
        }-*/;

        public final native WorkspaceInfoImpl setSubmitter(com.google.collide.dto.UserDetails submitter) /*-{
            this["submitter"] = submitter;
            return this;
        }-*/;

        public final native boolean hasSubmitter() /*-{
            return this.hasOwnProperty("submitter");
        }-*/;

        @Override
        public final native com.google.collide.dto.WorkspaceInfo.WorkspaceType getWorkspaceType() /*-{
            return @com.google.collide.dto.WorkspaceInfo.WorkspaceType::valueOf(Ljava/lang/String;)(this["workspaceType"]);
        }-*/;

        public final native WorkspaceInfoImpl setWorkspaceType(com.google.collide.dto.WorkspaceInfo.WorkspaceType workspaceType) /*-{
            workspaceType = workspaceType.@com.google.collide.dto.WorkspaceInfo.WorkspaceType::toString()();
            this["workspaceType"] = workspaceType;
            return this;
        }-*/;

        public final native boolean hasWorkspaceType() /*-{
            return this.hasOwnProperty("workspaceType");
        }-*/;

        @Override
        public final native com.google.collide.dto.Role getCurrentUserRoleForParent() /*-{
            return @com.google.collide.dto.Role::valueOf(Ljava/lang/String;)(this["currentUserRoleForParent"]);
        }-*/;

        public final native WorkspaceInfoImpl setCurrentUserRoleForParent(com.google.collide.dto.Role currentUserRoleForParent) /*-{
            currentUserRoleForParent = currentUserRoleForParent.@com.google.collide.dto.Role::toString()();
            this["currentUserRoleForParent"] = currentUserRoleForParent;
            return this;
        }-*/;

        public final native boolean hasCurrentUserRoleForParent() /*-{
            return this.hasOwnProperty("currentUserRoleForParent");
        }-*/;

        @Override
        public final native com.google.collide.dto.Role getCurrentUserRole() /*-{
            return @com.google.collide.dto.Role::valueOf(Ljava/lang/String;)(this["currentUserRole"]);
        }-*/;

        public final native WorkspaceInfoImpl setCurrentUserRole(com.google.collide.dto.Role currentUserRole) /*-{
            currentUserRole = currentUserRole.@com.google.collide.dto.Role::toString()();
            this["currentUserRole"] = currentUserRole;
            return this;
        }-*/;

        public final native boolean hasCurrentUserRole() /*-{
            return this.hasOwnProperty("currentUserRole");
        }-*/;

        @Override
        public final native java.lang.String getDescription() /*-{
            return this["description"];
        }-*/;

        public final native WorkspaceInfoImpl setDescription(java.lang.String description) /*-{
            this["description"] = description;
            return this;
        }-*/;

        public final native boolean hasDescription() /*-{
            return this.hasOwnProperty("description");
        }-*/;

        @Override
        public final native java.lang.String getName() /*-{
            return this["name"];
        }-*/;

        public final native WorkspaceInfoImpl setName(java.lang.String name) /*-{
            this["name"] = name;
            return this;
        }-*/;

        public final native boolean hasName() /*-{
            return this.hasOwnProperty("name");
        }-*/;

        @Override
        public final native java.lang.String getId() /*-{
            return this["id"];
        }-*/;

        public final native WorkspaceInfoImpl setId(java.lang.String id) /*-{
            this["id"] = id;
            return this;
        }-*/;

        public final native boolean hasId() /*-{
            return this.hasOwnProperty("id");
        }-*/;

        public static native WorkspaceInfoImpl make() /*-{
            return {
                _type: 116
            };
        }-*/;
    }


    public static class WorkspaceTreeUpdateImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.WorkspaceTreeUpdate {
        protected WorkspaceTreeUpdateImpl() {
        }

        @Override
        public final native java.lang.String getAuthorClientId() /*-{
            return this["authorClientId"];
        }-*/;

        public final native WorkspaceTreeUpdateImpl setAuthorClientId(java.lang.String authorClientId) /*-{
            this["authorClientId"] = authorClientId;
            return this;
        }-*/;

        public final native boolean hasAuthorClientId() /*-{
            return this.hasOwnProperty("authorClientId");
        }-*/;

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.Mutation> getMutations() /*-{
            return this["mutations"];
        }-*/;

        public final native WorkspaceTreeUpdateImpl setMutations(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.Mutation> mutations) /*-{
            this["mutations"] = mutations;
            return this;
        }-*/;

        public final native boolean hasMutations() /*-{
            return this.hasOwnProperty("mutations");
        }-*/;

        public static native WorkspaceTreeUpdateImpl make() /*-{
            return {
                _type: 117
            };
        }-*/;
    }


    public static class WorkspaceTreeUpdateBroadcastImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl
            implements com.google.collide.dto.WorkspaceTreeUpdateBroadcast {
        protected WorkspaceTreeUpdateBroadcastImpl() {
        }

        @Override
        public final native com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.Mutation> getMutations() /*-{
            return this["mutations"];
        }-*/;

        public final native WorkspaceTreeUpdateBroadcastImpl setMutations(
                com.codenvy.ide.json.shared.JsonArray<com.google.collide.dto.Mutation> mutations) /*-{
            this["mutations"] = mutations;
            return this;
        }-*/;

        public final native boolean hasMutations() /*-{
            return this.hasOwnProperty("mutations");
        }-*/;

        @Override
        public final native java.lang.String getNewTreeVersion() /*-{
            return this["newTreeVersion"];
        }-*/;

        public final native WorkspaceTreeUpdateBroadcastImpl setNewTreeVersion(java.lang.String newTreeVersion) /*-{
            this["newTreeVersion"] = newTreeVersion;
            return this;
        }-*/;

        public final native boolean hasNewTreeVersion() /*-{
            return this.hasOwnProperty("newTreeVersion");
        }-*/;

        public static native WorkspaceTreeUpdateBroadcastImpl make() /*-{
            return {
                _type: 118
            };
        }-*/;
    }

}