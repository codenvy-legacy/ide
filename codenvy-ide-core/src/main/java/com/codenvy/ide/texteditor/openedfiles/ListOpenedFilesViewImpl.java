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
package com.codenvy.ide.texteditor.openedfiles;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.api.resources.model.File;
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
