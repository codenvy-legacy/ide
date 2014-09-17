/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.preferences;

import elemental.html.TableElement;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.preferences.PreferencesPagePresenter;
import com.codenvy.ide.ui.list.CategoriesList;
import com.codenvy.ide.ui.list.Category;
import com.codenvy.ide.ui.list.CategoryRenderer;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.wizard.project.ProjectWizardResources;
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
    Button btnOk;
    Button btnApply;
    @UiField
    SimplePanel               preferences;
    @UiField
    SimplePanel               contentPanel;
    @UiField(provided = true)
    com.codenvy.ide.Resources res;
    @UiField(provided = true)
    final ProjectWizardResources wizardResources;

    private CoreLocalizationConstant locale;
    private ActionDelegate           delegate;
    private PreferencesPagePresenter firstPage;
    private CategoriesList           list;

    private final Category.CategoryEventDelegate<PreferencesPagePresenter> PreferencesPageDelegate =
            new Category.CategoryEventDelegate<PreferencesPagePresenter>() {
                @Override
                public void onListItemClicked(com.google.gwt.dom.client.Element listItemBase, PreferencesPagePresenter itemData) {
                    delegate.selectedPreference(itemData);
                }
            };

    private final CategoryRenderer<PreferencesPagePresenter> PreferencesPageRenderer =
            new CategoryRenderer<PreferencesPagePresenter>() {
                @Override
                public void renderElement(com.google.gwt.dom.client.Element element, PreferencesPagePresenter preference) {
                    element.setInnerText(preference.getTitle());
                }

                @Override
                public com.google.gwt.dom.client.SpanElement renderCategory(Category<PreferencesPagePresenter> category) {
                    SpanElement spanElement = Document.get().createSpanElement();
                    spanElement.setClassName(res.defaultCategoriesListCss().headerText());
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
    protected PreferencesViewImpl(com.codenvy.ide.Resources resources, PreferenceViewImplUiBinder uiBinder,
                                  CoreLocalizationConstant locale, ProjectWizardResources wizardResources) {
        this.res = resources;
        this.locale = locale;
        this.wizardResources = wizardResources;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle("Preferences");
        this.setWidget(widget);

        //create list of preferences
        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = new CategoriesList(res);
        this.preferences.add(list);
        createButtons();
    }

    private void createButtons() {
        btnClose = createButton(locale.close(), "window-preferences-close", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        btnClose.addStyleName(wizardResources.css().button());
        getFooter().add(btnClose);

        btnOk = createButton(locale.ok(), "window-preferences-ok", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onOkClicked();
            }
        });
        btnOk.addStyleName(wizardResources.css().button());
        getFooter().add(btnOk);

        btnApply = createButton(locale.apply(), "window-preferences-apply", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onApplyClicked();
            }
        });
        btnApply.addStyleName(wizardResources.css().button());
        getFooter().add(btnApply);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;

        //show first page if page is exist
        if (firstPage != null) {
            PreferencesPageDelegate.onListItemClicked(null, firstPage);
        } else {
            btnApply.setEnabled(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showPreferences() {
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContentPanel() {
        return contentPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void setApplyButtonEnabled(boolean isEnabled) {
        btnApply.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setPreferences(Map<String, Set<PreferencesPagePresenter>> preferences, PreferencesPagePresenter firstPage) {
        this.firstPage = firstPage;

        List<Category<?>> categoriesList = new ArrayList<Category<?>>();
        for (String s : preferences.keySet()) {
            Category<PreferencesPagePresenter> category =
                    new Category<PreferencesPagePresenter>(s, PreferencesPageRenderer, preferences.get(s), PreferencesPageDelegate);
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
    public void selectPreference(PreferencesPagePresenter preference) {
        list.selectElement(preference);
    }
}