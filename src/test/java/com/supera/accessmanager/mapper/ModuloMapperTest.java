package com.supera.accessmanager.mapper;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModuloMapperTest {

    private static Object newInstance(Class<?> cls) throws Exception {
        try {
            Constructor<?> ctor = cls.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException e) {
            return cls.getDeclaredConstructor().newInstance();
        }
    }

    private static void setProperty(Object target, String propName, Object value) throws Exception {
        Class<?> cls = target.getClass();
        String setter = "set" + Character.toUpperCase(propName.charAt(0)) + propName.substring(1);

        for (Method m : cls.getMethods()) {
            if (m.getName().equals(setter) && m.getParameterCount() == 1) {
                m.invoke(target, value);
                return;
            }
        }
        Field f = cls.getDeclaredField(propName);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Test
    void toResponse_deveMapearTodosOsCamposCorretamente() throws Exception {
        Class<?> moduloCls = Class.forName("com.supera.accessmanager.domain.modulo.Modulo");

        Object modulo = newInstance(moduloCls);

        setProperty(modulo, "id", 123L);

        Method getTipo = moduloCls.getMethod("getTipo");
        Class<?> tipoEnumClass = getTipo.getReturnType();
        Object[] tipoConsts = tipoEnumClass.getEnumConstants();
        assertNotNull(tipoConsts, "Enum de tipo do módulo deve existir");
        Object tipo1 = tipoConsts[0];
        Object tipo2 = tipoConsts.length > 1 ? tipoConsts[1] : tipoConsts[0];

        setProperty(modulo, "tipo", tipo1);

        setProperty(modulo, "descricao", "Descrição teste");
        setProperty(modulo, "ativo", true);

        Class<?> deptEnumClass = Class.forName("com.supera.accessmanager.domain.usuario.Departamento");
        Object[] deptConsts = deptEnumClass.getEnumConstants();
        assertNotNull(deptConsts, "Enum Departamento deve existir");
        Object deptExample = deptConsts[0];
        Set<Object> deptSet = Collections.singleton(deptExample);
        setProperty(modulo, "departamentosPermitidos", deptSet);

        Object otherModulo = newInstance(moduloCls);
        setProperty(otherModulo, "id", 321L);
        setProperty(otherModulo, "tipo", tipo2);
        setProperty(otherModulo, "descricao", "Outro");
        setProperty(otherModulo, "ativo", true);

        Set<Object> incompSet = Collections.singleton(otherModulo);
        setProperty(modulo, "modulosIncompativeis", incompSet);

        var response = ModuloMapper.toResponse((com.supera.accessmanager.domain.modulo.Modulo) modulo);

        assertNotNull(response);
        assertEquals(123L, response.id());
        assertEquals(((Enum<?>) tipo1).name(), response.tipo());
        assertEquals("Descrição teste", response.descricao());
        assertTrue(response.ativo());

        assertEquals(1, response.departamentosPermitidos().size());
        assertTrue(response.departamentosPermitidos().contains(((Enum<?>) deptExample).name()));

        assertEquals(1, response.modulosIncompativeis().size());
        assertTrue(response.modulosIncompativeis().contains(((Enum<?>) tipo2).name()));
    }


    @Test
    void toResponse_comColecoesVazias_deveRetornarSetsVazios() throws Exception {
        Class<?> moduloCls = Class.forName("com.supera.accessmanager.domain.modulo.Modulo");
        Object modulo = newInstance(moduloCls);

        setProperty(modulo, "id", 10L);

        Method getTipo = moduloCls.getMethod("getTipo");
        Class<?> tipoEnumClass = getTipo.getReturnType();
        Object[] tipoConsts = tipoEnumClass.getEnumConstants();
        assertNotNull(tipoConsts);
        Object tipo = tipoConsts[0];
        setProperty(modulo, "tipo", tipo);

        setProperty(modulo, "descricao", "vazio");
        setProperty(modulo, "ativo", false);

        setProperty(modulo, "departamentosPermitidos", Collections.emptySet());
        setProperty(modulo, "modulosIncompativeis", Collections.emptySet());

        var response = ModuloMapper.toResponse((com.supera.accessmanager.domain.modulo.Modulo) modulo);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals(((Enum<?>) tipo).name(), response.tipo());
        assertEquals("vazio", response.descricao());
        assertFalse(response.ativo());
        assertTrue(response.departamentosPermitidos().isEmpty());
        assertTrue(response.modulosIncompativeis().isEmpty());
    }
}
