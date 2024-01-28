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

        var toUpperCaseSemantic = kernel.
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

        var userNameExtractor = kernel.
                getSemanticFunctionBuilder()
                .withFunctionName("userNameExtractor")
                .withDescription("extract username from the sentence")
                .withPromptTemplate("""
                        '{{$input}}'
                        identify and return the user's name from the above input
                        """.stripIndent())
                .withSkillName("SemanticSkill")
                .withCompletionConfig(new PromptTemplateConfig.CompletionConfigBuilder()
                        .maxTokens(100)
                        .temperature(0)
                        .topP(1)
                        .build())
                .build();

        var flightBookingLookup = kernel.
                getSemanticFunctionBuilder()
                .withFunctionName("flightBookingLookup")
                .withDescription("research cheapest flight options")
                .withPromptTemplate("""
                        '{{$input}}'
                        Research about flight booking from the above input on www.google.com.
                        If needed, follow the results of the google search results. Return 10 options in the below format.
                        {
                        flight_date: "",
                        price: "",
                        number_of_stops: "",
                        airline_name: "",
                        website_link: ""
                        }
                        """.stripIndent())
                .withSkillName("SemanticSkill")
                .withCompletionConfig(new PromptTemplateConfig.CompletionConfigBuilder()
                        .maxTokens(100)
                        .temperature(0)
                        .topP(1)
                        .build())
                .build();

        //kernel.registerSemanticFunction(toUpperCaseSemantic);
        //kernel.registerSemanticFunction(userNameExtractor);
        kernel.registerSemanticFunction(flightBookingLookup);

        kernel.importSkill(new GreetNativeSkill(), "GreetNativeSkill");

        //SequentialPlanner planner = new SequentialPlanner(kernel, null, null);

//        Plan plan = planner.createPlanAsync("""
//                        print cheapest flight options
//                        """.stripIndent()).block();

//        System.out.println(plan.toPlanString());

        String message = "I want to go to Dubai from Bangalore";
        //String result = plan.invokeAsync(message).block().getResult();

        var result = flightBookingLookup.invokeAsync(message).block().getResult();
        System.out.println(result);
    }
}
