package io.github.coderodde.simple.stack.machine;

public interface InstructionImplementation {

    /**
     * Executes an operation on the given machine.
     * 
     * @param machine the target machine.
     */
    public void execute(final SimpleStackMachine machine);
}
