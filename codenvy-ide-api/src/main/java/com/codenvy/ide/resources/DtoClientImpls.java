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
package com.codenvy.ide.resources;

import com.codenvy.ide.api.user.UpdateUserAttributes;
import com.codenvy.ide.api.user.User;
import com.codenvy.ide.dto.client.RoutableDtoClientImpl;

@SuppressWarnings({"unchecked", "cast"})
public class DtoClientImpls {

  private  DtoClientImpls() {}

  public static final String CLIENT_SERVER_PROTOCOL_HASH = "648f8d86f27105b66b0eb5f90b185c9f029001e1";


  public static class UpdateUserAttributesImpl extends RoutableDtoClientImpl implements UpdateUserAttributes {
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
        _type: 2
      };
    }-*/;  }


  public static class UserImpl extends RoutableDtoClientImpl implements User {
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