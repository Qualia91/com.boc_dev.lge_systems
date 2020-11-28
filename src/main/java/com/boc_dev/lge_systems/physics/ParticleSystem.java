package com.boc_dev.lge_systems.physics;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.*;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.physics_library.particle_system_dynamics_verbose.*;

import java.util.*;

public class ParticleSystem implements GcsSystem<ParticleBodyObject> {

	private final ParticleSimulation particleSimulation = new ParticleSimulation();

	@Override
	public void update(long time, HashSet<ParticleBodyObject> particleBodyObjects, Registry registry) {

		ArrayList<Particle> particles = new ArrayList<>();

		for (ParticleBodyObject particleBodyObject : particleBodyObjects) {

			// get parent transform
			if (particleBodyObject.getParent() != null && particleBodyObject.getParent().getComponentType().equals(ComponentType.TRANSFORM)) {

				TransformObject transformObject = (TransformObject) particleBodyObject.getParent();

				// get forces
				ArrayList<NaryForce> forces = new ArrayList<>();
				for (Component child : particleBodyObject.getChildren()) {
					if (child.getComponentType().equals(ComponentType.GRAVITY)) {
						forces.add(new SimpleGravity(((GravityObject) child).getG()));
					} else if (child.getComponentType().equals(ComponentType.PARTICLESPRING)) {
						ParticleSpringObject spring = (ParticleSpringObject) child;
						forces.add(new Spring(spring.getRestLength(), spring.getSpringConstant(), spring.getDampingConstant()));
					} else if (child.getComponentType().equals(ComponentType.VISCOUSDRAG)) {
						forces.add(new ViscousDrag(((ViscousDragObject) child).getCoefficientOfDrag()));
					}
				}

				particles.add(new Particle(
						particleBodyObject.getUuid(),
						particleBodyObject.getMass(),
						transformObject.getPosition().toVecd(),
						particleBodyObject.getVelocity(),
						forces
				));

			}
		}

		particleSimulation.iterate(0.02, particles, new ArrayList());

		Iterator<Particle> particleIterator = particles.iterator();
		while (particleIterator.hasNext()) {

			Particle next = particleIterator.next();

			// find particle in sim
			for (ParticleBodyObject particleBodyObject : particleBodyObjects) {

				if (next.getUuid().equals(particleBodyObject.getUuid())) {

					particleBodyObject.getUpdater().setVelocity(next.getVelocity()).sendUpdate();

					if (particleBodyObject.getParent() != null && particleBodyObject.getParent().getComponentType().equals(ComponentType.TRANSFORM)) {

						TransformObject transformObject = (TransformObject) particleBodyObject.getParent();

						transformObject.getUpdater()
								.setPosition(next.getPosition().toVec3f())
								.sendUpdate();
					}
				}

			}

			particleIterator.remove();
		}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.PARTICLEBODY;
	}
}
