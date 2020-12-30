package com.boc_dev.lge_systems.gui;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ButtonObject;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.SelectableObject;
import com.boc_dev.lge_model.generated.components.TimerObject;
import com.boc_dev.lge_model.systems.GcsSystem;

import java.util.HashSet;

public class ButtonSystem implements GcsSystem<ButtonObject> {
	@Override
	public void update(long time, HashSet<ButtonObject> components, Registry registry) {

		for (ButtonObject component : components) {

			// if active, check it
			if (component.getActive()) {

				// get selectableObject which is next to it
				if (component.getParent() != null) {

					for (Component child : component.getParent().getChildren()) {
						if (child.getComponentType().equals(ComponentType.SELECTABLE)) {

							SelectableObject selectableObject = (SelectableObject) child;

							if (selectableObject.getSelected()) {

								System.out.println(component.getFunctionName());

								selectableObject.getUpdater().setSelected(false).sendUpdate();

							}

							break;
						}
					}

				}

			}

		}
	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.BUTTON;
	}
}
