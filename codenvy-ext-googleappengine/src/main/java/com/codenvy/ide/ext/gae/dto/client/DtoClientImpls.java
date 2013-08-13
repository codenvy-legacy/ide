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
package com.codenvy.ide.ext.gae.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "efdcf2efa4e7c3f879ee5a55c98126adc24628f6";


  public static class ApplicationInfoImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.gae.shared.ApplicationInfo {
    protected ApplicationInfoImpl() {}

    @Override
    public final native java.lang.String getWebURL() /*-{
      return this["webURL"];
    }-*/;

    public final native ApplicationInfoImpl setWebURL(java.lang.String webURL) /*-{
      this["webURL"] = webURL;
      return this;
    }-*/;

    public final native boolean hasWebURL() /*-{
      return this.hasOwnProperty("webURL");
    }-*/;

    @Override
    public final native java.lang.String getApplicationId() /*-{
      return this["applicationId"];
    }-*/;

    public final native ApplicationInfoImpl setApplicationId(java.lang.String applicationId) /*-{
      this["applicationId"] = applicationId;
      return this;
    }-*/;

    public final native boolean hasApplicationId() /*-{
      return this.hasOwnProperty("applicationId");
    }-*/;

    public static native ApplicationInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class BackendImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.gae.shared.Backend {
    protected BackendImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native BackendImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.gae.shared.Backend.State getState() /*-{
      return @com.codenvy.ide.ext.gae.shared.Backend.State::valueOf(Ljava/lang/String;)(this["state"]);
    }-*/;

    public final native BackendImpl setState(com.codenvy.ide.ext.gae.shared.Backend.State state) /*-{
      state = state.@com.codenvy.ide.ext.gae.shared.Backend.State::toString()();
      this["state"] = state;
      return this;
    }-*/;

    public final native boolean hasState() /*-{
      return this.hasOwnProperty("state");
    }-*/;

    @Override
    public final native java.lang.Boolean isPublic() /*-{
      return this["isPublic"];
    }-*/;

    public final native BackendImpl setIsPublic(java.lang.Boolean isPublic) /*-{
      this["isPublic"] = isPublic;
      return this;
    }-*/;

    public final native boolean hasIsPublic() /*-{
      return this.hasOwnProperty("isPublic");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.gae.shared.Backend.Option> getOptions() /*-{
      _tmp = [];
      this["options"].forEach(function(in1, tmp0) {
        out1 = @com.codenvy.ide.ext.gae.shared.Backend.Option::valueOf(Ljava/lang/String;)(in1);
        _tmp[tmp0] = out1;
      });
      return _tmp;
    }-*/;

    public final native BackendImpl setOptions(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.gae.shared.Backend.Option> options) /*-{
      _tmp = options;
      tmp0 = [];
      _tmp.forEach(function(in1) {
        out1 = in1.@com.codenvy.ide.ext.gae.shared.Backend.Option::toString()();
        tmp0.push(out1);
      });
      options = tmp0;
      this["options"] = options;
      return this;
    }-*/;

    public final native boolean hasOptions() /*-{
      return this.hasOwnProperty("options");
    }-*/;

    @Override
    public final native java.lang.Boolean isFailFast() /*-{
      return this["isFailFast"];
    }-*/;

    public final native BackendImpl setIsFailFast(java.lang.Boolean isFailFast) /*-{
      this["isFailFast"] = isFailFast;
      return this;
    }-*/;

    public final native boolean hasIsFailFast() /*-{
      return this.hasOwnProperty("isFailFast");
    }-*/;

    @Override
    public final native java.lang.String getInstanceClass() /*-{
      return this["instanceClass"];
    }-*/;

    public final native BackendImpl setInstanceClass(java.lang.String instanceClass) /*-{
      this["instanceClass"] = instanceClass;
      return this;
    }-*/;

    public final native boolean hasInstanceClass() /*-{
      return this.hasOwnProperty("instanceClass");
    }-*/;

    @Override
    public final native java.lang.Integer getInstances() /*-{
      return this["instances"];
    }-*/;

    public final native BackendImpl setInstances(java.lang.Integer instances) /*-{
      this["instances"] = instances;
      return this;
    }-*/;

    public final native boolean hasInstances() /*-{
      return this.hasOwnProperty("instances");
    }-*/;

    @Override
    public final native java.lang.Integer getMaxConcurrentRequests() /*-{
      return this["maxConcurrentRequests"];
    }-*/;

    public final native BackendImpl setMaxConcurrentRequests(java.lang.Integer maxConcurrentRequests) /*-{
      this["maxConcurrentRequests"] = maxConcurrentRequests;
      return this;
    }-*/;

    public final native boolean hasMaxConcurrentRequests() /*-{
      return this.hasOwnProperty("maxConcurrentRequests");
    }-*/;

    @Override
    public final native java.lang.Boolean isDynamic() /*-{
      return this["isDynamic"];
    }-*/;

    public final native BackendImpl setIsDynamic(java.lang.Boolean isDynamic) /*-{
      this["isDynamic"] = isDynamic;
      return this;
    }-*/;

    public final native boolean hasIsDynamic() /*-{
      return this.hasOwnProperty("isDynamic");
    }-*/;

    public static native BackendImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class CredentialsImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.gae.shared.Credentials {
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


  public static class CronEntryImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.gae.shared.CronEntry {
    protected CronEntryImpl() {}

    @Override
    public final native java.lang.String getDescription() /*-{
      return this["description"];
    }-*/;

    public final native CronEntryImpl setDescription(java.lang.String description) /*-{
      this["description"] = description;
      return this;
    }-*/;

    public final native boolean hasDescription() /*-{
      return this.hasOwnProperty("description");
    }-*/;

    @Override
    public final native java.lang.String getSchedule() /*-{
      return this["schedule"];
    }-*/;

    public final native CronEntryImpl setSchedule(java.lang.String schedule) /*-{
      this["schedule"] = schedule;
      return this;
    }-*/;

    public final native boolean hasSchedule() /*-{
      return this.hasOwnProperty("schedule");
    }-*/;

    @Override
    public final native java.lang.String getUrl() /*-{
      return this["url"];
    }-*/;

    public final native CronEntryImpl setUrl(java.lang.String url) /*-{
      this["url"] = url;
      return this;
    }-*/;

    public final native boolean hasUrl() /*-{
      return this.hasOwnProperty("url");
    }-*/;

    @Override
    public final native java.lang.String getTimezone() /*-{
      return this["timezone"];
    }-*/;

    public final native CronEntryImpl setTimezone(java.lang.String timezone) /*-{
      this["timezone"] = timezone;
      return this;
    }-*/;

    public final native boolean hasTimezone() /*-{
      return this.hasOwnProperty("timezone");
    }-*/;

    @Override
    public final native java.lang.Object getNextTimesIterato() /*-{
      return this["nextTimesIterato"];
    }-*/;

    public final native CronEntryImpl setNextTimesIterato(java.lang.Object nextTimesIterato) /*-{
      this["nextTimesIterato"] = nextTimesIterato;
      return this;
    }-*/;

    public final native boolean hasNextTimesIterato() /*-{
      return this.hasOwnProperty("nextTimesIterato");
    }-*/;

    public static native CronEntryImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class GaeUserImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.gae.shared.GaeUser {
    protected GaeUserImpl() {}

    @Override
    public final native java.lang.String getId() /*-{
      return this["id"];
    }-*/;

    public final native GaeUserImpl setId(java.lang.String id) /*-{
      this["id"] = id;
      return this;
    }-*/;

    public final native boolean hasId() /*-{
      return this.hasOwnProperty("id");
    }-*/;

    @Override
    public final native java.lang.String getEmail() /*-{
      return this["email"];
    }-*/;

    public final native GaeUserImpl setEmail(java.lang.String email) /*-{
      this["email"] = email;
      return this;
    }-*/;

    public final native boolean hasEmail() /*-{
      return this.hasOwnProperty("email");
    }-*/;

    @Override
    public final native com.codenvy.ide.ext.gae.shared.Token getToken() /*-{
      return this["token"];
    }-*/;

    public final native GaeUserImpl setToken(com.codenvy.ide.ext.gae.shared.Token token) /*-{
      this["token"] = token;
      return this;
    }-*/;

    public final native boolean hasToken() /*-{
      return this.hasOwnProperty("token");
    }-*/;

    public static native GaeUserImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ResourceLimitImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.gae.shared.ResourceLimit {
    protected ResourceLimitImpl() {}

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native ResourceLimitImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native double getValue() /*-{
      return this["value"];
    }-*/;

    public final native ResourceLimitImpl setValue(double value) /*-{
      this["value"] = value;
      return this;
    }-*/;

    public final native boolean hasValue() /*-{
      return this.hasOwnProperty("value");
    }-*/;

    public static native ResourceLimitImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class TokenImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.gae.shared.Token {
    protected TokenImpl() {}

    @Override
    public final native java.lang.String getScope() /*-{
      return this["scope"];
    }-*/;

    public final native TokenImpl setScope(java.lang.String scope) /*-{
      this["scope"] = scope;
      return this;
    }-*/;

    public final native boolean hasScope() /*-{
      return this.hasOwnProperty("scope");
    }-*/;

    @Override
    public final native java.lang.String getToken() /*-{
      return this["token"];
    }-*/;

    public final native TokenImpl setToken(java.lang.String token) /*-{
      this["token"] = token;
      return this;
    }-*/;

    public final native boolean hasToken() /*-{
      return this.hasOwnProperty("token");
    }-*/;

    public static native TokenImpl make() /*-{
      return {

      };
    }-*/;  }

}