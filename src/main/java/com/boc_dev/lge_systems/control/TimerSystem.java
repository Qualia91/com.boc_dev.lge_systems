package com.boc_dev.lge_systems.control;

import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.TimerObject;
import com.boc_dev.lge_model.systems.GcsSystem;

import java.util.HashSet;

public class TimerSystem implements GcsSystem<TimerObject> {
	@Override
	public void update(long time, HashSet<TimerObject> components, Registry registry) {

		for (TimerObject component : components) {

			// if active, check it
			if (component.getActive()) {

				if (component.getTimeoutFlag()) {
					// todo this will call function, somehow. Probably add function name of execute component, then a system calls them
					System.out.println(component.getFunctionName() + " " + time);
					if (component.getRepeate()) {
						component.getUpdater().setStartFrame(time - 1).setTimeoutFlag(false).sendUpdate();
					} else {
						component.getUpdater().setActive(false).setTimeoutFlag(false).sendUpdate();
					}
				} else {
					if (component.getStartFrame() + component.getTimeoutLength() <= time) {
						component.getUpdater().setTimeoutFlag(true).sendUpdate();
					}
				}

			} else {
				// if not active, but someone has set a start time that happens to be this time, restart it
				if (component.getStartFrame() == time) {
					component.getUpdater().setActive(true).setTimeoutFlag(false).sendUpdate();
				}
			}

		}
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.TIMER;
	}
}
