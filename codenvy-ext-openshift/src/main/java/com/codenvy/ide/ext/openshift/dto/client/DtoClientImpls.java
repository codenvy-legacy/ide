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
package com.codenvy.ide.ext.openshift.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "2b96fd0ee15491ddaca2e409b8ccf0d67c15f074";


  public static class CredentialsImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.openshift.shared.Credentials {
    protected CredentialsImpl() {}

    @Override
    public final native java.lang.String getRhlogin() /*-{
      return this["rhlogin"];
    }-*/;

    public final native CredentialsImpl setRhlogin(java.lang.String rhlogin) /*-{
      this["rhlogin"] = rhlogin;
      return this;
    }-*/;

    public final native boolean hasRhlogin() /*-{
      return this.hasOwnProperty("rhlogin");
    }-*/;

    @Override
    public final native java.lang.String getPassword() /*-{
      return this["password"];
    }-*/;

    public final native CredentialsImpl setPassword(java.lang.String password) /*-{
      this["password"] = password;
      return this;
    }-*/;

    public final native boolean hasPassword() /*-{
      return this.hasOwnProperty("password");
    }-*/;

    public static native CredentialsImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class RHUserInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.openshift.shared.RHUserInfo {
    protected RHUserInfoImpl() {}

    @Override
    public final native java.lang.String getRhcDomain() /*-{
      return this["rhcDomain"];
    }-*/;

    public final native RHUserInfoImpl setRhcDomain(java.lang.String rhcDomain) /*-{
      this["rhcDomain"] = rhcDomain;
      return this;
    }-*/;

    public final native boolean hasRhcDomain() /*-{
      return this.hasOwnProperty("rhcDomain");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.openshift.shared.AppInfo> getApps() /*-{
      return this["apps"];
    }-*/;

    public final native RHUserInfoImpl setApps(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.openshift.shared.AppInfo> apps) /*-{
      this["apps"] = apps;
      return this;
    }-*/;

    public final native boolean hasApps() /*-{
      return this.hasOwnProperty("apps");
    }-*/;

    @Override
    public final native java.lang.String getUuid() /*-{
      return this["uuid"];
    }-*/;

    public final native RHUserInfoImpl setUuid(java.lang.String uuid) /*-{
      this["uuid"] = uuid;
      return this;
    }-*/;

    public final native boolean hasUuid() /*-{
      return this.hasOwnProperty("uuid");
    }-*/;

    @Override
    public final native java.lang.String getRhlogin() /*-{
      return this["rhlogin"];
    }-*/;

    public final native RHUserInfoImpl setRhlogin(java.lang.String rhlogin) /*-{
      this["rhlogin"] = rhlogin;
      return this;
    }-*/;

    public final native boolean hasRhlogin() /*-{
      return this.hasOwnProperty("rhlogin");
    }-*/;

    @Override
    public final native java.lang.String getNamespace() /*-{
      return this["namespace"];
    }-*/;

    public final native RHUserInfoImpl setNamespace(java.lang.String namespace) /*-{
      this["namespace"] = namespace;
      return this;
    }-*/;

    public final native boolean hasNamespace() /*-{
      return this.hasOwnProperty("namespace");
    }-*/;

    public static native RHUserInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class AppInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.openshift.shared.AppInfo {
    protected AppInfoImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge> getEmbeddedCartridges() /*-{
      return this["embeddedCartridges"];
    }-*/;

    public final native AppInfoImpl setEmbeddedCartridges(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge> embeddedCartridges) /*-{
      this["embeddedCartridges"] = embeddedCartridges;
      return this;
    }-*/;

    public final native boolean hasEmbeddedCartridges() /*-{
      return this.hasOwnProperty("embeddedCartridges");
    }-*/;

    @Override
    public final native double getCreationTime() /*-{
      return this["creationTime"];
    }-*/;

    public final native AppInfoImpl setCreationTime(double creationTime) /*-{
      this["creationTime"] = creationTime;
      return this;
    }-*/;

    public final native boolean hasCreationTime() /*-{
      return this.hasOwnProperty("creationTime");
    }-*/;

    @Override
    public final native java.lang.String getPublicUrl() /*-{
      return this["publicUrl"];
    }-*/;

    public final native AppInfoImpl setPublicUrl(java.lang.String publicUrl) /*-{
      this["publicUrl"] = publicUrl;
      return this;
    }-*/;

    public final native boolean hasPublicUrl() /*-{
      return this.hasOwnProperty("publicUrl");
    }-*/;

    @Override
    public final native java.lang.String getGitUrl() /*-{
      return this["gitUrl"];
    }-*/;

    public final native AppInfoImpl setGitUrl(java.lang.String gitUrl) /*-{
      this["gitUrl"] = gitUrl;
      return this;
    }-*/;

    public final native boolean hasGitUrl() /*-{
      return this.hasOwnProperty("gitUrl");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native AppInfoImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getType() /*-{
      return this["type"];
    }-*/;

    public final native AppInfoImpl setType(java.lang.String type) /*-{
      this["type"] = type;
      return this;
    }-*/;

    public final native boolean hasType() /*-{
      return this.hasOwnProperty("type");
    }-*/;

    public static native AppInfoImpl make() /*-{
      return {

      };
    }-*/;  }

}