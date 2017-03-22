package me.vrekt.lunar.server;

import jdk.nashorn.internal.runtime.options.Option;
import me.vrekt.lunar.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Rick on 3/21/2017.
 */
public class EntityRegistry {
    static HashMap<Integer, Class<? extends Entity>> entityRegistry = new HashMap<>();
    private static int nextAvailableId = 0;

    public static int registerEntity(Class<? extends Entity> entityClass) {
        for (Map.Entry<Integer, Class<? extends Entity>> entry : entityRegistry.entrySet()) {
            if (entry.getValue() == entityClass) {
                System.err.printf("Entity %s is already registered with id %d!\n", entityClass.getCanonicalName(), entry.getKey());
                return entry.getKey();
            }
        }
        int id = nextAvailableId++;
        entityRegistry.put(id, entityClass);
        return id;
    }

    public static boolean updateEntityId(int id, String name) {
        Optional<Map.Entry<Integer, Class<? extends Entity>>> nameMatch = entityRegistry.entrySet().stream()
                .filter(entry -> entry.getValue().getCanonicalName().equals(name)).findAny();
        Optional<Map.Entry<Integer, Class<? extends Entity>>> idMatch = entityRegistry.entrySet().stream()
                .filter(entry -> entry.getKey() == id).findAny();
        if (nameMatch.isPresent()) {
            entityRegistry.put(id, nameMatch.get().getValue());
            idMatch.ifPresent(integerClassEntry -> entityRegistry.put(nameMatch.get().getKey(), integerClassEntry.getValue()));
            return true;
        } else {
            try {
                Class<?> aClass = EntityRegistry.class.getClassLoader().loadClass(name);
                entityRegistry.put(id, (Class<? extends Entity>)aClass);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Class<? extends Entity> getEntityForId(int id) {
        return entityRegistry.get(id);
    }

    public static Optional<Integer> getIdForEntity(Class<? extends Entity> entityClass) {
        for (Map.Entry<Integer, Class<? extends Entity>> entry : entityRegistry.entrySet()) {
            if (entry.getValue() == entityClass) {
                return Optional.of(entry.getKey());
            }
        }

        return Optional.empty();
    }

    public static void printEntityRegistry() {
        System.out.println("EntityRegistry");
        entityRegistry.forEach((key, val) -> System.out.printf(" %d: %s\n", key, val.getCanonicalName()));
    }

    private EntityRegistry() {}
}
