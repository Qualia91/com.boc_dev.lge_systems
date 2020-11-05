package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.gcs_model.gcs.Registry;
import com.nick.wood.game_engine.gcs_model.generated.components.ComponentType;
import com.nick.wood.game_engine.gcs_model.generated.components.GeometryObject;
import com.nick.wood.game_engine.gcs_model.generated.components.TransformObject;
import com.nick.wood.game_engine.gcs_model.systems.GcsSystem;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.vector.Vec;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class TestGcsSystem implements GcsSystem<TransformObject> {

	private final QuaternionF rotation = QuaternionF.RotationX(0.1f);

	@Override
	public void update(long timeStep, HashSet<TransformObject> transformComponents, Registry registry) {

		if (timeStep % 100 == 99) {

			for (TransformObject transformObject : transformComponents) {
				if (transformObject.getChildren().stream().noneMatch(to -> to.getComponentType().equals(ComponentType.CAMERA))) {
					transformObject.getUpdater()
							.setRotation(transformObject.getRotation().multiply(rotation))
							.sendUpdate();
				}
			}

		}



	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.TRANSFORM;
	}
}
