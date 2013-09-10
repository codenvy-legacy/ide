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


// GENERATED SOURCE. DO NOT EDIT.
package com.codenvy.ide.ext.extruntime.dto.server;

import com.codenvy.ide.dto.server.JsonSerializable;

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

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "cc706fffe5699d4c938d361617d9d6a128bfde9d";

  public static class ApplicationInstanceImpl implements com.codenvy.ide.ext.extruntime.shared.ApplicationInstance, JsonSerializable {

    public static ApplicationInstanceImpl make() {
      return new ApplicationInstanceImpl();
    }

    protected int codeServerPort;
    private boolean _hasCodeServerPort;
    protected java.lang.String codeServerHost;
    private boolean _hasCodeServerHost;
    protected java.lang.String id;
    private boolean _hasId;
    protected java.lang.String host;
    private boolean _hasHost;
    protected int port;
    private boolean _hasPort;

    public boolean hasCodeServerPort() {
      return _hasCodeServerPort;
    }

    @Override
    public int getCodeServerPort() {
      return codeServerPort;
    }

    public ApplicationInstanceImpl setCodeServerPort(int v) {
      _hasCodeServerPort = true;
      codeServerPort = v;
      return this;
    }

    public boolean hasCodeServerHost() {
      return _hasCodeServerHost;
    }

    @Override
    public java.lang.String getCodeServerHost() {
      return codeServerHost;
    }

    public ApplicationInstanceImpl setCodeServerHost(java.lang.String v) {
      _hasCodeServerHost = true;
      codeServerHost = v;
      return this;
    }

    public boolean hasId() {
      return _hasId;
    }

    @Override
    public java.lang.String getId() {
      return id;
    }

    public ApplicationInstanceImpl setId(java.lang.String v) {
      _hasId = true;
      id = v;
      return this;
    }

    public boolean hasHost() {
      return _hasHost;
    }

    @Override
    public java.lang.String getHost() {
      return host;
    }

    public ApplicationInstanceImpl setHost(java.lang.String v) {
      _hasHost = true;
      host = v;
      return this;
    }

    public boolean hasPort() {
      return _hasPort;
    }

    @Override
    public int getPort() {
      return port;
    }

    public ApplicationInstanceImpl setPort(int v) {
      _hasPort = true;
      port = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof ApplicationInstanceImpl)) {
        return false;
      }
      ApplicationInstanceImpl other = (ApplicationInstanceImpl) o;
      if (this._hasCodeServerPort != other._hasCodeServerPort) {
        return false;
      }
      if (this._hasCodeServerPort) {
        if (this.codeServerPort != other.codeServerPort) {
          return false;
        }
      }
      if (this._hasCodeServerHost != other._hasCodeServerHost) {
        return false;
      }
      if (this._hasCodeServerHost) {
        if (!this.codeServerHost.equals(other.codeServerHost)) {
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
      if (this._hasHost != other._hasHost) {
        return false;
      }
      if (this._hasHost) {
        if (!this.host.equals(other.host)) {
          return false;
        }
      }
      if (this._hasPort != other._hasPort) {
        return false;
      }
      if (this._hasPort) {
        if (this.port != other.port) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasCodeServerPort ? java.lang.Integer.valueOf(codeServerPort).hashCode() : 0);
      hash = hash * 31 + (_hasCodeServerHost ? codeServerHost.hashCode() : 0);
      hash = hash * 31 + (_hasId ? id.hashCode() : 0);
      hash = hash * 31 + (_hasHost ? host.hashCode() : 0);
      hash = hash * 31 + (_hasPort ? java.lang.Integer.valueOf(port).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonPrimitive codeServerPortOut = new JsonPrimitive(codeServerPort);
      result.add("codeServerPort", codeServerPortOut);

      JsonElement codeServerHostOut = (codeServerHost == null) ? JsonNull.INSTANCE : new JsonPrimitive(codeServerHost);
      result.add("codeServerHost", codeServerHostOut);

      JsonElement idOut = (id == null) ? JsonNull.INSTANCE : new JsonPrimitive(id);
      result.add("id", idOut);

      JsonElement hostOut = (host == null) ? JsonNull.INSTANCE : new JsonPrimitive(host);
      result.add("host", hostOut);

      JsonPrimitive portOut = new JsonPrimitive(port);
      result.add("port", portOut);
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

    public static ApplicationInstanceImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ApplicationInstanceImpl dto = new ApplicationInstanceImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("codeServerPort")) {
        JsonElement codeServerPortIn = json.get("codeServerPort");
        int codeServerPortOut = codeServerPortIn.getAsInt();
        dto.setCodeServerPort(codeServerPortOut);
      }

      if (json.has("codeServerHost")) {
        JsonElement codeServerHostIn = json.get("codeServerHost");
        java.lang.String codeServerHostOut = gson.fromJson(codeServerHostIn, java.lang.String.class);
        dto.setCodeServerHost(codeServerHostOut);
      }

      if (json.has("id")) {
        JsonElement idIn = json.get("id");
        java.lang.String idOut = gson.fromJson(idIn, java.lang.String.class);
        dto.setId(idOut);
      }

      if (json.has("host")) {
        JsonElement hostIn = json.get("host");
        java.lang.String hostOut = gson.fromJson(hostIn, java.lang.String.class);
        dto.setHost(hostOut);
      }

      if (json.has("port")) {
        JsonElement portIn = json.get("port");
        int portOut = portIn.getAsInt();
        dto.setPort(portOut);
      }

      return dto;
    }
    public static ApplicationInstanceImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class BuildStatusImpl implements com.codenvy.ide.extension.maven.shared.BuildStatus, JsonSerializable {

    public static BuildStatusImpl make() {
      return new BuildStatusImpl();
    }

    protected java.lang.String time;
    private boolean _hasTime;
    protected int exitCode;
    private boolean _hasExitCode;
    protected com.codenvy.ide.extension.maven.shared.BuildStatus.Status status;
    private boolean _hasStatus;
    protected java.lang.String error;
    private boolean _hasError;
    protected java.lang.String downloadUrl;
    private boolean _hasDownloadUrl;

    public boolean hasTime() {
      return _hasTime;
    }

    @Override
    public java.lang.String getTime() {
      return time;
    }

    public BuildStatusImpl setTime(java.lang.String v) {
      _hasTime = true;
      time = v;
      return this;
    }

    public boolean hasExitCode() {
      return _hasExitCode;
    }

    @Override
    public int getExitCode() {
      return exitCode;
    }

    public BuildStatusImpl setExitCode(int v) {
      _hasExitCode = true;
      exitCode = v;
      return this;
    }

    public boolean hasStatus() {
      return _hasStatus;
    }

    @Override
    public com.codenvy.ide.extension.maven.shared.BuildStatus.Status getStatus() {
      return status;
    }

    public BuildStatusImpl setStatus(com.codenvy.ide.extension.maven.shared.BuildStatus.Status v) {
      _hasStatus = true;
      status = v;
      return this;
    }

    public boolean hasError() {
      return _hasError;
    }

    @Override
    public java.lang.String getError() {
      return error;
    }

    public BuildStatusImpl setError(java.lang.String v) {
      _hasError = true;
      error = v;
      return this;
    }

    public boolean hasDownloadUrl() {
      return _hasDownloadUrl;
    }

    @Override
    public java.lang.String getDownloadUrl() {
      return downloadUrl;
    }

    public BuildStatusImpl setDownloadUrl(java.lang.String v) {
      _hasDownloadUrl = true;
      downloadUrl = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof BuildStatusImpl)) {
        return false;
      }
      BuildStatusImpl other = (BuildStatusImpl) o;
      if (this._hasTime != other._hasTime) {
        return false;
      }
      if (this._hasTime) {
        if (!this.time.equals(other.time)) {
          return false;
        }
      }
      if (this._hasExitCode != other._hasExitCode) {
        return false;
      }
      if (this._hasExitCode) {
        if (this.exitCode != other.exitCode) {
          return false;
        }
      }
      if (this._hasStatus != other._hasStatus) {
        return false;
      }
      if (this._hasStatus) {
        if (!this.status.equals(other.status)) {
          return false;
        }
      }
      if (this._hasError != other._hasError) {
        return false;
      }
      if (this._hasError) {
        if (!this.error.equals(other.error)) {
          return false;
        }
      }
      if (this._hasDownloadUrl != other._hasDownloadUrl) {
        return false;
      }
      if (this._hasDownloadUrl) {
        if (!this.downloadUrl.equals(other.downloadUrl)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasTime ? time.hashCode() : 0);
      hash = hash * 31 + (_hasExitCode ? java.lang.Integer.valueOf(exitCode).hashCode() : 0);
      hash = hash * 31 + (_hasStatus ? status.hashCode() : 0);
      hash = hash * 31 + (_hasError ? error.hashCode() : 0);
      hash = hash * 31 + (_hasDownloadUrl ? downloadUrl.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement timeOut = (time == null) ? JsonNull.INSTANCE : new JsonPrimitive(time);
      result.add("time", timeOut);

      JsonPrimitive exitCodeOut = new JsonPrimitive(exitCode);
      result.add("exitCode", exitCodeOut);

      JsonElement statusOut = (status == null) ? JsonNull.INSTANCE : new JsonPrimitive(status.name());
      result.add("status", statusOut);

      JsonElement errorOut = (error == null) ? JsonNull.INSTANCE : new JsonPrimitive(error);
      result.add("error", errorOut);

      JsonElement downloadUrlOut = (downloadUrl == null) ? JsonNull.INSTANCE : new JsonPrimitive(downloadUrl);
      result.add("downloadUrl", downloadUrlOut);
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

    public static BuildStatusImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      BuildStatusImpl dto = new BuildStatusImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("time")) {
        JsonElement timeIn = json.get("time");
        java.lang.String timeOut = gson.fromJson(timeIn, java.lang.String.class);
        dto.setTime(timeOut);
      }

      if (json.has("exitCode")) {
        JsonElement exitCodeIn = json.get("exitCode");
        int exitCodeOut = exitCodeIn.getAsInt();
        dto.setExitCode(exitCodeOut);
      }

      if (json.has("status")) {
        JsonElement statusIn = json.get("status");
        com.codenvy.ide.extension.maven.shared.BuildStatus.Status statusOut = gson.fromJson(statusIn, com.codenvy.ide.extension.maven.shared.BuildStatus.Status.class);
        dto.setStatus(statusOut);
      }

      if (json.has("error")) {
        JsonElement errorIn = json.get("error");
        java.lang.String errorOut = gson.fromJson(errorIn, java.lang.String.class);
        dto.setError(errorOut);
      }

      if (json.has("downloadUrl")) {
        JsonElement downloadUrlIn = json.get("downloadUrl");
        java.lang.String downloadUrlOut = gson.fromJson(downloadUrlIn, java.lang.String.class);
        dto.setDownloadUrl(downloadUrlOut);
      }

      return dto;
    }
    public static BuildStatusImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

}