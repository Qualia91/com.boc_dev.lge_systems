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
import com.boc_dev.lge_model.systems.GcsSystem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class PickingSystem implements GcsSystem<PickableObject>, Subscribable {

	private final Set<Class<?>> supports = new HashSet<>();
	private final ArrayBlockingQueue<PickingResponseEventData> pickingResponseQueue = new ArrayBlockingQueue<>(10);
	private final ArrayList<PickingResponseEventData> pickingResponseList = new ArrayList<>();

	public PickingSystem() {
		this.supports.add(PickingEvent.class);
	}

	@Override
	public void handle(Event<?> event) {
		if (event.getType().equals(PickingEventType.RESPONSE)) {
			PickingEvent pickingEvent = (PickingEvent) event;
			pickingResponseQueue.offer((PickingResponseEventData)pickingEvent.getData());
		}
	}

	@Override
	public boolean supports(Class<? extends Event> aClass) {
		return supports.contains(aClass);
	}

	@Override
	public void update(long time, HashSet<PickableObject> components, Registry registry) {

		// get picking responses
		pickingResponseQueue.drainTo(pickingResponseList);

		for (PickingResponseEventData pickingResponseEventData : pickingResponseList) {
			// see if it is for this layer
			if (pickingResponseEventData.getSceneLayerName().equals(registry.getLayerName())) {
				// loop over components and find parent (pickable is child of pickable objects)
				for (PickableObject component : components) {
					if (component.getActive() && component.getParent() != null) {
						Component parent = component.getParent();
						if (parent.getUuid().equals(pickingResponseEventData.getUuid())) {
							System.out.println(parent.getName());

							// todo testing
							//parent.getParent().getUpdater().delete();
						}
					}
				}
			}
		}


		pickingResponseList.clear();
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.PICKABLE;
	}
}
