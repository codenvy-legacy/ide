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
package com.codenvy.ide.preferences;

import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ui.list.SimpleList;
import com.codenvy.ide.ui.list.SimpleList.View;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
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
public class PreferencesViewImpl extends DialogBox implements PreferencesView {
    interface PreferenceViewImplUiBinder extends UiBinder<Widget, PreferencesViewImpl> {
    }

    @UiField
    Button      btnClose;
    @UiField
    Button      btnOk;
    @UiField
    Button      btnApply;
    @UiField
    ScrollPanel preferences;
    @UiField
    SimplePanel contentPanel;
    @UiField(provided = true)
    Resources   res;
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
                            "<div id=\"" + UIObject.DEBUG_ID_PREFIX + "preferencesView-tableCellElement-" + itemData.getTitle() + "\">");
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
    protected PreferencesViewImpl(Resources resources, PreferenceViewImplUiBinder uiBinder) {
        this.res = resources;

        Widget widget = uiBinder.createAndBindUi(this);

        this.setText("Preferences");
        this.setWidget(widget);

        //create list of preferences
        TableElement tableElement = Elements.createTableElement();
        tableElement.setAttribute("style", "width: 100%");
        list = SimpleList.create((View)tableElement, res.defaultSimpleListCss(), listItemRenderer, listDelegate);
        this.preferences.add(list);
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

    @UiHandler("btnApply")
    void onBtnApplyClick(ClickEvent event) {
        delegate.onApplyClicked();
    }

    @UiHandler("btnOk")
    void onBtnOkClick(ClickEvent event) {
        delegate.onOkClicked();
    }

    @UiHandler("btnClose")
    void onBtnCloseClick(ClickEvent event) {
        delegate.onCloseClicked();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showPreferences() {
        this.center();
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
}