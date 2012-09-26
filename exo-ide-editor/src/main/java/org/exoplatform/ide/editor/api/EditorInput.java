/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.api;

import org.exoplatform.ide.resources.model.File;

import com.google.gwt.resources.client.ImageResource;

/**
 * <code>EditorInput</code> is a light weight descriptor of editor input,
 * like a file name but more abstract. It is not a model. It is a description of
 * the model source for an <code>Editor</code>.
 * <p>
 * An editor input is passed to an editor via the <code>EditorPartPresenter.init</code>
 * method.
 * </p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface EditorInput
{
   /**
    * Returns the image descriptor for this input.
    * 
    * @return the image resource for this input; never <code>null</code>
    */
   public ImageResource getImageResource();

   /**
    * Returns the name of this editor input for display purposes.
    * <p>
    * For instance, when the input is from a file, the return value would
    * ordinarily be just the file name.
    * 
    * @return the name string; never <code>null</code>;
    */
   public String getName();
   
   /**
    * Returns the tool tip text for this editor input. This text is used to
    * differentiate between two input with the same name. For instance,
    * MyClass.java in folder X and MyClass.java in folder Y. The format of the
    * text varies between input types.
    * </p>
    * 
    * @return the tool tip text; never <code>null</code>.
    */
   public String getToolTipText();
   
   /**
    * Return the file of this input
    * @return the File; never <code>null</code>
    */
   public File getFile();
}
