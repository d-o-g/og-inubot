package com.inubot.oldschool.analysis;

import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import com.inubot.modscript.hook.FieldHook;
import com.inubot.modscript.hook.InvokeHook;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.FieldMemberNode;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

@VisitorInfo(hooks = {"tail", "head", "next", "current"})
public class NodeDeque extends GraphVisitor {

    @Override
    public boolean validate(ClassNode cn) {
        return cn.ownerless() && cn.fieldCount() == 2 && cn.fieldCount(desc("Node")) == 2 && cn.interfaces.size() == 0;
    }

    @Override
    public void visit() {
        visit(new NodeHooks());
	    methods();
    }

    private class NodeHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ACMPNE) {
                        FieldMemberNode fmn = jn.firstField();
                        String node = desc("Node");
                        if (fmn != null && fmn.desc().equals(node)) {
                            hooks.put("tail", new FieldHook("tail", fmn.fin()));
                            for (FieldNode fn : cn.fields) {
                                if (fn.desc.equals(node)) {
                                    if (!fn.name.equals(fmn.name())) {
                                        hooks.put("head", new FieldHook("head", fn));
                                        break;
                                    }
                                }
                            }
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

	private void methods() {
		for (MethodNode mn : cn.methods) {
			if (mn.desc.equals("()L" + clazz("Node") + ";") && mn.referenced(updater.archive.build().get("client"))) {
				int count = 0;
				search: {
					for (AbstractInsnNode ain : mn.instructions.toArray()) {
						if (ain.opcode() == Opcodes.GETFIELD) {
							count++;
						} else if (ain.opcode() == Opcodes.INVOKEVIRTUAL) {
							break search;
						}
					}
					if (count == 4) {
                        hooks.put("current", new InvokeHook("current", mn));
					} else if (count == 3) {
						hooks.put("next", new InvokeHook("next", mn));
					}
				}
			}
		}
	}
}
