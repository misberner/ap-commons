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
package com.github.misberner.apcommons.processing;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.github.misberner.apcommons.processing.exceptions.FatalProcessingException;
import com.github.misberner.apcommons.processing.exceptions.ProcessingException;
import com.github.misberner.apcommons.util.APUtils;
import com.github.misberner.apcommons.util.ElementUtils;

/**
 * An annotation processor that sequentially dispatches multiple
 * {@link SingleAnnotationProcessorModule}s.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 */
public class MultiModuleProcessor extends AbstractProcessor {


	protected APUtils utils;
	private final SingleAnnotationProcessorModule<?>[] modules;

	/**
	 * Constructor.
	 * 
	 * @param modules the processor modules, which are dispatched in the given
	 * order.
	 */
	public MultiModuleProcessor(SingleAnnotationProcessorModule<?>... modules) {
		this.modules = modules.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.annotation.processing.AbstractProcessor#init(javax.annotation.processing.ProcessingEnvironment)
	 */
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		this.utils = new APUtils(processingEnv);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		if(utils == null) {
			throw new IllegalStateException("Asked to process before init() was called!");
		}
		
		boolean noErrors = true;
		
		for(SingleAnnotationProcessorModule<?> module : modules) {
			noErrors = noErrors & dispatchModule(module, roundEnv);
		}
		
		if(!noErrors) {
			utils.getMessager().printMessage(Kind.ERROR, "Errors during annotation processing");
		}
		
		return true;
	}
	
	
	/**
	 * Dispatch the processing of a {@link SingleAnnotationProcessorModule} during a round of
	 * annotation processing.
	 * 
	 * @param module the module to dispatch
	 * @param roundEnv the environment for the respective round of annotation processing
	 * @return <tt>true</tt> if all annotations were processed without error, <tt>false</tt>
	 * otherwise.
	 */
	protected <A extends Annotation> boolean dispatchModule(SingleAnnotationProcessorModule<A> module,
			RoundEnvironment roundEnv) {
		assert utils != null;
		
		Class<A> annotationType = module.getAnnotationType();
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotationType);
		
		if(annotatedElements.isEmpty()) {
			return true;
		}
		
		boolean noError = true;
		
		try {
			module.pre(utils);
			
			for(Element e : annotatedElements) {
				AnnotationMirror annotationMirror
					= ElementUtils.findAnnotationMirror(e, annotationType);
				
				if(annotationMirror == null) {
					// Something is REALLY wrong 
					throw new FatalProcessingException("Could not find annotation mirror of type " + annotationType.getCanonicalName(), e);
				}
				
				A annotationObject = e.getAnnotation(annotationType);
				if(annotationObject == null) {
					throw new FatalProcessingException("Found annotation mirror, but could not get annotation object", e, annotationMirror);
				}
				
				try {
					module.process(e, annotationMirror, annotationObject, utils);
				}
				catch(FatalProcessingException ex) {
					throw ex; // rethrow
				}
				catch(ProcessingException ex) {
					ex.print(utils.getMessager());
					noError = false;
				}
				catch(Exception ex) {
					utils.getMessager().printMessage(Kind.ERROR, "Exception during annotation processing: "
							+ ex.getMessage(), e, annotationMirror);
					noError = false;
				}
			}
			
			if(noError) {
				module.post(utils);
			}
			else {
				module.postFailure(utils); // TODO: Currently not called after a FatalProcessingException
			}
		}
		catch(ProcessingException ex) {
			ex.print(utils.getMessager());
			noError = false;
		}
		catch(Exception ex) {
			utils.getMessager().printMessage(Kind.ERROR, "Exception during annotation processing: "
					+ ex.getMessage());
			noError = false;
		}
		
		return noError;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.annotation.processing.AbstractProcessor#getSupportedAnnotationTypes()
	 */
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> result = new HashSet<>();
		for(SingleAnnotationProcessorModule<?> module : modules) {
			Class<? extends Annotation> annotationType = module.getAnnotationType();
			result.add(annotationType.getCanonicalName());
		}
		return result;
	}

}
