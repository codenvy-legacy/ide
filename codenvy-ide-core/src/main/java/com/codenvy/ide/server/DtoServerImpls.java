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
package com.codenvy.ide.server;

import com.codenvy.ide.dtogen.server.JsonSerializable;

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

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "d066778066846c22736706d3ccc1241d5083940d";

  public static class RemoveUserAttributeImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.ide.shared.RemoveUserAttribute, JsonSerializable {

    private RemoveUserAttributeImpl() {
      super(4);
    }

    protected RemoveUserAttributeImpl(int type) {
      super(type);
    }

    protected java.lang.String attributeName;
    private boolean _hasAttributeName;
    protected java.lang.String userId;
    private boolean _hasUserId;

    public boolean hasAttributeName() {
      return _hasAttributeName;
    }

    @Override
    public java.lang.String getAttributeName() {
      return attributeName;
    }

    public RemoveUserAttributeImpl setAttributeName(java.lang.String v) {
      _hasAttributeName = true;
      attributeName = v;
      return this;
    }

    public boolean hasUserId() {
      return _hasUserId;
    }

    @Override
    public java.lang.String getUserId() {
      return userId;
    }

    public RemoveUserAttributeImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof RemoveUserAttributeImpl)) {
        return false;
      }
      RemoveUserAttributeImpl other = (RemoveUserAttributeImpl) o;
      if (this._hasAttributeName != other._hasAttributeName) {
        return false;
      }
      if (this._hasAttributeName) {
        if (!this.attributeName.equals(other.attributeName)) {
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
      hash = hash * 31 + (_hasAttributeName ? attributeName.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement attributeNameOut = (attributeName == null) ? JsonNull.INSTANCE : new JsonPrimitive(attributeName);
      result.add("attributeName", attributeNameOut);

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

    public static RemoveUserAttributeImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      RemoveUserAttributeImpl dto = new RemoveUserAttributeImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("attributeName")) {
        JsonElement attributeNameIn = json.get("attributeName");
        java.lang.String attributeNameOut = gson.fromJson(attributeNameIn, java.lang.String.class);
        dto.setAttributeName(attributeNameOut);
      }

      if (json.has("userId")) {
        JsonElement userIdIn = json.get("userId");
        java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
        dto.setUserId(userIdOut);
      }

      return dto;
    }
    public static RemoveUserAttributeImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UpdateUserAttributeImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.ide.shared.UpdateUserAttribute, JsonSerializable {

    private UpdateUserAttributeImpl() {
      super(2);
    }

    protected UpdateUserAttributeImpl(int type) {
      super(type);
    }

    protected java.lang.String attributeValue;
    private boolean _hasAttributeValue;
    protected java.lang.String attributeName;
    private boolean _hasAttributeName;
    protected java.lang.String userId;
    private boolean _hasUserId;

    public boolean hasAttributeValue() {
      return _hasAttributeValue;
    }

    @Override
    public java.lang.String getAttributeValue() {
      return attributeValue;
    }

    public UpdateUserAttributeImpl setAttributeValue(java.lang.String v) {
      _hasAttributeValue = true;
      attributeValue = v;
      return this;
    }

    public boolean hasAttributeName() {
      return _hasAttributeName;
    }

    @Override
    public java.lang.String getAttributeName() {
      return attributeName;
    }

    public UpdateUserAttributeImpl setAttributeName(java.lang.String v) {
      _hasAttributeName = true;
      attributeName = v;
      return this;
    }

    public boolean hasUserId() {
      return _hasUserId;
    }

    @Override
    public java.lang.String getUserId() {
      return userId;
    }

    public UpdateUserAttributeImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UpdateUserAttributeImpl)) {
        return false;
      }
      UpdateUserAttributeImpl other = (UpdateUserAttributeImpl) o;
      if (this._hasAttributeValue != other._hasAttributeValue) {
        return false;
      }
      if (this._hasAttributeValue) {
        if (!this.attributeValue.equals(other.attributeValue)) {
          return false;
        }
      }
      if (this._hasAttributeName != other._hasAttributeName) {
        return false;
      }
      if (this._hasAttributeName) {
        if (!this.attributeName.equals(other.attributeName)) {
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
      hash = hash * 31 + (_hasAttributeValue ? attributeValue.hashCode() : 0);
      hash = hash * 31 + (_hasAttributeName ? attributeName.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement attributeValueOut = (attributeValue == null) ? JsonNull.INSTANCE : new JsonPrimitive(attributeValue);
      result.add("attributeValue", attributeValueOut);

      JsonElement attributeNameOut = (attributeName == null) ? JsonNull.INSTANCE : new JsonPrimitive(attributeName);
      result.add("attributeName", attributeNameOut);

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

    public static UpdateUserAttributeImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UpdateUserAttributeImpl dto = new UpdateUserAttributeImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("attributeValue")) {
        JsonElement attributeValueIn = json.get("attributeValue");
        java.lang.String attributeValueOut = gson.fromJson(attributeValueIn, java.lang.String.class);
        dto.setAttributeValue(attributeValueOut);
      }

      if (json.has("attributeName")) {
        JsonElement attributeNameIn = json.get("attributeName");
        java.lang.String attributeNameOut = gson.fromJson(attributeNameIn, java.lang.String.class);
        dto.setAttributeName(attributeNameOut);
      }

      if (json.has("userId")) {
        JsonElement userIdIn = json.get("userId");
        java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
        dto.setUserId(userIdOut);
      }

      return dto;
    }
    public static UpdateUserAttributeImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class UpdateUserAttributesImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.ide.shared.UpdateUserAttributes, JsonSerializable {

    private UpdateUserAttributesImpl() {
      super(3);
    }

    protected UpdateUserAttributesImpl(int type) {
      super(type);
    }

    protected java.util.Map<String, java.lang.String> attributes;
    private boolean _hasAttributes;
    protected java.lang.String userId;
    private boolean _hasUserId;

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

    public boolean hasUserId() {
      return _hasUserId;
    }

    @Override
    public java.lang.String getUserId() {
      return userId;
    }

    public UpdateUserAttributesImpl setUserId(java.lang.String v) {
      _hasUserId = true;
      userId = v;
      return this;
    }

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
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
      hash = hash * 31 + (_hasAttributes ? attributes.hashCode() : 0);
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
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

      if (json.has("userId")) {
        JsonElement userIdIn = json.get("userId");
        java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
        dto.setUserId(userIdOut);
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

  public static class UserImpl extends com.codenvy.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.ide.shared.User, JsonSerializable {

    private UserImpl() {
      super(1);
    }

    protected UserImpl(int type) {
      super(type);
    }

    public static UserImpl make() {
      return new UserImpl();
    }

    protected java.lang.String userId;
    private boolean _hasUserId;
    protected java.util.Map<String, java.lang.String> profileAttributes;
    private boolean _hasProfileAttributes;

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

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof UserImpl)) {
        return false;
      }
      UserImpl other = (UserImpl) o;
      if (this._hasUserId != other._hasUserId) {
        return false;
      }
      if (this._hasUserId) {
        if (!this.userId.equals(other.userId)) {
          return false;
        }
      }
      if (this._hasProfileAttributes != other._hasProfileAttributes) {
        return false;
      }
      if (this._hasProfileAttributes) {
        if (!this.profileAttributes.equals(other.profileAttributes)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasUserId ? userId.hashCode() : 0);
      hash = hash * 31 + (_hasProfileAttributes ? profileAttributes.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement userIdOut = (userId == null) ? JsonNull.INSTANCE : new JsonPrimitive(userId);
      result.add("userId", userIdOut);

      JsonObject profileAttributesOut = new JsonObject();
      ensureProfileAttributes();
      for (Map.Entry<String, java.lang.String> entry0 : profileAttributes.entrySet()) {
        java.lang.String profileAttributes_ = entry0.getValue();
        JsonElement profileAttributesOut_ = (profileAttributes_ == null) ? JsonNull.INSTANCE : new JsonPrimitive(profileAttributes_);
        profileAttributesOut.add(entry0.getKey(), profileAttributesOut_);
      }
      result.add("profileAttributes", profileAttributesOut);
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

    public static UserImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      UserImpl dto = new UserImpl();
      JsonObject json = jsonElem.getAsJsonObject();

      if (json.has("userId")) {
        JsonElement userIdIn = json.get("userId");
        java.lang.String userIdOut = gson.fromJson(userIdIn, java.lang.String.class);
        dto.setUserId(userIdOut);
      }

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