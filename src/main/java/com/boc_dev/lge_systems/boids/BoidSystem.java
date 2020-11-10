package com.boc_dev.lge_systems.boids;

import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.BoidObject;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.TransformObject;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.HashSet;

public class BoidSystem implements GcsSystem<BoidObject> {


	@Override
	public void update(long timeStep, HashSet<BoidObject> boidObjects, Registry registry) {

		updatePositions(0.02f, boidObjects);

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.BOID;
	}

	public void updatePositions(float timeStep, HashSet<BoidObject> boidObjects) {

		for (BoidObject boidObject : boidObjects) {

			// check parent is transform
			if (boidObject.getParent().getComponentType().equals(ComponentType.TRANSFORM)) {

				TransformObject transformObject = (TransformObject) boidObject.getParent();

				Vec3f newVelocity = limitVelocity(boidObject,
						boidObject.getVelocity()
								.add(asc(boidObject, transformObject, boidObjects)));

				boidObject.getUpdater().setVelocity(newVelocity).sendUpdate();

				transformObject.getUpdater().setPosition(transformObject.getPosition().add(newVelocity.scale(timeStep * 10))).sendUpdate();


			}

		}

	}

	private Vec3f intersectionVector(Vec3f pos1, float rad1, Vec3f pos2, float rad2) {

		float dx = pos1.getX() - pos2.getX();
		float dy = pos1.getY() - pos2.getY();
		float dz = pos1.getZ() - pos2.getZ();

		float distance2 = dx * dx + dy * dy + dz * dz;

		if (distance2 <= (rad1 + rad2) * (rad1 + rad2)) {
			// find vector from pos1 to pos2
			return pos2.subtract(pos1);
		}

		return null;
	}

	private Vec3f limitVelocity(BoidObject boid, Vec3f vel) {

		float speed = boid.getSpeed();

		if (vel.length() > speed) {

			vel = vel.normalise().scale(speed);

		}

		return vel;
	}

	private Vec3f asc(BoidObject boid, TransformObject transformObject, HashSet<BoidObject> boidObjects) {
		float vxAlign = 0;
		float vyAlign = 0;
		float vzAlign = 0;
		float numAlign = 0;

		float xSep = 0;
		float ySep = 0;
		float zSep = 0;

		float xCoh = 0;
		float yCoh = 0;
		float zCoh = 0;
		float numCoh = 0;

		float x = 0;
		float y = 0;
		float z = 0;

		for (BoidObject otherBoid : boidObjects) {

			if (!boid.equals(otherBoid)) {

				if (otherBoid.getParent().getComponentType().equals(ComponentType.TRANSFORM)) {

					TransformObject otherTransformObject = (TransformObject) otherBoid.getParent();

					if (Math.abs(transformObject.getPosition().subtract(otherTransformObject.getPosition()).length2()) < boid.getLengthAwayGroupSquared()) {

						vxAlign += otherBoid.getVelocity().getX();
						vyAlign += otherBoid.getVelocity().getY();
						vzAlign += otherBoid.getVelocity().getZ();
						numAlign++;

						xCoh += otherTransformObject.getPosition().getX();
						yCoh += otherTransformObject.getPosition().getY();
						zCoh += otherTransformObject.getPosition().getZ();
						numCoh++;

					}

					if (Math.abs(transformObject.getPosition().subtract(otherTransformObject.getPosition()).length2()) < boid.getLengthAwayMinSquared()) {

						xSep -= (otherTransformObject.getPosition().getX() - transformObject.getPosition().getX()) * boid.getAntiCollideScale();
						ySep -= (otherTransformObject.getPosition().getY() - transformObject.getPosition().getY()) * boid.getAntiCollideScale();
						zSep -= (otherTransformObject.getPosition().getZ() - transformObject.getPosition().getZ()) * boid.getAntiCollideScale();

					}

					// todo radius should be scaled by transform scale
					Vec3f collisionVector = intersectionVector(otherTransformObject.getPosition(), otherBoid.getRadius(), transformObject.getPosition(), boid.getRadius());
					if (collisionVector != null) {
						x = collisionVector.getX() * boid.getBoundScale();
						y = collisionVector.getY() * boid.getBoundScale();
						z = collisionVector.getZ() * boid.getBoundScale();
					}

				}
			}



		}
		Vec3f alignVec = Vec3f.ZERO;
		Vec3f cohVec = Vec3f.ZERO;

		if (numAlign != 0) {
			alignVec = new Vec3f(
					((vxAlign / numAlign) - boid.getVelocity().getX()) * boid.getVelocityMatchScale(),
					((vyAlign / numAlign) - boid.getVelocity().getY()) * boid.getVelocityMatchScale(),
					((vzAlign / numAlign) - boid.getVelocity().getZ()) * boid.getVelocityMatchScale()
			);
		}

		if (numCoh != 0) {
			cohVec = new Vec3f(
					((xCoh / numCoh) - transformObject.getPosition().getX()) * boid.getPerceivedCenterScale(),
					((yCoh / numCoh) - transformObject.getPosition().getY()) * boid.getPerceivedCenterScale(),
					((zCoh / numCoh) - transformObject.getPosition().getZ()) * boid.getPerceivedCenterScale()
			);
		}

		return alignVec.add(new Vec3f(xSep, ySep, zSep)).add(cohVec).add(new Vec3f(x, y, z));
	}
}
