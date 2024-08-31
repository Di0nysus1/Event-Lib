# EventManager Library

## Übersicht

Die **EventManager** Library ist eine flexible und leistungsstarke Event-Verwaltungslösung für Java. Sie ermöglicht das einfache Registrieren, Verwalten und Auslösen von Events, wobei Listener dynamisch aufgerufen werden. Die Bibliothek unterstützt Prioritäten für Event-Listener, asynchrone Events und die Verarbeitung von Superklassen-Events.

## Funktionen

- **Einfache Registrierung von Listenern**: Listener können entweder als Klassen oder Instanzen registriert werden.
- **Prioritätsbasierte Listener**: Listener können mit unterschiedlichen Prioritäten registriert werden, um die Reihenfolge der Ausführung zu steuern.
- **Asynchrone Events**: Unterstützung für asynchrone Event-Ausführung, um langwierige Aufgaben parallel zu verarbeiten.
- **Super Listener**: Möglichkeit, Listener für übergeordnete Klassen von Events zu definieren, die auch ausgelöst werden, wenn ein abgeleitetes Event auftritt.
- **Flexible Event-Filterung**: Implementiere benutzerdefinierte Logik, um zu steuern, welche Listener für ein bestimmtes Event aufgerufen werden sollen.

## Installation

Füge die **EventManager** Library zu deinem Projekt hinzu, indem du die `.jar`-Datei in deinen Build-Pfad einbindest oder die Quellen direkt in dein Projekt integrierst.

## Verwendung

Hier ein Beispiel, wie die **EventManager** Library in einem Java-Projekt verwendet werden kann:

### 1. Erstellen eines Event-Listeners

```java
package de.dion.eventmanager.example;

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

    @EventHandler(Priority.High)
    public static void onChat2(PlayerChatEvent e) {
        // z.B. e.interrupt();
    }
}
```

### 2. Registrieren des Listeners im EventManager
```java

package de.dion.eventmanager.example;

import de.dion.eventmanager.EventManager;
import de.dion.eventmanager.events.PlayerChatEvent;

public class ExampleMain {

    static EventManager<Object> em1 = new EventManager<Object>() {
        @Override
        public boolean shouldCallEvent(Object listener, Event event) {
            return true;
        }
    };

    public static void main(String[] args) {
        // Listener registrieren
        ChatListener listener = new ChatListener();
        em1.registerEvents(listener);
        em1.sortEvents();

        // Events auslösen
        chat("Peter", "windows ist kacke");
        chat("Kevin", "Gönn dir Apple Macbook");
        chat("Karlotto", "linux ist kompliziert");
    }

    public static void chat(String name, String message) {
        PlayerChatEvent e = new PlayerChatEvent(name, message);
        em1.call(e);

        if (!e.isCanceled()) {
            System.out.println(name + " -> " + e.getMessage());
        }
    }
}
```

### 3. Events auslösen und verarbeiten
In diesem Beispiel wird eine Chatnachricht gesendet und das Event PlayerChatEvent verarbeitet. Basierend auf den Bedingungen in den Listenern wird die Nachricht entweder geändert, blockiert oder durchgelassen.

## Weitere Informationen

Die EventManager Library bietet eine Vielzahl weiterer Funktionen und Erweiterungsmöglichkeiten. Weitere Details und vollständige API-Dokumentation findest du in der Dokumentation und im Quellcode.

## Lizenz

Dieses Projekt steht unter der MIT-Lizenz. Siehe die [LICENSE](LICENSE)-Datei für weitere Details.

## Mitwirken

Beiträge, Fehlerberichte und Feature-Vorschläge sind willkommen! Bitte erstelle ein Issue oder sende einen Pull-Request auf GitHub.

---

Vielen Dank für die Nutzung der **EventManager** Library!

Diese README.md Datei wurde mit ChatGPT erstellt.


