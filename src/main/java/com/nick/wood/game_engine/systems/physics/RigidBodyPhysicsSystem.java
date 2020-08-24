package com.nick.wood.game_engine.systems.physics;

import com.nick.wood.game_engine.model.game_objects.GameObject;
import com.nick.wood.game_engine.model.game_objects.RigidBodyObject;
import com.nick.wood.game_engine.model.game_objects.TransformObject;
import com.nick.wood.game_engine.model.types.GameObjectType;
import com.nick.wood.game_engine.model.types.RigidBodyObjectType;
import com.nick.wood.game_engine.model.utils.GameObjectUtils;
import com.nick.wood.game_engine.systems.GESystem;
import com.nick.wood.maths.objects.vector.Vec3f;
import com.nick.wood.physics_library.rigid_body_dynamics_verbose.RigidBody;
import com.nick.wood.physics_library.rigid_body_dynamics_verbose.RigidBodyType;
import com.nick.wood.physics_library.rigid_body_dynamics_verbose.Simulation;

import java.util.ArrayList;
import java.util.HashMap;

public class RigidBodyPhysicsSystem implements GESystem {

	private final Simulation rbSim;
	private final long steps;
	private long lastTime = 0L;

	public RigidBodyPhysicsSystem(long steps) {
		this.rbSim = new Simulation();
		this.steps = steps;
	}

	@Override
	public void update(HashMap<String, ArrayList<GameObject>> layeredGameObjectsMap, long timeSinceStart) {

		// find all rigid bodies in sim
		// create physics rigid bodies
		// iterate physics sim
		// get rigid bodies from sim
		// go through and update transforms accordingly
		long stepTimeNano = timeSinceStart - lastTime;
		double stepTime = (double) stepTimeNano / 1_000_000_000;
		if (stepTimeNano > 17_000_000) {

			for (ArrayList<GameObject> gameObjects : layeredGameObjectsMap.values()) {

				ArrayList<RigidBodyObject> foundGameObjects = GameObjectUtils.FindGameObjectsByType(gameObjects, GameObjectType.RIGID_BODY);

				ArrayList<RigidBody> rigidBodies = new ArrayList<>();

				for (RigidBodyObject foundGameObject : foundGameObjects) {
					rigidBodies.add(new RigidBody(
							foundGameObject.getGameObjectData().getUuid(),
							foundGameObject.getBuilder().getMass(),
							foundGameObject.getBuilder().getDimensions(),
							foundGameObject.getBuilder().getOrigin(),
							foundGameObject.getBuilder().getRotation(),
							foundGameObject.getBuilder().getLinearMomentum(),
							foundGameObject.getBuilder().getAngularMomentum(),
							convertRigidBodyType(foundGameObject.getBuilder().getRigidBodyType())
					));
				}

				ArrayList<RigidBody> updatedRigidBodies = rbSim.iterate(
						stepTime,
						rigidBodies
				);

				for (RigidBodyObject foundGameObject : foundGameObjects) {
					RigidBody rigidBody = null;
					for (RigidBody updatedRigidBody : updatedRigidBodies) {
						if (foundGameObject.getGameObjectData().getUuid().equals(updatedRigidBody.getUuid())) {
							rigidBody = updatedRigidBody;
						}
						if (rigidBody != null) {
							for (GameObject child : foundGameObject.getGameObjectData().getChildren()) {
								if (child.getGameObjectData().getType().equals(GameObjectType.TRANSFORM)) {
									((TransformObject) child).setPosition((Vec3f) rigidBody.getOrigin().toVecf());
									((TransformObject) child).setRotation(rigidBody.getRotation().toQuatF());
									foundGameObject.getBuilder().setData(
											rigidBody.getMass(),
											rigidBody.getOrigin(),
											rigidBody.getRotation(),
											rigidBody.getLinearMomentum(),
											rigidBody.getAngularMomentum(),
											rigidBody.getType().toString()
									);
								}
							}
						}
					}
				}
			}
			lastTime = timeSinceStart;
		}
	}

	private RigidBodyType convertRigidBodyType(RigidBodyObjectType rigidBodyType) {
		switch (rigidBodyType) {
			case CUBOID:
				return RigidBodyType.CUBOID;
			case SPHERE_INNER:
				return RigidBodyType.SPHERE_INNER;
			default:
				return RigidBodyType.SPHERE;
		}
	}
}
