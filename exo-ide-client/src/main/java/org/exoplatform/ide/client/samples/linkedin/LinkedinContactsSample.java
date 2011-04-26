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
package org.exoplatform.ide.client.samples.linkedin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * @author vetal
 *
 */
public interface LinkedinContactsSample extends ClientBundle
{
   LinkedinContactsSample INSTANCE = GWT.create(LinkedinContactsSample.class);
     
   @Source("logic/contacts.js")
   TextResource getLogicContactsJs();
   
   @Source("logic/offline.js")
   TextResource getLogicOfflineJs();
   
   @Source("skin/exomobile.css")
   TextResource getExomobileCss();
   
   @Source("mobile.html")
   TextResource getMobileSource();
   
   @Source("readme-linkedin-contacts.txt")
   TextResource getReadme();
   
   @Source("cache.manifest")
   TextResource getCachManifest();
   
   //get source from lib folder
   @Source("lib/jqtouch.min.css")
   TextResource getLibJqtouchMinCss();
   
   @Source("lib/jqtouch.min.js")
   TextResource getLibJqtouchMinJs();
   
   @Source("lib/jquery.1.4.2.min.js")
   TextResource getLibJqueryMinJs();
   
   @Source("lib/template.js")
   TextResource getLibTemplateJs();
   
   @Source("lib/linkedin.js")
   TextResource getLibLinkedinJs();
   
}
