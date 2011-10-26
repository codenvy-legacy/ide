/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.extension.java.client.project_explorer;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.extension.java.client.JavaClientBundle;
import org.exoplatform.ide.extension.java.client.JavaClientService;
import org.exoplatform.ide.extension.java.shared.ast.AstItem;
import org.exoplatform.ide.extension.java.shared.ast.CompilationUnit;
import org.exoplatform.ide.extension.java.shared.ast.JavaProject;
import org.exoplatform.ide.extension.java.shared.ast.Package;
import org.exoplatform.ide.extension.java.shared.ast.RootPackage;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ASTTreeViewModel implements TreeViewModel
{

   private final MultiSelectionModel<AstItem> selectionModel;

   private List<JavaProject> projects;

   public ASTTreeViewModel(MultiSelectionModel<AstItem> selectionModel, List<JavaProject> projects)
   {
      this.selectionModel = selectionModel;
      this.projects = projects;
   }

   private List<AstItem> astItemsList(List<? extends AstItem> list)
   {
      List<AstItem> items = new ArrayList<AstItem>();
      items.addAll(list);
      return items;
   }

   public class ProjectEntriesDataProvider extends AsyncDataProvider<AstItem>
   {

      private JavaProject javaProject;

      public ProjectEntriesDataProvider(JavaProject javaProject)
      {
         this.javaProject = javaProject;
      }

      @Override
      protected void onRangeChanged(HasData<AstItem> display)
      {
         JavaClientService.getInstance().getRootPackages("dev-monit", javaProject.getId(),
            new AsyncRequestCallback<List<RootPackage>>()
            {
               @Override
               protected void onSuccess(List<RootPackage> result)
               {
                  List<AstItem> items = new ArrayList<AstItem>();
                  items.addAll(result);
                  updateRowData(0, items);
               }
            });
      }
   };

   public class RootPackageEntriesDataProvider extends AsyncDataProvider<AstItem>
   {

      private RootPackage rootPackage;

      public RootPackageEntriesDataProvider(RootPackage rootPackage)
      {
         this.rootPackage = rootPackage;
      }

      @Override
      protected void onRangeChanged(HasData<AstItem> display)
      {
         JavaClientService.getInstance().getPackages("dev-monit", rootPackage.getProjectId(), rootPackage.getSource(),
            new AsyncRequestCallback<List<Package>>()
            {
               @Override
               protected void onSuccess(List<Package> result)
               {
                  List<AstItem> items = astItemsList(result);
                  if (items.size() == 0)
                  {
                     updateRowCount(0, true);
                  }
                  else
                  {
                     updateRowData(0, items);
                  }
               }
            });
      }
   }

   public class PackageEntriesDataProvider extends AsyncDataProvider<AstItem>
   {

      private Package pack;

      public PackageEntriesDataProvider(Package pack)
      {
         this.pack = pack;
      }

      @Override
      protected void onRangeChanged(HasData<AstItem> display)
      {
         JavaClientService.getInstance().getPackageEntries("dev-monit", pack.getProjectId(), pack.getSource(), pack.getName(), new AsyncRequestCallback<List<AstItem>>()
         {
            @Override
            protected void onSuccess(List<AstItem> result)
            {
               List<AstItem> items = astItemsList(result);
               if (items.size() == 0) {
                  updateRowCount(0, true);
               } else {
                  updateRowData(0, items);
               }
            }
         });
         
         //updateRowCount(0, true);
      }
   }

   @Override
   public <T> NodeInfo<?> getNodeInfo(T value)
   {
      if (value == null)
      {
         ListDataProvider<AstItem> dataProvider = new ListDataProvider<AstItem>(astItemsList(projects));
         return new DefaultNodeInfo<AstItem>(dataProvider, treeCell, selectionModel, null);
      }

      if (value instanceof JavaProject)
      {
         ProjectEntriesDataProvider pdp = new ProjectEntriesDataProvider((JavaProject)value);
         return new DefaultNodeInfo<AstItem>(pdp, treeCell, selectionModel, null);
      }

      if (value instanceof RootPackage)
      {
         RootPackageEntriesDataProvider dataProvider = new RootPackageEntriesDataProvider((RootPackage)value);
         return new DefaultNodeInfo<AstItem>(dataProvider, treeCell, selectionModel, null);
      }

      if (value instanceof Package)
      {
         PackageEntriesDataProvider dataProvider = new PackageEntriesDataProvider((Package)value);
         return new DefaultNodeInfo<AstItem>(dataProvider, treeCell, selectionModel, null);
      }
      
      if (value instanceof CompilationUnit) {
         return new DefaultNodeInfo<AstItem>(null, treeCell, selectionModel, null);
      }

      return null;
   }

   @Override
   public boolean isLeaf(Object value)
   {
      if (value == null)
      {
         return false;
      }

      if (value instanceof JavaProject)
      {
         return false;
      }

      if (value instanceof RootPackage)
      {
         return false;
      }

      if (value instanceof Package)
      {
         return false;
      }

      return true;
   }

   Cell<AstItem> treeCell = new AbstractCell<AstItem>()
   {

      private void renderHTML(SafeHtmlBuilder sb, ImageResource imageResource, String text)
      {
         String image = "<div style=\"left: 19px; position: absolute; line-height: 0px; top: 50%; margin-top: -8px;\">";
         image += ImageHelper.getImageHTML(imageResource);
         image += "</div>";
         sb.appendHtmlConstant(image);
         sb.appendHtmlConstant("<div style=\"left:40px; position:absolute;\">");
         sb.appendEscaped(text);
         sb.appendHtmlConstant("</div>");
      }

      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, AstItem value, SafeHtmlBuilder sb)
      {
         if (value == null)
         {
            return;
         }

         if (value instanceof JavaProject)
         {
            renderHTML(sb, JavaClientBundle.INSTANCE.javaProject(), ((JavaProject)value).getName());
         }

         if (value instanceof RootPackage)
         {
            renderHTML(sb, JavaClientBundle.INSTANCE.javaPackages(), ((RootPackage)value).getSource());
         }

         if (value instanceof Package)
         {
            renderHTML(sb, JavaClientBundle.INSTANCE.javaPackage(), ((Package)value).getName());
         }
         
         if (value instanceof CompilationUnit) {
            renderHTML(sb, JavaClientBundle.INSTANCE.javaFile(), ((CompilationUnit)value).getName() );
         }

      }

   };

}
