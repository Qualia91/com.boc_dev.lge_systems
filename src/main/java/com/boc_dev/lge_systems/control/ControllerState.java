package com.boc_dev.lge_systems.control;

import com.boc_dev.event_bus.event_data.MoveEventData;
import com.boc_dev.event_bus.event_data.PressEventData;
import com.boc_dev.event_bus.event_types.ManagementEventType;
import com.boc_dev.event_bus.events.ControlEvent;
import com.boc_dev.event_bus.events.ManagementEvent;
import com.boc_dev.event_bus.interfaces.Event;
import com.boc_dev.event_bus.interfaces.Subscribable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class ControllerState implements Subscribable, Runnable {

	private final Set<Class<?>> supports = new HashSet<>();
	private final ArrayBlockingQueue<Event<?>> eventQueue = new ArrayBlockingQueue<>(50);
	private final ArrayList<Event<?>> drainToList = new ArrayList<>();

	private boolean[] keys = new boolean[348];
	private boolean[] buttons = new boolean[7];
	private double mouseX = 0;
	private double mouseY = 0;
	private double dX = 0;
	private double dY = 0;
	private double offsetX = 0;
	private double offsetY = 0;

	public ControllerState() {
		supports.add(ControlEvent.class);
		supports.add(ManagementEvent.class);
	}

	public void run() {

		while (true) {

			eventQueue.drainTo(drainToList);

			for (Event<?> event : drainToList) {

				if (event instanceof ManagementEvent) {

					ManagementEvent managementEvent = (ManagementEvent) event;

					if (managementEvent.getType().equals(ManagementEventType.SHUTDOWN)) {
						return;
					}

				} else {

					actionEvent(event);

				}

			}

			drainToList.clear();

		}

	}

	private void actionEvent(Event<?> event) {

		if (event instanceof ControlEvent) {
			ControlEvent controlEvent = (ControlEvent) event;
			switch(controlEvent.getType()) {
				case KEY:
					PressEventData keyPressEventData = (PressEventData) controlEvent.getData();
					keys[keyPressEventData.getKey()] = keyPressEventData.getAction() == 1;
					break;

				case MOUSE_BUTTON:
					PressEventData mouseButtonPressEventData = (PressEventData) controlEvent.getData();
					buttons[mouseButtonPressEventData.getKey()] = mouseButtonPressEventData.getAction() == 1;
					break;

				case MOUSE:
					MoveEventData mouseMoveEventData = (MoveEventData) controlEvent.getData();
					double newMouseX = mouseMoveEventData.getXAxis();
					double newMouseY = mouseMoveEventData.getYAxis();
					if (Math.abs(mouseX) <= 0.000001) {
						mouseX = newMouseX;
					}
					if (Math.abs(mouseY) <= 0.000001) {
						mouseY = newMouseY;
					}
					dX = newMouseX - mouseX;
					dY = newMouseY - mouseY;
					mouseX = newMouseX;
					mouseY = newMouseY;
					break;

				case SCROLL:
					MoveEventData scrollMoveEventData = (MoveEventData) controlEvent.getData();
					offsetX += scrollMoveEventData.getXAxis();
					offsetY += scrollMoveEventData.getYAxis();
					break;
			}
		}
		
	}

	public boolean[] getKeys() {
		return keys;
	}

	public boolean[] getButtons() {
		return buttons;
	}

	public double getOffsetX() {
		return offsetX;
	}

	public double getOffsetY() {
		return offsetY;
	}

	public boolean isKeyPressed(int key) {
		return keys[key];
	}

	@Override
	public void handle(Event<?> event) {
		eventQueue.offer(event);
	}

	@Override
	public boolean supports(Class<? extends Event> aClass) {
		return supports.contains(aClass);
	}

	public double getMouseDelX() {
		return dX;
	}

	public double getMouseDelY() {
		return dY;
	}

	public void setMouseDelX(int delX) {
		dX = delX;
	}

	public void setMouseDelY(int delY) {
		dY = delY;
	}
}
