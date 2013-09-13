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
package org.exoplatform.ide.client.framework.control;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */
@RolesAllowed({"developer"})
public class NewItemControl extends SimpleControl implements IDEControl {

    private String mimeType;

    public NewItemControl(String id, String title, String prompt, ImageResource icon, ImageResource disabledIcon,
                          GwtEvent<?> event) {
        super(id);

        setTitle(title);
        setPrompt(prompt);
        setNormalImage(icon);
        setDisabledImage(disabledIcon);
        setEvent(event);
    }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    public void initialize() {
    }

    public NewItemControl(String id, String title, String prompt, String icon, GwtEvent<?> event) {
        super(id);

        setTitle(title);
        setPrompt(prompt);
        setIcon(icon);
        setEvent(event);
    }

    public NewItemControl(String id, String title, String prompt, String icon, String mimeType) {
        super(id);

        setTitle(title);
        setPrompt(prompt);
        setIcon(icon);
        this.mimeType = mimeType;
    }

    public NewItemControl(String id, String title, String prompt, String icon, String mimeType,
                          boolean hasDelimiterBefore) {
        this(id, title, prompt, icon, mimeType);
        setDelimiterBefore(hasDelimiterBefore);
    }

    public NewItemControl(String id, String title, String prompt, ImageResource icon, ImageResource disabledIcon,
                          String mimeType) {
        super(id);

        setTitle(title);
        setPrompt(prompt);
        setNormalImage(icon);
        setDisabledImage(disabledIcon);
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
