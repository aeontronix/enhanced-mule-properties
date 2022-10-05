package com.aeontronix.enhancedmule.propertiesprovider;

public class EncryptionKeyMissingException extends Exception {
    public EncryptionKeyMissingException() {
        super("Encryption key missing");
    }
}
