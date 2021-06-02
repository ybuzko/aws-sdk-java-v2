package software.amazon.awssdk.services.kinesis;

import java.time.Instant;
import org.junit.Test;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;

public class Repro {
    @Test
    public void reproP47678696() {
        System.setProperty(SdkSystemSetting.CBOR_ENABLED.property(), "false");
        KinesisClient client = KinesisClient.create();
        System.out.println(client.getShardIterator(r -> r.streamName("test")
                                                         .shardId("shardId-000000000000")
                                                         .shardIteratorType(ShardIteratorType.AT_TIMESTAMP)
                                                         .timestamp(Instant.now())));
    }
}
