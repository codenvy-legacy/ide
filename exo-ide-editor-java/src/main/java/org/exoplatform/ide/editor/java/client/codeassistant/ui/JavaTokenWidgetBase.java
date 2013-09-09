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
package org.exoplatform.ide.editor.java.client.codeassistant.ui;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.codeassistant.ModifierHelper;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 9:42:07 AM evgen $
 */
public abstract class JavaTokenWidgetBase extends TokenWidget {

    protected int modifieres;

    protected String docContext;

    protected String projectId;

    /** @param token */
    @SuppressWarnings("unchecked")
    public JavaTokenWidgetBase(Token token, String docContext, String projectId) {
        super(token);
        this.docContext = docContext;
        this.projectId = projectId;
        if (token.hasProperty(TokenProperties.MODIFIERS)) {
            TokenProperty mod = token.getProperty(TokenProperties.MODIFIERS);
            if (mod.isNumericProperty() != null)
                modifieres = mod.isNumericProperty().numericValue().intValue();
            else {
                modifieres = getModifires((List<Modifier>)mod.isObjectProperty().objectValue());
            }
        } else
            modifieres = 0;
    }

    protected String getModifiers() {

        String span =
                "<span style = \"position: absolute; margin-top: -5px; margin-left: -25px; width: 22px; "
                + "height: 10px; font-family:  font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 10px; \">";
        span += (ModifierHelper.isAbstract(modifieres)) ? "<font color ='#004e00' style='float: right;'>A</font>" : "";
        // span += (ModifierHelper.isFinal(modifieres)) ? "<font color ='#174c83' style='float: right;'>F</font>" : "";
        span += (ModifierHelper.isStatic(modifieres)) ? "<font color ='#6d0000' style='float: right;'>S</font>" : "";
        span += "</span>";
        return span;
    }

    /**
     * @param modifiers
     * @return
     */
    private int getModifires(List<Modifier> modifiers) {
        int i = 0;
        for (Modifier m : modifiers) {
            i = i | m.value();
        }
        return i;
    }


    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenName() */
    @Override
    public String getTokenName() {
        return getToken().getName();
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setSelectedStyle() */
    @Override
    public void setSelectedStyle() {
        setStyleName(JavaClientBundle.INSTANCE.css().selectedItem());
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setDefaultStyle() */
    @Override
    public void setDefaultStyle() {
        setStyleName(JavaClientBundle.INSTANCE.css().item());
    }

    /** @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenDecription() */
    @Override
    public Widget getTokenDecription() {
        return new Frame(docContext + token.getProperty(TokenProperties.DECLARING_CLASS).isStringProperty().stringValue()
                         + URL.encodeQueryString("#") + getTokenValue() + "&projectid=" + projectId + "&vfsid="
                         + VirtualFileSystem.getInstance().getInfo().getId() + "&isclass=false");
    }

}
