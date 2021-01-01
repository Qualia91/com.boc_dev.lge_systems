package com.boc_dev.lge_systems.gui;

import com.boc_dev.lge_model.gcs.Registry;
import com.boc_dev.lge_model.generated.components.*;
import com.boc_dev.lge_model.systems.GcsSystem;
import com.boc_dev.maths.objects.QuaternionF;
import com.boc_dev.maths.objects.matrix.Matrix4f;
import com.boc_dev.maths.objects.srt.Transform;
import com.boc_dev.maths.objects.srt.TransformBuilder;
import com.boc_dev.maths.objects.vector.Vec3f;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.UUID;

public class TextChangeSystem implements GcsSystem<TextObject> {

	private final DecimalFormat df = new DecimalFormat("#.00");

	@Override
	public void update(long timeStep, HashSet<TextObject> textObjects, Registry registry) {

		for (TextObject textObject : textObjects) {
			if (textObject.getName().equals("GameEngineTimeText")) {
				textObject.getUpdater().setText(String.valueOf(timeStep)).sendUpdate();
				//textObject.getUpdater().setText(String.valueOf(df.format(timeStep * 0.02))).sendUpdate();
			}
		}

	}

	@Override
	public ComponentType getTypeSystemUpdates() {
		return ComponentType.TEXT;
	}
}
