package com.example.demo.elements.impl;

import java.util.HashMap;

import org.springframework.stereotype.Component;

import com.example.demo.elements.Attributes;
import com.example.demo.elements.KEY;

/**
 * Implementation of the {@link Attributes} interface, extending {@link HashMap}
 * to manage application-wide attributes using predefined {@link KEY} values.
 * <p>
 * This component is designed to store and retrieve various configuration and
 * state attributes within the application, supporting attribute management
 * using a centralized storage approach. It uses {@link KEY} enums as keys,
 * allowing for type-safe access and improved readability.
 * </p>
 *
 * <p>
 * Attributes stored in this implementation are accessible throughout the
 * application due to the {@code @Component} annotation, which ensures that a
 * single instance is managed by the Spring container.
 * </p>
 */
@Component
public class AttributesImpl extends HashMap<KEY, Object> implements Attributes {

	/**
	 * Serial version UID for ensuring compatibility during the serialization and
	 * deserialization process of {@code AttributesImpl} instances.
	 */
	private static final long serialVersionUID = 1L;

}
