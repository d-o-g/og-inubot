package com.inubot.modscript.hook;

import com.inubot.modscript.Crypto;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.DataOutputStream;
import java.io.IOException;

public class InvokeHook extends Hook {

    public String clazz, method, desc;
    public int predicate = Integer.MAX_VALUE;
    public Class<?> predicateType = int.class;

    public InvokeHook(String name, String clazz, String method, String desc) {
        super(name);
        this.clazz = clazz;
        this.method = method;
        this.desc = desc;
    }

    public InvokeHook(String name, MethodNode mn) {
        this(name, mn.owner.name, mn.name, mn.desc);
    }

    public InvokeHook(String name, MethodInsnNode min) {
        this(name, min.owner, min.name, min.desc);
    }

    public void setOpaquePredicate(int predicate, Class<?> predicateType) {
        this.predicate = predicate;
        this.predicateType = predicateType;
    }

    @Override
    public byte getType() {
        return Type.INVOKE;
    }

    @Override
    public String getOutput() {
        String desc0 = org.objectweb.asm.Type.getType(desc).getReturnType().getClassName();
        org.objectweb.asm.Type[] args = org.objectweb.asm.Type.getArgumentTypes(desc);
        String params = "(";
        int i = 0;
        for (org.objectweb.asm.Type arg : args) {
            String ok = arg.getClassName();
            if (ok.lastIndexOf('.') != -1) {
                ok = ok.substring(ok.lastIndexOf('.') + 1);
            }
            params += ok;
            if (++i != args.length) {
                params += ", ";
            }
        }
        params += ")";
        StringBuilder sb = new StringBuilder().append("¤ ").append(desc0).append(" ").append(name).append(" is ")
                .append(clazz).append(".").append(method).append(params);
        if (predicate != Integer.MAX_VALUE) {
            sb.append(" [").append(predicate).append("]");
        }
        return sb.toString();
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(clazz);
        out.writeUTF(method);
        out.writeUTF(desc);
        out.writeInt(predicate);
        out.writeUTF(predicateType == int.class ? "I" : (predicateType == byte.class ? "B" : "S"));
    }

    @Override
    protected void writeEncryptedData(DataOutputStream out) throws IOException {
        out.writeUTF(Crypto.encrypt(name));
        out.writeUTF(Crypto.encrypt(clazz));
        out.writeUTF(Crypto.encrypt(method));
        out.writeUTF(Crypto.encrypt(desc));
        out.writeInt(predicate);
        out.writeUTF(Crypto.encrypt(predicateType == int.class ? "I" : (predicateType == byte.class ? "B" : "S")));
    }

    public String key() {
        return clazz + "." + method + desc;
    }
}