package learn.semantic.kernal.s.m.skill;

import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionParameters;
public class GreetNativeSkill {

    @DefineSKFunction(name = "print", description = "prints any text")
    public String print(@SKFunctionParameters(name = "textToPrint",
            description = "input message") String text) {
        return String.format("[output] %s", text);
    }

}