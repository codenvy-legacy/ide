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
package org.eclipse.jdt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Apr 3, 2012 3:08:46 PM anya $
 *
 */
public interface JdtClientBundle extends ClientBundle
{
   JdtClientBundle INSTANCE = GWT.<JdtClientBundle> create(JdtClientBundle.class);

   @Source("org/eclipse/jdt/client/core/formatter/exo-jboss-codestyle.xml")
   ExternalTextResource eXoProfile();

   @Source("org/eclipse/jdt/client/core/formatter/formatter-sample.txt")
   TextResource formatterSample();

   @Source("org/eclipse/jdt/images/controls/clean.png")
   ImageResource clean();

   @Source("org/eclipse/jdt/images/controls/clean_Disabled.png")
   ImageResource cleanDisabled();

   @Source("org/eclipse/jdt/images/controls/format.png")
   ImageResource formatterProfiles();

   @Source("org/eclipse/jdt/images/controls/format_Disabled.png")
   ImageResource formatterProfilesDisabled();

   @Source("org/eclipse/jdt/client/internal/text/correction/proposals/correction_change.gif")
   ImageResource correction_change();

   @Source("org/eclipse/jdt/client/internal/text/correction/proposals/field_public_obj.gif")
   ImageResource field_public();

   @Source("org/eclipse/jdt/client/internal/text/correction/proposals/local.png")
   ImageResource local_var();

   @Source("org/eclipse/jdt/client/internal/text/correction/proposals/private-field.png")
   ImageResource privateField();

   @Source("org/eclipse/jdt/client/internal/text/correction/proposals/correction_cast.gif")
   ImageResource correction_cast();

   @Source("org/eclipse/jdt/client/internal/text/correction/proposals/public-method.png")
   ImageResource publicMethod();

   @Source("org/eclipse/jdt/client/internal/text/correction/proposals/packd_obj.gif")
   ImageResource packd_obj();

   @Source("org/eclipse/jdt/client/internal/text/correction/proposals/delete_obj.gif")
   ImageResource delete_obj();

}
