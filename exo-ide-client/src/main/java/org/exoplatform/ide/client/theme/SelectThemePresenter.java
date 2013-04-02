/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
