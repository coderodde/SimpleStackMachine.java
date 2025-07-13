package io.github.coderodde.simple.stack.machine;

import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.AddInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.CallInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.CompareInstructionImplementation;    
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.ConstInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.DivideInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.DuplicateInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.HaltInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfAboveInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfAboveOrEqualInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfAboveZeroInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfBelowInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfBelowOrEqualInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfBelowZeroInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfEqualInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfNotEqualInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfNotZeroInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.JumpIfZeroInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.LoadInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.ModuloInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.MultiplyInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.NopInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.PopInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.PrintNumberInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.PrintStringInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.PushInstructionImplementation;
import io.github.coderodde.simple.stack.machine.MachineLanguageSpecification.ReturnInstructionImplementation;
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
 * @version 1.2.0 (Jul 9, 2025)
 * @since 1.0.0 (Jul 3, 2025)
 */
public enum Operation {
    
    NOP   ("nop"  , (byte) 0x0, new NopInstructionImplementation()),
    PUSH  ("push" , (byte) 0x1, new PushInstructionImplementation()),
    POP   ("pop"  , (byte) 0x2, new PopInstructionImplementation()), 
    CONST ("const", (byte) 0x0, new ConstInstructionImplementation()),
    LOAD  ("load" , (byte) 0x4, new LoadInstructionImplementation()),
    STORE ("store", (byte) 0x5, new StoreInstructionImplementation()),
    
    ADD  ("add", (byte) 0x6, new AddInstructionImplementation()),
    SUB  ("sub", (byte) 0x7, new SubInstructionImplementation()),
    MUL  ("mul", (byte) 0x8, new MultiplyInstructionImplementation()),
    DIV  ("div", (byte) 0x9, new DivideInstructionImplementation()),
    MOD  ("mod", (byte) 0xa, new ModuloInstructionImplementation()),
    
    CALL ("call", (byte) 0xb, new CallInstructionImplementation()), 
    RET  ("ret" , (byte) 0xc, new ReturnInstructionImplementation()),
    DUP  ("dup" , (byte) 0xd, new DuplicateInstructionImplementation()),
    SWAP ("swap", (byte) 0xe, new SwapInstructionImplementation()),
    
    CMP ("cmp", (byte) 0x0f, new CompareInstructionImplementation()),
    JMP ("jmp", (byte) 0x10, new UnconditionalJumpInstructionImplementation()), 
    JZ  ("jz" , (byte) 0x11, new JumpIfZeroInstructionImplementation()), 
    JNZ ("jnz", (byte) 0x12, new JumpIfNotZeroInstructionImplementation()), 
    JBZ ("jbz", (byte) 0x13, new JumpIfBelowZeroInstructionImplementation()), 
    JAZ ("jaz", (byte) 0x14, new JumpIfAboveZeroInstructionImplementation()),
    JL  ("jl" , (byte) 0xf0, new JumpIfBelowInstructionImplementation()), 
    JLE ("jle", (byte) 0xf1, new JumpIfBelowOrEqualInstructionImplementation()),
    JE  ("je" , (byte) 0xf2, new JumpIfEqualInstructionImplementation()),
    JNE ("jne", (byte) 0xf3, new JumpIfNotEqualInstructionImplementation()),
    JA  ("ja" , (byte) 0xf4, new JumpIfAboveInstructionImplementation()), 
    JAE ("jae", (byte) 0xf5, new JumpIfAboveOrEqualInstructionImplementation()),
    
    PRINT_INT    ("iout", (byte) 0x15, new PrintNumberInstructionImplementation()),
    PRINT_STRING ("sout", (byte) 0x16, new PrintStringInstructionImplementation()),
    READ_INT     ("iin",  (byte) 0x17, null), 
    READ_STRING  ("sin",  (byte) 0x18, null), 
    HALT         ("halt", (byte) 0xff, new HaltInstructionImplementation());
    
    private static final Map<String, Operation> mapOperationNameToOperationEnum 
            = new HashMap<>();
    
    private static final Map<Byte, Operation> mapOperationByteToOperatoinEnum = 
            new HashMap<>();
    
    private final String opcodeName;
    private final byte opcodeByte;
    private final InstructionImplementation impl;
    
    private Operation(final String opcodeName, 
                      final byte opcodeByte,
                      final InstructionImplementation impl) {
        this.opcodeName = opcodeName;
        this.opcodeByte = opcodeByte;
        this.impl = impl;
    }
    
    public InstructionImplementation getImpl() {
        return this.impl;
    }
    
    public String getOperationName() {
        return opcodeName;
    }
    
    public byte getOpcodeByte() {
        return this.opcodeByte;
    }
    
    public static Operation getOperation(final String operationName) {
        return mapOperationNameToOperationEnum.get(operationName);
    }
    
    public static Operation getOperation(final byte opcode) {
        return mapOperationByteToOperatoinEnum.get(opcode);
    }
    
    static {
        for (final Operation o : Operation.values()) {
            mapOperationNameToOperationEnum.put(o.name(), o);
            mapOperationByteToOperatoinEnum.put(o.opcodeByte, o);
        }
    }
}
