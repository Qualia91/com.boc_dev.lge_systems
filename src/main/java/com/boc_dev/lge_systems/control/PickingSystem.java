package com.boc_dev.lge_systems.control;

import com.boc_dev.event_bus.event_data.PickingResponseEventData;
import com.boc_dev.event_bus.event_types.PickingEventType;
import com.boc_dev.event_bus.events.PickingEvent;
import com.boc_dev.event_bus.interfaces.Event;
import com.boc_dev.event_bus.interfaces.Subscribable;
import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.GeometryObject;
import com.boc_dev.lge_model.generated.components.PickableObject;
import com.boc_dev.lge_model.generated.components.SelectableObject;
import com.boc_dev.lge_model.systems.GcsSystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class PickingSystem implements GcsSystem<GeometryObject>, Subscribable {

	private final Set<Class<?>> supports = new HashSet<>();
	private final ArrayBlockingQueue<UUID> pickingResponseQueue = new ArrayBlockingQueue<>(10);
	private final ArrayList<UUID> pickingResponseList = new ArrayList<>();

	public PickingSystem() {
		this.supports.add(PickingEvent.class);
	}

	@Override
	public void handle(Event<?> event) {
		if (event.getType().equals(PickingEventType.RESPONSE)) {
			PickingEvent pickingEvent = (PickingEvent) event;
			pickingResponseQueue.offer(((PickingResponseEventData) pickingEvent.getData()).getUuid());
		}
	}

	@Override
	public boolean supports(Class<? extends Event> aClass) {
		return supports.contains(aClass);
	}

	@Override
	public void update(long time, HashSet<GeometryObject> components, Registry registry) {

		// get picking responses
		pickingResponseQueue.drainTo(pickingResponseList);

		if (!pickingResponseList.isEmpty()) {

			// loop over components and find parent (pickable is child of pickable objects)
			for (GeometryObject geometryObject : components) {
				for (Component child : geometryObject.getChildren()) {
					if (child.getComponentType().equals(ComponentType.SELECTABLE)) {
						SelectableObject selectableObject = (SelectableObject) child;
						if (pickingResponseList.contains(geometryObject.getUuid())) {
							selectableObject.getUpdater().setSelected(true).sendUpdate();
						} else if (pickingResponseList.contains(PickingResponseEventData.NO_DATA_SELECTED)) {
							selectableObject.getUpdater().setSelected(false).sendUpdate();
						} else {
							selectableObject.getUpdater().setSelected(false).sendUpdate();
						}
						break;
					}
				}

			}

		}

		pickingResponseList.clear();
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.GEOMETRY;
	}
}
