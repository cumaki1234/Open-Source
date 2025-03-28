package org.sourceforge.kga;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class KitchenGardenAidTest {

    private KitchenGardenAid kitchenGardenAid;

    @BeforeEach
    void setUp() {
        kitchenGardenAid = KitchenGardenAid.getInstance(); // Usamos Singleton correctamente
    }

    @Test
    void getInstance_ShouldReturnSameInstance() {
        KitchenGardenAid instance1 = KitchenGardenAid.getInstance();
        KitchenGardenAid instance2 = KitchenGardenAid.getInstance();

        assertNotNull(instance1, "La instancia no debería ser nula");
        assertSame(instance1, instance2, "Se espera que las instancias sean las mismas");
    }

    @Test
    void start_ShouldShowStage() throws Exception {
        Stage mockStage = Mockito.mock(Stage.class);

        kitchenGardenAid.start(mockStage);

        // Verificamos que el método show() haya sido llamado en el Stage
        verify(mockStage).show();
    }

    @Test
    void main_ShouldNotThrowExceptions() {
        String[] args = {"test"};

        assertDoesNotThrow(() -> KitchenGardenAid.main(args),
                "El método main no debería lanzar excepciones");
    }
}
