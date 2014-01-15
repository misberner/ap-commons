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
package com.github.misberner.apcommons.util;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * Utility methods for working with parameter lists.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class ParameterUtils {
	
	/**
	 * Turns a list of {@link VariableElement}s into a {@link ParameterInfo} list.
	 * 
	 * @param veList the list of {@link VariableElement}s
	 * @param varArgs whether this parameter list is of a <i>varargs</i> method.
	 * If set to {@code true}, the last parameter must have an array type and is interpreted
	 * as a <i>varargs</i> parameter
	 * @return the corresponding list of {@link ParameterInfo} objects
	 * @throws IllegalArgumentException if one of the following conditions is true:
	 * <ul>
	 * <li>one of the elements of {@code veList} is {@code null}</li>
	 * <li>one of the elements of {@code veList} has a {@code null} type or name</li>
	 * <li>{@code varArgs} is set to {@code true}, but {@link veList} is empty</li>
	 * <li>{@code varArgs} is set to {@code true}, but the type of the last element of {@code veList}
	 * is not an array type</li>
	 * </ul>
	 */
	public static List<ParameterInfo> fromParamList(List<? extends VariableElement> veList, boolean varArgs)
			throws IllegalArgumentException {
		int size = veList.size();
		List<ParameterInfo> params = new ArrayList<>(veList.size());
		int i = 0;
		for(VariableElement ve : veList) {
			boolean va = varArgs && (++i == size);
			params.add(new ParameterInfo(ve.asType(), ve.getSimpleName(), va));
		}
		return params;
	}
	
	/**
	 * Turns the parameter list of an {@link ExecutableElement} into a list of {@link ParameterInfo}
	 * objects.
	 * <p>
	 * This is a convenience method, equivalent to
	 * <pre>
	 * fromParamList(method.getParameters(), method.isVarArgs())
	 * </pre>
	 * 
	 * @param method the method from which to retrieve the parameter list
	 * @return the corresponding list of {@link ParameterInfo} objects
	 */
	public static List<ParameterInfo> getParameters(ExecutableElement method) {
		return fromParamList(method.getParameters(), method.isVarArgs());
	}
}
