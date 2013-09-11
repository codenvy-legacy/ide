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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.preference.PreferenceItem;
import org.exoplatform.ide.client.framework.preference.Preferences;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import java.util.List;

/**
 * Presenter for managing application preferences.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 18, 2012 3:18:53 PM anya $
 */
public class PreferencesPresenter implements ShowPreferencesHandler, ViewClosedHandler {
    interface Display extends IsView {
        HasClickHandlers getCloseButton();

        void openView(View view);

        void setValue(List<PreferenceItem> values);

        void selectToken(PreferenceItem token);

        SingleSelectionModel<PreferenceItem> getSingleSelectionModel();
    }

    private Display display;

    private View openedView;

    public PreferencesPresenter() {
        IDE.getInstance().addControl(new ShowPreferencesControl());

        IDE.addHandler(ShowPreferencesEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getSingleSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {

            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                if (display.getSingleSelectionModel().getSelectedObject() instanceof PreferenceItem) {
                    // TODO check has performer
                    if (openedView != null) {
                        IDE.fireEvent(new ViewClosedEvent(openedView));
                    }
                    openedView =
                            ((PreferenceItem)display.getSingleSelectionModel().getSelectedObject()).getPreferencePerformer()
                                                                                                   .getPreference();
                    display.openView(openedView);
                }
            }
        });
    }

    /** @see org.exoplatform.ide.client.preferences.ShowPreferencesHandler#onShowPreferences(org.exoplatform.ide.client.preferences
     * .ShowPreferencesEvent) */
    @Override
    public void onShowPreferences(ShowPreferencesEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }

        display.setValue(Preferences.get().getPreferences());
        if (Preferences.get().getPreferences().size() > 0) {
            display.selectToken(Preferences.get().getPreferences().get(0));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            if (openedView != null) {
                IDE.fireEvent(new ViewClosedEvent(openedView));
            }
        }
    }
}
