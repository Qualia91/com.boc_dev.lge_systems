package com.boc_dev.lge_systems.generation;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.*;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.noise.Perlin2Df;
import com.boc_dev.maths.objects.vector.Vec2i;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * has to go underneath object you want it to generate around
 */
public class TerrainGeneration implements GcsSystem<TerrainGenerationObject> {

	private final ProceduralGeneration proceduralGeneration = new ProceduralGeneration();
	// todo hate this
	private final Perlin2Df[] perlinNoise = new Perlin2Df[]{
			new Perlin2Df(5000, 50),
			new Perlin2Df(5000, 50),
			new Perlin2Df(5000, 50),
			new Perlin2Df(5000, 50),
			new Perlin2Df(5000, 50)
	};

	@Override
	public void update(long time, HashSet<TerrainGenerationObject> terrainGenerationObjects, Registry registry) {

		for (TerrainGenerationObject terrainGenerationObject : terrainGenerationObjects) {

			// center position of terrain generation
			Vec3f startingPos = Vec3f.ZERO;

			// get parent object. if null, then it is just generated at 0, 0, 0 and will not generate anymore
			Component parent = terrainGenerationObject.getParent();
			if (parent != null) {
				startingPos = parent.getGlobalTransform().getTranslation();
			}

			// now work out all the indices of terrain generation that should be created using the starting position and the
			// terrain generation object properties
			int startingXIndex = (int) (startingPos.getX() / (double) (terrainGenerationObject.getChunkSize() * terrainGenerationObject.getCellSpace()));
			int startingYIndex = (int) (startingPos.getY() / (double) (terrainGenerationObject.getChunkSize() * terrainGenerationObject.getCellSpace()));

			// create a list of valid chunk index's
			ArrayList<Vec2i> validIndices = new ArrayList<>();
			for (int x = startingXIndex - terrainGenerationObject.getGenerationRange(); x <= startingXIndex + terrainGenerationObject.getGenerationRange(); x++) {
				for (int y = startingYIndex - terrainGenerationObject.getGenerationRange(); y <= startingYIndex + terrainGenerationObject.getGenerationRange(); y++) {
					validIndices.add(new Vec2i(x, y));
				}
			}

			// get all the terrain chunks underneath this object
			// and get a map of currently generated terrain chunk indices and chunk
			HashMap<Vec2i, TerrainChunkObject> indices = new HashMap<>();
			for (Component child : terrainGenerationObject.getChildren()) {
				if (child.getComponentType().equals(ComponentType.TERRAINCHUNK)) {
					TerrainChunkObject chunk = (TerrainChunkObject) child;
					// if chunk has a none valid index, delete it
					if (!validIndices.contains(chunk.getIndex())) {
						child.getUpdater().delete();
					}
					indices.put(chunk.getIndex(), chunk);
				}
			}

			// loop through all valid indices
			for (Vec2i validIndex : validIndices) {
				// if valid index is not in current index list, make a new terrain chunk
				if (!indices.containsKey(validIndex)) {
					createChunk(registry,
							validIndex,
							terrainGenerationObject.getChunkSize(),
							perlinNoise,
							terrainGenerationObject.getCellSpace(),
							terrainGenerationObject.getAmplitude(),
							terrainGenerationObject);
				}
				// if valid index is in current index list, do nothing
			}
		}
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.TERRAINGENERATION;
	}

	private void createChunk(Registry registry,
	                         Vec2i chunkIndex,
	                         int chunkSize,
	                         Perlin2Df[] perlin2Ds,
	                         int cellSpace,
	                         int amplitude,
	                         TerrainGenerationObject terrainGenerationObject) {

		float[][] grid = proceduralGeneration.generateHeightMapChunk(
				chunkSize + 1,
				0.7,
				chunkIndex.getX() * chunkSize,
				chunkIndex.getY() * chunkSize,
				perlin2Ds,
				amplitude
		);

		TerrainChunkObject terrainChunkObject = new TerrainChunkObject(
				registry,
				chunkIndex.toString() + "terrain_chunk",
				cellSpace,
				grid,
				chunkIndex,
				terrainGenerationObject.getMaterialID(),
				new Vec3f(chunkIndex.getX() * chunkSize * cellSpace, chunkIndex.getY() * chunkSize * cellSpace, 0)
		);

		terrainChunkObject.getUpdater().setParent(terrainGenerationObject).sendUpdate();


	}
}
