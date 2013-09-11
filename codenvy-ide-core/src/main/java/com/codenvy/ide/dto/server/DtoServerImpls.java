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
package com.codenvy.ide.dto.server;

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

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "7ad4068e7941e9d7bf87807856f452203f8353a1";

  public static class RetainLineImpl extends DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent.RetainLine, JsonSerializable {

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

  public static class RetainImpl extends DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent.Retain, JsonSerializable {

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

  public static class InsertImpl extends DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent.Insert, JsonSerializable {

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

  public static class DocOpImpl implements com.codenvy.ide.dto.DocOp, JsonSerializable {

    public static DocOpImpl make() {
      return new DocOpImpl();
    }

    protected java.util.List<DocOpComponentImpl> components;
    private boolean _hasComponents;

    public boolean hasComponents() {
      return _hasComponents;
    }

    @Override
    public com.codenvy.ide.json.JsonArray<com.codenvy.ide.dto.DocOpComponent> getComponents() {
      ensureComponents();
      return (com.codenvy.ide.json.JsonArray) new com.codenvy.ide.json.java.JsonArrayListAdapter(components);
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

  public static class DeleteImpl extends DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent.Delete, JsonSerializable {

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

  public static class DocOpComponentImpl implements com.codenvy.ide.dto.DocOpComponent, JsonSerializable {

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

}