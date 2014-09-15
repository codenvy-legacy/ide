package com.codenvy.ide.extension.runner.client.run;

/**
 * @author Stéphane Daviet
 */
public enum RunnerStatus {
    IDLE,
    IN_QUEUE,
    IN_PROGRESS,
    RUNNING,
    DONE,
    FAILED,
    TIMEOUT
}
