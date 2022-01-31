package de.dion.eventmanager.example;

import java.io.IOException;

import de.dion.eventmanager.EventManager;
import de.dion.eventmanager.events.Event;
import de.dion.eventmanager.events.PlayerChatEvent;
import de.dion.eventmanager.utils.ClassHelper;

public class ExampleMain {

	static EventManager<Object> em1 = new EventManager<Object>() {

		@Override
		public boolean shouldCallEvent(Object listener, Event event) {
			return true;
		}
	};
	static EventManager<Class> em2 = new EventManager<Class>() {

		@Override
		public boolean shouldCallEvent(Class listener, Event event) {
			return true;
		}
	};
	static EventManager<Class> em3 = new EventManager<Class>() {

		@Override
		public boolean shouldCallEvent(Class listener, Event event) {
			return true;
		}
	};

	public static void main(String[] args) {
		// Möglichkeit 1
		ChatListener listener = new ChatListener();
		em1.registerEvents(listener);
		em1.sortEvents();

		// Möglichkeit 2
		em2.registerEvents(ChatListener.class);
		em2.sortEvents();

		// Möglichkeit 3
		try {
			em3.registerEvents(ClassHelper.getClasses("de.dion.eventmanager.example"));
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		em3.sortEvents();
		
		
		//EventTree anzeigen
		em3.printEventTree();
		System.out.println();
		
		chat("Peter", "windows ist kacke");
		chat("Kevin", "Gönn dir Apple Macbook");
		chat("Karlotto", "linux ist kompliziert");
	}
	
	public static void chat(String name, String message) {
		PlayerChatEvent e = new PlayerChatEvent(name, message);
		call(e);
		
		if (!e.isCanceled()) {
			System.out.println(name + " -> " + e.getMessage());
		}
	}

	public static void call(Event event) {
		em1.call(event);
		em2.call(event);
		em3.call(event);
	}

}
