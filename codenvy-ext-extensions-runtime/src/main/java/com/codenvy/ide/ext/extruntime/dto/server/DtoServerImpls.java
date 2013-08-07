/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;


@SuppressWarnings({"unchecked", "cast"})
public class DtoServerImpls {

  private static final Gson gson = new GsonBuilder().serializeNulls().create();

  private  DtoServerImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "d31e55ce0f4d3818d4006b775ad01432e034db2d";

  public static class ApplicationInstanceImpl implements com.codenvy.ide.ext.extruntime.shared.ApplicationInstance, JsonSerializable {

    public static ApplicationInstanceImpl make() {
      return new ApplicationInstanceImpl();
    }

    protected java.lang.String name;
    private boolean _hasName;
    protected java.lang.String host;
    private boolean _hasHost;
    protected int port;
    private boolean _hasPort;

    public boolean hasName() {
      return _hasName;
    }

    @Override
    public java.lang.String getName() {
      return name;
    }

    public ApplicationInstanceImpl setName(java.lang.String v) {
      _hasName = true;
      name = v;
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
      if (this._hasName != other._hasName) {
        return false;
      }
      if (this._hasName) {
        if (!this.name.equals(other.name)) {
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
      hash = hash * 31 + (_hasName ? name.hashCode() : 0);
      hash = hash * 31 + (_hasHost ? host.hashCode() : 0);
      hash = hash * 31 + (_hasPort ? java.lang.Integer.valueOf(port).hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement nameOut = (name == null) ? JsonNull.INSTANCE : new JsonPrimitive(name);
      result.add("name", nameOut);

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

      if (json.has("name")) {
        JsonElement nameIn = json.get("name");
        java.lang.String nameOut = gson.fromJson(nameIn, java.lang.String.class);
        dto.setName(nameOut);
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

}