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
package org.exoplatform.ide.client.framework.ui.upload;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FileUpload;

import org.exoplatform.gwtframework.ui.client.GwtResources;

/**
 * A widget that wraps the HTML &lt;input type='file'&gt; element.
 * <p/>
 * When file selected, calls onFileSelected method from {@link FileSelectedHandler}
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class FileUploadInput extends FileUpload implements HasFileSelectedHandler {

    public FileUploadInput() {
        setName(FormFields.FILE);
        addStyleName("UploadFile-FileSelect");

        ((InputElement)getElement().cast()).setSize(1);
        sinkEvents(Event.ONCHANGE);
    }

    public void onBrowserEvent(Event event) {
        String fileName = getFilename();

        if (fileName == null || fileName.trim().length() == 0) {
            return;
        }

        FileSelectedEvent fileSelectedEvent = new FileSelectedEvent(fileName);
        fireEvent(fileSelectedEvent);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
    }

    public void setTransparent(boolean transparent) {
        if (transparent) {
            setStyleName(GwtResources.INSTANCE.css().transparent(), true);
        } else {
            removeStyleName(GwtResources.INSTANCE.css().transparent());
        }
    }

    @Override
    public HandlerRegistration addFileSelectedHandler(FileSelectedHandler handler) {
        return addHandler(handler, FileSelectedEvent.TYPE);
    }

}
