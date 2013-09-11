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
package com.codenvy.ide.ext.cloudbees.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "a44b6af3b1980efd1526089bcd3c28471860120d";


  public static class CloudBeesAccountImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.cloudbees.shared.CloudBeesAccount {
    protected CloudBeesAccountImpl() {}

    @Override
    public final native java.lang.String getCompany() /*-{
      return this["company"];
    }-*/;

    public final native CloudBeesAccountImpl setCompany(java.lang.String company) /*-{
      this["company"] = company;
      return this;
    }-*/;

    public final native boolean hasCompany() /*-{
      return this.hasOwnProperty("company");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native CloudBeesAccountImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    public static native CloudBeesAccountImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CredentialsImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.cloudbees.shared.Credentials {
    protected CredentialsImpl() {}

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

    @Override
    public final native java.lang.String getEmail() /*-{
      return this["email"];
    }-*/;

    public final native CredentialsImpl setEmail(java.lang.String email) /*-{
      this["email"] = email;
      return this;
    }-*/;

    public final native boolean hasEmail() /*-{
      return this.hasOwnProperty("email");
    }-*/;

    public static native CredentialsImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CloudBeesUserImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.cloudbees.shared.CloudBeesUser {
    protected CloudBeesUserImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.cloudbees.shared.CloudBeesSshKey> getSsh_keys() /*-{
      return this["ssh_keys"];
    }-*/;

    public final native CloudBeesUserImpl setSsh_keys(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.cloudbees.shared.CloudBeesSshKey> ssh_keys) /*-{
      this["ssh_keys"] = ssh_keys;
      return this;
    }-*/;

    public final native boolean hasSsh_keys() /*-{
      return this.hasOwnProperty("ssh_keys");
    }-*/;

    @Override
    public final native java.lang.String getLast_name() /*-{
      return this["last_name"];
    }-*/;

    public final native CloudBeesUserImpl setLast_name(java.lang.String last_name) /*-{
      this["last_name"] = last_name;
      return this;
    }-*/;

    public final native boolean hasLast_name() /*-{
      return this.hasOwnProperty("last_name");
    }-*/;

    @Override
    public final native java.lang.String getFirst_name() /*-{
      return this["first_name"];
    }-*/;

    public final native CloudBeesUserImpl setFirst_name(java.lang.String first_name) /*-{
      this["first_name"] = first_name;
      return this;
    }-*/;

    public final native boolean hasFirst_name() /*-{
      return this.hasOwnProperty("first_name");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.cloudbees.shared.CloudBeesAccount> getAccounts() /*-{
      return this["accounts"];
    }-*/;

    public final native CloudBeesUserImpl setAccounts(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.cloudbees.shared.CloudBeesAccount> accounts) /*-{
      this["accounts"] = accounts;
      return this;
    }-*/;

    public final native boolean hasAccounts() /*-{
      return this.hasOwnProperty("accounts");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native CloudBeesUserImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getId() /*-{
      return this["id"];
    }-*/;

    public final native CloudBeesUserImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    @Override
    public final native java.lang.String getRole() /*-{
      return this["role"];
    }-*/;

    public final native CloudBeesUserImpl setRole(java.lang.String role) /*-{
      this["role"] = role;
      return this;
    }-*/;

    public final native boolean hasRole() /*-{
      return this.hasOwnProperty("role");
    }-*/;

    @Override
    public final native java.lang.String getPassword() /*-{
      return this["password"];
    }-*/;

    public final native CloudBeesUserImpl setPassword(java.lang.String password) /*-{
      this["password"] = password;
      return this;
    }-*/;

    public final native boolean hasPassword() /*-{
      return this.hasOwnProperty("password");
    }-*/;

    @Override
    public final native java.lang.String getEmail() /*-{
      return this["email"];
    }-*/;

    public final native CloudBeesUserImpl setEmail(java.lang.String email) /*-{
      this["email"] = email;
      return this;
    }-*/;

    public final native boolean hasEmail() /*-{
      return this.hasOwnProperty("email");
    }-*/;

    public static native CloudBeesUserImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ApplicationInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo {
    protected ApplicationInfoImpl() {}

    @Override
    public final native java.lang.String getInstances() /*-{
      return this["instances"];
    }-*/;

    public final native ApplicationInfoImpl setInstances(java.lang.String instances) /*-{
      this["instances"] = instances;
      return this;
    }-*/;

    public final native boolean hasInstances() /*-{
      return this.hasOwnProperty("instances");
    }-*/;

    @Override
    public final native java.lang.String getIdleTimeout() /*-{
      return this["idleTimeout"];
    }-*/;

    public final native ApplicationInfoImpl setIdleTimeout(java.lang.String idleTimeout) /*-{
      this["idleTimeout"] = idleTimeout;
      return this;
    }-*/;

    public final native boolean hasIdleTimeout() /*-{
      return this.hasOwnProperty("idleTimeout");
    }-*/;

    @Override
    public final native java.lang.String getServerPool() /*-{
      return this["serverPool"];
    }-*/;

    public final native ApplicationInfoImpl setServerPool(java.lang.String serverPool) /*-{
      this["serverPool"] = serverPool;
      return this;
    }-*/;

    public final native boolean hasServerPool() /*-{
      return this.hasOwnProperty("serverPool");
    }-*/;

    @Override
    public final native java.lang.String getClusterSize() /*-{
      return this["clusterSize"];
    }-*/;

    public final native ApplicationInfoImpl setClusterSize(java.lang.String clusterSize) /*-{
      this["clusterSize"] = clusterSize;
      return this;
    }-*/;

    public final native boolean hasClusterSize() /*-{
      return this.hasOwnProperty("clusterSize");
    }-*/;

    @Override
    public final native java.lang.String getTitle() /*-{
      return this["title"];
    }-*/;

    public final native ApplicationInfoImpl setTitle(java.lang.String title) /*-{
      this["title"] = title;
      return this;
    }-*/;

    public final native boolean hasTitle() /*-{
      return this.hasOwnProperty("title");
    }-*/;

    @Override
    public final native java.lang.String getMaxMemory() /*-{
      return this["maxMemory"];
    }-*/;

    public final native ApplicationInfoImpl setMaxMemory(java.lang.String maxMemory) /*-{
      this["maxMemory"] = maxMemory;
      return this;
    }-*/;

    public final native boolean hasMaxMemory() /*-{
      return this.hasOwnProperty("maxMemory");
    }-*/;

    @Override
    public final native java.lang.String getSecurityMode() /*-{
      return this["securityMode"];
    }-*/;

    public final native ApplicationInfoImpl setSecurityMode(java.lang.String securityMode) /*-{
      this["securityMode"] = securityMode;
      return this;
    }-*/;

    public final native boolean hasSecurityMode() /*-{
      return this.hasOwnProperty("securityMode");
    }-*/;

    @Override
    public final native java.lang.String getId() /*-{
      return this["id"];
    }-*/;

    public final native ApplicationInfoImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    @Override
    public final native java.lang.String getStatus() /*-{
      return this["status"];
    }-*/;

    public final native ApplicationInfoImpl setStatus(java.lang.String status) /*-{
      this["status"] = status;
      return this;
    }-*/;

    public final native boolean hasStatus() /*-{
      return this.hasOwnProperty("status");
    }-*/;

    @Override
    public final native java.lang.String getUrl() /*-{
      return this["url"];
    }-*/;

    public final native ApplicationInfoImpl setUrl(java.lang.String url) /*-{
      this["url"] = url;
      return this;
    }-*/;

    public final native boolean hasUrl() /*-{
      return this.hasOwnProperty("url");
    }-*/;

    @Override
    public final native java.lang.String getContainer() /*-{
      return this["container"];
    }-*/;

    public final native ApplicationInfoImpl setContainer(java.lang.String container) /*-{
      this["container"] = container;
      return this;
    }-*/;

    public final native boolean hasContainer() /*-{
      return this.hasOwnProperty("container");
    }-*/;

    public static native ApplicationInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CloudBeesSshKeyImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.cloudbees.shared.CloudBeesSshKey {
    protected CloudBeesSshKeyImpl() {}

    @Override
    public final native java.lang.String getFingerprint() /*-{
      return this["fingerprint"];
    }-*/;

    public final native CloudBeesSshKeyImpl setFingerprint(java.lang.String fingerprint) /*-{
      this["fingerprint"] = fingerprint;
      return this;
    }-*/;

    public final native boolean hasFingerprint() /*-{
      return this.hasOwnProperty("fingerprint");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native CloudBeesSshKeyImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getContent() /*-{
      return this["content"];
    }-*/;

    public final native CloudBeesSshKeyImpl setContent(java.lang.String content) /*-{
      this["content"] = content;
      return this;
    }-*/;

    public final native boolean hasContent() /*-{
      return this.hasOwnProperty("content");
    }-*/;

    public static native CloudBeesSshKeyImpl make() /*-{
      return {

      };
    }-*/;  }

}