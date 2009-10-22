package net.abhinavsarkar.jywrapper;

import net.abhinavsarkar.jywrapper.PyObjectProxy.MemberType;

public enum PyMethodType {
	INIT(MemberType.INIT), 
	DIRECT(MemberType.DIRECT), 
	UNDERSCORED(MemberType.UNDERSCORED), 
	NUMERIC(MemberType.NUMERIC);
	
	private final MemberType memberType;

	private PyMethodType(MemberType memberType) {
		this.memberType = memberType;
	}

	public MemberType getMemberType() {
		return memberType;
	}
}
