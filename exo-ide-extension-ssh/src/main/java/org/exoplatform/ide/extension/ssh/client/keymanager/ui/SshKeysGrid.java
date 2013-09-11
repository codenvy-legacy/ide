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
package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.ssh.shared.KeyItem;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: SshKeysGrid May 18, 2011 12:20:34 PM evgen $
 */
public class SshKeysGrid extends ListGrid<KeyItem> implements HasSshGrid<KeyItem> {

    private Column<KeyItem, String> hostColumn;

    private Column<KeyItem, String> publicKeyColumn;

    private Column<KeyItem, String> deleteKeyColumn;

    /**
     *
     */
    public SshKeysGrid() {
        TextCell hostCell = new TextCell();
        hostColumn = new Column<KeyItem, String>(hostCell) {
            @Override
            public String getValue(KeyItem object) {
                return object.getHost();
            }
        };
        publicKeyColumn = new Column<KeyItem, String>(new Link()) {

            @Override
            public String getValue(KeyItem object) {
                if (object.getPublicKeyURL() != null)
                    return "View";
                else
                    return "";
            }
        };

        deleteKeyColumn = new Column<KeyItem, String>(new Link()) {

            @Override
            public String getValue(KeyItem object) {
                return "Delete";
            }
        };

        hostColumn.setSortable(true);
        publicKeyColumn.setFieldUpdater(new FieldUpdater<KeyItem, String>() {

            @Override
            public void update(int index, KeyItem object, String value) {
            }
        });

        getCellTable().addColumn(hostColumn, "Host");
        getCellTable().addColumn(publicKeyColumn, "Public Key");
        getCellTable().addColumn(deleteKeyColumn, "Delete");
        getCellTable().setColumnWidth(hostColumn, 50, Unit.PCT);
        getCellTable().setColumnWidth(publicKeyColumn, 30, Unit.PX);
        getCellTable().setColumnWidth(deleteKeyColumn, 30, Unit.PX);
    }

    /** @see org.exoplatform.ide.extension.ssh.client.keymanager.HasSshGrid#addViewButtonSelectionHandler(com.google.gwt.event.logical
     * .shared.SelectionHandler) */
    @Override
    public HandlerRegistration addViewButtonSelectionHandler(final SelectionHandler<KeyItem> handler) {
        publicKeyColumn.setFieldUpdater(new FieldUpdater<KeyItem, String>() {

            @Override
            public void update(int index, KeyItem object, String value) {

                handler.onSelection(new SelectionEventImpl(object));
            }
        });
        return null;
    }

    /** @see org.exoplatform.ide.extension.ssh.client.keymanager.HasSshGrid#addDeleteButtonSelectionHandler(com.google.gwt.event.logical
     * .shared.SelectionHandler) */
    @Override
    public HandlerRegistration addDeleteButtonSelectionHandler(final SelectionHandler<KeyItem> handler) {
        deleteKeyColumn.setFieldUpdater(new FieldUpdater<KeyItem, String>() {

            @Override
            public void update(int index, KeyItem object, String value) {

                handler.onSelection(new SelectionEventImpl(object));
            }
        });
        return null;
    }

    private class SelectionEventImpl extends SelectionEvent<KeyItem> {
        /** @param selectedItem */
        protected SelectionEventImpl(KeyItem selectedItem) {
            super(selectedItem);
        }

    }

    private class Link extends ClickableTextCell {
        /**
         * @see com.google.gwt.cell.client.ClickableTextCell#render(com.google.gwt.cell.client.Cell.Context,
         *      com.google.gwt.safehtml.shared.SafeHtml, com.google.gwt.safehtml.shared.SafeHtmlBuilder)
         */
        @Override
        protected void render(com.google.gwt.cell.client.Cell.Context context, final SafeHtml value, SafeHtmlBuilder sb) {
            SafeHtml s = new SafeHtml() {
                private static final long serialVersionUID = 1L;

                @Override
                public String asString() {
                    return "<u style=\"cursor: pointer; color:#2039f8\">" + value.asString() + "</u>";
                }
            };
            sb.append(s);

        }
    }
}
