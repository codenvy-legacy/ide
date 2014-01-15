package com.codenvy.ide.ext.git.server.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Provider that getting all enabled Git services and retrieve specific for us based on service name of git url(if some pattern matches).
 */
public class GitVendorServiceProvider {

    private final Map<String, GitVendorService> gitServices;

    Logger LOG = LoggerFactory.getLogger(GitVendorServiceProvider.class);

    @Inject
    public GitVendorServiceProvider(Set<GitVendorService> gitVendorServices) {
        gitServices = new HashMap<>();
        if (!gitVendorServices.isEmpty()) {
            for (Object o : gitVendorServices) {
                GitVendorService gitService = (GitVendorService)o;
                gitServices.put(gitService.getVendorName(), gitService);
            }
        }
    }

    public GitVendorService getGitService(String gitVendorName) {
        GitVendorService gitVendorService = gitServices.get(gitVendorName);
        if (gitVendorService == null) {
            LOG.warn("Provider for vendor name: " + gitVendorName + " not found.");
        }
        return gitVendorService;
    }

    public GitVendorService getGitServiceByUrlMatch(String vcsUrl) {
        for (Map.Entry<String, GitVendorService> entry : gitServices.entrySet()) {
            if (entry.getValue().getVendorUrlPattern().matcher(vcsUrl).matches()) {
                return entry.getValue();
            }
        }

        LOG.warn("Provider for vcs url: " + vcsUrl + " not found.");
        return null;
    }
}
