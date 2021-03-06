/*
 * Copyright (c) 2013-2014 by Malte Isberner (https://github.com/misberner).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.misberner.apcommons.util.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

// TODO Documentation
public class TypeUtils {
	
	public static interface TypeMatcher {
		public boolean matches(TypeMirror type);
	}
	
	public static class KindMatcher implements TypeMatcher {
		private final TypeKind kind;
		
		public KindMatcher(TypeKind kind) {
			this.kind = kind;
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.github.misberner.apcommons.util.TypeUtils.TypeMatcher#matches(javax.lang.model.type.TypeMirror)
		 */
		@Override
		public boolean matches(TypeMirror type) {
			return (type.getKind() == kind);
		}
	}
	
	public static class DeclaredTypeNameMatcher implements TypeMatcher {
		private final String name;
		
		public DeclaredTypeNameMatcher(String name) {
			this.name = name;
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.github.misberner.apcommons.util.TypeUtils.TypeMatcher#matches(javax.lang.model.type.TypeMirror)
		 */
		@Override
		public boolean matches(TypeMirror type) {
			if(type.getKind() != TypeKind.DECLARED) {
				return false;
			}
			DeclaredType dt = (DeclaredType)type;
			TypeElement te = (TypeElement)dt.asElement();
			return te.getQualifiedName().contentEquals(name);
		}
	}
	
	public static class SubTypeMatcher implements TypeMatcher {
		private final TypeMatcher supertypeMatcher;
		
		public SubTypeMatcher(TypeMatcher supertypeMatcher) {
			this.supertypeMatcher = supertypeMatcher;
		}
		
		@Override
		public boolean matches(TypeMirror type) {
			while(type.getKind() == TypeKind.DECLARED) {
				if(supertypeMatcher.matches(type)) {
					return true;
				}
				DeclaredType dt = (DeclaredType)type;
				TypeElement te = (TypeElement)dt.asElement();
				type = te.getSuperclass();
			}
			return false;
		}
	}
	
	public static class DeclaredTypeNamesMatcher implements TypeMatcher {
		private final Set<String> typeNames;
		
		public DeclaredTypeNamesMatcher(Collection<? extends String> typeNames) {
			this.typeNames = new HashSet<>(typeNames);
		}

		@Override
		public boolean matches(TypeMirror type) {
			if(type.getKind() != TypeKind.DECLARED) {
				return false;
			}
			DeclaredType dt = (DeclaredType)type;
			TypeElement te = (TypeElement)dt.asElement();
			String nameStr = te.getQualifiedName().toString();
			return typeNames.contains(nameStr);
		}
	}
	
	public static class ArrayTypeMatcher implements TypeMatcher {
		private final TypeMatcher componentTypeMatcher;
		
		public ArrayTypeMatcher(TypeMatcher componentTypeMatcher) {
			this.componentTypeMatcher = componentTypeMatcher;
		}
		
		/*
		 * (non-Javadoc)
		 * @see com.github.misberner.apcommons.util.TypeUtils.TypeMatcher#matches(javax.lang.model.type.TypeMirror)
		 */
		@Override
		public boolean matches(TypeMirror type) {
			if(type.getKind() != TypeKind.ARRAY) {
				return false;
			}
			ArrayType at = (ArrayType)type;
			TypeMirror componentType = at.getComponentType();
			
			return componentTypeMatcher.matches(componentType);
		}
	}
	
	private static final Map<Class<?>,TypeKind> CLASS_KIND_MAP;
	
	private static final KindMatcher[] KIND_MATCHERS;
	
	static {
		CLASS_KIND_MAP = new HashMap<>();
		CLASS_KIND_MAP.put(void.class, TypeKind.VOID);
		CLASS_KIND_MAP.put(boolean.class, TypeKind.BOOLEAN);
		CLASS_KIND_MAP.put(char.class, TypeKind.CHAR);
		CLASS_KIND_MAP.put(byte.class, TypeKind.BYTE);
		CLASS_KIND_MAP.put(short.class, TypeKind.SHORT);
		CLASS_KIND_MAP.put(int.class, TypeKind.INT);
		CLASS_KIND_MAP.put(long.class, TypeKind.LONG);
		CLASS_KIND_MAP.put(float.class, TypeKind.FLOAT);
		CLASS_KIND_MAP.put(double.class, TypeKind.DOUBLE);
		
		TypeKind[] typeKinds = TypeKind.values();
		KIND_MATCHERS = new KindMatcher[typeKinds.length];
		for(TypeKind tk : typeKinds) {
			KIND_MATCHERS[tk.ordinal()] = new KindMatcher(tk);
		}
	}
	
	
	public static TypeKind getKind(Class<?> clazz) {
		if(clazz.isArray()) {
			return TypeKind.ARRAY;
		}
		if(clazz.isPrimitive()) {
			return CLASS_KIND_MAP.get(clazz);
		}
		return TypeKind.DECLARED;
	}
	
	protected static TypeMatcher getArrayMatcher(Class<?> arrayClazz) {
		assert arrayClazz.isArray();
		Class<?> componentClazz = arrayClazz.getComponentType();
		TypeMatcher matcher = getMatcher(componentClazz);
		return new ArrayTypeMatcher(matcher);
	}
	
	protected static TypeMatcher getPrimitiveMatcher(Class<?> primitiveClazz) {
		assert primitiveClazz.isPrimitive();
		TypeKind primitiveKind = CLASS_KIND_MAP.get(primitiveClazz);
		return KIND_MATCHERS[primitiveKind.ordinal()];
	}
	
	protected static TypeMatcher getDeclaredMatcher(Class<?> clazz) {
		return new DeclaredTypeNameMatcher(clazz.getName());
	}
	
	
	public static TypeMatcher getMatcher(Class<?> clazz) {
		if(clazz.isArray()) {
			return getArrayMatcher(clazz);
		}
		if(clazz.isPrimitive()) {
			return getPrimitiveMatcher(clazz);
		}
		return getDeclaredMatcher(clazz);
	}
	
	public static TypeMatcher getSubtypeMatcher(Class<?> clazz) {
		if(clazz.isArray()) {
			Class<?> componentClazz = clazz.getComponentType();
			TypeMatcher matcher = getSubtypeMatcher(componentClazz);
			return new ArrayTypeMatcher(matcher);
		}
		if(clazz.isPrimitive()) {
			return getPrimitiveMatcher(clazz);
		}
		TypeMatcher tm = getDeclaredMatcher(clazz);
		return new SubTypeMatcher(tm);
	}
	
	@SafeVarargs
	public static TypeMatcher[] getMatchers(Class<?> ...clazzes) {
		TypeMatcher[] matchers = new TypeMatcher[clazzes.length];
		
		for(int i = 0; i < clazzes.length; i++) {
			matchers[i] = getMatcher(clazzes[i]);
		}
		
		return matchers;
	}
	
	
	
	
	
	
	
	
	/**
	 * Checks if the type represented by the given element can be instantiated. This requires
	 * the type to be a non-abstract class.
	 * <p>
	 * Note that this method does not check whether the class actually exposes any visible
	 * constructors.
	 * 
	 * @param elem the type element to check
	 * @return {@code true} if the type is a non-abstract class, {@code false} otherwise
	 */
	public static boolean isInstantiable(TypeElement elem) {
		if(elem.getKind() == ElementKind.INTERFACE || elem.getModifiers().contains(Modifier.ABSTRACT)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if the type represented by the given element can be instantiated
	 * on a stand-alone basis. This requires the type to be a non-abstract class
	 * which is defined either at package level, or, if it is an inner class,
	 * with a {@code static} modifier.
	 * <p>
	 * Note that like the above {@link #isInstantiable(TypeElement)}, this method does not
	 * check if the class actually exposes any visible constructors.
	 * 
	 * @param elem the type element to check
	 * @return {@code true} if this type is stand-alone instantiable, {@code false}
	 * otherwise.
	 */
	public static boolean isStandaloneInstantiable(TypeElement elem) {
		if(!isInstantiable(elem)) {
			return false;
		}
		
		Element enclosing = elem.getEnclosingElement();
		if(enclosing.getKind() == ElementKind.PACKAGE) {
			// top-level class
			return true;
		}
		
		// only static inner classes are instantiable
		return elem.getModifiers().contains(Modifier.STATIC);
	}
	
	
	/**
	 * Retrieves all directly declared methods of a given type.
	 * 
	 * @param typeElem the type element
	 * @return a list of all directly declared methods of the given type
	 */
	public static List<ExecutableElement> getDeclaredMethods(TypeElement typeElem) {
		return ElementFilter.methodsIn(typeElem.getEnclosedElements());
	}
	
	/**
	 * Retrieves all directly declared fields of a given type.
	 * 
	 * @param typeElem the type element
	 * @return a list of all directly declared fields of the given type
	 */
	public static List<VariableElement> getDeclaredFields(TypeElement typeElem) {
		return ElementFilter.fieldsIn(typeElem.getEnclosedElements());
	}
	
	/**
	 * Retrieves all constructors of a given class.
	 * 
	 * @param typeElem the type element for the class
	 * @return a list of all constructors of the specified class
	 */
	public static List<ExecutableElement> getConstructors(TypeElement typeElem) {
		return ElementFilter.constructorsIn(typeElem.getEnclosedElements());
	}
	

}
