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
            machine.checkReserve(1);
            machine.advanceInstructionPointer();
        }
    }
    
    public static /*final*/ class PushInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            
            final long number = 
                    machine.readNumberFromTape(machine.getInstructionPointer()  );
            
            machine.push(number);
        }
    }
    
    public static final class PopInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.advanceInstructionPointer();
            machine.pop();
        }
    }
    
    public static final class LoadInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            
            final long address = 
                    machine.readNumberFromTape(machine.getInstructionPointer());
            
            final long datum = machine.readNumberFromTape((int) address);
            
            machine.push(datum);
        }
    }
    
    public static final class StoreInstructionImplementation 
            implements InstructionImplementation {

        @Override
        public void execute(SimpleStackMachine machine) {
            machine.checkReserve(1 + 2 * Long.BYTES);
            machine.advanceInstructionPointer();
            
            final long address = 
                    machine.readNumberFromTape(machine.getInstructionPointer());
            
            machine.advanceInstructionPointer(Long.BYTES);
            
            final long datum = machine.readNumberFromTape(machine.getInstructionPointer());
            
            machine.writeNumberToTape((int) address, datum);
            machine.advanceInstructionPointer(Long.BYTES);
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
                    machine.readNumberFromTape(machine.getInstructionPointer());
            
            final long number2 = 
                    machine.readNumberFromTape(
                            machine.getInstructionPointer() + Long.BYTES);
            
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
            machine.checkReserve(1);
            machine.requireStackSize(1);
            machine.push(machine.top());
            machine.advanceInstructionPointer();
        }
    }
    
    public static final class SwapInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.requireStackSize(2);
            final long number1 = machine.pop();
            final long number2 = machine.pop();
            machine.push(number1);
            machine.push(number2);
        }
    }
    
    public static final class CompareInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1);
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
    
    public static final class UnconditionalJumpInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1 + Long.BYTES);
            machine.advanceInstructionPointer();
            final int jumpAddress = 
                    (int) machine.readNumberFromTape(
                               machine.getInstructionPointer());
            
            machine.checkReserve(jumpAddress);
            machine.setInstructionPointer(jumpAddress);
        }
    }
    
    public static final class HaltInstructionImplementation 
            implements InstructionImplementation {
        
        @Override
        public void execute(final SimpleStackMachine machine) {
            machine.checkReserve(1);
            machine.requestHalt();
        }
    }
    
}
