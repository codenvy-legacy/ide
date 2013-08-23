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

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.user.client.ui.RadioButton;

import org.exoplatform.gwtframework.ui.client.GwtResources;

/**
 * {@link RadioItem} differs from GWT {@link RadioButton} only with css style.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RadioItem extends RadioButton {
    /**
     * @param name
     *         name of the form's element
     */
    public RadioItem(String name) {
        super(name);
        initElement();
    }

    /**
     * @param name
     *         name of the form's element
     * @param title
     *         title shown near radiobutton
     */
    public RadioItem(String name, String title) {
        super(name, title);
        initElement();
    }

    /** Set the CSS style to the element. */
    private void initElement() {
        GwtResources.INSTANCE.css().ensureInjected();
        getElement().setClassName(GwtResources.INSTANCE.css().radioButton());
    }

    /** @see com.google.gwt.user.client.ui.CheckBox#setEnabled(boolean) */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            getElement().removeClassName(GwtResources.INSTANCE.css().radioButtonDisabled());
        } else {
            getElement().addClassName(GwtResources.INSTANCE.css().radioButtonDisabled());
        }
    }
}
