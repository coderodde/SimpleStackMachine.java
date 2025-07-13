package io.github.coderodde.simple.stack.machine;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Scanner;

/**
 * This class implements the actual stack virtual machine.
 * 
 * @version 1.1.0 (Jul 3, 2025)
 * @since 1.0.0 (Jul 3, 2025)
 */
public class SimpleStackMachine {
    
    private final Scanner scanner = new Scanner(System.in);
    
    int getInstructionPointer() {
        return instructionPointer;
    }
    
    void setInstructionPointer(final int address) {
        this.instructionPointer = address;
    }
    
    void printString(final int characterTotal, 
                     final long[] stringByteData) {
        final StringBuilder sb = new StringBuilder();
        
        loadBytes(characterTotal,
                  stringByteData,
                  sb);
        
        // Note, no "ln". The client programmer must append an '\n' to the end
        // of the printed string in order to have a new line:
        System.out.print(sb.toString());
    }
    
    void loadBytes(final int stringLength,
                   final long[] stringByteData, 
                   final StringBuilder sb) {
        
        for (int byteIndex = 0; 
                 byteIndex < stringLength
                ; 
                 byteIndex++) {
            final int stringByteDataIndex = byteIndex / Byte.SIZE;
            final int localByteIndex      = byteIndex % Byte.SIZE;
            
            final byte b =
                    (byte)((stringByteData[stringByteDataIndex] >>>
                            localByteIndex * Byte.SIZE) & 0xff);
            
            sb.append((char)(b & 0xff));
        }
    }
    
    int top() {
        requireStackSize(1);
        return stack.element();
    }
    
    int pop() {
        return stack.pop();
    }
    
    void push(final int datum) {
        stack.push(datum);
    }
    
    /** 
     * The length of the memory tape in bytes. Effectively, 16 kilobytes.
     */
    private static final int TAPE_LENGTH_IN_BYTES = 16 * 1024;
   
    /**
     * This inner static class models all the processor flags.
     */
    final class ProcessorFlags {
        
        /**
         * Set to {@code true} if and only if the most previous comparison 
         * ended up with equality.
         */
        boolean equalFlag = false;
       
        /**
         * Set to {@code true} if and only if the most previous comparison ended
         * up with non-equality.
         */
        boolean notEqualFlag = false;
        
        /**
         * Set to {@code true} if the comparison did not end with equality.
         */
        boolean zeroFlag = false;
        
        /**
         * Set to {@code true} if the test argument was not zero.
         */
        boolean notZeroFlag = false;
        
        /**
         * Set to {@code true} if the test argument was above zero.
         */
        boolean aboveZeroFlag = false;
        
        /**
         * Set to {@code true} if the test argument was below zero.
         */
        boolean belowZeroFlag = false;
        
        /**
         * Set to {@code true} if and only if the most previous comparison 
         * concluded that the top most number was larger than the second top 
         * most one.
         */
        boolean aboveFlag = false;
        
        /**
         * Set to {@code true} if and only if the most previous comparison 
         * concluded that the top most number was smaller than the second top 
         * most one.
         */
        boolean belowFlag = false;
        
        /**
         * Clears all the flags.
         */
        void unsetAll() {
            equalFlag     = false;
            notEqualFlag  = false;
            aboveFlag     = false;
            belowFlag     = false;
            aboveZeroFlag = false;
            belowZeroFlag = false;
            zeroFlag      = false;
            notZeroFlag   = false;
        }
    }
    
    /**
     * The flags object.
     */
    private final ProcessorFlags flags = new ProcessorFlags();
    
    /**
     * The memory tape.
     */
    private final byte[] tape = new byte[TAPE_LENGTH_IN_BYTES];
    
    /**
     * The operand stack. 
     */
    private final Deque<Integer> stack = new ArrayDeque<>();

    /**
     * The instruction pointer.
     */
    private int instructionPointer = 0;
    
    /**
     * The halting flag. When set to {@code true}, execution must end.
     */
    private boolean haltIsRequested = false;
    
    public ProcessorFlags flags() {
        return flags;
    }
    
    public void execute(final byte[] programBytes) {
        Objects.requireNonNull(programBytes,
                               "The input program byte array is null.");
        
        if (programBytes.length > tape.length) {
            final String exceptionMessage =
                    String.format(
                            "programBytes.length(%d) > tape.length(%d)", 
                            programBytes.length, 
                            tape.length);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
        
        System.arraycopy(programBytes, 
                         0,
                         tape,
                         0,
                         programBytes.length);
        
        while (!haltIsRequested) {
            executeImpl();
        }
    }
    
    private void executeImpl() {
        final byte opcode = tape[instructionPointer];
//        final InstructionImplementation impl = Operation.
    }
    
    public static void main(String[] args) {
        
    }
    
    void requireStackSize(final int requestedSize) {
        if (stack.size() < requestedSize) {
            throw new StackMachineException(
                    String.format("'stack.size()' is %d, 'requestedSize' is %d",
                                  stack.size(),
                                  requestedSize));
        }
    }
    
    int readWordFromTape(final int address) {
        if (address + Integer.BYTES > tape.length) {
            final String exceptionMessage = 
                    String.format(
                            "address(%d) + Integer.BYTES(%d) > " + 
                            "tape.length(%d) = %d", 
                            address, 
                            Integer.BYTES, 
                            tape.length, 
                            Integer.BYTES + tape.length);
            
            throw new StackMachineException(exceptionMessage);
        }
        
        final int nb0 = Byte.toUnsignedInt(tape[address]);
        final int nb1 = Byte.toUnsignedInt(tape[address + 1]) << 8;
        final int nb2 = Byte.toUnsignedInt(tape[address + 2]) << 16;
        final int nb3 = Byte.toUnsignedInt(tape[address + 3]) << 24;
        
        return nb0 |
               nb1 |
               nb2 |
               nb3;
    }
    
    void writeWordToTape(final int address, int word) {
        
        final byte[] bytes = new byte[Integer.BYTES];
        
        bytes[0] = (byte) (word & 0xffL);
        bytes[1] = (byte)((word >>= Byte.SIZE) & 0xffL);
        bytes[2] = (byte)((word >>= Byte.SIZE) & 0xffL);
        bytes[3] = (byte)((word >>  Byte.SIZE) & 0xffL);
        
        System.arraycopy(bytes, 
                         0,
                         tape,
                         address, 
                         Integer.BYTES);
    }
    
    void advanceInstructionPointer() {
        checkTapeReserve(1);
        ++instructionPointer;
    }
    
    void advanceInstructionPointer(final int bytes) {
        checkTapeReserve(bytes);
        instructionPointer += bytes;
    }
    
    void checkTapeReserve(int bytesToReserve) {
        if (instructionPointer + bytesToReserve >= tape.length) {
            final String exceptionMessage = 
                    String.format(
                            "instructionPointer(%d) + bytesToReserve(%d) " + 
                                    "= %d >= tape.length(%d)",
                            instructionPointer,
                            bytesToReserve,
                            bytesToReserve + instructionPointer,
                            tape.length);
            
            throw new StackMachineException(exceptionMessage);
        }
    }
    
    void requestHalt() {
        haltIsRequested = true;
    }
}
