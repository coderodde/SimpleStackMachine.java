package io.github.coderodde.simple.stack.machine;

public class CodeBuilder {
    
    private final byte[] code;
    private int pointer = 0;

    CodeBuilder(int capacity) {
        this.code = new byte[capacity];
    }

    void emit(byte b) {
        code[pointer++] = b;
    }

    void emit(int word) {
        for (final byte b : intToBytes(word)) {
            emit(b);
        }
    }
    
    void setInstructionPointer(int pointer) {
        this.pointer = pointer; 
    }

    void emit(String str) {
        byte[] stringBytes = str.getBytes();

        for (final byte b : stringBytes) {
            emit(b);
        }
    }

    void emit(String str, int startIndex) {
        byte[] stringBytes = str.getBytes();

        for (int i = 0; i < stringBytes.length; ++i) {
            code[startIndex + i] = stringBytes[i];
        }
    }

    byte[] toByteArray() {
        return code;
    }

    static byte[] intToBytes(int value) {
        return new byte[] {
            (byte)(value),
            (byte)(value >>> 8),
            (byte)(value >>> 16),
            (byte)(value >>> 24)};
    }
}