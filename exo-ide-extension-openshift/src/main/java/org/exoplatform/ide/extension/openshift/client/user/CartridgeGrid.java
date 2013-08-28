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
package org.exoplatform.ide.extension.openshift.client.user;

import com.google.gwt.cell.client.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientBundle;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.OpenShiftEmbeddableCartridge;

import java.util.Collections;
import java.util.List;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 14, 2011 11:11:34 AM anya $
 */
public class CartridgeGrid extends ListGrid<OpenShiftEmbeddableCartridge> {
    private static final String ID = "ideOpenShiftCartridgeGrid";

    private Column<OpenShiftEmbeddableCartridge, OpenShiftEmbeddableCartridge> cartridgeNameColumn;

    private Column<OpenShiftEmbeddableCartridge, String> deleteCartridgeColumn;

    private Column<OpenShiftEmbeddableCartridge, String> startCartridgeColumn;

    private Column<OpenShiftEmbeddableCartridge, String> stopCartridgeColumn;

    private Column<OpenShiftEmbeddableCartridge, String> restartCartridgeColumn;

    private Column<OpenShiftEmbeddableCartridge, String> reloadCartridgeColumn;

    private AppInfo appInfo;

    public CartridgeGrid() {
        setID(ID);
        setWidth("auto");
        setHeight(140);
        initColumns();

        getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        getElement().getStyle().setBorderColor("#D4D4D4");
        getElement().getStyle().setOverflowY(Style.Overflow.AUTO);

        HTML emptyTable = new HTML(OpenShiftExtension.LOCALIZATION_CONSTANT.createAppForCartridgesView());
        emptyTable.getElement().getStyle().setMarginBottom(2, Style.Unit.PX);
        getCellTable().setEmptyTableWidget(emptyTable);

        setValue(Collections.<OpenShiftEmbeddableCartridge>emptyList());
    }

    /** Initialize columns. */
    private void initColumns() {
        cartridgeNameColumn = new Column<OpenShiftEmbeddableCartridge, OpenShiftEmbeddableCartridge>(new OpenShiftEmbeddableCartridgeCell(
                BrowserEvents.CLICK)) {

            @Override
            public OpenShiftEmbeddableCartridge getValue(OpenShiftEmbeddableCartridge cartridge) {
                return cartridge;
            }
        };

        deleteCartridgeColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ImageButtonCell(OpenShiftClientBundle.INSTANCE.deleteCartridge())) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge cartridge) {
                        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                        return "";
                    }
                };

        startCartridgeColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ImageButtonCell(OpenShiftClientBundle.INSTANCE.startCartridge())) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge cartridge) {
                        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                        return "";
                    }
                };

        stopCartridgeColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ImageButtonCell(OpenShiftClientBundle.INSTANCE.stopCartridge())) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge cartridge) {
                        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                        return "";
                    }
                };

        restartCartridgeColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ImageButtonCell(OpenShiftClientBundle.INSTANCE.restartCartridge())) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge cartridge) {
                        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                        return "";
                    }
                };

        reloadCartridgeColumn =
                new Column<OpenShiftEmbeddableCartridge, String>(new ImageButtonCell(OpenShiftClientBundle.INSTANCE.reloadCartridge())) {
                    @Override
                    public String getValue(OpenShiftEmbeddableCartridge cartridge) {
                        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
                        return "";
                    }
                };

        getCellTable().addColumn(cartridgeNameColumn, "Cartridge");
        getCellTable().setColumnWidth(cartridgeNameColumn, "100%");
        getCellTable().addColumn(startCartridgeColumn, "Start");
        getCellTable().setColumnWidth(startCartridgeColumn, "16");
        getCellTable().addColumn(stopCartridgeColumn, "Stop");
        getCellTable().setColumnWidth(stopCartridgeColumn, "16");
        getCellTable().addColumn(restartCartridgeColumn, "Restart");
        getCellTable().setColumnWidth(restartCartridgeColumn, "16");
        getCellTable().addColumn(reloadCartridgeColumn, "Reload");
        getCellTable().setColumnWidth(reloadCartridgeColumn, "16");
        getCellTable().addColumn(deleteCartridgeColumn, "Delete");
        getCellTable().setColumnWidth(deleteCartridgeColumn, "16");
    }

    /**
     * Handler for deleting applications.
     *
     * @param handler
     * @return
     */
    public HandlerRegistration addDeleteButtonSelectionHandler(final SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        deleteCartridgeColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {

            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
        return null;
    }

    public HandlerRegistration addStartButtonSelectionHandler(final SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        startCartridgeColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
        return null;
    }

    public HandlerRegistration addStopButtonSelectionHandler(final SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        stopCartridgeColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
        return null;
    }

    public HandlerRegistration addRestartButtonSelectionHandler(final SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        restartCartridgeColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
        return null;
    }

    public HandlerRegistration addReloadButtonSelectionHandler(final SelectionHandler<OpenShiftEmbeddableCartridge> handler) {
        reloadCartridgeColumn.setFieldUpdater(new FieldUpdater<OpenShiftEmbeddableCartridge, String>() {
            @Override
            public void update(int index, OpenShiftEmbeddableCartridge object, String value) {
                handler.onSelection(new SelectionEventImpl(object));
            }
        });
        return null;
    }

    /** Implementation of {@link com.google.gwt.event.logical.shared.SelectionEvent} event. */
    private class SelectionEventImpl extends SelectionEvent<OpenShiftEmbeddableCartridge> {
        /**
         * @param selectedItem
         *         selected application
         */
        protected SelectionEventImpl(OpenShiftEmbeddableCartridge selectedItem) {
            super(selectedItem);
        }

    }

    /** @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List) */
    @Override
    public void setValue(List<OpenShiftEmbeddableCartridge> value) {
        super.setValue(value);

        if (value.size() == 0) {
            setHeight(45);
        } else {
            setHeight(140);
        }

        updateGrid();
    }

    public void setApplicationInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    /** Cell for clicking to delete application. */
    private class ImageButtonCell extends ButtonCell {

        private ImageResource imgResource;

        private ImageButtonCell(ImageResource imgResource) {
            this.imgResource = imgResource;
        }

        @Override
        public void render(Context context, final SafeHtml data, SafeHtmlBuilder sb) {
            ImageResourceRenderer renderer = new ImageResourceRenderer();

            sb.appendHtmlConstant("<div style=\"cursor: pointer; margin-top: 2px;\">");
            sb.append(renderer.render(imgResource));
            sb.appendHtmlConstant("</div>");
        }
    }

    private class OpenShiftEmbeddableCartridgeCell extends AbstractCell<OpenShiftEmbeddableCartridge> {
        private OpenShiftEmbeddableCartridgeCell(String... consumedEvents) {
            super(consumedEvents);
        }

        @Override
        public void render(Context context, final OpenShiftEmbeddableCartridge value, SafeHtmlBuilder sb) {
            ImageResourceRenderer renderer = new ImageResourceRenderer();

            sb.appendHtmlConstant("<div style=\"float: left; margin-top: 3px;\">" + value.getName() + "</div>");
            sb.appendHtmlConstant("<div style=\"cursor: pointer; margin-top: 2px; float: left;\">");
            sb.append(renderer.render(OpenShiftClientBundle.INSTANCE.credentialCartridge()));
            sb.appendHtmlConstant("</div>");
        }


        @Override
        public void onBrowserEvent(Context context, Element parent, OpenShiftEmbeddableCartridge value, NativeEvent event,
                                   ValueUpdater<OpenShiftEmbeddableCartridge> valueUpdater) {
            if (CLICK.equals(event.getType())) {
                PopupPanel cartridgeCredPopup = new PopupPanel(true, true);
                cartridgeCredPopup.setWidth("auto");

                StringBuilder info = new StringBuilder();
                info.append("<ul style=\"");
                info.append("list-style: none;");
                info.append("-webkit-padding-start: 5px;");
                info.append("-webkit-padding-end: 5px;");
                info.append("-webkit-margin-after: 5px;");
                info.append("-webkit-margin-before: 5px;\">");

                if (value.getProperties().size() > 0) {
                    if (value.getProperties().get("connection_url") != null) {
                        info.append("<li><span style=\"font-weight: bold;\">Connection url:</span>&nbsp;");
                        info.append(value.getProperties().get("connection_url"));
                        info.append("</li>");
                    }

                    if (value.getProperties().get("username") != null) {
                        info.append("<li><span style=\"font-weight: bold;\">Username:</span>&nbsp;");
                        info.append(value.getProperties().get("username"));
                        info.append("</li>");
                    }

                    if (value.getProperties().get("password") != null) {
                        info.append("<li><span style=\"font-weight: bold;\">Password:</span>&nbsp;");
                        info.append(value.getProperties().get("password"));
                        info.append("</li>");
                    }

                    if (value.getProperties().get("database_name") != null) {
                        info.append("<li><span style=\"font-weight: bold;\">Database url:</span>&nbsp;");
                        info.append(value.getProperties().get("database_name"));
                        info.append("</li>");
                    }
                } else if (value.getName().startsWith("haproxy")) {
                    info.append("<li><span style=\"font-weight: bold;\">Status url:</span>&nbsp;");
                    info.append(
                            "<a href=\"" + appInfo.getPublicUrl() + "haproxy-status/\">" + appInfo.getPublicUrl() + "haproxy-status/</a>");
                    info.append("</li>");
                } else {
                    info.append("<li>No properties provided.</li>");
                }

                info.append("</ul>");

                cartridgeCredPopup.setWidget(new HTML(info.toString()));

                int left = event.getClientX() + 10;
                int top = event.getClientY() + 10;
                cartridgeCredPopup.setPopupPosition(left, top);
                cartridgeCredPopup.show();
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
        }
    }
}
