package learn.semantic.kernal.s.m;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.KeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.planner.actionplanner.Plan;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlanner;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import learn.semantic.kernal.s.m.skill.GreetNativeSkill;

public class AppRunner {
    public static void main( String[] args ) {
        String apiKey = System.getenv("OPENAI-API-KEY");
        KeyCredential credential = new KeyCredential(apiKey);
        OpenAIAsyncClient client = new OpenAIClientBuilder()
                .credential(credential)
                .buildAsyncClient();

        TextCompletion textCompletionService = SKBuilders.textCompletion()
                .withModelId("gpt-3.5-turbo-instruct")
                .withOpenAIClient(client)
                .build();

        Kernel kernel = SKBuilders.kernel().withDefaultAIService(textCompletionService).build();
        kernel.importSkill(new GreetNativeSkill(), "GreetNativeSkill");

        SequentialPlanner planner = new SequentialPlanner(kernel, null, null);

        Plan plan = planner.createPlanAsync("Greet the user when they introduce themselves").block();

        System.out.println(plan.toPlanString());

        String message = "Hi! My name is Sujal";
        String result = plan.invokeAsync(message).block().getResult();

        System.out.println(result);
    }
}
