package com.renamer.provider;

import com.renamer.tree.ClassMember;

/**
 * @author Mark
 *
 */
public abstract class ClassProvider {

	protected ClassProvider cp;

	public ClassProvider(ClassProvider delegate) {
		this.cp = delegate;
	}

	public abstract ClassMember getClass(String name);

}
