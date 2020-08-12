package com.nick.wood.game_engine.systems.boids;

import com.nick.wood.maths.objects.vector.Vecd;

public class Goal {

	private final Vecd position;
	private final int goalNumber;
	private final boolean active;

	public Goal(Vecd position, int goalNumber, boolean active) {
		this.position = position;
		this.goalNumber = goalNumber;
		this.active = active;
	}

	public Vecd getPosition() {
		return position;
	}

	public int getGoalNumber() {
		return goalNumber;
	}

	public boolean isActive() {
		return active;
	}
}
