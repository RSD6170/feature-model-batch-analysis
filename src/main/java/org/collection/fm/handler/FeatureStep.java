package org.collection.fm.handler;

import java.nio.file.Path;
import java.util.List;

public record FeatureStep(String name, Path file, List<String> values, double usedRuntime, FeatureStatus featureStatus) {
}
