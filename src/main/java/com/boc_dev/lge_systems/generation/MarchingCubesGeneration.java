package com.boc_dev.lge_systems.generation;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.*;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.noise.Perlin3D;
import com.boc_dev.maths.objects.vector.Vec3f;
import com.boc_dev.maths.objects.vector.Vec3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * has to go underneath object you want it to generate around
 */
public class MarchingCubesGeneration implements GcsSystem<MarchingCubeGenerationObject> {

	private final ProceduralGeneration proceduralGeneration = new ProceduralGeneration();
	// todo hate this
	private final Perlin3D perlinNoise = new Perlin3D(1000, 10);

	@Override
	public void update(long time, HashSet<MarchingCubeGenerationObject> marchingCubeGenerationObjects, Registry registry) {

		for (MarchingCubeGenerationObject marchingCubeGenerationObject : marchingCubeGenerationObjects) {

			// center position of terrain generation
			Vec3f startingPos = Vec3f.ZERO;

			// get parent object. if null, then it is just generated at 0, 0, 0 and will not generate anymore
			Component parent = marchingCubeGenerationObject.getParent();
			if (parent != null) {
				startingPos = parent.getGlobalTransform().getTranslation();
			}

			// now work out all the indices of terrain generation that should be created using the starting position and the
			// terrain generation object properties
			int startingXIndex = (int) (startingPos.getX() / (double) (marchingCubeGenerationObject.getChunkSize()));
			int startingYIndex = (int) (startingPos.getY() / (double) (marchingCubeGenerationObject.getChunkSize()));
			int startingZIndex = (int) (startingPos.getZ() / (double) (marchingCubeGenerationObject.getChunkSize()));

			// create a list of valid chunk index's
			ArrayList<Vec3i> validIndices = new ArrayList<>();
			for (int x = startingXIndex - marchingCubeGenerationObject.getGenerationRange(); x <= startingXIndex + marchingCubeGenerationObject.getGenerationRange(); x++) {
				for (int y = startingYIndex - marchingCubeGenerationObject.getGenerationRange(); y <= startingYIndex + marchingCubeGenerationObject.getGenerationRange(); y++) {
					for (int z = startingZIndex - marchingCubeGenerationObject.getGenerationRange(); z <= startingZIndex + marchingCubeGenerationObject.getGenerationRange(); z++) {
						validIndices.add(new Vec3i(x, y, z));
					}
				}
			}

			// get all the terrain chunks underneath this object
			// and get a map of currently generated terrain chunk indices and chunk
			HashMap<Vec3i, MeshObject> indices = new HashMap<>();
			for (Component child : marchingCubeGenerationObject.getChildren()) {
				if (child.getComponentType().equals(ComponentType.MESH)) {
					MeshObject chunk = (MeshObject) child;
					// if chunk has a none valid index, delete it
					if (!validIndices.contains(chunk.getIndex())) {
						child.getUpdater().delete();
					}
					indices.put(chunk.getIndex(), chunk);
				}
			}

			int cubesBuild = 0;
			// loop through all valid indices
			for (Vec3i validIndex : validIndices) {
				// if valid index is not in current index list, make a new terrain chunk
				if (!indices.containsKey(validIndex)) {
					if (cubesBuild > 5) {
						return;
					}

					createChunk(registry,
							validIndex,
							marchingCubeGenerationObject.getChunkSize(),
							perlinNoise,
							marchingCubeGenerationObject);

					cubesBuild++;
				}
				// if valid index is in current index list, do nothing
			}
		}
	}

	private void createChunk(Registry registry, Vec3i chunkIndex, int chunkSize, Perlin3D perlin3D, MarchingCubeGenerationObject marchingCubeGenerationObject) {

		ArrayList<Vec3f> vertices = new ArrayList<>();

		for (int i = 0; i < chunkSize; i++) {
			for (int j = 0; j < chunkSize; j++) {
				for (int k = 0; k < chunkSize; k++) {

					Cube cube = calcCubeIndex(i, j, k, perlin3D, chunkSize, chunkIndex);

					int[] ints = MarchingCubesTables.TRIANGLE_TABLE[cube.getCubeIndex()];

					for (int anInt : ints) {

						int edgeFirstVertex = MarchingCubesTables.EDGE_FIRST_VERTEX[anInt];
						int edgeSecondVertex = MarchingCubesTables.EDGE_SECOND_VERTEX[anInt];

						// get 2 cube corners from these index's
						vertices.add((cube.getVertices()[edgeFirstVertex].add(cube.getVertices()[edgeSecondVertex])).scale(0.5f));

					}
				}
			}
		}

		MeshObject meshObject = new MeshObject(
				registry,
				"MarchingCubes_" + chunkIndex.getX() + "_" + chunkIndex.getY() + "_" + chunkIndex.getZ(),
				chunkIndex,
				marchingCubeGenerationObject.getMaterialID(),
				vertices.toArray(new Vec3f[vertices.size()])
		);

		meshObject.getUpdater().setParent(marchingCubeGenerationObject).sendUpdate();
	}

	public Cube calcCubeIndex(int i, int j, int k, Perlin3D perlin3D, int size, Vec3i chunkIndex) {

		final int edgeLength = 2;
		int cubeVertexIndex = 0;
		int cubeIndex = 0;
		int cubeIndexRHS = 1;

		Vec3f[] vertex = new Vec3f[8];

		for (int y = 0; y < edgeLength; ++y) {
			for (int z = 0; z < edgeLength; ++z) {
				for (int x = z % edgeLength; x >= 0 && x < edgeLength; x += (z == 0 ? 1 : -1)) {
					vertex[cubeVertexIndex] = new Vec3f(i + x + (chunkIndex.getX() * size), j + y + (chunkIndex.getY() * size), k + z + (chunkIndex.getZ() * size));

					double point = perlin3D.getPoint(Math.abs(i + x + (chunkIndex.getX() * size)),
							Math.abs(j + y + (chunkIndex.getY() * size)),
							Math.abs(k + z + (chunkIndex.getZ() * size)));

					cubeVertexIndex++;

					if (point > 0) {
						cubeIndex |= cubeIndexRHS;
					}

					cubeIndexRHS <<= 1;
				}
			}
		}

		return new Cube(cubeIndex, vertex);

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.MARCHINGCUBEGENERATION;
	}

}
