/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.ide.extension.runner.client.wizard;

import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;
import com.google.inject.ImplementedBy;

import java.util.Map;
import java.util.Set;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(SelectRunnerPageViewImpl.class)
public interface SelectRunnerPageView extends View<SelectRunnerPageView.ActionDelegate> {

    public enum Target {
        CONSOLE("console"),
        WEBAPP("webapp"),
        MOBILE("mobile"),
        STANDALONE("standalone");

        private final String id;

        Target(String id) {
            this.id = id;
        }
        
        public String getId() {
            return id;
        }
        
        public static Target fromString(String v) {
            if (v != null) {
                for (Target t : Target.values()) {
                    if (v.equalsIgnoreCase(t.id)) {
                        return t;
                    }
                }
            }
            throw new IllegalArgumentException("Unknown version " + v + ".");
        }
    }

    void showTechnologies(Array<String> values);
    
    /**
     * Show sub technologies.
     * 
     * @param values sub technologies
     */
    void showSubTechnologies(Set<String> values);
    
    /**
     * Show the given environments.
     * 
     * @param environments
     */
    void showEnvironments(Map<String, RunnerEnvironment> environments);

    /**
     * Select the given technology
     *  
     * @param technology technology to select
     */
    void selectTechnology(String technology);
    
    /**
     * @return {@link String} the selected technology
     */
    String getSelectedTechnology();
    
    /**
     * @return {@link Target} the selected target
     */
    Target getSelectedTarget();
    
    /**
     * Display the given targets.
     * 
     * @param targets
     */
    void displayTargets(Array<Target> targets);
    
    /**
     * Hide the section with targets.
     */
    void hideTargets();
    
    /**
     * Hide the section with sub technologies.
     */
    void hideSubTechnologies();
    
    /**
     * Select the environment target.
     * 
     * @param target target to select
     */
    void selectTarget(Target target);
    
    /**
     * Set selected environment.
     * 
     * @param environmentName runner environment
     */
    void setSelectedEnvironment(String environmentName);

    /** Set recommended memory size for runner. */
    void setRecommendedMemorySize(String recommendedRam);

    /** Get recommended memory size for runner. */
    String getRecommendedMemorySize();
    
    /**
     * Changes the enabled/disabled state of the environments list box.
     * 
     * @param isEnabled
     */
    void setEnvironmentsEnableState(boolean isEnabled);

    public interface ActionDelegate {
        
        void technologySelected(String technology);
        
        void subTechnologySelected(String subTechnology);

        void runnerEnvironmentSelected(String environmentId);

        void recommendedMemoryChanged();

        void targetSelected(Target target);
    }
}
