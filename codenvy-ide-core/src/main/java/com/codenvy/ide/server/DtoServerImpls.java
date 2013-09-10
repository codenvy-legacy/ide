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
package com.codenvy.ide.server;

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

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "89fa4319e484f2fd1f6ddf74028df2fe86085c6f";

  public static class UpdateUserAttributesImpl implements com.codenvy.ide.api.user.UpdateUserAttributes, JsonSerializable {

    public static UpdateUserAttributesImpl make() {
      return new UpdateUserAttributesImpl();
    }

    protected java.util.Map<String, java.lang.String> attributes;
    private boolean _hasAttributes;

    public boolean hasAttributes() {
      return _hasAttributes;
    }

    @Override
    public com.codenvy.ide.json.JsonStringMap<java.lang.String> getAttributes() {
      ensureAttributes();
      return (com.codenvy.ide.json.JsonStringMap) new com.codenvy.ide.json.java.JsonStringMapAdapter(attributes);
    }

    public UpdateUserAttributesImpl setAttributes(java.util.Map<String, java.lang.String> v) {
      _hasAttributes = true;
      attributes = v;
      return this;
    }

    public void putAttributes(String k, java.lang.String v) {
      ensureAttributes();
      attributes.put(k, v);
    }

    public void clearAttributes() {
      ensureAttributes();
      attributes.clear();
    }

    private void ensureAttributes() {
      if (!_hasAttributes) {
        setAttributes(attributes != null ? attributes : new java.util.HashMap<String, java.lang.String>());
      }
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof UpdateUserAttributesImpl)) {
        return false;
      }
      UpdateUserAttributesImpl other = (UpdateUserAttributesImpl) o;
      if (this._hasAttributes != other._hasAttributes) {
        return false;
      }
      if (this._hasAttributes) {
        if (!this.attributes.equals(other.attributes)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = 1;
      hash = hash * 31 + (_hasAttributes ? attributes.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonObject attributesOut = new JsonObject();
      ensureAttributes();
      for (Map.Entry<String, java.lang.String> entry0 : attributes.entrySet()) {
        java.lang.String attributes_ = entry0.getValue();
        JsonElement attributesOut_ = (attributes_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(attributes_);
        attributesOut.add(entry0.getKey(), attributesOut_);
      }
      result.add("attributes", attributesOut);
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

    public static UpdateUserAttributesImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UpdateUserAttributesImpl dto = new UpdateUserAttributesImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("attributes")) {
        JsonElement attributesIn = json.get("attributes");
        java.util.HashMap<String, java.lang.String> attributesOut = null;
        if (attributesIn != null && !attributesIn.isJsonNull()) {
          attributesOut = new java.util.HashMap<String, java.lang.String>();
          java.util.Set<Map.Entry<String, JsonElement>> entries0 = attributesIn.getAsJsonObject().entrySet();
          for (Map.Entry<String, JsonElement> entry0 : entries0) {
            JsonElement attributesIn_ = entry0.getValue();
            java.lang.String attributesOut_ = gson.fromJson(attributesIn_, java.lang.String.class);
            attributesOut.put(entry0.getKey(), attributesOut_);
          }
        }
        dto.setAttributes(attributesOut);
      }

      return dto;
    }
    public static UpdateUserAttributesImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UserImpl implements com.codenvy.ide.api.user.User, JsonSerializable {

    public static UserImpl make() {
      return new UserImpl();
    }

    protected java.util.Map<String, java.lang.String> profileAttributes;
    private boolean _hasProfileAttributes;
    protected java.lang.String userId;
    private boolean _hasUserId;

    public boolean hasProfileAttributes() {
      return _hasProfileAttributes;
    }

    @Override
    public com.codenvy.ide.json.JsonStringMap<java.lang.String> getProfileAttributes() {
      ensureProfileAttributes();
      return (com.codenvy.ide.json.JsonStringMap) new com.codenvy.ide.json.java.JsonStringMapAdapter(profileAttributes);
    }

    public UserImpl setProfileAttributes(java.util.Map<String, java.lang.String> v) {
      _hasProfileAttributes = true;
      profileAttributes = v;
      return this;
    }

    public void putProfileAttributes(String k, java.lang.String v) {
      ensureProfileAttributes();
      profileAttributes.put(k, v);
    }

    public void clearProfileAttributes() {
      ensureProfileAttributes();
      profileAttributes.clear();
    }

    private void ensureProfileAttributes() {
      if (!_hasProfileAttributes) {
        setProfileAttributes(profileAttributes != null ? profileAttributes : new java.util.HashMap<String, java.lang.String>());
      }
    }

    public boolean hasUserId() {
      return _hasUserId;
    }

    @Override
    public java.lang.String getUserId() {
      return userId;
    }

    public UserImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!(o instanceof UserImpl)) {
        return false;
      }
      UserImpl other = (UserImpl) o;
      if (this._hasProfileAttributes != other._hasProfileAttributes) {
        return false;
      }
      if (this._hasProfileAttributes) {
        if (!this.profileAttributes.equals(other.profileAttributes)) {
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
      hash = hash * 31 + (_hasProfileAttributes ? profileAttributes.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonObject profileAttributesOut = new JsonObject();
      ensureProfileAttributes();
      for (Map.Entry<String, java.lang.String> entry0 : profileAttributes.entrySet()) {
        java.lang.String profileAttributes_ = entry0.getValue();
        JsonElement profileAttributesOut_ = (profileAttributes_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(profileAttributes_);
        profileAttributesOut.add(entry0.getKey(), profileAttributesOut_);
      }
      result.add("profileAttributes", profileAttributesOut);

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

    public static UserImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UserImpl dto = new UserImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("profileAttributes")) {
        JsonElement profileAttributesIn = json.get("profileAttributes");
        java.util.HashMap<String, java.lang.String> profileAttributesOut = null;
        if (profileAttributesIn != null && !profileAttributesIn.isJsonNull()) {
          profileAttributesOut = new java.util.HashMap<String, java.lang.String>();
          java.util.Set<Map.Entry<String, JsonElement>> entries0 = profileAttributesIn.getAsJsonObject().entrySet();
          for (Map.Entry<String, JsonElement> entry0 : entries0) {
            JsonElement profileAttributesIn_ = entry0.getValue();
            java.lang.String profileAttributesOut_ = gson.fromJson(profileAttributesIn_, java.lang.String.class);
            profileAttributesOut.put(entry0.getKey(), profileAttributesOut_);
          }
        }
        dto.setProfileAttributes(profileAttributesOut);
      }

      if (json.has("userId")) {
        JsonElement userIdIn = json.get("userId");
        java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
        dto.setUserId(userIdOut);
      }

      return dto;
    }
    public static UserImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

}