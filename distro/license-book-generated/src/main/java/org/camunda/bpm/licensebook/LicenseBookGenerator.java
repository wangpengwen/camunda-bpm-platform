package org.camunda.bpm.licensebook;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

public class LicenseBookGenerator {

	/*
	 * TODO:
	 * 
	 * - indentation
	 * - license header etc.
	 * - exception handling
	 */
	
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			throw new RuntimeException("Requires 1 argument: The directory with the input files");
		}
		
		String inputDirName = args[0];
		
		File inputDir = new File(inputDirName);
		Collection<File> inputFiles = FileUtils.listFiles(inputDir, new String[] { "txt" }, false);
		
		for (File camundaModuleDescriptor : inputFiles) {
			List<String> dependencyCoordinates = FileUtils.readLines(camundaModuleDescriptor);
			
			for (String dep : dependencyCoordinates) {
				System.out.println(dep);
			}
		}
		
		Map<MavenArtifact, List<CamundaModule>> artifactsByModule = new TreeMap<>();
	}
}
