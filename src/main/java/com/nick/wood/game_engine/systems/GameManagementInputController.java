package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.event_bus.event_data.ManagementEventData;
import com.nick.wood.game_engine.event_bus.event_types.ManagementEventType;
import com.nick.wood.game_engine.event_bus.events.ManagementEvent;
import com.nick.wood.game_engine.event_bus.interfaces.Bus;
import com.nick.wood.game_engine.model.input.ControllerState;

public class GameManagementInputController implements Control {

	private final Bus bus;

	public GameManagementInputController(Bus bus) {
		this.bus = bus;
	}

	public void update(ControllerState controllerState) {
		// ESC
		if (controllerState.getKeys()[256]) {
			bus.dispatch(new ManagementEvent(new ManagementEventData(), ManagementEventType.SHUTDOWN));
		}
	}
}
