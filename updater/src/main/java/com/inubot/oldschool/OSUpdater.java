package com.inubot.oldschool;

import com.inubot.Updater;
import com.inubot.oldschool.analysis.Character;
import com.inubot.visitor.GraphVisitor;
import com.inubot.util.Configuration;
import com.inubot.oldschool.analysis.*;
import org.objectweb.asm.commons.cfg.Block;
import org.objectweb.asm.commons.cfg.BlockVisitor;
import org.objectweb.asm.commons.cfg.graph.FlowGraph;
import org.objectweb.asm.commons.cfg.tree.NodeVisitor;
import org.objectweb.asm.commons.cfg.tree.node.NumberNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarFile;

/**
 * @author Tyler Sedlar
 */
public class OSUpdater extends Updater {

    private static final boolean server =  new File("/usr/share/nginx/html/data/").exists();

    private static GraphVisitor[] createVisitors() {
        return new GraphVisitor[]{
                new Node(), new CacheNode(), new Renderable(), new NodeTable(),
                new Cache(), new NodeDeque(), new Queue(), new Tile(), new Model(),
                new AnimationSequence(), new Character(), new NpcDefinition(), new Npc(),
                new Player(), new Item(), new ItemDefinition(), new InteractableEntity(),
                new FloorDecoration(), new Boundary(), new BoundaryDecoration(),
                new ObjectDefinition(), new Region(), new Canvas(), new WidgetNode(),
                new ItemTable(), new CollisionMap(), new Buffer(), new Widget(),
                new Sprite(), new Varps(), new VarpBit(), new Font(), new Client()
        };
    }

    @Override
    public String getType() {
        return "oldschool";
    }

    @Override
    public String getHash() {
        try (JarFile jar = new JarFile(file)) {
            return Integer.toString(jar.getManifest().hashCode());
        } catch (IOException | NullPointerException e) {
            return file.getName().replace(".jar", "");
        }
    }

    @Override
    public String getAccessorPrefix() {
        return "com/inubot/client/natives/oldschool/RS";
    }

    @Override
    public String getWrapperPrefix() {
        return "com/inubot/api/oldschool/";
    }

    @Override
    public String getModscriptLocation() {
        return server ? "/usr/share/nginx/html/data/oldschool.dat" : Configuration.CACHE + "/oldschool.dat";
    }

    @Override
    public int getRevision(Map<ClassNode, Map<MethodNode, FlowGraph>> graphs) {
        ClassNode client = classnodes.get("client");
        MethodNode init = client.getMethodByName("init");
        FlowGraph graph = graphs.get(client).get(init);
        final AtomicInteger revision = new AtomicInteger(0);
        for (Block block : graph) {
            new BlockVisitor() {
                public boolean validate() {
                    return revision.get() == 0;
                }

                public void visit(Block block) {
                    block.tree().accept(new NodeVisitor(this) {
                        public void visitNumber(NumberNode nn) {
                            if (nn != null && nn.opcode() == SIPUSH) {
                                if ((nn = nn.nextNumber()) != null && nn.opcode() == SIPUSH) {
                                    if ((nn = nn.nextNumber()) != null) {
                                        revision.set(nn.number());
                                    }
                                }
                            }
                        }
                    });
                }
            }.visit(block);
        }
        return revision.get();
    }

    static {
        Configuration.setup();
    }

    public OSUpdater(File file, GraphVisitor[] visitors, boolean closeOnOld) throws Exception {
        super(file, visitors, closeOnOld);
    }

    public OSUpdater(File file, boolean closeOnOld) throws Exception {
        this(file, createVisitors(), closeOnOld);
    }

    public static void main(String[] args) throws Exception {
        Updater updater = new OSUpdater(null, false);
        updater.print = true;
        updater.run();
        updater.archive.write(new File("dump.jar"));
    }
}
