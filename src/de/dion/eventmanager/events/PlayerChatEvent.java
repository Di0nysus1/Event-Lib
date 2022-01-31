package de.dion.eventmanager.events;

import de.dion.eventmanager.Cancelable;
import de.dion.eventmanager.Interruptable;

public class PlayerChatEvent extends Event implements Cancelable, Interruptable {

	private final String sender;
	private String message;

	/**
	 * <B>Example Event!</B>
	 */
	public PlayerChatEvent(String sender, String message) {
		super();
		this.sender = sender;
		this.message = message;
		
		setInfo("Dieses Event wird aufgerufen wenn ein Spieler etwas in den Chat geschrieben hat");
		setAsynchronous(false);
	}

	/**
	 * {@link #getSender()} überladen
	 */
	public String getPlayer() {
		return getSender();
	}

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
