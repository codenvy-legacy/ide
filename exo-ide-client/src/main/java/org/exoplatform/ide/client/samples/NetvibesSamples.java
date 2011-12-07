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
package org.exoplatform.ide.client.samples;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface NetvibesSamples extends ClientBundle
{
   
   NetvibesSamples INSTANCE = GWT.create(NetvibesSamples.class);
   
   @Source("netvibes/SampleBlogPostWidget.html")
   TextResource getSampleBlogPostWidgetSource();
   
   @Source("netvibes/SampleChartWidget.html")
   TextResource getSampleChartWidgetSource();
   
   @Source("netvibes/SampleFlashWidget.html")
   TextResource getSampleFlashWidgetSource();
   
   @Source("netvibes/SampleTabbedWidget.html")
   TextResource getSampleTabbedWidgetSource();

}
