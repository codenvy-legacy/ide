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
package com.codenvy.vfs.dto.server;

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

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "e67dddb2fe5089b103e87f7007b04d9b0871e1fc";

  public static class FileDeletedDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.FileDeletedDto, JsonSerializable {

    private FileDeletedDtoImpl() {
      super(3);
    }

    protected FileDeletedDtoImpl(int type) {
      super(type);
    }

    public static FileDeletedDtoImpl make() {
      return new FileDeletedDtoImpl();
    }

    protected java.lang.String fileId;
    private boolean _hasFileId;
    protected java.lang.String filePath;
    private boolean _hasFilePath;

    public boolean hasFileId() {
      return _hasFileId;
    }

    @Override
    public java.lang.String getFileId() {
      return fileId;
    }

    public FileDeletedDtoImpl setFileId(java.lang.String v) {
      _hasFileId = true;
      fileId = v;
      return this;
    }

    public boolean hasFilePath() {
      return _hasFilePath;
    }

    @Override
    public java.lang.String getFilePath() {
      return filePath;
    }

    public FileDeletedDtoImpl setFilePath(java.lang.String v) {
      _hasFilePath = true;
      filePath = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof FileDeletedDtoImpl)) {
        return false;
      }
      FileDeletedDtoImpl other = (FileDeletedDtoImpl) o;
      if (this._hasFileId != other._hasFileId) {
        return false;
      }
      if (this._hasFileId) {
        if (!this.fileId.equals(other.fileId)) {
          return false;
        }
      }
      if (this._hasFilePath != other._hasFilePath) {
        return false;
      }
      if (this._hasFilePath) {
        if (!this.filePath.equals(other.filePath)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasFileId ? fileId.hashCode() : 0);
      hash = hash * 31 + (_hasFilePath ? filePath.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement fileIdOut = (fileId == null) ? JsonNull.INSTANCE : new JsonPrimitive(fileId);
      result.add("fileId", fileIdOut);

      JsonElement filePathOut = (filePath == null) ? JsonNull.INSTANCE : new JsonPrimitive(filePath);
      result.add("filePath", filePathOut);
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

    public static FileDeletedDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      FileDeletedDtoImpl dto = new FileDeletedDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("fileId")) {
        JsonElement fileIdIn = json.get("fileId");
        java.lang.String fileIdOut = gson.fromJson(fileIdIn, java.lang.String.class);
        dto.setFileId(fileIdOut);
      }

      if (json.has("filePath")) {
        JsonElement filePathIn = json.get("filePath");
        java.lang.String filePathOut = gson.fromJson(filePathIn, java.lang.String.class);
        dto.setFilePath(filePathOut);
      }

      return dto;
    }
    public static FileDeletedDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ProjectClosedDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.ProjectClosedDto, JsonSerializable {

    private ProjectClosedDtoImpl() {
      super(2);
    }

    protected ProjectClosedDtoImpl(int type) {
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
      ProjectClosedDtoImpl other = (ProjectClosedDtoImpl) o;
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

    public static ProjectClosedDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ProjectClosedDtoImpl dto = new ProjectClosedDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

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

  public static class ProjectOpenedDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.ProjectOpenedDto, JsonSerializable {

    private ProjectOpenedDtoImpl() {
      super(1);
    }

    protected ProjectOpenedDtoImpl(int type) {
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
      ProjectOpenedDtoImpl other = (ProjectOpenedDtoImpl) o;
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

    public static ProjectOpenedDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ProjectOpenedDtoImpl dto = new ProjectOpenedDtoImpl();
      JsonObject json = jsonElem.getAsJsonObject();

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

}