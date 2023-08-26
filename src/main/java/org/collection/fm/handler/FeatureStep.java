package org.collection.fm.handler;

import java.nio.file.Path;
import java.util.List;

public record FeatureStep(Path file, List<String> values, double usedRuntime, FeatureStatus featureStatus) {
}
