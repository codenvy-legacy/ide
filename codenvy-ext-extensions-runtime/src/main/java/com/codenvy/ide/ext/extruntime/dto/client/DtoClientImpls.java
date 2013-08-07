/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.dto.client;

@SuppressWarnings({"cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "56ba43ccc89eb770776177a49fc68b56f986803b";


  public static class ApplicationInstanceImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.extruntime.shared.ApplicationInstance {
    protected ApplicationInstanceImpl() {}

    @Override
    public final native int getCodeServerPort() /*-{
      return this["codeServerPort"];
    }-*/;

    public final native ApplicationInstanceImpl setCodeServerPort(int codeServerPort) /*-{
      this["codeServerPort"] = codeServerPort;
      return this;
    }-*/;

    public final native boolean hasCodeServerPort() /*-{
      return this.hasOwnProperty("codeServerPort");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native ApplicationInstanceImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    @Override
    public final native java.lang.String getHost() /*-{
      return this["host"];
    }-*/;

    public final native ApplicationInstanceImpl setHost(java.lang.String host) /*-{
      this["host"] = host;
      return this;
    }-*/;

    public final native boolean hasHost() /*-{
      return this.hasOwnProperty("host");
    }-*/;

    @Override
    public final native int getPort() /*-{
      return this["port"];
    }-*/;

    public final native ApplicationInstanceImpl setPort(int port) /*-{
      this["port"] = port;
      return this;
    }-*/;

    public final native boolean hasPort() /*-{
      return this.hasOwnProperty("port");
    }-*/;

    public static native ApplicationInstanceImpl make() /*-{
      return {

      };
    }-*/;  }

}