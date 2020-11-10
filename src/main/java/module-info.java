module com.boc_dev.lge_systems {
	exports com.boc_dev.lge_systems;
	exports com.boc_dev.lge_systems.control;
	exports com.boc_dev.lge_systems.generation;
	exports com.boc_dev.lge_systems.physics;
	exports com.boc_dev.lge_systems.boids;
	requires com.boc_dev.event_bus;
	requires com.boc_dev.maths;
	requires com.boc_dev.physics_library;
    requires com.boc_dev.lge_model;
}