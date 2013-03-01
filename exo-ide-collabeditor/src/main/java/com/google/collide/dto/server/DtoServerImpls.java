/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */


// GENERATED SOURCE. DO NOT EDIT.
package com.google.collide.dto.server;

import org.exoplatform.ide.dtogen.server.JsonSerializable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.List;
import java.util.Map;


@SuppressWarnings({"unchecked", "cast"})
public class DtoServerImpls {

  private static final Gson gson = new GsonBuilder().serializeNulls().create();

  private  DtoServerImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "35435f2fd870c9a675e56301d0098a5208d4816f";

  public static class AddMembersResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.AddMembersResponse, JsonSerializable {

    private AddMembersResponseImpl() {
      super(1);
    }

    protected AddMembersResponseImpl(int type) {
      super(type);
    }

    public static AddMembersResponseImpl make() {
      return new AddMembersResponseImpl();
    }

    protected java.util.List<UserDetailsWithRoleImpl> newMembers;
    private boolean _hasNewMembers;
    protected java.util.List<java.lang.String> invalidEmails;
    private boolean _hasInvalidEmails;

    public boolean hasNewMembers() {
      return _hasNewMembers;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> getNewMembers() {
      ensureNewMembers();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(newMembers);
    }

    public AddMembersResponseImpl setNewMembers(java.util.List<UserDetailsWithRoleImpl> v) {
      _hasNewMembers = true;
      newMembers = v;
      return this;
    }

    public void addNewMembers(UserDetailsWithRoleImpl v) {
      ensureNewMembers();
      newMembers.add(v);
    }

    public void clearNewMembers() {
      ensureNewMembers();
      newMembers.clear();
    }

    private void ensureNewMembers() {
      if (!_hasNewMembers) {
        setNewMembers(newMembers != null ? newMembers : new java.util.ArrayList<UserDetailsWithRoleImpl>());
      }
    }

    public boolean hasInvalidEmails() {
      return _hasInvalidEmails;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getInvalidEmails() {
      ensureInvalidEmails();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(invalidEmails);
    }

    public AddMembersResponseImpl setInvalidEmails(java.util.List<java.lang.String> v) {
      _hasInvalidEmails = true;
      invalidEmails = v;
      return this;
    }

    public void addInvalidEmails(java.lang.String v) {
      ensureInvalidEmails();
      invalidEmails.add(v);
    }

    public void clearInvalidEmails() {
      ensureInvalidEmails();
      invalidEmails.clear();
    }

    private void ensureInvalidEmails() {
      if (!_hasInvalidEmails) {
        setInvalidEmails(invalidEmails != null ? invalidEmails : new java.util.ArrayList<java.lang.String>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof AddMembersResponseImpl)) {
        return false;
      }
      AddMembersResponseImpl other = (AddMembersResponseImpl) o;
      if (this._hasNewMembers != other._hasNewMembers) {
        return false;
      }
      if (this._hasNewMembers) {
        if (!this.newMembers.equals(other.newMembers)) {
          return false;
        }
      }
      if (this._hasInvalidEmails != other._hasInvalidEmails) {
        return false;
      }
      if (this._hasInvalidEmails) {
        if (!this.invalidEmails.equals(other.invalidEmails)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasNewMembers ? newMembers.hashCode() : 0);
      hash = hash * 31 + (_hasInvalidEmails ? invalidEmails.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray newMembersOut = new JsonArray();
      ensureNewMembers();
      for (UserDetailsWithRoleImpl newMembers_ : newMembers) {
        JsonElement newMembersOut_ = newMembers_ == null ? JsonNull.INSTANCE : newMembers_.toJsonElement();
        newMembersOut.add(newMembersOut_);
      }
      result.add("newMembers", newMembersOut);

      JsonArray invalidEmailsOut = new JsonArray();
      ensureInvalidEmails();
      for (java.lang.String invalidEmails_ : invalidEmails) {
        JsonElement invalidEmailsOut_ = (invalidEmails_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(invalidEmails_);
        invalidEmailsOut.add(invalidEmailsOut_);
      }
      result.add("invalidEmails", invalidEmailsOut);
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

    public static AddMembersResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      AddMembersResponseImpl dto = new AddMembersResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("newMembers")) {
        JsonElement newMembersIn = json.get("newMembers");
        java.util.ArrayList<UserDetailsWithRoleImpl> newMembersOut = null;
        if (newMembersIn != null && !newMembersIn.isJsonNull()) {
          newMembersOut = new java.util.ArrayList<UserDetailsWithRoleImpl>();
          java.util.Iterator<JsonElement> newMembersInIterator = newMembersIn.getAsJsonArray().iterator();
          while (newMembersInIterator.hasNext()) {
            JsonElement newMembersIn_ = newMembersInIterator.next();
            UserDetailsWithRoleImpl newMembersOut_ = UserDetailsWithRoleImpl.fromJsonElement(newMembersIn_);
            newMembersOut.add(newMembersOut_);
          }
        }
        dto.setNewMembers(newMembersOut);
      }

      if (json.has("invalidEmails")) {
        JsonElement invalidEmailsIn = json.get("invalidEmails");
        java.util.ArrayList<java.lang.String> invalidEmailsOut = null;
        if (invalidEmailsIn != null && !invalidEmailsIn.isJsonNull()) {
          invalidEmailsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> invalidEmailsInIterator = invalidEmailsIn.getAsJsonArray().iterator();
          while (invalidEmailsInIterator.hasNext()) {
            JsonElement invalidEmailsIn_ = invalidEmailsInIterator.next();
            java.lang.String invalidEmailsOut_ = gson.fromJson(invalidEmailsIn_, java.lang.String.class);
            invalidEmailsOut.add(invalidEmailsOut_);
          }
        }
        dto.setInvalidEmails(invalidEmailsOut);
      }

      return dto;
    }
    public static AddMembersResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class AddProjectMembersImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.AddProjectMembers, JsonSerializable {

    private AddProjectMembersImpl() {
      super(2);
    }

    protected AddProjectMembersImpl(int type) {
      super(type);
    }

    protected ChangeRoleInfoImpl changeRoleInfo;
    private boolean _hasChangeRoleInfo;
    protected java.lang.String userEmails;
    private boolean _hasUserEmails;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasChangeRoleInfo() {
      return _hasChangeRoleInfo;
    }

    @Override
    public com.google.collide.dto.ChangeRoleInfo getChangeRoleInfo() {
      return changeRoleInfo;
    }

    public AddProjectMembersImpl setChangeRoleInfo(ChangeRoleInfoImpl v) {
      _hasChangeRoleInfo = true;
      changeRoleInfo = v;
      return this;
    }

    public boolean hasUserEmails() {
      return _hasUserEmails;
    }

    @Override
    public java.lang.String getUserEmails() {
      return userEmails;
    }

    public AddProjectMembersImpl setUserEmails(java.lang.String v) {
      _hasUserEmails = true;
      userEmails = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public AddProjectMembersImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof AddProjectMembersImpl)) {
        return false;
      }
      AddProjectMembersImpl other = (AddProjectMembersImpl) o;
      if (this._hasChangeRoleInfo != other._hasChangeRoleInfo) {
        return false;
      }
      if (this._hasChangeRoleInfo) {
        if (!this.changeRoleInfo.equals(other.changeRoleInfo)) {
          return false;
        }
      }
      if (this._hasUserEmails != other._hasUserEmails) {
        return false;
      }
      if (this._hasUserEmails) {
        if (!this.userEmails.equals(other.userEmails)) {
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
      hash = hash * 31 + (_hasChangeRoleInfo ? changeRoleInfo.hashCode() : 0);
      hash = hash * 31 + (_hasUserEmails ? userEmails.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement changeRoleInfoOut = changeRoleInfo == null ? JsonNull.INSTANCE : changeRoleInfo.toJsonElement();
      result.add("changeRoleInfo", changeRoleInfoOut);

      JsonElement userEmailsOut = (userEmails == null) ? JsonNull.INSTANCE : new JsonPrimitive(userEmails);
      result.add("userEmails", userEmailsOut);

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

    public static AddProjectMembersImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      AddProjectMembersImpl dto = new AddProjectMembersImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("changeRoleInfo")) {
        JsonElement changeRoleInfoIn = json.get("changeRoleInfo");
        ChangeRoleInfoImpl changeRoleInfoOut = ChangeRoleInfoImpl.fromJsonElement(changeRoleInfoIn);
        dto.setChangeRoleInfo(changeRoleInfoOut);
      }

      if (json.has("userEmails")) {
        JsonElement userEmailsIn = json.get("userEmails");
        java.lang.String userEmailsOut = gson.fromJson(userEmailsIn, java.lang.String.class);
        dto.setUserEmails(userEmailsOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static AddProjectMembersImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class AddWorkspaceMembersImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.AddWorkspaceMembers, JsonSerializable {

    private AddWorkspaceMembersImpl() {
      super(3);
    }

    protected AddWorkspaceMembersImpl(int type) {
      super(type);
    }

    protected ChangeRoleInfoImpl changeRoleInfo;
    private boolean _hasChangeRoleInfo;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String userEmails;
    private boolean _hasUserEmails;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasChangeRoleInfo() {
      return _hasChangeRoleInfo;
    }

    @Override
    public com.google.collide.dto.ChangeRoleInfo getChangeRoleInfo() {
      return changeRoleInfo;
    }

    public AddWorkspaceMembersImpl setChangeRoleInfo(ChangeRoleInfoImpl v) {
      _hasChangeRoleInfo = true;
      changeRoleInfo = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public AddWorkspaceMembersImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasUserEmails() {
      return _hasUserEmails;
    }

    @Override
    public java.lang.String getUserEmails() {
      return userEmails;
    }

    public AddWorkspaceMembersImpl setUserEmails(java.lang.String v) {
      _hasUserEmails = true;
      userEmails = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public AddWorkspaceMembersImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof AddWorkspaceMembersImpl)) {
        return false;
      }
      AddWorkspaceMembersImpl other = (AddWorkspaceMembersImpl) o;
      if (this._hasChangeRoleInfo != other._hasChangeRoleInfo) {
        return false;
      }
      if (this._hasChangeRoleInfo) {
        if (!this.changeRoleInfo.equals(other.changeRoleInfo)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasUserEmails != other._hasUserEmails) {
        return false;
      }
      if (this._hasUserEmails) {
        if (!this.userEmails.equals(other.userEmails)) {
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
      hash = hash * 31 + (_hasChangeRoleInfo ? changeRoleInfo.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasUserEmails ? userEmails.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement changeRoleInfoOut = changeRoleInfo == null ? JsonNull.INSTANCE : changeRoleInfo.toJsonElement();
      result.add("changeRoleInfo", changeRoleInfoOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement userEmailsOut = (userEmails == null) ? JsonNull.INSTANCE : new JsonPrimitive(userEmails);
      result.add("userEmails", userEmailsOut);

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

    public static AddWorkspaceMembersImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      AddWorkspaceMembersImpl dto = new AddWorkspaceMembersImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("changeRoleInfo")) {
        JsonElement changeRoleInfoIn = json.get("changeRoleInfo");
        ChangeRoleInfoImpl changeRoleInfoOut = ChangeRoleInfoImpl.fromJsonElement(changeRoleInfoIn);
        dto.setChangeRoleInfo(changeRoleInfoOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("userEmails")) {
        JsonElement userEmailsIn = json.get("userEmails");
        java.lang.String userEmailsOut = gson.fromJson(userEmailsIn, java.lang.String.class);
        dto.setUserEmails(userEmailsOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static AddWorkspaceMembersImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class BeginUploadSessionImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.BeginUploadSession, JsonSerializable {

    private BeginUploadSessionImpl() {
      super(4);
    }

    protected BeginUploadSessionImpl(int type) {
      super(type);
    }

    protected java.util.List<java.lang.String> workspacePathsToReplace;
    private boolean _hasWorkspacePathsToReplace;
    protected java.util.List<java.lang.String> workspacePathsToUnzip;
    private boolean _hasWorkspacePathsToUnzip;
    protected java.util.List<java.lang.String> workspaceDirsToCreate;
    private boolean _hasWorkspaceDirsToCreate;
    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String sessionId;
    private boolean _hasSessionId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasWorkspacePathsToReplace() {
      return _hasWorkspacePathsToReplace;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getWorkspacePathsToReplace() {
      ensureWorkspacePathsToReplace();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(workspacePathsToReplace);
    }

    public BeginUploadSessionImpl setWorkspacePathsToReplace(java.util.List<java.lang.String> v) {
      _hasWorkspacePathsToReplace = true;
      workspacePathsToReplace = v;
      return this;
    }

    public void addWorkspacePathsToReplace(java.lang.String v) {
      ensureWorkspacePathsToReplace();
      workspacePathsToReplace.add(v);
    }

    public void clearWorkspacePathsToReplace() {
      ensureWorkspacePathsToReplace();
      workspacePathsToReplace.clear();
    }

    private void ensureWorkspacePathsToReplace() {
      if (!_hasWorkspacePathsToReplace) {
        setWorkspacePathsToReplace(workspacePathsToReplace != null ? workspacePathsToReplace : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasWorkspacePathsToUnzip() {
      return _hasWorkspacePathsToUnzip;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getWorkspacePathsToUnzip() {
      ensureWorkspacePathsToUnzip();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(workspacePathsToUnzip);
    }

    public BeginUploadSessionImpl setWorkspacePathsToUnzip(java.util.List<java.lang.String> v) {
      _hasWorkspacePathsToUnzip = true;
      workspacePathsToUnzip = v;
      return this;
    }

    public void addWorkspacePathsToUnzip(java.lang.String v) {
      ensureWorkspacePathsToUnzip();
      workspacePathsToUnzip.add(v);
    }

    public void clearWorkspacePathsToUnzip() {
      ensureWorkspacePathsToUnzip();
      workspacePathsToUnzip.clear();
    }

    private void ensureWorkspacePathsToUnzip() {
      if (!_hasWorkspacePathsToUnzip) {
        setWorkspacePathsToUnzip(workspacePathsToUnzip != null ? workspacePathsToUnzip : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasWorkspaceDirsToCreate() {
      return _hasWorkspaceDirsToCreate;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getWorkspaceDirsToCreate() {
      ensureWorkspaceDirsToCreate();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(workspaceDirsToCreate);
    }

    public BeginUploadSessionImpl setWorkspaceDirsToCreate(java.util.List<java.lang.String> v) {
      _hasWorkspaceDirsToCreate = true;
      workspaceDirsToCreate = v;
      return this;
    }

    public void addWorkspaceDirsToCreate(java.lang.String v) {
      ensureWorkspaceDirsToCreate();
      workspaceDirsToCreate.add(v);
    }

    public void clearWorkspaceDirsToCreate() {
      ensureWorkspaceDirsToCreate();
      workspaceDirsToCreate.clear();
    }

    private void ensureWorkspaceDirsToCreate() {
      if (!_hasWorkspaceDirsToCreate) {
        setWorkspaceDirsToCreate(workspaceDirsToCreate != null ? workspaceDirsToCreate : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public BeginUploadSessionImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasSessionId() {
      return _hasSessionId;
    }

    @Override
    public java.lang.String getSessionId() {
      return sessionId;
    }

    public BeginUploadSessionImpl setSessionId(java.lang.String v) {
      _hasSessionId = true;
      sessionId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public BeginUploadSessionImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof BeginUploadSessionImpl)) {
        return false;
      }
      BeginUploadSessionImpl other = (BeginUploadSessionImpl) o;
      if (this._hasWorkspacePathsToReplace != other._hasWorkspacePathsToReplace) {
        return false;
      }
      if (this._hasWorkspacePathsToReplace) {
        if (!this.workspacePathsToReplace.equals(other.workspacePathsToReplace)) {
          return false;
        }
      }
      if (this._hasWorkspacePathsToUnzip != other._hasWorkspacePathsToUnzip) {
        return false;
      }
      if (this._hasWorkspacePathsToUnzip) {
        if (!this.workspacePathsToUnzip.equals(other.workspacePathsToUnzip)) {
          return false;
        }
      }
      if (this._hasWorkspaceDirsToCreate != other._hasWorkspaceDirsToCreate) {
        return false;
      }
      if (this._hasWorkspaceDirsToCreate) {
        if (!this.workspaceDirsToCreate.equals(other.workspaceDirsToCreate)) {
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
      if (this._hasSessionId != other._hasSessionId) {
        return false;
      }
      if (this._hasSessionId) {
        if (!this.sessionId.equals(other.sessionId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspacePathsToReplace ? workspacePathsToReplace.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspacePathsToUnzip ? workspacePathsToUnzip.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceDirsToCreate ? workspaceDirsToCreate.hashCode() : 0);
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasSessionId ? sessionId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray workspacePathsToReplaceOut = new JsonArray();
      ensureWorkspacePathsToReplace();
      for (java.lang.String workspacePathsToReplace_ : workspacePathsToReplace) {
        JsonElement workspacePathsToReplaceOut_ = (workspacePathsToReplace_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspacePathsToReplace_);
        workspacePathsToReplaceOut.add(workspacePathsToReplaceOut_);
      }
      result.add("workspacePathsToReplace", workspacePathsToReplaceOut);

      JsonArray workspacePathsToUnzipOut = new JsonArray();
      ensureWorkspacePathsToUnzip();
      for (java.lang.String workspacePathsToUnzip_ : workspacePathsToUnzip) {
        JsonElement workspacePathsToUnzipOut_ = (workspacePathsToUnzip_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspacePathsToUnzip_);
        workspacePathsToUnzipOut.add(workspacePathsToUnzipOut_);
      }
      result.add("workspacePathsToUnzip", workspacePathsToUnzipOut);

      JsonArray workspaceDirsToCreateOut = new JsonArray();
      ensureWorkspaceDirsToCreate();
      for (java.lang.String workspaceDirsToCreate_ : workspaceDirsToCreate) {
        JsonElement workspaceDirsToCreateOut_ = (workspaceDirsToCreate_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceDirsToCreate_);
        workspaceDirsToCreateOut.add(workspaceDirsToCreateOut_);
      }
      result.add("workspaceDirsToCreate", workspaceDirsToCreateOut);

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement sessionIdOut = (sessionId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sessionId);
      result.add("sessionId", sessionIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static BeginUploadSessionImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      BeginUploadSessionImpl dto = new BeginUploadSessionImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspacePathsToReplace")) {
        JsonElement workspacePathsToReplaceIn = json.get("workspacePathsToReplace");
        java.util.ArrayList<java.lang.String> workspacePathsToReplaceOut = null;
        if (workspacePathsToReplaceIn != null && !workspacePathsToReplaceIn.isJsonNull()) {
          workspacePathsToReplaceOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> workspacePathsToReplaceInIterator = workspacePathsToReplaceIn.getAsJsonArray().iterator();
          while (workspacePathsToReplaceInIterator.hasNext()) {
            JsonElement workspacePathsToReplaceIn_ = workspacePathsToReplaceInIterator.next();
            java.lang.String workspacePathsToReplaceOut_ = gson.fromJson(workspacePathsToReplaceIn_, java.lang.String.class);
            workspacePathsToReplaceOut.add(workspacePathsToReplaceOut_);
          }
        }
        dto.setWorkspacePathsToReplace(workspacePathsToReplaceOut);
      }

      if (json.has("workspacePathsToUnzip")) {
        JsonElement workspacePathsToUnzipIn = json.get("workspacePathsToUnzip");
        java.util.ArrayList<java.lang.String> workspacePathsToUnzipOut = null;
        if (workspacePathsToUnzipIn != null && !workspacePathsToUnzipIn.isJsonNull()) {
          workspacePathsToUnzipOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> workspacePathsToUnzipInIterator = workspacePathsToUnzipIn.getAsJsonArray().iterator();
          while (workspacePathsToUnzipInIterator.hasNext()) {
            JsonElement workspacePathsToUnzipIn_ = workspacePathsToUnzipInIterator.next();
            java.lang.String workspacePathsToUnzipOut_ = gson.fromJson(workspacePathsToUnzipIn_, java.lang.String.class);
            workspacePathsToUnzipOut.add(workspacePathsToUnzipOut_);
          }
        }
        dto.setWorkspacePathsToUnzip(workspacePathsToUnzipOut);
      }

      if (json.has("workspaceDirsToCreate")) {
        JsonElement workspaceDirsToCreateIn = json.get("workspaceDirsToCreate");
        java.util.ArrayList<java.lang.String> workspaceDirsToCreateOut = null;
        if (workspaceDirsToCreateIn != null && !workspaceDirsToCreateIn.isJsonNull()) {
          workspaceDirsToCreateOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> workspaceDirsToCreateInIterator = workspaceDirsToCreateIn.getAsJsonArray().iterator();
          while (workspaceDirsToCreateInIterator.hasNext()) {
            JsonElement workspaceDirsToCreateIn_ = workspaceDirsToCreateInIterator.next();
            java.lang.String workspaceDirsToCreateOut_ = gson.fromJson(workspaceDirsToCreateIn_, java.lang.String.class);
            workspaceDirsToCreateOut.add(workspaceDirsToCreateOut_);
          }
        }
        dto.setWorkspaceDirsToCreate(workspaceDirsToCreateOut);
      }

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("sessionId")) {
        JsonElement sessionIdIn = json.get("sessionId");
        java.lang.String sessionIdOut = gson.fromJson(sessionIdIn, java.lang.String.class);
        dto.setSessionId(sessionIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static BeginUploadSessionImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ChangeRoleInfoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ChangeRoleInfo, JsonSerializable {

    private ChangeRoleInfoImpl() {
      super(5);
    }

    protected ChangeRoleInfoImpl(int type) {
      super(type);
    }

    protected boolean emailUsers;
    private boolean _hasEmailUsers;
    protected java.lang.String emailMessage;
    private boolean _hasEmailMessage;
    protected boolean emailSelf;
    private boolean _hasEmailSelf;
    protected com.google.collide.dto.Role role;
    private boolean _hasRole;

    public boolean hasEmailUsers() {
      return _hasEmailUsers;
    }

    @Override
    public boolean emailUsers() {
      return emailUsers;
    }

    public ChangeRoleInfoImpl setEmailUsers(boolean v) {
      _hasEmailUsers = true;
      emailUsers = v;
      return this;
    }

    public boolean hasEmailMessage() {
      return _hasEmailMessage;
    }

    @Override
    public java.lang.String getEmailMessage() {
      return emailMessage;
    }

    public ChangeRoleInfoImpl setEmailMessage(java.lang.String v) {
      _hasEmailMessage = true;
      emailMessage = v;
      return this;
    }

    public boolean hasEmailSelf() {
      return _hasEmailSelf;
    }

    @Override
    public boolean emailSelf() {
      return emailSelf;
    }

    public ChangeRoleInfoImpl setEmailSelf(boolean v) {
      _hasEmailSelf = true;
      emailSelf = v;
      return this;
    }

    public boolean hasRole() {
      return _hasRole;
    }

    @Override
    public com.google.collide.dto.Role getRole() {
      return role;
    }

    public ChangeRoleInfoImpl setRole(com.google.collide.dto.Role v) {
      _hasRole = true;
      role = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ChangeRoleInfoImpl)) {
        return false;
      }
      ChangeRoleInfoImpl other = (ChangeRoleInfoImpl) o;
      if (this._hasEmailUsers != other._hasEmailUsers) {
        return false;
      }
      if (this._hasEmailUsers) {
        if (this.emailUsers != other.emailUsers) {
          return false;
        }
      }
      if (this._hasEmailMessage != other._hasEmailMessage) {
        return false;
      }
      if (this._hasEmailMessage) {
        if (!this.emailMessage.equals(other.emailMessage)) {
          return false;
        }
      }
      if (this._hasEmailSelf != other._hasEmailSelf) {
        return false;
      }
      if (this._hasEmailSelf) {
        if (this.emailSelf != other.emailSelf) {
          return false;
        }
      }
      if (this._hasRole != other._hasRole) {
        return false;
      }
      if (this._hasRole) {
        if (!this.role.equals(other.role)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasEmailUsers ? java.lang.Boolean.valueOf(emailUsers).hashCode() : 0);
      hash = hash * 31 + (_hasEmailMessage ? emailMessage.hashCode() : 0);
      hash = hash * 31 + (_hasEmailSelf ? java.lang.Boolean.valueOf(emailSelf).hashCode() : 0);
      hash = hash * 31 + (_hasRole ? role.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive emailUsersOut = new JsonPrimitive(emailUsers);
      result.add("emailUsers", emailUsersOut);

      JsonElement emailMessageOut = (emailMessage == null) ? JsonNull.INSTANCE : new JsonPrimitive(emailMessage);
      result.add("emailMessage", emailMessageOut);

      JsonPrimitive emailSelfOut = new JsonPrimitive(emailSelf);
      result.add("emailSelf", emailSelfOut);

      JsonElement roleOut = (role == null) ? JsonNull.INSTANCE : new JsonPrimitive(role.name());
      result.add("role", roleOut);
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

    public static ChangeRoleInfoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ChangeRoleInfoImpl dto = new ChangeRoleInfoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("emailUsers")) {
        JsonElement emailUsersIn = json.get("emailUsers");
        boolean emailUsersOut = emailUsersIn.getAsBoolean();
        dto.setEmailUsers(emailUsersOut);
      }

      if (json.has("emailMessage")) {
        JsonElement emailMessageIn = json.get("emailMessage");
        java.lang.String emailMessageOut = gson.fromJson(emailMessageIn, java.lang.String.class);
        dto.setEmailMessage(emailMessageOut);
      }

      if (json.has("emailSelf")) {
        JsonElement emailSelfIn = json.get("emailSelf");
        boolean emailSelfOut = emailSelfIn.getAsBoolean();
        dto.setEmailSelf(emailSelfOut);
      }

      if (json.has("role")) {
        JsonElement roleIn = json.get("role");
        com.google.collide.dto.Role roleOut = gson.fromJson(roleIn, com.google.collide.dto.Role.class);
        dto.setRole(roleOut);
      }

      return dto;
    }
    public static ChangeRoleInfoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ClientToServerDocOpImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ClientToServerDocOp, JsonSerializable {

    private ClientToServerDocOpImpl() {
      super(6);
    }

    protected ClientToServerDocOpImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.util.List<java.lang.String> docOps2;
    private boolean _hasDocOps2;
    protected int ccRevision;
    private boolean _hasCcRevision;
    protected DocumentSelectionImpl selection;
    private boolean _hasSelection;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public ClientToServerDocOpImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public ClientToServerDocOpImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public ClientToServerDocOpImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasDocOps2() {
      return _hasDocOps2;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getDocOps2() {
      ensureDocOps2();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(docOps2);
    }

    public ClientToServerDocOpImpl setDocOps2(java.util.List<java.lang.String> v) {
      _hasDocOps2 = true;
      docOps2 = v;
      return this;
    }

    public void addDocOps2(java.lang.String v) {
      ensureDocOps2();
      docOps2.add(v);
    }

    public void clearDocOps2() {
      ensureDocOps2();
      docOps2.clear();
    }

    private void ensureDocOps2() {
      if (!_hasDocOps2) {
        setDocOps2(docOps2 != null ? docOps2 : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasCcRevision() {
      return _hasCcRevision;
    }

    @Override
    public int getCcRevision() {
      return ccRevision;
    }

    public ClientToServerDocOpImpl setCcRevision(int v) {
      _hasCcRevision = true;
      ccRevision = v;
      return this;
    }

    public boolean hasSelection() {
      return _hasSelection;
    }

    @Override
    public com.google.collide.dto.DocumentSelection getSelection() {
      return selection;
    }

    public ClientToServerDocOpImpl setSelection(DocumentSelectionImpl v) {
      _hasSelection = true;
      selection = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ClientToServerDocOpImpl)) {
        return false;
      }
      ClientToServerDocOpImpl other = (ClientToServerDocOpImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasDocOps2 != other._hasDocOps2) {
        return false;
      }
      if (this._hasDocOps2) {
        if (!this.docOps2.equals(other.docOps2)) {
          return false;
        }
      }
      if (this._hasCcRevision != other._hasCcRevision) {
        return false;
      }
      if (this._hasCcRevision) {
        if (this.ccRevision != other.ccRevision) {
          return false;
        }
      }
      if (this._hasSelection != other._hasSelection) {
        return false;
      }
      if (this._hasSelection) {
        if (!this.selection.equals(other.selection)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasDocOps2 ? docOps2.hashCode() : 0);
      hash = hash * 31 + (_hasCcRevision ? java.lang.Integer.valueOf(ccRevision).hashCode() : 0);
      hash = hash * 31 + (_hasSelection ? selection.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonArray docOps2Out = new JsonArray();
      ensureDocOps2();
      for (java.lang.String docOps2_ : docOps2) {
        JsonElement docOps2Out_ = (docOps2_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(docOps2_);
        docOps2Out.add(docOps2Out_);
      }
      result.add("docOps2", docOps2Out);

      JsonPrimitive ccRevisionOut = new JsonPrimitive(ccRevision);
      result.add("ccRevision", ccRevisionOut);

      JsonElement selectionOut = selection == null ? JsonNull.INSTANCE : selection.toJsonElement();
      result.add("selection", selectionOut);
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

    public static ClientToServerDocOpImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ClientToServerDocOpImpl dto = new ClientToServerDocOpImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("docOps2")) {
        JsonElement docOps2In = json.get("docOps2");
        java.util.ArrayList<java.lang.String> docOps2Out = null;
        if (docOps2In != null && !docOps2In.isJsonNull()) {
          docOps2Out = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> docOps2InIterator = docOps2In.getAsJsonArray().iterator();
          while (docOps2InIterator.hasNext()) {
            JsonElement docOps2In_ = docOps2InIterator.next();
            java.lang.String docOps2Out_ = gson.fromJson(docOps2In_, java.lang.String.class);
            docOps2Out.add(docOps2Out_);
          }
        }
        dto.setDocOps2(docOps2Out);
      }

      if (json.has("ccRevision")) {
        JsonElement ccRevisionIn = json.get("ccRevision");
        int ccRevisionOut = ccRevisionIn.getAsInt();
        dto.setCcRevision(ccRevisionOut);
      }

      if (json.has("selection")) {
        JsonElement selectionIn = json.get("selection");
        DocumentSelectionImpl selectionOut = DocumentSelectionImpl.fromJsonElement(selectionIn);
        dto.setSelection(selectionOut);
      }

      return dto;
    }
    public static ClientToServerDocOpImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CloseEditorImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CloseEditor, JsonSerializable {

    private CloseEditorImpl() {
      super(126);
    }

    protected CloseEditorImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public CloseEditorImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public CloseEditorImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CloseEditorImpl)) {
        return false;
      }
      CloseEditorImpl other = (CloseEditorImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);
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

    public static CloseEditorImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CloseEditorImpl dto = new CloseEditorImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      return dto;
    }
    public static CloseEditorImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeBlockImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CodeBlock, JsonSerializable {

    private CodeBlockImpl() {
      super(7);
    }

    protected CodeBlockImpl(int type) {
      super(type);
    }

    public static CodeBlockImpl make() {
      return new CodeBlockImpl();
    }

    protected java.lang.String id;
    private boolean _hasId;
    protected int blockType;
    private boolean _hasBlockType;
    protected int endColumn;
    private boolean _hasEndColumn;
    protected int endLineNumber;
    private boolean _hasEndLineNumber;
    protected java.lang.String name;
    private boolean _hasName;
    protected int startColumn;
    private boolean _hasStartColumn;
    protected int startLineNumber;
    private boolean _hasStartLineNumber;
    protected java.util.List<CodeBlockImpl> children;
    private boolean _hasChildren;

    public boolean hasId() {
      return _hasId;
    }

    @Override
    public java.lang.String getId() {
      return id;
    }

    public CodeBlockImpl setId(java.lang.String v) {
      _hasId = true;
      id = v;
      return this;
    }

    public boolean hasBlockType() {
      return _hasBlockType;
    }

    @Override
    public int getBlockType() {
      return blockType;
    }

    public CodeBlockImpl setBlockType(int v) {
      _hasBlockType = true;
      blockType = v;
      return this;
    }

    public boolean hasEndColumn() {
      return _hasEndColumn;
    }

    @Override
    public int getEndColumn() {
      return endColumn;
    }

    public CodeBlockImpl setEndColumn(int v) {
      _hasEndColumn = true;
      endColumn = v;
      return this;
    }

    public boolean hasEndLineNumber() {
      return _hasEndLineNumber;
    }

    @Override
    public int getEndLineNumber() {
      return endLineNumber;
    }

    public CodeBlockImpl setEndLineNumber(int v) {
      _hasEndLineNumber = true;
      endLineNumber = v;
      return this;
    }

    public boolean hasName() {
      return _hasName;
    }

    @Override
    public java.lang.String getName() {
      return name;
    }

    public CodeBlockImpl setName(java.lang.String v) {
      _hasName = true;
      name = v;
      return this;
    }

    public boolean hasStartColumn() {
      return _hasStartColumn;
    }

    @Override
    public int getStartColumn() {
      return startColumn;
    }

    public CodeBlockImpl setStartColumn(int v) {
      _hasStartColumn = true;
      startColumn = v;
      return this;
    }

    public boolean hasStartLineNumber() {
      return _hasStartLineNumber;
    }

    @Override
    public int getStartLineNumber() {
      return startLineNumber;
    }

    public CodeBlockImpl setStartLineNumber(int v) {
      _hasStartLineNumber = true;
      startLineNumber = v;
      return this;
    }

    public boolean hasChildren() {
      return _hasChildren;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.CodeBlock> getChildren() {
      ensureChildren();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(children);
    }

    public CodeBlockImpl setChildren(java.util.List<CodeBlockImpl> v) {
      _hasChildren = true;
      children = v;
      return this;
    }

    public void addChildren(CodeBlockImpl v) {
      ensureChildren();
      children.add(v);
    }

    public void clearChildren() {
      ensureChildren();
      children.clear();
    }

    private void ensureChildren() {
      if (!_hasChildren) {
        setChildren(children != null ? children : new java.util.ArrayList<CodeBlockImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CodeBlockImpl)) {
        return false;
      }
      CodeBlockImpl other = (CodeBlockImpl) o;
      if (this._hasId != other._hasId) {
        return false;
      }
      if (this._hasId) {
        if (!this.id.equals(other.id)) {
          return false;
        }
      }
      if (this._hasBlockType != other._hasBlockType) {
        return false;
      }
      if (this._hasBlockType) {
        if (this.blockType != other.blockType) {
          return false;
        }
      }
      if (this._hasEndColumn != other._hasEndColumn) {
        return false;
      }
      if (this._hasEndColumn) {
        if (this.endColumn != other.endColumn) {
          return false;
        }
      }
      if (this._hasEndLineNumber != other._hasEndLineNumber) {
        return false;
      }
      if (this._hasEndLineNumber) {
        if (this.endLineNumber != other.endLineNumber) {
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
      if (this._hasStartColumn != other._hasStartColumn) {
        return false;
      }
      if (this._hasStartColumn) {
        if (this.startColumn != other.startColumn) {
          return false;
        }
      }
      if (this._hasStartLineNumber != other._hasStartLineNumber) {
        return false;
      }
      if (this._hasStartLineNumber) {
        if (this.startLineNumber != other.startLineNumber) {
          return false;
        }
      }
      if (this._hasChildren != other._hasChildren) {
        return false;
      }
      if (this._hasChildren) {
        if (!this.children.equals(other.children)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasId ? id.hashCode() : 0);
      hash = hash * 31 + (_hasBlockType ? java.lang.Integer.valueOf(blockType).hashCode() : 0);
      hash = hash * 31 + (_hasEndColumn ? java.lang.Integer.valueOf(endColumn).hashCode() : 0);
      hash = hash * 31 + (_hasEndLineNumber ? java.lang.Integer.valueOf(endLineNumber).hashCode() : 0);
      hash = hash * 31 + (_hasName ? name.hashCode() : 0);
      hash = hash * 31 + (_hasStartColumn ? java.lang.Integer.valueOf(startColumn).hashCode() : 0);
      hash = hash * 31 + (_hasStartLineNumber ? java.lang.Integer.valueOf(startLineNumber).hashCode() : 0);
      hash = hash * 31 + (_hasChildren ? children.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonArray result = new JsonArray();

      JsonElement idOut = (id == null) ? JsonNull.INSTANCE : new JsonPrimitive(id);
      result.add(idOut);

      JsonPrimitive blockTypeOut = new JsonPrimitive(blockType);
      result.add(blockTypeOut);

      JsonPrimitive endColumnOut = new JsonPrimitive(endColumn);
      result.add(endColumnOut);

      JsonPrimitive endLineNumberOut = new JsonPrimitive(endLineNumber);
      result.add(endLineNumberOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add(nameOut);

      JsonPrimitive startColumnOut = new JsonPrimitive(startColumn);
      result.add(startColumnOut);

      JsonPrimitive startLineNumberOut = new JsonPrimitive(startLineNumber);
      result.add(startLineNumberOut);

      JsonArray childrenOut = new JsonArray();
      ensureChildren();
      for (CodeBlockImpl children_ : children) {
        JsonElement childrenOut_ = children_ == null ? JsonNull.INSTANCE : children_.toJsonElement();
        childrenOut.add(childrenOut_);
      }
      if (childrenOut.size() != 0) {
        result.add(childrenOut);
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

    public static CodeBlockImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeBlockImpl dto = new CodeBlockImpl();
      JsonArray json = jsonElem.getAsJsonArray();

      if (0 < json.size()) {
        JsonElement idIn = json.get(0);
        java.lang.String idOut = gson.fromJson(idIn, java.lang.String.class);
        dto.setId(idOut);
      }

      if (1 < json.size()) {
        JsonElement blockTypeIn = json.get(1);
        int blockTypeOut = blockTypeIn.getAsInt();
        dto.setBlockType(blockTypeOut);
      }

      if (2 < json.size()) {
        JsonElement endColumnIn = json.get(2);
        int endColumnOut = endColumnIn.getAsInt();
        dto.setEndColumn(endColumnOut);
      }

      if (3 < json.size()) {
        JsonElement endLineNumberIn = json.get(3);
        int endLineNumberOut = endLineNumberIn.getAsInt();
        dto.setEndLineNumber(endLineNumberOut);
      }

      if (4 < json.size()) {
        JsonElement nameIn = json.get(4);
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      if (5 < json.size()) {
        JsonElement startColumnIn = json.get(5);
        int startColumnOut = startColumnIn.getAsInt();
        dto.setStartColumn(startColumnOut);
      }

      if (6 < json.size()) {
        JsonElement startLineNumberIn = json.get(6);
        int startLineNumberOut = startLineNumberIn.getAsInt();
        dto.setStartLineNumber(startLineNumberOut);
      }

      if (7 < json.size()) {
        JsonElement childrenIn = json.get(7);
        java.util.ArrayList<CodeBlockImpl> childrenOut = null;
        if (childrenIn != null && !childrenIn.isJsonNull()) {
          childrenOut = new java.util.ArrayList<CodeBlockImpl>();
          java.util.Iterator<JsonElement> childrenInIterator = childrenIn.getAsJsonArray().iterator();
          while (childrenInIterator.hasNext()) {
            JsonElement childrenIn_ = childrenInIterator.next();
            CodeBlockImpl childrenOut_ = CodeBlockImpl.fromJsonElement(childrenIn_);
            childrenOut.add(childrenOut_);
          }
        }
        dto.setChildren(childrenOut);
      }

      return dto;
    }
    public static CodeBlockImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeBlockAssociationImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CodeBlockAssociation, JsonSerializable {

    private CodeBlockAssociationImpl() {
      super(8);
    }

    protected CodeBlockAssociationImpl(int type) {
      super(type);
    }

    public static CodeBlockAssociationImpl make() {
      return new CodeBlockAssociationImpl();
    }

    protected java.lang.String sourceFileId;
    private boolean _hasSourceFileId;
    protected java.lang.String sourceLocalId;
    private boolean _hasSourceLocalId;
    protected java.lang.String targetFileId;
    private boolean _hasTargetFileId;
    protected java.lang.String targetLocalId;
    private boolean _hasTargetLocalId;
    protected boolean isRootAssociation;
    private boolean _hasIsRootAssociation;

    public boolean hasSourceFileId() {
      return _hasSourceFileId;
    }

    @Override
    public java.lang.String getSourceFileId() {
      return sourceFileId;
    }

    public CodeBlockAssociationImpl setSourceFileId(java.lang.String v) {
      _hasSourceFileId = true;
      sourceFileId = v;
      return this;
    }

    public boolean hasSourceLocalId() {
      return _hasSourceLocalId;
    }

    @Override
    public java.lang.String getSourceLocalId() {
      return sourceLocalId;
    }

    public CodeBlockAssociationImpl setSourceLocalId(java.lang.String v) {
      _hasSourceLocalId = true;
      sourceLocalId = v;
      return this;
    }

    public boolean hasTargetFileId() {
      return _hasTargetFileId;
    }

    @Override
    public java.lang.String getTargetFileId() {
      return targetFileId;
    }

    public CodeBlockAssociationImpl setTargetFileId(java.lang.String v) {
      _hasTargetFileId = true;
      targetFileId = v;
      return this;
    }

    public boolean hasTargetLocalId() {
      return _hasTargetLocalId;
    }

    @Override
    public java.lang.String getTargetLocalId() {
      return targetLocalId;
    }

    public CodeBlockAssociationImpl setTargetLocalId(java.lang.String v) {
      _hasTargetLocalId = true;
      targetLocalId = v;
      return this;
    }

    public boolean hasIsRootAssociation() {
      return _hasIsRootAssociation;
    }

    @Override
    public boolean getIsRootAssociation() {
      return isRootAssociation;
    }

    public CodeBlockAssociationImpl setIsRootAssociation(boolean v) {
      _hasIsRootAssociation = true;
      isRootAssociation = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CodeBlockAssociationImpl)) {
        return false;
      }
      CodeBlockAssociationImpl other = (CodeBlockAssociationImpl) o;
      if (this._hasSourceFileId != other._hasSourceFileId) {
        return false;
      }
      if (this._hasSourceFileId) {
        if (!this.sourceFileId.equals(other.sourceFileId)) {
          return false;
        }
      }
      if (this._hasSourceLocalId != other._hasSourceLocalId) {
        return false;
      }
      if (this._hasSourceLocalId) {
        if (!this.sourceLocalId.equals(other.sourceLocalId)) {
          return false;
        }
      }
      if (this._hasTargetFileId != other._hasTargetFileId) {
        return false;
      }
      if (this._hasTargetFileId) {
        if (!this.targetFileId.equals(other.targetFileId)) {
          return false;
        }
      }
      if (this._hasTargetLocalId != other._hasTargetLocalId) {
        return false;
      }
      if (this._hasTargetLocalId) {
        if (!this.targetLocalId.equals(other.targetLocalId)) {
          return false;
        }
      }
      if (this._hasIsRootAssociation != other._hasIsRootAssociation) {
        return false;
      }
      if (this._hasIsRootAssociation) {
        if (this.isRootAssociation != other.isRootAssociation) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasSourceFileId ? sourceFileId.hashCode() : 0);
      hash = hash * 31 + (_hasSourceLocalId ? sourceLocalId.hashCode() : 0);
      hash = hash * 31 + (_hasTargetFileId ? targetFileId.hashCode() : 0);
      hash = hash * 31 + (_hasTargetLocalId ? targetLocalId.hashCode() : 0);
      hash = hash * 31 + (_hasIsRootAssociation ? java.lang.Boolean.valueOf(isRootAssociation).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonArray result = new JsonArray();

      JsonElement sourceFileIdOut = (sourceFileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sourceFileId);
      result.add(sourceFileIdOut);

      JsonElement sourceLocalIdOut = (sourceLocalId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sourceLocalId);
      result.add(sourceLocalIdOut);

      JsonElement targetFileIdOut = (targetFileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetFileId);
      result.add(targetFileIdOut);

      JsonElement targetLocalIdOut = (targetLocalId == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetLocalId);
      result.add(targetLocalIdOut);

      JsonPrimitive isRootAssociationOut = new JsonPrimitive(isRootAssociation);
      result.add(isRootAssociationOut);
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

    public static CodeBlockAssociationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeBlockAssociationImpl dto = new CodeBlockAssociationImpl();
      JsonArray json = jsonElem.getAsJsonArray();

      if (0 < json.size()) {
        JsonElement sourceFileIdIn = json.get(0);
        java.lang.String sourceFileIdOut = gson.fromJson(sourceFileIdIn, java.lang.String.class);
        dto.setSourceFileId(sourceFileIdOut);
      }

      if (1 < json.size()) {
        JsonElement sourceLocalIdIn = json.get(1);
        java.lang.String sourceLocalIdOut = gson.fromJson(sourceLocalIdIn, java.lang.String.class);
        dto.setSourceLocalId(sourceLocalIdOut);
      }

      if (2 < json.size()) {
        JsonElement targetFileIdIn = json.get(2);
        java.lang.String targetFileIdOut = gson.fromJson(targetFileIdIn, java.lang.String.class);
        dto.setTargetFileId(targetFileIdOut);
      }

      if (3 < json.size()) {
        JsonElement targetLocalIdIn = json.get(3);
        java.lang.String targetLocalIdOut = gson.fromJson(targetLocalIdIn, java.lang.String.class);
        dto.setTargetLocalId(targetLocalIdOut);
      }

      if (4 < json.size()) {
        JsonElement isRootAssociationIn = json.get(4);
        boolean isRootAssociationOut = isRootAssociationIn.getAsBoolean();
        dto.setIsRootAssociation(isRootAssociationOut);
      }

      return dto;
    }
    public static CodeBlockAssociationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeErrorImpl implements com.google.collide.dto.CodeError, JsonSerializable {

    public static CodeErrorImpl make() {
      return new CodeErrorImpl();
    }

    protected FilePositionImpl errorEnd;
    private boolean _hasErrorEnd;
    protected FilePositionImpl errorStart;
    private boolean _hasErrorStart;
    protected java.lang.String message;
    private boolean _hasMessage;
    protected boolean isError;
    private boolean _hasIsError;

    public boolean hasErrorEnd() {
      return _hasErrorEnd;
    }

    @Override
    public com.google.collide.dto.FilePosition getErrorEnd() {
      return errorEnd;
    }

    public CodeErrorImpl setErrorEnd(FilePositionImpl v) {
      _hasErrorEnd = true;
      errorEnd = v;
      return this;
    }

    public boolean hasErrorStart() {
      return _hasErrorStart;
    }

    @Override
    public com.google.collide.dto.FilePosition getErrorStart() {
      return errorStart;
    }

    public CodeErrorImpl setErrorStart(FilePositionImpl v) {
      _hasErrorStart = true;
      errorStart = v;
      return this;
    }

    public boolean hasMessage() {
      return _hasMessage;
    }

    @Override
    public java.lang.String getMessage() {
      return message;
    }

    public CodeErrorImpl setMessage(java.lang.String v) {
      _hasMessage = true;
      message = v;
      return this;
    }

    public boolean hasIsError() {
      return _hasIsError;
    }

    @Override
    public boolean isError() {
      return isError;
    }

    public CodeErrorImpl setIsError(boolean v) {
      _hasIsError = true;
      isError = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof CodeErrorImpl)) {
        return false;
      }
      CodeErrorImpl other = (CodeErrorImpl) o;
      if (this._hasErrorEnd != other._hasErrorEnd) {
        return false;
      }
      if (this._hasErrorEnd) {
        if (!this.errorEnd.equals(other.errorEnd)) {
          return false;
        }
      }
      if (this._hasErrorStart != other._hasErrorStart) {
        return false;
      }
      if (this._hasErrorStart) {
        if (!this.errorStart.equals(other.errorStart)) {
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
      if (this._hasIsError != other._hasIsError) {
        return false;
      }
      if (this._hasIsError) {
        if (this.isError != other.isError) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasErrorEnd ? errorEnd.hashCode() : 0);
      hash = hash * 31 + (_hasErrorStart ? errorStart.hashCode() : 0);
      hash = hash * 31 + (_hasMessage ? message.hashCode() : 0);
      hash = hash * 31 + (_hasIsError ? java.lang.Boolean.valueOf(isError).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement errorEndOut = errorEnd == null ? JsonNull.INSTANCE : errorEnd.toJsonElement();
      result.add("errorEnd", errorEndOut);

      JsonElement errorStartOut = errorStart == null ? JsonNull.INSTANCE : errorStart.toJsonElement();
      result.add("errorStart", errorStartOut);

      JsonElement messageOut = (message == null) ? JsonNull.INSTANCE : new JsonPrimitive(message);
      result.add("message", messageOut);

      JsonPrimitive isErrorOut = new JsonPrimitive(isError);
      result.add("isError", isErrorOut);
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

    public static CodeErrorImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeErrorImpl dto = new CodeErrorImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("errorEnd")) {
        JsonElement errorEndIn = json.get("errorEnd");
        FilePositionImpl errorEndOut = FilePositionImpl.fromJsonElement(errorEndIn);
        dto.setErrorEnd(errorEndOut);
      }

      if (json.has("errorStart")) {
        JsonElement errorStartIn = json.get("errorStart");
        FilePositionImpl errorStartOut = FilePositionImpl.fromJsonElement(errorStartIn);
        dto.setErrorStart(errorStartOut);
      }

      if (json.has("message")) {
        JsonElement messageIn = json.get("message");
        java.lang.String messageOut = gson.fromJson(messageIn, java.lang.String.class);
        dto.setMessage(messageOut);
      }

      if (json.has("isError")) {
        JsonElement isErrorIn = json.get("isError");
        boolean isErrorOut = isErrorIn.getAsBoolean();
        dto.setIsError(isErrorOut);
      }

      return dto;
    }
    public static CodeErrorImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeErrorsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CodeErrors, JsonSerializable {

    private CodeErrorsImpl() {
      super(9);
    }

    protected CodeErrorsImpl(int type) {
      super(type);
    }

    public static CodeErrorsImpl make() {
      return new CodeErrorsImpl();
    }

    protected java.util.List<CodeErrorImpl> codeErrors;
    private boolean _hasCodeErrors;
    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;

    public boolean hasCodeErrors() {
      return _hasCodeErrors;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.CodeError> getCodeErrors() {
      ensureCodeErrors();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(codeErrors);
    }

    public CodeErrorsImpl setCodeErrors(java.util.List<CodeErrorImpl> v) {
      _hasCodeErrors = true;
      codeErrors = v;
      return this;
    }

    public void addCodeErrors(CodeErrorImpl v) {
      ensureCodeErrors();
      codeErrors.add(v);
    }

    public void clearCodeErrors() {
      ensureCodeErrors();
      codeErrors.clear();
    }

    private void ensureCodeErrors() {
      if (!_hasCodeErrors) {
        setCodeErrors(codeErrors != null ? codeErrors : new java.util.ArrayList<CodeErrorImpl>());
      }
    }

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public CodeErrorsImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CodeErrorsImpl)) {
        return false;
      }
      CodeErrorsImpl other = (CodeErrorsImpl) o;
      if (this._hasCodeErrors != other._hasCodeErrors) {
        return false;
      }
      if (this._hasCodeErrors) {
        if (!this.codeErrors.equals(other.codeErrors)) {
          return false;
        }
      }
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasCodeErrors ? codeErrors.hashCode() : 0);
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray codeErrorsOut = new JsonArray();
      ensureCodeErrors();
      for (CodeErrorImpl codeErrors_ : codeErrors) {
        JsonElement codeErrorsOut_ = codeErrors_ == null ? JsonNull.INSTANCE : codeErrors_.toJsonElement();
        codeErrorsOut.add(codeErrorsOut_);
      }
      result.add("codeErrors", codeErrorsOut);

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);
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

    public static CodeErrorsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeErrorsImpl dto = new CodeErrorsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("codeErrors")) {
        JsonElement codeErrorsIn = json.get("codeErrors");
        java.util.ArrayList<CodeErrorImpl> codeErrorsOut = null;
        if (codeErrorsIn != null && !codeErrorsIn.isJsonNull()) {
          codeErrorsOut = new java.util.ArrayList<CodeErrorImpl>();
          java.util.Iterator<JsonElement> codeErrorsInIterator = codeErrorsIn.getAsJsonArray().iterator();
          while (codeErrorsInIterator.hasNext()) {
            JsonElement codeErrorsIn_ = codeErrorsInIterator.next();
            CodeErrorImpl codeErrorsOut_ = CodeErrorImpl.fromJsonElement(codeErrorsIn_);
            codeErrorsOut.add(codeErrorsOut_);
          }
        }
        dto.setCodeErrors(codeErrorsOut);
      }

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      return dto;
    }
    public static CodeErrorsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeErrorsRequestImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CodeErrorsRequest, JsonSerializable {

    private CodeErrorsRequestImpl() {
      super(10);
    }

    protected CodeErrorsRequestImpl(int type) {
      super(type);
    }

    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public CodeErrorsRequestImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public CodeErrorsRequestImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CodeErrorsRequestImpl)) {
        return false;
      }
      CodeErrorsRequestImpl other = (CodeErrorsRequestImpl) o;
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static CodeErrorsRequestImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeErrorsRequestImpl dto = new CodeErrorsRequestImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static CodeErrorsRequestImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeGraphImpl implements com.google.collide.dto.CodeGraph, JsonSerializable {

    public static CodeGraphImpl make() {
      return new CodeGraphImpl();
    }

    protected java.util.Map<String, CodeBlockImpl> codeBlockMap;
    private boolean _hasCodeBlockMap;
    protected CodeBlockImpl defaultPackage;
    private boolean _hasDefaultPackage;
    protected java.util.List<InheritanceAssociationImpl> inheritanceAssociations;
    private boolean _hasInheritanceAssociations;
    protected java.util.List<TypeAssociationImpl> typeAssociations;
    private boolean _hasTypeAssociations;
    protected java.util.List<ImportAssociationImpl> importAssociations;
    private boolean _hasImportAssociations;

    public boolean hasCodeBlockMap() {
      return _hasCodeBlockMap;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonStringMap<com.google.collide.dto.CodeBlock> getCodeBlockMap() {
      ensureCodeBlockMap();
      return (org.exoplatform.ide.json.shared.JsonStringMap) new org.exoplatform.ide.json.server.JsonStringMapAdapter(codeBlockMap);
    }

    public CodeGraphImpl setCodeBlockMap(java.util.Map<String, CodeBlockImpl> v) {
      _hasCodeBlockMap = true;
      codeBlockMap = v;
      return this;
    }

    public void putCodeBlockMap(String k, CodeBlockImpl v) {
      ensureCodeBlockMap();
      codeBlockMap.put(k, v);
    }

    public void clearCodeBlockMap() {
      ensureCodeBlockMap();
      codeBlockMap.clear();
    }

    private void ensureCodeBlockMap() {
      if (!_hasCodeBlockMap) {
        setCodeBlockMap(codeBlockMap != null ? codeBlockMap : new java.util.HashMap<String, CodeBlockImpl>());
      }
    }

    public boolean hasDefaultPackage() {
      return _hasDefaultPackage;
    }

    @Override
    public com.google.collide.dto.CodeBlock getDefaultPackage() {
      return defaultPackage;
    }

    public CodeGraphImpl setDefaultPackage(CodeBlockImpl v) {
      _hasDefaultPackage = true;
      defaultPackage = v;
      return this;
    }

    public boolean hasInheritanceAssociations() {
      return _hasInheritanceAssociations;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.InheritanceAssociation> getInheritanceAssociations() {
      ensureInheritanceAssociations();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(inheritanceAssociations);
    }

    public CodeGraphImpl setInheritanceAssociations(java.util.List<InheritanceAssociationImpl> v) {
      _hasInheritanceAssociations = true;
      inheritanceAssociations = v;
      return this;
    }

    public void addInheritanceAssociations(InheritanceAssociationImpl v) {
      ensureInheritanceAssociations();
      inheritanceAssociations.add(v);
    }

    public void clearInheritanceAssociations() {
      ensureInheritanceAssociations();
      inheritanceAssociations.clear();
    }

    private void ensureInheritanceAssociations() {
      if (!_hasInheritanceAssociations) {
        setInheritanceAssociations(inheritanceAssociations != null ? inheritanceAssociations : new java.util.ArrayList<InheritanceAssociationImpl>());
      }
    }

    public boolean hasTypeAssociations() {
      return _hasTypeAssociations;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.TypeAssociation> getTypeAssociations() {
      ensureTypeAssociations();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(typeAssociations);
    }

    public CodeGraphImpl setTypeAssociations(java.util.List<TypeAssociationImpl> v) {
      _hasTypeAssociations = true;
      typeAssociations = v;
      return this;
    }

    public void addTypeAssociations(TypeAssociationImpl v) {
      ensureTypeAssociations();
      typeAssociations.add(v);
    }

    public void clearTypeAssociations() {
      ensureTypeAssociations();
      typeAssociations.clear();
    }

    private void ensureTypeAssociations() {
      if (!_hasTypeAssociations) {
        setTypeAssociations(typeAssociations != null ? typeAssociations : new java.util.ArrayList<TypeAssociationImpl>());
      }
    }

    public boolean hasImportAssociations() {
      return _hasImportAssociations;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ImportAssociation> getImportAssociations() {
      ensureImportAssociations();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(importAssociations);
    }

    public CodeGraphImpl setImportAssociations(java.util.List<ImportAssociationImpl> v) {
      _hasImportAssociations = true;
      importAssociations = v;
      return this;
    }

    public void addImportAssociations(ImportAssociationImpl v) {
      ensureImportAssociations();
      importAssociations.add(v);
    }

    public void clearImportAssociations() {
      ensureImportAssociations();
      importAssociations.clear();
    }

    private void ensureImportAssociations() {
      if (!_hasImportAssociations) {
        setImportAssociations(importAssociations != null ? importAssociations : new java.util.ArrayList<ImportAssociationImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof CodeGraphImpl)) {
        return false;
      }
      CodeGraphImpl other = (CodeGraphImpl) o;
      if (this._hasCodeBlockMap != other._hasCodeBlockMap) {
        return false;
      }
      if (this._hasCodeBlockMap) {
        if (!this.codeBlockMap.equals(other.codeBlockMap)) {
          return false;
        }
      }
      if (this._hasDefaultPackage != other._hasDefaultPackage) {
        return false;
      }
      if (this._hasDefaultPackage) {
        if (!this.defaultPackage.equals(other.defaultPackage)) {
          return false;
        }
      }
      if (this._hasInheritanceAssociations != other._hasInheritanceAssociations) {
        return false;
      }
      if (this._hasInheritanceAssociations) {
        if (!this.inheritanceAssociations.equals(other.inheritanceAssociations)) {
          return false;
        }
      }
      if (this._hasTypeAssociations != other._hasTypeAssociations) {
        return false;
      }
      if (this._hasTypeAssociations) {
        if (!this.typeAssociations.equals(other.typeAssociations)) {
          return false;
        }
      }
      if (this._hasImportAssociations != other._hasImportAssociations) {
        return false;
      }
      if (this._hasImportAssociations) {
        if (!this.importAssociations.equals(other.importAssociations)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasCodeBlockMap ? codeBlockMap.hashCode() : 0);
      hash = hash * 31 + (_hasDefaultPackage ? defaultPackage.hashCode() : 0);
      hash = hash * 31 + (_hasInheritanceAssociations ? inheritanceAssociations.hashCode() : 0);
      hash = hash * 31 + (_hasTypeAssociations ? typeAssociations.hashCode() : 0);
      hash = hash * 31 + (_hasImportAssociations ? importAssociations.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonObject codeBlockMapOut = new JsonObject();
      ensureCodeBlockMap();
      for (Map.Entry<String, CodeBlockImpl> entry0 : codeBlockMap.entrySet()) {
        CodeBlockImpl codeBlockMap_ = entry0.getValue();
        JsonElement codeBlockMapOut_ = codeBlockMap_ == null ? JsonNull.INSTANCE : codeBlockMap_.toJsonElement();
        codeBlockMapOut.add(entry0.getKey(), codeBlockMapOut_);
      }
      result.add("codeBlockMap", codeBlockMapOut);

      JsonElement defaultPackageOut = defaultPackage == null ? JsonNull.INSTANCE : defaultPackage.toJsonElement();
      result.add("defaultPackage", defaultPackageOut);

      JsonArray inheritanceAssociationsOut = new JsonArray();
      ensureInheritanceAssociations();
      for (InheritanceAssociationImpl inheritanceAssociations_ : inheritanceAssociations) {
        JsonElement inheritanceAssociationsOut_ = inheritanceAssociations_ == null ? JsonNull.INSTANCE : inheritanceAssociations_.toJsonElement();
        inheritanceAssociationsOut.add(inheritanceAssociationsOut_);
      }
      result.add("inheritanceAssociations", inheritanceAssociationsOut);

      JsonArray typeAssociationsOut = new JsonArray();
      ensureTypeAssociations();
      for (TypeAssociationImpl typeAssociations_ : typeAssociations) {
        JsonElement typeAssociationsOut_ = typeAssociations_ == null ? JsonNull.INSTANCE : typeAssociations_.toJsonElement();
        typeAssociationsOut.add(typeAssociationsOut_);
      }
      result.add("typeAssociations", typeAssociationsOut);

      JsonArray importAssociationsOut = new JsonArray();
      ensureImportAssociations();
      for (ImportAssociationImpl importAssociations_ : importAssociations) {
        JsonElement importAssociationsOut_ = importAssociations_ == null ? JsonNull.INSTANCE : importAssociations_.toJsonElement();
        importAssociationsOut.add(importAssociationsOut_);
      }
      result.add("importAssociations", importAssociationsOut);
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

    public static CodeGraphImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeGraphImpl dto = new CodeGraphImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("codeBlockMap")) {
        JsonElement codeBlockMapIn = json.get("codeBlockMap");
        java.util.HashMap<String, CodeBlockImpl> codeBlockMapOut = null;
        if (codeBlockMapIn != null && !codeBlockMapIn.isJsonNull()) {
          codeBlockMapOut = new java.util.HashMap<String, CodeBlockImpl>();
          java.util.Set<Map.Entry<String, JsonElement>> entries0 = codeBlockMapIn.getAsJsonObject().entrySet();
          for (Map.Entry<String, JsonElement> entry0 : entries0) {
            JsonElement codeBlockMapIn_ = entry0.getValue();
            CodeBlockImpl codeBlockMapOut_ = CodeBlockImpl.fromJsonElement(codeBlockMapIn_);
            codeBlockMapOut.put(entry0.getKey(), codeBlockMapOut_);
          }
        }
        dto.setCodeBlockMap(codeBlockMapOut);
      }

      if (json.has("defaultPackage")) {
        JsonElement defaultPackageIn = json.get("defaultPackage");
        CodeBlockImpl defaultPackageOut = CodeBlockImpl.fromJsonElement(defaultPackageIn);
        dto.setDefaultPackage(defaultPackageOut);
      }

      if (json.has("inheritanceAssociations")) {
        JsonElement inheritanceAssociationsIn = json.get("inheritanceAssociations");
        java.util.ArrayList<InheritanceAssociationImpl> inheritanceAssociationsOut = null;
        if (inheritanceAssociationsIn != null && !inheritanceAssociationsIn.isJsonNull()) {
          inheritanceAssociationsOut = new java.util.ArrayList<InheritanceAssociationImpl>();
          java.util.Iterator<JsonElement> inheritanceAssociationsInIterator = inheritanceAssociationsIn.getAsJsonArray().iterator();
          while (inheritanceAssociationsInIterator.hasNext()) {
            JsonElement inheritanceAssociationsIn_ = inheritanceAssociationsInIterator.next();
            InheritanceAssociationImpl inheritanceAssociationsOut_ = InheritanceAssociationImpl.fromJsonElement(inheritanceAssociationsIn_);
            inheritanceAssociationsOut.add(inheritanceAssociationsOut_);
          }
        }
        dto.setInheritanceAssociations(inheritanceAssociationsOut);
      }

      if (json.has("typeAssociations")) {
        JsonElement typeAssociationsIn = json.get("typeAssociations");
        java.util.ArrayList<TypeAssociationImpl> typeAssociationsOut = null;
        if (typeAssociationsIn != null && !typeAssociationsIn.isJsonNull()) {
          typeAssociationsOut = new java.util.ArrayList<TypeAssociationImpl>();
          java.util.Iterator<JsonElement> typeAssociationsInIterator = typeAssociationsIn.getAsJsonArray().iterator();
          while (typeAssociationsInIterator.hasNext()) {
            JsonElement typeAssociationsIn_ = typeAssociationsInIterator.next();
            TypeAssociationImpl typeAssociationsOut_ = TypeAssociationImpl.fromJsonElement(typeAssociationsIn_);
            typeAssociationsOut.add(typeAssociationsOut_);
          }
        }
        dto.setTypeAssociations(typeAssociationsOut);
      }

      if (json.has("importAssociations")) {
        JsonElement importAssociationsIn = json.get("importAssociations");
        java.util.ArrayList<ImportAssociationImpl> importAssociationsOut = null;
        if (importAssociationsIn != null && !importAssociationsIn.isJsonNull()) {
          importAssociationsOut = new java.util.ArrayList<ImportAssociationImpl>();
          java.util.Iterator<JsonElement> importAssociationsInIterator = importAssociationsIn.getAsJsonArray().iterator();
          while (importAssociationsInIterator.hasNext()) {
            JsonElement importAssociationsIn_ = importAssociationsInIterator.next();
            ImportAssociationImpl importAssociationsOut_ = ImportAssociationImpl.fromJsonElement(importAssociationsIn_);
            importAssociationsOut.add(importAssociationsOut_);
          }
        }
        dto.setImportAssociations(importAssociationsOut);
      }

      return dto;
    }
    public static CodeGraphImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeGraphFreshnessImpl implements com.google.collide.dto.CodeGraphFreshness, JsonSerializable {

    public static CodeGraphFreshnessImpl make() {
      return new CodeGraphFreshnessImpl();
    }

    protected java.lang.String fullGraph;
    private boolean _hasFullGraph;
    protected java.lang.String fileTree;
    private boolean _hasFileTree;
    protected java.lang.String libsSubgraph;
    private boolean _hasLibsSubgraph;
    protected java.lang.String fileTreeHash;
    private boolean _hasFileTreeHash;
    protected java.lang.String workspaceTree;
    private boolean _hasWorkspaceTree;
    protected java.lang.String workspaceLinks;
    private boolean _hasWorkspaceLinks;
    protected java.lang.String fileReferences;
    private boolean _hasFileReferences;

    public boolean hasFullGraph() {
      return _hasFullGraph;
    }

    @Override
    public java.lang.String getFullGraph() {
      return fullGraph;
    }

    public CodeGraphFreshnessImpl setFullGraph(java.lang.String v) {
      _hasFullGraph = true;
      fullGraph = v;
      return this;
    }

    public boolean hasFileTree() {
      return _hasFileTree;
    }

    @Override
    public java.lang.String getFileTree() {
      return fileTree;
    }

    public CodeGraphFreshnessImpl setFileTree(java.lang.String v) {
      _hasFileTree = true;
      fileTree = v;
      return this;
    }

    public boolean hasLibsSubgraph() {
      return _hasLibsSubgraph;
    }

    @Override
    public java.lang.String getLibsSubgraph() {
      return libsSubgraph;
    }

    public CodeGraphFreshnessImpl setLibsSubgraph(java.lang.String v) {
      _hasLibsSubgraph = true;
      libsSubgraph = v;
      return this;
    }

    public boolean hasFileTreeHash() {
      return _hasFileTreeHash;
    }

    @Override
    public java.lang.String getFileTreeHash() {
      return fileTreeHash;
    }

    public CodeGraphFreshnessImpl setFileTreeHash(java.lang.String v) {
      _hasFileTreeHash = true;
      fileTreeHash = v;
      return this;
    }

    public boolean hasWorkspaceTree() {
      return _hasWorkspaceTree;
    }

    @Override
    public java.lang.String getWorkspaceTree() {
      return workspaceTree;
    }

    public CodeGraphFreshnessImpl setWorkspaceTree(java.lang.String v) {
      _hasWorkspaceTree = true;
      workspaceTree = v;
      return this;
    }

    public boolean hasWorkspaceLinks() {
      return _hasWorkspaceLinks;
    }

    @Override
    public java.lang.String getWorkspaceLinks() {
      return workspaceLinks;
    }

    public CodeGraphFreshnessImpl setWorkspaceLinks(java.lang.String v) {
      _hasWorkspaceLinks = true;
      workspaceLinks = v;
      return this;
    }

    public boolean hasFileReferences() {
      return _hasFileReferences;
    }

    @Override
    public java.lang.String getFileReferences() {
      return fileReferences;
    }

    public CodeGraphFreshnessImpl setFileReferences(java.lang.String v) {
      _hasFileReferences = true;
      fileReferences = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof CodeGraphFreshnessImpl)) {
        return false;
      }
      CodeGraphFreshnessImpl other = (CodeGraphFreshnessImpl) o;
      if (this._hasFullGraph != other._hasFullGraph) {
        return false;
      }
      if (this._hasFullGraph) {
        if (!this.fullGraph.equals(other.fullGraph)) {
          return false;
        }
      }
      if (this._hasFileTree != other._hasFileTree) {
        return false;
      }
      if (this._hasFileTree) {
        if (!this.fileTree.equals(other.fileTree)) {
          return false;
        }
      }
      if (this._hasLibsSubgraph != other._hasLibsSubgraph) {
        return false;
      }
      if (this._hasLibsSubgraph) {
        if (!this.libsSubgraph.equals(other.libsSubgraph)) {
          return false;
        }
      }
      if (this._hasFileTreeHash != other._hasFileTreeHash) {
        return false;
      }
      if (this._hasFileTreeHash) {
        if (!this.fileTreeHash.equals(other.fileTreeHash)) {
          return false;
        }
      }
      if (this._hasWorkspaceTree != other._hasWorkspaceTree) {
        return false;
      }
      if (this._hasWorkspaceTree) {
        if (!this.workspaceTree.equals(other.workspaceTree)) {
          return false;
        }
      }
      if (this._hasWorkspaceLinks != other._hasWorkspaceLinks) {
        return false;
      }
      if (this._hasWorkspaceLinks) {
        if (!this.workspaceLinks.equals(other.workspaceLinks)) {
          return false;
        }
      }
      if (this._hasFileReferences != other._hasFileReferences) {
        return false;
      }
      if (this._hasFileReferences) {
        if (!this.fileReferences.equals(other.fileReferences)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasFullGraph ? fullGraph.hashCode() : 0);
      hash = hash * 31 + (_hasFileTree ? fileTree.hashCode() : 0);
      hash = hash * 31 + (_hasLibsSubgraph ? libsSubgraph.hashCode() : 0);
      hash = hash * 31 + (_hasFileTreeHash ? fileTreeHash.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceTree ? workspaceTree.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceLinks ? workspaceLinks.hashCode() : 0);
      hash = hash * 31 + (_hasFileReferences ? fileReferences.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fullGraphOut = (fullGraph == null) ? JsonNull.INSTANCE : new JsonPrimitive(fullGraph);
      result.add("fullGraph", fullGraphOut);

      JsonElement fileTreeOut = (fileTree == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileTree);
      result.add("fileTree", fileTreeOut);

      JsonElement libsSubgraphOut = (libsSubgraph == null) ? JsonNull.INSTANCE : new JsonPrimitive(libsSubgraph);
      result.add("libsSubgraph", libsSubgraphOut);

      JsonElement fileTreeHashOut = (fileTreeHash == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileTreeHash);
      result.add("fileTreeHash", fileTreeHashOut);

      JsonElement workspaceTreeOut = (workspaceTree == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceTree);
      result.add("workspaceTree", workspaceTreeOut);

      JsonElement workspaceLinksOut = (workspaceLinks == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceLinks);
      result.add("workspaceLinks", workspaceLinksOut);

      JsonElement fileReferencesOut = (fileReferences == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileReferences);
      result.add("fileReferences", fileReferencesOut);
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

    public static CodeGraphFreshnessImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeGraphFreshnessImpl dto = new CodeGraphFreshnessImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fullGraph")) {
        JsonElement fullGraphIn = json.get("fullGraph");
        java.lang.String fullGraphOut = gson.fromJson(fullGraphIn, java.lang.String.class);
        dto.setFullGraph(fullGraphOut);
      }

      if (json.has("fileTree")) {
        JsonElement fileTreeIn = json.get("fileTree");
        java.lang.String fileTreeOut = gson.fromJson(fileTreeIn, java.lang.String.class);
        dto.setFileTree(fileTreeOut);
      }

      if (json.has("libsSubgraph")) {
        JsonElement libsSubgraphIn = json.get("libsSubgraph");
        java.lang.String libsSubgraphOut = gson.fromJson(libsSubgraphIn, java.lang.String.class);
        dto.setLibsSubgraph(libsSubgraphOut);
      }

      if (json.has("fileTreeHash")) {
        JsonElement fileTreeHashIn = json.get("fileTreeHash");
        java.lang.String fileTreeHashOut = gson.fromJson(fileTreeHashIn, java.lang.String.class);
        dto.setFileTreeHash(fileTreeHashOut);
      }

      if (json.has("workspaceTree")) {
        JsonElement workspaceTreeIn = json.get("workspaceTree");
        java.lang.String workspaceTreeOut = gson.fromJson(workspaceTreeIn, java.lang.String.class);
        dto.setWorkspaceTree(workspaceTreeOut);
      }

      if (json.has("workspaceLinks")) {
        JsonElement workspaceLinksIn = json.get("workspaceLinks");
        java.lang.String workspaceLinksOut = gson.fromJson(workspaceLinksIn, java.lang.String.class);
        dto.setWorkspaceLinks(workspaceLinksOut);
      }

      if (json.has("fileReferences")) {
        JsonElement fileReferencesIn = json.get("fileReferences");
        java.lang.String fileReferencesOut = gson.fromJson(fileReferencesIn, java.lang.String.class);
        dto.setFileReferences(fileReferencesOut);
      }

      return dto;
    }
    public static CodeGraphFreshnessImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeGraphRequestImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CodeGraphRequest, JsonSerializable {

    private CodeGraphRequestImpl() {
      super(11);
    }

    protected CodeGraphRequestImpl(int type) {
      super(type);
    }

    protected java.lang.String filePath;
    private boolean _hasFilePath;
    protected CodeGraphFreshnessImpl freshness;
    private boolean _hasFreshness;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasFilePath() {
      return _hasFilePath;
    }

    @Override
    public java.lang.String getFilePath() {
      return filePath;
    }

    public CodeGraphRequestImpl setFilePath(java.lang.String v) {
      _hasFilePath = true;
      filePath = v;
      return this;
    }

    public boolean hasFreshness() {
      return _hasFreshness;
    }

    @Override
    public com.google.collide.dto.CodeGraphFreshness getFreshness() {
      return freshness;
    }

    public CodeGraphRequestImpl setFreshness(CodeGraphFreshnessImpl v) {
      _hasFreshness = true;
      freshness = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public CodeGraphRequestImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CodeGraphRequestImpl)) {
        return false;
      }
      CodeGraphRequestImpl other = (CodeGraphRequestImpl) o;
      if (this._hasFilePath != other._hasFilePath) {
        return false;
      }
      if (this._hasFilePath) {
        if (!this.filePath.equals(other.filePath)) {
          return false;
        }
      }
      if (this._hasFreshness != other._hasFreshness) {
        return false;
      }
      if (this._hasFreshness) {
        if (!this.freshness.equals(other.freshness)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFilePath ? filePath.hashCode() : 0);
      hash = hash * 31 + (_hasFreshness ? freshness.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement filePathOut = (filePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(filePath);
      result.add("filePath", filePathOut);

      JsonElement freshnessOut = freshness == null ? JsonNull.INSTANCE : freshness.toJsonElement();
      result.add("freshness", freshnessOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static CodeGraphRequestImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeGraphRequestImpl dto = new CodeGraphRequestImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("filePath")) {
        JsonElement filePathIn = json.get("filePath");
        java.lang.String filePathOut = gson.fromJson(filePathIn, java.lang.String.class);
        dto.setFilePath(filePathOut);
      }

      if (json.has("freshness")) {
        JsonElement freshnessIn = json.get("freshness");
        CodeGraphFreshnessImpl freshnessOut = CodeGraphFreshnessImpl.fromJsonElement(freshnessIn);
        dto.setFreshness(freshnessOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static CodeGraphRequestImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeGraphResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CodeGraphResponse, JsonSerializable {

    private CodeGraphResponseImpl() {
      super(12);
    }

    protected CodeGraphResponseImpl(int type) {
      super(type);
    }

    public static CodeGraphResponseImpl make() {
      return new CodeGraphResponseImpl();
    }

    protected CodeGraphFreshnessImpl freshness;
    private boolean _hasFreshness;
    protected java.lang.String workspaceTreeJson;
    private boolean _hasWorkspaceTreeJson;
    protected java.lang.String workspaceLinksJson;
    private boolean _hasWorkspaceLinksJson;
    protected java.lang.String fullGraphJson;
    private boolean _hasFullGraphJson;
    protected java.lang.String fileReferencesJson;
    private boolean _hasFileReferencesJson;
    protected java.lang.String libsSubgraphJson;
    private boolean _hasLibsSubgraphJson;
    protected java.lang.String fileTreeJson;
    private boolean _hasFileTreeJson;

    public boolean hasFreshness() {
      return _hasFreshness;
    }

    @Override
    public com.google.collide.dto.CodeGraphFreshness getFreshness() {
      return freshness;
    }

    public CodeGraphResponseImpl setFreshness(CodeGraphFreshnessImpl v) {
      _hasFreshness = true;
      freshness = v;
      return this;
    }

    public boolean hasWorkspaceTreeJson() {
      return _hasWorkspaceTreeJson;
    }

    @Override
    public java.lang.String getWorkspaceTreeJson() {
      return workspaceTreeJson;
    }

    public CodeGraphResponseImpl setWorkspaceTreeJson(java.lang.String v) {
      _hasWorkspaceTreeJson = true;
      workspaceTreeJson = v;
      return this;
    }

    public boolean hasWorkspaceLinksJson() {
      return _hasWorkspaceLinksJson;
    }

    @Override
    public java.lang.String getWorkspaceLinksJson() {
      return workspaceLinksJson;
    }

    public CodeGraphResponseImpl setWorkspaceLinksJson(java.lang.String v) {
      _hasWorkspaceLinksJson = true;
      workspaceLinksJson = v;
      return this;
    }

    public boolean hasFullGraphJson() {
      return _hasFullGraphJson;
    }

    @Override
    public java.lang.String getFullGraphJson() {
      return fullGraphJson;
    }

    public CodeGraphResponseImpl setFullGraphJson(java.lang.String v) {
      _hasFullGraphJson = true;
      fullGraphJson = v;
      return this;
    }

    public boolean hasFileReferencesJson() {
      return _hasFileReferencesJson;
    }

    @Override
    public java.lang.String getFileReferencesJson() {
      return fileReferencesJson;
    }

    public CodeGraphResponseImpl setFileReferencesJson(java.lang.String v) {
      _hasFileReferencesJson = true;
      fileReferencesJson = v;
      return this;
    }

    public boolean hasLibsSubgraphJson() {
      return _hasLibsSubgraphJson;
    }

    @Override
    public java.lang.String getLibsSubgraphJson() {
      return libsSubgraphJson;
    }

    public CodeGraphResponseImpl setLibsSubgraphJson(java.lang.String v) {
      _hasLibsSubgraphJson = true;
      libsSubgraphJson = v;
      return this;
    }

    public boolean hasFileTreeJson() {
      return _hasFileTreeJson;
    }

    @Override
    public java.lang.String getFileTreeJson() {
      return fileTreeJson;
    }

    public CodeGraphResponseImpl setFileTreeJson(java.lang.String v) {
      _hasFileTreeJson = true;
      fileTreeJson = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CodeGraphResponseImpl)) {
        return false;
      }
      CodeGraphResponseImpl other = (CodeGraphResponseImpl) o;
      if (this._hasFreshness != other._hasFreshness) {
        return false;
      }
      if (this._hasFreshness) {
        if (!this.freshness.equals(other.freshness)) {
          return false;
        }
      }
      if (this._hasWorkspaceTreeJson != other._hasWorkspaceTreeJson) {
        return false;
      }
      if (this._hasWorkspaceTreeJson) {
        if (!this.workspaceTreeJson.equals(other.workspaceTreeJson)) {
          return false;
        }
      }
      if (this._hasWorkspaceLinksJson != other._hasWorkspaceLinksJson) {
        return false;
      }
      if (this._hasWorkspaceLinksJson) {
        if (!this.workspaceLinksJson.equals(other.workspaceLinksJson)) {
          return false;
        }
      }
      if (this._hasFullGraphJson != other._hasFullGraphJson) {
        return false;
      }
      if (this._hasFullGraphJson) {
        if (!this.fullGraphJson.equals(other.fullGraphJson)) {
          return false;
        }
      }
      if (this._hasFileReferencesJson != other._hasFileReferencesJson) {
        return false;
      }
      if (this._hasFileReferencesJson) {
        if (!this.fileReferencesJson.equals(other.fileReferencesJson)) {
          return false;
        }
      }
      if (this._hasLibsSubgraphJson != other._hasLibsSubgraphJson) {
        return false;
      }
      if (this._hasLibsSubgraphJson) {
        if (!this.libsSubgraphJson.equals(other.libsSubgraphJson)) {
          return false;
        }
      }
      if (this._hasFileTreeJson != other._hasFileTreeJson) {
        return false;
      }
      if (this._hasFileTreeJson) {
        if (!this.fileTreeJson.equals(other.fileTreeJson)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFreshness ? freshness.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceTreeJson ? workspaceTreeJson.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceLinksJson ? workspaceLinksJson.hashCode() : 0);
      hash = hash * 31 + (_hasFullGraphJson ? fullGraphJson.hashCode() : 0);
      hash = hash * 31 + (_hasFileReferencesJson ? fileReferencesJson.hashCode() : 0);
      hash = hash * 31 + (_hasLibsSubgraphJson ? libsSubgraphJson.hashCode() : 0);
      hash = hash * 31 + (_hasFileTreeJson ? fileTreeJson.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement freshnessOut = freshness == null ? JsonNull.INSTANCE : freshness.toJsonElement();
      result.add("freshness", freshnessOut);

      JsonElement workspaceTreeJsonOut = (workspaceTreeJson == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceTreeJson);
      result.add("workspaceTreeJson", workspaceTreeJsonOut);

      JsonElement workspaceLinksJsonOut = (workspaceLinksJson == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceLinksJson);
      result.add("workspaceLinksJson", workspaceLinksJsonOut);

      JsonElement fullGraphJsonOut = (fullGraphJson == null) ? JsonNull.INSTANCE : new JsonPrimitive(fullGraphJson);
      result.add("fullGraphJson", fullGraphJsonOut);

      JsonElement fileReferencesJsonOut = (fileReferencesJson == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileReferencesJson);
      result.add("fileReferencesJson", fileReferencesJsonOut);

      JsonElement libsSubgraphJsonOut = (libsSubgraphJson == null) ? JsonNull.INSTANCE : new JsonPrimitive(libsSubgraphJson);
      result.add("libsSubgraphJson", libsSubgraphJsonOut);

      JsonElement fileTreeJsonOut = (fileTreeJson == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileTreeJson);
      result.add("fileTreeJson", fileTreeJsonOut);
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

    public static CodeGraphResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeGraphResponseImpl dto = new CodeGraphResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("freshness")) {
        JsonElement freshnessIn = json.get("freshness");
        CodeGraphFreshnessImpl freshnessOut = CodeGraphFreshnessImpl.fromJsonElement(freshnessIn);
        dto.setFreshness(freshnessOut);
      }

      if (json.has("workspaceTreeJson")) {
        JsonElement workspaceTreeJsonIn = json.get("workspaceTreeJson");
        java.lang.String workspaceTreeJsonOut = gson.fromJson(workspaceTreeJsonIn, java.lang.String.class);
        dto.setWorkspaceTreeJson(workspaceTreeJsonOut);
      }

      if (json.has("workspaceLinksJson")) {
        JsonElement workspaceLinksJsonIn = json.get("workspaceLinksJson");
        java.lang.String workspaceLinksJsonOut = gson.fromJson(workspaceLinksJsonIn, java.lang.String.class);
        dto.setWorkspaceLinksJson(workspaceLinksJsonOut);
      }

      if (json.has("fullGraphJson")) {
        JsonElement fullGraphJsonIn = json.get("fullGraphJson");
        java.lang.String fullGraphJsonOut = gson.fromJson(fullGraphJsonIn, java.lang.String.class);
        dto.setFullGraphJson(fullGraphJsonOut);
      }

      if (json.has("fileReferencesJson")) {
        JsonElement fileReferencesJsonIn = json.get("fileReferencesJson");
        java.lang.String fileReferencesJsonOut = gson.fromJson(fileReferencesJsonIn, java.lang.String.class);
        dto.setFileReferencesJson(fileReferencesJsonOut);
      }

      if (json.has("libsSubgraphJson")) {
        JsonElement libsSubgraphJsonIn = json.get("libsSubgraphJson");
        java.lang.String libsSubgraphJsonOut = gson.fromJson(libsSubgraphJsonIn, java.lang.String.class);
        dto.setLibsSubgraphJson(libsSubgraphJsonOut);
      }

      if (json.has("fileTreeJson")) {
        JsonElement fileTreeJsonIn = json.get("fileTreeJson");
        java.lang.String fileTreeJsonOut = gson.fromJson(fileTreeJsonIn, java.lang.String.class);
        dto.setFileTreeJson(fileTreeJsonOut);
      }

      return dto;
    }
    public static CodeGraphResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeReferenceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CodeReference, JsonSerializable {

    private CodeReferenceImpl() {
      super(13);
    }

    protected CodeReferenceImpl(int type) {
      super(type);
    }

    public static CodeReferenceImpl make() {
      return new CodeReferenceImpl();
    }

    protected com.google.collide.dto.CodeReference.Type referenceType;
    private boolean _hasReferenceType;
    protected FilePositionImpl referenceStart;
    private boolean _hasReferenceStart;
    protected FilePositionImpl referenceEnd;
    private boolean _hasReferenceEnd;
    protected java.lang.String targetFilePath;
    private boolean _hasTargetFilePath;
    protected FilePositionImpl targetStart;
    private boolean _hasTargetStart;
    protected FilePositionImpl targetEnd;
    private boolean _hasTargetEnd;
    protected java.lang.String targetSnippet;
    private boolean _hasTargetSnippet;

    public boolean hasReferenceType() {
      return _hasReferenceType;
    }

    @Override
    public com.google.collide.dto.CodeReference.Type getReferenceType() {
      return referenceType;
    }

    public CodeReferenceImpl setReferenceType(com.google.collide.dto.CodeReference.Type v) {
      _hasReferenceType = true;
      referenceType = v;
      return this;
    }

    public boolean hasReferenceStart() {
      return _hasReferenceStart;
    }

    @Override
    public com.google.collide.dto.FilePosition getReferenceStart() {
      return referenceStart;
    }

    public CodeReferenceImpl setReferenceStart(FilePositionImpl v) {
      _hasReferenceStart = true;
      referenceStart = v;
      return this;
    }

    public boolean hasReferenceEnd() {
      return _hasReferenceEnd;
    }

    @Override
    public com.google.collide.dto.FilePosition getReferenceEnd() {
      return referenceEnd;
    }

    public CodeReferenceImpl setReferenceEnd(FilePositionImpl v) {
      _hasReferenceEnd = true;
      referenceEnd = v;
      return this;
    }

    public boolean hasTargetFilePath() {
      return _hasTargetFilePath;
    }

    @Override
    public java.lang.String getTargetFilePath() {
      return targetFilePath;
    }

    public CodeReferenceImpl setTargetFilePath(java.lang.String v) {
      _hasTargetFilePath = true;
      targetFilePath = v;
      return this;
    }

    public boolean hasTargetStart() {
      return _hasTargetStart;
    }

    @Override
    public com.google.collide.dto.FilePosition getTargetStart() {
      return targetStart;
    }

    public CodeReferenceImpl setTargetStart(FilePositionImpl v) {
      _hasTargetStart = true;
      targetStart = v;
      return this;
    }

    public boolean hasTargetEnd() {
      return _hasTargetEnd;
    }

    @Override
    public com.google.collide.dto.FilePosition getTargetEnd() {
      return targetEnd;
    }

    public CodeReferenceImpl setTargetEnd(FilePositionImpl v) {
      _hasTargetEnd = true;
      targetEnd = v;
      return this;
    }

    public boolean hasTargetSnippet() {
      return _hasTargetSnippet;
    }

    @Override
    public java.lang.String getTargetSnippet() {
      return targetSnippet;
    }

    public CodeReferenceImpl setTargetSnippet(java.lang.String v) {
      _hasTargetSnippet = true;
      targetSnippet = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CodeReferenceImpl)) {
        return false;
      }
      CodeReferenceImpl other = (CodeReferenceImpl) o;
      if (this._hasReferenceType != other._hasReferenceType) {
        return false;
      }
      if (this._hasReferenceType) {
        if (!this.referenceType.equals(other.referenceType)) {
          return false;
        }
      }
      if (this._hasReferenceStart != other._hasReferenceStart) {
        return false;
      }
      if (this._hasReferenceStart) {
        if (!this.referenceStart.equals(other.referenceStart)) {
          return false;
        }
      }
      if (this._hasReferenceEnd != other._hasReferenceEnd) {
        return false;
      }
      if (this._hasReferenceEnd) {
        if (!this.referenceEnd.equals(other.referenceEnd)) {
          return false;
        }
      }
      if (this._hasTargetFilePath != other._hasTargetFilePath) {
        return false;
      }
      if (this._hasTargetFilePath) {
        if (!this.targetFilePath.equals(other.targetFilePath)) {
          return false;
        }
      }
      if (this._hasTargetStart != other._hasTargetStart) {
        return false;
      }
      if (this._hasTargetStart) {
        if (!this.targetStart.equals(other.targetStart)) {
          return false;
        }
      }
      if (this._hasTargetEnd != other._hasTargetEnd) {
        return false;
      }
      if (this._hasTargetEnd) {
        if (!this.targetEnd.equals(other.targetEnd)) {
          return false;
        }
      }
      if (this._hasTargetSnippet != other._hasTargetSnippet) {
        return false;
      }
      if (this._hasTargetSnippet) {
        if (!this.targetSnippet.equals(other.targetSnippet)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasReferenceType ? referenceType.hashCode() : 0);
      hash = hash * 31 + (_hasReferenceStart ? referenceStart.hashCode() : 0);
      hash = hash * 31 + (_hasReferenceEnd ? referenceEnd.hashCode() : 0);
      hash = hash * 31 + (_hasTargetFilePath ? targetFilePath.hashCode() : 0);
      hash = hash * 31 + (_hasTargetStart ? targetStart.hashCode() : 0);
      hash = hash * 31 + (_hasTargetEnd ? targetEnd.hashCode() : 0);
      hash = hash * 31 + (_hasTargetSnippet ? targetSnippet.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonArray result = new JsonArray();

      JsonElement referenceTypeOut = (referenceType == null) ? JsonNull.INSTANCE : new JsonPrimitive(referenceType.name());
      result.add(referenceTypeOut);

      JsonElement referenceStartOut = referenceStart == null ? JsonNull.INSTANCE : referenceStart.toJsonElement();
      result.add(referenceStartOut);

      JsonElement referenceEndOut = referenceEnd == null ? JsonNull.INSTANCE : referenceEnd.toJsonElement();
      result.add(referenceEndOut);

      JsonElement targetFilePathOut = (targetFilePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetFilePath);
      result.add(targetFilePathOut);

      JsonElement targetStartOut = targetStart == null ? JsonNull.INSTANCE : targetStart.toJsonElement();
      result.add(targetStartOut);

      JsonElement targetEndOut = targetEnd == null ? JsonNull.INSTANCE : targetEnd.toJsonElement();
      result.add(targetEndOut);

      JsonElement targetSnippetOut = (targetSnippet == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetSnippet);
      result.add(targetSnippetOut);
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

    public static CodeReferenceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeReferenceImpl dto = new CodeReferenceImpl();
      JsonArray json = jsonElem.getAsJsonArray();

      if (0 < json.size()) {
        JsonElement referenceTypeIn = json.get(0);
        com.google.collide.dto.CodeReference.Type referenceTypeOut = gson.fromJson(referenceTypeIn, com.google.collide.dto.CodeReference.Type.class);
        dto.setReferenceType(referenceTypeOut);
      }

      if (1 < json.size()) {
        JsonElement referenceStartIn = json.get(1);
        FilePositionImpl referenceStartOut = FilePositionImpl.fromJsonElement(referenceStartIn);
        dto.setReferenceStart(referenceStartOut);
      }

      if (2 < json.size()) {
        JsonElement referenceEndIn = json.get(2);
        FilePositionImpl referenceEndOut = FilePositionImpl.fromJsonElement(referenceEndIn);
        dto.setReferenceEnd(referenceEndOut);
      }

      if (3 < json.size()) {
        JsonElement targetFilePathIn = json.get(3);
        java.lang.String targetFilePathOut = gson.fromJson(targetFilePathIn, java.lang.String.class);
        dto.setTargetFilePath(targetFilePathOut);
      }

      if (4 < json.size()) {
        JsonElement targetStartIn = json.get(4);
        FilePositionImpl targetStartOut = FilePositionImpl.fromJsonElement(targetStartIn);
        dto.setTargetStart(targetStartOut);
      }

      if (5 < json.size()) {
        JsonElement targetEndIn = json.get(5);
        FilePositionImpl targetEndOut = FilePositionImpl.fromJsonElement(targetEndIn);
        dto.setTargetEnd(targetEndOut);
      }

      if (6 < json.size()) {
        JsonElement targetSnippetIn = json.get(6);
        java.lang.String targetSnippetOut = gson.fromJson(targetSnippetIn, java.lang.String.class);
        dto.setTargetSnippet(targetSnippetOut);
      }

      return dto;
    }
    public static CodeReferenceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CodeReferencesImpl implements com.google.collide.dto.CodeReferences, JsonSerializable {

    public static CodeReferencesImpl make() {
      return new CodeReferencesImpl();
    }

    protected java.util.List<CodeReferenceImpl> references;
    private boolean _hasReferences;

    public boolean hasReferences() {
      return _hasReferences;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.CodeReference> getReferences() {
      ensureReferences();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(references);
    }

    public CodeReferencesImpl setReferences(java.util.List<CodeReferenceImpl> v) {
      _hasReferences = true;
      references = v;
      return this;
    }

    public void addReferences(CodeReferenceImpl v) {
      ensureReferences();
      references.add(v);
    }

    public void clearReferences() {
      ensureReferences();
      references.clear();
    }

    private void ensureReferences() {
      if (!_hasReferences) {
        setReferences(references != null ? references : new java.util.ArrayList<CodeReferenceImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof CodeReferencesImpl)) {
        return false;
      }
      CodeReferencesImpl other = (CodeReferencesImpl) o;
      if (this._hasReferences != other._hasReferences) {
        return false;
      }
      if (this._hasReferences) {
        if (!this.references.equals(other.references)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasReferences ? references.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray referencesOut = new JsonArray();
      ensureReferences();
      for (CodeReferenceImpl references_ : references) {
        JsonElement referencesOut_ = references_ == null ? JsonNull.INSTANCE : references_.toJsonElement();
        referencesOut.add(referencesOut_);
      }
      result.add("references", referencesOut);
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

    public static CodeReferencesImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CodeReferencesImpl dto = new CodeReferencesImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("references")) {
        JsonElement referencesIn = json.get("references");
        java.util.ArrayList<CodeReferenceImpl> referencesOut = null;
        if (referencesIn != null && !referencesIn.isJsonNull()) {
          referencesOut = new java.util.ArrayList<CodeReferenceImpl>();
          java.util.Iterator<JsonElement> referencesInIterator = referencesIn.getAsJsonArray().iterator();
          while (referencesInIterator.hasNext()) {
            JsonElement referencesIn_ = referencesInIterator.next();
            CodeReferenceImpl referencesOut_ = CodeReferenceImpl.fromJsonElement(referencesIn_);
            referencesOut.add(referencesOut_);
          }
        }
        dto.setReferences(referencesOut);
      }

      return dto;
    }
    public static CodeReferencesImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ConflictChunkImpl implements com.google.collide.dto.ConflictChunk, JsonSerializable {

    public static ConflictChunkImpl make() {
      return new ConflictChunkImpl();
    }

    protected java.lang.String localText;
    private boolean _hasLocalText;
    protected int startLineNumber;
    private boolean _hasStartLineNumber;
    protected int endLineNumber;
    private boolean _hasEndLineNumber;
    protected java.lang.String baseText;
    private boolean _hasBaseText;
    protected java.lang.String remoteText;
    private boolean _hasRemoteText;
    protected boolean isResolved;
    private boolean _hasIsResolved;

    public boolean hasLocalText() {
      return _hasLocalText;
    }

    @Override
    public java.lang.String getLocalText() {
      return localText;
    }

    public ConflictChunkImpl setLocalText(java.lang.String v) {
      _hasLocalText = true;
      localText = v;
      return this;
    }

    public boolean hasStartLineNumber() {
      return _hasStartLineNumber;
    }

    @Override
    public int getStartLineNumber() {
      return startLineNumber;
    }

    public ConflictChunkImpl setStartLineNumber(int v) {
      _hasStartLineNumber = true;
      startLineNumber = v;
      return this;
    }

    public boolean hasEndLineNumber() {
      return _hasEndLineNumber;
    }

    @Override
    public int getEndLineNumber() {
      return endLineNumber;
    }

    public ConflictChunkImpl setEndLineNumber(int v) {
      _hasEndLineNumber = true;
      endLineNumber = v;
      return this;
    }

    public boolean hasBaseText() {
      return _hasBaseText;
    }

    @Override
    public java.lang.String getBaseText() {
      return baseText;
    }

    public ConflictChunkImpl setBaseText(java.lang.String v) {
      _hasBaseText = true;
      baseText = v;
      return this;
    }

    public boolean hasRemoteText() {
      return _hasRemoteText;
    }

    @Override
    public java.lang.String getRemoteText() {
      return remoteText;
    }

    public ConflictChunkImpl setRemoteText(java.lang.String v) {
      _hasRemoteText = true;
      remoteText = v;
      return this;
    }

    public boolean hasIsResolved() {
      return _hasIsResolved;
    }

    @Override
    public boolean isResolved() {
      return isResolved;
    }

    public ConflictChunkImpl setIsResolved(boolean v) {
      _hasIsResolved = true;
      isResolved = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ConflictChunkImpl)) {
        return false;
      }
      ConflictChunkImpl other = (ConflictChunkImpl) o;
      if (this._hasLocalText != other._hasLocalText) {
        return false;
      }
      if (this._hasLocalText) {
        if (!this.localText.equals(other.localText)) {
          return false;
        }
      }
      if (this._hasStartLineNumber != other._hasStartLineNumber) {
        return false;
      }
      if (this._hasStartLineNumber) {
        if (this.startLineNumber != other.startLineNumber) {
          return false;
        }
      }
      if (this._hasEndLineNumber != other._hasEndLineNumber) {
        return false;
      }
      if (this._hasEndLineNumber) {
        if (this.endLineNumber != other.endLineNumber) {
          return false;
        }
      }
      if (this._hasBaseText != other._hasBaseText) {
        return false;
      }
      if (this._hasBaseText) {
        if (!this.baseText.equals(other.baseText)) {
          return false;
        }
      }
      if (this._hasRemoteText != other._hasRemoteText) {
        return false;
      }
      if (this._hasRemoteText) {
        if (!this.remoteText.equals(other.remoteText)) {
          return false;
        }
      }
      if (this._hasIsResolved != other._hasIsResolved) {
        return false;
      }
      if (this._hasIsResolved) {
        if (this.isResolved != other.isResolved) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasLocalText ? localText.hashCode() : 0);
      hash = hash * 31 + (_hasStartLineNumber ? java.lang.Integer.valueOf(startLineNumber).hashCode() : 0);
      hash = hash * 31 + (_hasEndLineNumber ? java.lang.Integer.valueOf(endLineNumber).hashCode() : 0);
      hash = hash * 31 + (_hasBaseText ? baseText.hashCode() : 0);
      hash = hash * 31 + (_hasRemoteText ? remoteText.hashCode() : 0);
      hash = hash * 31 + (_hasIsResolved ? java.lang.Boolean.valueOf(isResolved).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement localTextOut = (localText == null) ? JsonNull.INSTANCE : new JsonPrimitive(localText);
      result.add("localText", localTextOut);

      JsonPrimitive startLineNumberOut = new JsonPrimitive(startLineNumber);
      result.add("startLineNumber", startLineNumberOut);

      JsonPrimitive endLineNumberOut = new JsonPrimitive(endLineNumber);
      result.add("endLineNumber", endLineNumberOut);

      JsonElement baseTextOut = (baseText == null) ? JsonNull.INSTANCE : new JsonPrimitive(baseText);
      result.add("baseText", baseTextOut);

      JsonElement remoteTextOut = (remoteText == null) ? JsonNull.INSTANCE : new JsonPrimitive(remoteText);
      result.add("remoteText", remoteTextOut);

      JsonPrimitive isResolvedOut = new JsonPrimitive(isResolved);
      result.add("isResolved", isResolvedOut);
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

    public static ConflictChunkImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ConflictChunkImpl dto = new ConflictChunkImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("localText")) {
        JsonElement localTextIn = json.get("localText");
        java.lang.String localTextOut = gson.fromJson(localTextIn, java.lang.String.class);
        dto.setLocalText(localTextOut);
      }

      if (json.has("startLineNumber")) {
        JsonElement startLineNumberIn = json.get("startLineNumber");
        int startLineNumberOut = startLineNumberIn.getAsInt();
        dto.setStartLineNumber(startLineNumberOut);
      }

      if (json.has("endLineNumber")) {
        JsonElement endLineNumberIn = json.get("endLineNumber");
        int endLineNumberOut = endLineNumberIn.getAsInt();
        dto.setEndLineNumber(endLineNumberOut);
      }

      if (json.has("baseText")) {
        JsonElement baseTextIn = json.get("baseText");
        java.lang.String baseTextOut = gson.fromJson(baseTextIn, java.lang.String.class);
        dto.setBaseText(baseTextOut);
      }

      if (json.has("remoteText")) {
        JsonElement remoteTextIn = json.get("remoteText");
        java.lang.String remoteTextOut = gson.fromJson(remoteTextIn, java.lang.String.class);
        dto.setRemoteText(remoteTextOut);
      }

      if (json.has("isResolved")) {
        JsonElement isResolvedIn = json.get("isResolved");
        boolean isResolvedOut = isResolvedIn.getAsBoolean();
        dto.setIsResolved(isResolvedOut);
      }

      return dto;
    }
    public static ConflictChunkImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ConflictChunkResolvedImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ConflictChunkResolved, JsonSerializable {

    private ConflictChunkResolvedImpl() {
      super(14);
    }

    protected ConflictChunkResolvedImpl(int type) {
      super(type);
    }

    public static ConflictChunkResolvedImpl make() {
      return new ConflictChunkResolvedImpl();
    }

    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected int conflictChunkIndex;
    private boolean _hasConflictChunkIndex;
    protected ConflictHandleImpl conflictHandle;
    private boolean _hasConflictHandle;
    protected boolean isResolved;
    private boolean _hasIsResolved;

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public ConflictChunkResolvedImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasConflictChunkIndex() {
      return _hasConflictChunkIndex;
    }

    @Override
    public int getConflictChunkIndex() {
      return conflictChunkIndex;
    }

    public ConflictChunkResolvedImpl setConflictChunkIndex(int v) {
      _hasConflictChunkIndex = true;
      conflictChunkIndex = v;
      return this;
    }

    public boolean hasConflictHandle() {
      return _hasConflictHandle;
    }

    @Override
    public com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() {
      return conflictHandle;
    }

    public ConflictChunkResolvedImpl setConflictHandle(ConflictHandleImpl v) {
      _hasConflictHandle = true;
      conflictHandle = v;
      return this;
    }

    public boolean hasIsResolved() {
      return _hasIsResolved;
    }

    @Override
    public boolean isResolved() {
      return isResolved;
    }

    public ConflictChunkResolvedImpl setIsResolved(boolean v) {
      _hasIsResolved = true;
      isResolved = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ConflictChunkResolvedImpl)) {
        return false;
      }
      ConflictChunkResolvedImpl other = (ConflictChunkResolvedImpl) o;
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasConflictChunkIndex != other._hasConflictChunkIndex) {
        return false;
      }
      if (this._hasConflictChunkIndex) {
        if (this.conflictChunkIndex != other.conflictChunkIndex) {
          return false;
        }
      }
      if (this._hasConflictHandle != other._hasConflictHandle) {
        return false;
      }
      if (this._hasConflictHandle) {
        if (!this.conflictHandle.equals(other.conflictHandle)) {
          return false;
        }
      }
      if (this._hasIsResolved != other._hasIsResolved) {
        return false;
      }
      if (this._hasIsResolved) {
        if (this.isResolved != other.isResolved) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasConflictChunkIndex ? java.lang.Integer.valueOf(conflictChunkIndex).hashCode() : 0);
      hash = hash * 31 + (_hasConflictHandle ? conflictHandle.hashCode() : 0);
      hash = hash * 31 + (_hasIsResolved ? java.lang.Boolean.valueOf(isResolved).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonPrimitive conflictChunkIndexOut = new JsonPrimitive(conflictChunkIndex);
      result.add("conflictChunkIndex", conflictChunkIndexOut);

      JsonElement conflictHandleOut = conflictHandle == null ? JsonNull.INSTANCE : conflictHandle.toJsonElement();
      result.add("conflictHandle", conflictHandleOut);

      JsonPrimitive isResolvedOut = new JsonPrimitive(isResolved);
      result.add("isResolved", isResolvedOut);
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

    public static ConflictChunkResolvedImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ConflictChunkResolvedImpl dto = new ConflictChunkResolvedImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("conflictChunkIndex")) {
        JsonElement conflictChunkIndexIn = json.get("conflictChunkIndex");
        int conflictChunkIndexOut = conflictChunkIndexIn.getAsInt();
        dto.setConflictChunkIndex(conflictChunkIndexOut);
      }

      if (json.has("conflictHandle")) {
        JsonElement conflictHandleIn = json.get("conflictHandle");
        ConflictHandleImpl conflictHandleOut = ConflictHandleImpl.fromJsonElement(conflictHandleIn);
        dto.setConflictHandle(conflictHandleOut);
      }

      if (json.has("isResolved")) {
        JsonElement isResolvedIn = json.get("isResolved");
        boolean isResolvedOut = isResolvedIn.getAsBoolean();
        dto.setIsResolved(isResolvedOut);
      }

      return dto;
    }
    public static ConflictChunkResolvedImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CreateAppEngineAppStatusImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CreateAppEngineAppStatus, JsonSerializable {

    private CreateAppEngineAppStatusImpl() {
      super(15);
    }

    protected CreateAppEngineAppStatusImpl(int type) {
      super(type);
    }

    public static CreateAppEngineAppStatusImpl make() {
      return new CreateAppEngineAppStatusImpl();
    }

    protected com.google.collide.dto.CreateAppEngineAppStatus.Status status;
    private boolean _hasStatus;

    public boolean hasStatus() {
      return _hasStatus;
    }

    @Override
    public com.google.collide.dto.CreateAppEngineAppStatus.Status getStatus() {
      return status;
    }

    public CreateAppEngineAppStatusImpl setStatus(com.google.collide.dto.CreateAppEngineAppStatus.Status v) {
      _hasStatus = true;
      status = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CreateAppEngineAppStatusImpl)) {
        return false;
      }
      CreateAppEngineAppStatusImpl other = (CreateAppEngineAppStatusImpl) o;
      if (this._hasStatus != other._hasStatus) {
        return false;
      }
      if (this._hasStatus) {
        if (!this.status.equals(other.status)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasStatus ? status.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement statusOut = (status == null) ? JsonNull.INSTANCE : new JsonPrimitive(status.name());
      result.add("status", statusOut);
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

    public static CreateAppEngineAppStatusImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CreateAppEngineAppStatusImpl dto = new CreateAppEngineAppStatusImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("status")) {
        JsonElement statusIn = json.get("status");
        com.google.collide.dto.CreateAppEngineAppStatus.Status statusOut = gson.fromJson(statusIn, com.google.collide.dto.CreateAppEngineAppStatus.Status.class);
        dto.setStatus(statusOut);
      }

      return dto;
    }
    public static CreateAppEngineAppStatusImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CreateProjectImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CreateProject, JsonSerializable {

    private CreateProjectImpl() {
      super(16);
    }

    protected CreateProjectImpl(int type) {
      super(type);
    }

    protected java.lang.String summary;
    private boolean _hasSummary;
    protected java.lang.String name;
    private boolean _hasName;

    public boolean hasSummary() {
      return _hasSummary;
    }

    @Override
    public java.lang.String getSummary() {
      return summary;
    }

    public CreateProjectImpl setSummary(java.lang.String v) {
      _hasSummary = true;
      summary = v;
      return this;
    }

    public boolean hasName() {
      return _hasName;
    }

    @Override
    public java.lang.String getName() {
      return name;
    }

    public CreateProjectImpl setName(java.lang.String v) {
      _hasName = true;
      name = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CreateProjectImpl)) {
        return false;
      }
      CreateProjectImpl other = (CreateProjectImpl) o;
      if (this._hasSummary != other._hasSummary) {
        return false;
      }
      if (this._hasSummary) {
        if (!this.summary.equals(other.summary)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasSummary ? summary.hashCode() : 0);
      hash = hash * 31 + (_hasName ? name.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement summaryOut = (summary == null) ? JsonNull.INSTANCE : new JsonPrimitive(summary);
      result.add("summary", summaryOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);
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

    public static CreateProjectImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CreateProjectImpl dto = new CreateProjectImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("summary")) {
        JsonElement summaryIn = json.get("summary");
        java.lang.String summaryOut = gson.fromJson(summaryIn, java.lang.String.class);
        dto.setSummary(summaryOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      return dto;
    }
    public static CreateProjectImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CreateProjectResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CreateProjectResponse, JsonSerializable {

    private CreateProjectResponseImpl() {
      super(17);
    }

    protected CreateProjectResponseImpl(int type) {
      super(type);
    }

    public static CreateProjectResponseImpl make() {
      return new CreateProjectResponseImpl();
    }

    protected ProjectInfoImpl project;
    private boolean _hasProject;

    public boolean hasProject() {
      return _hasProject;
    }

    @Override
    public com.google.collide.dto.ProjectInfo getProject() {
      return project;
    }

    public CreateProjectResponseImpl setProject(ProjectInfoImpl v) {
      _hasProject = true;
      project = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CreateProjectResponseImpl)) {
        return false;
      }
      CreateProjectResponseImpl other = (CreateProjectResponseImpl) o;
      if (this._hasProject != other._hasProject) {
        return false;
      }
      if (this._hasProject) {
        if (!this.project.equals(other.project)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasProject ? project.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement projectOut = project == null ? JsonNull.INSTANCE : project.toJsonElement();
      result.add("project", projectOut);
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

    public static CreateProjectResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CreateProjectResponseImpl dto = new CreateProjectResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("project")) {
        JsonElement projectIn = json.get("project");
        ProjectInfoImpl projectOut = ProjectInfoImpl.fromJsonElement(projectIn);
        dto.setProject(projectOut);
      }

      return dto;
    }
    public static CreateProjectResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CreateWorkspaceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CreateWorkspace, JsonSerializable {

    private CreateWorkspaceImpl() {
      super(18);
    }

    protected CreateWorkspaceImpl(int type) {
      super(type);
    }

    protected java.lang.String baseWorkspaceId;
    private boolean _hasBaseWorkspaceId;
    protected java.lang.String name;
    private boolean _hasName;
    protected java.lang.String description;
    private boolean _hasDescription;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasBaseWorkspaceId() {
      return _hasBaseWorkspaceId;
    }

    @Override
    public java.lang.String getBaseWorkspaceId() {
      return baseWorkspaceId;
    }

    public CreateWorkspaceImpl setBaseWorkspaceId(java.lang.String v) {
      _hasBaseWorkspaceId = true;
      baseWorkspaceId = v;
      return this;
    }

    public boolean hasName() {
      return _hasName;
    }

    @Override
    public java.lang.String getName() {
      return name;
    }

    public CreateWorkspaceImpl setName(java.lang.String v) {
      _hasName = true;
      name = v;
      return this;
    }

    public boolean hasDescription() {
      return _hasDescription;
    }

    @Override
    public java.lang.String getDescription() {
      return description;
    }

    public CreateWorkspaceImpl setDescription(java.lang.String v) {
      _hasDescription = true;
      description = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public CreateWorkspaceImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CreateWorkspaceImpl)) {
        return false;
      }
      CreateWorkspaceImpl other = (CreateWorkspaceImpl) o;
      if (this._hasBaseWorkspaceId != other._hasBaseWorkspaceId) {
        return false;
      }
      if (this._hasBaseWorkspaceId) {
        if (!this.baseWorkspaceId.equals(other.baseWorkspaceId)) {
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
      if (this._hasDescription != other._hasDescription) {
        return false;
      }
      if (this._hasDescription) {
        if (!this.description.equals(other.description)) {
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
      hash = hash * 31 + (_hasBaseWorkspaceId ? baseWorkspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasName ? name.hashCode() : 0);
      hash = hash * 31 + (_hasDescription ? description.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement baseWorkspaceIdOut = (baseWorkspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(baseWorkspaceId);
      result.add("baseWorkspaceId", baseWorkspaceIdOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);

      JsonElement descriptionOut = (description == null) ? JsonNull.INSTANCE : new JsonPrimitive(description);
      result.add("description", descriptionOut);

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

    public static CreateWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CreateWorkspaceImpl dto = new CreateWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("baseWorkspaceId")) {
        JsonElement baseWorkspaceIdIn = json.get("baseWorkspaceId");
        java.lang.String baseWorkspaceIdOut = gson.fromJson(baseWorkspaceIdIn, java.lang.String.class);
        dto.setBaseWorkspaceId(baseWorkspaceIdOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      if (json.has("description")) {
        JsonElement descriptionIn = json.get("description");
        java.lang.String descriptionOut = gson.fromJson(descriptionIn, java.lang.String.class);
        dto.setDescription(descriptionOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static CreateWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CreateWorkspaceResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CreateWorkspaceResponse, JsonSerializable {

    private CreateWorkspaceResponseImpl() {
      super(19);
    }

    protected CreateWorkspaceResponseImpl(int type) {
      super(type);
    }

    public static CreateWorkspaceResponseImpl make() {
      return new CreateWorkspaceResponseImpl();
    }

    protected WorkspaceInfoImpl workspace;
    private boolean _hasWorkspace;

    public boolean hasWorkspace() {
      return _hasWorkspace;
    }

    @Override
    public com.google.collide.dto.WorkspaceInfo getWorkspace() {
      return workspace;
    }

    public CreateWorkspaceResponseImpl setWorkspace(WorkspaceInfoImpl v) {
      _hasWorkspace = true;
      workspace = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CreateWorkspaceResponseImpl)) {
        return false;
      }
      CreateWorkspaceResponseImpl other = (CreateWorkspaceResponseImpl) o;
      if (this._hasWorkspace != other._hasWorkspace) {
        return false;
      }
      if (this._hasWorkspace) {
        if (!this.workspace.equals(other.workspace)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspace ? workspace.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceOut = workspace == null ? JsonNull.INSTANCE : workspace.toJsonElement();
      result.add("workspace", workspaceOut);
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

    public static CreateWorkspaceResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CreateWorkspaceResponseImpl dto = new CreateWorkspaceResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspace")) {
        JsonElement workspaceIn = json.get("workspace");
        WorkspaceInfoImpl workspaceOut = WorkspaceInfoImpl.fromJsonElement(workspaceIn);
        dto.setWorkspace(workspaceOut);
      }

      return dto;
    }
    public static CreateWorkspaceResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class CubePingImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.CubePing, JsonSerializable {

    private CubePingImpl() {
      super(20);
    }

    protected CubePingImpl(int type) {
      super(type);
    }

    public static CubePingImpl make() {
      return new CubePingImpl();
    }

    protected java.lang.String fullGraphFreshness;
    private boolean _hasFullGraphFreshness;

    public boolean hasFullGraphFreshness() {
      return _hasFullGraphFreshness;
    }

    @Override
    public java.lang.String getFullGraphFreshness() {
      return fullGraphFreshness;
    }

    public CubePingImpl setFullGraphFreshness(java.lang.String v) {
      _hasFullGraphFreshness = true;
      fullGraphFreshness = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof CubePingImpl)) {
        return false;
      }
      CubePingImpl other = (CubePingImpl) o;
      if (this._hasFullGraphFreshness != other._hasFullGraphFreshness) {
        return false;
      }
      if (this._hasFullGraphFreshness) {
        if (!this.fullGraphFreshness.equals(other.fullGraphFreshness)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFullGraphFreshness ? fullGraphFreshness.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fullGraphFreshnessOut = (fullGraphFreshness == null) ? JsonNull.INSTANCE : new JsonPrimitive(fullGraphFreshness);
      result.add("fullGraphFreshness", fullGraphFreshnessOut);
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

    public static CubePingImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      CubePingImpl dto = new CubePingImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fullGraphFreshness")) {
        JsonElement fullGraphFreshnessIn = json.get("fullGraphFreshness");
        java.lang.String fullGraphFreshnessOut = gson.fromJson(fullGraphFreshnessIn, java.lang.String.class);
        dto.setFullGraphFreshness(fullGraphFreshnessOut);
      }

      return dto;
    }
    public static CubePingImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DeployWorkspaceImpl extends GetAppEngineClusterTypeImpl implements com.google.collide.dto.DeployWorkspace, JsonSerializable {

    private DeployWorkspaceImpl() {
      super(21);
    }

    protected DeployWorkspaceImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String appId;
    private boolean _hasAppId;
    protected java.lang.String appVersion;
    private boolean _hasAppVersion;
    protected java.lang.String basePath;
    private boolean _hasBasePath;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public DeployWorkspaceImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasAppId() {
      return _hasAppId;
    }

    @Override
    public java.lang.String appId() {
      return appId;
    }

    public DeployWorkspaceImpl setAppId(java.lang.String v) {
      _hasAppId = true;
      appId = v;
      return this;
    }

    public boolean hasAppVersion() {
      return _hasAppVersion;
    }

    @Override
    public java.lang.String appVersion() {
      return appVersion;
    }

    public DeployWorkspaceImpl setAppVersion(java.lang.String v) {
      _hasAppVersion = true;
      appVersion = v;
      return this;
    }

    public boolean hasBasePath() {
      return _hasBasePath;
    }

    @Override
    public java.lang.String basePath() {
      return basePath;
    }

    public DeployWorkspaceImpl setBasePath(java.lang.String v) {
      _hasBasePath = true;
      basePath = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof DeployWorkspaceImpl)) {
        return false;
      }
      DeployWorkspaceImpl other = (DeployWorkspaceImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasAppId != other._hasAppId) {
        return false;
      }
      if (this._hasAppId) {
        if (!this.appId.equals(other.appId)) {
          return false;
        }
      }
      if (this._hasAppVersion != other._hasAppVersion) {
        return false;
      }
      if (this._hasAppVersion) {
        if (!this.appVersion.equals(other.appVersion)) {
          return false;
        }
      }
      if (this._hasBasePath != other._hasBasePath) {
        return false;
      }
      if (this._hasBasePath) {
        if (!this.basePath.equals(other.basePath)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasAppId ? appId.hashCode() : 0);
      hash = hash * 31 + (_hasAppVersion ? appVersion.hashCode() : 0);
      hash = hash * 31 + (_hasBasePath ? basePath.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement appIdOut = (appId == null) ? JsonNull.INSTANCE : new JsonPrimitive(appId);
      result.add("appId", appIdOut);

      JsonElement appVersionOut = (appVersion == null) ? JsonNull.INSTANCE : new JsonPrimitive(appVersion);
      result.add("appVersion", appVersionOut);

      JsonElement basePathOut = (basePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(basePath);
      result.add("basePath", basePathOut);

      JsonElement clusterTypeOut = (clusterType == null) ? JsonNull.INSTANCE : new JsonPrimitive(clusterType.name());
      result.add("clusterType", clusterTypeOut);
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

    public static DeployWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DeployWorkspaceImpl dto = new DeployWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("appId")) {
        JsonElement appIdIn = json.get("appId");
        java.lang.String appIdOut = gson.fromJson(appIdIn, java.lang.String.class);
        dto.setAppId(appIdOut);
      }

      if (json.has("appVersion")) {
        JsonElement appVersionIn = json.get("appVersion");
        java.lang.String appVersionOut = gson.fromJson(appVersionIn, java.lang.String.class);
        dto.setAppVersion(appVersionOut);
      }

      if (json.has("basePath")) {
        JsonElement basePathIn = json.get("basePath");
        java.lang.String basePathOut = gson.fromJson(basePathIn, java.lang.String.class);
        dto.setBasePath(basePathOut);
      }

      if (json.has("clusterType")) {
        JsonElement clusterTypeIn = json.get("clusterType");
        com.google.collide.dto.GetAppEngineClusterType.Type clusterTypeOut = gson.fromJson(clusterTypeIn, com.google.collide.dto.GetAppEngineClusterType.Type.class);
        dto.setClusterType(clusterTypeOut);
      }

      return dto;
    }
    public static DeployWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DeployWorkspaceStatusImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.DeployWorkspaceStatus, JsonSerializable {

    private DeployWorkspaceStatusImpl() {
      super(22);
    }

    protected DeployWorkspaceStatusImpl(int type) {
      super(type);
    }

    public static DeployWorkspaceStatusImpl make() {
      return new DeployWorkspaceStatusImpl();
    }

    protected java.lang.String appUrl;
    private boolean _hasAppUrl;
    protected java.lang.String message;
    private boolean _hasMessage;
    protected int status;
    private boolean _hasStatus;

    public boolean hasAppUrl() {
      return _hasAppUrl;
    }

    @Override
    public java.lang.String getAppUrl() {
      return appUrl;
    }

    public DeployWorkspaceStatusImpl setAppUrl(java.lang.String v) {
      _hasAppUrl = true;
      appUrl = v;
      return this;
    }

    public boolean hasMessage() {
      return _hasMessage;
    }

    @Override
    public java.lang.String getMessage() {
      return message;
    }

    public DeployWorkspaceStatusImpl setMessage(java.lang.String v) {
      _hasMessage = true;
      message = v;
      return this;
    }

    public boolean hasStatus() {
      return _hasStatus;
    }

    @Override
    public int getStatus() {
      return status;
    }

    public DeployWorkspaceStatusImpl setStatus(int v) {
      _hasStatus = true;
      status = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof DeployWorkspaceStatusImpl)) {
        return false;
      }
      DeployWorkspaceStatusImpl other = (DeployWorkspaceStatusImpl) o;
      if (this._hasAppUrl != other._hasAppUrl) {
        return false;
      }
      if (this._hasAppUrl) {
        if (!this.appUrl.equals(other.appUrl)) {
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
      if (this._hasStatus != other._hasStatus) {
        return false;
      }
      if (this._hasStatus) {
        if (this.status != other.status) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasAppUrl ? appUrl.hashCode() : 0);
      hash = hash * 31 + (_hasMessage ? message.hashCode() : 0);
      hash = hash * 31 + (_hasStatus ? java.lang.Integer.valueOf(status).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement appUrlOut = (appUrl == null) ? JsonNull.INSTANCE : new JsonPrimitive(appUrl);
      result.add("appUrl", appUrlOut);

      JsonElement messageOut = (message == null) ? JsonNull.INSTANCE : new JsonPrimitive(message);
      result.add("message", messageOut);

      JsonPrimitive statusOut = new JsonPrimitive(status);
      result.add("status", statusOut);
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

    public static DeployWorkspaceStatusImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DeployWorkspaceStatusImpl dto = new DeployWorkspaceStatusImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("appUrl")) {
        JsonElement appUrlIn = json.get("appUrl");
        java.lang.String appUrlOut = gson.fromJson(appUrlIn, java.lang.String.class);
        dto.setAppUrl(appUrlOut);
      }

      if (json.has("message")) {
        JsonElement messageIn = json.get("message");
        java.lang.String messageOut = gson.fromJson(messageIn, java.lang.String.class);
        dto.setMessage(messageOut);
      }

      if (json.has("status")) {
        JsonElement statusIn = json.get("status");
        int statusOut = statusIn.getAsInt();
        dto.setStatus(statusOut);
      }

      return dto;
    }
    public static DeployWorkspaceStatusImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DiffChunkResponseImpl implements com.google.collide.dto.DiffChunkResponse, JsonSerializable {

    public static DiffChunkResponseImpl make() {
      return new DiffChunkResponseImpl();
    }

    protected java.lang.String beforeData;
    private boolean _hasBeforeData;
    protected com.google.collide.dto.DiffChunkResponse.DiffType diffType;
    private boolean _hasDiffType;
    protected java.lang.String afterData;
    private boolean _hasAfterData;

    public boolean hasBeforeData() {
      return _hasBeforeData;
    }

    @Override
    public java.lang.String getBeforeData() {
      return beforeData;
    }

    public DiffChunkResponseImpl setBeforeData(java.lang.String v) {
      _hasBeforeData = true;
      beforeData = v;
      return this;
    }

    public boolean hasDiffType() {
      return _hasDiffType;
    }

    @Override
    public com.google.collide.dto.DiffChunkResponse.DiffType getDiffType() {
      return diffType;
    }

    public DiffChunkResponseImpl setDiffType(com.google.collide.dto.DiffChunkResponse.DiffType v) {
      _hasDiffType = true;
      diffType = v;
      return this;
    }

    public boolean hasAfterData() {
      return _hasAfterData;
    }

    @Override
    public java.lang.String getAfterData() {
      return afterData;
    }

    public DiffChunkResponseImpl setAfterData(java.lang.String v) {
      _hasAfterData = true;
      afterData = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof DiffChunkResponseImpl)) {
        return false;
      }
      DiffChunkResponseImpl other = (DiffChunkResponseImpl) o;
      if (this._hasBeforeData != other._hasBeforeData) {
        return false;
      }
      if (this._hasBeforeData) {
        if (!this.beforeData.equals(other.beforeData)) {
          return false;
        }
      }
      if (this._hasDiffType != other._hasDiffType) {
        return false;
      }
      if (this._hasDiffType) {
        if (!this.diffType.equals(other.diffType)) {
          return false;
        }
      }
      if (this._hasAfterData != other._hasAfterData) {
        return false;
      }
      if (this._hasAfterData) {
        if (!this.afterData.equals(other.afterData)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasBeforeData ? beforeData.hashCode() : 0);
      hash = hash * 31 + (_hasDiffType ? diffType.hashCode() : 0);
      hash = hash * 31 + (_hasAfterData ? afterData.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement beforeDataOut = (beforeData == null) ? JsonNull.INSTANCE : new JsonPrimitive(beforeData);
      result.add("beforeData", beforeDataOut);

      JsonElement diffTypeOut = (diffType == null) ? JsonNull.INSTANCE : new JsonPrimitive(diffType.name());
      result.add("diffType", diffTypeOut);

      JsonElement afterDataOut = (afterData == null) ? JsonNull.INSTANCE : new JsonPrimitive(afterData);
      result.add("afterData", afterDataOut);
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

    public static DiffChunkResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DiffChunkResponseImpl dto = new DiffChunkResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("beforeData")) {
        JsonElement beforeDataIn = json.get("beforeData");
        java.lang.String beforeDataOut = gson.fromJson(beforeDataIn, java.lang.String.class);
        dto.setBeforeData(beforeDataOut);
      }

      if (json.has("diffType")) {
        JsonElement diffTypeIn = json.get("diffType");
        com.google.collide.dto.DiffChunkResponse.DiffType diffTypeOut = gson.fromJson(diffTypeIn, com.google.collide.dto.DiffChunkResponse.DiffType.class);
        dto.setDiffType(diffTypeOut);
      }

      if (json.has("afterData")) {
        JsonElement afterDataIn = json.get("afterData");
        java.lang.String afterDataOut = gson.fromJson(afterDataIn, java.lang.String.class);
        dto.setAfterData(afterDataOut);
      }

      return dto;
    }
    public static DiffChunkResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DiffStatsDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.DiffStatsDto, JsonSerializable {

    private DiffStatsDtoImpl() {
      super(23);
    }

    protected DiffStatsDtoImpl(int type) {
      super(type);
    }

    public static DiffStatsDtoImpl make() {
      return new DiffStatsDtoImpl();
    }

    protected int added;
    private boolean _hasAdded;
    protected int changed;
    private boolean _hasChanged;
    protected int deleted;
    private boolean _hasDeleted;
    protected int unchanged;
    private boolean _hasUnchanged;

    public boolean hasAdded() {
      return _hasAdded;
    }

    @Override
    public int getAdded() {
      return added;
    }

    public DiffStatsDtoImpl setAdded(int v) {
      _hasAdded = true;
      added = v;
      return this;
    }

    public boolean hasChanged() {
      return _hasChanged;
    }

    @Override
    public int getChanged() {
      return changed;
    }

    public DiffStatsDtoImpl setChanged(int v) {
      _hasChanged = true;
      changed = v;
      return this;
    }

    public boolean hasDeleted() {
      return _hasDeleted;
    }

    @Override
    public int getDeleted() {
      return deleted;
    }

    public DiffStatsDtoImpl setDeleted(int v) {
      _hasDeleted = true;
      deleted = v;
      return this;
    }

    public boolean hasUnchanged() {
      return _hasUnchanged;
    }

    @Override
    public int getUnchanged() {
      return unchanged;
    }

    public DiffStatsDtoImpl setUnchanged(int v) {
      _hasUnchanged = true;
      unchanged = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof DiffStatsDtoImpl)) {
        return false;
      }
      DiffStatsDtoImpl other = (DiffStatsDtoImpl) o;
      if (this._hasAdded != other._hasAdded) {
        return false;
      }
      if (this._hasAdded) {
        if (this.added != other.added) {
          return false;
        }
      }
      if (this._hasChanged != other._hasChanged) {
        return false;
      }
      if (this._hasChanged) {
        if (this.changed != other.changed) {
          return false;
        }
      }
      if (this._hasDeleted != other._hasDeleted) {
        return false;
      }
      if (this._hasDeleted) {
        if (this.deleted != other.deleted) {
          return false;
        }
      }
      if (this._hasUnchanged != other._hasUnchanged) {
        return false;
      }
      if (this._hasUnchanged) {
        if (this.unchanged != other.unchanged) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasAdded ? java.lang.Integer.valueOf(added).hashCode() : 0);
      hash = hash * 31 + (_hasChanged ? java.lang.Integer.valueOf(changed).hashCode() : 0);
      hash = hash * 31 + (_hasDeleted ? java.lang.Integer.valueOf(deleted).hashCode() : 0);
      hash = hash * 31 + (_hasUnchanged ? java.lang.Integer.valueOf(unchanged).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive addedOut = new JsonPrimitive(added);
      result.add("added", addedOut);

      JsonPrimitive changedOut = new JsonPrimitive(changed);
      result.add("changed", changedOut);

      JsonPrimitive deletedOut = new JsonPrimitive(deleted);
      result.add("deleted", deletedOut);

      JsonPrimitive unchangedOut = new JsonPrimitive(unchanged);
      result.add("unchanged", unchangedOut);
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

    public static DiffStatsDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DiffStatsDtoImpl dto = new DiffStatsDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("added")) {
        JsonElement addedIn = json.get("added");
        int addedOut = addedIn.getAsInt();
        dto.setAdded(addedOut);
      }

      if (json.has("changed")) {
        JsonElement changedIn = json.get("changed");
        int changedOut = changedIn.getAsInt();
        dto.setChanged(changedOut);
      }

      if (json.has("deleted")) {
        JsonElement deletedIn = json.get("deleted");
        int deletedOut = deletedIn.getAsInt();
        dto.setDeleted(deletedOut);
      }

      if (json.has("unchanged")) {
        JsonElement unchangedIn = json.get("unchanged");
        int unchangedOut = unchangedIn.getAsInt();
        dto.setUnchanged(unchangedOut);
      }

      return dto;
    }
    public static DiffStatsDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DirInfoImpl extends TreeNodeInfoImpl implements com.google.collide.dto.DirInfo, JsonSerializable {

    public static DirInfoImpl make() {
      return new DirInfoImpl();
    }

    protected boolean isComplete;
    private boolean _hasIsComplete;
    protected java.util.List<DirInfoImpl> subDirectories;
    private boolean _hasSubDirectories;
    protected java.util.List<FileInfoImpl> files;
    private boolean _hasFiles;

    public boolean hasIsComplete() {
      return _hasIsComplete;
    }

    @Override
    public boolean isComplete() {
      return isComplete;
    }

    public DirInfoImpl setIsComplete(boolean v) {
      _hasIsComplete = true;
      isComplete = v;
      return this;
    }

    public boolean hasSubDirectories() {
      return _hasSubDirectories;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.DirInfo> getSubDirectories() {
      ensureSubDirectories();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(subDirectories);
    }

    public DirInfoImpl setSubDirectories(java.util.List<DirInfoImpl> v) {
      _hasSubDirectories = true;
      subDirectories = v;
      return this;
    }

    public void addSubDirectories(DirInfoImpl v) {
      ensureSubDirectories();
      subDirectories.add(v);
    }

    public void clearSubDirectories() {
      ensureSubDirectories();
      subDirectories.clear();
    }

    private void ensureSubDirectories() {
      if (!_hasSubDirectories) {
        setSubDirectories(subDirectories != null ? subDirectories : new java.util.ArrayList<DirInfoImpl>());
      }
    }

    public boolean hasFiles() {
      return _hasFiles;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.FileInfo> getFiles() {
      ensureFiles();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(files);
    }

    public DirInfoImpl setFiles(java.util.List<FileInfoImpl> v) {
      _hasFiles = true;
      files = v;
      return this;
    }

    public void addFiles(FileInfoImpl v) {
      ensureFiles();
      files.add(v);
    }

    public void clearFiles() {
      ensureFiles();
      files.clear();
    }

    private void ensureFiles() {
      if (!_hasFiles) {
        setFiles(files != null ? files : new java.util.ArrayList<FileInfoImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof DirInfoImpl)) {
        return false;
      }
      DirInfoImpl other = (DirInfoImpl) o;
      if (this._hasIsComplete != other._hasIsComplete) {
        return false;
      }
      if (this._hasIsComplete) {
        if (this.isComplete != other.isComplete) {
          return false;
        }
      }
      if (this._hasSubDirectories != other._hasSubDirectories) {
        return false;
      }
      if (this._hasSubDirectories) {
        if (!this.subDirectories.equals(other.subDirectories)) {
          return false;
        }
      }
      if (this._hasFiles != other._hasFiles) {
        return false;
      }
      if (this._hasFiles) {
        if (!this.files.equals(other.files)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasIsComplete ? java.lang.Boolean.valueOf(isComplete).hashCode() : 0);
      hash = hash * 31 + (_hasSubDirectories ? subDirectories.hashCode() : 0);
      hash = hash * 31 + (_hasFiles ? files.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive isCompleteOut = new JsonPrimitive(isComplete);
      result.add("isComplete", isCompleteOut);

      JsonArray subDirectoriesOut = new JsonArray();
      ensureSubDirectories();
      for (DirInfoImpl subDirectories_ : subDirectories) {
        JsonElement subDirectoriesOut_ = subDirectories_ == null ? JsonNull.INSTANCE : subDirectories_.toJsonElement();
        subDirectoriesOut.add(subDirectoriesOut_);
      }
      result.add("subDirectories", subDirectoriesOut);

      JsonArray filesOut = new JsonArray();
      ensureFiles();
      for (FileInfoImpl files_ : files) {
        JsonElement filesOut_ = files_ == null ? JsonNull.INSTANCE : files_.toJsonElement();
        filesOut.add(filesOut_);
      }
      result.add("files", filesOut);

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonPrimitive nodeTypeOut = new JsonPrimitive(nodeType);
      result.add("nodeType", nodeTypeOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);
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

    public static DirInfoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DirInfoImpl dto = new DirInfoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("isComplete")) {
        JsonElement isCompleteIn = json.get("isComplete");
        boolean isCompleteOut = isCompleteIn.getAsBoolean();
        dto.setIsComplete(isCompleteOut);
      }

      if (json.has("subDirectories")) {
        JsonElement subDirectoriesIn = json.get("subDirectories");
        java.util.ArrayList<DirInfoImpl> subDirectoriesOut = null;
        if (subDirectoriesIn != null && !subDirectoriesIn.isJsonNull()) {
          subDirectoriesOut = new java.util.ArrayList<DirInfoImpl>();
          java.util.Iterator<JsonElement> subDirectoriesInIterator = subDirectoriesIn.getAsJsonArray().iterator();
          while (subDirectoriesInIterator.hasNext()) {
            JsonElement subDirectoriesIn_ = subDirectoriesInIterator.next();
            DirInfoImpl subDirectoriesOut_ = DirInfoImpl.fromJsonElement(subDirectoriesIn_);
            subDirectoriesOut.add(subDirectoriesOut_);
          }
        }
        dto.setSubDirectories(subDirectoriesOut);
      }

      if (json.has("files")) {
        JsonElement filesIn = json.get("files");
        java.util.ArrayList<FileInfoImpl> filesOut = null;
        if (filesIn != null && !filesIn.isJsonNull()) {
          filesOut = new java.util.ArrayList<FileInfoImpl>();
          java.util.Iterator<JsonElement> filesInIterator = filesIn.getAsJsonArray().iterator();
          while (filesInIterator.hasNext()) {
            JsonElement filesIn_ = filesInIterator.next();
            FileInfoImpl filesOut_ = FileInfoImpl.fromJsonElement(filesIn_);
            filesOut.add(filesOut_);
          }
        }
        dto.setFiles(filesOut);
      }

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("nodeType")) {
        JsonElement nodeTypeIn = json.get("nodeType");
        int nodeTypeOut = nodeTypeIn.getAsInt();
        dto.setNodeType(nodeTypeOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      return dto;
    }
    public static DirInfoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DocOpImpl implements com.google.collide.dto.DocOp, JsonSerializable {

    public static DocOpImpl make() {
      return new DocOpImpl();
    }

    protected java.util.List<DocOpComponentImpl> components;
    private boolean _hasComponents;

    public boolean hasComponents() {
      return _hasComponents;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.DocOpComponent> getComponents() {
      ensureComponents();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(components);
    }

    public DocOpImpl setComponents(java.util.List<DocOpComponentImpl> v) {
      _hasComponents = true;
      components = v;
      return this;
    }

    public void addComponents(DocOpComponentImpl v) {
      ensureComponents();
      components.add(v);
    }

    public void clearComponents() {
      ensureComponents();
      components.clear();
    }

    private void ensureComponents() {
      if (!_hasComponents) {
        setComponents(components != null ? components : new java.util.ArrayList<DocOpComponentImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof DocOpImpl)) {
        return false;
      }
      DocOpImpl other = (DocOpImpl) o;
      if (this._hasComponents != other._hasComponents) {
        return false;
      }
      if (this._hasComponents) {
        if (!this.components.equals(other.components)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasComponents ? components.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray componentsOut = new JsonArray();
      ensureComponents();
      for (DocOpComponentImpl components_ : components) {
        JsonElement componentsOut_ = components_ == null ? JsonNull.INSTANCE : components_.toJsonElement();
        componentsOut.add(componentsOut_);
      }
      result.add("components", componentsOut);
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

    public static DocOpImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DocOpImpl dto = new DocOpImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("components")) {
        JsonElement componentsIn = json.get("components");
        java.util.ArrayList<DocOpComponentImpl> componentsOut = null;
        if (componentsIn != null && !componentsIn.isJsonNull()) {
          componentsOut = new java.util.ArrayList<DocOpComponentImpl>();
          java.util.Iterator<JsonElement> componentsInIterator = componentsIn.getAsJsonArray().iterator();
          while (componentsInIterator.hasNext()) {
            JsonElement componentsIn_ = componentsInIterator.next();
            DocOpComponentImpl componentsOut_ = DocOpComponentImpl.fromJsonElement(componentsIn_);
            componentsOut.add(componentsOut_);
          }
        }
        dto.setComponents(componentsOut);
      }

      return dto;
    }
    public static DocOpImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DeleteImpl extends DocOpComponentImpl implements com.google.collide.dto.DocOpComponent.Delete, JsonSerializable {

    public static DeleteImpl make() {
      return new DeleteImpl();
    }

    protected java.lang.String text;
    private boolean _hasText;

    public boolean hasText() {
      return _hasText;
    }

    @Override
    public java.lang.String getText() {
      return text;
    }

    public DeleteImpl setText(java.lang.String v) {
      _hasText = true;
      text = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof DeleteImpl)) {
        return false;
      }
      DeleteImpl other = (DeleteImpl) o;
      if (this._hasText != other._hasText) {
        return false;
      }
      if (this._hasText) {
        if (!this.text.equals(other.text)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasText ? text.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement textOut = (text == null) ? JsonNull.INSTANCE : new JsonPrimitive(text);
      result.add("text", textOut);
      result.add("type", new JsonPrimitive(getType()));
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

    public static DeleteImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DeleteImpl dto = new DeleteImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("text")) {
        JsonElement textIn = json.get("text");
        java.lang.String textOut = gson.fromJson(textIn, java.lang.String.class);
        dto.setText(textOut);
      }

      return dto;
    }
    public static DeleteImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class InsertImpl extends DocOpComponentImpl implements com.google.collide.dto.DocOpComponent.Insert, JsonSerializable {

    public static InsertImpl make() {
      return new InsertImpl();
    }

    protected java.lang.String text;
    private boolean _hasText;

    public boolean hasText() {
      return _hasText;
    }

    @Override
    public java.lang.String getText() {
      return text;
    }

    public InsertImpl setText(java.lang.String v) {
      _hasText = true;
      text = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof InsertImpl)) {
        return false;
      }
      InsertImpl other = (InsertImpl) o;
      if (this._hasText != other._hasText) {
        return false;
      }
      if (this._hasText) {
        if (!this.text.equals(other.text)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasText ? text.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement textOut = (text == null) ? JsonNull.INSTANCE : new JsonPrimitive(text);
      result.add("text", textOut);
      result.add("type", new JsonPrimitive(getType()));
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

    public static InsertImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      InsertImpl dto = new InsertImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("text")) {
        JsonElement textIn = json.get("text");
        java.lang.String textOut = gson.fromJson(textIn, java.lang.String.class);
        dto.setText(textOut);
      }

      return dto;
    }
    public static InsertImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RetainImpl extends DocOpComponentImpl implements com.google.collide.dto.DocOpComponent.Retain, JsonSerializable {

    public static RetainImpl make() {
      return new RetainImpl();
    }

    protected boolean hasTrailingNewline;
    private boolean _hasHasTrailingNewline;
    protected int count;
    private boolean _hasCount;

    public boolean hasHasTrailingNewline() {
      return _hasHasTrailingNewline;
    }

    @Override
    public boolean hasTrailingNewline() {
      return hasTrailingNewline;
    }

    public RetainImpl setHasTrailingNewline(boolean v) {
      _hasHasTrailingNewline = true;
      hasTrailingNewline = v;
      return this;
    }

    public boolean hasCount() {
      return _hasCount;
    }

    @Override
    public int getCount() {
      return count;
    }

    public RetainImpl setCount(int v) {
      _hasCount = true;
      count = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RetainImpl)) {
        return false;
      }
      RetainImpl other = (RetainImpl) o;
      if (this._hasHasTrailingNewline != other._hasHasTrailingNewline) {
        return false;
      }
      if (this._hasHasTrailingNewline) {
        if (this.hasTrailingNewline != other.hasTrailingNewline) {
          return false;
        }
      }
      if (this._hasCount != other._hasCount) {
        return false;
      }
      if (this._hasCount) {
        if (this.count != other.count) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasHasTrailingNewline ? java.lang.Boolean.valueOf(hasTrailingNewline).hashCode() : 0);
      hash = hash * 31 + (_hasCount ? java.lang.Integer.valueOf(count).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive hasTrailingNewlineOut = new JsonPrimitive(hasTrailingNewline);
      result.add("hasTrailingNewline", hasTrailingNewlineOut);

      JsonPrimitive countOut = new JsonPrimitive(count);
      result.add("count", countOut);
      result.add("type", new JsonPrimitive(getType()));
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

    public static RetainImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RetainImpl dto = new RetainImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("hasTrailingNewline")) {
        JsonElement hasTrailingNewlineIn = json.get("hasTrailingNewline");
        boolean hasTrailingNewlineOut = hasTrailingNewlineIn.getAsBoolean();
        dto.setHasTrailingNewline(hasTrailingNewlineOut);
      }

      if (json.has("count")) {
        JsonElement countIn = json.get("count");
        int countOut = countIn.getAsInt();
        dto.setCount(countOut);
      }

      return dto;
    }
    public static RetainImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RetainLineImpl extends DocOpComponentImpl implements com.google.collide.dto.DocOpComponent.RetainLine, JsonSerializable {

    public static RetainLineImpl make() {
      return new RetainLineImpl();
    }

    protected int lineCount;
    private boolean _hasLineCount;

    public boolean hasLineCount() {
      return _hasLineCount;
    }

    @Override
    public int getLineCount() {
      return lineCount;
    }

    public RetainLineImpl setLineCount(int v) {
      _hasLineCount = true;
      lineCount = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RetainLineImpl)) {
        return false;
      }
      RetainLineImpl other = (RetainLineImpl) o;
      if (this._hasLineCount != other._hasLineCount) {
        return false;
      }
      if (this._hasLineCount) {
        if (this.lineCount != other.lineCount) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasLineCount ? java.lang.Integer.valueOf(lineCount).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive lineCountOut = new JsonPrimitive(lineCount);
      result.add("lineCount", lineCountOut);
      result.add("type", new JsonPrimitive(getType()));
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

    public static RetainLineImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RetainLineImpl dto = new RetainLineImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("lineCount")) {
        JsonElement lineCountIn = json.get("lineCount");
        int lineCountOut = lineCountIn.getAsInt();
        dto.setLineCount(lineCountOut);
      }

      return dto;
    }
    public static RetainLineImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DocOpComponentImpl implements com.google.collide.dto.DocOpComponent, JsonSerializable {

    public static DocOpComponentImpl make() {
      return new DocOpComponentImpl();
    }

    protected int type;
    private boolean _hasType;

    public boolean hasType() {
      return _hasType;
    }

    @Override
    public int getType() {
      return type;
    }

    public DocOpComponentImpl setType(int v) {
      _hasType = true;
      type = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof DocOpComponentImpl)) {
        return false;
      }
      DocOpComponentImpl other = (DocOpComponentImpl) o;
      if (this._hasType != other._hasType) {
        return false;
      }
      if (this._hasType) {
        if (this.type != other.type) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasType ? java.lang.Integer.valueOf(type).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();
      result.add("type", new JsonPrimitive(getType()));
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

    public static DocOpComponentImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DocOpComponentImpl dto = new DocOpComponentImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      return dto;
    }
    public static DocOpComponentImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DocumentSelectionImpl implements com.google.collide.dto.DocumentSelection, JsonSerializable {

    public static DocumentSelectionImpl make() {
      return new DocumentSelectionImpl();
    }

    protected FilePositionImpl basePosition;
    private boolean _hasBasePosition;
    protected FilePositionImpl cursorPosition;
    private boolean _hasCursorPosition;
    protected java.lang.String userId;
    private boolean _hasUserId;

    public boolean hasBasePosition() {
      return _hasBasePosition;
    }

    @Override
    public com.google.collide.dto.FilePosition getBasePosition() {
      return basePosition;
    }

    public DocumentSelectionImpl setBasePosition(FilePositionImpl v) {
      _hasBasePosition = true;
      basePosition = v;
      return this;
    }

    public boolean hasCursorPosition() {
      return _hasCursorPosition;
    }

    @Override
    public com.google.collide.dto.FilePosition getCursorPosition() {
      return cursorPosition;
    }

    public DocumentSelectionImpl setCursorPosition(FilePositionImpl v) {
      _hasCursorPosition = true;
      cursorPosition = v;
      return this;
    }

    public boolean hasUserId() {
      return _hasUserId;
    }

    @Override
    public java.lang.String getUserId() {
      return userId;
    }

    public DocumentSelectionImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof DocumentSelectionImpl)) {
        return false;
      }
      DocumentSelectionImpl other = (DocumentSelectionImpl) o;
      if (this._hasBasePosition != other._hasBasePosition) {
        return false;
      }
      if (this._hasBasePosition) {
        if (!this.basePosition.equals(other.basePosition)) {
          return false;
        }
      }
      if (this._hasCursorPosition != other._hasCursorPosition) {
        return false;
      }
      if (this._hasCursorPosition) {
        if (!this.cursorPosition.equals(other.cursorPosition)) {
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
      hash = hash * 31 + (_hasBasePosition ? basePosition.hashCode() : 0);
      hash = hash * 31 + (_hasCursorPosition ? cursorPosition.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement basePositionOut = basePosition == null ? JsonNull.INSTANCE : basePosition.toJsonElement();
      result.add("basePosition", basePositionOut);

      JsonElement cursorPositionOut = cursorPosition == null ? JsonNull.INSTANCE : cursorPosition.toJsonElement();
      result.add("cursorPosition", cursorPositionOut);

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

    public static DocumentSelectionImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DocumentSelectionImpl dto = new DocumentSelectionImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("basePosition")) {
        JsonElement basePositionIn = json.get("basePosition");
        FilePositionImpl basePositionOut = FilePositionImpl.fromJsonElement(basePositionIn);
        dto.setBasePosition(basePositionOut);
      }

      if (json.has("cursorPosition")) {
        JsonElement cursorPositionIn = json.get("cursorPosition");
        FilePositionImpl cursorPositionOut = FilePositionImpl.fromJsonElement(cursorPositionIn);
        dto.setCursorPosition(cursorPositionOut);
      }

      if (json.has("userId")) {
        JsonElement userIdIn = json.get("userId");
        java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
        dto.setUserId(userIdOut);
      }

      return dto;
    }
    public static DocumentSelectionImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class EmptyMessageImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.EmptyMessage, JsonSerializable {

    private EmptyMessageImpl() {
      super(24);
    }

    protected EmptyMessageImpl(int type) {
      super(type);
    }

    public static EmptyMessageImpl make() {
      return new EmptyMessageImpl();
    }


    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof EmptyMessageImpl)) {
        return false;
      }
      EmptyMessageImpl other = (EmptyMessageImpl) o;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();
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

    public static EmptyMessageImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      EmptyMessageImpl dto = new EmptyMessageImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      return dto;
    }
    public static EmptyMessageImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class EndUploadSessionImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.EndUploadSession, JsonSerializable {

    private EndUploadSessionImpl() {
      super(26);
    }

    protected EndUploadSessionImpl(int type) {
      super(type);
    }

    protected java.lang.String sessionId;
    private boolean _hasSessionId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasSessionId() {
      return _hasSessionId;
    }

    @Override
    public java.lang.String getSessionId() {
      return sessionId;
    }

    public EndUploadSessionImpl setSessionId(java.lang.String v) {
      _hasSessionId = true;
      sessionId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public EndUploadSessionImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof EndUploadSessionImpl)) {
        return false;
      }
      EndUploadSessionImpl other = (EndUploadSessionImpl) o;
      if (this._hasSessionId != other._hasSessionId) {
        return false;
      }
      if (this._hasSessionId) {
        if (!this.sessionId.equals(other.sessionId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasSessionId ? sessionId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement sessionIdOut = (sessionId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sessionId);
      result.add("sessionId", sessionIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static EndUploadSessionImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      EndUploadSessionImpl dto = new EndUploadSessionImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("sessionId")) {
        JsonElement sessionIdIn = json.get("sessionId");
        java.lang.String sessionIdOut = gson.fromJson(sessionIdIn, java.lang.String.class);
        dto.setSessionId(sessionIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static EndUploadSessionImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UnzipFailureImpl implements com.google.collide.dto.EndUploadSessionFinished.UnzipFailure, JsonSerializable {

    public static UnzipFailureImpl make() {
      return new UnzipFailureImpl();
    }

    protected java.lang.String zipWorkspacePath;
    private boolean _hasZipWorkspacePath;
    protected java.util.List<java.lang.String> displayFailedWorkspacePaths;
    private boolean _hasDisplayFailedWorkspacePaths;

    public boolean hasZipWorkspacePath() {
      return _hasZipWorkspacePath;
    }

    @Override
    public java.lang.String getZipWorkspacePath() {
      return zipWorkspacePath;
    }

    public UnzipFailureImpl setZipWorkspacePath(java.lang.String v) {
      _hasZipWorkspacePath = true;
      zipWorkspacePath = v;
      return this;
    }

    public boolean hasDisplayFailedWorkspacePaths() {
      return _hasDisplayFailedWorkspacePaths;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getDisplayFailedWorkspacePaths() {
      ensureDisplayFailedWorkspacePaths();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(displayFailedWorkspacePaths);
    }

    public UnzipFailureImpl setDisplayFailedWorkspacePaths(java.util.List<java.lang.String> v) {
      _hasDisplayFailedWorkspacePaths = true;
      displayFailedWorkspacePaths = v;
      return this;
    }

    public void addDisplayFailedWorkspacePaths(java.lang.String v) {
      ensureDisplayFailedWorkspacePaths();
      displayFailedWorkspacePaths.add(v);
    }

    public void clearDisplayFailedWorkspacePaths() {
      ensureDisplayFailedWorkspacePaths();
      displayFailedWorkspacePaths.clear();
    }

    private void ensureDisplayFailedWorkspacePaths() {
      if (!_hasDisplayFailedWorkspacePaths) {
        setDisplayFailedWorkspacePaths(displayFailedWorkspacePaths != null ? displayFailedWorkspacePaths : new java.util.ArrayList<java.lang.String>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof UnzipFailureImpl)) {
        return false;
      }
      UnzipFailureImpl other = (UnzipFailureImpl) o;
      if (this._hasZipWorkspacePath != other._hasZipWorkspacePath) {
        return false;
      }
      if (this._hasZipWorkspacePath) {
        if (!this.zipWorkspacePath.equals(other.zipWorkspacePath)) {
          return false;
        }
      }
      if (this._hasDisplayFailedWorkspacePaths != other._hasDisplayFailedWorkspacePaths) {
        return false;
      }
      if (this._hasDisplayFailedWorkspacePaths) {
        if (!this.displayFailedWorkspacePaths.equals(other.displayFailedWorkspacePaths)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasZipWorkspacePath ? zipWorkspacePath.hashCode() : 0);
      hash = hash * 31 + (_hasDisplayFailedWorkspacePaths ? displayFailedWorkspacePaths.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement zipWorkspacePathOut = (zipWorkspacePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(zipWorkspacePath);
      result.add("zipWorkspacePath", zipWorkspacePathOut);

      JsonArray displayFailedWorkspacePathsOut = new JsonArray();
      ensureDisplayFailedWorkspacePaths();
      for (java.lang.String displayFailedWorkspacePaths_ : displayFailedWorkspacePaths) {
        JsonElement displayFailedWorkspacePathsOut_ = (displayFailedWorkspacePaths_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(displayFailedWorkspacePaths_);
        displayFailedWorkspacePathsOut.add(displayFailedWorkspacePathsOut_);
      }
      result.add("displayFailedWorkspacePaths", displayFailedWorkspacePathsOut);
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

    public static UnzipFailureImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UnzipFailureImpl dto = new UnzipFailureImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("zipWorkspacePath")) {
        JsonElement zipWorkspacePathIn = json.get("zipWorkspacePath");
        java.lang.String zipWorkspacePathOut = gson.fromJson(zipWorkspacePathIn, java.lang.String.class);
        dto.setZipWorkspacePath(zipWorkspacePathOut);
      }

      if (json.has("displayFailedWorkspacePaths")) {
        JsonElement displayFailedWorkspacePathsIn = json.get("displayFailedWorkspacePaths");
        java.util.ArrayList<java.lang.String> displayFailedWorkspacePathsOut = null;
        if (displayFailedWorkspacePathsIn != null && !displayFailedWorkspacePathsIn.isJsonNull()) {
          displayFailedWorkspacePathsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> displayFailedWorkspacePathsInIterator = displayFailedWorkspacePathsIn.getAsJsonArray().iterator();
          while (displayFailedWorkspacePathsInIterator.hasNext()) {
            JsonElement displayFailedWorkspacePathsIn_ = displayFailedWorkspacePathsInIterator.next();
            java.lang.String displayFailedWorkspacePathsOut_ = gson.fromJson(displayFailedWorkspacePathsIn_, java.lang.String.class);
            displayFailedWorkspacePathsOut.add(displayFailedWorkspacePathsOut_);
          }
        }
        dto.setDisplayFailedWorkspacePaths(displayFailedWorkspacePathsOut);
      }

      return dto;
    }
    public static UnzipFailureImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class EndUploadSessionFinishedImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.EndUploadSessionFinished, JsonSerializable {

    private EndUploadSessionFinishedImpl() {
      super(120);
    }

    protected EndUploadSessionFinishedImpl(int type) {
      super(type);
    }

    public static EndUploadSessionFinishedImpl make() {
      return new EndUploadSessionFinishedImpl();
    }

    protected java.lang.String sessionId;
    private boolean _hasSessionId;
    protected java.util.List<java.lang.String> failedFileWorkspacePaths;
    private boolean _hasFailedFileWorkspacePaths;
    protected java.util.List<java.lang.String> failedDirWorkspacePaths;
    private boolean _hasFailedDirWorkspacePaths;
    protected java.util.List<UnzipFailureImpl> unzipFailures;
    private boolean _hasUnzipFailures;

    public boolean hasSessionId() {
      return _hasSessionId;
    }

    @Override
    public java.lang.String getSessionId() {
      return sessionId;
    }

    public EndUploadSessionFinishedImpl setSessionId(java.lang.String v) {
      _hasSessionId = true;
      sessionId = v;
      return this;
    }

    public boolean hasFailedFileWorkspacePaths() {
      return _hasFailedFileWorkspacePaths;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getFailedFileWorkspacePaths() {
      ensureFailedFileWorkspacePaths();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(failedFileWorkspacePaths);
    }

    public EndUploadSessionFinishedImpl setFailedFileWorkspacePaths(java.util.List<java.lang.String> v) {
      _hasFailedFileWorkspacePaths = true;
      failedFileWorkspacePaths = v;
      return this;
    }

    public void addFailedFileWorkspacePaths(java.lang.String v) {
      ensureFailedFileWorkspacePaths();
      failedFileWorkspacePaths.add(v);
    }

    public void clearFailedFileWorkspacePaths() {
      ensureFailedFileWorkspacePaths();
      failedFileWorkspacePaths.clear();
    }

    private void ensureFailedFileWorkspacePaths() {
      if (!_hasFailedFileWorkspacePaths) {
        setFailedFileWorkspacePaths(failedFileWorkspacePaths != null ? failedFileWorkspacePaths : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasFailedDirWorkspacePaths() {
      return _hasFailedDirWorkspacePaths;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getFailedDirWorkspacePaths() {
      ensureFailedDirWorkspacePaths();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(failedDirWorkspacePaths);
    }

    public EndUploadSessionFinishedImpl setFailedDirWorkspacePaths(java.util.List<java.lang.String> v) {
      _hasFailedDirWorkspacePaths = true;
      failedDirWorkspacePaths = v;
      return this;
    }

    public void addFailedDirWorkspacePaths(java.lang.String v) {
      ensureFailedDirWorkspacePaths();
      failedDirWorkspacePaths.add(v);
    }

    public void clearFailedDirWorkspacePaths() {
      ensureFailedDirWorkspacePaths();
      failedDirWorkspacePaths.clear();
    }

    private void ensureFailedDirWorkspacePaths() {
      if (!_hasFailedDirWorkspacePaths) {
        setFailedDirWorkspacePaths(failedDirWorkspacePaths != null ? failedDirWorkspacePaths : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasUnzipFailures() {
      return _hasUnzipFailures;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.EndUploadSessionFinished.UnzipFailure> getUnzipFailures() {
      ensureUnzipFailures();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(unzipFailures);
    }

    public EndUploadSessionFinishedImpl setUnzipFailures(java.util.List<UnzipFailureImpl> v) {
      _hasUnzipFailures = true;
      unzipFailures = v;
      return this;
    }

    public void addUnzipFailures(UnzipFailureImpl v) {
      ensureUnzipFailures();
      unzipFailures.add(v);
    }

    public void clearUnzipFailures() {
      ensureUnzipFailures();
      unzipFailures.clear();
    }

    private void ensureUnzipFailures() {
      if (!_hasUnzipFailures) {
        setUnzipFailures(unzipFailures != null ? unzipFailures : new java.util.ArrayList<UnzipFailureImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof EndUploadSessionFinishedImpl)) {
        return false;
      }
      EndUploadSessionFinishedImpl other = (EndUploadSessionFinishedImpl) o;
      if (this._hasSessionId != other._hasSessionId) {
        return false;
      }
      if (this._hasSessionId) {
        if (!this.sessionId.equals(other.sessionId)) {
          return false;
        }
      }
      if (this._hasFailedFileWorkspacePaths != other._hasFailedFileWorkspacePaths) {
        return false;
      }
      if (this._hasFailedFileWorkspacePaths) {
        if (!this.failedFileWorkspacePaths.equals(other.failedFileWorkspacePaths)) {
          return false;
        }
      }
      if (this._hasFailedDirWorkspacePaths != other._hasFailedDirWorkspacePaths) {
        return false;
      }
      if (this._hasFailedDirWorkspacePaths) {
        if (!this.failedDirWorkspacePaths.equals(other.failedDirWorkspacePaths)) {
          return false;
        }
      }
      if (this._hasUnzipFailures != other._hasUnzipFailures) {
        return false;
      }
      if (this._hasUnzipFailures) {
        if (!this.unzipFailures.equals(other.unzipFailures)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasSessionId ? sessionId.hashCode() : 0);
      hash = hash * 31 + (_hasFailedFileWorkspacePaths ? failedFileWorkspacePaths.hashCode() : 0);
      hash = hash * 31 + (_hasFailedDirWorkspacePaths ? failedDirWorkspacePaths.hashCode() : 0);
      hash = hash * 31 + (_hasUnzipFailures ? unzipFailures.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement sessionIdOut = (sessionId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sessionId);
      result.add("sessionId", sessionIdOut);

      JsonArray failedFileWorkspacePathsOut = new JsonArray();
      ensureFailedFileWorkspacePaths();
      for (java.lang.String failedFileWorkspacePaths_ : failedFileWorkspacePaths) {
        JsonElement failedFileWorkspacePathsOut_ = (failedFileWorkspacePaths_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(failedFileWorkspacePaths_);
        failedFileWorkspacePathsOut.add(failedFileWorkspacePathsOut_);
      }
      result.add("failedFileWorkspacePaths", failedFileWorkspacePathsOut);

      JsonArray failedDirWorkspacePathsOut = new JsonArray();
      ensureFailedDirWorkspacePaths();
      for (java.lang.String failedDirWorkspacePaths_ : failedDirWorkspacePaths) {
        JsonElement failedDirWorkspacePathsOut_ = (failedDirWorkspacePaths_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(failedDirWorkspacePaths_);
        failedDirWorkspacePathsOut.add(failedDirWorkspacePathsOut_);
      }
      result.add("failedDirWorkspacePaths", failedDirWorkspacePathsOut);

      JsonArray unzipFailuresOut = new JsonArray();
      ensureUnzipFailures();
      for (UnzipFailureImpl unzipFailures_ : unzipFailures) {
        JsonElement unzipFailuresOut_ = unzipFailures_ == null ? JsonNull.INSTANCE : unzipFailures_.toJsonElement();
        unzipFailuresOut.add(unzipFailuresOut_);
      }
      result.add("unzipFailures", unzipFailuresOut);
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

    public static EndUploadSessionFinishedImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      EndUploadSessionFinishedImpl dto = new EndUploadSessionFinishedImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("sessionId")) {
        JsonElement sessionIdIn = json.get("sessionId");
        java.lang.String sessionIdOut = gson.fromJson(sessionIdIn, java.lang.String.class);
        dto.setSessionId(sessionIdOut);
      }

      if (json.has("failedFileWorkspacePaths")) {
        JsonElement failedFileWorkspacePathsIn = json.get("failedFileWorkspacePaths");
        java.util.ArrayList<java.lang.String> failedFileWorkspacePathsOut = null;
        if (failedFileWorkspacePathsIn != null && !failedFileWorkspacePathsIn.isJsonNull()) {
          failedFileWorkspacePathsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> failedFileWorkspacePathsInIterator = failedFileWorkspacePathsIn.getAsJsonArray().iterator();
          while (failedFileWorkspacePathsInIterator.hasNext()) {
            JsonElement failedFileWorkspacePathsIn_ = failedFileWorkspacePathsInIterator.next();
            java.lang.String failedFileWorkspacePathsOut_ = gson.fromJson(failedFileWorkspacePathsIn_, java.lang.String.class);
            failedFileWorkspacePathsOut.add(failedFileWorkspacePathsOut_);
          }
        }
        dto.setFailedFileWorkspacePaths(failedFileWorkspacePathsOut);
      }

      if (json.has("failedDirWorkspacePaths")) {
        JsonElement failedDirWorkspacePathsIn = json.get("failedDirWorkspacePaths");
        java.util.ArrayList<java.lang.String> failedDirWorkspacePathsOut = null;
        if (failedDirWorkspacePathsIn != null && !failedDirWorkspacePathsIn.isJsonNull()) {
          failedDirWorkspacePathsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> failedDirWorkspacePathsInIterator = failedDirWorkspacePathsIn.getAsJsonArray().iterator();
          while (failedDirWorkspacePathsInIterator.hasNext()) {
            JsonElement failedDirWorkspacePathsIn_ = failedDirWorkspacePathsInIterator.next();
            java.lang.String failedDirWorkspacePathsOut_ = gson.fromJson(failedDirWorkspacePathsIn_, java.lang.String.class);
            failedDirWorkspacePathsOut.add(failedDirWorkspacePathsOut_);
          }
        }
        dto.setFailedDirWorkspacePaths(failedDirWorkspacePathsOut);
      }

      if (json.has("unzipFailures")) {
        JsonElement unzipFailuresIn = json.get("unzipFailures");
        java.util.ArrayList<UnzipFailureImpl> unzipFailuresOut = null;
        if (unzipFailuresIn != null && !unzipFailuresIn.isJsonNull()) {
          unzipFailuresOut = new java.util.ArrayList<UnzipFailureImpl>();
          java.util.Iterator<JsonElement> unzipFailuresInIterator = unzipFailuresIn.getAsJsonArray().iterator();
          while (unzipFailuresInIterator.hasNext()) {
            JsonElement unzipFailuresIn_ = unzipFailuresInIterator.next();
            UnzipFailureImpl unzipFailuresOut_ = UnzipFailureImpl.fromJsonElement(unzipFailuresIn_);
            unzipFailuresOut.add(unzipFailuresOut_);
          }
        }
        dto.setUnzipFailures(unzipFailuresOut);
      }

      return dto;
    }
    public static EndUploadSessionFinishedImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class EnterWorkspaceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.EnterWorkspace, JsonSerializable {

    private EnterWorkspaceImpl() {
      super(27);
    }

    protected EnterWorkspaceImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public EnterWorkspaceImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public EnterWorkspaceImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof EnterWorkspaceImpl)) {
        return false;
      }
      EnterWorkspaceImpl other = (EnterWorkspaceImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
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
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

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

    public static EnterWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      EnterWorkspaceImpl dto = new EnterWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static EnterWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class EnterWorkspaceResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.EnterWorkspaceResponse, JsonSerializable {

    private EnterWorkspaceResponseImpl() {
      super(28);
    }

    protected EnterWorkspaceResponseImpl(int type) {
      super(type);
    }

    public static EnterWorkspaceResponseImpl make() {
      return new EnterWorkspaceResponseImpl();
    }

    protected GetDirectoryResponseImpl fileTree;
    private boolean _hasFileTree;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.util.List<ParticipantUserDetailsImpl> participants;
    private boolean _hasParticipants;
    protected java.lang.String participantsNextVersion;
    private boolean _hasParticipantsNextVersion;
    protected GetWorkspaceMetaDataResponseImpl userWorkspaceMetadata;
    private boolean _hasUserWorkspaceMetadata;
    protected int keepAliveTimerIntervalMs;
    private boolean _hasKeepAliveTimerIntervalMs;
    protected WorkspaceInfoImpl workspaceInfo;
    private boolean _hasWorkspaceInfo;
    protected java.lang.String workspaceSessionHost;
    private boolean _hasWorkspaceSessionHost;
    protected com.google.collide.dto.GetSyncStateResponse.SyncState syncState;
    private boolean _hasSyncState;
    protected boolean isReadOnly;
    private boolean _hasIsReadOnly;

    public boolean hasFileTree() {
      return _hasFileTree;
    }

    @Override
    public com.google.collide.dto.GetDirectoryResponse getFileTree() {
      return fileTree;
    }

    public EnterWorkspaceResponseImpl setFileTree(GetDirectoryResponseImpl v) {
      _hasFileTree = true;
      fileTree = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public EnterWorkspaceResponseImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasParticipants() {
      return _hasParticipants;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> getParticipants() {
      ensureParticipants();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(participants);
    }

    public EnterWorkspaceResponseImpl setParticipants(java.util.List<ParticipantUserDetailsImpl> v) {
      _hasParticipants = true;
      participants = v;
      return this;
    }

    public void addParticipants(ParticipantUserDetailsImpl v) {
      ensureParticipants();
      participants.add(v);
    }

    public void clearParticipants() {
      ensureParticipants();
      participants.clear();
    }

    private void ensureParticipants() {
      if (!_hasParticipants) {
        setParticipants(participants != null ? participants : new java.util.ArrayList<ParticipantUserDetailsImpl>());
      }
    }

    public boolean hasParticipantsNextVersion() {
      return _hasParticipantsNextVersion;
    }

    @Override
    public java.lang.String getParticipantsNextVersion() {
      return participantsNextVersion;
    }

    public EnterWorkspaceResponseImpl setParticipantsNextVersion(java.lang.String v) {
      _hasParticipantsNextVersion = true;
      participantsNextVersion = v;
      return this;
    }

    public boolean hasUserWorkspaceMetadata() {
      return _hasUserWorkspaceMetadata;
    }

    @Override
    public com.google.collide.dto.GetWorkspaceMetaDataResponse getUserWorkspaceMetadata() {
      return userWorkspaceMetadata;
    }

    public EnterWorkspaceResponseImpl setUserWorkspaceMetadata(GetWorkspaceMetaDataResponseImpl v) {
      _hasUserWorkspaceMetadata = true;
      userWorkspaceMetadata = v;
      return this;
    }

    public boolean hasKeepAliveTimerIntervalMs() {
      return _hasKeepAliveTimerIntervalMs;
    }

    @Override
    public int getKeepAliveTimerIntervalMs() {
      return keepAliveTimerIntervalMs;
    }

    public EnterWorkspaceResponseImpl setKeepAliveTimerIntervalMs(int v) {
      _hasKeepAliveTimerIntervalMs = true;
      keepAliveTimerIntervalMs = v;
      return this;
    }

    public boolean hasWorkspaceInfo() {
      return _hasWorkspaceInfo;
    }

    @Override
    public com.google.collide.dto.WorkspaceInfo getWorkspaceInfo() {
      return workspaceInfo;
    }

    public EnterWorkspaceResponseImpl setWorkspaceInfo(WorkspaceInfoImpl v) {
      _hasWorkspaceInfo = true;
      workspaceInfo = v;
      return this;
    }

    public boolean hasWorkspaceSessionHost() {
      return _hasWorkspaceSessionHost;
    }

    @Override
    public java.lang.String getWorkspaceSessionHost() {
      return workspaceSessionHost;
    }

    public EnterWorkspaceResponseImpl setWorkspaceSessionHost(java.lang.String v) {
      _hasWorkspaceSessionHost = true;
      workspaceSessionHost = v;
      return this;
    }

    public boolean hasSyncState() {
      return _hasSyncState;
    }

    @Override
    public com.google.collide.dto.GetSyncStateResponse.SyncState getSyncState() {
      return syncState;
    }

    public EnterWorkspaceResponseImpl setSyncState(com.google.collide.dto.GetSyncStateResponse.SyncState v) {
      _hasSyncState = true;
      syncState = v;
      return this;
    }

    public boolean hasIsReadOnly() {
      return _hasIsReadOnly;
    }

    @Override
    public boolean isReadOnly() {
      return isReadOnly;
    }

    public EnterWorkspaceResponseImpl setIsReadOnly(boolean v) {
      _hasIsReadOnly = true;
      isReadOnly = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof EnterWorkspaceResponseImpl)) {
        return false;
      }
      EnterWorkspaceResponseImpl other = (EnterWorkspaceResponseImpl) o;
      if (this._hasFileTree != other._hasFileTree) {
        return false;
      }
      if (this._hasFileTree) {
        if (!this.fileTree.equals(other.fileTree)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasParticipants != other._hasParticipants) {
        return false;
      }
      if (this._hasParticipants) {
        if (!this.participants.equals(other.participants)) {
          return false;
        }
      }
      if (this._hasParticipantsNextVersion != other._hasParticipantsNextVersion) {
        return false;
      }
      if (this._hasParticipantsNextVersion) {
        if (!this.participantsNextVersion.equals(other.participantsNextVersion)) {
          return false;
        }
      }
      if (this._hasUserWorkspaceMetadata != other._hasUserWorkspaceMetadata) {
        return false;
      }
      if (this._hasUserWorkspaceMetadata) {
        if (!this.userWorkspaceMetadata.equals(other.userWorkspaceMetadata)) {
          return false;
        }
      }
      if (this._hasKeepAliveTimerIntervalMs != other._hasKeepAliveTimerIntervalMs) {
        return false;
      }
      if (this._hasKeepAliveTimerIntervalMs) {
        if (this.keepAliveTimerIntervalMs != other.keepAliveTimerIntervalMs) {
          return false;
        }
      }
      if (this._hasWorkspaceInfo != other._hasWorkspaceInfo) {
        return false;
      }
      if (this._hasWorkspaceInfo) {
        if (!this.workspaceInfo.equals(other.workspaceInfo)) {
          return false;
        }
      }
      if (this._hasWorkspaceSessionHost != other._hasWorkspaceSessionHost) {
        return false;
      }
      if (this._hasWorkspaceSessionHost) {
        if (!this.workspaceSessionHost.equals(other.workspaceSessionHost)) {
          return false;
        }
      }
      if (this._hasSyncState != other._hasSyncState) {
        return false;
      }
      if (this._hasSyncState) {
        if (!this.syncState.equals(other.syncState)) {
          return false;
        }
      }
      if (this._hasIsReadOnly != other._hasIsReadOnly) {
        return false;
      }
      if (this._hasIsReadOnly) {
        if (this.isReadOnly != other.isReadOnly) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFileTree ? fileTree.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasParticipants ? participants.hashCode() : 0);
      hash = hash * 31 + (_hasParticipantsNextVersion ? participantsNextVersion.hashCode() : 0);
      hash = hash * 31 + (_hasUserWorkspaceMetadata ? userWorkspaceMetadata.hashCode() : 0);
      hash = hash * 31 + (_hasKeepAliveTimerIntervalMs ? java.lang.Integer.valueOf(keepAliveTimerIntervalMs).hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceInfo ? workspaceInfo.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceSessionHost ? workspaceSessionHost.hashCode() : 0);
      hash = hash * 31 + (_hasSyncState ? syncState.hashCode() : 0);
      hash = hash * 31 + (_hasIsReadOnly ? java.lang.Boolean.valueOf(isReadOnly).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileTreeOut = fileTree == null ? JsonNull.INSTANCE : fileTree.toJsonElement();
      result.add("fileTree", fileTreeOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonArray participantsOut = new JsonArray();
      ensureParticipants();
      for (ParticipantUserDetailsImpl participants_ : participants) {
        JsonElement participantsOut_ = participants_ == null ? JsonNull.INSTANCE : participants_.toJsonElement();
        participantsOut.add(participantsOut_);
      }
      result.add("participants", participantsOut);

      JsonElement participantsNextVersionOut = (participantsNextVersion == null) ? JsonNull.INSTANCE : new JsonPrimitive(participantsNextVersion);
      result.add("participantsNextVersion", participantsNextVersionOut);

      JsonElement userWorkspaceMetadataOut = userWorkspaceMetadata == null ? JsonNull.INSTANCE : userWorkspaceMetadata.toJsonElement();
      result.add("userWorkspaceMetadata", userWorkspaceMetadataOut);

      JsonPrimitive keepAliveTimerIntervalMsOut = new JsonPrimitive(keepAliveTimerIntervalMs);
      result.add("keepAliveTimerIntervalMs", keepAliveTimerIntervalMsOut);

      JsonElement workspaceInfoOut = workspaceInfo == null ? JsonNull.INSTANCE : workspaceInfo.toJsonElement();
      result.add("workspaceInfo", workspaceInfoOut);

      JsonElement workspaceSessionHostOut = (workspaceSessionHost == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceSessionHost);
      result.add("workspaceSessionHost", workspaceSessionHostOut);

      JsonElement syncStateOut = (syncState == null) ? JsonNull.INSTANCE : new JsonPrimitive(syncState.name());
      result.add("syncState", syncStateOut);

      JsonPrimitive isReadOnlyOut = new JsonPrimitive(isReadOnly);
      result.add("isReadOnly", isReadOnlyOut);
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

    public static EnterWorkspaceResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      EnterWorkspaceResponseImpl dto = new EnterWorkspaceResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileTree")) {
        JsonElement fileTreeIn = json.get("fileTree");
        GetDirectoryResponseImpl fileTreeOut = GetDirectoryResponseImpl.fromJsonElement(fileTreeIn);
        dto.setFileTree(fileTreeOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("participants")) {
        JsonElement participantsIn = json.get("participants");
        java.util.ArrayList<ParticipantUserDetailsImpl> participantsOut = null;
        if (participantsIn != null && !participantsIn.isJsonNull()) {
          participantsOut = new java.util.ArrayList<ParticipantUserDetailsImpl>();
          java.util.Iterator<JsonElement> participantsInIterator = participantsIn.getAsJsonArray().iterator();
          while (participantsInIterator.hasNext()) {
            JsonElement participantsIn_ = participantsInIterator.next();
            ParticipantUserDetailsImpl participantsOut_ = ParticipantUserDetailsImpl.fromJsonElement(participantsIn_);
            participantsOut.add(participantsOut_);
          }
        }
        dto.setParticipants(participantsOut);
      }

      if (json.has("participantsNextVersion")) {
        JsonElement participantsNextVersionIn = json.get("participantsNextVersion");
        java.lang.String participantsNextVersionOut = gson.fromJson(participantsNextVersionIn, java.lang.String.class);
        dto.setParticipantsNextVersion(participantsNextVersionOut);
      }

      if (json.has("userWorkspaceMetadata")) {
        JsonElement userWorkspaceMetadataIn = json.get("userWorkspaceMetadata");
        GetWorkspaceMetaDataResponseImpl userWorkspaceMetadataOut = GetWorkspaceMetaDataResponseImpl.fromJsonElement(userWorkspaceMetadataIn);
        dto.setUserWorkspaceMetadata(userWorkspaceMetadataOut);
      }

      if (json.has("keepAliveTimerIntervalMs")) {
        JsonElement keepAliveTimerIntervalMsIn = json.get("keepAliveTimerIntervalMs");
        int keepAliveTimerIntervalMsOut = keepAliveTimerIntervalMsIn.getAsInt();
        dto.setKeepAliveTimerIntervalMs(keepAliveTimerIntervalMsOut);
      }

      if (json.has("workspaceInfo")) {
        JsonElement workspaceInfoIn = json.get("workspaceInfo");
        WorkspaceInfoImpl workspaceInfoOut = WorkspaceInfoImpl.fromJsonElement(workspaceInfoIn);
        dto.setWorkspaceInfo(workspaceInfoOut);
      }

      if (json.has("workspaceSessionHost")) {
        JsonElement workspaceSessionHostIn = json.get("workspaceSessionHost");
        java.lang.String workspaceSessionHostOut = gson.fromJson(workspaceSessionHostIn, java.lang.String.class);
        dto.setWorkspaceSessionHost(workspaceSessionHostOut);
      }

      if (json.has("syncState")) {
        JsonElement syncStateIn = json.get("syncState");
        com.google.collide.dto.GetSyncStateResponse.SyncState syncStateOut = gson.fromJson(syncStateIn, com.google.collide.dto.GetSyncStateResponse.SyncState.class);
        dto.setSyncState(syncStateOut);
      }

      if (json.has("isReadOnly")) {
        JsonElement isReadOnlyIn = json.get("isReadOnly");
        boolean isReadOnlyOut = isReadOnlyIn.getAsBoolean();
        dto.setIsReadOnly(isReadOnlyOut);
      }

      return dto;
    }
    public static EnterWorkspaceResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class FileCollaboratorGoneImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.FileCollaboratorGone, JsonSerializable {

    private FileCollaboratorGoneImpl() {
      super(127);
    }

    protected FileCollaboratorGoneImpl(int type) {
      super(type);
    }

    public static FileCollaboratorGoneImpl make() {
      return new FileCollaboratorGoneImpl();
    }

    protected ParticipantUserDetailsImpl participant;
    private boolean _hasParticipant;
    protected java.lang.String path;
    private boolean _hasPath;

    public boolean hasParticipant() {
      return _hasParticipant;
    }

    @Override
    public com.google.collide.dto.ParticipantUserDetails getParticipant() {
      return participant;
    }

    public FileCollaboratorGoneImpl setParticipant(ParticipantUserDetailsImpl v) {
      _hasParticipant = true;
      participant = v;
      return this;
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public FileCollaboratorGoneImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof FileCollaboratorGoneImpl)) {
        return false;
      }
      FileCollaboratorGoneImpl other = (FileCollaboratorGoneImpl) o;
      if (this._hasParticipant != other._hasParticipant) {
        return false;
      }
      if (this._hasParticipant) {
        if (!this.participant.equals(other.participant)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasParticipant ? participant.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement participantOut = participant == null ? JsonNull.INSTANCE : participant.toJsonElement();
      result.add("participant", participantOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);
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

    public static FileCollaboratorGoneImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      FileCollaboratorGoneImpl dto = new FileCollaboratorGoneImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("participant")) {
        JsonElement participantIn = json.get("participant");
        ParticipantUserDetailsImpl participantOut = ParticipantUserDetailsImpl.fromJsonElement(participantIn);
        dto.setParticipant(participantOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      return dto;
    }
    public static FileCollaboratorGoneImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class FileContentsImpl implements com.google.collide.dto.FileContents, JsonSerializable {

    public static FileContentsImpl make() {
      return new FileContentsImpl();
    }

    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected int ccRevision;
    private boolean _hasCcRevision;
    protected ConflictHandleImpl conflictHandle;
    private boolean _hasConflictHandle;
    protected java.util.List<ConflictChunkImpl> conflicts;
    private boolean _hasConflicts;
    protected java.lang.String mimeType;
    private boolean _hasMimeType;
    protected java.util.List<java.lang.String> selections;
    private boolean _hasSelections;
    protected java.lang.String path;
    private boolean _hasPath;
    protected com.google.collide.dto.FileContents.ContentType contentType;
    private boolean _hasContentType;
    protected java.lang.String contents;
    private boolean _hasContents;

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public FileContentsImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasCcRevision() {
      return _hasCcRevision;
    }

    @Override
    public int getCcRevision() {
      return ccRevision;
    }

    public FileContentsImpl setCcRevision(int v) {
      _hasCcRevision = true;
      ccRevision = v;
      return this;
    }

    public boolean hasConflictHandle() {
      return _hasConflictHandle;
    }

    @Override
    public com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() {
      return conflictHandle;
    }

    public FileContentsImpl setConflictHandle(ConflictHandleImpl v) {
      _hasConflictHandle = true;
      conflictHandle = v;
      return this;
    }

    public boolean hasConflicts() {
      return _hasConflicts;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ConflictChunk> getConflicts() {
      ensureConflicts();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(conflicts);
    }

    public FileContentsImpl setConflicts(java.util.List<ConflictChunkImpl> v) {
      _hasConflicts = true;
      conflicts = v;
      return this;
    }

    public void addConflicts(ConflictChunkImpl v) {
      ensureConflicts();
      conflicts.add(v);
    }

    public void clearConflicts() {
      ensureConflicts();
      conflicts.clear();
    }

    private void ensureConflicts() {
      if (!_hasConflicts) {
        setConflicts(conflicts != null ? conflicts : new java.util.ArrayList<ConflictChunkImpl>());
      }
    }

    public boolean hasMimeType() {
      return _hasMimeType;
    }

    @Override
    public java.lang.String getMimeType() {
      return mimeType;
    }

    public FileContentsImpl setMimeType(java.lang.String v) {
      _hasMimeType = true;
      mimeType = v;
      return this;
    }

    public boolean hasSelections() {
      return _hasSelections;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getSelections() {
      ensureSelections();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(selections);
    }

    public FileContentsImpl setSelections(java.util.List<java.lang.String> v) {
      _hasSelections = true;
      selections = v;
      return this;
    }

    public void addSelections(java.lang.String v) {
      ensureSelections();
      selections.add(v);
    }

    public void clearSelections() {
      ensureSelections();
      selections.clear();
    }

    private void ensureSelections() {
      if (!_hasSelections) {
        setSelections(selections != null ? selections : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public FileContentsImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    public boolean hasContentType() {
      return _hasContentType;
    }

    @Override
    public com.google.collide.dto.FileContents.ContentType getContentType() {
      return contentType;
    }

    public FileContentsImpl setContentType(com.google.collide.dto.FileContents.ContentType v) {
      _hasContentType = true;
      contentType = v;
      return this;
    }

    public boolean hasContents() {
      return _hasContents;
    }

    @Override
    public java.lang.String getContents() {
      return contents;
    }

    public FileContentsImpl setContents(java.lang.String v) {
      _hasContents = true;
      contents = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof FileContentsImpl)) {
        return false;
      }
      FileContentsImpl other = (FileContentsImpl) o;
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasCcRevision != other._hasCcRevision) {
        return false;
      }
      if (this._hasCcRevision) {
        if (this.ccRevision != other.ccRevision) {
          return false;
        }
      }
      if (this._hasConflictHandle != other._hasConflictHandle) {
        return false;
      }
      if (this._hasConflictHandle) {
        if (!this.conflictHandle.equals(other.conflictHandle)) {
          return false;
        }
      }
      if (this._hasConflicts != other._hasConflicts) {
        return false;
      }
      if (this._hasConflicts) {
        if (!this.conflicts.equals(other.conflicts)) {
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
      if (this._hasSelections != other._hasSelections) {
        return false;
      }
      if (this._hasSelections) {
        if (!this.selections.equals(other.selections)) {
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
      if (this._hasContentType != other._hasContentType) {
        return false;
      }
      if (this._hasContentType) {
        if (!this.contentType.equals(other.contentType)) {
          return false;
        }
      }
      if (this._hasContents != other._hasContents) {
        return false;
      }
      if (this._hasContents) {
        if (!this.contents.equals(other.contents)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasCcRevision ? java.lang.Integer.valueOf(ccRevision).hashCode() : 0);
      hash = hash * 31 + (_hasConflictHandle ? conflictHandle.hashCode() : 0);
      hash = hash * 31 + (_hasConflicts ? conflicts.hashCode() : 0);
      hash = hash * 31 + (_hasMimeType ? mimeType.hashCode() : 0);
      hash = hash * 31 + (_hasSelections ? selections.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      hash = hash * 31 + (_hasContentType ? contentType.hashCode() : 0);
      hash = hash * 31 + (_hasContents ? contents.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonPrimitive ccRevisionOut = new JsonPrimitive(ccRevision);
      result.add("ccRevision", ccRevisionOut);

      JsonElement conflictHandleOut = conflictHandle == null ? JsonNull.INSTANCE : conflictHandle.toJsonElement();
      result.add("conflictHandle", conflictHandleOut);

      JsonArray conflictsOut = new JsonArray();
      ensureConflicts();
      for (ConflictChunkImpl conflicts_ : conflicts) {
        JsonElement conflictsOut_ = conflicts_ == null ? JsonNull.INSTANCE : conflicts_.toJsonElement();
        conflictsOut.add(conflictsOut_);
      }
      result.add("conflicts", conflictsOut);

      JsonElement mimeTypeOut = (mimeType == null) ? JsonNull.INSTANCE : new JsonPrimitive(mimeType);
      result.add("mimeType", mimeTypeOut);

      JsonArray selectionsOut = new JsonArray();
      ensureSelections();
      for (java.lang.String selections_ : selections) {
        JsonElement selectionsOut_ = (selections_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(selections_);
        selectionsOut.add(selectionsOut_);
      }
      result.add("selections", selectionsOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);

      JsonElement contentTypeOut = (contentType == null) ? JsonNull.INSTANCE : new JsonPrimitive(contentType.name());
      result.add("contentType", contentTypeOut);

      JsonElement contentsOut = (contents == null) ? JsonNull.INSTANCE : new JsonPrimitive(contents);
      result.add("contents", contentsOut);
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

    public static FileContentsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      FileContentsImpl dto = new FileContentsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("ccRevision")) {
        JsonElement ccRevisionIn = json.get("ccRevision");
        int ccRevisionOut = ccRevisionIn.getAsInt();
        dto.setCcRevision(ccRevisionOut);
      }

      if (json.has("conflictHandle")) {
        JsonElement conflictHandleIn = json.get("conflictHandle");
        ConflictHandleImpl conflictHandleOut = ConflictHandleImpl.fromJsonElement(conflictHandleIn);
        dto.setConflictHandle(conflictHandleOut);
      }

      if (json.has("conflicts")) {
        JsonElement conflictsIn = json.get("conflicts");
        java.util.ArrayList<ConflictChunkImpl> conflictsOut = null;
        if (conflictsIn != null && !conflictsIn.isJsonNull()) {
          conflictsOut = new java.util.ArrayList<ConflictChunkImpl>();
          java.util.Iterator<JsonElement> conflictsInIterator = conflictsIn.getAsJsonArray().iterator();
          while (conflictsInIterator.hasNext()) {
            JsonElement conflictsIn_ = conflictsInIterator.next();
            ConflictChunkImpl conflictsOut_ = ConflictChunkImpl.fromJsonElement(conflictsIn_);
            conflictsOut.add(conflictsOut_);
          }
        }
        dto.setConflicts(conflictsOut);
      }

      if (json.has("mimeType")) {
        JsonElement mimeTypeIn = json.get("mimeType");
        java.lang.String mimeTypeOut = gson.fromJson(mimeTypeIn, java.lang.String.class);
        dto.setMimeType(mimeTypeOut);
      }

      if (json.has("selections")) {
        JsonElement selectionsIn = json.get("selections");
        java.util.ArrayList<java.lang.String> selectionsOut = null;
        if (selectionsIn != null && !selectionsIn.isJsonNull()) {
          selectionsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> selectionsInIterator = selectionsIn.getAsJsonArray().iterator();
          while (selectionsInIterator.hasNext()) {
            JsonElement selectionsIn_ = selectionsInIterator.next();
            java.lang.String selectionsOut_ = gson.fromJson(selectionsIn_, java.lang.String.class);
            selectionsOut.add(selectionsOut_);
          }
        }
        dto.setSelections(selectionsOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      if (json.has("contentType")) {
        JsonElement contentTypeIn = json.get("contentType");
        com.google.collide.dto.FileContents.ContentType contentTypeOut = gson.fromJson(contentTypeIn, com.google.collide.dto.FileContents.ContentType.class);
        dto.setContentType(contentTypeOut);
      }

      if (json.has("contents")) {
        JsonElement contentsIn = json.get("contents");
        java.lang.String contentsOut = gson.fromJson(contentsIn, java.lang.String.class);
        dto.setContents(contentsOut);
      }

      return dto;
    }
    public static FileContentsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class FileInfoImpl extends TreeNodeInfoImpl implements com.google.collide.dto.FileInfo, JsonSerializable {

    public static FileInfoImpl make() {
      return new FileInfoImpl();
    }

    protected java.lang.String size;
    private boolean _hasSize;

    public boolean hasSize() {
      return _hasSize;
    }

    @Override
    public java.lang.String getSize() {
      return size;
    }

    public FileInfoImpl setSize(java.lang.String v) {
      _hasSize = true;
      size = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof FileInfoImpl)) {
        return false;
      }
      FileInfoImpl other = (FileInfoImpl) o;
      if (this._hasSize != other._hasSize) {
        return false;
      }
      if (this._hasSize) {
        if (!this.size.equals(other.size)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasSize ? size.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement sizeOut = (size == null) ? JsonNull.INSTANCE : new JsonPrimitive(size);
      result.add("size", sizeOut);

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonPrimitive nodeTypeOut = new JsonPrimitive(nodeType);
      result.add("nodeType", nodeTypeOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);
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

    public static FileInfoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      FileInfoImpl dto = new FileInfoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("size")) {
        JsonElement sizeIn = json.get("size");
        java.lang.String sizeOut = gson.fromJson(sizeIn, java.lang.String.class);
        dto.setSize(sizeOut);
      }

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("nodeType")) {
        JsonElement nodeTypeIn = json.get("nodeType");
        int nodeTypeOut = nodeTypeIn.getAsInt();
        dto.setNodeType(nodeTypeOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      return dto;
    }
    public static FileInfoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class FileOperationNotificationImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.FileOperationNotification, JsonSerializable {

    private FileOperationNotificationImpl() {
      super(130);
    }

    protected FileOperationNotificationImpl(int type) {
      super(type);
    }

    public static FileOperationNotificationImpl make() {
      return new FileOperationNotificationImpl();
    }

    protected java.lang.String filePath;
    private boolean _hasFilePath;
    protected com.google.collide.dto.FileOperationNotification.Operation operation;
    private boolean _hasOperation;
    protected java.lang.String target;
    private boolean _hasTarget;
    protected java.lang.String userId;
    private boolean _hasUserId;

    public boolean hasFilePath() {
      return _hasFilePath;
    }

    @Override
    public java.lang.String getFilePath() {
      return filePath;
    }

    public FileOperationNotificationImpl setFilePath(java.lang.String v) {
      _hasFilePath = true;
      filePath = v;
      return this;
    }

    public boolean hasOperation() {
      return _hasOperation;
    }

    @Override
    public com.google.collide.dto.FileOperationNotification.Operation getOperation() {
      return operation;
    }

    public FileOperationNotificationImpl setOperation(com.google.collide.dto.FileOperationNotification.Operation v) {
      _hasOperation = true;
      operation = v;
      return this;
    }

    public boolean hasTarget() {
      return _hasTarget;
    }

    @Override
    public java.lang.String getTarget() {
      return target;
    }

    public FileOperationNotificationImpl setTarget(java.lang.String v) {
      _hasTarget = true;
      target = v;
      return this;
    }

    public boolean hasUserId() {
      return _hasUserId;
    }

    @Override
    public java.lang.String getUserId() {
      return userId;
    }

    public FileOperationNotificationImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof FileOperationNotificationImpl)) {
        return false;
      }
      FileOperationNotificationImpl other = (FileOperationNotificationImpl) o;
      if (this._hasFilePath != other._hasFilePath) {
        return false;
      }
      if (this._hasFilePath) {
        if (!this.filePath.equals(other.filePath)) {
          return false;
        }
      }
      if (this._hasOperation != other._hasOperation) {
        return false;
      }
      if (this._hasOperation) {
        if (!this.operation.equals(other.operation)) {
          return false;
        }
      }
      if (this._hasTarget != other._hasTarget) {
        return false;
      }
      if (this._hasTarget) {
        if (!this.target.equals(other.target)) {
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
      hash = hash * 31 + (_hasOperation ? operation.hashCode() : 0);
      hash = hash * 31 + (_hasTarget ? target.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement filePathOut = (filePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(filePath);
      result.add("filePath", filePathOut);

      JsonElement operationOut = (operation == null) ? JsonNull.INSTANCE : new JsonPrimitive(operation.name());
      result.add("operation", operationOut);

      JsonElement targetOut = (target == null) ? JsonNull.INSTANCE : new JsonPrimitive(target);
      result.add("target", targetOut);

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

    public static FileOperationNotificationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      FileOperationNotificationImpl dto = new FileOperationNotificationImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("filePath")) {
        JsonElement filePathIn = json.get("filePath");
        java.lang.String filePathOut = gson.fromJson(filePathIn, java.lang.String.class);
        dto.setFilePath(filePathOut);
      }

      if (json.has("operation")) {
        JsonElement operationIn = json.get("operation");
        com.google.collide.dto.FileOperationNotification.Operation operationOut = gson.fromJson(operationIn, com.google.collide.dto.FileOperationNotification.Operation.class);
        dto.setOperation(operationOut);
      }

      if (json.has("target")) {
        JsonElement targetIn = json.get("target");
        java.lang.String targetOut = gson.fromJson(targetIn, java.lang.String.class);
        dto.setTarget(targetOut);
      }

      if (json.has("userId")) {
        JsonElement userIdIn = json.get("userId");
        java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
        dto.setUserId(userIdOut);
      }

      return dto;
    }
    public static FileOperationNotificationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class FilePositionImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.FilePosition, JsonSerializable {

    private FilePositionImpl() {
      super(29);
    }

    protected FilePositionImpl(int type) {
      super(type);
    }

    public static FilePositionImpl make() {
      return new FilePositionImpl();
    }

    protected int lineNumber;
    private boolean _hasLineNumber;
    protected int column;
    private boolean _hasColumn;

    public boolean hasLineNumber() {
      return _hasLineNumber;
    }

    @Override
    public int getLineNumber() {
      return lineNumber;
    }

    public FilePositionImpl setLineNumber(int v) {
      _hasLineNumber = true;
      lineNumber = v;
      return this;
    }

    public boolean hasColumn() {
      return _hasColumn;
    }

    @Override
    public int getColumn() {
      return column;
    }

    public FilePositionImpl setColumn(int v) {
      _hasColumn = true;
      column = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof FilePositionImpl)) {
        return false;
      }
      FilePositionImpl other = (FilePositionImpl) o;
      if (this._hasLineNumber != other._hasLineNumber) {
        return false;
      }
      if (this._hasLineNumber) {
        if (this.lineNumber != other.lineNumber) {
          return false;
        }
      }
      if (this._hasColumn != other._hasColumn) {
        return false;
      }
      if (this._hasColumn) {
        if (this.column != other.column) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasLineNumber ? java.lang.Integer.valueOf(lineNumber).hashCode() : 0);
      hash = hash * 31 + (_hasColumn ? java.lang.Integer.valueOf(column).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonArray result = new JsonArray();

      JsonPrimitive lineNumberOut = new JsonPrimitive(lineNumber);
      result.add(lineNumberOut);

      JsonPrimitive columnOut = new JsonPrimitive(column);
      result.add(columnOut);
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

    public static FilePositionImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      FilePositionImpl dto = new FilePositionImpl();
      JsonArray json = jsonElem.getAsJsonArray();

      if (0 < json.size()) {
        JsonElement lineNumberIn = json.get(0);
        int lineNumberOut = lineNumberIn.getAsInt();
        dto.setLineNumber(lineNumberOut);
      }

      if (1 < json.size()) {
        JsonElement columnIn = json.get(1);
        int columnOut = columnIn.getAsInt();
        dto.setColumn(columnOut);
      }

      return dto;
    }
    public static FilePositionImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetAppEngineClusterTypeImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetAppEngineClusterType, JsonSerializable {

    private GetAppEngineClusterTypeImpl() {
      super(30);
    }

    protected GetAppEngineClusterTypeImpl(int type) {
      super(type);
    }

    protected com.google.collide.dto.GetAppEngineClusterType.Type clusterType;
    private boolean _hasClusterType;

    public boolean hasClusterType() {
      return _hasClusterType;
    }

    @Override
    public com.google.collide.dto.GetAppEngineClusterType.Type getClusterType() {
      return clusterType;
    }

    public GetAppEngineClusterTypeImpl setClusterType(com.google.collide.dto.GetAppEngineClusterType.Type v) {
      _hasClusterType = true;
      clusterType = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetAppEngineClusterTypeImpl)) {
        return false;
      }
      GetAppEngineClusterTypeImpl other = (GetAppEngineClusterTypeImpl) o;
      if (this._hasClusterType != other._hasClusterType) {
        return false;
      }
      if (this._hasClusterType) {
        if (!this.clusterType.equals(other.clusterType)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClusterType ? clusterType.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clusterTypeOut = (clusterType == null) ? JsonNull.INSTANCE : new JsonPrimitive(clusterType.name());
      result.add("clusterType", clusterTypeOut);
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

    public static GetAppEngineClusterTypeImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetAppEngineClusterTypeImpl dto = new GetAppEngineClusterTypeImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clusterType")) {
        JsonElement clusterTypeIn = json.get("clusterType");
        com.google.collide.dto.GetAppEngineClusterType.Type clusterTypeOut = gson.fromJson(clusterTypeIn, com.google.collide.dto.GetAppEngineClusterType.Type.class);
        dto.setClusterType(clusterTypeOut);
      }

      return dto;
    }
    public static GetAppEngineClusterTypeImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetDeployInformationImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetDeployInformation, JsonSerializable {

    private GetDeployInformationImpl() {
      super(31);
    }

    protected GetDeployInformationImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public GetDeployInformationImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetDeployInformationImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetDeployInformationImpl)) {
        return false;
      }
      GetDeployInformationImpl other = (GetDeployInformationImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static GetDeployInformationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetDeployInformationImpl dto = new GetDeployInformationImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static GetDeployInformationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class DeployInformationImpl implements com.google.collide.dto.GetDeployInformationResponse.DeployInformation, JsonSerializable {

    public static DeployInformationImpl make() {
      return new DeployInformationImpl();
    }

    protected java.lang.String appId;
    private boolean _hasAppId;
    protected java.lang.String appYamlPath;
    private boolean _hasAppYamlPath;
    protected java.lang.String version;
    private boolean _hasVersion;

    public boolean hasAppId() {
      return _hasAppId;
    }

    @Override
    public java.lang.String getAppId() {
      return appId;
    }

    public DeployInformationImpl setAppId(java.lang.String v) {
      _hasAppId = true;
      appId = v;
      return this;
    }

    public boolean hasAppYamlPath() {
      return _hasAppYamlPath;
    }

    @Override
    public java.lang.String getAppYamlPath() {
      return appYamlPath;
    }

    public DeployInformationImpl setAppYamlPath(java.lang.String v) {
      _hasAppYamlPath = true;
      appYamlPath = v;
      return this;
    }

    public boolean hasVersion() {
      return _hasVersion;
    }

    @Override
    public java.lang.String getVersion() {
      return version;
    }

    public DeployInformationImpl setVersion(java.lang.String v) {
      _hasVersion = true;
      version = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof DeployInformationImpl)) {
        return false;
      }
      DeployInformationImpl other = (DeployInformationImpl) o;
      if (this._hasAppId != other._hasAppId) {
        return false;
      }
      if (this._hasAppId) {
        if (!this.appId.equals(other.appId)) {
          return false;
        }
      }
      if (this._hasAppYamlPath != other._hasAppYamlPath) {
        return false;
      }
      if (this._hasAppYamlPath) {
        if (!this.appYamlPath.equals(other.appYamlPath)) {
          return false;
        }
      }
      if (this._hasVersion != other._hasVersion) {
        return false;
      }
      if (this._hasVersion) {
        if (!this.version.equals(other.version)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasAppId ? appId.hashCode() : 0);
      hash = hash * 31 + (_hasAppYamlPath ? appYamlPath.hashCode() : 0);
      hash = hash * 31 + (_hasVersion ? version.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement appIdOut = (appId == null) ? JsonNull.INSTANCE : new JsonPrimitive(appId);
      result.add("appId", appIdOut);

      JsonElement appYamlPathOut = (appYamlPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(appYamlPath);
      result.add("appYamlPath", appYamlPathOut);

      JsonElement versionOut = (version == null) ? JsonNull.INSTANCE : new JsonPrimitive(version);
      result.add("version", versionOut);
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

    public static DeployInformationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      DeployInformationImpl dto = new DeployInformationImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("appId")) {
        JsonElement appIdIn = json.get("appId");
        java.lang.String appIdOut = gson.fromJson(appIdIn, java.lang.String.class);
        dto.setAppId(appIdOut);
      }

      if (json.has("appYamlPath")) {
        JsonElement appYamlPathIn = json.get("appYamlPath");
        java.lang.String appYamlPathOut = gson.fromJson(appYamlPathIn, java.lang.String.class);
        dto.setAppYamlPath(appYamlPathOut);
      }

      if (json.has("version")) {
        JsonElement versionIn = json.get("version");
        java.lang.String versionOut = gson.fromJson(versionIn, java.lang.String.class);
        dto.setVersion(versionOut);
      }

      return dto;
    }
    public static DeployInformationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetDeployInformationResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetDeployInformationResponse, JsonSerializable {

    private GetDeployInformationResponseImpl() {
      super(32);
    }

    protected GetDeployInformationResponseImpl(int type) {
      super(type);
    }

    public static GetDeployInformationResponseImpl make() {
      return new GetDeployInformationResponseImpl();
    }

    protected java.util.List<DeployInformationImpl> deployInformation;
    private boolean _hasDeployInformation;

    public boolean hasDeployInformation() {
      return _hasDeployInformation;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.GetDeployInformationResponse.DeployInformation> getDeployInformation() {
      ensureDeployInformation();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(deployInformation);
    }

    public GetDeployInformationResponseImpl setDeployInformation(java.util.List<DeployInformationImpl> v) {
      _hasDeployInformation = true;
      deployInformation = v;
      return this;
    }

    public void addDeployInformation(DeployInformationImpl v) {
      ensureDeployInformation();
      deployInformation.add(v);
    }

    public void clearDeployInformation() {
      ensureDeployInformation();
      deployInformation.clear();
    }

    private void ensureDeployInformation() {
      if (!_hasDeployInformation) {
        setDeployInformation(deployInformation != null ? deployInformation : new java.util.ArrayList<DeployInformationImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetDeployInformationResponseImpl)) {
        return false;
      }
      GetDeployInformationResponseImpl other = (GetDeployInformationResponseImpl) o;
      if (this._hasDeployInformation != other._hasDeployInformation) {
        return false;
      }
      if (this._hasDeployInformation) {
        if (!this.deployInformation.equals(other.deployInformation)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasDeployInformation ? deployInformation.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray deployInformationOut = new JsonArray();
      ensureDeployInformation();
      for (DeployInformationImpl deployInformation_ : deployInformation) {
        JsonElement deployInformationOut_ = deployInformation_ == null ? JsonNull.INSTANCE : deployInformation_.toJsonElement();
        deployInformationOut.add(deployInformationOut_);
      }
      result.add("deployInformation", deployInformationOut);
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

    public static GetDeployInformationResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetDeployInformationResponseImpl dto = new GetDeployInformationResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("deployInformation")) {
        JsonElement deployInformationIn = json.get("deployInformation");
        java.util.ArrayList<DeployInformationImpl> deployInformationOut = null;
        if (deployInformationIn != null && !deployInformationIn.isJsonNull()) {
          deployInformationOut = new java.util.ArrayList<DeployInformationImpl>();
          java.util.Iterator<JsonElement> deployInformationInIterator = deployInformationIn.getAsJsonArray().iterator();
          while (deployInformationInIterator.hasNext()) {
            JsonElement deployInformationIn_ = deployInformationInIterator.next();
            DeployInformationImpl deployInformationOut_ = DeployInformationImpl.fromJsonElement(deployInformationIn_);
            deployInformationOut.add(deployInformationOut_);
          }
        }
        dto.setDeployInformation(deployInformationOut);
      }

      return dto;
    }
    public static GetDeployInformationResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetDirectoryImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetDirectory, JsonSerializable {

    private GetDirectoryImpl() {
      super(33);
    }

    protected GetDirectoryImpl(int type) {
      super(type);
    }

    protected java.lang.String rootId;
    private boolean _hasRootId;
    protected java.lang.String path;
    private boolean _hasPath;
    protected int depth;
    private boolean _hasDepth;

    public boolean hasRootId() {
      return _hasRootId;
    }

    @Override
    public java.lang.String rootId() {
      return rootId;
    }

    public GetDirectoryImpl setRootId(java.lang.String v) {
      _hasRootId = true;
      rootId = v;
      return this;
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public GetDirectoryImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    public boolean hasDepth() {
      return _hasDepth;
    }

    @Override
    public int getDepth() {
      return depth;
    }

    public GetDirectoryImpl setDepth(int v) {
      _hasDepth = true;
      depth = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetDirectoryImpl)) {
        return false;
      }
      GetDirectoryImpl other = (GetDirectoryImpl) o;
      if (this._hasRootId != other._hasRootId) {
        return false;
      }
      if (this._hasRootId) {
        if (!this.rootId.equals(other.rootId)) {
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
      if (this._hasDepth != other._hasDepth) {
        return false;
      }
      if (this._hasDepth) {
        if (this.depth != other.depth) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasRootId ? rootId.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      hash = hash * 31 + (_hasDepth ? java.lang.Integer.valueOf(depth).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement rootIdOut = (rootId == null) ? JsonNull.INSTANCE : new JsonPrimitive(rootId);
      result.add("rootId", rootIdOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);

      JsonPrimitive depthOut = new JsonPrimitive(depth);
      result.add("depth", depthOut);
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

    public static GetDirectoryImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetDirectoryImpl dto = new GetDirectoryImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("rootId")) {
        JsonElement rootIdIn = json.get("rootId");
        java.lang.String rootIdOut = gson.fromJson(rootIdIn, java.lang.String.class);
        dto.setRootId(rootIdOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      if (json.has("depth")) {
        JsonElement depthIn = json.get("depth");
        int depthOut = depthIn.getAsInt();
        dto.setDepth(depthOut);
      }

      return dto;
    }
    public static GetDirectoryImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetDirectoryResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetDirectoryResponse, JsonSerializable {

    private GetDirectoryResponseImpl() {
      super(34);
    }

    protected GetDirectoryResponseImpl(int type) {
      super(type);
    }

    public static GetDirectoryResponseImpl make() {
      return new GetDirectoryResponseImpl();
    }

    protected java.lang.String rootId;
    private boolean _hasRootId;
    protected java.lang.String path;
    private boolean _hasPath;
    protected DirInfoImpl baseDirectory;
    private boolean _hasBaseDirectory;

    public boolean hasRootId() {
      return _hasRootId;
    }

    @Override
    public java.lang.String getRootId() {
      return rootId;
    }

    public GetDirectoryResponseImpl setRootId(java.lang.String v) {
      _hasRootId = true;
      rootId = v;
      return this;
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public GetDirectoryResponseImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    public boolean hasBaseDirectory() {
      return _hasBaseDirectory;
    }

    @Override
    public com.google.collide.dto.DirInfo getBaseDirectory() {
      return baseDirectory;
    }

    public GetDirectoryResponseImpl setBaseDirectory(DirInfoImpl v) {
      _hasBaseDirectory = true;
      baseDirectory = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetDirectoryResponseImpl)) {
        return false;
      }
      GetDirectoryResponseImpl other = (GetDirectoryResponseImpl) o;
      if (this._hasRootId != other._hasRootId) {
        return false;
      }
      if (this._hasRootId) {
        if (!this.rootId.equals(other.rootId)) {
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
      if (this._hasBaseDirectory != other._hasBaseDirectory) {
        return false;
      }
      if (this._hasBaseDirectory) {
        if (!this.baseDirectory.equals(other.baseDirectory)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasRootId ? rootId.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      hash = hash * 31 + (_hasBaseDirectory ? baseDirectory.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement rootIdOut = (rootId == null) ? JsonNull.INSTANCE : new JsonPrimitive(rootId);
      result.add("rootId", rootIdOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);

      JsonElement baseDirectoryOut = baseDirectory == null ? JsonNull.INSTANCE : baseDirectory.toJsonElement();
      result.add("baseDirectory", baseDirectoryOut);
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

    public static GetDirectoryResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetDirectoryResponseImpl dto = new GetDirectoryResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("rootId")) {
        JsonElement rootIdIn = json.get("rootId");
        java.lang.String rootIdOut = gson.fromJson(rootIdIn, java.lang.String.class);
        dto.setRootId(rootIdOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      if (json.has("baseDirectory")) {
        JsonElement baseDirectoryIn = json.get("baseDirectory");
        DirInfoImpl baseDirectoryOut = DirInfoImpl.fromJsonElement(baseDirectoryIn);
        dto.setBaseDirectory(baseDirectoryOut);
      }

      return dto;
    }
    public static GetDirectoryResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetEditSessionCollaboratorsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetEditSessionCollaborators, JsonSerializable {

    private GetEditSessionCollaboratorsImpl() {
      super(123);
    }

    protected GetEditSessionCollaboratorsImpl(int type) {
      super(type);
    }

    protected java.lang.String editSessionId;
    private boolean _hasEditSessionId;

    public boolean hasEditSessionId() {
      return _hasEditSessionId;
    }

    @Override
    public java.lang.String getEditSessionId() {
      return editSessionId;
    }

    public GetEditSessionCollaboratorsImpl setEditSessionId(java.lang.String v) {
      _hasEditSessionId = true;
      editSessionId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetEditSessionCollaboratorsImpl)) {
        return false;
      }
      GetEditSessionCollaboratorsImpl other = (GetEditSessionCollaboratorsImpl) o;
      if (this._hasEditSessionId != other._hasEditSessionId) {
        return false;
      }
      if (this._hasEditSessionId) {
        if (!this.editSessionId.equals(other.editSessionId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasEditSessionId ? editSessionId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement editSessionIdOut = (editSessionId == null) ? JsonNull.INSTANCE : new JsonPrimitive(editSessionId);
      result.add("editSessionId", editSessionIdOut);
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

    public static GetEditSessionCollaboratorsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetEditSessionCollaboratorsImpl dto = new GetEditSessionCollaboratorsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("editSessionId")) {
        JsonElement editSessionIdIn = json.get("editSessionId");
        java.lang.String editSessionIdOut = gson.fromJson(editSessionIdIn, java.lang.String.class);
        dto.setEditSessionId(editSessionIdOut);
      }

      return dto;
    }
    public static GetEditSessionCollaboratorsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetEditSessionCollaboratorsResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetEditSessionCollaboratorsResponse, JsonSerializable {

    private GetEditSessionCollaboratorsResponseImpl() {
      super(124);
    }

    protected GetEditSessionCollaboratorsResponseImpl(int type) {
      super(type);
    }

    public static GetEditSessionCollaboratorsResponseImpl make() {
      return new GetEditSessionCollaboratorsResponseImpl();
    }

    protected java.util.List<ParticipantUserDetailsImpl> participants;
    private boolean _hasParticipants;

    public boolean hasParticipants() {
      return _hasParticipants;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> getParticipants() {
      ensureParticipants();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(participants);
    }

    public GetEditSessionCollaboratorsResponseImpl setParticipants(java.util.List<ParticipantUserDetailsImpl> v) {
      _hasParticipants = true;
      participants = v;
      return this;
    }

    public void addParticipants(ParticipantUserDetailsImpl v) {
      ensureParticipants();
      participants.add(v);
    }

    public void clearParticipants() {
      ensureParticipants();
      participants.clear();
    }

    private void ensureParticipants() {
      if (!_hasParticipants) {
        setParticipants(participants != null ? participants : new java.util.ArrayList<ParticipantUserDetailsImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetEditSessionCollaboratorsResponseImpl)) {
        return false;
      }
      GetEditSessionCollaboratorsResponseImpl other = (GetEditSessionCollaboratorsResponseImpl) o;
      if (this._hasParticipants != other._hasParticipants) {
        return false;
      }
      if (this._hasParticipants) {
        if (!this.participants.equals(other.participants)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasParticipants ? participants.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray participantsOut = new JsonArray();
      ensureParticipants();
      for (ParticipantUserDetailsImpl participants_ : participants) {
        JsonElement participantsOut_ = participants_ == null ? JsonNull.INSTANCE : participants_.toJsonElement();
        participantsOut.add(participantsOut_);
      }
      result.add("participants", participantsOut);
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

    public static GetEditSessionCollaboratorsResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetEditSessionCollaboratorsResponseImpl dto = new GetEditSessionCollaboratorsResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("participants")) {
        JsonElement participantsIn = json.get("participants");
        java.util.ArrayList<ParticipantUserDetailsImpl> participantsOut = null;
        if (participantsIn != null && !participantsIn.isJsonNull()) {
          participantsOut = new java.util.ArrayList<ParticipantUserDetailsImpl>();
          java.util.Iterator<JsonElement> participantsInIterator = participantsIn.getAsJsonArray().iterator();
          while (participantsInIterator.hasNext()) {
            JsonElement participantsIn_ = participantsInIterator.next();
            ParticipantUserDetailsImpl participantsOut_ = ParticipantUserDetailsImpl.fromJsonElement(participantsIn_);
            participantsOut.add(participantsOut_);
          }
        }
        dto.setParticipants(participantsOut);
      }

      return dto;
    }
    public static GetEditSessionCollaboratorsResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetFileContentsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetFileContents, JsonSerializable {

    private GetFileContentsImpl() {
      super(35);
    }

    protected GetFileContentsImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String path;
    private boolean _hasPath;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public GetFileContentsImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetFileContentsImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public GetFileContentsImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetFileContentsImpl)) {
        return false;
      }
      GetFileContentsImpl other = (GetFileContentsImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);
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

    public static GetFileContentsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetFileContentsImpl dto = new GetFileContentsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      return dto;
    }
    public static GetFileContentsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetFileContentsResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetFileContentsResponse, JsonSerializable {

    private GetFileContentsResponseImpl() {
      super(36);
    }

    protected GetFileContentsResponseImpl(int type) {
      super(type);
    }

    public static GetFileContentsResponseImpl make() {
      return new GetFileContentsResponseImpl();
    }

    protected FileContentsImpl fileContents;
    private boolean _hasFileContents;
    protected boolean fileExists;
    private boolean _hasFileExists;

    public boolean hasFileContents() {
      return _hasFileContents;
    }

    @Override
    public com.google.collide.dto.FileContents getFileContents() {
      return fileContents;
    }

    public GetFileContentsResponseImpl setFileContents(FileContentsImpl v) {
      _hasFileContents = true;
      fileContents = v;
      return this;
    }

    public boolean hasFileExists() {
      return _hasFileExists;
    }

    @Override
    public boolean getFileExists() {
      return fileExists;
    }

    public GetFileContentsResponseImpl setFileExists(boolean v) {
      _hasFileExists = true;
      fileExists = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetFileContentsResponseImpl)) {
        return false;
      }
      GetFileContentsResponseImpl other = (GetFileContentsResponseImpl) o;
      if (this._hasFileContents != other._hasFileContents) {
        return false;
      }
      if (this._hasFileContents) {
        if (!this.fileContents.equals(other.fileContents)) {
          return false;
        }
      }
      if (this._hasFileExists != other._hasFileExists) {
        return false;
      }
      if (this._hasFileExists) {
        if (this.fileExists != other.fileExists) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFileContents ? fileContents.hashCode() : 0);
      hash = hash * 31 + (_hasFileExists ? java.lang.Boolean.valueOf(fileExists).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileContentsOut = fileContents == null ? JsonNull.INSTANCE : fileContents.toJsonElement();
      result.add("fileContents", fileContentsOut);

      JsonPrimitive fileExistsOut = new JsonPrimitive(fileExists);
      result.add("fileExists", fileExistsOut);
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

    public static GetFileContentsResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetFileContentsResponseImpl dto = new GetFileContentsResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileContents")) {
        JsonElement fileContentsIn = json.get("fileContents");
        FileContentsImpl fileContentsOut = FileContentsImpl.fromJsonElement(fileContentsIn);
        dto.setFileContents(fileContentsOut);
      }

      if (json.has("fileExists")) {
        JsonElement fileExistsIn = json.get("fileExists");
        boolean fileExistsOut = fileExistsIn.getAsBoolean();
        dto.setFileExists(fileExistsOut);
      }

      return dto;
    }
    public static GetFileContentsResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetFileDiffImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetFileDiff, JsonSerializable {

    private GetFileDiffImpl() {
      super(37);
    }

    protected GetFileDiffImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected com.google.collide.dto.NodeMutationDto.MutationType changedType;
    private boolean _hasChangedType;
    protected java.lang.String beforeNodeId;
    private boolean _hasBeforeNodeId;
    protected boolean isStatsOnly;
    private boolean _hasIsStatsOnly;
    protected java.lang.String afterNodeId;
    private boolean _hasAfterNodeId;
    protected java.lang.String path;
    private boolean _hasPath;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public GetFileDiffImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetFileDiffImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasChangedType() {
      return _hasChangedType;
    }

    @Override
    public com.google.collide.dto.NodeMutationDto.MutationType getChangedType() {
      return changedType;
    }

    public GetFileDiffImpl setChangedType(com.google.collide.dto.NodeMutationDto.MutationType v) {
      _hasChangedType = true;
      changedType = v;
      return this;
    }

    public boolean hasBeforeNodeId() {
      return _hasBeforeNodeId;
    }

    @Override
    public java.lang.String getBeforeNodeId() {
      return beforeNodeId;
    }

    public GetFileDiffImpl setBeforeNodeId(java.lang.String v) {
      _hasBeforeNodeId = true;
      beforeNodeId = v;
      return this;
    }

    public boolean hasIsStatsOnly() {
      return _hasIsStatsOnly;
    }

    @Override
    public boolean isStatsOnly() {
      return isStatsOnly;
    }

    public GetFileDiffImpl setIsStatsOnly(boolean v) {
      _hasIsStatsOnly = true;
      isStatsOnly = v;
      return this;
    }

    public boolean hasAfterNodeId() {
      return _hasAfterNodeId;
    }

    @Override
    public java.lang.String getAfterNodeId() {
      return afterNodeId;
    }

    public GetFileDiffImpl setAfterNodeId(java.lang.String v) {
      _hasAfterNodeId = true;
      afterNodeId = v;
      return this;
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public GetFileDiffImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetFileDiffImpl)) {
        return false;
      }
      GetFileDiffImpl other = (GetFileDiffImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasChangedType != other._hasChangedType) {
        return false;
      }
      if (this._hasChangedType) {
        if (!this.changedType.equals(other.changedType)) {
          return false;
        }
      }
      if (this._hasBeforeNodeId != other._hasBeforeNodeId) {
        return false;
      }
      if (this._hasBeforeNodeId) {
        if (!this.beforeNodeId.equals(other.beforeNodeId)) {
          return false;
        }
      }
      if (this._hasIsStatsOnly != other._hasIsStatsOnly) {
        return false;
      }
      if (this._hasIsStatsOnly) {
        if (this.isStatsOnly != other.isStatsOnly) {
          return false;
        }
      }
      if (this._hasAfterNodeId != other._hasAfterNodeId) {
        return false;
      }
      if (this._hasAfterNodeId) {
        if (!this.afterNodeId.equals(other.afterNodeId)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasChangedType ? changedType.hashCode() : 0);
      hash = hash * 31 + (_hasBeforeNodeId ? beforeNodeId.hashCode() : 0);
      hash = hash * 31 + (_hasIsStatsOnly ? java.lang.Boolean.valueOf(isStatsOnly).hashCode() : 0);
      hash = hash * 31 + (_hasAfterNodeId ? afterNodeId.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement changedTypeOut = (changedType == null) ? JsonNull.INSTANCE : new JsonPrimitive(changedType.name());
      result.add("changedType", changedTypeOut);

      JsonElement beforeNodeIdOut = (beforeNodeId == null) ? JsonNull.INSTANCE : new JsonPrimitive(beforeNodeId);
      result.add("beforeNodeId", beforeNodeIdOut);

      JsonPrimitive isStatsOnlyOut = new JsonPrimitive(isStatsOnly);
      result.add("isStatsOnly", isStatsOnlyOut);

      JsonElement afterNodeIdOut = (afterNodeId == null) ? JsonNull.INSTANCE : new JsonPrimitive(afterNodeId);
      result.add("afterNodeId", afterNodeIdOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);
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

    public static GetFileDiffImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetFileDiffImpl dto = new GetFileDiffImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("changedType")) {
        JsonElement changedTypeIn = json.get("changedType");
        com.google.collide.dto.NodeMutationDto.MutationType changedTypeOut = gson.fromJson(changedTypeIn, com.google.collide.dto.NodeMutationDto.MutationType.class);
        dto.setChangedType(changedTypeOut);
      }

      if (json.has("beforeNodeId")) {
        JsonElement beforeNodeIdIn = json.get("beforeNodeId");
        java.lang.String beforeNodeIdOut = gson.fromJson(beforeNodeIdIn, java.lang.String.class);
        dto.setBeforeNodeId(beforeNodeIdOut);
      }

      if (json.has("isStatsOnly")) {
        JsonElement isStatsOnlyIn = json.get("isStatsOnly");
        boolean isStatsOnlyOut = isStatsOnlyIn.getAsBoolean();
        dto.setIsStatsOnly(isStatsOnlyOut);
      }

      if (json.has("afterNodeId")) {
        JsonElement afterNodeIdIn = json.get("afterNodeId");
        java.lang.String afterNodeIdOut = gson.fromJson(afterNodeIdIn, java.lang.String.class);
        dto.setAfterNodeId(afterNodeIdOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      return dto;
    }
    public static GetFileDiffImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetFileDiffResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetFileDiffResponse, JsonSerializable {

    private GetFileDiffResponseImpl() {
      super(38);
    }

    protected GetFileDiffResponseImpl(int type) {
      super(type);
    }

    public static GetFileDiffResponseImpl make() {
      return new GetFileDiffResponseImpl();
    }

    protected DiffStatsDtoImpl diffStats;
    private boolean _hasDiffStats;
    protected java.util.List<DiffChunkResponseImpl> diffChunks;
    private boolean _hasDiffChunks;
    protected java.lang.String beforeFilePath;
    private boolean _hasBeforeFilePath;
    protected java.lang.String afterFilePath;
    private boolean _hasAfterFilePath;

    public boolean hasDiffStats() {
      return _hasDiffStats;
    }

    @Override
    public com.google.collide.dto.DiffStatsDto getDiffStats() {
      return diffStats;
    }

    public GetFileDiffResponseImpl setDiffStats(DiffStatsDtoImpl v) {
      _hasDiffStats = true;
      diffStats = v;
      return this;
    }

    public boolean hasDiffChunks() {
      return _hasDiffChunks;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.DiffChunkResponse> getDiffChunks() {
      ensureDiffChunks();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(diffChunks);
    }

    public GetFileDiffResponseImpl setDiffChunks(java.util.List<DiffChunkResponseImpl> v) {
      _hasDiffChunks = true;
      diffChunks = v;
      return this;
    }

    public void addDiffChunks(DiffChunkResponseImpl v) {
      ensureDiffChunks();
      diffChunks.add(v);
    }

    public void clearDiffChunks() {
      ensureDiffChunks();
      diffChunks.clear();
    }

    private void ensureDiffChunks() {
      if (!_hasDiffChunks) {
        setDiffChunks(diffChunks != null ? diffChunks : new java.util.ArrayList<DiffChunkResponseImpl>());
      }
    }

    public boolean hasBeforeFilePath() {
      return _hasBeforeFilePath;
    }

    @Override
    public java.lang.String getBeforeFilePath() {
      return beforeFilePath;
    }

    public GetFileDiffResponseImpl setBeforeFilePath(java.lang.String v) {
      _hasBeforeFilePath = true;
      beforeFilePath = v;
      return this;
    }

    public boolean hasAfterFilePath() {
      return _hasAfterFilePath;
    }

    @Override
    public java.lang.String getAfterFilePath() {
      return afterFilePath;
    }

    public GetFileDiffResponseImpl setAfterFilePath(java.lang.String v) {
      _hasAfterFilePath = true;
      afterFilePath = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetFileDiffResponseImpl)) {
        return false;
      }
      GetFileDiffResponseImpl other = (GetFileDiffResponseImpl) o;
      if (this._hasDiffStats != other._hasDiffStats) {
        return false;
      }
      if (this._hasDiffStats) {
        if (!this.diffStats.equals(other.diffStats)) {
          return false;
        }
      }
      if (this._hasDiffChunks != other._hasDiffChunks) {
        return false;
      }
      if (this._hasDiffChunks) {
        if (!this.diffChunks.equals(other.diffChunks)) {
          return false;
        }
      }
      if (this._hasBeforeFilePath != other._hasBeforeFilePath) {
        return false;
      }
      if (this._hasBeforeFilePath) {
        if (!this.beforeFilePath.equals(other.beforeFilePath)) {
          return false;
        }
      }
      if (this._hasAfterFilePath != other._hasAfterFilePath) {
        return false;
      }
      if (this._hasAfterFilePath) {
        if (!this.afterFilePath.equals(other.afterFilePath)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasDiffStats ? diffStats.hashCode() : 0);
      hash = hash * 31 + (_hasDiffChunks ? diffChunks.hashCode() : 0);
      hash = hash * 31 + (_hasBeforeFilePath ? beforeFilePath.hashCode() : 0);
      hash = hash * 31 + (_hasAfterFilePath ? afterFilePath.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement diffStatsOut = diffStats == null ? JsonNull.INSTANCE : diffStats.toJsonElement();
      result.add("diffStats", diffStatsOut);

      JsonArray diffChunksOut = new JsonArray();
      ensureDiffChunks();
      for (DiffChunkResponseImpl diffChunks_ : diffChunks) {
        JsonElement diffChunksOut_ = diffChunks_ == null ? JsonNull.INSTANCE : diffChunks_.toJsonElement();
        diffChunksOut.add(diffChunksOut_);
      }
      result.add("diffChunks", diffChunksOut);

      JsonElement beforeFilePathOut = (beforeFilePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(beforeFilePath);
      result.add("beforeFilePath", beforeFilePathOut);

      JsonElement afterFilePathOut = (afterFilePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(afterFilePath);
      result.add("afterFilePath", afterFilePathOut);
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

    public static GetFileDiffResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetFileDiffResponseImpl dto = new GetFileDiffResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("diffStats")) {
        JsonElement diffStatsIn = json.get("diffStats");
        DiffStatsDtoImpl diffStatsOut = DiffStatsDtoImpl.fromJsonElement(diffStatsIn);
        dto.setDiffStats(diffStatsOut);
      }

      if (json.has("diffChunks")) {
        JsonElement diffChunksIn = json.get("diffChunks");
        java.util.ArrayList<DiffChunkResponseImpl> diffChunksOut = null;
        if (diffChunksIn != null && !diffChunksIn.isJsonNull()) {
          diffChunksOut = new java.util.ArrayList<DiffChunkResponseImpl>();
          java.util.Iterator<JsonElement> diffChunksInIterator = diffChunksIn.getAsJsonArray().iterator();
          while (diffChunksInIterator.hasNext()) {
            JsonElement diffChunksIn_ = diffChunksInIterator.next();
            DiffChunkResponseImpl diffChunksOut_ = DiffChunkResponseImpl.fromJsonElement(diffChunksIn_);
            diffChunksOut.add(diffChunksOut_);
          }
        }
        dto.setDiffChunks(diffChunksOut);
      }

      if (json.has("beforeFilePath")) {
        JsonElement beforeFilePathIn = json.get("beforeFilePath");
        java.lang.String beforeFilePathOut = gson.fromJson(beforeFilePathIn, java.lang.String.class);
        dto.setBeforeFilePath(beforeFilePathOut);
      }

      if (json.has("afterFilePath")) {
        JsonElement afterFilePathIn = json.get("afterFilePath");
        java.lang.String afterFilePathOut = gson.fromJson(afterFilePathIn, java.lang.String.class);
        dto.setAfterFilePath(afterFilePathOut);
      }

      return dto;
    }
    public static GetFileDiffResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetFileRevisionsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetFileRevisions, JsonSerializable {

    private GetFileRevisionsImpl() {
      super(39);
    }

    protected GetFileRevisionsImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String rootId;
    private boolean _hasRootId;
    protected java.lang.String pathRootId;
    private boolean _hasPathRootId;
    protected int numOfRevisions;
    private boolean _hasNumOfRevisions;
    protected boolean includeBranchRevision;
    private boolean _hasIncludeBranchRevision;
    protected boolean includeMostRecentRevision;
    private boolean _hasIncludeMostRecentRevision;
    protected java.lang.String minId;
    private boolean _hasMinId;
    protected java.lang.String path;
    private boolean _hasPath;
    protected boolean filtering;
    private boolean _hasFiltering;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public GetFileRevisionsImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetFileRevisionsImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasRootId() {
      return _hasRootId;
    }

    @Override
    public java.lang.String getRootId() {
      return rootId;
    }

    public GetFileRevisionsImpl setRootId(java.lang.String v) {
      _hasRootId = true;
      rootId = v;
      return this;
    }

    public boolean hasPathRootId() {
      return _hasPathRootId;
    }

    @Override
    public java.lang.String getPathRootId() {
      return pathRootId;
    }

    public GetFileRevisionsImpl setPathRootId(java.lang.String v) {
      _hasPathRootId = true;
      pathRootId = v;
      return this;
    }

    public boolean hasNumOfRevisions() {
      return _hasNumOfRevisions;
    }

    @Override
    public int getNumOfRevisions() {
      return numOfRevisions;
    }

    public GetFileRevisionsImpl setNumOfRevisions(int v) {
      _hasNumOfRevisions = true;
      numOfRevisions = v;
      return this;
    }

    public boolean hasIncludeBranchRevision() {
      return _hasIncludeBranchRevision;
    }

    @Override
    public boolean getIncludeBranchRevision() {
      return includeBranchRevision;
    }

    public GetFileRevisionsImpl setIncludeBranchRevision(boolean v) {
      _hasIncludeBranchRevision = true;
      includeBranchRevision = v;
      return this;
    }

    public boolean hasIncludeMostRecentRevision() {
      return _hasIncludeMostRecentRevision;
    }

    @Override
    public boolean getIncludeMostRecentRevision() {
      return includeMostRecentRevision;
    }

    public GetFileRevisionsImpl setIncludeMostRecentRevision(boolean v) {
      _hasIncludeMostRecentRevision = true;
      includeMostRecentRevision = v;
      return this;
    }

    public boolean hasMinId() {
      return _hasMinId;
    }

    @Override
    public java.lang.String getMinId() {
      return minId;
    }

    public GetFileRevisionsImpl setMinId(java.lang.String v) {
      _hasMinId = true;
      minId = v;
      return this;
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public GetFileRevisionsImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    public boolean hasFiltering() {
      return _hasFiltering;
    }

    @Override
    public boolean filtering() {
      return filtering;
    }

    public GetFileRevisionsImpl setFiltering(boolean v) {
      _hasFiltering = true;
      filtering = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetFileRevisionsImpl)) {
        return false;
      }
      GetFileRevisionsImpl other = (GetFileRevisionsImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasRootId != other._hasRootId) {
        return false;
      }
      if (this._hasRootId) {
        if (!this.rootId.equals(other.rootId)) {
          return false;
        }
      }
      if (this._hasPathRootId != other._hasPathRootId) {
        return false;
      }
      if (this._hasPathRootId) {
        if (!this.pathRootId.equals(other.pathRootId)) {
          return false;
        }
      }
      if (this._hasNumOfRevisions != other._hasNumOfRevisions) {
        return false;
      }
      if (this._hasNumOfRevisions) {
        if (this.numOfRevisions != other.numOfRevisions) {
          return false;
        }
      }
      if (this._hasIncludeBranchRevision != other._hasIncludeBranchRevision) {
        return false;
      }
      if (this._hasIncludeBranchRevision) {
        if (this.includeBranchRevision != other.includeBranchRevision) {
          return false;
        }
      }
      if (this._hasIncludeMostRecentRevision != other._hasIncludeMostRecentRevision) {
        return false;
      }
      if (this._hasIncludeMostRecentRevision) {
        if (this.includeMostRecentRevision != other.includeMostRecentRevision) {
          return false;
        }
      }
      if (this._hasMinId != other._hasMinId) {
        return false;
      }
      if (this._hasMinId) {
        if (!this.minId.equals(other.minId)) {
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
      if (this._hasFiltering != other._hasFiltering) {
        return false;
      }
      if (this._hasFiltering) {
        if (this.filtering != other.filtering) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasRootId ? rootId.hashCode() : 0);
      hash = hash * 31 + (_hasPathRootId ? pathRootId.hashCode() : 0);
      hash = hash * 31 + (_hasNumOfRevisions ? java.lang.Integer.valueOf(numOfRevisions).hashCode() : 0);
      hash = hash * 31 + (_hasIncludeBranchRevision ? java.lang.Boolean.valueOf(includeBranchRevision).hashCode() : 0);
      hash = hash * 31 + (_hasIncludeMostRecentRevision ? java.lang.Boolean.valueOf(includeMostRecentRevision).hashCode() : 0);
      hash = hash * 31 + (_hasMinId ? minId.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      hash = hash * 31 + (_hasFiltering ? java.lang.Boolean.valueOf(filtering).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement rootIdOut = (rootId == null) ? JsonNull.INSTANCE : new JsonPrimitive(rootId);
      result.add("rootId", rootIdOut);

      JsonElement pathRootIdOut = (pathRootId == null) ? JsonNull.INSTANCE : new JsonPrimitive(pathRootId);
      result.add("pathRootId", pathRootIdOut);

      JsonPrimitive numOfRevisionsOut = new JsonPrimitive(numOfRevisions);
      result.add("numOfRevisions", numOfRevisionsOut);

      JsonPrimitive includeBranchRevisionOut = new JsonPrimitive(includeBranchRevision);
      result.add("includeBranchRevision", includeBranchRevisionOut);

      JsonPrimitive includeMostRecentRevisionOut = new JsonPrimitive(includeMostRecentRevision);
      result.add("includeMostRecentRevision", includeMostRecentRevisionOut);

      JsonElement minIdOut = (minId == null) ? JsonNull.INSTANCE : new JsonPrimitive(minId);
      result.add("minId", minIdOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);

      JsonPrimitive filteringOut = new JsonPrimitive(filtering);
      result.add("filtering", filteringOut);
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

    public static GetFileRevisionsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetFileRevisionsImpl dto = new GetFileRevisionsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("rootId")) {
        JsonElement rootIdIn = json.get("rootId");
        java.lang.String rootIdOut = gson.fromJson(rootIdIn, java.lang.String.class);
        dto.setRootId(rootIdOut);
      }

      if (json.has("pathRootId")) {
        JsonElement pathRootIdIn = json.get("pathRootId");
        java.lang.String pathRootIdOut = gson.fromJson(pathRootIdIn, java.lang.String.class);
        dto.setPathRootId(pathRootIdOut);
      }

      if (json.has("numOfRevisions")) {
        JsonElement numOfRevisionsIn = json.get("numOfRevisions");
        int numOfRevisionsOut = numOfRevisionsIn.getAsInt();
        dto.setNumOfRevisions(numOfRevisionsOut);
      }

      if (json.has("includeBranchRevision")) {
        JsonElement includeBranchRevisionIn = json.get("includeBranchRevision");
        boolean includeBranchRevisionOut = includeBranchRevisionIn.getAsBoolean();
        dto.setIncludeBranchRevision(includeBranchRevisionOut);
      }

      if (json.has("includeMostRecentRevision")) {
        JsonElement includeMostRecentRevisionIn = json.get("includeMostRecentRevision");
        boolean includeMostRecentRevisionOut = includeMostRecentRevisionIn.getAsBoolean();
        dto.setIncludeMostRecentRevision(includeMostRecentRevisionOut);
      }

      if (json.has("minId")) {
        JsonElement minIdIn = json.get("minId");
        java.lang.String minIdOut = gson.fromJson(minIdIn, java.lang.String.class);
        dto.setMinId(minIdOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      if (json.has("filtering")) {
        JsonElement filteringIn = json.get("filtering");
        boolean filteringOut = filteringIn.getAsBoolean();
        dto.setFiltering(filteringOut);
      }

      return dto;
    }
    public static GetFileRevisionsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetFileRevisionsResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetFileRevisionsResponse, JsonSerializable {

    private GetFileRevisionsResponseImpl() {
      super(40);
    }

    protected GetFileRevisionsResponseImpl(int type) {
      super(type);
    }

    public static GetFileRevisionsResponseImpl make() {
      return new GetFileRevisionsResponseImpl();
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.util.List<RevisionImpl> revisions;
    private boolean _hasRevisions;
    protected java.lang.String path;
    private boolean _hasPath;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetFileRevisionsResponseImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasRevisions() {
      return _hasRevisions;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.Revision> getRevisions() {
      ensureRevisions();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(revisions);
    }

    public GetFileRevisionsResponseImpl setRevisions(java.util.List<RevisionImpl> v) {
      _hasRevisions = true;
      revisions = v;
      return this;
    }

    public void addRevisions(RevisionImpl v) {
      ensureRevisions();
      revisions.add(v);
    }

    public void clearRevisions() {
      ensureRevisions();
      revisions.clear();
    }

    private void ensureRevisions() {
      if (!_hasRevisions) {
        setRevisions(revisions != null ? revisions : new java.util.ArrayList<RevisionImpl>());
      }
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public GetFileRevisionsResponseImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetFileRevisionsResponseImpl)) {
        return false;
      }
      GetFileRevisionsResponseImpl other = (GetFileRevisionsResponseImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasRevisions != other._hasRevisions) {
        return false;
      }
      if (this._hasRevisions) {
        if (!this.revisions.equals(other.revisions)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasRevisions ? revisions.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonArray revisionsOut = new JsonArray();
      ensureRevisions();
      for (RevisionImpl revisions_ : revisions) {
        JsonElement revisionsOut_ = revisions_ == null ? JsonNull.INSTANCE : revisions_.toJsonElement();
        revisionsOut.add(revisionsOut_);
      }
      result.add("revisions", revisionsOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);
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

    public static GetFileRevisionsResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetFileRevisionsResponseImpl dto = new GetFileRevisionsResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("revisions")) {
        JsonElement revisionsIn = json.get("revisions");
        java.util.ArrayList<RevisionImpl> revisionsOut = null;
        if (revisionsIn != null && !revisionsIn.isJsonNull()) {
          revisionsOut = new java.util.ArrayList<RevisionImpl>();
          java.util.Iterator<JsonElement> revisionsInIterator = revisionsIn.getAsJsonArray().iterator();
          while (revisionsInIterator.hasNext()) {
            JsonElement revisionsIn_ = revisionsInIterator.next();
            RevisionImpl revisionsOut_ = RevisionImpl.fromJsonElement(revisionsIn_);
            revisionsOut.add(revisionsOut_);
          }
        }
        dto.setRevisions(revisionsOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      return dto;
    }
    public static GetFileRevisionsResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetOpenendFilesInWorkspaceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetOpenendFilesInWorkspace, JsonSerializable {

    private GetOpenendFilesInWorkspaceImpl() {
      super(128);
    }

    protected GetOpenendFilesInWorkspaceImpl(int type) {
      super(type);
    }


    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetOpenendFilesInWorkspaceImpl)) {
        return false;
      }
      GetOpenendFilesInWorkspaceImpl other = (GetOpenendFilesInWorkspaceImpl) o;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();
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

    public static GetOpenendFilesInWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetOpenendFilesInWorkspaceImpl dto = new GetOpenendFilesInWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      return dto;
    }
    public static GetOpenendFilesInWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetOpenendFilesInWorkspaceResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetOpenendFilesInWorkspaceResponse, JsonSerializable {

    private GetOpenendFilesInWorkspaceResponseImpl() {
      super(129);
    }

    protected GetOpenendFilesInWorkspaceResponseImpl(int type) {
      super(type);
    }

    public static GetOpenendFilesInWorkspaceResponseImpl make() {
      return new GetOpenendFilesInWorkspaceResponseImpl();
    }

    protected java.util.Map<String, java.util.ArrayList<ParticipantUserDetailsImpl>> openedFiles;
    private boolean _hasOpenedFiles;

    public boolean hasOpenedFiles() {
      return _hasOpenedFiles;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonStringMap<org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails>> getOpenedFiles() {
      ensureOpenedFiles();
      return (org.exoplatform.ide.json.shared.JsonStringMap) new org.exoplatform.ide.json.server.JsonStringMapAdapter(openedFiles);
    }

    public GetOpenendFilesInWorkspaceResponseImpl setOpenedFiles(java.util.Map<String, java.util.ArrayList<ParticipantUserDetailsImpl>> v) {
      _hasOpenedFiles = true;
      openedFiles = v;
      return this;
    }

    public void putOpenedFiles(String k, java.util.ArrayList<ParticipantUserDetailsImpl> v) {
      ensureOpenedFiles();
      openedFiles.put(k, v);
    }

    public void clearOpenedFiles() {
      ensureOpenedFiles();
      openedFiles.clear();
    }

    private void ensureOpenedFiles() {
      if (!_hasOpenedFiles) {
        setOpenedFiles(openedFiles != null ? openedFiles : new java.util.HashMap<String, java.util.ArrayList<ParticipantUserDetailsImpl>>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetOpenendFilesInWorkspaceResponseImpl)) {
        return false;
      }
      GetOpenendFilesInWorkspaceResponseImpl other = (GetOpenendFilesInWorkspaceResponseImpl) o;
      if (this._hasOpenedFiles != other._hasOpenedFiles) {
        return false;
      }
      if (this._hasOpenedFiles) {
        if (!this.openedFiles.equals(other.openedFiles)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasOpenedFiles ? openedFiles.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonObject openedFilesOut = new JsonObject();
      ensureOpenedFiles();
      for (Map.Entry<String, java.util.ArrayList<ParticipantUserDetailsImpl>> entry0 : openedFiles.entrySet()) {
        java.util.ArrayList<ParticipantUserDetailsImpl> openedFiles_ = entry0.getValue();
        JsonArray openedFilesOut_ = new JsonArray();
        for (ParticipantUserDetailsImpl openedFiles__ : openedFiles_) {
          JsonElement openedFilesOut__ = openedFiles__ == null ? JsonNull.INSTANCE : openedFiles__.toJsonElement();
          openedFilesOut_.add(openedFilesOut__);
        }
        openedFilesOut.add(entry0.getKey(), openedFilesOut_);
      }
      result.add("openedFiles", openedFilesOut);
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

    public static GetOpenendFilesInWorkspaceResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetOpenendFilesInWorkspaceResponseImpl dto = new GetOpenendFilesInWorkspaceResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("openedFiles")) {
        JsonElement openedFilesIn = json.get("openedFiles");
        java.util.HashMap<String, java.util.ArrayList<ParticipantUserDetailsImpl>> openedFilesOut = null;
        if (openedFilesIn != null && !openedFilesIn.isJsonNull()) {
          openedFilesOut = new java.util.HashMap<String, java.util.ArrayList<ParticipantUserDetailsImpl>>();
          java.util.Set<Map.Entry<String, JsonElement>> entries0 = openedFilesIn.getAsJsonObject().entrySet();
          for (Map.Entry<String, JsonElement> entry0 : entries0) {
            JsonElement openedFilesIn_ = entry0.getValue();
            java.util.ArrayList<ParticipantUserDetailsImpl> openedFilesOut_ = null;
            if (openedFilesIn_ != null && !openedFilesIn_.isJsonNull()) {
              openedFilesOut_ = new java.util.ArrayList<ParticipantUserDetailsImpl>();
              java.util.Iterator<JsonElement> openedFilesIn_Iterator = openedFilesIn_.getAsJsonArray().iterator();
              while (openedFilesIn_Iterator.hasNext()) {
                JsonElement openedFilesIn__ = openedFilesIn_Iterator.next();
                ParticipantUserDetailsImpl openedFilesOut__ = ParticipantUserDetailsImpl.fromJsonElement(openedFilesIn__);
                openedFilesOut_.add(openedFilesOut__);
              }
            }
            openedFilesOut.put(entry0.getKey(), openedFilesOut_);
          }
        }
        dto.setOpenedFiles(openedFilesOut);
      }

      return dto;
    }
    public static GetOpenendFilesInWorkspaceResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetOwningProjectImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetOwningProject, JsonSerializable {

    private GetOwningProjectImpl() {
      super(43);
    }

    protected GetOwningProjectImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetOwningProjectImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetOwningProjectImpl)) {
        return false;
      }
      GetOwningProjectImpl other = (GetOwningProjectImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static GetOwningProjectImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetOwningProjectImpl dto = new GetOwningProjectImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static GetOwningProjectImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetOwningProjectResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetOwningProjectResponse, JsonSerializable {

    private GetOwningProjectResponseImpl() {
      super(44);
    }

    protected GetOwningProjectResponseImpl(int type) {
      super(type);
    }

    public static GetOwningProjectResponseImpl make() {
      return new GetOwningProjectResponseImpl();
    }

    protected ProjectInfoImpl owningProject;
    private boolean _hasOwningProject;
    protected ProjectMembersInfoImpl projectMembersInfo;
    private boolean _hasProjectMembersInfo;
    protected WorkspaceInfoImpl workspace;
    private boolean _hasWorkspace;

    public boolean hasOwningProject() {
      return _hasOwningProject;
    }

    @Override
    public com.google.collide.dto.ProjectInfo getOwningProject() {
      return owningProject;
    }

    public GetOwningProjectResponseImpl setOwningProject(ProjectInfoImpl v) {
      _hasOwningProject = true;
      owningProject = v;
      return this;
    }

    public boolean hasProjectMembersInfo() {
      return _hasProjectMembersInfo;
    }

    @Override
    public com.google.collide.dto.ProjectMembersInfo getProjectMembersInfo() {
      return projectMembersInfo;
    }

    public GetOwningProjectResponseImpl setProjectMembersInfo(ProjectMembersInfoImpl v) {
      _hasProjectMembersInfo = true;
      projectMembersInfo = v;
      return this;
    }

    public boolean hasWorkspace() {
      return _hasWorkspace;
    }

    @Override
    public com.google.collide.dto.WorkspaceInfo getWorkspace() {
      return workspace;
    }

    public GetOwningProjectResponseImpl setWorkspace(WorkspaceInfoImpl v) {
      _hasWorkspace = true;
      workspace = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetOwningProjectResponseImpl)) {
        return false;
      }
      GetOwningProjectResponseImpl other = (GetOwningProjectResponseImpl) o;
      if (this._hasOwningProject != other._hasOwningProject) {
        return false;
      }
      if (this._hasOwningProject) {
        if (!this.owningProject.equals(other.owningProject)) {
          return false;
        }
      }
      if (this._hasProjectMembersInfo != other._hasProjectMembersInfo) {
        return false;
      }
      if (this._hasProjectMembersInfo) {
        if (!this.projectMembersInfo.equals(other.projectMembersInfo)) {
          return false;
        }
      }
      if (this._hasWorkspace != other._hasWorkspace) {
        return false;
      }
      if (this._hasWorkspace) {
        if (!this.workspace.equals(other.workspace)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasOwningProject ? owningProject.hashCode() : 0);
      hash = hash * 31 + (_hasProjectMembersInfo ? projectMembersInfo.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspace ? workspace.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement owningProjectOut = owningProject == null ? JsonNull.INSTANCE : owningProject.toJsonElement();
      result.add("owningProject", owningProjectOut);

      JsonElement projectMembersInfoOut = projectMembersInfo == null ? JsonNull.INSTANCE : projectMembersInfo.toJsonElement();
      result.add("projectMembersInfo", projectMembersInfoOut);

      JsonElement workspaceOut = workspace == null ? JsonNull.INSTANCE : workspace.toJsonElement();
      result.add("workspace", workspaceOut);
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

    public static GetOwningProjectResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetOwningProjectResponseImpl dto = new GetOwningProjectResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("owningProject")) {
        JsonElement owningProjectIn = json.get("owningProject");
        ProjectInfoImpl owningProjectOut = ProjectInfoImpl.fromJsonElement(owningProjectIn);
        dto.setOwningProject(owningProjectOut);
      }

      if (json.has("projectMembersInfo")) {
        JsonElement projectMembersInfoIn = json.get("projectMembersInfo");
        ProjectMembersInfoImpl projectMembersInfoOut = ProjectMembersInfoImpl.fromJsonElement(projectMembersInfoIn);
        dto.setProjectMembersInfo(projectMembersInfoOut);
      }

      if (json.has("workspace")) {
        JsonElement workspaceIn = json.get("workspace");
        WorkspaceInfoImpl workspaceOut = WorkspaceInfoImpl.fromJsonElement(workspaceIn);
        dto.setWorkspace(workspaceOut);
      }

      return dto;
    }
    public static GetOwningProjectResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetProjectByIdImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetProjectById, JsonSerializable {

    private GetProjectByIdImpl() {
      super(45);
    }

    protected GetProjectByIdImpl(int type) {
      super(type);
    }

    protected com.google.collide.dto.WorkspaceInfo.WorkspaceType workspaceType;
    private boolean _hasWorkspaceType;
    protected java.lang.String startKey;
    private boolean _hasStartKey;
    protected int pageLength;
    private boolean _hasPageLength;
    protected boolean shouldLoadWorkspaces;
    private boolean _hasShouldLoadWorkspaces;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasWorkspaceType() {
      return _hasWorkspaceType;
    }

    @Override
    public com.google.collide.dto.WorkspaceInfo.WorkspaceType getWorkspaceType() {
      return workspaceType;
    }

    public GetProjectByIdImpl setWorkspaceType(com.google.collide.dto.WorkspaceInfo.WorkspaceType v) {
      _hasWorkspaceType = true;
      workspaceType = v;
      return this;
    }

    public boolean hasStartKey() {
      return _hasStartKey;
    }

    @Override
    public java.lang.String getStartKey() {
      return startKey;
    }

    public GetProjectByIdImpl setStartKey(java.lang.String v) {
      _hasStartKey = true;
      startKey = v;
      return this;
    }

    public boolean hasPageLength() {
      return _hasPageLength;
    }

    @Override
    public int getPageLength() {
      return pageLength;
    }

    public GetProjectByIdImpl setPageLength(int v) {
      _hasPageLength = true;
      pageLength = v;
      return this;
    }

    public boolean hasShouldLoadWorkspaces() {
      return _hasShouldLoadWorkspaces;
    }

    @Override
    public boolean getShouldLoadWorkspaces() {
      return shouldLoadWorkspaces;
    }

    public GetProjectByIdImpl setShouldLoadWorkspaces(boolean v) {
      _hasShouldLoadWorkspaces = true;
      shouldLoadWorkspaces = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public GetProjectByIdImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetProjectByIdImpl)) {
        return false;
      }
      GetProjectByIdImpl other = (GetProjectByIdImpl) o;
      if (this._hasWorkspaceType != other._hasWorkspaceType) {
        return false;
      }
      if (this._hasWorkspaceType) {
        if (!this.workspaceType.equals(other.workspaceType)) {
          return false;
        }
      }
      if (this._hasStartKey != other._hasStartKey) {
        return false;
      }
      if (this._hasStartKey) {
        if (!this.startKey.equals(other.startKey)) {
          return false;
        }
      }
      if (this._hasPageLength != other._hasPageLength) {
        return false;
      }
      if (this._hasPageLength) {
        if (this.pageLength != other.pageLength) {
          return false;
        }
      }
      if (this._hasShouldLoadWorkspaces != other._hasShouldLoadWorkspaces) {
        return false;
      }
      if (this._hasShouldLoadWorkspaces) {
        if (this.shouldLoadWorkspaces != other.shouldLoadWorkspaces) {
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
      hash = hash * 31 + (_hasWorkspaceType ? workspaceType.hashCode() : 0);
      hash = hash * 31 + (_hasStartKey ? startKey.hashCode() : 0);
      hash = hash * 31 + (_hasPageLength ? java.lang.Integer.valueOf(pageLength).hashCode() : 0);
      hash = hash * 31 + (_hasShouldLoadWorkspaces ? java.lang.Boolean.valueOf(shouldLoadWorkspaces).hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceTypeOut = (workspaceType == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceType.name());
      result.add("workspaceType", workspaceTypeOut);

      JsonElement startKeyOut = (startKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(startKey);
      result.add("startKey", startKeyOut);

      JsonPrimitive pageLengthOut = new JsonPrimitive(pageLength);
      result.add("pageLength", pageLengthOut);

      JsonPrimitive shouldLoadWorkspacesOut = new JsonPrimitive(shouldLoadWorkspaces);
      result.add("shouldLoadWorkspaces", shouldLoadWorkspacesOut);

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

    public static GetProjectByIdImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetProjectByIdImpl dto = new GetProjectByIdImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceType")) {
        JsonElement workspaceTypeIn = json.get("workspaceType");
        com.google.collide.dto.WorkspaceInfo.WorkspaceType workspaceTypeOut = gson.fromJson(workspaceTypeIn, com.google.collide.dto.WorkspaceInfo.WorkspaceType.class);
        dto.setWorkspaceType(workspaceTypeOut);
      }

      if (json.has("startKey")) {
        JsonElement startKeyIn = json.get("startKey");
        java.lang.String startKeyOut = gson.fromJson(startKeyIn, java.lang.String.class);
        dto.setStartKey(startKeyOut);
      }

      if (json.has("pageLength")) {
        JsonElement pageLengthIn = json.get("pageLength");
        int pageLengthOut = pageLengthIn.getAsInt();
        dto.setPageLength(pageLengthOut);
      }

      if (json.has("shouldLoadWorkspaces")) {
        JsonElement shouldLoadWorkspacesIn = json.get("shouldLoadWorkspaces");
        boolean shouldLoadWorkspacesOut = shouldLoadWorkspacesIn.getAsBoolean();
        dto.setShouldLoadWorkspaces(shouldLoadWorkspacesOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static GetProjectByIdImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetProjectByIdResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetProjectByIdResponse, JsonSerializable {

    private GetProjectByIdResponseImpl() {
      super(46);
    }

    protected GetProjectByIdResponseImpl(int type) {
      super(type);
    }

    public static GetProjectByIdResponseImpl make() {
      return new GetProjectByIdResponseImpl();
    }

    protected ProjectMembersInfoImpl projectMembersInfo;
    private boolean _hasProjectMembersInfo;
    protected java.util.List<WorkspaceInfoImpl> workspaces;
    private boolean _hasWorkspaces;
    protected ProjectInfoImpl project;
    private boolean _hasProject;

    public boolean hasProjectMembersInfo() {
      return _hasProjectMembersInfo;
    }

    @Override
    public com.google.collide.dto.ProjectMembersInfo getProjectMembersInfo() {
      return projectMembersInfo;
    }

    public GetProjectByIdResponseImpl setProjectMembersInfo(ProjectMembersInfoImpl v) {
      _hasProjectMembersInfo = true;
      projectMembersInfo = v;
      return this;
    }

    public boolean hasWorkspaces() {
      return _hasWorkspaces;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.WorkspaceInfo> getWorkspaces() {
      ensureWorkspaces();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(workspaces);
    }

    public GetProjectByIdResponseImpl setWorkspaces(java.util.List<WorkspaceInfoImpl> v) {
      _hasWorkspaces = true;
      workspaces = v;
      return this;
    }

    public void addWorkspaces(WorkspaceInfoImpl v) {
      ensureWorkspaces();
      workspaces.add(v);
    }

    public void clearWorkspaces() {
      ensureWorkspaces();
      workspaces.clear();
    }

    private void ensureWorkspaces() {
      if (!_hasWorkspaces) {
        setWorkspaces(workspaces != null ? workspaces : new java.util.ArrayList<WorkspaceInfoImpl>());
      }
    }

    public boolean hasProject() {
      return _hasProject;
    }

    @Override
    public com.google.collide.dto.ProjectInfo getProject() {
      return project;
    }

    public GetProjectByIdResponseImpl setProject(ProjectInfoImpl v) {
      _hasProject = true;
      project = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetProjectByIdResponseImpl)) {
        return false;
      }
      GetProjectByIdResponseImpl other = (GetProjectByIdResponseImpl) o;
      if (this._hasProjectMembersInfo != other._hasProjectMembersInfo) {
        return false;
      }
      if (this._hasProjectMembersInfo) {
        if (!this.projectMembersInfo.equals(other.projectMembersInfo)) {
          return false;
        }
      }
      if (this._hasWorkspaces != other._hasWorkspaces) {
        return false;
      }
      if (this._hasWorkspaces) {
        if (!this.workspaces.equals(other.workspaces)) {
          return false;
        }
      }
      if (this._hasProject != other._hasProject) {
        return false;
      }
      if (this._hasProject) {
        if (!this.project.equals(other.project)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasProjectMembersInfo ? projectMembersInfo.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaces ? workspaces.hashCode() : 0);
      hash = hash * 31 + (_hasProject ? project.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement projectMembersInfoOut = projectMembersInfo == null ? JsonNull.INSTANCE : projectMembersInfo.toJsonElement();
      result.add("projectMembersInfo", projectMembersInfoOut);

      JsonArray workspacesOut = new JsonArray();
      ensureWorkspaces();
      for (WorkspaceInfoImpl workspaces_ : workspaces) {
        JsonElement workspacesOut_ = workspaces_ == null ? JsonNull.INSTANCE : workspaces_.toJsonElement();
        workspacesOut.add(workspacesOut_);
      }
      result.add("workspaces", workspacesOut);

      JsonElement projectOut = project == null ? JsonNull.INSTANCE : project.toJsonElement();
      result.add("project", projectOut);
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

    public static GetProjectByIdResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetProjectByIdResponseImpl dto = new GetProjectByIdResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("projectMembersInfo")) {
        JsonElement projectMembersInfoIn = json.get("projectMembersInfo");
        ProjectMembersInfoImpl projectMembersInfoOut = ProjectMembersInfoImpl.fromJsonElement(projectMembersInfoIn);
        dto.setProjectMembersInfo(projectMembersInfoOut);
      }

      if (json.has("workspaces")) {
        JsonElement workspacesIn = json.get("workspaces");
        java.util.ArrayList<WorkspaceInfoImpl> workspacesOut = null;
        if (workspacesIn != null && !workspacesIn.isJsonNull()) {
          workspacesOut = new java.util.ArrayList<WorkspaceInfoImpl>();
          java.util.Iterator<JsonElement> workspacesInIterator = workspacesIn.getAsJsonArray().iterator();
          while (workspacesInIterator.hasNext()) {
            JsonElement workspacesIn_ = workspacesInIterator.next();
            WorkspaceInfoImpl workspacesOut_ = WorkspaceInfoImpl.fromJsonElement(workspacesIn_);
            workspacesOut.add(workspacesOut_);
          }
        }
        dto.setWorkspaces(workspacesOut);
      }

      if (json.has("project")) {
        JsonElement projectIn = json.get("project");
        ProjectInfoImpl projectOut = ProjectInfoImpl.fromJsonElement(projectIn);
        dto.setProject(projectOut);
      }

      return dto;
    }
    public static GetProjectByIdResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetProjectMembersImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetProjectMembers, JsonSerializable {

    private GetProjectMembersImpl() {
      super(47);
    }

    protected GetProjectMembersImpl(int type) {
      super(type);
    }

    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public GetProjectMembersImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetProjectMembersImpl)) {
        return false;
      }
      GetProjectMembersImpl other = (GetProjectMembersImpl) o;
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
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

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

    public static GetProjectMembersImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetProjectMembersImpl dto = new GetProjectMembersImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static GetProjectMembersImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetProjectMembersResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetProjectMembersResponse, JsonSerializable {

    private GetProjectMembersResponseImpl() {
      super(48);
    }

    protected GetProjectMembersResponseImpl(int type) {
      super(type);
    }

    public static GetProjectMembersResponseImpl make() {
      return new GetProjectMembersResponseImpl();
    }

    protected java.util.List<UserDetailsWithRoleImpl> pendingMembers;
    private boolean _hasPendingMembers;
    protected java.util.List<UserDetailsWithRoleImpl> members;
    private boolean _hasMembers;

    public boolean hasPendingMembers() {
      return _hasPendingMembers;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> getPendingMembers() {
      ensurePendingMembers();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(pendingMembers);
    }

    public GetProjectMembersResponseImpl setPendingMembers(java.util.List<UserDetailsWithRoleImpl> v) {
      _hasPendingMembers = true;
      pendingMembers = v;
      return this;
    }

    public void addPendingMembers(UserDetailsWithRoleImpl v) {
      ensurePendingMembers();
      pendingMembers.add(v);
    }

    public void clearPendingMembers() {
      ensurePendingMembers();
      pendingMembers.clear();
    }

    private void ensurePendingMembers() {
      if (!_hasPendingMembers) {
        setPendingMembers(pendingMembers != null ? pendingMembers : new java.util.ArrayList<UserDetailsWithRoleImpl>());
      }
    }

    public boolean hasMembers() {
      return _hasMembers;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> getMembers() {
      ensureMembers();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(members);
    }

    public GetProjectMembersResponseImpl setMembers(java.util.List<UserDetailsWithRoleImpl> v) {
      _hasMembers = true;
      members = v;
      return this;
    }

    public void addMembers(UserDetailsWithRoleImpl v) {
      ensureMembers();
      members.add(v);
    }

    public void clearMembers() {
      ensureMembers();
      members.clear();
    }

    private void ensureMembers() {
      if (!_hasMembers) {
        setMembers(members != null ? members : new java.util.ArrayList<UserDetailsWithRoleImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetProjectMembersResponseImpl)) {
        return false;
      }
      GetProjectMembersResponseImpl other = (GetProjectMembersResponseImpl) o;
      if (this._hasPendingMembers != other._hasPendingMembers) {
        return false;
      }
      if (this._hasPendingMembers) {
        if (!this.pendingMembers.equals(other.pendingMembers)) {
          return false;
        }
      }
      if (this._hasMembers != other._hasMembers) {
        return false;
      }
      if (this._hasMembers) {
        if (!this.members.equals(other.members)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasPendingMembers ? pendingMembers.hashCode() : 0);
      hash = hash * 31 + (_hasMembers ? members.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray pendingMembersOut = new JsonArray();
      ensurePendingMembers();
      for (UserDetailsWithRoleImpl pendingMembers_ : pendingMembers) {
        JsonElement pendingMembersOut_ = pendingMembers_ == null ? JsonNull.INSTANCE : pendingMembers_.toJsonElement();
        pendingMembersOut.add(pendingMembersOut_);
      }
      result.add("pendingMembers", pendingMembersOut);

      JsonArray membersOut = new JsonArray();
      ensureMembers();
      for (UserDetailsWithRoleImpl members_ : members) {
        JsonElement membersOut_ = members_ == null ? JsonNull.INSTANCE : members_.toJsonElement();
        membersOut.add(membersOut_);
      }
      result.add("members", membersOut);
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

    public static GetProjectMembersResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetProjectMembersResponseImpl dto = new GetProjectMembersResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("pendingMembers")) {
        JsonElement pendingMembersIn = json.get("pendingMembers");
        java.util.ArrayList<UserDetailsWithRoleImpl> pendingMembersOut = null;
        if (pendingMembersIn != null && !pendingMembersIn.isJsonNull()) {
          pendingMembersOut = new java.util.ArrayList<UserDetailsWithRoleImpl>();
          java.util.Iterator<JsonElement> pendingMembersInIterator = pendingMembersIn.getAsJsonArray().iterator();
          while (pendingMembersInIterator.hasNext()) {
            JsonElement pendingMembersIn_ = pendingMembersInIterator.next();
            UserDetailsWithRoleImpl pendingMembersOut_ = UserDetailsWithRoleImpl.fromJsonElement(pendingMembersIn_);
            pendingMembersOut.add(pendingMembersOut_);
          }
        }
        dto.setPendingMembers(pendingMembersOut);
      }

      if (json.has("members")) {
        JsonElement membersIn = json.get("members");
        java.util.ArrayList<UserDetailsWithRoleImpl> membersOut = null;
        if (membersIn != null && !membersIn.isJsonNull()) {
          membersOut = new java.util.ArrayList<UserDetailsWithRoleImpl>();
          java.util.Iterator<JsonElement> membersInIterator = membersIn.getAsJsonArray().iterator();
          while (membersInIterator.hasNext()) {
            JsonElement membersIn_ = membersInIterator.next();
            UserDetailsWithRoleImpl membersOut_ = UserDetailsWithRoleImpl.fromJsonElement(membersIn_);
            membersOut.add(membersOut_);
          }
        }
        dto.setMembers(membersOut);
      }

      return dto;
    }
    public static GetProjectMembersResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetProjectsResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetProjectsResponse, JsonSerializable {

    private GetProjectsResponseImpl() {
      super(49);
    }

    protected GetProjectsResponseImpl(int type) {
      super(type);
    }

    public static GetProjectsResponseImpl make() {
      return new GetProjectsResponseImpl();
    }

    protected java.util.List<java.lang.String> hiddenProjectIds;
    private boolean _hasHiddenProjectIds;
    protected java.lang.String activeProjectId;
    private boolean _hasActiveProjectId;
    protected java.lang.String userMembershipChangeNextVersion;
    private boolean _hasUserMembershipChangeNextVersion;
    protected java.util.List<ProjectInfoImpl> projects;
    private boolean _hasProjects;

    public boolean hasHiddenProjectIds() {
      return _hasHiddenProjectIds;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getHiddenProjectIds() {
      ensureHiddenProjectIds();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(hiddenProjectIds);
    }

    public GetProjectsResponseImpl setHiddenProjectIds(java.util.List<java.lang.String> v) {
      _hasHiddenProjectIds = true;
      hiddenProjectIds = v;
      return this;
    }

    public void addHiddenProjectIds(java.lang.String v) {
      ensureHiddenProjectIds();
      hiddenProjectIds.add(v);
    }

    public void clearHiddenProjectIds() {
      ensureHiddenProjectIds();
      hiddenProjectIds.clear();
    }

    private void ensureHiddenProjectIds() {
      if (!_hasHiddenProjectIds) {
        setHiddenProjectIds(hiddenProjectIds != null ? hiddenProjectIds : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasActiveProjectId() {
      return _hasActiveProjectId;
    }

    @Override
    public java.lang.String getActiveProjectId() {
      return activeProjectId;
    }

    public GetProjectsResponseImpl setActiveProjectId(java.lang.String v) {
      _hasActiveProjectId = true;
      activeProjectId = v;
      return this;
    }

    public boolean hasUserMembershipChangeNextVersion() {
      return _hasUserMembershipChangeNextVersion;
    }

    @Override
    public java.lang.String getUserMembershipChangeNextVersion() {
      return userMembershipChangeNextVersion;
    }

    public GetProjectsResponseImpl setUserMembershipChangeNextVersion(java.lang.String v) {
      _hasUserMembershipChangeNextVersion = true;
      userMembershipChangeNextVersion = v;
      return this;
    }

    public boolean hasProjects() {
      return _hasProjects;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ProjectInfo> getProjects() {
      ensureProjects();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(projects);
    }

    public GetProjectsResponseImpl setProjects(java.util.List<ProjectInfoImpl> v) {
      _hasProjects = true;
      projects = v;
      return this;
    }

    public void addProjects(ProjectInfoImpl v) {
      ensureProjects();
      projects.add(v);
    }

    public void clearProjects() {
      ensureProjects();
      projects.clear();
    }

    private void ensureProjects() {
      if (!_hasProjects) {
        setProjects(projects != null ? projects : new java.util.ArrayList<ProjectInfoImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetProjectsResponseImpl)) {
        return false;
      }
      GetProjectsResponseImpl other = (GetProjectsResponseImpl) o;
      if (this._hasHiddenProjectIds != other._hasHiddenProjectIds) {
        return false;
      }
      if (this._hasHiddenProjectIds) {
        if (!this.hiddenProjectIds.equals(other.hiddenProjectIds)) {
          return false;
        }
      }
      if (this._hasActiveProjectId != other._hasActiveProjectId) {
        return false;
      }
      if (this._hasActiveProjectId) {
        if (!this.activeProjectId.equals(other.activeProjectId)) {
          return false;
        }
      }
      if (this._hasUserMembershipChangeNextVersion != other._hasUserMembershipChangeNextVersion) {
        return false;
      }
      if (this._hasUserMembershipChangeNextVersion) {
        if (!this.userMembershipChangeNextVersion.equals(other.userMembershipChangeNextVersion)) {
          return false;
        }
      }
      if (this._hasProjects != other._hasProjects) {
        return false;
      }
      if (this._hasProjects) {
        if (!this.projects.equals(other.projects)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasHiddenProjectIds ? hiddenProjectIds.hashCode() : 0);
      hash = hash * 31 + (_hasActiveProjectId ? activeProjectId.hashCode() : 0);
      hash = hash * 31 + (_hasUserMembershipChangeNextVersion ? userMembershipChangeNextVersion.hashCode() : 0);
      hash = hash * 31 + (_hasProjects ? projects.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray hiddenProjectIdsOut = new JsonArray();
      ensureHiddenProjectIds();
      for (java.lang.String hiddenProjectIds_ : hiddenProjectIds) {
        JsonElement hiddenProjectIdsOut_ = (hiddenProjectIds_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(hiddenProjectIds_);
        hiddenProjectIdsOut.add(hiddenProjectIdsOut_);
      }
      result.add("hiddenProjectIds", hiddenProjectIdsOut);

      JsonElement activeProjectIdOut = (activeProjectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(activeProjectId);
      result.add("activeProjectId", activeProjectIdOut);

      JsonElement userMembershipChangeNextVersionOut = (userMembershipChangeNextVersion == null) ? JsonNull.INSTANCE : new JsonPrimitive(userMembershipChangeNextVersion);
      result.add("userMembershipChangeNextVersion", userMembershipChangeNextVersionOut);

      JsonArray projectsOut = new JsonArray();
      ensureProjects();
      for (ProjectInfoImpl projects_ : projects) {
        JsonElement projectsOut_ = projects_ == null ? JsonNull.INSTANCE : projects_.toJsonElement();
        projectsOut.add(projectsOut_);
      }
      result.add("projects", projectsOut);
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

    public static GetProjectsResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetProjectsResponseImpl dto = new GetProjectsResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("hiddenProjectIds")) {
        JsonElement hiddenProjectIdsIn = json.get("hiddenProjectIds");
        java.util.ArrayList<java.lang.String> hiddenProjectIdsOut = null;
        if (hiddenProjectIdsIn != null && !hiddenProjectIdsIn.isJsonNull()) {
          hiddenProjectIdsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> hiddenProjectIdsInIterator = hiddenProjectIdsIn.getAsJsonArray().iterator();
          while (hiddenProjectIdsInIterator.hasNext()) {
            JsonElement hiddenProjectIdsIn_ = hiddenProjectIdsInIterator.next();
            java.lang.String hiddenProjectIdsOut_ = gson.fromJson(hiddenProjectIdsIn_, java.lang.String.class);
            hiddenProjectIdsOut.add(hiddenProjectIdsOut_);
          }
        }
        dto.setHiddenProjectIds(hiddenProjectIdsOut);
      }

      if (json.has("activeProjectId")) {
        JsonElement activeProjectIdIn = json.get("activeProjectId");
        java.lang.String activeProjectIdOut = gson.fromJson(activeProjectIdIn, java.lang.String.class);
        dto.setActiveProjectId(activeProjectIdOut);
      }

      if (json.has("userMembershipChangeNextVersion")) {
        JsonElement userMembershipChangeNextVersionIn = json.get("userMembershipChangeNextVersion");
        java.lang.String userMembershipChangeNextVersionOut = gson.fromJson(userMembershipChangeNextVersionIn, java.lang.String.class);
        dto.setUserMembershipChangeNextVersion(userMembershipChangeNextVersionOut);
      }

      if (json.has("projects")) {
        JsonElement projectsIn = json.get("projects");
        java.util.ArrayList<ProjectInfoImpl> projectsOut = null;
        if (projectsIn != null && !projectsIn.isJsonNull()) {
          projectsOut = new java.util.ArrayList<ProjectInfoImpl>();
          java.util.Iterator<JsonElement> projectsInIterator = projectsIn.getAsJsonArray().iterator();
          while (projectsInIterator.hasNext()) {
            JsonElement projectsIn_ = projectsInIterator.next();
            ProjectInfoImpl projectsOut_ = ProjectInfoImpl.fromJsonElement(projectsIn_);
            projectsOut.add(projectsOut_);
          }
        }
        dto.setProjects(projectsOut);
      }

      return dto;
    }
    public static GetProjectsResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetStagingServerInfoResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetStagingServerInfoResponse, JsonSerializable {

    private GetStagingServerInfoResponseImpl() {
      super(50);
    }

    protected GetStagingServerInfoResponseImpl(int type) {
      super(type);
    }

    public static GetStagingServerInfoResponseImpl make() {
      return new GetStagingServerInfoResponseImpl();
    }

    protected java.lang.String stagingServerAppId;
    private boolean _hasStagingServerAppId;
    protected int latestMimicVersionId;
    private boolean _hasLatestMimicVersionId;
    protected int lastKnownMimicVersionId;
    private boolean _hasLastKnownMimicVersionId;
    protected boolean autoUpdate;
    private boolean _hasAutoUpdate;

    public boolean hasStagingServerAppId() {
      return _hasStagingServerAppId;
    }

    @Override
    public java.lang.String getStagingServerAppId() {
      return stagingServerAppId;
    }

    public GetStagingServerInfoResponseImpl setStagingServerAppId(java.lang.String v) {
      _hasStagingServerAppId = true;
      stagingServerAppId = v;
      return this;
    }

    public boolean hasLatestMimicVersionId() {
      return _hasLatestMimicVersionId;
    }

    @Override
    public int getLatestMimicVersionId() {
      return latestMimicVersionId;
    }

    public GetStagingServerInfoResponseImpl setLatestMimicVersionId(int v) {
      _hasLatestMimicVersionId = true;
      latestMimicVersionId = v;
      return this;
    }

    public boolean hasLastKnownMimicVersionId() {
      return _hasLastKnownMimicVersionId;
    }

    @Override
    public int getLastKnownMimicVersionId() {
      return lastKnownMimicVersionId;
    }

    public GetStagingServerInfoResponseImpl setLastKnownMimicVersionId(int v) {
      _hasLastKnownMimicVersionId = true;
      lastKnownMimicVersionId = v;
      return this;
    }

    public boolean hasAutoUpdate() {
      return _hasAutoUpdate;
    }

    @Override
    public boolean getAutoUpdate() {
      return autoUpdate;
    }

    public GetStagingServerInfoResponseImpl setAutoUpdate(boolean v) {
      _hasAutoUpdate = true;
      autoUpdate = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetStagingServerInfoResponseImpl)) {
        return false;
      }
      GetStagingServerInfoResponseImpl other = (GetStagingServerInfoResponseImpl) o;
      if (this._hasStagingServerAppId != other._hasStagingServerAppId) {
        return false;
      }
      if (this._hasStagingServerAppId) {
        if (!this.stagingServerAppId.equals(other.stagingServerAppId)) {
          return false;
        }
      }
      if (this._hasLatestMimicVersionId != other._hasLatestMimicVersionId) {
        return false;
      }
      if (this._hasLatestMimicVersionId) {
        if (this.latestMimicVersionId != other.latestMimicVersionId) {
          return false;
        }
      }
      if (this._hasLastKnownMimicVersionId != other._hasLastKnownMimicVersionId) {
        return false;
      }
      if (this._hasLastKnownMimicVersionId) {
        if (this.lastKnownMimicVersionId != other.lastKnownMimicVersionId) {
          return false;
        }
      }
      if (this._hasAutoUpdate != other._hasAutoUpdate) {
        return false;
      }
      if (this._hasAutoUpdate) {
        if (this.autoUpdate != other.autoUpdate) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasStagingServerAppId ? stagingServerAppId.hashCode() : 0);
      hash = hash * 31 + (_hasLatestMimicVersionId ? java.lang.Integer.valueOf(latestMimicVersionId).hashCode() : 0);
      hash = hash * 31 + (_hasLastKnownMimicVersionId ? java.lang.Integer.valueOf(lastKnownMimicVersionId).hashCode() : 0);
      hash = hash * 31 + (_hasAutoUpdate ? java.lang.Boolean.valueOf(autoUpdate).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement stagingServerAppIdOut = (stagingServerAppId == null) ? JsonNull.INSTANCE : new JsonPrimitive(stagingServerAppId);
      result.add("stagingServerAppId", stagingServerAppIdOut);

      JsonPrimitive latestMimicVersionIdOut = new JsonPrimitive(latestMimicVersionId);
      result.add("latestMimicVersionId", latestMimicVersionIdOut);

      JsonPrimitive lastKnownMimicVersionIdOut = new JsonPrimitive(lastKnownMimicVersionId);
      result.add("lastKnownMimicVersionId", lastKnownMimicVersionIdOut);

      JsonPrimitive autoUpdateOut = new JsonPrimitive(autoUpdate);
      result.add("autoUpdate", autoUpdateOut);
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

    public static GetStagingServerInfoResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetStagingServerInfoResponseImpl dto = new GetStagingServerInfoResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("stagingServerAppId")) {
        JsonElement stagingServerAppIdIn = json.get("stagingServerAppId");
        java.lang.String stagingServerAppIdOut = gson.fromJson(stagingServerAppIdIn, java.lang.String.class);
        dto.setStagingServerAppId(stagingServerAppIdOut);
      }

      if (json.has("latestMimicVersionId")) {
        JsonElement latestMimicVersionIdIn = json.get("latestMimicVersionId");
        int latestMimicVersionIdOut = latestMimicVersionIdIn.getAsInt();
        dto.setLatestMimicVersionId(latestMimicVersionIdOut);
      }

      if (json.has("lastKnownMimicVersionId")) {
        JsonElement lastKnownMimicVersionIdIn = json.get("lastKnownMimicVersionId");
        int lastKnownMimicVersionIdOut = lastKnownMimicVersionIdIn.getAsInt();
        dto.setLastKnownMimicVersionId(lastKnownMimicVersionIdOut);
      }

      if (json.has("autoUpdate")) {
        JsonElement autoUpdateIn = json.get("autoUpdate");
        boolean autoUpdateOut = autoUpdateIn.getAsBoolean();
        dto.setAutoUpdate(autoUpdateOut);
      }

      return dto;
    }
    public static GetStagingServerInfoResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetSyncStateImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetSyncState, JsonSerializable {

    private GetSyncStateImpl() {
      super(51);
    }

    protected GetSyncStateImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetSyncStateImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetSyncStateImpl)) {
        return false;
      }
      GetSyncStateImpl other = (GetSyncStateImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static GetSyncStateImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetSyncStateImpl dto = new GetSyncStateImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static GetSyncStateImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetSyncStateResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetSyncStateResponse, JsonSerializable {

    private GetSyncStateResponseImpl() {
      super(52);
    }

    protected GetSyncStateResponseImpl(int type) {
      super(type);
    }

    public static GetSyncStateResponseImpl make() {
      return new GetSyncStateResponseImpl();
    }

    protected com.google.collide.dto.GetSyncStateResponse.SyncState syncState;
    private boolean _hasSyncState;

    public boolean hasSyncState() {
      return _hasSyncState;
    }

    @Override
    public com.google.collide.dto.GetSyncStateResponse.SyncState getSyncState() {
      return syncState;
    }

    public GetSyncStateResponseImpl setSyncState(com.google.collide.dto.GetSyncStateResponse.SyncState v) {
      _hasSyncState = true;
      syncState = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetSyncStateResponseImpl)) {
        return false;
      }
      GetSyncStateResponseImpl other = (GetSyncStateResponseImpl) o;
      if (this._hasSyncState != other._hasSyncState) {
        return false;
      }
      if (this._hasSyncState) {
        if (!this.syncState.equals(other.syncState)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasSyncState ? syncState.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement syncStateOut = (syncState == null) ? JsonNull.INSTANCE : new JsonPrimitive(syncState.name());
      result.add("syncState", syncStateOut);
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

    public static GetSyncStateResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetSyncStateResponseImpl dto = new GetSyncStateResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("syncState")) {
        JsonElement syncStateIn = json.get("syncState");
        com.google.collide.dto.GetSyncStateResponse.SyncState syncStateOut = gson.fromJson(syncStateIn, com.google.collide.dto.GetSyncStateResponse.SyncState.class);
        dto.setSyncState(syncStateOut);
      }

      return dto;
    }
    public static GetSyncStateResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetTemplatesImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetTemplates, JsonSerializable {

    private GetTemplatesImpl() {
      super(53);
    }

    protected GetTemplatesImpl(int type) {
      super(type);
    }

    protected java.lang.String locale;
    private boolean _hasLocale;

    public boolean hasLocale() {
      return _hasLocale;
    }

    @Override
    public java.lang.String getLocale() {
      return locale;
    }

    public GetTemplatesImpl setLocale(java.lang.String v) {
      _hasLocale = true;
      locale = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetTemplatesImpl)) {
        return false;
      }
      GetTemplatesImpl other = (GetTemplatesImpl) o;
      if (this._hasLocale != other._hasLocale) {
        return false;
      }
      if (this._hasLocale) {
        if (!this.locale.equals(other.locale)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasLocale ? locale.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement localeOut = (locale == null) ? JsonNull.INSTANCE : new JsonPrimitive(locale);
      result.add("locale", localeOut);
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

    public static GetTemplatesImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetTemplatesImpl dto = new GetTemplatesImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("locale")) {
        JsonElement localeIn = json.get("locale");
        java.lang.String localeOut = gson.fromJson(localeIn, java.lang.String.class);
        dto.setLocale(localeOut);
      }

      return dto;
    }
    public static GetTemplatesImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetTemplatesResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetTemplatesResponse, JsonSerializable {

    private GetTemplatesResponseImpl() {
      super(54);
    }

    protected GetTemplatesResponseImpl(int type) {
      super(type);
    }

    public static GetTemplatesResponseImpl make() {
      return new GetTemplatesResponseImpl();
    }

    protected java.util.Map<String, java.lang.String> templates;
    private boolean _hasTemplates;

    public boolean hasTemplates() {
      return _hasTemplates;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonStringMap<java.lang.String> getTemplates() {
      ensureTemplates();
      return (org.exoplatform.ide.json.shared.JsonStringMap) new org.exoplatform.ide.json.server.JsonStringMapAdapter(templates);
    }

    public GetTemplatesResponseImpl setTemplates(java.util.Map<String, java.lang.String> v) {
      _hasTemplates = true;
      templates = v;
      return this;
    }

    public void putTemplates(String k, java.lang.String v) {
      ensureTemplates();
      templates.put(k, v);
    }

    public void clearTemplates() {
      ensureTemplates();
      templates.clear();
    }

    private void ensureTemplates() {
      if (!_hasTemplates) {
        setTemplates(templates != null ? templates : new java.util.HashMap<String, java.lang.String>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetTemplatesResponseImpl)) {
        return false;
      }
      GetTemplatesResponseImpl other = (GetTemplatesResponseImpl) o;
      if (this._hasTemplates != other._hasTemplates) {
        return false;
      }
      if (this._hasTemplates) {
        if (!this.templates.equals(other.templates)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasTemplates ? templates.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonObject templatesOut = new JsonObject();
      ensureTemplates();
      for (Map.Entry<String, java.lang.String> entry0 : templates.entrySet()) {
        java.lang.String templates_ = entry0.getValue();
        JsonElement templatesOut_ = (templates_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(templates_);
        templatesOut.add(entry0.getKey(), templatesOut_);
      }
      result.add("templates", templatesOut);
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

    public static GetTemplatesResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetTemplatesResponseImpl dto = new GetTemplatesResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("templates")) {
        JsonElement templatesIn = json.get("templates");
        java.util.HashMap<String, java.lang.String> templatesOut = null;
        if (templatesIn != null && !templatesIn.isJsonNull()) {
          templatesOut = new java.util.HashMap<String, java.lang.String>();
          java.util.Set<Map.Entry<String, JsonElement>> entries0 = templatesIn.getAsJsonObject().entrySet();
          for (Map.Entry<String, JsonElement> entry0 : entries0) {
            JsonElement templatesIn_ = entry0.getValue();
            java.lang.String templatesOut_ = gson.fromJson(templatesIn_, java.lang.String.class);
            templatesOut.put(entry0.getKey(), templatesOut_);
          }
        }
        dto.setTemplates(templatesOut);
      }

      return dto;
    }
    public static GetTemplatesResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetUserAppEngineAppIdsImpl extends GetAppEngineClusterTypeImpl implements com.google.collide.dto.GetUserAppEngineAppIds, JsonSerializable {

    private GetUserAppEngineAppIdsImpl() {
      super(55);
    }

    protected GetUserAppEngineAppIdsImpl(int type) {
      super(type);
    }


    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetUserAppEngineAppIdsImpl)) {
        return false;
      }
      GetUserAppEngineAppIdsImpl other = (GetUserAppEngineAppIdsImpl) o;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clusterTypeOut = (clusterType == null) ? JsonNull.INSTANCE : new JsonPrimitive(clusterType.name());
      result.add("clusterType", clusterTypeOut);
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

    public static GetUserAppEngineAppIdsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetUserAppEngineAppIdsImpl dto = new GetUserAppEngineAppIdsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clusterType")) {
        JsonElement clusterTypeIn = json.get("clusterType");
        com.google.collide.dto.GetAppEngineClusterType.Type clusterTypeOut = gson.fromJson(clusterTypeIn, com.google.collide.dto.GetAppEngineClusterType.Type.class);
        dto.setClusterType(clusterTypeOut);
      }

      return dto;
    }
    public static GetUserAppEngineAppIdsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetUserAppEngineAppIdsResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetUserAppEngineAppIdsResponse, JsonSerializable {

    private GetUserAppEngineAppIdsResponseImpl() {
      super(56);
    }

    protected GetUserAppEngineAppIdsResponseImpl(int type) {
      super(type);
    }

    public static GetUserAppEngineAppIdsResponseImpl make() {
      return new GetUserAppEngineAppIdsResponseImpl();
    }

    protected java.util.List<java.lang.String> appIds;
    private boolean _hasAppIds;

    public boolean hasAppIds() {
      return _hasAppIds;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getAppIds() {
      ensureAppIds();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(appIds);
    }

    public GetUserAppEngineAppIdsResponseImpl setAppIds(java.util.List<java.lang.String> v) {
      _hasAppIds = true;
      appIds = v;
      return this;
    }

    public void addAppIds(java.lang.String v) {
      ensureAppIds();
      appIds.add(v);
    }

    public void clearAppIds() {
      ensureAppIds();
      appIds.clear();
    }

    private void ensureAppIds() {
      if (!_hasAppIds) {
        setAppIds(appIds != null ? appIds : new java.util.ArrayList<java.lang.String>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetUserAppEngineAppIdsResponseImpl)) {
        return false;
      }
      GetUserAppEngineAppIdsResponseImpl other = (GetUserAppEngineAppIdsResponseImpl) o;
      if (this._hasAppIds != other._hasAppIds) {
        return false;
      }
      if (this._hasAppIds) {
        if (!this.appIds.equals(other.appIds)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasAppIds ? appIds.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray appIdsOut = new JsonArray();
      ensureAppIds();
      for (java.lang.String appIds_ : appIds) {
        JsonElement appIdsOut_ = (appIds_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(appIds_);
        appIdsOut.add(appIdsOut_);
      }
      result.add("appIds", appIdsOut);
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

    public static GetUserAppEngineAppIdsResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetUserAppEngineAppIdsResponseImpl dto = new GetUserAppEngineAppIdsResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("appIds")) {
        JsonElement appIdsIn = json.get("appIds");
        java.util.ArrayList<java.lang.String> appIdsOut = null;
        if (appIdsIn != null && !appIdsIn.isJsonNull()) {
          appIdsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> appIdsInIterator = appIdsIn.getAsJsonArray().iterator();
          while (appIdsInIterator.hasNext()) {
            JsonElement appIdsIn_ = appIdsInIterator.next();
            java.lang.String appIdsOut_ = gson.fromJson(appIdsIn_, java.lang.String.class);
            appIdsOut.add(appIdsOut_);
          }
        }
        dto.setAppIds(appIdsOut);
      }

      return dto;
    }
    public static GetUserAppEngineAppIdsResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspace, JsonSerializable {

    private GetWorkspaceImpl() {
      super(63);
    }

    protected GetWorkspaceImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetWorkspaceImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public GetWorkspaceImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceImpl)) {
        return false;
      }
      GetWorkspaceImpl other = (GetWorkspaceImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
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
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

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

    public static GetWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceImpl dto = new GetWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static GetWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceChangeSummaryImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceChangeSummary, JsonSerializable {

    private GetWorkspaceChangeSummaryImpl() {
      super(57);
    }

    protected GetWorkspaceChangeSummaryImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetWorkspaceChangeSummaryImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public GetWorkspaceChangeSummaryImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceChangeSummaryImpl)) {
        return false;
      }
      GetWorkspaceChangeSummaryImpl other = (GetWorkspaceChangeSummaryImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
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
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

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

    public static GetWorkspaceChangeSummaryImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceChangeSummaryImpl dto = new GetWorkspaceChangeSummaryImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static GetWorkspaceChangeSummaryImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceChangeSummaryResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceChangeSummaryResponse, JsonSerializable {

    private GetWorkspaceChangeSummaryResponseImpl() {
      super(58);
    }

    protected GetWorkspaceChangeSummaryResponseImpl(int type) {
      super(type);
    }

    public static GetWorkspaceChangeSummaryResponseImpl make() {
      return new GetWorkspaceChangeSummaryResponseImpl();
    }

    protected java.util.List<NodeMutationDtoImpl> nodeMutations;
    private boolean _hasNodeMutations;
    protected java.lang.String baseRootId;
    private boolean _hasBaseRootId;
    protected java.lang.String finalRootId;
    private boolean _hasFinalRootId;

    public boolean hasNodeMutations() {
      return _hasNodeMutations;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.NodeMutationDto> getNodeMutations() {
      ensureNodeMutations();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(nodeMutations);
    }

    public GetWorkspaceChangeSummaryResponseImpl setNodeMutations(java.util.List<NodeMutationDtoImpl> v) {
      _hasNodeMutations = true;
      nodeMutations = v;
      return this;
    }

    public void addNodeMutations(NodeMutationDtoImpl v) {
      ensureNodeMutations();
      nodeMutations.add(v);
    }

    public void clearNodeMutations() {
      ensureNodeMutations();
      nodeMutations.clear();
    }

    private void ensureNodeMutations() {
      if (!_hasNodeMutations) {
        setNodeMutations(nodeMutations != null ? nodeMutations : new java.util.ArrayList<NodeMutationDtoImpl>());
      }
    }

    public boolean hasBaseRootId() {
      return _hasBaseRootId;
    }

    @Override
    public java.lang.String getBaseRootId() {
      return baseRootId;
    }

    public GetWorkspaceChangeSummaryResponseImpl setBaseRootId(java.lang.String v) {
      _hasBaseRootId = true;
      baseRootId = v;
      return this;
    }

    public boolean hasFinalRootId() {
      return _hasFinalRootId;
    }

    @Override
    public java.lang.String getFinalRootId() {
      return finalRootId;
    }

    public GetWorkspaceChangeSummaryResponseImpl setFinalRootId(java.lang.String v) {
      _hasFinalRootId = true;
      finalRootId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceChangeSummaryResponseImpl)) {
        return false;
      }
      GetWorkspaceChangeSummaryResponseImpl other = (GetWorkspaceChangeSummaryResponseImpl) o;
      if (this._hasNodeMutations != other._hasNodeMutations) {
        return false;
      }
      if (this._hasNodeMutations) {
        if (!this.nodeMutations.equals(other.nodeMutations)) {
          return false;
        }
      }
      if (this._hasBaseRootId != other._hasBaseRootId) {
        return false;
      }
      if (this._hasBaseRootId) {
        if (!this.baseRootId.equals(other.baseRootId)) {
          return false;
        }
      }
      if (this._hasFinalRootId != other._hasFinalRootId) {
        return false;
      }
      if (this._hasFinalRootId) {
        if (!this.finalRootId.equals(other.finalRootId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasNodeMutations ? nodeMutations.hashCode() : 0);
      hash = hash * 31 + (_hasBaseRootId ? baseRootId.hashCode() : 0);
      hash = hash * 31 + (_hasFinalRootId ? finalRootId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray nodeMutationsOut = new JsonArray();
      ensureNodeMutations();
      for (NodeMutationDtoImpl nodeMutations_ : nodeMutations) {
        JsonElement nodeMutationsOut_ = nodeMutations_ == null ? JsonNull.INSTANCE : nodeMutations_.toJsonElement();
        nodeMutationsOut.add(nodeMutationsOut_);
      }
      result.add("nodeMutations", nodeMutationsOut);

      JsonElement baseRootIdOut = (baseRootId == null) ? JsonNull.INSTANCE : new JsonPrimitive(baseRootId);
      result.add("baseRootId", baseRootIdOut);

      JsonElement finalRootIdOut = (finalRootId == null) ? JsonNull.INSTANCE : new JsonPrimitive(finalRootId);
      result.add("finalRootId", finalRootIdOut);
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

    public static GetWorkspaceChangeSummaryResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceChangeSummaryResponseImpl dto = new GetWorkspaceChangeSummaryResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("nodeMutations")) {
        JsonElement nodeMutationsIn = json.get("nodeMutations");
        java.util.ArrayList<NodeMutationDtoImpl> nodeMutationsOut = null;
        if (nodeMutationsIn != null && !nodeMutationsIn.isJsonNull()) {
          nodeMutationsOut = new java.util.ArrayList<NodeMutationDtoImpl>();
          java.util.Iterator<JsonElement> nodeMutationsInIterator = nodeMutationsIn.getAsJsonArray().iterator();
          while (nodeMutationsInIterator.hasNext()) {
            JsonElement nodeMutationsIn_ = nodeMutationsInIterator.next();
            NodeMutationDtoImpl nodeMutationsOut_ = NodeMutationDtoImpl.fromJsonElement(nodeMutationsIn_);
            nodeMutationsOut.add(nodeMutationsOut_);
          }
        }
        dto.setNodeMutations(nodeMutationsOut);
      }

      if (json.has("baseRootId")) {
        JsonElement baseRootIdIn = json.get("baseRootId");
        java.lang.String baseRootIdOut = gson.fromJson(baseRootIdIn, java.lang.String.class);
        dto.setBaseRootId(baseRootIdOut);
      }

      if (json.has("finalRootId")) {
        JsonElement finalRootIdIn = json.get("finalRootId");
        java.lang.String finalRootIdOut = gson.fromJson(finalRootIdIn, java.lang.String.class);
        dto.setFinalRootId(finalRootIdOut);
      }

      return dto;
    }
    public static GetWorkspaceChangeSummaryResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceMembersImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceMembers, JsonSerializable {

    private GetWorkspaceMembersImpl() {
      super(59);
    }

    protected GetWorkspaceMembersImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetWorkspaceMembersImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public GetWorkspaceMembersImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceMembersImpl)) {
        return false;
      }
      GetWorkspaceMembersImpl other = (GetWorkspaceMembersImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
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
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

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

    public static GetWorkspaceMembersImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceMembersImpl dto = new GetWorkspaceMembersImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static GetWorkspaceMembersImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceMembersResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceMembersResponse, JsonSerializable {

    private GetWorkspaceMembersResponseImpl() {
      super(60);
    }

    protected GetWorkspaceMembersResponseImpl(int type) {
      super(type);
    }

    public static GetWorkspaceMembersResponseImpl make() {
      return new GetWorkspaceMembersResponseImpl();
    }

    protected java.util.List<UserDetailsWithRoleImpl> members;
    private boolean _hasMembers;

    public boolean hasMembers() {
      return _hasMembers;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.UserDetailsWithRole> getMembers() {
      ensureMembers();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(members);
    }

    public GetWorkspaceMembersResponseImpl setMembers(java.util.List<UserDetailsWithRoleImpl> v) {
      _hasMembers = true;
      members = v;
      return this;
    }

    public void addMembers(UserDetailsWithRoleImpl v) {
      ensureMembers();
      members.add(v);
    }

    public void clearMembers() {
      ensureMembers();
      members.clear();
    }

    private void ensureMembers() {
      if (!_hasMembers) {
        setMembers(members != null ? members : new java.util.ArrayList<UserDetailsWithRoleImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceMembersResponseImpl)) {
        return false;
      }
      GetWorkspaceMembersResponseImpl other = (GetWorkspaceMembersResponseImpl) o;
      if (this._hasMembers != other._hasMembers) {
        return false;
      }
      if (this._hasMembers) {
        if (!this.members.equals(other.members)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasMembers ? members.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray membersOut = new JsonArray();
      ensureMembers();
      for (UserDetailsWithRoleImpl members_ : members) {
        JsonElement membersOut_ = members_ == null ? JsonNull.INSTANCE : members_.toJsonElement();
        membersOut.add(membersOut_);
      }
      result.add("members", membersOut);
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

    public static GetWorkspaceMembersResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceMembersResponseImpl dto = new GetWorkspaceMembersResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("members")) {
        JsonElement membersIn = json.get("members");
        java.util.ArrayList<UserDetailsWithRoleImpl> membersOut = null;
        if (membersIn != null && !membersIn.isJsonNull()) {
          membersOut = new java.util.ArrayList<UserDetailsWithRoleImpl>();
          java.util.Iterator<JsonElement> membersInIterator = membersIn.getAsJsonArray().iterator();
          while (membersInIterator.hasNext()) {
            JsonElement membersIn_ = membersInIterator.next();
            UserDetailsWithRoleImpl membersOut_ = UserDetailsWithRoleImpl.fromJsonElement(membersIn_);
            membersOut.add(membersOut_);
          }
        }
        dto.setMembers(membersOut);
      }

      return dto;
    }
    public static GetWorkspaceMembersResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceMetaDataImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceMetaData, JsonSerializable {

    private GetWorkspaceMetaDataImpl() {
      super(122);
    }

    protected GetWorkspaceMetaDataImpl(int type) {
      super(type);
    }


    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceMetaDataImpl)) {
        return false;
      }
      GetWorkspaceMetaDataImpl other = (GetWorkspaceMetaDataImpl) o;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();
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

    public static GetWorkspaceMetaDataImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceMetaDataImpl dto = new GetWorkspaceMetaDataImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      return dto;
    }
    public static GetWorkspaceMetaDataImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceMetaDataResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceMetaDataResponse, JsonSerializable {

    private GetWorkspaceMetaDataResponseImpl() {
      super(115);
    }

    protected GetWorkspaceMetaDataResponseImpl(int type) {
      super(type);
    }

    public static GetWorkspaceMetaDataResponseImpl make() {
      return new GetWorkspaceMetaDataResponseImpl();
    }

    protected java.lang.String workspaceName;
    private boolean _hasWorkspaceName;
    protected java.util.List<java.lang.String> lastOpenFiles;
    private boolean _hasLastOpenFiles;
    protected RunTargetImpl runTarget;
    private boolean _hasRunTarget;

    public boolean hasWorkspaceName() {
      return _hasWorkspaceName;
    }

    @Override
    public java.lang.String getWorkspaceName() {
      return workspaceName;
    }

    public GetWorkspaceMetaDataResponseImpl setWorkspaceName(java.lang.String v) {
      _hasWorkspaceName = true;
      workspaceName = v;
      return this;
    }

    public boolean hasLastOpenFiles() {
      return _hasLastOpenFiles;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getLastOpenFiles() {
      ensureLastOpenFiles();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(lastOpenFiles);
    }

    public GetWorkspaceMetaDataResponseImpl setLastOpenFiles(java.util.List<java.lang.String> v) {
      _hasLastOpenFiles = true;
      lastOpenFiles = v;
      return this;
    }

    public void addLastOpenFiles(java.lang.String v) {
      ensureLastOpenFiles();
      lastOpenFiles.add(v);
    }

    public void clearLastOpenFiles() {
      ensureLastOpenFiles();
      lastOpenFiles.clear();
    }

    private void ensureLastOpenFiles() {
      if (!_hasLastOpenFiles) {
        setLastOpenFiles(lastOpenFiles != null ? lastOpenFiles : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasRunTarget() {
      return _hasRunTarget;
    }

    @Override
    public com.google.collide.dto.RunTarget getRunTarget() {
      return runTarget;
    }

    public GetWorkspaceMetaDataResponseImpl setRunTarget(RunTargetImpl v) {
      _hasRunTarget = true;
      runTarget = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceMetaDataResponseImpl)) {
        return false;
      }
      GetWorkspaceMetaDataResponseImpl other = (GetWorkspaceMetaDataResponseImpl) o;
      if (this._hasWorkspaceName != other._hasWorkspaceName) {
        return false;
      }
      if (this._hasWorkspaceName) {
        if (!this.workspaceName.equals(other.workspaceName)) {
          return false;
        }
      }
      if (this._hasLastOpenFiles != other._hasLastOpenFiles) {
        return false;
      }
      if (this._hasLastOpenFiles) {
        if (!this.lastOpenFiles.equals(other.lastOpenFiles)) {
          return false;
        }
      }
      if (this._hasRunTarget != other._hasRunTarget) {
        return false;
      }
      if (this._hasRunTarget) {
        if (!this.runTarget.equals(other.runTarget)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceName ? workspaceName.hashCode() : 0);
      hash = hash * 31 + (_hasLastOpenFiles ? lastOpenFiles.hashCode() : 0);
      hash = hash * 31 + (_hasRunTarget ? runTarget.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceNameOut = (workspaceName == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceName);
      result.add("workspaceName", workspaceNameOut);

      JsonArray lastOpenFilesOut = new JsonArray();
      ensureLastOpenFiles();
      for (java.lang.String lastOpenFiles_ : lastOpenFiles) {
        JsonElement lastOpenFilesOut_ = (lastOpenFiles_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(lastOpenFiles_);
        lastOpenFilesOut.add(lastOpenFilesOut_);
      }
      result.add("lastOpenFiles", lastOpenFilesOut);

      JsonElement runTargetOut = runTarget == null ? JsonNull.INSTANCE : runTarget.toJsonElement();
      result.add("runTarget", runTargetOut);
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

    public static GetWorkspaceMetaDataResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceMetaDataResponseImpl dto = new GetWorkspaceMetaDataResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceName")) {
        JsonElement workspaceNameIn = json.get("workspaceName");
        java.lang.String workspaceNameOut = gson.fromJson(workspaceNameIn, java.lang.String.class);
        dto.setWorkspaceName(workspaceNameOut);
      }

      if (json.has("lastOpenFiles")) {
        JsonElement lastOpenFilesIn = json.get("lastOpenFiles");
        java.util.ArrayList<java.lang.String> lastOpenFilesOut = null;
        if (lastOpenFilesIn != null && !lastOpenFilesIn.isJsonNull()) {
          lastOpenFilesOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> lastOpenFilesInIterator = lastOpenFilesIn.getAsJsonArray().iterator();
          while (lastOpenFilesInIterator.hasNext()) {
            JsonElement lastOpenFilesIn_ = lastOpenFilesInIterator.next();
            java.lang.String lastOpenFilesOut_ = gson.fromJson(lastOpenFilesIn_, java.lang.String.class);
            lastOpenFilesOut.add(lastOpenFilesOut_);
          }
        }
        dto.setLastOpenFiles(lastOpenFilesOut);
      }

      if (json.has("runTarget")) {
        JsonElement runTargetIn = json.get("runTarget");
        RunTargetImpl runTargetOut = RunTargetImpl.fromJsonElement(runTargetIn);
        dto.setRunTarget(runTargetOut);
      }

      return dto;
    }
    public static GetWorkspaceMetaDataResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceParticipantsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceParticipants, JsonSerializable {

    private GetWorkspaceParticipantsImpl() {
      super(61);
    }

    protected GetWorkspaceParticipantsImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.util.List<java.lang.String> participantIds;
    private boolean _hasParticipantIds;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public GetWorkspaceParticipantsImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasParticipantIds() {
      return _hasParticipantIds;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getParticipantIds() {
      ensureParticipantIds();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(participantIds);
    }

    public GetWorkspaceParticipantsImpl setParticipantIds(java.util.List<java.lang.String> v) {
      _hasParticipantIds = true;
      participantIds = v;
      return this;
    }

    public void addParticipantIds(java.lang.String v) {
      ensureParticipantIds();
      participantIds.add(v);
    }

    public void clearParticipantIds() {
      ensureParticipantIds();
      participantIds.clear();
    }

    private void ensureParticipantIds() {
      if (!_hasParticipantIds) {
        setParticipantIds(participantIds != null ? participantIds : new java.util.ArrayList<java.lang.String>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceParticipantsImpl)) {
        return false;
      }
      GetWorkspaceParticipantsImpl other = (GetWorkspaceParticipantsImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasParticipantIds != other._hasParticipantIds) {
        return false;
      }
      if (this._hasParticipantIds) {
        if (!this.participantIds.equals(other.participantIds)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasParticipantIds ? participantIds.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonArray participantIdsOut = new JsonArray();
      ensureParticipantIds();
      for (java.lang.String participantIds_ : participantIds) {
        JsonElement participantIdsOut_ = (participantIds_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(participantIds_);
        participantIdsOut.add(participantIdsOut_);
      }
      result.add("participantIds", participantIdsOut);
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

    public static GetWorkspaceParticipantsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceParticipantsImpl dto = new GetWorkspaceParticipantsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("participantIds")) {
        JsonElement participantIdsIn = json.get("participantIds");
        java.util.ArrayList<java.lang.String> participantIdsOut = null;
        if (participantIdsIn != null && !participantIdsIn.isJsonNull()) {
          participantIdsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> participantIdsInIterator = participantIdsIn.getAsJsonArray().iterator();
          while (participantIdsInIterator.hasNext()) {
            JsonElement participantIdsIn_ = participantIdsInIterator.next();
            java.lang.String participantIdsOut_ = gson.fromJson(participantIdsIn_, java.lang.String.class);
            participantIdsOut.add(participantIdsOut_);
          }
        }
        dto.setParticipantIds(participantIdsOut);
      }

      return dto;
    }
    public static GetWorkspaceParticipantsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceParticipantsResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceParticipantsResponse, JsonSerializable {

    private GetWorkspaceParticipantsResponseImpl() {
      super(62);
    }

    protected GetWorkspaceParticipantsResponseImpl(int type) {
      super(type);
    }

    public static GetWorkspaceParticipantsResponseImpl make() {
      return new GetWorkspaceParticipantsResponseImpl();
    }

    protected java.util.List<ParticipantUserDetailsImpl> participants;
    private boolean _hasParticipants;

    public boolean hasParticipants() {
      return _hasParticipants;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ParticipantUserDetails> getParticipants() {
      ensureParticipants();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(participants);
    }

    public GetWorkspaceParticipantsResponseImpl setParticipants(java.util.List<ParticipantUserDetailsImpl> v) {
      _hasParticipants = true;
      participants = v;
      return this;
    }

    public void addParticipants(ParticipantUserDetailsImpl v) {
      ensureParticipants();
      participants.add(v);
    }

    public void clearParticipants() {
      ensureParticipants();
      participants.clear();
    }

    private void ensureParticipants() {
      if (!_hasParticipants) {
        setParticipants(participants != null ? participants : new java.util.ArrayList<ParticipantUserDetailsImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceParticipantsResponseImpl)) {
        return false;
      }
      GetWorkspaceParticipantsResponseImpl other = (GetWorkspaceParticipantsResponseImpl) o;
      if (this._hasParticipants != other._hasParticipants) {
        return false;
      }
      if (this._hasParticipants) {
        if (!this.participants.equals(other.participants)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasParticipants ? participants.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray participantsOut = new JsonArray();
      ensureParticipants();
      for (ParticipantUserDetailsImpl participants_ : participants) {
        JsonElement participantsOut_ = participants_ == null ? JsonNull.INSTANCE : participants_.toJsonElement();
        participantsOut.add(participantsOut_);
      }
      result.add("participants", participantsOut);
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

    public static GetWorkspaceParticipantsResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceParticipantsResponseImpl dto = new GetWorkspaceParticipantsResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("participants")) {
        JsonElement participantsIn = json.get("participants");
        java.util.ArrayList<ParticipantUserDetailsImpl> participantsOut = null;
        if (participantsIn != null && !participantsIn.isJsonNull()) {
          participantsOut = new java.util.ArrayList<ParticipantUserDetailsImpl>();
          java.util.Iterator<JsonElement> participantsInIterator = participantsIn.getAsJsonArray().iterator();
          while (participantsInIterator.hasNext()) {
            JsonElement participantsIn_ = participantsInIterator.next();
            ParticipantUserDetailsImpl participantsOut_ = ParticipantUserDetailsImpl.fromJsonElement(participantsIn_);
            participantsOut.add(participantsOut_);
          }
        }
        dto.setParticipants(participantsOut);
      }

      return dto;
    }
    public static GetWorkspaceParticipantsResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class GetWorkspaceResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.GetWorkspaceResponse, JsonSerializable {

    private GetWorkspaceResponseImpl() {
      super(64);
    }

    protected GetWorkspaceResponseImpl(int type) {
      super(type);
    }

    public static GetWorkspaceResponseImpl make() {
      return new GetWorkspaceResponseImpl();
    }

    protected WorkspaceInfoImpl workspace;
    private boolean _hasWorkspace;

    public boolean hasWorkspace() {
      return _hasWorkspace;
    }

    @Override
    public com.google.collide.dto.WorkspaceInfo getWorkspace() {
      return workspace;
    }

    public GetWorkspaceResponseImpl setWorkspace(WorkspaceInfoImpl v) {
      _hasWorkspace = true;
      workspace = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof GetWorkspaceResponseImpl)) {
        return false;
      }
      GetWorkspaceResponseImpl other = (GetWorkspaceResponseImpl) o;
      if (this._hasWorkspace != other._hasWorkspace) {
        return false;
      }
      if (this._hasWorkspace) {
        if (!this.workspace.equals(other.workspace)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspace ? workspace.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceOut = workspace == null ? JsonNull.INSTANCE : workspace.toJsonElement();
      result.add("workspace", workspaceOut);
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

    public static GetWorkspaceResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      GetWorkspaceResponseImpl dto = new GetWorkspaceResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspace")) {
        JsonElement workspaceIn = json.get("workspace");
        WorkspaceInfoImpl workspaceOut = WorkspaceInfoImpl.fromJsonElement(workspaceIn);
        dto.setWorkspace(workspaceOut);
      }

      return dto;
    }
    public static GetWorkspaceResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ImportAssociationImpl extends CodeBlockAssociationImpl implements com.google.collide.dto.ImportAssociation, JsonSerializable {

    private ImportAssociationImpl() {
      super(65);
    }

    protected ImportAssociationImpl(int type) {
      super(type);
    }

    public static ImportAssociationImpl make() {
      return new ImportAssociationImpl();
    }


    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ImportAssociationImpl)) {
        return false;
      }
      ImportAssociationImpl other = (ImportAssociationImpl) o;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonArray result = new JsonArray();

      JsonElement sourceFileIdOut = (sourceFileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sourceFileId);
      result.add(sourceFileIdOut);

      JsonElement sourceLocalIdOut = (sourceLocalId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sourceLocalId);
      result.add(sourceLocalIdOut);

      JsonElement targetFileIdOut = (targetFileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetFileId);
      result.add(targetFileIdOut);

      JsonElement targetLocalIdOut = (targetLocalId == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetLocalId);
      result.add(targetLocalIdOut);

      JsonPrimitive isRootAssociationOut = new JsonPrimitive(isRootAssociation);
      result.add(isRootAssociationOut);
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

    public static ImportAssociationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ImportAssociationImpl dto = new ImportAssociationImpl();
      JsonArray json = jsonElem.getAsJsonArray();

      if (0 < json.size()) {
        JsonElement sourceFileIdIn = json.get(0);
        java.lang.String sourceFileIdOut = gson.fromJson(sourceFileIdIn, java.lang.String.class);
        dto.setSourceFileId(sourceFileIdOut);
      }

      if (1 < json.size()) {
        JsonElement sourceLocalIdIn = json.get(1);
        java.lang.String sourceLocalIdOut = gson.fromJson(sourceLocalIdIn, java.lang.String.class);
        dto.setSourceLocalId(sourceLocalIdOut);
      }

      if (2 < json.size()) {
        JsonElement targetFileIdIn = json.get(2);
        java.lang.String targetFileIdOut = gson.fromJson(targetFileIdIn, java.lang.String.class);
        dto.setTargetFileId(targetFileIdOut);
      }

      if (3 < json.size()) {
        JsonElement targetLocalIdIn = json.get(3);
        java.lang.String targetLocalIdOut = gson.fromJson(targetLocalIdIn, java.lang.String.class);
        dto.setTargetLocalId(targetLocalIdOut);
      }

      if (4 < json.size()) {
        JsonElement isRootAssociationIn = json.get(4);
        boolean isRootAssociationOut = isRootAssociationIn.getAsBoolean();
        dto.setIsRootAssociation(isRootAssociationOut);
      }

      return dto;
    }
    public static ImportAssociationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class InheritanceAssociationImpl extends CodeBlockAssociationImpl implements com.google.collide.dto.InheritanceAssociation, JsonSerializable {

    private InheritanceAssociationImpl() {
      super(66);
    }

    protected InheritanceAssociationImpl(int type) {
      super(type);
    }

    public static InheritanceAssociationImpl make() {
      return new InheritanceAssociationImpl();
    }


    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof InheritanceAssociationImpl)) {
        return false;
      }
      InheritanceAssociationImpl other = (InheritanceAssociationImpl) o;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonArray result = new JsonArray();

      JsonElement sourceFileIdOut = (sourceFileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sourceFileId);
      result.add(sourceFileIdOut);

      JsonElement sourceLocalIdOut = (sourceLocalId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sourceLocalId);
      result.add(sourceLocalIdOut);

      JsonElement targetFileIdOut = (targetFileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetFileId);
      result.add(targetFileIdOut);

      JsonElement targetLocalIdOut = (targetLocalId == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetLocalId);
      result.add(targetLocalIdOut);

      JsonPrimitive isRootAssociationOut = new JsonPrimitive(isRootAssociation);
      result.add(isRootAssociationOut);
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

    public static InheritanceAssociationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      InheritanceAssociationImpl dto = new InheritanceAssociationImpl();
      JsonArray json = jsonElem.getAsJsonArray();

      if (0 < json.size()) {
        JsonElement sourceFileIdIn = json.get(0);
        java.lang.String sourceFileIdOut = gson.fromJson(sourceFileIdIn, java.lang.String.class);
        dto.setSourceFileId(sourceFileIdOut);
      }

      if (1 < json.size()) {
        JsonElement sourceLocalIdIn = json.get(1);
        java.lang.String sourceLocalIdOut = gson.fromJson(sourceLocalIdIn, java.lang.String.class);
        dto.setSourceLocalId(sourceLocalIdOut);
      }

      if (2 < json.size()) {
        JsonElement targetFileIdIn = json.get(2);
        java.lang.String targetFileIdOut = gson.fromJson(targetFileIdIn, java.lang.String.class);
        dto.setTargetFileId(targetFileIdOut);
      }

      if (3 < json.size()) {
        JsonElement targetLocalIdIn = json.get(3);
        java.lang.String targetLocalIdOut = gson.fromJson(targetLocalIdIn, java.lang.String.class);
        dto.setTargetLocalId(targetLocalIdOut);
      }

      if (4 < json.size()) {
        JsonElement isRootAssociationIn = json.get(4);
        boolean isRootAssociationOut = isRootAssociationIn.getAsBoolean();
        dto.setIsRootAssociation(isRootAssociationOut);
      }

      return dto;
    }
    public static InheritanceAssociationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class InvalidationMessageImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.InvalidationMessage, JsonSerializable {

    private InvalidationMessageImpl() {
      super(25);
    }

    protected InvalidationMessageImpl(int type) {
      super(type);
    }

    public static InvalidationMessageImpl make() {
      return new InvalidationMessageImpl();
    }

    protected java.lang.String payload;
    private boolean _hasPayload;
    protected java.lang.String objectName;
    private boolean _hasObjectName;
    protected java.lang.String version;
    private boolean _hasVersion;

    public boolean hasPayload() {
      return _hasPayload;
    }

    @Override
    public java.lang.String getPayload() {
      return payload;
    }

    public InvalidationMessageImpl setPayload(java.lang.String v) {
      _hasPayload = true;
      payload = v;
      return this;
    }

    public boolean hasObjectName() {
      return _hasObjectName;
    }

    @Override
    public java.lang.String getObjectName() {
      return objectName;
    }

    public InvalidationMessageImpl setObjectName(java.lang.String v) {
      _hasObjectName = true;
      objectName = v;
      return this;
    }

    public boolean hasVersion() {
      return _hasVersion;
    }

    @Override
    public java.lang.String getVersion() {
      return version;
    }

    public InvalidationMessageImpl setVersion(java.lang.String v) {
      _hasVersion = true;
      version = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof InvalidationMessageImpl)) {
        return false;
      }
      InvalidationMessageImpl other = (InvalidationMessageImpl) o;
      if (this._hasPayload != other._hasPayload) {
        return false;
      }
      if (this._hasPayload) {
        if (!this.payload.equals(other.payload)) {
          return false;
        }
      }
      if (this._hasObjectName != other._hasObjectName) {
        return false;
      }
      if (this._hasObjectName) {
        if (!this.objectName.equals(other.objectName)) {
          return false;
        }
      }
      if (this._hasVersion != other._hasVersion) {
        return false;
      }
      if (this._hasVersion) {
        if (!this.version.equals(other.version)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasPayload ? payload.hashCode() : 0);
      hash = hash * 31 + (_hasObjectName ? objectName.hashCode() : 0);
      hash = hash * 31 + (_hasVersion ? version.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement payloadOut = (payload == null) ? JsonNull.INSTANCE : new JsonPrimitive(payload);
      result.add("payload", payloadOut);

      JsonElement objectNameOut = (objectName == null) ? JsonNull.INSTANCE : new JsonPrimitive(objectName);
      result.add("objectName", objectNameOut);

      JsonElement versionOut = (version == null) ? JsonNull.INSTANCE : new JsonPrimitive(version);
      result.add("version", versionOut);
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

    public static InvalidationMessageImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      InvalidationMessageImpl dto = new InvalidationMessageImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("payload")) {
        JsonElement payloadIn = json.get("payload");
        java.lang.String payloadOut = gson.fromJson(payloadIn, java.lang.String.class);
        dto.setPayload(payloadOut);
      }

      if (json.has("objectName")) {
        JsonElement objectNameIn = json.get("objectName");
        java.lang.String objectNameOut = gson.fromJson(objectNameIn, java.lang.String.class);
        dto.setObjectName(objectNameOut);
      }

      if (json.has("version")) {
        JsonElement versionIn = json.get("version");
        java.lang.String versionOut = gson.fromJson(versionIn, java.lang.String.class);
        dto.setVersion(versionOut);
      }

      return dto;
    }
    public static InvalidationMessageImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class KeepAliveImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.KeepAlive, JsonSerializable {

    private KeepAliveImpl() {
      super(67);
    }

    protected KeepAliveImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public KeepAliveImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof KeepAliveImpl)) {
        return false;
      }
      KeepAliveImpl other = (KeepAliveImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static KeepAliveImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      KeepAliveImpl dto = new KeepAliveImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static KeepAliveImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class LeaveWorkspaceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.LeaveWorkspace, JsonSerializable {

    private LeaveWorkspaceImpl() {
      super(69);
    }

    protected LeaveWorkspaceImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public LeaveWorkspaceImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof LeaveWorkspaceImpl)) {
        return false;
      }
      LeaveWorkspaceImpl other = (LeaveWorkspaceImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static LeaveWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      LeaveWorkspaceImpl dto = new LeaveWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static LeaveWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class LoadTemplateImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.LoadTemplate, JsonSerializable {

    private LoadTemplateImpl() {
      super(70);
    }

    protected LoadTemplateImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String templateTag;
    private boolean _hasTemplateTag;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public LoadTemplateImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasTemplateTag() {
      return _hasTemplateTag;
    }

    @Override
    public java.lang.String getTemplateTag() {
      return templateTag;
    }

    public LoadTemplateImpl setTemplateTag(java.lang.String v) {
      _hasTemplateTag = true;
      templateTag = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public LoadTemplateImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof LoadTemplateImpl)) {
        return false;
      }
      LoadTemplateImpl other = (LoadTemplateImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasTemplateTag != other._hasTemplateTag) {
        return false;
      }
      if (this._hasTemplateTag) {
        if (!this.templateTag.equals(other.templateTag)) {
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
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasTemplateTag ? templateTag.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement templateTagOut = (templateTag == null) ? JsonNull.INSTANCE : new JsonPrimitive(templateTag);
      result.add("templateTag", templateTagOut);

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

    public static LoadTemplateImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      LoadTemplateImpl dto = new LoadTemplateImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("templateTag")) {
        JsonElement templateTagIn = json.get("templateTag");
        java.lang.String templateTagOut = gson.fromJson(templateTagIn, java.lang.String.class);
        dto.setTemplateTag(templateTagOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static LoadTemplateImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class LoadTemplateResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.LoadTemplateResponse, JsonSerializable {

    private LoadTemplateResponseImpl() {
      super(71);
    }

    protected LoadTemplateResponseImpl(int type) {
      super(type);
    }

    public static LoadTemplateResponseImpl make() {
      return new LoadTemplateResponseImpl();
    }

    protected RunTargetImpl runTarget;
    private boolean _hasRunTarget;

    public boolean hasRunTarget() {
      return _hasRunTarget;
    }

    @Override
    public com.google.collide.dto.RunTarget getRunTarget() {
      return runTarget;
    }

    public LoadTemplateResponseImpl setRunTarget(RunTargetImpl v) {
      _hasRunTarget = true;
      runTarget = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof LoadTemplateResponseImpl)) {
        return false;
      }
      LoadTemplateResponseImpl other = (LoadTemplateResponseImpl) o;
      if (this._hasRunTarget != other._hasRunTarget) {
        return false;
      }
      if (this._hasRunTarget) {
        if (!this.runTarget.equals(other.runTarget)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasRunTarget ? runTarget.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement runTargetOut = runTarget == null ? JsonNull.INSTANCE : runTarget.toJsonElement();
      result.add("runTarget", runTargetOut);
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

    public static LoadTemplateResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      LoadTemplateResponseImpl dto = new LoadTemplateResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("runTarget")) {
        JsonElement runTargetIn = json.get("runTarget");
        RunTargetImpl runTargetOut = RunTargetImpl.fromJsonElement(runTargetIn);
        dto.setRunTarget(runTargetOut);
      }

      return dto;
    }
    public static LoadTemplateResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class LogFatalRecordImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.LogFatalRecord, JsonSerializable {

    private LogFatalRecordImpl() {
      super(72);
    }

    protected LogFatalRecordImpl(int type) {
      super(type);
    }

    protected ThrowableDtoImpl throwable;
    private boolean _hasThrowable;
    protected java.lang.String windowLocation;
    private boolean _hasWindowLocation;
    protected java.util.List<java.lang.String> recentHistory;
    private boolean _hasRecentHistory;
    protected java.lang.String permutationStrongName;
    private boolean _hasPermutationStrongName;
    protected java.lang.String message;
    private boolean _hasMessage;

    public boolean hasThrowable() {
      return _hasThrowable;
    }

    @Override
    public com.google.collide.dto.ThrowableDto getThrowable() {
      return throwable;
    }

    public LogFatalRecordImpl setThrowable(ThrowableDtoImpl v) {
      _hasThrowable = true;
      throwable = v;
      return this;
    }

    public boolean hasWindowLocation() {
      return _hasWindowLocation;
    }

    @Override
    public java.lang.String getWindowLocation() {
      return windowLocation;
    }

    public LogFatalRecordImpl setWindowLocation(java.lang.String v) {
      _hasWindowLocation = true;
      windowLocation = v;
      return this;
    }

    public boolean hasRecentHistory() {
      return _hasRecentHistory;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getRecentHistory() {
      ensureRecentHistory();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(recentHistory);
    }

    public LogFatalRecordImpl setRecentHistory(java.util.List<java.lang.String> v) {
      _hasRecentHistory = true;
      recentHistory = v;
      return this;
    }

    public void addRecentHistory(java.lang.String v) {
      ensureRecentHistory();
      recentHistory.add(v);
    }

    public void clearRecentHistory() {
      ensureRecentHistory();
      recentHistory.clear();
    }

    private void ensureRecentHistory() {
      if (!_hasRecentHistory) {
        setRecentHistory(recentHistory != null ? recentHistory : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasPermutationStrongName() {
      return _hasPermutationStrongName;
    }

    @Override
    public java.lang.String getPermutationStrongName() {
      return permutationStrongName;
    }

    public LogFatalRecordImpl setPermutationStrongName(java.lang.String v) {
      _hasPermutationStrongName = true;
      permutationStrongName = v;
      return this;
    }

    public boolean hasMessage() {
      return _hasMessage;
    }

    @Override
    public java.lang.String getMessage() {
      return message;
    }

    public LogFatalRecordImpl setMessage(java.lang.String v) {
      _hasMessage = true;
      message = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof LogFatalRecordImpl)) {
        return false;
      }
      LogFatalRecordImpl other = (LogFatalRecordImpl) o;
      if (this._hasThrowable != other._hasThrowable) {
        return false;
      }
      if (this._hasThrowable) {
        if (!this.throwable.equals(other.throwable)) {
          return false;
        }
      }
      if (this._hasWindowLocation != other._hasWindowLocation) {
        return false;
      }
      if (this._hasWindowLocation) {
        if (!this.windowLocation.equals(other.windowLocation)) {
          return false;
        }
      }
      if (this._hasRecentHistory != other._hasRecentHistory) {
        return false;
      }
      if (this._hasRecentHistory) {
        if (!this.recentHistory.equals(other.recentHistory)) {
          return false;
        }
      }
      if (this._hasPermutationStrongName != other._hasPermutationStrongName) {
        return false;
      }
      if (this._hasPermutationStrongName) {
        if (!this.permutationStrongName.equals(other.permutationStrongName)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasThrowable ? throwable.hashCode() : 0);
      hash = hash * 31 + (_hasWindowLocation ? windowLocation.hashCode() : 0);
      hash = hash * 31 + (_hasRecentHistory ? recentHistory.hashCode() : 0);
      hash = hash * 31 + (_hasPermutationStrongName ? permutationStrongName.hashCode() : 0);
      hash = hash * 31 + (_hasMessage ? message.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement throwableOut = throwable == null ? JsonNull.INSTANCE : throwable.toJsonElement();
      result.add("throwable", throwableOut);

      JsonElement windowLocationOut = (windowLocation == null) ? JsonNull.INSTANCE : new JsonPrimitive(windowLocation);
      result.add("windowLocation", windowLocationOut);

      JsonArray recentHistoryOut = new JsonArray();
      ensureRecentHistory();
      for (java.lang.String recentHistory_ : recentHistory) {
        JsonElement recentHistoryOut_ = (recentHistory_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(recentHistory_);
        recentHistoryOut.add(recentHistoryOut_);
      }
      result.add("recentHistory", recentHistoryOut);

      JsonElement permutationStrongNameOut = (permutationStrongName == null) ? JsonNull.INSTANCE : new JsonPrimitive(permutationStrongName);
      result.add("permutationStrongName", permutationStrongNameOut);

      JsonElement messageOut = (message == null) ? JsonNull.INSTANCE : new JsonPrimitive(message);
      result.add("message", messageOut);
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

    public static LogFatalRecordImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      LogFatalRecordImpl dto = new LogFatalRecordImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("throwable")) {
        JsonElement throwableIn = json.get("throwable");
        ThrowableDtoImpl throwableOut = ThrowableDtoImpl.fromJsonElement(throwableIn);
        dto.setThrowable(throwableOut);
      }

      if (json.has("windowLocation")) {
        JsonElement windowLocationIn = json.get("windowLocation");
        java.lang.String windowLocationOut = gson.fromJson(windowLocationIn, java.lang.String.class);
        dto.setWindowLocation(windowLocationOut);
      }

      if (json.has("recentHistory")) {
        JsonElement recentHistoryIn = json.get("recentHistory");
        java.util.ArrayList<java.lang.String> recentHistoryOut = null;
        if (recentHistoryIn != null && !recentHistoryIn.isJsonNull()) {
          recentHistoryOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> recentHistoryInIterator = recentHistoryIn.getAsJsonArray().iterator();
          while (recentHistoryInIterator.hasNext()) {
            JsonElement recentHistoryIn_ = recentHistoryInIterator.next();
            java.lang.String recentHistoryOut_ = gson.fromJson(recentHistoryIn_, java.lang.String.class);
            recentHistoryOut.add(recentHistoryOut_);
          }
        }
        dto.setRecentHistory(recentHistoryOut);
      }

      if (json.has("permutationStrongName")) {
        JsonElement permutationStrongNameIn = json.get("permutationStrongName");
        java.lang.String permutationStrongNameOut = gson.fromJson(permutationStrongNameIn, java.lang.String.class);
        dto.setPermutationStrongName(permutationStrongNameOut);
      }

      if (json.has("message")) {
        JsonElement messageIn = json.get("message");
        java.lang.String messageOut = gson.fromJson(messageIn, java.lang.String.class);
        dto.setMessage(messageOut);
      }

      return dto;
    }
    public static LogFatalRecordImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class LogFatalRecordResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.LogFatalRecordResponse, JsonSerializable {

    private LogFatalRecordResponseImpl() {
      super(73);
    }

    protected LogFatalRecordResponseImpl(int type) {
      super(type);
    }

    public static LogFatalRecordResponseImpl make() {
      return new LogFatalRecordResponseImpl();
    }

    protected java.lang.String serviceName;
    private boolean _hasServiceName;
    protected java.lang.String throwableProtoHex;
    private boolean _hasThrowableProtoHex;
    protected java.lang.String stackTrace;
    private boolean _hasStackTrace;

    public boolean hasServiceName() {
      return _hasServiceName;
    }

    @Override
    public java.lang.String getServiceName() {
      return serviceName;
    }

    public LogFatalRecordResponseImpl setServiceName(java.lang.String v) {
      _hasServiceName = true;
      serviceName = v;
      return this;
    }

    public boolean hasThrowableProtoHex() {
      return _hasThrowableProtoHex;
    }

    @Override
    public java.lang.String getThrowableProtoHex() {
      return throwableProtoHex;
    }

    public LogFatalRecordResponseImpl setThrowableProtoHex(java.lang.String v) {
      _hasThrowableProtoHex = true;
      throwableProtoHex = v;
      return this;
    }

    public boolean hasStackTrace() {
      return _hasStackTrace;
    }

    @Override
    public java.lang.String getStackTrace() {
      return stackTrace;
    }

    public LogFatalRecordResponseImpl setStackTrace(java.lang.String v) {
      _hasStackTrace = true;
      stackTrace = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof LogFatalRecordResponseImpl)) {
        return false;
      }
      LogFatalRecordResponseImpl other = (LogFatalRecordResponseImpl) o;
      if (this._hasServiceName != other._hasServiceName) {
        return false;
      }
      if (this._hasServiceName) {
        if (!this.serviceName.equals(other.serviceName)) {
          return false;
        }
      }
      if (this._hasThrowableProtoHex != other._hasThrowableProtoHex) {
        return false;
      }
      if (this._hasThrowableProtoHex) {
        if (!this.throwableProtoHex.equals(other.throwableProtoHex)) {
          return false;
        }
      }
      if (this._hasStackTrace != other._hasStackTrace) {
        return false;
      }
      if (this._hasStackTrace) {
        if (!this.stackTrace.equals(other.stackTrace)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasServiceName ? serviceName.hashCode() : 0);
      hash = hash * 31 + (_hasThrowableProtoHex ? throwableProtoHex.hashCode() : 0);
      hash = hash * 31 + (_hasStackTrace ? stackTrace.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement serviceNameOut = (serviceName == null) ? JsonNull.INSTANCE : new JsonPrimitive(serviceName);
      result.add("serviceName", serviceNameOut);

      JsonElement throwableProtoHexOut = (throwableProtoHex == null) ? JsonNull.INSTANCE : new JsonPrimitive(throwableProtoHex);
      result.add("throwableProtoHex", throwableProtoHexOut);

      JsonElement stackTraceOut = (stackTrace == null) ? JsonNull.INSTANCE : new JsonPrimitive(stackTrace);
      result.add("stackTrace", stackTraceOut);
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

    public static LogFatalRecordResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      LogFatalRecordResponseImpl dto = new LogFatalRecordResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("serviceName")) {
        JsonElement serviceNameIn = json.get("serviceName");
        java.lang.String serviceNameOut = gson.fromJson(serviceNameIn, java.lang.String.class);
        dto.setServiceName(serviceNameOut);
      }

      if (json.has("throwableProtoHex")) {
        JsonElement throwableProtoHexIn = json.get("throwableProtoHex");
        java.lang.String throwableProtoHexOut = gson.fromJson(throwableProtoHexIn, java.lang.String.class);
        dto.setThrowableProtoHex(throwableProtoHexOut);
      }

      if (json.has("stackTrace")) {
        JsonElement stackTraceIn = json.get("stackTrace");
        java.lang.String stackTraceOut = gson.fromJson(stackTraceIn, java.lang.String.class);
        dto.setStackTrace(stackTraceOut);
      }

      return dto;
    }
    public static LogFatalRecordResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class LogMetricImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.LogMetric, JsonSerializable {

    private LogMetricImpl() {
      super(74);
    }

    protected LogMetricImpl(int type) {
      super(type);
    }

    protected java.lang.String event;
    private boolean _hasEvent;
    protected java.lang.String action;
    private boolean _hasAction;
    protected java.lang.String message;
    private boolean _hasMessage;
    protected double timestamp;
    private boolean _hasTimestamp;

    public boolean hasEvent() {
      return _hasEvent;
    }

    @Override
    public java.lang.String getEvent() {
      return event;
    }

    public LogMetricImpl setEvent(java.lang.String v) {
      _hasEvent = true;
      event = v;
      return this;
    }

    public boolean hasAction() {
      return _hasAction;
    }

    @Override
    public java.lang.String getAction() {
      return action;
    }

    public LogMetricImpl setAction(java.lang.String v) {
      _hasAction = true;
      action = v;
      return this;
    }

    public boolean hasMessage() {
      return _hasMessage;
    }

    @Override
    public java.lang.String getMessage() {
      return message;
    }

    public LogMetricImpl setMessage(java.lang.String v) {
      _hasMessage = true;
      message = v;
      return this;
    }

    public boolean hasTimestamp() {
      return _hasTimestamp;
    }

    @Override
    public double getTimestamp() {
      return timestamp;
    }

    public LogMetricImpl setTimestamp(double v) {
      _hasTimestamp = true;
      timestamp = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof LogMetricImpl)) {
        return false;
      }
      LogMetricImpl other = (LogMetricImpl) o;
      if (this._hasEvent != other._hasEvent) {
        return false;
      }
      if (this._hasEvent) {
        if (!this.event.equals(other.event)) {
          return false;
        }
      }
      if (this._hasAction != other._hasAction) {
        return false;
      }
      if (this._hasAction) {
        if (!this.action.equals(other.action)) {
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
      if (this._hasTimestamp != other._hasTimestamp) {
        return false;
      }
      if (this._hasTimestamp) {
        if (this.timestamp != other.timestamp) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasEvent ? event.hashCode() : 0);
      hash = hash * 31 + (_hasAction ? action.hashCode() : 0);
      hash = hash * 31 + (_hasMessage ? message.hashCode() : 0);
      hash = hash * 31 + (_hasTimestamp ? java.lang.Double.valueOf(timestamp).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement eventOut = (event == null) ? JsonNull.INSTANCE : new JsonPrimitive(event);
      result.add("event", eventOut);

      JsonElement actionOut = (action == null) ? JsonNull.INSTANCE : new JsonPrimitive(action);
      result.add("action", actionOut);

      JsonElement messageOut = (message == null) ? JsonNull.INSTANCE : new JsonPrimitive(message);
      result.add("message", messageOut);

      JsonPrimitive timestampOut = new JsonPrimitive(timestamp);
      result.add("timestamp", timestampOut);
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

    public static LogMetricImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      LogMetricImpl dto = new LogMetricImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("event")) {
        JsonElement eventIn = json.get("event");
        java.lang.String eventOut = gson.fromJson(eventIn, java.lang.String.class);
        dto.setEvent(eventOut);
      }

      if (json.has("action")) {
        JsonElement actionIn = json.get("action");
        java.lang.String actionOut = gson.fromJson(actionIn, java.lang.String.class);
        dto.setAction(actionOut);
      }

      if (json.has("message")) {
        JsonElement messageIn = json.get("message");
        java.lang.String messageOut = gson.fromJson(messageIn, java.lang.String.class);
        dto.setMessage(messageOut);
      }

      if (json.has("timestamp")) {
        JsonElement timestampIn = json.get("timestamp");
        double timestampOut = timestampIn.getAsDouble();
        dto.setTimestamp(timestampOut);
      }

      return dto;
    }
    public static LogMetricImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class LogMetricsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.LogMetrics, JsonSerializable {

    private LogMetricsImpl() {
      super(75);
    }

    protected LogMetricsImpl(int type) {
      super(type);
    }

    protected java.util.List<LogMetricImpl> metrics;
    private boolean _hasMetrics;

    public boolean hasMetrics() {
      return _hasMetrics;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.LogMetric> getMetrics() {
      ensureMetrics();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(metrics);
    }

    public LogMetricsImpl setMetrics(java.util.List<LogMetricImpl> v) {
      _hasMetrics = true;
      metrics = v;
      return this;
    }

    public void addMetrics(LogMetricImpl v) {
      ensureMetrics();
      metrics.add(v);
    }

    public void clearMetrics() {
      ensureMetrics();
      metrics.clear();
    }

    private void ensureMetrics() {
      if (!_hasMetrics) {
        setMetrics(metrics != null ? metrics : new java.util.ArrayList<LogMetricImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof LogMetricsImpl)) {
        return false;
      }
      LogMetricsImpl other = (LogMetricsImpl) o;
      if (this._hasMetrics != other._hasMetrics) {
        return false;
      }
      if (this._hasMetrics) {
        if (!this.metrics.equals(other.metrics)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasMetrics ? metrics.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray metricsOut = new JsonArray();
      ensureMetrics();
      for (LogMetricImpl metrics_ : metrics) {
        JsonElement metricsOut_ = metrics_ == null ? JsonNull.INSTANCE : metrics_.toJsonElement();
        metricsOut.add(metricsOut_);
      }
      result.add("metrics", metricsOut);
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

    public static LogMetricsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      LogMetricsImpl dto = new LogMetricsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("metrics")) {
        JsonElement metricsIn = json.get("metrics");
        java.util.ArrayList<LogMetricImpl> metricsOut = null;
        if (metricsIn != null && !metricsIn.isJsonNull()) {
          metricsOut = new java.util.ArrayList<LogMetricImpl>();
          java.util.Iterator<JsonElement> metricsInIterator = metricsIn.getAsJsonArray().iterator();
          while (metricsInIterator.hasNext()) {
            JsonElement metricsIn_ = metricsInIterator.next();
            LogMetricImpl metricsOut_ = LogMetricImpl.fromJsonElement(metricsIn_);
            metricsOut.add(metricsOut_);
          }
        }
        dto.setMetrics(metricsOut);
      }

      return dto;
    }
    public static LogMetricsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class MembershipChangedPayloadImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.MembershipChangedPayload, JsonSerializable {

    private MembershipChangedPayloadImpl() {
      super(76);
    }

    protected MembershipChangedPayloadImpl(int type) {
      super(type);
    }

    public static MembershipChangedPayloadImpl make() {
      return new MembershipChangedPayloadImpl();
    }

    protected com.google.collide.dto.MembershipChangedPayload.MembershipChange membershipChange;
    private boolean _hasMembershipChange;
    protected java.lang.String id;
    private boolean _hasId;

    public boolean hasMembershipChange() {
      return _hasMembershipChange;
    }

    @Override
    public com.google.collide.dto.MembershipChangedPayload.MembershipChange getMembershipChange() {
      return membershipChange;
    }

    public MembershipChangedPayloadImpl setMembershipChange(com.google.collide.dto.MembershipChangedPayload.MembershipChange v) {
      _hasMembershipChange = true;
      membershipChange = v;
      return this;
    }

    public boolean hasId() {
      return _hasId;
    }

    @Override
    public java.lang.String getId() {
      return id;
    }

    public MembershipChangedPayloadImpl setId(java.lang.String v) {
      _hasId = true;
      id = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof MembershipChangedPayloadImpl)) {
        return false;
      }
      MembershipChangedPayloadImpl other = (MembershipChangedPayloadImpl) o;
      if (this._hasMembershipChange != other._hasMembershipChange) {
        return false;
      }
      if (this._hasMembershipChange) {
        if (!this.membershipChange.equals(other.membershipChange)) {
          return false;
        }
      }
      if (this._hasId != other._hasId) {
        return false;
      }
      if (this._hasId) {
        if (!this.id.equals(other.id)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasMembershipChange ? membershipChange.hashCode() : 0);
      hash = hash * 31 + (_hasId ? id.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement membershipChangeOut = (membershipChange == null) ? JsonNull.INSTANCE : new JsonPrimitive(membershipChange.name());
      result.add("membershipChange", membershipChangeOut);

      JsonElement idOut = (id == null) ? JsonNull.INSTANCE : new JsonPrimitive(id);
      result.add("id", idOut);
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

    public static MembershipChangedPayloadImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      MembershipChangedPayloadImpl dto = new MembershipChangedPayloadImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("membershipChange")) {
        JsonElement membershipChangeIn = json.get("membershipChange");
        com.google.collide.dto.MembershipChangedPayload.MembershipChange membershipChangeOut = gson.fromJson(membershipChangeIn, com.google.collide.dto.MembershipChangedPayload.MembershipChange.class);
        dto.setMembershipChange(membershipChangeOut);
      }

      if (json.has("id")) {
        JsonElement idIn = json.get("id");
        java.lang.String idOut = gson.fromJson(idIn, java.lang.String.class);
        dto.setId(idOut);
      }

      return dto;
    }
    public static MembershipChangedPayloadImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class MutationImpl implements com.google.collide.dto.Mutation, JsonSerializable {

    public static MutationImpl make() {
      return new MutationImpl();
    }

    protected com.google.collide.dto.Mutation.Type mutationType;
    private boolean _hasMutationType;
    protected TreeNodeInfoImpl newNodeInfo;
    private boolean _hasNewNodeInfo;
    protected java.lang.String oldPath;
    private boolean _hasOldPath;
    protected java.lang.String newPath;
    private boolean _hasNewPath;

    public boolean hasMutationType() {
      return _hasMutationType;
    }

    @Override
    public com.google.collide.dto.Mutation.Type getMutationType() {
      return mutationType;
    }

    public MutationImpl setMutationType(com.google.collide.dto.Mutation.Type v) {
      _hasMutationType = true;
      mutationType = v;
      return this;
    }

    public boolean hasNewNodeInfo() {
      return _hasNewNodeInfo;
    }

    @Override
    public com.google.collide.dto.TreeNodeInfo getNewNodeInfo() {
      return newNodeInfo;
    }

    public MutationImpl setNewNodeInfo(TreeNodeInfoImpl v) {
      _hasNewNodeInfo = true;
      newNodeInfo = v;
      return this;
    }

    public boolean hasOldPath() {
      return _hasOldPath;
    }

    @Override
    public java.lang.String getOldPath() {
      return oldPath;
    }

    public MutationImpl setOldPath(java.lang.String v) {
      _hasOldPath = true;
      oldPath = v;
      return this;
    }

    public boolean hasNewPath() {
      return _hasNewPath;
    }

    @Override
    public java.lang.String getNewPath() {
      return newPath;
    }

    public MutationImpl setNewPath(java.lang.String v) {
      _hasNewPath = true;
      newPath = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof MutationImpl)) {
        return false;
      }
      MutationImpl other = (MutationImpl) o;
      if (this._hasMutationType != other._hasMutationType) {
        return false;
      }
      if (this._hasMutationType) {
        if (!this.mutationType.equals(other.mutationType)) {
          return false;
        }
      }
      if (this._hasNewNodeInfo != other._hasNewNodeInfo) {
        return false;
      }
      if (this._hasNewNodeInfo) {
        if (!this.newNodeInfo.equals(other.newNodeInfo)) {
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
      if (this._hasNewPath != other._hasNewPath) {
        return false;
      }
      if (this._hasNewPath) {
        if (!this.newPath.equals(other.newPath)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasMutationType ? mutationType.hashCode() : 0);
      hash = hash * 31 + (_hasNewNodeInfo ? newNodeInfo.hashCode() : 0);
      hash = hash * 31 + (_hasOldPath ? oldPath.hashCode() : 0);
      hash = hash * 31 + (_hasNewPath ? newPath.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement mutationTypeOut = (mutationType == null) ? JsonNull.INSTANCE : new JsonPrimitive(mutationType.name());
      result.add("mutationType", mutationTypeOut);

      JsonElement newNodeInfoOut = newNodeInfo == null ? JsonNull.INSTANCE : newNodeInfo.toJsonElement();
      result.add("newNodeInfo", newNodeInfoOut);

      JsonElement oldPathOut = (oldPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(oldPath);
      result.add("oldPath", oldPathOut);

      JsonElement newPathOut = (newPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(newPath);
      result.add("newPath", newPathOut);
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

    public static MutationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      MutationImpl dto = new MutationImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("mutationType")) {
        JsonElement mutationTypeIn = json.get("mutationType");
        com.google.collide.dto.Mutation.Type mutationTypeOut = gson.fromJson(mutationTypeIn, com.google.collide.dto.Mutation.Type.class);
        dto.setMutationType(mutationTypeOut);
      }

      if (json.has("newNodeInfo")) {
        JsonElement newNodeInfoIn = json.get("newNodeInfo");
        TreeNodeInfoImpl newNodeInfoOut = TreeNodeInfoImpl.fromJsonElement(newNodeInfoIn);
        dto.setNewNodeInfo(newNodeInfoOut);
      }

      if (json.has("oldPath")) {
        JsonElement oldPathIn = json.get("oldPath");
        java.lang.String oldPathOut = gson.fromJson(oldPathIn, java.lang.String.class);
        dto.setOldPath(oldPathOut);
      }

      if (json.has("newPath")) {
        JsonElement newPathIn = json.get("newPath");
        java.lang.String newPathOut = gson.fromJson(newPathIn, java.lang.String.class);
        dto.setNewPath(newPathOut);
      }

      return dto;
    }
    public static MutationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class NewFileCollaboratorImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.NewFileCollaborator, JsonSerializable {

    private NewFileCollaboratorImpl() {
      super(125);
    }

    protected NewFileCollaboratorImpl(int type) {
      super(type);
    }

    public static NewFileCollaboratorImpl make() {
      return new NewFileCollaboratorImpl();
    }

    protected ParticipantUserDetailsImpl participant;
    private boolean _hasParticipant;
    protected java.lang.String path;
    private boolean _hasPath;

    public boolean hasParticipant() {
      return _hasParticipant;
    }

    @Override
    public com.google.collide.dto.ParticipantUserDetails getParticipant() {
      return participant;
    }

    public NewFileCollaboratorImpl setParticipant(ParticipantUserDetailsImpl v) {
      _hasParticipant = true;
      participant = v;
      return this;
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public NewFileCollaboratorImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof NewFileCollaboratorImpl)) {
        return false;
      }
      NewFileCollaboratorImpl other = (NewFileCollaboratorImpl) o;
      if (this._hasParticipant != other._hasParticipant) {
        return false;
      }
      if (this._hasParticipant) {
        if (!this.participant.equals(other.participant)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasParticipant ? participant.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement participantOut = participant == null ? JsonNull.INSTANCE : participant.toJsonElement();
      result.add("participant", participantOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);
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

    public static NewFileCollaboratorImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      NewFileCollaboratorImpl dto = new NewFileCollaboratorImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("participant")) {
        JsonElement participantIn = json.get("participant");
        ParticipantUserDetailsImpl participantOut = ParticipantUserDetailsImpl.fromJsonElement(participantIn);
        dto.setParticipant(participantOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      return dto;
    }
    public static NewFileCollaboratorImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ConflictHandleImpl implements com.google.collide.dto.NodeConflictDto.ConflictHandle, JsonSerializable {

    public static ConflictHandleImpl make() {
      return new ConflictHandleImpl();
    }

    protected int conflictIndex;
    private boolean _hasConflictIndex;
    protected java.lang.String conflictId;
    private boolean _hasConflictId;

    public boolean hasConflictIndex() {
      return _hasConflictIndex;
    }

    @Override
    public int getConflictIndex() {
      return conflictIndex;
    }

    public ConflictHandleImpl setConflictIndex(int v) {
      _hasConflictIndex = true;
      conflictIndex = v;
      return this;
    }

    public boolean hasConflictId() {
      return _hasConflictId;
    }

    @Override
    public java.lang.String getConflictId() {
      return conflictId;
    }

    public ConflictHandleImpl setConflictId(java.lang.String v) {
      _hasConflictId = true;
      conflictId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ConflictHandleImpl)) {
        return false;
      }
      ConflictHandleImpl other = (ConflictHandleImpl) o;
      if (this._hasConflictIndex != other._hasConflictIndex) {
        return false;
      }
      if (this._hasConflictIndex) {
        if (this.conflictIndex != other.conflictIndex) {
          return false;
        }
      }
      if (this._hasConflictId != other._hasConflictId) {
        return false;
      }
      if (this._hasConflictId) {
        if (!this.conflictId.equals(other.conflictId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasConflictIndex ? java.lang.Integer.valueOf(conflictIndex).hashCode() : 0);
      hash = hash * 31 + (_hasConflictId ? conflictId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive conflictIndexOut = new JsonPrimitive(conflictIndex);
      result.add("conflictIndex", conflictIndexOut);

      JsonElement conflictIdOut = (conflictId == null) ? JsonNull.INSTANCE : new JsonPrimitive(conflictId);
      result.add("conflictId", conflictIdOut);
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

    public static ConflictHandleImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ConflictHandleImpl dto = new ConflictHandleImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("conflictIndex")) {
        JsonElement conflictIndexIn = json.get("conflictIndex");
        int conflictIndexOut = conflictIndexIn.getAsInt();
        dto.setConflictIndex(conflictIndexOut);
      }

      if (json.has("conflictId")) {
        JsonElement conflictIdIn = json.get("conflictId");
        java.lang.String conflictIdOut = gson.fromJson(conflictIdIn, java.lang.String.class);
        dto.setConflictId(conflictIdOut);
      }

      return dto;
    }
    public static ConflictHandleImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ConflictedPathImpl implements com.google.collide.dto.NodeConflictDto.ConflictedPath, JsonSerializable {

    public static ConflictedPathImpl make() {
      return new ConflictedPathImpl();
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected int nodeType;
    private boolean _hasNodeType;
    protected boolean isUtf8;
    private boolean _hasIsUtf8;
    protected java.lang.String startId;
    private boolean _hasStartId;
    protected java.lang.String path;
    private boolean _hasPath;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public ConflictedPathImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasNodeType() {
      return _hasNodeType;
    }

    @Override
    public int getNodeType() {
      return nodeType;
    }

    public ConflictedPathImpl setNodeType(int v) {
      _hasNodeType = true;
      nodeType = v;
      return this;
    }

    public boolean hasIsUtf8() {
      return _hasIsUtf8;
    }

    @Override
    public boolean isUtf8() {
      return isUtf8;
    }

    public ConflictedPathImpl setIsUtf8(boolean v) {
      _hasIsUtf8 = true;
      isUtf8 = v;
      return this;
    }

    public boolean hasStartId() {
      return _hasStartId;
    }

    @Override
    public java.lang.String getStartId() {
      return startId;
    }

    public ConflictedPathImpl setStartId(java.lang.String v) {
      _hasStartId = true;
      startId = v;
      return this;
    }

    public boolean hasPath() {
      return _hasPath;
    }

    @Override
    public java.lang.String getPath() {
      return path;
    }

    public ConflictedPathImpl setPath(java.lang.String v) {
      _hasPath = true;
      path = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ConflictedPathImpl)) {
        return false;
      }
      ConflictedPathImpl other = (ConflictedPathImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasNodeType != other._hasNodeType) {
        return false;
      }
      if (this._hasNodeType) {
        if (this.nodeType != other.nodeType) {
          return false;
        }
      }
      if (this._hasIsUtf8 != other._hasIsUtf8) {
        return false;
      }
      if (this._hasIsUtf8) {
        if (this.isUtf8 != other.isUtf8) {
          return false;
        }
      }
      if (this._hasStartId != other._hasStartId) {
        return false;
      }
      if (this._hasStartId) {
        if (!this.startId.equals(other.startId)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasNodeType ? java.lang.Integer.valueOf(nodeType).hashCode() : 0);
      hash = hash * 31 + (_hasIsUtf8 ? java.lang.Boolean.valueOf(isUtf8).hashCode() : 0);
      hash = hash * 31 + (_hasStartId ? startId.hashCode() : 0);
      hash = hash * 31 + (_hasPath ? path.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonPrimitive nodeTypeOut = new JsonPrimitive(nodeType);
      result.add("nodeType", nodeTypeOut);

      JsonPrimitive isUtf8Out = new JsonPrimitive(isUtf8);
      result.add("isUtf8", isUtf8Out);

      JsonElement startIdOut = (startId == null) ? JsonNull.INSTANCE : new JsonPrimitive(startId);
      result.add("startId", startIdOut);

      JsonElement pathOut = (path == null) ? JsonNull.INSTANCE : new JsonPrimitive(path);
      result.add("path", pathOut);
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

    public static ConflictedPathImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ConflictedPathImpl dto = new ConflictedPathImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("nodeType")) {
        JsonElement nodeTypeIn = json.get("nodeType");
        int nodeTypeOut = nodeTypeIn.getAsInt();
        dto.setNodeType(nodeTypeOut);
      }

      if (json.has("isUtf8")) {
        JsonElement isUtf8In = json.get("isUtf8");
        boolean isUtf8Out = isUtf8In.getAsBoolean();
        dto.setIsUtf8(isUtf8Out);
      }

      if (json.has("startId")) {
        JsonElement startIdIn = json.get("startId");
        java.lang.String startIdOut = gson.fromJson(startIdIn, java.lang.String.class);
        dto.setStartId(startIdOut);
      }

      if (json.has("path")) {
        JsonElement pathIn = json.get("path");
        java.lang.String pathOut = gson.fromJson(pathIn, java.lang.String.class);
        dto.setPath(pathOut);
      }

      return dto;
    }
    public static ConflictedPathImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class NodeConflictDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.NodeConflictDto, JsonSerializable {

    private NodeConflictDtoImpl() {
      super(77);
    }

    protected NodeConflictDtoImpl(int type) {
      super(type);
    }

    public static NodeConflictDtoImpl make() {
      return new NodeConflictDtoImpl();
    }

    protected ConflictHandleImpl conflictHandle;
    private boolean _hasConflictHandle;
    protected ConflictedPathImpl childPath;
    private boolean _hasChildPath;
    protected java.util.List<com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice> validResolutions;
    private boolean _hasValidResolutions;
    protected com.google.collide.dto.NodeConflictDto.SimplifiedConflictType simplifiedConflictType;
    private boolean _hasSimplifiedConflictType;
    protected java.lang.String childDescription;
    private boolean _hasChildDescription;
    protected java.lang.String parentDescription;
    private boolean _hasParentDescription;
    protected java.lang.String conflictDescription;
    private boolean _hasConflictDescription;
    protected java.util.List<NodeConflictDtoImpl> groupedConflicts;
    private boolean _hasGroupedConflicts;
    protected java.util.List<ConflictedPathImpl> parentPaths;
    private boolean _hasParentPaths;

    public boolean hasConflictHandle() {
      return _hasConflictHandle;
    }

    @Override
    public com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() {
      return conflictHandle;
    }

    public NodeConflictDtoImpl setConflictHandle(ConflictHandleImpl v) {
      _hasConflictHandle = true;
      conflictHandle = v;
      return this;
    }

    public boolean hasChildPath() {
      return _hasChildPath;
    }

    @Override
    public com.google.collide.dto.NodeConflictDto.ConflictedPath getChildPath() {
      return childPath;
    }

    public NodeConflictDtoImpl setChildPath(ConflictedPathImpl v) {
      _hasChildPath = true;
      childPath = v;
      return this;
    }

    public boolean hasValidResolutions() {
      return _hasValidResolutions;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice> getValidResolutions() {
      ensureValidResolutions();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(validResolutions);
    }

    public NodeConflictDtoImpl setValidResolutions(java.util.List<com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice> v) {
      _hasValidResolutions = true;
      validResolutions = v;
      return this;
    }

    public void addValidResolutions(com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice v) {
      ensureValidResolutions();
      validResolutions.add(v);
    }

    public void clearValidResolutions() {
      ensureValidResolutions();
      validResolutions.clear();
    }

    private void ensureValidResolutions() {
      if (!_hasValidResolutions) {
        setValidResolutions(validResolutions != null ? validResolutions : new java.util.ArrayList<com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice>());
      }
    }

    public boolean hasSimplifiedConflictType() {
      return _hasSimplifiedConflictType;
    }

    @Override
    public com.google.collide.dto.NodeConflictDto.SimplifiedConflictType getSimplifiedConflictType() {
      return simplifiedConflictType;
    }

    public NodeConflictDtoImpl setSimplifiedConflictType(com.google.collide.dto.NodeConflictDto.SimplifiedConflictType v) {
      _hasSimplifiedConflictType = true;
      simplifiedConflictType = v;
      return this;
    }

    public boolean hasChildDescription() {
      return _hasChildDescription;
    }

    @Override
    public java.lang.String getChildDescription() {
      return childDescription;
    }

    public NodeConflictDtoImpl setChildDescription(java.lang.String v) {
      _hasChildDescription = true;
      childDescription = v;
      return this;
    }

    public boolean hasParentDescription() {
      return _hasParentDescription;
    }

    @Override
    public java.lang.String getParentDescription() {
      return parentDescription;
    }

    public NodeConflictDtoImpl setParentDescription(java.lang.String v) {
      _hasParentDescription = true;
      parentDescription = v;
      return this;
    }

    public boolean hasConflictDescription() {
      return _hasConflictDescription;
    }

    @Override
    public java.lang.String getConflictDescription() {
      return conflictDescription;
    }

    public NodeConflictDtoImpl setConflictDescription(java.lang.String v) {
      _hasConflictDescription = true;
      conflictDescription = v;
      return this;
    }

    public boolean hasGroupedConflicts() {
      return _hasGroupedConflicts;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto> getGroupedConflicts() {
      ensureGroupedConflicts();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(groupedConflicts);
    }

    public NodeConflictDtoImpl setGroupedConflicts(java.util.List<NodeConflictDtoImpl> v) {
      _hasGroupedConflicts = true;
      groupedConflicts = v;
      return this;
    }

    public void addGroupedConflicts(NodeConflictDtoImpl v) {
      ensureGroupedConflicts();
      groupedConflicts.add(v);
    }

    public void clearGroupedConflicts() {
      ensureGroupedConflicts();
      groupedConflicts.clear();
    }

    private void ensureGroupedConflicts() {
      if (!_hasGroupedConflicts) {
        setGroupedConflicts(groupedConflicts != null ? groupedConflicts : new java.util.ArrayList<NodeConflictDtoImpl>());
      }
    }

    public boolean hasParentPaths() {
      return _hasParentPaths;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto.ConflictedPath> getParentPaths() {
      ensureParentPaths();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(parentPaths);
    }

    public NodeConflictDtoImpl setParentPaths(java.util.List<ConflictedPathImpl> v) {
      _hasParentPaths = true;
      parentPaths = v;
      return this;
    }

    public void addParentPaths(ConflictedPathImpl v) {
      ensureParentPaths();
      parentPaths.add(v);
    }

    public void clearParentPaths() {
      ensureParentPaths();
      parentPaths.clear();
    }

    private void ensureParentPaths() {
      if (!_hasParentPaths) {
        setParentPaths(parentPaths != null ? parentPaths : new java.util.ArrayList<ConflictedPathImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof NodeConflictDtoImpl)) {
        return false;
      }
      NodeConflictDtoImpl other = (NodeConflictDtoImpl) o;
      if (this._hasConflictHandle != other._hasConflictHandle) {
        return false;
      }
      if (this._hasConflictHandle) {
        if (!this.conflictHandle.equals(other.conflictHandle)) {
          return false;
        }
      }
      if (this._hasChildPath != other._hasChildPath) {
        return false;
      }
      if (this._hasChildPath) {
        if (!this.childPath.equals(other.childPath)) {
          return false;
        }
      }
      if (this._hasValidResolutions != other._hasValidResolutions) {
        return false;
      }
      if (this._hasValidResolutions) {
        if (!this.validResolutions.equals(other.validResolutions)) {
          return false;
        }
      }
      if (this._hasSimplifiedConflictType != other._hasSimplifiedConflictType) {
        return false;
      }
      if (this._hasSimplifiedConflictType) {
        if (!this.simplifiedConflictType.equals(other.simplifiedConflictType)) {
          return false;
        }
      }
      if (this._hasChildDescription != other._hasChildDescription) {
        return false;
      }
      if (this._hasChildDescription) {
        if (!this.childDescription.equals(other.childDescription)) {
          return false;
        }
      }
      if (this._hasParentDescription != other._hasParentDescription) {
        return false;
      }
      if (this._hasParentDescription) {
        if (!this.parentDescription.equals(other.parentDescription)) {
          return false;
        }
      }
      if (this._hasConflictDescription != other._hasConflictDescription) {
        return false;
      }
      if (this._hasConflictDescription) {
        if (!this.conflictDescription.equals(other.conflictDescription)) {
          return false;
        }
      }
      if (this._hasGroupedConflicts != other._hasGroupedConflicts) {
        return false;
      }
      if (this._hasGroupedConflicts) {
        if (!this.groupedConflicts.equals(other.groupedConflicts)) {
          return false;
        }
      }
      if (this._hasParentPaths != other._hasParentPaths) {
        return false;
      }
      if (this._hasParentPaths) {
        if (!this.parentPaths.equals(other.parentPaths)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasConflictHandle ? conflictHandle.hashCode() : 0);
      hash = hash * 31 + (_hasChildPath ? childPath.hashCode() : 0);
      hash = hash * 31 + (_hasValidResolutions ? validResolutions.hashCode() : 0);
      hash = hash * 31 + (_hasSimplifiedConflictType ? simplifiedConflictType.hashCode() : 0);
      hash = hash * 31 + (_hasChildDescription ? childDescription.hashCode() : 0);
      hash = hash * 31 + (_hasParentDescription ? parentDescription.hashCode() : 0);
      hash = hash * 31 + (_hasConflictDescription ? conflictDescription.hashCode() : 0);
      hash = hash * 31 + (_hasGroupedConflicts ? groupedConflicts.hashCode() : 0);
      hash = hash * 31 + (_hasParentPaths ? parentPaths.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement conflictHandleOut = conflictHandle == null ? JsonNull.INSTANCE : conflictHandle.toJsonElement();
      result.add("conflictHandle", conflictHandleOut);

      JsonElement childPathOut = childPath == null ? JsonNull.INSTANCE : childPath.toJsonElement();
      result.add("childPath", childPathOut);

      JsonArray validResolutionsOut = new JsonArray();
      ensureValidResolutions();
      for (com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice validResolutions_ : validResolutions) {
        JsonElement validResolutionsOut_ = (validResolutions_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(validResolutions_.name());
        validResolutionsOut.add(validResolutionsOut_);
      }
      result.add("validResolutions", validResolutionsOut);

      JsonElement simplifiedConflictTypeOut = (simplifiedConflictType == null) ? JsonNull.INSTANCE : new JsonPrimitive(simplifiedConflictType.name());
      result.add("simplifiedConflictType", simplifiedConflictTypeOut);

      JsonElement childDescriptionOut = (childDescription == null) ? JsonNull.INSTANCE : new JsonPrimitive(childDescription);
      result.add("childDescription", childDescriptionOut);

      JsonElement parentDescriptionOut = (parentDescription == null) ? JsonNull.INSTANCE : new JsonPrimitive(parentDescription);
      result.add("parentDescription", parentDescriptionOut);

      JsonElement conflictDescriptionOut = (conflictDescription == null) ? JsonNull.INSTANCE : new JsonPrimitive(conflictDescription);
      result.add("conflictDescription", conflictDescriptionOut);

      JsonArray groupedConflictsOut = new JsonArray();
      ensureGroupedConflicts();
      for (NodeConflictDtoImpl groupedConflicts_ : groupedConflicts) {
        JsonElement groupedConflictsOut_ = groupedConflicts_ == null ? JsonNull.INSTANCE : groupedConflicts_.toJsonElement();
        groupedConflictsOut.add(groupedConflictsOut_);
      }
      result.add("groupedConflicts", groupedConflictsOut);

      JsonArray parentPathsOut = new JsonArray();
      ensureParentPaths();
      for (ConflictedPathImpl parentPaths_ : parentPaths) {
        JsonElement parentPathsOut_ = parentPaths_ == null ? JsonNull.INSTANCE : parentPaths_.toJsonElement();
        parentPathsOut.add(parentPathsOut_);
      }
      result.add("parentPaths", parentPathsOut);
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

    public static NodeConflictDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      NodeConflictDtoImpl dto = new NodeConflictDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("conflictHandle")) {
        JsonElement conflictHandleIn = json.get("conflictHandle");
        ConflictHandleImpl conflictHandleOut = ConflictHandleImpl.fromJsonElement(conflictHandleIn);
        dto.setConflictHandle(conflictHandleOut);
      }

      if (json.has("childPath")) {
        JsonElement childPathIn = json.get("childPath");
        ConflictedPathImpl childPathOut = ConflictedPathImpl.fromJsonElement(childPathIn);
        dto.setChildPath(childPathOut);
      }

      if (json.has("validResolutions")) {
        JsonElement validResolutionsIn = json.get("validResolutions");
        java.util.ArrayList<com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice> validResolutionsOut = null;
        if (validResolutionsIn != null && !validResolutionsIn.isJsonNull()) {
          validResolutionsOut = new java.util.ArrayList<com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice>();
          java.util.Iterator<JsonElement> validResolutionsInIterator = validResolutionsIn.getAsJsonArray().iterator();
          while (validResolutionsInIterator.hasNext()) {
            JsonElement validResolutionsIn_ = validResolutionsInIterator.next();
            com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice validResolutionsOut_ = gson.fromJson(validResolutionsIn_, com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice.class);
            validResolutionsOut.add(validResolutionsOut_);
          }
        }
        dto.setValidResolutions(validResolutionsOut);
      }

      if (json.has("simplifiedConflictType")) {
        JsonElement simplifiedConflictTypeIn = json.get("simplifiedConflictType");
        com.google.collide.dto.NodeConflictDto.SimplifiedConflictType simplifiedConflictTypeOut = gson.fromJson(simplifiedConflictTypeIn, com.google.collide.dto.NodeConflictDto.SimplifiedConflictType.class);
        dto.setSimplifiedConflictType(simplifiedConflictTypeOut);
      }

      if (json.has("childDescription")) {
        JsonElement childDescriptionIn = json.get("childDescription");
        java.lang.String childDescriptionOut = gson.fromJson(childDescriptionIn, java.lang.String.class);
        dto.setChildDescription(childDescriptionOut);
      }

      if (json.has("parentDescription")) {
        JsonElement parentDescriptionIn = json.get("parentDescription");
        java.lang.String parentDescriptionOut = gson.fromJson(parentDescriptionIn, java.lang.String.class);
        dto.setParentDescription(parentDescriptionOut);
      }

      if (json.has("conflictDescription")) {
        JsonElement conflictDescriptionIn = json.get("conflictDescription");
        java.lang.String conflictDescriptionOut = gson.fromJson(conflictDescriptionIn, java.lang.String.class);
        dto.setConflictDescription(conflictDescriptionOut);
      }

      if (json.has("groupedConflicts")) {
        JsonElement groupedConflictsIn = json.get("groupedConflicts");
        java.util.ArrayList<NodeConflictDtoImpl> groupedConflictsOut = null;
        if (groupedConflictsIn != null && !groupedConflictsIn.isJsonNull()) {
          groupedConflictsOut = new java.util.ArrayList<NodeConflictDtoImpl>();
          java.util.Iterator<JsonElement> groupedConflictsInIterator = groupedConflictsIn.getAsJsonArray().iterator();
          while (groupedConflictsInIterator.hasNext()) {
            JsonElement groupedConflictsIn_ = groupedConflictsInIterator.next();
            NodeConflictDtoImpl groupedConflictsOut_ = NodeConflictDtoImpl.fromJsonElement(groupedConflictsIn_);
            groupedConflictsOut.add(groupedConflictsOut_);
          }
        }
        dto.setGroupedConflicts(groupedConflictsOut);
      }

      if (json.has("parentPaths")) {
        JsonElement parentPathsIn = json.get("parentPaths");
        java.util.ArrayList<ConflictedPathImpl> parentPathsOut = null;
        if (parentPathsIn != null && !parentPathsIn.isJsonNull()) {
          parentPathsOut = new java.util.ArrayList<ConflictedPathImpl>();
          java.util.Iterator<JsonElement> parentPathsInIterator = parentPathsIn.getAsJsonArray().iterator();
          while (parentPathsInIterator.hasNext()) {
            JsonElement parentPathsIn_ = parentPathsInIterator.next();
            ConflictedPathImpl parentPathsOut_ = ConflictedPathImpl.fromJsonElement(parentPathsIn_);
            parentPathsOut.add(parentPathsOut_);
          }
        }
        dto.setParentPaths(parentPathsOut);
      }

      return dto;
    }
    public static NodeConflictDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class NodeHistoryInfoImpl extends TreeNodeInfoImpl implements com.google.collide.dto.NodeHistoryInfo, JsonSerializable {

    public static NodeHistoryInfoImpl make() {
      return new NodeHistoryInfoImpl();
    }

    protected java.lang.String predecessorId;
    private boolean _hasPredecessorId;
    protected java.lang.String creationTime;
    private boolean _hasCreationTime;

    public boolean hasPredecessorId() {
      return _hasPredecessorId;
    }

    @Override
    public java.lang.String getPredecessorId() {
      return predecessorId;
    }

    public NodeHistoryInfoImpl setPredecessorId(java.lang.String v) {
      _hasPredecessorId = true;
      predecessorId = v;
      return this;
    }

    public boolean hasCreationTime() {
      return _hasCreationTime;
    }

    @Override
    public java.lang.String getCreationTime() {
      return creationTime;
    }

    public NodeHistoryInfoImpl setCreationTime(java.lang.String v) {
      _hasCreationTime = true;
      creationTime = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof NodeHistoryInfoImpl)) {
        return false;
      }
      NodeHistoryInfoImpl other = (NodeHistoryInfoImpl) o;
      if (this._hasPredecessorId != other._hasPredecessorId) {
        return false;
      }
      if (this._hasPredecessorId) {
        if (!this.predecessorId.equals(other.predecessorId)) {
          return false;
        }
      }
      if (this._hasCreationTime != other._hasCreationTime) {
        return false;
      }
      if (this._hasCreationTime) {
        if (!this.creationTime.equals(other.creationTime)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasPredecessorId ? predecessorId.hashCode() : 0);
      hash = hash * 31 + (_hasCreationTime ? creationTime.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement predecessorIdOut = (predecessorId == null) ? JsonNull.INSTANCE : new JsonPrimitive(predecessorId);
      result.add("predecessorId", predecessorIdOut);

      JsonElement creationTimeOut = (creationTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(creationTime);
      result.add("creationTime", creationTimeOut);

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonPrimitive nodeTypeOut = new JsonPrimitive(nodeType);
      result.add("nodeType", nodeTypeOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);
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

    public static NodeHistoryInfoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      NodeHistoryInfoImpl dto = new NodeHistoryInfoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("predecessorId")) {
        JsonElement predecessorIdIn = json.get("predecessorId");
        java.lang.String predecessorIdOut = gson.fromJson(predecessorIdIn, java.lang.String.class);
        dto.setPredecessorId(predecessorIdOut);
      }

      if (json.has("creationTime")) {
        JsonElement creationTimeIn = json.get("creationTime");
        java.lang.String creationTimeOut = gson.fromJson(creationTimeIn, java.lang.String.class);
        dto.setCreationTime(creationTimeOut);
      }

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("nodeType")) {
        JsonElement nodeTypeIn = json.get("nodeType");
        int nodeTypeOut = nodeTypeIn.getAsInt();
        dto.setNodeType(nodeTypeOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      return dto;
    }
    public static NodeHistoryInfoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class NodeMutationDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.NodeMutationDto, JsonSerializable {

    private NodeMutationDtoImpl() {
      super(78);
    }

    protected NodeMutationDtoImpl(int type) {
      super(type);
    }

    public static NodeMutationDtoImpl make() {
      return new NodeMutationDtoImpl();
    }

    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected DiffStatsDtoImpl diffStats;
    private boolean _hasDiffStats;
    protected com.google.collide.dto.NodeMutationDto.MutationType mutationType;
    private boolean _hasMutationType;
    protected java.lang.String oldPath;
    private boolean _hasOldPath;
    protected java.lang.String newPath;
    private boolean _hasNewPath;
    protected boolean isFile;
    private boolean _hasIsFile;

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public NodeMutationDtoImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public NodeMutationDtoImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasDiffStats() {
      return _hasDiffStats;
    }

    @Override
    public com.google.collide.dto.DiffStatsDto getDiffStats() {
      return diffStats;
    }

    public NodeMutationDtoImpl setDiffStats(DiffStatsDtoImpl v) {
      _hasDiffStats = true;
      diffStats = v;
      return this;
    }

    public boolean hasMutationType() {
      return _hasMutationType;
    }

    @Override
    public com.google.collide.dto.NodeMutationDto.MutationType getMutationType() {
      return mutationType;
    }

    public NodeMutationDtoImpl setMutationType(com.google.collide.dto.NodeMutationDto.MutationType v) {
      _hasMutationType = true;
      mutationType = v;
      return this;
    }

    public boolean hasOldPath() {
      return _hasOldPath;
    }

    @Override
    public java.lang.String getOldPath() {
      return oldPath;
    }

    public NodeMutationDtoImpl setOldPath(java.lang.String v) {
      _hasOldPath = true;
      oldPath = v;
      return this;
    }

    public boolean hasNewPath() {
      return _hasNewPath;
    }

    @Override
    public java.lang.String getNewPath() {
      return newPath;
    }

    public NodeMutationDtoImpl setNewPath(java.lang.String v) {
      _hasNewPath = true;
      newPath = v;
      return this;
    }

    public boolean hasIsFile() {
      return _hasIsFile;
    }

    @Override
    public boolean isFile() {
      return isFile;
    }

    public NodeMutationDtoImpl setIsFile(boolean v) {
      _hasIsFile = true;
      isFile = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof NodeMutationDtoImpl)) {
        return false;
      }
      NodeMutationDtoImpl other = (NodeMutationDtoImpl) o;
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasDiffStats != other._hasDiffStats) {
        return false;
      }
      if (this._hasDiffStats) {
        if (!this.diffStats.equals(other.diffStats)) {
          return false;
        }
      }
      if (this._hasMutationType != other._hasMutationType) {
        return false;
      }
      if (this._hasMutationType) {
        if (!this.mutationType.equals(other.mutationType)) {
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
      if (this._hasNewPath != other._hasNewPath) {
        return false;
      }
      if (this._hasNewPath) {
        if (!this.newPath.equals(other.newPath)) {
          return false;
        }
      }
      if (this._hasIsFile != other._hasIsFile) {
        return false;
      }
      if (this._hasIsFile) {
        if (this.isFile != other.isFile) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasDiffStats ? diffStats.hashCode() : 0);
      hash = hash * 31 + (_hasMutationType ? mutationType.hashCode() : 0);
      hash = hash * 31 + (_hasOldPath ? oldPath.hashCode() : 0);
      hash = hash * 31 + (_hasNewPath ? newPath.hashCode() : 0);
      hash = hash * 31 + (_hasIsFile ? java.lang.Boolean.valueOf(isFile).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement diffStatsOut = diffStats == null ? JsonNull.INSTANCE : diffStats.toJsonElement();
      result.add("diffStats", diffStatsOut);

      JsonElement mutationTypeOut = (mutationType == null) ? JsonNull.INSTANCE : new JsonPrimitive(mutationType.name());
      result.add("mutationType", mutationTypeOut);

      JsonElement oldPathOut = (oldPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(oldPath);
      result.add("oldPath", oldPathOut);

      JsonElement newPathOut = (newPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(newPath);
      result.add("newPath", newPathOut);

      JsonPrimitive isFileOut = new JsonPrimitive(isFile);
      result.add("isFile", isFileOut);
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

    public static NodeMutationDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      NodeMutationDtoImpl dto = new NodeMutationDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("diffStats")) {
        JsonElement diffStatsIn = json.get("diffStats");
        DiffStatsDtoImpl diffStatsOut = DiffStatsDtoImpl.fromJsonElement(diffStatsIn);
        dto.setDiffStats(diffStatsOut);
      }

      if (json.has("mutationType")) {
        JsonElement mutationTypeIn = json.get("mutationType");
        com.google.collide.dto.NodeMutationDto.MutationType mutationTypeOut = gson.fromJson(mutationTypeIn, com.google.collide.dto.NodeMutationDto.MutationType.class);
        dto.setMutationType(mutationTypeOut);
      }

      if (json.has("oldPath")) {
        JsonElement oldPathIn = json.get("oldPath");
        java.lang.String oldPathOut = gson.fromJson(oldPathIn, java.lang.String.class);
        dto.setOldPath(oldPathOut);
      }

      if (json.has("newPath")) {
        JsonElement newPathIn = json.get("newPath");
        java.lang.String newPathOut = gson.fromJson(newPathIn, java.lang.String.class);
        dto.setNewPath(newPathOut);
      }

      if (json.has("isFile")) {
        JsonElement isFileIn = json.get("isFile");
        boolean isFileOut = isFileIn.getAsBoolean();
        dto.setIsFile(isFileOut);
      }

      return dto;
    }
    public static NodeMutationDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ParticipantImpl implements com.google.collide.dto.Participant, JsonSerializable {

    public static ParticipantImpl make() {
      return new ParticipantImpl();
    }

    protected java.lang.String id;
    private boolean _hasId;
    protected java.lang.String userId;
    private boolean _hasUserId;

    public boolean hasId() {
      return _hasId;
    }

    @Override
    public java.lang.String getId() {
      return id;
    }

    public ParticipantImpl setId(java.lang.String v) {
      _hasId = true;
      id = v;
      return this;
    }

    public boolean hasUserId() {
      return _hasUserId;
    }

    @Override
    public java.lang.String getUserId() {
      return userId;
    }

    public ParticipantImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ParticipantImpl)) {
        return false;
      }
      ParticipantImpl other = (ParticipantImpl) o;
      if (this._hasId != other._hasId) {
        return false;
      }
      if (this._hasId) {
        if (!this.id.equals(other.id)) {
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
      hash = hash * 31 + (_hasId ? id.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement idOut = (id == null) ? JsonNull.INSTANCE : new JsonPrimitive(id);
      result.add("id", idOut);

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

    public static ParticipantImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ParticipantImpl dto = new ParticipantImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("id")) {
        JsonElement idIn = json.get("id");
        java.lang.String idOut = gson.fromJson(idIn, java.lang.String.class);
        dto.setId(idOut);
      }

      if (json.has("userId")) {
        JsonElement userIdIn = json.get("userId");
        java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
        dto.setUserId(userIdOut);
      }

      return dto;
    }
    public static ParticipantImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ParticipantUserDetailsImpl implements com.google.collide.dto.ParticipantUserDetails, JsonSerializable {

    public static ParticipantUserDetailsImpl make() {
      return new ParticipantUserDetailsImpl();
    }

    protected ParticipantImpl participant;
    private boolean _hasParticipant;
    protected UserDetailsImpl userDetails;
    private boolean _hasUserDetails;

    public boolean hasParticipant() {
      return _hasParticipant;
    }

    @Override
    public com.google.collide.dto.Participant getParticipant() {
      return participant;
    }

    public ParticipantUserDetailsImpl setParticipant(ParticipantImpl v) {
      _hasParticipant = true;
      participant = v;
      return this;
    }

    public boolean hasUserDetails() {
      return _hasUserDetails;
    }

    @Override
    public com.google.collide.dto.UserDetails getUserDetails() {
      return userDetails;
    }

    public ParticipantUserDetailsImpl setUserDetails(UserDetailsImpl v) {
      _hasUserDetails = true;
      userDetails = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ParticipantUserDetailsImpl)) {
        return false;
      }
      ParticipantUserDetailsImpl other = (ParticipantUserDetailsImpl) o;
      if (this._hasParticipant != other._hasParticipant) {
        return false;
      }
      if (this._hasParticipant) {
        if (!this.participant.equals(other.participant)) {
          return false;
        }
      }
      if (this._hasUserDetails != other._hasUserDetails) {
        return false;
      }
      if (this._hasUserDetails) {
        if (!this.userDetails.equals(other.userDetails)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasParticipant ? participant.hashCode() : 0);
      hash = hash * 31 + (_hasUserDetails ? userDetails.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement participantOut = participant == null ? JsonNull.INSTANCE : participant.toJsonElement();
      result.add("participant", participantOut);

      JsonElement userDetailsOut = userDetails == null ? JsonNull.INSTANCE : userDetails.toJsonElement();
      result.add("userDetails", userDetailsOut);
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

    public static ParticipantUserDetailsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ParticipantUserDetailsImpl dto = new ParticipantUserDetailsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("participant")) {
        JsonElement participantIn = json.get("participant");
        ParticipantImpl participantOut = ParticipantImpl.fromJsonElement(participantIn);
        dto.setParticipant(participantOut);
      }

      if (json.has("userDetails")) {
        JsonElement userDetailsIn = json.get("userDetails");
        UserDetailsImpl userDetailsOut = UserDetailsImpl.fromJsonElement(userDetailsIn);
        dto.setUserDetails(userDetailsOut);
      }

      return dto;
    }
    public static ParticipantUserDetailsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ProjectInfoImpl implements com.google.collide.dto.ProjectInfo, JsonSerializable {

    public static ProjectInfoImpl make() {
      return new ProjectInfoImpl();
    }

    protected java.lang.String summary;
    private boolean _hasSummary;
    protected java.lang.String logoUrl;
    private boolean _hasLogoUrl;
    protected com.google.collide.dto.Role currentUserRole;
    private boolean _hasCurrentUserRole;
    protected java.lang.String rootWsId;
    private boolean _hasRootWsId;
    protected java.lang.String name;
    private boolean _hasName;
    protected java.lang.String id;
    private boolean _hasId;

    public boolean hasSummary() {
      return _hasSummary;
    }

    @Override
    public java.lang.String getSummary() {
      return summary;
    }

    public ProjectInfoImpl setSummary(java.lang.String v) {
      _hasSummary = true;
      summary = v;
      return this;
    }

    public boolean hasLogoUrl() {
      return _hasLogoUrl;
    }

    @Override
    public java.lang.String getLogoUrl() {
      return logoUrl;
    }

    public ProjectInfoImpl setLogoUrl(java.lang.String v) {
      _hasLogoUrl = true;
      logoUrl = v;
      return this;
    }

    public boolean hasCurrentUserRole() {
      return _hasCurrentUserRole;
    }

    @Override
    public com.google.collide.dto.Role getCurrentUserRole() {
      return currentUserRole;
    }

    public ProjectInfoImpl setCurrentUserRole(com.google.collide.dto.Role v) {
      _hasCurrentUserRole = true;
      currentUserRole = v;
      return this;
    }

    public boolean hasRootWsId() {
      return _hasRootWsId;
    }

    @Override
    public java.lang.String getRootWsId() {
      return rootWsId;
    }

    public ProjectInfoImpl setRootWsId(java.lang.String v) {
      _hasRootWsId = true;
      rootWsId = v;
      return this;
    }

    public boolean hasName() {
      return _hasName;
    }

    @Override
    public java.lang.String getName() {
      return name;
    }

    public ProjectInfoImpl setName(java.lang.String v) {
      _hasName = true;
      name = v;
      return this;
    }

    public boolean hasId() {
      return _hasId;
    }

    @Override
    public java.lang.String getId() {
      return id;
    }

    public ProjectInfoImpl setId(java.lang.String v) {
      _hasId = true;
      id = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ProjectInfoImpl)) {
        return false;
      }
      ProjectInfoImpl other = (ProjectInfoImpl) o;
      if (this._hasSummary != other._hasSummary) {
        return false;
      }
      if (this._hasSummary) {
        if (!this.summary.equals(other.summary)) {
          return false;
        }
      }
      if (this._hasLogoUrl != other._hasLogoUrl) {
        return false;
      }
      if (this._hasLogoUrl) {
        if (!this.logoUrl.equals(other.logoUrl)) {
          return false;
        }
      }
      if (this._hasCurrentUserRole != other._hasCurrentUserRole) {
        return false;
      }
      if (this._hasCurrentUserRole) {
        if (!this.currentUserRole.equals(other.currentUserRole)) {
          return false;
        }
      }
      if (this._hasRootWsId != other._hasRootWsId) {
        return false;
      }
      if (this._hasRootWsId) {
        if (!this.rootWsId.equals(other.rootWsId)) {
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
      if (this._hasId != other._hasId) {
        return false;
      }
      if (this._hasId) {
        if (!this.id.equals(other.id)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasSummary ? summary.hashCode() : 0);
      hash = hash * 31 + (_hasLogoUrl ? logoUrl.hashCode() : 0);
      hash = hash * 31 + (_hasCurrentUserRole ? currentUserRole.hashCode() : 0);
      hash = hash * 31 + (_hasRootWsId ? rootWsId.hashCode() : 0);
      hash = hash * 31 + (_hasName ? name.hashCode() : 0);
      hash = hash * 31 + (_hasId ? id.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement summaryOut = (summary == null) ? JsonNull.INSTANCE : new JsonPrimitive(summary);
      result.add("summary", summaryOut);

      JsonElement logoUrlOut = (logoUrl == null) ? JsonNull.INSTANCE : new JsonPrimitive(logoUrl);
      result.add("logoUrl", logoUrlOut);

      JsonElement currentUserRoleOut = (currentUserRole == null) ? JsonNull.INSTANCE : new JsonPrimitive(currentUserRole.name());
      result.add("currentUserRole", currentUserRoleOut);

      JsonElement rootWsIdOut = (rootWsId == null) ? JsonNull.INSTANCE : new JsonPrimitive(rootWsId);
      result.add("rootWsId", rootWsIdOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);

      JsonElement idOut = (id == null) ? JsonNull.INSTANCE : new JsonPrimitive(id);
      result.add("id", idOut);
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

    public static ProjectInfoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ProjectInfoImpl dto = new ProjectInfoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("summary")) {
        JsonElement summaryIn = json.get("summary");
        java.lang.String summaryOut = gson.fromJson(summaryIn, java.lang.String.class);
        dto.setSummary(summaryOut);
      }

      if (json.has("logoUrl")) {
        JsonElement logoUrlIn = json.get("logoUrl");
        java.lang.String logoUrlOut = gson.fromJson(logoUrlIn, java.lang.String.class);
        dto.setLogoUrl(logoUrlOut);
      }

      if (json.has("currentUserRole")) {
        JsonElement currentUserRoleIn = json.get("currentUserRole");
        com.google.collide.dto.Role currentUserRoleOut = gson.fromJson(currentUserRoleIn, com.google.collide.dto.Role.class);
        dto.setCurrentUserRole(currentUserRoleOut);
      }

      if (json.has("rootWsId")) {
        JsonElement rootWsIdIn = json.get("rootWsId");
        java.lang.String rootWsIdOut = gson.fromJson(rootWsIdIn, java.lang.String.class);
        dto.setRootWsId(rootWsIdOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      if (json.has("id")) {
        JsonElement idIn = json.get("id");
        java.lang.String idOut = gson.fromJson(idIn, java.lang.String.class);
        dto.setId(idOut);
      }

      return dto;
    }
    public static ProjectInfoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ProjectMembersInfoImpl implements com.google.collide.dto.ProjectMembersInfo, JsonSerializable {

    public static ProjectMembersInfoImpl make() {
      return new ProjectMembersInfoImpl();
    }

    protected int pendingMembersCount;
    private boolean _hasPendingMembersCount;
    protected java.util.List<UserDetailsImpl> members;
    private boolean _hasMembers;

    public boolean hasPendingMembersCount() {
      return _hasPendingMembersCount;
    }

    @Override
    public int pendingMembersCount() {
      return pendingMembersCount;
    }

    public ProjectMembersInfoImpl setPendingMembersCount(int v) {
      _hasPendingMembersCount = true;
      pendingMembersCount = v;
      return this;
    }

    public boolean hasMembers() {
      return _hasMembers;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.UserDetails> getMembers() {
      ensureMembers();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(members);
    }

    public ProjectMembersInfoImpl setMembers(java.util.List<UserDetailsImpl> v) {
      _hasMembers = true;
      members = v;
      return this;
    }

    public void addMembers(UserDetailsImpl v) {
      ensureMembers();
      members.add(v);
    }

    public void clearMembers() {
      ensureMembers();
      members.clear();
    }

    private void ensureMembers() {
      if (!_hasMembers) {
        setMembers(members != null ? members : new java.util.ArrayList<UserDetailsImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ProjectMembersInfoImpl)) {
        return false;
      }
      ProjectMembersInfoImpl other = (ProjectMembersInfoImpl) o;
      if (this._hasPendingMembersCount != other._hasPendingMembersCount) {
        return false;
      }
      if (this._hasPendingMembersCount) {
        if (this.pendingMembersCount != other.pendingMembersCount) {
          return false;
        }
      }
      if (this._hasMembers != other._hasMembers) {
        return false;
      }
      if (this._hasMembers) {
        if (!this.members.equals(other.members)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasPendingMembersCount ? java.lang.Integer.valueOf(pendingMembersCount).hashCode() : 0);
      hash = hash * 31 + (_hasMembers ? members.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive pendingMembersCountOut = new JsonPrimitive(pendingMembersCount);
      result.add("pendingMembersCount", pendingMembersCountOut);

      JsonArray membersOut = new JsonArray();
      ensureMembers();
      for (UserDetailsImpl members_ : members) {
        JsonElement membersOut_ = members_ == null ? JsonNull.INSTANCE : members_.toJsonElement();
        membersOut.add(membersOut_);
      }
      result.add("members", membersOut);
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

    public static ProjectMembersInfoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ProjectMembersInfoImpl dto = new ProjectMembersInfoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("pendingMembersCount")) {
        JsonElement pendingMembersCountIn = json.get("pendingMembersCount");
        int pendingMembersCountOut = pendingMembersCountIn.getAsInt();
        dto.setPendingMembersCount(pendingMembersCountOut);
      }

      if (json.has("members")) {
        JsonElement membersIn = json.get("members");
        java.util.ArrayList<UserDetailsImpl> membersOut = null;
        if (membersIn != null && !membersIn.isJsonNull()) {
          membersOut = new java.util.ArrayList<UserDetailsImpl>();
          java.util.Iterator<JsonElement> membersInIterator = membersIn.getAsJsonArray().iterator();
          while (membersInIterator.hasNext()) {
            JsonElement membersIn_ = membersInIterator.next();
            UserDetailsImpl membersOut_ = UserDetailsImpl.fromJsonElement(membersIn_);
            membersOut.add(membersOut_);
          }
        }
        dto.setMembers(membersOut);
      }

      return dto;
    }
    public static ProjectMembersInfoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RecoverFromDroppedTangoInvalidationImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.RecoverFromDroppedTangoInvalidation, JsonSerializable {

    private RecoverFromDroppedTangoInvalidationImpl() {
      super(80);
    }

    protected RecoverFromDroppedTangoInvalidationImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String tangoObjectIdName;
    private boolean _hasTangoObjectIdName;
    protected int currentClientVersion;
    private boolean _hasCurrentClientVersion;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public RecoverFromDroppedTangoInvalidationImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasTangoObjectIdName() {
      return _hasTangoObjectIdName;
    }

    @Override
    public java.lang.String getTangoObjectIdName() {
      return tangoObjectIdName;
    }

    public RecoverFromDroppedTangoInvalidationImpl setTangoObjectIdName(java.lang.String v) {
      _hasTangoObjectIdName = true;
      tangoObjectIdName = v;
      return this;
    }

    public boolean hasCurrentClientVersion() {
      return _hasCurrentClientVersion;
    }

    @Override
    public int getCurrentClientVersion() {
      return currentClientVersion;
    }

    public RecoverFromDroppedTangoInvalidationImpl setCurrentClientVersion(int v) {
      _hasCurrentClientVersion = true;
      currentClientVersion = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RecoverFromDroppedTangoInvalidationImpl)) {
        return false;
      }
      RecoverFromDroppedTangoInvalidationImpl other = (RecoverFromDroppedTangoInvalidationImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasTangoObjectIdName != other._hasTangoObjectIdName) {
        return false;
      }
      if (this._hasTangoObjectIdName) {
        if (!this.tangoObjectIdName.equals(other.tangoObjectIdName)) {
          return false;
        }
      }
      if (this._hasCurrentClientVersion != other._hasCurrentClientVersion) {
        return false;
      }
      if (this._hasCurrentClientVersion) {
        if (this.currentClientVersion != other.currentClientVersion) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasTangoObjectIdName ? tangoObjectIdName.hashCode() : 0);
      hash = hash * 31 + (_hasCurrentClientVersion ? java.lang.Integer.valueOf(currentClientVersion).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement tangoObjectIdNameOut = (tangoObjectIdName == null) ? JsonNull.INSTANCE : new JsonPrimitive(tangoObjectIdName);
      result.add("tangoObjectIdName", tangoObjectIdNameOut);

      JsonPrimitive currentClientVersionOut = new JsonPrimitive(currentClientVersion);
      result.add("currentClientVersion", currentClientVersionOut);
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

    public static RecoverFromDroppedTangoInvalidationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RecoverFromDroppedTangoInvalidationImpl dto = new RecoverFromDroppedTangoInvalidationImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("tangoObjectIdName")) {
        JsonElement tangoObjectIdNameIn = json.get("tangoObjectIdName");
        java.lang.String tangoObjectIdNameOut = gson.fromJson(tangoObjectIdNameIn, java.lang.String.class);
        dto.setTangoObjectIdName(tangoObjectIdNameOut);
      }

      if (json.has("currentClientVersion")) {
        JsonElement currentClientVersionIn = json.get("currentClientVersion");
        int currentClientVersionOut = currentClientVersionIn.getAsInt();
        dto.setCurrentClientVersion(currentClientVersionOut);
      }

      return dto;
    }
    public static RecoverFromDroppedTangoInvalidationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RecoveredPayloadImpl implements com.google.collide.dto.RecoverFromDroppedTangoInvalidationResponse.RecoveredPayload, JsonSerializable {

    public static RecoveredPayloadImpl make() {
      return new RecoveredPayloadImpl();
    }

    protected java.lang.String payload;
    private boolean _hasPayload;
    protected int payloadVersion;
    private boolean _hasPayloadVersion;

    public boolean hasPayload() {
      return _hasPayload;
    }

    @Override
    public java.lang.String getPayload() {
      return payload;
    }

    public RecoveredPayloadImpl setPayload(java.lang.String v) {
      _hasPayload = true;
      payload = v;
      return this;
    }

    public boolean hasPayloadVersion() {
      return _hasPayloadVersion;
    }

    @Override
    public int getPayloadVersion() {
      return payloadVersion;
    }

    public RecoveredPayloadImpl setPayloadVersion(int v) {
      _hasPayloadVersion = true;
      payloadVersion = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof RecoveredPayloadImpl)) {
        return false;
      }
      RecoveredPayloadImpl other = (RecoveredPayloadImpl) o;
      if (this._hasPayload != other._hasPayload) {
        return false;
      }
      if (this._hasPayload) {
        if (!this.payload.equals(other.payload)) {
          return false;
        }
      }
      if (this._hasPayloadVersion != other._hasPayloadVersion) {
        return false;
      }
      if (this._hasPayloadVersion) {
        if (this.payloadVersion != other.payloadVersion) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasPayload ? payload.hashCode() : 0);
      hash = hash * 31 + (_hasPayloadVersion ? java.lang.Integer.valueOf(payloadVersion).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement payloadOut = (payload == null) ? JsonNull.INSTANCE : new JsonPrimitive(payload);
      result.add("payload", payloadOut);

      JsonPrimitive payloadVersionOut = new JsonPrimitive(payloadVersion);
      result.add("payloadVersion", payloadVersionOut);
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

    public static RecoveredPayloadImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RecoveredPayloadImpl dto = new RecoveredPayloadImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("payload")) {
        JsonElement payloadIn = json.get("payload");
        java.lang.String payloadOut = gson.fromJson(payloadIn, java.lang.String.class);
        dto.setPayload(payloadOut);
      }

      if (json.has("payloadVersion")) {
        JsonElement payloadVersionIn = json.get("payloadVersion");
        int payloadVersionOut = payloadVersionIn.getAsInt();
        dto.setPayloadVersion(payloadVersionOut);
      }

      return dto;
    }
    public static RecoveredPayloadImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RecoverFromDroppedTangoInvalidationResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.RecoverFromDroppedTangoInvalidationResponse, JsonSerializable {

    private RecoverFromDroppedTangoInvalidationResponseImpl() {
      super(81);
    }

    protected RecoverFromDroppedTangoInvalidationResponseImpl(int type) {
      super(type);
    }

    public static RecoverFromDroppedTangoInvalidationResponseImpl make() {
      return new RecoverFromDroppedTangoInvalidationResponseImpl();
    }

    protected java.util.List<RecoveredPayloadImpl> payloads;
    private boolean _hasPayloads;
    protected int currentObjectVersion;
    private boolean _hasCurrentObjectVersion;

    public boolean hasPayloads() {
      return _hasPayloads;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.RecoverFromDroppedTangoInvalidationResponse.RecoveredPayload> getPayloads() {
      ensurePayloads();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(payloads);
    }

    public RecoverFromDroppedTangoInvalidationResponseImpl setPayloads(java.util.List<RecoveredPayloadImpl> v) {
      _hasPayloads = true;
      payloads = v;
      return this;
    }

    public void addPayloads(RecoveredPayloadImpl v) {
      ensurePayloads();
      payloads.add(v);
    }

    public void clearPayloads() {
      ensurePayloads();
      payloads.clear();
    }

    private void ensurePayloads() {
      if (!_hasPayloads) {
        setPayloads(payloads != null ? payloads : new java.util.ArrayList<RecoveredPayloadImpl>());
      }
    }

    public boolean hasCurrentObjectVersion() {
      return _hasCurrentObjectVersion;
    }

    @Override
    public int getCurrentObjectVersion() {
      return currentObjectVersion;
    }

    public RecoverFromDroppedTangoInvalidationResponseImpl setCurrentObjectVersion(int v) {
      _hasCurrentObjectVersion = true;
      currentObjectVersion = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RecoverFromDroppedTangoInvalidationResponseImpl)) {
        return false;
      }
      RecoverFromDroppedTangoInvalidationResponseImpl other = (RecoverFromDroppedTangoInvalidationResponseImpl) o;
      if (this._hasPayloads != other._hasPayloads) {
        return false;
      }
      if (this._hasPayloads) {
        if (!this.payloads.equals(other.payloads)) {
          return false;
        }
      }
      if (this._hasCurrentObjectVersion != other._hasCurrentObjectVersion) {
        return false;
      }
      if (this._hasCurrentObjectVersion) {
        if (this.currentObjectVersion != other.currentObjectVersion) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasPayloads ? payloads.hashCode() : 0);
      hash = hash * 31 + (_hasCurrentObjectVersion ? java.lang.Integer.valueOf(currentObjectVersion).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray payloadsOut = new JsonArray();
      ensurePayloads();
      for (RecoveredPayloadImpl payloads_ : payloads) {
        JsonElement payloadsOut_ = payloads_ == null ? JsonNull.INSTANCE : payloads_.toJsonElement();
        payloadsOut.add(payloadsOut_);
      }
      result.add("payloads", payloadsOut);

      JsonPrimitive currentObjectVersionOut = new JsonPrimitive(currentObjectVersion);
      result.add("currentObjectVersion", currentObjectVersionOut);
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

    public static RecoverFromDroppedTangoInvalidationResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RecoverFromDroppedTangoInvalidationResponseImpl dto = new RecoverFromDroppedTangoInvalidationResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("payloads")) {
        JsonElement payloadsIn = json.get("payloads");
        java.util.ArrayList<RecoveredPayloadImpl> payloadsOut = null;
        if (payloadsIn != null && !payloadsIn.isJsonNull()) {
          payloadsOut = new java.util.ArrayList<RecoveredPayloadImpl>();
          java.util.Iterator<JsonElement> payloadsInIterator = payloadsIn.getAsJsonArray().iterator();
          while (payloadsInIterator.hasNext()) {
            JsonElement payloadsIn_ = payloadsInIterator.next();
            RecoveredPayloadImpl payloadsOut_ = RecoveredPayloadImpl.fromJsonElement(payloadsIn_);
            payloadsOut.add(payloadsOut_);
          }
        }
        dto.setPayloads(payloadsOut);
      }

      if (json.has("currentObjectVersion")) {
        JsonElement currentObjectVersionIn = json.get("currentObjectVersion");
        int currentObjectVersionOut = currentObjectVersionIn.getAsInt();
        dto.setCurrentObjectVersion(currentObjectVersionOut);
      }

      return dto;
    }
    public static RecoverFromDroppedTangoInvalidationResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RecoverFromMissedDocOpsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.RecoverFromMissedDocOps, JsonSerializable {

    private RecoverFromMissedDocOpsImpl() {
      super(82);
    }

    protected RecoverFromMissedDocOpsImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.util.List<java.lang.String> docOps2;
    private boolean _hasDocOps2;
    protected int currentCcRevision;
    private boolean _hasCurrentCcRevision;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public RecoverFromMissedDocOpsImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public RecoverFromMissedDocOpsImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public RecoverFromMissedDocOpsImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasDocOps2() {
      return _hasDocOps2;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getDocOps2() {
      ensureDocOps2();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(docOps2);
    }

    public RecoverFromMissedDocOpsImpl setDocOps2(java.util.List<java.lang.String> v) {
      _hasDocOps2 = true;
      docOps2 = v;
      return this;
    }

    public void addDocOps2(java.lang.String v) {
      ensureDocOps2();
      docOps2.add(v);
    }

    public void clearDocOps2() {
      ensureDocOps2();
      docOps2.clear();
    }

    private void ensureDocOps2() {
      if (!_hasDocOps2) {
        setDocOps2(docOps2 != null ? docOps2 : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasCurrentCcRevision() {
      return _hasCurrentCcRevision;
    }

    @Override
    public int getCurrentCcRevision() {
      return currentCcRevision;
    }

    public RecoverFromMissedDocOpsImpl setCurrentCcRevision(int v) {
      _hasCurrentCcRevision = true;
      currentCcRevision = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RecoverFromMissedDocOpsImpl)) {
        return false;
      }
      RecoverFromMissedDocOpsImpl other = (RecoverFromMissedDocOpsImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasDocOps2 != other._hasDocOps2) {
        return false;
      }
      if (this._hasDocOps2) {
        if (!this.docOps2.equals(other.docOps2)) {
          return false;
        }
      }
      if (this._hasCurrentCcRevision != other._hasCurrentCcRevision) {
        return false;
      }
      if (this._hasCurrentCcRevision) {
        if (this.currentCcRevision != other.currentCcRevision) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasDocOps2 ? docOps2.hashCode() : 0);
      hash = hash * 31 + (_hasCurrentCcRevision ? java.lang.Integer.valueOf(currentCcRevision).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonArray docOps2Out = new JsonArray();
      ensureDocOps2();
      for (java.lang.String docOps2_ : docOps2) {
        JsonElement docOps2Out_ = (docOps2_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(docOps2_);
        docOps2Out.add(docOps2Out_);
      }
      result.add("docOps2", docOps2Out);

      JsonPrimitive currentCcRevisionOut = new JsonPrimitive(currentCcRevision);
      result.add("currentCcRevision", currentCcRevisionOut);
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

    public static RecoverFromMissedDocOpsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RecoverFromMissedDocOpsImpl dto = new RecoverFromMissedDocOpsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("docOps2")) {
        JsonElement docOps2In = json.get("docOps2");
        java.util.ArrayList<java.lang.String> docOps2Out = null;
        if (docOps2In != null && !docOps2In.isJsonNull()) {
          docOps2Out = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> docOps2InIterator = docOps2In.getAsJsonArray().iterator();
          while (docOps2InIterator.hasNext()) {
            JsonElement docOps2In_ = docOps2InIterator.next();
            java.lang.String docOps2Out_ = gson.fromJson(docOps2In_, java.lang.String.class);
            docOps2Out.add(docOps2Out_);
          }
        }
        dto.setDocOps2(docOps2Out);
      }

      if (json.has("currentCcRevision")) {
        JsonElement currentCcRevisionIn = json.get("currentCcRevision");
        int currentCcRevisionOut = currentCcRevisionIn.getAsInt();
        dto.setCurrentCcRevision(currentCcRevisionOut);
      }

      return dto;
    }
    public static RecoverFromMissedDocOpsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RecoverFromMissedDocOpsResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.RecoverFromMissedDocOpsResponse, JsonSerializable {

    private RecoverFromMissedDocOpsResponseImpl() {
      super(83);
    }

    protected RecoverFromMissedDocOpsResponseImpl(int type) {
      super(type);
    }

    public static RecoverFromMissedDocOpsResponseImpl make() {
      return new RecoverFromMissedDocOpsResponseImpl();
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.util.List<ServerToClientDocOpImpl> docOps;
    private boolean _hasDocOps;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public RecoverFromMissedDocOpsResponseImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasDocOps() {
      return _hasDocOps;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ServerToClientDocOp> getDocOps() {
      ensureDocOps();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(docOps);
    }

    public RecoverFromMissedDocOpsResponseImpl setDocOps(java.util.List<ServerToClientDocOpImpl> v) {
      _hasDocOps = true;
      docOps = v;
      return this;
    }

    public void addDocOps(ServerToClientDocOpImpl v) {
      ensureDocOps();
      docOps.add(v);
    }

    public void clearDocOps() {
      ensureDocOps();
      docOps.clear();
    }

    private void ensureDocOps() {
      if (!_hasDocOps) {
        setDocOps(docOps != null ? docOps : new java.util.ArrayList<ServerToClientDocOpImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RecoverFromMissedDocOpsResponseImpl)) {
        return false;
      }
      RecoverFromMissedDocOpsResponseImpl other = (RecoverFromMissedDocOpsResponseImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasDocOps != other._hasDocOps) {
        return false;
      }
      if (this._hasDocOps) {
        if (!this.docOps.equals(other.docOps)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasDocOps ? docOps.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonArray docOpsOut = new JsonArray();
      ensureDocOps();
      for (ServerToClientDocOpImpl docOps_ : docOps) {
        JsonElement docOpsOut_ = docOps_ == null ? JsonNull.INSTANCE : docOps_.toJsonElement();
        docOpsOut.add(docOpsOut_);
      }
      result.add("docOps", docOpsOut);
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

    public static RecoverFromMissedDocOpsResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RecoverFromMissedDocOpsResponseImpl dto = new RecoverFromMissedDocOpsResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("docOps")) {
        JsonElement docOpsIn = json.get("docOps");
        java.util.ArrayList<ServerToClientDocOpImpl> docOpsOut = null;
        if (docOpsIn != null && !docOpsIn.isJsonNull()) {
          docOpsOut = new java.util.ArrayList<ServerToClientDocOpImpl>();
          java.util.Iterator<JsonElement> docOpsInIterator = docOpsIn.getAsJsonArray().iterator();
          while (docOpsInIterator.hasNext()) {
            JsonElement docOpsIn_ = docOpsInIterator.next();
            ServerToClientDocOpImpl docOpsOut_ = ServerToClientDocOpImpl.fromJsonElement(docOpsIn_);
            docOpsOut.add(docOpsOut_);
          }
        }
        dto.setDocOps(docOpsOut);
      }

      return dto;
    }
    public static RecoverFromMissedDocOpsResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RefreshWorkspaceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.RefreshWorkspace, JsonSerializable {

    private RefreshWorkspaceImpl() {
      super(84);
    }

    protected RefreshWorkspaceImpl(int type) {
      super(type);
    }

    public static RefreshWorkspaceImpl make() {
      return new RefreshWorkspaceImpl();
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String basePath;
    private boolean _hasBasePath;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public RefreshWorkspaceImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasBasePath() {
      return _hasBasePath;
    }

    @Override
    public java.lang.String getBasePath() {
      return basePath;
    }

    public RefreshWorkspaceImpl setBasePath(java.lang.String v) {
      _hasBasePath = true;
      basePath = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RefreshWorkspaceImpl)) {
        return false;
      }
      RefreshWorkspaceImpl other = (RefreshWorkspaceImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasBasePath != other._hasBasePath) {
        return false;
      }
      if (this._hasBasePath) {
        if (!this.basePath.equals(other.basePath)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasBasePath ? basePath.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement basePathOut = (basePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(basePath);
      result.add("basePath", basePathOut);
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

    public static RefreshWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RefreshWorkspaceImpl dto = new RefreshWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("basePath")) {
        JsonElement basePathIn = json.get("basePath");
        java.lang.String basePathOut = gson.fromJson(basePathIn, java.lang.String.class);
        dto.setBasePath(basePathOut);
      }

      return dto;
    }
    public static RefreshWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RequestProjectMembershipImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.RequestProjectMembership, JsonSerializable {

    private RequestProjectMembershipImpl() {
      super(85);
    }

    protected RequestProjectMembershipImpl(int type) {
      super(type);
    }

    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String projectId() {
      return projectId;
    }

    public RequestProjectMembershipImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RequestProjectMembershipImpl)) {
        return false;
      }
      RequestProjectMembershipImpl other = (RequestProjectMembershipImpl) o;
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
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

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

    public static RequestProjectMembershipImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RequestProjectMembershipImpl dto = new RequestProjectMembershipImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static RequestProjectMembershipImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ResolveConflictChunkImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ResolveConflictChunk, JsonSerializable {

    private ResolveConflictChunkImpl() {
      super(86);
    }

    protected ResolveConflictChunkImpl(int type) {
      super(type);
    }

    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected int conflictChunkIndex;
    private boolean _hasConflictChunkIndex;
    protected ConflictHandleImpl conflictHandle;
    private boolean _hasConflictHandle;
    protected boolean isResolved;
    private boolean _hasIsResolved;

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public ResolveConflictChunkImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public ResolveConflictChunkImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasConflictChunkIndex() {
      return _hasConflictChunkIndex;
    }

    @Override
    public int getConflictChunkIndex() {
      return conflictChunkIndex;
    }

    public ResolveConflictChunkImpl setConflictChunkIndex(int v) {
      _hasConflictChunkIndex = true;
      conflictChunkIndex = v;
      return this;
    }

    public boolean hasConflictHandle() {
      return _hasConflictHandle;
    }

    @Override
    public com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() {
      return conflictHandle;
    }

    public ResolveConflictChunkImpl setConflictHandle(ConflictHandleImpl v) {
      _hasConflictHandle = true;
      conflictHandle = v;
      return this;
    }

    public boolean hasIsResolved() {
      return _hasIsResolved;
    }

    @Override
    public boolean isResolved() {
      return isResolved;
    }

    public ResolveConflictChunkImpl setIsResolved(boolean v) {
      _hasIsResolved = true;
      isResolved = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ResolveConflictChunkImpl)) {
        return false;
      }
      ResolveConflictChunkImpl other = (ResolveConflictChunkImpl) o;
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasConflictChunkIndex != other._hasConflictChunkIndex) {
        return false;
      }
      if (this._hasConflictChunkIndex) {
        if (this.conflictChunkIndex != other.conflictChunkIndex) {
          return false;
        }
      }
      if (this._hasConflictHandle != other._hasConflictHandle) {
        return false;
      }
      if (this._hasConflictHandle) {
        if (!this.conflictHandle.equals(other.conflictHandle)) {
          return false;
        }
      }
      if (this._hasIsResolved != other._hasIsResolved) {
        return false;
      }
      if (this._hasIsResolved) {
        if (this.isResolved != other.isResolved) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasConflictChunkIndex ? java.lang.Integer.valueOf(conflictChunkIndex).hashCode() : 0);
      hash = hash * 31 + (_hasConflictHandle ? conflictHandle.hashCode() : 0);
      hash = hash * 31 + (_hasIsResolved ? java.lang.Boolean.valueOf(isResolved).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonPrimitive conflictChunkIndexOut = new JsonPrimitive(conflictChunkIndex);
      result.add("conflictChunkIndex", conflictChunkIndexOut);

      JsonElement conflictHandleOut = conflictHandle == null ? JsonNull.INSTANCE : conflictHandle.toJsonElement();
      result.add("conflictHandle", conflictHandleOut);

      JsonPrimitive isResolvedOut = new JsonPrimitive(isResolved);
      result.add("isResolved", isResolvedOut);
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

    public static ResolveConflictChunkImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ResolveConflictChunkImpl dto = new ResolveConflictChunkImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("conflictChunkIndex")) {
        JsonElement conflictChunkIndexIn = json.get("conflictChunkIndex");
        int conflictChunkIndexOut = conflictChunkIndexIn.getAsInt();
        dto.setConflictChunkIndex(conflictChunkIndexOut);
      }

      if (json.has("conflictHandle")) {
        JsonElement conflictHandleIn = json.get("conflictHandle");
        ConflictHandleImpl conflictHandleOut = ConflictHandleImpl.fromJsonElement(conflictHandleIn);
        dto.setConflictHandle(conflictHandleOut);
      }

      if (json.has("isResolved")) {
        JsonElement isResolvedIn = json.get("isResolved");
        boolean isResolvedOut = isResolvedIn.getAsBoolean();
        dto.setIsResolved(isResolvedOut);
      }

      return dto;
    }
    public static ResolveConflictChunkImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ResolveTreeConflictImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ResolveTreeConflict, JsonSerializable {

    private ResolveTreeConflictImpl() {
      super(87);
    }

    protected ResolveTreeConflictImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected ConflictHandleImpl conflictHandle;
    private boolean _hasConflictHandle;
    protected java.lang.String newPath;
    private boolean _hasNewPath;
    protected com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice resolutionChoice;
    private boolean _hasResolutionChoice;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public ResolveTreeConflictImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasConflictHandle() {
      return _hasConflictHandle;
    }

    @Override
    public com.google.collide.dto.NodeConflictDto.ConflictHandle getConflictHandle() {
      return conflictHandle;
    }

    public ResolveTreeConflictImpl setConflictHandle(ConflictHandleImpl v) {
      _hasConflictHandle = true;
      conflictHandle = v;
      return this;
    }

    public boolean hasNewPath() {
      return _hasNewPath;
    }

    @Override
    public java.lang.String getNewPath() {
      return newPath;
    }

    public ResolveTreeConflictImpl setNewPath(java.lang.String v) {
      _hasNewPath = true;
      newPath = v;
      return this;
    }

    public boolean hasResolutionChoice() {
      return _hasResolutionChoice;
    }

    @Override
    public com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice getResolutionChoice() {
      return resolutionChoice;
    }

    public ResolveTreeConflictImpl setResolutionChoice(com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice v) {
      _hasResolutionChoice = true;
      resolutionChoice = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ResolveTreeConflictImpl)) {
        return false;
      }
      ResolveTreeConflictImpl other = (ResolveTreeConflictImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasConflictHandle != other._hasConflictHandle) {
        return false;
      }
      if (this._hasConflictHandle) {
        if (!this.conflictHandle.equals(other.conflictHandle)) {
          return false;
        }
      }
      if (this._hasNewPath != other._hasNewPath) {
        return false;
      }
      if (this._hasNewPath) {
        if (!this.newPath.equals(other.newPath)) {
          return false;
        }
      }
      if (this._hasResolutionChoice != other._hasResolutionChoice) {
        return false;
      }
      if (this._hasResolutionChoice) {
        if (!this.resolutionChoice.equals(other.resolutionChoice)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasConflictHandle ? conflictHandle.hashCode() : 0);
      hash = hash * 31 + (_hasNewPath ? newPath.hashCode() : 0);
      hash = hash * 31 + (_hasResolutionChoice ? resolutionChoice.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement conflictHandleOut = conflictHandle == null ? JsonNull.INSTANCE : conflictHandle.toJsonElement();
      result.add("conflictHandle", conflictHandleOut);

      JsonElement newPathOut = (newPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(newPath);
      result.add("newPath", newPathOut);

      JsonElement resolutionChoiceOut = (resolutionChoice == null) ? JsonNull.INSTANCE : new JsonPrimitive(resolutionChoice.name());
      result.add("resolutionChoice", resolutionChoiceOut);
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

    public static ResolveTreeConflictImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ResolveTreeConflictImpl dto = new ResolveTreeConflictImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("conflictHandle")) {
        JsonElement conflictHandleIn = json.get("conflictHandle");
        ConflictHandleImpl conflictHandleOut = ConflictHandleImpl.fromJsonElement(conflictHandleIn);
        dto.setConflictHandle(conflictHandleOut);
      }

      if (json.has("newPath")) {
        JsonElement newPathIn = json.get("newPath");
        java.lang.String newPathOut = gson.fromJson(newPathIn, java.lang.String.class);
        dto.setNewPath(newPathOut);
      }

      if (json.has("resolutionChoice")) {
        JsonElement resolutionChoiceIn = json.get("resolutionChoice");
        com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice resolutionChoiceOut = gson.fromJson(resolutionChoiceIn, com.google.collide.dto.ResolveTreeConflict.ConflictResolutionChoice.class);
        dto.setResolutionChoice(resolutionChoiceOut);
      }

      return dto;
    }
    public static ResolveTreeConflictImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ResolveTreeConflictResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ResolveTreeConflictResponse, JsonSerializable {

    private ResolveTreeConflictResponseImpl() {
      super(88);
    }

    protected ResolveTreeConflictResponseImpl(int type) {
      super(type);
    }

    public static ResolveTreeConflictResponseImpl make() {
      return new ResolveTreeConflictResponseImpl();
    }

    protected java.lang.String refreshPath;
    private boolean _hasRefreshPath;

    public boolean hasRefreshPath() {
      return _hasRefreshPath;
    }

    @Override
    public java.lang.String getRefreshPath() {
      return refreshPath;
    }

    public ResolveTreeConflictResponseImpl setRefreshPath(java.lang.String v) {
      _hasRefreshPath = true;
      refreshPath = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ResolveTreeConflictResponseImpl)) {
        return false;
      }
      ResolveTreeConflictResponseImpl other = (ResolveTreeConflictResponseImpl) o;
      if (this._hasRefreshPath != other._hasRefreshPath) {
        return false;
      }
      if (this._hasRefreshPath) {
        if (!this.refreshPath.equals(other.refreshPath)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasRefreshPath ? refreshPath.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement refreshPathOut = (refreshPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(refreshPath);
      result.add("refreshPath", refreshPathOut);
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

    public static ResolveTreeConflictResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ResolveTreeConflictResponseImpl dto = new ResolveTreeConflictResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("refreshPath")) {
        JsonElement refreshPathIn = json.get("refreshPath");
        java.lang.String refreshPathOut = gson.fromJson(refreshPathIn, java.lang.String.class);
        dto.setRefreshPath(refreshPathOut);
      }

      return dto;
    }
    public static ResolveTreeConflictResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RetryAlreadyTransferredUploadImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.RetryAlreadyTransferredUpload, JsonSerializable {

    private RetryAlreadyTransferredUploadImpl() {
      super(121);
    }

    protected RetryAlreadyTransferredUploadImpl(int type) {
      super(type);
    }

    protected java.lang.String sessionId;
    private boolean _hasSessionId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.util.List<java.lang.String> fileWorkspacePaths;
    private boolean _hasFileWorkspacePaths;
    protected java.util.List<java.lang.String> unzipWorkspacePaths;
    private boolean _hasUnzipWorkspacePaths;

    public boolean hasSessionId() {
      return _hasSessionId;
    }

    @Override
    public java.lang.String getSessionId() {
      return sessionId;
    }

    public RetryAlreadyTransferredUploadImpl setSessionId(java.lang.String v) {
      _hasSessionId = true;
      sessionId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public RetryAlreadyTransferredUploadImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasFileWorkspacePaths() {
      return _hasFileWorkspacePaths;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getFileWorkspacePaths() {
      ensureFileWorkspacePaths();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(fileWorkspacePaths);
    }

    public RetryAlreadyTransferredUploadImpl setFileWorkspacePaths(java.util.List<java.lang.String> v) {
      _hasFileWorkspacePaths = true;
      fileWorkspacePaths = v;
      return this;
    }

    public void addFileWorkspacePaths(java.lang.String v) {
      ensureFileWorkspacePaths();
      fileWorkspacePaths.add(v);
    }

    public void clearFileWorkspacePaths() {
      ensureFileWorkspacePaths();
      fileWorkspacePaths.clear();
    }

    private void ensureFileWorkspacePaths() {
      if (!_hasFileWorkspacePaths) {
        setFileWorkspacePaths(fileWorkspacePaths != null ? fileWorkspacePaths : new java.util.ArrayList<java.lang.String>());
      }
    }

    public boolean hasUnzipWorkspacePaths() {
      return _hasUnzipWorkspacePaths;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getUnzipWorkspacePaths() {
      ensureUnzipWorkspacePaths();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(unzipWorkspacePaths);
    }

    public RetryAlreadyTransferredUploadImpl setUnzipWorkspacePaths(java.util.List<java.lang.String> v) {
      _hasUnzipWorkspacePaths = true;
      unzipWorkspacePaths = v;
      return this;
    }

    public void addUnzipWorkspacePaths(java.lang.String v) {
      ensureUnzipWorkspacePaths();
      unzipWorkspacePaths.add(v);
    }

    public void clearUnzipWorkspacePaths() {
      ensureUnzipWorkspacePaths();
      unzipWorkspacePaths.clear();
    }

    private void ensureUnzipWorkspacePaths() {
      if (!_hasUnzipWorkspacePaths) {
        setUnzipWorkspacePaths(unzipWorkspacePaths != null ? unzipWorkspacePaths : new java.util.ArrayList<java.lang.String>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RetryAlreadyTransferredUploadImpl)) {
        return false;
      }
      RetryAlreadyTransferredUploadImpl other = (RetryAlreadyTransferredUploadImpl) o;
      if (this._hasSessionId != other._hasSessionId) {
        return false;
      }
      if (this._hasSessionId) {
        if (!this.sessionId.equals(other.sessionId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasFileWorkspacePaths != other._hasFileWorkspacePaths) {
        return false;
      }
      if (this._hasFileWorkspacePaths) {
        if (!this.fileWorkspacePaths.equals(other.fileWorkspacePaths)) {
          return false;
        }
      }
      if (this._hasUnzipWorkspacePaths != other._hasUnzipWorkspacePaths) {
        return false;
      }
      if (this._hasUnzipWorkspacePaths) {
        if (!this.unzipWorkspacePaths.equals(other.unzipWorkspacePaths)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasSessionId ? sessionId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasFileWorkspacePaths ? fileWorkspacePaths.hashCode() : 0);
      hash = hash * 31 + (_hasUnzipWorkspacePaths ? unzipWorkspacePaths.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement sessionIdOut = (sessionId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sessionId);
      result.add("sessionId", sessionIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonArray fileWorkspacePathsOut = new JsonArray();
      ensureFileWorkspacePaths();
      for (java.lang.String fileWorkspacePaths_ : fileWorkspacePaths) {
        JsonElement fileWorkspacePathsOut_ = (fileWorkspacePaths_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileWorkspacePaths_);
        fileWorkspacePathsOut.add(fileWorkspacePathsOut_);
      }
      result.add("fileWorkspacePaths", fileWorkspacePathsOut);

      JsonArray unzipWorkspacePathsOut = new JsonArray();
      ensureUnzipWorkspacePaths();
      for (java.lang.String unzipWorkspacePaths_ : unzipWorkspacePaths) {
        JsonElement unzipWorkspacePathsOut_ = (unzipWorkspacePaths_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(unzipWorkspacePaths_);
        unzipWorkspacePathsOut.add(unzipWorkspacePathsOut_);
      }
      result.add("unzipWorkspacePaths", unzipWorkspacePathsOut);
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

    public static RetryAlreadyTransferredUploadImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RetryAlreadyTransferredUploadImpl dto = new RetryAlreadyTransferredUploadImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("sessionId")) {
        JsonElement sessionIdIn = json.get("sessionId");
        java.lang.String sessionIdOut = gson.fromJson(sessionIdIn, java.lang.String.class);
        dto.setSessionId(sessionIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("fileWorkspacePaths")) {
        JsonElement fileWorkspacePathsIn = json.get("fileWorkspacePaths");
        java.util.ArrayList<java.lang.String> fileWorkspacePathsOut = null;
        if (fileWorkspacePathsIn != null && !fileWorkspacePathsIn.isJsonNull()) {
          fileWorkspacePathsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> fileWorkspacePathsInIterator = fileWorkspacePathsIn.getAsJsonArray().iterator();
          while (fileWorkspacePathsInIterator.hasNext()) {
            JsonElement fileWorkspacePathsIn_ = fileWorkspacePathsInIterator.next();
            java.lang.String fileWorkspacePathsOut_ = gson.fromJson(fileWorkspacePathsIn_, java.lang.String.class);
            fileWorkspacePathsOut.add(fileWorkspacePathsOut_);
          }
        }
        dto.setFileWorkspacePaths(fileWorkspacePathsOut);
      }

      if (json.has("unzipWorkspacePaths")) {
        JsonElement unzipWorkspacePathsIn = json.get("unzipWorkspacePaths");
        java.util.ArrayList<java.lang.String> unzipWorkspacePathsOut = null;
        if (unzipWorkspacePathsIn != null && !unzipWorkspacePathsIn.isJsonNull()) {
          unzipWorkspacePathsOut = new java.util.ArrayList<java.lang.String>();
          java.util.Iterator<JsonElement> unzipWorkspacePathsInIterator = unzipWorkspacePathsIn.getAsJsonArray().iterator();
          while (unzipWorkspacePathsInIterator.hasNext()) {
            JsonElement unzipWorkspacePathsIn_ = unzipWorkspacePathsInIterator.next();
            java.lang.String unzipWorkspacePathsOut_ = gson.fromJson(unzipWorkspacePathsIn_, java.lang.String.class);
            unzipWorkspacePathsOut.add(unzipWorkspacePathsOut_);
          }
        }
        dto.setUnzipWorkspacePaths(unzipWorkspacePathsOut);
      }

      return dto;
    }
    public static RetryAlreadyTransferredUploadImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RevisionImpl implements com.google.collide.dto.Revision, JsonSerializable {

    public static RevisionImpl make() {
      return new RevisionImpl();
    }

    protected java.lang.String rootId;
    private boolean _hasRootId;
    protected java.lang.String nodeId;
    private boolean _hasNodeId;
    protected com.google.collide.dto.Revision.RevisionType revisionType;
    private boolean _hasRevisionType;
    protected boolean hasUnresolvedConflicts;
    private boolean _hasHasUnresolvedConflicts;
    protected boolean isFinalResolution;
    private boolean _hasIsFinalResolution;
    protected int previousNodesSkipped;
    private boolean _hasPreviousNodesSkipped;
    protected java.lang.String timestamp;
    private boolean _hasTimestamp;

    public boolean hasRootId() {
      return _hasRootId;
    }

    @Override
    public java.lang.String getRootId() {
      return rootId;
    }

    public RevisionImpl setRootId(java.lang.String v) {
      _hasRootId = true;
      rootId = v;
      return this;
    }

    public boolean hasNodeId() {
      return _hasNodeId;
    }

    @Override
    public java.lang.String getNodeId() {
      return nodeId;
    }

    public RevisionImpl setNodeId(java.lang.String v) {
      _hasNodeId = true;
      nodeId = v;
      return this;
    }

    public boolean hasRevisionType() {
      return _hasRevisionType;
    }

    @Override
    public com.google.collide.dto.Revision.RevisionType getRevisionType() {
      return revisionType;
    }

    public RevisionImpl setRevisionType(com.google.collide.dto.Revision.RevisionType v) {
      _hasRevisionType = true;
      revisionType = v;
      return this;
    }

    public boolean hasHasUnresolvedConflicts() {
      return _hasHasUnresolvedConflicts;
    }

    @Override
    public boolean getHasUnresolvedConflicts() {
      return hasUnresolvedConflicts;
    }

    public RevisionImpl setHasUnresolvedConflicts(boolean v) {
      _hasHasUnresolvedConflicts = true;
      hasUnresolvedConflicts = v;
      return this;
    }

    public boolean hasIsFinalResolution() {
      return _hasIsFinalResolution;
    }

    @Override
    public boolean getIsFinalResolution() {
      return isFinalResolution;
    }

    public RevisionImpl setIsFinalResolution(boolean v) {
      _hasIsFinalResolution = true;
      isFinalResolution = v;
      return this;
    }

    public boolean hasPreviousNodesSkipped() {
      return _hasPreviousNodesSkipped;
    }

    @Override
    public int getPreviousNodesSkipped() {
      return previousNodesSkipped;
    }

    public RevisionImpl setPreviousNodesSkipped(int v) {
      _hasPreviousNodesSkipped = true;
      previousNodesSkipped = v;
      return this;
    }

    public boolean hasTimestamp() {
      return _hasTimestamp;
    }

    @Override
    public java.lang.String getTimestamp() {
      return timestamp;
    }

    public RevisionImpl setTimestamp(java.lang.String v) {
      _hasTimestamp = true;
      timestamp = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof RevisionImpl)) {
        return false;
      }
      RevisionImpl other = (RevisionImpl) o;
      if (this._hasRootId != other._hasRootId) {
        return false;
      }
      if (this._hasRootId) {
        if (!this.rootId.equals(other.rootId)) {
          return false;
        }
      }
      if (this._hasNodeId != other._hasNodeId) {
        return false;
      }
      if (this._hasNodeId) {
        if (!this.nodeId.equals(other.nodeId)) {
          return false;
        }
      }
      if (this._hasRevisionType != other._hasRevisionType) {
        return false;
      }
      if (this._hasRevisionType) {
        if (!this.revisionType.equals(other.revisionType)) {
          return false;
        }
      }
      if (this._hasHasUnresolvedConflicts != other._hasHasUnresolvedConflicts) {
        return false;
      }
      if (this._hasHasUnresolvedConflicts) {
        if (this.hasUnresolvedConflicts != other.hasUnresolvedConflicts) {
          return false;
        }
      }
      if (this._hasIsFinalResolution != other._hasIsFinalResolution) {
        return false;
      }
      if (this._hasIsFinalResolution) {
        if (this.isFinalResolution != other.isFinalResolution) {
          return false;
        }
      }
      if (this._hasPreviousNodesSkipped != other._hasPreviousNodesSkipped) {
        return false;
      }
      if (this._hasPreviousNodesSkipped) {
        if (this.previousNodesSkipped != other.previousNodesSkipped) {
          return false;
        }
      }
      if (this._hasTimestamp != other._hasTimestamp) {
        return false;
      }
      if (this._hasTimestamp) {
        if (!this.timestamp.equals(other.timestamp)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasRootId ? rootId.hashCode() : 0);
      hash = hash * 31 + (_hasNodeId ? nodeId.hashCode() : 0);
      hash = hash * 31 + (_hasRevisionType ? revisionType.hashCode() : 0);
      hash = hash * 31 + (_hasHasUnresolvedConflicts ? java.lang.Boolean.valueOf(hasUnresolvedConflicts).hashCode() : 0);
      hash = hash * 31 + (_hasIsFinalResolution ? java.lang.Boolean.valueOf(isFinalResolution).hashCode() : 0);
      hash = hash * 31 + (_hasPreviousNodesSkipped ? java.lang.Integer.valueOf(previousNodesSkipped).hashCode() : 0);
      hash = hash * 31 + (_hasTimestamp ? timestamp.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement rootIdOut = (rootId == null) ? JsonNull.INSTANCE : new JsonPrimitive(rootId);
      result.add("rootId", rootIdOut);

      JsonElement nodeIdOut = (nodeId == null) ? JsonNull.INSTANCE : new JsonPrimitive(nodeId);
      result.add("nodeId", nodeIdOut);

      JsonElement revisionTypeOut = (revisionType == null) ? JsonNull.INSTANCE : new JsonPrimitive(revisionType.name());
      result.add("revisionType", revisionTypeOut);

      JsonPrimitive hasUnresolvedConflictsOut = new JsonPrimitive(hasUnresolvedConflicts);
      result.add("hasUnresolvedConflicts", hasUnresolvedConflictsOut);

      JsonPrimitive isFinalResolutionOut = new JsonPrimitive(isFinalResolution);
      result.add("isFinalResolution", isFinalResolutionOut);

      JsonPrimitive previousNodesSkippedOut = new JsonPrimitive(previousNodesSkipped);
      result.add("previousNodesSkipped", previousNodesSkippedOut);

      JsonElement timestampOut = (timestamp == null) ? JsonNull.INSTANCE : new JsonPrimitive(timestamp);
      result.add("timestamp", timestampOut);
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

    public static RevisionImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RevisionImpl dto = new RevisionImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("rootId")) {
        JsonElement rootIdIn = json.get("rootId");
        java.lang.String rootIdOut = gson.fromJson(rootIdIn, java.lang.String.class);
        dto.setRootId(rootIdOut);
      }

      if (json.has("nodeId")) {
        JsonElement nodeIdIn = json.get("nodeId");
        java.lang.String nodeIdOut = gson.fromJson(nodeIdIn, java.lang.String.class);
        dto.setNodeId(nodeIdOut);
      }

      if (json.has("revisionType")) {
        JsonElement revisionTypeIn = json.get("revisionType");
        com.google.collide.dto.Revision.RevisionType revisionTypeOut = gson.fromJson(revisionTypeIn, com.google.collide.dto.Revision.RevisionType.class);
        dto.setRevisionType(revisionTypeOut);
      }

      if (json.has("hasUnresolvedConflicts")) {
        JsonElement hasUnresolvedConflictsIn = json.get("hasUnresolvedConflicts");
        boolean hasUnresolvedConflictsOut = hasUnresolvedConflictsIn.getAsBoolean();
        dto.setHasUnresolvedConflicts(hasUnresolvedConflictsOut);
      }

      if (json.has("isFinalResolution")) {
        JsonElement isFinalResolutionIn = json.get("isFinalResolution");
        boolean isFinalResolutionOut = isFinalResolutionIn.getAsBoolean();
        dto.setIsFinalResolution(isFinalResolutionOut);
      }

      if (json.has("previousNodesSkipped")) {
        JsonElement previousNodesSkippedIn = json.get("previousNodesSkipped");
        int previousNodesSkippedOut = previousNodesSkippedIn.getAsInt();
        dto.setPreviousNodesSkipped(previousNodesSkippedOut);
      }

      if (json.has("timestamp")) {
        JsonElement timestampIn = json.get("timestamp");
        java.lang.String timestampOut = gson.fromJson(timestampIn, java.lang.String.class);
        dto.setTimestamp(timestampOut);
      }

      return dto;
    }
    public static RevisionImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class RunTargetImpl implements com.google.collide.dto.RunTarget, JsonSerializable {

    public static RunTargetImpl make() {
      return new RunTargetImpl();
    }

    protected com.google.collide.dto.RunTarget.RunMode runMode;
    private boolean _hasRunMode;
    protected java.lang.String alwaysRunFilename;
    private boolean _hasAlwaysRunFilename;
    protected java.lang.String alwaysRunUrlOrQuery;
    private boolean _hasAlwaysRunUrlOrQuery;

    public boolean hasRunMode() {
      return _hasRunMode;
    }

    @Override
    public com.google.collide.dto.RunTarget.RunMode getRunMode() {
      return runMode;
    }

    public RunTargetImpl setRunMode(com.google.collide.dto.RunTarget.RunMode v) {
      _hasRunMode = true;
      runMode = v;
      return this;
    }

    public boolean hasAlwaysRunFilename() {
      return _hasAlwaysRunFilename;
    }

    @Override
    public java.lang.String getAlwaysRunFilename() {
      return alwaysRunFilename;
    }

    public RunTargetImpl setAlwaysRunFilename(java.lang.String v) {
      _hasAlwaysRunFilename = true;
      alwaysRunFilename = v;
      return this;
    }

    public boolean hasAlwaysRunUrlOrQuery() {
      return _hasAlwaysRunUrlOrQuery;
    }

    @Override
    public java.lang.String getAlwaysRunUrlOrQuery() {
      return alwaysRunUrlOrQuery;
    }

    public RunTargetImpl setAlwaysRunUrlOrQuery(java.lang.String v) {
      _hasAlwaysRunUrlOrQuery = true;
      alwaysRunUrlOrQuery = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof RunTargetImpl)) {
        return false;
      }
      RunTargetImpl other = (RunTargetImpl) o;
      if (this._hasRunMode != other._hasRunMode) {
        return false;
      }
      if (this._hasRunMode) {
        if (!this.runMode.equals(other.runMode)) {
          return false;
        }
      }
      if (this._hasAlwaysRunFilename != other._hasAlwaysRunFilename) {
        return false;
      }
      if (this._hasAlwaysRunFilename) {
        if (!this.alwaysRunFilename.equals(other.alwaysRunFilename)) {
          return false;
        }
      }
      if (this._hasAlwaysRunUrlOrQuery != other._hasAlwaysRunUrlOrQuery) {
        return false;
      }
      if (this._hasAlwaysRunUrlOrQuery) {
        if (!this.alwaysRunUrlOrQuery.equals(other.alwaysRunUrlOrQuery)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasRunMode ? runMode.hashCode() : 0);
      hash = hash * 31 + (_hasAlwaysRunFilename ? alwaysRunFilename.hashCode() : 0);
      hash = hash * 31 + (_hasAlwaysRunUrlOrQuery ? alwaysRunUrlOrQuery.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement runModeOut = (runMode == null) ? JsonNull.INSTANCE : new JsonPrimitive(runMode.name());
      result.add("runMode", runModeOut);

      JsonElement alwaysRunFilenameOut = (alwaysRunFilename == null) ? JsonNull.INSTANCE : new JsonPrimitive(alwaysRunFilename);
      result.add("alwaysRunFilename", alwaysRunFilenameOut);

      JsonElement alwaysRunUrlOrQueryOut = (alwaysRunUrlOrQuery == null) ? JsonNull.INSTANCE : new JsonPrimitive(alwaysRunUrlOrQuery);
      result.add("alwaysRunUrlOrQuery", alwaysRunUrlOrQueryOut);
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

    public static RunTargetImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RunTargetImpl dto = new RunTargetImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("runMode")) {
        JsonElement runModeIn = json.get("runMode");
        com.google.collide.dto.RunTarget.RunMode runModeOut = gson.fromJson(runModeIn, com.google.collide.dto.RunTarget.RunMode.class);
        dto.setRunMode(runModeOut);
      }

      if (json.has("alwaysRunFilename")) {
        JsonElement alwaysRunFilenameIn = json.get("alwaysRunFilename");
        java.lang.String alwaysRunFilenameOut = gson.fromJson(alwaysRunFilenameIn, java.lang.String.class);
        dto.setAlwaysRunFilename(alwaysRunFilenameOut);
      }

      if (json.has("alwaysRunUrlOrQuery")) {
        JsonElement alwaysRunUrlOrQueryIn = json.get("alwaysRunUrlOrQuery");
        java.lang.String alwaysRunUrlOrQueryOut = gson.fromJson(alwaysRunUrlOrQueryIn, java.lang.String.class);
        dto.setAlwaysRunUrlOrQuery(alwaysRunUrlOrQueryOut);
      }

      return dto;
    }
    public static RunTargetImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SearchImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.Search, JsonSerializable {

    private SearchImpl() {
      super(89);
    }

    protected SearchImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected int page;
    private boolean _hasPage;
    protected java.lang.String query;
    private boolean _hasQuery;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public SearchImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasPage() {
      return _hasPage;
    }

    @Override
    public int getPage() {
      return page;
    }

    public SearchImpl setPage(int v) {
      _hasPage = true;
      page = v;
      return this;
    }

    public boolean hasQuery() {
      return _hasQuery;
    }

    @Override
    public java.lang.String getQuery() {
      return query;
    }

    public SearchImpl setQuery(java.lang.String v) {
      _hasQuery = true;
      query = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SearchImpl)) {
        return false;
      }
      SearchImpl other = (SearchImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasPage != other._hasPage) {
        return false;
      }
      if (this._hasPage) {
        if (this.page != other.page) {
          return false;
        }
      }
      if (this._hasQuery != other._hasQuery) {
        return false;
      }
      if (this._hasQuery) {
        if (!this.query.equals(other.query)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasPage ? java.lang.Integer.valueOf(page).hashCode() : 0);
      hash = hash * 31 + (_hasQuery ? query.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonPrimitive pageOut = new JsonPrimitive(page);
      result.add("page", pageOut);

      JsonElement queryOut = (query == null) ? JsonNull.INSTANCE : new JsonPrimitive(query);
      result.add("query", queryOut);
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

    public static SearchImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SearchImpl dto = new SearchImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("page")) {
        JsonElement pageIn = json.get("page");
        int pageOut = pageIn.getAsInt();
        dto.setPage(pageOut);
      }

      if (json.has("query")) {
        JsonElement queryIn = json.get("query");
        java.lang.String queryOut = gson.fromJson(queryIn, java.lang.String.class);
        dto.setQuery(queryOut);
      }

      return dto;
    }
    public static SearchImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SearchResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SearchResponse, JsonSerializable {

    private SearchResponseImpl() {
      super(90);
    }

    protected SearchResponseImpl(int type) {
      super(type);
    }

    public static SearchResponseImpl make() {
      return new SearchResponseImpl();
    }

    protected int resultCount;
    private boolean _hasResultCount;
    protected int pageCount;
    private boolean _hasPageCount;
    protected int page;
    private boolean _hasPage;
    protected java.util.List<SearchResultImpl> results;
    private boolean _hasResults;

    public boolean hasResultCount() {
      return _hasResultCount;
    }

    @Override
    public int getResultCount() {
      return resultCount;
    }

    public SearchResponseImpl setResultCount(int v) {
      _hasResultCount = true;
      resultCount = v;
      return this;
    }

    public boolean hasPageCount() {
      return _hasPageCount;
    }

    @Override
    public int getPageCount() {
      return pageCount;
    }

    public SearchResponseImpl setPageCount(int v) {
      _hasPageCount = true;
      pageCount = v;
      return this;
    }

    public boolean hasPage() {
      return _hasPage;
    }

    @Override
    public int getPage() {
      return page;
    }

    public SearchResponseImpl setPage(int v) {
      _hasPage = true;
      page = v;
      return this;
    }

    public boolean hasResults() {
      return _hasResults;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.SearchResult> getResults() {
      ensureResults();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(results);
    }

    public SearchResponseImpl setResults(java.util.List<SearchResultImpl> v) {
      _hasResults = true;
      results = v;
      return this;
    }

    public void addResults(SearchResultImpl v) {
      ensureResults();
      results.add(v);
    }

    public void clearResults() {
      ensureResults();
      results.clear();
    }

    private void ensureResults() {
      if (!_hasResults) {
        setResults(results != null ? results : new java.util.ArrayList<SearchResultImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SearchResponseImpl)) {
        return false;
      }
      SearchResponseImpl other = (SearchResponseImpl) o;
      if (this._hasResultCount != other._hasResultCount) {
        return false;
      }
      if (this._hasResultCount) {
        if (this.resultCount != other.resultCount) {
          return false;
        }
      }
      if (this._hasPageCount != other._hasPageCount) {
        return false;
      }
      if (this._hasPageCount) {
        if (this.pageCount != other.pageCount) {
          return false;
        }
      }
      if (this._hasPage != other._hasPage) {
        return false;
      }
      if (this._hasPage) {
        if (this.page != other.page) {
          return false;
        }
      }
      if (this._hasResults != other._hasResults) {
        return false;
      }
      if (this._hasResults) {
        if (!this.results.equals(other.results)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasResultCount ? java.lang.Integer.valueOf(resultCount).hashCode() : 0);
      hash = hash * 31 + (_hasPageCount ? java.lang.Integer.valueOf(pageCount).hashCode() : 0);
      hash = hash * 31 + (_hasPage ? java.lang.Integer.valueOf(page).hashCode() : 0);
      hash = hash * 31 + (_hasResults ? results.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive resultCountOut = new JsonPrimitive(resultCount);
      result.add("resultCount", resultCountOut);

      JsonPrimitive pageCountOut = new JsonPrimitive(pageCount);
      result.add("pageCount", pageCountOut);

      JsonPrimitive pageOut = new JsonPrimitive(page);
      result.add("page", pageOut);

      JsonArray resultsOut = new JsonArray();
      ensureResults();
      for (SearchResultImpl results_ : results) {
        JsonElement resultsOut_ = results_ == null ? JsonNull.INSTANCE : results_.toJsonElement();
        resultsOut.add(resultsOut_);
      }
      result.add("results", resultsOut);
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

    public static SearchResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SearchResponseImpl dto = new SearchResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("resultCount")) {
        JsonElement resultCountIn = json.get("resultCount");
        int resultCountOut = resultCountIn.getAsInt();
        dto.setResultCount(resultCountOut);
      }

      if (json.has("pageCount")) {
        JsonElement pageCountIn = json.get("pageCount");
        int pageCountOut = pageCountIn.getAsInt();
        dto.setPageCount(pageCountOut);
      }

      if (json.has("page")) {
        JsonElement pageIn = json.get("page");
        int pageOut = pageIn.getAsInt();
        dto.setPage(pageOut);
      }

      if (json.has("results")) {
        JsonElement resultsIn = json.get("results");
        java.util.ArrayList<SearchResultImpl> resultsOut = null;
        if (resultsIn != null && !resultsIn.isJsonNull()) {
          resultsOut = new java.util.ArrayList<SearchResultImpl>();
          java.util.Iterator<JsonElement> resultsInIterator = resultsIn.getAsJsonArray().iterator();
          while (resultsInIterator.hasNext()) {
            JsonElement resultsIn_ = resultsInIterator.next();
            SearchResultImpl resultsOut_ = SearchResultImpl.fromJsonElement(resultsIn_);
            resultsOut.add(resultsOut_);
          }
        }
        dto.setResults(resultsOut);
      }

      return dto;
    }
    public static SearchResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SearchResultImpl implements com.google.collide.dto.SearchResult, JsonSerializable {

    public static SearchResultImpl make() {
      return new SearchResultImpl();
    }

    protected java.lang.String title;
    private boolean _hasTitle;
    protected java.util.List<SnippetImpl> snippets;
    private boolean _hasSnippets;
    protected java.lang.String url;
    private boolean _hasUrl;

    public boolean hasTitle() {
      return _hasTitle;
    }

    @Override
    public java.lang.String getTitle() {
      return title;
    }

    public SearchResultImpl setTitle(java.lang.String v) {
      _hasTitle = true;
      title = v;
      return this;
    }

    public boolean hasSnippets() {
      return _hasSnippets;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.Snippet> getSnippets() {
      ensureSnippets();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(snippets);
    }

    public SearchResultImpl setSnippets(java.util.List<SnippetImpl> v) {
      _hasSnippets = true;
      snippets = v;
      return this;
    }

    public void addSnippets(SnippetImpl v) {
      ensureSnippets();
      snippets.add(v);
    }

    public void clearSnippets() {
      ensureSnippets();
      snippets.clear();
    }

    private void ensureSnippets() {
      if (!_hasSnippets) {
        setSnippets(snippets != null ? snippets : new java.util.ArrayList<SnippetImpl>());
      }
    }

    public boolean hasUrl() {
      return _hasUrl;
    }

    @Override
    public java.lang.String getUrl() {
      return url;
    }

    public SearchResultImpl setUrl(java.lang.String v) {
      _hasUrl = true;
      url = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof SearchResultImpl)) {
        return false;
      }
      SearchResultImpl other = (SearchResultImpl) o;
      if (this._hasTitle != other._hasTitle) {
        return false;
      }
      if (this._hasTitle) {
        if (!this.title.equals(other.title)) {
          return false;
        }
      }
      if (this._hasSnippets != other._hasSnippets) {
        return false;
      }
      if (this._hasSnippets) {
        if (!this.snippets.equals(other.snippets)) {
          return false;
        }
      }
      if (this._hasUrl != other._hasUrl) {
        return false;
      }
      if (this._hasUrl) {
        if (!this.url.equals(other.url)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasTitle ? title.hashCode() : 0);
      hash = hash * 31 + (_hasSnippets ? snippets.hashCode() : 0);
      hash = hash * 31 + (_hasUrl ? url.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement titleOut = (title == null) ? JsonNull.INSTANCE : new JsonPrimitive(title);
      result.add("title", titleOut);

      JsonArray snippetsOut = new JsonArray();
      ensureSnippets();
      for (SnippetImpl snippets_ : snippets) {
        JsonElement snippetsOut_ = snippets_ == null ? JsonNull.INSTANCE : snippets_.toJsonElement();
        snippetsOut.add(snippetsOut_);
      }
      result.add("snippets", snippetsOut);

      JsonElement urlOut = (url == null) ? JsonNull.INSTANCE : new JsonPrimitive(url);
      result.add("url", urlOut);
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

    public static SearchResultImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SearchResultImpl dto = new SearchResultImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("title")) {
        JsonElement titleIn = json.get("title");
        java.lang.String titleOut = gson.fromJson(titleIn, java.lang.String.class);
        dto.setTitle(titleOut);
      }

      if (json.has("snippets")) {
        JsonElement snippetsIn = json.get("snippets");
        java.util.ArrayList<SnippetImpl> snippetsOut = null;
        if (snippetsIn != null && !snippetsIn.isJsonNull()) {
          snippetsOut = new java.util.ArrayList<SnippetImpl>();
          java.util.Iterator<JsonElement> snippetsInIterator = snippetsIn.getAsJsonArray().iterator();
          while (snippetsInIterator.hasNext()) {
            JsonElement snippetsIn_ = snippetsInIterator.next();
            SnippetImpl snippetsOut_ = SnippetImpl.fromJsonElement(snippetsIn_);
            snippetsOut.add(snippetsOut_);
          }
        }
        dto.setSnippets(snippetsOut);
      }

      if (json.has("url")) {
        JsonElement urlIn = json.get("url");
        java.lang.String urlOut = gson.fromJson(urlIn, java.lang.String.class);
        dto.setUrl(urlOut);
      }

      return dto;
    }
    public static SearchResultImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ServerToClientDocOpImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ServerToClientDocOp, JsonSerializable {

    private ServerToClientDocOpImpl() {
      super(92);
    }

    protected ServerToClientDocOpImpl(int type) {
      super(type);
    }

    public static ServerToClientDocOpImpl make() {
      return new ServerToClientDocOpImpl();
    }

    protected java.lang.String filePath;
    private boolean _hasFilePath;
    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected DocumentSelectionImpl selection;
    private boolean _hasSelection;
    protected DocOpImpl docOp2;
    private boolean _hasDocOp2;
    protected int appliedCcRevision;
    private boolean _hasAppliedCcRevision;

    public boolean hasFilePath() {
      return _hasFilePath;
    }

    @Override
    public java.lang.String getFilePath() {
      return filePath;
    }

    public ServerToClientDocOpImpl setFilePath(java.lang.String v) {
      _hasFilePath = true;
      filePath = v;
      return this;
    }

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public ServerToClientDocOpImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public ServerToClientDocOpImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public ServerToClientDocOpImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasSelection() {
      return _hasSelection;
    }

    @Override
    public com.google.collide.dto.DocumentSelection getSelection() {
      return selection;
    }

    public ServerToClientDocOpImpl setSelection(DocumentSelectionImpl v) {
      _hasSelection = true;
      selection = v;
      return this;
    }

    public boolean hasDocOp2() {
      return _hasDocOp2;
    }

    @Override
    public com.google.collide.dto.DocOp getDocOp2() {
      return docOp2;
    }

    public ServerToClientDocOpImpl setDocOp2(DocOpImpl v) {
      _hasDocOp2 = true;
      docOp2 = v;
      return this;
    }

    public boolean hasAppliedCcRevision() {
      return _hasAppliedCcRevision;
    }

    @Override
    public int getAppliedCcRevision() {
      return appliedCcRevision;
    }

    public ServerToClientDocOpImpl setAppliedCcRevision(int v) {
      _hasAppliedCcRevision = true;
      appliedCcRevision = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ServerToClientDocOpImpl)) {
        return false;
      }
      ServerToClientDocOpImpl other = (ServerToClientDocOpImpl) o;
      if (this._hasFilePath != other._hasFilePath) {
        return false;
      }
      if (this._hasFilePath) {
        if (!this.filePath.equals(other.filePath)) {
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
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasSelection != other._hasSelection) {
        return false;
      }
      if (this._hasSelection) {
        if (!this.selection.equals(other.selection)) {
          return false;
        }
      }
      if (this._hasDocOp2 != other._hasDocOp2) {
        return false;
      }
      if (this._hasDocOp2) {
        if (!this.docOp2.equals(other.docOp2)) {
          return false;
        }
      }
      if (this._hasAppliedCcRevision != other._hasAppliedCcRevision) {
        return false;
      }
      if (this._hasAppliedCcRevision) {
        if (this.appliedCcRevision != other.appliedCcRevision) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFilePath ? filePath.hashCode() : 0);
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasSelection ? selection.hashCode() : 0);
      hash = hash * 31 + (_hasDocOp2 ? docOp2.hashCode() : 0);
      hash = hash * 31 + (_hasAppliedCcRevision ? java.lang.Integer.valueOf(appliedCcRevision).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement filePathOut = (filePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(filePath);
      result.add("filePath", filePathOut);

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement selectionOut = selection == null ? JsonNull.INSTANCE : selection.toJsonElement();
      result.add("selection", selectionOut);

      JsonElement docOp2Out = docOp2 == null ? JsonNull.INSTANCE : docOp2.toJsonElement();
      result.add("docOp2", docOp2Out);

      JsonPrimitive appliedCcRevisionOut = new JsonPrimitive(appliedCcRevision);
      result.add("appliedCcRevision", appliedCcRevisionOut);
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

    public static ServerToClientDocOpImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ServerToClientDocOpImpl dto = new ServerToClientDocOpImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("filePath")) {
        JsonElement filePathIn = json.get("filePath");
        java.lang.String filePathOut = gson.fromJson(filePathIn, java.lang.String.class);
        dto.setFilePath(filePathOut);
      }

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("selection")) {
        JsonElement selectionIn = json.get("selection");
        DocumentSelectionImpl selectionOut = DocumentSelectionImpl.fromJsonElement(selectionIn);
        dto.setSelection(selectionOut);
      }

      if (json.has("docOp2")) {
        JsonElement docOp2In = json.get("docOp2");
        DocOpImpl docOp2Out = DocOpImpl.fromJsonElement(docOp2In);
        dto.setDocOp2(docOp2Out);
      }

      if (json.has("appliedCcRevision")) {
        JsonElement appliedCcRevisionIn = json.get("appliedCcRevision");
        int appliedCcRevisionOut = appliedCcRevisionIn.getAsInt();
        dto.setAppliedCcRevision(appliedCcRevisionOut);
      }

      return dto;
    }
    public static ServerToClientDocOpImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ServerToClientDocOpsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ServerToClientDocOps, JsonSerializable {

    private ServerToClientDocOpsImpl() {
      super(93);
    }

    protected ServerToClientDocOpsImpl(int type) {
      super(type);
    }

    public static ServerToClientDocOpsImpl make() {
      return new ServerToClientDocOpsImpl();
    }

    protected java.util.List<ServerToClientDocOpImpl> docOps;
    private boolean _hasDocOps;

    public boolean hasDocOps() {
      return _hasDocOps;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.ServerToClientDocOp> getDocOps() {
      ensureDocOps();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(docOps);
    }

    public ServerToClientDocOpsImpl setDocOps(java.util.List<ServerToClientDocOpImpl> v) {
      _hasDocOps = true;
      docOps = v;
      return this;
    }

    public void addDocOps(ServerToClientDocOpImpl v) {
      ensureDocOps();
      docOps.add(v);
    }

    public void clearDocOps() {
      ensureDocOps();
      docOps.clear();
    }

    private void ensureDocOps() {
      if (!_hasDocOps) {
        setDocOps(docOps != null ? docOps : new java.util.ArrayList<ServerToClientDocOpImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ServerToClientDocOpsImpl)) {
        return false;
      }
      ServerToClientDocOpsImpl other = (ServerToClientDocOpsImpl) o;
      if (this._hasDocOps != other._hasDocOps) {
        return false;
      }
      if (this._hasDocOps) {
        if (!this.docOps.equals(other.docOps)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasDocOps ? docOps.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray docOpsOut = new JsonArray();
      ensureDocOps();
      for (ServerToClientDocOpImpl docOps_ : docOps) {
        JsonElement docOpsOut_ = docOps_ == null ? JsonNull.INSTANCE : docOps_.toJsonElement();
        docOpsOut.add(docOpsOut_);
      }
      result.add("docOps", docOpsOut);
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

    public static ServerToClientDocOpsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ServerToClientDocOpsImpl dto = new ServerToClientDocOpsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("docOps")) {
        JsonElement docOpsIn = json.get("docOps");
        java.util.ArrayList<ServerToClientDocOpImpl> docOpsOut = null;
        if (docOpsIn != null && !docOpsIn.isJsonNull()) {
          docOpsOut = new java.util.ArrayList<ServerToClientDocOpImpl>();
          java.util.Iterator<JsonElement> docOpsInIterator = docOpsIn.getAsJsonArray().iterator();
          while (docOpsInIterator.hasNext()) {
            JsonElement docOpsIn_ = docOpsInIterator.next();
            ServerToClientDocOpImpl docOpsOut_ = ServerToClientDocOpImpl.fromJsonElement(docOpsIn_);
            docOpsOut.add(docOpsOut_);
          }
        }
        dto.setDocOps(docOpsOut);
      }

      return dto;
    }
    public static ServerToClientDocOpsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SetActiveProjectImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SetActiveProject, JsonSerializable {

    private SetActiveProjectImpl() {
      super(94);
    }

    protected SetActiveProjectImpl(int type) {
      super(type);
    }

    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public SetActiveProjectImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SetActiveProjectImpl)) {
        return false;
      }
      SetActiveProjectImpl other = (SetActiveProjectImpl) o;
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
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

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

    public static SetActiveProjectImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SetActiveProjectImpl dto = new SetActiveProjectImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static SetActiveProjectImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SetProjectHiddenImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SetProjectHidden, JsonSerializable {

    private SetProjectHiddenImpl() {
      super(95);
    }

    protected SetProjectHiddenImpl(int type) {
      super(type);
    }

    protected boolean isHidden;
    private boolean _hasIsHidden;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasIsHidden() {
      return _hasIsHidden;
    }

    @Override
    public boolean isHidden() {
      return isHidden;
    }

    public SetProjectHiddenImpl setIsHidden(boolean v) {
      _hasIsHidden = true;
      isHidden = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public SetProjectHiddenImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SetProjectHiddenImpl)) {
        return false;
      }
      SetProjectHiddenImpl other = (SetProjectHiddenImpl) o;
      if (this._hasIsHidden != other._hasIsHidden) {
        return false;
      }
      if (this._hasIsHidden) {
        if (this.isHidden != other.isHidden) {
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
      hash = hash * 31 + (_hasIsHidden ? java.lang.Boolean.valueOf(isHidden).hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive isHiddenOut = new JsonPrimitive(isHidden);
      result.add("isHidden", isHiddenOut);

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

    public static SetProjectHiddenImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SetProjectHiddenImpl dto = new SetProjectHiddenImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("isHidden")) {
        JsonElement isHiddenIn = json.get("isHidden");
        boolean isHiddenOut = isHiddenIn.getAsBoolean();
        dto.setIsHidden(isHiddenOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static SetProjectHiddenImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SetProjectRoleImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SetProjectRole, JsonSerializable {

    private SetProjectRoleImpl() {
      super(96);
    }

    protected SetProjectRoleImpl(int type) {
      super(type);
    }

    protected ChangeRoleInfoImpl changeRoleInfo;
    private boolean _hasChangeRoleInfo;
    protected java.lang.String projectId;
    private boolean _hasProjectId;
    protected java.lang.String userId;
    private boolean _hasUserId;

    public boolean hasChangeRoleInfo() {
      return _hasChangeRoleInfo;
    }

    @Override
    public com.google.collide.dto.ChangeRoleInfo getChangeRoleInfo() {
      return changeRoleInfo;
    }

    public SetProjectRoleImpl setChangeRoleInfo(ChangeRoleInfoImpl v) {
      _hasChangeRoleInfo = true;
      changeRoleInfo = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public SetProjectRoleImpl setProjectId(java.lang.String v) {
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

    public SetProjectRoleImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SetProjectRoleImpl)) {
        return false;
      }
      SetProjectRoleImpl other = (SetProjectRoleImpl) o;
      if (this._hasChangeRoleInfo != other._hasChangeRoleInfo) {
        return false;
      }
      if (this._hasChangeRoleInfo) {
        if (!this.changeRoleInfo.equals(other.changeRoleInfo)) {
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
      hash = hash * 31 + (_hasChangeRoleInfo ? changeRoleInfo.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement changeRoleInfoOut = changeRoleInfo == null ? JsonNull.INSTANCE : changeRoleInfo.toJsonElement();
      result.add("changeRoleInfo", changeRoleInfoOut);

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

    public static SetProjectRoleImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SetProjectRoleImpl dto = new SetProjectRoleImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("changeRoleInfo")) {
        JsonElement changeRoleInfoIn = json.get("changeRoleInfo");
        ChangeRoleInfoImpl changeRoleInfoOut = ChangeRoleInfoImpl.fromJsonElement(changeRoleInfoIn);
        dto.setChangeRoleInfo(changeRoleInfoOut);
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
    public static SetProjectRoleImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SetRoleResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SetRoleResponse, JsonSerializable {

    private SetRoleResponseImpl() {
      super(97);
    }

    protected SetRoleResponseImpl(int type) {
      super(type);
    }

    public static SetRoleResponseImpl make() {
      return new SetRoleResponseImpl();
    }

    protected UserDetailsWithRoleImpl updatedUserDetails;
    private boolean _hasUpdatedUserDetails;

    public boolean hasUpdatedUserDetails() {
      return _hasUpdatedUserDetails;
    }

    @Override
    public com.google.collide.dto.UserDetailsWithRole getUpdatedUserDetails() {
      return updatedUserDetails;
    }

    public SetRoleResponseImpl setUpdatedUserDetails(UserDetailsWithRoleImpl v) {
      _hasUpdatedUserDetails = true;
      updatedUserDetails = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SetRoleResponseImpl)) {
        return false;
      }
      SetRoleResponseImpl other = (SetRoleResponseImpl) o;
      if (this._hasUpdatedUserDetails != other._hasUpdatedUserDetails) {
        return false;
      }
      if (this._hasUpdatedUserDetails) {
        if (!this.updatedUserDetails.equals(other.updatedUserDetails)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasUpdatedUserDetails ? updatedUserDetails.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement updatedUserDetailsOut = updatedUserDetails == null ? JsonNull.INSTANCE : updatedUserDetails.toJsonElement();
      result.add("updatedUserDetails", updatedUserDetailsOut);
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

    public static SetRoleResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SetRoleResponseImpl dto = new SetRoleResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("updatedUserDetails")) {
        JsonElement updatedUserDetailsIn = json.get("updatedUserDetails");
        UserDetailsWithRoleImpl updatedUserDetailsOut = UserDetailsWithRoleImpl.fromJsonElement(updatedUserDetailsIn);
        dto.setUpdatedUserDetails(updatedUserDetailsOut);
      }

      return dto;
    }
    public static SetRoleResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SetStagingServerAppIdImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SetStagingServerAppId, JsonSerializable {

    private SetStagingServerAppIdImpl() {
      super(98);
    }

    protected SetStagingServerAppIdImpl(int type) {
      super(type);
    }

    protected java.lang.String stagingServerAppId;
    private boolean _hasStagingServerAppId;

    public boolean hasStagingServerAppId() {
      return _hasStagingServerAppId;
    }

    @Override
    public java.lang.String getStagingServerAppId() {
      return stagingServerAppId;
    }

    public SetStagingServerAppIdImpl setStagingServerAppId(java.lang.String v) {
      _hasStagingServerAppId = true;
      stagingServerAppId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SetStagingServerAppIdImpl)) {
        return false;
      }
      SetStagingServerAppIdImpl other = (SetStagingServerAppIdImpl) o;
      if (this._hasStagingServerAppId != other._hasStagingServerAppId) {
        return false;
      }
      if (this._hasStagingServerAppId) {
        if (!this.stagingServerAppId.equals(other.stagingServerAppId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasStagingServerAppId ? stagingServerAppId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement stagingServerAppIdOut = (stagingServerAppId == null) ? JsonNull.INSTANCE : new JsonPrimitive(stagingServerAppId);
      result.add("stagingServerAppId", stagingServerAppIdOut);
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

    public static SetStagingServerAppIdImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SetStagingServerAppIdImpl dto = new SetStagingServerAppIdImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("stagingServerAppId")) {
        JsonElement stagingServerAppIdIn = json.get("stagingServerAppId");
        java.lang.String stagingServerAppIdOut = gson.fromJson(stagingServerAppIdIn, java.lang.String.class);
        dto.setStagingServerAppId(stagingServerAppIdOut);
      }

      return dto;
    }
    public static SetStagingServerAppIdImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SetWorkspaceArchiveStateImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SetWorkspaceArchiveState, JsonSerializable {

    private SetWorkspaceArchiveStateImpl() {
      super(99);
    }

    protected SetWorkspaceArchiveStateImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String projectId;
    private boolean _hasProjectId;
    protected boolean archive;
    private boolean _hasArchive;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public SetWorkspaceArchiveStateImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public SetWorkspaceArchiveStateImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    public boolean hasArchive() {
      return _hasArchive;
    }

    @Override
    public boolean archive() {
      return archive;
    }

    public SetWorkspaceArchiveStateImpl setArchive(boolean v) {
      _hasArchive = true;
      archive = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SetWorkspaceArchiveStateImpl)) {
        return false;
      }
      SetWorkspaceArchiveStateImpl other = (SetWorkspaceArchiveStateImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
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
      if (this._hasArchive != other._hasArchive) {
        return false;
      }
      if (this._hasArchive) {
        if (this.archive != other.archive) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      hash = hash * 31 + (_hasArchive ? java.lang.Boolean.valueOf(archive).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
      result.add("projectId", projectIdOut);

      JsonPrimitive archiveOut = new JsonPrimitive(archive);
      result.add("archive", archiveOut);
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

    public static SetWorkspaceArchiveStateImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SetWorkspaceArchiveStateImpl dto = new SetWorkspaceArchiveStateImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      if (json.has("archive")) {
        JsonElement archiveIn = json.get("archive");
        boolean archiveOut = archiveIn.getAsBoolean();
        dto.setArchive(archiveOut);
      }

      return dto;
    }
    public static SetWorkspaceArchiveStateImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SetWorkspaceArchiveStateResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SetWorkspaceArchiveStateResponse, JsonSerializable {

    private SetWorkspaceArchiveStateResponseImpl() {
      super(100);
    }

    protected SetWorkspaceArchiveStateResponseImpl(int type) {
      super(type);
    }

    public static SetWorkspaceArchiveStateResponseImpl make() {
      return new SetWorkspaceArchiveStateResponseImpl();
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String archivedTime;
    private boolean _hasArchivedTime;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public SetWorkspaceArchiveStateResponseImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasArchivedTime() {
      return _hasArchivedTime;
    }

    @Override
    public java.lang.String getArchivedTime() {
      return archivedTime;
    }

    public SetWorkspaceArchiveStateResponseImpl setArchivedTime(java.lang.String v) {
      _hasArchivedTime = true;
      archivedTime = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SetWorkspaceArchiveStateResponseImpl)) {
        return false;
      }
      SetWorkspaceArchiveStateResponseImpl other = (SetWorkspaceArchiveStateResponseImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasArchivedTime != other._hasArchivedTime) {
        return false;
      }
      if (this._hasArchivedTime) {
        if (!this.archivedTime.equals(other.archivedTime)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasArchivedTime ? archivedTime.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement archivedTimeOut = (archivedTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(archivedTime);
      result.add("archivedTime", archivedTimeOut);
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

    public static SetWorkspaceArchiveStateResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SetWorkspaceArchiveStateResponseImpl dto = new SetWorkspaceArchiveStateResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("archivedTime")) {
        JsonElement archivedTimeIn = json.get("archivedTime");
        java.lang.String archivedTimeOut = gson.fromJson(archivedTimeIn, java.lang.String.class);
        dto.setArchivedTime(archivedTimeOut);
      }

      return dto;
    }
    public static SetWorkspaceArchiveStateResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SetWorkspaceRoleImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SetWorkspaceRole, JsonSerializable {

    private SetWorkspaceRoleImpl() {
      super(101);
    }

    protected SetWorkspaceRoleImpl(int type) {
      super(type);
    }

    protected ChangeRoleInfoImpl changeRoleInfo;
    private boolean _hasChangeRoleInfo;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String projectId;
    private boolean _hasProjectId;
    protected java.lang.String userId;
    private boolean _hasUserId;

    public boolean hasChangeRoleInfo() {
      return _hasChangeRoleInfo;
    }

    @Override
    public com.google.collide.dto.ChangeRoleInfo getChangeRoleInfo() {
      return changeRoleInfo;
    }

    public SetWorkspaceRoleImpl setChangeRoleInfo(ChangeRoleInfoImpl v) {
      _hasChangeRoleInfo = true;
      changeRoleInfo = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public SetWorkspaceRoleImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public SetWorkspaceRoleImpl setProjectId(java.lang.String v) {
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

    public SetWorkspaceRoleImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SetWorkspaceRoleImpl)) {
        return false;
      }
      SetWorkspaceRoleImpl other = (SetWorkspaceRoleImpl) o;
      if (this._hasChangeRoleInfo != other._hasChangeRoleInfo) {
        return false;
      }
      if (this._hasChangeRoleInfo) {
        if (!this.changeRoleInfo.equals(other.changeRoleInfo)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
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
      hash = hash * 31 + (_hasChangeRoleInfo ? changeRoleInfo.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement changeRoleInfoOut = changeRoleInfo == null ? JsonNull.INSTANCE : changeRoleInfo.toJsonElement();
      result.add("changeRoleInfo", changeRoleInfoOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

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

    public static SetWorkspaceRoleImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SetWorkspaceRoleImpl dto = new SetWorkspaceRoleImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("changeRoleInfo")) {
        JsonElement changeRoleInfoIn = json.get("changeRoleInfo");
        ChangeRoleInfoImpl changeRoleInfoOut = ChangeRoleInfoImpl.fromJsonElement(changeRoleInfoIn);
        dto.setChangeRoleInfo(changeRoleInfoOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
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
    public static SetWorkspaceRoleImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SnippetImpl implements com.google.collide.dto.Snippet, JsonSerializable {

    public static SnippetImpl make() {
      return new SnippetImpl();
    }

    protected java.lang.String snippetText;
    private boolean _hasSnippetText;
    protected int lineNumber;
    private boolean _hasLineNumber;

    public boolean hasSnippetText() {
      return _hasSnippetText;
    }

    @Override
    public java.lang.String getSnippetText() {
      return snippetText;
    }

    public SnippetImpl setSnippetText(java.lang.String v) {
      _hasSnippetText = true;
      snippetText = v;
      return this;
    }

    public boolean hasLineNumber() {
      return _hasLineNumber;
    }

    @Override
    public int getLineNumber() {
      return lineNumber;
    }

    public SnippetImpl setLineNumber(int v) {
      _hasLineNumber = true;
      lineNumber = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof SnippetImpl)) {
        return false;
      }
      SnippetImpl other = (SnippetImpl) o;
      if (this._hasSnippetText != other._hasSnippetText) {
        return false;
      }
      if (this._hasSnippetText) {
        if (!this.snippetText.equals(other.snippetText)) {
          return false;
        }
      }
      if (this._hasLineNumber != other._hasLineNumber) {
        return false;
      }
      if (this._hasLineNumber) {
        if (this.lineNumber != other.lineNumber) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasSnippetText ? snippetText.hashCode() : 0);
      hash = hash * 31 + (_hasLineNumber ? java.lang.Integer.valueOf(lineNumber).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement snippetTextOut = (snippetText == null) ? JsonNull.INSTANCE : new JsonPrimitive(snippetText);
      result.add("snippetText", snippetTextOut);

      JsonPrimitive lineNumberOut = new JsonPrimitive(lineNumber);
      result.add("lineNumber", lineNumberOut);
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

    public static SnippetImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SnippetImpl dto = new SnippetImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("snippetText")) {
        JsonElement snippetTextIn = json.get("snippetText");
        java.lang.String snippetTextOut = gson.fromJson(snippetTextIn, java.lang.String.class);
        dto.setSnippetText(snippetTextOut);
      }

      if (json.has("lineNumber")) {
        JsonElement lineNumberIn = json.get("lineNumber");
        int lineNumberOut = lineNumberIn.getAsInt();
        dto.setLineNumber(lineNumberOut);
      }

      return dto;
    }
    public static SnippetImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class StackTraceElementDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.StackTraceElementDto, JsonSerializable {

    private StackTraceElementDtoImpl() {
      super(103);
    }

    protected StackTraceElementDtoImpl(int type) {
      super(type);
    }

    protected java.lang.String methodName;
    private boolean _hasMethodName;
    protected java.lang.String className;
    private boolean _hasClassName;
    protected java.lang.String fileName;
    private boolean _hasFileName;
    protected int lineNumber;
    private boolean _hasLineNumber;

    public boolean hasMethodName() {
      return _hasMethodName;
    }

    @Override
    public java.lang.String getMethodName() {
      return methodName;
    }

    public StackTraceElementDtoImpl setMethodName(java.lang.String v) {
      _hasMethodName = true;
      methodName = v;
      return this;
    }

    public boolean hasClassName() {
      return _hasClassName;
    }

    @Override
    public java.lang.String getClassName() {
      return className;
    }

    public StackTraceElementDtoImpl setClassName(java.lang.String v) {
      _hasClassName = true;
      className = v;
      return this;
    }

    public boolean hasFileName() {
      return _hasFileName;
    }

    @Override
    public java.lang.String getFileName() {
      return fileName;
    }

    public StackTraceElementDtoImpl setFileName(java.lang.String v) {
      _hasFileName = true;
      fileName = v;
      return this;
    }

    public boolean hasLineNumber() {
      return _hasLineNumber;
    }

    @Override
    public int getLineNumber() {
      return lineNumber;
    }

    public StackTraceElementDtoImpl setLineNumber(int v) {
      _hasLineNumber = true;
      lineNumber = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof StackTraceElementDtoImpl)) {
        return false;
      }
      StackTraceElementDtoImpl other = (StackTraceElementDtoImpl) o;
      if (this._hasMethodName != other._hasMethodName) {
        return false;
      }
      if (this._hasMethodName) {
        if (!this.methodName.equals(other.methodName)) {
          return false;
        }
      }
      if (this._hasClassName != other._hasClassName) {
        return false;
      }
      if (this._hasClassName) {
        if (!this.className.equals(other.className)) {
          return false;
        }
      }
      if (this._hasFileName != other._hasFileName) {
        return false;
      }
      if (this._hasFileName) {
        if (!this.fileName.equals(other.fileName)) {
          return false;
        }
      }
      if (this._hasLineNumber != other._hasLineNumber) {
        return false;
      }
      if (this._hasLineNumber) {
        if (this.lineNumber != other.lineNumber) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasMethodName ? methodName.hashCode() : 0);
      hash = hash * 31 + (_hasClassName ? className.hashCode() : 0);
      hash = hash * 31 + (_hasFileName ? fileName.hashCode() : 0);
      hash = hash * 31 + (_hasLineNumber ? java.lang.Integer.valueOf(lineNumber).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement methodNameOut = (methodName == null) ? JsonNull.INSTANCE : new JsonPrimitive(methodName);
      result.add("methodName", methodNameOut);

      JsonElement classNameOut = (className == null) ? JsonNull.INSTANCE : new JsonPrimitive(className);
      result.add("className", classNameOut);

      JsonElement fileNameOut = (fileName == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileName);
      result.add("fileName", fileNameOut);

      JsonPrimitive lineNumberOut = new JsonPrimitive(lineNumber);
      result.add("lineNumber", lineNumberOut);
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

    public static StackTraceElementDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      StackTraceElementDtoImpl dto = new StackTraceElementDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("methodName")) {
        JsonElement methodNameIn = json.get("methodName");
        java.lang.String methodNameOut = gson.fromJson(methodNameIn, java.lang.String.class);
        dto.setMethodName(methodNameOut);
      }

      if (json.has("className")) {
        JsonElement classNameIn = json.get("className");
        java.lang.String classNameOut = gson.fromJson(classNameIn, java.lang.String.class);
        dto.setClassName(classNameOut);
      }

      if (json.has("fileName")) {
        JsonElement fileNameIn = json.get("fileName");
        java.lang.String fileNameOut = gson.fromJson(fileNameIn, java.lang.String.class);
        dto.setFileName(fileNameOut);
      }

      if (json.has("lineNumber")) {
        JsonElement lineNumberIn = json.get("lineNumber");
        int lineNumberOut = lineNumberIn.getAsInt();
        dto.setLineNumber(lineNumberOut);
      }

      return dto;
    }
    public static StackTraceElementDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SubmitImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.Submit, JsonSerializable {

    private SubmitImpl() {
      super(104);
    }

    protected SubmitImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected java.lang.String workspaceName;
    private boolean _hasWorkspaceName;
    protected java.lang.String workspaceDescription;
    private boolean _hasWorkspaceDescription;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public SubmitImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public SubmitImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasWorkspaceName() {
      return _hasWorkspaceName;
    }

    @Override
    public java.lang.String getWorkspaceName() {
      return workspaceName;
    }

    public SubmitImpl setWorkspaceName(java.lang.String v) {
      _hasWorkspaceName = true;
      workspaceName = v;
      return this;
    }

    public boolean hasWorkspaceDescription() {
      return _hasWorkspaceDescription;
    }

    @Override
    public java.lang.String getWorkspaceDescription() {
      return workspaceDescription;
    }

    public SubmitImpl setWorkspaceDescription(java.lang.String v) {
      _hasWorkspaceDescription = true;
      workspaceDescription = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public SubmitImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SubmitImpl)) {
        return false;
      }
      SubmitImpl other = (SubmitImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasWorkspaceName != other._hasWorkspaceName) {
        return false;
      }
      if (this._hasWorkspaceName) {
        if (!this.workspaceName.equals(other.workspaceName)) {
          return false;
        }
      }
      if (this._hasWorkspaceDescription != other._hasWorkspaceDescription) {
        return false;
      }
      if (this._hasWorkspaceDescription) {
        if (!this.workspaceDescription.equals(other.workspaceDescription)) {
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
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceName ? workspaceName.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceDescription ? workspaceDescription.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement workspaceNameOut = (workspaceName == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceName);
      result.add("workspaceName", workspaceNameOut);

      JsonElement workspaceDescriptionOut = (workspaceDescription == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceDescription);
      result.add("workspaceDescription", workspaceDescriptionOut);

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

    public static SubmitImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SubmitImpl dto = new SubmitImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("workspaceName")) {
        JsonElement workspaceNameIn = json.get("workspaceName");
        java.lang.String workspaceNameOut = gson.fromJson(workspaceNameIn, java.lang.String.class);
        dto.setWorkspaceName(workspaceNameOut);
      }

      if (json.has("workspaceDescription")) {
        JsonElement workspaceDescriptionIn = json.get("workspaceDescription");
        java.lang.String workspaceDescriptionOut = gson.fromJson(workspaceDescriptionIn, java.lang.String.class);
        dto.setWorkspaceDescription(workspaceDescriptionOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static SubmitImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SubmitResponseImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SubmitResponse, JsonSerializable {

    private SubmitResponseImpl() {
      super(105);
    }

    protected SubmitResponseImpl(int type) {
      super(type);
    }

    public static SubmitResponseImpl make() {
      return new SubmitResponseImpl();
    }

    protected java.lang.String submissionTime;
    private boolean _hasSubmissionTime;
    protected UserDetailsImpl submitter;
    private boolean _hasSubmitter;

    public boolean hasSubmissionTime() {
      return _hasSubmissionTime;
    }

    @Override
    public java.lang.String getSubmissionTime() {
      return submissionTime;
    }

    public SubmitResponseImpl setSubmissionTime(java.lang.String v) {
      _hasSubmissionTime = true;
      submissionTime = v;
      return this;
    }

    public boolean hasSubmitter() {
      return _hasSubmitter;
    }

    @Override
    public com.google.collide.dto.UserDetails getSubmitter() {
      return submitter;
    }

    public SubmitResponseImpl setSubmitter(UserDetailsImpl v) {
      _hasSubmitter = true;
      submitter = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SubmitResponseImpl)) {
        return false;
      }
      SubmitResponseImpl other = (SubmitResponseImpl) o;
      if (this._hasSubmissionTime != other._hasSubmissionTime) {
        return false;
      }
      if (this._hasSubmissionTime) {
        if (!this.submissionTime.equals(other.submissionTime)) {
          return false;
        }
      }
      if (this._hasSubmitter != other._hasSubmitter) {
        return false;
      }
      if (this._hasSubmitter) {
        if (!this.submitter.equals(other.submitter)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasSubmissionTime ? submissionTime.hashCode() : 0);
      hash = hash * 31 + (_hasSubmitter ? submitter.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement submissionTimeOut = (submissionTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(submissionTime);
      result.add("submissionTime", submissionTimeOut);

      JsonElement submitterOut = submitter == null ? JsonNull.INSTANCE : submitter.toJsonElement();
      result.add("submitter", submitterOut);
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

    public static SubmitResponseImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SubmitResponseImpl dto = new SubmitResponseImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("submissionTime")) {
        JsonElement submissionTimeIn = json.get("submissionTime");
        java.lang.String submissionTimeOut = gson.fromJson(submissionTimeIn, java.lang.String.class);
        dto.setSubmissionTime(submissionTimeOut);
      }

      if (json.has("submitter")) {
        JsonElement submitterIn = json.get("submitter");
        UserDetailsImpl submitterOut = UserDetailsImpl.fromJsonElement(submitterIn);
        dto.setSubmitter(submitterOut);
      }

      return dto;
    }
    public static SubmitResponseImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SubmittedWorkspaceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.SubmittedWorkspace, JsonSerializable {

    private SubmittedWorkspaceImpl() {
      super(106);
    }

    protected SubmittedWorkspaceImpl(int type) {
      super(type);
    }

    public static SubmittedWorkspaceImpl make() {
      return new SubmittedWorkspaceImpl();
    }


    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SubmittedWorkspaceImpl)) {
        return false;
      }
      SubmittedWorkspaceImpl other = (SubmittedWorkspaceImpl) o;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();
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

    public static SubmittedWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SubmittedWorkspaceImpl dto = new SubmittedWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      return dto;
    }
    public static SubmittedWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SyncImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.Sync, JsonSerializable {

    private SyncImpl() {
      super(107);
    }

    protected SyncImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public SyncImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public SyncImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof SyncImpl)) {
        return false;
      }
      SyncImpl other = (SyncImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static SyncImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SyncImpl dto = new SyncImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static SyncImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class SyncConflictsImpl implements com.google.collide.dto.SyncConflicts, JsonSerializable {

    public static SyncConflictsImpl make() {
      return new SyncConflictsImpl();
    }

    protected java.util.List<NodeConflictDtoImpl> conflicts;
    private boolean _hasConflicts;
    protected java.lang.String nextTangoVersion;
    private boolean _hasNextTangoVersion;

    public boolean hasConflicts() {
      return _hasConflicts;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.NodeConflictDto> getConflicts() {
      ensureConflicts();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(conflicts);
    }

    public SyncConflictsImpl setConflicts(java.util.List<NodeConflictDtoImpl> v) {
      _hasConflicts = true;
      conflicts = v;
      return this;
    }

    public void addConflicts(NodeConflictDtoImpl v) {
      ensureConflicts();
      conflicts.add(v);
    }

    public void clearConflicts() {
      ensureConflicts();
      conflicts.clear();
    }

    private void ensureConflicts() {
      if (!_hasConflicts) {
        setConflicts(conflicts != null ? conflicts : new java.util.ArrayList<NodeConflictDtoImpl>());
      }
    }

    public boolean hasNextTangoVersion() {
      return _hasNextTangoVersion;
    }

    @Override
    public java.lang.String getNextTangoVersion() {
      return nextTangoVersion;
    }

    public SyncConflictsImpl setNextTangoVersion(java.lang.String v) {
      _hasNextTangoVersion = true;
      nextTangoVersion = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof SyncConflictsImpl)) {
        return false;
      }
      SyncConflictsImpl other = (SyncConflictsImpl) o;
      if (this._hasConflicts != other._hasConflicts) {
        return false;
      }
      if (this._hasConflicts) {
        if (!this.conflicts.equals(other.conflicts)) {
          return false;
        }
      }
      if (this._hasNextTangoVersion != other._hasNextTangoVersion) {
        return false;
      }
      if (this._hasNextTangoVersion) {
        if (!this.nextTangoVersion.equals(other.nextTangoVersion)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasConflicts ? conflicts.hashCode() : 0);
      hash = hash * 31 + (_hasNextTangoVersion ? nextTangoVersion.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray conflictsOut = new JsonArray();
      ensureConflicts();
      for (NodeConflictDtoImpl conflicts_ : conflicts) {
        JsonElement conflictsOut_ = conflicts_ == null ? JsonNull.INSTANCE : conflicts_.toJsonElement();
        conflictsOut.add(conflictsOut_);
      }
      result.add("conflicts", conflictsOut);

      JsonElement nextTangoVersionOut = (nextTangoVersion == null) ? JsonNull.INSTANCE : new JsonPrimitive(nextTangoVersion);
      result.add("nextTangoVersion", nextTangoVersionOut);
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

    public static SyncConflictsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      SyncConflictsImpl dto = new SyncConflictsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("conflicts")) {
        JsonElement conflictsIn = json.get("conflicts");
        java.util.ArrayList<NodeConflictDtoImpl> conflictsOut = null;
        if (conflictsIn != null && !conflictsIn.isJsonNull()) {
          conflictsOut = new java.util.ArrayList<NodeConflictDtoImpl>();
          java.util.Iterator<JsonElement> conflictsInIterator = conflictsIn.getAsJsonArray().iterator();
          while (conflictsInIterator.hasNext()) {
            JsonElement conflictsIn_ = conflictsInIterator.next();
            NodeConflictDtoImpl conflictsOut_ = NodeConflictDtoImpl.fromJsonElement(conflictsIn_);
            conflictsOut.add(conflictsOut_);
          }
        }
        dto.setConflicts(conflictsOut);
      }

      if (json.has("nextTangoVersion")) {
        JsonElement nextTangoVersionIn = json.get("nextTangoVersion");
        java.lang.String nextTangoVersionOut = gson.fromJson(nextTangoVersionIn, java.lang.String.class);
        dto.setNextTangoVersion(nextTangoVersionOut);
      }

      return dto;
    }
    public static SyncConflictsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ThrowableDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.ThrowableDto, JsonSerializable {

    private ThrowableDtoImpl() {
      super(108);
    }

    protected ThrowableDtoImpl(int type) {
      super(type);
    }

    public static ThrowableDtoImpl make() {
      return new ThrowableDtoImpl();
    }

    protected ThrowableDtoImpl cause;
    private boolean _hasCause;
    protected java.lang.String message;
    private boolean _hasMessage;
    protected java.util.List<StackTraceElementDtoImpl> stackTrace;
    private boolean _hasStackTrace;
    protected java.lang.String className;
    private boolean _hasClassName;

    public boolean hasCause() {
      return _hasCause;
    }

    @Override
    public com.google.collide.dto.ThrowableDto getCause() {
      return cause;
    }

    public ThrowableDtoImpl setCause(ThrowableDtoImpl v) {
      _hasCause = true;
      cause = v;
      return this;
    }

    public boolean hasMessage() {
      return _hasMessage;
    }

    @Override
    public java.lang.String getMessage() {
      return message;
    }

    public ThrowableDtoImpl setMessage(java.lang.String v) {
      _hasMessage = true;
      message = v;
      return this;
    }

    public boolean hasStackTrace() {
      return _hasStackTrace;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.StackTraceElementDto> getStackTrace() {
      ensureStackTrace();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(stackTrace);
    }

    public ThrowableDtoImpl setStackTrace(java.util.List<StackTraceElementDtoImpl> v) {
      _hasStackTrace = true;
      stackTrace = v;
      return this;
    }

    public void addStackTrace(StackTraceElementDtoImpl v) {
      ensureStackTrace();
      stackTrace.add(v);
    }

    public void clearStackTrace() {
      ensureStackTrace();
      stackTrace.clear();
    }

    private void ensureStackTrace() {
      if (!_hasStackTrace) {
        setStackTrace(stackTrace != null ? stackTrace : new java.util.ArrayList<StackTraceElementDtoImpl>());
      }
    }

    public boolean hasClassName() {
      return _hasClassName;
    }

    @Override
    public java.lang.String className() {
      return className;
    }

    public ThrowableDtoImpl setClassName(java.lang.String v) {
      _hasClassName = true;
      className = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ThrowableDtoImpl)) {
        return false;
      }
      ThrowableDtoImpl other = (ThrowableDtoImpl) o;
      if (this._hasCause != other._hasCause) {
        return false;
      }
      if (this._hasCause) {
        if (!this.cause.equals(other.cause)) {
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
      if (this._hasStackTrace != other._hasStackTrace) {
        return false;
      }
      if (this._hasStackTrace) {
        if (!this.stackTrace.equals(other.stackTrace)) {
          return false;
        }
      }
      if (this._hasClassName != other._hasClassName) {
        return false;
      }
      if (this._hasClassName) {
        if (!this.className.equals(other.className)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasCause ? cause.hashCode() : 0);
      hash = hash * 31 + (_hasMessage ? message.hashCode() : 0);
      hash = hash * 31 + (_hasStackTrace ? stackTrace.hashCode() : 0);
      hash = hash * 31 + (_hasClassName ? className.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement causeOut = cause == null ? JsonNull.INSTANCE : cause.toJsonElement();
      result.add("cause", causeOut);

      JsonElement messageOut = (message == null) ? JsonNull.INSTANCE : new JsonPrimitive(message);
      result.add("message", messageOut);

      JsonArray stackTraceOut = new JsonArray();
      ensureStackTrace();
      for (StackTraceElementDtoImpl stackTrace_ : stackTrace) {
        JsonElement stackTraceOut_ = stackTrace_ == null ? JsonNull.INSTANCE : stackTrace_.toJsonElement();
        stackTraceOut.add(stackTraceOut_);
      }
      result.add("stackTrace", stackTraceOut);

      JsonElement classNameOut = (className == null) ? JsonNull.INSTANCE : new JsonPrimitive(className);
      result.add("className", classNameOut);
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

    public static ThrowableDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ThrowableDtoImpl dto = new ThrowableDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("cause")) {
        JsonElement causeIn = json.get("cause");
        ThrowableDtoImpl causeOut = ThrowableDtoImpl.fromJsonElement(causeIn);
        dto.setCause(causeOut);
      }

      if (json.has("message")) {
        JsonElement messageIn = json.get("message");
        java.lang.String messageOut = gson.fromJson(messageIn, java.lang.String.class);
        dto.setMessage(messageOut);
      }

      if (json.has("stackTrace")) {
        JsonElement stackTraceIn = json.get("stackTrace");
        java.util.ArrayList<StackTraceElementDtoImpl> stackTraceOut = null;
        if (stackTraceIn != null && !stackTraceIn.isJsonNull()) {
          stackTraceOut = new java.util.ArrayList<StackTraceElementDtoImpl>();
          java.util.Iterator<JsonElement> stackTraceInIterator = stackTraceIn.getAsJsonArray().iterator();
          while (stackTraceInIterator.hasNext()) {
            JsonElement stackTraceIn_ = stackTraceInIterator.next();
            StackTraceElementDtoImpl stackTraceOut_ = StackTraceElementDtoImpl.fromJsonElement(stackTraceIn_);
            stackTraceOut.add(stackTraceOut_);
          }
        }
        dto.setStackTrace(stackTraceOut);
      }

      if (json.has("className")) {
        JsonElement classNameIn = json.get("className");
        java.lang.String classNameOut = gson.fromJson(classNameIn, java.lang.String.class);
        dto.setClassName(classNameOut);
      }

      return dto;
    }
    public static ThrowableDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class TreeNodeInfoImpl implements com.google.collide.dto.TreeNodeInfo, JsonSerializable {

    public static TreeNodeInfoImpl make() {
      return new TreeNodeInfoImpl();
    }

    protected java.lang.String fileEditSessionKey;
    private boolean _hasFileEditSessionKey;
    protected int nodeType;
    private boolean _hasNodeType;
    protected java.lang.String name;
    private boolean _hasName;

    public boolean hasFileEditSessionKey() {
      return _hasFileEditSessionKey;
    }

    @Override
    public java.lang.String getFileEditSessionKey() {
      return fileEditSessionKey;
    }

    public TreeNodeInfoImpl setFileEditSessionKey(java.lang.String v) {
      _hasFileEditSessionKey = true;
      fileEditSessionKey = v;
      return this;
    }

    public boolean hasNodeType() {
      return _hasNodeType;
    }

    @Override
    public int getNodeType() {
      return nodeType;
    }

    public TreeNodeInfoImpl setNodeType(int v) {
      _hasNodeType = true;
      nodeType = v;
      return this;
    }

    public boolean hasName() {
      return _hasName;
    }

    @Override
    public java.lang.String getName() {
      return name;
    }

    public TreeNodeInfoImpl setName(java.lang.String v) {
      _hasName = true;
      name = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof TreeNodeInfoImpl)) {
        return false;
      }
      TreeNodeInfoImpl other = (TreeNodeInfoImpl) o;
      if (this._hasFileEditSessionKey != other._hasFileEditSessionKey) {
        return false;
      }
      if (this._hasFileEditSessionKey) {
        if (!this.fileEditSessionKey.equals(other.fileEditSessionKey)) {
          return false;
        }
      }
      if (this._hasNodeType != other._hasNodeType) {
        return false;
      }
      if (this._hasNodeType) {
        if (this.nodeType != other.nodeType) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasFileEditSessionKey ? fileEditSessionKey.hashCode() : 0);
      hash = hash * 31 + (_hasNodeType ? java.lang.Integer.valueOf(nodeType).hashCode() : 0);
      hash = hash * 31 + (_hasName ? name.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileEditSessionKeyOut = (fileEditSessionKey == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileEditSessionKey);
      result.add("fileEditSessionKey", fileEditSessionKeyOut);

      JsonPrimitive nodeTypeOut = new JsonPrimitive(nodeType);
      result.add("nodeType", nodeTypeOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);
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

    public static TreeNodeInfoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      TreeNodeInfoImpl dto = new TreeNodeInfoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileEditSessionKey")) {
        JsonElement fileEditSessionKeyIn = json.get("fileEditSessionKey");
        java.lang.String fileEditSessionKeyOut = gson.fromJson(fileEditSessionKeyIn, java.lang.String.class);
        dto.setFileEditSessionKey(fileEditSessionKeyOut);
      }

      if (json.has("nodeType")) {
        JsonElement nodeTypeIn = json.get("nodeType");
        int nodeTypeOut = nodeTypeIn.getAsInt();
        dto.setNodeType(nodeTypeOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      return dto;
    }
    public static TreeNodeInfoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class TypeAssociationImpl extends CodeBlockAssociationImpl implements com.google.collide.dto.TypeAssociation, JsonSerializable {

    private TypeAssociationImpl() {
      super(109);
    }

    protected TypeAssociationImpl(int type) {
      super(type);
    }

    public static TypeAssociationImpl make() {
      return new TypeAssociationImpl();
    }


    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof TypeAssociationImpl)) {
        return false;
      }
      TypeAssociationImpl other = (TypeAssociationImpl) o;
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonArray result = new JsonArray();

      JsonElement sourceFileIdOut = (sourceFileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sourceFileId);
      result.add(sourceFileIdOut);

      JsonElement sourceLocalIdOut = (sourceLocalId == null) ? JsonNull.INSTANCE : new JsonPrimitive(sourceLocalId);
      result.add(sourceLocalIdOut);

      JsonElement targetFileIdOut = (targetFileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetFileId);
      result.add(targetFileIdOut);

      JsonElement targetLocalIdOut = (targetLocalId == null) ? JsonNull.INSTANCE : new JsonPrimitive(targetLocalId);
      result.add(targetLocalIdOut);

      JsonPrimitive isRootAssociationOut = new JsonPrimitive(isRootAssociation);
      result.add(isRootAssociationOut);
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

    public static TypeAssociationImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      TypeAssociationImpl dto = new TypeAssociationImpl();
      JsonArray json = jsonElem.getAsJsonArray();

      if (0 < json.size()) {
        JsonElement sourceFileIdIn = json.get(0);
        java.lang.String sourceFileIdOut = gson.fromJson(sourceFileIdIn, java.lang.String.class);
        dto.setSourceFileId(sourceFileIdOut);
      }

      if (1 < json.size()) {
        JsonElement sourceLocalIdIn = json.get(1);
        java.lang.String sourceLocalIdOut = gson.fromJson(sourceLocalIdIn, java.lang.String.class);
        dto.setSourceLocalId(sourceLocalIdOut);
      }

      if (2 < json.size()) {
        JsonElement targetFileIdIn = json.get(2);
        java.lang.String targetFileIdOut = gson.fromJson(targetFileIdIn, java.lang.String.class);
        dto.setTargetFileId(targetFileIdOut);
      }

      if (3 < json.size()) {
        JsonElement targetLocalIdIn = json.get(3);
        java.lang.String targetLocalIdOut = gson.fromJson(targetLocalIdIn, java.lang.String.class);
        dto.setTargetLocalId(targetLocalIdOut);
      }

      if (4 < json.size()) {
        JsonElement isRootAssociationIn = json.get(4);
        boolean isRootAssociationOut = isRootAssociationIn.getAsBoolean();
        dto.setIsRootAssociation(isRootAssociationOut);
      }

      return dto;
    }
    public static TypeAssociationImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UndoLastSyncImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.UndoLastSync, JsonSerializable {

    private UndoLastSyncImpl() {
      super(110);
    }

    protected UndoLastSyncImpl(int type) {
      super(type);
    }

    protected java.lang.String clientId;
    private boolean _hasClientId;
    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;

    public boolean hasClientId() {
      return _hasClientId;
    }

    @Override
    public java.lang.String getClientId() {
      return clientId;
    }

    public UndoLastSyncImpl setClientId(java.lang.String v) {
      _hasClientId = true;
      clientId = v;
      return this;
    }

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public UndoLastSyncImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UndoLastSyncImpl)) {
        return false;
      }
      UndoLastSyncImpl other = (UndoLastSyncImpl) o;
      if (this._hasClientId != other._hasClientId) {
        return false;
      }
      if (this._hasClientId) {
        if (!this.clientId.equals(other.clientId)) {
          return false;
        }
      }
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasClientId ? clientId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement clientIdOut = (clientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(clientId);
      result.add("clientId", clientIdOut);

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);
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

    public static UndoLastSyncImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UndoLastSyncImpl dto = new UndoLastSyncImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("clientId")) {
        JsonElement clientIdIn = json.get("clientId");
        java.lang.String clientIdOut = gson.fromJson(clientIdIn, java.lang.String.class);
        dto.setClientId(clientIdOut);
      }

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      return dto;
    }
    public static UndoLastSyncImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UpdateProjectImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.UpdateProject, JsonSerializable {

    private UpdateProjectImpl() {
      super(111);
    }

    protected UpdateProjectImpl(int type) {
      super(type);
    }

    protected java.lang.String summary;
    private boolean _hasSummary;
    protected java.lang.String name;
    private boolean _hasName;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasSummary() {
      return _hasSummary;
    }

    @Override
    public java.lang.String getSummary() {
      return summary;
    }

    public UpdateProjectImpl setSummary(java.lang.String v) {
      _hasSummary = true;
      summary = v;
      return this;
    }

    public boolean hasName() {
      return _hasName;
    }

    @Override
    public java.lang.String getName() {
      return name;
    }

    public UpdateProjectImpl setName(java.lang.String v) {
      _hasName = true;
      name = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public UpdateProjectImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UpdateProjectImpl)) {
        return false;
      }
      UpdateProjectImpl other = (UpdateProjectImpl) o;
      if (this._hasSummary != other._hasSummary) {
        return false;
      }
      if (this._hasSummary) {
        if (!this.summary.equals(other.summary)) {
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
      hash = hash * 31 + (_hasSummary ? summary.hashCode() : 0);
      hash = hash * 31 + (_hasName ? name.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement summaryOut = (summary == null) ? JsonNull.INSTANCE : new JsonPrimitive(summary);
      result.add("summary", summaryOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);

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

    public static UpdateProjectImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UpdateProjectImpl dto = new UpdateProjectImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("summary")) {
        JsonElement summaryIn = json.get("summary");
        java.lang.String summaryOut = gson.fromJson(summaryIn, java.lang.String.class);
        dto.setSummary(summaryOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static UpdateProjectImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UpdateUserWorkspaceMetadataImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.UpdateUserWorkspaceMetadata, JsonSerializable {

    private UpdateUserWorkspaceMetadataImpl() {
      super(112);
    }

    protected UpdateUserWorkspaceMetadataImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected GetWorkspaceMetaDataResponseImpl userWorkspaceMetadata;
    private boolean _hasUserWorkspaceMetadata;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public UpdateUserWorkspaceMetadataImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasUserWorkspaceMetadata() {
      return _hasUserWorkspaceMetadata;
    }

    @Override
    public com.google.collide.dto.GetWorkspaceMetaDataResponse getUserWorkspaceMetadata() {
      return userWorkspaceMetadata;
    }

    public UpdateUserWorkspaceMetadataImpl setUserWorkspaceMetadata(GetWorkspaceMetaDataResponseImpl v) {
      _hasUserWorkspaceMetadata = true;
      userWorkspaceMetadata = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UpdateUserWorkspaceMetadataImpl)) {
        return false;
      }
      UpdateUserWorkspaceMetadataImpl other = (UpdateUserWorkspaceMetadataImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasUserWorkspaceMetadata != other._hasUserWorkspaceMetadata) {
        return false;
      }
      if (this._hasUserWorkspaceMetadata) {
        if (!this.userWorkspaceMetadata.equals(other.userWorkspaceMetadata)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasUserWorkspaceMetadata ? userWorkspaceMetadata.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement userWorkspaceMetadataOut = userWorkspaceMetadata == null ? JsonNull.INSTANCE : userWorkspaceMetadata.toJsonElement();
      result.add("userWorkspaceMetadata", userWorkspaceMetadataOut);
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

    public static UpdateUserWorkspaceMetadataImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UpdateUserWorkspaceMetadataImpl dto = new UpdateUserWorkspaceMetadataImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("userWorkspaceMetadata")) {
        JsonElement userWorkspaceMetadataIn = json.get("userWorkspaceMetadata");
        GetWorkspaceMetaDataResponseImpl userWorkspaceMetadataOut = GetWorkspaceMetaDataResponseImpl.fromJsonElement(userWorkspaceMetadataIn);
        dto.setUserWorkspaceMetadata(userWorkspaceMetadataOut);
      }

      return dto;
    }
    public static UpdateUserWorkspaceMetadataImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UpdateWorkspaceImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.UpdateWorkspace, JsonSerializable {

    private UpdateWorkspaceImpl() {
      super(113);
    }

    protected UpdateWorkspaceImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected WorkspaceInfoImpl workspaceUpdates;
    private boolean _hasWorkspaceUpdates;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public UpdateWorkspaceImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasWorkspaceUpdates() {
      return _hasWorkspaceUpdates;
    }

    @Override
    public com.google.collide.dto.WorkspaceInfo getWorkspaceUpdates() {
      return workspaceUpdates;
    }

    public UpdateWorkspaceImpl setWorkspaceUpdates(WorkspaceInfoImpl v) {
      _hasWorkspaceUpdates = true;
      workspaceUpdates = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public UpdateWorkspaceImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UpdateWorkspaceImpl)) {
        return false;
      }
      UpdateWorkspaceImpl other = (UpdateWorkspaceImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasWorkspaceUpdates != other._hasWorkspaceUpdates) {
        return false;
      }
      if (this._hasWorkspaceUpdates) {
        if (!this.workspaceUpdates.equals(other.workspaceUpdates)) {
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
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceUpdates ? workspaceUpdates.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement workspaceUpdatesOut = workspaceUpdates == null ? JsonNull.INSTANCE : workspaceUpdates.toJsonElement();
      result.add("workspaceUpdates", workspaceUpdatesOut);

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

    public static UpdateWorkspaceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UpdateWorkspaceImpl dto = new UpdateWorkspaceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("workspaceUpdates")) {
        JsonElement workspaceUpdatesIn = json.get("workspaceUpdates");
        WorkspaceInfoImpl workspaceUpdatesOut = WorkspaceInfoImpl.fromJsonElement(workspaceUpdatesIn);
        dto.setWorkspaceUpdates(workspaceUpdatesOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static UpdateWorkspaceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UpdateWorkspaceRunTargetsImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.UpdateWorkspaceRunTargets, JsonSerializable {

    private UpdateWorkspaceRunTargetsImpl() {
      super(114);
    }

    protected UpdateWorkspaceRunTargetsImpl(int type) {
      super(type);
    }

    protected java.lang.String workspaceId;
    private boolean _hasWorkspaceId;
    protected RunTargetImpl runTarget;
    private boolean _hasRunTarget;
    protected java.lang.String projectId;
    private boolean _hasProjectId;

    public boolean hasWorkspaceId() {
      return _hasWorkspaceId;
    }

    @Override
    public java.lang.String getWorkspaceId() {
      return workspaceId;
    }

    public UpdateWorkspaceRunTargetsImpl setWorkspaceId(java.lang.String v) {
      _hasWorkspaceId = true;
      workspaceId = v;
      return this;
    }

    public boolean hasRunTarget() {
      return _hasRunTarget;
    }

    @Override
    public com.google.collide.dto.RunTarget getRunTarget() {
      return runTarget;
    }

    public UpdateWorkspaceRunTargetsImpl setRunTarget(RunTargetImpl v) {
      _hasRunTarget = true;
      runTarget = v;
      return this;
    }

    public boolean hasProjectId() {
      return _hasProjectId;
    }

    @Override
    public java.lang.String getProjectId() {
      return projectId;
    }

    public UpdateWorkspaceRunTargetsImpl setProjectId(java.lang.String v) {
      _hasProjectId = true;
      projectId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UpdateWorkspaceRunTargetsImpl)) {
        return false;
      }
      UpdateWorkspaceRunTargetsImpl other = (UpdateWorkspaceRunTargetsImpl) o;
      if (this._hasWorkspaceId != other._hasWorkspaceId) {
        return false;
      }
      if (this._hasWorkspaceId) {
        if (!this.workspaceId.equals(other.workspaceId)) {
          return false;
        }
      }
      if (this._hasRunTarget != other._hasRunTarget) {
        return false;
      }
      if (this._hasRunTarget) {
        if (!this.runTarget.equals(other.runTarget)) {
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
      hash = hash * 31 + (_hasWorkspaceId ? workspaceId.hashCode() : 0);
      hash = hash * 31 + (_hasRunTarget ? runTarget.hashCode() : 0);
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement workspaceIdOut = (workspaceId == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceId);
      result.add("workspaceId", workspaceIdOut);

      JsonElement runTargetOut = runTarget == null ? JsonNull.INSTANCE : runTarget.toJsonElement();
      result.add("runTarget", runTargetOut);

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

    public static UpdateWorkspaceRunTargetsImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UpdateWorkspaceRunTargetsImpl dto = new UpdateWorkspaceRunTargetsImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("workspaceId")) {
        JsonElement workspaceIdIn = json.get("workspaceId");
        java.lang.String workspaceIdOut = gson.fromJson(workspaceIdIn, java.lang.String.class);
        dto.setWorkspaceId(workspaceIdOut);
      }

      if (json.has("runTarget")) {
        JsonElement runTargetIn = json.get("runTarget");
        RunTargetImpl runTargetOut = RunTargetImpl.fromJsonElement(runTargetIn);
        dto.setRunTarget(runTargetOut);
      }

      if (json.has("projectId")) {
        JsonElement projectIdIn = json.get("projectId");
        java.lang.String projectIdOut = gson.fromJson(projectIdIn, java.lang.String.class);
        dto.setProjectId(projectIdOut);
      }

      return dto;
    }
    public static UpdateWorkspaceRunTargetsImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UserDetailsImpl implements com.google.collide.dto.UserDetails, JsonSerializable {

    public static UserDetailsImpl make() {
      return new UserDetailsImpl();
    }

    protected java.lang.String givenName;
    private boolean _hasGivenName;
    protected java.lang.String displayEmail;
    private boolean _hasDisplayEmail;
    protected java.lang.String portraitUrl;
    private boolean _hasPortraitUrl;
    protected boolean isCurrentUser;
    private boolean _hasIsCurrentUser;
    protected java.lang.String displayName;
    private boolean _hasDisplayName;
    protected java.lang.String userId;
    private boolean _hasUserId;

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
      UserDetailsImpl other = (UserDetailsImpl) o;
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

  public static class UserDetailsWithRoleImpl extends UserDetailsImpl implements com.google.collide.dto.UserDetailsWithRole, JsonSerializable {

    public static UserDetailsWithRoleImpl make() {
      return new UserDetailsWithRoleImpl();
    }

    protected boolean isCreator;
    private boolean _hasIsCreator;
    protected com.google.collide.dto.Role role;
    private boolean _hasRole;

    public boolean hasIsCreator() {
      return _hasIsCreator;
    }

    @Override
    public boolean isCreator() {
      return isCreator;
    }

    public UserDetailsWithRoleImpl setIsCreator(boolean v) {
      _hasIsCreator = true;
      isCreator = v;
      return this;
    }

    public boolean hasRole() {
      return _hasRole;
    }

    @Override
    public com.google.collide.dto.Role getRole() {
      return role;
    }

    public UserDetailsWithRoleImpl setRole(com.google.collide.dto.Role v) {
      _hasRole = true;
      role = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UserDetailsWithRoleImpl)) {
        return false;
      }
      UserDetailsWithRoleImpl other = (UserDetailsWithRoleImpl) o;
      if (this._hasIsCreator != other._hasIsCreator) {
        return false;
      }
      if (this._hasIsCreator) {
        if (this.isCreator != other.isCreator) {
          return false;
        }
      }
      if (this._hasRole != other._hasRole) {
        return false;
      }
      if (this._hasRole) {
        if (!this.role.equals(other.role)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasIsCreator ? java.lang.Boolean.valueOf(isCreator).hashCode() : 0);
      hash = hash * 31 + (_hasRole ? role.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive isCreatorOut = new JsonPrimitive(isCreator);
      result.add("isCreator", isCreatorOut);

      JsonElement roleOut = (role == null) ? JsonNull.INSTANCE : new JsonPrimitive(role.name());
      result.add("role", roleOut);

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

    public static UserDetailsWithRoleImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UserDetailsWithRoleImpl dto = new UserDetailsWithRoleImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("isCreator")) {
        JsonElement isCreatorIn = json.get("isCreator");
        boolean isCreatorOut = isCreatorIn.getAsBoolean();
        dto.setIsCreator(isCreatorOut);
      }

      if (json.has("role")) {
        JsonElement roleIn = json.get("role");
        com.google.collide.dto.Role roleOut = gson.fromJson(roleIn, com.google.collide.dto.Role.class);
        dto.setRole(roleOut);
      }

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
    public static UserDetailsWithRoleImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UserLogInDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.UserLogInDto, JsonSerializable {

    private UserLogInDtoImpl() {
      super(131);
    }

    protected UserLogInDtoImpl(int type) {
      super(type);
    }

    public static UserLogInDtoImpl make() {
      return new UserLogInDtoImpl();
    }

    protected ParticipantUserDetailsImpl participant;
    private boolean _hasParticipant;

    public boolean hasParticipant() {
      return _hasParticipant;
    }

    @Override
    public com.google.collide.dto.ParticipantUserDetails getParticipant() {
      return participant;
    }

    public UserLogInDtoImpl setParticipant(ParticipantUserDetailsImpl v) {
      _hasParticipant = true;
      participant = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UserLogInDtoImpl)) {
        return false;
      }
      UserLogInDtoImpl other = (UserLogInDtoImpl) o;
      if (this._hasParticipant != other._hasParticipant) {
        return false;
      }
      if (this._hasParticipant) {
        if (!this.participant.equals(other.participant)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasParticipant ? participant.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement participantOut = participant == null ? JsonNull.INSTANCE : participant.toJsonElement();
      result.add("participant", participantOut);
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

    public static UserLogInDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UserLogInDtoImpl dto = new UserLogInDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("participant")) {
        JsonElement participantIn = json.get("participant");
        ParticipantUserDetailsImpl participantOut = ParticipantUserDetailsImpl.fromJsonElement(participantIn);
        dto.setParticipant(participantOut);
      }

      return dto;
    }
    public static UserLogInDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UserLogOutDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.UserLogOutDto, JsonSerializable {

    private UserLogOutDtoImpl() {
      super(132);
    }

    protected UserLogOutDtoImpl(int type) {
      super(type);
    }

    public static UserLogOutDtoImpl make() {
      return new UserLogOutDtoImpl();
    }

    protected ParticipantImpl participant;
    private boolean _hasParticipant;

    public boolean hasParticipant() {
      return _hasParticipant;
    }

    @Override
    public com.google.collide.dto.Participant getParticipant() {
      return participant;
    }

    public UserLogOutDtoImpl setParticipant(ParticipantImpl v) {
      _hasParticipant = true;
      participant = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UserLogOutDtoImpl)) {
        return false;
      }
      UserLogOutDtoImpl other = (UserLogOutDtoImpl) o;
      if (this._hasParticipant != other._hasParticipant) {
        return false;
      }
      if (this._hasParticipant) {
        if (!this.participant.equals(other.participant)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasParticipant ? participant.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement participantOut = participant == null ? JsonNull.INSTANCE : participant.toJsonElement();
      result.add("participant", participantOut);
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

    public static UserLogOutDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UserLogOutDtoImpl dto = new UserLogOutDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("participant")) {
        JsonElement participantIn = json.get("participant");
        ParticipantImpl participantOut = ParticipantImpl.fromJsonElement(participantIn);
        dto.setParticipant(participantOut);
      }

      return dto;
    }
    public static UserLogOutDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class WorkspaceInfoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.WorkspaceInfo, JsonSerializable {

    private WorkspaceInfoImpl() {
      super(116);
    }

    protected WorkspaceInfoImpl(int type) {
      super(type);
    }

    public static WorkspaceInfoImpl make() {
      return new WorkspaceInfoImpl();
    }

    protected java.lang.String owningProjectId;
    private boolean _hasOwningProjectId;
    protected java.lang.String createdTime;
    private boolean _hasCreatedTime;
    protected java.lang.String archivedTime;
    private boolean _hasArchivedTime;
    protected java.lang.String sortTime;
    private boolean _hasSortTime;
    protected com.google.collide.dto.Role currentUserRole;
    private boolean _hasCurrentUserRole;
    protected java.lang.String parentId;
    private boolean _hasParentId;
    protected java.lang.String submissionTime;
    private boolean _hasSubmissionTime;
    protected com.google.collide.dto.Visibility visibility;
    private boolean _hasVisibility;
    protected RunTargetImpl runTarget;
    private boolean _hasRunTarget;
    protected com.google.collide.dto.WorkspaceInfo.WorkspaceType workspaceType;
    private boolean _hasWorkspaceType;
    protected UserDetailsImpl submitter;
    private boolean _hasSubmitter;
    protected com.google.collide.dto.Role currentUserRoleForParent;
    private boolean _hasCurrentUserRoleForParent;
    protected java.lang.String name;
    private boolean _hasName;
    protected java.lang.String id;
    private boolean _hasId;
    protected java.lang.String description;
    private boolean _hasDescription;

    public boolean hasOwningProjectId() {
      return _hasOwningProjectId;
    }

    @Override
    public java.lang.String getOwningProjectId() {
      return owningProjectId;
    }

    public WorkspaceInfoImpl setOwningProjectId(java.lang.String v) {
      _hasOwningProjectId = true;
      owningProjectId = v;
      return this;
    }

    public boolean hasCreatedTime() {
      return _hasCreatedTime;
    }

    @Override
    public java.lang.String getCreatedTime() {
      return createdTime;
    }

    public WorkspaceInfoImpl setCreatedTime(java.lang.String v) {
      _hasCreatedTime = true;
      createdTime = v;
      return this;
    }

    public boolean hasArchivedTime() {
      return _hasArchivedTime;
    }

    @Override
    public java.lang.String getArchivedTime() {
      return archivedTime;
    }

    public WorkspaceInfoImpl setArchivedTime(java.lang.String v) {
      _hasArchivedTime = true;
      archivedTime = v;
      return this;
    }

    public boolean hasSortTime() {
      return _hasSortTime;
    }

    @Override
    public java.lang.String getSortTime() {
      return sortTime;
    }

    public WorkspaceInfoImpl setSortTime(java.lang.String v) {
      _hasSortTime = true;
      sortTime = v;
      return this;
    }

    public boolean hasCurrentUserRole() {
      return _hasCurrentUserRole;
    }

    @Override
    public com.google.collide.dto.Role getCurrentUserRole() {
      return currentUserRole;
    }

    public WorkspaceInfoImpl setCurrentUserRole(com.google.collide.dto.Role v) {
      _hasCurrentUserRole = true;
      currentUserRole = v;
      return this;
    }

    public boolean hasParentId() {
      return _hasParentId;
    }

    @Override
    public java.lang.String getParentId() {
      return parentId;
    }

    public WorkspaceInfoImpl setParentId(java.lang.String v) {
      _hasParentId = true;
      parentId = v;
      return this;
    }

    public boolean hasSubmissionTime() {
      return _hasSubmissionTime;
    }

    @Override
    public java.lang.String getSubmissionTime() {
      return submissionTime;
    }

    public WorkspaceInfoImpl setSubmissionTime(java.lang.String v) {
      _hasSubmissionTime = true;
      submissionTime = v;
      return this;
    }

    public boolean hasVisibility() {
      return _hasVisibility;
    }

    @Override
    public com.google.collide.dto.Visibility getVisibility() {
      return visibility;
    }

    public WorkspaceInfoImpl setVisibility(com.google.collide.dto.Visibility v) {
      _hasVisibility = true;
      visibility = v;
      return this;
    }

    public boolean hasRunTarget() {
      return _hasRunTarget;
    }

    @Override
    public com.google.collide.dto.RunTarget getRunTarget() {
      return runTarget;
    }

    public WorkspaceInfoImpl setRunTarget(RunTargetImpl v) {
      _hasRunTarget = true;
      runTarget = v;
      return this;
    }

    public boolean hasWorkspaceType() {
      return _hasWorkspaceType;
    }

    @Override
    public com.google.collide.dto.WorkspaceInfo.WorkspaceType getWorkspaceType() {
      return workspaceType;
    }

    public WorkspaceInfoImpl setWorkspaceType(com.google.collide.dto.WorkspaceInfo.WorkspaceType v) {
      _hasWorkspaceType = true;
      workspaceType = v;
      return this;
    }

    public boolean hasSubmitter() {
      return _hasSubmitter;
    }

    @Override
    public com.google.collide.dto.UserDetails getSubmitter() {
      return submitter;
    }

    public WorkspaceInfoImpl setSubmitter(UserDetailsImpl v) {
      _hasSubmitter = true;
      submitter = v;
      return this;
    }

    public boolean hasCurrentUserRoleForParent() {
      return _hasCurrentUserRoleForParent;
    }

    @Override
    public com.google.collide.dto.Role getCurrentUserRoleForParent() {
      return currentUserRoleForParent;
    }

    public WorkspaceInfoImpl setCurrentUserRoleForParent(com.google.collide.dto.Role v) {
      _hasCurrentUserRoleForParent = true;
      currentUserRoleForParent = v;
      return this;
    }

    public boolean hasName() {
      return _hasName;
    }

    @Override
    public java.lang.String getName() {
      return name;
    }

    public WorkspaceInfoImpl setName(java.lang.String v) {
      _hasName = true;
      name = v;
      return this;
    }

    public boolean hasId() {
      return _hasId;
    }

    @Override
    public java.lang.String getId() {
      return id;
    }

    public WorkspaceInfoImpl setId(java.lang.String v) {
      _hasId = true;
      id = v;
      return this;
    }

    public boolean hasDescription() {
      return _hasDescription;
    }

    @Override
    public java.lang.String getDescription() {
      return description;
    }

    public WorkspaceInfoImpl setDescription(java.lang.String v) {
      _hasDescription = true;
      description = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof WorkspaceInfoImpl)) {
        return false;
      }
      WorkspaceInfoImpl other = (WorkspaceInfoImpl) o;
      if (this._hasOwningProjectId != other._hasOwningProjectId) {
        return false;
      }
      if (this._hasOwningProjectId) {
        if (!this.owningProjectId.equals(other.owningProjectId)) {
          return false;
        }
      }
      if (this._hasCreatedTime != other._hasCreatedTime) {
        return false;
      }
      if (this._hasCreatedTime) {
        if (!this.createdTime.equals(other.createdTime)) {
          return false;
        }
      }
      if (this._hasArchivedTime != other._hasArchivedTime) {
        return false;
      }
      if (this._hasArchivedTime) {
        if (!this.archivedTime.equals(other.archivedTime)) {
          return false;
        }
      }
      if (this._hasSortTime != other._hasSortTime) {
        return false;
      }
      if (this._hasSortTime) {
        if (!this.sortTime.equals(other.sortTime)) {
          return false;
        }
      }
      if (this._hasCurrentUserRole != other._hasCurrentUserRole) {
        return false;
      }
      if (this._hasCurrentUserRole) {
        if (!this.currentUserRole.equals(other.currentUserRole)) {
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
      if (this._hasSubmissionTime != other._hasSubmissionTime) {
        return false;
      }
      if (this._hasSubmissionTime) {
        if (!this.submissionTime.equals(other.submissionTime)) {
          return false;
        }
      }
      if (this._hasVisibility != other._hasVisibility) {
        return false;
      }
      if (this._hasVisibility) {
        if (!this.visibility.equals(other.visibility)) {
          return false;
        }
      }
      if (this._hasRunTarget != other._hasRunTarget) {
        return false;
      }
      if (this._hasRunTarget) {
        if (!this.runTarget.equals(other.runTarget)) {
          return false;
        }
      }
      if (this._hasWorkspaceType != other._hasWorkspaceType) {
        return false;
      }
      if (this._hasWorkspaceType) {
        if (!this.workspaceType.equals(other.workspaceType)) {
          return false;
        }
      }
      if (this._hasSubmitter != other._hasSubmitter) {
        return false;
      }
      if (this._hasSubmitter) {
        if (!this.submitter.equals(other.submitter)) {
          return false;
        }
      }
      if (this._hasCurrentUserRoleForParent != other._hasCurrentUserRoleForParent) {
        return false;
      }
      if (this._hasCurrentUserRoleForParent) {
        if (!this.currentUserRoleForParent.equals(other.currentUserRoleForParent)) {
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
      if (this._hasId != other._hasId) {
        return false;
      }
      if (this._hasId) {
        if (!this.id.equals(other.id)) {
          return false;
        }
      }
      if (this._hasDescription != other._hasDescription) {
        return false;
      }
      if (this._hasDescription) {
        if (!this.description.equals(other.description)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasOwningProjectId ? owningProjectId.hashCode() : 0);
      hash = hash * 31 + (_hasCreatedTime ? createdTime.hashCode() : 0);
      hash = hash * 31 + (_hasArchivedTime ? archivedTime.hashCode() : 0);
      hash = hash * 31 + (_hasSortTime ? sortTime.hashCode() : 0);
      hash = hash * 31 + (_hasCurrentUserRole ? currentUserRole.hashCode() : 0);
      hash = hash * 31 + (_hasParentId ? parentId.hashCode() : 0);
      hash = hash * 31 + (_hasSubmissionTime ? submissionTime.hashCode() : 0);
      hash = hash * 31 + (_hasVisibility ? visibility.hashCode() : 0);
      hash = hash * 31 + (_hasRunTarget ? runTarget.hashCode() : 0);
      hash = hash * 31 + (_hasWorkspaceType ? workspaceType.hashCode() : 0);
      hash = hash * 31 + (_hasSubmitter ? submitter.hashCode() : 0);
      hash = hash * 31 + (_hasCurrentUserRoleForParent ? currentUserRoleForParent.hashCode() : 0);
      hash = hash * 31 + (_hasName ? name.hashCode() : 0);
      hash = hash * 31 + (_hasId ? id.hashCode() : 0);
      hash = hash * 31 + (_hasDescription ? description.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement owningProjectIdOut = (owningProjectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(owningProjectId);
      result.add("owningProjectId", owningProjectIdOut);

      JsonElement createdTimeOut = (createdTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(createdTime);
      result.add("createdTime", createdTimeOut);

      JsonElement archivedTimeOut = (archivedTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(archivedTime);
      result.add("archivedTime", archivedTimeOut);

      JsonElement sortTimeOut = (sortTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(sortTime);
      result.add("sortTime", sortTimeOut);

      JsonElement currentUserRoleOut = (currentUserRole == null) ? JsonNull.INSTANCE : new JsonPrimitive(currentUserRole.name());
      result.add("currentUserRole", currentUserRoleOut);

      JsonElement parentIdOut = (parentId == null) ? JsonNull.INSTANCE : new JsonPrimitive(parentId);
      result.add("parentId", parentIdOut);

      JsonElement submissionTimeOut = (submissionTime == null) ? JsonNull.INSTANCE : new JsonPrimitive(submissionTime);
      result.add("submissionTime", submissionTimeOut);

      JsonElement visibilityOut = (visibility == null) ? JsonNull.INSTANCE : new JsonPrimitive(visibility.name());
      result.add("visibility", visibilityOut);

      JsonElement runTargetOut = runTarget == null ? JsonNull.INSTANCE : runTarget.toJsonElement();
      result.add("runTarget", runTargetOut);

      JsonElement workspaceTypeOut = (workspaceType == null) ? JsonNull.INSTANCE : new JsonPrimitive(workspaceType.name());
      result.add("workspaceType", workspaceTypeOut);

      JsonElement submitterOut = submitter == null ? JsonNull.INSTANCE : submitter.toJsonElement();
      result.add("submitter", submitterOut);

      JsonElement currentUserRoleForParentOut = (currentUserRoleForParent == null) ? JsonNull.INSTANCE : new JsonPrimitive(currentUserRoleForParent.name());
      result.add("currentUserRoleForParent", currentUserRoleForParentOut);

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);

      JsonElement idOut = (id == null) ? JsonNull.INSTANCE : new JsonPrimitive(id);
      result.add("id", idOut);

      JsonElement descriptionOut = (description == null) ? JsonNull.INSTANCE : new JsonPrimitive(description);
      result.add("description", descriptionOut);
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

    public static WorkspaceInfoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      WorkspaceInfoImpl dto = new WorkspaceInfoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("owningProjectId")) {
        JsonElement owningProjectIdIn = json.get("owningProjectId");
        java.lang.String owningProjectIdOut = gson.fromJson(owningProjectIdIn, java.lang.String.class);
        dto.setOwningProjectId(owningProjectIdOut);
      }

      if (json.has("createdTime")) {
        JsonElement createdTimeIn = json.get("createdTime");
        java.lang.String createdTimeOut = gson.fromJson(createdTimeIn, java.lang.String.class);
        dto.setCreatedTime(createdTimeOut);
      }

      if (json.has("archivedTime")) {
        JsonElement archivedTimeIn = json.get("archivedTime");
        java.lang.String archivedTimeOut = gson.fromJson(archivedTimeIn, java.lang.String.class);
        dto.setArchivedTime(archivedTimeOut);
      }

      if (json.has("sortTime")) {
        JsonElement sortTimeIn = json.get("sortTime");
        java.lang.String sortTimeOut = gson.fromJson(sortTimeIn, java.lang.String.class);
        dto.setSortTime(sortTimeOut);
      }

      if (json.has("currentUserRole")) {
        JsonElement currentUserRoleIn = json.get("currentUserRole");
        com.google.collide.dto.Role currentUserRoleOut = gson.fromJson(currentUserRoleIn, com.google.collide.dto.Role.class);
        dto.setCurrentUserRole(currentUserRoleOut);
      }

      if (json.has("parentId")) {
        JsonElement parentIdIn = json.get("parentId");
        java.lang.String parentIdOut = gson.fromJson(parentIdIn, java.lang.String.class);
        dto.setParentId(parentIdOut);
      }

      if (json.has("submissionTime")) {
        JsonElement submissionTimeIn = json.get("submissionTime");
        java.lang.String submissionTimeOut = gson.fromJson(submissionTimeIn, java.lang.String.class);
        dto.setSubmissionTime(submissionTimeOut);
      }

      if (json.has("visibility")) {
        JsonElement visibilityIn = json.get("visibility");
        com.google.collide.dto.Visibility visibilityOut = gson.fromJson(visibilityIn, com.google.collide.dto.Visibility.class);
        dto.setVisibility(visibilityOut);
      }

      if (json.has("runTarget")) {
        JsonElement runTargetIn = json.get("runTarget");
        RunTargetImpl runTargetOut = RunTargetImpl.fromJsonElement(runTargetIn);
        dto.setRunTarget(runTargetOut);
      }

      if (json.has("workspaceType")) {
        JsonElement workspaceTypeIn = json.get("workspaceType");
        com.google.collide.dto.WorkspaceInfo.WorkspaceType workspaceTypeOut = gson.fromJson(workspaceTypeIn, com.google.collide.dto.WorkspaceInfo.WorkspaceType.class);
        dto.setWorkspaceType(workspaceTypeOut);
      }

      if (json.has("submitter")) {
        JsonElement submitterIn = json.get("submitter");
        UserDetailsImpl submitterOut = UserDetailsImpl.fromJsonElement(submitterIn);
        dto.setSubmitter(submitterOut);
      }

      if (json.has("currentUserRoleForParent")) {
        JsonElement currentUserRoleForParentIn = json.get("currentUserRoleForParent");
        com.google.collide.dto.Role currentUserRoleForParentOut = gson.fromJson(currentUserRoleForParentIn, com.google.collide.dto.Role.class);
        dto.setCurrentUserRoleForParent(currentUserRoleForParentOut);
      }

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
      }

      if (json.has("id")) {
        JsonElement idIn = json.get("id");
        java.lang.String idOut = gson.fromJson(idIn, java.lang.String.class);
        dto.setId(idOut);
      }

      if (json.has("description")) {
        JsonElement descriptionIn = json.get("description");
        java.lang.String descriptionOut = gson.fromJson(descriptionIn, java.lang.String.class);
        dto.setDescription(descriptionOut);
      }

      return dto;
    }
    public static WorkspaceInfoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class WorkspaceTreeUpdateImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.WorkspaceTreeUpdate, JsonSerializable {

    private WorkspaceTreeUpdateImpl() {
      super(117);
    }

    protected WorkspaceTreeUpdateImpl(int type) {
      super(type);
    }

    protected java.lang.String authorClientId;
    private boolean _hasAuthorClientId;
    protected java.util.List<MutationImpl> mutations;
    private boolean _hasMutations;

    public boolean hasAuthorClientId() {
      return _hasAuthorClientId;
    }

    @Override
    public java.lang.String getAuthorClientId() {
      return authorClientId;
    }

    public WorkspaceTreeUpdateImpl setAuthorClientId(java.lang.String v) {
      _hasAuthorClientId = true;
      authorClientId = v;
      return this;
    }

    public boolean hasMutations() {
      return _hasMutations;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.Mutation> getMutations() {
      ensureMutations();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(mutations);
    }

    public WorkspaceTreeUpdateImpl setMutations(java.util.List<MutationImpl> v) {
      _hasMutations = true;
      mutations = v;
      return this;
    }

    public void addMutations(MutationImpl v) {
      ensureMutations();
      mutations.add(v);
    }

    public void clearMutations() {
      ensureMutations();
      mutations.clear();
    }

    private void ensureMutations() {
      if (!_hasMutations) {
        setMutations(mutations != null ? mutations : new java.util.ArrayList<MutationImpl>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof WorkspaceTreeUpdateImpl)) {
        return false;
      }
      WorkspaceTreeUpdateImpl other = (WorkspaceTreeUpdateImpl) o;
      if (this._hasAuthorClientId != other._hasAuthorClientId) {
        return false;
      }
      if (this._hasAuthorClientId) {
        if (!this.authorClientId.equals(other.authorClientId)) {
          return false;
        }
      }
      if (this._hasMutations != other._hasMutations) {
        return false;
      }
      if (this._hasMutations) {
        if (!this.mutations.equals(other.mutations)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasAuthorClientId ? authorClientId.hashCode() : 0);
      hash = hash * 31 + (_hasMutations ? mutations.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement authorClientIdOut = (authorClientId == null) ? JsonNull.INSTANCE : new JsonPrimitive(authorClientId);
      result.add("authorClientId", authorClientIdOut);

      JsonArray mutationsOut = new JsonArray();
      ensureMutations();
      for (MutationImpl mutations_ : mutations) {
        JsonElement mutationsOut_ = mutations_ == null ? JsonNull.INSTANCE : mutations_.toJsonElement();
        mutationsOut.add(mutationsOut_);
      }
      result.add("mutations", mutationsOut);
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

    public static WorkspaceTreeUpdateImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      WorkspaceTreeUpdateImpl dto = new WorkspaceTreeUpdateImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("authorClientId")) {
        JsonElement authorClientIdIn = json.get("authorClientId");
        java.lang.String authorClientIdOut = gson.fromJson(authorClientIdIn, java.lang.String.class);
        dto.setAuthorClientId(authorClientIdOut);
      }

      if (json.has("mutations")) {
        JsonElement mutationsIn = json.get("mutations");
        java.util.ArrayList<MutationImpl> mutationsOut = null;
        if (mutationsIn != null && !mutationsIn.isJsonNull()) {
          mutationsOut = new java.util.ArrayList<MutationImpl>();
          java.util.Iterator<JsonElement> mutationsInIterator = mutationsIn.getAsJsonArray().iterator();
          while (mutationsInIterator.hasNext()) {
            JsonElement mutationsIn_ = mutationsInIterator.next();
            MutationImpl mutationsOut_ = MutationImpl.fromJsonElement(mutationsIn_);
            mutationsOut.add(mutationsOut_);
          }
        }
        dto.setMutations(mutationsOut);
      }

      return dto;
    }
    public static WorkspaceTreeUpdateImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class WorkspaceTreeUpdateBroadcastImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.google.collide.dto.WorkspaceTreeUpdateBroadcast, JsonSerializable {

    private WorkspaceTreeUpdateBroadcastImpl() {
      super(118);
    }

    protected WorkspaceTreeUpdateBroadcastImpl(int type) {
      super(type);
    }

    public static WorkspaceTreeUpdateBroadcastImpl make() {
      return new WorkspaceTreeUpdateBroadcastImpl();
    }

    protected java.util.List<MutationImpl> mutations;
    private boolean _hasMutations;
    protected java.lang.String newTreeVersion;
    private boolean _hasNewTreeVersion;

    public boolean hasMutations() {
      return _hasMutations;
    }

    @Override
    public org.exoplatform.ide.json.shared.JsonArray<com.google.collide.dto.Mutation> getMutations() {
      ensureMutations();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(mutations);
    }

    public WorkspaceTreeUpdateBroadcastImpl setMutations(java.util.List<MutationImpl> v) {
      _hasMutations = true;
      mutations = v;
      return this;
    }

    public void addMutations(MutationImpl v) {
      ensureMutations();
      mutations.add(v);
    }

    public void clearMutations() {
      ensureMutations();
      mutations.clear();
    }

    private void ensureMutations() {
      if (!_hasMutations) {
        setMutations(mutations != null ? mutations : new java.util.ArrayList<MutationImpl>());
      }
    }

    public boolean hasNewTreeVersion() {
      return _hasNewTreeVersion;
    }

    @Override
    public java.lang.String getNewTreeVersion() {
      return newTreeVersion;
    }

    public WorkspaceTreeUpdateBroadcastImpl setNewTreeVersion(java.lang.String v) {
      _hasNewTreeVersion = true;
      newTreeVersion = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof WorkspaceTreeUpdateBroadcastImpl)) {
        return false;
      }
      WorkspaceTreeUpdateBroadcastImpl other = (WorkspaceTreeUpdateBroadcastImpl) o;
      if (this._hasMutations != other._hasMutations) {
        return false;
      }
      if (this._hasMutations) {
        if (!this.mutations.equals(other.mutations)) {
          return false;
        }
      }
      if (this._hasNewTreeVersion != other._hasNewTreeVersion) {
        return false;
      }
      if (this._hasNewTreeVersion) {
        if (!this.newTreeVersion.equals(other.newTreeVersion)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasMutations ? mutations.hashCode() : 0);
      hash = hash * 31 + (_hasNewTreeVersion ? newTreeVersion.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonArray mutationsOut = new JsonArray();
      ensureMutations();
      for (MutationImpl mutations_ : mutations) {
        JsonElement mutationsOut_ = mutations_ == null ? JsonNull.INSTANCE : mutations_.toJsonElement();
        mutationsOut.add(mutationsOut_);
      }
      result.add("mutations", mutationsOut);

      JsonElement newTreeVersionOut = (newTreeVersion == null) ? JsonNull.INSTANCE : new JsonPrimitive(newTreeVersion);
      result.add("newTreeVersion", newTreeVersionOut);
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

    public static WorkspaceTreeUpdateBroadcastImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      WorkspaceTreeUpdateBroadcastImpl dto = new WorkspaceTreeUpdateBroadcastImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("mutations")) {
        JsonElement mutationsIn = json.get("mutations");
        java.util.ArrayList<MutationImpl> mutationsOut = null;
        if (mutationsIn != null && !mutationsIn.isJsonNull()) {
          mutationsOut = new java.util.ArrayList<MutationImpl>();
          java.util.Iterator<JsonElement> mutationsInIterator = mutationsIn.getAsJsonArray().iterator();
          while (mutationsInIterator.hasNext()) {
            JsonElement mutationsIn_ = mutationsInIterator.next();
            MutationImpl mutationsOut_ = MutationImpl.fromJsonElement(mutationsIn_);
            mutationsOut.add(mutationsOut_);
          }
        }
        dto.setMutations(mutationsOut);
      }

      if (json.has("newTreeVersion")) {
        JsonElement newTreeVersionIn = json.get("newTreeVersion");
        java.lang.String newTreeVersionOut = gson.fromJson(newTreeVersionIn, java.lang.String.class);
        dto.setNewTreeVersion(newTreeVersionOut);
      }

      return dto;
    }
    public static WorkspaceTreeUpdateBroadcastImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

}