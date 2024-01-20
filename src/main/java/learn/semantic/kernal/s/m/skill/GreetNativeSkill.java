package learn.semantic.kernal.s.m.skill;

import com.microsoft.semantickernel.skilldefinition.annotations.DefineSKFunction;
import com.microsoft.semantickernel.skilldefinition.annotations.SKFunctionParameters;
public class GreetNativeSkill {

    @DefineSKFunction(name = "greet", description = "Greets the user")
    public String greet(@SKFunctionParameters(name = "username") String username) {
        return String.format("Sup my homie %s !", username);
    }

}