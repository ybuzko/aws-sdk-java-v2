import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import software.amazon.awssdk.protocols.jsoncore.JsonNode;
import software.amazon.awssdk.thirdparty.jacksoncore.core.JsonFactoryBuilder;
import software.amazon.awssdk.utils.StringInputStream;

public class Foo {
    @Test
    public void test() {
        JsonNode node = JsonNode.parse(new JsonFactoryBuilder().build(),
                                       new StringInputStream("{}"));
        assertThat(node.isObject()).isTrue();
    }
}
