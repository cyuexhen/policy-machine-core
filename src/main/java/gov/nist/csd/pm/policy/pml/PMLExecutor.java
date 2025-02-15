package gov.nist.csd.pm.policy.pml;

import gov.nist.csd.pm.policy.Policy;
import gov.nist.csd.pm.policy.pml.model.context.ExecutionContext;
import gov.nist.csd.pm.policy.pml.model.exception.PMLExecutionException;
import gov.nist.csd.pm.policy.pml.model.expression.Value;
import gov.nist.csd.pm.policy.pml.statement.FunctionDefinitionStatement;
import gov.nist.csd.pm.policy.pml.statement.PMLStatement;
import gov.nist.csd.pm.policy.exceptions.PMException;
import gov.nist.csd.pm.policy.model.access.UserContext;
import gov.nist.csd.pm.policy.pml.model.scope.PMLScopeException;

import java.util.*;

public class PMLExecutor {

    public static void compileAndExecutePML(Policy policy, UserContext author, String input,
                                            FunctionDefinitionStatement ... customFunctions) throws PMException {
        // compile the PML into statements
        List<PMLStatement> compiledStatements = PMLCompiler.compilePML(policy, input, customFunctions);

        // initialize the execution context
        ExecutionContext ctx = new ExecutionContext(author);
        ctx.scope().loadFromPMLContext(PMLContext.fromPolicy(policy));

        ExecutionContext predefined;
        try {
            // add custom builtin functions to scope
            for (FunctionDefinitionStatement func : customFunctions) {
                ctx.scope().addFunction(func);
            }

            // store the predefined ctx to avoid adding again at the end of execution
            predefined = ctx.copy();
        } catch (PMLScopeException e) {
            throw new PMLExecutionException(e.getMessage());
        }

        // execute each statement
        for (PMLStatement stmt : compiledStatements) {
            try {
                stmt.execute(ctx, policy);
            } catch (PMException e) {
                System.err.println(e.getMessage() + ": " + stmt);
                throw e;
            }
        }

        // save any top level functions and constants to be used later
        saveTopLevelFunctionsAndConstants(policy, predefined, ctx);
    }

    private static void saveTopLevelFunctionsAndConstants(Policy policy, ExecutionContext predefinedCtx, ExecutionContext ctx) throws PMException {
        Map<String, FunctionDefinitionStatement> predefinedFunctions = predefinedCtx.scope().functions();
        Map<String, Value> predefinedConstants = predefinedCtx.scope().values();

        Map<String, FunctionDefinitionStatement> topLevelFunctions = ctx.scope().functions();
        for (String funcName : topLevelFunctions.keySet()) {
            if (predefinedFunctions.containsKey(funcName)) {
                continue;
            }

            FunctionDefinitionStatement funcDef = topLevelFunctions.get(funcName);
            policy.userDefinedPML().addFunction(funcDef);
        }

        Map<String, Value> topLevelConstants = ctx.scope().values();
        for (String name : topLevelConstants.keySet()) {
            if (predefinedConstants.containsKey(name)) {
                continue;
            }

            Value value = topLevelConstants.get(name);
            policy.userDefinedPML().addConstant(name, value);
        }
    }

    public static Value executeStatementBlock(ExecutionContext executionCtx, Policy policyAuthor, List<PMLStatement> statements) throws PMException {
        for (PMLStatement statement : statements) {
            Value value = statement.execute(executionCtx, policyAuthor);
            if (value.isReturn() || value.isBreak() || value.isContinue()) {
                return value;
            }
        }

        return new Value();
    }
}
