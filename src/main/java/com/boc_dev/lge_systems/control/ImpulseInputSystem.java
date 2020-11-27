package com.boc_dev.lge_systems.control;

import com.boc_dev.event_bus.busses.GameBus;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.ControllableObject;
import com.boc_dev.lge_model.generated.components.ImpulseControllableObject;
import com.boc_dev.lge_model.generated.components.TransformObject;
import com.boc_dev.lge_model.systems.GcsSystem;

import java.util.ArrayList;
import java.util.HashSet;

public class ImpulseInputSystem implements GcsSystem<ImpulseControllableObject> {

	private final ArrayList<Control> controls = new ArrayList<>();
	private final ControllerState controllerState;
	private final ImpulseTransformController impulseTransformController;

	public ImpulseInputSystem(ControllerState controllerState, GameBus gameBus) {
		this.controllerState = controllerState;

		GameManagementInputController gameManagementInputController = new GameManagementInputController(gameBus);
		controls.add(gameManagementInputController);

		this.impulseTransformController = new ImpulseTransformController(controllerState);
	}

	@Override
	public void update(long time, HashSet<ImpulseControllableObject> controllableObjects, Registry registry) {

		// game management control first, probably move this to GCS next
		for (Control control : controls) {
			control.update(controllerState);
		}

		// iterate over controllableObjects and find transform they lie under
		for (ImpulseControllableObject controllableObject : controllableObjects) {

			if (controllableObject.getParent() != null && controllableObject.getParent().getComponentType().equals(ComponentType.TRANSFORM)) {
				TransformObject transformObject = (TransformObject) controllableObject.getParent();
				impulseTransformController.update(controllableObject);
			}

		}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.IMPULSECONTROLLABLE;
	}
}
