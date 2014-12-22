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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.projecttree.VirtualFile;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.annotation.Nonnull;

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
     * Returns the content description for this editor input. This content description is used for editors' information panel
     *
     * @return the content description for this input
     */
    String getContentDescription();

    /**
     * Returns the image descriptor for this input.
     *
     * @return the image resource for this input.
     */
    @Nonnull
    ImageResource getImageResource();

    /**
     * Returns the image descriptor for this input.
     *
     * @return the SVG image resource for this input.
     */
    @Nonnull
    SVGResource getSVGResource();

    /**
     * Returns the name of this editor input for display purposes.
     * <p/>
     * For instance, when the input is from a file, the return value would ordinarily be just the file name.
     *
     * @return the name string; never <code>null</code>;
     */
    @Nonnull
    String getName();

    /**
     * Returns the tool tip text for this editor input. This text is used to differentiate between two input with the same name. For
     * instance, MyClass.java in folder X and MyClass.java in folder Y. The format of the text varies between input types.
     * </p>
     *
     * @return the tool tip text; never <code>null</code>.
     */
    @Nonnull
    String getToolTipText();

    /**
     * Return the file of this input
     *
     * @return the File; never <code>null</code>
     */
    @Nonnull
    VirtualFile getFile();

    /**
     * Sets file of this input.
     *
     * @param file
     */
    void setFile(@Nonnull VirtualFile file);
}