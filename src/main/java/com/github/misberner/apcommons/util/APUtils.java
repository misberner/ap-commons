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
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.github.misberner.apcommons.reporting.AnnotationReporter;
import com.github.misberner.apcommons.reporting.ElementReporter;
import com.github.misberner.apcommons.reporting.RootReporter;
import com.github.misberner.apcommons.reporting.ValueReporter;

public class APUtils {

	private final ProcessingEnvironment processingEnv;
	private final RootReporter rootReporter;
	
	public APUtils(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
		this.rootReporter = new RootReporter(processingEnv);
	}
	
	/**
	 * Retrieves a reference to the processing environment.
	 * @return a reference to the processing environment.
	 */
	public ProcessingEnvironment getProcessingEnv() {
		return processingEnv;
	}
	
	/**
	 * Retrieves a reference to the type utilities.
	 * @return a reference to the type utilities. 
	 */
	public Types getTypeUtils() {
		return processingEnv.getTypeUtils();
	}
	
	/**
	 * Retrieves a reference to the element utilities.
	 * @return a reference to the element utilities.
	 */
	public Elements getElementUtils() {
		return processingEnv.getElementUtils();
	}
	
	/**
	 * Retrieves a reference to the filer.
	 * @return a reference to the filer
	 */
	public Filer getFiler() {
		return processingEnv.getFiler();
	}
	
	/**
	 * Retrieves a reference to the messager.
	 * @return a reference to the messager
	 */
	public Messager getMessager() {
		return processingEnv.getMessager();
	}
	
	
	public boolean checkMethodSignature(ExecutableElement method,
			CharSequence ...paramTypeNames) {
		int arity = paramTypeNames.length;
		
		List<? extends VariableElement> params = method.getParameters();
		if(params.size() != arity) {
			return false;
		}

		Types types = getTypeUtils();
		
		int pidx = 0;
		for(VariableElement param : params) {
			CharSequence expectedTypeName = paramTypeNames[pidx];
			
			TypeMirror paramType = param.asType();
			TypeMirror erasedParamType = types.erasure(paramType);
			if(!erasedParamType.toString().contentEquals(expectedTypeName)) {
				return false;
			}
			
			pidx++;
		}
		
		return true;
	}
	
	public boolean checkMethodSignature(ExecutableElement method,
			Class<?>[] paramTypes) {
		int arity = paramTypes.length;
		CharSequence[] paramTypeNames = new CharSequence[arity];
		for(int i = 0; i < arity; i++) {
			paramTypeNames[i] = paramTypes[i].getCanonicalName();
		}
		return checkMethodSignature(method, paramTypes);
	}
	
	public boolean checkMethodSignature(ExecutableElement method,
			Class<?> firstParamType, Class<?> ...otherParamTypes) {
		Class<?>[] paramTypes = new Class[1 + otherParamTypes.length];
		paramTypes[0] = firstParamType;
		System.arraycopy(otherParamTypes, 0, paramTypes, 1, otherParamTypes.length);
		return checkMethodSignature(method, paramTypes);
	}
	
	public ExecutableElement findConstructor(Iterable<? extends ExecutableElement> allConstructors,
			CharSequence ...paramTypeNames) {
		for(ExecutableElement ctor : allConstructors) {
			if(checkMethodSignature(ctor, paramTypeNames)) {
				return ctor;
			}
		}
		return null;
	}
	
	public ExecutableElement findConstructor(Iterable<? extends ExecutableElement> allConstructors,
			Class<?> ...paramTypes) {
		String[] paramTypeNames = NameUtils.getCanonicalNames(paramTypes);
		return findConstructor(allConstructors, paramTypeNames);
	}
	
	public ExecutableElement findConstructor(Iterable<? extends ExecutableElement> allConstructors,
			Class<?> firstParamType, Class<?> ...otherParamTypes) {
		String[] paramTypeNames = NameUtils.getCanonicalNames(firstParamType, otherParamTypes);
		return findConstructor(allConstructors, paramTypeNames);
	}
	
	public ExecutableElement findConstructor(TypeElement type,
			CharSequence ...paramTypeNames) {
		List<? extends ExecutableElement> ctors = ElementFilter.constructorsIn(type.getEnclosedElements());
		return findConstructor(ctors, paramTypeNames);
	}
	
	public ExecutableElement findConstructor(TypeElement type,
			Class<?> firstParamType, Class<?> ...otherParamTypes) {
		List<? extends ExecutableElement> ctors = ElementFilter.constructorsIn(type.getEnclosedElements());
		return findConstructor(ctors, firstParamType, otherParamTypes);
	}
	
	public ExecutableElement findConstructor(TypeElement type,
			Class<?>[] paramTypes) {
		List<? extends ExecutableElement> ctors = ElementFilter.constructorsIn(type.getEnclosedElements());
		return findConstructor(ctors, paramTypes);
	}
	
	
	public ExecutableElement findMethod(Iterable<? extends ExecutableElement> allMethods,
			CharSequence name, CharSequence ...paramTypeNames) {
		
		for(ExecutableElement method : allMethods) {
			if(method.getSimpleName().contentEquals(name)) {
				continue;
			}
			
			if(checkMethodSignature(method, paramTypeNames)) {
				return method;
			}
		}
		
		return null;
	}
	
	public ExecutableElement findMethod(Iterable<? extends ExecutableElement> allMethods,
			CharSequence name, Class<?> firstParamType, Class<?> ...otherParamTypes) {
		CharSequence[] paramTypeNames = new CharSequence[otherParamTypes.length + 1];
		paramTypeNames[0] = firstParamType.getCanonicalName();
		for(int i = 0; i < otherParamTypes.length; i++) {
			paramTypeNames[i+1] = otherParamTypes[i].getCanonicalName();
		}
		return findMethod(allMethods, name, paramTypeNames);
	}
	
	public ExecutableElement findDeclaredMethod(TypeElement type,
			CharSequence name, String ...paramTypeNames) {
		List<? extends ExecutableElement> methods = ElementFilter.methodsIn(type.getEnclosedElements());
		return findMethod(methods, name, paramTypeNames);
	}
	
	public ExecutableElement findDeclaredMethod(TypeElement type,
			CharSequence name, Class<?> firstParamType, Class<?> ...otherParamTypes) {
		List<? extends ExecutableElement> methods = ElementFilter.methodsIn(type.getEnclosedElements());
		return findMethod(methods, name, firstParamType, otherParamTypes);
	}
	
	public ExecutableElement findMethod(TypeElement type,
			CharSequence name, String ...paramTypeNames) {
		List<? extends Element> allMembers = getElementUtils().getAllMembers(type);
		List<? extends ExecutableElement> methods = ElementFilter.methodsIn(allMembers);
		return findMethod(methods, name, paramTypeNames);
	}
	
	
	public ExecutableElement findMethod(TypeElement type,
			CharSequence name, Class<?> firstParamType, Class<?> ...otherParamTypes) {
		List<? extends Element> allMembers = getElementUtils().getAllMembers(type);
		List<? extends ExecutableElement> methods = ElementFilter.methodsIn(allMembers);
		return findMethod(methods, name, firstParamType, otherParamTypes);
	}
	
	
	/**
	 * Retrieves a by-name map of the directly declared methods of a given
	 * type.
	 * <p>
	 * Note that due to overloading, several methods with the same type may exist.
	 * @param type the type
	 * @return a by-name map of the directly declared methods of the
	 * specified type.
	 */
	public Map<String,List<ExecutableElement>> declaredMethodsByName(TypeElement type) {
		List<? extends ExecutableElement> declaredMethods = getDeclaredMethods(type);
		return ElementUtils.elementsByName(declaredMethods);
	}
	
	/**
	 * Retrieves a by-name map of all methods (inherited or directly declared) of a given
	 * type.
	 * <p>
	 * Note that due to overloading, several methods with the same type may exist.
	 * @param type the type
	 * @return a by-name map of all methods (inherited or directly declared) of the
	 * specified type.
	 */
	public Map<String,List<ExecutableElement>> methodsByName(TypeElement type) {
		List<? extends ExecutableElement> allMethods = getAllMethods(type);
		return ElementUtils.elementsByName(allMethods);
	}
	
	/**
	 * Retrieves a by-name map of the directly declared fields of a given type. 
	 * @param type the type
	 * @return a by-name map of the directly declared fields of the specified type.
	 */
	public Map<String,VariableElement> declaredFieldsByName(TypeElement type) {
		List<? extends VariableElement> declaredFields = getDeclaredFields(type);
		return ElementUtils.elementsByUniqueName(declaredFields);
	}
	
	/**
	 * Retrieves a by-name map of all fields (inherited or directly declared) of a given
	 * type.
	 * @param type the type
	 * @return a by-name map of all fields (inherited or directly declared) of the specified
	 * type.
	 */
	public Map<String,VariableElement> allFieldsByName(TypeElement type) {
		List<? extends VariableElement> allFields = getAllFields(type);
		return ElementUtils.elementsByUniqueName(allFields);
	}
	
	/**
	 * Retrieves the directly declared methods of a given type.
	 * @param type the type
	 * @return a list of the directly declared methods of the specified type.
	 */
	public List<? extends ExecutableElement> getDeclaredMethods(TypeElement type) {
		return ElementFilter.methodsIn(type.getEnclosedElements());
	}
	
	/**
	 * Retrieves all methods of a given type, whether inherited or directly declared.
	 * @param type the type
	 * @return a list of all methods (inherited or directly declared) of the specified
	 * type.
	 */
	public List<? extends ExecutableElement> getAllMethods(TypeElement type) {
		return ElementFilter.methodsIn(getElementUtils().getAllMembers(type));
	}
	
	/**
	 * Retrieves the directly declared fields of a given type.
	 * @param type the type
	 * @return a list of the directly declared fields of the specified type.
	 */
	public List<? extends VariableElement> getDeclaredFields(TypeElement type) {
		return ElementFilter.fieldsIn(type.getEnclosedElements());
	}
	
	/**
	 * Retrieves all fields of a given type, whether inherited or directly declared.
	 * @param type the type
	 * @return a list of all fields (inherited or directly declared) of the specified
	 * type.
	 */
	public List<? extends VariableElement> getAllFields(TypeElement type) {
		return ElementFilter.fieldsIn(getElementUtils().getAllMembers(type));
	}
	
	
	public RootReporter getReporter() {
		return rootReporter;
	}
	
	public ElementReporter getReporter(Element elem) {
		return getReporter().forElement(elem);
	}
	
	public AnnotationReporter getReporter(Element elem, AnnotationMirror annotation) {
		return getReporter(elem).forAnnotation(annotation); 
	}
	
	public AnnotationReporter getReporter(Element elem, CharSequence annotationName) {
		return getReporter(elem).forAnnotation(annotationName);
	}
	
	public AnnotationReporter getReporter(Element elem, Class<? extends Annotation> annotationClazz) {
		return getReporter(elem).forAnnotation(annotationClazz);
	}
	
	public ValueReporter getReporter(Element elem, AnnotationMirror annotation, AnnotationValue value) {
		return getReporter(elem, annotation).forValue(value);
	}
	
	public ValueReporter getReporter(Element elem, AnnotationMirror annotation, CharSequence valueName) {
		return getReporter(elem, annotation).forValue(valueName);
	}
	
	public ValueReporter getReporter(Element elem, CharSequence annotationName, CharSequence valueName) {
		return getReporter(elem, annotationName).forValue(valueName);
	}
	
	public ValueReporter getReporter(Element elem, Class<? extends Annotation> annotationClazz, CharSequence valueName) {
		return getReporter(elem, annotationClazz).forValue(valueName);
	}
	
}
