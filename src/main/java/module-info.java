module com.nick.wood.game.engine.systems {
	exports com.nick.wood.game_engine.systems;
	exports com.nick.wood.game_engine.systems.control;
	exports com.nick.wood.game_engine.systems.generation;
	requires com.nick.wood.game_engine.event_bus;
	requires com.nick.wood.game_engine.model;
	requires com.nick.wood.maths;
}