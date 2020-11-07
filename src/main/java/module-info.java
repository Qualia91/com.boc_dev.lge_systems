module com.nick.wood.game.engine.systems {
	exports com.nick.wood.game_engine.systems;
	exports com.nick.wood.game_engine.systems.control;
	exports com.nick.wood.game_engine.systems.generation;
	exports com.nick.wood.game_engine.systems.physics;
	exports com.nick.wood.game_engine.systems.boids;
	requires com.nick.wood.game_engine.event_bus;
	requires com.nick.wood.maths;
	requires com.nick.wood.rigid_body_dynamics;
	requires com.nick.wood.game_engine.gcs_model;
}