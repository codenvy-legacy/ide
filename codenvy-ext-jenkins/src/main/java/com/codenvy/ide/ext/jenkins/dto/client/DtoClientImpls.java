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
package com.codenvy.ide.ext.jenkins.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "5cf85eb2c8f7381e1458a20a0069a4d706be9198";


  public static class JobImpl extends com.codenvy.ide.json.client.Jso implements com.codenvy.ide.ext.jenkins.shared.Job {
    protected JobImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native JobImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getBuildUrl() /*-{
      return this["buildUrl"];
    }-*/;

    public final native JobImpl setBuildUrl(java.lang.String buildUrl) /*-{
      this["buildUrl"] = buildUrl;
      return this;
    }-*/;

    public final native boolean hasBuildUrl() /*-{
      return this.hasOwnProperty("buildUrl");
    }-*/;

    @Override
    public final native java.lang.String getStatusUrl() /*-{
      return this["statusUrl"];
    }-*/;

    public final native JobImpl setStatusUrl(java.lang.String statusUrl) /*-{
      this["statusUrl"] = statusUrl;
      return this;
    }-*/;

    public final native boolean hasStatusUrl() /*-{
      return this.hasOwnProperty("statusUrl");
    }-*/;

    public static native JobImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class JobStatusImpl extends com.codenvy.ide.json.client.Jso implements com.codenvy.ide.ext.jenkins.shared.JobStatus {
    protected JobStatusImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native JobStatusImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.jenkins.shared.JobStatus.Status getStatus() /*-{
      return @com.codenvy.ide.ext.jenkins.shared.JobStatus.Status::valueOf(Ljava/lang/String;)(this["status"]);
    }-*/;

    public final native JobStatusImpl setStatus(com.codenvy.ide.ext.jenkins.shared.JobStatus.Status status) /*-{
      status = status.@com.codenvy.ide.ext.jenkins.shared.JobStatus.Status::toString()();
      this["status"] = status;
      return this;
    }-*/;

    public final native boolean hasStatus() /*-{
      return this.hasOwnProperty("status");
    }-*/;

    @Override
    public final native java.lang.String getLastBuildResult() /*-{
      return this["lastBuildResult"];
    }-*/;

    public final native JobStatusImpl setLastBuildResult(java.lang.String lastBuildResult) /*-{
      this["lastBuildResult"] = lastBuildResult;
      return this;
    }-*/;

    public final native boolean hasLastBuildResult() /*-{
      return this.hasOwnProperty("lastBuildResult");
    }-*/;

    @Override
    public final native java.lang.String getArtifactUrl() /*-{
      return this["artifactUrl"];
    }-*/;

    public final native JobStatusImpl setArtifactUrl(java.lang.String artifactUrl) /*-{
      this["artifactUrl"] = artifactUrl;
      return this;
    }-*/;

    public final native boolean hasArtifactUrl() /*-{
      return this.hasOwnProperty("artifactUrl");
    }-*/;

    public static native JobStatusImpl make() /*-{
      return {

      };
    }-*/;  }

}