package io.github.coderodde.simple.stack.machine;

import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.AddInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.CompareInstructionImplementation;    
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.ConstInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.DivideInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.DuplicateInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.HaltInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.LoadInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.ModuloInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.MultiplyInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.NopInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.PopInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.PushInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.StoreInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.SubInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.SwapInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.UnconditionalJumpInstructionImplementation;
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
    
    NOP   ("nop"  , (byte) 0x0, new NopInstructionImplementation()),
    PUSH  ("push" , (byte) 0x1, new PushInstructionImplementation()), // Push a value to the stack.
    POP   ("pop"  , (byte) 0x2, new PopInstructionImplementation()), // Pop the stack.
    CONST ("const", (byte) 0x0, new ConstInstructionImplementation()), // Push a value to the stack. Effectively, POP.
    LOAD  ("load" , (byte) 0x4, new LoadInstructionImplementation()), // Pushes a value from the tape to the stack.
    STORE ("store", (byte) 0x5, new StoreInstructionImplementation()), // Pops the stack and stores the popped value 
                                 // at a given address of the tape.
    
    ADD  ("add", (byte) 0x6, new AddInstructionImplementation()), // Pops two values from the stack, and pushes the
                              // sum of two to the stack.
    SUB  ("sub", (byte) 0x7, new SubInstructionImplementation()), // Just like "add" but stores the difference
                              // between the top most number and the second
                              // top most number.
    MUL  ("mul", (byte) 0x8, new MultiplyInstructionImplementation()), // Multiplies the two topmost numbers.
    DIV  ("div", (byte) 0x9, new DivideInstructionImplementation()), // Divides the two numbers. Top most divided by
                              // the second top most.
    MOD  ("mod", (byte) 0xa, new ModuloInstructionImplementation()), // The modulo operation, a % b, where a is the
                              // top most number and b is under a. (Top is the
                              // highest.)
    
    CALL ("call", (byte) 0xb), // Procedure call.
    RET  ("ret" , (byte) 0xc), // Procedure exit request.
    DUP  ("dup" , (byte) 0xd, new DuplicateInstructionImplementation()), // Duplicates the stack.
    SWAP ("swap", (byte) 0xe, new SwapInstructionImplementation()), // Swaps the two top most numbers in the stack.
    
    CMP ("cmp", (byte) 0x0f, new CompareInstructionImplementation()), // Compares the two top most numbers. Sets a
                              // status flag.
    JMP ("jmp", (byte) 0x10, new UnconditionalJumpInstructionImplementation()), // Unconditional jump.
    JZ  ("jz" , (byte) 0x11), // Jump if zero flag is set.
    JNZ ("jnz", (byte) 0x12), // Jump if zero flag is not set.
    JBZ ("jbz", (byte) 0x13), // Jump if the "below zero" flag is set.
    JAZ ("jaz", (byte) 0x14), // Jump if the "above zero" flag is set.
    JL  ("jl" , (byte) 0xf0), // Jump if the less than relation.
    JLE ("jle", (byte) 0xf1), // Jump if the less than or equal relation.
    JE  ("je" , (byte) 0xf2), // Jump if equal.
    JNE ("jne", (byte) 0xf3), // Jump if not equal.
    JA  ("ja" , (byte) 0xf4), // Jump if above.
    JAE ("jae", (byte) 0xf5), // Jump if above or equal.
    
    PRINT_INT    ("iout", (byte) 0x15), // Prints the top most number.
    PRINT_STRING ("sout", (byte) 0x16), // Prints the string with bytes on the 
                                        // stack.
    READ_INT     ("iin", (byte) 0x17), // Read a single number and push it on 
                                        // the stack.
    READ_STRING  ("sin", (byte) 0x18), // Reads a null-terminated string and
                                        // stores its bytes in the top of the 
                                        // stack.
    HALT ("halt", (byte) 0xff, new HaltInstructionImplementation()); // Halts the machine.
    
    private static final Map<String, Operation> mapOperationNameToOperationEnum 
            = new HashMap<>();
    
    private final byte opcodeByte;
    private final InstructionImplementation impl;
    
    private Operation(final String name, 
                      final byte opcodeByte,
                      final InstructionImplementation impl) {
        this.opcodeByte = opcodeByte;
        this.impl = impl;
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
