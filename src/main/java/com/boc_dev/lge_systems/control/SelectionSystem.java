package com.boc_dev.lge_systems.control;

import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.*;
import com.boc_dev.lge_model.systems.GcsSystem;

import java.util.HashSet;
import java.util.UUID;

public class SelectionSystem implements GcsSystem<SelectableObject> {

	@Override
	public void update(long time, HashSet<SelectableObject> components, Registry registry) {

			for (SelectableObject selectableObject : components) {
				// get parent geometry
				if (selectableObject.getParent().getComponentType().equals(ComponentType.GEOMETRY)) {
					GeometryObject geometryObject = (GeometryObject) selectableObject.getParent();

					// check if the material is correct given its selection state
					UUID selectionMaterialStateUUID = selectableObject.getSelectedMaterialUUID();
					if (!selectableObject.getSelected()) {
						selectionMaterialStateUUID = selectableObject.getUnselectedMaterialUUID();
					}

					if (!geometryObject.getMaterial().equals(selectionMaterialStateUUID)) {
						geometryObject.getUpdater().setMaterial(selectionMaterialStateUUID).sendUpdate();
					}
				}
			}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.SELECTABLE;
	}
}
