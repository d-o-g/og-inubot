package com.inubot.bundledscripts.mad.util.wrappers;

import com.inubot.bundledscripts.mad.util.interfaces.Condition;
import com.inubot.bundledscripts.mad.util.interfaces.Module;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Created by Mad on 6/30/2015.
 */
public class ModuleWrapper {

    private LinkedHashMap<Condition, ArrayList<Module>> arrayListMap = new LinkedHashMap<>();

    public ArrayList addModuleList(Condition condition) {
        return arrayListMap.put(condition, new ArrayList<>());
    }

    private ArrayList<Module> getMapKey(int key) {
        return arrayListMap.get(arrayListMap.keySet().toArray()[key]);
    }

    public void submit(final Module module, int key) {
        if (getMapKey(key) != null) getMapKey(key).add(module);
    }

    public Condition getValidCondition() {
        return arrayListMap.keySet().stream().filter(Condition::check).findFirst().get();
    }

    public Optional<Module> getValidModule() {
        return arrayListMap.get(getValidCondition()).stream().filter(Module::validate).findFirst();
    }

    public void execute() {
        getValidModule().ifPresent(Module::execute);
    }

    public int getValidListSize() {
        return arrayListMap.get(getValidCondition()).size();
    }

    public String getStatus() {
        return getValidModule().getClass().getSimpleName();
    }

}
