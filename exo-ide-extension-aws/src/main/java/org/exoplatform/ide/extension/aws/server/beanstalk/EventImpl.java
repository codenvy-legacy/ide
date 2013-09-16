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
package org.exoplatform.ide.extension.aws.server.beanstalk;

import org.exoplatform.ide.extension.aws.shared.beanstalk.Event;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsSeverity;

import java.util.Date;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EventImpl implements Event {
    private long           eventDate;
    private String         message;
    private String         applicationName;
    private String         versionLabel;
    private String         templateName;
    private String         environmentName;
    private EventsSeverity severity;

    public static class Builder {
        private long           eventDate;
        private String         message;
        private String         applicationName;
        private String         versionLabel;
        private String         templateName;
        private String         environmentName;
        private EventsSeverity severity;

        public Builder eventDate(Date eventDate) {
            if (eventDate == null) {
                this.eventDate = -1;
                return this;
            }
            this.eventDate = eventDate.getTime();
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder applicationName(String applicationName) {
            this.applicationName = applicationName;
            return this;
        }

        public Builder versionLabel(String versionLabel) {
            this.versionLabel = versionLabel;
            return this;
        }

        public Builder templateName(String templateName) {
            this.templateName = templateName;
            return this;
        }

        public Builder environmentName(String environmentName) {
            this.environmentName = environmentName;
            return this;
        }

        public Builder severity(String severity) {
            this.severity = EventsSeverity.fromValue(severity);
            return this;
        }

        public Event build() {
            return new EventImpl(this);
        }
    }

    private EventImpl(Builder builder) {
        this.eventDate = builder.eventDate;
        this.message = builder.message;
        this.applicationName = builder.applicationName;
        this.versionLabel = builder.versionLabel;
        this.templateName = builder.templateName;
        this.environmentName = builder.environmentName;
        this.severity = builder.severity;
    }

    public EventImpl() {
    }

    @Override
    public long getEventDate() {
        return eventDate;
    }

    @Override
    public void setEventDate(long eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String getVersionLabel() {
        return versionLabel;
    }

    @Override
    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    @Override
    public String getTemplateName() {
        return templateName;
    }

    @Override
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    @Override
    public String getEnvironmentName() {
        return environmentName;
    }

    @Override
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }

    @Override
    public EventsSeverity getSeverity() {
        return severity;
    }

    @Override
    public void setSeverity(EventsSeverity severity) {
        this.severity = severity;
    }

    @Override
    public String toString() {
        return "EventImpl{" +
               "eventDate=" + eventDate +
               ", message='" + message + '\'' +
               ", applicationName='" + applicationName + '\'' +
               ", versionLabel='" + versionLabel + '\'' +
               ", templateName='" + templateName + '\'' +
               ", environmentName='" + environmentName + '\'' +
               ", severity=" + severity +
               '}';
    }
}
