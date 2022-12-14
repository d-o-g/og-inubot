package com.inubot.oldschool.analysis;

import com.inubot.modscript.hook.FieldHook;
import com.inubot.modscript.hook.InvokeHook;
import com.inubot.util.ArrayIterator;
import com.inubot.visitor.GraphVisitor;
import com.inubot.visitor.VisitorInfo;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.query.InsnQuery;
import org.objectweb.asm.commons.cfg.query.MemberQuery;
import org.objectweb.asm.commons.cfg.tree.NodeTree;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.*;
import org.objectweb.asm.commons.cfg.tree.util.TreeBuilder;
import org.objectweb.asm.tree.*;

import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.*;

@VisitorInfo(hooks = {"processAction", "players", "npcs", "canvas", "player", "region", "interfaceComponents", "objects",
        "groundItems", "cameraX", "cameraY", "cameraZ", "cameraPitch", "cameraYaw", "mapScale", "mapOffset",
        "mapRotation", "baseX", "baseY", "varps", "tempVarps", "interfacePositionsX", "interfacePositionsY",
        "interfaceWidths", "interfaceHeights", "renderRules", "tileHeights", "interfaceNodes", "npcIndices",
        "loadObjectDefinition", "loadNpcDefinition", "loadItemDefinition", "plane", "gameState", "mouseIdleTime",
        "energy", "weight", "experiences", "levels", "realLevels", "playerActions", "collisionMaps",
        "hoveredRegionTileX", "hoveredRegionTileY", "socketState", "menuOpcodes", "menuArg0", "menuArg1", "menuArg2",
        "username", "password", "getVarpBit", "hintArrowX", "hintArrowY", "hintArrowType", "loginState",
        "itemTables", "lowMemory", "hintArrowNpcIndex", "hintArrowPlayerIndex", "loadItemSprite", "engineCycle",
        "screenWidth", "screenHeight", "screenZoom", "screenState", "font_p12full", "grandExchangeOffers",
        "currentWorld", "membersWorld", "onCursorUids", "onCursorCount", "menuX", "menuY", "menuWidth", "menuHeight",
        "viewportWalking", "socket", "spellTargetFlags", "spellSelected", "interfaceConfigs", "redrawMode",
        "varpBitCache", "varpBitTable", "clanChatName", "clanMateCount", "friendCount", "rights", "clanChatRank",
        "clanChatOwner", "ignoreCount"})
public class Client extends GraphVisitor {

    private final Map<String, String> profiledStrings = new HashMap<>();

    private FieldInsnNode next(AbstractInsnNode from, int op, String desc, String owner, int skips) {
        return next(0, from, op, desc, owner, skips);
    }

    private FieldInsnNode next(int followed, AbstractInsnNode from, int op, String desc, String owner, int skips) {
        int skipped = 0;
        int maxfollow = 20;

        AbstractInsnNode curr = from;
        while ((curr = curr.next()) != null) {
            if (curr.opcode() == op) {
                FieldInsnNode topkek = (FieldInsnNode) curr;
                if ((desc == null || topkek.desc.equals(desc)) && (owner == null || owner.equals(topkek.owner))) {
                    if (skipped == skips) {
                        return topkek;
                    }
                    skipped++;
                }
            } else if (curr.opcode() == GOTO && followed < maxfollow) {
                curr = ((JumpInsnNode) curr).label.next();
                followed++;
            }
        }
        return null;
    }


    @Override
    public String iface() {
        return updater.getAccessorPackage() + "/Client";
    }

    @Override
    public boolean validate(ClassNode cn) {
        return cn.name.equals("client");
    }

    @Override
    public void visit() {
        profileStrings();
        visitProcessAction();
        visit("Region", new ViewportWalking());
        visitMouseIdleTime();
        visitDefLoader("loadObjectDefinition", "ObjectDefinition", false);
        visitDefLoader("loadNpcDefinition", "NpcDefinition", false);
        visitDefLoader("loadItemDefinition", "ItemDefinition", false);
        visitStaticFields();
        visitAll(new CameraXY());
        visitAll(new CameraZ());
        visitAll(new CameraPY());
        visitAll(new MapHooks());
        visitAll(new MapAngle());
        visit("Varps", new SettingHooks());
        visitAll(new InterfacePositionHooks());
        visitAll(new RenderRules());
        visitAll(new TileHeights());
        visitAll(new InterfaceNodes());
        visitAll(new InterfaceConfigs());
        visitAll(new HintHooks());
        visitAll(new NpcIndices());
        visitAll(new Plane());
        visitAll(new GameState());
        visitAll(new RunHooks());
        visitAll(new ExperienceHooks());
        visitAll(new PlayerActions());
        visitAll(new SocketState());
        visitAll(new Username());
        visitAll(new Password());
        visitAll(new LoginState());
        visitAll(new LowMemory());
        visitAll(new HintPlayerIndex());
        visitAll(new SpellTargetFlags());
        visitAll(new HintNpcIndex());
        visitAll(new Cycle());
        visitAll(new BufferBaseRead());
        visitAll(new ScreenSizes());
        visitIfM(new ScreenState(), t -> t.desc.startsWith("([L") && t.desc.contains(";IIIIII"));
        visitAll(new HoveredTile());
        visitAll(new MenuPositionHooks());
        visitLocalIfM(new RedrawMode(),
                m -> !Modifier.isPublic(m.access)
                        && !Modifier.isStatic(m.access)
                        && Modifier.isFinal(m.access)
                        && Type.getArgumentTypes(m.desc).length <= 1
                        && m.desc.endsWith("V"));
        //visitAll(new Socket());
        visitIf("ItemDefinition", new ItemDefActions(), m -> m.name.equals("<init>"));


        //if this ever breaks:
        //find int[] init'd of size 1000 new int[1000]
        //and then find thatArray[anotherInt++] = x;
        //for validation, check that its used in some model calc
        CursorUidsP1 cursorUidsP1 = new CursorUidsP1();
        CursorUidsP2 cursorUidsP2 = new CursorUidsP2();
        visitIfM(cursorUidsP1, mn -> !Modifier.isStatic(mn.access) && mn.desc.endsWith("V")
                && mn.desc.startsWith("(ZZZI") && mn.owner.name.equals(clazz("Model")));
        //just incase it gets inlined
        visitIfM(cursorUidsP2, mn -> !Modifier.isStatic(mn.access) && mn.desc.endsWith("V")
                && mn.desc.startsWith("(ZZZI") && mn.owner.name.equals(clazz("Model")));
        if (cursorUidsP1.invoke == null) {
            System.out.println("haha rip cursor uids");
        } else {
            visitIfM(cursorUidsP2, mn -> mn.key().equals(cursorUidsP1.invoke.key()));
        }

        GraphVisitor icomp = updater.visitor("InterfaceComponent");
        for (ClassNode cn : updater.classnodes.values()) {
            for (MethodNode mn : cn.methods) {
                if (mn.desc.contains(desc("ScriptEvent")) || mn.desc.contains(desc("RuneScript"))) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain instanceof IntInsnNode) {
                            int oper = ((IntInsnNode) ain).operand;
                            if (oper == 3600) {
                                FieldInsnNode fin = next(ain, GETSTATIC, "I", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("friendCount", fin));
                                }
                            } else if (oper == 3621) {
                                FieldInsnNode fin = next(ain, GETSTATIC, "I", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("ignoreCount", fin));
                                }
                            } else if (oper == 3611) {
                                FieldInsnNode fin = next(ain, GETSTATIC, "Ljava/lang/String;", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("clanChatOwner", fin));
                                }
                            } else if (oper == 3625) {
                                FieldInsnNode fin = next(ain, GETSTATIC, "Ljava/lang/String;", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("clanChatName", fin));
                                }
                            } else if (oper == 3618) {
                                FieldInsnNode fin = next(ain, GETSTATIC, "B", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("clanChatRank", fin));
                                }
                            } else if (oper == 3612) {
                                FieldInsnNode fin = next(ain, GETSTATIC, "I", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("clanMateCount", fin));
                                }
                            } else if (oper == 3316) {
                                FieldInsnNode fin = next(ain, GETSTATIC, "I", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("rights", fin));
                                }
                            } else if (oper == 1201) {
                                FieldInsnNode fin = next(ain, PUTFIELD, "I", icomp.cn.name, 1);
                                if (fin != null) {
                                    icomp.addHook(new FieldHook("modelId", fin));
                                }
                            } else if (oper == 1106) {
                                FieldInsnNode fin = next(ain, PUTFIELD, "I", icomp.cn.name, 0);
                                if (fin != null) {
                                    icomp.addHook(new FieldHook("spriteId", fin));
                                }
                            } else if (oper == 1117) {
                                FieldInsnNode fin = next(ain, PUTFIELD, "I", icomp.cn.name, 0);
                                if (fin != null) {
                                    icomp.addHook(new FieldHook("shadowColor", fin));
                                }
                            } else if (oper == 1113) {
                                FieldInsnNode fin = next(ain, PUTFIELD, "I", icomp.cn.name, 0);
                                if (fin != null) {
                                    icomp.addHook(new FieldHook("fontId", fin));
                                }
                            } else if (oper == 1101) {
                                FieldInsnNode fin = next(ain, PUTFIELD, "I", icomp.cn.name, 0);
                                if (fin != null) {
                                    icomp.addHook(new FieldHook("textColor", fin));
                                }
                            } else if (oper == 1116) {
                                FieldInsnNode fin = next(ain, PUTFIELD, "I", icomp.cn.name, 0);
                                if (fin != null) {
                                    icomp.addHook(new FieldHook("borderThickness", fin));
                                }
                            } else if (oper == 1306) {
                                FieldInsnNode fin = next(ain, PUTFIELD, "Ljava/lang/String;", icomp.cn.name, 0);
                                if (fin != null) {
                                    icomp.addHook(new FieldHook("selectedAction", fin));
                                }
                            } else if (oper == 3912) {
                                FieldInsnNode fin = next(ain, GETSTATIC, "Z", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("membersWorld", fin));
                                }
                            } else if (oper == 3918) { //TODO FIXME
                                FieldInsnNode fin = next(ain, GETSTATIC, "I", null, 0);
                                if (fin != null) {
                                    addHook(new FieldHook("currentWorld", fin));
                                }
                            }
                        }
                    }
                }

                if (!mn.desc.startsWith("(Ljava/lang/String;Ljava/lang/String;IIII"))
                    continue;
                TreeBuilder.build(mn).accept(new NodeVisitor() {
                    @Override
                    public void visitNumber(NumberNode nn) {
                        if (nn.number() != 500)
                            return;
                        nn.tree().accept(new NodeVisitor() {
                            @Override
                            public void visitVariable(VariableNode vn) {
                                FieldMemberNode fmn = vn.parent().firstField();
                                if (fmn == null)
                                    return;
                                if (vn.var() == 2) {
                                    addHook(new FieldHook("menuOpcodes", fmn.fin()));
                                } else if (vn.var() == 3) {
                                    addHook(new FieldHook("menuArg0", fmn.fin()));
                                } else if (vn.var() == 4) {
                                    addHook(new FieldHook("menuArg1", fmn.fin()));
                                } else if (vn.var() == 5) {
                                    addHook(new FieldHook("menuArg2", fmn.fin()));
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    private void profileStrings() {
        for (ClassNode cn : updater.classnodes.values()) {
            if (cn.fieldCount("Ljava/lang/String;", false) < 200 || cn.name.equals("client"))
                continue;
            TreeBuilder.build(cn.getMethod("<clinit>", "()V")).forEach(node -> node.accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("Ljava/lang/String;") && fmn.children() == 1) {
                        final ConstantNode cn = fmn.firstConstant();
                        if (cn != null) {
                            profiledStrings.put(fmn.key(), (String) cn.cst());
                        }
                    }
                }
            }));
        }
    }

    private void visitMouseIdleTime() {
        for (ClassNode cn : updater.classnodes.values()) {
            if (!cn.interfaces.contains("java/awt/event/MouseListener"))
                continue;
            for (MethodNode meth : cn.methods) {
                if (!meth.name.equals("mouseExited"))
                    continue;
                updater.graphs().get(cn).get(meth).forEach(b -> b.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() != PUTSTATIC || fmn.children() != 1)
                            return;
                        NumberNode iconst_0 = fmn.firstNumber();
                        if (iconst_0 == null || iconst_0.number() != 0)
                            return;
                        addHook(new FieldHook("mouseIdleTime", fmn.fin()));
                    }
                }));
            }
        }
    }

    private void visitProcessAction() {
        for (ClassNode cn : updater.classnodes.values()) {
            cn.methods.stream().filter(mn -> mn.desc.startsWith("(IIIILjava/lang/String;Ljava/lang/String;") &&
                    mn.desc.endsWith(")V")).forEach(mn -> addHook(new InvokeHook("processAction", mn)));
        }
    }

    private void visitDefLoader(String hook, String visitor, boolean transform) {
        for (ClassNode cn : updater.classnodes.values()) {
            cn.methods.stream().filter(mn -> mn.desc.endsWith(")" + desc(visitor))).forEach(mn -> {
                int access = mn.access & ACC_STATIC;
                if (transform ? access == 0 : access > 0)
                    addHook(new InvokeHook(hook, cn.name, mn.name, mn.desc));
            });
        }
    }

    private void visitStaticFields() {
        String playerDesc = desc("Player");
        String regionDesc = desc("Region");
        String widgetDesc = desc("InterfaceComponent");
        String objectDesc = desc("EntityMarker");
        String dequeDesc = desc("NodeDeque");
        String collisionDesc = desc("CollisionMap");
        for (ClassNode node : updater.classnodes.values()) {
            for (FieldNode fn : node.fields) {
                if ((fn.access & Opcodes.ACC_STATIC) != 0) {
                    if (playerDesc != null && fn.desc.equals(playerDesc)) {
                        add("player", fn);
                    } else if (regionDesc != null && fn.desc.equals(regionDesc)) {
                        add("region", fn);
                    } else if (widgetDesc != null && fn.desc.equals("[[" + widgetDesc)) {
                        add("interfaceComponents", fn);
                    } else if (objectDesc != null && fn.desc.equals("[" + objectDesc)) {
                        add("objects", fn);
                    } else if (dequeDesc != null && fn.desc.equals("[[[" + dequeDesc)) {
                        add("groundItems", fn);
                    } else if (collisionDesc != null && fn.desc.equals("[" + collisionDesc)) {
                        add("collisionMaps", fn);
                    } else if (fn.desc.equals("[" + desc("GrandExchangeOffer"))) {
                        add("grandExchangeOffers", fn);
                    } else if (fn.desc.equals("[" + desc("Player"))) {
                        add("players", fn);
                    } else if (fn.desc.equals("[" + desc("Npc"))) {
                        add("npcs", fn);
                    }
                } else if (fn.desc.equals("Ljava/awt/Canvas;")) {
                    FieldHook fh = new FieldHook("canvas", node.name, fn.name, fn.desc);
                    fh.forceOwner = false;
                    addHook(fh);
                }
            }
        }
    }

    private class RedrawMode extends BlockVisitor {

        private final List<FieldMemberNode> test = new ArrayList<>();

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    if (jn.hasChild(ICONST_3)) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                        if (fmn != null && fmn.desc().equals("I")) {
                            String cs = getHookKey("gameState");
                            if (!fmn.key().equals(cs)) {
                                test.add(fmn);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void visitEnd() {
            for (ClassNode cn : updater.classnodes.values()) {
                for (MethodNode mn : cn.methods) {
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.opcode() != PUTSTATIC) {
                            continue;
                        }
                        FieldInsnNode fin = (FieldInsnNode) ain;
                        if (fin.owner.equals("client") && fin.desc.equals("I")) {
                            for (int i = 0; i < test.size(); i++) {
                                FieldMemberNode t = test.get(i);
                                if (fin.name.equals(t.name()) && !mn.name.equals("<clinit>")) {
                                    test.remove(i);
                                }
                            }
                        }
                    }
                }
            }
            //should only be 1 left but loop just incase, so updater prints warning if overwrite
            for (FieldMemberNode t : test) {
                addHook(new FieldHook("redrawMode", t.fin()));
            }
        }
    }

    private class ItemDefActions extends BlockVisitor {

        private final GraphVisitor itemdef = updater.visitor("ItemDefinition");

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    FieldMemberNode actions = (FieldMemberNode) fmn.tree().first(f -> f.opcode() == PUTFIELD
                            && ((FieldMemberNode) f).desc().equals("[Ljava/lang/String;")
                            && itemdef.resolve(((FieldMemberNode) f).key()) == null);
                    if (actions != null) {
                        String ye = profiledStrings.get(fmn.key());
                        if (ye == null) {
                            return;
                        }
                        if (ye.equals("Drop")) {
                            itemdef.addHook(new FieldHook("actions", actions.fin()));
                        } else if (ye.equals("Take")) {
                            itemdef.addHook(new FieldHook("groundActions", actions.fin()));
                        }
                    }
                }
            });
        }
    }

    private class ViewportWalking extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (((block.owner.access & ACC_STATIC) == 0) //method = non-static
                    && block.owner.desc.startsWith("(IIIZ")) {
                block.tree().accept(new NodeVisitor() {

                    //return bool && int != -1, visit call
                    public void visitMethod(MethodMemberNode mmn) {
                        if (mmn.desc().endsWith("Z") && mmn.opcode() == INVOKESTATIC) {
                            updater.getGraph(mmn).forEach(block -> block.tree().accept(this));
                        }
                    }

                    //method got inlined, code is changed to boolean x = bool && int != -1
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.desc().equals("Z") && fmn.opcode() == GETSTATIC) { //booleans
                            addHook(new FieldHook("viewportWalking", fmn.fin()));
                            lock.set(true);
                        }
                    }
                });
            }
        }
    }

    private class MenuPositionHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKESTATIC && mmn.desc().matches("\\(IIII(I|B|S|^)\\)V")) {
                        AbstractNode xmul = mmn.find(IMUL, 0);
                        if (xmul == null) return;
                        FieldMemberNode x = xmul.firstField();
                        if (x == null || x.opcode() != GETSTATIC) return;
                        AbstractNode ymul = mmn.find(IMUL, 1);
                        if (ymul == null) return;
                        FieldMemberNode y = ymul.firstField();
                        if (y == null || y.opcode() != GETSTATIC) return;
                        AbstractNode wmul = mmn.find(IMUL, 2);
                        if (wmul == null) return;
                        FieldMemberNode w = wmul.firstField();
                        if (w == null || w.opcode() != GETSTATIC) return;
                        AbstractNode hmul = mmn.find(IMUL, 3);
                        if (hmul == null) return;
                        FieldMemberNode h = hmul.firstField();
                        if (h == null || h.opcode() != GETSTATIC) return;
                        addHook(new FieldHook("menuX", x.fin()));
                        addHook(new FieldHook("menuY", y.fin()));
                        addHook(new FieldHook("menuWidth", w.fin()));
                        addHook(new FieldHook("menuHeight", h.fin()));
                    }
                }
            });
        }
    }

    private class SpellTargetFlags extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            if (block.count(ICONST_4) == 2 && block.count(IAND) == 1 && block.count(GETSTATIC) == 1 && block.count(GETFIELD) == 0) {
                block.follow().tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.desc().equals("I")) {
                            addHook(new FieldHook("spellTargetFlags", fmn.fin()));
                            for (Block b : updater.graphs().get(block.owner.owner).get(block.owner)) { //ew
                                b.tree().accept(new NodeVisitor() {
                                    @Override
                                    public void visitJump(JumpNode jn) {
                                        FieldMemberNode fmn = jn.firstField();
                                        if (fmn != null && fmn.opcode() == GETSTATIC && fmn.desc().equals("Z")
                                                && fmn.owner().equals("client")
                                                && resolve(fmn.owner() + "." + fmn.name()) == null
                                                && fmn.name().startsWith("k")) { //half hardcoded because bitch hook
                                            addHook(new FieldHook("spellSelected", fmn.fin()));
                                        }
                                    }
                                });
                            }
                            lock.set(true);
                        }
                    }
                });
            }
        }
    }

    private class Socket extends BlockVisitor {

        private final List<FieldMemberNode> fields = new ArrayList<>();

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals(desc("Socket")) && fmn.hasParent() && fmn.parent().hasParent() && fmn.parent().opcode() == INVOKEVIRTUAL && fmn.parent().parent().opcode() == ISTORE) {
                        MethodMemberNode mmn = (MethodMemberNode) fmn.parent();
                        if (mmn.owner().equals(clazz("Socket"))) {
                            fields.add(fmn);
                        }
                    }
                }
            });
        }

        @Override
        public void visitEnd() {
//            addHook(new FieldHook("socket", mostCommon(fields).fin()));
        }

        public <T> T mostCommon(List<T> list) {
            Map<T, Integer> map = new HashMap<>();

            for (T t : list) {
                Integer val = map.get(t);
                map.put(t, val == null ? 1 : val + 1);
            }

            Map.Entry<T, Integer> max = null;

            for (Map.Entry<T, Integer> e : map.entrySet()) {
                if (max == null || e.getValue() > max.getValue())
                    max = e;
            }

            return max != null ? max.getKey() : null;
        }
    }

    private class CursorUidsP1 extends BlockVisitor {

        private MethodMemberNode invoke = null;

        @Override
        public boolean validate() {
            return invoke == null;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    //length <= 2 to allow room for 1 dummy arg
                    if (mmn.desc().startsWith("(I") && mmn.desc().endsWith("V") && Type.getArgumentTypes(mmn.desc()).length <= 2) {
                        VariableNode fourthParam = mmn.firstVariable();
                        if (fourthParam != null && fourthParam.var() == 4) {
                            invoke = mmn;
                        }
                    }
                }
            });
        }
    }

    private class CursorUidsP2 extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitAny(AbstractNode n) {
                    if (n.opcode() == IASTORE) {
                        FieldMemberNode targetArray = (FieldMemberNode) n.layer(GETSTATIC);
                        if (targetArray != null) {
                            FieldMemberNode incremented = (FieldMemberNode) n.layer(ISUB, IMUL, PUTSTATIC);
                            if (incremented != null) {
                                FieldMemberNode check = (FieldMemberNode) incremented.layer(DUP, IADD, GETSTATIC);
                                if (check != null && check.key().equals(incremented.key())) {
                                    addHook(new FieldHook("onCursorCount", check.fin()));
                                    addHook(new FieldHook("onCursorUids", targetArray.fin()));
                                    lock.set(true);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class ScreenState extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            if (block.count(new InsnQuery(ISTORE)) > 0) {
                block.tree().accept(new NodeVisitor(this) {
                    public void visitField(FieldMemberNode fmn) {
                        if (fmn.opcode() == GETSTATIC && fmn.desc().equals("I")) {
                            VariableNode vn = (VariableNode) fmn.preLayer(IMUL, ISTORE);
                            if (vn != null) {
                                String name = null;
                                int var = vn.var();
                                if (var == 21) {
                                    name = "screenState";
                                } else if (var == 22) {
                                    name = "screenWidth";
                                } else if (var == 23) {
                                    name = "screenHeight";
                                }
                                if (name == null || hooks.containsKey(name))
                                    return;
                                added++;
                                addHook(new FieldHook(name, fmn.fin()));
                            }
                        }
                    }
                });
            }
        }
    }

    private class ScreenSizes extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 3;
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 334) {
                        FieldMemberNode set = (FieldMemberNode) nn.preLayer(IDIV, ISHL, IMUL, PUTSTATIC);
                        if (set != null) {
                            addHook(new FieldHook("screenZoom", set.fin()));
                            added++;
                        }
                    }
                }

                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC) {
                        List<AbstractNode> divs = fmn.layerAll(IMUL, IADD, IDIV);
                        if (divs == null || divs.size() != 2)
                            return;
                        for (AbstractNode idiv : divs) {
                            FieldMemberNode val = (FieldMemberNode) idiv.layer(IMUL, GETSTATIC);
                            if (val == null)
                                continue;
                            if (!hooks.containsKey("screenWidth")) {
                                addHook(new FieldHook("screenWidth", val.fin()));
                                added++;
                            } else if (!hooks.containsKey("screenHeight")) {
                                addHook(new FieldHook("screenHeight", val.fin()));
                                added++;
                            }
                        }
                    }
                }
            });
        }
    }

    private class Cycle extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKEVIRTUAL && mmn.desc().matches("\\(III(I|B|S|^)\\)V")) {
                        FieldMemberNode fmn = (FieldMemberNode) mmn.layer(IMUL, GETSTATIC);
                        if (fmn != null && fmn.owner().equals("client") && fmn.desc().equals("I")) {
                            addHook(new FieldHook("engineCycle", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class BufferBaseRead extends BlockVisitor {

        private final FieldHook bx = (FieldHook) hooks.get("baseX"), by = (FieldHook) hooks.get("baseY");
        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        /*
        Client.fieldCo.writeShort  (classDc.baseX      * -2071335905 + arg1, 1548147820);
        Client.fieldCo.writeLEShort(CalendarUtil.baseY * -1046564359 + arg2, 1937409312);
         */

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKEVIRTUAL) {
                        //I don't have PacketBuffer hooked and the method is called on a PacketBuffer object so....
                        ClassNode cn = updater.classnodes.get(mmn.owner());
                        if (cn == null || !cn.superName.equals(clazz("Buffer")))
                            return;
                        ArithmeticNode addition = mmn.firstOperation();
                        if (addition == null || addition.opcode() != IADD || addition.children() != 2) //ensure 2 subjects
                            return;
                        FieldMemberNode subjectA = (FieldMemberNode) addition.layer(IMUL, GETSTATIC);
                        VariableNode subjectB = addition.firstVariable();
                        if (subjectA != null && subjectB != null) {
                            if (subjectA.key().equals(bx.key())) {
                                updater.visitor("Buffer").addHook(new InvokeHook("writeShort", cn.name, mmn.name(), mmn.desc()));
                                added++;
                            } else if (subjectA.key().equals(by.key())) {
                                updater.visitor("Buffer").addHook(new InvokeHook("writeLEShort", cn.name, mmn.name(), mmn.desc()));
                                added++;
                                ;
                            }
                        }
                    }
                }
            });
        }
    }

    private class LowMemory extends BlockVisitor {

        private final List<String> keys = new ArrayList<>();

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.desc().equals("Ljava/lang/String;") && fmn.hasPrevious()) {
                        MethodMemberNode hostbase = (MethodMemberNode) fmn.layer(INVOKEVIRTUAL, INVOKEVIRTUAL);
                        if (hostbase == null || !hostbase.name().equals("getCodeBase"))
                            return;
                        for (Block block0 : updater.graphs().get(block.owner.owner).get(block.owner)) {
                            block0.tree().accept(new NodeVisitor() {
                                @Override
                                public void visitField(FieldMemberNode fmn) {
                                    if (fmn.desc().equals("Z"))
                                        keys.add(fmn.key());
                                }
                            });
                        }
                    }
                }
            });
        }

        public void visitEnd() {
            updater.graphs().forEach((cn, mn) -> mn.values().forEach(graph -> graph.forEach(block -> {
                block.tree().accept(new NodeVisitor() {
                    @Override
                    public void visitField(FieldMemberNode fmn) {
                        for (String key : keys) {
                            if (fmn.key().equals(key) && fmn.owner().equals("client"))
                                addHook(new FieldHook("lowMemory", fmn.fin()));
                        }
                    }
                });
            })));
        }
    }

    private class LoginState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        NumberNode nn = jn.firstNumber();
                        if (nn != null && nn.number() >= 3) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                            if (fmn == null || !fmn.owner().equals(((FieldHook) hooks.get("username")).clazz) || !fmn.desc().equals("I"))
                                return;
                            addHook(new FieldHook("loginState", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class Username extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    NumberNode len = jn.firstNumber();
                    MethodMemberNode lenCall = jn.firstMethod();
                    if (len == null || len.number() != 320 || lenCall == null || !lenCall.name().equals("length"))
                        return;
                    FieldMemberNode fmn = lenCall.firstField();
                    if (fmn != null) {
                        addHook(new FieldHook("username", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class Password extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitJump(JumpNode jn) {
                    NumberNode len = jn.firstNumber();
                    MethodMemberNode lenCall = jn.firstMethod();
                    if (len == null || len.number() != 20 || lenCall == null || !lenCall.name().equals("length"))
                        return;
                    FieldMemberNode fmn = lenCall.firstField();
                    if (fmn != null) {
                        addHook(new FieldHook("password", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class SocketState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitNumber(NumberNode nn) {
                    if (nn.number() == 15000 && nn.hasParent() && nn.parent() instanceof JumpNode) {
                        for (Block block : updater.graphs().get(nn.method().owner).get(nn.method())) {
                            block.tree().accept(new NodeVisitor() {
                                @Override
                                public void visitField(FieldMemberNode fmn) {
                                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("I")) {
                                        NumberNode set = fmn.firstNumber();
                                        if (set == null || set.opcode() != LDC)
                                            return;
                                        BigInteger decoder = updater.inverseVisitor.inverseFor(fmn.owner(), fmn.name());
                                        if (decoder != null && decoder.intValue() * set.number() != 250)
                                            return;
                                        addHook(new FieldHook("socketState", fmn.fin()));
                                        lock.set(true);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    private class HoveredTile extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            NodeTree tree = block.follow().tree();
            List<AbstractNode> layer = tree.layerAll(ISTORE, GETSTATIC);
            if (layer != null && layer.size() == 2) {
                tree.accept(new NodeVisitor() {

                    private boolean valid = false;

                    @Override
                    public void visit(AbstractNode n) {
                        if (n.opcode() == BALOAD) {
                            FieldMemberNode fmn = n.firstField();
                            if (fmn != null && fmn.desc().equals("[Z")) {
                                valid = true;
                            }
                        }
                    }

                    @Override
                    public void visitEnd() {
                        if (valid) {
                            layer.sort((a, b) -> {
                                VariableNode vA = (VariableNode) a.parent();
                                VariableNode vB = (VariableNode) b.parent();
                                return vA.var() - vB.var();
                            });
                            FieldMemberNode x = (FieldMemberNode) layer.get(0);
                            FieldMemberNode y = (FieldMemberNode) layer.get(1);
                            addHook(new FieldHook("hoveredRegionTileX", x.fin()));
                            addHook(new FieldHook("hoveredRegionTileY", y.fin()));
                            lock.set(true);
                        }
                    }
                });
            }
        }
    }

    private class PlayerActions extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                @Override
                public void visitField(FieldMemberNode fmn) {
                    String value = profiledStrings.get(fmn.key());
                    if (value != null && value.equals("Attack") && fmn.hasParent() && fmn.parent().opcode() == INVOKEVIRTUAL) {
                        FieldMemberNode actions = (FieldMemberNode) fmn.parent().layer(AALOAD, GETSTATIC);
                        if (actions == null || !actions.desc().equals("[Ljava/lang/String;"))
                            return;
                        addHook(new FieldHook("playerActions", actions.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class ExperienceHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            if (block.count(new InsnQuery(ISTORE)) == 4 && block.count(new InsnQuery(IASTORE)) == 3) {
                NodeTree root = block.tree();
                AbstractNode storeE = root.find(IASTORE, 0);
                if (storeE == null) return;
                FieldMemberNode experiences = storeE.firstField();
                if (experiences == null || experiences.opcode() != GETSTATIC) return;
                AbstractNode storeL = root.find(IASTORE, 1);
                if (storeL == null) return;
                FieldMemberNode levels = storeL.firstField();
                if (levels == null || levels.opcode() != GETSTATIC) return;
                AbstractNode storeRL = root.find(IASTORE, 2);
                if (storeRL == null) return;
                FieldMemberNode realLevels = storeRL.firstField();
                if (realLevels == null || realLevels.opcode() != GETSTATIC) return;
                addHook(new FieldHook("experiences", experiences.fin()));
                addHook(new FieldHook("levels", levels.fin()));
                addHook(new FieldHook("realLevels", realLevels.fin()));
                lock.set(true);
            }
        }
    }

    private class RunHooks extends BlockVisitor {

        private final Set<Block> blocks = new TreeSet<>();
        private final ArrayIterator<String> names = new ArrayIterator<>("energy", "weight");

        @Override
        public boolean validate() {
            return blocks.size() < 2;
        }

        @Override
        public void visit(final Block block) {
            if (block.owner.desc.matches("\\(" + desc("InterfaceComponent") + "I(I|B|S|^)\\)I")) {
                if (block.count(new MemberQuery(GETSTATIC, "client", "I")) > 0)
                    blocks.add(block);
            }
        }

        @Override
        public void visitEnd() {
            Iterator<Block> itr = blocks.iterator();
            for (int i = 0; i < names.size() && itr.hasNext(); i++)
                addHook(new FieldHook(names.next(), (FieldInsnNode) itr.next().get(GETSTATIC)));
        }
    }

    private class CameraXY extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    FieldMemberNode x = (FieldMemberNode) jn.layer(IAND, BALOAD, AALOAD, ISHR, IMUL, GETSTATIC);
                    if (x == null) return;
                    FieldMemberNode y = (FieldMemberNode) jn.layer(IAND, BALOAD, ISHR, IMUL, GETSTATIC);
                    if (y == null) return;
                    addHook(new FieldHook("cameraX", x.fin()));
                    addHook(new FieldHook("cameraY", y.fin()));
                    lock.set(true);
                }
            });
        }
    }

    private class CameraZ extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    NumberNode nn = jn.firstNumber();
                    if (nn != null && nn.number() == 800) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(ISUB, IMUL, GETSTATIC);
                        if (fmn != null) {
                            addHook(new FieldHook("cameraZ", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class CameraPY extends BlockVisitor {

        private int added = 0;

        @Override
        public boolean validate() {
            return added < 2;
        }

        @Override
        public void visit(final Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC) {
                        NumberNode nn = (NumberNode) fmn.layer(IMUL, IAND, D2I, DMUL, LDC);
                        if (nn != null) {
                            int mul = nn.number();
                            nn = (NumberNode) fmn.layer(IMUL, IAND, SIPUSH);
                            if (nn != null && nn.number() == 0x07FF) {
                                String name = "camera" + (mul > 0 ? "Pitch" : "Yaw");
                                if (hooks.containsKey(name)) return;
                                addHook(new FieldHook(name, fmn.fin()));
                            }
                        }
                    }
                }
            });
        }
    }

    private class MapHooks extends BlockVisitor {

        private final ArrayIterator<String> itr = new ArrayIterator<>("mapScale", "mapOffset");

        @Override
        public boolean validate() {
            return itr.hasNext();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPLE || jn.opcode() == IF_ICMPGE) {
                        int push = jn.opcode() == IF_ICMPLE ? 60 : -20;
                        NumberNode nn = jn.firstNumber();
                        if (nn != null && nn.number() == push) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                            if (fmn != null && fmn.desc().equals("I")) {
                                int nameIdx = jn.opcode() == IF_ICMPLE ? 0 : 1;
                                addHook(new FieldHook(itr.getAt(nameIdx), fmn.fin()));
                            }
                        }
                    }
                }
            });
        }
    }

    private class MapAngle extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("I")) {
                        if (fmn.layer(IMUL, IAND, IADD, IDIV) != null) {
                            hooks.put("mapRotation", new FieldHook("mapRotation", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class HintHooks extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.opcode() == INVOKESTATIC) {
                        if (mmn.child(0) != null && mmn.child(0).opcode() == IADD) {
                            if (mmn.child(1) != null && mmn.child(1).opcode() == IADD) {
                                if (mmn.child(2) != null && mmn.child(2).opcode() == IMUL) {
                                    AbstractNode xBlock = mmn.child(0).layer(ISHL, ISUB);
                                    AbstractNode yBlock = mmn.child(1).layer(ISHL, ISUB);
                                    FieldMemberNode type = (FieldMemberNode) mmn.child(2).first(GETSTATIC);
                                    if (xBlock != null && yBlock != null && type != null) {
                                        FieldMemberNode x = (FieldMemberNode) xBlock.layer(IMUL, GETSTATIC);
                                        FieldMemberNode baseX = x.parent().next().firstField();
                                        FieldMemberNode y = (FieldMemberNode) yBlock.layer(IMUL, GETSTATIC);
                                        FieldMemberNode baseY = y.parent().next().firstField();
                                        hooks.put("hintArrowX", new FieldHook("hintArrowX", x.fin()));
                                        hooks.put("baseX", new FieldHook("baseX", baseX.fin()));
                                        hooks.put("hintArrowY", new FieldHook("hintArrowY", y.fin()));
                                        hooks.put("baseY", new FieldHook("baseY", baseY.fin()));
                                        hooks.put("hintArrowType", new FieldHook("hintArrowType", type.fin()));
                                        lock.set(true);
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class SettingHooks extends BlockVisitor {

        private final ArrayIterator<String> itr = new ArrayIterator<>("varps", "tempVarps");

        @Override
        public boolean validate() {
            return itr.hasNext();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.opcode() == PUTSTATIC && fmn.desc().equals("[I")) {
                        NumberNode nn = (NumberNode) fmn.layer(NEWARRAY, SIPUSH);
                        if (nn != null && nn.number() == 2000) {
                            addHook(new FieldHook(itr.next(), fmn.fin()));
                        }
                    }
                }
            });
        }
    }

    private class InterfacePositionHooks extends BlockVisitor {

        private final ArrayIterator<String> itr = new ArrayIterator<>(
                "interfacePositionsX", "interfacePositionsY", "interfaceWidths", "interfaceHeights"
        );

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitMethod(MethodMemberNode mmn) {
                    if (mmn.desc().startsWith("(IIIII")) {
                        AbstractNode x = mmn.child(0);
                        if (x == null || x.opcode() != IALOAD) return;
                        AbstractNode y = mmn.child(1);
                        if (y == null || y.opcode() != IALOAD) return;
                        AbstractNode w = mmn.child(2);
                        if (w == null || w.opcode() != IALOAD) return;
                        AbstractNode h = mmn.child(3);
                        if (h == null || h.opcode() != IALOAD) return;
                        AbstractNode[] parents = {x, y, w, h};
                        FieldMemberNode[] fields = new FieldMemberNode[4];
                        for (int i = 0; i < parents.length; i++) {
                            FieldMemberNode fmn = parents[i].firstField();
                            if (fmn == null || !fmn.desc().equals("[I")) return;
                            for (int j = i - 1; j > 0; j--) {
                                if (fields[j].key().equals(fmn.key())) return;
                            }
                            fields[i] = fmn;
                        }
                        for (int i = 0; i < itr.size(); i++) {
                            hooks.put(itr.getAt(i), new FieldHook(itr.getAt(i), fields[i].fin()));
                        }
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class RenderRules extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    FieldMemberNode fmn = (FieldMemberNode) jn.layer(IAND, BALOAD, AALOAD, AALOAD, GETSTATIC);
                    if (fmn != null && fmn.desc().equals("[[[B")) {
                        addHook(new FieldHook("renderRules", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class TileHeights extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitVariable(VariableNode vn) {
                    FieldMemberNode fmn = (FieldMemberNode) vn.layer(ISUB, IALOAD, AALOAD, AALOAD, GETSTATIC);
                    if (fmn != null && fmn.desc().equals("[[[I")) {
                        hooks.put("tileHeights", new FieldHook("tileHeights", fmn.fin()));
                        lock.set(true);
                    }
                }
            });
        }
    }

    private class InterfaceNodes extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ASTORE) {
                        TypeNode tn = vn.firstType();
                        if (tn != null && tn.type().equals(clazz("InterfaceNode"))) {
                            FieldMemberNode fmn = (FieldMemberNode) vn.layer(INVOKEVIRTUAL, GETSTATIC);
                            if (fmn != null && fmn.desc().equals(desc("NodeTable"))) {
                                addHook(new FieldHook("interfaceNodes", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class InterfaceConfigs extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitVariable(VariableNode vn) {
                    if (vn.opcode() == ASTORE) {
                        TypeNode tn = vn.firstType();
                        if (tn != null && tn.type().equals(clazz("IntegerNode"))) {
                            FieldMemberNode fmn = (FieldMemberNode) vn.layer(INVOKEVIRTUAL, GETSTATIC);
                            if (fmn != null && fmn.desc().equals(desc("NodeTable"))) {
                                updater.getGraph(block.owner).forEach(b -> b.tree().accept(new NodeVisitor() {
                                    @Override
                                    public void visitField(FieldMemberNode f) {
                                        if (f.owner().equals(clazz("InterfaceComponent"))) {
                                            AbstractNode ireturn = f.preLayer(IMUL, IRETURN);
                                            if (ireturn != null) {
                                                addHook(new FieldHook("interfaceConfigs", fmn.fin()));
                                                updater.visitor("InterfaceComponent").addHook(new FieldHook("config", f.fin()));
                                                lock.set(true);
                                            }
                                        }
                                    }
                                }));

                            }
                        }
                    }
                }
            });
        }
    }

    private class NpcIndices extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor() {
                public void visitField(FieldMemberNode fmn) {
                    if (fmn.parent() != null && fmn.parent().opcode() == AALOAD) {
                        if (fmn.desc().equals("[" + desc("Npc"))) {
                            fmn = (FieldMemberNode) fmn.parent().layer(IALOAD, GETSTATIC);
                            if (fmn != null && fmn.desc().equals("[I")) {
                                addHook(new FieldHook("npcIndices", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class Plane extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visit(AbstractNode n) {
                    if (n.opcode() == AASTORE) {
                        FieldMemberNode fmn = (FieldMemberNode) n.layer(AALOAD, AALOAD, IMUL, GETSTATIC);
                        if (fmn != null && fmn.desc().equals("I")) {
                            hooks.put("plane", new FieldHook("plane", fmn.fin()));
                            lock.set(true);
                        }
                    }
                }
            });
        }
    }

    private class GameState extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        NumberNode nn = jn.firstNumber();
                        if (nn != null && nn.number() == 1000) {
                            FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                            if (fmn != null && fmn.owner().equals("client") && fmn.desc().equals("I")) {
                                hooks.put("gameState", new FieldHook("gameState", fmn.fin()));
                                lock.set(true);
                            }
                        }
                    }
                }
            });
        }
    }

    private class HintPlayerIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                        if (fmn != null && fmn.opcode() == GETSTATIC && fmn.owner().equals("client")) {
                            FieldMemberNode array = (FieldMemberNode) jn.layer(IALOAD, GETSTATIC);
                            if (array != null && array.desc().equals("[I")) {
                                VariableNode vn = array.nextVariable();
                                if (vn != null && vn.var() == 1) {
                                    addHook(new FieldHook("hintArrowPlayerIndex", fmn.fin()));
                                    lock.set(true);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private class HintNpcIndex extends BlockVisitor {

        @Override
        public boolean validate() {
            return !lock.get();
        }

        @Override
        public void visit(Block block) {
            block.tree().accept(new NodeVisitor(this) {
                public void visitJump(JumpNode jn) {
                    if (jn.opcode() == IF_ICMPNE) {
                        FieldMemberNode fmn = (FieldMemberNode) jn.layer(IMUL, GETSTATIC);
                        if (fmn != null && fmn.opcode() == GETSTATIC && fmn.owner().equals("client")) {
                            FieldMemberNode array = (FieldMemberNode) jn.layer(IALOAD, GETSTATIC);
                            if (array != null && array.desc().equals("[I") && getHookKey("npcIndices").equals(array.key())) {
                                AbstractNode an = array.next(ISUB);
                                if (an != null && an.layer(ILOAD) != null) {
                                    addHook(new FieldHook("hintArrowNpcIndex", fmn.fin()));
                                    lock.set(true);
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}
