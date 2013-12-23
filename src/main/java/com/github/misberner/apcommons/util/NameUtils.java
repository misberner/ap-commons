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

public class NameUtils {
	/**
	 * Qualifies a simple name, if applicable. A package name which is <tt>null</tt> or which
	 * is empty is assumed to refer to the default package. Hence, the returned
	 * qualified name will equal the simple name in this case. Otherwise, the
	 * package name is prepended, followed by a dot ('.').
	 * 
	 * @param simpleName the simple name
	 * @param packageName the package name, or <tt>null</tt> for the default package
	 * @return the qualified name
	 */
	public static String qualifiedName(CharSequence simpleName, CharSequence packageName) {
		StringBuilder sb = new StringBuilder();
		if(packageName != null && packageName.length() > 0) {
			sb.append(packageName).append('.');
		}
		sb.append(simpleName);
		return sb.toString();
	}
	
	/**
	 * Capitalizes the first character (if existent) in the given character sequence,
	 * and returns the result.
	 * @param str the character sequence
	 * @return a capitalized version of the specified character sequence
	 */
	public static String capitalizeFirst(CharSequence str) {
		if(str.length() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder(str.length());
		sb.append(str.charAt(0));
		sb.append(str.subSequence(1, str.length()));
		return sb.toString();
	}
	
	/**
	 * Resolves the name of an accessor method by following a certain set of rules.
	 * <ol>
	 * <li>If <tt>userName</tt> is both non-<tt>null</tt> and non-empty, its value is used
	 * as the name.</li>
	 * <li>Otherwise, the name consists of a prefix followed by <tt>fieldName</tt>. The first character of
	 * <tt>fieldName</tt> is capitalized if the prefix is non-empty. The prefix is determined as follows:
	 * <ol>
	 * <li>If <tt>userPrefix</tt> is non-null, it is used as the prefix</li>
	 * <li>Otherwise, <tt>defaultPrefix</tt> is used as the prefix.
	 * </ol>
	 * </li>
	 * </ol>
	 * 
	 * @param userName the user-specified name, or <tt>null</tt>.
	 * @param userPrefix the user-specified prefix, or <tt>null</tt>.
	 * @param defaultPrefix the default prefix, or <tt>null</tt>.
	 * @param fieldName the name of the field
	 * @return the name of an accessor method for the specified field, as determined by the above-described rules.
	 */
	public static String accessorName(CharSequence userName,
			CharSequence userPrefix,
			CharSequence defaultPrefix,
			CharSequence fieldName) {
		
		if(userName != null && userName.length() > 0)
			return userName.toString();
		
		CharSequence prefix = userPrefix;
		if(prefix == null) {
			prefix = defaultPrefix;
		}
		
		if(prefix == null || prefix.length() == 0) {
			return fieldName.toString();
		}
		
		StringBuilder sb = new StringBuilder(prefix.length() + fieldName.length());
		
		sb.append(prefix);
		sb.append(capitalizeFirst(fieldName));
		
		return sb.toString();
	}
	
	/**
	 * Convience method. Calling this method is equivalent to calling
	 * <code>accessorName(userName, userPrefix, "get", fieldName)</code>
	 */
	public static String getterName(CharSequence userName, CharSequence userPrefix, CharSequence fieldName) {
		return accessorName(userName, userPrefix, "get", fieldName);
	}
	
	/**
	 * Convenience method. Calling this method is equivalent to calling
	 * <code>getterName(userName, null, fieldName)</code>
	 */
	public static String getterName(CharSequence userName, CharSequence fieldName) {
		return getterName(userName, null, fieldName);
	}
	
	/**
	 * Convenience method. Calling this method is equivalent to calling
	 * <code>getterName(null, fieldName)</code>
	 */
	public static String getterName(CharSequence fieldName) {
		return getterName(null, fieldName);
	}
	
	/**
	 * Convenience method. Calling this method is equivalent to calling
	 * <code>accessorName(userName, userPrefix, "set", fieldName)</code>
	 */
	public static String setterName(CharSequence userName, CharSequence userPrefix, CharSequence fieldName) {
		return accessorName(userName, userPrefix, "set", fieldName);
	}
	
	/**
	 * Convenience method. Calling this method is equivalent to calling
	 * <code>setterName(userName, null, fieldName)</code>
	 */
	public static String setterName(CharSequence userName, CharSequence fieldName) {
		return setterName(userName, null, fieldName);
	}
	
	/**
	 * Convenience method. Calling this method is equivalent to calling
	 * <code>setterName(null, fieldName)</code>
	 */
	public static String setterName(CharSequence fieldName) {
		return setterName(null, fieldName);
	}
	
	/**
	 * Resolves a name of a package from a specification. A specification is either a fully-qualified package
	 * name (including <tt>""</tt> for the empty package), or a relative specification beginning with a dot
	 * (<tt>'.'</tt>). In the latter case, the specification will be interpreted as relative
	 * to a given reference package name.
	 * <p>
	 * Usage examples:
	 * <ul>
	 * <li><code>resolvePackageName(null, "com.example")</code> returns <tt>"com.example"</tt></li>
	 * <li><code>resolvePackageName("", "com.example")</code> returns <tt>""</tt></li>
	 * <li><code>resolvePackageName("foo.bar", "com.example")</code> returns <tt>"foo.bar"</tt></li>
	 * <li><code>resolvePackageName(".", "com.example")</code> returns <tt>"com.example"</tt></li>
	 * <li><code>resolvePackageName(".foo.bar", "com.example")</code> returns <tt>"com.example.foo.bar"</tt></li>
	 * </ul>
	 * 
	 * @param packageNameSpec the specification of the package name, or <tt>null</tt>
	 * @param referencePackage the reference package name 
	 * @return the resolved, fully-qualified package name.
	 */
	public static String resolvePackageName(CharSequence packageNameSpec, CharSequence referencePackage) {
		if(packageNameSpec == null) {
			return referencePackage.toString();
		}
		
		if(packageNameSpec.length() == 0 || packageNameSpec.charAt(0) != '.') {
			return packageNameSpec.toString();
		}
		
		if(referencePackage.length() == 0) {
			return packageNameSpec.subSequence(1, packageNameSpec.length()).toString();
		}
		
		StringBuilder sb = new StringBuilder(referencePackage.length() + packageNameSpec.length());
		sb.append(referencePackage).append(packageNameSpec);
		return sb.toString();
	}
	
	
	public static String defined(CharSequence ...charSeqs) {
		for(CharSequence cs : charSeqs) {
			if(cs != null && cs.length() > 0) {
				return cs.toString();
			}
		}
		return null;
	}
	
	
	public static String[] getCanonicalNames(Class<?> ...classes) {
		String[] result = new String[classes.length];
		for(int i = 0; i < result.length; i++) {
			result[i] = classes[i].getCanonicalName();
		}
		return result;
	}
	
	public static String[] getCanonicalNames(Class<?> firstClass, Class<?> ...otherClasses) {
		String[] result = new String[otherClasses.length + 1];
		result[0] = firstClass.getCanonicalName();
		for(int i = 0; i < otherClasses.length; i++) {
			result[i+1] = otherClasses[i].getCanonicalName();
		}
		return result;
	}

}
