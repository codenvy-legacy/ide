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
package com.codenvy.ide.upload;

import com.codenvy.ide.CoreLocalizationConstant;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;
import com.googlecode.gwt.test.utils.events.Browser;

import org.junit.Before;
import org.junit.Test;


import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Testing {@link UploadFileViewImpl} functionality.
 *
 * @author Roman Nikitenko.
 */
@GwtModule("com.codenvy.ide.Core")
public class UploadFileViewImplTest extends GwtTestWithMockito {

    private UploadFileViewImpl  view;
    private UploadFilePresenter presenter;

    UploadFileViewImpl.UploadFileViewBinder binder = GWT.create(UploadFileViewImpl.UploadFileViewBinder.class);
    CoreLocalizationConstant                locale = GWT.create(CoreLocalizationConstant.class);

    @Before
    public void setup() {
        presenter = mock(UploadFilePresenter.class);
        view = new UploadFileViewImpl(binder, locale);
        view.setDelegate(presenter);
    }

    @Test
    public void clickOnCancelButton() {

        Browser.click(view.btnCancel);

        verify(presenter).onCancelClicked();
    }

    @Test
    public void clickOnUploadButton() {

        Browser.click(view.btnUpload);

        verify(presenter).onUploadClicked();
    }

    @Test
    public void getFileNameShouldBeExecuted() {
        view.file = mock(FileUpload.class);

        view.getFileName();

        verify(view.file).getName();
    }

    @Test
    public void submitShouldBeExecuted() {
        view.uploadForm = mock(FormPanel.class);

        view.submit();

        verify(view.uploadForm).submit();
    }

    @Test
    public void setActionShouldBeExecuted() {
        view.uploadForm = mock(FormPanel.class);

        view.setAction("url");

        verify(view.uploadForm).setAction(eq("url"));
        verify(view.uploadForm).setMethod(eq(FormPanel.METHOD_POST));
    }

    @Test
    public void setEncodingShouldBeExecuted() {
        view.uploadForm = mock(FormPanel.class);

        view.setEncoding(FormPanel.ENCODING_MULTIPART);

        verify(view.uploadForm).setEncoding(eq(FormPanel.ENCODING_MULTIPART));
    }

    @Test
    public void setEnabledUploadButtonShouldBeExecuted() {
        view.btnUpload = mock(Button.class);

        view.setEnabledUploadButton(true);
        verify(view.btnUpload).setEnabled(eq(true));
    }

    @Test
    public void closeShouldBeExecuted() {
        view.uploadForm = mock(FormPanel.class);

        view.close();

        verify(view.uploadForm).remove((FileUpload)anyObject());
    }
}
