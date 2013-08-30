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
package org.exoplatform.ide.maven;

import com.codenvy.ide.commons.server.PomUtils;
import com.codenvy.ide.commons.server.PomUtils.Pom;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import static com.codenvy.commons.lang.IoUtil.createTempDirectory;
import static com.codenvy.commons.lang.IoUtil.deleteRecursive;
import static com.codenvy.commons.lang.ZipUtils.unzip;
import static com.codenvy.commons.lang.ZipUtils.zipDir;



/**
 * Build manager.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BuildService.java 16504 2011-02-16 09:27:51Z andrew00x $
 */
public class BuildService {
    /**
     * Name of configuration parameter that points to the directory where all builds stored.
     * Is such parameter is not specified then 'java.io.tmpdir' used.
     */
    public static final String BUILDER_REPOSITORY = "builder.repository";

    /**
     * Name of configuration parameter that points to the directory where stored build after deploy command.
     * Is such parameter is not specified then 'java.io.tmpdir' used.
     */
    public static final String BUILDER_PUBLISH_REPOSITORY = "builder.publish-repository";

    /**
     * Name of configuration parameter that points URL for public maven repository.
     * This is mirror of the directory where stored build after deploy command.
     * If not set will be null.
     */
    public static final String BUILDER_PUBLISH_REPOSITORY_URL = "builder.publish-repository-url";

    /**
     * Name of configuration parameter that provides build timeout is seconds. After this time build may be terminated.
     *
     * @see #DEFAULT_BUILDER_TIMEOUT
     */
    public static final String BUILDER_TIMEOUT = "builder.timeout";

    /**
     * Name of configuration parameter that sets the number of build workers. In other words it set the number of build
     * process that can be run at the same time. If this parameter is not set then the number of available processors
     * used, e.g. <code>Runtime.getRuntime().availableProcessors();</code>
     */
    public static final String BUILDER_WORKERS_NUMBER = "builder.workers.number";

    /**
     * Name of configuration parameter that sets time of keeping the results (artifact and logs) of build. After this
     * time the results of build may be removed.
     *
     * @see #DEFAULT_BUILDER_CLEAN_RESULT_DELAY_TIME
     */
    public static final String BUILDER_CLEAN_RESULT_DELAY_TIME = "builder.clean.result.delay.time";

    /**
     * Name of parameter that set the max size of build queue. The number of build task in queue may not be greater than
     * provided by this parameter.
     *
     * @see #DEFAULT_BUILDER_QUEUE_SIZE
     */
    public static final String BUILDER_QUEUE_SIZE = "builder.queue.size";

    /** Default build timeout in seconds (120). After this time build may be terminated. */
    public static final int DEFAULT_BUILDER_TIMEOUT = 120;

    /** Default max size of build queue (100). */
    public static final int DEFAULT_BUILDER_QUEUE_SIZE = 100;

    /**
     * Default time of keeping the results of build in minutes (60).
     * After this time the results of build (artifact and logs) may be removed.
     */
    public static final int DEFAULT_BUILDER_CLEAN_RESULT_DELAY_TIME = 60;

    /** Maven build goals 'test package'. */
    private static final String[] BUILD_GOALS = new String[]{"test", "package"};

    /** Maven build goals 'test deploy'. */
    private static final String[] DEPLOY_GOALS = new String[]{"source:jar", "deploy"};

    //   /** Maven deploy profile '-Preleasesss'. */
    //   private static final String[] DEPLOY_PROFILES = new String[]{"releasesss"};

    /** Maven list dependencies goals 'dependency:list'. */
    private static final String[] DEPENDENCIES_LIST_GOALS = new String[]{"dependency:list"};

    /** Maven copy dependencies goals 'dependency:copy-dependencies'. */
    private static final String[] DEPENDENCIES_COPY_GOALS = new String[]{"dependency:copy-dependencies"};

    /** Build task ID generator. */
    private static final AtomicLong idGenerator = new AtomicLong(1);

    private static String nextTaskID() {
        return Long.toString(idGenerator.getAndIncrement());
    }

    //
    private final ExecutorService pool;

    private final ConcurrentMap<String, CacheElement> map;

    private final Queue<CacheElement> queue;

    private final ScheduledExecutorService cleaner;

    private final Queue<File> cleanerQueue;

    private final File repository;

    private String publishRepository;

    private String publishRepositoryUrl;

    private final long timeoutMillis;

    private final long cleanBuildResultDelayMillis;

    private final int maxSizeOfBuildQueue;

    private final int workerNumber;

    public BuildService(Map<String, Object> config) {
        this(getOption(config, BUILDER_REPOSITORY, String.class, System.getProperty("java.io.tmpdir")), //
             getOption(config, BUILDER_PUBLISH_REPOSITORY, String.class, System.getProperty("java.io.tmpdir")), //
             getOption(config, BUILDER_PUBLISH_REPOSITORY_URL, String.class, null), //
             getOption(config, BUILDER_TIMEOUT, Integer.class, DEFAULT_BUILDER_TIMEOUT),//
             getOption(config, BUILDER_WORKERS_NUMBER, Integer.class, Runtime.getRuntime().availableProcessors()),//
             getOption(config, BUILDER_QUEUE_SIZE, Integer.class, DEFAULT_BUILDER_QUEUE_SIZE),//
             getOption(config, BUILDER_CLEAN_RESULT_DELAY_TIME, Integer.class, DEFAULT_BUILDER_CLEAN_RESULT_DELAY_TIME));
    }

    /**
     * @param repository
     *         the repository for build
     *         //    * @param goals the maven build goals
     * @param timeout
     *         the build timeout in seconds
     * @param workerNumber
     *         the number of build workers
     * @param buildQueueSize
     *         the max size of build queue. If this number reached then all new build request rejected
     * @param cleanBuildResultDelay
     *         the time of keeping the results of build in minutes. After this time result of build
     *         (both artifact and logs) may be removed.
     */
    protected BuildService(String repository, String publishRepository, String publishRepositoryUrl, int timeout,
                           int workerNumber, int buildQueueSize, int cleanBuildResultDelay) {

        if (repository == null || repository.isEmpty()) {
            throw new IllegalArgumentException("Build repository may not be null or empty string. ");
        }
        if (publishRepository == null || publishRepository.isEmpty()) {
            throw new IllegalArgumentException("Publish repository may not be null or empty string. ");
        }
        if (publishRepositoryUrl == null || publishRepositoryUrl.isEmpty()) {
            throw new IllegalArgumentException("Publish repository URL may not be null or empty string.");
        }
        if (workerNumber <= 0) {
            throw new IllegalArgumentException("Number of build workers may not be equals or less than 0. ");
        }
        if (buildQueueSize <= 0) {
            throw new IllegalArgumentException("Size of build queue may not be equals or less than 0. ");
        }
        if (cleanBuildResultDelay <= 0) {
            throw new IllegalArgumentException("Delay time of cleaning build results may not be equals or less than 0. ");
        }

        this.repository = new File(repository);
        this.publishRepository = publishRepository;
        this.publishRepositoryUrl = publishRepositoryUrl;
        this.timeoutMillis = timeout * 1000; // to milliseconds
        this.cleanBuildResultDelayMillis = cleanBuildResultDelay * 60 * 1000; // to milliseconds
        this.maxSizeOfBuildQueue = buildQueueSize;
        this.workerNumber = workerNumber;

        //
        this.map = new ConcurrentHashMap<String, CacheElement>();
        this.queue = new ConcurrentLinkedQueue<CacheElement>();

        //
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleanerQueue = new ConcurrentLinkedQueue<File>();
        cleaner.scheduleAtFixedRate(new CleanTask(), cleanBuildResultDelay, cleanBuildResultDelay, TimeUnit.MINUTES);

        //
        this.pool =
                new ThreadPoolExecutor(workerNumber, workerNumber, 0L, TimeUnit.MILLISECONDS,
                                       new LinkedBlockingQueue<Runnable>(buildQueueSize), new ManyBuildTasksPolicy(
                        new ThreadPoolExecutor.AbortPolicy()));
    }

    private static <O> O getOption(Map<String, Object> config, String option, Class<O> type, O defaultValue) {
        if (config != null) {
            Object value = config.get(option);
            return value != null ? type.cast(value) : defaultValue;
        }
        return defaultValue;
    }

    /**
     * Start new build.
     *
     * @param data
     *         the zipped maven project for build
     * @return build task
     * @throws java.io.IOException
     *         if i/o error occur when try to unzip project
     */
    public MavenBuildTask build(InputStream data) throws IOException {
        return addTask(makeProject(data), BUILD_GOALS, null, null, Collections.<Runnable>emptyList(),
                       Collections.<Runnable>emptyList(), WAR_FILE_GETTER);
    }

    /**
     * Start new build.
     *
     * @param data
     *         the zipped maven project for build
     * @return build task
     * @throws java.io.IOException
     *         if i/o error occur when try to unzip project
     */
    public MavenBuildTask deploy(InputStream data) throws IOException {
        Properties properties = new Properties();
        properties.put("altDeploymentRepository", "id::default::file:" + publishRepository);
        return addTask(makeProject(data), DEPLOY_GOALS, properties, null, Collections.<Runnable>emptyList(),
                       Collections.<Runnable>emptyList(), new PublicArtifactGetter(publishRepositoryUrl));
    }

    /**
     * Get list of dependencies of project in JSON format. It the same as run command:
     * <pre>
     *    mvn dependency:list
     * </pre>
     *
     * @param data
     *         the zipped maven project
     * @return build task
     * @throws java.io.IOException
     *         if i/o error occur when try to unzip project
     */
    public MavenBuildTask dependenciesList(InputStream data) throws IOException {
        File projectDirectory = makeProject(data);
        Properties properties = new Properties();
        // Save result in file.
        properties.put("outputFile", projectDirectory.getAbsolutePath() + "/dependencies.txt");
        return addTask(projectDirectory, DEPENDENCIES_LIST_GOALS, properties, null, Collections.<Runnable>emptyList(),
                       Collections.<Runnable>emptyList(), DEPENDENCIES_LIST_GETTER);
    }

    /**
     * Get copy of all dependencies of project in zip. It the same as run command:
     * <pre>
     *    mvn dependency:copy-dependencies
     * </pre>
     *
     * @param data
     *         the zipped maven project
     * @param classifier
     *         classifier to look for, e.g. : sources. May be <code>null</code>.
     * @return build task
     * @throws java.io.IOException
     *         if i/o error occur when try to unzip project
     */
    public MavenBuildTask dependenciesCopy(InputStream data, String classifier) throws IOException {
        Properties properties = null;
        if (!(classifier == null || classifier.isEmpty())) {
            properties = new Properties();
            properties.put("classifier", classifier);
            properties.put("mdep.failOnMissingClassifierArtifact", "false");
        }
        return addTask(makeProject(data), DEPENDENCIES_COPY_GOALS, properties, null, Collections.<Runnable>emptyList(),
                       Collections.<Runnable>emptyList(), COPY_DEPENDENCIES_GETTER);
    }

    private File makeProject(InputStream data) throws IOException {
        File projectDirectory = createTempDirectory(repository, "build-");
        unzip(data, projectDirectory);
        return projectDirectory;
    }

    private MavenBuildTask addTask(File projectDirectory, String[] goals, Properties properties, String[] profiles,
                                   List<Runnable> preBuildTasks, List<Runnable> postBuildTasks, ResultGetter resultGetter)
            throws IOException {
        final MavenInvoker invoker = new MavenInvoker(resultGetter).setTimeout(timeoutMillis);

        for (Runnable r : preBuildTasks) {
            invoker.addPreBuildTask(r);
        }

        for (Runnable r : postBuildTasks) {
            invoker.addPostBuildTask(r);
        }

        List<String> theGoals = new ArrayList<String>(goals.length);
        Collections.addAll(theGoals, goals);

        List<String> theProfiles = null;
        if (profiles != null) {
            theProfiles = new ArrayList<String>(profiles.length);
            Collections.addAll(theProfiles, profiles);
        }

        File logFile = new File(projectDirectory.getParentFile(), projectDirectory.getName() + ".log");
        TaskLogger taskLogger = new TaskLogger(logFile/*, new SystemOutHandler()*/);

        final InvocationRequest request =
                new DefaultInvocationRequest().setBaseDirectory(projectDirectory).setGoals(theGoals)
                                              .setOutputHandler(taskLogger).setErrorHandler(taskLogger).setProperties(properties)
                                              .setProfiles(theProfiles);

        final Callable<InvocationResultImpl> callable = new Callable<InvocationResultImpl>() {
            @Override
            public InvocationResultImpl call() throws MavenInvocationException {
                return invoker.execute(request);
            }
        };

        FutureTask<InvocationResultImpl> f = new FutureTask<InvocationResultImpl>(callable);

        final String id = nextTaskID();
        MavenBuildTask task = new MavenBuildTask(id, f, projectDirectory, taskLogger);
        addInQueue(id, task, System.currentTimeMillis() + cleanBuildResultDelayMillis);

        pool.execute(f);

        return task;
    }

    private void addInQueue(String id, MavenBuildTask task, long expirationTime) {
        CacheElement newElement = new CacheElement(id, task, expirationTime);
        CacheElement prevElement = map.put(id, newElement);
        if (prevElement != null) {
            queue.remove(prevElement);
        }

        queue.add(newElement);

        CacheElement current;
        while ((current = queue.peek()) != null && current.isExpired()) {
            // Task must be already stopped. MavenInvoker controls build process and terminated it if build time exceeds
            // the limit (DEFAULT_BUILDER_TIMEOUT).
            queue.remove(current);
            map.remove(current.id);
            cleanerQueue.offer(current.task.getProjectDirectory());
            cleanerQueue.offer(current.task.getLogger().getFile());
        }
    }

    /**
     * Get the build task by ID.
     *
     * @param id
     *         the build ID
     * @return build task or <code>null</code> if there is no build with specified ID
     */
    public MavenBuildTask get(String id) {
        CacheElement e = map.get(id);
        return e != null ? e.task : null;
    }

    /**
     * Cancel build.
     *
     * @param id
     *         the ID of build to cancel
     * @return canceled build task or <code>null</code> if there is no build with specified ID
     */
    public MavenBuildTask cancel(String id) {
        MavenBuildTask task = get(id);
        if (task != null) {
            task.cancel();
        }
        return task;
    }

    /** Shutdown current BuildService. */
    public void shutdown() {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        } finally {
            cleaner.shutdownNow();

            // Remove all build results.
            // Not need to keep any artifacts of logs since they are inaccessible after stopping BuildService.
            for (File f : repository.listFiles(BUILD_FILES_FILTER)) {
                deleteRecursive(f);
            }
        }
    }

    public int getSize() {
        return queue.size();
    }

    public long getCleanBuildResultDelayMillis() {
        return cleanBuildResultDelayMillis;
    }

    public String getPublishRepository() {
        return publishRepository;
    }

    public String getPublishRepositoryUrl() {
        return publishRepositoryUrl;
    }

    public String getRepository() {
        return repository != null ? repository.getPath() : "";
    }

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    public int getMaxSizeOfBuildQueue() {
        return maxSizeOfBuildQueue;
    }

    public int getWorkerNumber() {
        return workerNumber;
    }
   

   /* ====================================================== */

    private static final FilenameFilter BUILD_FILES_FILTER = new BuildFilesFilter();

    private static class BuildFilesFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.startsWith("build-");
        }
    }

    private static final ResultGetter WAR_FILE_GETTER = new WarFileGetter();

    private static class WarFileGetter implements ResultGetter {
        @Override
        public Result getResult(File projectDirectory) throws FileNotFoundException {
            File target = new File(projectDirectory, "target");
            File[] filtered = target.listFiles(new FilenameFilter() {
                public boolean accept(File parent, String name) {
                    return name.endsWith(".war");
                }
            });
            if (filtered != null && filtered.length > 0) {
                return new Result(filtered[0], "application/zip", filtered[0].getName(), filtered[0].lastModified());
            }
            return null;
        }
    }

    private static class PublicArtifactGetter implements ResultGetter {
        private final String publishRepositoryUrl;

        private PublicArtifactGetter(String publishRepositoryUrl) {
            this.publishRepositoryUrl = publishRepositoryUrl;
        }

        @Override
        public Result getResult(File projectDirectory) throws IOException {
            Pom pom;
            try {
                pom = PomUtils.parse(new FileInputStream(projectDirectory + "/pom.xml"));
            } catch (Exception e) {
                //must never been happened
                throw new RuntimeException(e.getMessage(), e);
            }
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            OutputStreamWriter w = new OutputStreamWriter(bout);
            w.write('{');
            w.write("\"suggestDependency\":\"");
            w.write(pom.getSuggestDependency());
            w.write("\",\"artifactDownloadUrl\":");
            w.write('"');
            w.write(artifactUriBuilder(publishRepositoryUrl, pom));
            w.write('"');
            w.write('}');
            w.flush();
            w.close();
            return new Result(new ByteArrayInputStream(bout.toByteArray()), "application/json", "dependencies.json", 0);
        }

        private String artifactUriBuilder(String repositoryUrl, Pom pom) {
            StringBuilder builder = new StringBuilder(repositoryUrl.endsWith("/") ? repositoryUrl : repositoryUrl + "/");
            builder.append(pom.getGroupId().replace('.', '/')).append('/').append(pom.getArtifactId()).append('/')
                   .append(pom.getVersion());
            return builder.toString();
        }
    }

   /* ====================================================== */

    private static final Pattern DEPENDENCY_LINE_SPLITTER = Pattern.compile(":");

    private static final ResultGetter DEPENDENCIES_LIST_GETTER = new DependenciesListGetter();

    private static class DependenciesListGetter implements ResultGetter {
        @Override
        public Result getResult(File projectDirectory) throws IOException {
            File[] filtered = projectDirectory.listFiles(new FilenameFilter() {
                public boolean accept(File parent, String name) {
                    return "dependencies.txt".equals(name);
                }
            });
            // Re-format in JSON.
            if (filtered != null && filtered.length > 0) {
                FileReader r = null;
                BufferedReader br = null;
                try {
                    r = new FileReader(filtered[0]);
                    br = new BufferedReader(r);
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    OutputStreamWriter w = new OutputStreamWriter(bout);
                    w.write('[');
                    String line;
                    int i = 0;
                    while ((line = br.readLine()) != null) {
                        // Line has format '   asm:asm:jar:sources?:3.0:compile'
                        String[] segments = DEPENDENCY_LINE_SPLITTER.split(line.trim());
                        if (segments.length >= 5) {
                            String groupID = segments[0];
                            String artifactID = segments[1];
                            String type = segments[2];
                            String classifier;
                            String version;
                            if (segments.length == 5) {
                                version = segments[3];
                                classifier = null;
                            } else {
                                version = segments[4];
                                classifier = segments[3];
                            }

                            if (i > 0) {
                                w.write(',');
                            }

                            w.write('{');

                            w.write("\"groupID\":\"");
                            w.write(groupID);
                            w.write('\"');
                            w.write(',');

                            w.write("\"artifactID\":\"");
                            w.write(artifactID);
                            w.write('\"');
                            w.write(',');

                            w.write("\"type\":\"");
                            w.write(type);
                            w.write('\"');
                            w.write(',');

                            if (classifier != null) {
                                w.write("\"classifier\":\"");
                                w.write(classifier);
                                w.write('\"');
                                w.write(',');
                            }

                            w.write("\"version\":\"");
                            w.write(version);
                            w.write('\"');

                            w.write('}');
                            i++;
                        }
                    }
                    w.write(']');
                    w.flush();
                    w.close();
                    return new Result(new ByteArrayInputStream(bout.toByteArray()), "application/json", "dependencies.json",
                                      0);
                } finally {
                    if (br != null) {
                        br.close();
                    }
                    if (r != null) {
                        r.close();
                    }
                }
            }
            return null;
        }
    }

   /* ====================================================== */

    private static final ResultGetter COPY_DEPENDENCIES_GETTER = new CopyDependenciesGetter();

    private static class CopyDependenciesGetter implements ResultGetter {
        @Override
        public Result getResult(File projectDirectory) throws IOException {
            File target = new File(projectDirectory, "target");
            File dependencies = new File(target, "dependency");
            if (dependencies.exists() && dependencies.isDirectory()) {
                File zip = new File(target, "dependencies.zip");
                zipDir(dependencies.getAbsolutePath(), dependencies, zip, null);
                return new Result(zip, "application/zip", zip.getName(), 0);
            }
            return null;
        }
    }

   /* ====================================================== */

    private class CleanTask implements Runnable {
        public void run() {
            //System.err.println("CLEAN " + new Date() + " " + cleanerQueue.size());
            Set<File> failToDelete = new LinkedHashSet<File>();
            File f;
            while ((f = cleanerQueue.poll()) != null) {
                if (!deleteRecursive(f)) {
                    failToDelete.add(f);
                }
            }
            if (!failToDelete.isEmpty()) {
                cleanerQueue.addAll(failToDelete);
            }
        }
    }

   /* ====================================================== */

    private static final class CacheElement {
        private final long expirationTime;

        private final int hash;

        final String id;

        final MavenBuildTask task;

        CacheElement(String id, MavenBuildTask task, long expirationTime) {
            this.id = id;
            this.task = task;
            this.expirationTime = expirationTime;
            this.hash = 7 * 31 + id.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CacheElement e = (CacheElement)o;
            return id.equals(e.id);
        }

        @Override
        public int hashCode() {
            return hash;
        }

        boolean isExpired() {
            return expirationTime < System.currentTimeMillis();
        }
    }
}
