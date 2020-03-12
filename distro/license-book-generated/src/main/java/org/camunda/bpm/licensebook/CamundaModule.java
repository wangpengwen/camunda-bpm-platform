package org.camunda.bpm.licensebook;

public class CamundaModule {

	protected String name;
	
	public CamundaModule(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static CamundaModule fromDependencyName(String name) {
		return new CamundaModule(name);
	}
	
	@Override
	public String toString() {
		return name;
	}
}
