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
package com.github.misberner.apcommons.reporting;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

/**
 * A {@link Reporter} that prints messages using the {@link Messager}
 * of a given {@link ProcessingEnvironment}.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 * @see Messager
 * @see ProcessingEnvironment#getMessager()
 */
public class RootReporter extends AbstractReporter {
	
	protected final ProcessingEnvironment processingEnv;

	/**
	 * Constructor.
	 * @param processingEnv the processing environment whose {@link Messager}
	 * to use
	 */
	public RootReporter(ProcessingEnvironment processingEnv) {
		this.processingEnv = processingEnv;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.reporting.AbstractReporter#printMessage(javax.tools.Diagnostic.Kind, java.lang.CharSequence)
	 */
	@Override
	protected void printMessage(Kind kind, CharSequence message) {
		processingEnv.getMessager().printMessage(kind, message);
	}
	
	/**
	 * Retrieves an {@link ElementReporter} for the given element.
	 * @param element the element for which to retrieve a reporter
	 */
	public ElementReporter forElement(Element element) {
		return new ElementReporter(processingEnv, element);
	}

	
}
