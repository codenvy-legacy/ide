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
package org.exoplatform.ide.editor.php.client.codeassistant.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.php.client.PhpClientBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base part implementation of {@link TokenWidget}, uses frol all PHP token widgets.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public abstract class PhpTokenWidgetBase extends TokenWidget {

    protected Grid grid;

    protected List<Modifier> modifieres;

    /** @param token */
    @SuppressWarnings("unchecked")
    public PhpTokenWidgetBase(Token token) {
        super(token);
        modifieres = new ArrayList<Modifier>();

        if (token.hasProperty(TokenProperties.MODIFIERS)) {
            modifieres.addAll((Collection<Modifier>)token.getProperty(TokenProperties.MODIFIERS).isObjectProperty()
                                                         .objectValue());
        }
    }

    protected String getModifiers() {

        String span =
                "<span style = \"position: absolute; margin-top: -5px; margin-left: -25px; width: 22px; "
                + "height: 10px; font-family:  font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 10px; \">";
        span += (modifieres.contains(Modifier.ABSTRACT)) ? "<font color ='#004e00' style='float: right;'>A</font>" : "";
        // span += (ModifierHelper.isFinal(modifieres)) ? "<font color ='#174c83' style='float: right;'>F</font>" : "";
        span += (modifieres.contains(Modifier.STATIC)) ? "<font color ='#6d0000' style='float: right;'>S</font>" : "";
        span += "</span>";
        return span;
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenName() */
    @Override
    public String getTokenName() {
        return getToken().getName();
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setSelectedStyle() */
    @Override
    public void setSelectedStyle() {
        setStyleName(PhpClientBundle.INSTANCE.css().selectedItem());
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setDefaultStyle() */
    @Override
    public void setDefaultStyle() {
        setStyleName(PhpClientBundle.INSTANCE.css().item());
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenValue() */
    @Override
    public String getTokenValue() {
        if (token.hasProperty(TokenProperties.CODE))
            return token.getProperty(TokenProperties.CODE).isStringProperty().stringValue();
        else
            return token.getName();
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenDecription() */
    @Override
    public Widget getTokenDecription() {
        return null;
    }

}
