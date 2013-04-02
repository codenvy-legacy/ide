/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.client.project.resource;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardPagingPolicy.KeyboardPagingPolicy;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class FilesListGrid extends ListGrid<FileModel> implements HasAllKeyHandlers {

    private static final HashMap<String, String> mimeTypeImages = new HashMap<String, String>();

    private String build(String image, String name) {
        String html = "<div style=\"height: 16px; padding:0px; margin:0px; line-height:16px;\">";
        if (image != null) {
            html += "<div style=\"width:16px; height:16px; overflow:hidden; float:left;\">" + image + "</div>";
        }

        html += "<div style=\"float:left;\">&nbsp;" + name + "</div>";
        html += "</div>";
        return html;
    }

    private String renderCell(final FileModel file) {
        String name = file.getName();

        String img = mimeTypeImages.get(file.getMimeType());
        if (img == null) {
            ImageResource resource = ImageUtil.getIcon(file.getMimeType());
            img = ImageHelper.getImageHTML(resource);
            mimeTypeImages.put(file.getMimeType(), img);
        }

        return build(img, name);
    }

    public FilesListGrid() {
        Column<FileModel, SafeHtml> nameColumn = new Column<FileModel, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final FileModel file) {
                SafeHtml html = new SafeHtml() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public String asString() {
                        return renderCell(file);
                    }
                };

                return html;
            }
        };

        nameColumn.setCellStyleNames("default-cursor");
        getCellTable().addColumn(nameColumn);

        //getCellTable().setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        getCellTable().setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

        getCellTable().setKeyboardPagingPolicy(KeyboardPagingPolicy.CURRENT_PAGE);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return addDomHandler(handler, KeyUpEvent.getType());
    }

    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return addDomHandler(handler, KeyPressEvent.getType());
    }

}
