/*
 * Copyright (c) 2015 NOVA, All rights reserved.
 * This library is free software, licensed under GNU Lesser General Public License version 3
 *
 * This file is part of NOVA.
 *
 * NOVA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NOVA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NOVA.  If not, see <http://www.gnu.org/licenses/>.
 */package nova.core.nativewrapper;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import nova.core.util.exception.NovaException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author TheSandromatic
 */
public class NativeManager {
	/**
	 * A map from a Nova component, to a Native interface.
	 */
	private final BiMap<Class<?>, Class<?>> novaComponentToNativeInterface = HashBiMap.create();

	private final List<NativeConverter> converters = new ArrayList<>();
	/**
	 * A map from a Native written type to a Converter.
	 */
	private final Map<Class<?>, NativeConverter> nativeConverters = new HashMap<>();
	/**
	 * A map from a Nova written type to a Converter.
	 */
	private final Map<Class<?>, NativeConverter> novaConverters = new HashMap<>();

	/**
	 * Registers a component to a native interface.
	 * @param component A component. Must extend INTERFACE.
	 * @param nativeInterface the class of the INTERFACE.
	 */
	public <INTERFACE> void registerComponentToInterface(Class<? extends INTERFACE> component, Class<INTERFACE> nativeInterface) {
		novaComponentToNativeInterface.put(component, nativeInterface);
	}

	/**
	 * Gets the interface registered for a component.
	 * @param component the component.
	 * @return The interface on the native side.
	 */
	public Class<?> getNativeInterface(Class<?> component) {
		return novaComponentToNativeInterface.get(component);
	}

	/**
	 * Gets the component registered for an interface.
	 * @param nativeInterface the interface.
	 * @return the component registered to it.
	 */
	public Class<?> getNovaComponent(Class<?> nativeInterface) {
		return novaComponentToNativeInterface.inverse().get(nativeInterface);
	}

	public void registerConverter(NativeConverter<?, ?> converter) {
		nativeConverters.put(converter.getNativeSide(), converter);
		novaConverters.put(converter.getNovaSide(), converter);
		converters.add(converter);
	}

	public <NOVA, NATIVE> NativeConverter<NOVA, NATIVE> getNative(Class<NOVA> novaClass, Class<NATIVE> nativeClass) {
		NativeConverter nativeConverter = novaConverters.get(novaClass);

		if (nativeConverter == nativeConverters.get(nativeClass)) {
			return nativeConverter;
		}

		throw new NativeException("Cannot find native converter for: " + novaClass);
	}

	private NativeConverter findConverter(Map<Class<?>, NativeConverter> map, Object obj) {
		Objects.requireNonNull(obj);
		Class<?> clazz = obj.getClass();

		return map
			.entrySet()
			.stream()
			.filter(e -> e.getKey().isAssignableFrom(clazz))
			.findFirst()
			.map(Map.Entry::getValue)
			.orElse(null);
	}

	/**
	 * Converts a native object to a nova object. This method has autocast, is DANGEROUS and may crash.
	 */
	public <T> T toNova(Object nativeObject) {
		Objects.requireNonNull(nativeObject);
		NativeConverter converter = findConverter(nativeConverters, nativeObject);
		if (converter == null) {
			throw new NativeException("NativeManager.toNova: Converter for " + nativeObject + " with class " + nativeObject.getClass() + " does not exist!");
		}

		return (T) converter.toNova(nativeObject);
	}

	/**
	 * Converts a nova object to a native object. This method has autocast, is DANGEROUS and may crash.
	 */
	public <T> T toNative(Object novaObject) {
		Objects.requireNonNull(novaObject);
		NativeConverter converter = findConverter(novaConverters, novaObject);
		if (converter == null) {
			throw new NativeException("NativeManager.toNative: Converter for " + novaObject + " with class " + novaObject.getClass() + " does not exist!");
		}

		return (T) converter.toNative(novaObject);
	}

	public List<NativeConverter> getNativeConverters() {
		return converters;
	}

	public static class NativeException extends NovaException {
		public NativeException() {
			super();
		}

		public NativeException(String message, Object... parameters) {
			super(message, parameters);
		}

		public NativeException(String message) {
			super(message);
		}

		public NativeException(String message, Throwable cause) {
			super(message, cause);
		}

		public NativeException(Throwable cause) {
			super(cause);
		}
	}
}
