package com.boc_dev.lge_systems;

import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.TransformObject;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.objects.QuaternionF;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.HashSet;

public class TransformObjectUpdaterSystem implements GcsSystem<TransformObject> {

	private final QuaternionF rotation = QuaternionF.RotationX(0.1f);

	@Override
	public void update(long timeStep, HashSet<TransformObject> transformComponents, Registry registry) {

			for (TransformObject transformObject : transformComponents) {
				if (transformObject.getChildren().stream().noneMatch(to -> to.getComponentType().equals(ComponentType.CAMERA))) {
					transformObject.getUpdater()
							.setRotation(transformObject.getRotation().multiply(rotation))
							.setPosition(new Vec3f(transformObject.getPosition().getX(), transformObject.getPosition().getY() + 0.01f, transformObject.getPosition().getZ()))
							.sendUpdate();
				}
			}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.TRANSFORM;
	}
}
