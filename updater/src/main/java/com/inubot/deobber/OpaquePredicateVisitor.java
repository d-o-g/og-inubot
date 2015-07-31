/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.deobber;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.cfg.transform.Transform;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

/**
 * Filter:
 * 1. The predicate is the final argument
 * 2. The predicate is always of an int type
 * 3. The predicate value passed by a caller is a constant
 * 4. The comparator is the same for both error and return types
 * 5. Return type predicates only occur within void methods
 * <p>
 * - Predicates are seemingly inserted only after a jump instructions,
 * though this property is not enforced.
 * <p>
 * Process:
 * 1. Find all callers of the function, ensure the predicate is constant.
 * 1. Search for the error types.
 * 2. Ensure the comparison patten is the same for all cases.
 * 3. Search for return types with the header of the comparator
 * 4. Remove the predicate block from the root method, and the constant
 * from any caller methods. Adjust the methods descriptor, and the target
 * method descriptor of all callers.
 * <p>
 * * We use error types to justify the return type.
 * <p>
 * ------------------------------------------------------------------------
 * <p>
 * Root Comparable: The constant value in which serves as the lower
 * bound of values in which the predicate may be, the maximum bound is
 * finally determines by the comparison, respectively based on the
 * comparison type.
 * <p>
 * Valid Predicate Values: {C,COMPARISON} ==> VALID_DOMAIN
 * {C,IF_ICMPEQ} ==> [C,C]
 * {C,IF_ICMPNE} ==> [MIN,C) U (C,MAX]
 * {C,IF_ICMPLT} ==> [MIN,C)
 * <p>
 * Comparison Header + Comparator:
 * > ILOAD: Load the predicate local parameter (always of an int-type)
 * Valid-Range Load ////////////////////////////////////////////////////////////////
 * > LOAD ROOT COMPARABLE: This constant defines the lower bound of the valid range, respectively
 * > LOAD COMPARATOR:      This determine the upper bound of the valid-range, respectively
 * <p>
 * Post-Header: The type is determined by the byproduct if the provided predicate did not fall within the valid-range:
 * <p>
 * Type Error: It'll throw an IllegalStateException
 * new IllegalStateException
 * dup
 * invokespecial void IllegalStateException.<init>()V
 * athrow
 * <p>
 * Type Return: It returns, this only occurs in void-type methods
 *
 * @author Brainfree
 */
public class OpaquePredicateVisitor extends Transform {

    private int pass = 0;
    private int unused = 0;
    private int undetermined = 0;
    private int num_pred = 0;

    private static final String ERROR_DESC = Type.getInternalName(IllegalStateException.class);
    private static final Predicate<MethodInsnNode> PRED_FILTER = new Predicate<MethodInsnNode>() {
        @Override
        public boolean test(MethodInsnNode min) {
            Type[] args = Type.getArgumentTypes(min.desc);
            if (args.length == 0) return false;
            final int sort = args[args.length - 1].getSort();
            if (sort == Type.BYTE || sort == Type.SHORT || sort == Type.INT) { //Of an int-type
                //Verifying the passed value is constant and directly before the method call
                assert min.previous() != null;
                final int pop = min.previous().opcode();
                if (pop >= 2 && pop <= 8) return true; // -1 <= C <= 5
                if (pop == BIPUSH) return true;
                if (pop == SIPUSH) return true;
                if (pop == LDC) {
                    LdcInsnNode lin = (LdcInsnNode) min.previous();
                    if (lin.cst.getClass() == Integer.class) return true;
                }
            }
            return false;
        }
    };

    private static MethodKey ensureKey(Map<String, ClassNode> classes, String owner, String name, String desc) {
        ClassNode cur = classes.get(owner);
        while (cur != null) {
            for (MethodNode mn : cur.methods) {
                if (mn.name.equals(name) && mn.desc.equals(desc)) {
                    return new MethodKey(cur.name, name, desc);
                }
            }
            cur = classes.get(cur.superName);
        }
        return null;
    }

    private void run(Map<String, ClassNode> classes) {
        HashMap<MethodKey, Deque<Caller>> call_tree = new HashMap<>();
        /** Find all possible 'predicaticated' methods and their callers **/
        for (ClassNode cn : classes.values()) {
            for (MethodNode mn : cn.methods) {
                for (AbstractInsnNode ain : mn.instructions.toArray()) {
                    if (ain instanceof MethodInsnNode) {
                        MethodInsnNode min = (MethodInsnNode) ain;
                        if (!PRED_FILTER.test(min)) continue;
                        MethodKey key = ensureKey(classes, min.owner, min.name, min.desc);
                        if (key == null) {
                            //System.err.println("Unknown method source:" + min.owner + "#" + min.name + "@" + min.desc);
                            continue;
                        }
                        if (!min.owner.equals(key.owner)) {
                            //System.out.println(min.owner + "#" + min.name + "@" + min.desc + " is actually referring to " + key.owner);
                        }
                        Deque<Caller> callers = call_tree.get(key);
                        if (callers == null) {
                            callers = new ArrayDeque<>();
                            call_tree.put(key, callers);
                        }
                        callers.add(new Caller(min, mn));
                    }
                }
            }
        }

        //System.out.println("Possible Predicate Methods:" + call_tree.size());

        NEXT_METHOD:
        for (MethodKey mk : call_tree.keySet()) {
            MethodNode mn = lookup(classes, mk);
            if (mn == null) continue;
            Type[] args = Type.getArgumentTypes(mn.desc);
            int last_var = args.length - 1;
            if (!Modifier.isStatic(mn.access)) last_var++;
            ValidDomain prev_domain = null;
            List<OpPredicate> preds = new ArrayList<>();
            AbstractInsnNode[] stack = mn.instructions.toArray();
            int pos = 0;
            boolean used = false; // is the variable used?
            /** Find and verify domains **/
            while (pos < stack.length - 1) {
                AbstractInsnNode ain = stack[pos++];
                ValidDomain domain = pullDomain(ain, last_var);
                if (domain == null) {
                    if (ain.opcode() == ILOAD) {
                        VarInsnNode vin = (VarInsnNode) ain;
                        if (vin.var == last_var) {
                            continue NEXT_METHOD; //Its referring to the final argument for local usage
                        }
                    }
                    continue;
                }

                if (prev_domain != null) {
                    if (prev_domain.base != domain.base
                            || prev_domain.comp != domain.comp)
                        continue NEXT_METHOD; //Domain is not constant throughout the method
                }

                used = true;
                prev_domain = domain;
                pos += 2; //Skip over the header

                /** Extract the predicate type **/
                AbstractInsnNode trigger = stack[pos]; //The 'trigger' instruction directly after the header

                if (trigger.opcode() == NEW) {
                    TypeInsnNode tin = (TypeInsnNode) trigger;
                    if (!tin.desc.equals(ERROR_DESC)) continue;
                    preds.add(new OpPredicate(ain, stack[pos + 3]));
                    pos += 4;
                } else if (trigger.opcode() == RETURN) {
                    preds.add(new OpPredicate(ain, trigger));
                    pos += 1;
                }
            }

            /** Remove the predicates from the method **/
            if (!preds.isEmpty() || !used) {
                //... If it makes it here, it means that the final argument is a op-predicate
                if (!used) unused++;
                pass++;
                num_pred += preds.size();
                //System.out.println("Removing " + preds.size() + " predicates from " + mk.owner + "#" + mk.name + "@" + mk.desc);

                /** Remove the predicates from the method **/
                InsnList structs = mn.instructions;
                for (OpPredicate pred : preds) {
                    pred.remove(structs);
                }

                /** Remove the predicate from the callers, and fix the method descriptor **/
                String dec0 = Type.getMethodDescriptor(Type.getReturnType(mn.desc),
                        Arrays.copyOf(args, args.length - 1));
                mn.desc = dec0;
                Deque<Caller> callers = call_tree.get(mk);
                for (Caller caller : callers) {
                    caller.call.desc = dec0;
                    caller.caller.instructions.remove(caller.call.previous()); //Remove the passed constant
                }
                callers.clear();
                preds.clear();
            } else {
                undetermined++;
            }
        }
        call_tree.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("^ Found ").append(unused).append(" unused dummy parameters").append("\n");
        sb.append("^ Found ").append(undetermined).append(" undetermined opaque predicates").append("\n");
        sb.append("^ Removed ").append(num_pred).append(" opaque predicates");
        return sb.toString();
    }

    private static ValidDomain pullDomain(AbstractInsnNode head, int var) {

        AbstractInsnNode pred = head;
        if (pred == null) return null;
        AbstractInsnNode base = pred.next();
        if (base == null) return null;
        AbstractInsnNode comp = base.next();
        if (comp == null) return null;

        /** Verify it's comparing two ints **/
        if (comp.opcode() < 159
                || comp.opcode() > 164) return null; //Not a integer comparison

        /** Verify its comparing the final argument **/
        if (pred.opcode() == ILOAD) { //its an int-type
            VarInsnNode vin = (VarInsnNode) pred;
            if (vin.var != var) return null; //It's not comparing the final var
            // ... It's comparing the final argument, in which is an int
        } else {
            return null;
        }
        /** Verify it's comparing to a constant value **/
        Integer base_value = extract(base);
        if (base_value == null) return null; //Not an int
        return new ValidDomain(base_value, comp.opcode());

    }

    private static Integer extract(AbstractInsnNode ain) {
        if (ain.opcode() >= 2 && ain.opcode() <= 8) {
            return ain.opcode() - 3;
        } else if (ain.opcode() == LDC) {
            Object cst = ((LdcInsnNode) ain).cst;
            if (cst instanceof Integer) return (Integer) cst;
        } else if (ain instanceof IntInsnNode) {
            return ((IntInsnNode) ain).operand;
        }
        return null;
    }

    private static MethodNode lookup(Map<String, ClassNode> classes, MethodKey key) {
        ClassNode cn = classes.get(key.owner);
        if (cn == null) return null; //Could be a jdk class
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals(key.name) && mn.desc.equals(key.desc)) {
                return mn;
            }
        }
        return null;
    }

    @Override
    public void transform(Map<String, ClassNode> classes) {
        run(classes);
    }

    private static class MethodKey {
        final String owner;
        final String name;
        final String desc;
        boolean cache = false;
        int hash = 0;

        MethodKey(String owner, String name, String desc) {
            this.owner = owner;
            this.name = name;
            this.desc = desc;
        }

        @Override
        public int hashCode() {
            if (cache) return hash;
            hash = Arrays.hashCode(new Object[]{owner, name, desc});
            cache = true;
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            assert o instanceof MethodKey;
            MethodKey key = (MethodKey) o;
            return key.owner.equals(owner)
                    && key.name.equals(name)
                    && key.desc.equals(desc);
        }
    }

    private static class Caller {
        final MethodInsnNode call;
        final MethodNode caller;

        Caller(MethodInsnNode call, MethodNode caller) {
            this.call = call;
            this.caller = caller;
        }

        public String toString() {
            return caller.owner.name + "#" + caller.name + "@" + caller.desc;
        }
    }

    private static class OpPredicate {
        final AbstractInsnNode head;
        final AbstractInsnNode tail;

        OpPredicate(AbstractInsnNode root,
                    AbstractInsnNode tail) { //Root being the header comparison
            this.head = root;
            this.tail = tail;
        }

        //   H              T
        //  PREV          PREV
        //  NEXT          NEXT
        void remove(InsnList src) {
            AbstractInsnNode next = head;
            do {
                AbstractInsnNode next0 = next.next();
                src.remove(next);
                next = next0;
            } while (next != tail);
            src.remove(tail);
        }
    }

    private static class ValidDomain {
        final int base;
        final int comp;

        public ValidDomain(int base, int comp) {
            this.base = base;
            this.comp = comp;
        }
    }
}
