/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.cfg.transform.Transform;
import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.JumpNode;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.commons.cfg.tree.node.VariableNode;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class RemoveOpaquePredicates implements Opcodes {

    public static int removed = 0;

    public static void dostuff(NodeTree tree) {
        Type[] types = Type.getArgumentTypes(tree.method().desc);
        if (types.length == 0) {
            return;
        }
        String end = types[types.length - 1].getDescriptor();
        if (!end.equals("I") && !end.equals("S") && !end.equals("B")) {
            return;
        }
        PairCollector visitor = new PairCollector();
        visitor.targetVar = calculateLastParameterIndex(types, Modifier.isStatic(tree.method().access));
        tree.accept(visitor);
        Set<ComparisonPair> pairs = visitor.pairs;
        if (pairs.size() > 0) {
            if (valid(pairs)) {
                Map<ComparisonPair, List<AbstractInsnNode>> map = new HashMap<>();
                boolean b = false;

                for (ComparisonPair pair : pairs) {
                    List<AbstractInsnNode> block = block(pair);

                    if (block == null) {
                        b = true;
                        break;
                    }
                    if (block.size() == 1) {
                        if (block.get(0).opcode() != RETURN) {
                            b = true;
                            break;
                        }
                    } else {
                        if (block.get(block.size() - 1).opcode() != ATHROW) {
                            b = true;
                            break;
                        } else {
                            AbstractInsnNode t = block.get(0);
                            if (t instanceof TypeInsnNode) {
                                TypeInsnNode tin = (TypeInsnNode) t;
                                if (!tin.desc.equals("java/lang/IllegalStateException")) {
                                    b = true;
                                    break;
                                }
                            } else {
                                b = true;
                                break;
                            }
                        }
                    }

                    map.put(pair, block);
                }

                if (!b) {
                    for (Map.Entry<ComparisonPair, List<AbstractInsnNode>> e : map.entrySet()) {
                        Jump jump = e.getKey().jump;
                                    /*
                                     * Redirect the false jump location of the
									 * jump and force it to the target.
									 */
                        tree.method().instructions.insert(jump.jin, new JumpInsnNode(GOTO, jump.jin.label));
                        tree.method().instructions.remove(jump.jin);

                        for (AbstractInsnNode a : jump.insns) {
                            tree.method().instructions.remove(a);
                        }

                        for (AbstractInsnNode a : e.getValue()) {
                            tree.method().instructions.remove(a);
                        }

                        removed++;
                    }
                }
            }
        }
        visitor.pairs.clear();
    }

    private static int calculateLastParameterIndex(Type[] args, boolean stat) {
        // starting index for static method = 0,
        // starting index for virtual method = 1 (this = 0)
        // but we have to start at (that - 1) as the first
        // parameter index will add to it.
        int c = stat ? -1 : 0;
        for (Type arg : args) {
            switch (arg.getDescriptor()) {
                case "D":
                case "J": {
                    c += 2;
                    break;
                }
                default: {
                    c += 1;
                    break;
                }
            }
        }
        return c;
    }

    private static List<AbstractInsnNode> block(ComparisonPair p) {
        List<AbstractInsnNode> ains = new ArrayList<>();
        AbstractInsnNode ain = p.jump.jin.next();
        while (true) {
            if (ain == null)
                return null;

            ains.add(ain);

            if (ain.opcode() == ATHROW || ain.opcode() == RETURN) {
                return ains;
            } else if (ain.type() == AbstractInsnNode.JUMP_INSN || ain.type() == AbstractInsnNode.LOOKUPSWITCH_INSN
                    || ain.type() == AbstractInsnNode.TABLESWITCH_INSN) {
                return null;
            }

            ain = ain.next();
        }
    }

    private static boolean valid(Set<ComparisonPair> psets) {
        int num = -1;
        int jop = -1;
        /*
         * Check to see if the comparison opcodes and the number being compared
		 * is the same. (we need to make sure that the parameter is actually a
		 * valid opaque).
		 */
        for (ComparisonPair p : psets) {
            if (num == -1) {
                num = p.num;
            } else if (num != p.num) {
                return false;
            }

            if (jop == -1) {
                jop = p.jump.jin.opcode();
            } else if (p.jump.jin.opcode() != jop) {
                return false;
            }
        }
        return true;
    }

    private static class PairCollector extends NodeVisitor {

        private final Set<ComparisonPair> pairs = new HashSet<>();
        private int targetVar;

        @Override
        public void visitJump(JumpNode jn) {
            if (jn.opcode() != GOTO && jn.children() == 2) {
                NumberNode nn = jn.firstNumber();
                VariableNode vn = jn.firstVariable();
                if (nn != null && vn != null && vn.var() == targetVar) {
                    Jump jump = new Jump(jn.insn(), nn.insn(), vn.insn());
                    ComparisonPair pair = new ComparisonPair(nn.number(), jump);
                    pairs.add(pair);
                }
            }
        }
    }

    private static class ComparisonPair {

        private final int num;
        private final Jump jump;

        ComparisonPair(int num, Jump jump) {
            this.num = num;
            this.jump = jump;
        }

        @Override
        public String toString() {
            return "ComparisonPair [num=" + num + ", jump=" + jump + "]";
        }
    }

    private static class Jump {

        private final JumpInsnNode jin;
        private final AbstractInsnNode[] insns;

        Jump(JumpInsnNode jin, AbstractInsnNode... insns) {
            this.jin = jin;
            this.insns = insns;
        }

        @Override
        public String toString() {
            return "Jump [jin=" + jin + ", insns=" + Arrays.toString(insns) + "]";
        }
    }
}
