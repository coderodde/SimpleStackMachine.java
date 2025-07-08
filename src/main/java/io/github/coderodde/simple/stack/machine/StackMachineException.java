package io.github.coderodde.simple.stack.machine;

/**
 * This class implements objects of the exceptions that raise in the VM.
 * 
 * @version 1.0.0 (Jul 8, 2025)
 * @since 1.0.0 (Jul 8, 2025)
 */
public final class StackMachineException extends RuntimeException {
    
    public StackMachineException(final String exceptionMessage) {
        super(exceptionMessage);
    }
}
