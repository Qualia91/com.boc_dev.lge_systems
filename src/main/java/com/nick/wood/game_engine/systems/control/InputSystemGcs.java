package com.nick.wood.game_engine.systems.control;

import com.nick.wood.game_engine.gcs_model.gcs.Registry;
import com.nick.wood.game_engine.gcs_model.generated.components.ComponentType;
import com.nick.wood.game_engine.gcs_model.generated.components.TransformObject;
import com.nick.wood.game_engine.gcs_model.systems.GcsSystem;
import com.nick.wood.game_engine.model.game_objects.GameObject;
import com.nick.wood.game_engine.model.input.ControllerState;
import com.nick.wood.game_engine.systems.GESystem;

import java.util.ArrayList;
import java.util.HashMap;

public class InputSystemGcs implements GcsSystem<TransformObject> {

	private final ArrayList<Control> controls = new ArrayList<>();
	private final ControllerState controllerState;

	public InputSystemGcs(ControllerState controllerState) {
		this.controllerState = controllerState;
	}

	public void addControl(Control control) {
		controls.add(control);
	}

	@Override
	public void update(long time, ArrayList<TransformObject> transformComponents, Registry registry) {
		for (Control control : controls) {
			control.update(controllerState);
		}
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.TRANSFORM;
	}
}
