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
package com.codenvy.ide.ext.ssh.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "cf1756076d885cef068c0f510a14e94de6119419";


  public static class GenKeyRequestImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.ssh.shared.GenKeyRequest {
    protected GenKeyRequestImpl() {}

    @Override
    public final native java.lang.String getHost() /*-{
      return this["host"];
    }-*/;

    public final native GenKeyRequestImpl setHost(java.lang.String host) /*-{
      this["host"] = host;
      return this;
    }-*/;

    public final native boolean hasHost() /*-{
      return this.hasOwnProperty("host");
    }-*/;

    @Override
    public final native java.lang.String getComment() /*-{
      return this["comment"];
    }-*/;

    public final native GenKeyRequestImpl setComment(java.lang.String comment) /*-{
      this["comment"] = comment;
      return this;
    }-*/;

    public final native boolean hasComment() /*-{
      return this.hasOwnProperty("comment");
    }-*/;

    @Override
    public final native java.lang.String getPassphrase() /*-{
      return this["passphrase"];
    }-*/;

    public final native GenKeyRequestImpl setPassphrase(java.lang.String passphrase) /*-{
      this["passphrase"] = passphrase;
      return this;
    }-*/;

    public final native boolean hasPassphrase() /*-{
      return this.hasOwnProperty("passphrase");
    }-*/;

    public static native GenKeyRequestImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class PublicKeyImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.ssh.shared.PublicKey {
    protected PublicKeyImpl() {}

    @Override
    public final native java.lang.String getKey() /*-{
      return this["key"];
    }-*/;

    public final native PublicKeyImpl setKey(java.lang.String key) /*-{
      this["key"] = key;
      return this;
    }-*/;

    public final native boolean hasKey() /*-{
      return this.hasOwnProperty("key");
    }-*/;

    @Override
    public final native java.lang.String getHost() /*-{
      return this["host"];
    }-*/;

    public final native PublicKeyImpl setHost(java.lang.String host) /*-{
      this["host"] = host;
      return this;
    }-*/;

    public final native boolean hasHost() /*-{
      return this.hasOwnProperty("host");
    }-*/;

    public static native PublicKeyImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class KeyItemImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.ssh.shared.KeyItem {
    protected KeyItemImpl() {}

    @Override
    public final native java.lang.String getPublicKeyURL() /*-{
      return this["publicKeyURL"];
    }-*/;

    public final native KeyItemImpl setPublicKeyURL(java.lang.String publicKeyURL) /*-{
      this["publicKeyURL"] = publicKeyURL;
      return this;
    }-*/;

    public final native boolean hasPublicKeyURL() /*-{
      return this.hasOwnProperty("publicKeyURL");
    }-*/;

    @Override
    public final native java.lang.String getRemoveKeyURL() /*-{
      return this["removeKeyURL"];
    }-*/;

    public final native KeyItemImpl setRemoveKeyURL(java.lang.String removeKeyURL) /*-{
      this["removeKeyURL"] = removeKeyURL;
      return this;
    }-*/;

    public final native boolean hasRemoveKeyURL() /*-{
      return this.hasOwnProperty("removeKeyURL");
    }-*/;

    @Override
    public final native java.lang.String getHost() /*-{
      return this["host"];
    }-*/;

    public final native KeyItemImpl setHost(java.lang.String host) /*-{
      this["host"] = host;
      return this;
    }-*/;

    public final native boolean hasHost() /*-{
      return this.hasOwnProperty("host");
    }-*/;

    public static native KeyItemImpl make() /*-{
      return {

      };
    }-*/;  }

}