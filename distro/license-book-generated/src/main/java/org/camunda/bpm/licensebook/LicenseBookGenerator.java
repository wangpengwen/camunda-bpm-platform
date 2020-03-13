package org.camunda.bpm.licensebook;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
		if (args.length != 3) {
			throw new RuntimeException("Requires 3 arguments: The directory with the input files, "
			    + "the root directory for the license info, "
			    + "the output file name");
		}
		
		String inputDirName = args[0];
		String licensesDirName = args[1];
		String outFileName = args[2];
		
		File inputDir = new File(inputDirName);
		Path licensesDir = new File(licensesDirName).toPath();
		File outFile = new File(outFileName);

		Collection<File> inputFiles = FileUtils.listFiles(inputDir, new String[] { "txt" }, false);
		
		Map<MavenArtifact, List<CamundaModule>> artifactsUsage = parseDependencies(inputFiles);

		System.out.println(artifactsUsage);

		FileUtils.forceMkdir(outFile.getParentFile());
		// TODO: close resources
		OutputStream outFileStream = Files.newOutputStream(outFile.toPath());
		BufferedOutputStream bufferedOutFileStream = new BufferedOutputStream(outFileStream);
		OutputStreamWriter streamWriter = new OutputStreamWriter(bufferedOutFileStream);

		
		List<MavenArtifact> artifactsWithoutLicensingInfos = new ArrayList<MavenArtifact>();
		
		for (Map.Entry<MavenArtifact, List<CamundaModule>> dependencyUsage : artifactsUsage.entrySet()) {
		  MavenArtifact dependency = dependencyUsage.getKey();
		  streamWriter.write(dependency.toString());
		  streamWriter.write("\n");
		  List<CamundaModule> usingModules = dependencyUsage.getValue();
		  String modulesList = usingModules.stream().map(Object::toString).collect(Collectors.joining(",\n\t"));
		  streamWriter.write("Used by Camunda modules:\n\t");
		  streamWriter.write(modulesList);
		  streamWriter.write("\n");
		  
		  String[] groupId = dependency.getGroupId().split("\\.");
		  // TODO: check file delimiter
		  String dependencySubPath = String.join("/", groupId) + "/" 
		      + dependency.getArtifactId() + "/"
		      + dependency.getVersion();
		  
		  Path dependencyDirPath = licensesDir.resolve(dependencySubPath);
		  
		  File dependencyDir = dependencyDirPath.toFile();
	    streamWriter.flush(); // TODO: not great
		  
		  if (dependencyDir.exists()) {
		    Collection<File> contentFiles = FileUtils.listFiles(dependencyDir, null, false);
		    // TODO: consider ordering them alphabetically

		    if (contentFiles.isEmpty()) {
		      artifactsWithoutLicensingInfos.add(dependency);
		    }
		    
		    for (File file : contentFiles) {
		      FileInputStream inputStream = FileUtils.openInputStream(file);
		      
		      // TODO: put somwhere central
		      byte[] buffer = new byte[1024];
		      int length;
		      while ((length = inputStream.read(buffer)) > 0) {
		        bufferedOutFileStream.write(buffer, 0, length);
		      }
		    }
		    streamWriter.write("\n");
		  }
		  else {
		    artifactsWithoutLicensingInfos.add(dependency);
		  }
		  
      streamWriter.write("\n");
		}
		
		streamWriter.write("Artifacts without licensing information:\n");
		for (MavenArtifact artifact : artifactsWithoutLicensingInfos) {
		  streamWriter.write(artifact.toString());
		  streamWriter.write("\n");
		}
		
		streamWriter.flush();
		bufferedOutFileStream.flush();
		outFileStream.flush();
		outFileStream.close();
	}

  private static Map<MavenArtifact, List<CamundaModule>> parseDependencies(Collection<File> inputFiles) throws IOException {
    
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
    
    return artifactsUsage;
  }
}
