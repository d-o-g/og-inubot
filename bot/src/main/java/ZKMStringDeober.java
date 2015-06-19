import jdk.internal.org.objectweb.asm.*;
import jdk.internal.org.objectweb.asm.tree.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.ZipEntry;

public class ZKMStringDeober implements Opcodes {

    public static final int TYPE_METHOD_POOL = 0;  // For string within methods of the class
    public static final int TYPE_MULTI_CASE = 1;
    public static final int TYPE_SINGLE_CASE = 2;

    public final HashMap<String, ClassNode> classNodes = new HashMap<>();

    public ZKMStringDeober() throws IOException {

        loadClasses(new File("osbuddy.jar"));

        for (ClassNode cn : classNodes.values()) {

            for (MethodNode mn : cn.methods) {
                try {
                    Deober deober = null;
                    ArrayDeque<AbstractInsnNode> nodes = new ArrayDeque<>();
                    for (AbstractInsnNode ain : mn.instructions.toArray()) {
                        if (ain.getOpcode() == LDC && ain.getNext().getOpcode() == JSR) {

                            String msg = (String) ((LdcInsnNode) ain).cst;
                            LabelNode stub = ((JumpInsnNode) ain.getNext()).label;

                            if (deober == null) {
                                deober = profileDeober(stub);
                                System.out.println("Deober Found @ " +
                                        cn.name + "." + mn.name + " : " + deober.toString());
                            } else if (deober.type != TYPE_METHOD_POOL) throw new Error();

                            String decoded = deober.deob(msg);
                            System.out.println("    -> " + decoded);
                            ((LdcInsnNode) ain).cst = decoded;
                            mn.instructions.remove(ain.getNext());
                        } else {


                            if (ain.getNext() != null &&
                                    ain.getNext().getOpcode() == LDC) {
                                int k = ain.getNext().getNext().getOpcode();

                                if ((k >= 2 && k <= 8 || k == BIPUSH) &&
                                        ain.getNext().getNext().getNext().getOpcode() == GOTO) {

                                    if (deober == null) {
                                        LabelNode stub = ((JumpInsnNode) ain.getNext().getNext().getNext()).label;
                                        deober = profileDeober(stub);
                                        System.out.println("Deober Found: @ " +
                                                cn.name + "." + mn.name + " : " + deober.toString());
                                    } else if (deober.type == TYPE_METHOD_POOL) throw new Error();


                                    LdcInsnNode msg = (LdcInsnNode) ain.getNext();
                                    String decoded = deober.deob((String) msg.cst);
                                    msg.cst = decoded;


                                    AbstractInsnNode ci = msg.getNext();
                                    int case_index;
                                    if (ci instanceof IntInsnNode) {
                                        case_index = ((IntInsnNode) ci).operand;
                                    } else case_index = (ci.getOpcode() - 3);

                                    case_index++;

                                    if (case_index < 0 || case_index > deober.numCases)
                                        throw new Error("Case Index Bounds: " + case_index);

                                    nodes.add(msg.getNext());

                      /*  mn.instructions.remove( msg.getNext() ); //table case value
                        mn.instructions.remove( msg.getNext().getNext() ); //deob invoke*/

                                    System.out.println("    [" + case_index + "] -> " + decoded);


                                }
                            }
                        }
                    }

                    if (deober != null) {
                        deober.remove(mn);
                        for (AbstractInsnNode node : nodes) {
                            mn.instructions.remove(node.getNext());
                            mn.instructions.remove(node);
                        }
                    }



                } catch (Throwable e) {
                    System.err.println("Failed @ " + cn.name + "." + mn.name);
                    e.printStackTrace();
                }
            }
        }

        dumpJar(new File("./Processed.jar"));

        System.out.println("Done");

    }

    public static void main(String[] args) throws IOException {
        new ZKMStringDeober();
    }

    public static String deob(String msg, byte[] keys) {
        char[] var10000 = msg.toCharArray();
        int var10002 = var10000.length;
        int cycle = 0;
        char[] var10001 = var10000;
        int var5 = var10002;
        if (var10002 > 1) {
            var10001 = var10000;
            var5 = var10002;
            if (var10002 <= cycle) {
                return (new String(var10000)).intern();
            }
        }

        do {
            char[] deobed = var10001;
            int i = cycle;

            while (true) {
                char cur = deobed[i];
                byte key;
                switch (cycle % 5) {
                    case 0:
                        key = keys[0];
                        break;
                    case 1:
                        key = keys[1];
                        break;
                    case 2:
                        key = keys[2];
                        break;
                    case 3:
                        key = keys[3];
                        break;
                    default:
                        key = keys[4];
                }

                deobed[i] = (char) (cur ^ key);
                ++cycle;
                if (var5 != 0) {
                    break;
                }

                i = var5;
                deobed = var10001;
            }
        } while (var5 > cycle);

        return (new String(var10001)).intern();
    }

    public AbstractInsnNode[] getPoper(AbstractInsnNode cur) { //Swap Pop Swap
        if (cur == null) return null;
        if (cur.getOpcode() == SWAP &&
                cur.getNext().getOpcode() == POP &&
                cur.getNext().getNext().getOpcode() == SWAP) {
            AbstractInsnNode[] ret = new AbstractInsnNode[4];
            ret[0] = cur; //Swap
            ret[1] = cur.getNext(); //Pop
            ret[2] = cur.getNext().getNext(); //Swap
            ret[3] = cur.getNext().getNext().getNext(); // THE Poper (Table or Forced)
            return ret;
        }
        return null;
    }

    public Deober profileDeober(LabelNode stub) {

        byte[] keys = null;
        ArrayDeque<AbstractInsnNode> code = new ArrayDeque<>();
        AbstractInsnNode next = stub.getNext();
        int type = -1, num = -1;
        List<LabelNode> cases = null;
        while ((next = next.getNext()) != null) {

            if (next.getOpcode() == RET) {
                code.add(next);
                type = TYPE_METHOD_POOL;
                break;
            }

            AbstractInsnNode[] poper0 = getPoper(next);

            if (poper0 != null) {


                AbstractInsnNode poper = poper0[3];

                if (poper.getOpcode() == POP) { // Single case

                    type = TYPE_SINGLE_CASE;
                    num = 1;
                } else if (poper.getOpcode() == TABLESWITCH) { // Multi Case


                    type = TYPE_MULTI_CASE;
                    cases = ((TableSwitchInsnNode) poper).labels;
                    num = cases.size();
                } else throw new Error("Unhandled Type");

                code.addAll(Arrays.asList(poper0));
                break;
            }


            code.add(next);
            if (keys == null && next instanceof TableSwitchInsnNode) {
                TableSwitchInsnNode table = (TableSwitchInsnNode) next;
                if (table.labels == null) throw new Error("No blocks");
                if (table.dflt == null) throw new Error("No default block");



                if (table.labels.size() != 4)
                    throw new Error("Expected table size of 4 got " + table.labels.size());
                keys = new byte[5];
                int k = 0;
                for (LabelNode block : table.labels)
                    keys[k++] = pullKey(block);
                keys[k] = pullKey(table.dflt);
            }

        }

        if (type == -1) throw new Error("Unknown Type");
        if (next == null) throw new Error("Failed to isolate deober");
        if (keys == null) throw new Error("Failed to pull keys from table");



        return new Deober(keys, stub, code, type, cases, num);
    }

    private byte pullKey(LabelNode block) {
        int nop = block.getNext().getOpcode();
        if (nop != BIPUSH && !(nop <= 8 && nop >= 2))
            throw new Error("Unexpected block stack (" + nop + ")");
        switch (nop) {
            case BIPUSH:
                IntInsnNode key0 = (IntInsnNode) block.getNext();
                return (byte) key0.operand;
            default:
                return (byte) (nop - 3);
        }
    }

    public void loadClasses(File file) throws IOException {
        JarFile jar = new JarFile(file);
        Enumeration<?> en = jar.entries();
        while (en.hasMoreElements()) {
            JarEntry entry = (JarEntry) en.nextElement();
            if (entry.getName().endsWith(".class") &&
                    !entry.getName().contains("sun")) {
                ClassReader reader = new ClassReader(jar.getInputStream(entry));
                ClassNode node = new ClassNode();
                reader.accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                classNodes.put(node.name, node);
            }
        }

        System.out.println("Loaded " + classNodes.size() + " classes.");

    }

    private void dumpJar(File loc) {
        try (JarOutputStream out = new JarOutputStream(
                new FileOutputStream(loc))) {
            for (ClassNode cn : classNodes.values()) {
                JarEntry entry = new JarEntry(
                        cn.name.replace('.', '/') + ".class");
                out.putNextEntry(entry);
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                cn.accept(writer);
                out.write(writer.toByteArray());
                out.closeEntry();
            }
            out.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
            out.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Jar Dumped @ " + loc);

    }

    class Deober {

        byte[] keys;
        LabelNode stub;
        ArrayDeque<AbstractInsnNode> code;
        int type;
        List<LabelNode> cases;
        int numCases = -1;

        public Deober(byte[] keys, LabelNode stub,
                      ArrayDeque<AbstractInsnNode> code,
                      int type,
                      List<LabelNode> cases,
                      int numCases) {
            this.keys = keys;
            this.stub = stub;
            this.code = code;
            this.type = type;
            this.numCases = numCases;
            if (type != TYPE_METHOD_POOL && numCases == -1)
                throw new Error("Invalid case count");
            if (type == TYPE_MULTI_CASE && cases == null)
                throw new Error("cases == null");
        }

        public void remove(MethodNode parent) {

            AbstractInsnNode next = stub; //Remove tailing common labels
            while ((next = next.getPrevious()) instanceof LabelNode)
                parent.instructions.remove(next);
            next = stub; //Remove leading common labels
            while ((next = next.getNext()) instanceof LabelNode)
                parent.instructions.remove(next);

            //remove stack instructions
            for (AbstractInsnNode e : code) {
                parent.instructions.remove(e);
            }

            //remove sub-table cases
            if (cases != null) {
                for (LabelNode case0 : cases) {
                    parent.instructions.remove(case0);
                }
            }
        }

        public String deob(String msg) {
            return ZKMStringDeober.deob(msg, keys);
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[Type=").append(type).append(",");
            builder.append("Cases=").append(numCases).append(",");
            builder.append("Keys=").append(Arrays.toString(keys)).append("]");
            return builder.toString();
        }
    }


}
