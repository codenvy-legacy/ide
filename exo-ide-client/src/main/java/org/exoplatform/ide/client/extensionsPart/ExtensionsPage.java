/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.extensionsPart;

import com.google.inject.Singleton;

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;

import org.exoplatform.ide.extension.ExtensionDescription;
import org.exoplatform.ide.extension.ExtensionManager;
import org.exoplatform.ide.part.PartPresenter;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@Singleton
public class ExtensionsPage implements PartPresenter
{

   private final ExtensionManager manager;

   @Inject
   public ExtensionsPage(ExtensionManager manager)
   {
      this.manager = manager;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void go(HasWidgets container)
   {

      StringBuilder builder = new StringBuilder();

      for (ExtensionDescription extensionDescription : manager.getExtensions())
      {
         builder.append("<div>");

         builder.append("<h3>");
         builder.append(extensionDescription.getName());
         builder.append("-");
         builder.append(extensionDescription.getVersion());
         builder.append("</h3>");

         if (!extensionDescription.getDependencies().isEmpty())
         {
            builder.append("<ul>");
            for (String dependency : extensionDescription.getDependencies())
            {
               builder.append("<li>");
               builder.append(dependency);
               builder.append("</li>");
            }
            builder.append("</ul>");
         }

         builder.append("</div>");
      }

      HTMLPanel htmlPanel = new HTMLPanel(builder.toString());
      container.add(htmlPanel);
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void onOpen()
   {
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public boolean onClose()
   {
      return false;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public String getTitle()
   {
      return "Extensions";
   }

}
