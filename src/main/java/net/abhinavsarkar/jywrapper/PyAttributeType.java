package net.abhinavsarkar.jywrapper;

import net.abhinavsarkar.jywrapper.PyObjectProxy.MemberType;

public enum PyAttributeType {

	GETTER(MemberType.GETTER), SETTER(MemberType.SETTER), CONST(MemberType.CONST);

	private final MemberType memberType;

	private PyAttributeType(final MemberType memberType) {
		this.memberType = memberType;
	}

	MemberType getMemberType() {
		return memberType;
	}
}
