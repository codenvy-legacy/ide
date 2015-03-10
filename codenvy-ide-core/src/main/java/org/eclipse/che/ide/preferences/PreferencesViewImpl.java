/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.preferences;

import elemental.html.TableElement;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.preferences.PreferencePagePresenter;
import org.eclipse.che.ide.ui.list.CategoriesList;
import org.eclipse.che.ide.ui.list.Category;
import org.eclipse.che.ide.ui.list.CategoryRenderer;
import org.eclipse.che.ide.ui.window.Window;
import org.eclipse.che.ide.util.dom.Elements;
import org.eclipse.che.ide.util.loging.Log;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * PreferenceViewImpl is the view of preferences.
 * The view shows preference pages to the end user. It has an area at the bottom containing
 * OK, Apply and Close buttons, on the left hand side of page is list of available preferences,
 * on the right hand side of page is current preference page.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class PreferencesViewImpl extends Window implements PreferencesView {
    interface PreferenceViewImplUiBinder extends UiBinder<Widget, PreferencesViewImpl> {
    }

    Button btnClose;
    Button btnSave;
    Button btnRefresh;

    @UiField
    SimplePanel                   preferences;
    @UiField
    SimplePanel                   contentPanel;
    @UiField(provided = true)
    org.eclipse.che.ide.Resources resources;

    private CoreLocalizationConstant locale;
    private ActionDelegate           delegate;
    private CategoriesList           list;

    private final Category.CategoryEventDelegate<PreferencePagePresenter> preferencesPageDelegate =
            new Category.CategoryEventDelegate<PreferencePagePresenter>() {
                @Override
                public void onListItemClicked(com.google.gwt.dom.client.Element listItemBase, PreferencePagePresenter itemData) {
                    delegate.onPreferenceSelected(itemData);
                }
            };

    private final CategoryRenderer<PreferencePagePresenter> preferencesPageRenderer =
            new CategoryRenderer<PreferencePagePresenter>() {
                @Override
                public void renderElement(com.google.gwt.dom.client.Element element, PreferencePagePresenter preference) {
                    element.setInnerText(preference.getTitle());
                }

                @Override
                public com.google.gwt.dom.client.SpanElement renderCategory(Category<PreferencePagePresenter> category) {
                    SpanElement spanElement = Document.get().createSpanElement();
                    spanElement.setClassName(resources.defaultCategoriesListCss().headerText());
                    spanElement.setInnerText(category.getTitle());
                    return spanElement;
                }
            };

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected PreferencesViewImpl(org.eclipse.che.ide.Resources resources, PreferenceViewImplUiBinder uiBinder,
                                  CoreLocalizationConstant locale) {
        this.resources = resources;
        this.locale = locale;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle("Preferences");
        this.setWidget(widget);

        //create list of preferences
        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = new CategoriesList(resources);
        preferences.add(list);
        createButtons();
    }

    private void createButtons() {
        /*
            Save
         */
        btnSave = createButton(locale.save(), "window-preferences-storeChanges", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Log.trace("< SAVE");
                delegate.onSaveClicked();
            }
        });

        btnSave.addStyleName(resources.wizardCss().button());
        btnSave.addStyleName(resources.wizardCss().rightButton());
        btnSave.addStyleName(resources.wizardCss().buttonPrimary());
        getFooter().add(btnSave);

        /*
            Refresh
         */
        btnRefresh = createButton(locale.refresh(), "window-preferences-refresh", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Log.trace("< REFRESH");
                delegate.onRefreshClicked();
            }
        });
        btnRefresh.addStyleName(resources.wizardCss().button());
        btnRefresh.addStyleName(resources.wizardCss().buttonSuccess());
        getFooter().add(btnRefresh);

        /*
            Close
         */
        btnClose = createButton(locale.close(), "window-preferences-close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Log.trace("< CLOSE");
                delegate.onCloseClicked();
            }
        });
        btnClose.addStyleName(resources.wizardCss().button());
        getFooter().add(btnClose);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        super.show();
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContentPanel() {
        return contentPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void enableSaveButton(boolean enabled) {
        btnSave.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setPreferences(Map<String, Set<PreferencePagePresenter>> preferences) {
        List<Category<?>> categoriesList = new ArrayList<Category<?>>();
        for (String s : preferences.keySet()) {
            Category<PreferencePagePresenter> category =
                    new Category<PreferencePagePresenter>(s, preferencesPageRenderer, preferences.get(s), preferencesPageDelegate);
            categoriesList.add(category);
        }
        list.render(categoriesList);
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }

    /** {@inheritDoc} */
    @Override
    public void selectPreference(PreferencePagePresenter preference) {
        list.selectElement(preference);
    }
}
