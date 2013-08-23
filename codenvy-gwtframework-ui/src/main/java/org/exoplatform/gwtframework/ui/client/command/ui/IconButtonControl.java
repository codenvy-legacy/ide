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

package org.exoplatform.gwtframework.ui.client.command.ui;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.gwtframework.ui.client.command.SimpleControlStateListener;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IconButtonControl extends IconButton implements SimpleControlStateListener {

    private final static String DISABLED_SUFFIX = "_Disabled";

    private HandlerManager eventBus;

    private SimpleControl control;

    private Toolbar toolbar;

    public IconButtonControl(HandlerManager eventBus, SimpleControl control, Toolbar toolbar) {
        super((String)null, (String)null);
        setCommand(iconButtonCommand);

        this.eventBus = eventBus;
        this.control = control;
        this.toolbar = toolbar;

        setIcon(getControlIcon());
        setDisabledIcon(getControlDisabledIcon());
        setEnabled(control.isEnabled());
        setTitle(control.getPrompt());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        getParent().setVisible(control.isVisible());
        control.getStateListeners().add(this);
    }

    @Override
    protected void onDetach() {
        super.onDetach();
        control.getStateListeners().remove(this);
    }

    protected String getControlIcon() {
        String icon = "";
        if (control.getNormalImage() != null) {
            icon = ImageHelper.getImageHTML(control.getNormalImage());
        } else if (control.getIcon() != null) {
            icon = ImageHelper.getImageHTML(control.getIcon());
        }

        return icon;
    }

    protected String getControlDisabledIcon() {
        String disabledIcon = "";
        if (control.getDisabledImage() != null) {
            disabledIcon = ImageHelper.getImageHTML(control.getDisabledImage());
        } else if (control.getIcon() != null) {
            String disabledIconURL = control.getIcon();
            disabledIconURL = control.getIcon().substring(0, control.getIcon().lastIndexOf("."));
            disabledIconURL += DISABLED_SUFFIX;
            disabledIconURL += control.getIcon().substring(control.getIcon().lastIndexOf("."));
            disabledIcon = ImageHelper.getImageHTML(disabledIconURL);
        }

        return disabledIcon;
    }

    private Command iconButtonCommand = new Command() {
        public void execute() {
            if (control.getEvent() != null) {
                eventBus.fireEvent(control.getEvent());
            }
        }
    };

    public void updateControlEnabling(boolean enabled) {
        setEnabled(enabled);
    }

    public void updateControlVisibility(boolean visible) {
        getParent().setVisible(visible);
        toolbar.hideDuplicatedDelimiters();
    }

    public void updateControlPrompt(String prompt) {
        setTitle(prompt);
    }

    public void updateControlIcon(String icon) {
        String iconHtml = "";
        if (control.isEnabled()) {
            if (control.getNormalImage() != null) {
                iconHtml = ImageHelper.getImageHTML(control.getNormalImage());
            } else if (control.getIcon() != null) {
                iconHtml = ImageHelper.getImageHTML(control.getIcon());
            }
        } else {
            if (control.getDisabledImage() != null) {
                iconHtml = ImageHelper.getImageHTML(control.getDisabledImage());
            } else if (control.getIcon() != null) {
                String iconNormal = control.getIcon();
                String iconDisabled = iconNormal.substring(0, iconNormal.lastIndexOf("."));
                iconDisabled += "_Disabled";
                iconDisabled += iconNormal.substring(iconNormal.lastIndexOf("."));
                iconHtml = ImageHelper.getImageHTML(iconDisabled);
            }
        }
        setIcon(iconHtml);
    }

    public void updateControlTitle(String title) {
    }

    public void updateControlSelectionState(boolean selected) {
        setSelected(selected);
    }

    public void updateControlHotKey(String hotKey) {
    }

}
