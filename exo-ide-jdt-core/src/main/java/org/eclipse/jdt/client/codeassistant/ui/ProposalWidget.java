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
package org.eclipse.jdt.client.codeassistant.ui;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

import org.eclipse.jdt.client.codeassistant.AbstractJavaCompletionProposal;
import org.eclipse.jdt.client.core.dom.Modifier;
import org.eclipse.jdt.client.templates.TemplateProposal;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;

/**
 * Base class for UI representation of token.<br>
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 4:13:18 PM evgen $
 */
public class ProposalWidget extends Composite implements HasClickHandlers, HasMouseOverHandlers, HasDoubleClickHandlers
{

   protected CompletionProposal proposal;

   protected Grid grid;

   public ProposalWidget(CompletionProposal proposal)
   {
      this.proposal = proposal;

      grid = new Grid(1, 2);
      grid.setStyleName(JavaClientBundle.INSTANCE.css().item());
      grid.setWidth("100%");
      Image i = proposal.getImage(); // getImage(proposal.getFlags());
      if (i == null)
         i = new Image(JavaClientBundle.INSTANCE.blankImage());

      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      HTML html = new HTML(proposal.getDisplayString());
      DOM.setStyleAttribute(html.getElement(), "whiteSpace", "nowrap");
      grid.setWidget(0, 1, html);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      initWidget(grid);

   }

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
   public String getName()
   {
      if (proposal instanceof AbstractJavaCompletionProposal)
         return ((AbstractJavaCompletionProposal)proposal).getSortString();
      if (proposal instanceof TemplateProposal)
         return ((TemplateProposal)proposal).getTemplate().getName();
      return proposal.getDisplayString();
   }

   /**
    * Get token description. It's may be javadoc, template content etc.
    * 
    * @return {@link Widget} with description
    */
   public Widget getDecription()
   {
      return proposal.getAdditionalProposalInfo();
   }

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

}
