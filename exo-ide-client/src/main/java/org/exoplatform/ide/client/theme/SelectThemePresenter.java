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
package org.exoplatform.ide.client.theme;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.preference.PreferencePerformer;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class SelectThemePresenter implements PreferencePerformer, ViewClosedHandler {

    public interface Display extends IsView {

        ListGridItem<Theme> getThemesListGrid();

        Theme getSelectedTheme();

        HasClickHandlers getApplyButton();

        void setApplyButtonEnabled(boolean enabled);

    }

    private Display display;

    public SelectThemePresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    private void bindDisplay() {
        display.getApplyButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                applyTheme();
            }
        });

        display.getThemesListGrid().addSelectionHandler(new SelectionHandler<Theme>() {
            @Override
            public void onSelection(SelectionEvent<Theme> event) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        themeSelected();
                    }
                });
            }
        });

    }

    @Override
    public View getPreference() {
        if (display == null) {
            display = new SelectThemeView();
            bindDisplay();
            display.getThemesListGrid().setValue(ThemeManager.getInstance().getThemes());
            display.setApplyButtonEnabled(false);
        }

        return display.asView();
    }

    private Theme selectedTheme;

    private void applyTheme() {
        ThemeManager.getInstance().changeTheme(selectedTheme.getName());
        display.setApplyButtonEnabled(false);

        display.getThemesListGrid().setValue(ThemeManager.getInstance().getThemes());
        display.getThemesListGrid().selectItem(selectedTheme);
    }

    private void themeSelected() {
        selectedTheme = display.getSelectedTheme();
        if (selectedTheme == null) {
            display.setApplyButtonEnabled(false);
            return;
        }

        if (ThemeManager.getInstance().getActiveThemeName().equals(selectedTheme.getName())) {
            display.setApplyButtonEnabled(false);
        } else {
            display.setApplyButtonEnabled(true);
        }
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

}
