package com.nick.wood.game_engine.systems;

import com.nick.wood.game_engine.event_bus.interfaces.Bus;
import com.nick.wood.game_engine.model.game_objects.*;
import com.nick.wood.game_engine.model.types.GameObjectType;
import com.nick.wood.game_engine.model.utils.GameObjectUtils;
import com.nick.wood.maths.noise.Perlin2Df;
import com.nick.wood.maths.objects.srt.Transform;
import com.nick.wood.maths.objects.srt.TransformBuilder;
import com.nick.wood.maths.objects.vector.Vec2i;
import com.nick.wood.maths.objects.vector.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkLoader implements GESystem {

	private final ArrayList<Vec2i> loadedChunkIndices = new ArrayList<>();
	private final ConcurrentHashMap<Vec2i, GameObject> chunkIndexSceneGraphHashMap = new ConcurrentHashMap<>();

	public void update(HashMap<String, ArrayList<GameObject>> layeredGameObjectsMap) {

		// first find the main camera. There should only be one main camera in any of the scenes
		TransformObject cameraTransform = GameObjectUtils.FindMainCameraTransform(layeredGameObjectsMap);

		if (cameraTransform != null) {

			for (ArrayList<GameObject> gameObjects : layeredGameObjectsMap.values()) {

				ArrayList<GameObject> foundGameObjects = GameObjectUtils.FindGameObjectsByType(gameObjects, GameObjectType.TERRAIN);

				for (GameObject foundGameObject : foundGameObjects) {

					TerrainGenerationObject terrainGenerationObject = (TerrainGenerationObject) foundGameObject;

					// get the index of the player position
					int xIndex = (int) (cameraTransform.getTransform().getPosition().getX() / (double) (terrainGenerationObject.getChunkSize() * terrainGenerationObject.getCellSpace()));
					int yIndex = (int) (cameraTransform.getTransform().getPosition().getY() / (double) (terrainGenerationObject.getChunkSize() * terrainGenerationObject.getCellSpace()));

					Vec2i playerChunk = new Vec2i(xIndex, yIndex);

					// use this position to create the tiles all around the player
					// load all 16 chunks around it
					for (int x = xIndex - terrainGenerationObject.getLoadingClippingDistance(); x <= xIndex + terrainGenerationObject.getLoadingClippingDistance(); x++) {
						for (int y = yIndex - terrainGenerationObject.getLoadingClippingDistance(); y <= yIndex + terrainGenerationObject.getLoadingClippingDistance(); y++) {

							Vec2i chunkIndex = new Vec2i(x, y);

							// see if the chunk hasn't already been loaded
							if (!loadedChunkIndices.contains(chunkIndex)) {
								// add chunk to new list
								// and load it
								GameObject gameObject = createChunk(chunkIndex,
										terrainGenerationObject.getChunkSize(),
										terrainGenerationObject.getPerlin2Ds(),
										terrainGenerationObject.getCellSpace(),
										terrainGenerationObject.getTerrainTextureGameObjects()
								);
								chunkIndexSceneGraphHashMap.put(chunkIndex, gameObject);
								loadedChunkIndices.add(chunkIndex);
								terrainGenerationObject.getGameObjectData().getUpdater().addChild(gameObject);

							}
						}
					}

					// see if the chunk hasn't already been loaded
					Iterator<Vec2i> iterator = loadedChunkIndices.iterator();
					while (iterator.hasNext()) {
						Vec2i next = iterator.next();
						int dist = next.distance2AwayFrom(playerChunk);
						// if chunk is within visual range, set render to true
						if (dist < terrainGenerationObject.getVisualClippingDistance2()) {
							chunkIndexSceneGraphHashMap.get(next).getGameObjectData().showAll();
						} else if (dist < terrainGenerationObject.getLoadingClippingDistance2()) {
							chunkIndexSceneGraphHashMap.get(next).getGameObjectData().hideAll();
						} else {
							destroyChunk(next, terrainGenerationObject);
							iterator.remove();
						}
					}

				}

			}

		}

	}

	private void destroyChunk(Vec2i chunkIndex, GameObject parent) {
		parent.getGameObjectData().getUpdater().removeChild(chunkIndexSceneGraphHashMap.get(chunkIndex));
		chunkIndexSceneGraphHashMap.remove(chunkIndex);
	}

	private GameObject createChunk(Vec2i chunkIndex,
	                               int chunkSize,
	                               Perlin2Df[] perlin2Ds,
	                               int cellSpace,
	                               ArrayList<TerrainTextureGameObject> terrainTextureGameObjects) {

		ProceduralGeneration proceduralGeneration = new ProceduralGeneration();
		float[][] grid = proceduralGeneration.generateHeightMapChunk(
				chunkSize + 1,
				0.7,
				chunkIndex.getX() * chunkSize,
				chunkIndex.getY() * chunkSize,
				perlin2Ds,
				30,
				amp -> amp * amp * amp
		);


		Transform transform = new TransformBuilder()
				.setPosition(new Vec3f(chunkIndex.getX() * chunkSize * cellSpace, chunkIndex.getY() * chunkSize * cellSpace, 0)).build();

		TransformObject transformObject = new TransformObject(transform);
		transformObject.getGameObjectData().hideAll();

		TerrainChunkObject terrainChunkObject = new TerrainChunkObject(
				chunkIndex.toString(),
				grid,
				terrainTextureGameObjects,
				cellSpace
		);
		transformObject.getGameObjectData().attachGameObjectNode(terrainChunkObject);

		return transformObject;
	}

}
