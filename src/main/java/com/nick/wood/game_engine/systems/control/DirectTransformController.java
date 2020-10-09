package com.nick.wood.game_engine.systems.control;

import com.nick.wood.game_engine.model.game_objects.TransformObject;
import com.nick.wood.game_engine.model.input.ControllerState;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.vector.Vec3f;

public class DirectTransformController implements Control {

	private TransformObject transformObject;
	private final boolean enableLook;
	private final boolean enableMove;
	private float sensitivity;
	private float speed;
	private KeyMapping keyMapping = new KeyMapping();

	public DirectTransformController(TransformObject transformObject, boolean enableLook, boolean enableMove, float sensitivity, float speed) {
		this.transformObject = transformObject;
		this.enableLook = enableLook;
		this.enableMove = enableMove;
		this.sensitivity = sensitivity;
		this.speed = speed;
	}

	public void changeDefaultKeyMapping(KeyMapping keyMapping) {
		this.keyMapping = keyMapping;
	}

	public void update(ControllerState controllerState) {
		if (controllerState != null) {

			// W
			if (controllerState.getKeys()[keyMapping.getForward()]) {
				forwardLinear();
			}
			// A
			if (controllerState.getKeys()[keyMapping.getLeft()]) {
				leftLinear();
			}
			// S
			if (controllerState.getKeys()[keyMapping.getBack()]) {
				backLinear();
			}
			// D
			if (controllerState.getKeys()[keyMapping.getRight()]) {
				rightLinear();
			}
			// Q
			if (controllerState.getKeys()[keyMapping.getUp()]) {
				upLinear();
			}
			// E
			if (controllerState.getKeys()[keyMapping.getDown()]) {
				downLinear();
			}
			// mouse movement
			mouseMove(controllerState.getMouseDelX(), controllerState.getMouseDelY());
			controllerState.setMouseDelX(0);
			controllerState.setMouseDelY(0);
		}
	}

	public void reset() {
	}

	public void mouseMove(double dx, double dy) {
		if (enableLook) {
			QuaternionF rotationX = QuaternionF.RotationZ((float) -dx * sensitivity);
			QuaternionF rotationZ = QuaternionF.RotationX((float) -dy * sensitivity);
			// x axis rotation in local frame
			QuaternionF multiplyGlobalAxisX = rotationX.multiply(transformObject.getTransform().getRotation());
			// y axis rotation in globals frame
			QuaternionF multiplyGlobalAxisZ = transformObject.getTransform().getRotation().multiply(rotationZ);
			transformObject.getTransform().setRotation(multiplyGlobalAxisX.add(multiplyGlobalAxisZ).normalise());
		}
	}

	public void leftLinear() {
			transformObject.getTransform().setPosition(
					transformObject.getTransform().getPosition()
							.add(transformObject.getTransform().getRotation().rotateVector(Vec3f.X.scale(-speed)).toVec3f()));
	}

	public void rightLinear() {
		transformObject.getTransform().setPosition(
				transformObject.getTransform().getPosition()
						.add(transformObject.getTransform().getRotation().rotateVector(Vec3f.X.scale(speed)).toVec3f()));
	}

	public void forwardLinear() {
		transformObject.getTransform().setPosition(
				transformObject.getTransform().getPosition()
						.add(transformObject.getTransform().getRotation().rotateVector(Vec3f.Z.scale(-speed)).toVec3f()));
	}

	public void backLinear() {
		transformObject.getTransform().setPosition(
				transformObject.getTransform().getPosition()
						.add(transformObject.getTransform().getRotation().rotateVector(Vec3f.Z.scale(speed)).toVec3f()));
	}

	public void upLinear() {
		transformObject.getTransform().setPosition(
				transformObject.getTransform().getPosition()
						.add(transformObject.getTransform().getRotation().rotateVector(Vec3f.Y.scale(speed)).toVec3f()));
	}

	public void downLinear() {
		transformObject.getTransform().setPosition(
				transformObject.getTransform().getPosition()
						.add(transformObject.getTransform().getRotation().rotateVector(Vec3f.Y.scale(-speed)).toVec3f()));
	}

	public void leftRoll() {
	}

	public void rightRoll() {
	}

	public void upPitch() {
	}

	public void downPitch() {
	}

	public void leftYaw() {
	}

	public void rightYaw() {
	}

	public void action() {
	}

}
