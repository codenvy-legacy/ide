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
