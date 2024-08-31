package de.dion.client.eventmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import de.dion.client.eventmanager.events.Event;

/**
 * @version 1.1
 * @author Dionysus
 */
public abstract class EventManager<T> {

	private HashMap<Class<? extends Event>, ArrayList<CallObject<T>>> eventTree = new HashMap<>();
	private boolean allowSuperListeners = false;
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
	 * Ruft die Listener für das angegebene Event auf, es sei denn, das Event ist als unterbrochen markiert.
	 *
	 * Das Event wird nur dann verarbeitet, wenn {@link #shouldCallEvent(Object, Event)} TRUE zurückgibt
	 * oder die Event-Methode mit der Annotation {@link CallAlways} versehen ist.
	 *
	 * Beispiel:
	 * <pre>
	 * {@literal @}CallAlways
	 * {@literal @}EventHandler(Priority.High)
	 * public static void onChat(PlayerChatEvent e) {}
	 * </pre>
	 *
	 * @param event Das Event, dessen Listener aufgerufen werden sollen.
	 */
	private void callListeners(Event event) {
		callListeners(event, event.getClass());
		if (isAllowSuperListeners()) {
			callSuperListeners(event);
		}
	}

	/**
	 * Ruft die Listener für die übergeordneten Klassen des Events auf, um die Vererbungshierarchie zu berücksichtigen.
	 *
	 * Diese Methode wird aufgerufen, wenn {@link #isAllowSuperListeners()} TRUE ist und durchläuft
	 * die Superklassen des Events, um entsprechende Listener aufzurufen.
	 *
	 * @param event Das Event, dessen Listener für die Superklassen aufgerufen werden sollen.
	 */
	private void callSuperListeners(Event event) {
		Class<?> superclass = event.getClass().getSuperclass();
		while (superclass != Object.class) {
			callListeners(event, superclass);
			superclass = superclass.getSuperclass();
		}
	}

	/**
	 * Ruft die Listener für eine spezifische Klasse des Events auf.
	 *
	 * Diese Methode wird sowohl für die eigentliche Eventklasse als auch für deren Superklassen verwendet.
	 * Sie prüft, ob das Event unterbrochen ist und ob die Listener durch {@link #shouldCallEvent(Object, Event)}
	 * oder die {@link CallAlways}-Annotation aufgerufen werden sollen.
	 *
	 * @param event Das Event, dessen Listener aufgerufen werden sollen.
	 * @param eventClass Die spezifische Klasse des Events, für die Listener aufgerufen werden.
	 */
	private void callListeners(Event event, Class<?> eventClass) {
		ArrayList<CallObject<T>> methods = eventTree.get(eventClass);
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

	public boolean isAllowSuperListeners() {
		return allowSuperListeners;
	}

	/**
	 * Legt fest, ob Listener, die direkt das generische "Event" abhören, ebenfalls aufgerufen werden sollen.
	 * Wenn auf TRUE gesetzt, werden auch solche Listener benachrichtigt, die auf das generische Event reagieren.
	 *
	 * Beispiel:
	 * <pre>
	 * {@literal @}EventHandler
	 * public void onEvent(Event event) {}
	 * </pre>
	 *
	 * @param allowSuperListeners TRUE, wenn Listener für generische Events aufgerufen werden sollen, andernfalls FALSE.
	 */
	public void setAllowSuperListeners(boolean allowSuperListeners) {
		this.allowSuperListeners = allowSuperListeners;
	}
}
