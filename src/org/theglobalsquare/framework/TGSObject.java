package org.theglobalsquare.framework;

public abstract class TGSObject implements ITGSObject {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
