package com.nick.wood.game_engine.systems.generation;

import com.nick.wood.game_engine.model.game_objects.*;
import com.nick.wood.game_engine.model.types.GameObjectType;
import com.nick.wood.game_engine.model.utils.GameObjectUtils;
import com.nick.wood.game_engine.systems.GESystem;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;

public class WaterGeneration implements GESystem {

	private final long steps;

	public WaterGeneration(long steps) {
		this.steps = steps;
	}

	public void update(HashMap<String, ArrayList<GameObject>> layeredGameObjectsMap, long timeSinceStart) {

		if (timeSinceStart % steps == 0) {
			// first find the main camera. There should only be one main camera in any of the scenes
			TransformObject cameraTransform = GameObjectUtils.FindMainCameraTransform(layeredGameObjectsMap);

			if (cameraTransform != null) {

				for (ArrayList<GameObject> gameObjects : layeredGameObjectsMap.values()) {

					ArrayList<GameObject> foundGameObjects = GameObjectUtils.FindGameObjectsByType(gameObjects, GameObjectType.WATER);

					for (GameObject foundGameObject : foundGameObjects) {

						WaterGenerationObject waterGenObject = (WaterGenerationObject) foundGameObject;
						float halfWidth = waterGenObject.getSize() * waterGenObject.getCellSize() / 2.0f;

						// if it has no children, create a transform then a water chunk
						if (waterGenObject.getGameObjectData().getChildren().isEmpty()) {

							Transform transform = new TransformBuilder()
									.setPosition(
											new Vec3f(cameraTransform.getTransform().getPosition().getX() - halfWidth,
													cameraTransform.getTransform().getPosition().getY() - halfWidth,
													0)
									)
									.build();

							TransformObject transformObject = new TransformObject(transform);

							WaterChunkObject waterChunkObject = new WaterChunkObject("Water_chunk",
									waterGenObject.getWaterTexture(),
									waterGenObject.getNormalMap(),
									waterGenObject.getSize(),
									waterGenObject.getWaterHeight(),
									waterGenObject.getCellSize());

							waterGenObject.getGameObjectData().getUpdater().addChild(transformObject);
							transformObject.getGameObjectData().getUpdater().addChild(waterChunkObject);

						} else {

							for (GameObject child : waterGenObject.getGameObjectData().getChildren()) {
								if (child.getGameObjectData().getType().equals(GameObjectType.TRANSFORM)) {
									TransformObject waterTransformObject = (TransformObject) child;
									// move chunk under camera
									waterTransformObject.setPosition(
											new Vec3f(cameraTransform.getTransform().getPosition().getX() - halfWidth,
													cameraTransform.getTransform().getPosition().getY() - halfWidth,
													0));

								}
							}

						}

					}

				}

			}
		}

	}

}
