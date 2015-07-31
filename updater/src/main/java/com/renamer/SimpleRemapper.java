package com.renamer;

import com.renamer.tree.BranchedClassNode;
import jdk.internal.org.objectweb.asm.commons.Remapper;

import java.util.Map;

public class SimpleRemapper extends Remapper {

	private Map<String, BranchedClassNode> tree;

	public SimpleRemapper(Map<String, BranchedClassNode> tree) {
		this.tree = tree;
	}

	@Override
	public String mapFieldName(String owner, String name, String desc) {
		BranchedClassNode cn = tree.get(owner);
		if (cn != null)
			return cn.getFieldName(name, desc);
		return name;
	}

	@Override
	public String mapMethodName(String owner, String name, String desc) {
		BranchedClassNode cn = tree.get(owner);
		if (cn != null)
			return cn.getMethodName(name, desc);
		return name;
	}

	@Override
	public String map(String typeName) {
		BranchedClassNode cn = tree.get(typeName);
		if (cn != null) {
			String name = cn.getNewName();
			if (name != null)
				return name;
		}
		return typeName;
	}
}
