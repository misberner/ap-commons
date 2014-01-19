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
package com.github.misberner.apcommons.util;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;


public enum Visibility {
	PRIVATE("private", "private "),
	PACKAGE_PRIVATE("package-private", ""),
	PROTECTED("protected", "protected "),
	PUBLIC("public", "public ");
	
	private final String name;
	private final String prefix;
	
	private Visibility(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public Visibility join(Visibility other) {
		if(compareTo(other) >= 0) {
			return this;
		}
		return other;
	}
	
	public Visibility meet(Visibility other) {
		if(compareTo(other) <= 0) {
			return this;
		}
		return other;
	}
	
	public static Visibility fromModifiers(Set<? extends Modifier> modifiers) {
		
		Visibility vis = null;
		
		if(modifiers.contains(Modifier.PUBLIC)) {
			vis = PUBLIC;
		}
		
		if(modifiers.contains(Modifier.PROTECTED)) {
			if(vis != null) {
				throw new IllegalArgumentException("Illegal modifier combination: "
						+ "element cannot be both 'public' and 'protected'");
			}
			vis = PROTECTED;
		}
		if(modifiers.contains(Modifier.PRIVATE)) {
			if(vis != null) {
				throw new IllegalArgumentException("Illegal modifier combination: "
						+ "element cannot be both '"
						+ ((vis == PUBLIC) ? "public" : "protected")
						+ "' and 'private'");
			}
			vis = PRIVATE;
		}
		
		if(vis == null) {
			vis = PACKAGE_PRIVATE;
		}
		
		return vis;
	}
	
	public static Visibility fromParamList(List<? extends VariableElement> params) {
		Visibility vis = Visibility.PUBLIC;
		for(VariableElement p : params) {
			vis = vis.meet(of(p.asType()));
		}
		return vis;
	}
	
	public static Visibility of(Element e) {
		return fromModifiers(e.getModifiers());
	}
	
	public static Visibility of(TypeMirror type) {
		if(type.getKind() == TypeKind.DECLARED) {
			DeclaredType dt = (DeclaredType)type;
			return Visibility.of(dt.asElement());
		}
		return Visibility.PUBLIC;
	}
}
