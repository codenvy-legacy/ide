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

import elemental.dom.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.window.Window;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;


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

    Button      btnClose;
    Button      btnOk;
    Button      btnApply;
    @UiField
    ScrollPanel preferences;
    @UiField
    SimplePanel contentPanel;
    @UiField(provided = true)
    com.codenvy.ide.Resources   res;
    private CoreLocalizationConstant locale;
    private ActionDelegate                       delegate;
    private PreferencesPagePresenter             firstPage;
    private SimpleList<PreferencesPagePresenter> list;
    private SimpleList.ListItemRenderer<PreferencesPagePresenter>  listItemRenderer =
            new SimpleList.ListItemRenderer<PreferencesPagePresenter>() {
                @Override
                public void render(Element itemElement, PreferencesPagePresenter itemData) {
                    TableCellElement label = Elements.createTDElement();

                    SafeHtmlBuilder sb = new SafeHtmlBuilder();
                    // Add icon
                    sb.appendHtmlConstant("<table><tr><td>");
                    ImageResource icon = itemData.getIcon();
                    if (icon != null) {
                        sb.appendHtmlConstant("<img src=\"" + icon.getSafeUri().asString() + "\">");
                    }
                    sb.appendHtmlConstant("</td>");

                    // Add title
                    sb.appendHtmlConstant("<td>");
                    sb.appendHtmlConstant(
                            "<div id=\"" + UIObject.DEBUG_ID_PREFIX + "window-preferences-" + itemData.getTitle() + "\">");
                    sb.appendEscaped(itemData.getTitle());
                    sb.appendHtmlConstant("</td></tr></table>");

                    label.setInnerHTML(sb.toSafeHtml().asString());

                    itemElement.appendChild(label);
                }

                @Override
                public Element createElement() {
                    return Elements.createTRElement();
                }
            };
    private SimpleList.ListEventDelegate<PreferencesPagePresenter> listDelegate     =
            new SimpleList.ListEventDelegate<PreferencesPagePresenter>() {
                public void onListItemClicked(Element itemElement, PreferencesPagePresenter itemData) {
                    list.getSelectionModel().setSelectedItem(itemData);
                    delegate.selectedPreference(itemData);
                }

                public void onListItemDoubleClicked(Element listItemBase, PreferencesPagePresenter itemData) {
                }
            };

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    protected PreferencesViewImpl(com.codenvy.ide.Resources resources, PreferenceViewImplUiBinder uiBinder, CoreLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setTitle("Preferences");
        this.setWidget(widget);

        //create list of preferences
        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((SimpleList.View)tableElement, res.defaultSimpleListCss(), listItemRenderer, listDelegate);
        this.preferences.add(list);
        createButtons();
    }
    
    private void createButtons(){
        btnClose = createButton(locale.close(), "window-preferences-close", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        getFooter().add(btnClose);

        btnOk = createButton(locale.ok(), "window-preferences-ok", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onOkClicked();
            }
        });
        getFooter().add(btnOk);

        btnApply = createButton(locale.apply(), "window-preferences-apply", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onApplyClicked();
            }
        });
        getFooter().add(btnApply);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;

        //show first page if page is exist
        if (firstPage != null) {
            listDelegate.onListItemClicked(null, firstPage);
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
    public void setPreferences(Array<PreferencesPagePresenter> preferences) {
        list.render(preferences);

        if (preferences.size() > 0) {
            firstPage = preferences.get(0);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}