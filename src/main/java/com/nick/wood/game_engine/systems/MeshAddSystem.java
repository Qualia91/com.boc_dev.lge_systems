package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.gcs_model.gcs.Registry;
import com.nick.wood.game_engine.gcs_model.generated.components.ComponentType;
import com.nick.wood.game_engine.gcs_model.generated.components.GeometryObject;
import com.nick.wood.game_engine.gcs_model.generated.components.MaterialObject;
import com.nick.wood.game_engine.gcs_model.generated.components.TransformObject;
import com.nick.wood.game_engine.gcs_model.systems.GcsSystem;
import com.nick.wood.maths.objects.QuaternionF;
import com.nick.wood.maths.objects.matrix.Matrix4f;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.HashSet;
import java.util.UUID;

public class MeshAddSystem implements GcsSystem<MaterialObject> {

	private final QuaternionF rotation = QuaternionF.RotationX(0.1f);

	@Override
	public void update(long timeStep, HashSet<MaterialObject> geometryObjects, Registry registry) {

		if (timeStep < 2500) {

			// find any of the materials
			UUID materialUUID = null;
			for (MaterialObject materialObject : geometryObjects) {
				if (materialObject.getName().equals("Material")) {
					materialUUID = materialObject.getUuid();
					Transform build = new TransformBuilder().setPosition(new Vec3f(0, (int) -timeStep / 100, -timeStep % 100)).build();


					TransformObject newTransformObject = new TransformObject(
							registry,
							"TransformObject" + timeStep,
							build.getPosition(),
							build.getRotation(),
							build.getScale());

					GeometryObject newGeometryObject = new GeometryObject(
							registry,
							"Geometry" + timeStep,
							Matrix4f.Identity,
							materialUUID,
							"DEFAULT_CUBE"
					);

					newGeometryObject.getUpdater().setParent(newTransformObject).sendUpdate();
					return;
				}
			}

		}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.MATERIAL;
	}
}
