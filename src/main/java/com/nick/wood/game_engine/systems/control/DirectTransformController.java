package com.nick.wood.game_engine.systems.control;

import com.nick.wood.game_engine.gcs_model.gcs.Registry;
import com.nick.wood.game_engine.gcs_model.generated.components.ControllableObject;
import com.nick.wood.game_engine.gcs_model.generated.components.TransformObject;
import com.nick.wood.game_engine.model.input.ControllerState;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.HashMap;
import java.util.Optional;

public class DirectTransformController {

	private final ControllerState controllerState;
	private KeyMapping keyMapping = new KeyMapping();
	private HashMap<String, Vec3f> translationVectorMap = new HashMap<>();

	public DirectTransformController(ControllerState controllerState) {
		this.controllerState = controllerState;
		translationVectorMap.put("leftLinear", Vec3f.X.neg());
		translationVectorMap.put("rightLinear", Vec3f.X);
		translationVectorMap.put("forwardLinear", Vec3f.Z.neg());
		translationVectorMap.put("backLinear", Vec3f.Z);
		translationVectorMap.put("upLinear", Vec3f.Y);
		translationVectorMap.put("downLinear", Vec3f.Y.neg());
	}

	public void changeDefaultKeyMapping(KeyMapping keyMapping) {
		this.keyMapping = keyMapping;
	}

	public void update(ControllableObject controllableObject, TransformObject transformObject) {
		if (controllerState != null) {

			TransformObject.TransformUpdater updater = transformObject.getUpdater();

			boolean sendNeeded = false;

			if (controllableObject.getEnableMove()) {
				Vec3f impulse = Vec3f.ZERO;

				// W
				if (controllerState.getKeys()[keyMapping.getForward()]) {
					impulse = impulse.add(translationVectorMap.get("forwardLinear"));
				}
				// A
				if (controllerState.getKeys()[keyMapping.getLeft()]) {
					impulse = impulse.add(translationVectorMap.get("leftLinear"));
				}
				// S
				if (controllerState.getKeys()[keyMapping.getBack()]) {
					impulse = impulse.add(translationVectorMap.get("backLinear"));
				}
				// D
				if (controllerState.getKeys()[keyMapping.getRight()]) {
					impulse = impulse.add(translationVectorMap.get("rightLinear"));
				}
				// Q
				if (controllerState.getKeys()[keyMapping.getUp()]) {
					impulse = impulse.add(translationVectorMap.get("upLinear"));
				}
				// E
				if (controllerState.getKeys()[keyMapping.getDown()]) {
					impulse = impulse.add(translationVectorMap.get("downLinear"));
				}

				if (impulse != Vec3f.ZERO){
					updater.setPosition(calculateTranslate(transformObject, impulse.normalise().scale(controllableObject.getSpeed())));
					sendNeeded = true;
				}
			}

			if (controllableObject.getEnableLook() && (Math.abs(controllerState.getMouseDelX()) > 0 || Math.abs(controllerState.getMouseDelY()) > 0)) {
				updater.setRotation(mouseMove(controllerState.getMouseDelX(), controllerState.getMouseDelY(), controllableObject.getSensitivity(), transformObject));
				controllerState.setMouseDelX(0);
				controllerState.setMouseDelY(0);
				sendNeeded = true;
			}

			if (sendNeeded) {
				updater.sendUpdate();
			}
		}
	}

	public void reset() {
	}

	public QuaternionF mouseMove(double dx, double dy, float sensitivity, TransformObject transformObject) {
		QuaternionF rotationX = QuaternionF.RotationZ((float) -dx * sensitivity);
		QuaternionF rotationZ = QuaternionF.RotationX((float) -dy * sensitivity);
		// x axis rotation in local frame
		QuaternionF multiplyGlobalAxisX = rotationX.multiply(transformObject.getRotation());
		// y axis rotation in globals frame
		QuaternionF multiplyGlobalAxisZ = transformObject.getRotation().multiply(rotationZ);
		return multiplyGlobalAxisX.add(multiplyGlobalAxisZ).normalise();
	}

	public Vec3f calculateTranslate(TransformObject transformObject, Vec3f translationVector) {
		return transformObject.getPosition().add(transformObject.getRotation().rotateVector(translationVector).toVec3f());
	}

}
