package org.exoplatform.ide.git.server.provider;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Provider that getting all enabled Git services and retrieve specific for us based on service name of git url(if some pattern matches).
 */
public class GitVendorServiceProvider {
    private final Map<String, GitVendorService> gitServices;

    Logger LOG = LoggerFactory.getLogger(GitVendorServiceProvider.class);

    public GitVendorServiceProvider(ExoContainerContext containerContext) {
        gitServices = new HashMap<>();
        ExoContainer container = containerContext.getContainer();
        List allGitServices = container.getComponentInstancesOfType(GitVendorService.class);
        if (!(allGitServices == null || allGitServices.isEmpty())) {
            for (Object o : allGitServices) {
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
