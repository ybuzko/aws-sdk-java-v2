package software.amazon.awssdk.protocol.tests;

import java.io.IOException;
import java.net.URI;
import org.junit.Test;
import software.amazon.awssdk.auth.signer.Aws4Signer;
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams;
import software.amazon.awssdk.core.signer.Signer;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.HttpExecuteResponse;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.apache.ApacheHttpClient;

public class Millem {
    @Test
    public void test() throws IOException {
        SdkHttpRequest request =
            SdkHttpRequest.builder()
                          .protocol("https")
                          .host("example.com")
                          .encodedPath("/foo%20baz")
                          .method(SdkHttpMethod.GET)
                          .build();

        Aws4Signer signer = Aws4Signer.create();
        request = signer.sign(request,
                              Aws4SignerParams.builder()
                                              .awsCredentials(credentials)
                                              .signingName("service-name")
                                              .signingRegion("us-west-2")
                                              .build());

        try (SdkHttpClient httpClient = ApacheHttpClient.create()) {
            HttpExecuteResponse response =
                httpClient.prepareRequest(HttpExecuteRequest.builder()
                                                            .request(request)
                                                            .build())
                          .call();
        }
    }
}
