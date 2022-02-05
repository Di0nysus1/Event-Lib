package de.dion.eventmanager.example;

import de.dion.eventmanager.CallAlways;
import de.dion.eventmanager.EventHandler;
import de.dion.eventmanager.Priority;
import de.dion.eventmanager.events.PlayerChatEvent;

public class ChatListener {

	@EventHandler(Priority.Normal)
	public static void onChat(PlayerChatEvent e) {
		String msg = e.getMessage();

		if (msg.contains("windows") && msg.contains("kacke")) {
			e.setMessage(msg.replace("kacke", "gut"));
		}
		if (msg.toLowerCase().contains("apple")) {
			e.setCanceled(true);
		}
	}

	@CallAlways
	@EventHandler(Priority.High)
	public static void onChat2(PlayerChatEvent e) {
//		e.interrupt();
	}

}
