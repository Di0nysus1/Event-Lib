package de.dion.eventmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Überschreibt die shouldCall Abfrage im Eventmanager<br>
 * <br>
 * Kann zum Beispiel gebraucht werden wenn du ein Event im Listener<br>
 * aufrufen möchtest, obwohl dieser deaktiviert ist.
 * 
 * @see EventManager#shouldCallEvent
 * @see EventManager#callListeners
 * */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CallAlways {

}
