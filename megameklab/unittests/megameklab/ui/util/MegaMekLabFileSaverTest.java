/*
 * Copyright (C) 2026 The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MegaMekLab.
 *
 * MegaMekLab is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPL),
 * version 3 or (at your option) any later version,
 * as published by the Free Software Foundation.
 *
 * MegaMekLab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * A copy of the GPL should have been included with this project;
 * if not, see <https://www.gnu.org/licenses/>.
 *
 * NOTICE: The MegaMek organization is a non-profit group of volunteers
 * creating free software for the BattleTech community.
 *
 * MechWarrior, BattleMech, `Mech and AeroTech are registered trademarks
 * of The Topps Company, Inc. All Rights Reserved.
 *
 * Catalyst Game Labs and the Catalyst Game Labs logo are trademarks of
 * InMediaRes Productions, LLC.
 *
 * MechWarrior Copyright Microsoft Corporation. MegaMekLab was created under
 * Microsoft's "Game Content Usage Rules"
 * <https://www.xbox.com/en-US/developers/rules> and it is not endorsed by or
 * affiliated with Microsoft.
 */
package megameklab.ui.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import megamek.common.units.BipedMek;
import megamek.common.units.Entity;
import megamek.logging.MMLogger;
import megameklab.testing.util.InitializeTypes;
import megameklab.ui.PopupMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

@ExtendWith(value = InitializeTypes.class)
class MegaMekLabFileSaverTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void saveAsWritesAndAdoptsNewUUID() throws Exception {
        Entity entity = new BipedMek();
        String previousUUID = entity.getUnitFileUUID();
        File destination = temporaryDirectory.resolve("saved-as.mtf").toFile();
        MegaMekLabFileSaver saver = new MegaMekLabFileSaver(
              MMLogger.create(MegaMekLabFileSaverTest.class), "Save As");

        String savedFile;
        try (MockedStatic<PopupMessages> ignored = mockStatic(PopupMessages.class)) {
            savedFile = saver.saveUnitAsTo(null, destination, entity);
        }

        assertEquals(destination.toString(), savedFile);
        assertNotEquals(previousUUID, entity.getUnitFileUUID());
        assertTrue(Files.readString(destination.toPath()).contains("uuid:" + entity.getUnitFileUUID()));
    }

    @Test
    void failedSaveAsRestoresPreviousUUID() {
        Entity entity = new BipedMek();
        String previousUUID = entity.getUnitFileUUID();
        MegaMekLabFileSaver saver = new MegaMekLabFileSaver(
              MMLogger.create(MegaMekLabFileSaverTest.class), "Save As");

        String savedFile;
        try (MockedStatic<PopupMessages> ignored = mockStatic(PopupMessages.class)) {
            savedFile = saver.saveUnitAsTo(null, temporaryDirectory.toFile(), entity);
        }

        assertNull(savedFile);
        assertEquals(previousUUID, entity.getUnitFileUUID());
    }
}
