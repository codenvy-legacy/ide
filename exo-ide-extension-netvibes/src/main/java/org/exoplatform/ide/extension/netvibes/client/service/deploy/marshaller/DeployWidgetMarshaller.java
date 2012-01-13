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
package org.exoplatform.ide.extension.netvibes.client.service.deploy.marshaller;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

import org.exoplatform.gwtframework.commons.rest.Marshallable;
import org.exoplatform.ide.extension.netvibes.client.model.DeployWidget;
import org.exoplatform.ide.extension.netvibes.client.model.Languages;
import org.exoplatform.ide.extension.netvibes.client.model.Regions;

/**
 * Creates XML request from {@link DeployWidget} bean.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 1, 2010 $
 * 
 */
public class DeployWidgetMarshaller implements Marshallable
{

   /**
    * Data to marshall.
    */
   private DeployWidget widget;

   /**
    * Entry tag.
    */
   private final String ENTRY = "entry";

   /**
    * Title tag.
    */
   private final String TITLE = "title";

   /**
    * Type attribute.
    */
   private final String TYPE = "type";

   /**
    * Summary tag.
    */
   private final String SUMMARY = "summary";

   /**
    * Content tag.
    */
   private final String CONTENT = "content";

   /**
    * Widget prefix.
    */
   private final String WIDGET = "widget";

   /**
    * Atom link tag.
    */
   private final String LINK = "link";

   /**
    * Category tag.
    */
   private final String CATEGORY = "category";

   /**
    * Schema attribute.
    */
   private final String SCHEME = "scheme";

   /**
    * Term attribute.
    */
   private final String TERM = "term";

   /**
    * Label attribute.
    */
   private final String LABEL = "label";

   /**
    * Version tag.
    */
   private final String VERSION = "version";

   /**
    * @param widget data for deploying
    */
   public DeployWidgetMarshaller(DeployWidget widget)
   {
      this.widget = widget;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal()
    */
   @Override
   public String marshal()
   {
      return build();
   }

   /**
    * Build XML request.
    * 
    * @return {@link String} string with xml request
    */
   private String build()
   {
      Document doc = XMLParser.createDocument();
      Element entry = doc.createElement(ENTRY);
      entry.setAttribute("xmlns", "http://www.w3.org/2005/Atom");
      entry.setAttribute("xmlns:" + WIDGET, "http://www.netvibes.com/ns/");

      Element title = doc.createElement(TITLE);
      title.setAttribute(TYPE, "text");
      title.appendChild(doc.createCDATASection(widget.getTitle()));
      entry.appendChild(title);

      Element summary = doc.createElement(SUMMARY);
      summary.setAttribute(TYPE, "text");
      summary.appendChild(doc.createCDATASection(widget.getDescription()));
      entry.appendChild(summary);

      Element content = doc.createElement(CONTENT);
      content.setAttribute(TYPE, "text");
      content.appendChild(doc.createCDATASection(widget.getDescription()));
      entry.appendChild(content);

      Element sourceLink = doc.createElement(LINK);
      sourceLink.setAttribute("rel", "source");
      sourceLink.setAttribute("href", widget.getUrl());
      entry.appendChild(sourceLink);

      if (widget.getThumbnail() != null && widget.getThumbnail().length() > 0)
      {
         Element thumbnailLink = doc.createElement(LINK);
         thumbnailLink.setAttribute("rel", "thumbnail");
         thumbnailLink.setAttribute("href", widget.getThumbnail());
         entry.appendChild(thumbnailLink);
      }

      if (widget.getThumbnail() != null && widget.getThumbnail().length() > 0)
      {
         Element version = doc.createElement(WIDGET + ":" + VERSION);
         version.appendChild(doc.createTextNode(widget.getVersion()));
         entry.appendChild(version);
      }

      if (widget.getCategoryId() != null && widget.getCategoryName() != null)
      {
         Element category = doc.createElement(CATEGORY);
         category.setAttribute(SCHEME, "http://eco.netvibes.com/category");
         category.setAttribute(TERM, widget.getCategoryId());
         category.setAttribute(LABEL, widget.getCategoryName());
         entry.appendChild(category);
      }

      if (widget.getMainLanguage() != null)
      {
         Element language = doc.createElement(CATEGORY);
         language.setAttribute(SCHEME, "http://eco.netvibes.com/lang");
         language.setAttribute(TERM, widget.getMainLanguage());
         language.setAttribute(LABEL, Languages.getLanguagesMap().get(widget.getMainLanguage()));
         entry.appendChild(language);
      }

      if (widget.getRegion() != null)
      {
         Element region = doc.createElement(CATEGORY);
         region.setAttribute(SCHEME, "http://eco.netvibes.com/region");
         region.setAttribute(TERM, widget.getRegion());
         region.setAttribute(LABEL, Regions.getRegionsMap().get(widget.getRegion()));
         entry.appendChild(region);
      }

      if (widget.getKeywords() != null && widget.getKeywords().length() > 0)
      {
         appendKeywords(widget.getKeywords(), entry, doc);
      }

      doc.appendChild(entry);
      String request = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + doc.toString();

      // FF cuts the "xmlns" attribute.
      if (request.indexOf("http://www.w3.org/2005/Atom") == -1)
      {
         return request.replaceAll("<entry", "<entry xmlns=\"http://www.w3.org/2005/Atom\"");
      }
      return request;
   }

   /**
    * Appends keyword XML elements to pointed element.
    * 
    * @param keywordStr string with keywords space separated
    * @param element element to append keywords XML elements
    */
   private void appendKeywords(String keywordStr, Element element, Document doc)
   {
      String[] keywords = keywordStr.split(" ");
      for (String keyword : keywords)
      {
         Element tag = doc.createElement(CATEGORY);
         tag.setAttribute(SCHEME, "http://eco.netvibes.com/tag");
         tag.setAttribute(TERM, keyword);
         tag.setAttribute(LABEL, keyword);
         element.appendChild(tag);
      }
   }
}
