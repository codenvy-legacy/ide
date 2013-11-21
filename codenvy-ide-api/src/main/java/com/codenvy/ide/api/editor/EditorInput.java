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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.resources.model.File;
import com.google.gwt.resources.client.ImageResource;

/**
 * <code>EditorInput</code> is a light weight descriptor of editor input, like a file name but more abstract. It is not a model. It is a
 * description of the model source for an <code>Editor</code>.
 * <p>
 * An editor input is passed to an editor via the <code>EditorPartPresenter.init</code> method.
 * </p>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public interface EditorInput {
    /**
     * Returns the image descriptor for this input.
     *
     * @return the image resource for this input; never <code>null</code>
     */
    @NotNull
    ImageResource getImageResource();

    /**
     * Returns the name of this editor input for display purposes.
     * <p/>
     * For instance, when the input is from a file, the return value would ordinarily be just the file name.
     *
     * @return the name string; never <code>null</code>;
     */
    @NotNull
    String getName();

    /**
     * Returns the tool tip text for this editor input. This text is used to differentiate between two input with the same name. For
     * instance, MyClass.java in folder X and MyClass.java in folder Y. The format of the text varies between input types.
     * </p>
     *
     * @return the tool tip text; never <code>null</code>.
     */
    @NotNull
    String getToolTipText();

    /**
     * Return the file of this input
     *
     * @return the File; never <code>null</code>
     */
    @NotNull
    File getFile();
}