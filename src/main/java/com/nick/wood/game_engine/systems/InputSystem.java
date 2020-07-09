package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.model.game_objects.GameObject;
import com.nick.wood.game_engine.model.input.ControllerState;

import java.util.ArrayList;
import java.util.HashMap;

public class InputSystem implements GESystem {

	private final ArrayList<Control> controls = new ArrayList<>();
	private final ControllerState controllerState;

	public InputSystem(ControllerState controllerState) {
		this.controllerState = controllerState;
	}

	public void addControl(Control control) {
		controls.add(control);
	}

	@Override
	public void update(HashMap<String, ArrayList<GameObject>> layeredGameObjectsMap, long timeSinceStart) {

		for (Control control : controls) {
			control.update(controllerState);
		}

	}
}
