package com.inubot.bot.modscript.transform;

import com.inubot.bot.modscript.asm.ClassStructure;
import jdk.internal.org.objectweb.asm.Opcodes;

import java.util.Map;

public interface Transform extends Opcodes {

    String PACKAGE = "com/inubot/client/natives/";

    void inject(Map<String, ClassStructure> classes);
}
