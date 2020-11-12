package com.boc_dev.lge_systems.generation;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.*;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.objects.QuaternionF;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.HashSet;

public class WaterGeneration implements GcsSystem<WaterGenerationObject> {

	@Override
	public void update(long time, HashSet<WaterGenerationObject> components, Registry registry) {

		for (WaterGenerationObject waterGenerationObject : components) {

			// center position of water generation
			Vec3f startingPos = Vec3f.ZERO;

			// get parent object. if null, then it is just generated at 0, 0, 0 and will not generate anymore
			Component parent = waterGenerationObject.getParent();
			if (parent != null) {
				startingPos = parent.getGlobalTransform().getTranslation();
			}

			float halfWidth = waterGenerationObject.getChunkSize() * waterGenerationObject.getCellSpace() / 2.0f;

			startingPos = startingPos.subtract(new Vec3f(halfWidth, halfWidth, 0));

			// see if it has a water chunk already
			for (Component component : waterGenerationObject.getChildren()) {
				if (component.getComponentType().equals(ComponentType.TRANSFORM)) {
					for (Component child : component.getChildren()) {
						if (component.getComponentType().equals(ComponentType.WATERCHUNK)) {
							TransformObject transformObject = (TransformObject) component;
							transformObject.getUpdater().setPosition(startingPos).sendUpdate();
							return;
						}
					}
				}
			}
			// if it has made it this far, it has no water chunk under it, so make one like before
			makeWaterChunk(registry, startingPos, waterGenerationObject);
		}
	}

	private void makeWaterChunk(Registry registry, Vec3f startingPos, WaterGenerationObject waterGenerationObject) {
		TransformObject transformObject = new TransformObject(registry, "water", startingPos, QuaternionF.Identity, Vec3f.ONE);

		float[][] grid = new float[waterGenerationObject.getChunkSize()][waterGenerationObject.getChunkSize()];

		for (int i = 0; i < waterGenerationObject.getChunkSize(); i++) {
			for (int j = 0; j < waterGenerationObject.getChunkSize(); j++) {
				grid[i][j] = 0;
			}
		}

		WaterChunkObject waterChunkObject = new WaterChunkObject(
				registry,
				"Water_chunk" + waterGenerationObject.getUuid(),
				waterGenerationObject.getCellSpace(),
				grid);

		waterChunkObject.getUpdater().setParent(transformObject);
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.WATERGENERATION;
	}

}
