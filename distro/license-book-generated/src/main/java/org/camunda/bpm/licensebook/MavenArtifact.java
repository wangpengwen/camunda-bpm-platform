package org.camunda.bpm.licensebook;

public class MavenArtifact {

	protected String groupId;
	protected String artifactId;
	protected String version;
	protected String type;
	
	public MavenArtifact(String groupId, String artifactId, String version, String type) {
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.type = type;
	}
	
	public String getGroupId() {
		return groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public String getVersion() {
		return version;
	}
	public String getType() {
		return type;
	}
}
