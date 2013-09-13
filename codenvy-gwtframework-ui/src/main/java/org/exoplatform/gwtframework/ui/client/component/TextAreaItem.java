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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Text area form's item with possibility to display title near it.
 * In HTML it is represented as <code><textarea /></code> element
 * and label with field's title.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 * @deprecated use {@link TextAreaInput}
 */
public class TextAreaItem extends TextItemBase {

    /** Default constructor. */
    public TextAreaItem() {
        super(new TextInputBase(new TextArea().getElement()));
    }

    /**
     * @param name
     *         form element's name
     */
    public TextAreaItem(String name) {
        this();
        setName(name);
    }

    /**
     * @param name
     *         form element's name
     * @param title
     *         title shown near form element
     */
    public TextAreaItem(String name, String title) {
        this();
        setName(name);
        setTitle(title);
    }

    /** Sets focus in text area. */
    public void focus() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                focusInItem();
            }
        });
    }
}
