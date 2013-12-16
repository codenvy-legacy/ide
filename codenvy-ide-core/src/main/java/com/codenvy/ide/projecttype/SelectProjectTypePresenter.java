/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.projecttype;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:ashumilova@codenvy.com">Ann Shumilova</a>
 * @version $Id:
 */
@Singleton
public class SelectProjectTypePresenter implements SelectProjectTypeView.ActionDelegate {

    private SelectProjectTypeView      view;

    private StringMap<Array<Property>> typesMap;

    private CoreLocalizationConstant   localizationConstant;

    private Project                    project;
    
    private AsyncCallback<Project>     callback;

    @Inject
    public SelectProjectTypePresenter(SelectProjectTypeView view, CoreLocalizationConstant localizationConstant) {
        this.view = view;
        this.localizationConstant = localizationConstant;
        view.setDelegate(this);
        initTypes();
        view.setTypes(typesMap.getKeys());
    }

    private void initTypes() {
        typesMap = Collections.createStringMap();
        Array<String> emptyArray = Collections.createArray();

        // Codenvy Extension properties
        Array<Property> codenvyExtensionProperties = Collections.createArray();
        codenvyExtensionProperties.add(new Property("nature.mixin", Collections.createArray("CodenvyExtension")));
        codenvyExtensionProperties.add(new Property("exoide:projectDescription", Collections.createArray("Codenvy extension.")));
        codenvyExtensionProperties.add(new Property("runner.name", Collections.createArray("sdk")));
        codenvyExtensionProperties.add(new Property("vfs:projectType", Collections.createArray("CodenvyExtension")));
        codenvyExtensionProperties.add(new Property("nature.primary", Collections.createArray("java")));
        codenvyExtensionProperties.add(new Property("vfs:mimeType", Collections.createArray("text/vnd.ideproject+directory")));
        codenvyExtensionProperties.add(new Property("builder.maven.targets", Collections.createArray("clean", "install")));
        codenvyExtensionProperties.add(new Property("builder.name", Collections.createArray("maven")));
        codenvyExtensionProperties.add(new Property("folders.source", Collections.createArray("src/main/java", "src/main/resources")));
        typesMap.put("CodenvyExtension", codenvyExtensionProperties);

        // Jar project properties
        Array<Property> jarProperties = Collections.createArray();
        jarProperties.add(new Property("nature.primary", Collections.createArray("java")));
        jarProperties.add(new Property("vfs:projectType", Collections.createArray("Jar")));
        jarProperties.add(new Property("exoide:classpath", emptyArray));
        jarProperties.add(new Property("nature.mixin", Collections.createArray("Jar")));
        jarProperties.add(new Property("vfs:mimeType", Collections.createArray("text/vnd.ideproject+directory")));
        jarProperties.add(new Property("builder.name", Collections.createArray("maven")));
        jarProperties.add(new Property("folders.source", Collections.createArray("src/main/java", "src/test/java")));
        jarProperties.add(new Property("exoide:projectDescription", Collections.createArray("Simple JAR project.")));
        typesMap.put("Jar", jarProperties);

        // War project properties
        Array<Property> warProperties = Collections.createArray();
        warProperties.add(new Property("nature.primary", Collections.createArray("java")));
        warProperties.add(new Property("exoide:classpath", emptyArray));
        warProperties.add(new Property("nature.mixin", Collections.createArray("War")));
        warProperties.add(new Property("exoide:target", Collections.createArray("CloudBees", "CloudFoundry", "AWS", "AppFog", "Tier3WF")));
        warProperties.add(new Property("runner.name", Collections.createArray("webapps")));
        warProperties.add(new Property("exoide:projectDescription", Collections.createArray("Java Web project.")));
        warProperties.add(new Property("vfs:projectType", Collections.createArray("War")));
        warProperties.add(new Property("vfs:mimeType", Collections.createArray("text/vnd.ideproject+directory")));
        warProperties.add(new Property("builder.name", Collections.createArray("maven")));
        warProperties.add(new Property("folders.source", Collections.createArray("src/main/java", "src/main/resources")));
        typesMap.put("War", warProperties);
    }


    /** Show dialog. */
    public void showDialog(@NotNull Project project, @NotNull AsyncCallback<Project> callback) {
        this.project = project;
        this.callback = callback;
        view.setLabel(localizationConstant.selectProjectType(project.getName()));
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        String projectType = view.getProjectType();
        Array<Property> properties = typesMap.get(projectType);

        for (Property property : properties.asIterable()) {
            Property projectProperty = project.getProperty(property.getName());
            if (projectProperty != null) {
                projectProperty.setValue(property.getValue());
            } else {
                project.getProperties().add(property);
            }
        }

        project.flushProjectProperties(new AsyncCallback<Project>() {

            @Override
            public void onSuccess(Project result) {
                view.close();
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(SelectProjectTypePresenter.class, "Can not save project properties.", caught);
                callback.onFailure(caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}
