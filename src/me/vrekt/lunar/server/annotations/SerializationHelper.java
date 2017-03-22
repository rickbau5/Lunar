package me.vrekt.lunar.server.annotations;

import me.vrekt.lunar.utilities.CheckedBiConsumer;
import me.vrekt.lunar.utilities.CheckedConsumer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Created by Rick on 3/21/2017.
 */
public class SerializationHelper {
    public static <T> boolean isSerializable(T obj) {
        return obj.getClass().isAnnotationPresent(Serializable.class);
    }

    public static <T> Optional<byte[]> serializeClass(T obj) {
        return serializeClass(obj, dos -> {});
    }

    public static <T> Optional<byte[]> serializeClass(T obj, CheckedConsumer<DataOutputStream> pre) {
        if (!isSerializable(obj)) {
            System.err.println("Cannot serialize class, no @Serializable annotation found: " + obj.getClass().getCanonicalName());
            return Optional.empty();
        }

        boolean foundSerialize = false;
        for (final Method method : obj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Serialize.class)) {
                foundSerialize = true;
                try {
                    Object ret = method.invoke(obj);
                    if (ret == null) {
                        System.err.println("No return for @Serialize method. Should have return type byte[]");
                        break;
                    }
                    if (!(ret instanceof byte[])) {
                        System.err.printf("@Serialize method has wrong type %s, should be byte[]\n", method.getReturnType());
                        break;
                    }

                    byte[] bytes = (byte[]) ret;
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(bos);

                    try {
                        pre.accept(dos);
                        dos.write(bytes);
                        return Optional.of(bos.toByteArray());
                    } catch (IOException e) {
                        System.err.println("Error while writing to byte stream.");
                        e.printStackTrace();
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.err.println("Failed invoking @Serialize method on " + obj.getClass().getCanonicalName());
                    e.printStackTrace();
                }

                break;
            }
        }
        if (!foundSerialize) {
            System.out.println("No @Serialize annotation found for @Serializable class: " + obj.getClass().getCanonicalName());
        }

        return Optional.empty();
    }

    public static <T extends Class<?>, U> Optional<U> deserializeClass(T clazz, DataInputStream dis) {
        return deserializeClass(clazz, dis, (d, o) -> {});
    }

    public static <T extends Class<?>, U> Optional<U> deserializeClass(T clazz, DataInputStream dis, CheckedBiConsumer<DataInputStream, U> pre) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Deserialize.class)) {
                continue;
            }

            try {
                final U instance = (U) clazz.newInstance();
                pre.accept(dis, instance);
                method.invoke(instance, dis);
                return Optional.of(instance);
            } catch (InstantiationException | IllegalAccessException | IOException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return Optional.empty();
    }
}
