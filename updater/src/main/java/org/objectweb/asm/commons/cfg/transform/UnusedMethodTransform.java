package org.objectweb.asm.commons.cfg.transform;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.wrapper.ClassFactory;
import org.objectweb.asm.commons.wrapper.ClassMethod;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.*;

/**
 * @author Tyler Sedlar
 * @since 3/19/15.
 */
public abstract class UnusedMethodTransform extends Transform {

    public abstract void populateEntryPoints(List<ClassMethod> entries);

    private final List<ClassMethod> entryPoints = new LinkedList<>();
    private final Set<String> validMethodKeys = new HashSet<>();
    private final List<ClassMethod> validMethods = new LinkedList<>();

    private int totalMethods = 0;

    public List<ClassMethod> found() {
        return validMethods;
    }

    private void follow(Map<String, ClassFactory> classes, List<ClassMethod> followed, ClassMethod method) {
        if (validMethodKeys.contains(method.key())) {
            return;
        }
        validMethodKeys.add(method.key());
        followed.add(method);
        method.method.accept(new MethodVisitor() {
            public void visitMethodInsn(MethodInsnNode min) {
                if (classes.containsKey(min.owner)) {
                    ClassFactory factory = classes.get(min.owner);
                    ClassMethod innerMethod = factory.findMethod(cm -> cm.method.name.equals(min.name) &&
                            cm.method.desc.equals(min.desc));
                    if (innerMethod != null)
                        follow(classes, followed, innerMethod);
                    if (classes.containsKey(factory.node.superName)) {
                        ClassFactory superFactory = classes.get(factory.node.superName);
                        ClassMethod superMethod = superFactory.findMethod(cm -> cm.method.name.equals(min.name) &&
                                cm.method.desc.equals(min.desc));
                        if (superMethod != null)
                            follow(classes, followed, superMethod);
                    }
                }
            }
        });
    }

    private int removed = 0;

    @Override
    public void transform(Map<String, ClassNode> classes) {
        Map<String, ClassFactory> factories = new HashMap<>();
        for (ClassNode cn : classes.values()) {
            factories.put(cn.name, new ClassFactory(cn));
            totalMethods += cn.methods.size();
        }

        int rm = -1;
        while (rm != 0) {
            rm = 0;
            populateEntryPoints(entryPoints);
            for (ClassMethod method : entryPoints)
                follow(factories, validMethods, method);
            for (ClassFactory cf : factories.values()) {
                for (ClassMethod m : cf.findMethods(mf -> !validMethodKeys.contains(mf.key()))) {
                    removed++;
                    rm++;
                    m.remove();
                }
            }
        }
    }

    @Override
    public String toString() {
        return String.format("^ Removed %d/%d dummy methods", removed, totalMethods);
    }

    public int getRemoved() {
        return totalMethods - validMethods.size();
    }

    public int getValid() {
        return validMethods.size();
    }

    public int getTotal() {
        return totalMethods;
    }
}
