package com.boc_dev.lge_systems.physics;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.ImpulseObject;
import com.boc_dev.lge_model.generated.components.RigidBodyObject;
import com.boc_dev.lge_model.generated.components.TransformObject;
import com.boc_dev.lge_model.generated.enums.RigidBodyObjectType;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.objects.vector.Vec3d;
import com.boc_dev.physics_library.rigid_body_dynamics_verbose.RigidBody;
import com.boc_dev.physics_library.rigid_body_dynamics_verbose.RigidBodyType;
import com.boc_dev.physics_library.rigid_body_dynamics_verbose.Simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class RigidBodyPhysicsSystem implements GcsSystem<RigidBodyObject> {

	private final Simulation rbSim;

	public RigidBodyPhysicsSystem() {
		this.rbSim = new Simulation();
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

	@Override
	public void update(long time, HashSet<RigidBodyObject> components, Registry registry) {

		ArrayList<RigidBody> rigidBodies = new ArrayList<>();

		for (RigidBodyObject rigidBodyObject : components) {

			// get parent transform
			if (rigidBodyObject.getParent() != null && rigidBodyObject.getParent().getComponentType().equals(ComponentType.TRANSFORM)) {

				TransformObject transformObject = (TransformObject) rigidBodyObject.getParent();

				// get child impulse object
				Vec3d velocityImpulse = Vec3d.ZERO;
				Vec3d angularVelocityImpulse = Vec3d.ZERO;
				for (Component child : transformObject.getChildren()) {
					if (child.getComponentType().equals(ComponentType.IMPULSE)) {
						ImpulseObject impulseObject = (ImpulseObject) child;
						velocityImpulse = impulseObject.getLinearVelocityImpulse();
						angularVelocityImpulse = impulseObject.getAngularVelocityImpulse();
					}
				}

				rigidBodies.add(new RigidBody(
						rigidBodyObject.getUuid(),
						rigidBodyObject.getMass(),
						rigidBodyObject.getDimensions(),
						transformObject.getPosition().toVecd(),
						transformObject.getRotation().toQuatD(),
						rigidBodyObject.getLinearMomentum(),
						rigidBodyObject.getAngularMomentum(),
						transformObject.getRotation().rotateVector(velocityImpulse.toVec3f()).toVec3f().toVecd(),
						transformObject.getRotation().rotateVector(angularVelocityImpulse.toVec3f()).toVec3f().toVecd(),
						convertRigidBodyType(rigidBodyObject.getRigidBodyType())
				));

			}
		}

		HashMap<UUID, RigidBody> rigidBodiesSimulated = rbSim.iterate(0.02, rigidBodies);

		for (RigidBodyObject rigidBodyObject : components) {
			RigidBody rigidBody = rigidBodiesSimulated.get(rigidBodyObject.getUuid());
			rigidBodyObject.getUpdater()
					.setAngularMomentum(rigidBody.getAngularMomentum())
					.setLinearMomentum(rigidBody.getLinearMomentum())
					.sendUpdate();

			if (rigidBodyObject.getParent() != null && rigidBodyObject.getParent().getComponentType().equals(ComponentType.TRANSFORM)) {

				TransformObject transformObject = (TransformObject) rigidBodyObject.getParent();

				transformObject.getUpdater()
						.setPosition(rigidBody.getOrigin().toVec3f())
						.setRotation(rigidBody.getRotation().toQuatF())
						.sendUpdate();
			}
		}
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.RIGIDBODY;
	}
}
