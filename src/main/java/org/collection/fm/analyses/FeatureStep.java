package org.collection.fm.analyses;

import org.collection.fm.util.BinaryRunner;

import java.nio.file.Path;
import java.util.List;

public record FeatureStep(Path file, List<String> values, double usedRuntime, FeatureStatus featureStatus) {
}
