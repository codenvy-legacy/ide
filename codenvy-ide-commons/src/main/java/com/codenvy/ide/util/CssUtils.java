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
import elemental.html.Element;

import com.google.gwt.dom.client.Style.Unit;

/** Utility methods for dealing with CSS. */
public class CssUtils {
    public static boolean containsClassName(
            com.google.gwt.dom.client.Element element, String className) {
        return (" " + element.getClassName() + " ").contains(" " + className + " ");
    }

    /**
     * Walks the DOM ancestry to find a node with the specified className.
     *
     * @return the element with the specified className, or {@code null} if none
     *         is found.
     */
    public static Element getAncestorOrSelfWithClassName(Element element, String className) {
        while (element != null && !containsClassName(element, className)) {
            element = element.getParentElement();
        }
        return element;
    }

    public static boolean containsClassName(Element element, String className) {
        return element.hasClassName(className);
    }

    public static void setClassNameEnabled(Element element, String className, boolean enable) {
        if (enable) {
            element.addClassName(className);
        } else {
            element.removeClassName(className);
        }
    }

    /**
     * Test if the element his its {@code display} set to something other than
     * {@code none}.
     */
    public static boolean isVisible(Element element) {
        return !element.getStyle().getDisplay().equals(CSSStyleDeclaration.Display.NONE);
    }

    /**
     * Parses the pixels from a value string in pixels, or returns 0 for an empty
     * value.
     */
    public static int parsePixels(String value) {
        assert value.length() == 0 || isPixels(value);
        return value.length() == 0 ? 0 : Integer.parseInt(
                value.substring(0, value.length() - CSSStyleDeclaration.Unit.PX.length()));
    }

  /*
   * TODO: in a separate CL, move all clients of this one over to
   * the second version of this method (have to make sure they adhere by the new
   * contract -- to much work for this CL)
   */

    /**
     * Sets the visibility of an element via the {@code display} CSS property. When visible, the
     * {@code display} will be set to {@code block}.
     */
    @Deprecated
    public static void setDisplayVisibility(Element element, boolean visible) {
        if (visible) {
            element.getStyle().setDisplay(CSSStyleDeclaration.Display.BLOCK);
        } else {
            element.getStyle().setDisplay(CSSStyleDeclaration.Display.NONE);
        }
    }

    /**
     * Sets the visibility of an element via the {@code display} CSS property.
     *
     * @param defaultVisible
     *         if true, the element is visible by default (according to its classes)
     * @param displayVisibleValue
     *         if {@code defaultVisible} is false, this must be given. This is the
     *         preferred 'display' value to make it visible
     */
    public static void setDisplayVisibility2(Element element, boolean visible,
                                             boolean defaultVisible, String displayVisibleValue) {
        if (visible) {
            if (defaultVisible) {
                element.getStyle().removeProperty("display");
            } else {
                element.getStyle().setDisplay(displayVisibleValue);
            }
        } else {
            if (defaultVisible) {
                element.getStyle().setDisplay(CSSStyleDeclaration.Display.NONE);
            } else {
                element.getStyle().removeProperty("display");
            }
        }
    }

    /**
     * Sets the visibility of an element via the {@code display} CSS property.
     * This should not be used if the default CSS keeps the element 'display: none'.
     * <p/>
     * When hidden, the display property on the element style will be set to none,
     * when visible the display property will be removed.
     */
    public static void setDisplayVisibility2(Element element, boolean visible) {
        setDisplayVisibility2(element, visible, true, null);
    }

    public static void setBoxShadow(Element element, String value) {
        element.getStyle().setProperty("-moz-box-shadow", value);
        element.getStyle().setProperty("-webkit-box-shadow", value);
        element.getStyle().setProperty("box-shadow", value);
    }

    public static void removeBoxShadow(Element element) {
        element.getStyle().removeProperty("-moz-box-shadow");
        element.getStyle().removeProperty("-webkit-box-shadow");
        element.getStyle().removeProperty("box-shadow");
    }

    public static void setUserSelect(Element element, boolean selectable) {
        String value = selectable ? "text" : "none";
        element.getStyle().setProperty("user-select", value);
        element.getStyle().setProperty("-moz-user-select", value);
        element.getStyle().setProperty("-webkit-user-select", value);
    }

    public static boolean isPixels(String value) {
        return value.toLowerCase().endsWith(CSSStyleDeclaration.Unit.PX);
    }

    public static native CSSStyleDeclaration getComputedStyle(Element element) /*-{
        return window.getComputedStyle(element);
    }-*/;

    /**
     * Sets a CSS property on an element as the new value, returning the
     * previously set value.
     */
    public static String setAndSaveProperty(Element element, String propertyName, String value) {
        String savedValue = element.getStyle().getPropertyValue(propertyName);
        element.getStyle().setProperty(propertyName, value);
        return savedValue;
    }

    public static void setTop(Element element, int value, Unit unit) {
        String topValue = Integer.toString(value) + unit;
        element.getStyle().setTop(topValue);
    }
}
