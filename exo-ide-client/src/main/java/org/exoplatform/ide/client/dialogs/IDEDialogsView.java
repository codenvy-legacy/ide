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

package org.exoplatform.ide.client.dialogs;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEDialogsView extends ViewImpl {

    private final String BUTTONS_PANEL_HEIGHT = "22px";

    private VerticalPanel mainLayout;

    private HorizontalPanel buttonsLayout;

    public IDEDialogsView(String id, String title, int width, int height, Widget content) {
        this(id, title, width, height, content, true);
    }

    public IDEDialogsView(String id, String title, int width, int height, Widget content, boolean modal) {
        super(id, modal == true ? ViewType.MODAL : ViewType.POPUP, title, null, width, height);
        setCloseOnEscape(true);

        mainLayout = new VerticalPanel();
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        mainLayout.setSpacing(10);
        mainLayout.add(content);

        mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        buttonsLayout = createButtonsLayout();
        mainLayout.add(buttonsLayout);

        add(mainLayout);
    }

    /**
     * Create layout for displaying buttons.
     *
     * @return {@link HorizontalPanel} layout for buttons
     */
    public HorizontalPanel createButtonsLayout() {
        HorizontalPanel buttonsLayout = new HorizontalPanel();
        buttonsLayout.setHeight(BUTTONS_PANEL_HEIGHT);
        buttonsLayout.setSpacing(5);
        buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        return buttonsLayout;
    }

    /** @return the mainLayout */
    public VerticalPanel getMainLayout() {
        return mainLayout;
    }

    /** @return the buttonsLayout */
    public HorizontalPanel getButtonsLayout() {
        return buttonsLayout;
    }

}
