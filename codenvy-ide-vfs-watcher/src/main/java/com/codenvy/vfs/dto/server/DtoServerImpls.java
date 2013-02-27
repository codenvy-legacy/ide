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

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "b05ff1873e61366aa9dcddb6eee35176ab925a1e";

  public static class ItemImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.Item, JsonSerializable {

    public static ItemImpl make() {
      return new ItemImpl();
    }

    protected java.lang.String id;
    private boolean _hasId;
    protected java.lang.String name;
    private boolean _hasName;
    protected com.codenvy.vfs.dto.Item.ItemType itemType;
    private boolean _hasItemType;
    protected java.lang.String path;
    private boolean _hasPath;
    protected java.lang.String parentId;
    private boolean _hasParentId;
    protected java.lang.String mimeType;
    private boolean _hasMimeType;
    protected java.util.List<PropertyImpl> properties;
    private boolean _hasProperties;
    protected java.util.Map<String, LinkImpl> links;
    private boolean _hasLinks;

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
    public com.codenvy.vfs.dto.Item.ItemType getItemType() {
      return itemType;
    }

    public ItemImpl setItemType(com.codenvy.vfs.dto.Item.ItemType v) {
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
    public org.exoplatform.ide.json.shared.JsonArray<com.codenvy.vfs.dto.Property> getProperties() {
      ensureProperties();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(properties);
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
    public org.exoplatform.ide.json.shared.JsonStringMap<com.codenvy.vfs.dto.Link> getLinks() {
      ensureLinks();
      return (org.exoplatform.ide.json.shared.JsonStringMap) new org.exoplatform.ide.json.server.JsonStringMapAdapter(links);
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
      ItemImpl other = (ItemImpl) o;
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
        com.codenvy.vfs.dto.Item.ItemType itemTypeOut = gson.fromJson(itemTypeIn, com.codenvy.vfs.dto.Item.ItemType.class);
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

  public static class ItemCreatedDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.ItemCreatedDto, JsonSerializable {

    private ItemCreatedDtoImpl() {
      super(4);
    }

    protected ItemCreatedDtoImpl(int type) {
      super(type);
    }

    public static ItemCreatedDtoImpl make() {
      return new ItemCreatedDtoImpl();
    }

    protected ItemImpl item;
    private boolean _hasItem;

    public boolean hasItem() {
      return _hasItem;
    }

    @Override
    public com.codenvy.vfs.dto.Item getItem() {
      return item;
    }

    public ItemCreatedDtoImpl setItem(ItemImpl v) {
      _hasItem = true;
      item = v;
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
      ItemCreatedDtoImpl other = (ItemCreatedDtoImpl) o;
      if (this._hasItem != other._hasItem) {
        return false;
      }
      if (this._hasItem) {
        if (!this.item.equals(other.item)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasItem ? item.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement itemOut = item == null ? JsonNull.INSTANCE : item.toJsonElement();
      result.add("item", itemOut);
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

      return dto;
    }
    public static ItemCreatedDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ItemDeletedDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.ItemDeletedDto, JsonSerializable {

    private ItemDeletedDtoImpl() {
      super(3);
    }

    protected ItemDeletedDtoImpl(int type) {
      super(type);
    }

    public static ItemDeletedDtoImpl make() {
      return new ItemDeletedDtoImpl();
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

    public ItemDeletedDtoImpl setFileId(java.lang.String v) {
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

    public ItemDeletedDtoImpl setFilePath(java.lang.String v) {
      _hasFilePath = true;
      filePath = v;
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
      ItemDeletedDtoImpl other = (ItemDeletedDtoImpl) o;
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

    public static ItemDeletedDtoImpl fromJsonElement(JsonElement jsonElem) {
      if (jsonElem == null || jsonElem.isJsonNull()) {
        return null;
      }

      ItemDeletedDtoImpl dto = new ItemDeletedDtoImpl();
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
    public static ItemDeletedDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class ItemMovedDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.ItemMovedDto, JsonSerializable {

    private ItemMovedDtoImpl() {
      super(5);
    }

    protected ItemMovedDtoImpl(int type) {
      super(type);
    }

    public static ItemMovedDtoImpl make() {
      return new ItemMovedDtoImpl();
    }

    protected ItemImpl movedItem;
    private boolean _hasMovedItem;
    protected java.lang.String oldPath;
    private boolean _hasOldPath;

    public boolean hasMovedItem() {
      return _hasMovedItem;
    }

    @Override
    public com.codenvy.vfs.dto.Item movedItem() {
      return movedItem;
    }

    public ItemMovedDtoImpl setMovedItem(ItemImpl v) {
      _hasMovedItem = true;
      movedItem = v;
      return this;
    }

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

    @Override
    public boolean equals(Object o) {
      if (!super.equals(o)) {
        return false;
      }
      if (!(o instanceof ItemMovedDtoImpl)) {
        return false;
      }
      ItemMovedDtoImpl other = (ItemMovedDtoImpl) o;
      if (this._hasMovedItem != other._hasMovedItem) {
        return false;
      }
      if (this._hasMovedItem) {
        if (!this.movedItem.equals(other.movedItem)) {
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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasMovedItem ? movedItem.hashCode() : 0);
      hash = hash * 31 + (_hasOldPath ? oldPath.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement movedItemOut = movedItem == null ? JsonNull.INSTANCE : movedItem.toJsonElement();
      result.add("movedItem", movedItemOut);

      JsonElement oldPathOut = (oldPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(oldPath);
      result.add("oldPath", oldPathOut);
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

      if (json.has("movedItem")) {
        JsonElement movedItemIn = json.get("movedItem");
        ItemImpl movedItemOut = ItemImpl.fromJsonElement(movedItemIn);
        dto.setMovedItem(movedItemOut);
      }

      if (json.has("oldPath")) {
        JsonElement oldPathIn = json.get("oldPath");
        java.lang.String oldPathOut = gson.fromJson(oldPathIn, java.lang.String.class);
        dto.setOldPath(oldPathOut);
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

  public static class ItemRenamedDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.ItemRenamedDto, JsonSerializable {

    private ItemRenamedDtoImpl() {
      super(6);
    }

    protected ItemRenamedDtoImpl(int type) {
      super(type);
    }

    public static ItemRenamedDtoImpl make() {
      return new ItemRenamedDtoImpl();
    }

    protected java.lang.String oldPath;
    private boolean _hasOldPath;
    protected ItemImpl renamedItem;
    private boolean _hasRenamedItem;

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

    public boolean hasRenamedItem() {
      return _hasRenamedItem;
    }

    @Override
    public com.codenvy.vfs.dto.Item renamedItem() {
      return renamedItem;
    }

    public ItemRenamedDtoImpl setRenamedItem(ItemImpl v) {
      _hasRenamedItem = true;
      renamedItem = v;
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
      ItemRenamedDtoImpl other = (ItemRenamedDtoImpl) o;
      if (this._hasOldPath != other._hasOldPath) {
        return false;
      }
      if (this._hasOldPath) {
        if (!this.oldPath.equals(other.oldPath)) {
          return false;
        }
      }
      if (this._hasRenamedItem != other._hasRenamedItem) {
        return false;
      }
      if (this._hasRenamedItem) {
        if (!this.renamedItem.equals(other.renamedItem)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasOldPath ? oldPath.hashCode() : 0);
      hash = hash * 31 + (_hasRenamedItem ? renamedItem.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement oldPathOut = (oldPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(oldPath);
      result.add("oldPath", oldPathOut);

      JsonElement renamedItemOut = renamedItem == null ? JsonNull.INSTANCE : renamedItem.toJsonElement();
      result.add("renamedItem", renamedItemOut);
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

      if (json.has("oldPath")) {
        JsonElement oldPathIn = json.get("oldPath");
        java.lang.String oldPathOut = gson.fromJson(oldPathIn, java.lang.String.class);
        dto.setOldPath(oldPathOut);
      }

      if (json.has("renamedItem")) {
        JsonElement renamedItemIn = json.get("renamedItem");
        ItemImpl renamedItemOut = ItemImpl.fromJsonElement(renamedItemIn);
        dto.setRenamedItem(renamedItemOut);
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

  public static class LinkImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.Link, JsonSerializable {

    public static LinkImpl make() {
      return new LinkImpl();
    }

    protected java.lang.String href;
    private boolean _hasHref;
    protected java.lang.String rel;
    private boolean _hasRel;
    protected java.lang.String typeLink;
    private boolean _hasTypeLink;

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
      LinkImpl other = (LinkImpl) o;
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

  public static class ProjectClosedDtoImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.ProjectClosedDto, JsonSerializable {

    private ProjectClosedDtoImpl() {
      super(2);
    }

    protected ProjectClosedDtoImpl(int type) {
      super(type);
    }

    protected java.lang.String projectId;
    private boolean _hasProjectId;
    protected java.lang.String projectPath;
    private boolean _hasProjectPath;
    protected java.lang.String vfsId;
    private boolean _hasVfsId;

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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      hash = hash * 31 + (_hasProjectPath ? projectPath.hashCode() : 0);
      hash = hash * 31 + (_hasVfsId ? vfsId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
      result.add("projectId", projectIdOut);

      JsonElement projectPathOut = (projectPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectPath);
      result.add("projectPath", projectPathOut);

      JsonElement vfsIdOut = (vfsId == null) ? JsonNull.INSTANCE : new JsonPrimitive(vfsId);
      result.add("vfsId", vfsIdOut);
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
    protected java.lang.String projectPath;
    private boolean _hasProjectPath;
    protected java.lang.String vfsId;
    private boolean _hasVfsId;

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
      return true;
    }

    @Override
    public int hashCode() {
      int hash = super.hashCode();
      hash = hash * 31 + (_hasProjectId ? projectId.hashCode() : 0);
      hash = hash * 31 + (_hasProjectPath ? projectPath.hashCode() : 0);
      hash = hash * 31 + (_hasVfsId ? vfsId.hashCode() : 0);
      return hash;
    }

    @Override
    public JsonElement toJsonElement() {
      JsonObject result = new JsonObject();

      JsonElement projectIdOut = (projectId == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectId);
      result.add("projectId", projectIdOut);

      JsonElement projectPathOut = (projectPath == null) ? JsonNull.INSTANCE : new JsonPrimitive(projectPath);
      result.add("projectPath", projectPathOut);

      JsonElement vfsIdOut = (vfsId == null) ? JsonNull.INSTANCE : new JsonPrimitive(vfsId);
      result.add("vfsId", vfsIdOut);
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

      return dto;
    }
    public static ProjectOpenedDtoImpl fromJsonString(String jsonString) {
      if (jsonString == null) {
        return null;
      }

      return fromJsonElement(new JsonParser().parse(jsonString));
    }
  }

  public static class PropertyImpl extends org.exoplatform.ide.dtogen.server.RoutableDtoServerImpl implements com.codenvy.vfs.dto.Property, JsonSerializable {

    public static PropertyImpl make() {
      return new PropertyImpl();
    }

    protected java.lang.String name;
    private boolean _hasName;
    protected java.util.List<java.lang.String> value;
    private boolean _hasValue;

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
    public org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getValue() {
      ensureValue();
      return (org.exoplatform.ide.json.shared.JsonArray) new org.exoplatform.ide.json.server.JsonArrayListAdapter(value);
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
      PropertyImpl other = (PropertyImpl) o;
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

}