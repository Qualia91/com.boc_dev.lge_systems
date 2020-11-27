package com.boc_dev.lge_systems.control;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.generated.components.*;
import com.boc_dev.maths.objects.QuaternionF;
import com.boc_dev.maths.objects.vector.Vec3d;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.HashMap;

public class ImpulseTransformController {

	private final ControllerState controllerState;
	private KeyMapping keyMapping = new KeyMapping();
	private HashMap<String, Vec3f> translationVectorMap = new HashMap<>();

	public ImpulseTransformController(ControllerState controllerState) {
		this.controllerState = controllerState;
		translationVectorMap.put("leftLinear", Vec3f.X.neg());
		translationVectorMap.put("rightLinear", Vec3f.X);
		translationVectorMap.put("forwardLinear", Vec3f.Z.neg());
		translationVectorMap.put("backLinear", Vec3f.Z);
		translationVectorMap.put("upLinear", Vec3f.Y);
		translationVectorMap.put("downLinear", Vec3f.Y.neg());

		translationVectorMap.put("pitchUp", Vec3f.X.neg());
		translationVectorMap.put("pitchDown", Vec3f.X);
		translationVectorMap.put("rollLeft", Vec3f.Z);
		translationVectorMap.put("rollRight", Vec3f.Z.neg());
		translationVectorMap.put("yawLeft", Vec3f.Y);
		translationVectorMap.put("yawRight", Vec3f.Y.neg());
	}

	public void changeDefaultKeyMapping(KeyMapping keyMapping) {
		this.keyMapping = keyMapping;
	}

	public void update(ControllableObject controllableObject, TransformObject transformObject) {
		if (controllerState != null) {

			// get rigid body object. It will be up one and then down one (same level as this)
			for (Component child : controllableObject.getParent().getChildren()) {
				if (child.getComponentType().equals(ComponentType.IMPULSE)) {

					ImpulseObject impulseObject = (ImpulseObject) child;

					ImpulseObject.ImpulseUpdater updater = impulseObject.getUpdater();

					// zero the impulse
					updater.setLinearVelocityImpulse(Vec3d.ZERO);

					if (controllableObject.getEnableMove()) {
						Vec3f impulse = Vec3f.ZERO;

						// W
						if (controllerState.getKeys()[keyMapping.getDown()]) {
							impulse = impulse.add(translationVectorMap.get("downLinear"));
						}
						// A
						if (controllerState.getKeys()[keyMapping.getBack()]) {
							impulse = impulse.add(translationVectorMap.get("backLinear"));
						}
						// S
						if (controllerState.getKeys()[keyMapping.getUp()]) {
							impulse = impulse.add(translationVectorMap.get("upLinear"));
						}
						// D
						if (controllerState.getKeys()[keyMapping.getForward()]) {
							impulse = impulse.add(translationVectorMap.get("forwardLinear"));
						}
						// Q
						if (controllerState.getKeys()[keyMapping.getLeft()]) {
							impulse = impulse.add(translationVectorMap.get("leftLinear"));
						}
						// E
						if (controllerState.getKeys()[keyMapping.getRight()]) {
							impulse = impulse.add(translationVectorMap.get("rightLinear"));
						}

						updater.setLinearVelocityImpulse(impulse.normalise().scale(controllableObject.getSpeed()).toVecd());
					}
					if (controllableObject.getEnableLook()) {
						Vec3f angularImpulse = Vec3f.ZERO;

						// up
						if (controllerState.getKeys()[keyMapping.getPitchUp()]) {
							angularImpulse = angularImpulse.add(translationVectorMap.get("pitchUp"));
						}
						// down
						if (controllerState.getKeys()[keyMapping.getPitchDown()]) {
							angularImpulse = angularImpulse.add(translationVectorMap.get("pitchDown"));
						}
						// left
						if (controllerState.getKeys()[keyMapping.getRollLeft()]) {
							angularImpulse = angularImpulse.add(translationVectorMap.get("rollLeft"));
						}
						// right
						if (controllerState.getKeys()[keyMapping.getRollRight()]) {
							angularImpulse = angularImpulse.add(translationVectorMap.get("rollRight"));
						}
						// z
						if (controllerState.getKeys()[keyMapping.getYawLeft()]) {
							angularImpulse = angularImpulse.add(translationVectorMap.get("yawLeft"));
						}
						// x
						if (controllerState.getKeys()[keyMapping.getYawRight()]) {
							angularImpulse = angularImpulse.add(translationVectorMap.get("yawRight"));
						}

						updater.setAngularVelocityImpulse(angularImpulse.normalise().scale(controllableObject.getSensitivity()).toVecd());
					}

					updater.sendUpdate();

				}
			}
		}
	}

}
