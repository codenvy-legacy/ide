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

package com.codenvy.ide.util;

import elemental.css.CSSStyleDeclaration;
import elemental.html.DivElement;
import elemental.html.Element;

import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.resources.client.ImageResource;

/**
 * Utilities for applying {@link com.google.gwt.resources.client.ImageResource}s to elements.
 */
public class ImageResourceUtils {

  /**
   * Applies the image resource to the specified element. The image will be
   * centered, and the height and width will be set to the height and width of
   * the image.
   */
  public static void applyImageResource(Element elem, ImageResource image) {
    applyImageResource(elem, image, "center", "center");
    elem.getStyle().setHeight(image.getHeight(), "px");
    elem.getStyle().setWidth(image.getWidth(), "px");
  }

  /**
   * Applies the image resource to the specified element with the specified
   * horizontal and vertical background positions.
   *
   * The height and width of the element are not modified under the presumption
   * that if you specify the horizontal and vertical position, the image will
   * not be the only content of the element.
   */
  public static void applyImageResource(Element elem, ImageResource image, String hPos,
      String vPos) {
    CSSStyleDeclaration style = elem.getStyle();
    style.setBackgroundImage("url(" + image.getSafeUri().asString() + ")");
    style.setProperty("background-repeat", "no-repeat");
    style.setProperty("background-position", hPos + " " + vPos);
    style.setOverflow("hidden");
  }

  /**
   * Creates a div from the specified {@link com.google.gwt.resources.client.ImageResource}.
   */
  public static DivElement createImageElement(ImageResource image) {
    DivElement elem = Elements.createDivElement();
    applyImageResource(elem, image);
    return elem;
  }
}
