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
package org.exoplatform.ide.editor.codeassistant.java.ui;

import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;
import org.exoplatform.ide.editor.codeassistant.util.ModifierHelper;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 9:42:07 AM evgen $
 *
 */
public abstract class JavaTokenWidgetBase extends TokenWidget
{

   protected int modifieres;

   protected String docContext;

   /**
    * @param token
    */
   @SuppressWarnings("unchecked")
   public JavaTokenWidgetBase(Token token, String docContext)
   {
      super(token);
      this.docContext = docContext;
      if (token.hasProperty(TokenProperties.MODIFIERS))
      {
         TokenProperty mod = token.getProperty(TokenProperties.MODIFIERS);
         if (mod.isNumericProperty() != null)
            modifieres = mod.isNumericProperty().numberValue().intValue();
         else
         {
            modifieres = getModifires((List<Modifier>)mod.isObjectProperty().objectValue());
         }
      }
      else
         modifieres = 0;
   }

   protected String getModifiers()
   {

      String span =
         "<span style = \"position: absolute; margin-top: -5px; margin-left: -25px; width: 22px; "
            + "height: 10px; font-family:  font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 10px; \">";
      span += (ModifierHelper.isAbstract(modifieres)) ? "<font color ='#004e00' style='float: right;'>A</font>" : "";
      //      span += (ModifierHelper.isFinal(modifieres)) ? "<font color ='#174c83' style='float: right;'>F</font>" : "";
      span += (ModifierHelper.isStatic(modifieres)) ? "<font color ='#6d0000' style='float: right;'>S</font>" : "";
      span += "</span>";
      return span;
   }

   /**
    * @param modifiers
    * @return
    */
   private int getModifires(List<Modifier> modifiers)
   {
      int i = 0;
      for (Modifier m : modifiers)
      {
         i = i | m.value();
      }
      return i;
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenName()
    */
   @Override
   public String getTokenName()
   {
      return getToken().getName();
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setSelectedStyle()
    */
   @Override
   public void setSelectedStyle()
   {
      setStyleName(CodeAssistantClientBundle.INSTANCE.css().selectedItem());
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setOveredStyle()
    */
   @Override
   public void setOveredStyle()
   {
      setStyleName(CodeAssistantClientBundle.INSTANCE.css().overedItem());
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setDefaultStyle()
    */
   @Override
   public void setDefaultStyle()
   {
      setStyleName(CodeAssistantClientBundle.INSTANCE.css().item());
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenDecription()
    */
   @Override
   public Widget getTokenDecription()
   {
      return new Frame(docContext + token.getProperty(TokenProperties.DECLARING_CLASS).isStringProperty().stringValue()
         + "." + getTokenValue());
   }

}
