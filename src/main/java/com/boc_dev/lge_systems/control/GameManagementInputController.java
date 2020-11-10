package com.boc_dev.lge_systems.control;

import com.boc_dev.event_bus.event_data.ManagementEventData;
import com.boc_dev.event_bus.event_types.ManagementEventType;
import com.boc_dev.event_bus.events.ManagementEvent;
import com.boc_dev.event_bus.interfaces.Bus;

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
