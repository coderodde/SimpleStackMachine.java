package io.github.coderodde.simple.stack.machine;

import java.util.HashMap;
import java.util.Map;

/**
 * This enumeration specifies the entire machine language of our stack virtual
 * machine.
 * 
 * @version 1.1.0 (Jul 8, 2025)
 * @since 1.0.0 (Jul 3, 2025)
 */
public enum Operation {
    
    NOP   ("nop",   (byte) 0x0), // No-operation.
    PUSH  ("push",  (byte) 0x1), // Push a value to the stack.
    POP   ("pop",   (byte) 0x2), // Pop the stack.
    CONST ("const", (byte) 0x0), // Push a value to the stack. Effectively, POP.
    LOAD  ("load",  (byte) 0x4), // Pushes a value from the tape to the stack.
    STORE ("store", (byte) 0x5), // Pops the stack and stores the popped value 
                                 // at a given address of the tape.
    
    ADD  ("add",  (byte) 0x6), // Pops two values from the stack, and pushes the
                               // sum of two to the stack.
    SUB  ("sub",  (byte) 0x7), // Just like "add" but stores the difference
                               // between the top most number and the second
                               // top most number.
    MUL  ("mul",  (byte) 0x8), // Multiplies the two topmost numbers.
    DIV  ("div",  (byte) 0x9), // Divides the two numbers. Top most divided by
                               // the second top most.
    MOD  ("mod",  (byte) 0xa), // The modulo operation, a % b, where a is the
                               // top most number and b is under a. (Top is the
                               // highest.)
    CALL ("call", (byte) 0xb), // Procedure call.
    RET  ("ret",  (byte) 0xc), // Procedure exit request.
    DUP  ("dup",  (byte) 0xd), // Duplicates the stack.
    SWAP ("swap", (byte) 0xe), // Swaps the two top most numbers in the stack.
    
    CMP ("cmp", (byte) 0x0f), // Compares the two top most numbers. Sets a
                              // status flag.
    JMP ("jmp", (byte) 0x10), // Unconditional jump.
    JZ  ("jz",  (byte) 0x11), // Jump if zero flag is set.
    JNZ ("jnz", (byte) 0x12), // Jump if zero flag is not set.
    JBZ ("jbz", (byte) 0x13), // Jump if the "below zero" flag is set.
    JAZ ("jaz", (byte) 0x14), // Jump if the "above zero" flag is set.
    JL  ("jL",  (byte) 0xf0), // Jump if the 
    
    PRINT_INT    ("iout", (byte) 0x15), // Prints the top most number.
    PRINT_STRING ("sout", (byte) 0x16), // Prints the string with bytes on the 
                                        // stack.
    READ_INT     ("iin",  (byte) 0x17), // Read a single number and push it on 
                                        // the stack.
    READ_STRING  ("sin",  (byte) 0x18); // Reads a null-terminated string and
                                        // stores its bytes in the top of the 
                                        // stack.
    
    private static final Map<String, Operation> mapOperationNameToOperationEnum 
            = new HashMap<>();
    
    private final byte opcodeByte;
    
    private Operation(final String name, 
                      final byte opcodeByte) {
        this.opcodeByte = opcodeByte;
    }
    
    public Operation getOperation(final String operationName) {
        return mapOperationNameToOperationEnum.get(operationName);
    }
    
    static {
        for (final Operation o : Operation.values()) {
            mapOperationNameToOperationEnum.put(o.name(), o);
        }
    }
}
