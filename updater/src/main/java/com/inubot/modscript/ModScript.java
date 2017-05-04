package com.inubot.modscript;

import com.inubot.visitor.GraphVisitor;
import com.inubot.modscript.hook.Hook;
import org.objectweb.asm.tree.ClassNode;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Map;

public class ModScript {

    private static final int MAGIC = 0xFADFAD;

    public static void write(String file, String hash, String type, Collection<GraphVisitor> visitors) throws Exception {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            out.writeInt(MAGIC);
            out.writeUTF(type);
            out.writeUTF(hash);
            out.writeInt(visitors.size());
            for (GraphVisitor gv : visitors) {
                ClassNode cn = gv.cn;
                out.writeBoolean(cn != null);
                if (cn == null) {
                    continue;
                }
                out.writeUTF(Crypto.encrypt(cn.name));
                out.writeUTF(Crypto.encrypt(gv.id()));
                out.writeInt(gv.hooks.size());
                for (Hook hook : gv.hooks.values()) {
                    hook.writeToEncryptedStream(out);
                }
            }
            out.flush();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to write modscript", e);
        }
    }
}
