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
package com.codenvy.ide.ext.java.dto.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "f1028d86edd4b084c8f48b3194086e7227cf1a05";


  public static class MemberImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.shared.Member {
    protected MemberImpl() {}

    @Override
    public final native int getModifiers() /*-{
      return this["modifiers"];
    }-*/;

    public final native MemberImpl setModifiers(int modifiers) /*-{
      this["modifiers"] = modifiers;
      return this;
    }-*/;

    public final native boolean hasModifiers() /*-{
      return this.hasOwnProperty("modifiers");
    }-*/;

    @Override
    public final native java.lang.String getName() /*-{
      return this["name"];
    }-*/;

    public final native MemberImpl setName(java.lang.String name) /*-{
      this["name"] = name;
      return this;
    }-*/;

    public final native boolean hasName() /*-{
      return this.hasOwnProperty("name");
    }-*/;

    public static native MemberImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class ShortTypeInfoImpl extends MemberImpl implements com.codenvy.ide.ext.java.shared.ShortTypeInfo {
    protected ShortTypeInfoImpl() {}

    @Override
    public final native java.lang.String getType() /*-{
      return this["type"];
    }-*/;

    public final native ShortTypeInfoImpl setType(java.lang.String type) /*-{
      this["type"] = type;
      return this;
    }-*/;

    public final native boolean hasType() /*-{
      return this.hasOwnProperty("type");
    }-*/;

    @Override
    public final native java.lang.String getSignature() /*-{
      return this["signature"];
    }-*/;

    public final native ShortTypeInfoImpl setSignature(java.lang.String signature) /*-{
      this["signature"] = signature;
      return this;
    }-*/;

    public final native boolean hasSignature() /*-{
      return this.hasOwnProperty("signature");
    }-*/;

    public static native ShortTypeInfoImpl make() /*-{
      return {

      };
    }-*/;  }


  public static class TypesListImpl extends com.codenvy.ide.json.js.Jso implements com.codenvy.ide.ext.java.shared.TypesList {
    protected TypesListImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.shared.ShortTypeInfo> getTypes() /*-{
      return this["types"];
    }-*/;

    public final native TypesListImpl setTypes(com.codenvy.ide.json.JsonArray<com.codenvy.ide.ext.java.shared.ShortTypeInfo> types) /*-{
      this["types"] = types;
      return this;
    }-*/;

    public final native boolean hasTypes() /*-{
      return this.hasOwnProperty("types");
    }-*/;

    public static native TypesListImpl make() /*-{
      return {

      };
    }-*/;  }

}