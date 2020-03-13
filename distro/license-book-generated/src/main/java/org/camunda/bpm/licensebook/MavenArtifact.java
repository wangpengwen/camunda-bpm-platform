package org.camunda.bpm.licensebook;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MavenArtifact implements Comparable<MavenArtifact> {
	
	protected static final Pattern CANONICAL_REGEX = Pattern.compile("(.+):(.+):(.+):(.+)");

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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MavenArtifact other = (MavenArtifact) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	public static MavenArtifact fromCanonicalString(String canonical) {
		Matcher matcher = CANONICAL_REGEX.matcher(canonical.trim());
		
		if (!matcher.matches()) {
			throw new RuntimeException("Canonical Maven string \"" + canonical + 
					"\" does not match expected format groupId:artifactId:type:version");
		}

		String groupId = matcher.group(1);
		String artifactId = matcher.group(2);
		String type = matcher.group(3);
		String version = matcher.group(4);
		
		return new MavenArtifact(groupId, artifactId, version, type);
	}
	
	@Override
	public String toString() {
		return String.join(":", groupId, artifactId, type, version);
	}

	@Override
	public int compareTo(MavenArtifact o) {
		return toString().compareTo(o.toString());
	}
}
