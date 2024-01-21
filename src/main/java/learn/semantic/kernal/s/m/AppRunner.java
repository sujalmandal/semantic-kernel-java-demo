package learn.semantic.kernal.s.m;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.core.credential.KeyCredential;
import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.SKBuilders;
import com.microsoft.semantickernel.planner.actionplanner.Plan;
import com.microsoft.semantickernel.planner.sequentialplanner.SequentialPlanner;
import com.microsoft.semantickernel.semanticfunctions.PromptTemplateConfig;
import com.microsoft.semantickernel.textcompletion.TextCompletion;
import learn.semantic.kernal.s.m.skill.GreetNativeSkill;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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

        var fixedFunction = kernel.
                getSemanticFunctionBuilder()
                .withFunctionName("convertToAllCaps")
                .withDescription("converts input message to all capital")
                .withPromptTemplate("""
                        '{{$input}}'
                        convert the above text into all capital
                        """.stripIndent())
                .withSkillName("SemanticSkill")
                .withCompletionConfig(new PromptTemplateConfig.CompletionConfigBuilder()
                        .maxTokens(100)
                        .temperature(0)
                        .topP(1)
                        .build())
                .build();

        kernel.registerSemanticFunction(fixedFunction);

        kernel.importSkill(new GreetNativeSkill(), "GreetNativeSkill");

        SequentialPlanner planner = new SequentialPlanner(kernel, null, null);

        Plan plan = planner.createPlanAsync(
                "convert the input to all caps and print it").block();

        System.out.println(plan.toPlanString());

        String message = "Hi! I my username is ss6sujal@gmail.com but please call me Sujal";
        String result = plan.invokeAsync(message).block().getResult();

        System.out.println(result);
    }
}
