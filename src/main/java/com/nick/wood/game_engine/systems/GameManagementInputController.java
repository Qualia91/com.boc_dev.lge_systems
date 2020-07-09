package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.event_bus.event_data.ManagementEventData;
import com.nick.wood.game_engine.event_bus.event_types.ManagementEventType;
import com.nick.wood.game_engine.event_bus.events.ManagementEvent;
import com.nick.wood.game_engine.event_bus.interfaces.Bus;
import com.nick.wood.game_engine.model.game_objects.GameObject;
import com.nick.wood.game_engine.model.input.ControllerState;

import java.util.ArrayList;
import java.util.HashMap;

public class GameManagementInputController implements GESystem {

	private final Bus bus;
	private ControllerState controllerState;

	public GameManagementInputController(Bus bus) {
		this.bus = bus;
	}

	public void update(HashMap<String, ArrayList<GameObject>> layeredGameObjectsMap) {
		// ESC
		if (controllerState.getKeys()[256]) {
			bus.dispatch(new ManagementEvent(new ManagementEventData(), ManagementEventType.SHUTDOWN));
		}
	}

	public void setUserInput(ControllerState controllerState) {
		this.controllerState = controllerState;
	}
}
