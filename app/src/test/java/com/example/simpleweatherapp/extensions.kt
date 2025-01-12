import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CharExtensionsTest {

    @Test
    fun `test isLetterOrWhitespace with letter`() {
        // Letters should return true
        assertTrue('a'.isLetterOrWhitespace())
        assertTrue('Z'.isLetterOrWhitespace())
    }

    @Test
    fun `test isLetterOrWhitespace with whitespace`() {
        // Whitespace characters should return true
        assertTrue(' '.isLetterOrWhitespace())
        assertTrue('\n'.isLetterOrWhitespace())
        assertTrue('\t'.isLetterOrWhitespace())
    }

    @Test
    fun `test isLetterOrWhitespace with digit`() {
        // Digits should return false
        assertFalse('1'.isLetterOrWhitespace())
        assertFalse('0'.isLetterOrWhitespace())
    }

    @Test
    fun `test isLetterOrWhitespace with special characters`() {
        // Special characters should return false
        assertFalse('#'.isLetterOrWhitespace())
        assertFalse('@'.isLetterOrWhitespace())
        assertFalse('!'.isLetterOrWhitespace())
    }

    @Test
    fun `test isLetterOrWhitespace with control characters`() {
        // Control characters should return false
        assertFalse('\u0000'.isLetterOrWhitespace())
    }
}
