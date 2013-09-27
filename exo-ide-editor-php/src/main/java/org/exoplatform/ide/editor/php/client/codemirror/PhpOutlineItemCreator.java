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
package org.exoplatform.ide.editor.php.client.codemirror;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.outline.OutlineItemCreatorImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.php.client.PhpClientBundle;

import java.util.List;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class PhpOutlineItemCreator extends OutlineItemCreatorImpl {
    @Override
    public ImageResource getTokenIcon(TokenBeenImpl token) {
        switch (token.getType()) {
            case PHP_TAG:
                return PhpClientBundle.INSTANCE.phpTag();

            case FUNCTION:
                return PhpClientBundle.INSTANCE.phpTag();

            case VARIABLE:
                return PhpClientBundle.INSTANCE.variable();

            case CONSTANT:
                return PhpClientBundle.INSTANCE.constantItem();

            case METHOD:
                if (isPrivate(token)) {
                    return PhpClientBundle.INSTANCE.privateMethod();
                } else if (isProtected(token)) {
                    return PhpClientBundle.INSTANCE.protectedMethod();
                } else if (isPublic(token)) {
                    return PhpClientBundle.INSTANCE.publicMethod();
                }

                return PhpClientBundle.INSTANCE.publicMethod();

            case PROPERTY:
                if (isPrivate(token)) {
                    return PhpClientBundle.INSTANCE.privateField();
                } else if (isProtected(token)) {
                    return PhpClientBundle.INSTANCE.protectedField();
                } else if (isPublic(token)) {
                    return PhpClientBundle.INSTANCE.publicField();
                }

                return PhpClientBundle.INSTANCE.publicField();

            case CLASS:
                return PhpClientBundle.INSTANCE.classItem();

            case INTERFACE:
                return PhpClientBundle.INSTANCE.interfaceItem();

            case CLASS_CONSTANT:
                return PhpClientBundle.INSTANCE.classConstant();

            case NAMESPACE:
                return PhpClientBundle.INSTANCE.namespace();

            default:
                return null;
        }
    }

    @Override
    public String getTokenDisplayTitle(TokenBeenImpl token) {
        String label = token.getName();

        label = getModifiersContainer(token) + "<span class='item-name' style='margin-left: 5px;'>" + label + "</span>";

        // Add parameter list
        if (TokenType.FUNCTION.equals(token.getType()) || TokenType.METHOD.equals(token.getType())) {
            label += getParametersList(token);
        }

        // Add field type or method return type
        if (token.getElementType() != null) {
            label += "<span style='color:#644a17;' class='item-type'>" + getElementType(token) + "</span>";
        }

        return label;
    }

    /**
     * Return parameters list from token.getParameters()
     *
     * @param token
     * @return parameters list like ($a, $b), or '()' if there are no parameters
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

                parametersDescription +=
                        "<span class='item-parameter'>" + parameter.getName() + getElementType(parameter) + "</span>";
            }
        }

        return parametersDescription + ")";
    }

    /**
     * @param token
     *         {@link TokenBeenImpl}
     * @return html element with modifiers sign
     */
    protected String getModifiersContainer(TokenBeenImpl token) {
        if (isStatic(token) || isFinal(token) || isAbstract(token)) {
            String span =
                    "<span style = \"position: relative; top: -5px; margin-left: -3px; font-family: Verdana,Bitstream Vera Sans," +
                    "sans-serif; font-size: 9px; text-align: right;' \">";
            span += (isStatic(token)) ? "<span class='item-modifier' color ='#6d0000'>s</span>" : "";
            span += (isFinal(token)) ? "<span class='item-modifier' color ='#174c83'>f</span>" : "";
            span += (isAbstract(token)) ? "<span class='item-modifier' color ='#004e00'>a</span>" : "";
            span += "</span>";

            return span;
        }

        return "";
    }
}