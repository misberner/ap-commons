/*
 *
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

package com.github.misberner.apcommons.util;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public enum AFModifier {
	ABSTRACT("abstract", "abstract "),
	FINAL("final", "final "),
	DEFAULT("default", "");
	
	private final String name;
	private final String prefix;
	
	private AFModifier(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}

	public String toString() {
		return name;
	}
	
	
	public static AFModifier ofElement(Element element) {
		AFModifier modifier = null;
		
		Set<Modifier> modifiers = element.getModifiers();
		
		if(modifiers.contains(Modifier.ABSTRACT)) {
			modifier = ABSTRACT;
		}
		
		if(modifiers.contains(Modifier.FINAL)) {
			if(modifier != null) {
				throw new IllegalArgumentException("Illegal modifier combination: abstract and final");
			}
			modifier = FINAL;
		}
		
		if(modifier == null) {
			modifier = DEFAULT;
		}
		
		return modifier;
	}
	
	
	public static AFModifier of(TypeElement typeElement) {
		return ofElement(typeElement);
	}
	
	public static AFModifier of(ExecutableElement methodElement) {
		return ofElement(methodElement);
	}
}
