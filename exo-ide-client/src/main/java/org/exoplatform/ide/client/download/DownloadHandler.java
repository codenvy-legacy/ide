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

package org.exoplatform.ide.client.download;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.messages.IdeUploadLocalizationConstant;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.List;

/** Process download selected in project explorer item. */
public class DownloadHandler implements ItemsSelectedHandler, DownloadItemHandler {

    private static final IdeUploadLocalizationConstant UPLOAD_LOCALIZATION_CONSTANT = GWT
            .create(IdeUploadLocalizationConstant.class);

    private AbsolutePanel downloadPanel;

    private List<Item> selectedItems = new ArrayList<Item>();

    public DownloadHandler() {
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
        IDE.addHandler(DownloadItemEvent.TYPE, this);

        IDE.getInstance().addControl(new DownloadItemControl(false));
        IDE.getInstance().addControl(new DownloadItemControl(true));
    }

    private void downloadResource(String url) {
        if (downloadPanel == null) {
            downloadPanel = new AbsolutePanel();
            downloadPanel.getElement().getStyle().setWidth(1, Unit.PX);
            downloadPanel.getElement().getStyle().setHeight(1, Unit.PX);
            downloadPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
            RootPanel.get().add(downloadPanel, -10000, -10000);
        }

        Frame frame = new Frame(url);
        frame.setHeight("100%");
        frame.setWidth("100%");
        downloadPanel.add(frame);
    }

    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        selectedItems = event.getSelectedItems();
    }

    @Override
    public void onDownloadItem(DownloadItemEvent event) {
        if (selectedItems.size() != 1) {
            Dialogs.getInstance().showError("Only one file must be selected.");
            return;
        }

        Item item = selectedItems.get(0);

        Link downloadLink = getLink(item);

        if (downloadLink == null) {
            getItemForDownload(item);
        } else {
            downloadResource(downloadLink.getHref());
        }
    }

    /** Try to get download link from item. */
    private Link getLink(Item item) {
        if (item instanceof FileModel) {
            return item.getLinkByRelation(Link.REL_DOWNLOAD_FILE);
        } else if (item instanceof FolderModel) {
            return item.getLinkByRelation(Link.REL_DOWNLOAD_ZIP);
        } else {
            return null;
        }
    }

    /** Try to get item and fetch download link if it not exist. */
    private void getItemForDownload(Item item) {
        try {
            VirtualFileSystem.getInstance()
                             .getItemById(item.getId(),
                                          new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper())) {
                                              @Override
                                              protected void onSuccess(ItemWrapper result) {
                                                  Link downloadLink = getLink(result.getItem());
                                                  if (downloadLink != null) {
                                                      downloadResource(downloadLink.getHref());
                                                  } else {
                                                      Dialogs.getInstance().showError(UPLOAD_LOCALIZATION_CONSTANT.downloadFileError());
                                                  }
                                              }

                                              @Override
                                              protected void onFailure(Throwable exception) {
                                                  Dialogs.getInstance().showError(UPLOAD_LOCALIZATION_CONSTANT.downloadFileError());
                                              }
                                          });
        } catch (RequestException e) {
            Dialogs.getInstance().showError(UPLOAD_LOCALIZATION_CONSTANT.downloadFileError());
        }
    }

}
