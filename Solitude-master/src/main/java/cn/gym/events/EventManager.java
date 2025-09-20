/*
 * MoonLight Hacked Client
 *
 * A free and open-source hacked client for Minecraft.
 * Developed using Minecraft's resources.
 *
 * Repository: https://github.com/randomguy3725/MoonLight
 *
 * Author(s): [Randumbguy & wxdbie & opZywl & MukjepScarlet & lucas & eonian]
 */
package cn.gym.events;

import cn.gym.events.annotations.EventPriority;
import cn.gym.events.annotations.EventTarget;
import cn.gym.events.impl.Event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private static Map<Method, Class<?>> registeredMethodMap;
    private static Map<Method, Object> methodObjectMap;
    private static Map<Class<? extends Event>, List<Method>> priorityMethodMap;

    public EventManager() {
        registeredMethodMap = new ConcurrentHashMap<>();
        methodObjectMap = new ConcurrentHashMap<>();
        priorityMethodMap = new ConcurrentHashMap<>();
    }

    private static final Comparator<Method> EVENT_HANDLER_COMPARATOR = Comparator.comparingInt(it -> {
        EventPriority priority = it.getAnnotation(EventPriority.class);
        return (priority != null) ? priority.value() : 10;
    });

    /**
     * Registers one or more objects to associate their methods with event annotations and stores them in the event handler.
     *
     * @param obj One or more objects to register.
     */
    public static void register(Object... obj) {
        for (Object object : obj) {
            register(object);
        }
    }

    /**
     * Registers an object to associate its methods with event annotations and stores them in the event handler.
     *
     * @param obj The object to register.
     */
    public static void register(Object obj) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Annotation[] annotations = method.getDeclaredAnnotations();

            for (Annotation annotation : annotations) {
                if (annotation.annotationType() == EventTarget.class && method.getParameterTypes().length == 1) {
                    registeredMethodMap.put(method, method.getParameterTypes()[0]);
                    methodObjectMap.put(method, obj);

                    Class<? extends Event> eventClass = method.getParameterTypes()[0].asSubclass(Event.class);
                    method.setAccessible(true);
                    final var methodList = priorityMethodMap.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>());
                    methodList.add(method);
                    methodList.sort(EVENT_HANDLER_COMPARATOR);
                }
            }
        }
    }

    /**
     * Unregisters an object, removing its associated methods from the event handler.
     *
     * @param obj The object to unregister.
     */
    public static void unregister(Object obj) {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (registeredMethodMap.containsKey(method)) {
                registeredMethodMap.remove(method);
                methodObjectMap.remove(method);
                Class<? extends Event> eventClass = method.getParameterTypes()[0].asSubclass(Event.class);
                List<Method> priorityMethods = priorityMethodMap.get(eventClass);
                if (priorityMethods != null) {
                    priorityMethods.remove(method);
                }
            }
        }
    }

    /**
     * Calls the registered methods associated with the provided event, respecting their priorities.
     *
     * @param event The event to call the registered methods for.
     * @return The modified or processed event after calling the methods.
     */
    public static Event call(Event event) {
        Class<? extends Event> eventClass = event.getClass();

        List<Method> methods = priorityMethodMap.get(eventClass);
        if (methods != null) {
            for (Method method : methods) {
                Object obj = methodObjectMap.get(method);
                try {
                    method.invoke(obj, event);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }

        return event;
    }
}