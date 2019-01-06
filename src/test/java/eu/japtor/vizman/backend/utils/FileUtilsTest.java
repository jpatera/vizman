package eu.japtor.vizman.backend.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class FileUtilsTest {

    @Test
    void testSanitizeFilename() {
    }

    @Test
    void testValidatePathname() {
    }

    @Test
    void testValidateFile() {
    }

    @Test
    public void testNormalizeFilepath() {
        String pathToTest = "žščřďťň ŽŠČŘĎŤŇ áäéíóúůüý ÁÄÉÍÓÚŮÜÝ";
        String expectedPath = "zscrdtn ZSCRDTN aaeiouuuy AAEIOUUUYxx";
        assertThat("Normalized string is not as expected"
                , FileUtils.normalizeFilepath(pathToTest), is(expectedPath));
    }

}