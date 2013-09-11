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
package org.exoplatform.ide.editor.java.client.codemirror;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.gwtframework.commons.util.StringEscapeUtils;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.outline.OutlineItemCreatorImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

import java.util.List;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class JavaOutlineItemCreator extends OutlineItemCreatorImpl {
    @Override
    public ImageResource getTokenIcon(TokenBeenImpl token) {
        switch (token.getType()) {
            case VARIABLE:
                return JavaClientBundle.INSTANCE.variable();

            case PROPERTY:
                if (isPrivate(token)) {
                    return JavaClientBundle.INSTANCE.privateField();
                } else if (isProtected(token)) {
                    return JavaClientBundle.INSTANCE.protectedField();
                } else if (isPublic(token)) {
                    return JavaClientBundle.INSTANCE.publicField();
                }

                return JavaClientBundle.INSTANCE.defaultField();

            case METHOD:
                if (isPrivate(token)) {
                    return JavaClientBundle.INSTANCE.privateMethod();
                } else if (isProtected(token)) {
                    return JavaClientBundle.INSTANCE.protectedMethod();
                } else if (isPublic(token)) {
                    return JavaClientBundle.INSTANCE.publicMethod();
                }

                return JavaClientBundle.INSTANCE.defaultMethod();

            case CLASS:
                return JavaClientBundle.INSTANCE.classItem();

            case INTERFACE:
                return JavaClientBundle.INSTANCE.interfaceItem();

            case JSP_TAG:
                return JavaClientBundle.INSTANCE.jspTagItem();

            default:
                return null;
        }
    }

    @Override
    public String getTokenDisplayTitle(TokenBeenImpl token) {
        String label = token.getName();

        // icon, that displays in right bottom corner, if token is CLASS,
        // and shows access modifier
        String modfImg = "";

        String synchImg = "";

        // should be refactored from using hard code to using ImageResource
        if (BrowserResolver.CURRENT_BROWSER != Browser.IE) {
            if (TokenType.CLASS.equals(token.getType()) || TokenType.INTERFACE.equals(token.getType())) {
                if (isPrivate(token)) {
                    modfImg =
                            "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                            + " suppress=\"TRUE\" src=\"" + UIHelper.getGadgetImagesURL() + "outline/class-private.png"
                            + "\" />";
                } else if (isProtected(token)) {
                    modfImg =
                            "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                            + " suppress=\"TRUE\" src=\"" + UIHelper.getGadgetImagesURL() + "outline/class-protected.png"
                            + "\" />";
                } else if (isPublic(token)) {
                } else {
                    modfImg =
                            "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                            + " suppress=\"TRUE\" src=\"" + UIHelper.getGadgetImagesURL() + "outline/class-default.png"
                            + "\" />";
                }
            }

            if (isSynchronized(token)) {
                final String marginLeft = modfImg.length() > 0 ? "-3" : "-10";
                synchImg =
                        "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:" + marginLeft
                        + "px; margin-top:8px;\"  border=\"0\"" + " suppress=\"TRUE\" src=\"" + UIHelper.getGadgetImagesURL()
                        + "outline/clock.png" + "\" />";
            }
        }

        String deprecateSign = isDeprecated(token) ? "style='text-decoration:line-through;'" : "";

        label =
                getModifiersContainer(token) + modfImg + synchImg + "<span class='item-name' " + deprecateSign
                + " style='margin-left: 5px;' title=\"" + getAnnotationList(token) + "\">" + label + "</span>";

        // Add parameter list
        if (TokenType.METHOD.equals(token.getType())) {
            label += getParametersList(token);
        }

        // Add field type or method return type
        if (token.getElementType() != null) {
            label +=
                    "<span style='color:#644a17;' class='item-type' title=\"" + getAnnotationList(token) + "\">"
                    + getElementType(token) + "</span>";
        }

        return label;
    }

    /**
     * Return parameters list from token.getParameters()
     *
     * @param token
     * @return parameters list like '(String, int)', or ($a, $b) for PHP-code, or '()' if there are no parameters
     */
    protected String getParametersList(TokenBeenImpl token) {
        String parametersDescription = "(";

        if (token.getParameters() != null && token.getParameters().size() > 0) {

            List<TokenBeenImpl> parameters = token.getParameters();

            for (int i = 0; i < parameters.size(); i++) {
                TokenBeenImpl parameter = parameters.get(i);
                if (i > 0) {
                    parametersDescription += ", ";
                }

                String annotationList = getAnnotationList(parameter);

                parametersDescription +=
                        "<span title=\"" + annotationList + "\">" + getAnnotationSign(annotationList)
                        + "<span class='item-parameter'>" + StringEscapeUtils.htmlEncode(parameter.getElementType())
                        + "</span></span>";
            }
        }

        return parametersDescription + ")";
    }

    /**
     * @param annotationList
     * @return HTML code to display "@" sign near the groovy token if annotationList is not empty, or "" otherwise
     */
    protected String getAnnotationSign(String annotationList) {
        return (!annotationList.isEmpty()
                ? "<span style = \"font-family: Verdana, Bitstream Vera Sans, sans-serif; color: #525252; position: relative; top: -5px;\">@</span>"
                : "");
    }
}