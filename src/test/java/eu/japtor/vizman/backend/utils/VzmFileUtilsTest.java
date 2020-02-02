package eu.japtor.vizman.backend.utils;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class VzmFileUtilsTest {

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
        String expectedPath = "zscrdtn_ZSCRDTN_aaeiouuuy_AAEIOUUUY";
        assertThat("Normalized string is not as expected"
                , VzmFileUtils.normalizeDirFileName(pathToTest), is(expectedPath));
    }

}