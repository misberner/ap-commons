package com.github.misberner.apcommons.util;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.github.misberner.apcommons.util.TypeUtils.TypeMatcher;

/**
 * Checks whether the specified method is one of the methods declared by the {@link Object} class.
 * Hence, the method returns <code>true</code> if the specified method is one of
 * <ul>
 * <li>{@link Object#clone()}</li>
 * <li>{@link Object#finalize()}</li>
 * <li>{@link Object#getClass()}</li>
 * <li>{@link Object#hashCode()}</li>
 * <li>{@link Object#notify()}</li>
 * <li>{@link Object#notifyAll()}</li>
 * <li>{@link Object#toString()}</li>
 * <li>{@link Object#wait()}</li>
 * <li>{@link Object#equals(Object)}</li>
 * <li>{@link Object#wait(long)}</li>
 * <li>{@link Object#wait(long, int)}</li>
 * </ul>
 * 
 * @param method the method to check
 * @return <tt>true</tt> if this method was declared by {@link Object}, <tt>false</tt> otherwise.
 */
public enum ObjectMethod {
	
	CLONE("clone", null),
	FINALIZE("finalize", void.class),
	GET_CLASS("getClass", Class.class),
	HASH_CODE("hashCode", int.class),
	NOTIFY("notify", void.class),
	NOTIFY_ALL("notifyAll", void.class),
	TO_STRING("toString", String.class),
	WAIT("wait", void.class),
	EQUALS("equals", boolean.class, Object.class),
	WAIT1("wait", void.class, long.class),
	WAIT2("wait", void.class, long.class, int.class)
	;
	
	private static final ObjectMethod[] NO_METHODS = {};
	
	private static final ObjectMethod[] ARITY_0_METHODS = {
		CLONE,
		FINALIZE,
		GET_CLASS,
		HASH_CODE,
		NOTIFY,
		NOTIFY_ALL,
		TO_STRING,
		WAIT
	};
	
	private static final ObjectMethod[] ARITY_1_METHODS = {
		EQUALS,
		WAIT1
	};
	
	private static final ObjectMethod[] ARITY_2_METHODS = {
		WAIT2
	};
	
	
	
	public static ObjectMethod[] allMethods() {
		return values();
	}
	
	public static ObjectMethod[] arity0Methods() {
		return ARITY_0_METHODS.clone();
	}
	
	public static ObjectMethod[] arity1Methods() {
		return ARITY_1_METHODS.clone();
	}
	
	public static ObjectMethod[] arity2Methods() {
		return ARITY_2_METHODS.clone();
	}
	
	public static ObjectMethod[] methodsOfArity(int arity) {
		switch(arity) {
		case 0:
			return arity0Methods();
		case 1:
			return arity1Methods();
		case 2:
			return arity2Methods();
		default:	
		}
		return NO_METHODS;
	}
	
	public static ObjectMethod getObjectMethod(ExecutableElement ee) {
		List<? extends VariableElement> params = ee.getParameters();
		
		int arity = params.size();
		
		ObjectMethod[] candidates = methodsOfArity(arity);
		
		for(ObjectMethod cand : candidates) {
			if(cand.is(ee)) {
				return cand;
			}
		}
		
		return null;
	}

	private final String simpleName;
	private final TypeMatcher returnTypeMatcher;
	private final TypeMatcher[] paramTypeMatchers;
	
	private ObjectMethod(String simpleName, Class<?> returnType,
			Class<?> ...paramTypes) {
		this.simpleName = simpleName;
		
		this.returnTypeMatcher = TypeUtils.getSubtypeMatcher(returnType);
		this.paramTypeMatchers = TypeUtils.getMatchers(paramTypes);
	}
	
	public String getSimpleName() {
		return simpleName;
	}
	
	public int getArity() {
		return paramTypeMatchers.length;
	}
	
	public boolean is(ExecutableElement ee) {
		List<? extends VariableElement> params = ee.getParameters();
		if(params.size() != getArity()) {
			return false;
		}
		if(!ee.getSimpleName().contentEquals(simpleName)) {
			return false;
		}
		
		return ElementUtils.checkSignature(ee, returnTypeMatcher, paramTypeMatchers);
	}

	
}
