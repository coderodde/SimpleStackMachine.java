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
            machine.checkTapeReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            
            final int number = 
                    machine.readWordFromTape(machine.getInstructionPointer());
            
            machine.push(number);
        }
    }
    
    public static final class PopInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkTapeReserve(Integer.BYTES);
            machine.advanceInstructionPointer();
            machine.pop();
        }
    }
    
    public static final class LoadInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.advanceInstructionPointer();
            
            final int address = 
                    machine.readWordFromTape(machine.getInstructionPointer());
            
            final int datum = machine.readWordFromTape(address);
            
            machine.push(datum);
        }
    }
    
    public static final class StoreInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.requireStackSize(2);
            machine.checkTapeReserve(2 * Integer.BYTES);
            machine.advanceInstructionPointer();
            
            final int address = 
                    machine.readWordFromTape(machine.getInstructionPointer());
            
            machine.advanceInstructionPointer(Integer.BYTES);
            
            final int datum = machine.readWordFromTape(
                    machine.getInstructionPointer());
            
            machine.writeWordToTape(address, datum);
            machine.advanceInstructionPointer(Integer.BYTES);
        }
    }
    
    /**
     * Delegates to the pop operation.
     */
    public static final class ConstInstructionImplementation 
            extends PushInstructionImplementation {
        
        @Override
        public void execute(SimpleStackMachine machine) {
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
            
            final int number1 = 
                    machine.readWordFromTape(machine.getInstructionPointer());
            
            machine.advanceInstructionPointer(Integer.BYTES);
            
            final int number2 = 
                    machine.readWordFromTape(
                            machine.getInstructionPointer());
            
            machine.pop();
            machine.pop();
            machine.push(func.apply(number1, number2));
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
            final long number1 = machine.pop();
            final long number2 = machine.pop();
            final int cmp = Long.compare(number1, 
                                         number2);
            
            if (cmp < 0) {
                machine.flags().belowFlag = true;
            } else if (cmp > 0) {
                machine.flags().aboveFlag = true;
            } else {
                machine.flags().equalFlag = true;
            }
        }
    }
    
    public static final class TestInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(1);
            final long number = machine.pop();
            
            if (number > 0L) {
                machine.flags().aboveZeroFlag = true;
                machine.flags().notZeroFlag   = true;
            } else if (number < 0L) {
                machine.flags().belowZeroFlag = true;
                machine.flags().notZeroFlag   = true;
            } else {
                machine.flags().zeroFlag = true;
                machine.flags().aboveZeroFlag = false;
                machine.flags().belowZeroFlag = false;
            }
        }
    }
    
    public static final class UnconditionalJumpInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            final int jumpAddress = 
                    (int) machine.readWordFromTape(
                               machine.getInstructionPointer());
            
            machine.checkTapeReserve(jumpAddress);
            machine.setInstructionPointer(jumpAddress);
        }
    }
    
    public static final class HaltInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requestHalt();
        }
    }
    
    public static final class CallInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1 + Integer.BYTES);
            machine.advanceInstructionPointer();
            
            final int address = 
                    machine.readWordFromTape(machine.getInstructionPointer());
            
            machine.advanceInstructionPointer(Integer.BYTES);
            machine.push(machine.getInstructionPointer());
            machine.setInstructionPointer(address);
        }
    }
    
    public static final class ReturnInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
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
    
    public static final class PrintNumberInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(1);
            machine.advanceInstructionPointer();
            machine.printNumber(machine.pop());
        }
    }
    
    public static final class PrintStringInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(2);
            machine.advanceInstructionPointer();
            
            final int numberOfChars    = (int) machine.pop();
            final int stringStartIndex = (int) machine.pop();
            final int numberOfNumbers  = 
                    (int)(numberOfChars / Long.BYTES 
                       + (numberOfChars % Long.BYTES == 0 ? 0 : 1));
            
            final long[] stringData = new long[numberOfNumbers];
            
            for (int p = stringStartIndex, i = 0; i < numberOfNumbers; ++i) {
                stringData[i] = machine.readNumber();
                machine.advanceInstructionPointer(Long.BYTES);
            }
            
            final byte[] asciiString = new byte[numberOfChars];
            
            for (int byteIndex = 0; byteIndex < numberOfChars; ++byteIndex) {
                final long textChunk = stringData[byteIndex / Long.BYTES];
                final byte byteChar  = 
                        (byte)(((textChunk) >>> 
                                 Byte.SIZE * (byteIndex % Long.BYTES)) & 0xff);
                
                asciiString[byteIndex] = byteChar;
            }
            
            final StringBuilder sb = new StringBuilder(numberOfChars);
            
            for (int i = 0; i < asciiString.length; ++i) {
                sb.append((char)(asciiString[i]));
            }
            
            System.out.println(sb.toString());
        }
    }
    
    public static final class ReadStringInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
    
    public static final class ReadNumberInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkTapeReserve(1);
            machine.requireStackSize(1);
            machine.advanceInstructionPointer();
            machine.readNumber();
        }
    }
}
