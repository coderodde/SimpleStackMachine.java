package io.github.coderodde.simple.stack.machine;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.BinaryOperator;

/**
 * This class implements the actual stack virtual machine.
 * 
 * @version 1.1.0 (Jul 3, 2025)
 * @since 1.0.0 (Jul 3, 2025)
 */
public class SimpleStackMachine {
    
    public abstract class InstructionImplementation {
        
        private SimpleStackMachine machine;
        
        public void setMachine(final SimpleStackMachine machine) {
            this.machine = machine;
        } 
        
        public abstract void execute(final SimpleStackMachine machine);
    }
    
    public static final class NopInstructionImplementation 
            extends InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.advanceInstructionPointer();
        }
    }
    
    public static class PushInstructionImplementation 
            extends InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            
            final long number = 
                    machine.readNumberFromTape(machine.instructionPointer);
            
            machine.stack.push(number);
        }
    }
    
    public static final class PopInstructionImplementation 
            extends InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.advanceInstructionPointer();
            machine.stack.pop();
        }
    }
    
    public static final class LoadInstructionImplementation 
            extends InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            
            final long address = 
                    machine.readNumberFromTape(machine.instructionPointer);
            
            final long datum = machine.tape[(int) address];
            
            machine.stack.push(datum);
        }
    }
    
    public static final class StoreInstructionImplementation 
            extends InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkReserve(1 + 2 * Long.BYTES);
            machine.advanceInstructionPointer();
            
            final long address = 
                    machine.readNumberFromTape(machine.instructionPointer);
            
            machine.advanceInstructionPointer(Long.BYTES);
            
            final long datum = machine.readNumberFromTape(machine.instructionPointer);
            
            machine.writeNumberToTape((int) address, datum);
            machine.advanceInstructionPointer(Long.BYTES);
        }
    }
    
    /**
     * Delegates to the pop operation.
     */
    public static final class ConstInstructionImplementation 
            extends PushInstructionImplementation {
        
    }
    
    public static class BinaryArithmeticInstructionImplementation 
            extends InstructionImplementation {
        
        private final BinaryOperator<Long> func;
        
        public BinaryArithmeticInstructionImplementation(
                final BinaryOperator<Long> func) {
            this.func = func;
        }

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkReserve(1 + 2 * Long.BYTES);
            machine.requireStackSize(2);
            machine.advanceInstructionPointer();
            
            final long number1 = 
                    machine.readNumberFromTape(machine.instructionPointer);
            
            final long number2 = 
                    machine.readNumberFromTape(
                            machine.instructionPointer + Long.BYTES);
            
            machine.stack.pop();
            machine.stack.pop();
            machine.stack.push(func.apply(number1, number2));
            machine.advanceInstructionPointer(Long.BYTES * 2);
        }
    }
    
    public static final class AddInstructionImplementation 
            extends BinaryArithmeticInstructionImplementation {

        public AddInstructionImplementation() {
            super((n1, n2) -> { return n1 + n2; });
        }
    }
    
    public static final class SubInstructionImplementation 
            extends BinaryArithmeticInstructionImplementation {

        public SubInstructionImplementation() {
            super((n1, n2) -> { return n1 - n2; });
        }
    }
    
    public static final class MultiplyInstructionImplementation 
            extends BinaryArithmeticInstructionImplementation {

        public MultiplyInstructionImplementation() {
            super((n1, n2) -> { return n1 * n2; });
        }
    }
    
    public static final class DivideInstructionImplementation 
            extends BinaryArithmeticInstructionImplementation {

        public DivideInstructionImplementation() {
            super((n1, n2) -> { return n1 / n2; });
        }
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            try {
                super.execute(machine);
            } catch (final ArithmeticException ex) {
                throw new StackMachineException(ex.getMessage());
            }
        }
    }
    
    public static final class ModuloInstructionImplementation 
            extends BinaryArithmeticInstructionImplementation {

        public ModuloInstructionImplementation() {
            super((n1, n2) -> { return n1 % n2; });
        }
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            try {
                super.execute(machine);
            } catch (final ArithmeticException ex) {
                throw new StackMachineException(ex.getMessage());
            }
        }
    }
    
    public static final class DuplicateInstructionImplementation 
            extends InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.requireStackSize(1);
            machine.stack.push(machine.stack.element());
            machine.advanceInstructionPointer();
        }
    }
    
    public static final class SwapInstructionImplementation 
            extends InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.requireStackSize(2);
            final long number1 = machine.stack.pop();
            final long number2 = machine.stack.pop();
            machine.stack.push(number1);
            machine.stack.push(number2);
        }
    }
    
    public static final class CompareInstructionImplementation 
            extends InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.requireStackSize(2);
            final long number1 = machine.stack.pop();
            final long number2 = machine.stack.pop();
            final int cmp = Long.compare(number1, 
                                         number2);
            
            if (cmp < 0) {
                machine.flags.belowFlag = true;
            } else if (cmp > 0) {
                machine.flags.aboveFlag = true;
            } else {
                machine.flags.equalFlag = true;
            }
        }
    }
    
    public static final class UnconditionalJumpInstructionImplementation 
            extends InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            final int jumpAddress = 
                    (int) machine.readNumberFromTape(
                               machine.instructionPointer);
            
            machine.checkReserve(jumpAddress);
            machine.instructionPointer = jumpAddress;
        }
    }
    
    public static final class UnconditionalJumpInstructionImplementation 
            extends InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            final int jumpAddress = 
                    (int) machine.readNumberFromTape(
                               machine.instructionPointer);
            
            machine.checkReserve(jumpAddress);
            machine.instructionPointer = jumpAddress;
        }
    }
    
    public static final class HaltInstructionImplementation 
            extends InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.haltIsRequested = true;
        }
    }
    
    private final Scanner scanner = new Scanner(System.in);
    
    String readString() {
        return scanner.next();
    }
    
    long readNumber() {
        return scanner.nextLong();
    }
    
    void printString(final int characterTotal, 
                     final long[] numbers) {
        final byte[] bytes = new byte[Long.BYTES * numbers.length];
        
        final StringBuilder sb = new StringBuilder();
        
        
        
        return sb.toString();
    }
    
    void printNumber(final long number) {
        System.out.println(number);
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
        
        boolean zeroFlag = false;
        boolean notZeroFlag = false;
        
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
    }
    
    private final ProcessorFlags flags = new ProcessorFlags();
    
    /**
     * The memory tape.
     */
    private final byte[] tape = new byte[TAPE_LENGTH_IN_BYTES];
    
    /**
     * The operand stack. 
     */
    private final Deque<Long> stack = new ArrayDeque<>();

    private int instructionPointer = 0;
    
    private boolean haltIsRequested = false;
    
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
        System.out.println("Hello World!");
    }
    
    private void requireStackSize(final int requestedSize) {
        if (stack.size() < requestedSize) {
            throw new StackMachineException(
                    String.format("'stack.size()' is %d, 'requestedSize' is %d",
                                  stack.size(),
                                  requestedSize));
        }
    }
    
    private long readNumberFromTape(final int address) {
        if (address + Long.BYTES > tape.length) {
            final String exceptionMessage = 
                    String.format(
                            "address(%d) + Long.BYTES(%d) > " + 
                            "tape.length(%d) = %d", 
                            address, 
                            Long.BYTES, 
                            tape.length, 
                            Long.BYTES + tape.length);
            
            throw new StackMachineException(exceptionMessage);
        }
        
        final long nb0 = tape[address];
        final long nb1 = tape[address + 1] << 1;
        final long nb2 = tape[address + 2] << 2;
        final long nb3 = tape[address + 3] << 3;
        final long nb4 = tape[address + 4] << 4;
        final long nb5 = tape[address + 5] << 5;
        final long nb6 = tape[address + 6] << 6;
        final long nb7 = tape[address + 7] << 7;
        
        return nb0 |
               nb1 |
               nb2 |
               nb3 |
               nb4 |
               nb5 |
               nb6 |
               nb7;
    }
    
    private void writeNumberToTape(final int address, long number) {
        
        final byte[] bytes = new byte[Long.BYTES];
        
        bytes[0] = (byte) (number & 0xffL);
        bytes[1] = (byte)((number >>= Byte.SIZE) & 0xffL);
        bytes[2] = (byte)((number >>= Byte.SIZE) & 0xffL);
        bytes[3] = (byte)((number >>= Byte.SIZE) & 0xffL);
        bytes[4] = (byte)((number >>= Byte.SIZE) & 0xffL);
        bytes[5] = (byte)((number >>= Byte.SIZE) & 0xffL);
        bytes[6] = (byte)((number >>= Byte.SIZE) & 0xffL);
        bytes[7] = (byte)((number >>  Byte.SIZE) & 0xffL);
        
        System.arraycopy(bytes, 0, tape, address, Long.BYTES);
    }
    
    void advanceInstructionPointer() {
        checkReserve(1);
        ++instructionPointer;
    }
    
    void advanceInstructionPointer(final int bytes) {
        checkReserve(bytes);
        instructionPointer += bytes;
    }
    
    void checkReserve(int bytesToReserve) {
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
}
