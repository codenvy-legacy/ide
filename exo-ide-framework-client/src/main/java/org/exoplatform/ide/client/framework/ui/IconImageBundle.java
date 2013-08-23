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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Image bundle for folder and default file icons.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImageBundle.java Sep 2, 2011 4:40:03 PM vereshchaka $
 */
public interface IconImageBundle extends ClientBundle {

    public static final IconImageBundle INSTANCE = GWT.create(IconImageBundle.class);

    @Source("org/exoplatform/ide/public/images/filetype/default.png")
    ImageResource defaultFile();

    @Source("org/exoplatform/ide/public/images/filetype/folder_closed.png")
    ImageResource folder();

    @Source("org/exoplatform/ide/client/bundled-images/projects/java-project.png")
    ImageResource javaProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/template_36.png")
    ImageResource template();

    @Source("org/exoplatform/ide/client/bundled-images/projects/jsp.png")
    ImageResource jspProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/jsp48x48.png")
    ImageResource jspProject48();

    @Source("org/exoplatform/ide/client/bundled-images/projects/gae_java.png")
    ImageResource gaeJavaProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/gae_python.png")
    ImageResource gaePythonProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/python.png")
    ImageResource pythonProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/python48x48.png")
    ImageResource pythonProject48();

    @Source("org/exoplatform/ide/client/bundled-images/projects/nodejs48x48.png")
    ImageResource nodejsProject48();

    @Source("org/exoplatform/ide/client/bundled-images/projects/nodejs.png")
    ImageResource nodejsProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/django.png")
    ImageResource djangoProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/ror.png")
    ImageResource rubyProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/ror48x48.png")
    ImageResource rubyProject48();

    @Source("org/exoplatform/ide/client/bundled-images/projects/spring.png")
    ImageResource springProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/spring48x48.png")
    ImageResource springProject48();

    @Source("org/exoplatform/ide/client/bundled-images/projects/groovy-project.png")
    ImageResource groovyProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/php.png")
    ImageResource phpProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/php48x48.png")
    ImageResource phpProject48();

    @Source("org/exoplatform/ide/client/bundled-images/projects/js.png")
    ImageResource jsProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/js48x48.png")
    ImageResource jsProject48();

    @Source("org/exoplatform/ide/client/bundled-images/projects/default-project.png")
    ImageResource defaultProject();

    @Source("org/exoplatform/ide/client/bundled-images/projects/jar48x48.png")
    ImageResource jarProject48();

    @Source("org/exoplatform/ide/client/bundled-images/projects/multi-module48x48.png")
    ImageResource multiModule48();

    /* Languages */
    @Source("org/exoplatform/ide/client/bundled-images/projects/java_type.png")
    ImageResource javaType();

    @Source("org/exoplatform/ide/client/bundled-images/projects/python_type.png")
    ImageResource pythonType();

    @Source("org/exoplatform/ide/client/bundled-images/projects/groovy_type.png")
    ImageResource groovyType();

    @Source("org/exoplatform/ide/client/bundled-images/projects/js_type.png")
    ImageResource jsType();

    @Source("org/exoplatform/ide/client/bundled-images/projects/ruby_type.png")
    ImageResource rubyType();

    @Source("org/exoplatform/ide/client/bundled-images/projects/php_type.png")
    ImageResource phpType();

    @Source("org/exoplatform/ide/client/bundled-images/projects/android.png")
    ImageResource androidType();

    @Source("org/exoplatform/ide/client/bundled-images/projects/android48x48.png")
    ImageResource androidProject48();
}
