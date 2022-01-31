package de.dion.eventmanager.events;

import de.dion.eventmanager.Cancelable;
import de.dion.eventmanager.Interruptable;

public class Event {

	private boolean canceled = false;
	private boolean asynchronous = false;
	private boolean interrupted;
	private String info;

	protected Event() {
	}

	/**
	 * Ist nur m�glich wenn das Event {@link Cancelable} implementiert
	 */
	public void setCanceled(boolean canceled) {
		if (this instanceof Cancelable) {
			this.canceled = canceled;
		}
	}

	public boolean isCanceled() {
		return canceled;
	}

	public boolean isAsynchronous() {
		return asynchronous;
	}

	/**
	 * Wenn true wird das Event beim callen im neuen Thread aufgerufen damit der
	 * code an der Stelle nicht h�ngen bleibt
	 */
	protected void setAsynchronous(boolean asynchronous) {
		if (this instanceof Cancelable && asynchronous) {
			throw new IllegalStateException("Das Event kann nicht gleichzeitig Cancelable und Asynchronous sein!");
		}
		this.asynchronous = asynchronous;
	}

	/**
	 * Kann freiwillig gesetzt werden
	 */
	protected void setInfo(String info) {
		this.info = info;
	}

	public boolean isInterrupted() {
		return interrupted;
	}
	
	/**
	 * Ist nur m�glich wenn das Event {@link Interruptable} implementiert<br>
	 * Stoppt das weitere callen der Listener von diesem einen Event
	 * @see de.dion.eventmanager.EventManager#callListeners(Event)
	 */
	public void interrupt() {
		if (this instanceof Interruptable) {
			interrupted = true;
		}
	}

	/**
	 * Returnt eine Beschreibung / einige Infos �ber dieses Event
	 */
	public String getInfo() {
		return info;
	}

	public boolean hasInfo() {
		return info != null;
	}

}
