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
package com.codenvy.ide.extension.maven.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "5bcd6573f4b7af4db3aaa4851ea8a94304b99c9b";


  public static class BuildStatusImpl extends com.codenvy.ide.json.client.Jso implements com.codenvy.ide.extension.maven.shared.BuildStatus {
    protected BuildStatusImpl() {}

    @Override
    public final native java.lang.String getTime() /*-{
      return this["time"];
    }-*/;

    public final native BuildStatusImpl setTime(java.lang.String time) /*-{
      this["time"] = time;
      return this;
    }-*/;

    public final native boolean hasTime() /*-{
      return this.hasOwnProperty("time");
    }-*/;

    @Override
    public final native int getExitCode() /*-{
      return this["exitCode"];
    }-*/;

    public final native BuildStatusImpl setExitCode(int exitCode) /*-{
      this["exitCode"] = exitCode;
      return this;
    }-*/;

    public final native boolean hasExitCode() /*-{
      return this.hasOwnProperty("exitCode");
    }-*/;

    @Override
    public final native com.codenvy.ide.extension.maven.shared.BuildStatus.Status getStatus() /*-{
      return @com.codenvy.ide.extension.maven.shared.BuildStatus.Status::valueOf(Ljava/lang/String;)(this["status"]);
    }-*/;

    public final native BuildStatusImpl setStatus(com.codenvy.ide.extension.maven.shared.BuildStatus.Status status) /*-{
      status = status.@com.codenvy.ide.extension.maven.shared.BuildStatus.Status::toString()();
      this["status"] = status;
      return this;
    }-*/;

    public final native boolean hasStatus() /*-{
      return this.hasOwnProperty("status");
    }-*/;

    @Override
    public final native java.lang.String getError() /*-{
      return this["error"];
    }-*/;

    public final native BuildStatusImpl setError(java.lang.String error) /*-{
      this["error"] = error;
      return this;
    }-*/;

    public final native boolean hasError() /*-{
      return this.hasOwnProperty("error");
    }-*/;

    @Override
    public final native java.lang.String getDownloadUrl() /*-{
      return this["downloadUrl"];
    }-*/;

    public final native BuildStatusImpl setDownloadUrl(java.lang.String downloadUrl) /*-{
      this["downloadUrl"] = downloadUrl;
      return this;
    }-*/;

    public final native boolean hasDownloadUrl() /*-{
      return this.hasOwnProperty("downloadUrl");
    }-*/;

    public static native BuildStatusImpl make() /*-{
      return {

      };
    }-*/;  }

}