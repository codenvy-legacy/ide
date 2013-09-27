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
package org.exoplatform.gwtframework.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources for common GWT components.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Feb 16, 2011 12:56:09 PM anya $
 */
public interface GwtResources extends ClientBundle {
    /** Instance of the GWT resources. */
    public static final GwtResources INSTANCE = GWT.create(GwtResources.class);

    /**
     * <b>Note : </b> To inject styles to DOM use command:
     * <pre>
     * GwtResources.INSTANCE.css().ensureInjected();
     * </pre>
     *
     * @return {@link Style} CSS styles
     */
    @Source("eXoStyle/skin/default/gwt-style.css")
    Style css();

    /** CSS styles for GWT components. */
    public interface Style extends CssResource {
        /** @return {@link String} checkbox style */
        String checkBox();

        /** @return {@link String} checkbox disabled state style */
        String checkBoxDisabled();

        /** @return {@link String} checkbox when title is at the left side */
        String checkBoxTitleLeft();

        /** @return {@link String} radio button's style */
        String radioButton();

        /** @return {@link String} radio button's style when disabled */
        String radioButtonDisabled();

        /** @return {@link String} text box's style */
        String textBox();

        /** @return {@link String} text box's style when has focus */
        String textBoxFocused();

        /** @return {@link String} text box's style in disabled state */
        String textBoxDisabled();

        String transparent();

        String loaderBackground();

        String loaderCenteredContent();

        String loaderImage();
    }

    @Source("org/exoplatform/gwtframework/ui/client/component/ajax-loader-new.gif")
    ImageResource loader();

    @Source("org/exoplatform/gwtframework/ui/client/component/loader-background-element.png")
    ImageResource loaderBackground();

}
