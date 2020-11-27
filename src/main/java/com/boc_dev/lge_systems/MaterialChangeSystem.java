package com.boc_dev.lge_systems;

import com.boc_dev.lge_model.gcs.Component;
import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.ComponentType;
import com.boc_dev.lge_model.generated.components.GeometryObject;
import com.boc_dev.lge_model.generated.components.MaterialObject;
import com.boc_dev.lge_model.generated.components.TransformObject;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.objects.QuaternionF;
import com.boc_dev.maths.objects.matrix.Matrix4f;
import com.boc_dev.maths.objects.srt.Transform;
import com.boc_dev.maths.objects.srt.TransformBuilder;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.util.HashSet;
import java.util.UUID;

public class MaterialChangeSystem implements GcsSystem<MaterialObject> {

	private final QuaternionF rotation = QuaternionF.RotationX(0.1f);

	@Override
	public void update(long timeStep, HashSet<MaterialObject> geometryObjects, Registry registry) {

		UUID materialUUID = null;
		if (timeStep == 500) {
			for (MaterialObject materialObject : geometryObjects) {
				if (materialObject.getName().equals("material1")) {
					materialUUID = materialObject.getUuid();
					System.out.println("material1");
				}
			}
		} else if (timeStep == 750) {
			for (MaterialObject materialObject : geometryObjects) {
				if (materialObject.getName().equals("material2")) {
					materialUUID = materialObject.getUuid();
					System.out.println("material2");
				}
			}
		} else if (timeStep == 1000) {
			for (MaterialObject materialObject : geometryObjects) {
				if (materialObject.getName().equals("material3")) {
					materialUUID = materialObject.getUuid();
					System.out.println("material3");
				}
			}
		} else if (timeStep == 1250) {
			for (MaterialObject materialObject : geometryObjects) {
				if (materialObject.getName().equals("material4")) {
					materialUUID = materialObject.getUuid();
					System.out.println("material4");
				}
			}
		}

		if (materialUUID != null) {
			// now loop over geometries and change materials
			for (Component component : registry.getComponentMap().get(ComponentType.GEOMETRY)) {
				GeometryObject geometryObject = (GeometryObject) component;
				geometryObject.getUpdater().setMaterial(materialUUID).sendUpdate();
			}

		}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.MATERIAL;
	}
}
