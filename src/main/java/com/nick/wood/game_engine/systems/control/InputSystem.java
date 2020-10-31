package com.nick.wood.game_engine.systems.control;

import com.nick.wood.game_engine.event_bus.busses.GameBus;
import com.nick.wood.game_engine.gcs_model.gcs.Component;
import com.nick.wood.game_engine.gcs_model.gcs.Registry;
import com.nick.wood.game_engine.gcs_model.generated.components.ComponentType;
import com.nick.wood.game_engine.gcs_model.generated.components.ControllableObject;
import com.nick.wood.game_engine.gcs_model.generated.components.TransformObject;
import com.nick.wood.game_engine.gcs_model.systems.GcsSystem;
import com.nick.wood.game_engine.model.game_objects.GameObject;
import com.nick.wood.game_engine.model.input.ControllerState;
import com.nick.wood.game_engine.systems.GESystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class InputSystem implements GcsSystem<ControllableObject> {

	private final ArrayList<Control> controls = new ArrayList<>();
	private final ControllerState controllerState;
	private final DirectTransformController directTransformController;

	public InputSystem(ControllerState controllerState, GameBus gameBus) {
		this.controllerState = controllerState;

		GameManagementInputController gameManagementInputController = new GameManagementInputController(gameBus);
		controls.add(gameManagementInputController);

		this.directTransformController = new DirectTransformController(controllerState);
	}

	@Override
	public void update(long time, HashSet<ControllableObject> controllableObjects, Registry registry) {

		// game management control first, probably move this to GCS next
		for (Control control : controls) {
			control.update(controllerState);
		}

		// iterate over controllableObjects and find transform they lie under
		for (ControllableObject controllableObject : controllableObjects) {

			if (controllableObject.getParent() != null && controllableObject.getParent().getComponentType().equals(ComponentType.TRANSFORM)) {
				TransformObject transformObject = (TransformObject) controllableObject.getParent();
				directTransformController.update(controllableObject, transformObject, registry);
			}

		}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.CONTROLLABLE;
	}
}
