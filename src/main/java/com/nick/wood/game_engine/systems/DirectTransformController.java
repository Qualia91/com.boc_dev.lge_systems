package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.model.game_objects.GameObject;
import com.nick.wood.game_engine.model.game_objects.TransformObject;
import com.nick.wood.game_engine.model.input.ControllerState;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

public class DirectTransformController implements GESystem {

	private TransformObject transformObject;
	private final boolean enableLook;
	private final boolean enableMove;
	private float sensitivity;
	private float speed;
	private ControllerState controllerState;

	public DirectTransformController(TransformObject transformObject, boolean enableLook, boolean enableMove, float sensitivity, float speed) {
		this.transformObject = transformObject;
		this.enableLook = enableLook;
		this.enableMove = enableMove;
		this.sensitivity = sensitivity;
		this.speed = speed;
	}

	public void update(HashMap<String, ArrayList<GameObject>> layeredGameObjectsMap) {
		if (controllerState != null) {

			// W
			if (controllerState.getKeys()[87]) {
				forwardLinear();
			}
			// A
			if (controllerState.getKeys()[65]) {
				leftLinear();
			}
			// S
			if (controllerState.getKeys()[83]) {
				backLinear();
			}
			// D
			if (controllerState.getKeys()[68]) {
				rightLinear();
			}
			// Q
			if (controllerState.getKeys()[81]) {
				upLinear();
			}
			// E
			if (controllerState.getKeys()[69]) {
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

	public void setUserInput(ControllerState controllerState) {
		this.controllerState = controllerState;
	}

}
