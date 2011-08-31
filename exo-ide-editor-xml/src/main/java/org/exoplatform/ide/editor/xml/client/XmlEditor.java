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
package org.exoplatform.ide.editor.xml.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.xml.client.codeassistant.XmlCodeAssistant;
import org.exoplatform.ide.editor.xml.client.codemirror.XmlOutlineItemCreator;
import org.exoplatform.ide.editor.xml.client.codemirror.XmlParser;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class XmlEditor extends Extension
{

   
   public final static XmlConstant CONSTANT = GWT.create(XmlConstant.class);
   
   public static final XmlClientBundle RESOURCES = GWT.create(XmlClientBundle.class);
   
   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      RESOURCES.css().ensureInjected();
      
      IDE.getInstance().addControl(new NewItemControl(
         "File/New/New XML",
         CONSTANT.controlNewXmlTitle(),
         CONSTANT.controlNewXmlPrompt(),
         Images.XML,
         MimeType.TEXT_XML).setGroup(1));      
      
      CodeAssistant xmlAssistant = new XmlCodeAssistant();
      IDE.getInstance().addEditor(new CodeMirrorProducer(MimeType.APPLICATION_XML, CONSTANT.xmlEditor(), "xml",
         RESOURCES.xml(), true, 
         new CodeMirrorConfiguration().
            setGenericParsers("['parsexml.js', 'tokenize.js']").
            setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']").
            setParser(new XmlParser()).
            setCanBeOutlined(true).
            setCodeAssistant(xmlAssistant)
      ));

      IDE.getInstance().addEditor(new CodeMirrorProducer(MimeType.TEXT_XML, CONSTANT.xmlEditor(), "xml",
         RESOURCES.xml(), true, 
         new CodeMirrorConfiguration().
            setGenericParsers("['parsexml.js', 'tokenize.js']").
            setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']").
            setParser(new XmlParser()).
            setCanBeOutlined(true).
            setCodeAssistant(xmlAssistant)
      ));
      
      IDE.getInstance().addOutlineItemCreator(MimeType.TEXT_XML, new XmlOutlineItemCreator());
   }

}
