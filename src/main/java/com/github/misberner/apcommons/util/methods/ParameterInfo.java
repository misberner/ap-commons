/*
 * Copyright (c) 2014 by Malte Isberner (https://github.com/misberner).
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
package com.github.misberner.apcommons.util.methods;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Represents parameters of a method, as a replacement for {@link VariableElement}.
 * <p>
 * This class offers the following benefits compared to {@link VariableElement}:
 * <ul>
 * <li>it provides access to both the parameter type as well as the parameter name
 * through appropriately named getters and setters</li>
 * <li>it has a {@link String} representation suitable for representing the parameter
 * in a parameter list</li>
 * <li>it provides <i>local</i> (i.e., per-parameter) access to the information whether the
 * respective parameter is a <i>varargs</i> parameter)</li>
 * </ul> 
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class ParameterInfo {
	private final TypeMirror type;
	private final String name;
	private final boolean varArgs;
	
	/**
	 * Constructor.
	 * @param type the type of the parameter
	 * @param name the name of the parameter
	 * @param varArgs whether or not this parameter is a <i>varargs</i> parameter
	 */
	public ParameterInfo(TypeMirror type, CharSequence name, boolean varArgs) {
		this.type = type;
		this.name = name.toString();
		this.varArgs = varArgs;
		if(varArgs && type.getKind() != TypeKind.ARRAY) {
			throw new IllegalArgumentException("Varargs parameters must be of array type");
		}
	}
	
	/**
	 * Retrieves the type of this parameter.
	 * @return the type of this parameter
	 */
	public TypeMirror getType() {
		return type;
	}
	
	/**
	 * Retrieves the name of this parameter.
	 * @return the name of this parameter
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Retrieves whether this parameter is a <i>varargs</i> parameter.
	 * @return {@code true} if this parameter is a <i>varargs</i> parameter,
	 * <i>false</i> otherwise. 
	 */
	public boolean isVarArgs() {
		return varArgs;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(!varArgs) {
			return type.toString() + " " + name;
		}
		ArrayType at = (ArrayType)type;
		return at.getComponentType() + "... " + name;
	}
}