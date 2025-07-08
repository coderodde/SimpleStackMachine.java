package io.github.coderodde.simple.stack.machine;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * This class implements the actual stack virtual machine.
 * 
 * @version 1.1.0 (Jul 3, 2025)
 * @since 1.0.0 (Jul 3, 2025)
 */
public class SimpleStackMachine {
    
    /**
     * The length of the memory tape in bytes. Effectively, 16 kilobytes.
     */
    private static final int TAPE_LENGTH_IN_BYTES = 16 * 1024;
    
    /**
     * This inner static class models all the processor flags.
     */
    static final class ProcessorFlags {
        
        /**
         * Set to {@code true} if and only if the most previous comparison 
         * ended up with equality.
         */
        boolean equalFlag = false;
        
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
    
    /**
     * The memory tape.
     */
    private final byte[] tape = new byte[TAPE_LENGTH_IN_BYTES];
    
    /**
     * The operand stack. 
     */
    private final Deque<Long> stack = new ArrayDeque<>();

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
        
        executeImpl();
    }
    
    private void executeImpl() {
        
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
}
