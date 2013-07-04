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
package org.exoplatform.ide.extension.maven.client.control;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.extension.maven.client.BuilderClientBundle;
import org.exoplatform.ide.extension.maven.client.BuilderExtension;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;


/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: BuildAndPublishProjectControl.java Oct 18, 2012 vetal $
 */
@RolesAllowed("developer")
public class BuildAndPublishProjectControl extends BuildProjectControlAbstract

{
    public static final String ID = BuilderExtension.LOCALIZATION_CONSTANT.buildAndPublishProjectControlId();

    private static final String TITLE = BuilderExtension.LOCALIZATION_CONSTANT.buildAndPublishProjectControlTitle();

    private static final String PROMPT = BuilderExtension.LOCALIZATION_CONSTANT.buildAndPublishProjectControlPrompt();

    public BuildAndPublishProjectControl() {
        super(ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(BuilderClientBundle.INSTANCE.build(), BuilderClientBundle.INSTANCE.buildDisabled());
        setEvent(new BuildProjectEvent(true));
    }
}
