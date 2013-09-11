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
package com.codenvy.ide.ext.jenkins.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "bb20f756eceb2c658af24800395de75fe9d9ce09";


  public static class JobImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.jenkins.shared.Job {
    protected JobImpl() {}

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

    public static native JobImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class JobStatusImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.jenkins.shared.JobStatus {
    protected JobStatusImpl() {}

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

    public static native JobStatusImpl make() /*-{
      return {

      };
    }-*/;  }

}