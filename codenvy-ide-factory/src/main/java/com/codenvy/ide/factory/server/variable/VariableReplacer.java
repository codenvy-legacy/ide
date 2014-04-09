package com.codenvy.ide.factory.server.variable;

import com.codenvy.api.factory.dto.Replacement;
import com.codenvy.api.factory.dto.Variable;
import com.codenvy.commons.lang.Deserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vladyslav Zhukovskii
 */
public class VariableReplacer {
    private final Path projectPath;

    private static final Logger LOG = LoggerFactory.getLogger(VariableReplacer.class);

    public VariableReplacer(Path projectPath) {
        this.projectPath = projectPath;
    }

    /** Perform searching in project path files given by variables list and make replacement variables in each file if it found. */
    public void performReplacement(List<Variable> variables) {
        if (variables.size() == 0) {
            return;
        }

        final Map<Path, ReplacementContainer> replacementMap = new HashMap<>();

        for (final Variable variable : variables) {
            for (final String glob : variable.getFiles()) {
                try {
                    Files.walkFileTree(projectPath, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**" + glob);

                            if (matcher.matches(file) && file.toFile().isFile()) {
                                ReplacementContainer container = replacementMap.get(file);
                                if (container == null) {
                                    container = new ReplacementContainer();
                                    replacementMap.put(file, container);
                                }

                                for (Replacement replacement : variable.getEntries()) {
                                    if (replacement.getReplacemode() == null
                                        || "variable_singlepass".equals(replacement.getReplacemode())) {
                                        container.getVariableProps().put(replacement.getFind(), replacement.getReplace());
                                    } else if ("text_multipass".equals(replacement.getReplacemode())) {
                                        container.getTextProps().put(replacement.getFind(), replacement.getReplace());
                                    }
                                }
                            }

                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            //need to continue work instead of exception
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }

        for (Map.Entry<Path, ReplacementContainer> entry : replacementMap.entrySet()) {
            try {
                if (entry.getValue().hasReplacements()) {
                    String content = new String(Files.readAllBytes(entry.getKey()));

                    String modified = Deserializer.resolveVariables(content, entry.getValue().getVariableProps(), false);
                    for (Map.Entry<String, String> replacement : entry.getValue().getTextProps().entrySet()) {
                        if (modified.contains(replacement.getKey())) {
                            modified = modified.replace(replacement.getKey(), replacement.getValue());
                        }
                    }

                    if (!content.equals(modified)) {
                        Files.write(entry.getKey(), modified.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                    }
                }
            } catch (IOException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /** Inner wrapper on Variables lists. */
    private class ReplacementContainer {
        private Map<String, String> variableProps = new HashMap<>();
        private Map<String, String> textProps     = new HashMap<>();

        private ReplacementContainer() {
        }

        public Map<String, String> getVariableProps() {
            return variableProps;
        }

        public Map<String, String> getTextProps() {
            return textProps;
        }

        public boolean hasReplacements() {
            return getVariableProps().size() > 0 || getTextProps().size() > 0;
        }
    }
}
