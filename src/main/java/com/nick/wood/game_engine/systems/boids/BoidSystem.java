package com.nick.wood.game_engine.systems.boids;

import com.nick.wood.maths.objects.vector.Vecd;
import com.nick.wood.maths.objects.vector.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class BoidSystem {


	private final Random rand = new Random();

	private final int mins[];
	private final int maxs[];

	private final double maxSpeed;
	private final double minSpeed;
	private final double lengthAwayGroup2;
	private final double lengthAwayMin2;
	private final double boundScale;
	private final double velocityMatchScale;
	private final double antiCollideScale;
	private final double perceivedCenterScale;
	private final double goalScale;
	private final int numberOfBoids;
	private final int vectorLength;
	private final Function<Boid, Boolean> collisionAvoidance;

	private Goal goal;

	private final ArrayList<Boid> boids;

	private final Vecd ZERO;

	public BoidSystem(int[] mins, int[] maxs, ArrayList<Boid> boids, BoidSystemData boidSystemData, Function<Boid, Boolean> collisionAvoidance) {

		assert mins.length == maxs.length;

		this.vectorLength = maxs.length;

		this.maxSpeed = boidSystemData.getMaxSpeed();
		this.minSpeed = boidSystemData.getMinSpeed();
		this.lengthAwayGroup2 = boidSystemData.getLengthAwayGroup2();
		this.lengthAwayMin2 = boidSystemData.getLengthAwayMin2();
		this.boundScale = boidSystemData.getBoundScale();
		this.velocityMatchScale = boidSystemData.getVelocityMatchScale();
		this.antiCollideScale = boidSystemData.getAntiCollideScale();
		this.perceivedCenterScale = boidSystemData.getPerceivedCenterScale();
		this.goalScale = boidSystemData.getGoalScale();
		this.numberOfBoids = boidSystemData.getNumberOfBoids();

		this.collisionAvoidance = collisionAvoidance;

		this.mins = mins;
		this.maxs = maxs;

		ZERO = Vector.ZERO(mins.length);

		this.boids = boids;

		goal = new Goal(ZERO, -1, false);

		for (int i = 0; i < numberOfBoids; i++) {

			boids.add(new Boid(
					Vector.Create(getVector(mins.length, (index) -> (double) rand.nextInt(maxs[index] - mins[index]) + mins[index])),
					Vector.Create(getVector(mins.length, (index) -> (double) rand.nextInt((int) (maxSpeed + minSpeed)) - minSpeed)),
					goal,
					Math.PI / 8,
					10f));
		}

	}

	private double[] getVector(int length, Function<Integer, Double> function) {
		double[] doubles = new double[length];

		for (int i = 0; i < length; i++) {
			doubles[i] = function.apply(i);
		}

		return doubles;
	}

	public void updatePositions(double timeStep) {

		for (Boid boid : boids) {

			// collision detection
			// Use function passed in to check weather boid need to avoid object in scene
			// and avoid it if necessary. if the function returns true, avoidance was necessary and the normal
			// update steps should not be preformed
			if (!collisionAvoidance.apply(boid)) {

				boid.setVelocity(
						boundVelocity(
								boid.getVelocity()
										.add(cohesion(boid))
										.add(separation(boid))
										.add(alignment(boid))
										.add(bound(boid))
										.add(goal(boid))));

			}

			boid.setPosition(boid.getPosition().add(boid.getVelocity().scale(timeStep)));

		}

	}

	private Vecd goal(Boid boid) {
		if (boid.getGoal().isActive()) {
			return boid.getGoal().getPosition().subtract(boid.getPosition()).normalise().scale(goalScale);
		} else if (goal.isActive()) {
			return goal.getPosition().subtract(boid.getPosition()).normalise().scale(goalScale);
		}
		return ZERO;
	}

	/**
	 * Bounding function.
	 * <p>
	 * Stops the boids flying outside the "game area".
	 *
	 * @param boid {@link Boid} which impulse is being calculated.
	 * @return {@link Vecd} Impulse calculated by bound function.
	 */
	private Vecd bound(Boid boid) {

		double[] elems = new double[vectorLength];

		for (int i = 0; i < vectorLength; i++) {
			if (boid.getPosition().get(i) < mins[i]) {
				elems[i] = (mins[i] - boid.getPosition().get(i)) * boundScale;
			} else if (boid.getPosition().get(i) > maxs[i]) {
				elems[i] = (maxs[i] - boid.getPosition().get(i)) * boundScale;
			}
		}

		return Vector.Create(elems);
	}

	private Vecd boundVelocity(Vecd vel) {

		double speed = vel.length();

		if (speed < minSpeed) {
			vel = vel.normalise().scale(minSpeed);
		} else if (speed > maxSpeed) {
			vel = vel.normalise().scale(maxSpeed);
		}

		return vel;
	}

	/**
	 * Alignment.
	 * <p>
	 * Will cause the boids to want to steer in an average velocity direction of the local group.
	 *
	 * @param boid {@link Boid} which impulse is being calculated.
	 * @return {@link Vecd} Impulse calculated by alignment function.
	 */
	private Vecd alignment(Boid boid) {

		// setup some variables we will need. num is to find the number of boids in the local group
		double[] vElems = new double[vectorLength];
		int num = 0;

		// loop through boids
		for (Boid otherBoid : boids) {

			if (!boid.equals(otherBoid)) {

				// see if the boid can see the other boid.
				// is if the dot product between the velocity vector and the vector from
				// the boid to the other boid is less than the FoV
				Vecd vectorToOtherBoid = otherBoid.getPosition().subtract(boid.getPosition());
				if (vectorToOtherBoid.dot(boid.getVelocity()) < boid.getFovAngle()) {

					// find the distance (squared) between the 2 boids and use if this distance is small enough
					if (Math.abs(vectorToOtherBoid.length2()) < lengthAwayGroup2) {

						// sum all the velocities that are close enough together
						for (int i = 0; i < vectorLength; i++) {
							vElems[i] += otherBoid.getVelocity().get(i);
						}

						num++;

					}
				}

			}

		}

		// if no boids are in the group, just return Zero
		if (num == 0) return ZERO;

		for (int i = 0; i < vectorLength; i++) {
			// new velocity = average velocity of group - boids current velocity all multiplied by a scale
			vElems[i] = ((vElems[i] / num) - (boid.getVelocity().get(i))) * velocityMatchScale;
		}

		return Vector.Create(vElems);

	}

	/**
	 * Separation.
	 * <p>
	 * Makes the boids keep some distance away from their neighbour boids so to not cause
	 * over crowding and eventual collisions.
	 *
	 * @param boid {@link Boid} which impulse is being calculated.
	 * @return {@link Vecd} Impulse calculated by separation function.
	 */
	private Vecd separation(Boid boid) {

		// Create an empty return array of the size of the vectors being used to update in the loop
		double[] e = new double[vectorLength];

		// loop over boids
		for (Boid otherBoid : boids) {

			// check you are not comparing the same boids
			if (!boid.equals(otherBoid)) {

				// see if the boid can see the other boid.
				// is if the dot product between the velocity vector and the vector from
				// the boid to the other boid is less than the FoV
				Vecd vectorToOtherBoid = otherBoid.getPosition().subtract(boid.getPosition());
				if (vectorToOtherBoid.dot(boid.getVelocity()) < boid.getFovAngle()) {

					// get the distance (squared for efficiency reasons) between the 2 boids and compare it to the user input
					// minimum length boids can be to each other.
					if (Math.abs(vectorToOtherBoid.length2()) < lengthAwayMin2) {

						// update impulse accordingly
						for (int i = 0; i < vectorLength; i++) {
							e[i] -= (otherBoid.getPosition().get(i) - boid.getPosition().get(i)) * antiCollideScale;
						}

					}

				}

			}

		}

		return Vector.Create(e);

	}

	/**
	 * Cohesion.
	 * <p>
	 * Will cause the boids to want to steer towards the center of mass of the local group.
	 *
	 * @param boid {@link Boid} which impulse is being calculated.
	 * @return {@link Vecd} Impulse calculated by cohesion function.
	 */
	private Vecd cohesion(Boid boid) {

		double[] e = new double[vectorLength];
		int num = 0;

		for (Boid otherBoid : boids) {

			if (!boid.equals(otherBoid)) {

				// see if the boid can see the other boid.
				// is if the dot product between the velocity vector and the vector from
				// the boid to the other boid is less than the FoV
				Vecd vectorToOtherBoid = otherBoid.getPosition().subtract(boid.getPosition());
				if (vectorToOtherBoid.dot(boid.getVelocity()) < boid.getFovAngle()) {

					if (Math.abs(vectorToOtherBoid.length2()) < lengthAwayGroup2) {

						for (int i = 0; i < vectorLength; i++) {
							e[i] += otherBoid.getPosition().get(i);
						}

						num++;

					}

				}

			}

		}

		if (num == 0) return ZERO;

		for (int i = 0; i < vectorLength; i++) {
			// new velocity = average position of group - boids current position all multiplied by a scale
			e[i] = ((e[i] / num) - boid.getPosition().get(i)) * perceivedCenterScale;
		}

		return Vector.Create(e);

	}

	public List<Boid> getBoids() {
		return boids;
	}

	public void setGoal(Goal goal) {

		this.goal = goal;

	}

	public Goal getGoal() {
		return goal;
	}
}
