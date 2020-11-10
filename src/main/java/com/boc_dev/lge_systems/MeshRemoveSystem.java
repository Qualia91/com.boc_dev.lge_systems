package com.boc_dev.lge_systems;

import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.GeometryObject;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.objects.QuaternionF;

import java.util.HashSet;

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
