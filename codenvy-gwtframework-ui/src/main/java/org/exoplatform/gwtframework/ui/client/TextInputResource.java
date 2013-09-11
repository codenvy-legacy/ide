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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import org.exoplatform.gwtframework.ui.client.component.TextItemBase;

/**
 * The resources for {@link TextItemBase} component.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Feb 16, 2011 12:56:09 PM anya $
 */
public interface TextInputResource extends ClientBundle {
    /** @return {@link Style} CSS styles of the text input element */
    @Source("eXoStyle/skin/default/TextItemBase.css")
    Style css();

    /** CSS style resource. */
    public interface Style extends CssResource {
        /** @return {@link String} style of text input */
        String textInput();

        /** @return {@link String} style of text input, when it's in disabled state */
        String textInputDisabled();

        /** @return {@link String} style of text input, when it's title has top orientation */
        String textInputWithTopTitle();

        /** @return {@link String} style of text input's title */
        String textInputTitle();

        /** @return {@link String} style of text input's title, when it's not shown */
        String textInputTitleHidden();

        /** @return {@link String} style of text input's title with top orientation */
        String textInputTitleTop();

        /** @return {@link String} style of text input's title with left orientation */
        String textInputTitleLeft();

        /** @return {@link String} style of text input's title with right orientation */
        String textInputTitleRight();

        /** @return {@link String} style of text input's title with text align center */
        String textAlignCenter();

        /** @return {@link String} style of text input's title with text align left */
        String textAlignLeft();

        /** @return {@link String} style of text input's title with text align right */
        String textAlignRight();

    }
}
