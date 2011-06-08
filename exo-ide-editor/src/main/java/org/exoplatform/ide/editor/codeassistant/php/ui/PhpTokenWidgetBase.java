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
package org.exoplatform.ide.editor.codeassistant.php.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base part implementation of {@link TokenWidget}, uses frol all PHP token widgets.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public abstract class PhpTokenWidgetBase extends TokenWidget 
{

   protected Grid grid;
   
   protected List<Modifier> modifieres;
   
   /**
    * @param token
    */
   @SuppressWarnings("unchecked")
   public PhpTokenWidgetBase(Token token)
   {
      super(token);
      modifieres = new ArrayList<Modifier>();
      
      if(token.hasProperty(TokenProperties.MODIFIERS))
      {
         modifieres.addAll((Collection<Modifier>)token.getProperty(TokenProperties.MODIFIERS).isObjectProperty().objectValue());
      }
   }
   
   protected String getModifiers()
   {

      String span =
         "<span style = \"position: absolute; margin-top: -5px; margin-left: -25px; width: 22px; "
            + "height: 10px; font-family:  font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 10px; \">";
      span += (modifieres.contains(Modifier.ABSTRACT)) ? "<font color ='#004e00' style='float: right;'>A</font>" : "";
      //      span += (ModifierHelper.isFinal(modifieres)) ? "<font color ='#174c83' style='float: right;'>F</font>" : "";
      span += (modifieres.contains(Modifier.STATIC)) ? "<font color ='#6d0000' style='float: right;'>S</font>" : "";
      span += "</span>";
      return span;
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
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenValue()
    */
   @Override
   public String getTokenValue()
   {
      if (token.hasProperty(TokenProperties.CODE))
         return token.getProperty(TokenProperties.CODE).isStringProperty().stringValue();
      else
         return token.getName();
   }
   
   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenDecription()
    */
   @Override
   public Widget getTokenDecription()
   {
      return null;
   }

   
}
