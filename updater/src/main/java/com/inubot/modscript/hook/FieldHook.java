package com.inubot.modscript.hook;

import com.inubot.modscript.Crypto;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.DataOutputStream;
import java.io.IOException;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author Tyler Sedlar
 */
public class FieldHook extends Hook {

    public String clazz;
    public String field;
    public String fieldDesc;
    public boolean isStatic;

    public int multiplier = 0;

    public FieldHook(String name, String clazz, String field, String fieldDesc, boolean isStatic) {
        super(name);
        this.clazz = clazz;
        this.field = field;
        this.fieldDesc = fieldDesc;
        this.isStatic = isStatic;
    }

    public FieldHook(String name, String clazz, String field, String fieldDesc) {
        this(name, clazz, field, fieldDesc, false);
    }

    public FieldHook(String name, FieldInsnNode fin) {
        this(name, fin.owner, fin.name, fin.desc, fin.opcode() == GETSTATIC || fin.opcode() == PUTSTATIC);
    }

    public FieldHook(String name, FieldNode fn) {
        this(name, fn.owner.name, fn.name, fn.desc, (fn.access & ACC_STATIC) > 0);
    }

    @Override
    public byte getType() {
        return Hook.Type.FIELD;
    }

    @Override
    public String getOutput() {
        StringBuilder output = new StringBuilder();
        String desc = org.objectweb.asm.Type.getType(fieldDesc).getClassName();
        int idx = desc.lastIndexOf('.');
        //TODO maybe replace obfuscated names with known names? e.g. xx -> Node
        output.append("∙ ").append(desc).append(" ").append(name).append(" is ").append(clazz).append('.').append(field);
        if (multiplier != 0) {
            output.append(" * ").append(multiplier);
        }
        return output.toString();
    }

    @Override
    protected void writeData(DataOutputStream out) throws IOException {
        out.writeUTF(name);
        out.writeUTF(clazz);
        out.writeUTF(field);
        out.writeUTF(fieldDesc);
        out.writeBoolean(isStatic);
        out.writeInt(multiplier);
    }

    @Override
    protected void writeEncryptedData(DataOutputStream out) throws IOException {
        out.writeUTF(Crypto.encrypt(name));
        out.writeUTF(Crypto.encrypt(clazz));
        out.writeUTF(Crypto.encrypt(field));
        out.writeUTF(Crypto.encrypt(fieldDesc));
        out.writeBoolean(isStatic);
        out.writeInt(multiplier);
    }

    public String key() {
        return clazz + "." + field;
    }
}
