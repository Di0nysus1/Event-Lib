package de.dion.eventmanager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import de.dion.eventmanager.events.Event;

/**
 * Der Classscanner durchsucht Klassen nach Events und trägt diese in den
 * eventTree ein
 */
public class ClassScanner<T> {

	private HashMap<Class<? extends Event>, ArrayList<CallObject<T>>> eventTree;

	public ClassScanner(HashMap<Class<? extends Event>, ArrayList<CallObject<T>>> eventTree) {
		this.eventTree = eventTree;
	}
	
	/**
	 * Sortiert alle Events im eventTree der Priorität nach<br>
	 * von <b>HIGHEST</b> bis <b>LOWEST</b>
	 * 
	 * @see de.dion.eventmanager.EventHandler
	 */
	public void sortEvents() {
		for (Class<? extends Event> cl : eventTree.keySet()) {

			ArrayList<CallObject<T>> oldMethods = eventTree.get(cl);
			ArrayList<CallObject<T>> newMethods = new ArrayList<>();

			newMethods.addAll(filterMethods(oldMethods, Priority.Highest));
			newMethods.addAll(filterMethods(oldMethods, Priority.High));
			newMethods.addAll(filterMethods(oldMethods, Priority.Normal));
			newMethods.addAll(filterMethods(oldMethods, Priority.Low));
			newMethods.addAll(filterMethods(oldMethods, Priority.Lowest));
			eventTree.replace(cl, newMethods);
		}
	}

	private ArrayList<CallObject<T>> filterMethods(ArrayList<CallObject<T>> methods, Priority priority) {
		ArrayList<CallObject<T>> newMethods = new ArrayList<>();

		for (CallObject<T> co : methods) {
			if (co.getPriority() == priority) {
				newMethods.add(co);
			}
		}
		return newMethods;
	}

	public void registerEvents(Collection<T> instances) {
		for (T type : instances) {
			registerEvents(type, getClass(type));
		}
	}

	@SuppressWarnings("unchecked")
	public void registerEvents(T... instances) {
		for (T type : instances) {
			registerEvents(type, getClass(type));
		}
	}

	private void registerEvents(T type, Class cl) {
		for (Method m : cl.getMethods()) {
			for (Annotation anno : m.getAnnotations()) {
				if (anno instanceof EventHandler) {
					checkMethod(type, m, (EventHandler) anno);
					break;
				}
			}
		}
	}

	private Class getClass(T type) {
		if (type instanceof Class) {
			return (Class) type;
		}
		return type.getClass();
	}

	@SuppressWarnings("unchecked")
	private void checkMethod(T type, Method m, EventHandler anno) {
		if (m.getParameterCount() == 1) {
			Class<?> paramType = m.getParameterTypes()[0];
			if (isEventClass(paramType)) {

				if (type instanceof Class) {
					if (Modifier.isStatic(m.getModifiers())) {
						saveMethod((Class<Event>) paramType, type, m, anno);
					} else {
						staticError();
					}
				} else {
					saveMethod((Class<Event>) paramType, type, m, anno);
				}
			}
		}
	}

	private void saveMethod(Class<Event> event, T type, Method m, EventHandler anno) {
		CallObject<T> co = new CallObject<>();
		co.setMethod(m);
		co.setType(type);
		co.setPriority(anno.value());
		co.setCallAlways(m.isAnnotationPresent(CallAlways.class));

		if (!eventTree.containsKey(event)) {
			ArrayList<CallObject<T>> methods = new ArrayList<>();
			methods.add(co);
			eventTree.put(event, methods);

		} else {
			ArrayList<CallObject<T>> methods = eventTree.get(event);

			for (CallObject<T> loopedCo : methods) {
				if (loopedCo.getMethod().equals(m)) {
					return;
				}
			}
			methods.add(co);
		}
	}

	private void staticError() {
		System.err.println();
		System.err.println("Wenn du bei registerEvents(...) eine Klasse statt ein Object angibst");
		System.err.println("müssen die event Methoden Statisch sein!");
		System.err.println("Beispiel:");
		System.err.println("eventManager.registerEvents(ChatListener.class);");
		System.err.println();
		System.err.println("@EventHandler");
		System.err.println("public static void onChat(PlayerChatEvent e) {}");
		System.err.println();
	}

	/**
	 * Prüft ob die Angegebe Klasse eine Unterinstanz der Klase Event ist
	 */
	private boolean isEventClass(Class<?> cl) {
		while (cl != Object.class) {
			cl = cl.getSuperclass();
			if (cl == Event.class) {
				return true;
			}
		}
		return false;
	}

}
