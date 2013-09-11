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
