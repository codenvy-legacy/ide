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
package com.codenvy.ide.client;



@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "644381d81eb2a0c00d85dd595f3b8d3f64bb9b27";


  public static class RemoveUserAttributeImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.ide.shared.RemoveUserAttribute {
    protected RemoveUserAttributeImpl() {}

    @Override
    public final native java.lang.String getAttributeName() /*-{
      return this["attributeName"];
    }-*/;

    public final native RemoveUserAttributeImpl setAttributeName(java.lang.String attributeName) /*-{
      this["attributeName"] = attributeName;
      return this;
    }-*/;

    public final native boolean hasAttributeName() /*-{
      return this.hasOwnProperty("attributeName");
    }-*/;

    public static native RemoveUserAttributeImpl make() /*-{
      return {
        _type: 4
      };
    }-*/;  }


  public static class UpdateUserAttributeImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.ide.shared.UpdateUserAttribute {
    protected UpdateUserAttributeImpl() {}

    @Override
    public final native java.lang.String getAttributeValue() /*-{
      return this["attributeValue"];
    }-*/;

    public final native UpdateUserAttributeImpl setAttributeValue(java.lang.String attributeValue) /*-{
      this["attributeValue"] = attributeValue;
      return this;
    }-*/;

    public final native boolean hasAttributeValue() /*-{
      return this.hasOwnProperty("attributeValue");
    }-*/;

    @Override
    public final native java.lang.String getAttributeName() /*-{
      return this["attributeName"];
    }-*/;

    public final native UpdateUserAttributeImpl setAttributeName(java.lang.String attributeName) /*-{
      this["attributeName"] = attributeName;
      return this;
    }-*/;

    public final native boolean hasAttributeName() /*-{
      return this.hasOwnProperty("attributeName");
    }-*/;

    public static native UpdateUserAttributeImpl make() /*-{
      return {
        _type: 2
      };
    }-*/;  }


  public static class UpdateUserAttributesImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.ide.shared.UpdateUserAttributes {
    protected UpdateUserAttributesImpl() {}

    @Override
    public final native com.codenvy.ide.json.JsonStringMap<java.lang.String> getAttributes() /*-{
      return this["attributes"];
    }-*/;

    public final native UpdateUserAttributesImpl setAttributes(com.codenvy.ide.json.JsonStringMap<java.lang.String> attributes) /*-{
      this["attributes"] = attributes;
      return this;
    }-*/;

    public final native boolean hasAttributes() /*-{
      return this.hasOwnProperty("attributes");
    }-*/;

    public static native UpdateUserAttributesImpl make() /*-{
      return {
        _type: 3
      };
    }-*/;  }


  public static class UserImpl extends com.codenvy.ide.dtogen.client.RoutableDtoClientImpl implements com.codenvy.ide.shared.User {
    protected UserImpl() {}

    @Override
    public final native java.lang.String getUserId() /*-{
      return this["userId"];
    }-*/;

    public final native UserImpl setUserId(java.lang.String userId) /*-{
      this["userId"] = userId;
      return this;
    }-*/;

    public final native boolean hasUserId() /*-{
      return this.hasOwnProperty("userId");
    }-*/;

    @Override
    public final native com.codenvy.ide.json.JsonStringMap<java.lang.String> getProfileAttributes() /*-{
      return this["profileAttributes"];
    }-*/;

    public final native UserImpl setProfileAttributes(com.codenvy.ide.json.JsonStringMap<java.lang.String> profileAttributes) /*-{
      this["profileAttributes"] = profileAttributes;
      return this;
    }-*/;

    public final native boolean hasProfileAttributes() /*-{
      return this.hasOwnProperty("profileAttributes");
    }-*/;

    public static native UserImpl make() /*-{
      return {
        _type: 1
      };
    }-*/;  }

}