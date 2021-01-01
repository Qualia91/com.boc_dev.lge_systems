package com.boc_dev.lge_systems.gui;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.ListObject;
import com.boc_dev.lge_model.generated.components.TextObject;
import com.boc_dev.lge_model.generated.components.TransformObject;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.text.DecimalFormat;
import java.util.HashSet;

public class ListSystem implements GcsSystem<ListObject> {

	@Override
	public void update(long timeStep, HashSet<ListObject> listObjects, Registry registry) {

		for (ListObject listObject : listObjects) {
			// get all child transforms
			float startZ = listObject.getGlobalTransform().getTranslationZ();
			for (Component child : listObject.getChildren()) {
				if (child.getComponentType().equals(ComponentType.TRANSFORM)) {
					// check if transform object y is correct
					TransformObject transformObject = (TransformObject) child;
					if (!approxEqual(transformObject.getPosition().getZ(), startZ)) {
						transformObject.getUpdater().setPosition(new Vec3f(
								listObject.getGlobalTransform().getTranslationX(),
								listObject.getGlobalTransform().getTranslationY(),
								startZ
						)).sendUpdate();
					}
				}

				startZ += listObject.getCellHeight();
			}
		}

	}

	private boolean approxEqual(float a, float b) {
		return a - 0.1f < b && a + 0.1f > b;
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.LIST;
	}
}
