package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.model.game_objects.GameObject;

import java.util.ArrayList;
import java.util.HashMap;

public interface GESystem {
	void update(HashMap<String, ArrayList<GameObject>> layeredGameObjectsMap);
}
