package io.github.coderodde.simple.stack.machine;

import org.junit.Test;

public class MachineLanguageSpecificationTest {
    
    @Test
    public void printString() {
        final SimpleStackMachine machine = new SimpleStackMachine();
        final CodeBuilder cb = new CodeBuilder(1000);
        
        cb.emit(Operation.PUSH.getOpcodeByte());
        cb.emit(900);
        cb.emit(Operation.PUSH.getOpcodeByte());
        cb.emit(6);
        cb.emit(Operation.PRINT_STRING.getOpcodeByte());
        cb.emit(Operation.HALT.getOpcodeByte());
        cb.emit("Hello!", 900);
        
        machine.execute(cb.toByteArray());
    }
}
