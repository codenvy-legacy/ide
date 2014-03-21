/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.texteditor.openedfiles;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.File;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link ListOpenedFilesView} view.
 * 
 * @author Ann Shumilova
 */
@Singleton
public class ListOpenedFilesViewImpl extends PopupPanel implements ListOpenedFilesView {

    private ActionDelegate       delegate;

    private PartStackUIResources partStackResources;

    private VerticalPanel        container;

    private class FileItem extends Composite implements HasClickHandlers, HasCloseHandlers<FileItem> {

        private Image closeButton;

        private File  file;

        public FileItem(File file) {
            this.file = file;
            FlowPanel panel = new FlowPanel();
            panel.setStyleName(partStackResources.partStackCss().idePartStackMultipleTabsItem());
            initWidget(panel);
            Label label = new InlineLabel(file.getName());
            panel.add(label);
            closeButton = new Image(partStackResources.close());
            closeButton.addStyleName(partStackResources.partStackCss().idePartStackTabCloseButton());
            panel.add(closeButton);
            addHandlers();
        }

        @Override
        public HandlerRegistration addClickHandler(ClickHandler handler) {
            return addDomHandler(handler, ClickEvent.getType());
        }


        private void addHandlers() {
            closeButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    CloseEvent.fire(FileItem.this, FileItem.this);
                    event.stopPropagation();
                    event.preventDefault();
                }
            });
        }

        public File getFile() {
            return file;
        }

        /** {@inheritDoc} */
        @Override
        public HandlerRegistration addCloseHandler(CloseHandler<FileItem> handler) {
            return addHandler(handler, CloseEvent.getType());
        }
    }

    @Inject
    public ListOpenedFilesViewImpl(PartStackUIResources partStackResources) {
        this.partStackResources = partStackResources;

        container = new VerticalPanel();

        this.setWidget(container);
        this.getElement().setClassName(partStackResources.partStackCss().idePartStackMultipleTabsContainer());
        setAutoHideEnabled(true);

        addCloseHandler(new CloseHandler<PopupPanel>() {

            @Override
            public void onClose(CloseEvent<PopupPanel> event) {
                delegate.onClose();
            }
        });
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
    public void showDialog(int x, int y) {
        this.show();
        this.setPopupPosition(x - getOffsetWidth(), y + 1);
    }

    /** {@inheritDoc} */
    @Override
    public void setOpenedFiles(Array<File> files) {
        container.clear();
        for (File file : files.asIterable()) {
            FileItem fileItem = new FileItem(file);
            bindEvents(fileItem);
            container.add(fileItem);
        }

    }

    private void bindEvents(final FileItem fileItem) {
        fileItem.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onFileSelected(fileItem.getFile());
            }
        });

        fileItem.addCloseHandler(new CloseHandler<ListOpenedFilesViewImpl.FileItem>() {

            @Override
            public void onClose(CloseEvent<FileItem> event) {
                delegate.onCloseFile(event.getTarget().getFile());
            }
        });
    }


}
