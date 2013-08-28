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
package org.eclipse.jdt.client.core.formatter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.JdtExtension;
import org.eclipse.jdt.client.event.ShowFormatterProfilesEvent;
import org.eclipse.jdt.client.event.ShowFormatterProfilesHandler;
import org.eclipse.jdt.client.internal.corext.util.CodeFormatterUtil;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.preference.PreferencePerformer;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.MalformedTreeException;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 12:33:26 PM Apr 3, 2012 evgen $
 */
public class FormatterProfilePresenter implements ShowFormatterProfilesHandler, ViewClosedHandler,
                                                  ApplicationSettingsReceivedHandler, PreferencePerformer {
    public final static String ECLIPSE_PROFILE = "org.eclipse.jdt.ui.default.eclipse_profile";

    public final static String EXO_PROFILE = "org.exoplatform.ide.default_profile";

    private final HandlerManager eventBus;

    public interface Display extends IsView {
        String ID = "eXoIdeJavaCodeFormatterProfileView";

        HasClickHandlers getOkButton();

        SelectItem getProfilesSelect();

        IDocument getDocument();

    }

    private Map<String, Profile> profiles = new HashMap<String, Profile>();

    private Display display;

    private ApplicationSettings settings;

    /**
     *
     */
    public FormatterProfilePresenter(HandlerManager eventBus) {
        this.eventBus = eventBus;
        eventBus.addHandler(ShowFormatterProfilesEvent.TYPE, this);
        eventBus.addHandler(ViewClosedEvent.TYPE, this);
        eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
        initProfiles();
    }

    /**
     *
     */
    private void bind() {
        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Profile profile = getProfileByName(display.getProfilesSelect().getValue());
                settings.setValue(JdtExtension.JAVA_CODE_FORMATTER, profile.getId(), Store.SERVER);
                eventBus.fireEvent(new SaveApplicationSettingsEvent(settings, SaveType.SERVER));
                JdtExtension.get().getOptions().putAll(profile.getSettings());
                IDE.getInstance().closeView(Display.ID);
            }
        });

        display.getProfilesSelect().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                String value = event.getValue();
                format(getProfileByName(value));
            }

        });

    }

    private Profile getProfileByName(String name) {
        for (Profile p : profiles.values()) {
            if (p.getName().equals(name)) {

                return p;
            }
        }
        return null;
    }

    private void format(Profile profile) {
        if (profile == null)
            return;
        TextEdit edit =
                CodeFormatterUtil.format2(org.eclipse.jdt.client.core.formatter.CodeFormatter.K_COMPILATION_UNIT, display
                        .getDocument().get(), 0, null, profile.getSettings());
        try {
            edit.apply(display.getDocument());
        } catch (MalformedTreeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadLocationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void initProfiles() {
        profiles.put(ECLIPSE_PROFILE, new Profile("Eclipse [built-in]", ECLIPSE_PROFILE, DefaultCodeFormatterOptions
                .getEclipseDefaultSettings().getMap()));
        try {
            JdtClientBundle.INSTANCE.eXoProfile().getText(new ResourceCallback<TextResource>() {

                @Override
                public void onSuccess(TextResource resource) {
                    Document exoSettings = XMLParser.parse(resource.getText());
                    NodeList nodeList = exoSettings.getElementsByTagName("setting");
                    HashMap<String, String> settings = new HashMap<String, String>();
                    for (int i = 0; i < nodeList.getLength(); i++) {
                        NamedNodeMap attributes = nodeList.item(i).getAttributes();
                        settings.put(attributes.getNamedItem("id").getNodeValue(), attributes.getNamedItem("value")
                                                                                             .getNodeValue());
                    }
                    profiles.put(EXO_PROFILE, new Profile("eXo [built-in]", EXO_PROFILE, settings));

                }

                @Override
                public void onError(ResourceException e) {
                    e.printStackTrace();
                }
            });
        } catch (ResourceException e) {
            e.printStackTrace();
        }
    }

    public Profile getProfile(String id) {
        return profiles.get(id);
    }

    public Profile getDefault() {
        return profiles.get(EXO_PROFILE);
    }

    /** @see org.eclipse.jdt.client.event.ShowFormatterProfilesHandler#onShowFormatterProfiles(org.eclipse.jdt.client.event
     * .ShowFormatterProfilesEvent) */
    @Override
    public void onShowFormatterProfiles(ShowFormatterProfilesEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bind();
        }
        IDE.getInstance().openView(display.asView());

        displayProfiles();
    }

    private void displayProfiles() {
        String profileNames[] = new String[profiles.size()];
        Collection<Profile> values = profiles.values();
        int i = 0;
        for (Iterator<Profile> iterator = values.iterator(); iterator.hasNext(); ) {
            Profile profile = iterator.next();
            profileNames[i] = profile.getName();
            i++;
        }
        Profile profile = null;
        if (settings.containsKey(JdtExtension.JAVA_CODE_FORMATTER)) {
            profile = getProfile(settings.getValueAsString(JdtExtension.JAVA_CODE_FORMATTER));
        } else
            profile = getDefault();
        display.getProfilesSelect().setValueMap(profileNames, profile.getName());
        format(profile);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display)
            display = null;
    }

    /** @see org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org
     * .exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent) */
    @Override
    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        settings = event.getApplicationSettings();
    }

    /** @see org.exoplatform.ide.client.framework.preference.PreferencePerformer#getPreference() */
    @Override
    public View getPreference() {
        if (display == null) {
            display = GWT.create(Display.class);
            bind();
        }
        displayProfiles();
        return display.asView();
    }
}
