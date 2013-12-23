/*
 * Copyright (c) 2013 by Malte Isberner (https://github.com/misberner).
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
package com.github.misberner.apcommons.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import com.github.misberner.apcommons.exceptions.DuplicateNameException;
import com.github.misberner.apcommons.util.TypeUtils.TypeMatcher;

public class ElementUtils {

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
	public static boolean isObjectMethod(ExecutableElement method) {
		return (ObjectMethod.getObjectMethod(method) != null);
	}
	
	/**
	 * Checks if the direct parent (i.e., enclosing element) of this element is annotated
	 * with the given annotation type, and if so, returns the annotation object.
	 * <p>
	 * This method is safe in the sense that it won't result in a {@link NullPointerException}
	 * when the specified element does not have a parent, or a <tt>null</tt> element reference
	 * is specified.
	 * 
	 * @param elem the element
	 * @param annotationType the annotation type to check for
	 * @return the annotation object, if the specified element has a parent that is annotated
	 * with the given annotation type.
	 */
	public static <A extends Annotation>
	A getParentAnnotation(Element elem, Class<A> annotationType) {
		if(elem == null) {
			return null;
		}
		Element enclosing = elem.getEnclosingElement();
		if(enclosing == null) {
			return null;
		}
		return enclosing.getAnnotation(annotationType);
	}
	
	/**
	 * Checks if the element or one of its ancestors (i.e., reflexive-transitively enclosing elements)
	 * is annotated with the given annotation type, and if so, returns the annotation object.
	 * <p>
	 * This method is safe in the sense that it won't result in a {@link NullPointerException}
	 * when the specified element does not have a parent, or a <tt>null</tt> element reference
	 * is specified.
	 * 
	 * @param elem the element
	 * @param annotationType the annotation type to check for
	 * @return the annotation object, if the specified element or one of its ancestors is annotated
	 * with the given annotation type.
	 */
	public static <A extends Annotation>
	A getAncestorAnnotation(Element elem, Class<A> annotationType) {
		Element curr = elem;
		while(curr != null) {
			A ann = curr.getAnnotation(annotationType);
			if(ann != null) {
				return ann;
			}
			curr = curr.getEnclosingElement();
		}
		return null;
	}
	
	public static <A extends Annotation>
	A getEnclosingAnnotation(Element elem, Class<A> annotationType, boolean onlyDirect) {
		if(onlyDirect) {
			return getParentAnnotation(elem, annotationType);
		}
		return getAncestorAnnotation(elem, annotationType);
	}
	
	@SafeVarargs
	public static boolean isAnnotated(Element elem, Class<? extends Annotation> ...expectedAnnotations) {
		if(elem == null) {
			return false;
		}
		
		for(Class<? extends Annotation> annotationType : expectedAnnotations) {
			if(elem.getAnnotation(annotationType) != null) {
				return true;
			}
		}
		return false;
	}
	
	@SafeVarargs
	public static boolean isParentAnnotated(Element elem, Class<? extends Annotation> ...expectedAnnotations) {
		if(elem == null) {
			return false;
		}
		Element parent = elem.getEnclosingElement();
		return isAnnotated(parent, expectedAnnotations);
	}
	
	@SafeVarargs
	public static boolean isAncestorAnnotated(Element elem, Class<? extends Annotation> ...expectedAnnotations) {
		Element curr = elem;
		while(curr != null) {
			if(isAnnotated(curr, expectedAnnotations)) {
				return true;
			}
			curr = curr.getEnclosingElement();
		}
		return false;
	}
	
	@SafeVarargs
	public static
	boolean isEnclosingAnnotated(Element elem, boolean onlyDirect, Class<? extends Annotation> ...expectedAnnotations) {
		if(onlyDirect) {
			return isParentAnnotated(elem, expectedAnnotations);
		}
		return isAncestorAnnotated(elem, expectedAnnotations);
	}

	/**
	 * Retrieves the {@link PackageElement} for the package which (indirectly) encloses the specified element.
	 * @param elem the element
	 * @return the package which encloses the element
	 */
	public static PackageElement getPackage(Element elem) {
		while(elem != null && elem.getKind() != ElementKind.PACKAGE) {
			elem = elem.getEnclosingElement();
		}
		return (PackageElement)elem;
	}
	
	/**
	 * Retrieves the name of the package which (indirectly) encloses the specified element.
	 * @param elem the element
	 * @return the package which encloses the element
	 */
	public static String getPackageName(Element elem) {
		PackageElement pkg = getPackage(elem);
		if(pkg == null) {
			return "";
		}
		return pkg.getQualifiedName().toString();
	}
	
	/**
	 * Find an annotation mirror of an element, given the annotation's fully qualified class name.
	 * 
	 * @param elem the element
	 * @param annClassName the fully qualified class name of the annotation
	 * @return the respective annotation mirror, or <tt>null</tt> if the element is not annotated
	 * with the given annotation.
	 */
	public static AnnotationMirror findAnnotationMirror(Element elem, CharSequence annClassName) {
		for(AnnotationMirror am : elem.getAnnotationMirrors()) {
			TypeElement te = (TypeElement)am.getAnnotationType().asElement();
			if(te.getQualifiedName().contentEquals(annClassName)) {
				return am;
			}
		}
		return null;
	}
	
	/**
	 * Find an annotation mirror of an element, given the annotation's {@link Class} object.
	 * 
	 * @param elem the element
	 * @param annClass the annotation's {@link Class} object
	 * @return the respective annotation mirror, or <tt>null</tt> if the element is not annotated
	 * with the given annotation.
	 */
	public static AnnotationMirror findAnnotationMirror(Element elem, Class<? extends Annotation> annClass) {
		return findAnnotationMirror(elem, annClass.getName());
	}
	
	/**
	 * Get an element value from an annotation mirror.
	 * <p>
	 * <i>Note:</i> Annotation mirrors usually do not contain default values, only those that were
	 * set explicitly.
	 * 
	 * @param am the annotation mirror
	 * @param valueName the name of the value
	 * @return the respective annotation value, or <tt>null</tt> if no such value exists or was explicitly set
	 */
	public static AnnotationValue findAnnotationValue(AnnotationMirror am, CharSequence valueName) {
		for(Map.Entry<? extends ExecutableElement,? extends AnnotationValue> e : am.getElementValues().entrySet()) {
			if(e.getKey().getSimpleName().contentEquals(valueName))
				return e.getValue();
		}
		return null;
	}
	
	public static Map<String,AnnotationValue> getAnnotationValues(AnnotationMirror am) {
		Map<String,AnnotationValue> result = new HashMap<>();
		for(Map.Entry<? extends ExecutableElement,? extends AnnotationValue> e : am.getElementValues().entrySet()) {
			result.put(e.getKey().getSimpleName().toString(), e.getValue());
		}
		return result;
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
	
	/**
	 * Retrieves a by-name mapping for all the elements contained in the argument.
	 * <p>
	 * <i>Note:</i> the values of the returned map are actually {@link List}s, since
	 * multiple elements might have the same simple name (e.g., in case of overloaded
	 * methods).
	 * 
	 * @param elements the elements
	 * @return a by-name multi-mapping for the specified elements 
	 */
	public static <E extends Element>
	Map<String,List<E>> elementsByName(Iterable<? extends E> elements) {
		
		Map<String,List<E>> result = new HashMap<>();
		
		for(E elem : elements) {
			String elemName = elem.getSimpleName().toString();
			List<E> list = result.get(elemName);
			if(list == null) {
				list = new ArrayList<>();
				result.put(elemName, list);
			}
			list.add(elem);
		}
		
		return result;
	}
	
	/**
	 * Retrieves a by-name mapping for all the elements contained in the argument.
	 * <p>
	 * <i>Note:</i> unlike the above {@link #elementsByName(Iterable)}, the mapping does
	 * not contain multiple elements per name. However, <tt>elements</tt> contains
	 * multiple elements with the same simple name, a {@link DuplicateNameException}
	 * is thrown.
	 * 
	 * @param elements the elements
	 * @return a by-name mapping for the specified elements
	 * @throws DuplicateNameException if at least two elements in <tt>elements</tt> have the same
	 * simple name.
	 */
	public static <E extends Element>
	Map<String,E> elementsByUniqueName(Iterable<? extends E> elements)
			throws DuplicateNameException {
		
		Map<String,E> result = new HashMap<>();
		
		for(E elem : elements) {
			String elemName = elem.getSimpleName().toString();
			if(result.put(elemName, elem) != null) {
				throw new DuplicateNameException(elemName);
			}
		}
		
		return result;
	}
	
	public static boolean checkSignature(ExecutableElement elem, TypeMatcher returnTypeMatcher, TypeMatcher ...paramTypeMatchers) {
		List<? extends VariableElement> params = elem.getParameters();
		if(params.size() != paramTypeMatchers.length) {
			return false;
		}
		if(!returnTypeMatcher.matches(elem.getReturnType())) {
			return false;
		}
		Iterator<? extends VariableElement> paramIt = params.iterator();
		int i = 0;
		while(paramIt.hasNext()) {
			TypeMatcher matcher = paramTypeMatchers[i++];
			TypeMirror paramType = paramIt.next().asType();
			if(!matcher.matches(paramType)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkParameters(ExecutableElement elem, TypeMatcher ...paramTypeMatchers) {
		List<? extends VariableElement> params = elem.getParameters();
		if(params.size() != paramTypeMatchers.length) {
			return false;
		}
		
		Iterator<? extends VariableElement> paramIt = params.iterator();
		int i = 0;
		while(paramIt.hasNext()) {
			TypeMatcher matcher = paramTypeMatchers[i++];
			TypeMirror paramType = paramIt.next().asType();
			if(!matcher.matches(paramType)) {
				return false;
			}
		}
		return true;
	}
	
}
