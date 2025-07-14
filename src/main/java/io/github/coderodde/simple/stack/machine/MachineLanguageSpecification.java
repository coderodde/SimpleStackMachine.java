package io.github.coderodde.simple.stack.machine;

import java.util.function.BinaryOperator;

/**
 *
 * @version 1.0.0 (Jul 9, 2025)
 * @since 1.0.0 (Jul 9, 2025)
 */
public final class MachineLanguageSpecification {
    
    public static final class NopInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.advanceInstructionPointer();
        }
    }
    
    public static /*final*/ class PushInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            final int number = 
                    machine.readWordFromTape(machine.getInstructionPointer());
            
            machine.advanceInstructionPointer(Integer.BYTES);
            machine.push(number);
        }
    }
    
    public static final class PopInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.advanceInstructionPointer();
            machine.pop();
        }
    }
    
    public static final class LoadInstructionImplementation 
            implements InstructionImplementation {

        /**
         * Pops the address and pushes the word at that address to the stack.
         * 
         * @param machine the target machine.
         */
        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(1);
            machine.advanceInstructionPointer();
            
            final int address = machine.pop();
            final int datum = machine.readWordFromTape(address);
            
            machine.push(datum);
        }
    }
    
    public static final class StoreInstructionImplementation 
            implements InstructionImplementation {

        /**
         * Pops the word and the address and stores the word at the address.
         * 
         * @param machine the target machine.
         */
        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(2);
            machine.advanceInstructionPointer();
            
            final int address = machine.pop();
            final int word    = machine.pop();
            
            machine.writeWordToTape(address, word);
        }
    }
    
    /**
     * Delegates to the pop operation.
     */
    public static final class ConstInstructionImplementation 
            extends PushInstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            super.execute(machine);
        }
    }
    
    public static /*final*/ class BinaryArithmeticInstructionImplementation 
            implements InstructionImplementation {
        
        private final BinaryOperator<Integer> func;
        
        public BinaryArithmeticInstructionImplementation(
                final BinaryOperator<Integer> func) {
            this.func = func;
        }

        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(2);
            machine.advanceInstructionPointer();
            
            final Integer word1 = machine.pop();
            final Integer word2 = machine.pop();
            
            machine.push(func.apply(word1, word2));
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
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(1);
            machine.push(machine.top());
            machine.advanceInstructionPointer();
        }
    }
    
    public static final class SwapInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(2);
            machine.advanceInstructionPointer();
            
            final int number1 = machine.pop();
            final int number2 = machine.pop();
            
            machine.push(number1);
            machine.push(number2);
        }
    }
    
    public static final class CompareInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(2);
            machine.flags().unsetAll();
            machine.advanceInstructionPointer();
            
            final int number1 = machine.pop();
            final int number2 = machine.pop();
            
            final int cmp = Integer.compare(number1, 
                                            number2);
            if (cmp < 0) {
                machine.flags().belowFlag    = true;
                machine.flags().notEqualFlag = true;
            } else if (cmp > 0) {
                machine.flags().aboveFlag    = true;
                machine.flags().notEqualFlag = true;
            } else {
                machine.flags().equalFlag = true;
                machine.flags().aboveFlag = false;
                machine.flags().belowFlag = false;
            }
        }
    }
    
    public static final class TestInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(1);
            machine.flags().unsetAll();
            final long number = machine.pop();
            
            if (number > 0L) {
                machine.flags().aboveZeroFlag = true;
                machine.flags().notZeroFlag   = true;
            } else if (number < 0L) {
                machine.flags().belowZeroFlag = true;
                machine.flags().notZeroFlag   = true;
            } else {
                machine.flags().zeroFlag      = true;
                machine.flags().aboveZeroFlag = false;
                machine.flags().belowZeroFlag = false;
            }
        }
    }
    
    public static final class UnconditionalJumpInstructionImplementation 
            implements InstructionImplementation {
        
        /**
         * Pops the jump address and jumps to it.
         * 
         * @param machine the target machine.
         */
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(1);
            machine.advanceInstructionPointer();
            final int jumpAddress = machine.pop();
            
            machine.checkTapeReserve(jumpAddress);
            machine.setInstructionPointer(jumpAddress);
        }
    }
    
    public static final class HaltInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.advanceInstructionPointer();
            machine.requestHalt();
        }
    }
    
    public static final class CallInstructionImplementation 
            implements InstructionImplementation {
        
        /**
         * Pops the call address, pushes the return address and enters a 
         * function.
         * 
         * @param machine the target machine.
         */
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.requireStackSize(1);
            machine.checkTapeReserve(1);
            machine.advanceInstructionPointer();
            
            final int address = machine.pop();
            
            machine.advanceInstructionPointer(Integer.BYTES);
            machine.push(machine.getInstructionPointer());
            machine.setInstructionPointer(address);
        }
    }
    
    public static final class ReturnInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.requireStackSize(1);
            machine.checkTapeReserve(1);
            machine.advanceInstructionPointer();
            
            final int address = machine.pop();
            machine.setInstructionPointer(address);
        }
    }
    
    public static final class JumpIfZeroInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().zeroFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfNotZeroInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().notZeroFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfBelowZeroInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().belowZeroFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfAboveZeroInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().aboveZeroFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfEqualInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().equalFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfNotEqualInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (!machine.flags().equalFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfAboveInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().aboveFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfAboveOrEqualInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().aboveFlag || machine.flags().equalFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfBelowInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().belowFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address);
            }
        }
    }
    
    public static final class JumpIfBelowOrEqualInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            if (machine.flags().belowFlag || machine.flags().equalFlag) {
                final int address = 
                        machine.readWordFromTape(
                                machine.getInstructionPointer());
                
                machine.setInstructionPointer(address); 
            }
        }
    }
    
    public static final class PrintStringInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(2);
            machine.advanceInstructionPointer();
            
            final int stringLength = machine.pop();
            final int startAddress = machine.pop();
            
            System.out.println(processStringPrint(machine,
                                                  stringLength,
                                                  startAddress));
        }
    }
    
    private static String processStringPrint(final SimpleStackMachine machine,
                                             final int stringLength,
                                             final int startAddress) {
        final byte[] stringData = new byte[stringLength];
        
        for (int i = 0; i < stringData.length; ++i) {
            stringData[i] = machine.readByteFromTape(startAddress + i);
        }
        
        return new String(stringData);
    }
    
    public static final class PrintNumberInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(1);
            machine.advanceInstructionPointer();
            
            System.out.println(machine.pop());
        }
    }
    
    public static final class ReadNumberInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.push(machine.readInt());
        }
    }
    
    public static final class ReadStringInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(2);
            machine.advanceInstructionPointer();
            
            final int bufferLength  = machine.pop();
            final int stringAddress = machine.pop();
            
            final String text = machine.readString();
            
            if (text.length() > bufferLength) {
                machine.push(Integer.MIN_VALUE);
            } else {
                machine.push(text.length());
                
                final byte[] stringData = text.getBytes();
                
                for (int i = 0; i < stringData.length; ++i) {
                    machine.writeByteToTape(stringAddress + i,
                                            stringData[i]);
                }
            }
        }
    }
}
