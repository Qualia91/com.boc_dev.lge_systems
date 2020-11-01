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


	@Override
	public void update(long timeStep, HashSet<TransformObject> transformComponents, Registry registry) {

		TransformObject newTransformObject = null;
		if (timeStep % 100 == 0) {
			newTransformObject = new TransformObject(registry, "hello", Vec3f.ONE, new Vec3f(0, timeStep/5, 0), QuaternionF.Identity);
			GeometryObject geometryObject = new GeometryObject(registry, "geometryhello", Matrix4f.Identity, UUID.randomUUID(), "DEFAULT");
			geometryObject.getUpdater().setParent(newTransformObject).sendUpdate();
		}

		for (TransformObject transformObject : transformComponents) {
			if (timeStep % 100 == 50 && transformObject.getName().equals("hello")) {
				transformObject.getUpdater().delete();
			}
			if (transformObject.getChildren().stream().noneMatch(to -> to.getComponentType().equals(ComponentType.CAMERA))) {
				transformObject.getUpdater()
						.setPosition(transformObject.getPosition().add(new Vec3f(0.1f, 0.1f, 0)))
						.sendUpdate();
			}
		}



	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.TRANSFORM;
	}
}
