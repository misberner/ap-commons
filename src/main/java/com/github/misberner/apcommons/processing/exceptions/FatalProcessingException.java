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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;

/**
 * Exception that indicates a fatal error occurred during annotation
 * processing. "Fatal" here means that this processor (or processor module)
 * will cease processing of any further elements.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class FatalProcessingException extends ProcessingException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FatalProcessingException() {}

	/**
	 * Constructor. 
	 * @param message
	 * @param element
	 * @param annotation
	 * @param value
	 */
	public FatalProcessingException(String message, Element element,
			AnnotationMirror annotation, AnnotationValue value) {
		super(message, element, annotation, value);
	}

	public FatalProcessingException(String message, Element element,
			AnnotationMirror annotation) {
		super(message, element, annotation);
	}

	public FatalProcessingException(String message, Element element) {
		super(message, element);
	}

	public FatalProcessingException(String message) {
		super(message);
	}
	
	

}
