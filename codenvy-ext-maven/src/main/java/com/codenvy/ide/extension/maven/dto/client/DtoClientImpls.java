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
package com.codenvy.ide.extension.maven.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "5bcd6573f4b7af4db3aaa4851ea8a94304b99c9b";


  public static class BuildStatusImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.extension.maven.shared.BuildStatus {
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