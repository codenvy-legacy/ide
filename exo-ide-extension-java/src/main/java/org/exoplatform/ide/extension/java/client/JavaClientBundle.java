/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.extension.java.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Java client resources (images).
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JavaClientBundle.java Jun 21, 2011 4:26:42 PM vereshchaka $
 */
public interface JavaClientBundle extends ClientBundle {
    JavaClientBundle INSTANCE = GWT.<JavaClientBundle>create(JavaClientBundle.class);

    @Source("org/exoplatform/ide/extension/java/images/java-project.png")
    ImageResource javaProject();

    @Source("org/exoplatform/ide/extension/java/images/java-project_Disabled.png")
    ImageResource javaProjectDisabled();

    @Source("org/exoplatform/ide/extension/java/images/spring.png")
    ImageResource springProject();

    @Source("org/exoplatform/ide/extension/java/images/spring_Disabled.png")
    ImageResource springProjectDisabled();

    @Source("org/exoplatform/ide/extension/java/images/ok.png")
    ImageResource okButton();

    @Source("org/exoplatform/ide/extension/java/images/ok_Disabled.png")
    ImageResource okButtonDisabled();

    @Source("org/exoplatform/ide/extension/java/images/cancel.png")
    ImageResource cancelButton();

    @Source("org/exoplatform/ide/extension/java/images/cancel_Disabled.png")
    ImageResource cancelButtonDisabled();

   /*
    * For Project Explorer
    */

    @Source("org/exoplatform/ide/extension/java/images/empty-java-package.png")
    ImageResource emptyJavaPackage();

    @Source("org/exoplatform/ide/extension/java/images/jar-library.png")
    ImageResource jarLibrary();

    @Source("org/exoplatform/ide/extension/java/images/java-class.png")
    ImageResource javaClass();

    @Source("org/exoplatform/ide/extension/java/images/java-file.png")
    ImageResource javaFile();

    @Source("org/exoplatform/ide/extension/java/images/java-package.png")
    ImageResource javaPackage();

    @Source("org/exoplatform/ide/extension/java/images/java-packages.png")
    ImageResource javaPackages();

    @Source("org/exoplatform/ide/extension/java/images/libraries.png")
    ImageResource libraries();

    @Source("org/exoplatform/ide/extension/java/images/datasource.png")
    ImageResource datasource();
    
    @Source("org/exoplatform/ide/extension/java/images/datasource-disabled.png")
    ImageResource datasourceDisabled();
    
}
