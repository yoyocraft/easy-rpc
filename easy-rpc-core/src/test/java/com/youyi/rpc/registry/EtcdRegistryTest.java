package com.youyi.rpc.registry;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author <a href="https://github.com/yoyocraft">youyi</a>
 */
@Slf4j
public class EtcdRegistryTest {

    public static void main(String[] args) {
        // create client using endpoints
        try (Client client = Client.builder()
                .endpoints("http://127.0.0.1:2379").build()) {
            KV kvClient = client.getKVClient();

            ByteSequence key = ByteSequence.from("test_key".getBytes());
            ByteSequence value = ByteSequence.from("test_value".getBytes());

            // put the key-value
            kvClient.put(key, value).get();

            // get the CompletableFuture
            CompletableFuture<GetResponse> getFuture = kvClient.get(key);

            // get the value from CompletableFuture
            GetResponse response = getFuture.get();

            log.info("kv: {}", response.getKvs());

            // delete the key
            kvClient.delete(key).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
