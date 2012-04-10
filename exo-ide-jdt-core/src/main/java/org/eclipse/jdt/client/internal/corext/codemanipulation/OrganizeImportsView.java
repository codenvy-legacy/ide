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
package org.eclipse.jdt.client.internal.corext.codemanipulation;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.view.client.HasData;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.core.search.TypeNameMatch;
import org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter.Display;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class OrganizeImportsView extends ViewImpl implements Display
{

   private static OrganizeImportsViewUiBinder uiBinder = GWT.create(OrganizeImportsViewUiBinder.class);

   @UiField
   TextInput filterTextInput;

   @UiField
   Label pageLabel;

   @UiField(provided = true)
   CellList<TypeNameMatch> cellList = new CellList<TypeNameMatch>(new AbstractCell<TypeNameMatch>()
   {
      @Override
      public void render(Context context, TypeNameMatch value, SafeHtmlBuilder sb)
      {
         sb.appendEscaped(value.getFullyQualifiedName());
      }
   });

   @UiField
   ImageButton backButton;

   @UiField
   ImageButton nextButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   ImageButton finishButton;

   interface OrganizeImportsViewUiBinder extends UiBinder<Widget, OrganizeImportsView>
   {
   }

   public OrganizeImportsView()
   {
      super(ID, ViewType.MODAL, "Organize Imports", null, 500, 500, false);
      add(uiBinder.createAndBindUi(this));
   }

   /**
    * @see org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter.Display#getNextButton()
    */
   @Override
   public HasClickHandlers getNextButton()
   {
      return nextButton;
   }

   /**
    * @see org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter.Display#getFinishButton()
    */
   @Override
   public HasClickHandlers getFinishButton()
   {
      return finishButton;
   }

   /**
    * @see org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter.Display#getTypeList()
    */
   @Override
   public HasData<TypeNameMatch> getTypeList()
   {
      return cellList;
   }

   /**
    * @see org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter.Display#getFilterInput()
    */
   @Override
   public HasValue<String> getFilterInput()
   {
      return filterTextInput;
   }

   /**
    * @see org.eclipse.jdt.client.internal.corext.codemanipulation.OrganizeImportsPresenter.Display#getPageLabel()
    */
   @Override
   public HasText getPageLabel()
   {
      return pageLabel;
   }

}
