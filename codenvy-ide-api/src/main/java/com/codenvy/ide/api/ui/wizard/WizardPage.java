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
package com.codenvy.ide.api.ui.wizard;

import com.codenvy.ide.api.mvp.Presenter;
import com.google.gwt.resources.client.ImageResource;

import static com.codenvy.ide.api.ui.wizard.WizardModel.UpdateDelegate;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public interface WizardPage extends Presenter {
    public interface CommitCallback {

        void onSuccess();

        void onFailure(Throwable exception);
    }

    void setUpdateDelegate(UpdateDelegate delegate);

    /**
     * Returns wizard page caption.
     *
     * @return caption
     */
    String getCaption();

    /**
     * Returns notice of wizard's page. Notice is a text message
     * displayed on the top of the dialog, usually used to guide
     * user through the process of filling the wizard pages. It
     * displays notices, error prompts and etc.
     *
     * @return notice text if any or null
     */
    String getNotice();

    /**
     * Returns this wizard page's image.
     *
     * @return the image for this wizard page, or <code>null</code> if none
     */
    ImageResource getImage();

    /**
     * Returns whether this page is complete or not.
     * This information is typically used by the wizard to decide
     * when it is okay to finish or to flip to the next page.
     *
     * @return <code>true</code> if this page is complete, and
     *         <code>false</code> otherwise
     */
    boolean isCompleted();

    boolean canSkip();

    void focusComponent();

    void commit(CommitCallback callback);

    void storeOptions();

    void removeOptions();

    void setContext(WizardContext wizardContext);
}