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
package org.exoplatform.ide.client.hotkeys.show;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.IDE;

/**
 * Grid for displaying keyboard shortcuts list.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: HotKeyItemListGrid.java May 10, 2012 2:21:56 PM azatsarynnyy $
 */

public class HotKeyItemListGrid extends ListGrid<HotKeyItem> {

    /** List grid identifier. */
    private final static String ID = "ideShowHotKeysListGrid";

    /** Title of command column. */
    private final static String COMMAND_COLUMN_TITLE = IDE.PREFERENCES_CONSTANT.showHotKeysListGridCommand();

    /** Title of shortcut column. */
    private final static String SHORTCUT_COLUMN_TITLE = IDE.PREFERENCES_CONSTANT.showHotKeysListGridShortcut();

    /** Text displaying for pop-up menu control title. */
    private final static String POPUP = IDE.PREFERENCES_CONSTANT.showHotKeysListGridPopup();

    public HotKeyItemListGrid() {
        setID(ID);
        initColumns();
    }

    /** Initialize column settings. */
    private void initColumns() {
        Column<HotKeyItem, SafeHtml> commandColumn =
                new Column<HotKeyItem, SafeHtml>(new SafeHtmlCell()) {

                    @Override
                    public SafeHtml getValue(final HotKeyItem item) {
                        SafeHtml html = new SafeHtml() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public String asString() {
                                return getItemTitle(item);
                            }
                        };
                        return html;
                    }

                };
        getCellTable().addColumn(commandColumn, COMMAND_COLUMN_TITLE);
        getCellTable().setColumnWidth(commandColumn, 50, Unit.PCT);

        Column<HotKeyItem, SafeHtml> shortcutColumn =
                new Column<HotKeyItem, SafeHtml>(new SafeHtmlCell()) {

                    @Override
                    public SafeHtml getValue(final HotKeyItem item) {
                        SafeHtml html = new SafeHtml() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public String asString() {
                                return item.getHotKey() == null ? "" : item.getHotKey();
                            }
                        };
                        return html;
                    }

                };
        getCellTable().addColumn(shortcutColumn, SHORTCUT_COLUMN_TITLE);
        getCellTable().setColumnWidth(shortcutColumn, 100, Unit.PCT);
    }

    /**
     * Returns keyboard shortcut title for displaying in list grid.
     *
     * @param item
     *         keyboard shortcut item
     * @return shortcut title
     */
    private String getItemTitle(HotKeyItem item) {
        if (item.isGroup()) {
            String title = item.getTitle();
            title = title.replace("/", "&nbsp;/&nbsp;");
            title = getDivider(title);
            return title;
        } else {
            String title = "";
            if (item.getCommand() == null) {
                return item.getTitle();
            }
            String commandName = item.getCommand().getId();
            if (commandName.indexOf("/") >= 0) {
                commandName = commandName.substring(commandName.lastIndexOf("/") + 1);
            }

            while (commandName.indexOf("\\") >= 0) {
                commandName = commandName.replace("\\", "/");
            }

            if (item.getCommand() instanceof PopupMenuControl) {
                commandName += "&nbsp;[" + POPUP + "]";
            }

            if (item.getCommand().getNormalImage() != null) {
                Image image = new Image(item.getCommand().getNormalImage());
                String imageHTML = ImageHelper.getImageHTML(image);
                title = "<span>" + imageHTML + "&nbsp;" + commandName + "</span>";
            } else if (item.getCommand().getIcon() != null) {
                title = "<span><img src = \"" + item.getCommand().getIcon() + "\"/>&nbsp;" + commandName + "</span>";
            } else {
                title = "<span>" + commandName + "</span>";
            }

            return title;
        }
    }

    /**
     * Returns formatted title for divider of items group.
     *
     * @param title
     *         divider title
     * @return formatted divider title
     */
    private String getDivider(String title) {
        String divider =
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"width:100%; height:20px;\">"
                + "<tr><td></td><td>&nbsp;" + "<b><font color=\"#3764A3\" style=\"font-size: 12px; margin-left: 15px\">"
                + title + "</font></b>" + "&nbsp;</td><td></td></tr>" + "</table>";

        return divider;
    }

}
