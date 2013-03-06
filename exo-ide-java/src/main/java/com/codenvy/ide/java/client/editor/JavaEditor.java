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
package com.codenvy.ide.java.client.editor;

import com.codenvy.ide.java.client.JavaClientBundle;
import com.codenvy.ide.java.client.editor.outline.JavaNodeRenderer;

import com.codenvy.ide.Resources;
import com.codenvy.ide.editor.DocumentProvider;
import com.codenvy.ide.outline.OutlineImpl;
import com.codenvy.ide.outline.OutlineModel;
import com.codenvy.ide.outline.OutlinePresenter;
import com.codenvy.ide.texteditor.TextEditorPresenter;

import com.codenvy.ide.util.executor.UserActivityManager;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaEditor extends TextEditorPresenter
{

   private final Resources resources;

   private OutlineImpl outline;

   /**
    * @param resources
    * @param userActivityManager
    * @param documentProvider
    * @param configuration
    */
   public JavaEditor(Resources resources, UserActivityManager userActivityManager, DocumentProvider documentProvider,
      JavaEditorConfiguration configuration)
   {
      super(resources, userActivityManager, documentProvider, configuration);
      this.resources = resources;
      configuration.setEditor(this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OutlinePresenter getOutline()
   {
      if (configuration instanceof JavaEditorConfiguration)
      {
         if (outline == null)
         {
            JavaEditorConfiguration conf = (JavaEditorConfiguration)configuration;
            OutlineModel outlineModel = conf.getOutlineModel();
            outline =
               new OutlineImpl(resources, outlineModel, new JavaNodeRenderer(JavaClientBundle.INSTANCE), editor, this);
         }
         return outline;
      }
      return super.getOutline();
   }
}
