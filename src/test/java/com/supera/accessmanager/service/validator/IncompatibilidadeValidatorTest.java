package com.supera.accessmanager.service.validator;

import com.supera.accessmanager.domain.modulo.Modulo;
import com.supera.accessmanager.domain.modulo.TipoModulo;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class IncompatibilidadeValidatorTest {

    private IncompatibilidadeValidator validator;

    private Modulo moduloA;
    private Modulo moduloB;
    private Modulo moduloC;

    @BeforeEach
    void setup() {
        validator = new IncompatibilidadeValidator();

        moduloA = Instancio.of(Modulo.class)
                .ignore(Select.field(Modulo.class, "modulosIncompativeis"))
                .create();
        moduloA.setModulosIncompativeis(Set.of());
        moduloA.setTipo(TipoModulo.RELATORIOS_GERENCIAIS);

        moduloB = Instancio.of(Modulo.class)
                .ignore(Select.field(Modulo.class, "modulosIncompativeis"))
                .create();
        moduloB.setModulosIncompativeis(Set.of());
        moduloB.setTipo(TipoModulo.GESTAO_FINANCEIRA);

        moduloC = Instancio.of(Modulo.class)
                .ignore(Select.field(Modulo.class, "modulosIncompativeis"))
                .create();
        moduloC.setModulosIncompativeis(Set.of());
        moduloC.setTipo(TipoModulo.APROVADOR_FINANCEIRO);
    }

    @Test
    void deveValidarComSucessoQuandoNaoHaIncompatibilidades() {
        Set<Modulo> modulos = Set.of(moduloA, moduloB, moduloC);

        assertDoesNotThrow(() -> validator.validar(modulos));
    }

    @Test
    void deveLancarErroQuandoModulosSaoIncompativeis() {
        moduloA.setModulosIncompativeis(Set.of(moduloB));

        Set<Modulo> modulos = Set.of(moduloA, moduloB);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validar(modulos));

        assertEquals(
                "Os módulos " + moduloA.getTipo() + " e " + moduloB.getTipo() + " são incompatíveis.",
                ex.getMessage()
        );
    }

    @Test
    void deveLancarErroMesmoQuandoApenasUmLadoEhIncompativel() {
        moduloC.setModulosIncompativeis(Set.of(moduloA));

        Set<Modulo> modulos = Set.of(moduloA, moduloC);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> validator.validar(modulos));

        assertEquals(
                "Os módulos " + moduloC.getTipo() + " e " + moduloA.getTipo() + " são incompatíveis.",
                ex.getMessage()
        );
    }

    @Test
    void naoDeveLancarErroQuandoComparaModuloComEleMesmo() {
        moduloA.setModulosIncompativeis(Set.of());

        Set<Modulo> modulos = Set.of(moduloA);

        assertDoesNotThrow(() -> validator.validar(modulos));
    }
}
