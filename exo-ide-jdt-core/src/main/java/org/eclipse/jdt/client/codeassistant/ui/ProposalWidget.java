/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.eclipse.jdt.client.codeassistant.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.dom.Modifier;
import org.eclipse.jdt.client.text.BadLocationException;
import org.eclipse.jdt.client.text.IDocument;
import org.eclipse.jdt.client.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * Base class for UI representation of token.<br>
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 4:13:18 PM evgen $
 */
public abstract class ProposalWidget extends Composite implements HasClickHandlers, HasMouseOverHandlers,
   HasDoubleClickHandlers
{

   protected CompletionProposal proposal;

   protected Grid grid;

   public ProposalWidget(CompletionProposal proposal)
   {
      this.proposal = proposal;

      grid = new Grid(1, 4);
      grid.setStyleName(JavaClientBundle.INSTANCE.css().item());
      grid.setWidth("100%");
      ImageResource image = getImage(proposal.getFlags());
      if (image == null)
         image = JavaClientBundle.INSTANCE.blankImage();

      Image i = new Image(image);
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      Label nameLabel = new Label(getName() + " ", false);
      nameLabel.getElement().setInnerHTML(getModifiers(proposal.getFlags()) + nameLabel.getElement().getInnerHTML());
      grid.setWidget(0, 1, nameLabel);

      String typeSignature = getTypeSignature();
      if (typeSignature != null)
      {
         Label type = new Label(" : " + typeSignature, false);
         grid.setWidget(0, 2, type);
      }

      String classSignature = getClassSignature();
      if (classSignature != null)
      {
         Label l = new Label("-" + classSignature, false);
         l.setStyleName(JavaClientBundle.INSTANCE.css().fqnStyle());
         grid.setWidget(0, 3, l);
      }

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 3, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 3, "100%");
      initWidget(grid);

   }

   protected abstract ImageResource getImage(int flags);

   protected abstract String getTypeSignature();

   protected abstract String getClassSignature();

   protected String getModifiers(int flags)
   {

      String span =
         "<span style = \"position: absolute; margin-top: -5px; margin-left: -25px; width: 22px; "
            + "height: 10px; font-family:  font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 10px; \">";
      span += (Modifier.isAbstract(flags)) ? "<font color ='#004e00' style='float: right;'>A</font>" : "";
      span += (Modifier.isFinal(flags)) ? "<font color ='#174c83' style='float: right;'>F</font>" : "";
      span += (Modifier.isStatic(flags)) ? "<font color ='#6d0000' style='float: right;'>S</font>" : "";
      span += "</span>";
      return span;
   }

   /** @return the token */
   public CompletionProposal getProposal()
   {
      return proposal;
   }

   /** @return name of token */
   public abstract String getName();

   /**
    * Get token description. It's may be javadoc, template content etc.
    * 
    * @return {@link Widget} with description
    */
   public abstract Widget getDecription();

   /** Calls when user select this {@link Widget} */
   public void setSelectedStyle()
   {
      setStyleName(JavaClientBundle.INSTANCE.css().selectedItem());
   }

   /** Calls when clear selection or mouse blur this {@link Widget} */
   public void setDefaultStyle()
   {
      setStyleName(JavaClientBundle.INSTANCE.css().item());
   }

   /** @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler) */
   public HandlerRegistration addClickHandler(ClickHandler handler)
   {

      return addDomHandler(handler, ClickEvent.getType());
   }

   /** @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler) */
   public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
   {
      return addDomHandler(handler, MouseOverEvent.getType());
   }

   /** @see com.google.gwt.event.dom.client.HasDoubleClickHandlers#addDoubleClickHandler(com.google.gwt.event.dom.client.DoubleClickHandler) */
   public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
   {
      return addDomHandler(handler, DoubleClickEvent.getType());
   }
   
   /**
    * Inserts the proposed completion into the given document.
    *
    * @param document the document into which to insert the proposed completion
    */
   public abstract void apply(IDocument document);

}
