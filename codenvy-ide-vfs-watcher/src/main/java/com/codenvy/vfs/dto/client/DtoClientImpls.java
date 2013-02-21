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

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "e67dddb2fe5089b103e87f7007b04d9b0871e1fc";


  public static class FileDeletedDtoImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.FileDeletedDto {
    protected FileDeletedDtoImpl() {}

    @Override
    public final native java.lang.String getFileId() /*-{
      return this["fileId"];
    }-*/;

    public final native FileDeletedDtoImpl setFileId(java.lang.String fileId) /*-{
      this["fileId"] = fileId;
      return this;
    }-*/;

    public final native boolean hasFileId() /*-{
      return this.hasOwnProperty("fileId");
    }-*/;

    @Override
    public final native java.lang.String getFilePath() /*-{
      return this["filePath"];
    }-*/;

    public final native FileDeletedDtoImpl setFilePath(java.lang.String filePath) /*-{
      this["filePath"] = filePath;
      return this;
    }-*/;

    public final native boolean hasFilePath() /*-{
      return this.hasOwnProperty("filePath");
    }-*/;

    public static native FileDeletedDtoImpl make() /*-{
      return {
        _type: 3
      };
    }-*/;  }


  public static class ProjectClosedDtoImpl extends org.exoplatform.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.vfs.dto.ProjectClosedDto {
    protected ProjectClosedDtoImpl() {}

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

}