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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class SelectThemeView extends ViewImpl implements org.exoplatform.ide.client.theme.SelectThemePresenter.Display {

    public static final String ID = "ideSelectThemeView";

    private static final String TITLE = "Theme";

    /** Initial width of this view */
    private static final int WIDTH = 725;

    /** Initial height of this view */
    private static final int HEIGHT = 390;

    private static SelectThemeViewUiBinder uiBinder = GWT.create(SelectThemeViewUiBinder.class);

    interface SelectThemeViewUiBinder extends UiBinder<Widget, SelectThemeView> {
    }

    @UiField
    ThemesListGrid themesListGrid;

    @UiField
    ImageButton applyButton;

    public SelectThemeView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.welcome()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
    }

    @Override
    public ListGridItem<Theme> getThemesListGrid() {
        return themesListGrid;
    }

    @Override
    public Theme getSelectedTheme() {
        List<Theme> selectedItems = themesListGrid.getSelectedItems();
        if (selectedItems == null) {
            return null;
        }

        if (selectedItems.isEmpty()) {
            return null;
        }

        return selectedItems.get(0);
    }

    @Override
    public HasClickHandlers getApplyButton() {
        return applyButton;
    }

    @Override
    public void setApplyButtonEnabled(boolean enabled) {
        applyButton.setEnabled(enabled);
    }

}
