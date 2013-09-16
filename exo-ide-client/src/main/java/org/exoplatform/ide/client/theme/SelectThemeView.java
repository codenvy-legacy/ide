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
