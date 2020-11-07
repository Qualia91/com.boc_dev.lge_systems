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

public class MeshRemoveSystem implements GcsSystem<GeometryObject> {

	private final QuaternionF rotation = QuaternionF.RotationX(0.1f);

	@Override
	public void update(long timeStep, HashSet<GeometryObject> geometryObjects, Registry registry) {


		if (timeStep > 2500) {

			for (GeometryObject geometryObject : geometryObjects) {
				geometryObject.getParent().getUpdater().delete();
				return;
			}

		}


	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.GEOMETRY;
	}
}
