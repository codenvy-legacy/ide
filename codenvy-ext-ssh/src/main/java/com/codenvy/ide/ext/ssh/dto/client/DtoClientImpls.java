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
package com.codenvy.ide.ext.ssh.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "049a42bf8edb11fcb3dbe264ba9241d81cd1dded";


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


  public static class KeyItemImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.ssh.shared.KeyItem {
    protected KeyItemImpl() {}

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

}