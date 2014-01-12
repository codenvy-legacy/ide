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
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.resources.ProjectTypeData;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.newproject.ProjectTypeAgentImpl;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.Iterator;

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
    private ProjectTypeAgentImpl       projectTypeAgent;

    @Inject
    public SelectProjectTypePresenter(SelectProjectTypeView view,
                                      CoreLocalizationConstant localizationConstant,
                                      ProjectTypeAgentImpl projectTypeAgent) {
        this.view = view;
        this.localizationConstant = localizationConstant;
        this.projectTypeAgent = projectTypeAgent;
        typesMap = Collections.createStringMap();
        view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog(@NotNull Project project, @NotNull AsyncCallback<Project> callback) {
        this.project = project;
        this.callback = callback;
        view.setLabel(localizationConstant.selectProjectType(project.getName()));
        view.clearTypes();

        Array<ProjectTypeData> projectsTypeData = projectTypeAgent.getProjectTypes();
        Iterator<ProjectTypeData> projectTypeDataIterator = projectsTypeData.asIterable().iterator();
        while (projectTypeDataIterator.hasNext()) {
            ProjectTypeData projectTypeData = projectTypeDataIterator.next();
            typesMap.put(projectTypeData.getTypeName(), projectTypeData.getProperties());
        }

        view.setTypes(typesMap.getKeys());
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
