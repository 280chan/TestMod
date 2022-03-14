package testmod.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import basemod.ReflectionHacks;
import basemod.ReflectionHacks.RMethod;
import basemod.ReflectionHacks.RStaticMethod;

public class ClassCommandManager<T> {
	public static HashMap<String, ClassCommandManager> MAP = new HashMap<String, ClassCommandManager>();
	public static HashMap<String, Variable> VARIABLES = new HashMap<String, Variable>();
	public Class<T> c;
	
	public static class Variable<R> {
		public R r;
		public Variable(R r) {
			this.r = r;
		}
		public R get() {
			return r;
		}
	}
	
	public static void clearVariable() {
		VARIABLES.clear();
	}
	
	
	public ClassCommandManager(Class<T> c) {
		this.c = c;
		if (!MAP.containsValue(this)) {
			MAP.put(c.getSimpleName(), this);
		}
	}
	
	public boolean equals(Object o) {
		if (!(o instanceof ClassCommandManager))
			return false;
		return c.equals(((ClassCommandManager) o).c);
	}
	
	public <R> R getVariable(T t, String name) {
		return ReflectionHacks.getPrivate(t, c, name);
	}
	
	public <R> R getStaticVariable(String name) {
		return ReflectionHacks.getPrivateStatic(c, name);
	}
	
	public <R> void setVariable(T t, String name, R r) {
		
		ReflectionHacks.setPrivate(t, c, name, r);
	}
	
	public <R> void setStaticVariable(String name, R r) {
		ReflectionHacks.setPrivateStatic(c, name, r);
	}
	
	public RMethod getMethod(String name, Class<?>... parameterTypes) {
		return ReflectionHacks.privateMethod(c, name, parameterTypes);
	}
	
	public <R> R runMethod(T t, RMethod m, Object... para) {
		return m.invoke(t, para);
	}
	
	public RStaticMethod getStaticMethod(String name, Class<?>... parameterTypes) {
		return ReflectionHacks.privateStaticMethod(c, name, parameterTypes);
	}
	
	public <R> R runStaticMethod(RStaticMethod m, Object... para) {
		return m.invoke(para);
	}
	
	public Constructor<T> getCtor(Class<?>... para) {
		try {
			Constructor<T> ctor = c.getConstructor(para);
			ctor.setAccessible(true);
			return ctor;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public T newInstance(Constructor<T> ctor, Object... para) {
		try {
			return ctor.newInstance(para);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
