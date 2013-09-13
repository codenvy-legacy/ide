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
package org.exoplatform.ide.client.preferences;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.preference.PreferenceItem;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import java.util.List;

/**
 * View for displaying application preferences.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 23, 2012 3:53:50 PM anya $
 */
public class PreferencesView extends ViewImpl implements PreferencesPresenter.Display {
    private static final int WIDTH = 950;

    private static final int HEIGHT = 500;

    private static final String ID = "eXoPreferencesView";

    private final String TREE_ID = "eXoPreferencesViewPreferencesTree";

    private final String CLOSE_BUTTON_ID = "eXoPreferencesViewCloseButton";

    private static PreferencesViewUiBinder uiBinder = GWT.create(PreferencesViewUiBinder.class);

    interface PreferencesViewUiBinder extends UiBinder<Widget, PreferencesView> {
    }

    private CellTree.Resources res = GWT.create(CellTreeResource.class);

    private SingleSelectionModel<PreferenceItem> selectionModel;

    private PreferencesTreeViewModel preferencesTreeViewModel;

    private CellTree preferencesNavigationTree;

    @UiField
    ImageButton closeButton;

    @UiField
    ScrollPanel treePanel;

    @UiField
    ScrollPanel viewPanel;

    public PreferencesView() {
        super(ID, ViewType.MODAL, IDE.PREFERENCES_CONSTANT.showPreferencesViewTitle(), new Image(
                IDEImageBundle.INSTANCE.preferences()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        selectionModel = new SingleSelectionModel<PreferenceItem>();
        preferencesTreeViewModel = new PreferencesTreeViewModel(selectionModel);
        preferencesNavigationTree = new CellTree(preferencesTreeViewModel, null, res);

        // Keyboard is disabled because of the selection problem (when selecting programmatically), if
        // KeyboardSelectionPolicy.BOUND_TO_SELECTION is set
        // and because of the focus border, when use KeyboardSelectionPolicy.ENABLED.
        preferencesNavigationTree.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

        preferencesNavigationTree.getElement().setId(TREE_ID);
        treePanel.add(preferencesNavigationTree);

        closeButton.setButtonId(CLOSE_BUTTON_ID);
        viewPanel.getElement().setId("eXoViewPanel");
    }

    /** @see org.exoplatform.ide.client.preferences.PreferencesPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.client.preferences.PreferencesPresenter.Display#openView(org.exoplatform.ide.client.framework.ui.api
     * .View) */
    @Override
    public void openView(View view) {
        viewPanel.setWidget(view.asWidget());
        view.asWidget().setWidth(view.getDefaultWidth() + "px");
        view.asWidget().setHeight(view.getDefaultHeight() + "px");
        // TODO fixes border:
        view.activate();
        this.activate();
    }

    /** @see org.exoplatform.ide.client.preferences.PreferencesPresenter.Display#setValue(java.util.List) */
    @Override
    public void setValue(List<PreferenceItem> values) {
        preferencesTreeViewModel.getDataProvider().getList().clear();
        preferencesTreeViewModel.getDataProvider().setList(values);
    }

    /** @see org.exoplatform.ide.client.preferences.PreferencesPresenter.Display#selectToken(org.exoplatform.ide.client.framework
     * .preference.PreferenceItem) */
    @Override
    public void selectToken(PreferenceItem item) {
        if (item != null) {
            selectionModel.setSelected(item, true);
        }
    }

    /** @see org.exoplatform.ide.client.preferences.PreferencesPresenter.Display#getSingleSelectionModel() */
    @Override
    public SingleSelectionModel<PreferenceItem> getSingleSelectionModel() {
        return selectionModel;
    }
}
