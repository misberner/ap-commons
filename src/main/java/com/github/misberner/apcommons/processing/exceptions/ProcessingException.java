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
package com.github.misberner.apcommons.processing.exceptions;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

/**
 * Indicates that an error occurred during annotation processing.
 * 
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class ProcessingException extends Exception {

	private static final long serialVersionUID = -1L;
	
	// These interfaces do not extend Serializable, so we should not rely
	// on their concrete implementations being serializable either
	private transient final Element element;
	private transient final AnnotationMirror annotation;
	private transient final AnnotationValue value;
	
	/**
	 * Constructor.
	 */
	public ProcessingException() {
		this(null);
	}

	/**
	 * Constructor.
	 * @param message the error message
	 */
	public ProcessingException(String message) {
		this(message, null);
	}
	
	/**
	 * Constructor.
	 * @param message the error message
	 * @param element the {@link Element} this error message refers to
	 */
	public ProcessingException(String message, Element element) {
		this(message, element, null);
	}
	
	/**
	 * Constructor.
	 * @param message the error message
	 * @param element the {@link Element} this error message refers to
	 * @param annotation the annotation of {@code element} this error message refers to
	 */
	public ProcessingException(String message, Element element, AnnotationMirror annotation) {
		this(message, element, annotation, null);
	}

	/**
	 * Constructor.
	 * @param message the error message
	 * @param element the {@link Element} this error message refers to
	 * @param annotation the annotation of {@code element} this error message refers to
	 * @param value the value of the {@code annotation} this error message refers to
	 */
	public ProcessingException(String message, Element element, AnnotationMirror annotation, AnnotationValue value) {
		super(message);
		this.element = element;
		this.annotation = annotation;
		this.value = value;
	}
	
	/**
	 * Prints the message provided in the constructor (if any) to a given {@link Messager} with a diagnostic
	 * kind of {@link Diagnostic.Kind#ERROR}.
	 * @param msgr the messager
	 * @return {@code true} if a message has been printed, {@code false} otherwise
	 * @see #print(Messager, Kind)
	 */
	public boolean print(Messager msgr) {
		return print(msgr, Diagnostic.Kind.ERROR);
	}
	
	/**
	 * Prints the message provided in the constructor (if any) to a given {@link Messager} with the specified
	 * diagnostic kind.
	 * 
	 * @param msgr the messager
	 * @param diagnosticKind the diagnostic kind of this message
	 * @return {@code true} if a message has been printed, {@code false} otherwise
	 */
	public boolean print(Messager msgr, Diagnostic.Kind diagnosticKind) {
		if(getMessage() != null) {
			msgr.printMessage(diagnosticKind, getMessage(), element, annotation, value);
			return true;
		}
		return false;
	}


}
