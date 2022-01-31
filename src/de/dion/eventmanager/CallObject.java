package de.dion.eventmanager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class CallObject<T> {

	private Method method;
	private T type;
	private Priority priority;

	public CallObject(Method method, T type, Priority priority) {
		this.method = method;
		this.type = type;
		this.priority = priority;
	}

	public CallObject() {
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public T getType() {
		return type;
	}

	public void setType(T type) {
		this.type = type;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/**
	 * Example Output:<br>
	 * <code>void onChat (High): ChatListener()</code> oder<br>
	 * <code>static void onChat2 (Low): ChatListener.class</code><br>
	 * <br>
	 * Wenn eine Klasse als Typ angegeben wurde ist die Endung ".class"<br>
	 * Bei einem Object ist es "()"<br>
	 * <br>
	 * Ist die Methode Statisch wird davor ein static gesetzt.
	 */
	@Override
	public String toString() {
		String out = Modifier.isStatic(method.getModifiers()) ? "static " : "";
		out += method.getGenericReturnType().getTypeName();
		out += " ";
		out += method.getName();
		out += " (" + priority + "): ";
		if (type instanceof Class) {
			String clName = type.toString();
			clName = clName.substring(clName.lastIndexOf(".") + 1, clName.length());
			out += clName + ".class";
		} else {
			out += type.getClass().getSimpleName() + "()";
		}
		return out;
	}
}
