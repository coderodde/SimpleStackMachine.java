package io.github.coderodde.simple.stack.machine;

import org.junit.Test;

public class MachineLanguageSpecificationTest {
    
    @Test
    public void printString() {
        final SimpleStackMachine machine = new SimpleStackMachine();
        final CodeBuilder cb = new CodeBuilder(1000);
        
        cb.emit(Operation.PUSH.getOpcodeByte());
        cb.emit(6);
        cb.emit(Operation.PUSH.getOpcodeByte());
        cb.emit(900);
        cb.emit(Operation.PRINT_STRING.getOpcodeByte());
        cb.emit(Operation.HALT.getOpcodeByte());
        cb.emit("Hello!", 900);
        
        machine.execute(cb.toByteArray());
    }
    
    private static class CodeBuilder {
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
                (byte)(value >>> 24), // highest byte (big-endian)
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value
    };
}
    }
}
