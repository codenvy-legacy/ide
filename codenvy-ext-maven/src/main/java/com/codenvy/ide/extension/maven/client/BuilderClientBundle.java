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
package com.codenvy.ide.extension.maven.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 3, 2012 3:03:35 PM anya $
 */
public interface BuilderClientBundle extends ClientBundle {
    BuilderClientBundle INSTANCE = GWT.<BuilderClientBundle>create(BuilderClientBundle.class);

    @Source("com/codenvy/ide/extension/maven/images/controls/build.png")
    ImageResource build();

    @Source("com/codenvy/ide/extension/maven/images/controls/build_Disabled.png")
    ImageResource buildDisabled();

    @Source("com/codenvy/ide/extension/maven/images/controls/clearOutput.png")
    ImageResource clearOutput();

    @Source("com/codenvy/ide/extension/maven/images/controls/clearOutput_Disabled.png")
    ImageResource clearOutputDisabled();
}
