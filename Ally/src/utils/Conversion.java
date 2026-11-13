/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 *
 * @author skillz
 */
public class Conversion {

    /**
     * Serializes an object and encodes the result as a Base64 string.
     *
     * <p>
     * Example:</p>
     * <pre>{@code
     * String encoded = Base64Util.objectToString(myObject);
     * }</pre>
     *
     * @param object the object to convert; it must implement
     * {@link Serializable} and all of its non-transient fields must be
     * serializable too
     * @return the Base64 representation of the serialized object
     * @throws IllegalArgumentException if {@code object} is {@code null}
     * @throws IOException if an I/O error occurs during serialization (e.g. a
     * field is not serializable, in which case a
     * {@link java.io.NotSerializableException} is thrown)
     */
    public static String objectToString(Serializable object) throws IOException {
        // Reject null input early with a clear message
        if (object == null) {
            throw new IllegalArgumentException("The object to serialize must not be null.");
        }

        // In-memory buffer that will hold the serialized bytes
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(); // Stream that converts the object graph into bytes
                 ObjectOutputStream objectStream = new ObjectOutputStream(byteStream)) {

            // Serialize the object and write it to the buffer
            objectStream.writeObject(object);

            // Make sure all buffered data is written to the underlying byte stream
            objectStream.flush();

            // Extract the raw bytes from the buffer
            byte[] bytes = byteStream.toByteArray();

            // Encode the bytes as a Base64 text string and return it
            return Base64.getEncoder().encodeToString(bytes);
        }
        // Both streams are closed automatically by try-with-resources
    }

    /**
     * Decodes a Base64 string and deserializes it back into an object.
     *
     * <p>
     * The target type is inferred by the compiler from the assignment:</p>
     * <pre>{@code
     * MyClass copy = Base64Util.stringToObject(encoded);
     * }</pre>
     *
     * <p>
     * If the decoded object is not of the expected type, a
     * {@link ClassCastException} will occur at the call site.</p>
     *
     * @param <T> the expected type of the deserialized object
     * @param base64 the Base64 string produced by
     * {@link #objectToString(Serializable)}
     * @return the reconstructed object
     * @throws IllegalArgumentException if {@code base64} is {@code null},
     * empty, or is not valid Base64
     * @throws IOException if an I/O error occurs during deserialization (e.g.
     * corrupted or truncated data)
     * @throws ClassNotFoundException if the class of the serialized object
     * cannot be found on the classpath
     */
    @SuppressWarnings("unchecked")
    public static <T> T stringToObject(String base64) throws IOException, ClassNotFoundException {
        // Reject null or empty input early with a clear message
        if (base64 == null || base64.isEmpty()) {
            throw new IllegalArgumentException("The Base64 string must not be null or empty.");
        }

        // Decode the Base64 text back into the original serialized bytes
        byte[] bytes = Base64.getDecoder().decode(base64);

        // Wrap the bytes in a stream so they can be read sequentially
        try (ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes); // Stream that rebuilds the object graph from the bytes
                 ObjectInputStream objectStream = new ObjectInputStream(byteStream)) {

            // Read the object and cast it to the type expected by the caller
            return (T) objectStream.readObject();
        }
        // Both streams are closed automatically by try-with-resources
    }
}
