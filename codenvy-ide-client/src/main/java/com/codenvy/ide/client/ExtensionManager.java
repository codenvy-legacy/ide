package com.codenvy.ide.client;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.extension.demo.DemoExtension;
import com.codenvy.ide.extension.maven.client.BuilderExtension;
import com.codenvy.ide.extension.tasks.TasksExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.java.client.JavaExtension;
import com.codenvy.ide.extension.css.CssExtension;
/**
 * THIS CLASS WILL BE OVERRIDEN BY MAVEN BUILD. DON'T EDIT CLASS, IT WILL HAVE NO EFFECT.
 */
public class ExtensionManager extends AbstractExtensionManager
{
   @Inject
   public ExtensionManager(ExtensionRegistry extensionRegistry,
      Provider<com.codenvy.ide.extension.demo.DemoExtension> demoextension,
      Provider<com.codenvy.ide.extension.maven.client.BuilderExtension> builderextension,
      Provider<com.codenvy.ide.extension.tasks.TasksExtension> tasksextension,
      Provider<com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension> cloudfoundryextension,
      Provider<com.codenvy.ide.java.client.JavaExtension> javaextension,
      Provider<com.codenvy.ide.extension.css.CssExtension> cssextension
   )
   {
      super(extensionRegistry);
      this.extensions.add(demoextension);
      this.extensions.add(builderextension);
      this.extensions.add(tasksextension);
      this.extensions.add(cloudfoundryextension);
      this.extensions.add(javaextension);
      this.extensions.add(cssextension);
   }
}
