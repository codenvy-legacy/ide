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
package com.codenvy.ide.tutorial.wysiwyg.editor;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.api.editor.AbstractEditorPresenter;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RichTextArea;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WysiwygEditor extends AbstractEditorPresenter {

    private ProjectServiceClient projectServiceClient;
    private RichTextArea textArea;

    public WysiwygEditor(ProjectServiceClient projectServiceClient) {
        this.projectServiceClient = projectServiceClient;
    }

    /** {@inheritDoc} */
    @Override
    protected void initializeEditor() {
        //create editor
        textArea = new RichTextArea();

        projectServiceClient.getFileContent(input.getFile().getPath(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                textArea.setHTML(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(WysiwygEditor.class, exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void doSave() {
    }

    @Override
    public void doSave(AsyncCallback<EditorInput> callback) {

    }

    /** {@inheritDoc} */
    @Override
    public void doSaveAs() {
    }

    /** {@inheritDoc} */
    @Override
    public void activate() {
        textArea.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "WYSIWYG Editor: " + input.getFile().getName();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return input.getImageResource();
    }

    /** {@inheritDoc} */
    @Override
    public SVGResource getTitleSVGImage() {
        return input.getSVGResource();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        textArea.setSize("100%", "100%");
        RichTextToolbar toolbar = new RichTextToolbar(textArea);

        DockLayoutPanel panel = new DockLayoutPanel(Style.Unit.PX);
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.addNorth(toolbar, 60);
        panel.add(textArea);

        // Add the components to a panel
        container.setWidget(panel);
    }
}
