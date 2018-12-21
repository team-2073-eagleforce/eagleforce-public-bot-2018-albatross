package org.usfirst.frc.team2073.robot.util.inject;

import com.google.inject.MembersInjector;
import com.google.inject.Provider;

import java.lang.reflect.Field;

public class InjectNamedMembersInjector<T> implements MembersInjector<T> {
    private final Field field;
    private final Provider<?> provider;

    InjectNamedMembersInjector(Field field, Provider<?> provider) {
        this.field = field;
        this.provider = provider;
        field.setAccessible(true);
    }

    @Override
    public void injectMembers(T instance) {
        try {
            field.set(instance, provider.get());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
