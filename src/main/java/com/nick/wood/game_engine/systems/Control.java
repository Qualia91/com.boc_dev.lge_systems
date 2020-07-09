package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.model.input.ControllerState;

public interface Control {
	void update(ControllerState controllerState);
}
