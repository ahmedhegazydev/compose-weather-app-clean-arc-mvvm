/**
 * Extension function to check if a character is either a letter or a whitespace.
 *
 * @receiver Char The character to check.
 * @return Boolean Returns `true` if the character is a letter or a whitespace, otherwise `false`.
 *
 */
fun Char.isLetterOrWhitespace(): Boolean {
    return this.isLetter() || this.isWhitespace()
}
