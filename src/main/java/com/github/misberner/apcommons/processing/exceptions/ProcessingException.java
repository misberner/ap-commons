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
package com.github.misberner.apcommons.processing.exceptions;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class ProcessingException extends Exception {

	private static final long serialVersionUID = -1L;
	
	private final Element element;
	private final AnnotationMirror annotation;
	private final AnnotationValue value;
	
	public ProcessingException() {
		this(null);
	}

	public ProcessingException(String message) {
		this(message, null);
	}
	
	public ProcessingException(String message, Element element) {
		this(message, element, null);
	}
	
	public ProcessingException(String message, Element element, AnnotationMirror annotation) {
		this(message, element, annotation, null);
	}

	public ProcessingException(String message, Element element, AnnotationMirror annotation, AnnotationValue value) {
		super(message);
		this.element = element;
		this.annotation = annotation;
		this.value = value;
	}
	
	public boolean print(Messager msg) {
		if(getMessage() != null) {
			msg.printMessage(Kind.ERROR, getMessage(), element, annotation, value);
			return true;
		}
		return false;
	}


}
