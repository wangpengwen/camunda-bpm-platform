package org.camunda.bpm.licensebook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
		if (args.length != 2) {
			throw new RuntimeException("Requires 1 argument: The directory with the input files");
		}
		
		String inputDirName = args[0];
		String licensesDir = args[1];
		
		File inputDir = new File(inputDirName);
		Collection<File> inputFiles = FileUtils.listFiles(inputDir, new String[] { "txt" }, false);
		
		Map<MavenArtifact, List<CamundaModule>> artifactsUsage = new TreeMap<>();

		for (File camundaModuleDescriptor : inputFiles) {
			List<String> dependencyCoordinates = FileUtils.readLines(camundaModuleDescriptor);
			
			String fileName = camundaModuleDescriptor.getName();
			
			CamundaModule camundaModule = CamundaModule.fromDependencyName(fileName);
			
			for (String dep : dependencyCoordinates) {
				if (dep.trim().isEmpty()) {
					continue;
				}
				
				MavenArtifact mavenArtifact;
				
				try {
					mavenArtifact = MavenArtifact.fromCanonicalString(dep);
					
				} catch (Exception e) {
					// TODO: tolerating file formatting problems for now; change this for the proper solution
					continue;
				}
				
				List<CamundaModule> usingModules = artifactsUsage.computeIfAbsent(mavenArtifact, k -> new ArrayList<>());
				usingModules.add(camundaModule);
			}
		}
		
		System.out.println(artifactsUsage);
	}
}
