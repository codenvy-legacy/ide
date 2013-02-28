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
package com.codenvy.vfs.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "86b20dcd80e4972e25174b9dd6c3338f3b70049c";


  public static class ItemImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.Item {
    protected ItemImpl() {}

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
    public final native com.codenvy.vfs.dto.Item.ItemType getItemType() /*-{
      return @com.codenvy.vfs.dto.Item.ItemType::valueOf(Ljava/lang/String;)(this[2]);
    }-*/;

    public final native ItemImpl setItemType(com.codenvy.vfs.dto.Item.ItemType itemType) /*-{
      itemType = itemType.@com.codenvy.vfs.dto.Item.ItemType::toString()();
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
    public final native org.exoplatform.ide.json.shared.JsonArray<com.codenvy.vfs.dto.Property> getProperties() /*-{
      return this[6];
    }-*/;

    public final native ItemImpl setProperties(org.exoplatform.ide.json.shared.JsonArray<com.codenvy.vfs.dto.Property> properties) /*-{
      this[6] = properties;
      return this;
    }-*/;

    public final native boolean hasProperties() /*-{
      return this.hasOwnProperty(6);
    }-*/;

    @Override
    public final native org.exoplatform.ide.json.shared.JsonStringMap<com.codenvy.vfs.dto.Link> getLinks() /*-{
      return this[7];
    }-*/;

    public final native ItemImpl setLinks(org.exoplatform.ide.json.shared.JsonStringMap<com.codenvy.vfs.dto.Link> links) /*-{
      this[7] = links;
      return this;
    }-*/;

    public final native boolean hasLinks() /*-{
      return this.hasOwnProperty(7);
    }-*/;

    public static native ItemImpl make() /*-{
      return [];
    }-*/;  }


  public static class ItemCreatedDtoImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.ItemCreatedDto {
    protected ItemCreatedDtoImpl() {}

    @Override
    public final native com.codenvy.vfs.dto.Item getItem() /*-{
      return this["item"];
    }-*/;

    public final native ItemCreatedDtoImpl setItem(com.codenvy.vfs.dto.Item item) /*-{
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
    }-*/;  }


  public static class ItemDeletedDtoImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.ItemDeletedDto {
    protected ItemDeletedDtoImpl() {}

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
    }-*/;  }


  public static class ItemMovedDtoImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.ItemMovedDto {
    protected ItemMovedDtoImpl() {}

    @Override
    public final native com.codenvy.vfs.dto.Item movedItem() /*-{
      return this["movedItem"];
    }-*/;

    public final native ItemMovedDtoImpl setMovedItem(com.codenvy.vfs.dto.Item movedItem) /*-{
      this["movedItem"] = movedItem;
      return this;
    }-*/;

    public final native boolean hasMovedItem() /*-{
      return this.hasOwnProperty("movedItem");
    }-*/;

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
    }-*/;  }


  public static class ItemRenamedDtoImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.ItemRenamedDto {
    protected ItemRenamedDtoImpl() {}

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
    public final native com.codenvy.vfs.dto.Item renamedItem() /*-{
      return this["renamedItem"];
    }-*/;

    public final native ItemRenamedDtoImpl setRenamedItem(com.codenvy.vfs.dto.Item renamedItem) /*-{
      this["renamedItem"] = renamedItem;
      return this;
    }-*/;

    public final native boolean hasRenamedItem() /*-{
      return this.hasOwnProperty("renamedItem");
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
    }-*/;  }


  public static class LinkImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.Link {
    protected LinkImpl() {}

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
    }-*/;  }


  public static class ProjectClosedDtoImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.ProjectClosedDto {
    protected ProjectClosedDtoImpl() {}

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
    }-*/;  }


  public static class ProjectOpenedDtoImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.ProjectOpenedDto {
    protected ProjectOpenedDtoImpl() {}

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
    }-*/;  }


  public static class PropertyImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.Property {
    protected PropertyImpl() {}

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
    public final native org.exoplatform.ide.json.shared.JsonArray<java.lang.String> getValue() /*-{
      if (!this.hasOwnProperty(1)) {
        this[1] = [];
      }
      return this[1];
    }-*/;

    public final native PropertyImpl setValue(org.exoplatform.ide.json.shared.JsonArray<java.lang.String> value) /*-{
      this[1] = value;
      return this;
    }-*/;

    public final native boolean hasValue() /*-{
      return this.hasOwnProperty(1);
    }-*/;

    public static native PropertyImpl make() /*-{
      return [];
    }-*/;  }

}