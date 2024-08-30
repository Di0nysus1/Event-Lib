package de.dion.client.eventmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import de.dion.client.eventmanager.events.Event;

/**
 * @version 1.0
 * @author Dionysus
 */
public abstract class EventManager<T> {

	private HashMap<Class<? extends Event>, ArrayList<CallObject<T>>> eventTree = new HashMap<>();
	ClassScanner<T> scanner;

	public EventManager() {
		scanner = new ClassScanner<>(eventTree);
	}

	/**
	 * L�scht alle gespeicherten Events aus dem eventTree
	 */
	public void unregisterAll() {
		eventTree.clear();
	}

	/**
	 * Sortiert alle Events im eventTree der Prioritaet nach<br>
	 * von <b>HIGHEST</b> bis <b>LOWEST</b>
	 * 
	 * @see EventHandler
	 */
	public void sortEvents() {
		scanner.sortEvents();
	}

	/**
	 * Diese Methode Scannt und sammelt die Event Methoden<br>
	 * aller angegebenen Listener.<br>
	 * Ein Event besteht aus einer Methode mit der @EventHandler Annotation<br>
	 * und einem Parameter als eine Klasse die Event extendet.<br>
	 * Beispiel: <br>
	 * <br>
	 * <code>
	 * EventHandler<br>
	 * public void onChat(PlayerChatEvent e) {}
	 * </code><br>
	 * <br>
	 * Parameter ist eine Instanz oder die Klasse des Listener:<br>
	 * <code>registerEvents(new ChatListener());</code><br>
	 * oder<br>
	 * <code>registerEvents(ChatListener.class);</code>
	 */
	@SuppressWarnings("unchecked")
	public void registerEvents(T... instances) {
		checkNull(instances);
		scanner.registerEvents(instances);
	}

	/**
	 * Funktioniert wie:<br>
	 * {@link #registerEvents(T[])}<br>
	 * Nur halt als Collection.<br>
	 * <br>
	 * Beispiel:<br>
	 * <code>registerEvents(ModuleManager.getAllModules());</code>
	 */
	public void registerEvents(Collection<? extends T> instances) {
		checkNull(instances);
		scanner.registerEvents(instances);
	}

	/**
	 * Ruft die Event Methoden in den gespeicherten Listenern auf<br>
	 * Parameter: Something extends Event
	 */
	public void call(Event event) {
		if (event.isAsynchronous()) {
			Thread asyncEvent = new Thread(new Runnable() {

				@Override
				public void run() {
					callListeners(event);
				}
			});
			asyncEvent.start();
		} else {
			callListeners(event);
		}
	}

	/**
	 * Wenn das Event Interrupted ist wird das aufrufen der Listener
	 * abgebrochen<br>
	 * Das Event wird nur aufgerufen wenn
	 * {@link #shouldCallEvent(Object, Event)} true returnt<br>
	 * oder die Event Methode die Annotation {@link CallAlways} gesetzt hat.<br>
	 * Beispiel:<br>
	 * <br>
	 * <code>@CallAlways<br>
	 * <code>@EventHandler(Priority.High)<br>
	 * public static void onChat(PlayerChatEvent e) {}
	 * </code>
	 */
	private void callListeners(Event event) {
		ArrayList<CallObject<T>> methods = eventTree.get(event.getClass());
		if (methods != null) {
			for (CallObject<T> co : methods) {
				if (event.isInterrupted()) {
					break;
				}
				if (shouldCallEvent(co.getType(), event)) {
					invoke(co, event);
				} else if (co.isCallAlways()) {
					invoke(co, event);
				}
			}
		}
	}

	private void invoke(CallObject<T> co, Event event) {
		try {
			co.getMethod().invoke(co.getType(), event);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Hier kannst du einzelne Listener black oder whitelisten<br>
	 * Beispiel:<br>
	 * <code>
	 * public boolean shouldCallEvent(Module listener, Event event) {<br>
	 * return listener.isEnabled();<br>
	 * } </code>
	 * 
	 * @see #callListeners(Event)
	 */
	public abstract boolean shouldCallEvent(T listener, Event event);

	/**
	 * Printet den Inhalt der Variable eventTree in aufgel�ster Darstellung<br>
	 * Darin sind alle Event Listener gespeichert
	 */
	public void printEventTree() {
		int counter = eventTree.size();
		String tab = "    ";

		System.out.println("EventTree: {");
		for (Class<? extends Event> cl : eventTree.keySet()) {
			System.out.println(tab + cl.getSimpleName() + ": {");

			Iterator<CallObject<T>> values = eventTree.get(cl).iterator();

			while (values.hasNext()) {
				System.out.println(tab + tab + values.next().toString());
				if (values.hasNext()) {
					System.out.println();
				}
			}

			System.out.print(tab + "}");
			if (counter > 1) {
				System.out.print(",");
				System.out.println();
			}
			System.out.println();
			counter--;
		}
		System.out.println("}");
	}

	private void checkNull(Object instances) {
		if (instances == null) {
			throw new IllegalArgumentException("Argument cannot be Null!");
		}
	}

}
