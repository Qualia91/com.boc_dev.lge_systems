package com.nick.wood.game_engine.systems.boids;

import com.nick.wood.maths.objects.vector.Vecd;

import java.util.UUID;

public class Boid {

	private final UUID uuid;
	private Vecd position;
	private Vecd velocity;
	private Goal goal;
	private double fovAngle;
	private double fovRange;

	public Boid(Vecd position, Vecd velocity, Goal goal, double fovAngle, double fovRange) {
		this.uuid = UUID.randomUUID();
		this.position = position;
		this.velocity = velocity;
		this.goal = goal;
		this.fovAngle = fovAngle;
		this.fovRange = fovRange;
	}

	public UUID getUuid() {
		return uuid;
	}

	public Vecd getPosition() {
		return position;
	}

	public void setPosition(Vecd position) {
		this.position = position;
	}

	public Vecd getVelocity() {
		return velocity;
	}

	public void setVelocity(Vecd velocity) {
		this.velocity = velocity;
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
	}

	public double getFovAngle() {
		return fovAngle;
	}

	public void setFovAngle(double fovAngle) {
		this.fovAngle = fovAngle;
	}

	public double getFovRange() {
		return fovRange;
	}

	public void setFovRange(double fovRange) {
		this.fovRange = fovRange;
	}
}
