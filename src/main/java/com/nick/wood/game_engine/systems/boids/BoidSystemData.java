package com.nick.wood.game_engine.systems.boids;

public class BoidSystemData {

	private double maxSpeed = 10;
	private double minSpeed = 5;
	private double lengthAwayGroup2 = 800;
	private double lengthAwayMin2 = 200;
	private double boundScale = 10;
	private double velocityMatchScale = 0.5;
	private double antiCollideScale = 0.5;
	private double perceivedCenterScale = 0.5;
	private double goalScale = 0.5;
	private int numberOfBoids = 100;

	public BoidSystemData() {

	}

	public double getMinSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(double minSpeed) {
		this.minSpeed = minSpeed;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getLengthAwayGroup2() {
		return lengthAwayGroup2;
	}

	public void setLengthAwayGroup2(double lengthAwayGroup2) {
		this.lengthAwayGroup2 = lengthAwayGroup2;
	}

	public double getLengthAwayMin2() {
		return lengthAwayMin2;
	}

	public void setLengthAwayMin2(double lengthAwayMin2) {
		this.lengthAwayMin2 = lengthAwayMin2;
	}

	public double getBoundScale() {
		return boundScale;
	}

	public void setBoundScale(double boundScale) {
		this.boundScale = boundScale;
	}

	public double getVelocityMatchScale() {
		return velocityMatchScale;
	}

	public void setVelocityMatchScale(double velocityMatchScale) {
		this.velocityMatchScale = velocityMatchScale;
	}

	public double getAntiCollideScale() {
		return antiCollideScale;
	}

	public void setAntiCollideScale(double antiCollideScale) {
		this.antiCollideScale = antiCollideScale;
	}

	public double getPerceivedCenterScale() {
		return perceivedCenterScale;
	}

	public void setPerceivedCenterScale(double perceivedCenterScale) {
		this.perceivedCenterScale = perceivedCenterScale;
	}

	public double getGoalScale() {
		return goalScale;
	}

	public void setGoalScale(double goalScale) {
		this.goalScale = goalScale;
	}

	public int getNumberOfBoids() {
		return numberOfBoids;
	}

	public void setNumberOfBoids(int numberOfBoids) {
		this.numberOfBoids = numberOfBoids;
	}
}
