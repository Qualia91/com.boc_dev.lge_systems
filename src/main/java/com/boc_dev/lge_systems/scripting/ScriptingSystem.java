package com.boc_dev.lge_systems.scripting;

import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.ScriptObject;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.lge_scripting.LuaScript;

import java.util.HashSet;

public class ScriptingSystem implements GcsSystem<ScriptObject> {

	private final LuaScript luaScript;

	public ScriptingSystem(String scriptsFolderLocation) {
		this.luaScript = new LuaScript(scriptsFolderLocation);
	}

	@Override
	public void update(long timeStep, HashSet<ScriptObject> scriptObjects, Registry registry) {

		for (ScriptObject scriptObject : scriptObjects) {

			luaScript.call(scriptObject.getScript(), scriptObject, registry);

		}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.SCRIPT;
	}
}
