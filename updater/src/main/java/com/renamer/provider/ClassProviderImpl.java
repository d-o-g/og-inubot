package com.renamer.provider;

import com.renamer.tree.ClassMember;

import jdk.internal.org.objectweb.asm.ClassReader;

import java.io.IOException;
/**
 * @author Mark
 *
 */
public class ClassProviderImpl extends ClassProvider {

	public ClassProviderImpl() {
		super(null);
	}

	public ClassProviderImpl(ClassProvider delegate) {
		super(delegate);
	}

	public ClassMember getClass(String name) {
		try {
			ClassMember cn = new ClassMember(true);
			ClassReader cr = new ClassReader(name);
			cr.accept(cn, 0);
			return cn;
		} catch (IOException ex) {
			if (cp != null) {
				return cp.getClass(name);
			}
		}
		return null;
	}

}
