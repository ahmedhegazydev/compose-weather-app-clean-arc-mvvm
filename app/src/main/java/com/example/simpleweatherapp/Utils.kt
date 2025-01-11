package com.example.simpleweatherapp;

/**
 * A wrapper for data that is exposed via a LiveData that represents an event.
 *
 * @param T The type of content being wrapped.
 *
 * This class prevents the content from being handled multiple times by keeping
 * track of whether it has been accessed or not.
 */
class Event<out T>(private val content: T) {

    // Flag to check if the event has been handled.
    private var hasBeenHandled = false;

    /**
     * Returns the content if it hasn't been handled yet, otherwise returns null.
     *
     * Once the content is accessed through this method, it is marked as handled.
     *
     * @return The content if not handled, otherwise null.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null;
        } else {
            hasBeenHandled = true;
            content;
        }
    }

    /**
     * Returns the content even if it has already been handled.
     *
     * This can be used to inspect the value without marking it as handled.
     *
     * @return The content.
     */
    fun peekContent(): T = content;
}
